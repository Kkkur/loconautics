/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.inventory.DispenserMenu
 *  net.minecraft.world.inventory.MenuConstructor
 *  net.minecraft.world.inventory.MenuType
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.storage.item.menu;

import com.simibubi.create.api.contraption.storage.item.menu.StorageInteractionWrapper;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class MountedStorageMenus {
    public static final List<MenuType<?>> GENERIC_CHEST_MENUS = List.of(MenuType.GENERIC_9x1, MenuType.GENERIC_9x2, MenuType.GENERIC_9x3, MenuType.GENERIC_9x4, MenuType.GENERIC_9x5, MenuType.GENERIC_9x6);

    @Nullable
    public static MenuProvider createGeneric(Component menuName, IItemHandlerModifiable handler, Predicate<Player> stillValid, Consumer<Player> onClose) {
        int rows = handler.getSlots() / 9;
        if (rows < 1 || rows > 6) {
            return null;
        }
        if (handler.getSlots() % 9 != 0) {
            return null;
        }
        MenuType<?> type = GENERIC_CHEST_MENUS.get(rows - 1);
        StorageInteractionWrapper wrapper = new StorageInteractionWrapper(handler, stillValid, onClose);
        MenuConstructor constructor = (id, inv, player) -> new ChestMenu(type, id, inv, wrapper, rows);
        return new SimpleMenuProvider(constructor, menuName);
    }

    @Nullable
    public static MenuProvider createGeneric9x9(Component name, IItemHandlerModifiable handler, Predicate<Player> stillValid, Consumer<Player> onClose) {
        if (handler.getSlots() != 9) {
            return null;
        }
        StorageInteractionWrapper wrapper = new StorageInteractionWrapper(handler, stillValid, onClose);
        MenuConstructor constructor = (id, inv, player) -> new DispenserMenu(id, inv, wrapper);
        return new SimpleMenuProvider(constructor, name);
    }
}
