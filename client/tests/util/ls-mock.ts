import { type Page } from '@playwright/test';

// maybe there's a better way to specify the routes (w/ ports)?

export const mockGetUsername = (page: Page, username: string) => page.route("**/oauth/username**", route => {
    route.fulfill({
        body: username
    });
});

// export const itemsRoute = (page: Page) => page.route("**/oauth/username", route => {
//     route.fulfill({
//         body: JSON.stringify(
//             [{ id: 1, name: "first item"}, { id: 2, name: "second item"}]
//         )
//     });
// });
