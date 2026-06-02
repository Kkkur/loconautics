/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.minecraft.Util
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.schematics.table;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.schematics.client.ClientSchematicLoader;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import com.simibubi.create.content.schematics.table.SchematicTableMenu;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.CreatePaths;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SchematicTableScreen
extends AbstractSimiContainerScreen<SchematicTableMenu> {
    private final Component uploading = CreateLang.translateDirect("gui.schematicTable.uploading", new Object[0]);
    private final Component finished = CreateLang.translateDirect("gui.schematicTable.finished", new Object[0]);
    private final Component refresh = CreateLang.translateDirect("gui.schematicTable.refresh", new Object[0]);
    private final Component folder = CreateLang.translateDirect("gui.schematicTable.open_folder", new Object[0]);
    private final Component noSchematics = CreateLang.translateDirect("gui.schematicTable.noSchematics", new Object[0]);
    private final Component availableSchematicsTitle = CreateLang.translateDirect("gui.schematicTable.availableSchematics", new Object[0]);
    protected AllGuiTextures background;
    private ScrollInput schematicsArea;
    private IconButton confirmButton;
    private IconButton folderButton;
    private IconButton refreshButton;
    private Label schematicsLabel;
    private float progress;
    private float chasingProgress;
    private float lastChasingProgress;
    private final ItemStack renderedItem = AllBlocks.SCHEMATIC_TABLE.asStack();
    private List<Rect2i> extraAreas = Collections.emptyList();

    public SchematicTableScreen(SchematicTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.background = AllGuiTextures.SCHEMATIC_TABLE;
    }

    @Override
    protected void init() {
        this.setWindowSize(this.background.getWidth(), this.background.getHeight() + 4 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        this.setWindowOffset(-11, 8);
        super.init();
        CreateClient.SCHEMATIC_SENDER.refresh();
        List<Component> availableSchematics = CreateClient.SCHEMATIC_SENDER.getAvailableSchematics();
        int x = this.leftPos;
        int y = this.topPos + 2;
        this.schematicsLabel = new Label(x + 51, y + 26, CommonComponents.EMPTY).withShadow();
        this.schematicsLabel.text = CommonComponents.EMPTY;
        if (!availableSchematics.isEmpty()) {
            this.schematicsArea = new SelectionScrollInput(x + 45, y + 21, 139, 18).forOptions(availableSchematics).titled(this.availableSchematicsTitle.plainCopy()).writingTo(this.schematicsLabel);
            this.addRenderableWidget((GuiEventListener)this.schematicsArea);
            this.addRenderableWidget((GuiEventListener)this.schematicsLabel);
        }
        this.confirmButton = new IconButton(x + 44, y + 56, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> {
            if (((SchematicTableMenu)this.menu).canWrite() && this.schematicsArea != null) {
                ClientSchematicLoader schematicSender = CreateClient.SCHEMATIC_SENDER;
                this.progress = 0.0f;
                this.chasingProgress = 0.0f;
                this.lastChasingProgress = 0.0f;
                List<Component> availableSchematics1 = schematicSender.getAvailableSchematics();
                Component schematic = availableSchematics1.get(this.schematicsArea.getState());
                schematicSender.startNewUpload(schematic.getString());
            }
        });
        this.folderButton = new IconButton(x + 20, y + 21, AllIcons.I_OPEN_FOLDER);
        this.folderButton.withCallback(() -> Util.getPlatform().openFile(CreatePaths.SCHEMATICS_DIR.toFile()));
        this.folderButton.setToolTip(this.folder);
        this.refreshButton = new IconButton(x + 206, y + 21, AllIcons.I_REFRESH);
        this.refreshButton.withCallback(() -> {
            ClientSchematicLoader schematicSender = CreateClient.SCHEMATIC_SENDER;
            schematicSender.refresh();
            List<Component> availableSchematics1 = schematicSender.getAvailableSchematics();
            this.removeWidget((GuiEventListener)this.schematicsArea);
            if (!availableSchematics1.isEmpty()) {
                this.schematicsArea = new SelectionScrollInput(this.leftPos + 45, this.topPos + 21, 139, 18).forOptions(availableSchematics1).titled(this.availableSchematicsTitle.plainCopy()).writingTo(this.schematicsLabel);
                this.schematicsArea.onChanged();
                this.addRenderableWidget((GuiEventListener)this.schematicsArea);
            } else {
                this.schematicsArea = null;
                this.schematicsLabel.text = CommonComponents.EMPTY;
            }
        });
        this.refreshButton.setToolTip(this.refresh);
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.addRenderableWidget((GuiEventListener)this.folderButton);
        this.addRenderableWidget((GuiEventListener)this.refreshButton);
        this.extraAreas = ImmutableList.of((Object)new Rect2i(x + this.background.getWidth(), y + this.background.getHeight() - 40, 48, 48), (Object)new Rect2i(this.refreshButton.getX(), this.refreshButton.getY(), this.refreshButton.getWidth(), this.refreshButton.getHeight()));
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int invX = this.getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = this.topPos + this.background.getHeight() + 4;
        this.renderPlayerInventory(graphics, invX, invY);
        int x = this.leftPos;
        int y = this.topPos;
        this.background.render(graphics, x, y);
        Component titleText = ((SchematicTableBlockEntity)((SchematicTableMenu)this.menu).contentHolder).isUploading ? this.uploading : (((SchematicTableMenu)this.menu).getSlot(1).hasItem() ? this.finished : this.title);
        graphics.drawString(this.font, titleText, x + (this.background.getWidth() - 8 - this.font.width((FormattedText)titleText)) / 2, y + 4, 0x505050, false);
        if (this.schematicsArea == null) {
            graphics.drawString(this.font, this.noSchematics, x + 54, y + 26, 0xD3D3D3);
        }
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)this.renderedItem).at((float)(x + this.background.getWidth()), (float)(y + this.background.getHeight() - 40), -200.0f)).scale(3.0).render(graphics);
        int width = (int)((float)AllGuiTextures.SCHEMATIC_TABLE_PROGRESS.getWidth() * Mth.lerp((float)partialTicks, (float)this.lastChasingProgress, (float)this.chasingProgress));
        int height = AllGuiTextures.SCHEMATIC_TABLE_PROGRESS.getHeight();
        graphics.blit(AllGuiTextures.SCHEMATIC_TABLE_PROGRESS.location, x + 70, y + 59, AllGuiTextures.SCHEMATIC_TABLE_PROGRESS.getStartX(), AllGuiTextures.SCHEMATIC_TABLE_PROGRESS.getStartY(), width, height);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        boolean finished = ((SchematicTableMenu)this.menu).getSlot(1).hasItem();
        if (((SchematicTableBlockEntity)((SchematicTableMenu)this.menu).contentHolder).isUploading || finished) {
            if (finished) {
                this.progress = 1.0f;
                this.lastChasingProgress = 1.0f;
                this.chasingProgress = 1.0f;
            } else {
                this.lastChasingProgress = this.chasingProgress;
                this.progress = ((SchematicTableBlockEntity)((SchematicTableMenu)this.menu).contentHolder).uploadingProgress;
                this.chasingProgress += (this.progress - this.chasingProgress) * 0.5f;
            }
            this.confirmButton.active = false;
            if (this.schematicsLabel != null) {
                this.schematicsLabel.colored(0xCCDDFF);
                String uploadingSchematic = ((SchematicTableBlockEntity)((SchematicTableMenu)this.menu).contentHolder).uploadingSchematic;
                this.schematicsLabel.text = uploadingSchematic == null ? null : Component.literal((String)uploadingSchematic);
            }
            if (this.schematicsArea != null) {
                this.schematicsArea.visible = false;
            }
        } else {
            this.progress = 0.0f;
            this.lastChasingProgress = 0.0f;
            this.chasingProgress = 0.0f;
            this.confirmButton.active = true;
            if (this.schematicsLabel != null) {
                this.schematicsLabel.colored(0xFFFFFF);
            }
            if (this.schematicsArea != null) {
                this.schematicsArea.writingTo(this.schematicsLabel);
                this.schematicsArea.visible = true;
            }
        }
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }
}
