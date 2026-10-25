package edu.project.network.service;

import edu.project.network.model.*;
import edu.project.network.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NetworkService {
    private final TopologyRepository topologyRepo;
    private final RouterRepository routerRepo;
    private final LinkRepository linkRepo;
    private final SimulationRepository simulationRepo;
    private final SimulationResultRepository resultRepo;

    public NetworkService(TopologyRepository topologyRepo, RouterRepository routerRepo, LinkRepository linkRepo,
                          SimulationRepository simulationRepo, SimulationResultRepository resultRepo) {
        this.topologyRepo = topologyRepo; this.routerRepo = routerRepo; this.linkRepo = linkRepo;
        this.simulationRepo = simulationRepo; this.resultRepo = resultRepo;
    }

    public Map<String,Object> getNetwork() {
        Topology topology = getDefaultTopology();
        List<Map<String,Object>> routers = routerRepo.findByTopologyId(topology.getId()).stream().map(r -> {
            Map<String,Object> m = new LinkedHashMap<>(); m.put("id", r.getId()); m.put("name", r.getName()); m.put("active", r.isActive()); return m;
        }).toList();
        List<Map<String,Object>> links = linkRepo.findByTopologyId(topology.getId()).stream().map(l -> {
            Map<String,Object> m = new LinkedHashMap<>(); m.put("id", l.getId()); m.put("source", l.getSourceRouter().getName());
            m.put("destination", l.getDestinationRouter().getName()); m.put("weight", l.getWeight()); m.put("active", l.isActive()); return m;
        }).toList();
        Map<String,Object> result = new LinkedHashMap<>(); result.put("topologyId", topology.getId()); result.put("name", topology.getName());
        result.put("routers", routers); result.put("links", links); return result;
    }

    public List<Map<String,Object>> getHistory() {
        return simulationRepo.findTop20ByOrderByCreatedAtDesc().stream().map(s -> {
            Map<String,Object> m = new LinkedHashMap<>(); m.put("id", s.getId()); m.put("source", s.getSourceRouter());
            m.put("destination", s.getDestinationRouter()); m.put("change", s.getChangeDescription()); m.put("createdAt", s.getCreatedAt().toString());
            resultRepo.findAll().stream().filter(r -> r.getSimulation().getId().equals(s.getId())).findFirst().ifPresent(r -> {
                m.put("route", r.getRoute()); m.put("cost", r.getTotalCost()); m.put("routeFound", r.isRouteFound());
            }); return m;
        }).toList();
    }

    @Transactional
    public Map<String,Object> simulate(String source, String destination, String changeType, Long linkId, Long routerId, Double newWeight) {
        Topology topology = getDefaultTopology();
        List<Router> routers = routerRepo.findByTopologyId(topology.getId());
        Router src = routers.stream().filter(r -> r.getName().equalsIgnoreCase(source)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown source router: " + source));
        Router dst = routers.stream().filter(r -> r.getName().equalsIgnoreCase(destination)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown destination router: " + destination));
        String description = "No topology change";
        if ("FAIL_LINK".equals(changeType) || "RESTORE_LINK".equals(changeType) || "CHANGE_WEIGHT".equals(changeType)) {
            Link link = linkRepo.findById(linkId == null ? -1L : linkId).orElseThrow(() -> new IllegalArgumentException("Select a valid link."));
            if ("FAIL_LINK".equals(changeType)) { link.setActive(false); description = "Link " + link.getId() + " failed"; }
            else if ("RESTORE_LINK".equals(changeType)) { link.setActive(true); description = "Link " + link.getId() + " restored"; }
            else {
                if (newWeight == null || newWeight < 0) throw new IllegalArgumentException("New weight must be zero or greater.");
                link.setWeight(newWeight); description = "Link " + link.getId() + " weight changed to " + newWeight;
            }
            linkRepo.save(link);
        } else if ("FAIL_ROUTER".equals(changeType) || "RESTORE_ROUTER".equals(changeType)) {
            Router router = routerRepo.findById(routerId == null ? -1L : routerId).orElseThrow(() -> new IllegalArgumentException("Select a valid router."));
            router.setActive("RESTORE_ROUTER".equals(changeType));
            routerRepo.save(router);
            description = "Router " + router.getName() + ("FAIL_ROUTER".equals(changeType) ? " failed" : " restored");
        }

        Map<String,Object> routeData = shortestPath(src, dst, routers, linkRepo.findByTopologyId(topology.getId()));
        Simulation sim = simulationRepo.save(new Simulation(topology, source, destination, changeType, description));
        boolean found = Boolean.TRUE.equals(routeData.get("routeFound"));
        SimulationResult sr = new SimulationResult(sim, (String)routeData.getOrDefault("route", "No route"),
                found ? ((Number)routeData.get("cost")).doubleValue() : null, found, (String)routeData.get("message"));
        resultRepo.save(sr);
        routeData.put("simulationId", sim.getId()); routeData.put("changeDescription", description); return routeData;
    }

    private Map<String,Object> shortestPath(Router src, Router dst, List<Router> routers, List<Link> links) {
        Map<String,Double> dist = new HashMap<>(); Map<String,String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>(); Map<String,List<Link>> adjacency = new HashMap<>();
        for (Router r : routers) if (r.isActive()) adjacency.put(r.getName(), new ArrayList<>());
        for (Link l : links) if (l.isActive() && l.getSourceRouter().isActive() && l.getDestinationRouter().isActive()) {
            adjacency.computeIfAbsent(l.getSourceRouter().getName(), k -> new ArrayList<>()).add(l);
            adjacency.computeIfAbsent(l.getDestinationRouter().getName(), k -> new ArrayList<>()).add(l);
        }
        Map<String,Object> out = new LinkedHashMap<>();
        if (!src.isActive() || !dst.isActive()) {
            out.put("routeFound", false); out.put("message", "Source or destination router is unavailable."); return out;
        }
        adjacency.keySet().forEach(n -> dist.put(n, Double.POSITIVE_INFINITY)); dist.put(src.getName(), 0.0);
        while (true) {
            String current = null; double best = Double.POSITIVE_INFINITY;
            for (String n : dist.keySet()) if (!visited.contains(n) && dist.get(n) < best) { current = n; best = dist.get(n); }
            if (current == null) break;
            if (current.equals(dst.getName())) break;
            visited.add(current);
            for (Link link : adjacency.getOrDefault(current, List.of())) {
                String next = link.getSourceRouter().getName().equals(current) ? link.getDestinationRouter().getName() : link.getSourceRouter().getName();
                double candidate = dist.get(current) + link.getWeight();
                if (candidate < dist.getOrDefault(next, Double.POSITIVE_INFINITY)) { dist.put(next, candidate); previous.put(next, current); }
            }
        }
        if (!Double.isFinite(dist.getOrDefault(dst.getName(), Double.POSITIVE_INFINITY))) {
            out.put("routeFound", false); out.put("message", "No available route connects the selected routers."); return out;
        }
        LinkedList<String> path = new LinkedList<>(); String at = dst.getName();
        while (at != null) { path.addFirst(at); at = previous.get(at); }
        out.put("routeFound", true); out.put("route", String.join(" → ", path)); out.put("path", path);
        out.put("cost", dist.get(dst.getName())); out.put("message", "Minimum-cost route recalculated successfully."); return out;
    }

    @Transactional
    public Map<String,Object> reset() {
        Topology t = getDefaultTopology();
        for (Router r : routerRepo.findByTopologyId(t.getId())) r.setActive(true);
        routerRepo.saveAll(routerRepo.findByTopologyId(t.getId()));
        for (Link l : linkRepo.findByTopologyId(t.getId())) {
            l.setActive(true);
            String a=l.getSourceRouter().getName(), b=l.getDestinationRouter().getName();
            if ((a.equals("A")&&b.equals("B"))||(a.equals("B")&&b.equals("A"))) l.setWeight(4);
            if ((a.equals("A")&&b.equals("C"))||(a.equals("C")&&b.equals("A"))) l.setWeight(2);
            if ((a.equals("C")&&b.equals("B"))||(a.equals("B")&&b.equals("C"))) l.setWeight(1);
            if ((a.equals("B")&&b.equals("D"))||(a.equals("D")&&b.equals("B"))) l.setWeight(5);
            if ((a.equals("C")&&b.equals("D"))||(a.equals("D")&&b.equals("C"))) l.setWeight(8);
            if ((a.equals("D")&&b.equals("E"))||(a.equals("E")&&b.equals("D"))) l.setWeight(2);
            if ((a.equals("B")&&b.equals("E"))||(a.equals("E")&&b.equals("B"))) l.setWeight(10);
        }
        linkRepo.saveAll(linkRepo.findByTopologyId(t.getId()));
        return getNetwork();
    }

    private Topology getDefaultTopology() {
        List<Topology> all = topologyRepo.findAll();
        if (!all.isEmpty()) return all.get(0);
        Topology t = topologyRepo.save(new Topology("Campus Network", "Sample weighted router network for route-failure simulation."));
        Router a=routerRepo.save(new Router(t,"A",true)); Router b=routerRepo.save(new Router(t,"B",true));
        Router c=routerRepo.save(new Router(t,"C",true)); Router d=routerRepo.save(new Router(t,"D",true)); Router e=routerRepo.save(new Router(t,"E",true));
        linkRepo.save(new Link(t,a,b,4,true)); linkRepo.save(new Link(t,a,c,2,true)); linkRepo.save(new Link(t,c,b,1,true));
        linkRepo.save(new Link(t,b,d,5,true)); linkRepo.save(new Link(t,c,d,8,true)); linkRepo.save(new Link(t,d,e,2,true)); linkRepo.save(new Link(t,b,e,10,true));
        return t;
    }
}
