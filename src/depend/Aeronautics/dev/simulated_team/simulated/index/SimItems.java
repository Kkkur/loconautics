/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem
 *  com.simibubi.create.foundation.data.AssetLookup
 *  com.simibubi.create.foundation.data.CreateRegistrate
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.entry.ItemEntry
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.Tags$Items
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.diagram.DiagramItem;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueItem;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItem;
import dev.simulated_team.simulated.content.items.rope.RopeItem.RopeItem;
import dev.simulated_team.simulated.content.items.spring.SpringItem;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItem;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.registrate.simulated_tab.CreativeTabItemTransforms;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public class SimItems {
    public static final SimulatedRegistrate REGISTRATE = Simulated.getRegistrate();
    public static final ItemEntry<DiagramItem> CONTRAPTION_DIAGRAM = REGISTRATE.item("contraption_diagram", DiagramItem::new).recipe((c, p) -> ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).requires((ItemLike)Items.PAPER).requires((ItemLike)SimBlocks.PHYSICS_ASSEMBLER.get()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)SimBlocks.PHYSICS_ASSEMBLER.get()))).save((RecipeOutput)p)).register();
    public static final ItemEntry<SpringItem> SPRING = REGISTRATE.item("spring", SpringItem::new).recipe((ctx, prov) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ctx.get()), (int)2).pattern("S").pattern("N").pattern("S").define(Character.valueOf('S'), (ItemLike)AllItems.IRON_SHEET).define(Character.valueOf('N'), (ItemLike)Items.IRON_NUGGET).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)AllItems.IRON_SHEET)).save((RecipeOutput)prov)).register();
    public static ItemEntry<RopeItem> ROPE_COUPLING = REGISTRATE.item("rope_coupling", RopeItem::new).recipe((ctx, prov) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ctx.get()), (int)1).pattern(" S ").pattern("NSN").pattern(" S ").define(Character.valueOf('S'), Tags.Items.STRINGS).define(Character.valueOf('N'), Tags.Items.NUGGETS_IRON).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((TagKey)Tags.Items.STRINGS)).save((RecipeOutput)prov)).register();
    public static ItemEntry<Item> GYRO_MECHANISM = SimItems.ingredient("gyroscopic_mechanism");
    public static ItemEntry<SequencedAssemblyItem> INCOMPLETE_GYRO_MECHANISM = ((ItemBuilder)REGISTRATE.item("incomplete_gyroscopic_mechanism", SequencedAssemblyItem::new).transform(CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    public static final ItemEntry<Item> ENGINE_ASSEMBLY = SimItems.ingredient("engine_assembly");
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_ENGINE_ASSEMBLY = ((ItemBuilder)REGISTRATE.item("incomplete_engine_assembly", SequencedAssemblyItem::new).transform(CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    public static final ItemEntry<HoneyGlueItem> HONEY_GLUE;
    public static final ItemEntry<PhysicsStaffItem> PHYSICS_STAFF;
    public static final ItemEntry<PlungerLauncherItem> PLUNGER_LAUNCHER;

    private static ItemEntry<Item> ingredient(String name) {
        return REGISTRATE.item(name, Item::new).register();
    }

    private static ItemBuilder<Item, CreateRegistrate> ingredientNoRegister(String name) {
        return REGISTRATE.item(name, Item::new);
    }

    public static void register() {
    }

    static {
        REGISTRATE.addExtraItem(ResourceLocation.withDefaultNamespace((String)"slime_ball"));
        HONEY_GLUE = REGISTRATE.item("honey_glue", HoneyGlueItem::new).properties(p -> p.stacksTo(1).durability(100)).tag(new TagKey[]{ItemTags.DURABILITY_ENCHANTABLE}).register();
        PHYSICS_STAFF = REGISTRATE.item("creative_physics_staff", PhysicsStaffItem::new).properties(p -> p.rarity(Rarity.EPIC).stacksTo(1)).model(AssetLookup.itemModelWithPartials()).register();
        PLUNGER_LAUNCHER = REGISTRATE.item("plunger_launcher", PlungerLauncherItem::new).properties(p -> p.stacksTo(1).durability(200)).model(AssetLookup.itemModelWithPartials()).tag(new TagKey[]{Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE}).register();
    }
}
