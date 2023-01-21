# Running Splendor


## Install Docker

Go to [docker](https://www.docker.com/) and install docker desktop (will install docker compose with it).


## Initialize submodules

The [LobbyService](https://github.com/m5c/LobbyService) submodule must be initialized.

```bash
git submodule init
git submodule update
```

## Run

### Running with the server

**The server needs to successfully build for this to work.**

**This would be mainly for testing/debugging the client.**

Start with a terminal in the root of the folder (likely "f2022-hexanome-04")

```bash
cd setup/has-server
docker compose up
```


### Running without the server

**This would be mainly for testing/debugging the server.**

Start with a terminal in the root of the folder (likely "f2022-hexanome-04")

```bash
cd setup/no-server
docker compose up
```

## Connecting to the client

Regardless if you run with/without the server, a web server will have been created for the client, which you can access with this link: [http://localhost:36104/titleScreen.html](http://localhost:36104/titleScreen.html).

***If you cannot login when using the client, ensure that the client is pointing to the correct API endpoints.***

In `f2022-hexanome-04/client/js/settings.js`, check that the `GS_API` and `LS_API` is `http://localhost:53402` and `http://localhost:54172` respectively.

Then clear your browser's cache or open the debugging tools, go into network, disable the cache and reload the page.
