import { SETTINGS } from "./settings.js";
import { transition } from "./titleScreen.js";
import { showError } from "./notify";
import { updateUserData, getUserDetail } from "./user-settings.js";

/**
 * Retrieve all data from specified endpoint.
 * @param {string} endpoint
 * @returns {Promise<JSON>} list of data
 */
const getData = async (endpoint) => {
    const url = new URL(`${SETTINGS.getLS_API()}${endpoint}`);
    url.search = new URLSearchParams({ "access_token": SETTINGS.getAccessToken() }).toString();

    const resp = await fetch(url, { method: "GET" });
    if(!resp.ok) {
        // not admin
        showError("Not authorized to use this page.");
        transition("/lobby");
        return null;
    }
    return await resp.json();
};

const setUpdatePassword = (name, container) => {
    const updateBtn = container.querySelector("button");
    updateBtn.addEventListener("click", () => {
        updateBtn.disabled = true;
        const newPassword = container.querySelector("input").value;
        console.log(`Requesting to change ${name}'s password`);
        updateUserData({
            endpoint: `/api/users/${name}/password`,
            bodyData: { oldPassword: "", nextPassword: newPassword },
            method: "POST",
            loadMessage: `Updating password for ${name}`,
            successMessage: `Successfully updated password for ${name}!`
        }).then((resp) => { 
            if(resp) {
                container.querySelector("input[type='password']").value = "";
            }
        }).finally(() => updateBtn.disabled = false);
    });
};

const setUpdateColour = (name, container) => {
    const updateBtn = container.querySelector("button");
    updateBtn.addEventListener("click", () => {
        updateBtn.disabled = true;
        const newColor = (container.querySelector("input").value + "").substring(1).toUpperCase();
        console.log(`Requesting to change ${name}'s color to ${newColor}`);
        updateUserData({
            endpoint: `/api/users/${name}/colour`,
            bodyData: { colour: newColor.replace("#", "") },
            method: "POST",
            loadMessage: `Updating colour for ${name}...`,
            successMessage: `Successfully updated colour for ${name}!`
        }).then((success) => {
            if(success && name === SETTINGS.getUsername()) {
                document.querySelector(".profile-pic").style.background = `#${newColor}`;
            }
        }).finally(() => updateBtn.disabled = false);
    });
};

const setDeleteUser = (name, trNode, deleteBtn) => {
    deleteBtn.addEventListener("click", () => {
        deleteBtn.disabled = true;
        console.log(`Requesting to delete user ${name}`);
        updateUserData({
            endpoint: `/api/users/${name}`,
            method: "DELETE",
            loadMessage: `Deleting user ${name}...`,
            successMessage: `Successfully deleted ${name}!`
        }).then((resp) => { 
            if(resp) {
                deleteBtn.parentNode.closest("tr").remove();
            }
        }).finally(() => deleteBtn.disabled = false);
    });
};

const showAllRegisteredUsers = async () => {
    await SETTINGS.verifyCredentials(); // ensure logged in
    const userData = await getData("/api/users");

    const allUsersTable = document.querySelector(".registered-users-table > tbody");
    userData.forEach(user => {
        // only add if user isn't already there
        if(allUsersTable.querySelector(`tr[username="${user.name}"]`)) {
            return;
        }

        const role = user.role.replace("ROLE_", "").toLowerCase();

        const tNode = document.querySelector("#registered-users-template").content.cloneNode(true).querySelector("tr");
        // set everything
        tNode.setAttribute("username", user.name);
        tNode.querySelector(".reg-user-name").textContent = user.name;
        tNode.querySelector(".reg-user-colour input").setAttribute("value", `#${user.preferredColour}`);
        tNode.querySelector(".reg-user-type").textContent = role.charAt(0).toUpperCase() + role.slice(1);

        setUpdatePassword(user.name, tNode.querySelector(".reg-user-pass"));
        setUpdateColour(user.name, tNode.querySelector(".reg-user-colour"));
        setDeleteUser(user.name, tNode, tNode.querySelector(".user-delete-btn"));

        allUsersTable.appendChild(tNode);
    });

};

const showAllRegisteredServices = async () => {
    await SETTINGS.verifyCredentials(); // ensure logged in
    const allServices = await getData("/api/gameservices");

    try {
        const allServicesTable = document.querySelector(".registered-services > tbody");
        for(const gameservice of allServices) {
            const details = await getData(`/api/gameservices/${gameservice.name}`);

            // only add if service isn't already there
            if(allServicesTable.querySelector(`tr[service="${details.name}"]`)) {
                return;
            }

            const tNode = document.querySelector("#registered-service-template").content.cloneNode(true).querySelector("tr");
            // set everything
            const link = tNode.querySelector(".service-link > a");
            link.setAttribute("href", details.location);
            link.textContent = details.location;

            tNode.setAttribute("service", details.name);
            tNode.querySelector(".service-name").textContent = details.name;
            tNode.querySelector(".service-display-name").textContent = details.displayName;
            tNode.querySelector(".service-player-count").textContent = `[${details.minSessionPlayers}-${details.maxSessionPlayers}]`;

            const deleteBtn = tNode.querySelector(".user-delete-btn");
            deleteBtn.addEventListener("click", () => {
                deleteBtn.disabled = true;
                console.log(`Requesting to delete service ${details.name}`);
                updateUserData({
                    endpoint: `/api/gameservices/${details.name}`,
                    method: "DELETE",
                    loadMessage: `Deleting service ${details.name}...`,
                    successMessage: `Successfully deleted service ${details.name}!`
                }).then((resp) => { 
                    if(resp) {
                        deleteBtn.parentNode.closest("tr").remove();
                    }
                }).finally(() => deleteBtn.disabled = false);
            });

            allServicesTable.appendChild(tNode);
        }
    } catch(err) {
        showError(err);
    }

};

const setupAddUser = () => {
    const parent = document.querySelector(".new-user-form");

    const nameField = parent.querySelector("#name-field");
    const passwordField = parent.querySelector("#password-field");
    const typeField = parent.querySelector(".select-type");
    const colourField = parent.querySelector("#colour-field");

    const addBtn = parent.querySelector("#add-user");
    addBtn.addEventListener("click", () => {
        addBtn.disabled = true;
        const username = nameField.value;
        const password = passwordField.value;
        const type = "ROLE_" + typeField.value;
        const colour = colourField.value.replace("#", "").toUpperCase();

        if(!username || username === "" || !password || password === "") {
            showError("Missing required new user information!");
            addBtn.disabled = false;
            return;
        }

        console.log(`Requesting to add new user ${username} ${colour} ${type}`);
        updateUserData({
            bodyData: {
                name: username,
                password: password,
                preferredColour: colour,
                role: type
            },
            endpoint: `/api/users/${username}`,
            method: "PUT",
            loadMessage: `Creating new user ${username}...`,
            successMessage: `Successfully created new user ${username}!`
        }).then((resp) => {
            if(resp) {
                // reset to default
                nameField.value = "";
                passwordField.value = "";
                typeField.value = "PLAYER";
                colourField.value = "#000000";
                showAllRegisteredUsers().then(() => {});
            }
        }).finally(() => addBtn.disabled = false);
    });
};

document.addEventListener("DOMContentLoaded", () => {
    showAllRegisteredUsers().then(() => {});
    showAllRegisteredServices().then(() => {});
    setupAddUser();

    // set user color
    getUserDetail().then((data) => {
        if(data) {
            document.querySelector(".profile-pic").style.background = `#${data.preferredColour}`;
        }
    }).catch((err) => showError(err));
});
