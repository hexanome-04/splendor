package ca.hexanome04.splendorgame.model;

/**
 * Class representing the orient development cards.
 */
public class OrientDevelopmentCard extends DevelopmentCard {

    private final Boolean reserveNoble;
    private final CascadeType cascadeType;
    private final Integer cascadeAmt;


    /**
     * Creates an orient development card by calling super on the abstract development card class.
     *
     * @param tokenType    The type of token associated to this card.
     *
     * @param bonus        The token bonus associated to this card
     *
     * @param cascadeType  The type of cascade associated to this card.
     *
     * @param cascadeAmt   The amount of cascaded cards associated to this card.
     *
     * @param reserveNoble Whether this development card allows you to reserve a noble.
     */
    public OrientDevelopmentCard(TokenType tokenType, Integer bonus, CascadeType cascadeType,
                                 Integer cascadeAmt, Boolean reserveNoble) {
        super(tokenType, bonus);
        this.reserveNoble = reserveNoble;
        this.cascadeType = cascadeType;
        this.cascadeAmt = cascadeAmt;
    }

}
