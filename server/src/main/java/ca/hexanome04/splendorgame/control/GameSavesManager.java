package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.GameSaveData;
import ca.hexanome04.splendorgame.control.templates.GameSaveInfo;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsGame;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Manages all of Splendor's game saves.
 */
@Component
public class GameSavesManager {

    static final Logger logger = LoggerFactory.getLogger(GameSavesManager.class);

    private Map<String, GameSaveInfo> gameSaves;
    private final File baseDirectory;
    private final String savesRecord;
    private final SplendorTypeAdapter splendorTypeAdapter;

    /**
     * Initialize game save manager.
     *
     * @param saveLocation path for saves
     * @param savesRecord filename for all basic info of saves
     * @param splendorTypeAdapter initializer
     */
    @Autowired
    public GameSavesManager(@Value("${save.location}") String saveLocation,
                            @Value("${save.records}") String savesRecord,
                            @Autowired SplendorTypeAdapter splendorTypeAdapter) {
        this(new File(saveLocation), savesRecord, splendorTypeAdapter);
    }

    /**
     * Alternative game save manager constructor, allowing you to pass in a file object.
     * (Easier testing)
     *
     * @param baseDirectory directory for saves
     * @param savesRecord filename for all basic info of saves
     * @param splendorTypeAdapter splendor types for Gson
     */
    protected GameSavesManager(File baseDirectory,
                               String savesRecord,
                               SplendorTypeAdapter splendorTypeAdapter) {
        this.baseDirectory = baseDirectory;
        this.savesRecord = savesRecord;
        this.splendorTypeAdapter = splendorTypeAdapter;
        this.gameSaves = new HashMap<>();

        this.checkSaves();
    }

    /**
     * Checks for already saved games.
     * Will not do a recursive check in the save location, however.
     */
    public void checkSaves() {
        try {
            this.checkCreateSavesFolder();
            if (new File(this.baseDirectory, this.savesRecord).isFile()) {
                JsonObject jsonObject = this.getJsonFile(this.savesRecord);
                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<String, GameSaveInfo>>() {
                }.getType();
                this.gameSaves = gson.fromJson(jsonObject, mapType);

                // check if the save files exist?
                Map<String, Integer> fileMap = new HashMap<>();
                File[] files = this.baseDirectory.listFiles();
                for (File f : files) {
                    String name = f.getName();
                    if (f.isFile() && name.endsWith(".json")) {
                        String gameSaveId = name.substring(0, name.length() - 5);
                        fileMap.put(gameSaveId, 1);
                    }
                }

                // now check & remove the ones that don't have
                this.gameSaves.entrySet().removeIf(entry -> !fileMap.containsKey(entry.getKey()));
            }
        } catch (Exception e) {
            logger.warn("Unable to complete check all records for game saves.");
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Saves the record of all game saves (basic info for all saves).
     */
    protected void writeGameSavesRecord() {
        try {
            this.checkCreateSavesFolder();
            this.writeJsonFile(this.savesRecord, this.gameSaves);
        } catch (Exception e) {
            logger.warn("Unable to complete write for all records of game saves.");
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Retrieve a list of the essential info for each game save.
     *
     * @return list of game save info
     */
    public List<GameSaveInfo> getGameSaves() {
        return new ArrayList<>(this.gameSaves.values());
    }

    /**
     * Get the game save information from the specified game save id.
     *
     * @param gameSaveId game save id
     * @return game save, null if not found
     */
    public GameSaveInfo getGameSave(String gameSaveId) {
        for (Map.Entry<String, GameSaveInfo> entry : this.gameSaves.entrySet()) {
            if (entry.getKey().equals(gameSaveId)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Read json file from the save location with specified filename.
     *
     * @param filename filename without .json
     * @return json object if json file was found else null
     */
    protected JsonObject getJsonFile(String filename) {
        File saveFile = new File(this.baseDirectory, filename);
        try (FileReader reader = new FileReader(saveFile)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.getAsJsonObject();
        } catch (Exception e) {
            logger.warn("Unable to read json file '{}'.", filename);
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Attempts to load a game instance from a json object.
     *
     * @param jsonObject json object
     * @return game instance if valid json object else null
     */
    protected Game getGameInstanceFromJson(JsonObject jsonObject) {
        Gson gson = SplendorTypeAdapter.createGson();
        try {
            GameVersions gameVer = GameVersions.valueOf(jsonObject.get("gameVersion").getAsString());
            return switch (gameVer) {
                case BASE_ORIENT -> gson.fromJson(jsonObject, OrientGame.class);
                case BASE_ORIENT_CITIES -> gson.fromJson(jsonObject, CitiesGame.class);
                case BASE_ORIENT_TRADE_ROUTES -> gson.fromJson(jsonObject, TradingPostsGame.class);
            };
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieve the game data required to recreate the game session.
     *
     * @param saveGameId save game id
     * @return game instance or null if it does not exist
     */
    public Game getGameSaveData(String saveGameId) {
        JsonObject jsonObject = this.getJsonFile(saveGameId + ".json");
        if (jsonObject == null) {
            return null;
        }

        Game gameData = getGameInstanceFromJson(jsonObject);
        if (gameData == null) {
            logger.warn("Invalid json data for game save id '{}'.", saveGameId);
        }
        return gameData;
    }

    /**
     * Delete a game save (and all of its data).
     * Does nothing if id does not correspond to a game save.
     *
     * @param saveGameId id of game save to be deleted
     */
    public void deleteGameSave(String saveGameId) {
        this.gameSaves.remove(saveGameId);

        File saveData = new File(this.baseDirectory, saveGameId + ".json");
        try {
            if (saveData.delete()) {
                logger.info("Deleted save data '{}'.", saveGameId);
                this.writeGameSavesRecord();
            } else {
                logger.warn("Unable to delete save data '{}'.", saveGameId);
            }
        } catch (SecurityException e) {
            logger.error("Unable to delete save data due to security '{}'.", saveGameId);
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Writes a game save to a json file.
     *
     * @param saveGameId game save id
     * @param game game instance to save
     * @return success of file writing
     */
    protected boolean writeGameSaveToFile(String saveGameId, Game game) {
        this.checkCreateSavesFolder();

        return this.writeJsonFile(saveGameId + ".json", game);
    }

    /**
     * Writes a json file with a specified Gson instance.
     *
     * @param filename filename
     * @param gson gson instance
     * @param object object to write
     * @return success of write
     */
    protected boolean writeJsonFile(String filename, Gson gson, Object object) {
        File saveData = new File(this.baseDirectory, filename);
        try (Writer writer = new FileWriter(saveData)) {
            gson.toJson(object, writer);
            return true;
        } catch (Exception e) {
            logger.warn("Unable to write json file '{}'.", filename);
            logger.warn(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Writes json file with a default gson instance with a Splendor type adapter.
     *
     * @param filename filename
     * @param object object to write
     * @return success of write
     */
    protected boolean writeJsonFile(String filename, Object object) {
        return this.writeJsonFile(filename, SplendorTypeAdapter.createGson(), object);
    }

    /**
     * Checks if the directory for game saves exists, if not, creates it.
     */
    protected void checkCreateSavesFolder() {
        try {
            if (!this.baseDirectory.isDirectory()) {
                this.baseDirectory.mkdir();
            }
        } catch (Exception e) {
            logger.error("Unable to check/create folder for game saves.");
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Saves the current state of the game.
     *
     * @param saveName name of game save
     * @param creator creator of game save
     * @param game game to be saved
     * @return id of game save if successful else empty string
     */
    public String createGameSave(String saveName, String creator, Game game) {
        String saveGameId = this.createNewGameSaveId();

        String gameName = this.splendorTypeAdapter.getGameName(game.getGameVersion());
        List<String> playerNames = new ArrayList<>();
        for (Player p : game.getPlayers()) {
            playerNames.add(p.getName());
        }

        if (this.writeGameSaveToFile(saveGameId, game)) {
            GameSaveData gameSaveData = new GameSaveData(gameName, playerNames, saveGameId);
            long currentTimestamp = System.currentTimeMillis();
            GameSaveInfo info = new GameSaveInfo(saveName, creator, currentTimestamp, gameSaveData);
            this.gameSaves.put(saveGameId, info);
            this.writeGameSavesRecord();
        }
        return (this.gameSaves.containsKey(saveGameId) ? saveGameId : "");
    }

    /**
     * Generate a new UUID for a game save id.
     *
     * @return uuid for a new game save
     */
    String createNewGameSaveId() {
        String id = "";
        while (id.isEmpty() || this.gameSaves.containsKey(id)) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

}
