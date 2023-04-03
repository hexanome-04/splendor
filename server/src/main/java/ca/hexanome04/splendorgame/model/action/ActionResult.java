package ca.hexanome04.splendorgame.model.action;

/**
 * Constants explaining why an action completed successfully or failed.
 */
public enum ActionResult {

    /**
     * Turn completed.
     */
    TURN_COMPLETED,

    /**
     * Valid action.
     */
    VALID_ACTION,

    /**
     * Invalid player attempting to take action (e.g. not their turn).
     */
    INVALID_PLAYER("It is not this players turn."),

    /**
     * Invalid number of tokens given.
     */
    INVALID_TOKENS_GIVEN("The tokens chosen are invalid."),

    /**
     * Invalid token chosen for satchel card assignment.
     */
    INVALID_TOKEN_CHOSEN("Invalid token type chosen for assigning satchel card bonus."),

    /**
     * Maximum number of cards reserved.
     */
    MAXIMUM_CARDS_RESERVED("You already have the maximum amount of reserved cards!"),

    /**
     * Not enough tokens left on the board to select.
     */
    NOT_ENOUGH_TOKENS_ON_BOARD("There are not enough tokens on the board for you to take."),

    /**
     * Player has hit maximum token threshold in inventory.
     */
    MAXIMUM_TOKENS_IN_INVENTORY("You have the maximum amount of tokens in your inventory!"),

    /**
     * Player does not have enough tokens in inventory.
     */
    NOT_ENOUGH_TOKENS_IN_INVENTORY("You do not have enough tokens in your inventory."),

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

    /**
     * Player has purchased a development card, so they can take an extra token using power 1.
     */
    MUST_TAKE_EXTRA_TOKEN_AFTER_PURCHASE,

    /**
     * Player has qualified for 2+ cities and must choose city.
     */
    MUST_CHOOSE_CITY;


    private final String description;

    /**
     * Construct a new Action Result.
     *
     * @param description description of the action result
     */
    ActionResult(String description) {
        this.description = description;
    }

    /**
     * Construct a new Action Result with no description.
     */
    ActionResult() {
        this("");
    }

    /**
     * Retrieve the description of the action result.
     *
     * @return action result description
     */
    public String getDescription() {
        return this.description;
    }
}
