```java
import org.springframework.boot.actuate.health.Health;import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.web.server.WebEndpointManagement;import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
public class HealthController implements HealthIndicator {
    private final WebEndpointManagement webEndpointManagement;

    public HealthController(WebEndpointManagement webEndpointManagement) {
        this.webEndpointManagement = webEndpointManagement;
    }

    @Override
    public Health health() {
        if (webEndpointManagement.isEndpointsEnabled()) {
            return Health.up().build();
        } else {
            return Health.down(Health.Details.of("Endpoints are disabled")).build();
        }
    }
}
```