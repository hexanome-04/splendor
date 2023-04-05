# Splendor
An online multiplayer web-based implementation of the strategy game Splendor.

[![Frontend Tests](https://github.com/COMP361/f2022-hexanome-04/actions/workflows/playwright.yml/badge.svg?branch=master)](https://github.com/COMP361/f2022-hexanome-04/actions/workflows/playwright.yml)
![Build with Maven](https://github.com/COMP361/f2022-hexanome-04/actions/workflows/build-backend.yml/badge.svg?branch=master)

## Features
 * Splendor with Orient Expansion
 * Splendor with Orient Expansion + Trading Routes
 * Splendor with Orient Expansion + Cities
 * Custom Noble and Custom City
 * Online multiplayer experience
 * Game spectator feature
 * Exciting animations

## Getting Started

If you wish to only set up the game so that you may play it, see the instructions for setting up [here](#setup).

See [here](setup/readme.md) if you wish to develop the frontend/backend.

## Setup

### Prerequisites

Ensure that you have all the necessary dependencies installed:
  * [Docker](https://www.docker.com/)
  * Git

Ensure that the docker engine is up and running.

The LobbyService submodule must be initialized.
Execute the following commands in the root of the project.

```bash
git submodule init
git submodule update
```

From the root of the project, navigate to the `has-server-client` directory.
```bash
cd setup/has-server-client
```

Start up the services using docker compose.
```bash
docker compose up --build
```
Note: The “--build” argument is necessary to ensure that the docker containers are up to date.
Optionally, you may add the “-d” argument to launch all the services detached.

### Playing

Once all the services are up and running, you can then navigate to [localhost:36104](http://localhost:36104) to start playing.

## Gameplay

![splendor](https://user-images.githubusercontent.com/17598972/229967618-cb24d268-fe54-40e8-bd1d-9526f44069ca.gif)

Upon startup, you will be greeted by the Splendor title page. Simply click to log in.

You may now log in to one of the preconfigured Lobby Service accounts made by an administrative user.

This will now take you to the lobby screen. You can either start up a brand new game or load a previously saved game. All previously saved games can be loaded by any player and only require the same number of players to launch. The upper righthand corner features the settings and logout buttons.

All players have access to the settings page and will be able to change their colors and passwords here.

Admin accounts will also notice an admin zone button in the upper lefthand corner. The admin zone allows admin accounts to add, delete, or modify user accounts and force unregister game services.

Hovering over the "Create Session" button allows you to choose the game version to play. You will see the newly created game appear once you click on one of the three versions.

The game requires at least two players. Players can click on “Join” to join a game. Notice that only the creator has the permission to delete an unlaunched game.

The creator can launch the game once enough players have gathered.

Every player should click on "Play" to show the game board.

![taketurn](https://user-images.githubusercontent.com/17598972/229967691-fcfe0cb8-6931-40de-8de5-df8660833f57.gif)

During your turn, you may either take tokens according to game rules, purchase a card, or reserve a card. As the game progresses, additional actions may be unlocked and automatically added to your turn.

## Authors

 * [Alex Lai](https://github.com/sandpipes)
 * [Alexa Vasilakos](https://github.com/itsAlexa)
 * [Chen Jun Chi](https://github.com/MosinLover)
 * [Jia Lin Sun](https://github.com/Lobo808)
 * [Richard Rassokhine](https://github.com/richardrxn)
 * [Sarah Youinou](https://github.com/syouinou)

