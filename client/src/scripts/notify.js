import { toast } from "react-toastify";

export const startToastLoad = (msg) => {
    return toast.loading(msg, {
        position: "top-center",
        pauseOnHover: true,
        theme: "colored",
    });
};

export const updateToastLoad = (id, msg, type, timeout = 4000) => {
    toast.update(id, { render: msg, type: type, isLoading: false, autoClose: timeout });
};

export const showError = (msg, timeout = 4000) => {
    toast.error(msg, {
        position: "top-center",
        autoClose: timeout,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        theme: "colored",
    });
};
