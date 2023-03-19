import { showCascadeCards } from "./cascade.js";
import { showExtraTokens } from "./extratoken.js";
import { initReserveNoble } from "./reserve-noble.js";
import { showSatchelBonuses } from "./satchel.js";

/**
 * Setup a selection for selectable items.
 * 
 * @param {*} selector selector for all selectable items 
 * @param {*} selectedSelector selector for when item is selected
 */
export const setupSelection = (selector, selectedSelector) => {
    const selectedClass = selectedSelector ? selectedSelector : "selected";
    // remove selection
    const oldSelected = document.querySelector(`${selector}.${selectedClass}`);
    if(oldSelected) {
        oldSelected.classList.remove(selectedClass);
    }
    document.querySelectorAll(selector).forEach((elm) => {
        elm.onclick = () => {
            // get the current selected token (should have .selected- as a class)
            const selectedExtraToken = document.querySelector(`${selector}.${selectedClass}`);
            // remove the selection
            if(selectedExtraToken) selectedExtraToken.classList.remove(selectedClass);

            // only add to clicked element if the old selected was not the clicked element
            if(selectedExtraToken !== elm) {
                elm.classList.add(selectedClass);
            }
        };
    });
}

/**
 * Attempts to perform a follow up action if required.
 * 
 * @param {*} data server data
 * @returns true if follow up action is required
 */
export function performFollowUpAction(data) {
    const actions = data.curValidActions;

    if (actions.length == 0) {
        return;
    }

    let modalSelector = "";
    switch(actions[0]) {
        case "CASCADE_1":
        case "CASCADE_2":
            showCascadeCards(actions[0]);
            modalSelector = "#cascade-modal";
            break;
        case "CHOOSE_SATCHEL_TOKEN":
            showSatchelBonuses(data);
            modalSelector = "#choose-satchel-modal";
            break;
        case "TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER":
            showExtraTokens();
            modalSelector = "#take-extra-token-modal";
            break;
        case "RESERVE_NOBLE":
            initReserveNoble();
            modalSelector = "#reserve-noble-modal";
            break;
        default:
            // its fine
    }

    if(modalSelector !== "") {
        verifyNoModals();
        showNextModal(modalSelector);
        return true;
    }
    return false;
}

const navContext = [];

const showModal = (selector) => {
    document.querySelector(selector).classList.add("show");
};

const closeModal = (selector) => {
    document.querySelector(selector).classList.remove("show");
};

export const showNextModal = (selector) => {
    // close current
    if(navContext.length > 0) {
        closeModal(navContext[navContext.length-1]);
    }

    // push new
    navContext.push(selector);
    showModal(navContext[navContext.length-1]);
};


export const backButton = () => {
    // pop top of stack (current modal), close it
    closeModal(navContext.pop());

    // open the previous one, if any
    if(navContext.length > 0) {
        showModal(navContext[navContext.length-1]);
    }
};

export function startTurn() {
    // attempt to start the turn if no modals are open and it is still your turn.
    if(navContext.length == 0) {
        showNextModal("#your-turn-modal")
    }
}

/**
 * Verify no turn modals are open by accident.
 */
export function verifyNoModals() {
    if(navContext.length != 0) {
        while(navContext.length > 0) {
            // ensure the modals get closed if they're open
            closeModal(navContext.pop());
        }
    }
}
