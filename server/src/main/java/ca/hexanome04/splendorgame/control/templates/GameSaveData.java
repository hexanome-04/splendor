package ca.hexanome04.splendorgame.control.templates;

import java.util.List;

/**
 * Class holding the necessary variables to register a save game to the LS.
 *
 * @param gamename game service name
 * @param players players originally in game (no need for same exact players, same num tho)
 * @param savegameid id of the saved game
 */
public record GameSaveData(String gamename, List<String> players, String savegameid) {
}
