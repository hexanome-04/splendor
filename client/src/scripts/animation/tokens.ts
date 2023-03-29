import anime from "animejs/lib/anime.es.js";
import { genRandomString, sizeAndPosElementToOther } from "./utils";

const createTempToken = (color: string) => {
    const t = document.createElement("div");
    t.classList.add(`${color.toLowerCase()}-token`, "board-token");
    return t;
};

const moveTokens = (count: number, tokenColor: string, startSelector: string,
                    endSelector: string, onFinish: () => {}): Promise<null> => {
    const targets = []
    const tokenClass = `.${tokenColor.toLowerCase()}-token`;

    for(let i = 0; i < count; i++) {
        const ranStr = genRandomString();
        const tokenT = createTempToken(tokenColor);
        const startElm = (document.querySelector(`${startSelector} ${tokenClass}`) as HTMLElement);
        sizeAndPosElementToOther(tokenT, startElm);

        tokenT.id = ranStr;
        document.querySelector("#anime-overlay").appendChild(tokenT);

        targets.push(`#anime-overlay #${ranStr}${tokenClass}`);
    }

    const endElm = document.querySelector(`${endSelector} ${tokenClass}`).getBoundingClientRect();
    console.log(`moving tokens from [${startSelector}] to [${endSelector}]`);
    return anime({
        targets: targets,
        top: endElm.top,
        left: endElm.left,
        width: endElm.width,
        height: endElm.height,
        delay: anime.stagger(80),
        loop: false,
        easing: "easeInOutQuart",
        complete: (anim) => {
            targets.forEach((sel) => document.querySelector(sel).remove());
            onFinish();
        }
    });;
};

export const animateMoveToken = (delta: number, tokenColor: string, inventorySelector: string,
                                 otherSelector: string, onFinish: () => {}): Promise<null> => {
    const isNegative = delta < 0;
    const startSel = isNegative ? inventorySelector : otherSelector;
    const endSel = isNegative ? otherSelector : inventorySelector;

    // if negative, remove the tokens first
    let finishCallback = onFinish;
    if(isNegative) {
        onFinish();
        finishCallback = () => "";
    }

    const count = isNegative ? delta * -1 : delta;
    return moveTokens(count, tokenColor, startSel, endSel, finishCallback);
};