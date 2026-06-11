package com.lycoris.loconautics.content.boiler;

import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Deferred registrations for the steam boiler multiblock.
 *
 * <p>Call {@link #register(IEventBus)} once from the mod constructor alongside
 * the other registries.
 */
public final class BoilerBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LoconauticsConstants.MOD_ID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(LoconauticsConstants.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE,
                    LoconauticsConstants.MOD_ID);

    // ------------------------------------------------------------------ Firebox

    public static final DeferredHolder<Block, FireboxBlock> FIREBOX =
            BLOCKS.register("firebox", () -> new FireboxBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(3.5f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> FIREBOX_ITEM =
            ITEMS.register("firebox", () ->
                    new BlockItem(FIREBOX.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FireboxBlockEntity>>
            FIREBOX_BE = BLOCK_ENTITIES.register("firebox", () ->
            BlockEntityType.Builder
                    .of((pos, state) -> new FireboxBlockEntity(FIREBOX_BE.get(), pos, state),
                            FIREBOX.get())
                    .build(null));

    // ------------------------------------------------------------------ Boiler Body

    public static final DeferredHolder<Block, BoilerBodyBlock> BOILER_BODY =
            BLOCKS.register("boiler_body", () -> new BoilerBodyBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> BOILER_BODY_ITEM =
            ITEMS.register("boiler_body", () ->
                    new BlockItem(BOILER_BODY.get(), new Item.Properties().stacksTo(64)));

    // ------------------------------------------------------------------ Boiler Controller

    public static final DeferredHolder<Block, BoilerControllerBlock> BOILER_CONTROLLER =
            BLOCKS.register("boiler_controller", () -> new BoilerControllerBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> BOILER_CONTROLLER_ITEM =
            ITEMS.register("boiler_controller", () ->
                    new BlockItem(BOILER_CONTROLLER.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoilerControllerBlockEntity>>
            BOILER_CONTROLLER_BE = BLOCK_ENTITIES.register("boiler_controller", () ->
            BlockEntityType.Builder
                    .of((pos, state) -> new BoilerControllerBlockEntity(BOILER_CONTROLLER_BE.get(), pos, state),
                            BOILER_CONTROLLER.get())
                    .build(null));

    // ------------------------------------------------------------------ register

    private BoilerBlocks() {}

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
    }
}