import { SETTINGS } from "./settings.js";
import { transition } from "./titleScreen.js";

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

        const url = new URL(`${SETTINGS.LS_API}/oauth/token`);
        url.search = new URLSearchParams(params).toString();

        fetch(url, {
            method: "POST",
            headers: headers
        })
            .catch((reason) => {
                // catch exceptions here

                // console.log(reason);
                // show user error message
                window.alert(reason);
            })
            .then((resp) => resp.json())
            .then((data) => {
                // console.log(data);

                if(!("access_token" in data)) {
                    window.alert(data.error_description);
                    return null;
                }

                SETTINGS.setAccessToken(data.access_token);
                SETTINGS.setRefreshToken(data.refresh_token);

                return SETTINGS.fetchUsername();
            }).then((username) => {
                if(!username) {
                    return;
                }

                SETTINGS.setUsername(username);

                transition("lobby.html");
            }).finally(() => enableInputs());
        event.preventDefault();
    });
});
