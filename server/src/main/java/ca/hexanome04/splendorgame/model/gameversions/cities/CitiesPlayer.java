package ca.hexanome04.splendorgame.model.gameversions.cities;

import ca.hexanome04.splendorgame.model.gameversions.orient.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A concrete implementation of Player for Cities game.
 */
public class CitiesPlayer extends OrientPlayer {

    List<CityCard> cities = new ArrayList<>();
    List<CityCard> citiesQualifiedFor = new ArrayList<>();

    /**
     * Creates a Player object with a given name and colour.
     *
     * @param name   The name of the player.
     * @param colour The color associated to the player.
     */
    public CitiesPlayer(String name, String colour) {
        super(name, colour);
    }

    /**
     * Adds a city to the player.
     *
     * @param city being added to player
     */
    public void addCity(CityCard city) {
        cities.add(city);
    }

    /**
     * Return list of cities earned by player.
     *
     * @return cities earned by player
     */
    public List<CityCard> getCities() {
        return new ArrayList<>(cities);
    }

    /**
     * Adds a city qualified for to the player.
     *
     * @param city being added to player
     */
    public void addCityQualifiedFor(CityCard city) {
        citiesQualifiedFor.add(city);
    }

    /**
     * Return list of cities qualified for by player.
     *
     * @return cities qualified for by player
     */
    public List<CityCard> getCitiesQualifiedFor() {
        return new ArrayList<>(citiesQualifiedFor);
    }

}
