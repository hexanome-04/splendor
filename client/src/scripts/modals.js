import { SETTINGS } from "./settings.js";

const purchaseBtn = document.getElementById("purchase-btn");
const reserveBtn = document.getElementById("reserve-btn");
const getTokensBtn = document.getElementById("get-tokens-btn");


// --------------------------------------------------

// Reserve/buy cards selection
function setupSelectionCards(selector) {
    document.querySelectorAll(selector).forEach((elm) => {
        elm.onclick = () => {
            // get the current selected card (should have .selected-card as a class)
            const selectedCard = document.querySelector(`${selector}.selected-card`);
            // remove the selection
            if(selectedCard) selectedCard.classList.remove("selected-card");

            // only add to clicked element if the old selected was not the clicked element
            if(selectedCard !== elm) {
                elm.classList.add("selected-card");
            }
        };
    });
}

const showReservableDevCards = () => {

    const confirmBtn = document.querySelector("#reserve-card-modal .reserve-card-confirm-btn");

    // literally just duplicate them from the board
    const cardRows = document.querySelectorAll("#board .board-cards .board-cards-row");

    // clear if already has
    const modalCardRows = document.querySelector("#reserve-card-board .modal-board-cards");
    modalCardRows.innerHTML = "";

    cardRows.forEach((elm) => {
        const cNode = elm.cloneNode(true);
        modalCardRows.appendChild(cNode);
    });

    const cardsSelectionSelector = "#reserve-card-modal .modal-board-cards .board-card-dev";

    setupSelectionCards(cardsSelectionSelector);

    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selectedCard = document.querySelector(`${cardsSelectionSelector}.selected-card`);
        if(!selectedCard) {
            // no card has been selected, error
            window.alert("You have not selected a card to reserve!");
            return;
        }
        const cardId = selectedCard.getAttribute("card-id");

        SETTINGS.verifyCredentials().then(() => {
            const windowParams = (new URL(document.location)).searchParams;
            const sessionId = windowParams.get("sessionId");

            const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/RESERVE_CARD`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();


            const jsonData = {
                "cardId": cardId,
            };

            fetch(url, {
                method: "PUT",
                headers: { "content-type": "application/json" },
                body: JSON.stringify(jsonData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
        });
    };

};

// CASCADE CARDS
const showCascadeCards = (tier) => {

    const confirmBtn = document.querySelector("#cascade-modal .cascade-confirm-btn");

    // Instead of showing all cards, only show the selectable rows
    const level1Row = document.querySelectorAll("#board .board-cards .board-cards-level1");
    const level2Row = document.querySelectorAll("#board .board-cards .board-cards-level2");

    // clear if already has
    const modalCardRows = document.querySelector("#cascade-board .modal-board-cards");
    modalCardRows.innerHTML = "";
        
    
    if (tier == "CASCADE_1") {
        level1Row.forEach((elm) => {
            const cNode = elm.cloneNode(true);
            modalCardRows.appendChild(cNode);
        });
        document.getElementById("cascade-text").textContent = "Please select a card from level 1.";
    }

    if (tier == "CASCADE_2") {
        level2Row.forEach((elm) => {
            const cNode = elm.cloneNode(true);
            modalCardRows.appendChild(cNode);
        });
        document.getElementById("cascade-text").textContent = "Please select a card from level 2.";
    }
    

    const cardsSelectionSelector = "#cascade-modal .modal-board-cards .board-card-dev";

    setupSelectionCards(cardsSelectionSelector);

    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selectedCard = document.querySelector(`${cardsSelectionSelector}.selected-card`);
        if(!selectedCard) {
            // no card has been selected, error
            window.alert("You have not selected a card!");
            return;
        }
        const cardId = selectedCard.getAttribute("card-id");

        SETTINGS.verifyCredentials().then(() => {
            const windowParams = (new URL(document.location)).searchParams;
            const sessionId = windowParams.get("sessionId");

            const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/${tier}`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();


            const jsonData = {
                "cardId": cardId,
            };

            fetch(url, {
                method: "PUT",
                headers: { "content-type": "application/json" },
                body: JSON.stringify(jsonData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
        });
    };

};


/**
 * Get the list of tokens when taking / putting back tokens or purchasing a development card
 * @returns [{ token: count }]
 */
const getTokensList = (selector) => {
    const tokensCountNodes = document.querySelectorAll(selector);

    const tokens = {};

    tokensCountNodes.forEach(node => {
        const tokenType = node.getAttribute("jewel-color");
        const count = node.parentNode.count;

        tokens[tokenType] = count;
    });

    return tokens;
};

const setBoardTokens = (modal) => {
    const boardNode = document.querySelector("#board .board-tokens");
    const modalNode = document.querySelector(modal);
    modalNode.querySelector(".red-token > span").textContent = boardNode.querySelector(".red-token > span").textContent;
    modalNode.querySelector(".blue-token > span").textContent = boardNode.querySelector(".blue-token > span").textContent;
    modalNode.querySelector(".green-token > span").textContent = boardNode.querySelector(".green-token > span").textContent;
    modalNode.querySelector(".white-token > span").textContent = boardNode.querySelector(".white-token > span").textContent;
    modalNode.querySelector(".brown-token > span").textContent = boardNode.querySelector(".brown-token > span").textContent;
    modalNode.querySelector(".gold-token > span").textContent = boardNode.querySelector(".gold-token > span").textContent;
};

const takeTokens = () => {
    
    // clear previous numbers
    document.querySelectorAll("#take-token-modal board-token").forEach(elm => {
        elm.setCount(0);
    });

    // set min and max for counters, TODO: set max and min values to what tokens the player has
    document.querySelectorAll("#take-token-modal board-token-counter").forEach(elm => {
        elm.setMax(10);
    });

    setBoardTokens("#take-token-modal");

    document.querySelector("#take-token-modal #take-token-confirm-btn").onclick = () => {
        putBackTokens();
        showNextModal("#put-back-token-modal");
    };
    
};

const putBackTokens = () => {

    const confirmBtn = document.querySelector("#put-back-token-modal .put-back-token-confirm-btn");
    
    // clear previous numbers
    document.querySelectorAll("#put-back-token-modal board-token").forEach(elm => {
        elm.setCount(0);
    });

    // set min and max for counters, TODO: set max and min values to what tokens the player has
    document.querySelectorAll("#put-back-token-modal board-token-counter").forEach(elm => {
        elm.setMax(10);
    });

    setBoardTokens("#put-back-token-modal");

    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        SETTINGS.verifyCredentials().then(() => {
            const windowParams = (new URL(document.location)).searchParams;
            const sessionId = windowParams.get("sessionId");

            const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/TAKE_TOKEN`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

            const jsonData = {
                "takeTokens": getTokensList("#take-token-modal board-token-counter board-token .board-token"),
                "putBackTokens": getTokensList("#put-back-token-modal board-token-counter board-token .board-token")
            };

            fetch(url, {
                method: "PUT",
                headers: { "content-type": "application/json" },
                body: JSON.stringify(jsonData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
        });
    };
};

const showPayment = (cardNode) => {

    const confirmBtn = document.querySelector("#dev-card-payment-modal .buy-card-confirm-btn");

    // to be used to restrict selection of tokens in the FUTURE
    // const cost = JSON.parse(cardNode.getAttribute("cost"));
    const cardId = cardNode.getAttribute("card-id");

    // show card in purchase window
    const imgSrc = `/images/development-cards/${cardId}.jpg`;
    document.querySelector("#dev-card-payment-modal .purchase-show-card img").setAttribute("src", imgSrc);

    // clear previous numbers
    document.querySelectorAll("#dev-card-payment-modal board-token").forEach(elm => {
        elm.setCount(0);
    });

    // set min and max for counters, TODO: set max and min values to what tokens the player has
    document.querySelectorAll("#dev-card-payment-modal board-token-counter").forEach(elm => {
        elm.setMax(10);
    });

    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        SETTINGS.verifyCredentials().then(() => {
            const windowParams = (new URL(document.location)).searchParams;
            const sessionId = windowParams.get("sessionId");

            const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/BUY_CARD`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();


            const jsonData = {
                "cardId": cardId,
                "tokens": getTokensList("#dev-card-payment-modal board-token-counter board-token .board-token")
            };

            fetch(url, {
                method: "PUT",
                headers: { "content-type": "application/json" },
                body: JSON.stringify(jsonData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
        });
    };

};

const showPurchasableDevCards = () => {
    // show reserved cards (from player inv, maybe should pass in data from server instead?)
    const reservedCardNodes = [];
    const reservedCards = document.querySelectorAll("#player-inventory .player-inventory-reservedcards .player-inventory-card-reserved");
    reservedCards.forEach((elm) => {
        const cid = elm.getAttribute("card-id");
        const node = document.querySelector("#board-card-dev-template").content.cloneNode(true);
        const div = node.querySelector("div");
        const imgUrl = `/images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        reservedCardNodes.push(node);
    });
    document.querySelector("#buy-card-modal .reserved-cards").replaceChildren(...reservedCardNodes);

    // literally just duplicate them from the board

    const cardRows = document.querySelectorAll("#board .board-cards .board-cards-row");

    // clear if already has
    const modalCardRows = document.querySelector("#buy-card-board .modal-board-cards");
    modalCardRows.innerHTML = "";

    cardRows.forEach((elm) => {
        const cNode = elm.cloneNode(true);
        modalCardRows.appendChild(cNode);
    });

    const cardsSelectionSelector = "#buy-card-modal .board-card-dev";

    setupSelectionCards(cardsSelectionSelector);

    document.querySelector("#buy-card-modal #buy-card-confirm-btn").onclick = () => {
        const selectedCard = document.querySelector(`${cardsSelectionSelector}.selected-card`);
        if(!selectedCard) {
            // no card has been selected, error
            window.alert("You have not selected a card to purchase!");
            return;
        }

        showPayment(selectedCard);
        showNextModal("#dev-card-payment-modal");
    };
};

export function followupActions(data) {
    const actions = data.curValidActions;

    if (actions.length == 0) {
        return;
    }
    
    if (actions[0] == "CASCADE_1" || actions[0] == "CASCADE_2") {
        verifyNoModals();

        showCascadeCards(actions[0]);
        // console.log("Nav after show cascade cards: " + navContext);

        const selector = "#cascade-modal";
        navContext.push(selector);
        showModal(selector);

    } else if(actions[0] == "CHOOSE_SATCHEL_TOKEN") {
        verifyNoModals();

        showSatchelBonuses(data);

        const selector = "#choose-satchel-modal";
        navContext.push(selector);
        showModal(selector);
        console.log("Nav context after satchel: " + navContext);
    } else if(actions[0] == "TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER") {
        verifyNoModals();

        showExtraTokens();
        const selector = "#take-extra-token-modal";
        navContext.push(selector);
        showModal(selector);
    } else if(actions[0] == "RESERVE_NOBLE") {

        verifyNoModals();
        initReserveNoble();
        const selector = "#reserve-noble-modal";
        navContext.push(selector);
        showModal(selector);

    }


    // TODO: add more followup actions like choose satchel token, choose noble, etc

}


// --------------------------------------------------

// CHOOSE SATCHEL BONUS SECTION
function setupBonusSelect(selector) {
    // remove selection
    const oldSelected = document.querySelector(`${selector}.selected-bonus`);
    if(oldSelected) {
        oldSelected.classList.remove("selected-bonus");
    }
    document.querySelectorAll(selector).forEach((elm) => {
        elm.onclick = () => {
            // get the current selected card (should have .selected-card as a class)
            const selectedBonus = document.querySelector(`${selector}.selected-bonus`);
            // remove the selection
            if(selectedBonus) selectedBonus.classList.remove("selected-bonus");

            // only add to clicked element if the old selected was not the clicked element
            if(selectedBonus !== elm) {
                elm.classList.add("selected-bonus");
            }
        };
    });
}

const showSatchelBonuses = (data) => {
    const pUsername = SETTINGS.getUsername();

    const confirmBtn = document.querySelector("#choose-satchel-modal .satchel-confirm-btn");

    const bonusSelectionSelector = "#choose-satchel-modal .board-token";

    setupBonusSelect(bonusSelectionSelector);

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

        const selectedBonus = document.querySelector(`${bonusSelectionSelector}.selected-bonus`);

        console.log("bonusSelector from document.query: " + bonusSelectionSelector);

        if(!selectedBonus) {
            // no card has been selected, error
            window.alert("You have not assigned this satchel card a bonus!");
            confirmBtn.disabled = false;
            return;
        }
        // assume the satchel card is the latest satchel card added to the player's inventory
        // (it shouldn't matter which satchel card we set right?)
        const cardId = document.querySelector("#player-inventory .player-inventory-card-drawer .player-inventory-card[satchel='true']").getAttribute("card-id");
        console.log("cardId from document.query: " + bonusSelectionSelector);
        const bonusColor = selectedBonus.getAttribute("color");

        SETTINGS.verifyCredentials().then(() => {
            const windowParams = (new URL(document.location)).searchParams;
            const sessionId = windowParams.get("sessionId");

            const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/CHOOSE_SATCHEL_TOKEN`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();


            const jsonData = {
                "cardId": cardId,
                "selected": bonusColor
            };

            fetch(url, {
                method: "PUT",
                headers: { "content-type": "application/json" },
                body: JSON.stringify(jsonData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
        });
    };

};

// --------------------------------------------------

// TAKE EXTRA TOKEN SECTION (TRADING POSTS)
function setupExtraTokenSelect(selector) {
    // remove selection
    const oldSelected = document.querySelector(`${selector}.selected-extra-token`);
    if(oldSelected) {
        oldSelected.classList.remove("selected-extra-token");
    }
    document.querySelectorAll(selector).forEach((elm) => {
        elm.onclick = () => {
            // get the current selected token (should have .selected- as a class)
            const selectedExtraToken = document.querySelector(`${selector}.selected-extra-token`);
            // remove the selection
            if(selectedExtraToken) selectedExtraToken.classList.remove("selected-extra-token");

            // only add to clicked element if the old selected was not the clicked element
            if(selectedExtraToken !== elm) {
                elm.classList.add("selected-extra-token");
            }
        };
    });
}

const showExtraTokens = () => {

    const takeConfirmBtn = document.querySelector("#take-extra-token-modal .extra-token-confirm-btn");
    takeConfirmBtn.onclick = () => {
        showNextModal("#putback-extra-token-modal");
    };

    const extraTokenSelectionSelector = "#take-extra-token-modal .board-token";
    setupExtraTokenSelect(extraTokenSelectionSelector);

    const putBackTokenSelectionSelector = "#putback-extra-token-modal .board-token";
    setupExtraTokenSelect(putBackTokenSelectionSelector);

    const putBackConfirmBtn = document.querySelector("#putback-extra-token-modal #putback-token-confirm-btn");
    document.querySelector("#putback-extra-token-modal #putback-token-back-btn").onclick = () => {
        backButton();
    };

    putBackConfirmBtn.onclick = () => {
        putBackConfirmBtn.disabled = true;

        const selectedExtraToken = document.querySelector(`${extraTokenSelectionSelector}.selected-extra-token`);
        const takeToken = selectedExtraToken ? selectedExtraToken.getAttribute("color") : null;

        const selectedPutBackToken = document.querySelector(`${putBackTokenSelectionSelector}.selected-extra-token`);
        const putBackToken = selectedPutBackToken ? selectedPutBackToken.getAttribute("color") : null;

        SETTINGS.verifyCredentials().then(() => {
            const windowParams = (new URL(document.location)).searchParams;
            const sessionId = windowParams.get("sessionId");

            const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();


            const jsonData = {};
            if(takeToken) jsonData["takeToken"] = takeToken;
            if(putBackToken) jsonData["putBackToken"] = putBackToken;

            fetch(url, {
                method: "PUT",
                headers: { "content-type": "application/json" },
                body: JSON.stringify(jsonData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  putBackConfirmBtn.disabled = false);
        });
    };

};


// Reserve noble
const setupSelect = (selector, selectedSelector = null) => {
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
};

const initReserveNoble = () => {

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
    setupSelect(selectionSelector, "selected-noble");

    const confirmBtn = document.querySelector("#reserve-noble-modal .reserve-card-confirm-btn");
    confirmBtn.onclick = () => {
        confirmBtn.disabled = true;

        const selected = document.querySelector(`${selectionSelector}.selected-noble`);
        if(!selected) {
            window.alert("You have not selected a noble!");
            confirmBtn.disabled = false;
            return;
        }

        SETTINGS.verifyCredentials().then(() => {
            const windowParams = (new URL(document.location)).searchParams;
            const sessionId = windowParams.get("sessionId");

            const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/RESERVE_NOBLE`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();


            const jsonData = {
                "cardId": selected.getAttribute("card-id")
            };

            fetch(url, {
                method: "PUT",
                headers: { "content-type": "application/json" },
                body: JSON.stringify(jsonData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() =>  confirmBtn.disabled = false);
        });
    };
};


const navContext = [];

const showModal = (selector) => {
    document.querySelector(selector).classList.add("show");
};

const closeModal = (selector) => {
    document.querySelector(selector).classList.remove("show");
};

export const showNextModal = (selector) => {
    // close current
    closeModal(navContext[navContext.length-1]);

    // push new
    navContext.push(selector);
    showModal(navContext[navContext.length-1]);
};

getTokensBtn.onclick = () => {
    takeTokens();

    showNextModal("#take-token-modal");
};

purchaseBtn.onclick = () => {
    // load the purchase items
    showPurchasableDevCards();

    showNextModal("#buy-card-modal");
};

reserveBtn.onclick = () => {
    // load the purchase items
    showReservableDevCards();
    
    showNextModal("#reserve-card-modal");
};


export const backButton = () => {
    // pop top of stack (current modal), close it
    closeModal(navContext.pop());

    // open the previous one, if any
    if(navContext.length > 0) {
        showModal(navContext[navContext.length-1]);
    }
};

document.querySelectorAll(".take-token-back-btn").forEach(elm => elm.onclick = backButton);
document.querySelectorAll(".put-back-token-back-btn").forEach(elm => elm.onclick = backButton);
document.querySelectorAll(".buy-card-back-btn").forEach(elm => elm.onclick = backButton);
document.querySelectorAll(".reserve-card-back-btn").forEach(elm => elm.onclick = backButton);

export function startTurn() {
    // attempt to start the turn if no modals are open and it is still your turn.
    if(navContext.length == 0) {
        navContext.push("#your-turn-modal"); // push your turn modal
        showModal(navContext[navContext.length-1]);
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
