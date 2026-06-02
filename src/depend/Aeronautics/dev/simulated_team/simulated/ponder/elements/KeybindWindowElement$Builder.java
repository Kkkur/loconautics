/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.createmod.ponder.api.element.InputElementBuilder
 *  net.createmod.ponder.enums.PonderGuiTextures
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.ponder.elements;

import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.ponder.api.element.InputElementBuilder;
import net.createmod.ponder.enums.PonderGuiTextures;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class KeybindWindowElement.Builder
implements InputElementBuilder {
    public KeybindWindowElement.Builder withItem(ItemStack stack) {
        KeybindWindowElement.this.item = stack;
        return this;
    }

    public KeybindWindowElement.Builder leftClick() {
        KeybindWindowElement.this.icon = PonderGuiTextures.ICON_LMB;
        return this;
    }

    public KeybindWindowElement.Builder scroll() {
        KeybindWindowElement.this.icon = PonderGuiTextures.ICON_SCROLL;
        return this;
    }

    public KeybindWindowElement.Builder rightClick() {
        KeybindWindowElement.this.icon = PonderGuiTextures.ICON_RMB;
        return this;
    }

    public KeybindWindowElement.Builder showing(ScreenElement icon) {
        KeybindWindowElement.this.icon = icon;
        return this;
    }

    public KeybindWindowElement.Builder whileSneaking() {
        throw new UnsupportedOperationException();
    }

    public KeybindWindowElement.Builder whileCTRL() {
        throw new UnsupportedOperationException();
    }

    public KeybindWindowElement.Builder keybind(String keybind) {
        KeybindWindowElement.this.keybind = Component.keybind((String)keybind).append(" +");
        return this;
    }
}
