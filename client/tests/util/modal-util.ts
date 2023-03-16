import { expect, Locator, type Page } from "@playwright/test";
import { Actions, Game } from "./game.js";
import { mockGameState } from "./server-mock.js";

export const checkActionRequest = (page: Page, action: Actions) => {
    return page.waitForRequest(request => {
        const url = new URL(request.url());
        return url.pathname.endsWith(action.toString()) && request.method() === "PUT";
    }, { timeout: 5000 });
};

export const verifyModalCloses = async (page: Page, game: Game, modal: Locator) => {
    // update the game state (sufficient to change who's turn it is)
    game
        .resetValidActions()
        .addDefaultActions()
        .setTurnCounter(1);
    mockGameState(page, game);

    await expect(modal).toBeHidden();
};