# Third-party content in TypeOmegaCore

TypeOmegaCore's own code is licensed under **LGPL-3.0** ([LICENSE.MD](LICENSE.MD)). Third-party material keeps its original license, listed below; the LGPL-3.0 of TypeOmegaCore's code does not relicense it.

> **Non-commercial notice:** TypeOmegaCore includes assets licensed under **CC BY-NC-SA 4.0**, so the combined distribution may not be used commercially.

If you are a rightsholder and want an entry corrected or removed, please open an issue.

| Project | License | What TypeOmegaCore uses | Status |
| --- | --- | --- | --- |
| [GregTech CEu Modern addon template](https://github.com/GregTechCEu/GregTech-Addon-Template) | LGPL-3.0 | Project skeleton and build setup | Attribution |
| [GregTech CEu Modern](https://github.com/GregTechCEu/GregTech-Modern) | LGPL-3.0 | API (materials, machines, recipes) | Dependency |
| [GregTech Nexus Addon (GTNA)](https://github.com/Type-Omega/GregTech-Nexus-Addon) | LGPL-3.0 (same author) | Compound material forms (`TypeOmegaExtraForms`, `TypeOmegaExtraFormRecipes`) adapted from GTNA | Same author |
| [cmme-additions](https://github.com/CristalGaming/cmme-additions) → [Modernity-GTNH](https://github.com/ModernityGTNH/Modernity-GTNH), via GTNA | CC BY-NC-SA 4.0 | 111 compound-form textures (double/triple/quadruple/quintuple ingots, multi plates…) under `assets/gtceu/textures/item/material_sets/`, identical to GTNA's | **Permission granted** by the Modernity-GTNH team (2026-10-10), with credit |
| [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) art | CC BY-NC-SA 3.0 (AE2 art assets) | Tinted placeholder copies of the AE2 interface and Acceleration Card textures (ME Interface II-V, Acceleration Card II-V), made by `tools/ae_tier_textures.py`; to be redrawn | Placeholder, derived work |
| [ExtendedAE Plus](https://github.com/GaLicn/ExtendedAE_Plus) (GaLicn and contributors) | LGPL-3.0 | The idea and flow of its automatic pattern upload by recipe type (the client notes the recipe at the viewer's "+", reports it before Encode, the server uploads after `encode()` into the one provider whose name fits, a pattern the provider already holds is not added again), plus the provider-name rule. Reimplemented for AE2 with no dependency on the mod, in `integration/ae/upload` and `mixin/ae/PatternEncodingTermMenuMixin`; no file is copied verbatim | Adapted, credited in the file headers |
| [ExtendedAE](https://github.com/GlodBlock/ExtendedAE) (GlodBlock and contributors) | LGPL-3.0 | The proportions, seated silhouette and four-direction blockstate of its `fishbig` plush, adapted for the Raishxn and Nyra plushies (`models/block/plush_*.json`, `blockstates/plush_*.json`); the textures are cut from the authors' own skins | Adapted, credited in the model files |

Everything else under `assets/typeomega/` and `assets/gtceu/textures/item/material_sets/` (Phase Matrix, phase sets, machine overlays, items) is original Type Omega art, licensed **CC BY-NC-SA 4.0**.
