import { performAction } from "../actions";
import { setupSelection } from "./modals.js";
import { showError } from "../notify.js";
import { SETTINGS } from "../settings";

const createCityCards = (cityIds) => {
    const cities = [];
    cityIds.forEach(cid => {
        const node = document.createElement("div");
        node.setAttribute("card-id", cid);
        node.classList.add("city-card");
        const img = document.createElement("img");
        img.setAttribute("src", `/images/cities/${cid}.jpg`);
        node.appendChild(img);
        cities.push(node);
    });

    return cities;
};

const qualifyingCities = (data) => {
    const curUsername = SETTINGS.getUsername();

    let playerData = null; // sus way to get current user info lol
    data.players.forEach((pInfo) => {
        if(pInfo.name === curUsername) {
            playerData = pInfo;
        }
    });

    if(!playerData) {
        return [];
    }

    return playerData.citiesQualifiedFor.map(c => c.id);
};

export const initChooseCity = (data) => {
    const cityContainer = document.querySelector(`#choose-city-modal .choose-city-board`);

    const citiesOnBoard = createCityCards(qualifyingCities(data));

    cityContainer.replaceChildren(...citiesOnBoard);

    const selectionSelector = `#choose-city-modal .city-card`;
    setupSelection(selectionSelector);

    const confirmBtn = document.querySelector(`#choose-city-modal .reserve-card-confirm-btn`);
    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selected = document.querySelector(`${selectionSelector}.selected`);
        if(!selected) {
            showError("You have not selected a city!");
            confirmBtn.disabled = false;
            return;
        }

        const dataCallback = () => {
            return { "cityId": selected.getAttribute("card-id") };
        };

        performAction("CHOOSE_CITY", dataCallback)
            .then((resp) => {
                if(resp.error) {
                    showError(resp.message);
                }
            }).catch((err) => {
                showError(err);
            }).finally(() =>  confirmBtn.disabled = false);
    };
};