import { SETTINGS } from "./settings";
import { startToastLoad, updateToastLoad } from "./notify";

/**
 * Common function to ask LS to update user details.
 * @param {*} options 
 * @returns {Promise<Boolean>} success
 */
export const updateUserData = async (options) => {
    await SETTINGS.verifyCredentials(); // ensure logged in

    const { endpoint, method, loadMessage, successMessage, bodyData = "{}" } = options;

    const url = new URL(`${SETTINGS.getLS_API()}${endpoint}`);
    url.search = new URLSearchParams({ "access_token": SETTINGS.getAccessToken() }).toString();

    const notify = startToastLoad(loadMessage);
    try {
        const resp = await fetch(url, {
            method: method,
            headers: { "content-type": "application/json" },
            body: JSON.stringify(bodyData)
        });

        if(!resp.ok) {
            const text = await resp.text();
            throw new Error(text);
        } else {
            updateToastLoad(notify, successMessage, "success");
            return true;
        }
    } catch(err) {
        updateToastLoad(notify, err.toString(), "error");
    }
    return false;
};

/**
 * Get the current users information from LS.
 * @returns {Promise<JSON>} data
 */
export const getUserDetail = async () => {
    const url = new URL(`${SETTINGS.getLS_API()}/api/users/${SETTINGS.getUsername()}`);
    url.search = new URLSearchParams({ "access_token": SETTINGS.getAccessToken() }).toString();

    const resp = await fetch(url, { method: "GET" });
    if(!resp.ok) {
        const text = await resp.text();
        console.log("Issue while getting user information: " + text);
        return null;
    }

    return await resp.json();
};
