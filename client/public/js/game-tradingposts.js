import { SETTINGS } from "./settings.js";
import { addUpdater } from "./game.js";

const coatOfArmsImages = [
    "RedCoatOfArms.png",
    "BlackCoatOfArms.png",
    "YellowCoatOfArms.png",
    "BlueCoatOfArms.png"
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


        // now update the coa on the powers
        const allPowers = [
            ["ExtraTokenAfterPurchasePower", "extraTokenAfterPurchase"],
            ["ExtraTokenAfterTakingSameColorTokensPower", "extraTokenAfterTakingSameColor"],
            ["GoldTokenWorthTwoTokensPower", "goldTokenWorthTwoTokens"],
            ["AddFivePrestigePointsPower", "addFivePrestigePoints"],
            ["AddPrestigePointsWithCoatOfArmsPower", "addPrestigePointsWithCoatsOfArms"],
        ];

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
