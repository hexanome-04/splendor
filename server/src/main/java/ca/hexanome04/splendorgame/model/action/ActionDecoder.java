package ca.hexanome04.splendorgame.model.action;

import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
import com.google.gson.Gson;

public class ActionDecoder {

    /**
     * Create an action from its enum constant name.
     *
     * @param identifier enum constant name
     * @param gson data that the action contains
     * @return a new action (null if invalid name)
     */
    public static Action createAction(String identifier, Gson gson) {
        try {
            Actions actionType = Actions.valueOf(identifier);

            return switch(actionType) {
                case BUY_CARD -> new BuyCardAction().decodeAction(gson);
            };
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
