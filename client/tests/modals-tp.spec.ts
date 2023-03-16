import { test, expect, Locator, type Page } from "@playwright/test";
import { Actions, DevCard, OrientDevCard, TokenType } from "./util/game.js";
import { mockGetUsername } from "./util/ls-mock.js";
import { checkActionRequest, verifyModalCloses } from "./util/modal-util.js";
import { mockGameState, createBasicGame } from "./util/server-mock.js";

test.describe.parallel("Test trading posts modals", () => {
    const MAIN_USER = "linus";

    test.beforeEach(async ({ page }) => {
        // page.on('console', msg => console.log(msg.location().url + " - [" + msg.location().lineNumber + "]: " + msg.text()))

        mockGetUsername(page, MAIN_USER);
        const game = createBasicGame();
        mockGameState(page, game);
        await page.goto("/gameboard-tradingposts/?sessionId=123");
    });

    test("Verify take extra token modal works", async ({ page }) => {
        const game = createBasicGame();
        game
            .resetValidActions()
            .curValidActions.push(Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);
        mockGameState(page, game);

        // verify that the modal appears properly
        const takeModal = page.locator("#take-extra-token-modal");
        const takeGreenToken = takeModal.locator(".green-token");
        await expect(takeModal).toBeVisible();
        takeGreenToken.click(); // select token to take
        await expect(takeGreenToken).toHaveClass(/selected/);

        // verify that next modal appears properly
        await takeModal.getByText("CONFIRM").click();
        await expect(takeModal).toBeHidden();

        const putModal = page.locator("#putback-extra-token-modal");
        const putBrownToken = putModal.locator(".brown-token");
        await expect(putModal).toBeVisible();
        putBrownToken.click(); // select token to put back
        await expect(putBrownToken).toHaveClass(/selected/);

        // verify that action request is sent
        const requestPromise = checkActionRequest(page, Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);

        await putModal.getByText("CONFIRM").click();
        const request = await requestPromise;
        const jsonData = request.postDataJSON();

        expect(jsonData.takeToken).toEqual(TokenType.Green.toString());
        expect(jsonData.putBackToken).toEqual(TokenType.Brown.toString());

        await verifyModalCloses(page, game, putModal);
    });

});

