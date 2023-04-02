// Define settings to be used, that are shared across the frontend

/**
 * Check if the endpoint is alive.
 * 
 * @param {string} endpoint 
 * @returns true if valid endpoint
 */
const checkEndpoint = async (endpoint) => {
    const resp = await fetch(`${endpoint}/api/online`);
    return resp.ok;
};

/**
 * Internal settings that shouldn't be exposed.
 */
const INTERNAL_SETTINGS = {
    /**
     * Location for access to the game service.
     * Null if custom endpoint not set.
     */
    GS_API: "",
    DEFAULT_GS_PORT: 33402,

    /**
     * Location for access to the Lobby Service.
     * Null if custom endpoint not set.
     */
    LS_API: "",
    DEFAULT_LS_PORT: 34172,
};

/**
 * Define the settings.
 */

export const SETTINGS = {

    /**
     * Set the GS API Endpoint origin.
     * 
     * @param {string} gs 
     */
    setGS_API: (gs) => {
        try {
            checkEndpoint(gs).then(() => INTERNAL_SETTINGS.GS_API = gs);
        } catch(err) {
            window.alert("Could not set GS Endpoint: " + err);
        }
    },

    /**
     * Location for access to the Game Service.
     * 
     * @returns location origin of GS
     */
    getGS_API: () => {
        // if custom set, return that
        if(INTERNAL_SETTINGS.GS_API !== "") {
            return INTERNAL_SETTINGS.GS_API;
        }

        return `${window.location.protocol}//${window.location.hostname}:${INTERNAL_SETTINGS.DEFAULT_GS_PORT}`
    },

    /**
     * Set the LS API Endpoint origin.
     * 
     * @param {string} ls 
     */
    setLS_API: (ls) => {
        try {
            checkEndpoint(ls).then(() => INTERNAL_SETTINGS.LS_API = ls);
        } catch(err) {
            window.alert("Could not set LS Endpoint: " + err);
        }
    },

    /**
     * Location for access to the Lobby Service.
     * 
     * @returns location origin of LS
     */
    getLS_API: () => {
        // if custom set, return that
        if(INTERNAL_SETTINGS.LS_API !== "") {
            return INTERNAL_SETTINGS.LS_API;
        }

        return `${window.location.protocol}//${window.location.hostname}:${INTERNAL_SETTINGS.DEFAULT_LS_PORT}`
    },

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
        const url = new URL(`${SETTINGS.getLS_API()}/oauth/username`);
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
        const url = new URL(`${SETTINGS.getLS_API()}/oauth/token`);
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
        window.location.pathname = "/login/";
    },

    /**
     * Verify that the user is logged in.
     * Will attempt to refresh token if available.
     * Will boot user to login screen if not.
     */
    verifyCredentials: async () => {
        // no token
        try {
            const username = await SETTINGS.fetchUsername();
            if(!username && !(await SETTINGS.refreshAccessToken())) {
                SETTINGS.goToLogin();
                return;
            }
        } catch(err) {
            SETTINGS.goToLogin();
            return;
        }

        // now set username again
        const username = await SETTINGS.fetchUsername();
        SETTINGS.setUsername(username);
    },
};

/**
 * Maps the game version enum from the backend to the corresponding board.
 */
export const GAME_VERSION_TO_BOARD = {
    "BASE_ORIENT": "gameboard",
    "BASE_ORIENT_CITIES": "gameboard-cities",
    "BASE_ORIENT_TRADE_ROUTES": "gameboard-tradingposts"
};
