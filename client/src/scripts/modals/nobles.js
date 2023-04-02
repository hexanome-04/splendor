import { performAction } from "../actions";
import { setupSelection } from "./modals.js";
import { showError } from "../notify.js";
import { SETTINGS } from "../settings";

const qualifyingNobles = (data) => {
    const nobleIds = [];
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

    // mayhaps the same logic in the backend
    const nobles = [...playerData.reservedNobles, ...data.nobleDeck.visibleCards];
    nobles.forEach((noble) => {
        let notEnoughBonusesCounter = 0;

        for(const tokenType of Object.keys(noble.tokenCost)) {
            // subtract player's bonuses of this TokenType from noble card cost
            const bonusRemaining = noble.tokenCost[tokenType] - playerData.bonuses[tokenType];
            if (bonusRemaining > 0) {
                notEnoughBonusesCounter++;
            }
        }

        if (notEnoughBonusesCounter === 0) {
            nobleIds.push(noble.id);
        }
    });

    return nobleIds;
};

const createNobleCards = (nobleIds) => {
    const nobles = [];
    nobleIds.forEach(cid => {
        const node = document.createElement("div");
        node.setAttribute("card-id", cid);
        node.classList.add("noble-card");
        const img = document.createElement("img");
        img.setAttribute("src", `/images/nobles/${cid}.jpg`);
        node.appendChild(img);
        nobles.push(node);
    });

    return nobles;
};

export const initChooseNoble = (data) => {
    // copy nobles
    const nobleContainer = document.querySelector(`#choose-noble-modal .reserve-noble-board`);

    const noblesOnBoard = createNobleCards(qualifyingNobles(data));

    nobleContainer.replaceChildren(...noblesOnBoard);

    const selectionSelector = `#choose-noble-modal .noble-card`;
    setupSelection(selectionSelector);

    const confirmBtn = document.querySelector(`#choose-noble-modal .reserve-card-confirm-btn`);
    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selected = document.querySelector(`${selectionSelector}.selected`);
        if(!selected) {
            showError("You have not selected a noble!");
            confirmBtn.disabled = false;
            return;
        }

        const dataCallback = () => {
            return { "cardId": selected.getAttribute("card-id") };
        };

        performAction("CHOOSE_NOBLE", dataCallback)
            .then((resp) => {
                if(resp.error) {
                    showError(resp.message);
                }
            }).catch((err) => {
                showError(err.toString());
            }).finally(() =>  confirmBtn.disabled = false);
    };
};

export const initReserveNoble = () => {

    // copy nobles
    const nobleContainer = document.querySelector(`#reserve-noble-modal .reserve-noble-board`);
    const noblesOnBoard = [];
    document.querySelectorAll("#board .board-nobles .noble-card").forEach((elm) => {
        if(elm.querySelector("img[src]")) {
            noblesOnBoard.push(elm.cloneNode(true));
        }
    });
    nobleContainer.replaceChildren(...noblesOnBoard);

    const selectionSelector = `#reserve-noble-modal .noble-card`;
    setupSelection(selectionSelector);

    const confirmBtn = document.querySelector(`#reserve-noble-modal .reserve-card-confirm-btn`);
    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selected = document.querySelector(`${selectionSelector}.selected`);
        if(!selected) {
            showError("You have not selected a noble!");
            confirmBtn.disabled = false;
            return;
        }

        const dataCallback = () => {
            return { "cardId": selected.getAttribute("card-id") };
        };

        performAction("RESERVE_NOBLE", dataCallback)
            .then((resp) => {
                if(resp.error) {
                    showError(resp.message);
                }
            }).catch((err) => {
                showError(err.toString());
            }).finally(() =>  confirmBtn.disabled = false);
    };
};
