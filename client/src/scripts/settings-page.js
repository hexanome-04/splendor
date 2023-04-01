import { SETTINGS } from "./settings.js";
import { showError } from "./notify";
import { updateUserData, getUserDetail } from "./user-settings.js";

const setUpdatePassword = (name, container) => {
    const updateBtn = container.querySelector("button");
    updateBtn.addEventListener("click", () => {
        updateBtn.disabled = true;
        const oldPass = document.querySelector("body:not(.is-admin) #oldpassword");

        const newPassword = container.querySelector("#newpassword").value;
        console.log(`Requesting to change ${name}'s password`);
        updateUserData({
            endpoint: `/api/users/${name}/password`,
            bodyData: { oldPassword: oldPass ? oldPass.value : "", nextPassword: newPassword },
            method: "POST",
            loadMessage: `Updating password for ${name}`,
            successMessage: `Successfully updated password for ${name}!`
        }).then((resp) => { 
            if(resp) {
                container.querySelector("#oldpassword").value = "";
                container.querySelector("#newpassword").value = "";
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
                document.querySelector(".your-colour input[type='color']").value = `#${data.preferredColour}`;
            }
        }).finally(() => updateBtn.disabled = false);
    });
};

const setDeleteUser = (name, deleteBtn) => {
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
                SETTINGS.verifyCredentials().then(() => {});
            }
        }).finally(() => deleteBtn.disabled = false);
    });
};

const updateUserInfo = async () => {
    const data = await getUserDetail();
    if(data) {
        document.querySelector(".profile-pic").style.background = `#${data.preferredColour}`;
        document.querySelector(".your-colour input[type='color']").value = `#${data.preferredColour}`;

        const role = data.role.replace("ROLE_", "").toLowerCase();
        document.querySelector(".your-role").textContent = role.charAt(0).toUpperCase() + role.slice(1);

        const name = data.name;
        document.querySelector(".your-username").textContent = name;
        setUpdateColour(name, document.querySelector(".color-controls"));
        setUpdatePassword(name, document.querySelector(".your-pass-container"));
        setDeleteUser(name, document.querySelector(".delete-account-btn"));

        if(data.role === "ROLE_ADMIN") {
            document.querySelector("body").classList.add("is-admin");
        } else {
            document.querySelector("body").classList.remove("is-admin");
        }
    } else {
        showError("Issue occurred while retrieving user data.");
    }
};

document.addEventListener("DOMContentLoaded", () => {
    SETTINGS.verifyCredentials()
            .then(updateUserInfo)
            .catch((err) => showError(err));

});
