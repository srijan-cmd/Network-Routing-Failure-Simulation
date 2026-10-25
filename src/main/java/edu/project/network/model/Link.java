package edu.project.network.model;

import jakarta.persistence.*;

@Entity
@Table(name = "link")
public class Link {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "topology_id")
    private Topology topology;
    @ManyToOne(optional = false) @JoinColumn(name = "source_router_id")
    private Router sourceRouter;
    @ManyToOne(optional = false) @JoinColumn(name = "destination_router_id")
    private Router destinationRouter;
    private double weight;
    private boolean active = true;

    public Link() {}
    public Link(Topology topology, Router sourceRouter, Router destinationRouter, double weight, boolean active) {
        this.topology = topology; this.sourceRouter = sourceRouter; this.destinationRouter = destinationRouter;
        this.weight = weight; this.active = active;
    }
    public Long getId() { return id; }
    public Topology getTopology() { return topology; }
    public Router getSourceRouter() { return sourceRouter; }
    public Router getDestinationRouter() { return destinationRouter; }
    public double getWeight() { return weight; }
    public boolean isActive() { return active; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setActive(boolean active) { this.active = active; }
}
