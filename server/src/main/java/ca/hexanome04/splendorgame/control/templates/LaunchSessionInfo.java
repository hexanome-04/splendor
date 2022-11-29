package ca.hexanome04.splendorgame.control.templates;

import java.util.List;

/**
 * Construct a launch session info.
 *
 * @param gameServer game service name
 * @param players list of players
 * @param creator creator name
 * @param savegame save game name
 */
public record LaunchSessionInfo(String gameServer,  List<PlayerInfo> players, String creator, String savegame) {
}
