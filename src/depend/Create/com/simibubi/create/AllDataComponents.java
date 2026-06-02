/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.IdMap
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponentType$Builder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.util.Unit
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.DeferredRegister$DataComponents
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.simibubi.create;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import com.simibubi.create.content.equipment.symmetryWand.mirror.SymmetryMirror;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.redstone.displayLink.ClickToLinkBlockItem;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackPlacement;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class AllDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents((ResourceKey)Registries.DATA_COMPONENT_TYPE, (String)"create");
    public static final DataComponentType<Integer> BACKTANK_AIR = AllDataComponents.register("banktank_air", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<BlockPos> BELT_FIRST_SHAFT = AllDataComponents.register("belt_first_shaft", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DataComponentType<Boolean> INFERRED_FROM_RECIPE = AllDataComponents.register("inferred_from_recipe", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<PlacementPatterns> PLACEMENT_PATTERN = AllDataComponents.register("placement_pattern", builder -> builder.persistent(PlacementPatterns.CODEC).networkSynchronized(PlacementPatterns.STREAM_CODEC));
    public static final DataComponentType<TerrainBrushes> SHAPER_BRUSH = AllDataComponents.register("shaper_brush", builder -> builder.persistent(TerrainBrushes.CODEC).networkSynchronized(TerrainBrushes.STREAM_CODEC));
    public static final DataComponentType<BlockPos> SHAPER_BRUSH_PARAMS = AllDataComponents.register("shaper_brush_params", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DataComponentType<PlacementOptions> SHAPER_PLACEMENT_OPTIONS = AllDataComponents.register("shaper_placement_options", builder -> builder.persistent(PlacementOptions.CODEC).networkSynchronized(PlacementOptions.STREAM_CODEC));
    public static final DataComponentType<TerrainTools> SHAPER_TOOL = AllDataComponents.register("shaper_tool", builder -> builder.persistent(TerrainTools.CODEC).networkSynchronized(TerrainTools.STREAM_CODEC));
    public static final DataComponentType<BlockState> SHAPER_BLOCK_USED = AllDataComponents.register("shaper_block_used", builder -> builder.persistent(BlockState.CODEC).networkSynchronized(ByteBufCodecs.idMapper((IdMap)Block.BLOCK_STATE_REGISTRY)));
    public static final DataComponentType<Boolean> SHAPER_SWAP = AllDataComponents.register("shaper_swap", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<CompoundTag> SHAPER_BLOCK_DATA = AllDataComponents.register("shaper_block_data", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG));
    public static final DataComponentType<ItemContainerContents> FILTER_ITEMS = AllDataComponents.register("filter_items", builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));
    public static final DataComponentType<Boolean> FILTER_ITEMS_RESPECT_NBT = AllDataComponents.register("filter_items_respect_nbt", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<Boolean> FILTER_ITEMS_BLACKLIST = AllDataComponents.register("filter_items_blacklist", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<AttributeFilterWhitelistMode> ATTRIBUTE_FILTER_WHITELIST_MODE = AllDataComponents.register("attribute_filter_whitelist_mode", builder -> builder.persistent(AttributeFilterWhitelistMode.CODEC).networkSynchronized(AttributeFilterWhitelistMode.STREAM_CODEC));
    public static final DataComponentType<List<ItemAttribute.ItemAttributeEntry>> ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES = AllDataComponents.register("attribute_filter_matched_attributes", builder -> builder.persistent(ItemAttribute.ItemAttributeEntry.CODEC.listOf()).networkSynchronized(CatnipStreamCodecBuilders.list(ItemAttribute.ItemAttributeEntry.STREAM_CODEC)));
    public static final DataComponentType<ClipboardContent> CLIPBOARD_CONTENT = AllDataComponents.register("clipboard_content", builder -> builder.persistent(ClipboardContent.CODEC).networkSynchronized(ClipboardContent.STREAM_CODEC));
    public static final DataComponentType<TrackPlacement.ConnectingFrom> TRACK_CONNECTING_FROM = AllDataComponents.register("track_connecting_from", builder -> builder.persistent(TrackPlacement.ConnectingFrom.CODEC).networkSynchronized(TrackPlacement.ConnectingFrom.STREAM_CODEC));
    public static final DataComponentType<Boolean> TRACK_EXTENDED_CURVE = AllDataComponents.register("track_extend_curve", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<BlockPos> TRACK_TARGETING_ITEM_SELECTED_POS = AllDataComponents.register("track_targeting_item_selected_pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DataComponentType<Boolean> TRACK_TARGETING_ITEM_SELECTED_DIRECTION = AllDataComponents.register("track_targeting_item_selected_direction", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<BezierTrackPointLocation> TRACK_TARGETING_ITEM_BEZIER = AllDataComponents.register("track_targeting_item_bezier", builder -> builder.persistent(BezierTrackPointLocation.CODEC).networkSynchronized(BezierTrackPointLocation.STREAM_CODEC));
    public static final DataComponentType<Boolean> SCHEMATIC_DEPLOYED = AllDataComponents.register("schematic_deployed", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<String> SCHEMATIC_OWNER = AllDataComponents.register("schematic_owner", builder -> builder.persistent((Codec)Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DataComponentType<String> SCHEMATIC_FILE = AllDataComponents.register("schematic_file", builder -> builder.persistent((Codec)Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DataComponentType<BlockPos> SCHEMATIC_ANCHOR = AllDataComponents.register("schematic_anchor", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DataComponentType<Rotation> SCHEMATIC_ROTATION = AllDataComponents.register("schematic_rotation", builder -> builder.persistent(Rotation.CODEC).networkSynchronized(CatnipStreamCodecs.ROTATION));
    public static final DataComponentType<Mirror> SCHEMATIC_MIRROR = AllDataComponents.register("schematic_mirror", builder -> builder.persistent(Mirror.CODEC).networkSynchronized(CatnipStreamCodecs.MIRROR));
    public static final DataComponentType<Vec3i> SCHEMATIC_BOUNDS = AllDataComponents.register("schematic_bounds", builder -> builder.persistent(Vec3i.CODEC).networkSynchronized(CatnipStreamCodecs.VEC3I));
    public static final DataComponentType<Integer> SCHEMATIC_HASH = AllDataComponents.register("schematic_hash", builder -> builder.persistent((Codec)Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<Integer> CHROMATIC_COMPOUND_COLLECTING_LIGHT = AllDataComponents.register("chromatic_compound_collecting_light", builder -> builder.persistent((Codec)Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<SandPaperItemComponent> SAND_PAPER_POLISHING = AllDataComponents.register("sand_paper_polishing", builder -> builder.persistent(SandPaperItemComponent.CODEC).networkSynchronized(SandPaperItemComponent.STREAM_CODEC));
    public static final DataComponentType<Unit> SAND_PAPER_JEI = AllDataComponents.register("sand_paper_jei", builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit((Object)Unit.INSTANCE)));
    public static final DataComponentType<CompoundTag> MINECRAFT_CONTRAPTION_DATA = AllDataComponents.register("minecart_contraption_data", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG));
    public static final DataComponentType<ItemContainerContents> LINKED_CONTROLLER_ITEMS = AllDataComponents.register("linked_controller_items", builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));
    public static final DataComponentType<ToolboxInventory> TOOLBOX_INVENTORY = AllDataComponents.register("toolbox_inventory", builder -> builder.persistent(ToolboxInventory.BACKWARDS_COMPAT_CODEC).networkSynchronized(ToolboxInventory.STREAM_CODEC));
    public static final DataComponentType<UUID> TOOLBOX_UUID = AllDataComponents.register("toolbox_uuid", builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC));
    public static final DataComponentType<SequencedAssemblyRecipe.SequencedAssembly> SEQUENCED_ASSEMBLY = AllDataComponents.register("sequenced_assembly", builder -> builder.persistent(SequencedAssemblyRecipe.SequencedAssembly.CODEC).networkSynchronized(SequencedAssemblyRecipe.SequencedAssembly.STREAM_CODEC));
    public static final DataComponentType<CompoundTag> TRAIN_SCHEDULE = AllDataComponents.register("train_schedule", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG));
    public static final DataComponentType<SymmetryMirror> SYMMETRY_WAND = AllDataComponents.register("symmetry_wand", builder -> builder.persistent(SymmetryMirror.CODEC).networkSynchronized(SymmetryMirror.STREAM_CODEC));
    public static final DataComponentType<Boolean> SYMMETRY_WAND_ENABLE = AllDataComponents.register("symmetry_wand_enable", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<Boolean> SYMMETRY_WAND_SIMULATE = AllDataComponents.register("symmetry_wand_simulate", builder -> builder.persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DataComponentType<PotionFluid.BottleType> POTION_FLUID_BOTTLE_TYPE = AllDataComponents.register("potion_fluid_bottle_type", builder -> builder.persistent(PotionFluid.BottleType.CODEC).networkSynchronized(PotionFluid.BottleType.STREAM_CODEC));
    public static final DataComponentType<SchematicannonBlockEntity.SchematicannonOptions> SCHEMATICANNON_OPTIONS = AllDataComponents.register("schematicannon_options", builder -> builder.persistent(SchematicannonBlockEntity.SchematicannonOptions.CODEC).networkSynchronized(SchematicannonBlockEntity.SchematicannonOptions.STREAM_CODEC));
    public static final DataComponentType<AutoRequestData> AUTO_REQUEST_DATA = AllDataComponents.register("auto_request_data", builder -> builder.persistent(AutoRequestData.CODEC).networkSynchronized(AutoRequestData.STREAM_CODEC));
    public static final DataComponentType<ShoppingListItem.ShoppingList> SHOPPING_LIST = AllDataComponents.register("shopping_list", builder -> builder.persistent(ShoppingListItem.ShoppingList.CODEC).networkSynchronized(ShoppingListItem.ShoppingList.STREAM_CODEC));
    public static final DataComponentType<String> SHOPPING_LIST_ADDRESS = AllDataComponents.register("shopping_list_address", builder -> builder.persistent((Codec)Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DataComponentType<String> PACKAGE_ADDRESS = AllDataComponents.register("package_address", builder -> builder.persistent((Codec)Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DataComponentType<ItemContainerContents> PACKAGE_CONTENTS = AllDataComponents.register("package_contents", builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));
    public static final DataComponentType<PackageItem.PackageOrderData> PACKAGE_ORDER_DATA = AllDataComponents.register("package_order_data", builder -> builder.persistent(PackageItem.PackageOrderData.CODEC).networkSynchronized(PackageItem.PackageOrderData.STREAM_CODEC));
    public static final DataComponentType<PackageOrderWithCrafts> PACKAGE_ORDER_CONTEXT = AllDataComponents.register("package_order_context", builder -> builder.persistent(PackageOrderWithCrafts.CODEC).networkSynchronized(PackageOrderWithCrafts.STREAM_CODEC));
    public static final DataComponentType<ClickToLinkBlockItem.ClickToLinkData> CLICK_TO_LINK_DATA = AllDataComponents.register("click_to_link_data", builder -> builder.persistent(ClickToLinkBlockItem.ClickToLinkData.CODEC).networkSynchronized(ClickToLinkBlockItem.ClickToLinkData.STREAM_CODEC));
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static final DataComponentType<ClipboardOverrides.ClipboardType> CLIPBOARD_TYPE = AllDataComponents.register("clipboard_type", builder -> builder.persistent(ClipboardOverrides.ClipboardType.CODEC).networkSynchronized(ClipboardOverrides.ClipboardType.STREAM_CODEC));
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static final DataComponentType<List<List<ClipboardEntry>>> CLIPBOARD_PAGES = AllDataComponents.register("clipboard_pages", builder -> builder.persistent(ClipboardEntry.CODEC.listOf().listOf()).networkSynchronized(CatnipStreamCodecBuilders.list((StreamCodec)CatnipStreamCodecBuilders.list(ClipboardEntry.STREAM_CODEC))));
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static final DataComponentType<Unit> CLIPBOARD_READ_ONLY = AllDataComponents.register("clipboard_read_only", builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit((Object)Unit.INSTANCE)));
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static final DataComponentType<CompoundTag> CLIPBOARD_COPIED_VALUES = AllDataComponents.register("clipboard_copied_values", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG));
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static final DataComponentType<Integer> CLIPBOARD_PREVIOUSLY_OPENED_PAGE = AllDataComponents.register("clipboard_previously_opened_page", builder -> builder.persistent((Codec)Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType type = ((DataComponentType.Builder)builder.apply(DataComponentType.builder())).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
