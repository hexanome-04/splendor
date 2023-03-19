import { performAction } from "../actions";
import { setupSelection } from "./modals.js";

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
            window.alert("You have not selected a noble!");
            confirmBtn.disabled = false;
            return;
        }

        const dataCallback = () => {
            return { "cardId": selected.getAttribute("card-id") };
        };

        performAction("RESERVE_NOBLE", dataCallback)
            .then((resp) => {
                if(resp.error) {
                    window.alert("Error: " + resp.message);
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
    };
};
