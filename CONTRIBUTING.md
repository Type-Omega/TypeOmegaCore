# Contributing to TypeOmegaCore

TypeOmegaCore holds only what is unique to the Type Omega modpack. Reusable machines and mechanics belong in [GTNA](https://github.com/Type-Omega/GregTech-Nexus-Addon). Pack-level issues (quests, progression, configs) go to the [modpack repository](https://github.com/Type-Omega/TypeOmega).

## Before a pull request

```sh
./gradlew spotlessApply
./gradlew runData            # regenerate lang, models, tags
./gradlew runGameTestServer  # all GameTests must pass
./gradlew assemble
python3 tools/qa_static.py
```

- Machines and recipes are Java. Put tests in `src/main/java/com/typeomega/core/gametest` (`runGameTestServer` uses `sourceSets.main`).
- Translations: English first (`en_us`), then `pt_br`.
- **Art:** don't replace existing textures. Propose new ones in an issue.
- **Third-party content:** anything taken or adapted from another project needs its license checked and an entry in [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md) in the same PR. If the source mod asks for it, also credit it in the tooltip.

## Reporting bugs

Report bugs on the [modpack repository](https://github.com/Type-Omega/TypeOmega/issues) with the pack version. Use this repository only for problems that are clearly in the mod (crashes in `com.typeomega.core`, missing textures, broken machines).
