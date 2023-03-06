import { SETTINGS } from "./settings.js";
import { startTurn, verifyNoModals, followupActions } from "./modals.js";

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

const updateMainPlayerInfo = (playerInfo) => {

    const playerInv = document.querySelector("#player-inventory");


    // update prestige points
    playerInv.querySelector(".player-prestige-points-container > span").textContent = playerInfo.prestigePoints;

    // why
    const tokenMap = playerInfo.tokens;
    updateTokensCount(".player-inventory-tokens", tokenMap, playerInfo.bonuses);

    // UPDATE DEVELOPMENT CARDS IN PLAYER INVENTORY
    const cardDrawer = playerInv.querySelector(".player-inventory-card-drawer");

    const currentInvCardIds = [];
    cardDrawer.querySelectorAll(".player-inventory-card").forEach(elm => currentInvCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverInvCardIds = playerInfo.cards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const nonExistCardsIds = currentInvCardIds.filter(x => !serverInvCardIds.includes(x));
    nonExistCardsIds.forEach(cid => cardDrawer.querySelector(`.player-inventory-card[card-id="${cid}"]`).remove());

    // add new cards to inv
    const toAddCardsIds = serverInvCardIds.filter(x => !currentInvCardIds.includes(x));
    toAddCardsIds.forEach(cid => {
        const tNode = document.querySelector("#development-card-template").content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardDrawer.appendChild(tNode);
    });

    // mark cards as satchel (so that we can select them later in the choose satchel token type)
    playerInfo.cards.forEach((c) => {
        const card = cardDrawer.querySelector(`.player-inventory-card[card-id="${c.id}"]`);
        if(c.tokenType === "Satchel") {
            card.setAttribute("satchel", "true");
        } else if(c.tokenType !== "Satchel" && card.hasAttribute("satchel")) {
            card.removeAttribute("satchel");
        }
    });


    // UPDATE RESERVED CARDS IN PLAYER INVENTORY
    const cardDrawerReserved = playerInv.querySelector(".player-inventory-reservedcards");

    const currentInvResCardIds = [];
    cardDrawerReserved.querySelectorAll(".player-inventory-card-reserved").forEach(elm => currentInvResCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverInvResCardIds = playerInfo.reservedCards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const nonExistReserveCardsIds = currentInvResCardIds.filter(x => !serverInvResCardIds.includes(x));
    nonExistReserveCardsIds.forEach(cid => cardDrawerReserved.querySelector(`.player-inventory-card-reserved[card-id="${cid}"]`).remove());

    // add new reserved cards to inv
    const toAddResCardsIds = serverInvResCardIds.filter(x => !currentInvResCardIds.includes(x));
    toAddResCardsIds.forEach(cid => {
        const tNode = document.querySelector("#player-inventory-reserved-card-template").content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardDrawerReserved.appendChild(tNode);
    });


    // Update nobles
    const nobleCardInv = playerInv.querySelector(".player-inventory-nobles");

    const currentNobleCardIds = [];
    nobleCardInv.querySelectorAll(".noble-card").forEach(elm => currentNobleCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverNobleCardIds = playerInfo.nobleCards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const nonExistNobleCardsIds = currentNobleCardIds.filter(x => !serverNobleCardIds.includes(x));
    nonExistNobleCardsIds.forEach(cid => nobleCardInv.querySelector(`.noble-card[card-id="${cid}"]`).remove());

    // add new reserved cards to inv
    const toAddNobleCardsIds = serverNobleCardIds.filter(x => !currentNobleCardIds.includes(x));
    toAddNobleCardsIds.forEach(cid => {
        const tNode = document.querySelector("#noble-card-template").content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/nobles/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        nobleCardInv.appendChild(tNode);
    });
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
    const cardDrawer = pNode.querySelector(".other-inventory-cards");

    const currentInvCardIds = [];
    cardDrawer.querySelectorAll(".other-inventory-card").forEach(elm => currentInvCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverInvCardIds = pInfo.cards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const nonExistCardsIds = currentInvCardIds.filter(x => !serverInvCardIds.includes(x));
    nonExistCardsIds.forEach(cid => cardDrawer.querySelector(`.other-inventory-card[card-id="${cid}"]`).remove());

    // add new cards to inv
    const toAddCardsIds = serverInvCardIds.filter(x => !currentInvCardIds.includes(x));
    toAddCardsIds.forEach(cid => {
        const tNode = document.querySelector("#other-player-dev-card-template").content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardDrawer.appendChild(tNode);
    });

    // UPDATE RESERVED CARDS IN OTHER PLAYER INVENTORIES
    const cardDrawerReserved = pNode.querySelector(".other-inventory-cards-reserved");

    const currentInvResCardIds = [];
    cardDrawerReserved.querySelectorAll(".other-inventory-card-reserved").forEach(elm => currentInvResCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverInvResCardIds = pInfo.reservedCards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const nonExistReserveCardsIds = currentInvResCardIds.filter(x => !serverInvResCardIds.includes(x));
    nonExistReserveCardsIds.forEach(cid => cardDrawerReserved.querySelector(`.other-inventory-card-reserved[card-id="${cid}"]`).remove());

    // add new cards to inv
    const toAddResCardsIds = serverInvResCardIds.filter(x => !currentInvResCardIds.includes(x));
    toAddResCardsIds.forEach(cid => {
        const tNode = document.querySelector("#other-player-reserved-card-template").content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardDrawerReserved.appendChild(tNode);
    });

    // Update nobles
    const nobleCardInv = pNode.querySelector(".other-inventory-noble-cards");

    const currentNobleCardIds = [];
    nobleCardInv.querySelectorAll(".noble-card").forEach(elm => currentNobleCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverNobleCardIds = pInfo.nobleCards.map(c => c.id);

    // cards that exist in inv but not server side, must remove
    const nonExistNobleCardsIds = currentNobleCardIds.filter(x => !serverNobleCardIds.includes(x));
    nonExistNobleCardsIds.forEach(cid => nobleCardInv.querySelector(`.noble-card[card-id="${cid}"]`).remove());

    // add new reserved cards to inv
    const toAddNobleCardsIds = serverNobleCardIds.filter(x => !currentNobleCardIds.includes(x));
    toAddNobleCardsIds.forEach(cid => {
        const tNode = document.querySelector("#noble-card-template").content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `/images/nobles/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        nobleCardInv.appendChild(tNode);
    });

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
                elm.setAttribute("src", imgSrc);
            }
        } else {
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

    followupActions(data);

    const curUsername = SETTINGS.getUsername();

    const playersData = data.players;
    playersData.forEach((pInfo) => {
        if(pInfo.name === curUsername) {
            updateMainPlayerInfo(pInfo);
        } else {
            updateOtherPlayerInfo(pInfo);
        }
    });

    furtherUpdates.forEach((func) => func(data));

    // check if it's your turn
    if(data.players[data.turnCounter].name === curUsername) {
        startTurn();
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
