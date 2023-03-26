
// show and hide functions for nav menu
export const gameMenu = document.querySelector(".navigation");

export const showDrawer = () => gameMenu.classList.add("active");
export const closeDrawer = () => gameMenu.classList.remove("active");

/**
 * Returns the session id if in a game, otherwise null.
 * @returns {String | null} session id
 */
export const getSessionId = () => {
    const searchParams = new URLSearchParams(window.location.search);
    return searchParams.get("sessionId");
};

/**
 * We have a separate navigation context for the game menu options.
 * This is to ensure that it's different from the action turn modals
 * and so that they do not interfere with each other.
 */
const menuNavContext = [];

const showMenu = (selector) => {
    document.querySelector(selector).classList.add("show");
};

const closeMenu = (selector) => {
    document.querySelector(selector).classList.remove("show");
};

export const showPreviousMenu = () => {
    // pop top of stack (current modal), close it
    closeMenu(menuNavContext.pop());

    // open the previous one, if any
    if(menuNavContext.length > 0) {
        showMenu(menuNavContext[menuNavContext.length-1]);
    }
};

export const showNextMenu = (selector) => {
    // close current
    if(menuNavContext.length > 0) {
        closeMenu(menuNavContext[menuNavContext.length-1]);
    }

    // push new
    menuNavContext.push(selector);
    showMenu(menuNavContext[menuNavContext.length-1]);
};

