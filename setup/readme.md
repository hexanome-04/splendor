# Running Splendor


## Install Docker

Go to [docker](https://www.docker.com/) and install docker desktop (will install docker compose with it).


## Initialize submodules

The [LobbyService](https://github.com/m5c/LobbyService) submodule must be initialized.

```bash
git submodule init
git submodule update
```

## Running a setup

### In general

Start with a terminal in the root of the folder (likely "f2022-hexanome-04")

```bash
cd setup/{setup name}
docker compose up --build
```

### List of setups

#### has-server-client
- The server needs to successfully build for this to work.
- The client needs to successfully build for this to work.
- Used for running the whole game all together.

#### no-client-server
- Used for testing/debugging the client & server.

#### no-client
- The server needs to successfully build for this to work.
- Used for testing/debugging the client.

#### no-server
- The client needs to successfully build for this to work.
- Used for testing/debugging the server.

## Connecting to the client (if it has)

A web server will have been created for the client, which you can access with this link: [http://localhost:36104](http://localhost:36104).

## Updates to the code

If you happen to change branches, update the code for the server and/or client, you must rebuild the docker images.
An easy way to do that is to add `--build` to the docker compose command.

```bash
docker compose up --build
```
