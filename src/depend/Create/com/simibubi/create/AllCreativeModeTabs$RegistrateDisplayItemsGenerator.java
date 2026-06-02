/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.entry.ItemEntry
 *  com.tterrag.registrate.util.entry.ItemProviderEntry
 *  com.tterrag.registrate.util.entry.RegistryEntry
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ReferenceArrayList
 *  it.unimi.dsi.fastutil.objects.ReferenceCollection
 *  it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator
 *  net.minecraft.world.item.CreativeModeTab$ItemDisplayParameters
 *  net.minecraft.world.item.CreativeModeTab$Output
 *  net.minecraft.world.item.CreativeModeTab$TabVisibility
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package com.simibubi.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.gearbox.VerticalGearboxItem;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.mutable.MutableObject;

private static class AllCreativeModeTabs.RegistrateDisplayItemsGenerator
implements CreativeModeTab.DisplayItemsGenerator {
    private static final Predicate<Item> IS_ITEM_3D_PREDICATE;
    private final boolean addItems;
    private final DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter;

    public AllCreativeModeTabs.RegistrateDisplayItemsGenerator(boolean addItems, DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter) {
        this.addItems = addItems;
        this.tabFilter = tabFilter;
    }

    private static Predicate<Item> makeExclusionPredicate() {
        ReferenceOpenHashSet exclusions = new ReferenceOpenHashSet();
        List<ItemProviderEntry> simpleExclusions = List.of(AllItems.INCOMPLETE_PRECISION_MECHANISM, AllItems.INCOMPLETE_REINFORCED_SHEET, AllItems.INCOMPLETE_TRACK, AllItems.CHROMATIC_COMPOUND, AllItems.SHADOW_STEEL, AllItems.REFINED_RADIANCE, AllItems.COPPER_BACKTANK_PLACEABLE, AllItems.NETHERITE_BACKTANK_PLACEABLE, AllItems.MINECART_CONTRAPTION, AllItems.FURNACE_MINECART_CONTRAPTION, AllItems.CHEST_MINECART_CONTRAPTION, AllItems.SCHEMATIC, AllItems.SHOPPING_LIST, AllBlocks.ANDESITE_ENCASED_SHAFT, AllBlocks.BRASS_ENCASED_SHAFT, AllBlocks.ANDESITE_ENCASED_COGWHEEL, AllBlocks.BRASS_ENCASED_COGWHEEL, AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL, AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL, AllBlocks.MYSTERIOUS_CUCKOO_CLOCK, AllBlocks.ELEVATOR_CONTACT, AllBlocks.SHADOW_STEEL_CASING, AllBlocks.REFINED_RADIANCE_CASING);
        List<ItemEntry<TagDependentIngredientItem>> tagDependentExclusions = List.of(AllItems.CRUSHED_OSMIUM, AllItems.CRUSHED_PLATINUM, AllItems.CRUSHED_SILVER, AllItems.CRUSHED_TIN, AllItems.CRUSHED_LEAD, AllItems.CRUSHED_QUICKSILVER, AllItems.CRUSHED_BAUXITE, AllItems.CRUSHED_URANIUM, AllItems.CRUSHED_NICKEL);
        exclusions.addAll(PackageStyles.RARE_BOXES);
        for (ItemProviderEntry itemProviderEntry : simpleExclusions) {
            exclusions.add(itemProviderEntry.asItem());
        }
        for (ItemEntry itemEntry : tagDependentExclusions) {
            TagDependentIngredientItem item = (TagDependentIngredientItem)((Object)itemEntry.get());
            if (!item.shouldHide()) continue;
            exclusions.add(itemEntry.asItem());
        }
        return ((Set)exclusions)::contains;
    }

    private static List<ItemOrdering> makeOrderings() {
        ReferenceArrayList orderings = new ReferenceArrayList();
        Map<ItemEntry<ScheduleItem>, BlockEntry<StationBlock>> simpleBeforeOrderings = Map.of(AllItems.EMPTY_BLAZE_BURNER, AllBlocks.BLAZE_BURNER, AllItems.SCHEDULE, AllBlocks.TRACK_STATION);
        Map<ItemEntry<VerticalGearboxItem>, BlockEntry<GearboxBlock>> simpleAfterOrderings = Map.of(AllItems.VERTICAL_GEARBOX, AllBlocks.GEARBOX);
        simpleBeforeOrderings.forEach((arg_0, arg_1) -> AllCreativeModeTabs.RegistrateDisplayItemsGenerator.lambda$makeOrderings$2((List)orderings, arg_0, arg_1));
        simpleAfterOrderings.forEach((arg_0, arg_1) -> AllCreativeModeTabs.RegistrateDisplayItemsGenerator.lambda$makeOrderings$3((List)orderings, arg_0, arg_1));
        PackageStyles.STANDARD_BOXES.forEach(arg_0 -> AllCreativeModeTabs.RegistrateDisplayItemsGenerator.lambda$makeOrderings$4((List)orderings, arg_0));
        return orderings;
    }

    private static Function<Item, ItemStack> makeStackFunc() {
        Reference2ReferenceOpenHashMap factories = new Reference2ReferenceOpenHashMap();
        Map<ItemEntry<? extends BacktankItem>, Function<Item, ItemStack>> simpleFactories = Map.of(AllItems.COPPER_BACKTANK, item -> {
            ItemStack stack = new ItemStack((ItemLike)item);
            stack.set(AllDataComponents.BACKTANK_AIR, (Object)BacktankUtil.maxAirWithoutEnchants());
            return stack;
        }, AllItems.NETHERITE_BACKTANK, item -> {
            ItemStack stack = new ItemStack((ItemLike)item);
            stack.set(AllDataComponents.BACKTANK_AIR, (Object)BacktankUtil.maxAirWithoutEnchants());
            return stack;
        });
        simpleFactories.forEach((arg_0, arg_1) -> AllCreativeModeTabs.RegistrateDisplayItemsGenerator.lambda$makeStackFunc$7((Map)factories, arg_0, arg_1));
        return arg_0 -> AllCreativeModeTabs.RegistrateDisplayItemsGenerator.lambda$makeStackFunc$8((Map)factories, arg_0);
    }

    private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
        Object block;
        Reference2ObjectOpenHashMap visibilities = new Reference2ObjectOpenHashMap();
        Map<ItemEntry<Item>, CreativeModeTab.TabVisibility> simpleVisibilities = Map.of(AllItems.BLAZE_CAKE_BASE, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        simpleVisibilities.forEach((arg_0, arg_1) -> AllCreativeModeTabs.RegistrateDisplayItemsGenerator.lambda$makeVisibilityFunc$9((Map)visibilities, arg_0, arg_1));
        for (BlockEntry<ValveHandleBlock> blockEntry : AllBlocks.DYED_VALVE_HANDLES) {
            visibilities.put(blockEntry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
        for (BlockEntry blockEntry : AllBlocks.SEATS) {
            block = (SeatBlock)blockEntry.get();
            if (((SeatBlock)block).getColor() == DyeColor.RED) continue;
            visibilities.put(blockEntry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
        for (BlockEntry blockEntry : AllBlocks.TABLE_CLOTHS) {
            block = (TableClothBlock)blockEntry.get();
            if (((TableClothBlock)block).getColor() == DyeColor.RED) continue;
            visibilities.put(blockEntry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
        for (BlockEntry blockEntry : AllBlocks.PACKAGE_POSTBOXES) {
            block = (PostboxBlock)blockEntry.get();
            if (((PostboxBlock)block).getColor() == DyeColor.WHITE) continue;
            visibilities.put(blockEntry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
        for (BlockEntry blockEntry : AllBlocks.TOOLBOXES) {
            block = (ToolboxBlock)blockEntry.get();
            if (((ToolboxBlock)block).getColor() == DyeColor.BROWN) continue;
            visibilities.put(blockEntry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
        return arg_0 -> AllCreativeModeTabs.RegistrateDisplayItemsGenerator.lambda$makeVisibilityFunc$10((Map)visibilities, arg_0);
    }

    public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        Predicate<Item> exclusionPredicate = AllCreativeModeTabs.RegistrateDisplayItemsGenerator.makeExclusionPredicate();
        List<ItemOrdering> orderings = AllCreativeModeTabs.RegistrateDisplayItemsGenerator.makeOrderings();
        Function<Item, ItemStack> stackFunc = AllCreativeModeTabs.RegistrateDisplayItemsGenerator.makeStackFunc();
        Function<Item, CreativeModeTab.TabVisibility> visibilityFunc = AllCreativeModeTabs.RegistrateDisplayItemsGenerator.makeVisibilityFunc();
        LinkedList<Item> items = new LinkedList<Item>();
        if (this.addItems) {
            items.addAll(this.collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE.negate())));
        }
        items.addAll(this.collectBlocks(exclusionPredicate));
        if (this.addItems) {
            items.addAll(this.collectItems(exclusionPredicate.or(IS_ITEM_3D_PREDICATE)));
        }
        AllCreativeModeTabs.RegistrateDisplayItemsGenerator.applyOrderings(items, orderings);
        AllCreativeModeTabs.RegistrateDisplayItemsGenerator.outputAll(output, items, stackFunc, visibilityFunc);
    }

    private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
        ReferenceArrayList items = new ReferenceArrayList();
        for (RegistryEntry entry : Create.registrate().getAll(Registries.BLOCK)) {
            Item item;
            if (!CreateRegistrate.isInCreativeTab(entry, this.tabFilter) || (item = ((Block)entry.get()).asItem()) == Items.AIR || exclusionPredicate.test(item)) continue;
            items.add(item);
        }
        items = new ReferenceArrayList((ReferenceCollection)new ReferenceLinkedOpenHashSet((Collection)items));
        return items;
    }

    private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
        ReferenceArrayList items = new ReferenceArrayList();
        for (RegistryEntry entry : Create.registrate().getAll(Registries.ITEM)) {
            Item item;
            if (!CreateRegistrate.isInCreativeTab(entry, this.tabFilter) || (item = (Item)entry.get()) instanceof BlockItem || exclusionPredicate.test(item)) continue;
            items.add(item);
        }
        return items;
    }

    private static void applyOrderings(List<Item> items, List<ItemOrdering> orderings) {
        for (ItemOrdering ordering : orderings) {
            int anchorIndex = items.indexOf(ordering.anchor());
            if (anchorIndex == -1) continue;
            Item item = ordering.item();
            int itemIndex = items.indexOf(item);
            if (itemIndex != -1) {
                items.remove(itemIndex);
                if (itemIndex < anchorIndex) {
                    --anchorIndex;
                }
            }
            if (ordering.type() == ItemOrdering.Type.AFTER) {
                items.add(anchorIndex + 1, item);
                continue;
            }
            items.add(anchorIndex, item);
        }
    }

    private static void outputAll(CreativeModeTab.Output output, List<Item> items, Function<Item, ItemStack> stackFunc, Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
        for (Item item : items) {
            output.accept(stackFunc.apply(item), visibilityFunc.apply(item));
        }
    }

    private static /* synthetic */ CreativeModeTab.TabVisibility lambda$makeVisibilityFunc$10(Map visibilities, Item item) {
        CreativeModeTab.TabVisibility visibility = (CreativeModeTab.TabVisibility)visibilities.get(item);
        if (visibility != null) {
            return visibility;
        }
        return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
    }

    private static /* synthetic */ void lambda$makeVisibilityFunc$9(Map visibilities, ItemProviderEntry entry, CreativeModeTab.TabVisibility factory) {
        visibilities.put(entry.asItem(), factory);
    }

    private static /* synthetic */ ItemStack lambda$makeStackFunc$8(Map factories, Item item) {
        Function factory = (Function)factories.get(item);
        if (factory != null) {
            return (ItemStack)factory.apply(item);
        }
        return new ItemStack((ItemLike)item);
    }

    private static /* synthetic */ void lambda$makeStackFunc$7(Map factories, ItemProviderEntry entry, Function factory) {
        factories.put(entry.asItem(), factory);
    }

    private static /* synthetic */ void lambda$makeOrderings$4(List orderings, PackageItem item) {
        if (RegisteredObjectsHelper.getKeyOrThrow((Item)item).getNamespace().equals("create")) {
            orderings.add(ItemOrdering.after(item, AllBlocks.PACKAGER.asItem()));
        }
    }

    private static /* synthetic */ void lambda$makeOrderings$3(List orderings, ItemProviderEntry entry, ItemProviderEntry otherEntry) {
        orderings.add(ItemOrdering.after(entry.asItem(), otherEntry.asItem()));
    }

    private static /* synthetic */ void lambda$makeOrderings$2(List orderings, ItemProviderEntry entry, ItemProviderEntry otherEntry) {
        orderings.add(ItemOrdering.before(entry.asItem(), otherEntry.asItem()));
    }

    static {
        MutableObject isItem3d = new MutableObject(item -> false);
        if (CatnipServices.PLATFORM.getEnv().isClient()) {
            isItem3d.setValue(item -> {
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                BakedModel model = itemRenderer.getModel(new ItemStack((ItemLike)item), null, null, 0);
                return model.isGui3d();
            });
        }
        IS_ITEM_3D_PREDICATE = (Predicate)isItem3d.getValue();
    }

    private record ItemOrdering(Item item, Item anchor, Type type) {
        public static ItemOrdering before(Item item, Item anchor) {
            return new ItemOrdering(item, anchor, Type.BEFORE);
        }

        public static ItemOrdering after(Item item, Item anchor) {
            return new ItemOrdering(item, anchor, Type.AFTER);
        }

        public static enum Type {
            BEFORE,
            AFTER;

        }
    }
}
