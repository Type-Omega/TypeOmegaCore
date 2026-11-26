package com.typeomega.core.kardashev;

/**
 * Where the energy the meter counts comes from (D-443). The first four are generation and all of them feed K; the
 * last two are what the team's wireless networks hand out. Local consumers are not counted: that would need a hook in
 * every machine, and what a Wireless Energy Hatch pulls is already counted when it leaves the network.
 */
public enum KardashevSource {

    WIRELESS_ENERGY("wireless_energy"),
    WIRELESS_STEAM("wireless_steam"),
    BOILER("boiler"),
    GENERATOR("generator"),
    SPENT_ENERGY("spent_energy"),
    SPENT_STEAM("spent_steam");

    public static final int COUNT = values().length;
    /** Sources before this ordinal are generation, the rest are spending. */
    public static final int GENERATED = 4;

    private final String id;

    KardashevSource(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public boolean generated() {
        return ordinal() < GENERATED;
    }
}
