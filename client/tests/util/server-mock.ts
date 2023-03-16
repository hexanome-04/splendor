import { type Page } from '@playwright/test';
import { Game, Actions, NobleCard, allTokens, OrientDevCard, TokenType, DevCard } from "./game.js";
import crypto from "crypto-js";

const { MD5 } = crypto;

// maybe there's a better way to specify the routes (w/ ports)?

export const mockGameState = (page: Page, gamestate: Game) => {
    const data = JSON.stringify(gamestate);
    const hash = MD5(data).toString();

    page.route("**/api/sessions/*?hash=**", route => {
        const url = new URL(route.request().url());
        const clientHash = url.searchParams.get("hash");
        if(hash === clientHash) {
            route.fulfill({ status: 204 });
            return;
        }
        
        route.fulfill({
            contentType: "application/json",
            body: data.toString()
        });
    });
};


export const createBasicGame = (): Game => {
    const game = new Game()
        .addDefaultActions()
        .addPlayer("linus", "maex");

    game.nobleDeck
        .addOrReplace(new NobleCard("20002"))
        .addOrReplace(new NobleCard("20003"))
        .addOrReplace(new NobleCard("20004"));

    for(const tt of allTokens) {
        if(tt !== TokenType.Satchel) {
            game.tokens.setCount(tt, 4);
        }
    }

    for(let i = 0; i < 2; i++) {
        game.tier1OrientDeck.addOrReplace(new OrientDevCard("o1_" + i, TokenType.Red)).setCanDraw(true);
        game.tier2OrientDeck.addOrReplace(new OrientDevCard("o2_" + i, TokenType.Green)).setCanDraw(true);
        game.tier3OrientDeck.addOrReplace(new OrientDevCard("o2_" + i, TokenType.Blue)).setCanDraw(true);
    }

    for(let i = 0; i < 4; i++) {
        game.tier1Deck.addOrReplace(new DevCard("d1_" + i, TokenType.Red)).setCanDraw(true);
        game.tier2Deck.addOrReplace(new DevCard("d2_" + i, TokenType.Green)).setCanDraw(true);
        game.tier3Deck.addOrReplace(new DevCard("d3_" + i, TokenType.Blue)).setCanDraw(true);
    }

    return game;
}