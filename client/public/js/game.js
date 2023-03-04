import { SETTINGS } from "./settings.js";
import { startTurn, verifyNoModals } from "./modals.js";

// eslint-disable-next-line no-undef
var MD5 = CryptoJS.MD5;

const updateTokensCount = (parentSelector, tokenInfo) => {
    const parentNode = document.querySelector(parentSelector);
    parentNode.querySelector(".red-token > span").textContent = tokenInfo.Red;
    parentNode.querySelector(".blue-token > span").textContent = tokenInfo.Blue;
    parentNode.querySelector(".green-token > span").textContent = tokenInfo.Green;
    parentNode.querySelector(".white-token > span").textContent = tokenInfo.White;
    parentNode.querySelector(".brown-token > span").textContent = tokenInfo.Brown;
    parentNode.querySelector(".gold-token > span").textContent = tokenInfo.Gold;
};

const updateTierRow = (selector, devCards, orientCards) => {
    const row = document.querySelector(selector);
    const cardElms = row.querySelectorAll(".board-card-dev");

    // clear images first
    cardElms.forEach(elm => {
        elm.querySelector("img").removeAttribute("src");
    });

    // mark deck as empty if needed
    const setEmptyIfNeeded = (deckSelector, isEmpty) => {
        if(isEmpty) {
            row.querySelector(deckSelector).setAttribute("empty", "true");
        }
    };
    
    setEmptyIfNeeded(".board-cards-dev-deck", devCards.length == 0);
    setEmptyIfNeeded(".board-cards-dev-deck-orient", orientCards.length == 0);

    const updateCardElement = (cardElm, cardInfo, imgBase) => {
        const cardId = cardInfo["id"];
        const cost = cardInfo["tokenCost"];

        const imgUrl = `${imgBase}/${cardId}.jpg`;
        const imgElm = cardElm.querySelector("img");
        imgElm.src = imgUrl;

        // add additional info
        cardElm.setAttribute("card-id", cardId);
        cardElm.setAttribute("cost", JSON.stringify(cost));
    };

    devCards.forEach((cardInfo, index) => updateCardElement(cardElms[index], cardInfo, "/images/development-cards"));
    // skip first 4 card elements in the row (reg dev cards)
    orientCards.forEach((cardInfo, index) => updateCardElement(cardElms[index + 4], cardInfo, "/images/orient-development-cards"));
};

const updateMainPlayerInfo = (playerInfo) => {

    const playerInv = document.querySelector("#player-inventory");


    // update prestige points
    playerInv.querySelector(".player-prestige-points-container > span").textContent = playerInfo.prestigePoints;

    // why
    const tokenMap = playerInfo.tokens;
    updateTokensCount(".player-inventory-tokens", tokenMap);

    // UPDATE DEVELOPMENT CARDS IN PLAYER INVENTORY
    const cardDrawer = playerInv.querySelector(".player-inventory-card-drawer");

    const currentInvCardIds = [];
    cardDrawer.querySelectorAll(".player-inventory-card").forEach(elm => currentInvCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverInvCardIds = playerInfo.cards.map(c => c.id);

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

    // UPDATE RESERVED CARDS IN PLAYER INVENTORY
    const cardDrawerReserved = playerInv.querySelector(".player-inventory-reservedcards");

    const currentInvResCardIds = [];
    cardDrawerReserved.querySelectorAll(".player-inventory-card-reserved").forEach(elm => currentInvResCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverInvResCardIds = playerInfo.reservedCards.map(c => c.id);

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
    updateTokensCount(selector, tokenMap);


    // UPDATE DEVELOPMENT CARDS IN OTHER PLAYER INVENTORIES
    const cardDrawer = pNode.querySelector(".other-inventory-cards");

    const currentInvCardIds = [];
    cardDrawer.querySelectorAll(".other-inventory-card").forEach(elm => currentInvCardIds.push(elm.getAttribute("card-id")));

    // server state cards
    const serverInvCardIds = pInfo.cards.map(c => c.id);

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

    // add new cards to inv
    const toAddResCardsIds = serverInvResCardIds.filter(x => !currentInvResCardIds.includes(x));
    toAddResCardsIds.forEach(cid => {
        const tNode = document.querySelector("#other-player-reserved-card-template").content.cloneNode(true);
        const div = tNode.querySelector("div");
        const imgUrl = `images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardDrawerReserved.appendChild(tNode);
    });

    // update prestige points
    pNode.querySelector(".other-prestige-point-container > span").textContent = pInfo.prestigePoints;

};

const updateNoblesBoard = async (cards) => {
    const noblesDiv = document.querySelectorAll(".board-nobles .board-noble-card img");

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

const updateGameboard = async () => {
    await SETTINGS.verifyCredentials();

    const windowParams = (new URL(document.location)).searchParams;
    const sessionId = windowParams.get("sessionId");
    const params = {
        "hash": gameStateHash
    };

    const url = new URL(`${SETTINGS.GS_API}/api/sessions/${sessionId}`);
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

    updateTierRow(".board-cards-row.board-cards-level1", data.tier1Deck.visibleCards, data.tier1OrientDeck.visibleCards);
    updateTierRow(".board-cards-row.board-cards-level2", data.tier2Deck.visibleCards, data.tier2OrientDeck.visibleCards);
    updateTierRow(".board-cards-row.board-cards-level3", data.tier3Deck.visibleCards, data.tier3OrientDeck.visibleCards);

    const curUsername = SETTINGS.getUsername();

    const playersData = data.players;
    playersData.forEach((pInfo) => {
        if(pInfo.name === curUsername) {
            updateMainPlayerInfo(pInfo);
        } else {
            updateOtherPlayerInfo(pInfo);
        }
    });

    // check if it's your turn
    if(data.players[data.turnCounter].name === curUsername) {
        startTurn();
    } else {
        verifyNoModals();
    }
};

document.addEventListener("DOMContentLoaded", () => {

    setTimeout(attempUpdate, 1);
});
