package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.SplendorGameApplication;
import ca.hexanome04.splendorgame.control.templates.GameServiceInfo;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
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

    // we exclude some code that runs automatically when Spring Boot starts, but we do test the methods individually
    @Value("${test:false}")
    private boolean isTestMode;
    @Value("${LS.location}")
    private String lsLocation;
    GameServiceInfo[] gameServices;
    Authentication auth;
    GameSaveInitializer gameSaveInitializer;
    RestTemplate restTemplate = new RestTemplate();

    /**
     * Initializes server with lobby service.
     *
     * @param auth methods relating to token
     * @param gameSaveInitializer game save initializer
     * @param gsName game service base name
     * @param gsDisplayName game service base display name
     * @param gsLocation game service location
     */
    public Initializer(@Autowired Authentication auth,
                       @Autowired GameSaveInitializer gameSaveInitializer,
                       @Value("${gs.name}") String gsName,
                       @Value("${gs.displayName}") String gsDisplayName,
                       @Value("${gs.location}") String gsLocation) {
        this.auth = auth;
        this.gameSaveInitializer = gameSaveInitializer;
        this.gameServices = new GameServiceInfo[] {
            new GameServiceInfo(gsName + "_" + GameVersions.BASE_ORIENT,
                        gsDisplayName + " Orient", gsLocation,
                    2, 4, "true"),

            new GameServiceInfo(gsName + "_" + GameVersions.BASE_ORIENT_CITIES,
                        gsDisplayName + " Cities", gsLocation,
                        2, 4, "true"),

            new GameServiceInfo(gsName + "_" + GameVersions.BASE_ORIENT_TRADE_ROUTES,
                        gsDisplayName + " Trade Routes", gsLocation,
                        2, 4, "true"),
        };
        this.restTemplate.setErrorHandler(new EmptyRestTemplateErrorHandler());
    }

    /**
     * Run tasks required when application is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void init() {
        // we exclude some code that runs automatically when Spring Boot starts, but we do test the methods individually
        if (isTestMode) {
            return;
        }
        this.registerWithLobbyService();
        this.gameSaveInitializer.registerCurrentGameSaves();
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
                // attempt to register all game versions
                for (GameServiceInfo gameServiceInfo : this.gameServices) {
                    if (checkRegistered(gameServiceInfo).getStatusCode().is2xxSuccessful()) {
                        logger.info("Game service ({}) already registered.", gameServiceInfo.displayName());
                        unregister(gameServiceInfo);
                    }

                    ResponseEntity regResp = attemptRegister(gameServiceInfo);
                    if (!regResp.getStatusCode().is2xxSuccessful()) {
                        throw new RestClientException(regResp.getBody().toString());
                    }
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
     *
     * @param gameServiceInfo game service info
     */
    private void unregister(GameServiceInfo gameServiceInfo) throws RestClientException {
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

        logger.info("Unregistered game service ({}).", gameServiceInfo.displayName());
    }

    /**
     * Check if our game service was already registered with the lobby service.
     *
     * @param gameServiceInfo game service info
     * @return response from server
     */
    private ResponseEntity checkRegistered(GameServiceInfo gameServiceInfo) throws RestClientException {
        logger.info("Checking if game service ({}) is already registered.", gameServiceInfo.displayName());

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
     * @param gameServiceInfo game service info
     * @return response from server
     */
    private ResponseEntity attemptRegister(GameServiceInfo gameServiceInfo) throws RestClientException {
        logger.info("Attempting to register our game service ({}).", gameServiceInfo.displayName());

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
