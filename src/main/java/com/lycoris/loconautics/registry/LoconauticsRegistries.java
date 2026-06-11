package com.lycoris.loconautics.registry;

import com.lycoris.loconautics.block.casing.ReinforcedCasingBlock;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlock;
import com.lycoris.loconautics.content.steelcable.SteelCableItem;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerMenu;
import com.lycoris.loconautics.content.transmission.TransmissionMenu;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlock;
import com.lycoris.loconautics.content.knuckle.KnuckleBlock;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlockEntity;
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

    // ------------------------------------------------------------------ Bearing Axle

    public static final DeferredHolder<Block, BearingAxleBlock> BEARING_AXLE =
            BLOCKS.register("bearing_axle", () -> new BearingAxleBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> BEARING_AXLE_ITEM =
            ITEMS.register("bearing_axle", () ->
                    new BlockItem(BEARING_AXLE.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BearingAxleBlockEntity>>
            BEARING_AXLE_BE = BLOCK_ENTITIES.register("bearing_axle", () ->
            BlockEntityType.Builder
                    .of((pos, state) -> new BearingAxleBlockEntity(
                                    LoconauticsRegistries.BEARING_AXLE_BE.get(), pos, state),
                            BEARING_AXLE.get())
                    .build(null));

    // ------------------------------------------------------------------ Knuckle

    public static final DeferredHolder<Block, KnuckleBlock> KNUCKLE =
            BLOCKS.register("knuckle", () -> new KnuckleBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Item, BlockItem> KNUCKLE_ITEM =
            ITEMS.register("knuckle", () ->
                    new BlockItem(KNUCKLE.get(), new Item.Properties().stacksTo(64)));

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

    public static final DeferredHolder<MenuType<?>, MenuType<TransmissionMenu>>
            TRANSMISSION_MENU = MENUS.register("transmission", () ->
            IMenuTypeExtension.create((id, inv, buf) ->
                    new TransmissionMenu(LoconauticsRegistries.TRANSMISSION_MENU.get(), id, inv, buf)));

    // ------------------------------------------------------------------ Steel Cable

    public static final DeferredHolder<Item, SteelCableItem> STEEL_CABLE =
            ITEMS.register("steel_cable", () -> new SteelCableItem(new Item.Properties().stacksTo(16)));

    // ------------------------------------------------------------------ Reinforced Iron Sheet (ported from create_tectonic)

    /** Intermediate crafting ingredient: iron sheet processed through sequenced assembly. */
    public static final DeferredHolder<Item, Item> REINFORCED_IRON_SHEET =
            ITEMS.register("reinforced_iron_sheet", () -> new Item(new Item.Properties().stacksTo(64)));

    /** Transitional item used during the reinforced iron sheet sequenced assembly process. */
    public static final DeferredHolder<Item, Item> UNPROCESSED_REINFORCED_IRON_SHEET =
            ITEMS.register("unprocessed_reinforced_iron_sheet", () -> new Item(new Item.Properties().stacksTo(64)));

    // ------------------------------------------------------------------ Sable Train Relocator

    /** Wrench-like tool: right-click a Sable train sub-level to relocate it onto a new rail (see
     *  {@link com.lycoris.loconautics.allsable.SableTrainRelocator}). Uses the vanilla stick texture. */
    public static final DeferredHolder<Item, Item> SABLE_TRAIN_RELOCATOR =
            ITEMS.register("sable_train_relocator", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Block, ReinforcedCasingBlock> REINFORCED_CASING =
            BLOCKS.register("reinforced_casing", () -> new ReinforcedCasingBlock(
                    BlockBehaviour.Properties.of()
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()));

    public static final DeferredHolder<Item, BlockItem> REINFORCED_CASING_ITEM =
            ITEMS.register("reinforced_casing", () ->
                    new BlockItem(REINFORCED_CASING.get(), new Item.Properties().stacksTo(64)));

    // ------------------------------------------------------------------ Creative tab

    public static final net.neoforged.neoforge.registries.DeferredHolder<CreativeModeTab, CreativeModeTab>
            LOCONAUTICS_TAB = CREATIVE_TABS.register("main", () ->
            net.minecraft.world.item.CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.loconautics.main"))
                    .icon(() -> new net.minecraft.world.item.ItemStack(ANALOG_CONTROLLER_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(ANALOG_CONTROLLER_ITEM.get());
                        output.accept(BEARING_AXLE_ITEM.get());
                        output.accept(KNUCKLE_ITEM.get());
                        output.accept(TRANSMISSION_ITEM.get());
                        output.accept(STEEL_CABLE.get());
                        output.accept(SABLE_TRAIN_RELOCATOR.get());
                        output.accept(REINFORCED_IRON_SHEET.get());
                        output.accept(UNPROCESSED_REINFORCED_IRON_SHEET.get());
                        output.accept(com.lycoris.loconautics.content.boiler.BoilerBlocks.FIREBOX_ITEM.get());
                        output.accept(com.lycoris.loconautics.content.boiler.BoilerBlocks.BOILER_BODY_ITEM.get());
                        output.accept(com.lycoris.loconautics.content.boiler.BoilerBlocks.BOILER_CONTROLLER_ITEM.get());
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