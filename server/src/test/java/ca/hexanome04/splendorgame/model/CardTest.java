package ca.hexanome04.splendorgame.model;

import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Test class for Card.
 * This class tests all applicable methods on all three types of card:
 * Noble, regular development, orient development.
 */
public class CardTest {

    // Dummy noble card used for testing.
    private NobleCard dummyNoble = new NobleCard(
            3,
            CostType.Bonus,
            new HashMap<>(){{
                put(TokenType.Green, 0);
                put(TokenType.White, 0);
                put(TokenType.Blue, 4);
                put(TokenType.Brown, 3);
                put(TokenType.Red, 4);
            }},
            "123"
    );
    // Dummy regular development card for testing.
    private RegDevelopmentCard dummyReg = new RegDevelopmentCard(
            CardTier.TIER_2,
            TokenType.Red,
            2,
            2,
            CostType.Token,
            new HashMap<>(){{
                put(TokenType.Green, 3);
                put(TokenType.White, 0);
                put(TokenType.Blue, 2);
                put(TokenType.Brown, 1);
                put(TokenType.Red, 1);
            }},
            "234"
    );

    /**
     * Testing the getCostType method.
     */
    @Test
    public void testGetCostType() {
        CostType costType = dummyNoble.getCostType();
        assertEquals(CostType.Bonus, costType);
    }

    /**
     * Test the getPrestigePoints method.
     */
    @Test
    public void testGetPrestigePoints() {
        int prestige = dummyNoble.getPrestigePoints();
        assertEquals(3, prestige);
    }

}
