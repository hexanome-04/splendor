import { SETTINGS } from "./settings.js";
import { addUpdater } from "./game.js";
import { registerPlayerUpdater, writeBasicUpdate } from "./history";

const coatOfArmsImages = [
    "RedCoatOfArms.png",
    "BlackCoatOfArms.png",
    "YellowCoatOfArms.png",
    "BlueCoatOfArms.png"
];

// now update the coa on the powers
const allPowers = [
    ["ExtraTokenAfterPurchasePower", "extraTokenAfterPurchase"],
    ["ExtraTokenAfterTakingSameColorTokensPower", "extraTokenAfterTakingSameColor"],
    ["GoldTokenWorthTwoTokensPower", "goldTokenWorthTwoTokens"],
    ["AddFivePrestigePointsPower", "addFivePrestigePoints"],
    ["AddPrestigePointsWithCoatOfArmsPower", "addPrestigePointsWithCoatsOfArms"],
];

const updatePowers = (data) => {

    const curUsername = SETTINGS.getUsername();

    data.players.forEach((p, index) => {
        // update the coat of arms images for each player
        let coaElm = null;
        if(p.name === curUsername) {
            coaElm = document.querySelector("#player-inventory .player-coat-of-arms > img");
        } else {
            coaElm = document.querySelector(`#other-players .other-player[pname="${p.name}"] .op-coa-image-container > img`);
        }

        // only update if needed
        if(!coaElm.hasAttribute("src")) {
            coaElm.setAttribute("src", `/images/trading-posts/${coatOfArmsImages[index]}`);
        }

        const powersContainer = document.querySelector(".powers-container");
        // loop through each power and check if unlocked
        allPowers.forEach((v) => {
            const [enumName, varName] = v;

            // retrive the relevant power
            const power = powersContainer.querySelector(`.power[power='${enumName}']`);
            const coaIndicator = power.querySelector(`img[src="/images/trading-posts/${coatOfArmsImages[index]}"]`);
            if(p[varName] && p[varName].unlocked) {
                // check if we've already enabled showing the activated power for the player
                if(!coaIndicator.classList.contains("show")) {
                    coaIndicator.classList.add("show")
                }
            } else if(coaIndicator.classList.contains("show")) {
                coaIndicator.classList.remove("show");
            }
        });

    });


};

addUpdater(updatePowers);

// history updates
const updatePlayerTPHistory = (oldState, newState) => {
    let newPowersCount = 0;
    allPowers.forEach((v) => {
        const [enumName, varName] = v;

        if(!oldState[varName] || !newState[varName]) {
            return;
        }

        if(oldState[varName].unlocked !== newState[varName].unlocked) {
            newPowersCount++;
        }
    });

    if(newPowersCount > 0) {
        writeBasicUpdate(`Unlocked ${newPowersCount} new power${newPowersCount > 1 ? "s" : ""}`);
    }
};
registerPlayerUpdater(updatePlayerTPHistory);
