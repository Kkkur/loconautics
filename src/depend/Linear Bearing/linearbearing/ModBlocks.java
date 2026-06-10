/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.properties.NoteBlockInstrument
 *  net.minecraft.world.level.material.MapColor
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.AndesiteLampBlock;
import com.bearing.linearbearing.LinearBearingBlock;
import com.bearing.linearbearing.LinearCasingBlock;
import com.bearing.linearbearing.LinearMovingBlock;
import com.bearing.linearbearing.MagneticPortBlock;
import com.bearing.linearbearing.MagneticPortBlockEntity;
import com.bearing.linearbearing.RedstoneConverterBlock;
import com.bearing.linearbearing.RedstoneConverterBlockEntity;
import com.bearing.linearbearing.TorsionalAnchorBlock;
import com.bearing.linearbearing.TorsionalAnchorBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((Registry)BuiltInRegistries.BLOCK, (String)"linearbearing");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((Registry)BuiltInRegistries.ITEM, (String)"linearbearing");
    public static final DeferredRegister<BlockEntityType<?>> BE_REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.BLOCK_ENTITY_TYPE, (String)"linearbearing");
    public static final Supplier<Block> LINEAR_BEARING = BLOCKS.register("linear_bearing", () -> new LinearBearingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f, 6.0f).requiresCorrectToolForDrops()));
    public static final Supplier<Item> LINEAR_BEARING_ITEM = ITEMS.register("linear_bearing", () -> new BlockItem(LINEAR_BEARING.get(), new Item.Properties()));
    public static final Supplier<Block> LINEAR_CASING = BLOCKS.register("linear_casing", () -> new LinearCasingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f, 6.0f).requiresCorrectToolForDrops()));
    public static final Supplier<Item> LINEAR_CASING_ITEM = ITEMS.register("linear_casing", () -> new BlockItem(LINEAR_CASING.get(), new Item.Properties()));
    public static final Supplier<Block> LINEAR_MOVING = BLOCKS.register("linear_moving", () -> new LinearMovingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f, 6.0f).noLootTable()));
    public static final Supplier<Block> MAGNETIC_PORT = BLOCKS.register("magnetic_port", () -> new MagneticPortBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f, 6.0f).requiresCorrectToolForDrops()));
    public static final Supplier<Item> MAGNETIC_PORT_ITEM = ITEMS.register("magnetic_port", () -> new BlockItem(MAGNETIC_PORT.get(), new Item.Properties()));
    public static final Supplier<Block> TORSIONAL_ANCHOR = BLOCKS.register("torsional_anchor", () -> new TorsionalAnchorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f, 6.0f).forceSolidOn().requiresCorrectToolForDrops()));
    public static final Supplier<Item> TORSIONAL_ANCHOR_ITEM = ITEMS.register("torsional_anchor", () -> new BlockItem(TORSIONAL_ANCHOR.get(), new Item.Properties()));
    public static final Supplier<Block> ANDESITE_LAMP = BLOCKS.register("andesite_lamp", () -> new AndesiteLampBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.3f, 0.1f).forceSolidOn().sound(SoundType.GLASS).requiresCorrectToolForDrops()));
    public static final Supplier<Item> ANDESITE_LAMP_ITEM = ITEMS.register("andesite_lamp", () -> new BlockItem(ANDESITE_LAMP.get(), new Item.Properties()));
    public static final Supplier<Block> REDSTONE_CONVERTER = BLOCKS.register("redstone_converter", () -> new RedstoneConverterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f, 6.0f).forceSolidOn().requiresCorrectToolForDrops()));
    public static final Supplier<Item> REDSTONE_CONVERTER_ITEM = ITEMS.register("redstone_converter", () -> new BlockItem(REDSTONE_CONVERTER.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagneticPortBlockEntity>> MAGNETIC_PORT_BE = BE_REGISTER.register("magnetic_port", () -> BlockEntityType.Builder.of((pos, state) -> new MagneticPortBlockEntity((BlockEntityType)MAGNETIC_PORT_BE.get(), pos, state), (Block[])new Block[]{MAGNETIC_PORT.get()}).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TorsionalAnchorBlockEntity>> TORSIONAL_ANCHOR_BE = BE_REGISTER.register("torsional_anchor", () -> BlockEntityType.Builder.of((pos, state) -> new TorsionalAnchorBlockEntity((BlockEntityType)TORSIONAL_ANCHOR_BE.get(), pos, state), (Block[])new Block[]{TORSIONAL_ANCHOR.get()}).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneConverterBlockEntity>> REDSTONE_CONVERTER_BE = BE_REGISTER.register("redstone_converter", () -> BlockEntityType.Builder.of((pos, state) -> new RedstoneConverterBlockEntity((BlockEntityType)REDSTONE_CONVERTER_BE.get(), pos, state), (Block[])new Block[]{REDSTONE_CONVERTER.get()}).build(null));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BE_REGISTER.register(eventBus);
    }
}
