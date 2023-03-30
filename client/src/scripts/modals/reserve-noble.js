import { performAction } from "../actions";
import { setupSelection } from "./modals.js";
import { showError } from "../notify.js";

export const initReserveNoble = () => {

    // copy nobles
    const nobleContainer = document.querySelector("#reserve-noble-board");
    const noblesOnBoard = [];
    document.querySelectorAll("#board .board-nobles .noble-card").forEach((elm) => {
        if(elm.querySelector("img[src]")) {
            noblesOnBoard.push(elm.cloneNode(true));
        }
    });
    nobleContainer.replaceChildren(...noblesOnBoard);

    const selectionSelector = "#reserve-noble-modal .noble-card";
    setupSelection(selectionSelector);

    const confirmBtn = document.querySelector("#reserve-noble-modal .reserve-card-confirm-btn");
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
                showError(err);
            }).finally(() =>  confirmBtn.disabled = false);
    };
};
