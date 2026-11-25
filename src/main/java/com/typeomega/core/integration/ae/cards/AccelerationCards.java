package com.typeomega.core.integration.ae.cards;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.localization.GuiText;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.typeomega.core.GTIAMod;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/** Registration of Acceleration Card (II) to (V): the items, the machines that take them and the recipes. */
public final class AccelerationCards {

    private static final Map<AccelerationTier, ItemEntry<AccelerationCardItem>> CARDS = new EnumMap<>(
            AccelerationTier.class);

    static {
        for (AccelerationTier tier : AccelerationTier.values()) {
            CARDS.put(tier, GTIAMod.REGISTRATE.item(tier.id(), p -> new AccelerationCardItem(p, tier))
                    .lang("Acceleration Card (" + tier.name() + ")")
                    .model((ctx, prov) -> prov.generated(ctx, GTIAMod.id("item/" + tier.id())))
                    .register());
        }
    }

    private AccelerationCards() {}

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(AccelerationCards::onLoadComplete);
    }

    public static Item card(AccelerationTier tier) {
        return CARDS.get(tier).get();
    }

    /** The card of a level, 0 being the AE2 one. */
    public static Item card(int level) {
        return level == 0 ? AEItems.SPEED_CARD.asItem() : card(AccelerationTier.ofLevel(level));
    }

    // EN: After AE2 and ExtendedAE have registered theirs, so each machine takes the new cards up to its own limit.
    // PT: Depois de o AE2 e o ExtendedAE registrarem os deles, para cada máquina aceitar os novos até o limite dela.
    // ZH: 在 AE2 与 ExtendedAE 注册完毕之后，使每台机器按自身上限接受新的卡。
    private static void onLoadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            String buses = GuiText.IOBuses.getTranslationKey();
            String extendedBuses = "group.ex_io_bus_part";
            accept(AEParts.IMPORT_BUS, buses);
            accept(AEParts.EXPORT_BUS, buses);
            accept(EPPItemAndBlock.EX_IMPORT_BUS, extendedBuses);
            accept(EPPItemAndBlock.EX_EXPORT_BUS, extendedBuses);
            accept(EPPItemAndBlock.TAG_EXPORT_BUS, null);
            accept(EPPItemAndBlock.MOD_EXPORT_BUS, null);
            accept(EPPItemAndBlock.THRESHOLD_EXPORT_BUS, null);
            accept(AEBlocks.IO_PORT, null);
            accept(EPPItemAndBlock.EX_IO_PORT, null);
        });
    }

    private static void accept(ItemLike machine, String group) {
        int limit = Upgrades.getMaxInstallable(AEItems.SPEED_CARD, machine);
        if (limit <= 0) return;
        for (AccelerationTier tier : AccelerationTier.values()) {
            Upgrades.add(card(tier), machine, limit, group);
        }
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        for (AccelerationTier tier : AccelerationTier.values()) {
            VanillaRecipeHelper.addShapedRecipe(provider, true, GTIAMod.id(tier.id()), new ItemStack(card(tier)),
                    " A ", "ACA", " A ",
                    'A', AEItems.SPEED_CARD.asItem(), 'C', tier.circuit);
        }
    }
}
