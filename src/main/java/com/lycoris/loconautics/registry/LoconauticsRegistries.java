package com.lycoris.loconautics.registry;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlock;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerMenu;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.world.inventory.MenuType;
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

    // Holder array breaks the self-reference: the lambda is called lazily after
    // ANALOG_CONTROLLER_BE is assigned, so holder[0].get() is safe at that point.
    @SuppressWarnings("unchecked")
    private static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnalogControllerBlockEntity>>[]
            BE_HOLDER = new DeferredHolder[1];

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnalogControllerBlockEntity>>
            ANALOG_CONTROLLER_BE = BE_HOLDER[0] = BLOCK_ENTITIES.register("analog_controller", () ->
            BlockEntityType.Builder
                    .of((pos, state) -> new AnalogControllerBlockEntity(BE_HOLDER[0].get(), pos, state),
                            ANALOG_CONTROLLER.get())
                    .build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<AnalogControllerMenu>>
            ANALOG_CONTROLLER_MENU = MENUS.register("analog_controller", () ->
            IMenuTypeExtension.create(AnalogControllerMenu::new));

    // ------------------------------------------------------------------ constructor / register

    private LoconauticsRegistries() {
    }

    /** Registers every DeferredRegister onto the mod event bus. Call once from the mod constructor. */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
    }
}