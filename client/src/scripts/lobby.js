import { SETTINGS, GAME_VERSION_TO_BOARD } from "./settings.js";

// perhaps we need a better build system
// eslint-disable-next-line no-undef
var MD5 = CryptoJS.MD5;

document.addEventListener("DOMContentLoaded", () => {

    var sessionTabBtn = document.querySelector("#select-sessions");
    var loadgameTabBtn = document.querySelector("#select-loadgame");
    var mainElm = document.querySelector("main");
    var logOutBtn = document.querySelector(".logout-btn");

    function changeTab(selectedElm, otherElm) {
        if(otherElm.classList.contains("selected")) {
            otherElm.classList.remove("selected");
        }

        if(!selectedElm.classList.contains("selected")) {
            selectedElm.classList.add("selected");
        }
    }

    sessionTabBtn.onclick = () => {
        changeTab(sessionTabBtn, loadgameTabBtn);
        mainElm.setAttribute("selected", "sessions");
    };

    loadgameTabBtn.onclick = () => {
        changeTab(loadgameTabBtn, sessionTabBtn);
        mainElm.setAttribute("selected", "loadgame");
    };

    logOutBtn.onclick = () => {
        window.location.href = "/";
    };

    // PLAYER GREETING
    document.getElementById("player-greeting").innerHTML = greetUser();
    console.log(greetUser());

    function greetUser() {
        var welcome = "Hello, " + SETTINGS.getUsername() +"!";
        return welcome;
    }
    
    /**
     * Create a session with the specified version of Splendor.
     *
     * @param {string} gameVersion version of splendor
     */
    function createSession(gameVersion) {
        const createBtn = document.querySelector(".create-session-btn");
        createBtn.disabled = true;

        SETTINGS.verifyCredentials().then(() => {
            const postData = {
                "creator": SETTINGS.getUsername(),
                "game": gameVersion,
                "savegame": ""
            };

            const url = new URL(`${SETTINGS.getLS_API()}/api/sessions`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

            fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(postData)
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { throw new Error(data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() => createBtn.disabled = false);
        });
    }

    // SPECIFY GAME VERSION
    document.querySelectorAll(".game-options li").forEach(option => {
        option.addEventListener("click", () => {
            const version = "splendor_" + option.getAttribute("version");
            createSession(version);
        });
    });

    const joinSession = (elm) => {
        elm.disabled = true;
        const sessionId = elm.closest("tr[session-id]").getAttribute("session-id");

        SETTINGS.verifyCredentials().then(() => {
            const url = new URL(`${SETTINGS.getLS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

            fetch(url, {
                method: "PUT"
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { throw new Error(data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() => elm.disabled = false);
        });
    };

    const leaveSession = (elm) => {
        elm.disabled = true;
        const sessionId = elm.closest("tr[session-id]").getAttribute("session-id");

        SETTINGS.verifyCredentials().then(() => {
            const url = new URL(`${SETTINGS.getLS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();
    
            fetch(url, {
                method: "DELETE"
            }).then((resp) => {
                if(!resp.ok) {
                    resp.json().then((data) => { window.alert("Error: " + data["error"]); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() => elm.disabled = false);
        });
    };

    const deleteSession = (elm) => {
        elm.disabled = true;
        const sessionId = elm.closest("tr[session-id]").getAttribute("session-id");

        SETTINGS.verifyCredentials().then(() => {
            const url = new URL(`${SETTINGS.getLS_API()}/api/sessions/${sessionId}`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

            fetch(url, {
                method: "DELETE"
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { throw new Error(data); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() => elm.disabled = false);
        });
    };

    const launchSession = (elm) => {
        elm.disabled = true;
        const sessionId = elm.closest("tr[session-id]").getAttribute("session-id");

        SETTINGS.verifyCredentials().then(() => {
            const url = new URL(`${SETTINGS.getLS_API()}/api/sessions/${sessionId}`);
            url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

            fetch(url, {
                method: "POST"
            }).then((resp) => {
                if(!resp.ok) {
                    resp.text().then((data) => { window.alert("Error: " + data["error"]); });
                }
            }).catch((err) => {
                window.alert("Error: " + err);
            }).finally(() => elm.disabled = false);
        });
    };

    const playSession = (elm, sesId, gameVer) => {
        elm.disabled = true;

        SETTINGS.verifyCredentials().then(() => {
            window.location.href = `/${GAME_VERSION_TO_BOARD[gameVer]}/?sessionId=${sesId}`
        }).finally(() => elm.disabled = false);
    };

    // Related to checking sessions that user can join
    var availableSessionHash = "-";

    function updateAvailableSessions(data) {
        // first remove any sessions that don't need to be shown anymore (if currently shown)
        const tableSel = ".current-sessions-table";
        const templateSel = "#available-session-template";

        const username = SETTINGS.getUsername();

        // map sessions
        const nowAvailableSesions = {};
        const nowAvailableSesIdList = [];
        for (const [sessionId, value] of Object.entries(data.sessions)) {
            nowAvailableSesions[sessionId] = value;
            nowAvailableSesIdList.push(sessionId);
        }

        // make list of all sessions already shown id
        const current = document.querySelectorAll(`${tableSel} tr:not(:first-child)`);
        const curSesIdList = [];
        current.forEach((elm) => {
            curSesIdList.push(elm.getAttribute("session-id"));
        });

        // calc difference of lists
        const toAddSesIdList = nowAvailableSesIdList.filter(x => !curSesIdList.includes(x));

        // remove session that are no longer there
        const toRemoveSesIdList = curSesIdList.filter(x => !nowAvailableSesIdList.includes(x));

        // Remove non available sessions
        toRemoveSesIdList.forEach((sesId) => {
            document.querySelector(`${tableSel} tr[session-id="${sesId}"]`).remove();
        });

        const setAttributes = (ses, node) => {
            const sesPlayers = ses.players;

            if(sesPlayers.includes(username)) {
                node.setAttribute("joined", "true");
            } else {
                node.removeAttribute("joined");
            }

            if(ses.creator === username) {
                node.setAttribute("created", "true");
                if(sesPlayers.length > 1 && !ses.launched) {
                    node.setAttribute("launchable", "true");
                } else {
                    node.removeAttribute("launchable");
                }
            }

            if(ses.launched) {
                node.setAttribute("started", "true");
            }
        };

        // Add available sessions
        const tbl = document.querySelector(tableSel);
        toAddSesIdList.forEach((sesId) => {
            const tempNode = document.querySelector(templateSel).content.cloneNode(true);
            const ses = nowAvailableSesions[sesId];
            const gameVer = ses.gameParameters.name.replace("splendor_", "");

            const trNode = tempNode.querySelector("tr");
            trNode.setAttribute("session-id", sesId);
            trNode.setAttribute("version", gameVer);

            trNode.querySelector(".session-game-name").textContent = ses.gameParameters.displayName;
            trNode.querySelector(".session-creator-name").textContent = ses.creator;

            const sesPlayers = ses.players;
            const curP = ses.players.length;
            const maxP = ses.gameParameters.maxSessionPlayers;
            trNode.querySelector(".session-players-info").textContent = `[${curP}/${maxP}]: ${sesPlayers.join(", ")}`;

            setAttributes(ses, trNode);

            // assign button events
            const delBtn = tempNode.querySelector(".del-btn.ses-btn");
            const leaveBtn = tempNode.querySelector(".leave-btn.ses-btn");
            const joinBtn = tempNode.querySelector(".join-btn.ses-btn");
            const launchBtn = tempNode.querySelector(".launch-btn.ses-btn");
            const playBtn = tempNode.querySelector(".play-btn.ses-btn");
            const spectateBtn = tempNode.querySelector(".spectate-btn.ses-btn");
            delBtn.onclick = () => { deleteSession(delBtn); };
            leaveBtn.onclick = () => { leaveSession(leaveBtn); };
            joinBtn.onclick = () => { joinSession(joinBtn); };
            launchBtn.onclick = () => { launchSession(launchBtn); };
            playBtn.onclick = () => playSession(playBtn, sesId, gameVer);
            spectateBtn.onclick = () => playSession(spectateBtn, sesId, gameVer);

            tbl.appendChild(tempNode);
        });


        // Must also check if player list changed
        const alreadyInListId = nowAvailableSesIdList.filter(x => curSesIdList.includes(x));
        alreadyInListId.forEach((sesId) => {
            const ses = nowAvailableSesions[sesId];
            const sesPlayers = ses.players;
            const curP = ses.players.length;
            const maxP = ses.gameParameters.maxSessionPlayers;

            const trNode = document.querySelector(`tr[session-id="${sesId}"]`);

            setAttributes(ses, trNode);

            const node = document.querySelector(`${tableSel} tr[session-id="${sesId}"] .session-players-info`);
            const newText = `[${curP}/${maxP}]: ${sesPlayers.join(", ")}`;
            if(node.textContent !== newText) {
                node.textContent = newText;
            }
        });

    }

    async function checkAvailableSesison() {
        await SETTINGS.verifyCredentials();

        console.log("[AS] Checking...");
        var nextCallTime = 1;
        const params = {
            "hash": availableSessionHash
        };
        const url = new URL(`${SETTINGS.getLS_API()}/api/sessions`);
        url.search = new URLSearchParams(params).toString();

        fetch(url, {
            method: "GET",
        }).then((resp) => resp.text()).then((t) => {
            // console.log("Received available sessions update");
            var data = {};
            try {
                data = JSON.parse(t);
            } catch (e) {
                // not a valid json string
                return;
            }

            // update hash
            // console.log("Update: " + t);
            const newHash = MD5(t);

            // update only if needed
            if(newHash !== availableSessionHash) {
                console.log("[AS] Update available!");
                availableSessionHash = newHash;
                updateAvailableSessions(data);
            }

        }).catch((err) => {
            if(!err.toString().includes("Failed to fetch" )) {
                console.log("[AS] Error during check (retry 30s): " + err);
            }
            nextCallTime = 30000;
        }).finally(() => {

            // recall
            setTimeout(checkAvailableSesison, nextCallTime);
        });
    }
    // start the updates
    setTimeout(checkAvailableSesison, 1);

});