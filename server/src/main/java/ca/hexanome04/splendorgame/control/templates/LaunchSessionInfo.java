package ca.hexanome04.splendorgame.control.templates;

import java.util.List;

/**
 * Information for a newly launched session.
 */
public record LaunchSessionInfo(String gamename, String sessionName, List<PlayerInfo> players, String creator) {
}
