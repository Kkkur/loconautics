package com.lycoris.loconautics.registry;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlock;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerMenu;
import com.lycoris.loconautics.content.transmission.TransmissionBlock;
import com.lycoris.loconautics.content.transmission.TransmissionBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

/**
 * Holds all DeferredRegisters for the mod.
 */
public final class LoconauticsRegistries {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LoconauticsConstants.MOD_ID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(LoconauticsConstants.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE,
                    LoconauticsConstants.MOD_ID);

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU,
                    LoconauticsConstants.MOD_ID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                    LoconauticsConstants.MOD_ID);

    // ------------------------------------------------------------------ Analog Controller

    public static final DeferredHolder<Block, AnalogControllerBlock> ANALOG_CONTROLLER =
            BLOCKS.register("analog_controller", () -> new AnalogControllerBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> ANALOG_CONTROLLER_ITEM =
            ITEMS.register("analog_controller", () ->
                    new BlockItem(ANALOG_CONTROLLER.get(),
                            new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnalogControllerBlockEntity>>
            ANALOG_CONTROLLER_BE = BLOCK_ENTITIES.register("analog_controller", () ->
            BlockEntityType.Builder
                    .of((pos, state) -> new AnalogControllerBlockEntity(
                                    LoconauticsRegistries.ANALOG_CONTROLLER_BE.get(), pos, state),
                            ANALOG_CONTROLLER.get())
                    .build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<AnalogControllerMenu>>
            ANALOG_CONTROLLER_MENU = MENUS.register("analog_controller", () ->
            IMenuTypeExtension.create((id, inv, buf) ->
                    new AnalogControllerMenu(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(), id, inv, buf)));

    // ------------------------------------------------------------------ Transmission

    public static final DeferredHolder<Block, TransmissionBlock> TRANSMISSION =
            BLOCKS.register("transmission", () -> new TransmissionBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> TRANSMISSION_ITEM =
            ITEMS.register("transmission", () ->
                    new BlockItem(TRANSMISSION.get(),
                            new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransmissionBlockEntity>>
            TRANSMISSION_BE = BLOCK_ENTITIES.register("transmission", () ->
            BlockEntityType.Builder
                    .of((pos, state) -> new TransmissionBlockEntity(
                                    LoconauticsRegistries.TRANSMISSION_BE.get(), pos, state),
                            TRANSMISSION.get())
                    .build(null));

    // ------------------------------------------------------------------ Creative tab

    public static final net.neoforged.neoforge.registries.DeferredHolder<CreativeModeTab, CreativeModeTab>
            LOCONAUTICS_TAB = CREATIVE_TABS.register("main", () ->
            net.minecraft.world.item.CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.loconautics.main"))
                    .icon(() -> new net.minecraft.world.item.ItemStack(ANALOG_CONTROLLER_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(ANALOG_CONTROLLER_ITEM.get());
                        output.accept(TRANSMISSION_ITEM.get());
                    })
                    .build());

    // ------------------------------------------------------------------ constructor / register

    private LoconauticsRegistries() {
    }

    /** Registers every DeferredRegister onto the mod event bus. Call once from the mod constructor. */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
    }
}