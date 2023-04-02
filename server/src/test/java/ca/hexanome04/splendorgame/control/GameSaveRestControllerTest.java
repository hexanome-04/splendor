package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.GameSaveData;
import ca.hexanome04.splendorgame.control.templates.GameSaveInfo;
import ca.hexanome04.splendorgame.model.GameSession;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

/**
 * Tests for the game saves REST controller.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class GameSaveRestControllerTest {

    private AutoCloseable closeable;

    @Mock
    Game game;
    @MockBean
    GameSavesManager gameSavesManager;
    @MockBean
    GameSaveInitializer gameSaveInitializer;
    @MockBean
    SessionManager sessionManager;
    @MockBean
    Authentication authentication;

    @InjectMocks
    @Autowired
    GameSaveRestController controller;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);

    }
    @AfterEach
    void finish() throws Exception {
        closeable.close();
    }


    @Test
    @DisplayName("Verify that you cannot create a game save from a session that doesn't exist.")
    void testInvalidGameSession() {
        Mockito.when(sessionManager.getGameSession(Mockito.any()))
                .thenReturn(null);

        assertThat(controller.createNewGameSave("", "", "").getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Verify that you anyone can create a game save they're not a part of.")
    void testAnyPlayerRequestSave() {
        GameSession ses = new GameSession("", "user", "");
        ses.setGame(game);
        Mockito.when(game.isGameOver())
                .thenReturn(false);

        Mockito.when(sessionManager.getGameSession(Mockito.any()))
                .thenReturn(ses);

        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("notuser");

        Mockito.when(gameSavesManager.createGameSave(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("not empty");

        GameSaveData gameSaveData = new GameSaveData("", List.of("user"), "1");
        GameSaveInfo gameSaveInfo = new GameSaveInfo("name here", "notuser", 0, gameSaveData);
        Mockito.when(gameSavesManager.getGameSave(Mockito.any()))
                .thenReturn(gameSaveInfo);

        assertThat(controller.createNewGameSave("", "", "").getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Verify that only logged in users can create game saves.")
    void testOnlyLoggedInPlayerCanSave() {
        GameSession ses = new GameSession("", "user", "");
        Mockito.when(sessionManager.getGameSession(Mockito.any()))
                .thenReturn(ses);

        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("");

        assertThat(controller.createNewGameSave("", "", "").getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Verify that failing to create a game save (for whatever reason) throws correct exception.")
    void testFailsToCreateGameSave() {
        GameSession ses = new GameSession("", "", "");
        ses.setGame(game);
        Mockito.when(game.isGameOver())
                .thenReturn(false);

        Mockito.when(sessionManager.getGameSession(Mockito.any()))
                .thenReturn(ses);

        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("non empty");

        Mockito.when(gameSavesManager.createGameSave(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("");

        assertThat(controller.createNewGameSave("", "", "").getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Verify no ended games can be saved.")
    void testNoEndedGameSaves() {
        GameSession ses = new GameSession("", "", "");
        ses.setGame(game);
        Mockito.when(game.isGameOver())
                .thenReturn(true);

        Mockito.when(sessionManager.getGameSession(Mockito.any()))
                .thenReturn(ses);

        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("not empty");

        Mockito.when(gameSavesManager.createGameSave(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("not empty");

        GameSaveData gameSaveData = new GameSaveData("", new ArrayList<>(), "");
        GameSaveInfo gameSaveInfo = new GameSaveInfo("", "", 0, gameSaveData);
        Mockito.when(this.gameSavesManager.getGameSave(Mockito.any()))
                .thenReturn(gameSaveInfo);

        ResponseEntity<String> resp = controller.createNewGameSave("", "", "");
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).contains("ended");
    }

    @Test
    @DisplayName("Verify that requesting to create a game save works.")
    void testSuccessCreateGameSave() {
        GameSession ses = new GameSession("", "", "");
        ses.setGame(game);
        Mockito.when(game.isGameOver())
                .thenReturn(false);

        Mockito.when(sessionManager.getGameSession(Mockito.any()))
                .thenReturn(ses);

        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("not empty");

        Mockito.when(gameSavesManager.createGameSave(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("not empty");

        GameSaveData gameSaveData = new GameSaveData("", new ArrayList<>(), "");
        GameSaveInfo gameSaveInfo = new GameSaveInfo("", "", 0, gameSaveData);
        Mockito.when(this.gameSavesManager.getGameSave(Mockito.any()))
                .thenReturn(gameSaveInfo);

        assertThat(controller.createNewGameSave("", "", "").getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }


    // -------------------------------------------------------------------------------------------------------------
    // Get game saves

    @Test
    @DisplayName("Verify you need to be authenticated to get game saves.")
    void testNotAuthGetSaves() {
        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("");

        assertThat(controller.getGameSaveInfo("").getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Verify can obtain game save info.")
    void testCanGetGameSaveInfo() {
        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("user");

        GameSaveData gameSaveData = new GameSaveData("", List.of("user"), "");
        List<GameSaveInfo> gameSavesList = List.of(
                new GameSaveInfo("", "creator", 0, gameSaveData)
        );
        Mockito.when(gameSavesManager.getGameSaves())
                .thenReturn(gameSavesList);

        var resp = controller.getGameSaveInfo("");
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);


        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<GameSaveInfo>>() {
        }.getType();
        List<GameSaveInfo> respList = gson.fromJson(resp.getBody(), listType);
        assertThat(respList.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Verify that anyone can get anyone elses saves.")
    void testCanGetAllGameSavesInfo() {
        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("user");

        GameSaveData gameSaveData = new GameSaveData("", List.of("not user"), "");
        List<GameSaveInfo> gameSavesList = List.of(
                new GameSaveInfo("",  "creator", 0, gameSaveData)
        );
        Mockito.when(gameSavesManager.getGameSaves())
                .thenReturn(gameSavesList);

        var resp = controller.getGameSaveInfo("");
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);


        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<GameSaveInfo>>() {
        }.getType();
        List<GameSaveInfo> respList = gson.fromJson(resp.getBody(), listType);
        assertThat(respList.size()).isEqualTo(1);
    }


    // -------------------------------------------------------------------------------------------------------------
    // Deleting game saves

    @Test
    @DisplayName("Verify that you cannot delete a game save that doesn't exist.")
    void testNoGameSaveExistsDelete() {
        Mockito.when(gameSavesManager.getGameSave(Mockito.any()))
                .thenReturn(null);

        assertThat(controller.deleteGameSave("", "").getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Verify that someone not the creator cannot delete anothers game save.")
    void testWrongPlayerRequestDelete() {
        GameSaveData gameSaveData = new GameSaveData("", List.of("user"), "");
        GameSaveInfo gameSaveInfo = new GameSaveInfo("", "creator", 0, gameSaveData);
        Mockito.when(gameSavesManager.getGameSave(Mockito.any()))
                .thenReturn(gameSaveInfo);

        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("notuser");

        assertThat(controller.deleteGameSave("", "").getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Verify that requesting to delete a game save works.")
    void testSuccessDeleteGameSave() {
        GameSaveData gameSaveData = new GameSaveData("", List.of("user"), "");
        GameSaveInfo gameSaveInfo = new GameSaveInfo("", "creator", 0, gameSaveData);
        Mockito.when(gameSavesManager.getGameSave(Mockito.any()))
                .thenReturn(gameSaveInfo);

        Mockito.when(authentication.getNameFromToken(Mockito.any()))
                .thenReturn("creator");

        assertThat(controller.deleteGameSave("", "").getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

}
