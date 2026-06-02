/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.util.datafix.fixes.ItemStackComponentizationFix
 *  net.minecraft.util.datafix.fixes.ItemStackComponentizationFix$ItemStackData
 *  net.minecraft.world.item.DyeColor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.simibubi.create.foundation.mixin.datafixer;

import com.mojang.serialization.Dynamic;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemStackComponentizationFix.class})
public class ItemStackComponentizationFixMixin {
    @Inject(method={"fixItemStack"}, at={@At(value="TAIL")})
    private static void create$fixItemsAndTranslateNBTIntoComponents(ItemStackComponentizationFix.ItemStackData stack, Dynamic<?> dynamic, CallbackInfo ci) {
        stack.removeTag("SequencedAssembly").result().ifPresent(d -> {
            float progress = d.get("Progress").asFloat(0.0f);
            String id = (String)d.get("id").asString().getOrThrow();
            int step = d.get("Step").asInt(1);
            Dynamic seqDynamic = dynamic.emptyMap();
            seqDynamic = seqDynamic.set("progress", dynamic.createFloat(progress));
            seqDynamic = seqDynamic.set("step", dynamic.createInt(step));
            seqDynamic = seqDynamic.set("id", dynamic.createString(id));
            stack.setComponent("create:sequenced_assembly", seqDynamic);
        });
        if (stack.is("create:copper_backtank") || stack.is("create:netherite_backtank")) {
            stack.moveTagToComponent("Air", "create:banktank_air");
        }
        if (stack.is("create:belt_connector")) {
            stack.moveTagToComponent("FirstPulley", "create:belt_first_shaft");
        }
        if (stack.is("create:handheld_worldshaper")) {
            ItemStackComponentizationFixMixin.create$moveTagToEnumComponent(stack, "Pattern", "create:placement_pattern", PlacementPatterns.class);
            ItemStackComponentizationFixMixin.create$moveTagToEnumComponent(stack, "Brush", "create:shaper_brush", TerrainBrushes.class);
            stack.moveTagToComponent("BrushParams", "create:shaper_brush_params");
            ItemStackComponentizationFixMixin.create$moveTagToEnumComponent(stack, "Placement", "create:shaper_placement_options", PlacementOptions.class);
            ItemStackComponentizationFixMixin.create$moveTagToEnumComponent(stack, "Tool", "create:shaper_tool", TerrainTools.class);
            stack.moveTagToComponent("BlockUsed", "create:shaper_block_used");
            stack.moveTagToComponent("_Swap", "create:shaper_swap");
            stack.moveTagToComponent("BlockData", "create:shaper_block_data");
        }
        if (stack.is("create:filter")) {
            ItemStackComponentizationFixMixin.create$moveItemStackHandlerToItemContainerContents(stack, dynamic, "Items", "create:filter_items");
            stack.moveTagToComponent("RespectNBT", "create:filter_items_respect_nbt");
            stack.moveTagToComponent("Blacklist", "create:filter_items_blacklist");
        }
        if (stack.is("create:attribute_filter")) {
            ItemStackComponentizationFixMixin.create$moveTagToEnumComponent(stack, "WhitelistMode", "create:filter_items", AttributeFilterWhitelistMode.class);
        }
        if (stack.is("create:clipboard")) {
            ItemStackComponentizationFixMixin.create$moveTagToEnumComponent(stack, "Type", "create:clipboard_type", ClipboardOverrides.ClipboardType.class);
            stack.removeTag("Readonly").result().ifPresent(itemDynamic -> stack.setComponent("create:clipboard_read_only", itemDynamic.emptyMap().createString("instance")));
            stack.moveTagToComponent("CopiedValues", "create:clipboard_copied_values");
            stack.moveTagToComponent("Pages", "create:clipboard_pages");
        }
        if (stack.is("create:track")) {
            stack.moveTagToComponent("ConnectingFrom", "create:track_connecting_from");
            stack.moveTagToComponent("ExtendCurve", "create:track_extended_curve");
        }
        if (stack.is(Set.of("create:track_station", "create:track_signal", "create:track_observer"))) {
            stack.moveTagToComponent("SelectedPos", "create:track_targeting_item_selected_pos");
            stack.moveTagToComponent("SelectedDirection", "create:track_targeting_item_selected_direction");
            stack.moveTagToComponent("Bezier", "track_targeting_item_bezier");
        }
        if (stack.is("create:schematic")) {
            stack.moveTagToComponent("Deployed", "create:schematic_deployed");
            stack.moveTagToComponent("Owner", "create:schematic_owner");
            stack.moveTagToComponent("File", "create:schematic_file");
            stack.moveTagToComponent("Anchor", "create:schematic_anchor");
            stack.moveTagToComponent("Rotation", "create:schematic_rotation");
            stack.moveTagToComponent("Mirror", "create:schematic_mirror");
            stack.moveTagToComponent("Bounds", "create:schematic_bounds");
        }
        if (stack.is("create:chromatic_compound")) {
            stack.moveTagToComponent("CollectingLight", "create:chromatic_compound_collecting_light");
        }
        if (stack.is(Set.of("create:sand_paper", "create:red_sand_paper"))) {
            stack.moveTagToComponent("Polishing", "create:sand_paper_polishing");
        }
        if (stack.is("create:minecart_contraption")) {
            stack.moveTagToComponent("Contraption", "create:minecart_contraption_data");
        }
        if (stack.is("create:linked_controller")) {
            ItemStackComponentizationFixMixin.create$moveItemStackHandlerToItemContainerContents(stack, dynamic, "Items", "create:linked_controller_items");
        }
        if (ItemStackComponentizationFixMixin.create$isCheckWithDyeColors(stack, "create:{}_toolbox")) {
            ItemStackComponentizationFixMixin.create$moveItemStackHandlerToItemContainerContents(stack, dynamic, "Inventory", "create:toolbox_inventory");
            stack.moveTagToComponent("UniqueId", "create:toolbox_uuid");
        }
        if (stack.is("create:schedule")) {
            stack.moveTagToComponent("Schedule", "create:train_schedule");
        }
        if (stack.is("create:display_link")) {
            stack.moveTagToComponent("SelectedPos", "create:display_link_selected_pos");
        }
        if (stack.is("create:schematicannon")) {
            stack.moveTagToComponent("BlockEntityTag.Options", "create:schematicannon_options");
        }
    }

    @Unique
    private static boolean create$isCheckWithDyeColors(ItemStackComponentizationFix.ItemStackData stack, String template) {
        HashSet<String> ids = new HashSet<String>();
        for (DyeColor dyeColor : DyeColor.values()) {
            ids.add(template.replace("{}", dyeColor.getName()));
        }
        return stack.is(ids);
    }

    @Unique
    private static <T extends Enum<?>> void create$moveTagToEnumComponent(ItemStackComponentizationFix.ItemStackData stack, String key, String component, Class<T> enumClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("moveTagToEnumComponent must be called with a enum class!");
        }
        Enum[] enumConstants = (Enum[])enumClass.getEnumConstants();
        stack.removeTag(key).result().ifPresent(itemDynamic -> {
            Enum enumConstant = enumConstants[itemDynamic.asInt(0)];
            if (!(enumConstant instanceof StringRepresentable)) {
                throw new IllegalArgumentException("moveTagToEnumComponent must be called with a enum class that implements StringRepresentable!");
            }
            StringRepresentable stringRepresentable = (StringRepresentable)enumConstant;
            String enumConstantName = stringRepresentable.getSerializedName();
            stack.setComponent(component, itemDynamic.emptyMap().createString(enumConstantName));
        });
    }

    @Unique
    private static void create$moveItemStackHandlerToItemContainerContents(ItemStackComponentizationFix.ItemStackData stack, Dynamic<?> dynamic, String key, String component) {
        try {
            stack.removeTag(key).result().ifPresent(itemDynamic -> {
                ArrayList list = new ArrayList();
                Stream stream = itemDynamic.get("Items").asStream();
                stream.forEach(d -> {
                    int slot = d.get("Slot").asInt(0);
                    String id = (String)d.get("id").asString().getOrThrow();
                    int count = d.get("Count").asInt(1);
                    Dynamic dynamicMain = dynamic.emptyMap();
                    Dynamic dynamicItem = dynamic.emptyMap();
                    dynamicItem = dynamicItem.set("count", dynamic.createInt(count));
                    dynamicItem = dynamicItem.set("id", dynamic.createString(id));
                    dynamicMain = dynamicMain.set("item", dynamicItem);
                    dynamicMain = dynamicMain.set("slot", dynamic.createInt(slot));
                    list.add(dynamicMain);
                });
                stack.setComponent(component, dynamic.createList(list.stream()));
            });
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}
