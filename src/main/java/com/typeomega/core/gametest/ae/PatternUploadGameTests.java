package com.typeomega.core.gametest.ae;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.menu.SlotSemantics;
import appeng.parts.encoding.EncodingMode;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.integration.ae.upload.PatternUploader;
import com.typeomega.core.integration.ae.upload.PendingKey;
import com.typeomega.core.integration.ae.upload.ProviderMatcher;
import com.typeomega.core.integration.ae.upload.RecipeKey;
import com.typeomega.core.integration.ae.upload.RecipeKeys;
import com.typeomega.core.integration.ae.upload.UploadRequestHolder;

import java.util.List;

/**
 * Automatic pattern upload (D-442): a processing pattern encoded from a GregTech recipe lands in the provider named for
 * the recipe type; with no provider, several, or a full one it stays in the terminal; a crafting pattern never goes.
 * The tests press Encode on the server with the key the client would have sent; the grid is a real powered network.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class PatternUploadGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private PatternUploadGameTests() {}

    // EN: Built when a test runs, not when the class loads: the machine names exist once the language is loaded.
    // PT: Montado quando o teste roda, não quando a classe carrega: os nomes das máquinas existem após o idioma
    // carregar.
    // ZH: 在测试运行时构建而非类加载时：语言加载完成后才有机器名称。
    private static RecipeKey type(GTRecipeType type) {
        var key = RecipeKeys.ofGtType(type);
        if (key == null) throw new AssertionError("no name for the recipe type " + type.registryName);
        return key;
    }

    private static RecipeKey assembler() {
        return type(GTRecipeTypes.ASSEMBLER_RECIPES);
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void gtPatternGoesToTheProviderOfItsRecipeType(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Assembler", "Centrifuge");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            int blanks = rig.blanks();
            rig.encode(assembler());
            h.assertTrue(rig.patternsIn(0) == 1, "the Assembler provider holds " + rig.patternsIn(0) + " patterns");
            h.assertTrue(rig.patternsIn(1) == 0, "the Centrifuge provider got a pattern it was not meant for");
            h.assertTrue(rig.encodedSlot().isEmpty(), "the pattern is still in the terminal's output slot");
            h.assertTrue(rig.blanks() == blanks - 1, "the blank pattern was not used: " + rig.blanks());
            var inv = ((appeng.helpers.patternprovider.PatternContainer) rig.providers[0])
                    .getTerminalPatternInventory();
            h.assertTrue(PatternDetailsHelper.decodePattern(inv.getStackInSlot(0), h.getLevel()) != null,
                    "what the provider holds is not a pattern");
        }).thenExecute(() -> {
            rig.arm(Items.COPPER_INGOT, Items.GOLD_NUGGET);
            rig.encode(type(GTRecipeTypes.CENTRIFUGE_RECIPES));
            h.assertTrue(rig.patternsIn(0) == 1 && rig.patternsIn(1) == 1,
                    "a second recipe type must go to its own provider: " + rig.patternsIn(0) + "/" + rig.patternsIn(1));
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void noProviderForTheRecipeTypeKeepsThePattern(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Assembler", "Centrifuge");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            rig.encode(type(GTRecipeTypes.BLAST_RECIPES));
            h.assertTrue(rig.patternsIn(0) + rig.patternsIn(1) == 0, "a pattern went to a provider of another type");
            h.assertTrue(!rig.encodedSlot().isEmpty() && PatternDetailsHelper.isEncodedPattern(rig.encodedSlot()),
                    "the pattern is not in the terminal's output slot");
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void severalProvidersForTheTypeKeepThePattern(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Basic Alloy Smelter", "Advanced Alloy Smelter");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            rig.encode(type(GTRecipeTypes.ALLOY_SMELTER_RECIPES));
            h.assertTrue(rig.patternsIn(0) + rig.patternsIn(1) == 0,
                    "a pattern was sent although two providers fit the type");
            h.assertTrue(!rig.encodedSlot().isEmpty(), "the pattern left the terminal");
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void anExactNameBeatsLongerNames(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Basic Assembler", "Assembler");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            rig.encode(assembler());
            h.assertTrue(rig.patternsIn(1) == 1 && rig.patternsIn(0) == 0,
                    "the provider named exactly 'Assembler' should take it: " + rig.patternsIn(0) + "/" +
                            rig.patternsIn(1));
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void aFullProviderPassesToItsTwinAndThenTheTerminalKeepsIt(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Assembler", "Assembler");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            var inv = ((appeng.helpers.patternprovider.PatternContainer) rig.providers[0])
                    .getTerminalPatternInventory();
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            for (int i = 0; i < inv.size(); i++) {
                rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
                var filler = new ItemStack(Items.STONE);
                rig.terminal.getLogic().getEncodedOutputInv().setStack(0,
                        new appeng.api.stacks.GenericStack(appeng.api.stacks.AEItemKey.of(filler), i + 2));
                rig.encode(null);
                inv.setItemDirect(i, rig.encodedSlot().copy());
                rig.menu.getSlots(SlotSemantics.ENCODED_PATTERN).get(0).set(ItemStack.EMPTY);
            }
            h.assertTrue(rig.patternsIn(0) == rig.capacityOf(0), "the first provider is not full");
            rig.arm(Items.COPPER_INGOT, Items.GOLD_NUGGET);
            rig.encode(assembler());
            h.assertTrue(rig.patternsIn(1) == 1 && rig.encodedSlot().isEmpty(),
                    "the pattern should go to the provider with the same name that still has room");
            for (int i = 0; i < rig.capacityOf(1) - 1; i++) {
                rig.arm(Items.COPPER_INGOT, Items.GOLD_NUGGET);
                rig.terminal.getLogic().getEncodedOutputInv().setStack(0,
                        new appeng.api.stacks.GenericStack(appeng.api.stacks.AEItemKey.of(new ItemStack(Items.DIRT)),
                                i + 2));
                rig.encode(assembler());
            }
            h.assertTrue(rig.patternsIn(1) == rig.capacityOf(1),
                    "the second provider did not fill: " + rig.patternsIn(1));
            rig.arm(Items.COPPER_INGOT, Items.GOLD_NUGGET);
            rig.terminal.getLogic().getEncodedOutputInv().setStack(0,
                    new appeng.api.stacks.GenericStack(appeng.api.stacks.AEItemKey.of(new ItemStack(Items.SAND)), 9));
            rig.encode(assembler());
            h.assertTrue(!rig.encodedSlot().isEmpty(),
                    "with both providers full the pattern must stay in the terminal");
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void theSamePatternIsNotUploadedTwice(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Assembler");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            rig.encode(assembler());
            h.assertTrue(rig.patternsIn(0) == 1, "the first copy did not go");
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            rig.encode(assembler());
            h.assertTrue(rig.patternsIn(0) == 1, "the provider now holds " + rig.patternsIn(0) + " copies");
            h.assertTrue(!rig.encodedSlot().isEmpty(), "the second copy must stay in the terminal");
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void withoutAKeyOrAfterUsingItNothingIsUploaded(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Assembler");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            rig.arm(Items.IRON_INGOT, Items.GOLD_INGOT);
            rig.encode(null);
            h.assertTrue(rig.patternsIn(0) == 0 && !rig.encodedSlot().isEmpty(),
                    "a hand-made pattern (no key) must stay in the terminal");
            rig.menu.getSlots(SlotSemantics.ENCODED_PATTERN).get(0).set(ItemStack.EMPTY);
            ((UploadRequestHolder) rig.menu).typeomega$setUploadKey(assembler());
            rig.arm(Items.COPPER_INGOT, Items.GOLD_NUGGET);
            rig.menu.encode();
            h.assertTrue(rig.patternsIn(0) == 1, "the keyed encode did not upload");
            rig.arm(Items.STONE, Items.GOLD_NUGGET);
            rig.menu.encode();
            h.assertTrue(rig.patternsIn(0) == 1 && !rig.encodedSlot().isEmpty(),
                    "the key was used twice: the next encode must stay in the terminal");
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void aCraftingPatternIsNeverUploaded(GameTestHelper h) {
        var rig = new UploadTestRig(h, "Assembler", "Crafting");
        h.startSequence().thenWaitUntil(rig::ready).thenExecute(() -> {
            var recipe = (CraftingRecipe) h.getLevel().getRecipeManager().byKey(new ResourceLocation("minecraft:stick"))
                    .orElseThrow();
            var planks = new ItemStack(Items.OAK_PLANKS);
            var ingredients = new ItemStack[9];
            java.util.Arrays.fill(ingredients, ItemStack.EMPTY);
            ingredients[0] = planks;
            ingredients[3] = planks;
            var pattern = PatternDetailsHelper.encodeCraftingPattern(recipe, ingredients, new ItemStack(Items.STICK, 4),
                    false, true);
            var slot = rig.menu.getSlots(SlotSemantics.ENCODED_PATTERN).get(0);
            slot.set(pattern);
            var outcome = PatternUploader.upload(rig.player, rig.menu, slot, assembler());
            h.assertTrue(outcome == PatternUploader.Outcome.NOT_PROCESSING,
                    "a crafting pattern got outcome " + outcome);
            var named = new RecipeKey(List.of(RecipeKey.Candidate.of(Component.literal("Crafting"))));
            h.assertTrue(
                    PatternUploader.upload(rig.player, rig.menu, slot, named) == PatternUploader.Outcome.NOT_PROCESSING,
                    "a crafting pattern went to a provider called 'Crafting'");
            h.assertTrue(rig.patternsIn(0) + rig.patternsIn(1) == 0 && !slot.getItem().isEmpty(),
                    "the crafting pattern left the terminal");
            rig.menu.setMode(EncodingMode.CRAFTING);
            rig.terminal.getLogic().setMode(EncodingMode.CRAFTING);
            rig.encode(assembler());
            h.assertTrue(rig.patternsIn(0) + rig.patternsIn(1) == 0, "an encode in crafting mode reached a provider");
        }).thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void theKeyOfAGregTechRecipeIsItsMachineName(GameTestHelper h) {
        h.assertTrue("Assembler".equals(assembler().primaryText()), "assembler key: " + assembler().primaryText());
        h.assertTrue("Electric Blast Furnace".equals(type(GTRecipeTypes.BLAST_RECIPES).primaryText()),
                "blast furnace key: " + type(GTRecipeTypes.BLAST_RECIPES).primaryText());
        var recipes = h.getLevel().getRecipeManager().getAllRecipesFor(GTRecipeTypes.ASSEMBLER_RECIPES);
        h.assertTrue(!recipes.isEmpty(), "no assembler recipe loaded");
        var fromRecipe = RecipeKeys.of(recipes.get(0), recipes.get(0));
        h.assertTrue(fromRecipe != null && "Assembler".equals(fromRecipe.primaryText()),
                "the key of an assembler recipe: " + fromRecipe);
        h.assertTrue(RecipeKeys.of(null, null) == null, "a key came out of nothing");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void theMatcherReadsNamesTheWayThePlayerSeesThem(GameTestHelper h) {
        List<String> names = List.of("Basic Assembler §r", "Centrifuge", "Furnace", "Electric Furnace");
        var found = ProviderMatcher.select(names, n -> n, assembler());
        h.assertTrue(found.kind() == ProviderMatcher.Kind.UNIQUE && found.items().equals(List.of("Basic Assembler §r")),
                "colour codes and case must not matter: " + found);
        var furnace = ProviderMatcher.select(names, n -> n, key("furnace"));
        h.assertTrue(furnace.kind() == ProviderMatcher.Kind.UNIQUE && furnace.name().equals("Furnace"),
                "the exact name should win over 'Electric Furnace': " + furnace);
        h.assertTrue(ProviderMatcher.select(names, n -> n, key("electric")).kind() == ProviderMatcher.Kind.UNIQUE,
                "one name holds 'electric'");
        h.assertTrue(ProviderMatcher.select(List.of("Alloy A", "Alloy B"), n -> n, key("alloy")).kind() ==
                ProviderMatcher.Kind.AMBIGUOUS, "two names hold 'alloy'");
        h.assertTrue(ProviderMatcher.select(names, n -> n, key("macerator")).kind() == ProviderMatcher.Kind.NONE,
                "nothing holds 'macerator'");
        var translated = new RecipeKey(List.of(new RecipeKey.Candidate(Component.translatable("no.such.key.typeomega"),
                "montadora")));
        h.assertTrue(ProviderMatcher.select(List.of("Montadora Rápida"), n -> n, translated).kind() ==
                ProviderMatcher.Kind.UNIQUE, "the text the client rendered must match too");
        var ordered = new RecipeKey(List.of(RecipeKey.Candidate.of(Component.literal("Assembler")),
                RecipeKey.Candidate.of(Component.literal("Basic"))));
        h.assertTrue(ProviderMatcher.select(List.of("Basic Assembler", "Basic Mixer"), n -> n, ordered).kind() ==
                ProviderMatcher.Kind.UNIQUE, "the most specific recipe name decides");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void theKeyTravelsInAPacketAndTheMenuCarriesIt(GameTestHelper h) {
        var buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        var key = new RecipeKey(List.of(RecipeKey.Candidate.of(Component.translatable("gtceu.assembler")),
                new RecipeKey.Candidate(Component.literal("x"), "montadora")));
        key.write(buf);
        var back = RecipeKey.read(buf);
        h.assertTrue(back.candidates().size() == 2 && back.primaryText().equals(key.primaryText()) &&
                back.candidates().get(1).text().equals("montadora"), "the key changed on the wire: " + back);
        var tooMany = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        tooMany.writeVarInt(RecipeKey.MAX_CANDIDATES + 1);
        boolean refused = false;
        try {
            RecipeKey.read(tooMany);
        } catch (RuntimeException e) {
            refused = true;
        }
        h.assertTrue(refused, "a key of too many names was accepted");

        var rig = new UploadTestRig(h);
        h.assertTrue(rig.menu instanceof UploadRequestHolder,
                "the encoding menu does not carry the upload key (mixin)");
        var holder = (UploadRequestHolder) rig.menu;
        holder.typeomega$setUploadKey(key);
        h.assertTrue(holder.typeomega$takeUploadKey() == key && holder.typeomega$takeUploadKey() == null,
                "the key can be taken only once");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void aNotedRecipeBelongsToTheTerminalItWasMovedInto(GameTestHelper h) {
        PendingKey.set(5, assembler());
        h.assertTrue(PendingKey.take(6) == null && PendingKey.take(PendingKey.WCWT) == null,
                "another menu or terminal took the key");
        h.assertTrue(PendingKey.take(5) != null, "the owner could not take its key");
        h.assertTrue(PendingKey.take(5) == null, "the key can be taken only once");
        PendingKey.set(5, assembler());
        PendingKey.set(5, null);
        h.assertTrue(PendingKey.take(5) == null, "a crafting transfer must drop the key of the last one");
        PendingKey.clear();
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void theWcwtHookNotesTheRecipeForItsProviderSearch(GameTestHelper h) throws ReflectiveOperationException {
        if (!ModList.get().isLoaded("wcwt")) {
            TypeOmegaMod.LOGGER.info("AE2 WCWT is not loaded: its upload hook is not checked in this run");
            h.succeed();
            return;
        }
        var resolver = Class.forName("com.lhy.wcwt.compat.WcwtRecipeSearchKeyResolver");
        var update = resolver.getMethod("updateEaepProviderSearchKey", Object.class,
                net.minecraft.world.item.crafting.Recipe.class,
                EncodingMode.class);
        var gt = h.getLevel().getRecipeManager().getAllRecipesFor(GTRecipeTypes.ASSEMBLER_RECIPES).get(0);
        PendingKey.clear();
        update.invoke(null, gt, gt, EncodingMode.PROCESSING);
        var key = PendingKey.take(PendingKey.WCWT);
        h.assertTrue(key != null && "Assembler".equals(key.primaryText()),
                "WCWT did not note the GregTech recipe: " + key);
        h.assertTrue(PendingKey.take(PendingKey.WCWT) == null, "the key can be taken only once");
        update.invoke(null, gt, gt, EncodingMode.PROCESSING);
        update.invoke(null, gt, gt, EncodingMode.CRAFTING);
        h.assertTrue(PendingKey.take(PendingKey.WCWT) == null,
                "a crafting transfer must drop the recipe of the last one");
        h.succeed();
    }

    private static RecipeKey key(String name) {
        return new RecipeKey(List.of(RecipeKey.Candidate.of(Component.literal(name))));
    }
}
