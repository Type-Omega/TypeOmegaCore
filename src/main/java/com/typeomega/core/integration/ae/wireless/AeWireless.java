package com.typeomega.core.integration.ae.wireless;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.AEItems;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.typeomega.core.TypeOmegaMod;

import java.util.function.Consumer;

/**
 * Wireless ME network (D-442): the ME Wireless Connector, the ME Wireless Configurator, and the sweep that keeps the
 * bound parts linked. Registration only; the behaviour is in the classes next to it.
 */
public final class AeWireless {

    public static final String CONNECTOR_ID = TypeOmegaMod.MOD_ID + ":me_wireless_connector";

    public static final BlockEntry<WirelessConnectorBlock> CONNECTOR = TypeOmegaMod.REGISTRATE
            .block("me_wireless_connector", p -> new WirelessConnectorBlock())
            .lang("ME Wireless Connector")
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate((ctx, prov) -> {})
            .loot((tables, b) -> tables.dropSelf(b))
            .item((b, p) -> new WirelessConnectorItem(b, p))
            .model((ctx, prov) -> {})
            .build()
            .register();

    public static final BlockEntityEntry<WirelessConnectorEntity> CONNECTOR_ENTITY = TypeOmegaMod.REGISTRATE
            .blockEntity("me_wireless_connector", WirelessConnectorEntity::new)
            .validBlock(CONNECTOR)
            .register();

    public static final ItemEntry<WirelessConfiguratorItem> CONFIGURATOR = TypeOmegaMod.REGISTRATE
            .item("me_wireless_configurator", WirelessConfiguratorItem::new)
            .lang("ME Wireless Configurator")
            .model((ctx, prov) -> prov.generated(ctx, TypeOmegaMod.id("item/me_wireless_configurator")))
            .register();

    private AeWireless() {}

    public static void init() {
        WirelessLang.init();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(AeWireless::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(WirelessLinker::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(WirelessLinker::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(WirelessLinker::onServerStopped);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            var block = CONNECTOR.get();
            var type = CONNECTOR_ENTITY.get();
            block.setBlockEntity(WirelessConnectorEntity.class, type, null, null);
            AEBaseBlockEntity.registerBlockEntityItem(type, block.asItem());
        });
    }

    // EN: The connector is ExtendedAE's Wireless Connector with a circuit on each corner; the configurator is a
    // EN: receiver on a processor, as in GTOCore, so the whole line stays behind the HV AE2 refinement.
    // PT: O conector é o Wireless Connector do ExtendedAE com um circuito em cada canto; o configurador é um
    // PT: receptor sobre um processador, como no GTOCore, e a linha toda fica atrás do refinamento HV do AE2.
    // ZH: 连接器是 ExtendedAE 的无线连接器，四角各配一块电路；配置器是接收器加处理器，与 GTOCore 相同，整条线都在 AE2 的 HV 精炼阶段之后。
    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        var wirelessConnect = ForgeRegistries.ITEMS
                .getValue(new ResourceLocation("expatternprovider", "wireless_connect"));
        if (wirelessConnect != null) {
            VanillaRecipeHelper.addShapedRecipe(provider, true, TypeOmegaMod.id("me_wireless_connector"),
                    new ItemStack(CONNECTOR.get()), "ABA", "B B", "ABA",
                    'A', CustomTags.EV_CIRCUITS, 'B', wirelessConnect);
        }
        VanillaRecipeHelper.addShapedRecipe(provider, true, TypeOmegaMod.id("me_wireless_configurator"),
                new ItemStack(CONFIGURATOR.get()), " A ", "BCB", " D ",
                'A', AEItems.WIRELESS_RECEIVER.asItem(), 'B',
                new com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry(TagPrefix.ingot,
                        com.gregtechceu.gtceu.common.data.GTMaterials.Iron),
                'C', CustomTags.EV_CIRCUITS, 'D', AEItems.ENGINEERING_PROCESSOR.asItem());
    }
}
