package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import com.typeomega.core.kardashev.KardashevMegas;

/**
 * A megastructure of Type 0 (GTIA-D-390): an HV multiblock that counts on the Kardashev meter of its owner's team
 * the first time it forms (GTIA-D-389).
 */
public class TypeZeroMegaMachine extends WorkableElectricMultiblockMachine {

    private final String megaId;

    public TypeZeroMegaMachine(IMachineBlockEntity holder, String megaId) {
        super(holder);
        this.megaId = megaId;
    }

    public String megaId() {
        return megaId;
    }

    /** Whether the formed structure already counts as the megastructure; the Cosmodrome also needs its link. */
    protected boolean countsAsBuilt() {
        return true;
    }

    /** Reports to the meter when the structure counts; safe to call often, only the first report changes K. */
    protected void reportIfBuilt() {
        if (isFormed() && countsAsBuilt()) KardashevMegas.report(this, megaId);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        reportIfBuilt();
    }
}
