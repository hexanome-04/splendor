import anime from "animejs/lib/anime.es.js";
import { genRandomString, sizeAndPosElementToOther } from "./utils";

export const moveCardFromDeck = (deckSelector: string, slotSelector: string) => {
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

    // console.log(`Begin: ${startElm.left} - End: ${endBB.left}`);

    document.querySelector("#anime-overlay").appendChild(tNode);

    // console.log(`target: #anime-overlay #${randomStr} .flip-card-inner`);
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
            // console.log("complete move card");
            tNode.remove();
            endElm.classList.remove("invisible");
        }
    });
};

export const moveCard = (startElm: HTMLElement, endElm: HTMLElement,
                         imgPath: string) => {
    const tNode: HTMLDivElement = ((document.querySelector("#card-to-slot") as HTMLTemplateElement)
                  .content.cloneNode(true) as HTMLDivElement).querySelector(".cts-container");

    // set image
    tNode.querySelector("img").setAttribute("src", `/images/${imgPath}`);

    sizeAndPosElementToOther(tNode, startElm);

    const randomStr = genRandomString();
    tNode.id = randomStr;

    const endBB = endElm.getBoundingClientRect();

    document.querySelector("#anime-overlay").appendChild(tNode);
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
            // console.log("complete move card");
            tNode.remove();
            const invElm = endElm.querySelector(".invisible");
            if(invElm) {
                invElm.classList.remove("invisible");
            }
        }
    });
};

export const moveCardReserved = (startElm: HTMLElement, endElm: HTMLElement,
                                 imgPath: string) => {
    const tNode: HTMLDivElement = ((document.querySelector("#card-to-slot-rotate") as HTMLTemplateElement)
    .content.cloneNode(true) as HTMLDivElement).querySelector(".cts-container-rotate");

    // set image
    tNode.querySelector("img").setAttribute("src", `/images/${imgPath}`);

    sizeAndPosElementToOther(tNode, startElm);

    const randomStr = genRandomString();
    tNode.id = randomStr;

    const endBB = endElm.getBoundingClientRect();

    document.querySelector("#anime-overlay").appendChild(tNode);
    anime({
        targets: `#anime-overlay #${randomStr}`,
        top: endBB.top,
        left: endBB.left,
        width: endBB.height,
        height: endBB.width,
        rotate: "90",
        translateY: "-100%",
        duration: 1500,
        delay: () => 400,
        loop: false,
        easing: "easeInOutQuart",
        complete: (anim) => {
            // console.log("complete move card");
            tNode.remove();
            const invElm = endElm.querySelector(".invisible");
            if(invElm) {
                invElm.classList.remove("invisible");
            }
        }
    });
};
