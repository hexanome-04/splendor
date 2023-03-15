import { SETTINGS } from "./settings.js";

type ActionResponse = {
    error: boolean;
    message: string;
};

type ActionDataCallback = () => JSON;

/**
 * Allows the client to perform an action.
 * This will perform a PUT request to the server with the specified data.
 * 
 * @param actionType one of Actions (from server enum)
 * @param actionDataCallback JSON data to be used or a callback to retrieve the JSON data
 * @returns response from server
 */
export const performAction = async (actionType: string, actionDataCallback: ActionDataCallback | JSON) => {
    await SETTINGS.verifyCredentials()

    const windowParams = (new URL(document.location.toString())).searchParams;
    const sessionId = windowParams.get("sessionId");

    const url = new URL(`${SETTINGS.getGS_API()}/api/sessions/${sessionId}/players/${SETTINGS.getUsername()}/actions/${actionType}`);
    url.search = new URLSearchParams({"access_token": SETTINGS.getAccessToken()}).toString();

    let actionData: JSON;
    if(actionDataCallback as ActionDataCallback) {
        actionData = (actionDataCallback as ActionDataCallback)();
    } else {
        actionData = (actionDataCallback as JSON);
    };

    const resp = await fetch(url, {
        method: "PUT",
        headers: { "content-type": "application/json" },
        body: JSON.stringify(actionData)
    });

    const respText = await resp.text();
    let hadError = false;

    if(!resp.ok) {
        // TODO : Replace alert window with something better
        hadError = true;
    }

    const out: ActionResponse = {
        error: hadError,
        message: respText
    };
    return out;
};