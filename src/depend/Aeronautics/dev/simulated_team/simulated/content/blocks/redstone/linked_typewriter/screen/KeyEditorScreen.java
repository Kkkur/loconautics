/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.AllKeys
 *  com.simibubi.create.foundation.gui.AllGuiTextures
 *  com.simibubi.create.foundation.gui.AllIcons
 *  com.simibubi.create.foundation.gui.widget.IconButton
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterScreen;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.widgets.ConfirmationWidgetBase;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.index.SimIcons;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class KeyEditorScreen {
    private static final SimGUITextures KEY_MENU = SimGUITextures.LINKED_TYPEWRITER_KEYS_MENU;
    private static final SimGUITextures KEY_ENTRY = SimGUITextures.LINKED_TYPEWRITER_KEY_ENTRY;
    private static final int MIN_SCROLL_Y = 50;
    private static final int ENTRY_HEIGHT_PADDING_PIXELS = 3;
    private final LinkedTypewriterScreen parentScreen;
    public boolean active;
    private int scroll = 0;
    private final LerpedFloat lerpedScroll = LerpedFloat.linear();
    ObjectArrayList<KeyEntryWidget> keyboardEntryWrappers = new ObjectArrayList();
    private final IconButton addWidget;
    private final IconButton confirmWidget;
    private final IconButton removeAllWidget;

    public KeyEditorScreen(LinkedTypewriterScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.addWidget = (IconButton)new IconButton(0, 0, (ScreenElement)AllIcons.I_ADD).withCallback(() -> this.modifyEntry(null));
        this.confirmWidget = (IconButton)new IconButton(0, 0, (ScreenElement)AllIcons.I_CONFIRM).withCallback(() -> this.parentScreen.switchScreen(false));
        this.removeAllWidget = (IconButton)new ConfirmationWidgetBase(0, 0, (ScreenElement)AllIcons.I_TRASH).withMessage(Component.translatable((String)"simulated.linked_typewriter.confirm_delete_all")).withCallback(() -> parentScreen.sendNewKeys(true));
        this.resetPositions();
    }

    public void startEditing() {
        this.resetPositions();
        this.addAllWidgets();
        this.rebuildWrappers();
        this.active = true;
    }

    public void endEditing() {
        this.removeAllWidgets();
        this.keyboardEntryWrappers.clear();
        this.active = false;
    }

    public void resetPositions() {
        int widgetHeight = this.topPos() + KeyEditorScreen.KEY_MENU.height - 24;
        this.addWidget.setX(this.leftPos() + KeyEditorScreen.KEY_MENU.width - 54);
        this.addWidget.setY(widgetHeight);
        this.confirmWidget.setX(this.leftPos() + KeyEditorScreen.KEY_MENU.width - 25);
        this.confirmWidget.setY(widgetHeight);
        this.removeAllWidget.setX(this.leftPos() + 8);
        this.removeAllWidget.setY(widgetHeight);
    }

    public void activateAllWidgets() {
        this.addWidget.active = true;
        this.confirmWidget.active = true;
        this.removeAllWidget.active = true;
        for (KeyEntryWidget wrapper : this.keyboardEntryWrappers) {
            wrapper.editWidget.active = true;
            wrapper.deleteWidget.active = true;
        }
    }

    public void deactivateAllWidgets() {
        this.addWidget.active = false;
        this.confirmWidget.active = false;
        this.removeAllWidget.active = false;
        for (KeyEntryWidget wrapper : this.keyboardEntryWrappers) {
            wrapper.editWidget.active = false;
            wrapper.deleteWidget.active = false;
        }
    }

    public void addAllWidgets() {
        this.parentScreen.addWidget(this.addWidget);
        this.parentScreen.addWidget(this.confirmWidget);
        this.parentScreen.addWidget(this.removeAllWidget);
        for (KeyEntryWidget wrapper : this.keyboardEntryWrappers) {
            this.parentScreen.addWidget(wrapper.editWidget);
            this.parentScreen.addWidget(wrapper.deleteWidget);
        }
    }

    public void removeAllWidgets() {
        this.parentScreen.removeWidget((GuiEventListener)this.addWidget);
        this.parentScreen.removeWidget((GuiEventListener)this.confirmWidget);
        this.parentScreen.removeWidget((GuiEventListener)this.removeAllWidget);
        for (KeyEntryWidget wrapper : this.keyboardEntryWrappers) {
            this.parentScreen.removeWidget((GuiEventListener)wrapper.editWidget);
            this.parentScreen.removeWidget((GuiEventListener)wrapper.deleteWidget);
        }
    }

    public void tick() {
        this.lerpedScroll.chase((double)this.scroll, 0.8, LerpedFloat.Chaser.EXP);
        this.lerpedScroll.tickChaser();
        this.clampScroll();
    }

    protected void shiftEntries(boolean shiftLeft) {
        int shiftBy = shiftLeft ? -1 : 1;
        this.scroll += shiftBy * 19;
        this.clampScroll();
    }

    private void clampScroll() {
        int maxScroll = Math.max(0, (this.parentScreen.getNewEntries().getSize() - 4) * (SimGUITextures.LINKED_TYPEWRITER_KEY_ENTRY.height + 3));
        this.scroll = Math.clamp((long)this.scroll, 0, maxScroll);
    }

    private void addEntry(LinkedTypewriterEntries.KeyboardEntry entryFromModifier) {
        this.parentScreen.getNewEntries().setKey(entryFromModifier.glfwKeyCode, entryFromModifier);
        this.rebuildWrappers();
    }

    private void removeWidget(KeyEntryWidget wrapper) {
        LinkedTypewriterEntries.KeyboardEntry entry = wrapper.entry;
        if (entry != null) {
            this.parentScreen.getNewEntries().setKey(entry.glfwKeyCode, null);
        }
        this.rebuildWrappers();
    }

    private void modifyEntry(@Nullable KeyEntryWidget widget) {
        this.parentScreen.modifier.startModifying(widget == null ? null : widget.entry, newEntry -> {
            if (newEntry != null) {
                KeyEntryWidget alreadyPresent = null;
                for (KeyEntryWidget wrapperEntry : this.keyboardEntryWrappers) {
                    if (wrapperEntry.entry.glfwKeyCode != newEntry.glfwKeyCode) continue;
                    alreadyPresent = wrapperEntry;
                    break;
                }
                if (alreadyPresent != null) {
                    this.parentScreen.getNewEntries().setKey(newEntry.glfwKeyCode, (LinkedTypewriterEntries.KeyboardEntry)newEntry);
                    alreadyPresent.entry = newEntry;
                } else {
                    this.addEntry((LinkedTypewriterEntries.KeyboardEntry)newEntry);
                }
            }
            this.activateAllWidgets();
        });
        if (widget != null) {
            this.removeWidget(widget);
        }
        this.deactivateAllWidgets();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt, PoseStack ps) {
        guiGraphics.enableScissor(0, this.topPos() + 20, this.parentScreen.width, this.topPos() + KeyEditorScreen.KEY_MENU.height - 35);
        for (KeyEntryWidget wrapper : this.keyboardEntryWrappers) {
            wrapper.render(guiGraphics, mouseX, mouseY, pt, ps);
        }
        guiGraphics.disableScissor();
        this.addWidget.render(guiGraphics, mouseX, mouseY, pt);
        this.confirmWidget.render(guiGraphics, mouseX, mouseY, pt);
        this.removeAllWidget.render(guiGraphics, mouseX, mouseY, pt);
        boolean fadeOffColor = false;
        int fadeFromColor = 0x77000000;
        guiGraphics.fillGradient(this.leftPos() + 7, this.topPos() + 20, this.leftPos() + 231, this.topPos() + 30, 0x77000000, 0);
        guiGraphics.fillGradient(this.leftPos() + 7, this.topPos() + 150, this.leftPos() + 231, this.topPos() + 160, 0, 0x77000000);
    }

    public void rebuildWrappers() {
        for (KeyEntryWidget wrapper : this.keyboardEntryWrappers) {
            this.parentScreen.removeWidget((GuiEventListener)wrapper.deleteWidget);
            this.parentScreen.removeWidget((GuiEventListener)wrapper.editWidget);
        }
        this.keyboardEntryWrappers.clear();
        LinkedTypewriterEntries entries = this.parentScreen.getNewEntries();
        for (LinkedTypewriterEntries.KeyboardEntry entry : entries.getEntries()) {
            KeyEntryWidget wrapper = new KeyEntryWidget(entry);
            this.keyboardEntryWrappers.add((Object)wrapper);
            this.parentScreen.addWidget(wrapper.editWidget);
            this.parentScreen.addWidget(wrapper.deleteWidget);
        }
    }

    public void renderBG(GuiGraphics guiGraphics, float v, int i, int i1) {
        KEY_MENU.render(guiGraphics, this.leftPos(), this.topPos());
        guiGraphics.enableScissor(0, this.topPos() + 20, this.parentScreen.width, this.topPos() + KeyEditorScreen.KEY_MENU.height - 35);
        for (KeyEntryWidget wrapper : this.keyboardEntryWrappers) {
            wrapper.renderBackground(guiGraphics, v, i, i1);
        }
        guiGraphics.disableScissor();
    }

    private int leftPos() {
        return this.parentScreen.getLeftPos();
    }

    private int topPos() {
        return this.parentScreen.getTopPos() - 40;
    }

    private class KeyEntryWidget {
        private final NoXYButton editWidget = (NoXYButton)new NoXYButton((ScreenElement)SimIcons.ADD_OR_EDIT).withCallback(() -> KeyEditorScreen.this.modifyEntry(this));
        private final NoXYButton deleteWidget = (NoXYButton)new NoXYButton((ScreenElement)AllIcons.I_TRASH).withCallback(() -> KeyEditorScreen.this.removeWidget(this));
        private LinkedTypewriterEntries.KeyboardEntry entry;

        public KeyEntryWidget(LinkedTypewriterEntries.KeyboardEntry entry) {
            this.entry = entry;
        }

        private float getCurrentHeight(float partialTick) {
            int index = KeyEditorScreen.this.keyboardEntryWrappers.indexOf((Object)this);
            return (float)(KeyEditorScreen.this.parentScreen.getTopPos() - 65 + 50) - KeyEditorScreen.this.lerpedScroll.getValue(partialTick) + (float)(index * (SimGUITextures.LINKED_TYPEWRITER_KEY_ENTRY.height + 3));
        }

        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt, PoseStack ps) {
            ps.pushPose();
            int x = KeyEditorScreen.this.leftPos() + 12;
            float y = this.getCurrentHeight(pt);
            ps.translate((float)x, y, 0.0f);
            int editIconOffset = 167;
            float iconY = (float)KeyEditorScreen.KEY_ENTRY.height / 2.0f;
            this.updateWidgetPositions(x, 167, y, iconY);
            this.renderItems(guiGraphics, ps);
            ps.popPose();
        }

        public void renderBackground(GuiGraphics guiGraphics, float pt, int mouseX, int mouseY) {
            PoseStack ps = guiGraphics.pose();
            int editIconOffset = 167;
            float iconY = (float)KeyEditorScreen.KEY_ENTRY.height / 2.0f;
            ps.pushPose();
            int x = KeyEditorScreen.this.leftPos() + 12;
            float y = this.getCurrentHeight(pt);
            ps.translate((float)x, y, 0.0f);
            KEY_ENTRY.render(guiGraphics, 0, 0);
            this.renderText(guiGraphics, ps);
            this.renderWidgets(guiGraphics, mouseX, mouseY, pt, ps, 167, iconY);
            ps.popPose();
        }

        private void updateWidgetPositions(int x, int editIconOffset, float y, float iconY) {
            this.editWidget.setX(x + editIconOffset);
            this.editWidget.setY((int)(y + iconY - 9.0f));
            this.deleteWidget.setX(x + editIconOffset + 23);
            this.deleteWidget.setY((int)(y + iconY - 9.0f));
        }

        private void renderItems(GuiGraphics guiGraphics, PoseStack ps) {
            if (!KeyEditorScreen.this.parentScreen.modifier.modifying) {
                ps.pushPose();
                ps.translate(0.0f, (float)KeyEditorScreen.KEY_ENTRY.height / 2.0f - 8.0f, 0.0f);
                ps.translate(82.0f, 0.0f, 0.0f);
                GuiGameElement.of((ItemStack)this.entry.getFirstAsItemStack()).render(guiGraphics);
                ps.translate(18.0f, 0.0f, 0.0f);
                GuiGameElement.of((ItemStack)this.entry.getSecondAsItemStack()).render(guiGraphics);
                ps.popPose();
            }
        }

        private void renderText(GuiGraphics guiGraphics, PoseStack ps) {
            ps.pushPose();
            ps.translate(9.0f, 11.0f, 0.0f);
            guiGraphics.drawString(Minecraft.getInstance().font, InputConstants.getKey((int)this.entry.glfwKeyCode, (int)-1).getDisplayName(), 0, 0, 0xFFFFFF, true);
            ps.popPose();
        }

        private void renderWidgets(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt, PoseStack ps, int editIconOffset, float iconY) {
            ps.pushPose();
            ps.translate((float)editIconOffset, iconY - 9.0f, 0.0f);
            this.editWidget.render(guiGraphics, mouseX, mouseY, pt);
            ps.translate(23.0f, 0.0f, 0.0f);
            this.deleteWidget.render(guiGraphics, mouseX, mouseY, pt);
            ps.popPose();
        }
    }

    public static class NoXYButton
    extends IconButton {
        public NoXYButton(ScreenElement icon) {
            super(0, 0, icon);
        }

        public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                boolean bl = this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
                AllGuiTextures button = !this.active ? AllGuiTextures.BUTTON_DISABLED : (this.isHovered && AllKeys.isMouseButtonDown((int)0) ? AllGuiTextures.BUTTON_DOWN : (this.isHovered ? AllGuiTextures.BUTTON_HOVER : (this.green ? AllGuiTextures.BUTTON_GREEN : AllGuiTextures.BUTTON)));
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                graphics.blit(button.location, 0, 0, button.getStartX(), button.getStartY(), button.getWidth(), button.getHeight());
                this.icon.render(graphics, 1, 1);
            }
        }
    }
}
