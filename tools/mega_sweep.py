"""Sweep check of the controller mount (D-445): the controller and its 3x3 of hatches must sit on a static piece, and no
moving piece may come near the mount while it turns. Reads typeomega_mega/<name>/{pattern.bin.gz,parts.json,tracks.json}
as MegaShape and MegaMotion do, turns every moving cell by the samples of its track (rotation about the pivot, then the
shift, as MegaMotion.Track.at does) and measures the distance from the swept cells to the box of the mount.

    python3 tools/mega_sweep.py [--margin 4] [name ...]      (default: the megastructures that have a mount)

Needs numpy. qa_static.py runs it for every megastructure whose parts.json has a mount piece.
"""
import gzip
import json
import pathlib
import struct
import sys

import numpy as np

DATA = pathlib.Path(__file__).resolve().parents[1] / "src/main/resources/typeomega_mega"
MIN_MARGIN = 4


def load(name):
    d = DATA / name
    raw = gzip.open(d / "pattern.bin.gz").read()
    assert raw[:4] == b"GTMB"
    nx, ny, nz, ck, cj, ci = struct.unpack(">6i", raw[4:28])
    pivot = np.array(struct.unpack(">3f", raw[28:40]))
    grid = np.frombuffer(raw[60:], np.uint8).reshape(nz, ny, nx)
    parts = {p["letter"]: p for p in json.load(open(d / "parts.json"))}
    tracks = json.load(open(d / "tracks.json"))["tracks"]
    return {"grid": grid, "ctrl": (ck, cj, ci), "pivot": pivot, "parts": parts, "tracks": tracks}


def has_mount(shape):
    return any(p["part"].startswith("mount") for p in shape["parts"].values())


def cells(shape, letters):
    """Grid cells (k, j, i) of the given letters, relative to the pivot."""
    mask = np.isin(shape["grid"], [ord(c) for c in letters])
    zyx = np.argwhere(mask)
    return zyx[:, ::-1].astype(float) - shape["pivot"]


def quaternion_matrix(q):
    x, y, z, w = q
    return np.array([[1 - 2 * (y * y + z * z), 2 * (x * y - z * w), 2 * (x * z + y * w)],
                     [2 * (x * y + z * w), 1 - 2 * (x * x + z * z), 2 * (y * z - x * w)],
                     [2 * (x * z - y * w), 2 * (y * z + x * w), 1 - 2 * (x * x + y * y)]])


def check(name, margin=MIN_MARGIN, max_samples=60):
    """Returns (problems, gap): the problems found and the smallest distance (in blocks) from any swept cell to the
    mount box, measured per axis (Chebyshev) so it is the clearance the player sees."""
    shape = load(name)
    problems = []
    parts = shape["parts"]
    mount_letters = [l for l, p in parts.items() if p["part"].startswith("mount")]
    ck, cj, ci = shape["ctrl"]
    ctrl_letter = chr(shape["grid"][ci, cj, ck])
    if ctrl_letter != "~":
        problems.append("the controller cell holds %r, not ~" % ctrl_letter)
    for l in mount_letters:
        if parts[l]["track"] is not None:
            problems.append("mount letter %r is on a moving track" % l)
    # The host: the cells around the mount's wall must belong to a static piece (a moving piece cannot carry it).
    mount = cells(shape, mount_letters + ["~"])
    lo, hi = mount.min(0), mount.max(0)
    gap = float("inf")
    for track_name, track in shape["tracks"].items():
        letters = [l for l, p in parts.items() if p["track"] == track_name]
        if not letters:
            continue
        pts = cells(shape, letters)
        samples = track["samples"]
        step = max(1, len(samples) // max_samples)
        for s in samples[::step]:
            moved = pts @ quaternion_matrix(s[:4]).T + np.array(s[4:7])
            outside = np.maximum(lo - moved, moved - hi).max(1)
            gap = min(gap, float(outside.min()))
    if gap < margin:
        problems.append("a moving piece passes %.1f blocks from the mount (at least %d wanted)" % (gap, margin))
    return problems, gap


def mounted():
    """The megastructures with a mount; the shape data is not in git (D-313), so without it there is nothing to check."""
    if not DATA.is_dir():
        return []
    return [p.name for p in sorted(DATA.iterdir()) if (p / "parts.json").exists() and has_mount(load(p.name))]


def main(argv):
    margin = MIN_MARGIN
    if "--margin" in argv:
        i = argv.index("--margin")
        margin = int(argv[i + 1])
        del argv[i:i + 2]
    names = argv or mounted()
    bad = 0
    for name in names:
        problems, gap = check(name, margin)
        print("%-14s gap %s %s" % (name, "none" if gap == float("inf") else "%.1f" % gap,
                                   "ok" if not problems else "; ".join(problems)))
        bad += bool(problems)
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
