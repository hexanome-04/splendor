
function show() {
    document.querySelector(".navigation").classList.add("active");
}


function close() {
    document.querySelector(".navigation").classList.remove("active");
}


document.getElementById("menu-icon").addEventListener("click", show);

document.querySelector(".resume").parentNode.addEventListener("click", close);


// ------------- Temporary for Testing -----------------
const yourTurnModal = document.getElementById("your-turn-modal");
const purchaseCardModal = document.getElementById("buy-card-modal");
const confirmPurchaseModal = document.getElementById("dev-card-payment-modal");


const purchaseBtn = document.getElementById("purchase-btn");
const back1 = document.getElementById("buy-card-back-btn");
const back2 = document.getElementById("purchase-confirm-back-btn");
const confirm1 = document.getElementById("buy-card-confirm-btn");
const confirm2 = document.getElementById("purchase-confirm-btn");



purchaseBtn.onclick = function() {
    purchaseCardModal.style.display = "block";
};

back1.onclick = function() {
    yourTurnModal.style.display = "block";
};

back2.onclick = function() {
    purchaseCardModal.style.display = "block";
};

confirm1.onclick = function() {
    purchaseCardModal.style.display = "block";
};

confirm2.onclick = function() {
    confirmPurchaseModal.style.display = "block";
};

// --------------------------------------------------

// PURCHASE DEVELOPMENT CARD - Token Count Functions

var countRuby, countSapphire, countEmerald, countDiamond, countOnyx, countJoker;
countRuby = countSapphire = countEmerald = countDiamond = countOnyx = countJoker = 0;

// RUBY
document.getElementById("increase-ruby").onclick = function() {
    if (countRuby !== 10) {
        countRuby += 1;
        document.getElementById("ruby-count").innerHTML = countRuby;
    }
};

document.getElementById("decrease-ruby").onclick = function() {
    if (countRuby !== 0) {
        countRuby -= 1;
        document.getElementById("ruby-count").innerHTML = countRuby;
    }
};

// SAPPHIRE
document.getElementById("increase-sapphire").onclick = function() {
    if (countSapphire !== 10) {
        countSapphire += 1;
        document.getElementById("sapphire-count").innerHTML = countSapphire;
    }
};

document.getElementById("decrease-sapphire").onclick = function() {
    if (countSapphire !== 0) {
        countSapphire -= 1;
        document.getElementById("sapphire-count").innerHTML = countSapphire;
    }
};

// EMERALD
document.getElementById("increase-emerald").onclick = function() {
    if (countEmerald !== 10) {
        countEmerald += 1;
        document.getElementById("emerald-count").innerHTML = countEmerald;
    }
};

document.getElementById("decrease-emerald").onclick = function() {
    if (countEmerald !== 0) {
        countEmerald -= 1;
        document.getElementById("emerald-count").innerHTML = countEmerald;
    }
};

// DIAMOND
document.getElementById("increase-diamond").onclick = function() {
    if (countDiamond !== 10) {
        countDiamond += 1;
        document.getElementById("diamond-count").innerHTML = countDiamond;
    }
};

document.getElementById("decrease-diamond").onclick = function() {
    if (countDiamond !== 0) {
        countDiamond -= 1;
        document.getElementById("diamond-count").innerHTML = countDiamond;
    }
};

// ONYX
document.getElementById("increase-onyx").onclick = function() {
    if (countOnyx !== 10) {
        countOnyx += 1;
        document.getElementById("onyx-count").innerHTML = countOnyx;
    }
};

document.getElementById("decrease-onyx").onclick = function() {
    if (countOnyx !== 0) {
        countOnyx -= 1;
        document.getElementById("onyx-count").innerHTML = countOnyx;
    }
};

// JOKER
document.getElementById("increase-joker").onclick = function() {
    if (countJoker !== 10) {
        countJoker += 1;
        document.getElementById("joker-count").innerHTML = countJoker;
    }
};

document.getElementById("decrease-joker").onclick = function() {
    if (countJoker !== 0) {
        countJoker -= 1;
        document.getElementById("joker-count").innerHTML = countJoker;
    }
};

// Reserve cards selection
function setupSelectionCards(selector) {
    document.querySelectorAll(selector).forEach((elm) => {
        elm.onclick = () => {
            // get the current selected card (should have .selected-card as a class)
            const selectedCard = document.querySelector(`${selector}.selected-card`);
            // remove the selection
            if(selectedCard) selectedCard.classList.remove("selected-card");

            // only add to clicked element if the old selected was not the clicked element
            if(selectedCard !== elm) {
                elm.classList.add("selected-card");
            }
        };
    });
}

setupSelectionCards("#buy-card-modal .modal-board-cards .board-card-dev");
