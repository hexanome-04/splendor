// Define settings to be used, that are shared across the frontend

/**
 * Define the settings.
 */

export const SETTINGS = {

    /**
     * Location for access to the game service.
     */
    GS_API: "http://localhost:33402",

    /**
     * Location for access to the Lobby Service.
     */
    LS_API: "http://localhost:34172",

    /**
     * Get the access token stored in local storage
     * @returns string | null
     */
    getAccessToken: () => {
        // can add check here to ensure that there's an access token
        // if not, can boot client to login screen if expired or just not theree

        return localStorage.getItem("accessToken");
    },

    /**
     * Store the access token in local storage
     * @param {string} token 
     */
    setAccessToken: (token) => {
        localStorage.setItem("accessToken", token);
    },

    /**
     * Get the refresh token stored in local storage
     * @returns string | null
     */
    getRefreshToken: () => {
        return localStorage.getItem("refreshToken");
    },

    /**
     * Store the refresh token in local storage
     * @param {string} token 
     */
    setRefreshToken: (token) => {
        localStorage.setItem("refreshToken", token);
    },

    /**
     * Get the username stored in local storage
     * @returns string | null
     */
    getUsername: () => {
        return localStorage.getItem("username");
    },
    
    /**
     * Store the username in local storage
     * @param {string} setUsername 
     */
    setUsername: (username) => {
        localStorage.setItem("username", username);
    },

    /**
     * Get the username of the currently stored token.
     * @returns string | null
     */
    fetchUsername: async () => {
        // build url
        const url = new URL(`${SETTINGS.LS_API}/oauth/username`);
        url.search = new URLSearchParams({ "access_token": SETTINGS.getAccessToken() }).toString();

        const resp = await fetch(url, { method: "GET" });

        if(!resp.ok) {
            return null;
        }

        const rTest = await resp.text();

        return rTest;
    },

    /**
     * Attempt to refresh access token with refresh token.
     * @returns string | null - returns access token
     */
    refreshAccessToken: async () => {
        const rt = SETTINGS.getRefreshToken();

        // nothing stored
        if(!rt) {
            return null;
        }

        const params = {
            "grant_type": "refresh_token",
            "refresh_token": rt
        };
        const url = new URL(`${SETTINGS.LS_API}/oauth/token`);
        url.search = new URLSearchParams(params).toString();

        const headers = new Headers();
        headers.set("Authorization", `Basic ${btoa("bgp-client-name:bgp-client-pw")}`);

        var newAT = "";
        try {
            const resp = await fetch(url, { method: "POST", headers: headers });

            if(!resp.ok) throw new Error("not ok: " + resp.statusText);

            const data = await resp.json();

            SETTINGS.setAccessToken(data.access_token);
            SETTINGS.setRefreshToken(data.refresh_token);
            newAT = data.access_token;

        } catch(e) {
            return null;
        }

        return newAT;
    },

    /**
     * Force client to go to login screen.
     */
    goToLogin: () => {
        window.location = "login.html";
    },

    /**
     * Verify that the user is logged in.
     * Will attempt to refresh token if available.
     * Will boot user to login screen if not.
     */
    verifyCredentials: async () => {
        // no token
        var username = await SETTINGS.fetchUsername();
        if(!username && !(await SETTINGS.refreshAccessToken())) {
            SETTINGS.goToLogin();
            return;
        }

        // now set username again
        username = await SETTINGS.fetchUsername();
        SETTINGS.setUsername(username);
    },
};
