package edu.project.network.repo;
import edu.project.network.model.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SimulationRepository extends JpaRepository<Simulation, Long> {
    List<Simulation> findTop20ByOrderByCreatedAtDesc();
}
