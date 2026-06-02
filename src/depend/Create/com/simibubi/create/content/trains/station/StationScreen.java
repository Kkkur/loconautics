/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.gui.TextureSheetSegment
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 */
package com.simibubi.create.content.trains.station;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.content.decoration.slidingDoor.DoorControl;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainIconType;
import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.AssemblyScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationEditPacket;
import com.simibubi.create.content.trains.station.TrainEditPacket;
import com.simibubi.create.content.trains.station.WideIconButton;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;

public class StationScreen
extends AbstractStationScreen {
    private EditBox nameBox;
    private EditBox trainNameBox;
    private IconButton newTrainButton;
    private IconButton disassembleTrainButton;
    private IconButton dropScheduleButton;
    private int leavingAnimation;
    private LerpedFloat trainPosition;
    private DoorControl doorControl;
    private ScrollInput colorTypeScroll;
    private int messedWithColors;
    private boolean switchingToAssemblyMode;

    public StationScreen(StationBlockEntity be, GlobalStation station) {
        super(be, station);
        this.background = AllGuiTextures.STATION;
        this.leavingAnimation = 0;
        this.trainPosition = LerpedFloat.linear().startWithValue(0.0);
        this.switchingToAssemblyMode = false;
        this.doorControl = be.doorControls.mode;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        Consumer<String> onTextChanged = s -> this.nameBox.setX(this.nameBoxX((String)s, this.nameBox));
        this.nameBox = new EditBox((Font)new NoShadowFontWrapper(this.font), x + 23, y + 4, this.background.getWidth() - 20, 10, (Component)Component.literal((String)this.station.name));
        this.nameBox.setBordered(false);
        this.nameBox.setMaxLength(25);
        this.nameBox.setTextColor(5841956);
        this.nameBox.setValue(this.station.name);
        this.nameBox.setFocused(false);
        this.nameBox.mouseClicked(0.0, 0.0, 0);
        this.nameBox.setResponder(onTextChanged);
        this.nameBox.setX(this.nameBoxX(this.nameBox.getValue(), this.nameBox));
        this.addRenderableWidget((GuiEventListener)this.nameBox);
        Runnable assemblyCallback = () -> {
            this.switchingToAssemblyMode = true;
            this.minecraft.setScreen((Screen)new AssemblyScreen(this.blockEntity, this.station));
        };
        this.newTrainButton = new WideIconButton(x + 84, y + 65, AllGuiTextures.I_NEW_TRAIN);
        this.newTrainButton.withCallback(assemblyCallback);
        this.addRenderableWidget((GuiEventListener)this.newTrainButton);
        this.disassembleTrainButton = new WideIconButton(x + 94, y + 65, AllGuiTextures.I_DISASSEMBLE_TRAIN);
        this.disassembleTrainButton.active = false;
        this.disassembleTrainButton.visible = false;
        this.disassembleTrainButton.withCallback(assemblyCallback);
        this.addRenderableWidget((GuiEventListener)this.disassembleTrainButton);
        this.dropScheduleButton = new IconButton(x + 73, y + 65, AllIcons.I_VIEW_SCHEDULE);
        this.dropScheduleButton.active = false;
        this.dropScheduleButton.visible = false;
        this.dropScheduleButton.withCallback(() -> CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.dropSchedule(this.blockEntity.getBlockPos())));
        this.addRenderableWidget((GuiEventListener)this.dropScheduleButton);
        this.colorTypeScroll = new ScrollInput(x + 166, y + 17, 22, 14).titled(CreateLang.translateDirect("station.train_map_color", new Object[0]));
        this.colorTypeScroll.withRange(0, 16);
        this.colorTypeScroll.withStepFunction(ctx -> this.colorTypeScroll.standardStep().apply((ScrollValueBehaviour.StepContext)ctx));
        this.colorTypeScroll.calling(s -> {
            Train train = (Train)this.displayedTrain.get();
            if (train != null) {
                train.mapColorIndex = s;
                this.messedWithColors = 10;
            }
        });
        this.colorTypeScroll.visible = false;
        this.colorTypeScroll.active = false;
        this.addRenderableWidget((GuiEventListener)this.colorTypeScroll);
        onTextChanged = s -> this.trainNameBox.setX(this.nameBoxX((String)s, this.trainNameBox));
        this.trainNameBox = new EditBox(this.font, x + 23, y + 47, this.background.getWidth() - 75, 10, CommonComponents.EMPTY);
        this.trainNameBox.setBordered(false);
        this.trainNameBox.setMaxLength(35);
        this.trainNameBox.setTextColor(0xC6C6C6);
        this.trainNameBox.setFocused(false);
        this.trainNameBox.mouseClicked(0.0, 0.0, 0);
        this.trainNameBox.setResponder(onTextChanged);
        this.trainNameBox.active = false;
        this.tickTrainDisplay();
        Pair<ScrollInput, Label> doorControlWidgets = DoorControl.createWidget(x + 35, y + 102, mode -> {
            this.doorControl = mode;
        }, this.doorControl);
        this.addRenderableWidget((GuiEventListener)((ScrollInput)((Object)doorControlWidgets.getFirst())));
        this.addRenderableWidget((GuiEventListener)((Label)((Object)doorControlWidgets.getSecond())));
    }

    @Override
    public void tick() {
        this.tickTrainDisplay();
        if (this.getFocused() != this.nameBox) {
            this.nameBox.setCursorPosition(this.nameBox.getValue().length());
            this.nameBox.setHighlightPos(this.nameBox.getCursorPosition());
        }
        if (this.getFocused() != this.trainNameBox || !this.trainNameBox.active) {
            this.trainNameBox.setCursorPosition(this.trainNameBox.getValue().length());
            this.trainNameBox.setHighlightPos(this.trainNameBox.getCursorPosition());
        }
        if (this.messedWithColors > 0) {
            --this.messedWithColors;
            if (this.messedWithColors == 0) {
                this.syncTrainNameAndColor();
            }
        }
        super.tick();
        this.updateAssemblyTooltip(this.blockEntity.edgePoint.isOnCurve() ? "no_assembly_curve" : (!this.blockEntity.edgePoint.isOrthogonal() ? "no_assembly_diagonal" : (this.trainPresent() && !this.blockEntity.trainCanDisassemble ? "train_not_aligned" : null)));
    }

    private void tickTrainDisplay() {
        Train train = (Train)this.displayedTrain.get();
        if (train == null) {
            if (this.trainNameBox.active) {
                this.trainNameBox.active = false;
                this.removeWidget((GuiEventListener)this.trainNameBox);
            }
            this.leavingAnimation = 0;
            this.newTrainButton.active = this.blockEntity.edgePoint.isOrthogonal();
            this.newTrainButton.visible = true;
            this.colorTypeScroll.visible = false;
            this.colorTypeScroll.active = false;
            Train imminentTrain = this.getImminent();
            if (imminentTrain != null) {
                this.displayedTrain = new WeakReference<Train>(imminentTrain);
                this.newTrainButton.active = false;
                this.newTrainButton.visible = false;
                this.disassembleTrainButton.active = false;
                this.disassembleTrainButton.visible = true;
                this.dropScheduleButton.active = this.blockEntity.trainHasSchedule;
                this.dropScheduleButton.visible = true;
                if (this.mapModsPresent()) {
                    this.colorTypeScroll.setState(imminentTrain.mapColorIndex);
                    this.colorTypeScroll.visible = true;
                    this.colorTypeScroll.active = true;
                }
                this.trainNameBox.active = true;
                this.trainNameBox.setValue(imminentTrain.name.getString());
                this.trainNameBox.setX(this.nameBoxX(this.trainNameBox.getValue(), this.trainNameBox));
                this.addRenderableWidget((GuiEventListener)this.trainNameBox);
                int trainIconWidth = this.getTrainIconWidth(imminentTrain);
                int targetPos = this.background.getWidth() / 2 - trainIconWidth / 2;
                if (trainIconWidth > 130) {
                    targetPos -= trainIconWidth - 130;
                }
                float f = (float)(imminentTrain.navigation.distanceToDestination / 15.0);
                if (this.trainPresent()) {
                    f = 0.0f;
                }
                this.trainPosition.startWithValue((double)((float)targetPos - (float)(targetPos + 5) * f));
            }
            return;
        }
        int trainIconWidth = this.getTrainIconWidth(train);
        int targetPos = this.background.getWidth() / 2 - trainIconWidth / 2;
        if (trainIconWidth > 130) {
            targetPos -= trainIconWidth - 130;
        }
        if (this.leavingAnimation > 0) {
            this.colorTypeScroll.visible = false;
            this.colorTypeScroll.active = false;
            this.disassembleTrainButton.active = false;
            float f = 1.0f - (float)this.leavingAnimation / 80.0f;
            this.trainPosition.setValue((double)((float)targetPos + f * f * f * (float)(this.background.getWidth() - targetPos + 5)));
            --this.leavingAnimation;
            if (this.leavingAnimation > 0) {
                return;
            }
            this.displayedTrain = new WeakReference<Object>(null);
            this.disassembleTrainButton.visible = false;
            this.dropScheduleButton.active = false;
            this.dropScheduleButton.visible = false;
            return;
        }
        if (this.getImminent() != train) {
            this.leavingAnimation = 80;
            return;
        }
        boolean trainAtStation = this.trainPresent();
        this.disassembleTrainButton.active = trainAtStation && this.blockEntity.trainCanDisassemble && this.blockEntity.edgePoint.isOrthogonal();
        this.dropScheduleButton.active = this.blockEntity.trainHasSchedule;
        if (this.blockEntity.trainHasSchedule) {
            this.dropScheduleButton.setToolTip((Component)CreateLang.translateDirect(this.blockEntity.trainHasAutoSchedule ? "station.remove_auto_schedule" : "station.remove_schedule", new Object[0]));
        } else {
            this.dropScheduleButton.getToolTip().clear();
        }
        float f = trainAtStation ? 0.0f : (float)(train.navigation.distanceToDestination / 30.0);
        this.trainPosition.setValue((double)((float)targetPos - (float)(targetPos + trainIconWidth) * f));
    }

    private int nameBoxX(String s, EditBox nameBox) {
        return this.guiLeft + this.background.getWidth() / 2 - (Math.min(this.font.width(s), nameBox.getWidth()) + 10) / 2;
    }

    private void updateAssemblyTooltip(String key) {
        if (key == null) {
            this.disassembleTrainButton.setToolTip((Component)CreateLang.translateDirect("station.disassemble_train", new Object[0]));
            this.newTrainButton.setToolTip((Component)CreateLang.translateDirect("station.create_train", new Object[0]));
            return;
        }
        for (IconButton ib : new IconButton[]{this.disassembleTrainButton, this.newTrainButton}) {
            List toolTip = ib.getToolTip();
            toolTip.clear();
            toolTip.add(CreateLang.translateDirect("station." + key, new Object[0]).withStyle(ChatFormatting.GRAY));
            toolTip.add(CreateLang.translateDirect("station." + key + "_1", new Object[0]).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(graphics, mouseX, mouseY, partialTicks);
        int x = this.guiLeft;
        int y = this.guiTop;
        String text = this.nameBox.getValue();
        if (!this.nameBox.isFocused()) {
            AllGuiTextures.STATION_EDIT_NAME.render(graphics, this.nameBoxX(text, this.nameBox) + this.font.width(text) + 5, y + 1);
        }
        graphics.renderItem(AllBlocks.TRAIN_DOOR.asStack(), x + 14, y + 103);
        Train train = (Train)this.displayedTrain.get();
        if (train == null) {
            MutableComponent header = CreateLang.translateDirect("station.idle", new Object[0]);
            graphics.drawString(this.font, (Component)header, x + 97 - this.font.width((FormattedText)header) / 2, y + 47, 0x7A7A7A, false);
            return;
        }
        float position = this.trainPosition.getValue(partialTicks);
        PoseStack ms = graphics.pose();
        ms.pushPose();
        RenderSystem.enableBlend();
        ms.translate(position, 0.0f, 0.0f);
        TrainIconType icon = train.icon;
        int offset = 0;
        List<Carriage> carriages = train.carriages;
        for (int i = carriages.size() - 1; i > 0; --i) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)Math.min(1.0f, Math.min((position + (float)offset - 10.0f) / 30.0f, ((float)(this.background.getWidth() - 40) - position - (float)offset) / 30.0f)));
            Carriage carriage = carriages.get(this.blockEntity.trainBackwards ? carriages.size() - i - 1 : i);
            offset += icon.render(carriage.bogeySpacing, graphics, x + offset, y + 20) + 1;
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)Math.min(1.0f, Math.min((position + (float)offset - 10.0f) / 30.0f, ((float)(this.background.getWidth() - 40) - position - (float)offset) / 30.0f)));
        offset += icon.render(-1, graphics, x + offset, y + 20);
        RenderSystem.disableBlend();
        ms.popPose();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        AllGuiTextures.STATION_TEXTBOX_TOP.render(graphics, x + 21, y + 42);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(x + 21), (int)(y + 60), (int)150, (int)26, (int)0, (TextureSheetSegment)AllGuiTextures.STATION_TEXTBOX_MIDDLE);
        AllGuiTextures.STATION_TEXTBOX_BOTTOM.render(graphics, x + 21, y + 86);
        ms.pushPose();
        ms.translate(Mth.clamp((float)(position + (float)offset - 13.0f), (float)25.0f, (float)159.0f), 0.0f, 0.0f);
        AllGuiTextures.STATION_TEXTBOX_SPEECH.render(graphics, x, y + 38);
        ms.popPose();
        text = this.trainNameBox.getValue();
        if (!this.trainNameBox.isFocused()) {
            int buttonX = this.nameBoxX(text, this.trainNameBox) + this.font.width(text) + 5;
            AllGuiTextures.STATION_EDIT_TRAIN_NAME.render(graphics, Math.min(buttonX, this.guiLeft + 156), y + 44);
            if (this.font.width(text) > this.trainNameBox.getWidth()) {
                graphics.drawString(this.font, "...", this.guiLeft + 26, this.guiTop + 47, 0xA6A6A6);
            }
        }
        if (!this.mapModsPresent()) {
            return;
        }
        AllGuiTextures sprite = AllGuiTextures.TRAINMAP_SPRITES;
        sprite.bind();
        int trainColorIndex = this.colorTypeScroll.getState();
        int colorRow = trainColorIndex / 4;
        int colorCol = trainColorIndex % 4;
        int rotation = AnimationTickHolder.getTicks() / 5 % 8;
        for (int slice = 0; slice < 3; ++slice) {
            int row = slice == 0 ? 1 : (slice == 2 ? 2 : 3);
            int col = rotation;
            int positionX = this.colorTypeScroll.getX() + 4;
            int positionY = this.colorTypeScroll.getY() - 1;
            int sheetX = col * 16 + colorCol * 128;
            int sheetY = row * 16 + colorRow * 64;
            graphics.blit(sprite.location, positionX, positionY, (float)sheetX, (float)sheetY, 16, 16, sprite.getWidth(), sprite.getHeight());
        }
    }

    public boolean mapModsPresent() {
        return Mods.FTBCHUNKS.isLoaded() || Mods.JOURNEYMAP.isLoaded() || Mods.XAEROWORLDMAP.isLoaded();
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!this.nameBox.isFocused() && pMouseY > (double)this.guiTop && pMouseY < (double)(this.guiTop + 14) && pMouseX > (double)this.guiLeft && pMouseX < (double)(this.guiLeft + this.background.getWidth())) {
            this.nameBox.setFocused(true);
            this.nameBox.setHighlightPos(0);
            this.setFocused((GuiEventListener)this.nameBox);
            return true;
        }
        if (this.trainNameBox.active && !this.trainNameBox.isFocused() && pMouseY > (double)(this.guiTop + 45) && pMouseY < (double)(this.guiTop + 58) && pMouseX > (double)(this.guiLeft + 25) && pMouseX < (double)(this.guiLeft + 168)) {
            this.trainNameBox.setFocused(true);
            this.trainNameBox.setHighlightPos(0);
            this.setFocused((GuiEventListener)this.trainNameBox);
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean hitEnter;
        boolean bl = hitEnter = this.getFocused() instanceof EditBox && (pKeyCode == 257 || pKeyCode == 335);
        if (hitEnter && this.nameBox.isFocused()) {
            this.nameBox.setFocused(false);
            this.syncStationName();
            return true;
        }
        if (hitEnter && this.trainNameBox.isFocused()) {
            this.trainNameBox.setFocused(false);
            this.syncTrainNameAndColor();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void syncTrainNameAndColor() {
        Train train = (Train)this.displayedTrain.get();
        if (train != null && !this.trainNameBox.getValue().equals(train.name.getString())) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new TrainEditPacket.Serverbound(train.id, this.trainNameBox.getValue(), train.icon.getId(), train.mapColorIndex));
        }
    }

    private void syncStationName() {
        if (!this.nameBox.getValue().equals(this.station.name)) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.configure(this.blockEntity.getBlockPos(), false, this.nameBox.getValue(), this.doorControl));
        }
    }

    public void removed() {
        super.removed();
        if (this.nameBox == null || this.trainNameBox == null) {
            return;
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.configure(this.blockEntity.getBlockPos(), this.switchingToAssemblyMode, this.nameBox.getValue(), this.doorControl));
        Train train = (Train)this.displayedTrain.get();
        if (train == null) {
            return;
        }
        if (!this.switchingToAssemblyMode) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new TrainEditPacket.Serverbound(train.id, this.trainNameBox.getValue(), train.icon.getId(), train.mapColorIndex));
        } else {
            this.blockEntity.imminentTrain = null;
        }
    }

    @Override
    protected PartialModel getFlag(float partialTicks) {
        return this.blockEntity.flag.getValue(partialTicks) > 0.75f ? AllPartialModels.STATION_ON : AllPartialModels.STATION_OFF;
    }
}
