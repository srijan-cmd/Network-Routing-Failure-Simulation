package edu.project.network.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulation")
public class Simulation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "topology_id")
    private Topology topology;
    private String sourceRouter;
    private String destinationRouter;
    private String changeType;
    private String changeDescription;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Simulation() {}
    public Simulation(Topology topology, String sourceRouter, String destinationRouter, String changeType, String changeDescription) {
        this.topology = topology; this.sourceRouter = sourceRouter; this.destinationRouter = destinationRouter;
        this.changeType = changeType; this.changeDescription = changeDescription; this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Topology getTopology() { return topology; }
    public String getSourceRouter() { return sourceRouter; }
    public String getDestinationRouter() { return destinationRouter; }
    public String getChangeType() { return changeType; }
    public String getChangeDescription() { return changeDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
