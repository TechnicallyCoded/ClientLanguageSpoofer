# Client Language Spoofer

Client-only Fabric mod for Minecraft 26.3, Fabric Loader 0.19.5 or later, and Java 25. Fabric API is not required.

## Install

Copy `ClientLanguageSpoofer-1.0.2.jar` into the Minecraft instance’s `mods` directory.

## Usage

- Multiplayer → select a server → Edit: use **Server Language: Default** to open the vanilla searchable language list. Choose a language and press Done, then save the server with Done. Cancel leaves the saved override unchanged.
- While connected: Options → Language has the server-language button at the top right. Selecting a language applies immediately and sends a client information update to the server.
- When an override exists, **Reset** appears directly to the right. Reset uses Minecraft’s current interface language again; in-game reset also sends an update immediately.

Selections affect only the language reported to that server. Minecraft’s interface and resource-pack language stay unchanged. The selected language is also reported during the next connection. Servers can choose how to interpret that information; this mod does not translate messages itself.

Overrides are saved in `config/client-language-spoofer.json`, keyed by the entered server hostname/address and port. Default entries are omitted. Different hostnames for the same physical server are independent; hostname case and the default port are normalized. Singleplayer uses the normal client language.

The labels are supplied in English, French, Spanish, German, Brazilian Portuguese, Russian, Simplified Chinese, Japanese, Korean, and Italian; language names come from Minecraft’s own language catalogue.

## Build and validation

```sh
./gradlew build
```

The distributable is `build/libs/ClientLanguageSpoofer-1.0.2.jar`.

Validated: compilation against Minecraft 26.3, access widener validation, five tests covering persistence, reset, address normalization, malformed files and failed saves, and development-client startup. Manual screen interaction and a live multiplayer packet capture remain to be checked; the test machine was locked during visual verification.
