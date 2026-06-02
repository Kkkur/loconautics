/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.MenuBuilder$ForgeMenuFactory
 *  com.tterrag.registrate.builders.MenuBuilder$ScreenFactory
 *  com.tterrag.registrate.util.entry.MenuEntry
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package dev.simulated_team.simulated.index;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterScreen;
import dev.simulated_team.simulated.service.SimMenuService;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SimMenuTypes {
    public static final MenuEntry<LinkedTypewriterMenuCommon> LINKED_TYPEWRITER = SimMenuTypes.register("linked_typewriter", SimMenuService.INSTANCE::getLoaderLinkedTypewriter, () -> LinkedTypewriterScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen> MenuEntry<C> register(String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return Simulated.getRegistrate().menu(name, factory, screenFactory).register();
    }

    public static void register() {
    }
}
