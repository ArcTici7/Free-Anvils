# FreeAnvil

Paper plugin (Minecraft 26.3) that makes all anvil actions free and removes the "Too Expensive!" limit.
The anvil block still wears down and breaks like vanilla.

## Build with GitHub Actions

1. Push this repo to GitHub.
2. Open the **Actions** tab. The **Build** workflow runs on every push (or run it manually).
3. Open the finished run and download the **FreeAnvil** artifact. It contains the plugin jar.
4. Put the jar in your server's `plugins/` folder and restart.

To publish a release with the jar attached, push a tag such as `v1.0.0`.

## Build locally

Requires JDK 25 and Gradle 9.1 or newer:

    gradle build

The jar is written to `build/libs/`.
