package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesGame;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesPlayer;
import ca.hexanome04.splendorgame.model.gameversions.cities.CityCard;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Perform the choose city action (in case when player qualifies for >1 city in one turn).
 */
public class ChooseCityAction extends Action {

    private String cityId;

    /**
     * Construct an action with the given action type.
     *
     * @param cityId id of the chosen city card
     */
    public ChooseCityAction(String cityId) {
        super(Actions.CHOOSE_CITY);
        this.cityId = cityId;
    }

    /**
     * Construct a choose city action (to be filled with info from decoder).
     */
    public ChooseCityAction() {
        this("");
    }

    @Override
    protected List<ActionResult> run(Game game, Player p) {

        ArrayList<ActionResult> result = new ArrayList<>();

        CitiesGame cgame = (CitiesGame) game;
        CitiesPlayer cplayer = (CitiesPlayer) p;

        CityCard cc = (CityCard) cgame.getCardFromId(this.cityId);

        if (cc == null) {
            throw new RuntimeException("City card with id '" + this.cityId + "' does not exist.");
        }

        if (!cgame.qualifiesCities(cplayer).contains(cc)) {
            throw new RuntimeException("City card with id '" + this.cityId + "' cannot be chosen.");
        }
        // error handling necessary ?
        cgame.takeCard(cc);
        cplayer.addCity(cc);

        if (cgame.getCurValidActions().contains(Actions.CHOOSE_CITY)) {
            result.add(ActionResult.VALID_ACTION);
        }
        result.add(ActionResult.TURN_COMPLETED);
        cgame.removeValidAction(Actions.CHOOSE_CITY);

        return result;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {
        this.cityId = jobj.get("cityId").getAsString();
        return this;
    }
}
