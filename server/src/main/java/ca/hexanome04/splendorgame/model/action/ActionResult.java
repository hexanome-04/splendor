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
     * Invalid token chosen for satchel card assignment.
     */
    INVALID_TOKEN_CHOSEN,

    /**
     * Maximum number of cards reserved.
     */
    MAXIMUM_CARDS_RESERVED,

    /**
     * Not enough tokens left on the board to select.
     */
    NOT_ENOUGH_TOKENS_ON_BOARD,

    /**
     * Player has hit maximum token threshold in inventory.
     */
    MAXIMUM_TOKENS_IN_INVENTORY,

    /**
     * Player has qualified for 2+ nobles and must choose noble.
     */
    MUST_CHOOSE_NOBLE,

    /**
     * Player has received an Orient card allowing them to reserve a noble.
     */
    MUST_RESERVE_NOBLE,

    /**
     * Player has received an Orient card allowing them to choose a cascade card of tier 1.
     */
    MUST_CHOOSE_CASCADE_CARD_TIER_1,

    /**
     * Player has received an Orient card allowing them to choose a cascade card of tier 2.
     */
    MUST_CHOOSE_CASCADE_CARD_TIER_2,

    /**
     * Player has received an Orient card of type satchel, so they must choose a token type.
     */
    MUST_CHOOSE_TOKEN_TYPE,

}
