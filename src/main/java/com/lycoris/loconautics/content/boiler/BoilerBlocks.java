package com.lycoris.loconautics.content.boiler;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.lycoris.loconautics.core.LoconauticsConstants;

/**
 * Deferred registrations for the steam boiler multiblock.
 *
 * <p>Blocks and items are registered onto the shared registers in
 * {@link LoconauticsRegistries} so that NeoForge sees a single
 * DeferredRegister per registry namespace. The block-entity register
 * is local because it is only used by the boiler.
 */
public final class BoilerBlocks {

    // Block-entity types get their own register — there is no conflict here
    // because LoconauticsRegistries doesn't have one that overlaps.
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE,
                    LoconauticsConstants.MOD_ID);

    // ------------------------------------------------------------------ Firebox

    public static final DeferredHolder<Block, FireboxBlock> FIREBOX =
            LoconauticsRegistries.BLOCKS.register("firebox", () -> new FireboxBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(3.5f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> FIREBOX_ITEM =
            LoconauticsRegistries.ITEMS.register("firebox", () ->
                    new BlockItem(FIREBOX.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FireboxBlockEntity>>
            FIREBOX_BE = BLOCK_ENTITIES.register("firebox", () ->
            BlockEntityType.Builder
                    .of(BoilerBlocks::createFireboxBE, FIREBOX.get())
                    .build(null));

    // ------------------------------------------------------------------ Boiler Body

    public static final DeferredHolder<Block, BoilerBodyBlock> BOILER_BODY =
            LoconauticsRegistries.BLOCKS.register("boiler_body", () -> new BoilerBodyBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> BOILER_BODY_ITEM =
            LoconauticsRegistries.ITEMS.register("boiler_body", () ->
                    new BlockItem(BOILER_BODY.get(), new Item.Properties().stacksTo(64)));

    // ------------------------------------------------------------------ Boiler Controller

    public static final DeferredHolder<Block, BoilerControllerBlock> BOILER_CONTROLLER =
            LoconauticsRegistries.BLOCKS.register("boiler_controller", () -> new BoilerControllerBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> BOILER_CONTROLLER_ITEM =
            LoconauticsRegistries.ITEMS.register("boiler_controller", () ->
                    new BlockItem(BOILER_CONTROLLER.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoilerControllerBlockEntity>>
            BOILER_CONTROLLER_BE = BLOCK_ENTITIES.register("boiler_controller", () ->
            BlockEntityType.Builder
                    .of(BoilerBlocks::createBoilerControllerBE, BOILER_CONTROLLER.get())
                    .build(null));

    // ------------------------------------------------------------------ factories

    static FireboxBlockEntity createFireboxBE(BlockPos pos, BlockState state) {
        return new FireboxBlockEntity(FIREBOX_BE.get(), pos, state);
    }

    static BoilerControllerBlockEntity createBoilerControllerBE(BlockPos pos, BlockState state) {
        return new BoilerControllerBlockEntity(BOILER_CONTROLLER_BE.get(), pos, state);
    }

    // ------------------------------------------------------------------ register

    private BoilerBlocks() {}

    /**
     * Registers only the block-entity DeferredRegister.
     * Blocks and items are already on the shared registers in LoconauticsRegistries.
     */
    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}