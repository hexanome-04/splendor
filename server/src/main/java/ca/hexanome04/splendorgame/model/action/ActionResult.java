package ca.hexanome04.splendorgame.model.action;

/**
 * Constants explaining why an action completed successfully or failed.
 */
public enum ActionResult {

    /**
     * Valid action.
     */
    VALID_ACTION,

    /**
     * Invalid player attempting to take action (e.g. not their turn).
     */
    INVALID_PLAYER,

    /**
     * Invalid number of tokens given.
     */
    INVALID_TOKENS_GIVEN,

    /**
     * Further action required.
     */
    FURTHER_ACTION_REQUIRED,

    /**
     * Maximum number of cards reserved.
     */
    MAXIMUM_CARDS_RESERVED,

    /**
     * Not enough tokens left on the board to select.
     */
    NOT_ENOUGH_TOKENS_ON_BOARD,
}
