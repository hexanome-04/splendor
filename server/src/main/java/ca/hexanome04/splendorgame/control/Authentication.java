package ca.hexanome04.splendorgame.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Provides useful methods to obtain the token required for the server and check user tokens.
 */
@Component
public class Authentication {

    final Logger logger = LoggerFactory.getLogger(Authentication.class);

    @Value("${LS.URL}")
    private String lsUrl;
    @Value("${LS.server.username}")
    private String username;
    @Value("${LS.server.password}")
    private String password;

    /**
     * Obtain a token with administrative-privileges.
     *
     * @return token with administrative-privileges
     */
    public String getToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("bgp-client-name", "bgp-client-pw");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromHttpUrl(lsUrl)
                .path("/oauth/token")
                .queryParam("grant_type", "password")
                .queryParam("username", username)
                .queryParam("password", password)
                .build().toUri();

        ResponseEntity<?> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                httpEntity,
                String.class // Response type.
        );

        // Maybe there should be a check to see if the response is a json and is what we expect?
        JsonObject jo = JsonParser.parseString((String) response.getBody()).getAsJsonObject();

        if (!jo.has("access_token")) {
            if (jo.has("error_description")) {
                logger.error("Error occurred when obtaining token: {}", jo.get("error_description").getAsString());
            }
            return "";
        }

        String t = jo.get("access_token").getAsString();
        logger.debug("Obtained token: {}", t);
        return t;
    }

    private JsonObject getTokenRole(String token) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl(lsUrl)
                .path("/oauth/role")
                .queryParam("access_token", token)
                .build().toUri();

        ResponseEntity<?> response;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    String.class
            );
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().is4xxClientError()) {
                // expected error from lobby server (i think)
                return JsonParser.parseString(e.getResponseBodyAsString()).getAsJsonObject();
            }
            return new JsonObject(); // maybe return something that's empty
        }

        // Might be a good idea to check if the json object is in the body.
        JsonArray jo = JsonParser.parseString((String) response.getBody()).getAsJsonArray();
        return jo.get(0).getAsJsonObject();
    }

    /**
     * Check if token has administrative-privileges.
     *
     * @param token token to check
     * @return true if token has token administrative-privileges
     */
    public boolean isAdminToken(String token) {
        JsonObject jo = getTokenRole(token);

        if (!jo.has("authority")) {
            if (jo.has("error_description")) {
                logger.error("Error occurred when checking if token has admin privileges: {}",
                        jo.get("error_description").getAsString());
            } else {
                logger.error("Unexpected error occurred when checking if token has admin privileges.");
            }
            return false;
        }

        String t = jo.get("authority").getAsString();
        if (!t.equals("ROLE_ADMIN")) {
            logger.debug("Token '{}' has role '{}'.", token, t);
            return false;
        }

        logger.debug("Token '{}' has admin-privileges.", token);
        return true;
    }

    /**
     * Check if token has player-privileges.
     *
     * @param token token to check
     * @return true if token has token player-privileges
     */
    public boolean isPlayerToken(String token) {
        JsonObject jo = getTokenRole(token);

        if (!jo.has("authority")) {
            if (jo.has("error_description")) {
                logger.error("Error occurred when checking if token has player privileges: {}",
                        jo.get("error_description").getAsString());
            } else {
                logger.error("Unexpected error occurred when checking if token has player privileges.");
            }
            return false;
        }

        String t = jo.get("authority").getAsString();
        if (!t.equals("ROLE_PLAYER")) {
            logger.debug("Token '{}' has role '{}'.", token, t);
            return false;
        }

        logger.debug("Token '{}' has player-privileges.", token);
        return true;
    }
}
