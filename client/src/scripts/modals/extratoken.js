import { performAction } from "../actions";
import { backButton, setupSelection, showNextModal } from "./modals.js";
import { showError } from "../notify.js";

export const showExtraTokens = () => {

    const takeConfirmBtn = document.querySelector("#take-extra-token-modal .extra-token-confirm-btn");
    takeConfirmBtn.onclick = () => {
        showNextModal("#putback-extra-token-modal");
    };

    const extraTokenSelectionSelector = "#take-extra-token-modal .board-token";
    setupSelection(extraTokenSelectionSelector, "selected-token");

    const putBackTokenSelectionSelector = "#putback-extra-token-modal .board-token";
    setupSelection(putBackTokenSelectionSelector,  "selected-token");

    const putBackConfirmBtn = document.querySelector("#putback-extra-token-modal #putback-token-confirm-btn");
    document.querySelector("#putback-extra-token-modal #putback-token-back-btn").onclick = () => {
        backButton();
    };

    putBackConfirmBtn.onclick = () => {
        putBackConfirmBtn.disabled = true;

        const selectedExtraToken = document.querySelector(`${extraTokenSelectionSelector}.selected-token`);
        const takeToken = selectedExtraToken ? selectedExtraToken.getAttribute("color") : null;

        const selectedPutBackToken = document.querySelector(`${putBackTokenSelectionSelector}.selected-token`);
        const putBackToken = selectedPutBackToken ? selectedPutBackToken.getAttribute("color") : null;

        const dataCallback = () => {
            const jsonData = {};
            if(takeToken) jsonData["takeToken"] = takeToken;
            if(putBackToken) jsonData["putBackToken"] = putBackToken;

            return jsonData;
        };

        performAction("TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER", dataCallback)
            .then((resp) => {
                if(resp.error) {
                    showError(resp.message);
                }
            }).catch((err) => {
                showError(err);
            }).finally(() =>  putBackConfirmBtn.disabled = false);
    };
};
