// TODO: Move js into <script> of GameNavMenu as a separate file is not needed
// or just have it all in src/ at least.
import { SETTINGS } from "./settings.js";

// show and hide functions
const gameMenu = document.querySelector(".navigation");
const show = () => gameMenu.classList.add("active");
const close = () => gameMenu.classList.remove("active");

document.getElementById("menu-icon").addEventListener("click", show);
document.querySelector(".resume").parentNode.addEventListener("click", close);

// quit -> go to lobby
gameMenu.querySelector(".quit").parentNode.addEventListener("click", () => {
    window.location.href = "/lobby/";
});

const restartBtn = gameMenu.querySelector(".restart").parentNode;
restartBtn.addEventListener("click", () => {
    restartBtn.disabled = true;

    document.querySelector("#restart-game-modal").classList.add("show");
});

// enable restart no button (should be within the RestartGameModal component TODO)
const closeRestartModal = () => document.querySelector("#restart-game-modal").classList.remove("show");
document.querySelector("#restart-game-modal #restart-no-btn").addEventListener("click", () => {
    closeRestartModal();
});

// enable restart yes button (ditto TODO)
document.querySelector("#restart-game-modal #restart-yes-btn").addEventListener("click", () => {
    SETTINGS.verifyCredentials().then(() => {
        const searchParams = new URLSearchParams(window.location.search);
        const sessionId = searchParams.get("sessionId");

        const url = new URL(`${SETTINGS.GS_API}/api/sessions/${sessionId}/restart`);
        url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

        fetch(url, {
            method: "POST"
        }).then((resp) => {
            if(!resp.ok) {
                resp.json().then((data) => { window.alert("Error: " + data["error"]); });
            } else {
                close();
            }
            closeRestartModal();
        }).catch((err) => {
            window.alert("Error: " + err);
        }).finally(() => restartBtn.disabled = false);
    });
});