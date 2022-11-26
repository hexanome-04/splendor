package ca.hexanome04.splendorgame.control.templates;

import java.util.List;

/**
 * Information for a newly launched session.
 */
public record LaunchSessionInfo(String gameServer,  List<PlayerInfo> players, String creator, String savegame) {
}
