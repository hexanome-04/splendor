package ca.hexanome04.splendorgame.control;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.*;
import org.springframework.web.client.*;
import java.net.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * Perform integration tests performed  functions that depend on the Lobby Service.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class InitializerTest {

    @Mock
    Authentication auth;
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    @Autowired
    Initializer initializer;

    @DisplayName("Test the registration of the game service with the lobby service")
    @Test
    void testRegisterWithLS() {
        // mock getting token
        Mockito.when(auth.getToken())
                .thenReturn("sometoken");

        // mock registration request
        Mockito.when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.PUT),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(""));

        // mock no registered game
        Mockito.when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));

        assertThatNoException().isThrownBy(() -> initializer.registerWithLobbyService());
    }

    @DisplayName("Test the registration of the game service with the lobby service, with retries")
    @Test
    void testRegisterWithLSRetry() {
        // mock getting token
        Mockito.when(auth.getToken())
                .thenReturn("sometoken");

        // mock request sent to LS
        Mockito.when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.PUT),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(""));

        // mock no registered game
        Mockito.when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));

        assertThatNoException().isThrownBy(() -> initializer.registerWithLobbyService());
    }

    @DisplayName("Test that registration fails with lobby service")
    @Test
    void testRegisterWithLSFail() {
        // mock getting token
        Mockito.when(auth.getToken())
                .thenReturn("sometoken");

        // mock request sent to LS
        Mockito.when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.PUT),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT));

        // mock no registered game
        Mockito.when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""));

        assertThatThrownBy(() -> initializer.registerWithLobbyService())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unable to register game service!");
    }

}
