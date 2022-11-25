package ca.hexanome04.splendorgame.control.templates;

/**
 * Hold information about the game service.
 *
 * @param name game service name
 * @param displayName game service display name
 * @param location location (url) to game service
 * @param minSessionPlayers minimum amount of players needed
 * @param maxSessionPlayers maximum amount of players allowed
 * @param webSupport no idea
 */
public record GameServiceInfo(String name, String displayName, String location,
                              int minSessionPlayers, int maxSessionPlayers, String webSupport) {}
