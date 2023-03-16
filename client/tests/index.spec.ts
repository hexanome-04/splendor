import { test, expect } from "@playwright/test";

test("Clicking start goes to login page", async ({ page }) => {
    await page.goto("/");

    page.locator("button:text('Login')").click();

    await expect(page).toHaveURL("/login/");
});

