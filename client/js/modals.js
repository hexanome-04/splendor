import { SETTINGS } from "./settings.js";

function show() {
    document.querySelector(".navigation").classList.add("active");
}


function close() {
    document.querySelector(".navigation").classList.remove("active");
}


document.getElementById("menu-icon").addEventListener("click", show);

document.querySelector(".resume").parentNode.addEventListener("click", close);

const purchaseBtn = document.getElementById("purchase-btn");


// --------------------------------------------------

// Reserve cards selection
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

/**
 * Get the require list for token payment for purchasing a development card.
 * @returns [{ token: count }]
 */
const getPaymentTokensList = () => {
    const tokensCountNodes = document.querySelectorAll("#dev-card-payment-modal board-token-counter board-token .board-token");

    const tokens = {};

    tokensCountNodes.forEach(node => {
        const tokenType = node.getAttribute("jewel-color");
        const count = node.parentNode.count;

        tokens[tokenType] = count;
    });

    return tokens;
};

const showPayment = (cardNode) => {

    const confirmBtn = document.querySelector("#dev-card-payment-modal .buy-card-confirm-btn");

    // to be used to restrict selection of tokens in the FUTURE
    // const cost = JSON.parse(cardNode.getAttribute("cost"));
    const cardId = cardNode.getAttribute("card-id");

    // show card in purchase window
    const imgSrc = `images/development-cards/${cardId}.jpg`;
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

            const url = new URL(`${SETTINGS.GS_API}/api/sessions/${sessionId}/game/players/${SETTINGS.getUsername()}/actions/BUY_CARD`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();


            const jsonData = {
                "cardId": cardId,
                "tokens": getPaymentTokensList()
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
    // literally just duplicate them from the board

    const cardRows = document.querySelectorAll("#board .board-cards .board-cards-row");

    // clear if already has
    const modalCardRows = document.querySelector("#buy-card-board .modal-board-cards");
    modalCardRows.innerHTML = "";

    cardRows.forEach((elm) => {
        const cNode = elm.cloneNode(true);
        modalCardRows.appendChild(cNode);
    });

    const cardsSelectionSelector = "#buy-card-modal .modal-board-cards .board-card-dev";

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


const navContext = [];

const showModal = (selector) => {
    document.querySelector(selector).classList.add("show");
};

const closeModal = (selector) => {
    document.querySelector(selector).classList.remove("show");
};

const showNextModal = (selector) => {
    // close current
    closeModal(navContext[navContext.length-1]);

    // push new
    navContext.push(selector);
    showModal(navContext[navContext.length-1]);
};

purchaseBtn.onclick = () => {
    // load the purchase items
    showPurchasableDevCards();

    showNextModal("#buy-card-modal");
};


const backButton = () => {
    // pop top of stack (current modal), close it
    closeModal(navContext.pop());

    // open the previous one, if any
    if(navContext.length > 0) {
        showModal(navContext[navContext.length-1]);
    }
};

document.querySelectorAll(".buy-card-back-btn").forEach(elm => elm.onclick = backButton);

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
