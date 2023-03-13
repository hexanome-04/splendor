
// show and hide functions for nav menu
export const gameMenu = document.querySelector(".navigation");

export const show = () => gameMenu.classList.add("active");
export const close = () => gameMenu.classList.remove("active");
