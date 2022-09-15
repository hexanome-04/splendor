# COMP 361 Project

 > See also [my video instuctions](https://www.cs.mcgill.ca/~mschie3/COMP361/Repository-Best-Practices.mp4) in the screencasts section on MyCourses.

## The Rules

 * Feel free to edit/replace this file.
 * Do not delete or rename the [reports](reports), [client](client), [server](server) or [docs](docs) directories.  
See [Static Content](#static-content)
 * Don't clutter your repo, update your [```.gitignore```](.gitignore) file, depending on your client language / technology.
    * Don't commit binaries. (Images, jar files, class files, etc...)
    * Don't commit buffer files. (Vim buffer files, IDE meta files etc...)
 * Place your documentation in [```docs```](docs) on [master](branch).
 * Commit frequently, commit fine grained.
 * Use branches
 * **Don't push on master!**
    * Create a new branch for your feature.
    * Work until stable / tested.
    * Merge / rebase your temporary branch back to master.
    * Delete your temporary branch.

## Static content

 * [```.gitignore```](.gitignore): Preliminary git exclusion instructions. Visit [Toptal's generator](https://www.toptal.com/developers/gitignore) to update.
 * [```reports```](reports): Base directory for automatic report collection. Your weekly reports go here. Must be uploaded every monday noon **to master** and follow correct date string ```YYYY-MM-DD.md```. Use [template](reports/YYYY-MM-DD.md) for your own reports. Do not resubmit same report / copy paste.
 * [```docs```](docs): source directory for your [enabled GitHub Pages](https://comp361.github.io/f2022-hexanome-00/). (Update number in link). Configure IDE to generate API docs into this directory or build your own webpage (optional).
 *  [```client```](client): Place your client sources into this directory. Do not use a separate repository for your work.
 * [```server```](server): Place your Spring Boot Game Server sources in this directory. Do not use a separate repository for your work.

## Useful Links

### Code Style and Tools

 * [Chrome MarkDown Plugin](https://chrome.google.com/webstore/detail/markdown-viewer/ckkdlimhmcjmikdlpkmbgfkaikojcbjk?hl=en).
    * Don't forget to enable ```file://``` in ```advanced settings```.
 * [IntelliJ Checkstyle Plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea).
    * Don't forget to enable [Google's Checkstyle Configuration](https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml).
 * [Git CheatSheet](git-cheatsheet.md).
 * [Advanced Rest Client (Rest Call Code Generator)](https://docs.advancedrestclient.com/installation).

### Requirements

 * [Lobby Service](https://github.com/kartoffelquadrat/LobbyService)
    * [Install Guide](https://github.com/kartoffelquadrat/LobbyService/blob/master/markdown/build-deploy.md)  
Recommended: Startup in ```dev``` profile (default).
    * [API Doc and ARC Configurations](https://github.com/kartoffelquadrat/LobbyService/blob/master/markdown/api.md)
    * [Game Developer Instructions](https://github.com/kartoffelquadrat/LobbyService/blob/master/markdown/game-dev.md)
 * [BGP sample deployment configuration](https://github.com/kartoffelquadrat/BoardGamePlatform) (This one is meant for testing / understanding the interaction between LS, UI and sample game)  
Board Game Platform (BGP) = Lobby Service + Lobby Service Web UI + Sample Game, all as docker containers.
    * Sample [Lobby Service Web UI](https://github.com/kartoffelquadrat/LobbyServiceWebInterface)
    * Sample Lobby Service compatible [Game (Tic Tac Toe, backend + frontend)](https://github.com/kartoffelquadrat/BgpXox)

 > Be careful not to confuse *Lobby Service* and *Board Game Platform*.

## Authors

Fill e.g. names + link to github profiles in list below.

 * ~~Maximilian Schiedermeier - [https://github.com/kartoffelquadrat]~~
 * ...


