import { SETTINGS } from "./settings.js";
import { transition } from "./titleScreen.js";
import { startToastLoad, updateToastLoad } from "./notify.js";

var inputs = document.querySelectorAll(".center input, .center button");
var enableInputs = () => inputs.forEach(e => e.disabled = false);
var disableInputs = () => inputs.forEach(e => e.disabled = true);

document.addEventListener("DOMContentLoaded", () => {
    document.querySelector("form").addEventListener("submit", (event) => {
        disableInputs();

        const username = document.querySelector(".txt-field input[name='uname']").value;
        const password = document.querySelector(".txt-field input[name='psw']").value;
        const params = {
            "grant_type": "password",
            "username": username,
            "password": password
        };
        const headers = new Headers();
        headers.set("Authorization", `Basic ${btoa("bgp-client-name:bgp-client-pw")}`);

        const url = new URL(`${SETTINGS.getLS_API()}/oauth/token`);
        url.search = new URLSearchParams(params).toString();

        const notify = startToastLoad("Logging in...");

        fetch(url, {
            method: "POST",
            headers: headers
        })
            .catch((reason) => {
                // catch exceptions here

                // console.log(reason);
                // show user error message
                updateToastLoad(notify, reason, "error", 2000);
            })
            .then((resp) => !resp ? null : resp.json())
            .then((data) => {
                // console.log(data);
                if(!data) {
                    updateToastLoad(notify, "Failed to connect to LS.", "error", 2000);
                    return null;
                }

                if(!("access_token" in data)) {
                    updateToastLoad(notify, data.error_description, "error", 2000);
                    return null;
                }

                SETTINGS.setAccessToken(data.access_token);
                SETTINGS.setRefreshToken(data.refresh_token);

                return SETTINGS.fetchUsername();
            }).then((username) => {
                if(!username) {
                    return;
                }
                updateToastLoad(notify, "Logged in!", "success");
                SETTINGS.setUsername(username);

                transition("/lobby/");
            }).finally(() => enableInputs());
        event.preventDefault();
    });
});
