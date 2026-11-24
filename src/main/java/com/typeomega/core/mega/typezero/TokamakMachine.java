package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * HV Tokamak (GTIA-D-379, D-390, D-421): a fusion generator. A cold plasma needs a start-up charge, drawn from its
 * Energy Hatches into the controller; once lit it keeps burning while fuel follows without a pause.
 */
public class TokamakMachine extends TypeZeroMegaMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(TokamakMachine.class,
            TypeZeroMegaMachine.MANAGED_FIELD_HOLDER);

    // EN: Start-up charge like the GTCEu fusion reactors, scaled to HV: 2 M EU is about 3 minutes of one HV Energy
    // Hatch (GTIA-D-421 "energia de partida"). A plasma idle for 5 s cools and needs the charge again.
    // PT: Carga de partida como nos reatores de fusão do GTCEu, na escala do HV: 2 M EU são uns 3 minutos de um
    // Energy Hatch HV (GTIA-D-421, "energia de partida"). Um plasma parado por 5 s esfria e pede a carga de novo.
    // ZH: 与 GTCEu 聚变反应堆一样需要启动能量，按 HV 缩放：2 M EU 约为一个 HV 能源仓 3 分钟的输入
    // （GTIA-D-421“启动能量”）。等离子体闲置 5 秒后冷却，需要重新充能。
    public static final long START_EU = 2_000_000L;
    public static final int COOL_AFTER_TICKS = 100;

    @Persisted
    private long startCharge;
    @Persisted
    private long lastBurnTick = Long.MIN_VALUE;
    private @Nullable TickableSubscription charger;

    public TokamakMachine(IMachineBlockEntity holder) {
        super(holder, Tokamak.ID);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public long startCharge() {
        return startCharge;
    }

    /** Adds start-up charge directly; the GameTests use it instead of minutes of hatch input. */
    public void addStartChargeForTests(long eu) {
        startCharge = Math.min(START_EU, startCharge + eu);
    }

    /** Whether the plasma is still lit from the last run. */
    public boolean lit() {
        return getLevel() != null && getLevel().getGameTime() - lastBurnTick <= COOL_AFTER_TICKS &&
                lastBurnTick != Long.MIN_VALUE;
    }

    /** The Energy Hatches that feed the start-up charge. */
    private EnergyContainerList inputHatches() {
        List<IEnergyContainer> containers = new ArrayList<>();
        for (var handler : getCapabilitiesFlat(IO.IN, EURecipeCapability.CAP))
            if (handler instanceof IEnergyContainer container) containers.add(container);
        return new EnergyContainerList(containers);
    }

    /** The dynamos carry the output, so the GTCEu display and tier read them, not the start-up hatches. */
    @Override
    public EnergyContainerList getEnergyContainer() {
        List<IEnergyContainer> containers = new ArrayList<>();
        for (var handler : getCapabilitiesFlat(IO.OUT, EURecipeCapability.CAP))
            if (handler instanceof IEnergyContainer container) containers.add(container);
        return new EnergyContainerList(containers);
    }

    private void charge() {
        if (startCharge >= START_EU) return;
        var hatches = inputHatches();
        long take = Math.min(hatches.getEnergyStored(), START_EU - startCharge);
        if (take > 0) startCharge += -hatches.changeEnergy(-take);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        charger = subscribeServerTick(charger, this::charge);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(charger);
        charger = null;
    }

    /** A cold plasma takes the whole start-up charge to light; a lit one runs on. */
    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (!super.beforeWorking(recipe)) return false;
        if (lit()) return true;
        if (startCharge < START_EU) return false;
        startCharge = 0;
        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        if (getLevel() != null) lastBurnTick = getLevel().getGameTime();
    }

    @Override
    public boolean onWorking() {
        boolean working = super.onWorking();
        if (getLevel() != null) lastBurnTick = getLevel().getGameTime();
        return working;
    }

    /** Keeps looking for fuel while charging, since the charge changes no hatch the recipe logic listens to. */
    @Override
    public boolean keepSubscribing() {
        return true;
    }

    /** The D-T recipe, shown on the Fusion tab before the first run. */
    public @Nullable GTRecipe defaultRecipe() {
        if (getLevel() == null) return null;
        return getLevel().getRecipeManager().byKey(TypeZeroRecipes.DT_RECIPE)
                .map(r -> r instanceof GTRecipe recipe ? recipe : null).orElse(null);
    }

    /** The Fusion tab: the same illustrated screen as the EMI page (GTIA-D-435, U6). */
    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        super.attachSideTabs(sideTabs);
        sideTabs.attachSubTab(TokamakScreen.tab(this));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(lit() ?
                Component.translatable("typeomega.machine.tokamak_hv.lit").withStyle(ChatFormatting.GREEN) :
                Component.translatable("typeomega.machine.tokamak_hv.charge", startCharge, START_EU)
                        .withStyle(ChatFormatting.YELLOW));
    }
}
