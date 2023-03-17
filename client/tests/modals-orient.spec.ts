import { test, expect, Locator, type Page } from "@playwright/test";
import { Actions, DevCard, OrientDevCard, TokenType } from "./util/game.js";
import { mockGetUsername } from "./util/ls-mock.js";
import { checkActionRequest, verifyModalCloses } from "./util/modal-util.js";
import { mockGameState, createBasicGame } from "./util/server-mock.js";

test.describe.parallel("Test orient modals", () => {
    const MAIN_USER = "linus";

    test.beforeEach(async ({ page }) => {
        // page.on('console', msg => console.log(msg.location().url + " - [" + msg.location().lineNumber + "]: " + msg.text()))

        mockGetUsername(page, MAIN_USER);
        const game = createBasicGame();
        mockGameState(page, game);
        await page.goto("/gameboard/?sessionId=123");
    });

    test("Verify choose satchel modal works", async ({ page }) => {
        const game = createBasicGame();
        const player = game.getPlayer(MAIN_USER);
        const satchelCard = new OrientDevCard("o1_test", TokenType.Satchel);

        player.bonuses.setCount(TokenType.Brown, 1);
        player.devCards.push(new DevCard("d_111", TokenType.Brown), satchelCard);
        game
            .resetValidActions()
            .curValidActions.push(Actions.CHOOSE_SATCHEL_TOKEN);

        mockGameState(page, game);

        // verify that the modal appears properly
        const satchelModal = page.locator("#choose-satchel-modal");
        const brownTokenSelect = satchelModal.locator(".brown-token");
        await expect(satchelModal).toBeVisible();
        await expect(brownTokenSelect).toBeVisible();
        await expect(satchelModal.locator(".green-token")).toBeHidden();

        // verify that action request is sent
        const requestPromise = checkActionRequest(page, Actions.CHOOSE_SATCHEL_TOKEN);

        await brownTokenSelect.click(); // select token for satchel
        await satchelModal.getByText("CONFIRM").click();
        const request = await requestPromise;
        const jsonData = request.postDataJSON();

        expect(jsonData.cardId).toEqual(satchelCard.id);
        expect(jsonData.selected).toEqual(TokenType.Brown.toString());

        await verifyModalCloses(page, game, satchelModal);
    });

    // do we need one for both?
    test("Verify cascade modal works", async ({ page }) => {
        const game = createBasicGame();
        game.resetValidActions()
            .curValidActions.push(Actions.CASCADE_1);
        mockGameState(page, game);

        // verify that the modal appears properly
        const cascadeModal = page.locator("#cascade-modal");
        await expect(cascadeModal).toBeVisible();

        // verify cards appear
        const cardsSelection = cascadeModal.locator(".modal-board-cards .board-card-dev");
        const allCards = await cardsSelection.all();
        expect(allCards.length).toBeGreaterThan(0);

        // verify that action request is sent
        const requestPromise = checkActionRequest(page, Actions.CASCADE_1);

        await allCards[0].click(); // select first
        await cascadeModal.getByText("CONFIRM").click();
        const request = await requestPromise;
        const jsonData = request.postDataJSON();

        // we selected the first one in the cascade, therefore it should be the first in this list
        expect(jsonData.cardId).toEqual(game.tier1Deck.visibleCards[0].id);

        await verifyModalCloses(page, game, cascadeModal);
    });

    test("Verify reserve nobles modal works", async ({ page }) => {
        const game = createBasicGame();
        game.resetValidActions()
            .curValidActions.push(Actions.RESERVE_NOBLE);
        mockGameState(page, game);

        // verify that the modal appears properly
        const modal = page.locator("#reserve-noble-modal");
        await expect(modal).toBeVisible();

        // verify nobles appear
        const cardsSelection = modal.locator(".noble-card");
        const allCards = await cardsSelection.all();
        expect(allCards.length).toBeGreaterThan(0);

        // verify that action request is sent
        const requestPromise = checkActionRequest(page, Actions.RESERVE_NOBLE);

        await allCards[0].click(); // select first
        await modal.getByText("CONFIRM").click();
        const request = await requestPromise;
        const jsonData = request.postDataJSON();

        // we selected the first one in the cascade, therefore it should be the first in this list
        expect(jsonData.cardId).toEqual(game.nobleDeck.visibleCards[0].id);

        await verifyModalCloses(page, game, modal);
    });
});

