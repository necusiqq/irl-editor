# Flashback Lighting Addon (Minecraft 26.3)

A client-side addon that registers an **IRL Light** keyframe type in Flashback and drives IRL Redactor's placed-light scene while the replay timeline is evaluated.

## Requirements

- Minecraft 26.3
- Fabric Loader 0.19.5 or newer
- Fabric API for Minecraft 26.3
- Flashback 0.43.6
- IRL Redactor for Minecraft 26.3 (and its required `irl-core` dependency)
- Java 25

The addon does not include or redistribute Flashback or IRL Redactor.

## Install

1. Install the matching Fabric versions of all requirements above.
2. Put the built `flashback-lighting-addon-*.jar` in the Minecraft `mods` folder.
3. Launch the game and open a Flashback replay in the editor.
4. Add a timeline element named **Light**. The addon creates a separate track for it.
5. Add keyframes to that track and edit the selected keyframe's position, intensity, radius, direction, RGB color, and Point/Spot settings.

For Spot lights, direction controls where the cone points; range and cone angles control its reach and spread.

## Build

From this directory, run:

```sh
gradle clean build
```

The output JAR is in `build/libs/`. CI builds it with Java 25.

## Current status / limitations

- The project has passed CI compilation. It has **not yet been verified in a live Minecraft client**.
- Runtime lighting requires IRL Redactor to be installed and working.
- Light tracks are reconciled against the current evaluated keyframes so seeking before a track's first keyframe or removing a track should remove its addon-owned light.
- The addon removes its lights when the replay/world closes.
- Export/render pipeline integration and round-trip testing of saved replay projects still need in-game validation.
