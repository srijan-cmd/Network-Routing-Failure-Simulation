package edu.project.network.repo;
import edu.project.network.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LinkRepository extends JpaRepository<Link, Long> {
    List<Link> findByTopologyId(Long topologyId);
}
