/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency
 *  com.simibubi.create.foundation.gui.AllIcons
 *  com.simibubi.create.foundation.gui.widget.IconButton
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterScreen;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.widgets.ConfirmationWidgetBase;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.widgets.PromptWidget;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.network.packets.linked_typewriter.TypewriterMenuModifySlots;
import foundry.veil.api.network.VeilPacketManager;
import java.util.function.Consumer;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EntryModifierScreen {
    public static final SimGUITextures MODIFICATION_MENU = SimGUITextures.LINKED_TYPEWRITER_KEY_MODIFICATION_MENU;
    public static final SimGUITextures MODIFICATION_ENTRY = SimGUITextures.LINKED_TYPEWRITER_BIND;
    public final LinkedTypewriterScreen parentScreen;
    private Consumer<LinkedTypewriterEntries.KeyboardEntry> finishedEntryCallback;
    public PsuedoKeyboardEntry psuedoEntry = null;
    public boolean modifying = false;
    public PromptWidget promptWidget;
    public IconButton confirmationWidget;
    public ConfirmationWidgetBase cancelEntryWidget;

    public EntryModifierScreen(LinkedTypewriterScreen screen) {
        this.parentScreen = screen;
    }

    public void init() {
        this.promptWidget = new PromptWidget(this, 0, 0, 68, 16);
        this.cancelEntryWidget = (ConfirmationWidgetBase)new ConfirmationWidgetBase(0, 0, (ScreenElement)AllIcons.I_TRASH).withMessage(SimLang.translate("linked_typewriter.delete.key", new Object[0]).component()).withCallback(this::finishWithoutEntry);
        this.confirmationWidget = (IconButton)new IconButton(0, 0, (ScreenElement)AllIcons.I_CONFIRM).withCallback(() -> {
            if (this.psuedoEntry != null) {
                this.psuedoEntry.finishModifications();
            }
        });
        this.resetXYPositions();
    }

    public void resetXYPositions() {
        int widgetHeight = this.getCenterHeight() + 32;
        this.promptWidget.setX(this.getCenterWidth() + 19);
        this.promptWidget.setY(widgetHeight);
        this.confirmationWidget.setX(this.getCenterWidth() + EntryModifierScreen.MODIFICATION_ENTRY.width - 56);
        this.confirmationWidget.setY(widgetHeight - 1);
        this.cancelEntryWidget.setX(this.getCenterWidth() + EntryModifierScreen.MODIFICATION_ENTRY.width - 33);
        this.cancelEntryWidget.setY(widgetHeight - 1);
    }

    public PsuedoKeyboardEntry startModifying(@Nullable LinkedTypewriterEntries.KeyboardEntry toModify, Consumer<LinkedTypewriterEntries.KeyboardEntry> onFinish) {
        PsuedoKeyboardEntry psuedoEntry = new PsuedoKeyboardEntry();
        if (toModify != null) {
            psuedoEntry.keyCode(toModify.glfwKeyCode).first(toModify.getFirst()).second(toModify.getSecond());
            this.parentScreen.getNewEntries().getKeyMap().remove(toModify.glfwKeyCode);
        } else {
            psuedoEntry.first = RedstoneLinkNetworkHandler.Frequency.EMPTY;
            psuedoEntry.second = RedstoneLinkNetworkHandler.Frequency.EMPTY;
        }
        this.parentScreen.addWidget(this.cancelEntryWidget);
        this.parentScreen.addWidget(this.confirmationWidget);
        this.parentScreen.addWidget(this.promptWidget);
        this.finishedEntryCallback = onFinish;
        this.modifying = true;
        this.psuedoEntry = psuedoEntry;
        LinkedTypewriterMenuCommon menu = (LinkedTypewriterMenuCommon)this.parentScreen.getMenu();
        menu.slotsActive = true;
        ItemStack first = psuedoEntry.first.getStack();
        ItemStack second = psuedoEntry.second.getStack();
        menu.ghostInventory.setStackInSlot(0, first);
        menu.ghostInventory.setStackInSlot(1, second);
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterMenuModifySlots(first, second)});
        return psuedoEntry;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt, PoseStack ps) {
        if (this.modifying) {
            ps.pushPose();
            this.confirmationWidget.render(guiGraphics, mouseX, mouseY, pt);
            this.promptWidget.render(guiGraphics, mouseX, mouseY, pt);
            this.cancelEntryWidget.render(guiGraphics, mouseX, mouseY, pt);
            ps.popPose();
        }
    }

    public void renderBG(GuiGraphics guiGraphics) {
        PoseStack ps = guiGraphics.pose();
        ps.pushPose();
        guiGraphics.fillGradient(0, 0, this.parentScreen.width, this.parentScreen.height, 1, -1072689136, -804253680);
        ps.translate(0.0f, 0.0f, 2.0f);
        MODIFICATION_MENU.render(guiGraphics, this.getCenterWidth(), this.getCenterHeight());
        this.parentScreen.renderPlayerInventory(guiGraphics, this.getCenterWidth() + 19, this.getCenterHeight() + 72);
        ps.popPose();
    }

    public int getCenterWidth() {
        return this.parentScreen.getLeftPos() + 11;
    }

    public int getCenterHeight() {
        return this.parentScreen.getTopPos() - 31;
    }

    public void finishWithoutEntry() {
        this.finishedEntryCallback.accept(null);
        this.disable();
    }

    public void disable() {
        LinkedTypewriterMenuCommon menu = (LinkedTypewriterMenuCommon)this.parentScreen.getMenu();
        this.modifying = false;
        this.psuedoEntry = null;
        this.parentScreen.removeWidget((GuiEventListener)this.cancelEntryWidget);
        this.parentScreen.removeWidget((GuiEventListener)this.promptWidget);
        this.parentScreen.removeWidget((GuiEventListener)this.confirmationWidget);
        this.finishedEntryCallback = null;
        menu.slotsActive = false;
        menu.ghostInventory.setStackInSlot(0, ItemStack.EMPTY);
        menu.ghostInventory.setStackInSlot(1, ItemStack.EMPTY);
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new TypewriterMenuModifySlots(ItemStack.EMPTY, ItemStack.EMPTY)});
    }

    public class PsuedoKeyboardEntry {
        public int glfwKeyCode = -1;
        private RedstoneLinkNetworkHandler.Frequency first;
        private RedstoneLinkNetworkHandler.Frequency second;

        public PsuedoKeyboardEntry keyCode(int newCode) {
            this.glfwKeyCode = newCode;
            return this;
        }

        public PsuedoKeyboardEntry first(RedstoneLinkNetworkHandler.Frequency newFrequency) {
            this.first = newFrequency;
            return this;
        }

        public PsuedoKeyboardEntry second(RedstoneLinkNetworkHandler.Frequency newFrequency) {
            this.second = newFrequency;
            return this;
        }

        public void finishModifications() {
            if (this.glfwKeyCode != -1) {
                this.first(RedstoneLinkNetworkHandler.Frequency.of((ItemStack)((LinkedTypewriterMenuCommon)EntryModifierScreen.this.parentScreen.getMenu()).ghostInventory.getStackInSlot(0)));
                this.second(RedstoneLinkNetworkHandler.Frequency.of((ItemStack)((LinkedTypewriterMenuCommon)EntryModifierScreen.this.parentScreen.getMenu()).ghostInventory.getStackInSlot(1)));
                LinkedTypewriterEntries.KeyboardEntry entry = new LinkedTypewriterEntries.KeyboardEntry(EntryModifierScreen.this.psuedoEntry.first, EntryModifierScreen.this.psuedoEntry.second, EntryModifierScreen.this.psuedoEntry.glfwKeyCode, EntryModifierScreen.this.parentScreen.clientBe.getBlockPos());
                EntryModifierScreen.this.finishedEntryCallback.accept(entry);
            } else {
                EntryModifierScreen.this.finishWithoutEntry();
            }
            EntryModifierScreen.this.disable();
        }
    }
}
