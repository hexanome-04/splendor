import { SETTINGS } from "./settings.js";
import { startTurn, verifyNoModals, performFollowUpAction } from "./modals.js";

// eslint-disable-next-line no-undef
var MD5 = CryptoJS.MD5;

const updateTokensCount = (parentSelector, tokenInfo, bonusInfo = null) => {
    const setTokenInfo = (elm, color) => {
        // set token count
        elm.textContent = tokenInfo[color];

        // set bonus
        if(bonusInfo) {
            // get bonus container
            elm.closest(".bonus-container").querySelector(".bonus-icon > span").textContent = bonusInfo[color];
        }
    };
    const colors = ["Red", "Blue", "Green", "White", "Brown", "Gold"];

    const parentNode = document.querySelector(parentSelector);
    colors.forEach((col) => {
        setTokenInfo(parentNode.querySelector(`.${col.toLowerCase()}-token > span`), col);
    });
};

const updateTierRow = (selector, devCardsDeck, orientCardsDeck) => {
    const devCards = devCardsDeck.visibleCards;
    const orientCards = orientCardsDeck.visibleCards;
    const row = document.querySelector(selector);
    const cardElms = row.querySelectorAll(".board-card-dev");

    // clear images first
    cardElms.forEach(elm => {
        elm.querySelector("img").removeAttribute("src");
    });

    // mark deck as empty if needed
    const setEmptyIfNeeded = (deckSelector, canDraw) => {
        if(!canDraw) {
            row.querySelector(deckSelector).setAttribute("empty", "true");
        }
    };
    
    setEmptyIfNeeded(".board-cards-dev-deck", devCardsDeck.canDraw);
    setEmptyIfNeeded(".board-cards-dev-deck-orient", orientCardsDeck.canDraw);

    const updateCardElement = (cardElm, cardInfo) => {
        const cardId = cardInfo["id"];
        const cost = cardInfo["tokenCost"];

        const imgUrl = `/images/development-cards/${cardId}.jpg`;
        const imgElm = cardElm.querySelector("img");
        imgElm.src = imgUrl;

        // add additional info
        cardElm.setAttribute("card-id", cardId);
        cardElm.setAttribute("cost", JSON.stringify(cost));
    };

    devCards.forEach((cardInfo, index) => updateCardElement(cardElms[index], cardInfo));
    // skip first 4 card elements in the row (reg dev cards)
    orientCards.forEach((cardInfo, index) => updateCardElement(cardElms[index + 4], cardInfo));
};

/**
 * Callback for card ids.
 *
 * @callback idsCallback
 * @param {Array<string>} ids
 * @returns {void}
 */

/**
 * Update a series of cards with the given array.
 * 
 * @param {Array} cards list of cards
 * @param {HTMLElement} baseElement base element for all other selectors
 * @param {string} containerSelector container selector for all cards
 * @param {string} cardSelector selector for single card
 * @param {string} imageFolder folder name for images
 * @param {string} cardTemplateSelector template for card (could become just an HTMLElement name todo)
 * @param {idsCallback} addedCallback callback for getting all added card ids
 * @param {idsCallback} removedCallback callback for getting all removed card ids
 */
const updateCards = (cards, baseElement, containerSelector, cardSelector, imageFolder, cardTemplateSelector,
                    addedCallback = (ids) => {}, removedCallback = (ids) => {}) => {

    const cardContainer = baseElement.querySelector(containerSelector);

    const currentCardIds = [];
    cardContainer.querySelectorAll(cardSelector).forEach(elm => currentCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverCardIds = cards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const outdatedCardIds = currentCardIds.filter(x => !serverCardIds.includes(x));
    outdatedCardIds.forEach(cid => cardContainer.querySelector(`${cardSelector}[card-id="${cid}"]`).remove());

    // add new reserved cards to inv
    const missingCardIds = serverCardIds.filter(x => !currentCardIds.includes(x));
    missingCardIds.forEach(cid => {
        const tNode = document.querySelector(cardTemplateSelector).content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/${imageFolder}/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardContainer.appendChild(tNode);
    });

    addedCallback(missingCardIds);
    removedCallback(outdatedCardIds);
};

const updateMainPlayerInfo = (playerInfo) => {

    const playerInv = document.querySelector("#player-inventory");


    // update prestige points
    playerInv.querySelector(".player-prestige-points-container > span").textContent = playerInfo.prestigePoints;

    // why
    const tokenMap = playerInfo.tokens;
    updateTokensCount(".player-inventory-tokens", tokenMap, playerInfo.bonuses);

    // UPDATE DEVELOPMENT CARDS IN PLAYER INVENTORY
    updateCards(playerInfo.devCards, playerInv, ".player-inventory-card-drawer",
                ".player-inventory-card", "development-cards", "#development-card-template");

    // mark cards as satchel (so that we can select them later in the choose satchel token type)
    playerInfo.devCards.forEach((c) => {
        const card = document.querySelector(`.player-inventory-card-drawer .player-inventory-card[card-id="${c.id}"]`);
        if(c.tokenType === "Satchel") {
            card.setAttribute("satchel", "true");
        } else if(c.tokenType !== "Satchel" && card.hasAttribute("satchel")) {
            card.removeAttribute("satchel");
        }
    });


    // UPDATE RESERVED CARDS IN PLAYER INVENTORY
    updateCards(playerInfo.reservedCards, playerInv, ".player-inventory-reservedcards",
                ".player-inventory-card-reserved", "development-cards", "#player-inventory-reserved-card-template");

    // Update nobles
    updateCards(playerInfo.nobleCards, playerInv, ".player-inventory-nobles",
                ".noble-card", "nobles", "#noble-card-template");

    // Update reserved nobles
    updateCards(playerInfo.reservedNobles, playerInv, ".player-inventory-reserved-nobles",
                ".noble-card", "nobles", "#noble-card-template");

    // shrink player inventory if too much crap
    let containerCount = 0;
    if(playerInfo.devCards.length > 0) containerCount++;
    if(playerInfo.reservedCards.length > 0) containerCount++;
    if(playerInfo.nobleCards.length > 0) containerCount++;
    if(playerInfo.reservedNobles.length > 0) containerCount++;

    if(containerCount > 2) {
        playerInv.classList.add("shrink");
    } else {
        playerInv.classList.remove("shrink");
    }
};

const updateOtherPlayerInfo = (pInfo) => {
    const selector = `.other-players .other-player[pname="${pInfo.name}"]`;
    let pNode = document.querySelector(selector);

    // if node doesn't exist, we must make
    if(!pNode) {
        const tNode = document.querySelector("#other-player-tab-template").content.cloneNode(true);
        const pDiv = tNode.querySelector(".other-player");

        pDiv.setAttribute("pname", pInfo.name);
        pDiv.querySelector(".other-player-name").textContent = pInfo.name;

        document.querySelector(".other-players").appendChild(tNode);

        // now reget the node
        pNode = document.querySelector(selector);
    }

    // update tokens, cards, prestige points
    const tokenMap = pInfo.tokens;
    updateTokensCount(selector, tokenMap, pInfo.bonuses);


    // UPDATE DEVELOPMENT CARDS IN OTHER PLAYER INVENTORIES
    updateCards(pInfo.devCards, pNode, ".other-inventory-cards",
                ".other-inventory-card", "development-cards", "#other-player-dev-card-template");

    // UPDATE RESERVED CARDS IN OTHER PLAYER INVENTORIES
    updateCards(pInfo.reservedCards, pNode, ".other-inventory-cards-reserved",
                ".other-inventory-card-reserved", "development-cards", "#other-player-reserved-card-template");

    // Update nobles
    updateCards(pInfo.nobleCards, pNode, ".other-inventory-noble-cards",
                ".noble-card", "nobles", "#noble-card-template");

    // Update reserved nobles
    updateCards(pInfo.reservedNobles, pNode, ".other-inventory-reserved-noble-cards",
                ".noble-card", "nobles", "#noble-card-template");

    // update prestige points
    pNode.querySelector(".other-prestige-point-container > span").textContent = pInfo.prestigePoints;

};

const updateNoblesBoard = async (cards) => {
    const noblesDiv = document.querySelectorAll(".board-nobles .noble-card img");

    // should be changed prolly
    noblesDiv.forEach((elm, index) => {
        const curSrc = elm.getAttribute("src");

        if(index < cards.length) {
            if(curSrc !== cards[index]) {
                const imgSrc = `/images/nobles/${cards[index].id}.jpg`;
                elm.parentElement.setAttribute("card-id", cards[index].id);
                elm.setAttribute("src", imgSrc);
            }
        } else {
            elm.parentElement.removeAttribute("card-id");
            elm.removeAttribute("src");
        }
    });
};

let gameStateHash = "-";

const attempUpdate = () => {
    let nextCallTime = 1;
    updateGameboard().catch((err) => {
        if(!err.toString().includes("Failed to fetch" )) {
            window.alert(err);
            console.log("[AS] Error during check (retry 30s): " + err);
        }
        nextCallTime = 30000;
    }).finally(() => {
        setTimeout(attempUpdate, nextCallTime);
    });
};

// array for other updates that can be done (extensions)
const furtherUpdates = [];

const updateGameboard = async () => {
    await SETTINGS.verifyCredentials();

    const windowParams = (new URL(document.location)).searchParams;
    const sessionId = windowParams.get("sessionId");
    const params = {
        "hash": gameStateHash
    };

    const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}`);
    url.search = new URLSearchParams(params).toString();

    const resp = await fetch(url);

    const dataText = await resp.text();

    if(resp.status == 204) {
        // no update
        return;
    }

    if(!resp.ok) {
        throw new Error(dataText);
    }

    // update hash
    // console.log("Update: " + t);
    const newHash = MD5(dataText);

    // update only if needed
    if(newHash === gameStateHash) {
        return;
    }

    gameStateHash = newHash;
    console.log("[AS] Update available!");
    const data = JSON.parse(dataText);

    // tokens update
    const tokenMap = data.tokens;
    updateTokensCount("#board .board-tokens", tokenMap);

    updateNoblesBoard(data.nobleDeck.visibleCards);

    updateTierRow(".board-cards-row.board-cards-level1", data.tier1Deck, data.tier1OrientDeck);
    updateTierRow(".board-cards-row.board-cards-level2", data.tier2Deck, data.tier2OrientDeck);
    updateTierRow(".board-cards-row.board-cards-level3", data.tier3Deck, data.tier3OrientDeck);

    const curUsername = SETTINGS.getUsername();

    const playersData = data.players;
    let amPlaying = false;
    playersData.forEach((pInfo) => {
        if(pInfo.name === curUsername) {
            updateMainPlayerInfo(pInfo);
            amPlaying = true;
        } else {
            updateOtherPlayerInfo(pInfo);
        }
    });
    const playerInv = document.querySelector("#player-inventory");
    if(!amPlaying) {
        // you are spectating, hide the player inventory on the left
        playerInv.classList.add("hide");
    } else if(playerInv.classList.contains("hide")) {
        playerInv.classList.remove("hide");
    }

    furtherUpdates.forEach((func) => func(data));

    // check if it's your turn
    if(data.players[data.turnCounter].name === curUsername) {
        if(!performFollowUpAction(data)) {
            startTurn();
        }
    } else {
        verifyNoModals();
    }
};

/**
 * Add an updater to the main update executor.
 * The added function should take in as an argument the data from the game server.
 * 
 * @param {Function} func 
 */
export const addUpdater = (func) => {
    furtherUpdates.push(func);
}

document.addEventListener("DOMContentLoaded", () => {

    setTimeout(attempUpdate, 1);
});
