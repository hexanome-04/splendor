
type Card = {
    id: string;
    newTokenType?: string;
};

/**
 * Returns the difference of the two cards list.
 * @param oldCards 
 * @param newCards 
 * @returns added cards, deleted cards, changed cards
 */
export const cardsDiff = (oldCards: any[], newCards: any[]): [Card[], Card[], Card[]] => {
    const added: Card[] = [];
    const removed: Card[] = [];
    const changed: Card[] = [];

    const oldCardsMap: Map<string, any> = new Map<string, any>();
    const newCardsMap: Map<string, any> = new Map<string, any>();
    oldCards.forEach(c => oldCardsMap.set(c.id, c));
    newCards.forEach(c => newCardsMap.set(c.id, c));

    // check if new cards were added
    newCards.forEach(c => {
        const oc = oldCardsMap.get(c.id);
        if(!oc) {
            added.push({ id: c.id });
        } else if(oc.isSatchel && c.isSatchel) {
            // check for changes, could have support for adding custom checks if needed
            if(oc.tokenType !== c.tokenType) {
                changed.push({ id: c.id, newTokenType: c.tokenType });
            }
        }
    });

    // check if cards were removed
    oldCards.forEach(c => {
        const oc = newCardsMap.get(c.id);
        if(!oc) {
            removed.push({ id: c.id });
        }
    });

    return [added, removed, changed];
};

export const writeCardUpdate = (prefix: string, cards: Card[], imagePath: string, suffix: string = null) => {
    if(cards.length === 0) return;

    const tNode = (document.querySelector("#history-card-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;
    tNode.querySelector(".prefix").innerHTML = prefix;
    tNode.querySelector(".suffix").innerHTML = suffix;
    const imgContainer = tNode.querySelector(".content");
    cards.forEach(c => {
        const img = document.createElement("img");
        img.setAttribute("src", `/images/${imagePath}/${c.id}.jpg`);
        imgContainer.appendChild(img);
    });

    appendHistory(tNode.querySelector(".event").innerHTML, "card-event");
};

const writeSatchelUpdate = (cards: Card[]) => {
    if(cards.length === 0) return;
    const tokenText = document.createElement("div");
    tokenText.textContent = "is now";
    tokenText.appendChild(createToken(cards[0].newTokenType));

    writeCardUpdate("Bonus of", cards, "development-cards", tokenText.innerHTML);
};

// token amount for showing
const createToken = (color: string, amount: number = null) => {
    const tNode = (document.querySelector("#history-token-template") as HTMLTemplateElement)
                    .content.cloneNode(true) as HTMLDivElement;
    if(amount) {
        tNode.querySelector(".amount").textContent = amount + "";
    }
    const tokenImg = document.createElement("div");
    tokenImg.classList.add(`${color.toLowerCase()}-token`, "board-token");
    tNode.querySelector(".token-change").appendChild(tokenImg);
    return tNode;
};

/**
 * Check if tokens changed between state, write update if yes.
 * @param oldTokens 
 * @param newTokens 
 */
const checkTokens = (oldTokens: any, newTokens: any) => {
    const setupDiv = (startText: string) => {
        const t = document.createElement("div");
        const headerText = document.createElement("div");
        headerText.textContent = startText;
        t.appendChild(headerText);
        return t;
    };

    // initial event content
    const tookContent = setupDiv("Took ");
    const putBackContent = setupDiv("Returned ");

    let taken = [];
    let putback = [];

    // add comma to last
    const addComma = (list: HTMLElement[]) => {
        if(list.length > 0) {
            const comma = document.createElement("div");
            comma.textContent = ",";
            list[list.length - 1].querySelector(".token-change").appendChild(comma);
        }
    };

    for (const [key, value] of Object.entries(oldTokens)) {
        if(newTokens[key] !== value) {
            // check the delta of token count
            const change = (newTokens[key] as number) - (value as number);
            if(change > 0) {
                addComma(taken);
                taken.push(createToken(key, change));
            } else {
                addComma(putback);
                putback.push(createToken(key, change * -1));
            }
        }
    }

    if(taken.length > 0) {
        taken.forEach(e => tookContent.appendChild(e));
        appendHistory(tookContent.innerHTML, "token-event");
    }
    if(putback.length > 0) {
        putback.forEach(e => putBackContent.appendChild(e));
        appendHistory(putBackContent.innerHTML, "token-event");
    }
};

const historyContainer = document.querySelector("#history .drawer");
export const focusLastEvent = () => {
    const lastEventContainer = historyContainer.querySelector(".event-container:last-child");
    if(lastEventContainer) {
        const lastEventContent = lastEventContainer.querySelector(".event:last-child");
        if(lastEventContent) {
            lastEventContent.scrollIntoView({ behavior: "smooth" });
        } else {
            lastEventContainer.scrollIntoView({ behavior: "smooth" });
        }
    }
};

const startEvent = (name: string): void => {
    const tNode = (document.querySelector("#history-event-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;
    tNode.querySelector(".event-name").textContent = `${name}'${name[name.length - 1].toLowerCase() === "s" ? "" : "s"} turn`;

    historyContainer.appendChild(tNode);
    focusLastEvent();
};

const appendHistory = (htmlText: string, ...classes: string[]): void => {
    const lastEventContainer = historyContainer.querySelector(".event-container:last-child");
    if(!lastEventContainer) {
        console.log("append history called but no event has been made");
        return;
    }

    const newEvent = document.createElement("div");
    newEvent.classList.add("event");
    newEvent.classList.add(...classes);
    newEvent.innerHTML = htmlText;
    lastEventContainer.querySelector(".event-content").appendChild(newEvent);

    focusLastEvent();
};

/**
 * Writes out who's turn it is, only if the current player has changed.
 * @param last 
 * @param current 
 */
const writeCurrentTurn = (last: any, current: any): void => {
    if(current.gameOver) {
        return;
    }

    if(!last || current.players[current.turnCounter].name !== last.players[last.turnCounter].name) {
        const name = current.players[current.turnCounter].name;
        console.log(`${name}'s turn`);
        startEvent(name);
    }
};

const writeGameOver = (data: any): void => {
    const tNode = (document.querySelector("#history-event-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;

    const overNode = (document.querySelector("#history-event-template") as HTMLTemplateElement)
                   .content.cloneNode(true) as HTMLDivElement;
    overNode.querySelector(".event-name").textContent = "The game has ended.";

    const winners = data.winners;

    if(winners.length > 1) {
        const names = winners.map(p => p.name);
        const text = names.length == 2 ? names.join(" and ") : names.join(", ");
        tNode.querySelector(".event-name").textContent = `🎉 ${text} are the winners! 🎉`;
    } else {
        // one person won
        tNode.querySelector(".event-name").textContent = `🎉 ${winners[0].name} is the winner! 🎉`;
    }

    historyContainer.appendChild(overNode);
    historyContainer.appendChild(tNode);
    focusLastEvent();
};

const playerUpdaters = [];
export const registerPlayerUpdater = (func: (oldState: any, newState: any) => {}): void => {
    playerUpdaters.push(func);
};

const gameUpdaters = [];
export const registerGameUpdater = (func: (oldState: any, newState: any) => {}): void => {
    gameUpdaters.push(func);
};

export const writeUpdate = (last: any, current: any): void => {
    if(current.gameOver && !last) {
        writeGameOver(current);
    }

    if(!last) {
        writeCurrentTurn(last, current);
        return;
    }

    // they should always have the same number of players
    const oldPlayerState: Map<string, any> = new Map<string, any>();
    last.players.forEach(p => oldPlayerState.set(p.name, p));
    current.players.forEach((newState) => {
        const oldState = oldPlayerState.get(newState.name);
        checkTokens(oldState.tokens, newState.tokens);

        // dev cards
        {
            const [newCards, oldCards, changedCards] = cardsDiff(oldState.devCards, newState.devCards);
            writeCardUpdate("Obtained", newCards, "development-cards");
            writeCardUpdate("Used", oldCards, "development-cards");
            writeSatchelUpdate(changedCards);
        }

        // reserved dev
        writeCardUpdate("Reserved", cardsDiff(oldState.reservedCards, newState.reservedCards)[0], "development-cards");

        // nobles
        writeCardUpdate("Visited by", cardsDiff(oldState.nobleCards, newState.nobleCards)[0], "nobles");

        // reserved nobles
        writeCardUpdate("Reserved", cardsDiff(oldState.reservedNobles, newState.reservedNobles)[0], "nobles");

        playerUpdaters.forEach(func => func(oldState, newState));
    });

    gameUpdaters.forEach(func => func(last, current));

    writeCurrentTurn(last, current);
    if(current.gameOver) {
        writeGameOver(current);
    }
};

export const writeBasicUpdate = (text: string): void => {
    const textNode = document.createElement("div");
    textNode.textContent = text;
    appendHistory(textNode.innerHTML, "card-event");
};