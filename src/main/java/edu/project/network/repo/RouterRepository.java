package edu.project.network.repo;
import edu.project.network.model.Router;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RouterRepository extends JpaRepository<Router, Long> {
    List<Router> findByTopologyId(Long topologyId);
}
