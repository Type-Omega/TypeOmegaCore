package com.typeomega.core.data.tooltip;

import java.util.Locale;

/** Number formats shared by the tooltip families. */
public final class Fmt {

    private Fmt() {}

    /** "2^40" for an exact power of two above 2^16, the plain number otherwise. */
    public static String pow2(double v) {
        if (v > 65536 && v < 0x1p1023) {
            int e = Math.getExponent(v);
            if (v == Math.scalb(1.0, e)) return "2^" + e;
        }
        return num(v);
    }

    /** An integer with thousands separators, or up to four significant digits. */
    public static String num(double v) {
        if (v == Math.rint(v) && Math.abs(v) < 1e7) return String.format(Locale.ROOT, "%,d", (long) v);
        if (Math.abs(v) >= 1e7 || Math.abs(v) < 1e-3) return sci(v);
        return String.format(Locale.ROOT, "%.4g", v).replaceAll("(\\.\\d*?)0+$", "$1").replaceAll("\\.$", "");
    }

    /** "1.5e11" style for very large or very small numbers. */
    public static String sci(double v) {
        String s = String.format(Locale.ROOT, "%.3g", v);
        if (!s.contains("e")) s = String.format(Locale.ROOT, "%.2e", v);
        String[] parts = s.split("e");
        String mantissa = parts[0].replaceAll("(\\.\\d*?)0+$", "$1").replaceAll("\\.$", "");
        int exponent = Integer.parseInt(parts[1]);
        return mantissa.equals("1") ? "10^" + exponent : mantissa + "×10^" + exponent;
    }

    /** A share as a percentage: 0.85 is "85%". */
    public static String pct(double share) {
        return num(Math.round(share * 10000) / 100.0) + "%";
    }

    /** Per mille as a percentage: 20 is "2%". */
    public static String perMille(int perMille) {
        return pct(perMille / 1000.0);
    }

    /** A multiplier: 2 is "x2". */
    public static String times(double factor) {
        return "x" + num(factor);
    }
}
