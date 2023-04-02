import anime from "animejs/lib/anime.es.js";
import { genRandomString, sizeAndPosElementToOther } from "./utils";

const finishAnime = (targetNode: Element, endElm: Element) => {
    targetNode.remove();
    const invElm = endElm.querySelector(".invisible");
    if(invElm) {
        invElm.classList.remove("invisible");
    } else if(endElm.classList.contains("invisible")) {
        endElm.classList.remove("invisible");
    }
};

export const moveCardFromDeck = (deckSelector: string, slotSelector: string, delayed: boolean = false) => {
    const tNode = ((document.querySelector("#deck-to-slot") as HTMLTemplateElement)
                  .content.cloneNode(true) as HTMLDivElement).querySelector(".flip-card");
    const container: HTMLElement = tNode.querySelector(".flip-card-inner");

    const endElm = document.querySelector(`${slotSelector} img`);
    const startElm = document.querySelector(`#board ${deckSelector} > img`) as HTMLElement;

    // set images
    const startImg = startElm.getAttribute("src");
    const endImg = endElm.getAttribute("src");
    tNode.querySelector(".flip-card-front img").setAttribute("src", startImg);
    tNode.querySelector(".flip-card-back img").setAttribute("src", endImg);

    sizeAndPosElementToOther(container, startElm);

    const randomStr = genRandomString();
    tNode.id = randomStr;

    const endBB = endElm.getBoundingClientRect();

    const execAnime = () => {
        document.querySelector("#anime-overlay").appendChild(tNode);
        anime({
            targets: `#anime-overlay #${randomStr}.flip-card .flip-card-inner`,
            top: endBB.top,
            left: endBB.left,
            width: endBB.width,
            height: endBB.height,
            duration: 1500,
            delay: () => 400,
            loop: false,
            easing: "easeInOutQuart",
            begin: (anim) => {
                container.classList.add("flip");
            },
            complete: (anim) => {
                correctEndPosition(`#anime-overlay #${randomStr}.flip-card .flip-card-inner`, tNode, endElm);
            }
        });
    };

    if(delayed) {
        setTimeout(() => {
            execAnime();
        }, 50);
    } else {
        execAnime();
    }
};

export const moveCard = (startElm: HTMLElement, endElm: HTMLElement,
                         imgPath: string, delayed: boolean = false) => {
    const tNode: HTMLDivElement = ((document.querySelector("#card-to-slot") as HTMLTemplateElement)
                  .content.cloneNode(true) as HTMLDivElement).querySelector(".cts-container");

    // set image
    tNode.querySelector("img").setAttribute("src", `/images/${imgPath}`);

    sizeAndPosElementToOther(tNode, startElm);

    const randomStr = genRandomString();
    tNode.id = randomStr;

    const endBB = endElm.getBoundingClientRect();
    document.querySelector("#anime-overlay").appendChild(tNode);

    const execAnime = () => {
        anime({
            targets: `#anime-overlay #${randomStr}`,
            top: endBB.top,
            left: endBB.left,
            width: endBB.width,
            height: endBB.height,
            duration: 1500,
            delay: () => 400,
            loop: false,
            easing: "easeInOutQuart",
            complete: (anim) => {
                correctEndPosition(`#anime-overlay #${randomStr}`, tNode, endElm);
            }
        });
    };

    if(delayed) {
        setTimeout(() => {
            execAnime();
        }, 50);
    } else {
        execAnime();
    }

};

export const moveCardReserved = (startElm: HTMLElement, endElm: HTMLElement,
                                 imgPath: string, delayed: boolean = false) => {
    const tNode: HTMLDivElement = ((document.querySelector("#card-to-slot-rotate") as HTMLTemplateElement)
    .content.cloneNode(true) as HTMLDivElement).querySelector(".cts-container-rotate");

    // set image
    tNode.querySelector("img").setAttribute("src", `/images/${imgPath}`);

    sizeAndPosElementToOther(tNode, startElm);

    const randomStr = genRandomString();
    tNode.id = randomStr;

    document.querySelector("#anime-overlay").appendChild(tNode);

    const execAnime = () => {
        const endBB = endElm.getBoundingClientRect();
        anime({
            targets: `#anime-overlay #${randomStr}`,
            top: endBB.top,
            left: endBB.left,
            width: endBB.height,
            height: endBB.width,
            rotate: "90",
            translateY: "-100%",
            duration: 1500,
            loop: false,
            easing: "easeInOutQuart",
            complete: (anim) => {
                correctEndPosition(`#anime-overlay #${randomStr}`, tNode, endElm);
            }
        });
    };
    
    if(delayed) {
        setTimeout(() => {
            execAnime();
        }, 50);
    } else {
        execAnime();
    }
};

const correctEndPosition = (target: string, targetNode: Element, endElm: Element, reserved: boolean = false) => {
    const endBB = endElm.getBoundingClientRect();
    const tarBB = document.querySelector(target).getBoundingClientRect();
    if(tarBB.top !== endBB.top || tarBB.left !== endBB.left) {

        // animate again to correct position
        const animeOptions = {
            targets: target,
            top: endBB.top,
            left: endBB.left,
            duration: 200,
            loop: false,
            easing: "easeInOutQuart",
            complete: (anim) => {
                // console.log("complete move card");
                finishAnime(targetNode, endElm);
            }
        };
        if(reserved) {
            animeOptions["rotate"] = "90";
            animeOptions["translateY"] = "-100%";
        }
    
        anime(animeOptions);
    } else {
        finishAnime(targetNode, endElm);
    }
};
