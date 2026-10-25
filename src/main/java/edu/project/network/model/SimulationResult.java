package edu.project.network.model;

import jakarta.persistence.*;

@Entity
@Table(name = "simulation_result")
public class SimulationResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false) @JoinColumn(name = "simulation_id", unique = true)
    private Simulation simulation;
    private String route;
    private Double totalCost;
    private boolean routeFound;
    @Column(length = 1000)
    private String message;

    public SimulationResult() {}
    public SimulationResult(Simulation simulation, String route, Double totalCost, boolean routeFound, String message) {
        this.simulation = simulation; this.route = route; this.totalCost = totalCost;
        this.routeFound = routeFound; this.message = message;
    }
    public Long getId() { return id; }
    public Simulation getSimulation() { return simulation; }
    public String getRoute() { return route; }
    public Double getTotalCost() { return totalCost; }
    public boolean isRouteFound() { return routeFound; }
    public String getMessage() { return message; }
}
