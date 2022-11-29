package ca.hexanome04.splendorgame.control;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.*;
import java.net.URI;
import java.nio.charset.*;

/**
 * Perform integration tests with functions that depend on the Lobby Service.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AuthenticationTest {

    final Logger logger = LoggerFactory.getLogger(AuthenticationTest.class);

    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    @Autowired
    Authentication auth;

    /**
     * Construct new authentication test instance.
     */
    public AuthenticationTest() {
        this.auth = new Authentication();
    }

    @Test
    @DisplayName("Verify that the server can obtain an admin token.")
    void testGetToken() {
        String jsonResp = "{\"access_token\": \"sometoken\"}";
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(jsonResp));
        assertThat(auth.getToken()).isNotEqualTo("");
    }

    @Test
    @DisplayName("Verify that the server can obtain an admin token.")
    void testGetTokenFail() {
        String jsonResp = "{\"error_description\": \"some error description here\"}";
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResp));
        assertThat(auth.getToken()).isEqualTo("");
    }

    @Test
    @DisplayName("Verify that a token has admin-privileges.")
    void testIsAdminToken() {
        String jsonResp = "[{\"authority\": \"ROLE_ADMIN\"}]";
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(jsonResp));
        assertThat(auth.isAdminToken("sometoken")).isTrue();
    }

    @Test
    @DisplayName("Verify that a token fails while checking if it is an admin token.")
    void testIsAdminTokenFail() {
        String jsonResp = "{\"error_description\": \"some error\"}";
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request", jsonResp.getBytes(), Charset.defaultCharset()));
        assertThat(auth.isAdminToken("invalid token")).isFalse();
    }

    @Test
    @DisplayName("Verify that a token fails unexpectedly while checking if it is an admin token.")
    void testIsAdminTokenFailUnexpected() {
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT));
        assertThat(auth.isAdminToken("invalid token")).isFalse();
    }

    @Test
    @DisplayName("Verify that a token has player-privileges.")
    void testIsPlayerToken() {
        String jsonResp = "[{\"authority\": \"ROLE_PLAYER\"}]";
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(jsonResp));
        assertThat(auth.isPlayerToken("sometoken")).isTrue();
    }

    @Test
    @DisplayName("Verify that a token fails while checking if it is an player token.")
    void testIsPlayerTokenFail() {
        String jsonResp = "{\"error_description\": \"some error\"}";
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request", jsonResp.getBytes(), Charset.defaultCharset()));
        assertThat(auth.isPlayerToken("invalid token")).isFalse();
    }

    @Test
    @DisplayName("Verify that a token fails unexpectedly while checking if it is an player token.")
    void testIsPlayerTokenFailUnexpected() {
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT));
        assertThat(auth.isPlayerToken("invalid token")).isFalse();
    }


    @Test
    @DisplayName("Verify that the token has a name")
    void testGetNameFromToken() {
        String strResp = "name";
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(strResp));
        assertThat(auth.getNameFromToken("sometoken")).isNotEqualTo("");
    }

    @Test
    @DisplayName("Verify that the token can fail to have a name")
    void testGetNameFromTokenFail() {
        Mockito.when(restTemplate.exchange(
                Mockito.any(URI.class),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        assertThat(auth.getNameFromToken("invalid_token")).isEqualTo("");
    }

}
