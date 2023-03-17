
export enum CostType {
    Token = "Token",
    Bonus = "Bonus"
}

export enum TokenType {
    Green = "Green",
    White = "White",
    Blue = "Blue",
    Brown = "Brown",
    Red = "Red",
    Gold = "Gold",
    Satchel = "Satchel"
}
export const allTokens = [ // amazing
    TokenType.Green, TokenType.White, TokenType.Blue, TokenType.Brown,
    TokenType.Red, TokenType.Gold, TokenType.Satchel
];

export enum CascadeType {
    Tier1 = "Tier1", Tier2 = "Tier2", None = "None"
}

export enum Actions {
    BUY_CARD = "BUY_CARD",
    TAKE_TOKEN = "TAKE_TOKEN",
    RESERVE_CARD = "RESERVE_CARD",
    CHOOSE_NOBLE = "CHOOSE_NOBLE",

    // Orient
    CASCADE_1 = "CASCADE_1",
    CASCADE_2 = "CASCADE_2",
    RESERVE_NOBLE = "RESERVE_NOBLE",
    CHOOSE_SATCHEL_TOKEN = "CHOOSE_SATCHEL_TOKEN",

    // Trading 
    TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER = "TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER"
}

class Player {
    name: string;
    colour: string;
    prestigePoints: number;
    tokens: TokensCount;
    bonuses: TokensCount;
    devCards: Array<DevCard>;
    reservedCards: Array<DevCard>;
    nobleCards: Array<Card>;
    reservedNobles: Array<Card>;

    constructor(name: string) {
        this.name = name;
        this.colour = "";
        this.prestigePoints = 0;

        this.tokens = new TokensCount();
        this.bonuses = new TokensCount();

        this.devCards = [];
        this.reservedCards = [];
        this.nobleCards = [];
        this.reservedNobles = [];
    }
}

export class TokensCount extends Map<TokenType, number> {
    constructor() {
        super();
        this.reset();
    }

    reset(): this {
        for(const tt of allTokens) {
            this[tt] = 0;
        }
        return this;
    }

    // Do not use 'set' in this class (does not work idk why)
    setCount(tokenType: TokenType, count: number): this {
        this[tokenType] = count;
        return this;
    }
}

class Card {
    id: string;
    costType: CostType;
    tokenCost: TokensCount;

    constructor(id: string) {
        this.id = id;
        this.costType = CostType.Token;
        this.tokenCost = new TokensCount();
    }

    setId(id: string): this {
        this.id = id;
        return this;
    }

    setCostType(costType: CostType): this {
        this.costType = costType;
        return this;
    }
}

export class NobleCard extends Card {}

export class DevCard extends Card {
    tokenType: TokenType;
    bonus: number;

    constructor(id: string, tokenType: TokenType, bonusAmount: number = 1) {
        super(id);
        this.tokenType = tokenType;
        this.bonus = bonusAmount;
    }
}

export class OrientDevCard extends DevCard {
    reserveNoble: boolean;
    cascadeType: CascadeType;

    constructor(id: string, tokenType: TokenType, bonusAmount: number = 1) {
        super(id, tokenType, bonusAmount);
        this.reserveNoble = false;
        this.cascadeType = CascadeType.None;
    }
}

class Deck<T extends Card> {
    visibleCards: Array<T>;
    canDraw: boolean;
    limit: number;

    constructor(limit: number) {
        this.limit = limit;
        this.visibleCards = [];
        this.canDraw = false;
    }

    setCanDraw(canDraw: boolean): this {
        this.canDraw = canDraw;
        return this;
    }

    addOrReplace(card: T): this {
        if(this.visibleCards.length >= this.limit) {
            this.visibleCards[0] = card;
        } else {
            this.visibleCards.push(card);
        }
        return this;
    }

    getOne(): T {
        return this.visibleCards[0];
    }

    reset(): this {
        this.visibleCards = [];
        return this;
    }
}

export class Game {
    nobleDeck: Deck<Card>;
    tier1OrientDeck: Deck<OrientDevCard>;
    tier2OrientDeck: Deck<OrientDevCard>;
    tier3OrientDeck: Deck<OrientDevCard>;

    players: Array<Player>
    prestigePointsToWin: number;
    turnCounter: number;
    curValidActions: Array<Actions>;
    tier1Deck: Deck<DevCard>;
    tier2Deck: Deck<DevCard>;
    tier3Deck: Deck<DevCard>;
    tokens: TokensCount;

    constructor() {
        this.nobleDeck = new Deck<Card>(3);
        this.tier1OrientDeck = new Deck<OrientDevCard>(2);
        this.tier2OrientDeck = new Deck<OrientDevCard>(2);
        this.tier3OrientDeck = new Deck<OrientDevCard>(2);

        this.players = [];
        this.prestigePointsToWin = 15;
        this.turnCounter = 0;
        this.curValidActions = [];

        this.tier1Deck = new Deck<DevCard>(4);
        this.tier2Deck = new Deck<DevCard>(4);
        this.tier3Deck = new Deck<DevCard>(4);

        this.tokens = new TokensCount();
    }

    addPlayer(...names: string[]): this {
        for(let name of names) {
            this.players.push(new Player(name));
        }
        this.nobleDeck.limit = this.players.length + 1;
        return this;
    }

    removePlayer(name: string = ""): this {
        if(name === "") {
            if(this.players.length > 0) {
                this.players.pop();
            }
        } else {
            this.players = this.players.filter((p) => p.name === name);
        }
        return this;
    }

    getPlayer(name: string): Player {
        for(let p of this.players) {
            if(p.name === name) {
                return p;
            }
        }
        throw new ReferenceError("Player does not exist");
    }

    setTurnCounter(turn: number): this {
        this.turnCounter = turn;
        return this;
    }

    resetValidActions(): this {
        this.curValidActions = [];
        return this;
    }

    addAction(...action: Actions[]): this {
        this.curValidActions = [...this.curValidActions, ...action];
        return this;
    }

    addDefaultActions(): this {
        this.addAction(Actions.BUY_CARD, Actions.TAKE_TOKEN, Actions.RESERVE_CARD);
        return this;
    }
};
