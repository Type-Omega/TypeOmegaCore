"""Draft textures of the Giga blocks (D-445, megas v02 study): charcoal hulls with one accent each, emissive accents and
the shared mount blocks. Everything here is new art for the author to review and redraw; the script is only a way to
get a structured, tinted draft in the study's palette, so run it once and edit the PNGs by hand afterwards.

    python3 tools/giga_textures.py [--force] [block ...]

Writes under src/main/resources/assets/typeomega/textures/block/casings/ and refuses to overwrite a file (the author
may have redrawn it) unless --force is given. A connected block gets the 32 x 32 LDLib sheet (TL = interior, TR =
left/right borders, BL = top/bottom borders, BR = all borders, the same layout as the Rocket Assembly Casing) and the
.mcmeta that points at it; an emissive block gets a `_bloom` overlay and the bloom .mcmeta.
"""
import json
import pathlib
import sys

import numpy as np
from PIL import Image

OUT = pathlib.Path(__file__).resolve().parents[1] / "src/main/resources/assets/typeomega/textures/block/casings"


def rgb(h):
    h = h.lstrip("#")
    return np.array([int(h[i:i + 2], 16) for i in (0, 2, 4)] + [255], dtype=np.uint8)


def noise(x, y, salt=0):
    """A deterministic 0..1 value per pixel, so a redraw gives the same file."""
    v = (x * 73856093) ^ (y * 19349663) ^ (salt * 83492791)
    return ((v >> 4) & 255) / 255.0


def mix(a, b, t):
    return (a.astype(float) * (1 - t) + b.astype(float) * t).astype(np.uint8)


def blank(color=None):
    img = np.zeros((16, 16, 4), np.uint8)
    if color is not None:
        img[:, :] = color
    return img


def frame(img, sides, outer, bevel, inner):
    """A 3-pixel border (dark edge, light bevel, shade) on the given sides of a 16 x 16 tile."""
    for s in sides:
        for k, color in enumerate((outer, bevel, inner)):
            if s == "L":
                img[:, k] = color
            elif s == "R":
                img[:, 15 - k] = color
            elif s == "T":
                img[k, :] = color
            elif s == "B":
                img[15 - k, :] = color
    return img


# ------------------------------------------------------------------ interiors (they tile with a period of 8 or 16)

def hex_cells(pal):
    """Staggered cells of 8 x 8 with cut corners: a hexagonal frame (rows offset by half a cell)."""
    img = blank()
    for y in range(16):
        for x in range(16):
            row = y // 8
            cx, cy = (x - 4 * (row % 2)) % 8, y % 8
            base = mix(pal[1], pal[2], 0.25 * noise(x, y))
            if cx == 0 or cy == 0:
                base = pal[0]
            elif cx == 1 or cy == 1:
                base = pal[3]
            elif (cx, cy) in ((2, 2),) or (cx + cy) > 12:
                base = pal[2]
            if (cx, cy) in ((0, 0), (1, 1)):
                base = pal[0]
            img[y, x] = base
    return img


def plates(pal, w, h, hot=None):
    """Riveted panels of w x h pixels, the seams dark; `hot` colours the lowest row of each panel (heat streak)."""
    img = blank()
    for y in range(16):
        for x in range(16):
            px, py = x % w, y % h
            base = mix(pal[1], pal[2], 0.3 * noise(x, y, 3))
            if px == 0 or py == 0:
                base = pal[0]
            elif px == 1 and py == 1 or px == w - 1 and py == h - 1:
                base = pal[3]
            elif py == h - 1 and hot is not None:
                base = mix(base, hot, 0.55)
            img[y, x] = base
    return img


# ------------------------------------------------------------------ the textures

def connected_sheet(make):
    """The 2 x 2 sheet from make(sides) -> a tile: TL interior, TR left/right, BL top/bottom, BR all four."""
    sheet = np.zeros((32, 32, 4), np.uint8)
    for (qx, qy), sides in (((0, 0), ""), ((1, 0), "LR"), ((0, 1), "TB"), ((1, 1), "LRTB")):
        sheet[qy * 16:qy * 16 + 16, qx * 16:qx * 16 + 16] = make(sides)
    return sheet


def orchid_cell_frame():
    pal = [rgb(c) for c in ("#1e2230", "#2b3140", "#3b4358", "#5d6a8c")]
    return lambda sides: frame(hex_cells(pal), sides, pal[0], pal[3], pal[2])


def forge_cobalt_plate():
    pal = [rgb(c) for c in ("#10294a", "#1f4f8a", "#3f7bc0", "#f2f6ff")]
    return lambda sides: frame(plates(pal, 8, 4), sides, pal[0], pal[3], pal[1])


def lifter_girdle_hull():
    pal = [rgb(c) for c in ("#17191d", "#262a30", "#3a3f46", "#c9622a")]
    return lambda sides: frame(plates(pal, 8, 8, hot=pal[3]), sides, pal[0], pal[2], pal[1])


def orchid_cell_glass():
    """Blue-violet glass at 55% with a hexagonal facet and a bright corner; the frame is charcoal and opaque."""
    pal = [rgb(c) for c in ("#3b4bd6", "#6a5ad6", "#a79bff")]
    dark = rgb("#1e2230")

    def make(sides):
        img = blank()
        for y in range(16):
            for x in range(16):
                row = y // 8
                cx, cy = (x - 4 * (row % 2)) % 8, y % 8
                c = mix(pal[0], pal[1], 0.5 * noise(x, y, 5))
                if cx == 0 or cy == 0:
                    c = pal[2]
                img[y, x] = c
                img[y, x, 3] = 140 if cx and cy else 190
        frame(img, sides, dark, pal[2], pal[1])
        return img
    return make


def giga_service_deck():
    """A floor grid of 4 x 4 plates with a yellow safety edge."""
    c0, c1, c2 = rgb("#1e2227"), rgb("#3b424a"), rgb("#d9b23a")
    img = blank()
    for y in range(16):
        for x in range(16):
            base = mix(c0, c1, 0.35 + 0.3 * noise(x, y, 7))
            if x % 4 == 0 or y % 4 == 0:
                base = rgb("#14161a")
            if x in (1, 14) or y in (1, 14):
                base = c2 if (x + y) % 4 < 2 else rgb("#2a2410")
            img[y, x] = base
    return img


def giga_truss():
    """A charcoal lattice with X braces; the holes are transparent."""
    c0, c1, c2 = rgb("#14161a"), rgb("#2b3037"), rgb("#55606a")
    img = blank()
    for y in range(16):
        for x in range(16):
            edge = x in (0, 1, 14, 15) or y in (0, 1, 14, 15)
            diag = abs(x - y) <= 1 or abs(x + y - 15) <= 1
            if edge or diag:
                c = c1
                if x in (0, 15) or y in (0, 15):
                    c = c0
                elif (x + y) % 5 == 0:
                    c = c2
                img[y, x] = c
    return img


def giga_lamp():
    c0, c1 = rgb("#1e2227"), rgb("#2b3037")
    img = blank()
    for y in range(16):
        for x in range(16):
            img[y, x] = mix(c0, c1, 0.4 * noise(x, y, 9))
            if x in (0, 15) or y in (0, 15):
                img[y, x] = rgb("#14161a")
    bloom = blank()
    for y in range(3, 13):
        for x in range(3, 13):
            d = max(abs(x - 7.5), abs(y - 7.5))
            if d <= 1.5:
                bloom[y, x] = rgb("#e8fff6")
            elif d <= 3.5:
                bloom[y, x] = rgb("#9ff5d0")
            elif d <= 4.5 and (x + y) % 2 == 0:
                bloom[y, x] = rgb("#9ff5d0")
                bloom[y, x, 3] = 150
    return img, bloom


def orchid_window_rim():
    """Dark slate with white contoured grooves, 4 x 1, in rows; the grooves glow."""
    c0, c1 = rgb("#1b1f2b"), rgb("#2b3140")
    img = blank()
    bloom = blank()
    for y in range(16):
        for x in range(16):
            img[y, x] = mix(c0, c1, 0.5 * noise(x, y, 11))
            if x in (0, 15) or y in (0, 15):
                img[y, x] = rgb("#12151d")
    for gy in (3, 7, 11):
        for gx0 in (1, 9) if gy != 7 else (5, 13):
            for gx in range(gx0, gx0 + 4):
                gx %= 16
                img[gy, gx] = rgb("#10131b")
                bloom[gy, gx] = rgb("#e8f0ff")
    return img, bloom


def terrace_rim_light():
    """A 3-pixel white-cyan strip across the middle, on a dark cobalt base."""
    c0, c1 = rgb("#0c1c34"), rgb("#10294a")
    img = blank()
    bloom = blank()
    for y in range(16):
        for x in range(16):
            img[y, x] = mix(c0, c1, 0.5 * noise(x, y, 13))
    for y in (6, 7, 8):
        for x in range(16):
            img[y, x] = rgb("#9be8ff")
            bloom[y, x] = rgb("#ffffff") if y == 7 else rgb("#9be8ff")
    return img, bloom


def heated_hex_plate():
    """A hexagon with an ember core: orange, hotter at the centre."""
    c0, c1 = rgb("#1b1512"), rgb("#6b2a10")
    img = blank()
    bloom = blank()
    for y in range(16):
        for x in range(16):
            img[y, x] = mix(c0, c1, 0.3 * noise(x, y, 17))
            dx, dy = abs(x - 7.5), abs(y - 7.5)
            hexd = max(dy, (dx * 0.866 + dy * 0.5))
            if hexd <= 6.5:
                heat = 1 - hexd / 6.5
                img[y, x] = mix(rgb("#6b2a10"), rgb("#ff7a1a"), min(1, heat * 1.4))
                if heat > 0.55:
                    img[y, x] = mix(rgb("#ff7a1a"), rgb("#ffd08a"), (heat - 0.55) / 0.45)
                    bloom[y, x] = img[y, x]
                elif heat > 0.25:
                    bloom[y, x] = rgb("#ff7a1a")
                    bloom[y, x, 3] = 170
            if abs(hexd - 6.5) < 0.7:
                img[y, x] = rgb("#14100d")
    return img, bloom


CONNECTED = {"orchid_cell_frame": orchid_cell_frame, "forge_cobalt_plate": forge_cobalt_plate,
             "lifter_girdle_hull": lifter_girdle_hull, "orchid_cell_glass": orchid_cell_glass}
FLAT = {"giga_service_deck": giga_service_deck, "giga_truss": giga_truss}
GLOWING = {"giga_lamp": giga_lamp, "orchid_window_rim": orchid_window_rim, "terrace_rim_light": terrace_rim_light,
           "heated_hex_plate": heated_hex_plate}


def save(name, array, force):
    path = OUT / name
    if path.exists() and not force:
        print("keeps", name, "(already there; --force overwrites)")
        return False
    Image.fromarray(array, "RGBA").save(path)
    print("wrote", name)
    return True


def write_mcmeta(name, meta, force):
    path = OUT / (name + ".png.mcmeta")
    if path.exists() and not force:
        return
    path.write_text(json.dumps(meta, indent=4) + "\n", encoding="utf-8")


def main(argv):
    force = "--force" in argv
    only = [a for a in argv if not a.startswith("--")]
    OUT.mkdir(parents=True, exist_ok=True)
    for name, factory in CONNECTED.items():
        if only and name not in only:
            continue
        make = factory()
        save(name + ".png", make("LRTB"), force)
        save(name + "_ctm.png", connected_sheet(make), force)
        write_mcmeta(name, {"ldlib": {"connection": "typeomega:block/casings/" + name + "_ctm"}}, force)
    for name, factory in FLAT.items():
        if not only or name in only:
            save(name + ".png", factory(), force)
    for name, factory in GLOWING.items():
        if only and name not in only:
            continue
        base, bloom = factory()
        save(name + ".png", base, force)
        save(name + "_bloom.png", bloom, force)
        write_mcmeta(name + "_bloom", {"ldlib": {"emissive": True}, "shimmer": {"bloom": True}}, force)


if __name__ == "__main__":
    main(sys.argv[1:])
