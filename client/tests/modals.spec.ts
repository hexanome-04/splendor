import { test, expect, Locator, type Page } from "@playwright/test";
import { mockGetUsername } from "./util/ls-mock.js";
import { mockGameState, createBasicGame } from "./util/server-mock.js";

test.describe.parallel("Test default game modals", () => {
    const MAIN_USER = "linus";

    test.beforeEach(async ({ page }) => {
        // page.on('console', msg => console.log(msg.location().url + " - [" + msg.location().lineNumber + "]: " + msg.text()))

        const game = createBasicGame();
        mockGameState(page, game);
        mockGetUsername(page, MAIN_USER);
        await page.goto("/gameboard/?sessionId=123");
    });

    test("Verify take token modal appears if it's your turn", async ({ page }) => {
        await expect(page.locator("#your-turn-modal")).toBeVisible();
    });

    test("Verify take token modal does not appear if it's not your turn", async ({ page }) => {
        mockGetUsername(page, "user3"); // change our username
        await page.reload();
        await page.waitForLoadState();

        await expect(page.locator("#your-turn-modal")).toBeHidden();
    });

});

