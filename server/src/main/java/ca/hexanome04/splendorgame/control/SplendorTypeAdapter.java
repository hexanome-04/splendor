package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.GameServiceInfo;
import ca.hexanome04.splendorgame.model.DevelopmentCard;
import ca.hexanome04.splendorgame.model.HideFromClient;
import ca.hexanome04.splendorgame.model.OrientDevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.RegDevelopmentCard;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesGame;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesPlayer;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientPlayer;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Creates a Gson with proper type adapters to handle serialization and deserialization.
 */
@Component
public class SplendorTypeAdapter {

    private String gsName;

    /**
     * Construct new SplendorTypeAdapter.
     *
     * @param gsName base game service name
     */
    public SplendorTypeAdapter(@Value("${gs.name}") String gsName) {
        this.gsName = gsName;
    }

    /**
     * Construct a Gson instance with proper type adapters for subclasses in Splendor.
     *
     * @return Gson instance
     */
    public static Gson createGson() {
        var playerAdapterFactory = RuntimeTypeAdapterFactory.of(Player.class)
                .registerSubtype(OrientPlayer.class)
                .registerSubtype(CitiesPlayer.class)
                .registerSubtype(TradingPostsPlayer.class);

        var gameAdapterFactory = RuntimeTypeAdapterFactory.of(Game.class)
                .registerSubtype(OrientGame.class)
                .registerSubtype(CitiesGame.class)
                .registerSubtype(TradingPostsGame.class);

        var cardAdapterFactory = RuntimeTypeAdapterFactory.of(DevelopmentCard.class)
                .registerSubtype(RegDevelopmentCard.class)
                .registerSubtype(OrientDevelopmentCard.class);

        return new GsonBuilder().registerTypeAdapterFactory(playerAdapterFactory)
                .registerTypeAdapterFactory(gameAdapterFactory)
                .registerTypeAdapterFactory(cardAdapterFactory)
                .create();
    }

    /**
     * Initialize a Gson instance that hides unnecessary fields from the client.
     *
     * @return new gson instance
     */
    public static Gson newClientGson() {
        ExclusionStrategy strategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }

            @Override
            public boolean shouldSkipField(FieldAttributes field) {
                return field.getAnnotation(HideFromClient.class) != null;
            }
        };

        return new GsonBuilder().addSerializationExclusionStrategy(strategy).create();
    }

    /**
     * Retrieve the corresponding game service name from the game version.
     *
     * @param gameVersion game version
     * @return registered game service name
     */
    public String getGameName(GameVersions gameVersion) {
        // yeah idk why this is in this class but there's no where else to put it
        return gsName + "_" + gameVersion;
    }
}
