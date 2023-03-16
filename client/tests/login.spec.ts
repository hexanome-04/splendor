import { test, expect } from "@playwright/test";

test("Login goes to lobby", async ({ page }) => {
    await page.goto("/");

    page.locator("button:text('Login')").click();

    await expect(page).toHaveURL("/login/");
});

