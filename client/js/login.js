
var inputs = document.querySelectorAll(".center input, .center button");
var centerDiv = document.querySelector(".center");
var enableInputs = () => inputs.forEach(e => e.disabled = false);
var disableInputs = () => inputs.forEach(e => e.disabled = true);

document.querySelector("body").onload = () => {
    document.querySelector("form").addEventListener("submit", (event) => {
        disableInputs();
        centerDiv.classList.remove("invalid");

        const username = document.querySelector(".txt-field input[name='uname']").value;
        const password = document.querySelector(".txt-field input[name='psw']").value;
        const params = {
            "grant_type": "password",
            "username": username,
            "password": password
        };
        const headers = new Headers();
        headers.set("Authorization", `Basic ${btoa("bgp-client-name:bgp-client-pw")}`);

        const url = new URL(`${window.SETTINGS.LS_API}/oauth/token`);
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

                if("error" in data) {
                    centerDiv.classList.add("invalid");
                    window.alert(data.error_description);
                    return;
                }

                window.SETTINGS.setAccessToken(data.access_token);
                window.SETTINGS.setRefreshToken(data.refresh_token);

                location.href = "lobby.html";
            })
            .finally(() => enableInputs());
        event.preventDefault();
    });
};
