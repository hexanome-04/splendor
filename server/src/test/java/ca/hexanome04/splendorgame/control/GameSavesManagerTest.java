package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.GameSaveInfo;
import ca.hexanome04.splendorgame.model.CardTier;
import ca.hexanome04.splendorgame.model.CascadeType;
import ca.hexanome04.splendorgame.model.CostType;
import ca.hexanome04.splendorgame.model.GameUtils;
import ca.hexanome04.splendorgame.model.NobleCard;
import ca.hexanome04.splendorgame.model.OrientDevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.RegDevelopmentCard;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GameSavesManagerTest {

    String recordsFilename = "all.json";
    File tempDir;
    TradingPostsGame tradingPostsGame;
    CitiesGame citiesGame;
    @InjectMocks
    GameSavesManager gameSavesManager;

    @BeforeEach
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("splendor-saves").toFile();
        gameSavesManager = new GameSavesManager(tempDir, recordsFilename, new SplendorTypeAdapter("splendor"));

        // add cities game to the tests too later
        tradingPostsGame =  GameUtils.createNewTradingPostGame(15, 2);

        HashMap<TokenType, Integer> cost = new HashMap<>(Map.of(TokenType.Red, 1));
        RegDevelopmentCard regDevCard = new RegDevelopmentCard(CardTier.TIER_1, TokenType.Red, 1,
                1, CostType.Token, cost, "1");
        OrientDevelopmentCard orientDevCard = new OrientDevelopmentCard(CardTier.TIER_1, TokenType.Red, 1,
                CascadeType.None, false, 1, CostType.Token, cost, "o1", false);
        NobleCard nobleCard = new NobleCard(1, CostType.Bonus, cost, "noble1");
        for (Player p : tradingPostsGame.getPlayers()) {
            p.addPrestigePoints(1);

            p.addNoble(nobleCard);
            p.reserveNoble(nobleCard);

            p.addCard(orientDevCard);
            p.addCard(regDevCard);
            p.reserveCard(orientDevCard);
            p.reserveCard(regDevCard);

            p.addTokens(new HashMap<>(Map.of(TokenType.Red, 1)));
        } // they should have 4 prestige points orientCard, dev card, noble card, and the add 1
    }

    /**
     * Verify that the input player has the default values set.
     *
     * @param p trading post player
     */
    void verifyTPPlayer(Player p) {
        assertThat(p.getPrestigePoints()).isEqualTo(4);

        assertThat(p.getNobles().size()).isEqualTo(1);
        assertThat(p.getReservedNobles().size()).isEqualTo(1);

        assertThat(p.getDevCards().size()).isEqualTo(2);
        assertThat(p.getReservedCards().size()).isEqualTo(2);

        assertThat(p.getTokens().get(TokenType.Red)).isEqualTo(1);
    }

    @AfterEach
    void complete() {
        tempDir.delete();
    }

    @Test
    @DisplayName("Verify that you can create a game save")
    void testCanCreateGameSave() {
        String saveId = gameSavesManager.createGameSave("save1", "creator", tradingPostsGame);
        assertThat(saveId).isNotEmpty();

        File saveFile = new File(tempDir, saveId + ".json");
        File recordsFile = new File(tempDir, recordsFilename);
        assertThat(saveFile.isFile()).isTrue();
        assertThat(recordsFile.isFile()).isTrue();
        assertThat(gameSavesManager.getGameSave(saveId).name()).isEqualTo("save1");
        assertThat(gameSavesManager.getGameSaves().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Verify game save records gets saved when creating a game save")
    void testGameSaveRecordsCreated() throws IOException {
        String saveId = gameSavesManager.createGameSave("save1", "creator", tradingPostsGame);
        assertThat(saveId).isNotEmpty();

        File recordsFile = new File(tempDir, recordsFilename);
        assertThat(recordsFile.isFile()).isTrue();

        HashMap<String, GameSaveInfo> allInfo = null;
        // now load it back and make sure the data matches
        try (FileReader reader = new FileReader(recordsFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();
            Type mapType = new TypeToken<HashMap<String, GameSaveInfo>>() {
            }.getType();
            allInfo = gson.fromJson(jsonObject, mapType);
        }

        assertThat(allInfo.get(saveId).name()).isEqualTo("save1");
    }

    @Test
    @DisplayName("Verify deletion of game save")
    void testCanDeleteGameSave() {
        String saveId = gameSavesManager.createGameSave("save1", "creator", tradingPostsGame);
        assertThat(saveId).isNotEmpty();

        // save file is there
        File saveFile = new File(tempDir, saveId + ".json");
        assertThat(saveFile.isFile()).isTrue();

        // now check if not there
        gameSavesManager.deleteGameSave(saveId);
        assertThat(saveFile.exists()).isFalse();
    }

    @Test
    @DisplayName("Verify that loading game save data is correct (not a full check, enough maybe)")
    void testValidGameDataReload() {
        String saveId = gameSavesManager.createGameSave("save1", "creator", tradingPostsGame);
        assertThat(saveId).isNotEmpty();

        TradingPostsGame game = (TradingPostsGame) gameSavesManager.getGameSaveData(saveId);
        for (Player p : game.getPlayers()) {
            verifyTPPlayer(p);
        }
    }

    @Test
    @DisplayName("Verify that reloading all records of game saves work")
    void testReloadAllRecords() {
        String saveId = gameSavesManager.createGameSave("save1", "creator", tradingPostsGame);
        assertThat(saveId).isNotEmpty();

        gameSavesManager = new GameSavesManager(tempDir, recordsFilename, new SplendorTypeAdapter("splendor"));

        assertThat(gameSavesManager.getGameSaves().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Verify no game save is returned with non-existed save id")
    void testGetInvalidGameSaveId() {
        assertThat(gameSavesManager.getGameSaveData("---")).isNull();
        assertThat(gameSavesManager.getGameSave("---")).isNull();
    }

    @Test
    @DisplayName("Verify getting invalid game instance from json throws")
    void testInvalidGetGameInstanceJson() {
        assertThat(gameSavesManager.getGameInstanceFromJson(null)).isNull();
    }

    @Test
    @DisplayName("Verify writing invalid parameters")
    void testInvalidWrite() {
        assertThat(gameSavesManager.writeJsonFile("no", null, null)).isFalse();
    }

    @Test
    @DisplayName("Verify that records will get deleted on reload if file does not exist for game save")
    void testRemoveNonExistentGameSavesFromRecords() {
        String saveId = gameSavesManager.createGameSave("save1", "creator", tradingPostsGame);
        assertThat(saveId).isNotEmpty();
        assertThat(gameSavesManager.getGameSaves().size()).isEqualTo(1);

        File gameSave = new File(tempDir, saveId + ".json");
        assertThat(gameSave.delete()).isTrue();

        gameSavesManager = new GameSavesManager(tempDir, recordsFilename, new SplendorTypeAdapter("splendor"));
        assertThat(gameSavesManager.getGameSaves().size()).isEqualTo(0);
    }

}
