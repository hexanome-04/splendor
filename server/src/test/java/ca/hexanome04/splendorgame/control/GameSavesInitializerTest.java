package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.GameSaveData;
import ca.hexanome04.splendorgame.control.templates.GameSaveInfo;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GameSavesInitializerTest {

    private AutoCloseable closeable;

    @MockBean
    Authentication auth;
    @MockBean
    GameSavesManager gameSavesManager;
    @MockBean
    RestTemplate restTemplate;

    @InjectMocks
    @Autowired
    GameSaveInitializer gameSaveInitializer;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);

        // mock default token
        when(auth.getToken()).thenReturn("token");
    }

    @AfterEach
    void finish() throws Exception {
        closeable.close();
    }


    @Test
    @DisplayName("Verify calling registerCurrentGameSaves() works")
    void testRegisterAllGameSaves() {
        GameSaveData data = new GameSaveData("", List.of("user1"), "");
        List<GameSaveInfo> gameSaveInfoList = List.of(
            new GameSaveInfo("", "creator", 0, data)
        );
        when(gameSavesManager.getGameSaves()).thenReturn(gameSaveInfoList);
        when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.PUT),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(""));

        assertThatNoException().isThrownBy(() -> gameSaveInitializer.registerCurrentGameSaves());
    }

    @Test
    @DisplayName("Verify success registerNewGameSave")
    void testRegisterNewGameSaveSuccess() {
        GameSaveData data = new GameSaveData("service", List.of("user1"), "some-id");
        when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.PUT),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(""));

        assertThat(gameSaveInitializer.registerNewGameSave(data)).isTrue();
    }

    @Test
    @DisplayName("Verify failure registerNewGameSave")
    void testRegisterNewGameSaveFail() {
        GameSaveData data = new GameSaveData("service", List.of("user1"), "some-id");
        when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.PUT),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some error idk"));

        assertThat(gameSaveInitializer.registerNewGameSave(data)).isFalse();
    }

    @Test
    @DisplayName("Verify success deleteGameSave")
    void testDeleteGameSaveSuccess() {
        GameSaveData data = new GameSaveData("service", List.of("user1"), "some-id");
        when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.DELETE),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.OK).body(""));

        assertThat(gameSaveInitializer.deleteGameSave(data)).isTrue();
    }

    @Test
    @DisplayName("Verify failure deleteGameSave")
    void testDeleteGameSaveFail() {
        GameSaveData data = new GameSaveData("service", List.of("user1"), "some-id");
        when(restTemplate.exchange(
                Mockito.any(),
                Mockito.eq(HttpMethod.DELETE),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some error idk"));

        assertThat(gameSaveInitializer.deleteGameSave(data)).isFalse();
    }

}
