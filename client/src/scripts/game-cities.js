import { SETTINGS } from "./settings.js";
import { addUpdater, updateCards } from "./game.js";

const updateCities = (data) => {

    console.log("UPDATING CITIES");

    // update city cards on board
    const citiesDiv = document.querySelectorAll(".cities-container .city-card img");
    let cards = [];
    cards = data.citiesDeck.visibleCards;

    citiesDiv.forEach((elm, index) => {
        const curSrc = elm.getAttribute("src");
        if(index < cards.length) {
            if(curSrc !== cards[index]) {
                const imgSrc = `/images/cities/${cards[index].id}.jpg`;
                elm.parentElement.setAttribute("card-id", cards[index].id);
                elm.setAttribute("src", imgSrc);
            }
        } else {
            elm.parentElement.removeAttribute("card-id");
            elm.removeAttribute("src");
        }
    });
    
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

};


addUpdater(updateCities);
