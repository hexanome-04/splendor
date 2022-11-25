// Define settings to be used, that are shared across the frontend

/**
 * Define the settings.
 */

export const SETTINGS = {
    /**
     * URL to access the Lobby Service
     */
    LS_API: "http://ls.hoshi.tv:54172",

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
    }
};
