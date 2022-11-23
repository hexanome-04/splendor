
function show() {
    document.querySelector(".navigation").classList.add("active");
}


function close() {
    document.querySelector(".navigation").classList.remove("active");
}


document.getElementById("menu-icon").addEventListener("click", show);

document.querySelector(".resume").parentNode.addEventListener("click", close);



