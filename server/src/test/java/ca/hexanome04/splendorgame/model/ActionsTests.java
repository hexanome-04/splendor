package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Tests for doing actions in a game.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ActionsTests {

    @DisplayName("Ensure players cannot buy a card with insufficient tokens in their inventory.")
    @Test
    void testPlayerBuyCardInsufficientTokens() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (id = 0)
        Player p1 = game.getPlayerFromName("0");

        List<Token> tokensToUse = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            tokensToUse.add(new Token(TokenType.Red));
        }

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // should be no more tier 1 cards available to be purchased
        assertThat(result).isEqualTo(ActionResult.INVALID_TOKENS_GIVEN);
    }

}
