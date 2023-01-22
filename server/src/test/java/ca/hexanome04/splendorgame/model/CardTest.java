package ca.hexanome04.splendorgame.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
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
                put(TokenType.Green, 0);
                put(TokenType.White, 0);
                put(TokenType.Blue, 0);
                put(TokenType.Brown, 0);
                put(TokenType.Red, 4);
            }},
            "234"
    );

    // Dummy regular development card for testing.
    private OrientDevelopmentCard dummyOrient = new OrientDevelopmentCard(
            CardTier.TIER_2,
            TokenType.Red,
            2,
            CascadeType.Tier1,
            false,
            1,
            CostType.Token,
            new HashMap<>(){{
                put(TokenType.Green, 3);
                put(TokenType.White, 0);
                put(TokenType.Blue, 2);
                put(TokenType.Brown, 1);
                put(TokenType.Red, 1);
            }},
            new HashMap<>(),
            "234"
    );

    /**
     * Test the getCOstType method.
     */
    @DisplayName("Test the getCostType method.")
    @Test
    public void testGetCostType() {
        CostType costType = dummyNoble.getCostType();
        assertEquals(CostType.Bonus, costType);
    }

    /**
     * Test the getPrestigePoints method.
     */
    @DisplayName("Test the getPrestigePoints method.")
    @Test
    public void testGetPrestigePoints() {
        int prestige = dummyNoble.getPrestigePoints();
        assertEquals(3, prestige);
    }

    /**
     * Test the getReserveNoble method for OrientDevelopmentCard.
     */
    @DisplayName("Test the getReserveNoble method for OrientDevelopmentCard.")
    @Test
    public void testGetReserveNoble() {
        assertEquals(dummyOrient.getReserveNoble(), false);
    }

    /**
     * Test the isCascade method for OrientDevelopmentCard.
     */
    @DisplayName("Test the isCascade method for OrientDevelopmentCard.")
    @Test
    public void testIsCascade() {
        assertEquals(dummyOrient.isCascade(), true);
    }

    /**
     * Test the getCascadeType method for OrientDevelopmentCard.
     */
    @DisplayName("Test the getCascadeType method for OrientDevelopmentCard.")
    @Test
    public void testGetCascadeType() {
        assertEquals(dummyOrient.getCascadeType(), CascadeType.Tier1);
    }

    /**
     * Test the getBurnBonuses method for OrientDevelopmentCard.
     */
    @DisplayName("Test the getBurnBonuses method for OrientDevelopmentCard.")
    @Test
    public void testGetBurnBonusCost() {
        assertEquals(dummyOrient.getBurnBonusCost(), new HashMap<>());
    }

    /**
     * Ensure isPurchasable correctly checks if player can purchase card.
     *
     * @throws FileNotFoundException Throws exception if loading from file fails
     */
    @DisplayName("Ensure isPurchasable correctly checks if player can purchase card.")
    @Test
    public void testIsPurchasable() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokens = new HashMap<>();
        tokens.put(TokenType.Red, 1);
        p1.addTokens(tokens);

        // Make sure tokens added to player are successfully retrieved by getter
        assertThat(dummyReg.isPurchasable(p1, tokens)).isEqualTo(true);
    }

}
