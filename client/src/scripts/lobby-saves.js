import { SETTINGS, GAME_VERSION_TO_BOARD } from "./settings.js";
import { createNewSession, showSessionTab, focusSession } from "./lobby.js";
import { showError } from "./notify.js";

/**
 * Initiate the calls to load/update game save information for the current user.
 */
export const checkForGameSaves = () => {

    // it shouldn't matter if this fails (prolly)
    try {
        updateGameSaveInfo().then(() => {
            console.log("Completed update for game saves");
        });
    } catch(e) {
        console.log("Something went wrong while retrieving info for game saves.");
        console.log(e);
    }
};

/**
 * Retrieve extra details about the current users game saves from the backend.
 * 
 * @returns {Promise<Array<GameSaveInfo>>}
 */
const getGameSaveDetails = async () => {
    const url = new URL(`${SETTINGS.getGS_API()}/api/saves`);
    url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

    const resp = await fetch(url);
    if(!resp.ok) { // ignore errors
        return [];
    }
    return await resp.json();
};

/**
 * Retrieve game saves for the specified version of Splendor from the LS.
 * 
 * @param {GameVersion} gameVersion Splendor version
 * @returns {Promise<Array<GameSaveData>>}
 */
const getGameSaveForVersion = async (gameVersion) => {
    const url = new URL(`${SETTINGS.getLS_API()}/api/gameservices/splendor_${gameVersion}/savegames`);
    url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

    const resp = await fetch(url);
    if(!resp.ok) { // ignore errors
        return [];
    }
    return await resp.json();
};

/**
 * Creats a new game save element to be added.
 * 
 * @param {GameSaveInfo} info game save info
 * @returns {HTMLTableRowElement} new row to be added
 */
const createNewSaveElement = (info) => {
    const tempNode = document.querySelector("#load-saved-game-template").content.cloneNode(true);
    const saveId = info.gameSaveData.savegameid;
    const gameVer = info.gameSaveData.gamename.replace("splendor_", "");
    const sesPlayers = info.gameSaveData.players;
    const timestamp = new Date(0);
    timestamp.setUTCMilliseconds(info.timestamp);

    const trNode = tempNode.querySelector("tr");
    trNode.setAttribute("save-id", saveId);
    trNode.setAttribute("version", gameVer);
    if(info.creator === SETTINGS.getUsername()) {
        trNode.setAttribute("created", true);
    }

    trNode.querySelector(".game-name").textContent = `${info.name}`;
    trNode.querySelector(".game-creator-name").textContent = `${info.creator}`;
    trNode.querySelector(".game-timestamp").textContent = `${timestamp.toLocaleString()}`;
    trNode.querySelector(".game-players-info").textContent = `${sesPlayers.join(", ")}`;

    const loadBtn = trNode.querySelector(".load-btn");
    const delBtn = trNode.querySelector(".del-btn");
    loadBtn.onclick = () => {
        loadBtn.disabled = true;
        createNewSession(info.gameSaveData.gamename, saveId)
            .then((sesId) => {
                // move to sessions screen
                showSessionTab();
                focusSession(sesId);
            }).catch((err) => showError(err))
            .finally(() => loadBtn.disabled = false);
    };
    delBtn.onclick = () => deleteGameSave(delBtn, saveId);

    return tempNode;
};

const deleteGameSave = (btn, saveId) => {
    btn.disabled = true;

    SETTINGS.verifyCredentials().then(() => {
        const url = new URL(`${SETTINGS.getGS_API()}/api/saves/${saveId}`);
        url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

        fetch(url, {
            method: "DELETE"
        }).then((resp) => {
            if(!resp.ok) {
                resp.text().then((data) => showError(data));
            } else {
                // delete element
                const elm = document.querySelector(`.saved-games-table tr[save-id="${saveId}"]`);
                if(elm) {
                    elm.remove();
                }
            }
        }).catch((err) => {
            throw new Error(err);
        }).finally(() => btn.disabled = false);
    }).catch((err) => showError(err));
};

const updateGameSaveInfo = async () => {
    await SETTINGS.verifyCredentials();
    const username = SETTINGS.getUsername();

    let gameSavesLS = [];
    for(const gv of Object.keys(GAME_VERSION_TO_BOARD)) {
        const gvGameSaves = await getGameSaveForVersion(gv);
        gameSavesLS = [...gameSavesLS, ...gvGameSaves];
    }

    const gameSavesInfo = {};
    // go through each item in list and make entry with save id as key
    const savesGS = await getGameSaveDetails();
    for(const s of savesGS) {
        gameSavesInfo[s.gameSaveData.savegameid] = s;
    }

    if(gameSavesLS.length !== savesGS.length) {
        console.log(`Backend has ${savesGS.length} game saves, but LS has ${gameSavesLS.length}.`);
    }


    // now update the html
    const tableElm = document.querySelector(".saved-games-table");
    const currentSaveIds = [];
    tableElm.querySelectorAll(`tr:not(:first-child)`).forEach((elm) => {
        currentSaveIds.push(elm.getAttribute("save-id"));
    });

    // current save ids on LS
    const serverSavesIds = [];
    gameSavesLS.forEach(v => serverSavesIds.push(v.savegameid));

    // remove obsolete saves
    const obsoleteSaves = currentSaveIds.filter(x => !serverSavesIds.includes(x));
    obsoleteSaves.forEach(saveId => tableElm.querySelector(`tr[save-id="${saveId}"]`).remove());


    // split game saves into 2 lists
    const amCreator = (data) => {
        const sid = data.savegameid;
        return sid in gameSavesInfo && gameSavesInfo[sid].creator === username;
    };
    const sortByTimestamp = (data1, data2) => {
        // if here, should have only the ones that exist in backend soooo no check now
        const t1 = gameSavesInfo[data1.savegameid].timestamp;
        const t2 = gameSavesInfo[data2.savegameid].timestamp;
        if (t1 < t2) {
            return -1;
        }
        if (t1 > t2) {
            return 1;
        }
        return 0;
    };
    const yourGameSaves = gameSavesLS.filter(info => amCreator(info)).sort(sortByTimestamp);
    const otherGameSaves = gameSavesLS.filter(info => !amCreator(info)).sort(sortByTimestamp);
    const allGameSaves = [...yourGameSaves, ...otherGameSaves];

    const rowsBody = tableElm.querySelector("tbody");
    const appendToTable = (prevSibling, saveData) => {
        const saveId = saveData.savegameid;
        const newElm = createNewSaveElement(gameSavesInfo[saveId]);
        if(prevSibling) {
            // insert it before (so everything is sorted by timestamp)
            prevSibling.after(newElm);
        } else {
            // insert at start of table
            rowsBody.querySelector("tr:first-child").after(newElm);
        }
    }

    for(let i = 0; i < allGameSaves.length; i++) {
        const saveData = allGameSaves[i];
        const saveId = saveData.savegameid;
        const existing = rowsBody.querySelector(`tr[save-id="${saveId}"]`);
        if(!existing && saveId in gameSavesInfo) {
            const prevI = i - 1;
            const prevSibling = prevI >= 0 ? rowsBody.querySelector(`tr[save-id="${allGameSaves[prevI].savegameid}"]`) : null;
            appendToTable(prevSibling, saveData);
        }
    }
};
