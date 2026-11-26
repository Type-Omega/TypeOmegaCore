"""Small, dependency-free checks for resources shipped with the TypeOmegaCore mod."""

import json
import struct
import sys
import tomllib
from zipfile import ZipFile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/typeomega"
MACHINE_TEXTURES = ASSETS / "textures/block/machines"
REQUIRED_MATERIALS = {
    "material.typeomega.pulsating_alloy",
    "material.typeomega.conductive_alloy",
    "material.typeomega.phase_matrix",
    "typeomega:phase_matrix_deposit",
}


def check_languages() -> None:
    languages = {}
    for code in ("en_us", "pt_br"):
        path = (ROOT / "src/generated/resources/assets/typeomega/lang/en_us.json"
                if code == "en_us" else ASSETS / "lang/pt_br.json")
        data = json.loads(path.read_text(encoding="utf-8"))
        if not isinstance(data, dict) or any(not isinstance(value, str) or not value for value in data.values()):
            raise ValueError(f"Invalid translation values: {path}")
        missing = REQUIRED_MATERIALS - data.keys()
        if missing:
            raise ValueError(f"Missing translations in {code}: {sorted(missing)}")
        languages[code] = set(data)
    if languages["en_us"] != languages["pt_br"]:
        raise ValueError("en_us and pt_br have different translation keys")


def check_overlays() -> None:
    """Every machine overlay is a 16x16 RGBA PNG (the Catalytic Dehydrator set this rule; it left in D-336)."""
    for path in MACHINE_TEXTURES.glob("*/overlay_*.png"):
        data = path.read_bytes()
        if data[:8] != b"\x89PNG\r\n\x1a\n" or data[12:16] != b"IHDR":
            raise ValueError(f"Invalid PNG header: {path}")
        width, height, bit_depth, color_type = struct.unpack(">IIBB", data[16:26])
        if (width, height, bit_depth, color_type) != (16, 16, 8, 6):
            raise ValueError(f"Expected 16x16 RGBA overlay: {path}")


def check_giga_blocks() -> None:
    """Every Giga block (D-445) has its 16x16 texture, its 32x32 connected sheet when the .mcmeta names one, and its
    bloom overlay when the model is a two-layer cube."""
    casings = ASSETS / "textures/block/casings"
    for lang_file in sorted((ASSETS / "mega_lang").glob("giga_*.json")):
        for key in json.loads(lang_file.read_text(encoding="utf-8")):
            block = key.removeprefix("block.typeomega.")
            names = [f"{block}.png"]
            meta = casings / f"{block}.png.mcmeta"
            if meta.exists():
                connection = json.loads(meta.read_text(encoding="utf-8")).get("ldlib", {}).get("connection")
                if connection:
                    names.append(connection.rsplit("/", 1)[1] + ".png")
            model = ROOT / f"src/generated/resources/assets/typeomega/models/block/{block}.json"
            if model.exists() and "cube_2_layer" in model.read_text(encoding="utf-8"):
                names.append(f"{block}_bloom.png")
            for name in names:
                path = casings / name
                if not path.exists():
                    raise ValueError(f"Missing texture {name} of {block}")
                width, height = struct.unpack(">II", path.read_bytes()[16:24])
                want = 32 if name.endswith("_ctm.png") else 16
                if (width, height) != (want, want):
                    raise ValueError(f"Expected {want}x{want} texture: {path} is {width}x{height}")


def check_mega_mounts() -> None:
    """The controller mount of each megastructure that has one is on a static piece, clear of every moving piece."""
    sys.path.insert(0, str(Path(__file__).resolve().parent))
    try:
        import mega_sweep
    except ImportError:
        print("numpy is missing: the mount sweep was not checked")
        return
    for name in mega_sweep.mounted():
        problems, _ = mega_sweep.check(name)
        if problems:
            raise ValueError(f"{name}: " + "; ".join(problems))


def check_built_jar() -> None:
    jars = sorted((ROOT / "build/libs").glob("typeomega-*.jar"))
    if not jars:
        raise ValueError("No built Type Omega jar found")
    with ZipFile(jars[-1]) as jar:
        metadata = tomllib.loads(jar.read("META-INF/mods.toml").decode("utf-8"))
        dependencies = {entry["modId"]: entry for entry in metadata["dependencies"]["typeomega"]}
        gtna = dependencies.get("gtna", {})
        if (gtna.get("mandatory") is not True or gtna.get("versionRange") != "[0.5.3,)"
                or gtna.get("ordering") != "AFTER" or gtna.get("side") != "BOTH"):
            raise ValueError("Distribution jar must require GTNA 0.5.3+ on both sides, loaded before TypeOmegaCore")
        packed_lang = json.loads(jar.read("assets/typeomega/lang/en_us.json"))
        source_lang = json.loads((ROOT / "src/generated/resources/assets/typeomega/lang/en_us.json")
                                 .read_text(encoding="utf-8"))
        if packed_lang != source_lang:
            raise ValueError("Built jar has stale English translations; run build again after runData")
        jar.read("assets/typeomega/models/block/machine/lv_entangled_miner.json")
        jar.read("assets/typeomega/blockstates/lv_entangled_miner.json")
        if 'modId="enderio"' in jar.read("META-INF/mods.toml").decode("utf-8"):
            raise ValueError("Distribution jar still declares an EnderIO dependency")
        if 'modId = "draconicevolution"' in jar.read("META-INF/mods.toml").decode("utf-8"):
            raise ValueError("Distribution jar still declares a Draconic Evolution dependency")
        for icon_set in ("bright", "dull", "metallic", "rough", "shiny"):
            jar.read(f"assets/gtceu/textures/item/material_sets/{icon_set}/ingot_triple.png")
        jar.read("assets/gtceu/textures/item/material_sets/radioactive/ingot_triple_secondary.png")
        for icon_set in ("phase_eternity", "crystalline_damascus"):
            for form in ("ingot_triple", "ingot_quadruple", "ingot_quintuple", "plate_triple",
                         "plate_quadruple", "plate_quintuple", "plate_superdense"):
                jar.read(f"assets/gtceu/textures/item/material_sets/{icon_set}/{form}.png")
                jar.read(f"assets/gtceu/models/item/material_sets/{icon_set}/{form}.json")


def main() -> int:
    try:
        check_languages()
        check_overlays()
        check_giga_blocks()
        check_mega_mounts()
        check_built_jar()
    except (OSError, ValueError, KeyError, json.JSONDecodeError) as exc:
        print(f"QA failed: {exc}", file=sys.stderr)
        return 1
    print("QA static passed: translations, machine overlays, Giga blocks, mega mounts, phase assets, and built jar")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
