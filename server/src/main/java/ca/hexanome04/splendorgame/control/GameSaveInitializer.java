package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.GameSaveData;
import ca.hexanome04.splendorgame.control.templates.GameSaveInfo;
import com.google.gson.Gson;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handles notifying Lobby Service of game saves.
 */
@Component
public class GameSaveInitializer {

    final Logger logger = LoggerFactory.getLogger(GameSaveInitializer.class);

    @Value("${LS.location}")
    private String lsLocation;
    Authentication auth;
    GameSavesManager gameSavesManager;
    RestTemplate restTemplate = new RestTemplate();

    /**
     * Initialize new game save initializer.
     *
     * @param auth authentication for tokens
     * @param gameSavesManager game saves manager
     */
    public GameSaveInitializer(@Autowired Authentication auth,
                               @Autowired GameSavesManager gameSavesManager) {
        this.auth = auth;
        this.gameSavesManager = gameSavesManager;
    }

    /**
     * Will notify Lobby Service of all currently loaded game saves.
     */
    public void registerCurrentGameSaves() {
        logger.info("Attempting to register any game saves with LS.");
        this.restTemplate.setErrorHandler(new EmptyRestTemplateErrorHandler());

        List<GameSaveInfo> allInfo =  this.gameSavesManager.getGameSaves();
        for (int i = 0; i < allInfo.size(); i++) {
            GameSaveInfo gsi = allInfo.get(i);
            this.registerNewGameSave(gsi.gameSaveData());
        }
    }

    /**
     * Builds a URI to for a specific game save id.
     *
     * @param gameSaveData game save info
     * @return URI for specific game save id
     */
    private URI getGameSaveUri(GameSaveData gameSaveData) {
        return UriComponentsBuilder.fromHttpUrl(lsLocation)
                .path("/api/gameservices/" + gameSaveData.gamename() + "/savegames/" + gameSaveData.savegameid())
                .queryParam("access_token", URLEncoder.encode(auth.getToken(), StandardCharsets.UTF_8))
                .build(true).toUri();
    }

    /**
     * Registers a new game save with the Lobby Service.
     *
     * @param gameSaveData game save info
     * @return success
     */
    public boolean registerNewGameSave(GameSaveData gameSaveData) {
        URI uri = this.getGameSaveUri(gameSaveData);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String body = new Gson().toJson(gameSaveData);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<?> response = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            logger.warn("Unable to register game save '{}' on LS.", gameSaveData.savegameid());
            Object respText = response.getBody();
            if (respText != null) {
                logger.warn(respText.toString());
            }
            return false;
        }

        logger.info("Registered new game save '{}' on LS.", gameSaveData.savegameid());
        return true;
    }

    /**
     * De-registers an existing game save with the Lobby Service.
     *
     * @param gameSaveData game save info
     * @return success
     */
    public boolean deleteGameSave(GameSaveData gameSaveData) {
        URI uri = this.getGameSaveUri(gameSaveData);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String body = new Gson().toJson(gameSaveData);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<?> response = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            logger.warn("Unable to delete game save '{}'.", gameSaveData.savegameid());
            Object respText = response.getBody();
            if (respText != null) {
                logger.warn(respText.toString());
            }
            return false;
        }

        logger.info("De-registered game save '{}'.", gameSaveData.savegameid());
        return true;
    }


}
