
document.querySelector("body").onload = () => {

    var sessionTabBtn = document.querySelector("#select-sessions");
    var loadgameTabBtn = document.querySelector("#select-loadgame");
    var mainElm = document.querySelector("main");
    var loadGameBtn = document.querySelector(".loadjoin-btn");
    var logOutBtn = document.querySelector(".logout-btn");

    function changeTab(selectedElm, otherElm) {
        if(otherElm.classList.contains("selected")) {
            otherElm.classList.remove("selected");
        }

        if(!selectedElm.classList.contains("selected")) {
            selectedElm.classList.add("selected");
        }
    }

    sessionTabBtn.onclick = () => {
        changeTab(sessionTabBtn, loadgameTabBtn);
        mainElm.setAttribute("selected", "sessions");
    };

    loadgameTabBtn.onclick = () => {
        changeTab(loadgameTabBtn, sessionTabBtn);
        mainElm.setAttribute("selected", "loadgame");
    };

    loadGameBtn.onclick = () => {
        window.location.href = "gameboard.html";
    };

    logOutBtn.onclick = () => {
        window.location.href = "titleScreen.html";
    };
};