const transition_el = document.querySelector(".transition");
const button_el = document.querySelectorAll(".title-login-button");

export function transition(newLink) {

    transition_el.classList.add("is-active");

    setTimeout(() => {
        window.location.href = newLink;
    }, 400);
}

window.addEventListener("load", () => {
    for (let i = 0; i < button_el.length; i++) {
        const btn = button_el[i];

        btn.addEventListener("click", function() {
            transition("login.html");
        });
    }
});

window.onpageshow = () => {
    setTimeout(() => {
        transition_el.classList.remove("is-active");
    }, 400);
};