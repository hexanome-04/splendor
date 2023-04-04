import { SETTINGS } from "../settings";

export const initGameOver = (data) => {

    const winnersText = document.querySelector("#gameover-modal .winners-text");
    const winners = data.winners;
    const names = winners.map(p => p.name);

    if(names.includes(SETTINGS.getUsername())) {
        const title = document.querySelector("#gameover-modal h1");
        title.textContent = "VICTORY";
        document.querySelector("#gameover-modal").classList.add("win");
    }

    if(winners.length > 1) {
        const text = names.length == 2 ? names.join(" and ") : names.join(", ");
        winnersText.textContent = `${text} tied as winners of the game!`;
    } else {
        // one person won
        winnersText.textContent = `${names[0]} wins the game!`;
    }

    // add rain particles
    const min = 5;
    const max = 95;
    const randomNum = () => (Math.random() * (max - min) + min).toFixed(4);
    const ranNumFloor = () => Math.floor(Math.random() * (20 - 10) + 10);
    const rain = Array.from(Array(ranNumFloor()).keys()).map(i => `--i: ${randomNum()}; --j: ${randomNum()}; --k: ${randomNum()}`);

    const particleContainer = document.querySelector("#gameover-modal .particle-container");
    rain.forEach((i) => {
        const node = document.createElement("div");
        node.classList.add("carrier");
        node.style.cssText = i;
        node.innerHTML = `<div class="particle"></div>`;
        particleContainer.appendChild(node);
    });
};
