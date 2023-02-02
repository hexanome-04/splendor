import { SETTINGS } from "./settings.js";
import { startTurn, verifyNoModals } from "./modals.js";

const updateTokensCount = (parentSelector, tokenInfo) => {
    const parentNode = document.querySelector(parentSelector);
    parentNode.querySelector(".red-token > span").textContent = tokenInfo.Red;
    parentNode.querySelector(".blue-token > span").textContent = tokenInfo.Blue;
    parentNode.querySelector(".green-token > span").textContent = tokenInfo.Green;
    parentNode.querySelector(".white-token > span").textContent = tokenInfo.White;
    parentNode.querySelector(".brown-token > span").textContent = tokenInfo.Brown;
    parentNode.querySelector(".gold-token > span").textContent = tokenInfo.Gold;
};

const updateTierRow = (selector, cards) => {
    const cardElms = document.querySelectorAll(`${selector} .board-card-dev:not(.board-card-dev-orient)`);

    cards.forEach((cardInfo, index) => {
        const cardId = cardInfo["id"];
        const cost = cardInfo["tokenCost"];
        const cardElm = cardElms[index];

        const imgUrl = `images/development-cards/${cardId}.jpg`;
        const imgElm = cardElm.querySelector("img");
        imgElm.src = imgUrl;

        // add additional info
        cardElm.setAttribute("card-id", cardId);
        cardElm.setAttribute("cost", JSON.stringify(cost));

    });

};

const updateMainPlayerInfo = (playerInfo) => {

    const playerInv = document.querySelector("#player-inventory");


    // update prestige points
    playerInv.querySelector(".player-prestige-points-container > span").textContent = playerInfo.prestigePoints;

    // why
    const tokenMap = playerInfo.tokens;
    updateTokensCount(".player-inventory-tokens", tokenMap);

    // update their cards
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
        const imgUrl = `images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardDrawer.appendChild(tNode);
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


    // update their cards
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
        const imgUrl = `images/development-cards/${cid}.jpg`;

        div.setAttribute("card-id", cid);
        div.querySelector("img").setAttribute("src", imgUrl);

        // add to inv
        cardDrawer.appendChild(tNode);
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
                const imgSrc = `images/nobles/${cards[index].id}.jpg`;
                elm.setAttribute("src", imgSrc);
            }
        } else {
            elm.removeAttribute("src");
        }
    });
};

const updateGameboard = async () => {
    await SETTINGS.verifyCredentials();

    const windowParams = (new URL(document.location)).searchParams;
    const sessionId = windowParams.get("sessionId");

    const resp = await fetch(`${SETTINGS.GS_API}/api/sessions/${sessionId}/game`);

    if(!resp.ok) {
        const dataText = await resp.text();

        console.log(dataText);
        window.alert(dataText);
        return;
    }

    const data = await resp.json();

    // tokens update
    const tokenMap = data.boardState.tokens;
    updateTokensCount("#board .board-tokens", tokenMap);

    updateNoblesBoard(data.boardState.nobleDeck.visibleCards);

    updateTierRow(".board-cards-row.board-cards-level1", data.boardState.tier1Deck.visibleCards);
    updateTierRow(".board-cards-row.board-cards-level2", data.boardState.tier2Deck.visibleCards);
    updateTierRow(".board-cards-row.board-cards-level3", data.boardState.tier3Deck.visibleCards);

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

    setTimeout(updateGameboard, 5000); // refresh every 5 seconds
};

document.addEventListener("DOMContentLoaded", () => {

    setTimeout(updateGameboard, 1);
});
