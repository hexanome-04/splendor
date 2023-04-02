import { performAction } from "../actions";
import { SETTINGS } from "../settings.js";
import { setupSelection } from "./modals.js";
import { showError } from "../notify.js";

export const showSatchelBonuses = (data) => {
    const pUsername = SETTINGS.getUsername();

    const confirmBtn = document.querySelector("#choose-satchel-modal .satchel-confirm-btn");

    const bonusSelectionSelector = "#choose-satchel-modal .board-token";

    setupSelection(bonusSelectionSelector, "selected-token");

    // disable the tokens that the player cannot select
    data.players.forEach((pInfo) => {
        if(pInfo.name === pUsername) {
            const allTokens = document.querySelectorAll(`${bonusSelectionSelector}`);
            allTokens.forEach((elm) => {
                const color = elm.getAttribute("color");
                if(pInfo.bonuses[color] > 0) {
                    elm.classList.add("show");
                } else {
                    elm.classList.remove("show");
                }
            });
        }
    });

    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selectedBonus = document.querySelector(`${bonusSelectionSelector}.selected-token`);

        console.log("bonusSelector from document.query: " + bonusSelectionSelector);

        if(!selectedBonus) {
            // no card has been selected, error
            showError("You have not assigned this satchel card a bonus!");
            confirmBtn.disabled = false;
            return;
        }
        // assume the satchel card is the latest satchel card added to the player's inventory
        // (it shouldn't matter which satchel card we set right?)
        const cardId = document.querySelector("#player-inventory .player-inventory-card-drawer .player-inventory-card[satchel='true']").getAttribute("card-id");
        console.log("cardId from document.query: " + bonusSelectionSelector);

        const dataCallback = () => {
            return {
                "cardId": cardId,
                "selected": selectedBonus.getAttribute("color")
            };
        };

        performAction("CHOOSE_SATCHEL_TOKEN", dataCallback)
            .then((resp) => {
                if(resp.error) {
                    showError(resp.message);
                }
            }).catch((err) => {
                showError(err.toString());
            }).finally(() =>  confirmBtn.disabled = false);
    };
};
