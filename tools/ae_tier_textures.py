#!/usr/bin/env python3
"""Placeholder textures of the tiered ME Interfaces and Acceleration Cards (GTIA-D-442).

Reads the AE2 interface and speed card textures from the AE2 jar, rotates the hue of their coloured pixels to a tier
colour and marks the tier with 2 to 5 dots; the greys are kept. Usage: ae_tier_textures.py PATH_TO_AE2_JAR
These are placeholders for the author to redraw; they are derived from AE2 art (CC BY-NC-SA 3.0).
"""
import colorsys
import io
import sys
import zipfile
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "src/main/resources/assets/typeomega/textures"
TIERS = {"ii": (130, 2), "iii": (42, 3), "iv": (285, 4), "v": (352, 5)}
# target name (with {t} for the tier) -> texture in the AE2 jar
SOURCES = {
    "block/me_interface_{t}": "assets/ae2/textures/block/interface.png",
    "part/me_interface_{t}": "assets/ae2/textures/part/interface.png",
    "part/me_interface_{t}_sides": "assets/ae2/textures/part/interface_sides.png",
    "part/me_interface_{t}_back": "assets/ae2/textures/part/interface_back.png",
}
CARD = "assets/ae2/textures/item/card_speed.png"


def recolor(image: Image.Image, hue: float, min_saturation: float) -> Image.Image:
    out = image.convert("RGBA")
    px = out.load()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = px[x, y]
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            if a and s >= min_saturation:
                nr, ng, nb = colorsys.hsv_to_rgb(hue / 360, min(1.0, s * 1.15 + 0.1), v)
                px[x, y] = (round(nr * 255), round(ng * 255), round(nb * 255), a)
    return out


def dots(image: Image.Image, count: int, y: int, color=(255, 255, 255, 255), left: int = 3) -> None:
    for i in range(count):
        image.putpixel((left + 2 * i, y), color)


def main(jar_path: str) -> None:
    with zipfile.ZipFile(jar_path) as jar:
        for tier, (hue, count) in TIERS.items():
            for name, source in SOURCES.items():
                image = recolor(Image.open(io.BytesIO(jar.read(source))), hue, 0.08)
                if name in ("block/me_interface_{t}", "part/me_interface_{t}"):
                    dots(image, count, 12, (20, 20, 20, 255), 8 - count + 1 if name.startswith("part") else 3)
                target = OUT / (name.format(t=tier) + ".png")
                target.parent.mkdir(parents=True, exist_ok=True)
                image.save(target)
            card = recolor(Image.open(io.BytesIO(jar.read(CARD))), hue, 0.25)
            dots(card, count, 1, (255, 255, 255, 255), 2)
            card.save(OUT / f"item/speed_card_{tier}.png")


if __name__ == "__main__":
    main(sys.argv[1])
