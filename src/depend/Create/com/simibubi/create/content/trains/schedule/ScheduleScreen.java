/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.gui.TextureSheetSegment
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.schedule;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.schedule.DestinationSuggestions;
import com.simibubi.create.content.trains.schedule.IScheduleInput;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.content.trains.schedule.ScheduleEditPacket;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleMenu;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.menu.GhostItemSubmitPacket;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScreenOverlay;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ScheduleScreen
extends AbstractSimiContainerScreen<ScheduleMenu> {
    private static final int CARD_HEADER = 22;
    private static final int CARD_WIDTH = 195;
    private List<Rect2i> extraAreas = Collections.emptyList();
    private List<LerpedFloat> horizontalScrolls = new ArrayList<LerpedFloat>();
    private LerpedFloat scroll = LerpedFloat.linear().startWithValue(0.0);
    private Schedule schedule;
    private IconButton confirmButton;
    private IconButton cyclicButton;
    private Indicator cyclicIndicator;
    private IconButton resetProgress;
    private IconButton skipProgress;
    private ScheduleInstruction editingDestination;
    private ScheduleWaitCondition editingCondition;
    private SelectionScrollInput scrollInput;
    private Label scrollInputLabel;
    private IconButton editorConfirm;
    private IconButton editorDelete;
    private EditorSubWidgets editorSubWidgets;
    private Consumer<Boolean> onEditorClose;
    private DestinationSuggestions destinationSuggestions;
    private Component clickToEdit = CreateLang.translateDirect("gui.schedule.lmb_edit", new Object[0]).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC});
    private Component rClickToDelete = CreateLang.translateDirect("gui.schedule.rmb_remove", new Object[0]).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC});

    public ScheduleScreen(ScheduleMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.schedule = new Schedule();
        CompoundTag tag = (CompoundTag)((ItemStack)menu.contentHolder).getOrDefault(AllDataComponents.TRAIN_SCHEDULE, (Object)new CompoundTag());
        if (!tag.isEmpty()) {
            this.schedule = Schedule.fromTag((HolderLookup.Provider)menu.player.registryAccess(), tag);
        }
        menu.slotsActive = false;
        this.editorSubWidgets = new EditorSubWidgets();
    }

    @Override
    protected void init() {
        AllGuiTextures bg = AllGuiTextures.SCHEDULE;
        this.setWindowSize(bg.getWidth(), bg.getHeight());
        super.init();
        this.clearWidgets();
        this.confirmButton = new IconButton(this.leftPos + bg.getWidth() - 42, this.topPos + bg.getHeight() - 30, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.minecraft.player.closeContainer());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.cyclicIndicator = new Indicator(this.leftPos + 21, this.topPos + 196, CommonComponents.EMPTY);
        this.cyclicIndicator.state = this.schedule.cyclic ? Indicator.State.ON : Indicator.State.OFF;
        ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
        tip.add(CreateLang.translateDirect("schedule.loop", new Object[0]));
        tip.add(CreateLang.translateDirect("gui.schematicannon.optionDisabled", new Object[0]).withStyle(ChatFormatting.RED));
        tip.add(CreateLang.translateDirect("schedule.loop1", new Object[0]).withStyle(ChatFormatting.GRAY));
        tip.add(CreateLang.translateDirect("schedule.loop2", new Object[0]).withStyle(ChatFormatting.GRAY));
        ArrayList<MutableComponent> tipEnabled = new ArrayList<MutableComponent>(tip);
        tipEnabled.set(1, CreateLang.translateDirect("gui.schematicannon.optionEnabled", new Object[0]).withStyle(ChatFormatting.DARK_GREEN));
        this.cyclicButton = new IconButton(this.leftPos + 21, this.topPos + 196, AllIcons.I_REFRESH);
        this.cyclicButton.withCallback(() -> {
            this.cyclicButton.green = this.schedule.cyclic = !this.schedule.cyclic;
            this.cyclicButton.getToolTip().clear();
            this.cyclicButton.getToolTip().addAll(this.schedule.cyclic ? tipEnabled : tip);
        });
        this.cyclicButton.green = this.schedule.cyclic;
        this.cyclicButton.getToolTip().clear();
        this.cyclicButton.getToolTip().addAll(this.schedule.cyclic ? tipEnabled : tip);
        this.addRenderableWidget((GuiEventListener)this.cyclicButton);
        this.resetProgress = new IconButton(this.leftPos + 45, this.topPos + 196, AllIcons.I_PRIORITY_VERY_HIGH);
        this.resetProgress.withCallback(() -> {
            this.schedule.savedProgress = 0;
            this.resetProgress.active = false;
        });
        this.resetProgress.active = this.schedule.savedProgress > 0 && !this.schedule.entries.isEmpty();
        this.resetProgress.setToolTip((Component)CreateLang.translateDirect("schedule.reset", new Object[0]));
        this.addRenderableWidget((GuiEventListener)this.resetProgress);
        this.skipProgress = new IconButton(this.leftPos + 63, this.topPos + 196, AllIcons.I_PRIORITY_LOW);
        this.skipProgress.withCallback(() -> {
            ++this.schedule.savedProgress;
            this.schedule.savedProgress %= this.schedule.entries.size();
            this.resetProgress.active = this.schedule.savedProgress > 0;
        });
        this.skipProgress.active = this.schedule.entries.size() > 1;
        this.skipProgress.setToolTip((Component)CreateLang.translateDirect("schedule.skip", new Object[0]));
        this.addRenderableWidget((GuiEventListener)this.skipProgress);
        this.stopEditing();
        this.extraAreas = ImmutableList.of((Object)new Rect2i(this.leftPos + bg.getWidth(), this.topPos + bg.getHeight() - 56, 48, 48));
        this.horizontalScrolls.clear();
        for (int i = 0; i < this.schedule.entries.size(); ++i) {
            this.horizontalScrolls.add(LerpedFloat.linear().startWithValue(0.0));
        }
        this.addRenderableWidget((GuiEventListener)this.editorSubWidgets);
    }

    protected void startEditing(IScheduleInput field, Consumer<Boolean> onClose, boolean allowDeletion) {
        int i;
        this.onEditorClose = onClose;
        this.confirmButton.visible = false;
        this.cyclicButton.visible = false;
        this.cyclicIndicator.visible = false;
        this.skipProgress.visible = false;
        this.resetProgress.visible = false;
        this.scrollInput = new SelectionScrollInput(this.leftPos + 56, this.topPos + 65, 143, 16);
        this.scrollInputLabel = new Label(this.leftPos + 59, this.topPos + 69, CommonComponents.EMPTY).withShadow();
        this.editorConfirm = new IconButton(this.leftPos + 56 + 168, this.topPos + 65 + 22, AllIcons.I_CONFIRM);
        if (allowDeletion) {
            this.editorDelete = new IconButton(this.leftPos + 56 - 45, this.topPos + 65 + 22, AllIcons.I_TRASH);
        }
        ((ScheduleMenu)this.menu).slotsActive = true;
        ((ScheduleMenu)this.menu).targetSlotsActive = field.slotsTargeted();
        for (int i2 = 0; i2 < field.slotsTargeted(); ++i2) {
            ItemStack item = field.getItem(i2);
            ((ScheduleMenu)this.menu).ghostInventory.setStackInSlot(i2, item);
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new GhostItemSubmitPacket(item, i2));
        }
        if (field instanceof ScheduleInstruction) {
            ScheduleInstruction instruction = (ScheduleInstruction)field;
            int startIndex = 0;
            for (i = 0; i < Schedule.INSTRUCTION_TYPES.size(); ++i) {
                if (!((ResourceLocation)Schedule.INSTRUCTION_TYPES.get(i).getFirst()).equals((Object)instruction.getId())) continue;
                startIndex = i;
            }
            this.editingDestination = instruction;
            this.updateEditorSubwidgets(this.editingDestination);
            this.scrollInput.forOptions(Schedule.getTypeOptions(Schedule.INSTRUCTION_TYPES)).titled(CreateLang.translateDirect("schedule.instruction_type", new Object[0])).writingTo(this.scrollInputLabel).calling(index -> {
                ScheduleInstruction newlyCreated = (ScheduleInstruction)((Supplier)Schedule.INSTRUCTION_TYPES.get((int)index).getSecond()).get();
                if (this.editingDestination.getId().equals((Object)newlyCreated.getId())) {
                    return;
                }
                this.editingDestination = newlyCreated;
                this.updateEditorSubwidgets(this.editingDestination);
            }).setState(startIndex);
        }
        if (field instanceof ScheduleWaitCondition) {
            ScheduleWaitCondition cond = (ScheduleWaitCondition)field;
            int startIndex = 0;
            for (i = 0; i < Schedule.CONDITION_TYPES.size(); ++i) {
                if (!((ResourceLocation)Schedule.CONDITION_TYPES.get(i).getFirst()).equals((Object)cond.getId())) continue;
                startIndex = i;
            }
            this.editingCondition = cond;
            this.updateEditorSubwidgets(this.editingCondition);
            this.scrollInput.forOptions(Schedule.getTypeOptions(Schedule.CONDITION_TYPES)).titled(CreateLang.translateDirect("schedule.condition_type", new Object[0])).writingTo(this.scrollInputLabel).calling(index -> {
                ScheduleWaitCondition newlyCreated = (ScheduleWaitCondition)((Supplier)Schedule.CONDITION_TYPES.get((int)index).getSecond()).get();
                if (this.editingCondition.getId().equals((Object)newlyCreated.getId())) {
                    return;
                }
                this.editingCondition = newlyCreated;
                this.updateEditorSubwidgets(this.editingCondition);
            }).setState(startIndex);
        }
        this.editorSubWidgets.add(this.scrollInput);
        this.editorSubWidgets.add(this.scrollInputLabel);
        this.editorSubWidgets.add(this.editorConfirm);
        if (allowDeletion) {
            this.editorSubWidgets.add(this.editorDelete);
        }
    }

    private void onDestinationEdited(String text) {
        if (this.destinationSuggestions != null) {
            this.destinationSuggestions.updateCommandInfo();
        }
    }

    protected void stopEditing() {
        this.confirmButton.visible = true;
        this.cyclicButton.visible = true;
        this.cyclicIndicator.visible = true;
        this.skipProgress.visible = true;
        this.resetProgress.visible = true;
        if (this.editingCondition == null && this.editingDestination == null) {
            return;
        }
        this.destinationSuggestions = null;
        this.removeWidget((GuiEventListener)this.scrollInput);
        this.removeWidget((GuiEventListener)this.scrollInputLabel);
        this.removeWidget((GuiEventListener)this.editorConfirm);
        this.removeWidget((GuiEventListener)this.editorDelete);
        ScheduleDataEntry editing = this.editingCondition == null ? this.editingDestination : this.editingCondition;
        for (int i = 0; i < editing.slotsTargeted(); ++i) {
            editing.setItem(i, ((ScheduleMenu)this.menu).ghostInventory.getStackInSlot(i));
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new GhostItemSubmitPacket(ItemStack.EMPTY, i));
        }
        this.editorSubWidgets.save(editing.getData());
        this.editorSubWidgets.clear();
        this.editingCondition = null;
        this.editingDestination = null;
        this.editorConfirm = null;
        this.editorDelete = null;
        ((ScheduleMenu)this.menu).slotsActive = false;
        this.init();
    }

    protected void updateEditorSubwidgets(IScheduleInput field) {
        this.destinationSuggestions = null;
        ((ScheduleMenu)this.menu).targetSlotsActive = field.slotsTargeted();
        this.editorSubWidgets.reset();
        field.initConfigurationWidgets(this.editorSubWidgets.newLineBuilder(this.font, this.getGuiLeft() + 77, this.getGuiTop() + 92).speechBubble());
        this.editorSubWidgets.load(field.getData());
        if (!(field instanceof DestinationInstruction)) {
            return;
        }
        this.editorSubWidgets.forEach(e -> {
            if (!(e instanceof EditBox)) {
                return;
            }
            EditBox destinationBox = (EditBox)e;
            this.destinationSuggestions = new DestinationSuggestions(this.minecraft, (Screen)this, destinationBox, this.font, this.getViableStations(field), false, this.topPos + 33);
            this.destinationSuggestions.setAllowSuggestions(true);
            this.destinationSuggestions.updateCommandInfo();
            destinationBox.setResponder(this::onDestinationEdited);
        });
    }

    private List<IntAttached<String>> getViableStations(IScheduleInput field) {
        GlobalRailwayManager railwayManager = Create.RAILWAYS.sided(null);
        HashSet<TrackGraph> viableGraphs = new HashSet<TrackGraph>(railwayManager.trackNetworks.values());
        for (ScheduleEntry entry : this.schedule.entries) {
            String filter;
            DestinationInstruction destination;
            ScheduleInstruction scheduleInstruction = entry.instruction;
            if (!(scheduleInstruction instanceof DestinationInstruction) || (destination = (DestinationInstruction)scheduleInstruction) == field || (filter = destination.getFilterForRegex()).isBlank()) continue;
            Iterator iterator = viableGraphs.iterator();
            block1: while (iterator.hasNext()) {
                TrackGraph trackGraph = (TrackGraph)iterator.next();
                for (GlobalStation station2 : trackGraph.getPoints(EdgePointType.STATION)) {
                    if (!station2.name.matches(filter)) continue;
                    continue block1;
                }
                iterator.remove();
            }
        }
        if (viableGraphs.isEmpty()) {
            viableGraphs = new HashSet<TrackGraph>(railwayManager.trackNetworks.values());
        }
        Vec3 position = this.minecraft.player.position();
        HashSet visited = new HashSet();
        return viableGraphs.stream().flatMap(g -> g.getPoints(EdgePointType.STATION).stream()).filter(station -> station.blockEntityPos != null).filter(station -> visited.add(station.name)).map(station -> IntAttached.with((int)((int)Vec3.atBottomCenterOf((Vec3i)station.blockEntityPos).distanceTo(position)), (Object)station.name)).toList();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.scroll.tickChaser();
        for (LerpedFloat lerpedFloat : this.horizontalScrolls) {
            lerpedFloat.tickChaser();
        }
        if (this.destinationSuggestions != null) {
            this.destinationSuggestions.tick();
        }
        this.schedule.savedProgress = this.schedule.entries.isEmpty() ? 0 : Mth.clamp((int)this.schedule.savedProgress, (int)0, (int)(this.schedule.entries.size() - 1));
        this.resetProgress.active = this.schedule.savedProgress > 0;
        this.skipProgress.active = this.schedule.entries.size() > 1;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        partialTicks = this.minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        if (((ScheduleMenu)this.menu).slotsActive) {
            super.render(graphics, mouseX, mouseY, partialTicks);
        } else {
            this.renderBackground(graphics, mouseX, mouseY, partialTicks);
            this.renderBg(graphics, partialTicks, mouseX, mouseY);
            for (Renderable widget : this.renderables) {
                widget.render(graphics, mouseX, mouseY, partialTicks);
            }
            this.renderForeground(graphics, mouseX, mouseY, partialTicks);
        }
    }

    protected void renderSchedule(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(this.leftPos + 33), (int)(this.topPos + 16), (int)3, (int)173, (int)200, (TextureSheetSegment)AllGuiTextures.SCHEDULE_STRIP_DARK);
        int yOffset = 25;
        List<ScheduleEntry> entries = this.schedule.entries;
        float scrollOffset = -this.scroll.getValue(partialTicks);
        graphics.enableScissor(this.leftPos + 16, this.topPos + 16, this.leftPos + 236, this.topPos + 189);
        for (int i = 0; i <= entries.size(); ++i) {
            if (this.schedule.savedProgress == i && !this.schedule.entries.isEmpty()) {
                matrixStack.pushPose();
                float expectedY = scrollOffset + (float)this.topPos + (float)yOffset + 4.0f;
                float actualY = Mth.clamp((float)expectedY, (float)(this.topPos + 18), (float)(this.topPos + 170));
                matrixStack.translate(0.0f, actualY, 0.0f);
                (expectedY == actualY ? AllGuiTextures.SCHEDULE_POINTER : AllGuiTextures.SCHEDULE_POINTER_OFFSCREEN).render(graphics, this.leftPos, 0);
                matrixStack.popPose();
            }
            matrixStack.pushPose();
            matrixStack.translate(0.0f, scrollOffset, 0.0f);
            if (i == 0 || entries.size() == 0) {
                UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(this.leftPos + 33), (int)(this.topPos + 16), (int)3, (int)10, (int)-100, (TextureSheetSegment)AllGuiTextures.SCHEDULE_STRIP_LIGHT);
            }
            if (i == entries.size()) {
                if (i > 0) {
                    yOffset += 9;
                }
                AllGuiTextures.SCHEDULE_STRIP_END.render(graphics, this.leftPos + 29, this.topPos + yOffset);
                AllGuiTextures.SCHEDULE_CARD_NEW.render(graphics, this.leftPos + 43, this.topPos + yOffset);
                matrixStack.popPose();
                break;
            }
            ScheduleEntry scheduleEntry = entries.get(i);
            int cardY = yOffset;
            int cardHeight = this.renderScheduleEntry(graphics, scheduleEntry, cardY, mouseX, mouseY, partialTicks);
            yOffset += cardHeight;
            if (i + 1 < entries.size()) {
                AllGuiTextures.SCHEDULE_STRIP_DOTTED.render(graphics, this.leftPos + 29, this.topPos + yOffset - 3);
                yOffset += 10;
            }
            matrixStack.popPose();
            if (!scheduleEntry.instruction.supportsConditions()) continue;
            float y1 = (float)(cardY + 24) + scrollOffset;
            float h = cardHeight - 26;
            float y2 = y1 + h;
            if (y2 > 189.0f) {
                h -= y2 - 189.0f;
            }
            if (y1 < 16.0f) {
                float correction = 16.0f - y1;
                y1 += correction;
                h -= correction;
            }
            if (h <= 0.0f) continue;
            graphics.enableScissor(this.leftPos + 43, 0, this.leftPos + 204, 400);
            matrixStack.pushPose();
            matrixStack.translate(0.0f, scrollOffset, 0.0f);
            this.renderScheduleConditions(graphics, scheduleEntry, cardY, mouseX, mouseY, partialTicks, cardHeight, i);
            matrixStack.popPose();
            graphics.disableScissor();
            if (!this.isConditionAreaScrollable(scheduleEntry)) continue;
            matrixStack.pushPose();
            matrixStack.translate(0.0f, scrollOffset, 0.0f);
            int center = (cardHeight - 8 + 22) / 2;
            float chaseTarget = this.horizontalScrolls.get(i).getChaseTarget();
            if (!Mth.equal((float)chaseTarget, (float)0.0f)) {
                AllGuiTextures.SCHEDULE_SCROLL_LEFT.render(graphics, this.leftPos + 40, this.topPos + cardY + center);
            }
            if (!Mth.equal((float)chaseTarget, (float)(scheduleEntry.conditions.size() - 1))) {
                AllGuiTextures.SCHEDULE_SCROLL_RIGHT.render(graphics, this.leftPos + 203, this.topPos + cardY + center);
            }
            matrixStack.popPose();
        }
        graphics.disableScissor();
        int zLevel = 200;
        graphics.fillGradient(this.leftPos + 16, this.topPos + 16, this.leftPos + 16 + 220, this.topPos + 16 + 10, zLevel, 0x77000000, 0);
        graphics.fillGradient(this.leftPos + 16, this.topPos + 179, this.leftPos + 16 + 220, this.topPos + 179 + 10, zLevel, 0, 0x77000000);
    }

    public int renderScheduleEntry(GuiGraphics graphics, ScheduleEntry entry, int yOffset, int mouseX, int mouseY, float partialTicks) {
        int zLevel = 0;
        AllGuiTextures light = AllGuiTextures.SCHEDULE_CARD_LIGHT;
        AllGuiTextures medium = AllGuiTextures.SCHEDULE_CARD_MEDIUM;
        AllGuiTextures dark = AllGuiTextures.SCHEDULE_CARD_DARK;
        int cardWidth = 195;
        int cardHeader = 22;
        int maxRows = 0;
        for (List<ScheduleWaitCondition> list : entry.conditions) {
            maxRows = Math.max(maxRows, list.size());
        }
        boolean supportsConditions = entry.instruction.supportsConditions();
        int cardHeight = cardHeader + (supportsConditions ? 24 + maxRows * 18 : 4);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)(this.leftPos + 25), (float)(this.topPos + yOffset), 0.0f);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)0, (int)1, (int)cardWidth, (int)(cardHeight - 2), (int)zLevel, (TextureSheetSegment)light);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)1, (int)0, (int)(cardWidth - 2), (int)cardHeight, (int)zLevel, (TextureSheetSegment)light);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)1, (int)1, (int)(cardWidth - 2), (int)(cardHeight - 2), (int)zLevel, (TextureSheetSegment)dark);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)2, (int)2, (int)(cardWidth - 4), (int)(cardHeight - 4), (int)zLevel, (TextureSheetSegment)medium);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)2, (int)2, (int)(cardWidth - 4), (int)cardHeader, (int)zLevel, (TextureSheetSegment)(supportsConditions ? light : medium));
        AllGuiTextures.SCHEDULE_CARD_REMOVE.render(graphics, cardWidth - 14, 2);
        AllGuiTextures.SCHEDULE_CARD_DUPLICATE.render(graphics, cardWidth - 14, cardHeight - 14);
        int i = this.schedule.entries.indexOf(entry);
        if (i > 0) {
            AllGuiTextures.SCHEDULE_CARD_MOVE_UP.render(graphics, cardWidth, cardHeader - 14);
        }
        if (i < this.schedule.entries.size() - 1) {
            AllGuiTextures.SCHEDULE_CARD_MOVE_DOWN.render(graphics, cardWidth, cardHeader);
        }
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)8, (int)0, (int)3, (int)(cardHeight + 10), (int)zLevel, (TextureSheetSegment)AllGuiTextures.SCHEDULE_STRIP_LIGHT);
        (supportsConditions ? AllGuiTextures.SCHEDULE_STRIP_TRAVEL : AllGuiTextures.SCHEDULE_STRIP_ACTION).render(graphics, 4, 6);
        if (supportsConditions) {
            AllGuiTextures.SCHEDULE_STRIP_WAIT.render(graphics, 4, 28);
        }
        Pair<ItemStack, Component> destination = entry.instruction.getSummary();
        this.renderInput(graphics, destination, 26, 5, false, 100);
        entry.instruction.renderSpecialIcon(graphics, 30, 5);
        matrixStack.popPose();
        return cardHeight;
    }

    public void renderScheduleConditions(GuiGraphics graphics, ScheduleEntry entry, int yOffset, int mouseX, int mouseY, float partialTicks, int cardHeight, int entryIndex) {
        int cardWidth = 195;
        int cardHeader = 22;
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)(this.leftPos + 25), (float)(this.topPos + yOffset), 0.0f);
        int xOffset = 26;
        float scrollOffset = this.getConditionScroll(entry, partialTicks, entryIndex);
        matrixStack.pushPose();
        matrixStack.translate(-scrollOffset, 0.0f, 0.0f);
        for (List<ScheduleWaitCondition> list : entry.conditions) {
            int maxWidth = this.getConditionColumnWidth(list);
            for (int i = 0; i < list.size(); ++i) {
                ScheduleWaitCondition scheduleWaitCondition = list.get(i);
                Math.max(maxWidth, this.renderInput(graphics, scheduleWaitCondition.getSummary(), xOffset, 29 + i * 18, i != 0, maxWidth));
                scheduleWaitCondition.renderSpecialIcon(graphics, xOffset + 4, 29 + i * 18);
            }
            AllGuiTextures.SCHEDULE_CONDITION_APPEND.render(graphics, xOffset + (maxWidth - 10) / 2, 29 + list.size() * 18);
            xOffset += maxWidth + 10;
        }
        AllGuiTextures.SCHEDULE_CONDITION_NEW.render(graphics, xOffset - 3, 29);
        matrixStack.popPose();
        if (xOffset + 16 > cardWidth - 26) {
            TransformStack.of((PoseStack)matrixStack).rotateZDegrees(-90.0f);
            int zLevel = 200;
            graphics.fillGradient(-cardHeight + 2, 18, -2 - cardHeader, 28, zLevel, 0x44000000, 0);
            graphics.fillGradient(-cardHeight + 2, cardWidth - 26, -2 - cardHeader, cardWidth - 16, zLevel, 0, 0x44000000);
        }
        matrixStack.popPose();
    }

    private boolean isConditionAreaScrollable(ScheduleEntry entry) {
        int xOffset = 26;
        for (List<ScheduleWaitCondition> list : entry.conditions) {
            xOffset += this.getConditionColumnWidth(list) + 10;
        }
        return xOffset + 16 > 169;
    }

    private float getConditionScroll(ScheduleEntry entry, float partialTicks, int entryIndex) {
        float scrollOffset = 0.0f;
        float scrollIndex = this.horizontalScrolls.get(entryIndex).getValue(partialTicks);
        for (List<ScheduleWaitCondition> list : entry.conditions) {
            int maxWidth = this.getConditionColumnWidth(list);
            float partialOfThisColumn = Math.min(1.0f, scrollIndex);
            scrollOffset += (float)(maxWidth + 10) * partialOfThisColumn;
            scrollIndex -= partialOfThisColumn;
        }
        return scrollOffset;
    }

    private int getConditionColumnWidth(List<ScheduleWaitCondition> list) {
        int maxWidth = 0;
        for (ScheduleWaitCondition scheduleWaitCondition : list) {
            maxWidth = Math.max(maxWidth, this.getFieldSize(32, scheduleWaitCondition.getSummary()));
        }
        return maxWidth;
    }

    protected int renderInput(GuiGraphics graphics, Pair<ItemStack, Component> pair, int x, int y, boolean clean, int minSize) {
        ItemStack stack = (ItemStack)pair.getFirst();
        Component text = (Component)pair.getSecond();
        boolean hasItem = !stack.isEmpty();
        int fieldSize = Math.min(this.getFieldSize(minSize, pair), 150);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        AllGuiTextures left = clean ? AllGuiTextures.SCHEDULE_CONDITION_LEFT_CLEAN : AllGuiTextures.SCHEDULE_CONDITION_LEFT;
        AllGuiTextures middle = AllGuiTextures.SCHEDULE_CONDITION_MIDDLE;
        AllGuiTextures item = AllGuiTextures.SCHEDULE_CONDITION_ITEM;
        AllGuiTextures right = AllGuiTextures.SCHEDULE_CONDITION_RIGHT;
        matrixStack.translate((float)x, (float)y, 0.0f);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)0, (int)0, (int)fieldSize, (int)16, (int)0, (TextureSheetSegment)middle);
        left.render(graphics, clean ? 0 : -3, 0);
        right.render(graphics, fieldSize - 2, 0);
        if (hasItem) {
            item.render(graphics, 3, 0);
        }
        if (hasItem) {
            item.render(graphics, 3, 0);
            if (stack.getItem() != Items.STRUCTURE_VOID) {
                GuiGameElement.of((ItemStack)stack).at(4.0f, 0.0f).render(graphics);
            }
        }
        if (text != null) {
            graphics.drawString(this.font, this.font.substrByWidth((FormattedText)text, 120).getString(), hasItem ? 28 : 8, 4, -855314);
        }
        matrixStack.popPose();
        return fieldSize;
    }

    public boolean action(@Nullable GuiGraphics graphics, double mouseX, double mouseY, int click) {
        if (this.editingCondition != null || this.editingDestination != null) {
            return false;
        }
        Component empty = CommonComponents.EMPTY;
        int mx = (int)mouseX;
        int my = (int)mouseY;
        int x = mx - this.leftPos - 25;
        int y = my - this.topPos - 25;
        if (x < 0 || x >= 205) {
            return false;
        }
        if (y < 0 || y >= 173) {
            return false;
        }
        y = (int)((float)y + this.scroll.getValue(0.0f));
        List<ScheduleEntry> entries = this.schedule.entries;
        for (int i = 0; i < entries.size(); ++i) {
            int center;
            ScheduleEntry entry = entries.get(i);
            int maxRows = 0;
            for (List<ScheduleWaitCondition> list : entry.conditions) {
                maxRows = Math.max(maxRows, list.size());
            }
            int cardHeight = 22 + (entry.instruction.supportsConditions() ? 24 + maxRows * 18 : 4);
            if (y >= cardHeight + 5) {
                if ((y -= cardHeight + 10) >= 0) continue;
                return false;
            }
            int fieldSize = this.getFieldSize(100, entry.instruction.getSummary());
            if (x > 25 && x <= 25 + fieldSize && y > 4 && y <= 20) {
                ArrayList<Component> components = new ArrayList<Component>();
                components.addAll(entry.instruction.getTitleAs("instruction"));
                components.add(empty);
                components.add(this.clickToEdit);
                this.renderActionTooltip(graphics, components, mx, my);
                if (click == 0) {
                    this.startEditing(entry.instruction, confirmed -> {
                        if (confirmed.booleanValue()) {
                            entry.instruction = this.editingDestination;
                        }
                    }, false);
                }
                return true;
            }
            if (x > 180 && x <= 192) {
                if (y > 0 && y <= 14) {
                    this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.remove_entry", new Object[0])), mx, my);
                    if (click == 0) {
                        entries.remove(entry);
                        this.init();
                    }
                    return true;
                }
                if (y > cardHeight - 14) {
                    this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.duplicate", new Object[0])), mx, my);
                    if (click == 0) {
                        entries.add(entries.indexOf(entry), entry.clone((HolderLookup.Provider)this.minecraft.level.registryAccess()));
                        this.init();
                    }
                    return true;
                }
            }
            if (x > 194) {
                if (y > 7 && y <= 20 && i > 0) {
                    this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.move_up", new Object[0])), mx, my);
                    if (click == 0) {
                        entries.remove(entry);
                        entries.add(i - 1, entry);
                        this.init();
                    }
                    return true;
                }
                if (y > 20 && y <= 33 && i < entries.size() - 1) {
                    this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.move_down", new Object[0])), mx, my);
                    if (click == 0) {
                        entries.remove(entry);
                        entries.add(i + 1, entry);
                        this.init();
                    }
                    return true;
                }
            }
            if (y > (center = (cardHeight - 8 + 22) / 2) - 1 && y <= center + 7 && this.isConditionAreaScrollable(entry)) {
                float chaseTarget = this.horizontalScrolls.get(i).getChaseTarget();
                if (x > 12 && x <= 19 && !Mth.equal((float)chaseTarget, (float)0.0f)) {
                    if (click == 0) {
                        this.horizontalScrolls.get(i).chase((double)(chaseTarget - 1.0f), 0.5, LerpedFloat.Chaser.EXP);
                    }
                    return true;
                }
                if (x > 177 && x <= 184 && !Mth.equal((float)chaseTarget, (float)(entry.conditions.size() - 1))) {
                    if (click == 0) {
                        this.horizontalScrolls.get(i).chase((double)(chaseTarget + 1.0f), 0.5, LerpedFloat.Chaser.EXP);
                    }
                    return true;
                }
            }
            if ((x -= 18) < 0 || (y -= 28) < 0 || x > 160) {
                return false;
            }
            x = (int)((float)x + (this.getConditionScroll(entry, 0.0f, i) - 8.0f));
            List<List<ScheduleWaitCondition>> columns = entry.conditions;
            for (int j = 0; j < columns.size(); ++j) {
                List<ScheduleWaitCondition> conditions = columns.get(j);
                if (x < 0) {
                    return false;
                }
                int w = this.getConditionColumnWidth(conditions);
                if (x >= w) {
                    x -= w + 10;
                    continue;
                }
                int row = y / 18;
                if (row < conditions.size() && row >= 0) {
                    boolean canRemove = conditions.size() > 1 || columns.size() > 1;
                    ArrayList<Component> components = new ArrayList<Component>();
                    components.add((Component)CreateLang.translateDirect("schedule.condition_type", new Object[0]).withStyle(ChatFormatting.GRAY));
                    ScheduleWaitCondition condition = conditions.get(row);
                    components.addAll(condition.getTitleAs("condition"));
                    components.add(empty);
                    components.add(this.clickToEdit);
                    if (canRemove) {
                        components.add(this.rClickToDelete);
                    }
                    this.renderActionTooltip(graphics, components, mx, my);
                    if (canRemove && click == 1) {
                        conditions.remove(row);
                        if (conditions.isEmpty()) {
                            columns.remove(conditions);
                        }
                    }
                    if (click == 0) {
                        this.startEditing(condition, confirmed -> {
                            conditions.remove(row);
                            if (confirmed.booleanValue()) {
                                conditions.add(row, this.editingCondition);
                                return;
                            }
                            if (conditions.isEmpty()) {
                                columns.remove(conditions);
                            }
                        }, canRemove);
                    }
                    return true;
                }
                if (y > 18 * conditions.size() && y <= 18 * conditions.size() + 10 && x >= w / 2 - 5 && x < w / 2 + 5) {
                    this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.add_condition", new Object[0])), mx, my);
                    if (click == 0) {
                        this.startEditing(new ScheduledDelay(), confirmed -> {
                            if (confirmed.booleanValue()) {
                                conditions.add(this.editingCondition);
                            }
                        }, true);
                    }
                    return true;
                }
                return false;
            }
            if (x < 0 || x > 15 || y > 20) {
                return false;
            }
            this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.alternative_condition", new Object[0])), mx, my);
            if (click == 0) {
                this.startEditing(new ScheduledDelay(), confirmed -> {
                    if (!confirmed.booleanValue()) {
                        return;
                    }
                    ArrayList<ScheduleWaitCondition> conditions = new ArrayList<ScheduleWaitCondition>();
                    conditions.add(this.editingCondition);
                    columns.add(conditions);
                }, true);
            }
            return true;
        }
        if (x < 18 || x > 33 || y > 14) {
            return false;
        }
        this.renderActionTooltip(graphics, (List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("gui.schedule.add_entry", new Object[0])), mx, my);
        if (click == 0) {
            this.startEditing(new DestinationInstruction(), confirmed -> {
                if (!confirmed.booleanValue()) {
                    return;
                }
                ScheduleEntry entry = new ScheduleEntry();
                ScheduledDelay delay = new ScheduledDelay();
                ArrayList<ScheduledDelay> initialConditions = new ArrayList<ScheduledDelay>();
                initialConditions.add(delay);
                entry.instruction = this.editingDestination;
                entry.conditions.add(initialConditions);
                this.schedule.entries.add(entry);
            }, true);
        }
        return true;
    }

    private void renderActionTooltip(@Nullable GuiGraphics graphics, List<Component> tooltip, int mx, int my) {
        if (graphics != null) {
            graphics.renderTooltip(this.font, tooltip, Optional.empty(), mx, my);
        }
    }

    private int getFieldSize(int minSize, Pair<ItemStack, Component> pair) {
        ItemStack stack = (ItemStack)pair.getFirst();
        Component text = (Component)pair.getSecond();
        boolean hasItem = !stack.isEmpty();
        return Math.max((text == null ? 0 : this.font.width((FormattedText)text)) + (hasItem ? 20 : 0) + 16, minSize);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.destinationSuggestions != null && this.destinationSuggestions.mouseClicked((int)pMouseX, (int)pMouseY, pButton)) {
            return true;
        }
        if (this.editorConfirm != null && this.editorConfirm.isMouseOver(pMouseX, pMouseY) && this.onEditorClose != null) {
            this.onEditorClose.accept(true);
            this.stopEditing();
            return true;
        }
        if (this.editorDelete != null && this.editorDelete.isMouseOver(pMouseX, pMouseY) && this.onEditorClose != null) {
            this.onEditorClose.accept(false);
            this.stopEditing();
            return true;
        }
        if (this.action(null, pMouseX, pMouseY, pButton)) {
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean hitE;
        if (this.destinationSuggestions != null && this.destinationSuggestions.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        if (this.editingCondition == null && this.editingDestination == null) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        InputConstants.Key mouseKey = InputConstants.getKey((int)pKeyCode, (int)pScanCode);
        boolean hitEnter = this.getFocused() instanceof EditBox && (pKeyCode == 257 || pKeyCode == 335);
        boolean bl = hitE = this.getFocused() == null || this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey);
        if (hitEnter) {
            this.onEditorClose.accept(true);
            this.stopEditing();
            return true;
        }
        if (hitE) {
            return false;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        int maxRows;
        if (this.destinationSuggestions != null && this.destinationSuggestions.mouseScrolled(Mth.clamp((double)pScrollY, (double)-1.0, (double)1.0))) {
            return true;
        }
        if (this.editingCondition != null || this.editingDestination != null) {
            return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
        }
        if (ScheduleScreen.hasShiftDown()) {
            List<ScheduleEntry> entries = this.schedule.entries;
            int y = (int)(pMouseY - (double)this.topPos - 25.0 + (double)this.scroll.getValue());
            for (int i = 0; i < entries.size(); ++i) {
                ScheduleEntry entry = entries.get(i);
                maxRows = 0;
                for (List<ScheduleWaitCondition> list : entry.conditions) {
                    maxRows = Math.max(maxRows, list.size());
                }
                int cardHeight = 46 + maxRows * 18;
                if (y >= cardHeight) {
                    if ((y -= cardHeight + 9) >= 0) continue;
                    break;
                }
                if (!this.isConditionAreaScrollable(entry) || y < 24 || pMouseX < (double)(this.leftPos + 25) || pMouseX > (double)(this.leftPos + 205)) break;
                float chaseTarget = this.horizontalScrolls.get(i).getChaseTarget();
                if (pScrollY > 0.0 && !Mth.equal((float)chaseTarget, (float)0.0f)) {
                    this.horizontalScrolls.get(i).chase((double)(chaseTarget - 1.0f), 0.5, LerpedFloat.Chaser.EXP);
                    return true;
                }
                if (pScrollY < 0.0 && !Mth.equal((float)chaseTarget, (float)(entry.conditions.size() - 1))) {
                    this.horizontalScrolls.get(i).chase((double)(chaseTarget + 1.0f), 0.5, LerpedFloat.Chaser.EXP);
                    return true;
                }
                return false;
            }
        }
        float chaseTarget = this.scroll.getChaseTarget();
        float max = -133.0f;
        for (ScheduleEntry scheduleEntry : this.schedule.entries) {
            maxRows = 0;
            for (List<ScheduleWaitCondition> list : scheduleEntry.conditions) {
                maxRows = Math.max(maxRows, list.size());
            }
            max += (float)(46 + maxRows * 18 + 10);
        }
        if (max > 0.0f) {
            chaseTarget = (float)((double)chaseTarget - pScrollY * 12.0);
            chaseTarget = Mth.clamp((float)chaseTarget, (float)0.0f, (float)max);
            this.scroll.chase((double)((int)chaseTarget), (double)0.7f, LerpedFloat.Chaser.EXP);
        } else {
            this.scroll.chase(0.0, (double)0.7f, LerpedFloat.Chaser.EXP);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        if (this.destinationSuggestions != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.0f, 0.0f, 500.0f);
            this.destinationSuggestions.render(graphics, mouseX, mouseY);
            matrixStack.popPose();
        }
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)((ItemStack)((ScheduleMenu)this.menu).contentHolder)).at((float)(this.leftPos + AllGuiTextures.SCHEDULE.getWidth()), (float)(this.topPos + AllGuiTextures.SCHEDULE.getHeight() - 56), -200.0f)).scale(3.0).render(graphics);
        this.action(graphics, mouseX, mouseY, -1);
        if (this.editingCondition == null && this.editingDestination == null) {
            return;
        }
        int x = this.leftPos + 53;
        int y = this.topPos + 87;
        if (mouseX < x || mouseY < y || mouseX >= x + 120 || mouseY >= y + 18) {
            return;
        }
        ScheduleDataEntry rendered = this.editingCondition == null ? this.editingDestination : this.editingCondition;
        for (int i = 0; i < Math.max(1, rendered.slotsTargeted()); ++i) {
            Slot slot;
            List<Component> secondLineTooltip = rendered.getSecondLineTooltip(i);
            if (secondLineTooltip == null || (slot = ((ScheduleMenu)this.menu).getSlot(36 + i)) == null || !slot.getItem().isEmpty() || mouseX < this.leftPos + slot.x || mouseX > this.leftPos + slot.x + 18 || mouseY < this.topPos + slot.y || mouseY > this.topPos + slot.y + 18) continue;
            this.renderActionTooltip(graphics, secondLineTooltip, mouseX, mouseY);
        }
    }

    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        AllGuiTextures.SCHEDULE.render(graphics, this.leftPos, this.topPos);
        FormattedCharSequence formattedcharsequence = this.title.getVisualOrderText();
        int center = this.leftPos + (AllGuiTextures.SCHEDULE.getWidth() - 8) / 2;
        graphics.drawString(this.font, formattedcharsequence, (float)(center - this.font.width(formattedcharsequence) / 2), (float)this.topPos + 4.0f, 0x505050, false);
        this.renderSchedule(graphics, pMouseX, pMouseY, pPartialTick);
        if (this.editingCondition == null && this.editingDestination == null) {
            return;
        }
        PoseStack matrices = graphics.pose();
        matrices.pushPose();
        matrices.translate(0.0f, 0.0f, 200.0f);
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        AllGuiTextures.SCHEDULE_EDITOR.render(graphics, this.leftPos - 2, this.topPos + 40);
        AllGuiTextures.PLAYER_INVENTORY.render(graphics, this.leftPos + 38, this.topPos + 122);
        graphics.drawString(this.font, this.playerInventoryTitle, this.leftPos + 46, this.topPos + 128, 0x505050, false);
        formattedcharsequence = this.editingCondition == null ? CreateLang.translateDirect("schedule.instruction.editor", new Object[0]).getVisualOrderText() : CreateLang.translateDirect("schedule.condition.editor", new Object[0]).getVisualOrderText();
        graphics.drawString(this.font, formattedcharsequence, (float)(center - this.font.width(formattedcharsequence) / 2), (float)this.topPos + 44.0f, 0x505050, false);
        ScheduleDataEntry rendered = this.editingCondition == null ? this.editingDestination : this.editingCondition;
        for (int i = 0; i < rendered.slotsTargeted(); ++i) {
            AllGuiTextures.SCHEDULE_EDITOR_ADDITIONAL_SLOT.render(graphics, this.leftPos + 53 + 20 * i, this.topPos + 87);
        }
        if (rendered.slotsTargeted() == 0 && !rendered.renderSpecialIcon(graphics, this.leftPos + 54, this.topPos + 88)) {
            Pair<ItemStack, Component> summary = rendered.getSummary();
            ItemStack icon = (ItemStack)summary.getFirst();
            if (icon.isEmpty()) {
                icon = rendered.getSecondLineIcon();
            }
            if (icon.isEmpty()) {
                AllGuiTextures.SCHEDULE_EDITOR_INACTIVE_SLOT.render(graphics, this.leftPos + 53, this.topPos + 87);
            } else {
                GuiGameElement.of((ItemStack)icon).at((float)(this.leftPos + 54), (float)(this.topPos + 88)).render(graphics);
            }
        }
        matrices.pushPose();
        matrices.translate(0.0f, (float)(this.getGuiTop() + 87), 0.0f);
        this.editorSubWidgets.renderBg(this.getGuiLeft() + 77, graphics);
        matrices.popPose();
        matrices.popPose();
    }

    public void removed() {
        super.removed();
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ScheduleEditPacket(this.schedule));
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }

    public Font getFont() {
        return this.font;
    }

    protected static final class EditorSubWidgets
    extends ScreenOverlay {
        private final ModularGuiLine line = new ModularGuiLine();

        protected EditorSubWidgets() {
            super(200);
        }

        protected void save(CompoundTag data) {
            this.line.saveValues(data);
        }

        protected void load(CompoundTag data) {
            this.line.loadValues(data, x$0 -> this.add((GuiEventListener)x$0), x$0 -> {
                GuiEventListener cfr_ignored_0 = (GuiEventListener)this.addRenderableOnly((Renderable)x$0);
            });
        }

        protected void forEach(Consumer<GuiEventListener> consumer) {
            this.line.forEach(consumer);
        }

        protected void reset() {
            this.line.forEach(this::remove);
            this.line.clear();
        }

        @Override
        public void clear() {
            super.clear();
            this.line.clear();
        }

        protected ModularGuiLineBuilder newLineBuilder(Font font, int x, int y) {
            return new ModularGuiLineBuilder(font, this.line, x, y);
        }

        protected void renderBg(int guiLeft, GuiGraphics graphics) {
            this.line.renderWidgetBG(guiLeft, graphics);
        }
    }
}
