package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import java.util.List;

public class BuyCardAction extends Action {

    private final String buyCardID;
    private final List<Token> selectedTokens;

    public BuyCardAction(String buyCardID, List<Token> selectedTokens) {
        this.buyCardID = buyCardID;
        this.selectedTokens = selectedTokens;
    }


    @Override
    public ActionResult executeAction(SplendorGame game, Player player) {

        SplendorBoard board = game.getBoardState();

        // get card from board
        // should be a development card (hopefully)
        RegDevelopmentCard dc = (RegDevelopmentCard) board.getCardFromID(this.buyCardID);

        if (!dc.isPurchasable(player, selectedTokens)) {
            return ActionResult.NOT_ENOUGH_TOKENS;
        }

        // no error handling
        board.takeCard(dc);
        player.buyCard(dc);

        return ActionResult.VALID_ACTION;
    }

}
