package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.SplendorGameApplication;
import ca.hexanome04.splendorgame.control.templates.GameServiceInfo;
import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.*;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Initializer for the server.
 */
@Component
public class Initializer {

    final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Value("${LS.location}")
    private String lsLocation;
    GameServiceInfo gameServiceInfo;
    Authentication auth;
    RestTemplate restTemplate = new RestTemplate();

    /**
     * Initializes server with lobby service.
     *
     * @param auth methods relating to token
     * @param gsName game service name
     * @param gsDisplayName game service display name
     * @param gsLocation game service location
     */
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
    @Profile("!test")
    public void init() {
        this.restTemplate.setErrorHandler(new EmptyRestTemplateErrorHandler());

        // Avoid blocking main thread
        new Thread(this::registerWithLobbyService).start();
    }

    /**
     * Registers our game with the lobby service.
     * Will delete a previously registered game service if exists.
     */
    public void registerWithLobbyService() {
        boolean registered = false;
        int maxRetries = 5;
        int retries = 0;
        while (retries < maxRetries && !registered) {
            try {
                if (checkRegistered().getStatusCode().is2xxSuccessful()) {
                    logger.info("Game service already registered.");
                    unregister();
                }

                ResponseEntity regResp = attemptRegister();
                if (!regResp.getStatusCode().is2xxSuccessful()) {
                    throw new RestClientException(regResp.getBody().toString());
                }
            } catch (RestClientException e) {
                logger.debug(e.toString());
                retries++;
                continue;
            }

            // if here, successful
            registered = true;
        }

        if (!registered) {
            logger.error("Failed to register game with lobby service.");
            SplendorGameApplication.shutdown(1);
            throw new RuntimeException("Unable to register game service!");
        }

        logger.info("Registered game on lobby service!");
    }

    /**
     * Unregister our game service from the lobby service.
     */
    private void unregister() throws RestClientException {
        String token = auth.getToken();

        URI uri = UriComponentsBuilder.fromHttpUrl(lsLocation)
                .path("/api/gameservices/" + gameServiceInfo.name())
                .queryParam("access_token", URLEncoder.encode(token, StandardCharsets.UTF_8))
                .build(true).toUri();

        ResponseEntity<?> response = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                null,
                String.class
        );

        logger.info("Unregistered game service.");
    }

    /**
     * Check if our game service was already registered with the lobby service.
     *
     * @return response from server
     */
    private ResponseEntity checkRegistered() throws RestClientException {
        logger.info("Checking if game service is already registered.");

        URI uri = UriComponentsBuilder.fromHttpUrl(lsLocation)
                .path("/api/gameservices/" + gameServiceInfo.name())
                .build(true).toUri();

        return restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                String.class // Response type.
        );
    }

    /**
     * Attempt to register our game with the lobby service.
     *
     * @return response from server
     */
    private ResponseEntity attemptRegister() throws RestClientException {
        logger.info("Attempting to register our game service.");

        String token = auth.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String body = new Gson().toJson(gameServiceInfo);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        URI uri = UriComponentsBuilder.fromHttpUrl(lsLocation)
                .path("/api/gameservices/" + gameServiceInfo.name())
                .queryParam("access_token", URLEncoder.encode(token, StandardCharsets.UTF_8))
                .build(true).toUri();

        return restTemplate.exchange(
                    uri,
                    HttpMethod.PUT,
                    httpEntity,
                    String.class // Response type.
        );
    }

}
