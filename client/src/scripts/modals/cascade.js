import { setupSelection } from "./modals.js";
import { performAction } from "../actions";

export const showCascadeCards = (tier) => {

    const confirmBtn = document.querySelector("#cascade-modal .cascade-confirm-btn");

    // tier looks like 'CASCADE_{n}'
    const tierNum = tier.slice(-1)[0];
    // Instead of showing all cards, only show the selectable rows
    const levelRow = document.querySelectorAll(`#board .board-cards .board-cards-level${tierNum}`);

    // clear if already has
    const modalCardRows = document.querySelector("#cascade-board .modal-board-cards");
    modalCardRows.innerHTML = "";

    // setup cards for selection
    levelRow.forEach((elm) => {
        const cNode = elm.cloneNode(true);
        modalCardRows.appendChild(cNode);
    });
    document.getElementById("cascade-text").textContent = `Please select a card from level ${tierNum}.`;

    const cardsSelectionSelector = "#cascade-modal .modal-board-cards .board-card-dev";

    setupSelection(cardsSelectionSelector);

    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selectedCard = document.querySelector(`${cardsSelectionSelector}.selected`);
        if(!selectedCard) {
            // no card has been selected, error
            window.alert("You have not selected a card!");
            return;
        }

        const dataCallback = () => {
            return { "cardId": selectedCard.getAttribute("card-id") };
        };

        performAction(tier, dataCallback)
            .then((resp) => {
                if(resp.error) {
                    window.alert("Error: " + resp.message);
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
    };
};