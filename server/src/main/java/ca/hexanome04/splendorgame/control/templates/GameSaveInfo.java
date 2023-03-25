package ca.hexanome04.splendorgame.control.templates;

/**
 * Defines extra information for a game save, also includes the backing game save.
 *
 * @param name game save name
 * @param creator creator of game save
 * @param timestamp timestamp of when save occurred
 * @param gameSaveData game save data
 */
public record GameSaveInfo(String name, String creator, long timestamp, GameSaveData gameSaveData) {
}
