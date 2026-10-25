package edu.project.network.web;

import edu.project.network.service.NetworkService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NetworkController {
    private final NetworkService service;
    public NetworkController(NetworkService service) { this.service = service; }

    @GetMapping("/network") public Map<String,Object> network() { return service.getNetwork(); }
    @GetMapping("/history") public Object history() { return service.getHistory(); }
    @PostMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestBody SimulationRequest request) {
        try {
            if (request.source() == null || request.source().isBlank() || request.destination() == null || request.destination().isBlank())
                return ResponseEntity.badRequest().body(Map.of("message","Source and destination are required."));
            return ResponseEntity.ok(service.simulate(request.source(), request.destination(),
                    request.changeType() == null ? "NONE" : request.changeType(), request.linkId(), request.routerId(), request.newWeight()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
    @PostMapping("/reset") public Map<String,Object> reset() { return service.reset(); }

    public record SimulationRequest(String source, String destination, String changeType, Long linkId, Long routerId, Double newWeight) {}
}
