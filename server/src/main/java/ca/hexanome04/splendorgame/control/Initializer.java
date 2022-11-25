package ca.hexanome04.splendorgame.control;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Initializer for the server.
 */
@Component
public class Initializer {

    final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Value("${LS.URL}")
    private String lsUrl;
    GameServiceInfo gameServiceInfo;
    Authentication auth;

    public Initializer(@Autowired Authentication auth,
                       @Value("${gs.name}") String gsName,
                       @Value("${gs.displayName}") String gsDisplayName,
                       @Value("${gs.location}") String gsLocation) {
        this.auth = auth;
        this.gameServiceInfo = new GameServiceInfo(gsName, gsDisplayName, gsLocation,
                2, 4, "true");
    }

    /**
     * Run tasks required when application is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // Avoid blocking main thread
        new Thread(this::registerWithLobbyService).start();
    }

    /**
     * Registers our game with the lobby service.
     */
    public void registerWithLobbyService() {
        boolean registered = false;
        int maxRetries = 5;
        int retries = 0;
        while(retries < maxRetries && !registered) {
            String token = auth.getToken();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = new Gson().toJson(gameServiceInfo);

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            URI uri = UriComponentsBuilder.fromHttpUrl(lsUrl)
                    .path("/api/gameservices/" + gameServiceInfo.name())
                    .queryParam("access_token", URLEncoder.encode(token, StandardCharsets.UTF_8))
                    .build(true).toUri();

            ResponseEntity<?> response = null;
            try {
                response = restTemplate.exchange(
                        uri,
                        HttpMethod.PUT,
                        httpEntity,
                        String.class // Response type.
                );
            } catch (RestClientException e) {
                logger.debug(e.toString());
                retries++;
                continue;
            }

            // if here, successful
            registered = true;
        }

        if(!registered) {
            logger.error("Failed to register game with lobby service.");
            throw new RuntimeException("Unable to register game with lobby service!");
        }
    }

}
