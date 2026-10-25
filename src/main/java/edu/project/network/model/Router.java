package edu.project.network.model;

import jakarta.persistence.*;

@Entity
@Table(name = "router")
public class Router {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "topology_id")
    private Topology topology;
    @Column(nullable = false)
    private String name;
    private boolean active = true;

    public Router() {}
    public Router(Topology topology, String name, boolean active) {
        this.topology = topology; this.name = name; this.active = active;
    }
    public Long getId() { return id; }
    public Topology getTopology() { return topology; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
