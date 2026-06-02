/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.RenderElement
 *  net.createmod.catnip.gui.widget.AbstractSimiWidget
 *  net.createmod.catnip.gui.widget.ElementWidget
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.computercraft;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.function.Supplier;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.RenderElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.gui.widget.ElementWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class ComputerScreen
extends AbstractSimiScreen {
    private final AllGuiTextures background = AllGuiTextures.COMPUTER;
    private final Supplier<Component> displayTitle;
    private final RenderWindowFunction additional;
    private final Screen previousScreen;
    private final Supplier<Boolean> hasAttachedComputer;
    private AbstractSimiWidget computerWidget;
    private IconButton confirmButton;

    public ComputerScreen(Component title, @Nullable RenderWindowFunction additional, Screen previousScreen, Supplier<Boolean> hasAttachedComputer) {
        this(title, () -> title, additional, previousScreen, hasAttachedComputer);
    }

    public ComputerScreen(Component title, Supplier<Component> displayTitle, @Nullable RenderWindowFunction additional, Screen previousScreen, Supplier<Boolean> hasAttachedComputer) {
        super(title);
        this.displayTitle = displayTitle;
        this.additional = additional;
        this.previousScreen = previousScreen;
        this.hasAttachedComputer = hasAttachedComputer;
    }

    public void tick() {
        if (!this.hasAttachedComputer.get().booleanValue()) {
            this.minecraft.setScreen(this.previousScreen);
        }
        super.tick();
    }

    protected void init() {
        this.setWindowSize(this.background.getWidth(), this.background.getHeight());
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        Mods.COMPUTERCRAFT.executeIfInstalled(() -> () -> {
            this.computerWidget = new ElementWidget(x + 33, y + 38).showingElement((RenderElement)GuiGameElement.of((ItemLike)Mods.COMPUTERCRAFT.getBlock("computer_advanced")));
            this.computerWidget.getToolTip().add(CreateLang.translate("gui.attached_computer.hint", new Object[0]).component());
            this.addRenderableWidget((GuiEventListener)this.computerWidget);
        });
        this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> ((ComputerScreen)this).onClose());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop;
        this.background.render(graphics, x, y);
        graphics.drawString(this.font, this.displayTitle.get(), Math.round((float)x + (float)this.background.getWidth() / 2.0f - (float)this.font.width((FormattedText)this.displayTitle.get()) / 2.0f), y + 4, 0x442000, false);
        graphics.drawWordWrap(this.font, (FormattedText)CreateLang.translate("gui.attached_computer.controlled", new Object[0]).component(), x + 55, y + 32, 111, 0x7A7A7A);
        if (this.additional != null) {
            this.additional.render(graphics, mouseX, mouseY, partialTicks, x, y, this.background);
        }
    }

    @FunctionalInterface
    public static interface RenderWindowFunction {
        public void render(GuiGraphics var1, int var2, int var3, float var4, int var5, int var6, AllGuiTextures var7);
    }
}
