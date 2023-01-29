package ca.hexanome04.splendorgame.model.action;

import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
import ca.hexanome04.splendorgame.model.action.actions.ChooseNobleAction;
import ca.hexanome04.splendorgame.model.action.actions.ReserveCardAction;
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
                case RESERVE_CARD -> new ReserveCardAction().decodeAction(gson);
                case CHOOSE_NOBLE -> new ChooseNobleAction().decodeAction(gson);
            };
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
