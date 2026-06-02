/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.platform.GlStateManager$LogicOp
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.StringSplitter
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.font.TextFieldHelper
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.PageButton
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StringUtil
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.clipboard;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEditPacket;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class ClipboardScreen
extends AbstractSimiScreen {
    public ClipboardContent content;
    public BlockPos targetedBlock;
    List<List<ClipboardEntry>> pages;
    List<ClipboardEntry> currentEntries;
    int editingIndex;
    int frameTick;
    PageButton forward;
    PageButton backward;
    int currentPage = 0;
    long lastClickTime;
    int lastIndex = -1;
    int hoveredEntry;
    boolean hoveredCheck;
    boolean readonly;
    DisplayCache displayCache = DisplayCache.EMPTY;
    TextFieldHelper editContext;
    IconButton closeBtn;
    IconButton clearBtn;
    private final int targetSlot;

    public ClipboardScreen(int targetSlot, DataComponentMap components, @Nullable BlockPos pos) {
        this.targetSlot = targetSlot;
        this.targetedBlock = pos;
        this.reopenWith((ClipboardContent)components.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, (Object)ClipboardContent.EMPTY));
    }

    public void reopenWith(ClipboardContent content) {
        this.content = content;
        this.pages = ClipboardEntry.readAll(content);
        if (this.pages.isEmpty()) {
            this.pages.add(new ArrayList());
        }
        if (this.clearBtn == null) {
            if (content != null) {
                this.currentPage = content.previouslyOpenedPage();
            }
            this.currentPage = Mth.clamp((int)this.currentPage, (int)0, (int)(this.pages.size() - 1));
        }
        this.currentEntries = this.pages.get(this.currentPage);
        boolean startEmpty = this.currentEntries.isEmpty();
        if (startEmpty) {
            this.currentEntries.add(new ClipboardEntry(false, Component.empty()));
        }
        this.editingIndex = 0;
        this.editContext = new TextFieldHelper(this::getCurrentEntryText, this::setCurrentEntryText, this::getClipboard, this::setClipboard, this::validateTextForEntry);
        this.editingIndex = startEmpty ? 0 : -1;
        boolean bl = this.readonly = content != null && content.readOnly();
        if (this.readonly) {
            this.editingIndex = -1;
        }
        if (this.clearBtn != null) {
            this.init();
        }
    }

    protected void init() {
        this.setWindowSize(256, 256);
        super.init();
        this.clearDisplayCache();
        int x = this.guiLeft;
        int y = this.guiTop - 8;
        this.clearWidgets();
        this.clearBtn = (IconButton)new IconButton(x + 234, y + 153, AllIcons.I_CLEAR_CHECKED).withCallback(() -> {
            this.editingIndex = -1;
            this.currentEntries.removeIf(ce -> ce.checked);
            if (this.currentEntries.isEmpty()) {
                this.currentEntries.add(new ClipboardEntry(false, Component.empty()));
            }
            this.sendIfEditingBlock();
        });
        this.clearBtn.setToolTip((Component)CreateLang.translateDirect("gui.clipboard.erase_checked", new Object[0]));
        this.closeBtn = (IconButton)new IconButton(x + 234, y + 175, AllIcons.I_PRIORITY_VERY_LOW).withCallback(() -> this.minecraft.setScreen(null));
        this.closeBtn.setToolTip((Component)CreateLang.translateDirect("station.close", new Object[0]));
        this.addRenderableWidget((GuiEventListener)this.closeBtn);
        this.addRenderableWidget((GuiEventListener)this.clearBtn);
        this.forward = new PageButton(x + 176, y + 229, true, $ -> this.changePage(true), true);
        this.backward = new PageButton(x + 53, y + 229, false, $ -> this.changePage(false), true);
        this.addRenderableWidget((GuiEventListener)this.forward);
        this.addRenderableWidget((GuiEventListener)this.backward);
        this.forward.visible = this.currentPage < 50 && (!this.readonly || this.currentPage + 1 < this.pages.size());
        this.backward.visible = this.currentPage > 0;
    }

    private int getNumPages() {
        return this.pages.size();
    }

    public void tick() {
        super.tick();
        ++this.frameTick;
        if (this.targetedBlock != null) {
            if (!this.minecraft.player.canInteractWithBlock(this.targetedBlock, 10.0)) {
                this.minecraft.setScreen(null);
                return;
            }
            if (!AllBlocks.CLIPBOARD.has(this.minecraft.level.getBlockState(this.targetedBlock))) {
                this.minecraft.setScreen(null);
                return;
            }
        }
        int mx = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
        int my = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
        this.hoveredCheck = false;
        this.hoveredEntry = -1;
        if ((mx -= this.guiLeft + 35) > 0 && mx < 183 && (my -= this.guiTop + 41) > 0 && my < 190) {
            this.hoveredCheck = mx < 20;
            int totalHeight = 0;
            for (int i = 0; i < this.currentEntries.size(); ++i) {
                ClipboardEntry clipboardEntry = this.currentEntries.get(i);
                String text = clipboardEntry.text.getString();
                if ((totalHeight += Math.max(12, this.font.split((FormattedText)Component.literal((String)text), clipboardEntry.icon.isEmpty() ? 150 : 130).size() * 9 + 3)) <= my) continue;
                this.hoveredEntry = i;
                return;
            }
            this.hoveredEntry = this.currentEntries.size();
        }
    }

    private String getCurrentEntryText() {
        return this.currentEntries.get((int)this.editingIndex).text.getString();
    }

    private void setCurrentEntryText(String text) {
        this.currentEntries.get((int)this.editingIndex).text = Component.literal((String)text);
        this.sendIfEditingBlock();
    }

    private void setClipboard(String p_98148_) {
        if (this.minecraft != null) {
            TextFieldHelper.setClipboardContents((Minecraft)this.minecraft, (String)p_98148_);
        }
    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents((Minecraft)this.minecraft) : "";
    }

    private boolean validateTextForEntry(String newText) {
        int totalHeight = 0;
        for (int i = 0; i < this.currentEntries.size(); ++i) {
            ClipboardEntry clipboardEntry = this.currentEntries.get(i);
            String text = i == this.editingIndex ? newText : clipboardEntry.text.getString();
            totalHeight += Math.max(12, this.font.split((FormattedText)Component.literal((String)text), 150).size() * 9 + 3);
        }
        return totalHeight < 185;
    }

    private int yOffsetOfEditingEntry() {
        int totalHeight = 0;
        for (int i = 0; i < this.currentEntries.size() && i != this.editingIndex; ++i) {
            ClipboardEntry clipboardEntry = this.currentEntries.get(i);
            totalHeight += Math.max(12, this.font.split((FormattedText)clipboardEntry.text, 150).size() * 9 + 3);
        }
        return totalHeight;
    }

    private void changePage(boolean next) {
        int previously = this.currentPage;
        this.currentPage = Mth.clamp((int)(this.currentPage + (next ? 1 : -1)), (int)0, (int)50);
        if (this.currentPage == previously) {
            return;
        }
        this.editingIndex = -1;
        if (this.pages.size() <= this.currentPage) {
            if (this.readonly) {
                this.currentPage = previously;
                return;
            }
            this.pages.add(new ArrayList());
        }
        this.currentEntries = this.pages.get(this.currentPage);
        if (this.currentEntries.isEmpty()) {
            this.currentEntries.add(new ClipboardEntry(false, Component.empty()));
            if (!this.readonly) {
                this.editingIndex = 0;
                this.editContext.setCursorToEnd();
                this.clearDisplayCacheAfterChange();
            }
        }
        this.forward.visible = this.currentPage < 50 && (!this.readonly || this.currentPage + 1 < this.pages.size());
        boolean bl = this.backward.visible = this.currentPage > 0;
        if (next) {
            return;
        }
        if (this.pages.get(this.currentPage + 1).stream().allMatch(ce -> ce.text.getString().isBlank())) {
            this.pages.remove(this.currentPage + 1);
        }
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop - 8;
        AllGuiTextures.CLIPBOARD.render(graphics, x, y);
        graphics.drawString(this.font, (Component)Component.translatable((String)"book.pageIndicator", (Object[])new Object[]{this.currentPage + 1, this.getNumPages()}), x + 150, y + 9, 0x43FFFFFF, false);
        for (int i = 0; i < this.currentEntries.size(); ++i) {
            boolean isAddress;
            ClipboardEntry clipboardEntry = this.currentEntries.get(i);
            boolean checked = clipboardEntry.checked;
            int iconOffset = clipboardEntry.icon.isEmpty() ? 0 : 16;
            MutableComponent text = clipboardEntry.text;
            String string = text.getString();
            boolean bl = isAddress = string.startsWith("#") && !string.substring(1).isBlank();
            if (isAddress) {
                RenderSystem.enableBlend();
                (checked ? AllGuiTextures.CLIPBOARD_ADDRESS_INACTIVE : AllGuiTextures.CLIPBOARD_ADDRESS).render(graphics, x + 44, y + 50);
                text = Component.literal((String)string.substring(1).stripLeading());
            } else {
                graphics.drawString(this.font, "\u25a1", x + 45, y + 51, checked ? 1720549227 : -7504021, false);
                if (checked) {
                    graphics.drawString(this.font, "\u2714", x + 45, y + 50, 3256925, false);
                }
            }
            List split = this.font.split((FormattedText)text, 150 - iconOffset);
            if (split.isEmpty()) {
                y += 12;
                continue;
            }
            if (!clipboardEntry.icon.isEmpty()) {
                graphics.renderItem(clipboardEntry.icon, x + 54, y + 50);
            }
            for (FormattedCharSequence sequence : split) {
                if (i != this.editingIndex) {
                    graphics.drawString(this.font, sequence, x + 58 + iconOffset, y + 50, checked ? (isAddress ? 1720549227 : 3256925) : 3217920, false);
                }
                y += 9;
            }
            y += 3;
        }
        if (this.editingIndex == -1) {
            return;
        }
        this.setFocused(null);
        DisplayCache cache = this.getDisplayCache();
        for (LineInfo line : cache.lines) {
            graphics.drawString(this.font, line.asComponent, line.x, line.y, 3217920, false);
        }
        this.renderHighlight(cache.selection);
        this.renderCursor(graphics, cache.cursor, cache.cursorAtEnd);
    }

    public void removed() {
        this.pages.forEach(list -> list.removeIf(ce -> ce.text.getString().isBlank()));
        this.pages.removeIf(List::isEmpty);
        for (int i = 0; i < this.pages.size(); ++i) {
            if (this.pages.get(i) != this.currentEntries) continue;
            this.content = this.content.setPreviouslyOpenedPage(i);
        }
        this.send();
        super.removed();
    }

    private void sendIfEditingBlock() {
        ClientPacketListener handler = this.minecraft.player.connection;
        if (handler.getOnlinePlayers().size() > 1 && this.targetedBlock != null) {
            this.send();
        }
    }

    private void send() {
        this.content = this.content.setPages(this.pages);
        this.content = this.content.setType(ClipboardOverrides.ClipboardType.WRITTEN);
        ClipboardContent toSend = null;
        if (!this.pages.isEmpty()) {
            toSend = this.content;
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ClipboardEditPacket(this.targetSlot, toSend, this.targetedBlock));
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        this.changePage(pScrollY < 0.0);
        return true;
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 266) {
            this.backward.onPress();
            return true;
        }
        if (pKeyCode == 267) {
            this.forward.onPress();
            return true;
        }
        if (this.editingIndex != -1 && pKeyCode != 256) {
            this.keyPressedWhileEditing(pKeyCode, pScanCode, pModifiers);
            this.clearDisplayCache();
            return true;
        }
        super.keyPressed(pKeyCode, pScanCode, pModifiers);
        return true;
    }

    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (super.charTyped(pCodePoint, pModifiers)) {
            return true;
        }
        if (!StringUtil.isAllowedChatCharacter((char)pCodePoint)) {
            return false;
        }
        if (this.editingIndex == -1) {
            return false;
        }
        this.editContext.insertText(Character.toString(pCodePoint));
        this.clearDisplayCache();
        return true;
    }

    private boolean keyPressedWhileEditing(int pKeyCode, int pScanCode, int pModifiers) {
        if (Screen.isSelectAll((int)pKeyCode)) {
            this.editContext.selectAll();
            return true;
        }
        if (Screen.isCopy((int)pKeyCode)) {
            this.editContext.copy();
            return true;
        }
        if (Screen.isPaste((int)pKeyCode)) {
            this.editContext.paste();
            return true;
        }
        if (Screen.isCut((int)pKeyCode)) {
            this.editContext.cut();
            return true;
        }
        switch (pKeyCode) {
            case 257: 
            case 335: {
                if (ClipboardScreen.hasShiftDown()) {
                    this.editContext.insertText("\n");
                    return true;
                }
                if (!ClipboardScreen.hasControlDown()) {
                    if (this.currentEntries.size() <= this.editingIndex + 1 || !this.currentEntries.get((int)(this.editingIndex + 1)).text.getString().isEmpty()) {
                        this.currentEntries.add(this.editingIndex + 1, new ClipboardEntry(false, Component.empty()));
                    }
                    ++this.editingIndex;
                    this.editContext.setCursorToEnd();
                    if (this.validateTextForEntry(" ")) {
                        return true;
                    }
                    this.currentEntries.remove(this.editingIndex);
                    --this.editingIndex;
                    this.editContext.setCursorToEnd();
                    return true;
                }
                this.editingIndex = -1;
                return true;
            }
            case 259: {
                if (this.currentEntries.get((int)this.editingIndex).text.getString().isEmpty() && this.currentEntries.size() > 1) {
                    this.currentEntries.remove(this.editingIndex);
                    this.editingIndex = Math.max(0, this.editingIndex - 1);
                    this.editContext.setCursorToEnd();
                    return true;
                }
                if (ClipboardScreen.hasControlDown()) {
                    int prevPos = this.editContext.getCursorPos();
                    this.editContext.moveByWords(-1);
                    if (prevPos != this.editContext.getCursorPos()) {
                        this.editContext.removeCharsFromCursor(prevPos - this.editContext.getCursorPos());
                    }
                    return true;
                }
                this.editContext.removeCharsFromCursor(-1);
                return true;
            }
            case 261: {
                if (ClipboardScreen.hasControlDown()) {
                    int prevPos = this.editContext.getCursorPos();
                    this.editContext.moveByWords(1);
                    if (prevPos != this.editContext.getCursorPos()) {
                        this.editContext.removeCharsFromCursor(prevPos - this.editContext.getCursorPos());
                    }
                    return true;
                }
                this.editContext.removeCharsFromCursor(1);
                return true;
            }
            case 262: {
                if (ClipboardScreen.hasControlDown()) {
                    this.editContext.moveByWords(1, Screen.hasShiftDown());
                    return true;
                }
                this.editContext.moveByChars(1, Screen.hasShiftDown());
                return true;
            }
            case 263: {
                if (ClipboardScreen.hasControlDown()) {
                    this.editContext.moveByWords(-1, Screen.hasShiftDown());
                    return true;
                }
                this.editContext.moveByChars(-1, Screen.hasShiftDown());
                return true;
            }
            case 264: {
                this.keyDown();
                return true;
            }
            case 265: {
                this.keyUp();
                return true;
            }
            case 268: {
                this.keyHome();
                return true;
            }
            case 269: {
                this.keyEnd();
                return true;
            }
        }
        return false;
    }

    private void keyUp() {
        this.changeLine(-1);
    }

    private void keyDown() {
        this.changeLine(1);
    }

    private void changeLine(int pYChange) {
        int i = this.editContext.getCursorPos();
        int j = this.getDisplayCache().changeLine(i, pYChange);
        this.editContext.setCursorPos(j, Screen.hasShiftDown());
    }

    private void keyHome() {
        int i = this.editContext.getCursorPos();
        int j = this.getDisplayCache().findLineStart(i);
        this.editContext.setCursorPos(j, Screen.hasShiftDown());
    }

    private void keyEnd() {
        DisplayCache cache = this.getDisplayCache();
        int i = this.editContext.getCursorPos();
        int j = cache.findLineEnd(i);
        this.editContext.setCursorPos(j, Screen.hasShiftDown());
    }

    private void renderCursor(GuiGraphics graphics, Pos2i pCursorPos, boolean pIsEndOfText) {
        if (this.frameTick / 6 % 2 != 0) {
            return;
        }
        pCursorPos = this.convertLocalToScreen(pCursorPos);
        if (!pIsEndOfText) {
            graphics.fill(pCursorPos.x, pCursorPos.y - 1, pCursorPos.x + 1, pCursorPos.y + 9, -16777216);
        } else {
            graphics.drawString(this.font, "_", (float)pCursorPos.x, (float)pCursorPos.y, 0, false);
        }
    }

    private void renderHighlight(Rect2i[] pSelected) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor((float)0.0f, (float)0.0f, (float)255.0f, (float)255.0f);
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp((GlStateManager.LogicOp)GlStateManager.LogicOp.OR_REVERSE);
        for (Rect2i rect2i : pSelected) {
            int i = rect2i.getX();
            int j = rect2i.getY();
            int k = i + rect2i.getWidth();
            int l = j + rect2i.getHeight();
            bufferbuilder.addVertex((float)i, (float)l, 0.0f);
            bufferbuilder.addVertex((float)k, (float)l, 0.0f);
            bufferbuilder.addVertex((float)k, (float)j, 0.0f);
            bufferbuilder.addVertex((float)i, (float)j, 0.0f);
        }
        @Nullable MeshData meshData = bufferbuilder.build();
        if (meshData != null) {
            BufferUploader.drawWithShader((MeshData)meshData);
        }
        RenderSystem.disableColorLogicOp();
    }

    private Pos2i convertScreenToLocal(Pos2i pScreenPos) {
        return new Pos2i(pScreenPos.x - (this.width - 192) / 2 - 36 + 10, pScreenPos.y - 32 - 24 - this.yOffsetOfEditingEntry() - this.guiTop + 14);
    }

    private Pos2i convertLocalToScreen(Pos2i pLocalScreenPos) {
        return new Pos2i(pLocalScreenPos.x + (this.width - 192) / 2 + 36 - 10, pLocalScreenPos.y + 32 + 24 + this.yOffsetOfEditingEntry() + this.guiTop - 14);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
            return true;
        }
        if (pButton != 0) {
            return true;
        }
        if (this.hoveredEntry != -1) {
            if (this.hoveredCheck) {
                this.editingIndex = -1;
                if (this.hoveredEntry < this.currentEntries.size()) {
                    this.currentEntries.get((int)this.hoveredEntry).checked ^= true;
                    if (this.currentEntries.get((int)this.hoveredEntry).checked) {
                        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)AllSoundEvents.CLIPBOARD_CHECKMARK.getMainEvent(), (float)(0.95f + (float)Math.random() * 0.05f)));
                    } else {
                        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)AllSoundEvents.CLIPBOARD_ERASE.getMainEvent(), (float)(0.9f + (float)Math.random() * 0.2f)));
                    }
                }
                this.sendIfEditingBlock();
                return true;
            }
            if (this.hoveredEntry != this.editingIndex && !this.readonly) {
                this.editingIndex = this.hoveredEntry;
                if (this.hoveredEntry >= this.currentEntries.size()) {
                    this.currentEntries.add(new ClipboardEntry(false, Component.empty()));
                    if (!this.validateTextForEntry(" ")) {
                        this.currentEntries.remove(this.hoveredEntry);
                        this.editingIndex = -1;
                        return true;
                    }
                }
                this.clearDisplayCacheAfterChange();
            }
        }
        if (this.editingIndex == -1) {
            return false;
        }
        if (pMouseX < (double)(this.guiLeft + 50) || pMouseX > (double)(this.guiLeft + 220) || pMouseY < (double)(this.guiTop + 30) || pMouseY > (double)(this.guiTop + 230)) {
            this.setFocused(null);
            this.clearDisplayCache();
            this.editingIndex = -1;
            return false;
        }
        long i = Util.getMillis();
        DisplayCache cache = this.getDisplayCache();
        int j = cache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)pMouseX, (int)pMouseY)));
        if (j >= 0) {
            if (j == this.lastIndex && i - this.lastClickTime < 250L) {
                if (!this.editContext.isSelecting()) {
                    this.selectWord(j);
                } else {
                    this.editContext.selectAll();
                }
            } else {
                this.editContext.setCursorPos(j, Screen.hasShiftDown());
            }
            this.clearDisplayCache();
        }
        this.lastIndex = j;
        this.lastClickTime = i;
        return true;
    }

    private void selectWord(int pIndex) {
        String s = this.getCurrentEntryText();
        this.editContext.setSelectionRange(StringSplitter.getWordPosition((String)s, (int)-1, (int)pIndex, (boolean)false), StringSplitter.getWordPosition((String)s, (int)1, (int)pIndex, (boolean)false));
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
            return true;
        }
        if (pButton != 0) {
            return true;
        }
        if (this.editingIndex == -1) {
            return false;
        }
        DisplayCache cache = this.getDisplayCache();
        int i = cache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)pMouseX, (int)pMouseY)));
        this.editContext.setCursorPos(i, true);
        this.clearDisplayCache();
        return true;
    }

    private DisplayCache getDisplayCache() {
        if (this.displayCache == null) {
            this.displayCache = this.rebuildDisplayCache();
        }
        return this.displayCache;
    }

    private void clearDisplayCache() {
        this.displayCache = null;
    }

    private void clearDisplayCacheAfterChange() {
        this.editContext.setCursorToEnd();
        this.clearDisplayCache();
    }

    private DisplayCache rebuildDisplayCache() {
        Pos2i pos;
        boolean flag;
        String current = this.getCurrentEntryText();
        boolean address = current.startsWith("#") && !current.substring(1).isBlank();
        int offset = 0;
        if (address) {
            String stripped = current.substring(1).stripLeading();
            offset = current.length() - stripped.length();
            current = stripped;
        }
        if (current.isEmpty()) {
            return DisplayCache.EMPTY;
        }
        String s = current;
        int i = this.editContext.getCursorPos();
        int j = this.editContext.getSelectionPos();
        i = Mth.clamp((int)(i - offset), (int)0, (int)s.length());
        j = Mth.clamp((int)(j - offset), (int)0, (int)s.length());
        IntArrayList intlist = new IntArrayList();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableint = new MutableInt();
        MutableBoolean mutableboolean = new MutableBoolean();
        StringSplitter stringsplitter = this.font.getSplitter();
        stringsplitter.splitLines(s, 150, Style.EMPTY, true, (arg_0, arg_1, arg_2) -> this.lambda$rebuildDisplayCache$8(mutableint, s, mutableboolean, (IntList)intlist, list, arg_0, arg_1, arg_2));
        int[] aint = intlist.toIntArray();
        boolean bl = flag = i == s.length();
        if (flag && mutableboolean.isTrue()) {
            pos = new Pos2i(0, list.size() * 9);
        } else {
            int k = ClipboardScreen.findLineFromPos(aint, i);
            int l = this.font.width(s.substring(aint[k], i));
            pos = new Pos2i(l, k * 9);
        }
        ArrayList list1 = Lists.newArrayList();
        if (i != j) {
            int k1;
            int l2 = Math.min(i, j);
            int i1 = Math.max(i, j);
            int j1 = ClipboardScreen.findLineFromPos(aint, l2);
            if (j1 == (k1 = ClipboardScreen.findLineFromPos(aint, i1))) {
                int l1 = j1 * 9;
                int i2 = aint[j1];
                list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i1, l1, i2));
            } else {
                int i3 = j1 + 1 > aint.length ? s.length() : aint[j1 + 1];
                list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i3, j1 * 9, aint[j1]));
                for (int j3 = j1 + 1; j3 < k1; ++j3) {
                    int j2 = j3 * 9;
                    String s1 = s.substring(aint[j3], aint[j3 + 1]);
                    int k2 = (int)stringsplitter.stringWidth(s1);
                    list1.add(this.createSelection(new Pos2i(0, j2), new Pos2i(k2, j2 + 9)));
                }
                list1.add(this.createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1]));
            }
        }
        return new DisplayCache(s, pos, flag, aint, list.toArray(new LineInfo[0]), list1.toArray(new Rect2i[0]));
    }

    static int findLineFromPos(int[] pLineStarts, int pFind) {
        int i = Arrays.binarySearch(pLineStarts, pFind);
        return i < 0 ? -(i + 2) : i;
    }

    private Rect2i createPartialLineSelection(String pInput, StringSplitter pSplitter, int p_98122_, int p_98123_, int p_98124_, int p_98125_) {
        String s = pInput.substring(p_98125_, p_98122_);
        String s1 = pInput.substring(p_98125_, p_98123_);
        Pos2i firstPos = new Pos2i((int)pSplitter.stringWidth(s), p_98124_);
        Pos2i secondPos = new Pos2i((int)pSplitter.stringWidth(s1), p_98124_ + 9);
        return this.createSelection(firstPos, secondPos);
    }

    private Rect2i createSelection(Pos2i pCorner1, Pos2i pCorner2) {
        Pos2i firstPos = this.convertLocalToScreen(pCorner1);
        Pos2i secondPos = this.convertLocalToScreen(pCorner2);
        int i = Math.min(firstPos.x, secondPos.x);
        int j = Math.max(firstPos.x, secondPos.x);
        int k = Math.min(firstPos.y, secondPos.y);
        int l = Math.max(firstPos.y, secondPos.y);
        return new Rect2i(i, k, j - i, l - k);
    }

    private /* synthetic */ void lambda$rebuildDisplayCache$8(MutableInt mutableint, String s, MutableBoolean mutableboolean, IntList intlist, List list, Style p_98132_, int p_98133_, int p_98134_) {
        int k3 = mutableint.getAndIncrement();
        String s2 = s.substring(p_98133_, p_98134_);
        mutableboolean.setValue(s2.endsWith("\n"));
        String s3 = StringUtils.stripEnd((String)s2, (String)" \n");
        int l3 = k3 * 9;
        Pos2i pos1 = this.convertLocalToScreen(new Pos2i(0, l3));
        intlist.add(p_98133_);
        list.add(new LineInfo(p_98132_, s3, pos1.x, pos1.y));
    }

    @OnlyIn(value=Dist.CLIENT)
    static class DisplayCache {
        static final DisplayCache EMPTY = new DisplayCache("", new Pos2i(0, 0), true, new int[]{0}, new LineInfo[]{new LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String fullText;
        final Pos2i cursor;
        final boolean cursorAtEnd;
        private final int[] lineStarts;
        final LineInfo[] lines;
        final Rect2i[] selection;

        public DisplayCache(String pFullText, Pos2i pCursor, boolean pCursorAtEnd, int[] pLineStarts, LineInfo[] pLines, Rect2i[] pSelection) {
            this.fullText = pFullText;
            this.cursor = pCursor;
            this.cursorAtEnd = pCursorAtEnd;
            this.lineStarts = pLineStarts;
            this.lines = pLines;
            this.selection = pSelection;
        }

        public int getIndexAtPosition(Font pFont, Pos2i pCursorPosition) {
            int i = pCursorPosition.y / 9;
            if (i < 0) {
                return 0;
            }
            if (i >= this.lines.length) {
                return this.fullText.length();
            }
            LineInfo line = this.lines[i];
            return this.lineStarts[i] + pFont.getSplitter().plainIndexAtWidth(line.contents, pCursorPosition.x, line.style);
        }

        public int changeLine(int pXChange, int pYChange) {
            int k;
            int i = ClipboardScreen.findLineFromPos(this.lineStarts, pXChange);
            int j = i + pYChange;
            if (0 <= j && j < this.lineStarts.length) {
                int l = pXChange - this.lineStarts[i];
                int i1 = this.lines[j].contents.length();
                k = this.lineStarts[j] + Math.min(l, i1);
            } else {
                k = pXChange;
            }
            return k;
        }

        public int findLineStart(int pLine) {
            int i = ClipboardScreen.findLineFromPos(this.lineStarts, pLine);
            return this.lineStarts[i];
        }

        public int findLineEnd(int pLine) {
            int i = ClipboardScreen.findLineFromPos(this.lineStarts, pLine);
            return this.lineStarts[i] + this.lines[i].contents.length();
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    static class LineInfo {
        final Style style;
        final String contents;
        final Component asComponent;
        final int x;
        final int y;

        public LineInfo(Style pStyle, String pContents, int pX, int pY) {
            this.style = pStyle;
            this.contents = pContents;
            this.x = pX;
            this.y = pY;
            this.asComponent = Component.literal((String)pContents).setStyle(pStyle);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    record Pos2i(int x, int y) {
    }
}
