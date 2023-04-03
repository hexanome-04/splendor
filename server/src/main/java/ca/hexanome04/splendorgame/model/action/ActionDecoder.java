package ca.hexanome04.splendorgame.model.action;

import ca.hexanome04.splendorgame.model.SplendorException;
import ca.hexanome04.splendorgame.model.action.actions.*;
import com.google.gson.JsonObject;

/**
 * Decodes actions from a given identifier.
 */
public class ActionDecoder {

    /**
     * Construct an action decoder.
     */
    public ActionDecoder() {
        // here
    }

    /**
     * Create an action from its enum constant name.
     *
     * @param identifier enum constant name
     * @param gson data that the action contains
     * @return a new action (null if invalid name)
     */
    public static Action createAction(String identifier, JsonObject gson) {
        try {
            Actions actionType = Actions.valueOf(identifier);

            return switch (actionType) {
                case BUY_CARD -> new BuyCardAction().decodeAction(gson);
                case TAKE_TOKEN -> new TakeTokenAction().decodeAction(gson);
                case RESERVE_CARD -> new ReserveCardAction().decodeAction(gson);
                case CHOOSE_NOBLE -> new ChooseNobleAction().decodeAction(gson);
                case CASCADE_1 -> new CascadeTier1Action().decodeAction(gson);
                case CASCADE_2 -> new CascadeTier2Action().decodeAction(gson);
                case RESERVE_NOBLE -> new ReserveNobleAction().decodeAction(gson);
                case CHOOSE_SATCHEL_TOKEN -> new ChooseTokenTypeAction().decodeAction(gson);
                case TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER -> new TakeExtraTokenAfterPurchasePowerAction().decodeAction(gson);
                case CHOOSE_CITY -> new ChooseCityAction().decodeAction(gson);
            };

        } catch (Exception e) {
            throw new SplendorException("Invalid data given for action.");
        }
    }

}
