
// prefix with alphabetical characters otherwise invalid id
export const genRandomString = (): string => "ran-" + Math.random().toString(36).substring(2, 10);

/**
 * Resizes and positions the first element with respect to the second element.
 * 
 * @param elmSel1 selector for element to resize and position
 * @param elmSel2 selector for element to base size and pos
 */
export const sizeAndPosElementToOther = (elm1: HTMLElement, elm2: HTMLElement) => {
    const elm2BB = elm2.getBoundingClientRect();

    elm1.style.width = elm2BB.width + "px";
    elm1.style.height = elm2BB.height + "px";
    elm1.style.top = elm2BB.top + "px";
    elm1.style.left = elm2BB.left + "px";
};
