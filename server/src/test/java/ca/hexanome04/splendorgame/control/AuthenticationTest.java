package ca.hexanome04.splendorgame.control;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Perform integration tests with functions that depend on the Lobby Service.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthenticationTest {

    final Logger logger = LoggerFactory.getLogger(AuthenticationTest.class);

    @Value("${LS.location}")
    String lsUrl;
    @Value("${LS.admin.username}")
    String adminUsername;
    @Value("${LS.admin.password}")
    String adminPassword;
    @Value("${LS.user.username}")
    String userUsername;
    @Value("${LS.user.password}")
    String userPassword;
    @Autowired
    Authentication auth;

    /**
     * Obtain an access token with the given username and password.
     *
     * @param username
     * @param password
     * @return access token
     */
    String getToken(String username, String password) {
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
        }

        String t = jo.get("access_token").getAsString();
        logger.debug("Obtained token: {}", t);
        return t;
    }

    @Test
    @DisplayName("Verify that the server can obtain an admin token.")
    void testGetToken() {
        assertThat(auth.getToken()).isNotEqualTo("");
    }

    @Test
    @DisplayName("Verify that a token has admin-privileges.")
    void testIsAdminToken() {
        String token = getToken(adminUsername, adminPassword);
        assertThat(auth.isAdminToken(token)).isTrue();
    }

    @Test
    @DisplayName("Verify that a token has player-privileges.")
    void testIsPlayerToken() {
        String token = getToken(userUsername, userPassword);
        assertThat(auth.isPlayerToken(token)).isTrue();
    }

}
