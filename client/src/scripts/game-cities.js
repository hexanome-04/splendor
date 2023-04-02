import { SETTINGS } from "./settings.js";
import { addUpdater, updateCards } from "./game.js";
import { registerPlayerUpdater, writeCardUpdate, cardsDiff } from "./history";

const updateCities = (data) => {

    console.log("UPDATING CITIES");

    // update city cards on board
    const citiesDiv = document.querySelectorAll(".cities-container .city-card img");
    let cards = [];
    cards = data.citiesDeck.visibleCards;

    // update city cards in inventories
    const curUsername = SETTINGS.getUsername();
    const playersData = data.players;
    playersData.forEach((pInfo) => {
        if(pInfo.name === curUsername) {
            // UPDATE CITY CARDS IN PLAYER INVENTORY
            const playerInv = document.querySelector("#player-inventory");
            updateCards(pInfo.cities, playerInv, ".player-inventory-cities",
                        ".city-card", "cities", "#city-card-template");
        } else {
            // UPDATE CITY CARDS IN OTHER INVENTORY
            const selector = `.other-players .other-player[pname="${pInfo.name}"]`;
            let pNode = document.querySelector(selector);
            updateCards(pInfo.cities, pNode, ".other-inventory-cities",
                        ".city-card", "cities", "#city-card-template");
        }
    });

    // update cities
    const selector = ".board-container .cities-container .city-card";

    const serverCardIds = cards.map(c => c.id);
    const currentCardIds = [...document.querySelectorAll(`${selector}[card-id]`)].map(elm => elm.getAttribute("card-id"));

    // cards that exist on board but not server side, must remove
    const outdatedCardIds = currentCardIds.filter(x => !serverCardIds.includes(x));
    outdatedCardIds.forEach(cid => {
        const oldElm = document.querySelector(`${selector}[card-id="${cid}"]`);
        oldElm.removeAttribute("card-id");
        oldElm.querySelector("img").removeAttribute("src");
    });

    const missingCards = cards.filter(c => !currentCardIds.includes(c.id));
    missingCards.forEach(card => {
        // select empty ones to fill
        const emptyCardElm = document.querySelector(`${selector}:not([card-id])`);
        emptyCardElm.setAttribute("card-id", card.id);
        emptyCardElm.querySelector("img").setAttribute("src", `/images/cities/${card.id}.jpg`);
        emptyCardElm.setAttribute("had-card", "true");
    });
};


addUpdater(updateCities);

// history updates
const updatePlayerCitiesHistory = (oldState, newState) => {
    writeCardUpdate("Obtained", cardsDiff(oldState.cities, newState.cities)[0], "cities");
};
registerPlayerUpdater(updatePlayerCitiesHistory);
