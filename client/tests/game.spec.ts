import { test, expect, type Page } from "@playwright/test";
import { mockGetUsername } from "./util/ls-mock.js";
import { mockGameState, createBasicGame } from "./util/server-mock.js";

test.describe.parallel("Test orient game", () => {
    const MAIN_USER = "linus";

    test.beforeEach(async ({ page }) => {
        // page.on('console', msg => console.log(msg.location().url + " - [" + msg.location().lineNumber + "]: " + msg.text()))

        const game = createBasicGame();
        await mockGameState(page, game);
        mockGetUsername(page, MAIN_USER);
        await page.goto("/gameboard/?sessionId=123");
    });

    test("Verify dev cards & nobles appear", async ({ page }) => {
        // await page.screenshot({ path: 'screenshot.png' });

        const game = createBasicGame();
        const nobleCount = game.players.length + 1;

        await expect(page.locator("#board .board-nobles .noble-card img[src]")).toHaveCount(nobleCount);
        await expect(page.locator("#board .board-cards-dev-selectable .board-card-dev:not(.board-card-dev-orient) img[src]")).toHaveCount(12);
        await expect(page.locator("#board .board-cards-dev-selectable .board-card-dev.board-card-dev-orient img[src]")).toHaveCount(6);
    });
});

