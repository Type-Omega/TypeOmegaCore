package com.typeomega.core.integration.ae.wireless;

import com.typeomega.core.TypeOmegaMod;

/** Texts of the wireless ME network (D-442): English here, pt_br and zh_cn in the lang files. */
final class WirelessLang {

    static final String SELECTED = "typeomega.wireless.selected";
    static final String SELECTED_NONE = "typeomega.wireless.selected_none";
    static final String CREATED = "typeomega.wireless.created";
    static final String BOUND = "typeomega.wireless.bound";
    static final String UNBOUND = "typeomega.wireless.unbound";
    static final String NOT_BOUND = "typeomega.wireless.not_bound";
    static final String READ = "typeomega.wireless.read";
    static final String NOT_BINDABLE = "typeomega.wireless.not_bindable";
    static final String NETWORK_DENIED = "typeomega.wireless.network_denied";
    static final String MACHINE_DENIED = "typeomega.wireless.machine_denied";
    static final String OWNER_OTHER_TEAM = "typeomega.wireless.owner_other_team";
    static final String CONNECTOR_TAKEN = "typeomega.wireless.connector_taken";
    static final String NETWORK_GONE = "typeomega.wireless.network_gone";
    static final String STATUS = "typeomega.wireless.status";
    static final String STATUS_NONE = "typeomega.wireless.status_none";

    static final String CONFIGURATOR_USE = "typeomega.wireless.configurator.use";
    static final String CONFIGURATOR_CREATE = "typeomega.wireless.configurator.create";
    static final String CONFIGURATOR_BIND = "typeomega.wireless.configurator.bind";
    static final String CONFIGURATOR_READ = "typeomega.wireless.configurator.read";
    static final String CONFIGURATOR_TEAM = "typeomega.wireless.configurator.team";
    static final String CONNECTOR_DESC = "typeomega.wireless.connector.desc";
    static final String CONNECTOR_CHANNELS = "typeomega.wireless.connector.channels";
    static final String CONNECTOR_ONE = "typeomega.wireless.connector.one";

    private WirelessLang() {}

    static void init() {
        var r = TypeOmegaMod.REGISTRATE;
        r.addRawLang(SELECTED, "Network: %s");
        r.addRawLang(SELECTED_NONE, "No network selected: using the configurator on a machine unbinds it");
        r.addRawLang(CREATED, "Created the network \"%s\" and selected it");
        r.addRawLang(BOUND, "Bound to the network \"%s\"");
        r.addRawLang(UNBOUND, "Unbound from its wireless network");
        r.addRawLang(NOT_BOUND, "This block is not bound to a wireless network");
        r.addRawLang(READ, "Selected the network \"%s\" of this block");
        r.addRawLang(NOT_BINDABLE, "This block cannot join a wireless ME network");
        r.addRawLang(NETWORK_DENIED, "Your team cannot use this network");
        r.addRawLang(MACHINE_DENIED, "You cannot configure this machine: it belongs to another team");
        r.addRawLang(OWNER_OTHER_TEAM, "The owner of this machine is not in the team of that network");
        r.addRawLang(CONNECTOR_TAKEN, "The network \"%s\" already has a connector; a network has only one");
        r.addRawLang(NETWORK_GONE, "That network no longer exists");
        r.addRawLang(STATUS, "Wireless network: %s");
        r.addRawLang(STATUS_NONE, "Not bound to a wireless network");
        r.addRawLang(CONFIGURATOR_USE,
                "Right-click the air to choose a network of your team, sneak right-click to create one (named like the item)");
        r.addRawLang(CONFIGURATOR_BIND,
                "Right-click an ME part or a connector to bind it to the chosen network; with none chosen it unbinds");
        r.addRawLang(CONFIGURATOR_READ, "Sneak right-click a bound block to choose its network");
        r.addRawLang(CONFIGURATOR_CREATE, "Selected: %s");
        r.addRawLang(CONFIGURATOR_TEAM, "Networks belong to your FTB team, or to you without one");
        r.addRawLang(CONNECTOR_DESC,
                "Put it on an ME cable and bind it: every part bound to the same network joins that ME network without cables");
        r.addRawLang(CONNECTOR_CHANNELS,
                "The link carries up to 32 channels, and each bound part takes one like a cabled part");
        r.addRawLang(CONNECTOR_ONE, "A network has one connector");
    }
}
