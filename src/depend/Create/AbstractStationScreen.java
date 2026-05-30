/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.trains.station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.CreateClient;
import com.simibubi.create.compat.computercraft.ComputerScreen;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainIconType;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationRenderer;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.lang.ref.WeakReference;
import java.util.List;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractStationScreen
extends AbstractSimiScreen {
    protected AllGuiTextures background;
    protected StationBlockEntity blockEntity;
    protected GlobalStation station;
    protected WeakReference<Train> displayedTrain;
    private IconButton confirmButton;

    public AbstractStationScreen(StationBlockEntity be, GlobalStation station) {
        super((Component)be.getBlockState().getBlock().getName());
        this.blockEntity = be;
        this.station = station;
        this.displayedTrain = new WeakReference<Object>(null);
    }

    protected void init() {
        if (this.blockEntity.computerBehaviour.hasAttachedComputer()) {
            this.minecraft.setScreen((Screen)new ComputerScreen(this.title, () -> Component.literal((String)this.station.name), this::renderAdditional, (Screen)this, this.blockEntity.computerBehaviour::hasAttachedComputer));
        }
        this.setWindowSize(this.background.getWidth(), this.background.getHeight());
        super.init();
        this.clearWidgets();
        int x = this.guiLeft;
        int y = this.guiTop;
        this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> ((AbstractStationScreen)this).onClose());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
    }

    public int getTrainIconWidth(Train train) {
        TrainIconType icon = train.icon;
        List<Carriage> carriages = train.carriages;
        int w = icon.getIconWidth(-1);
        if (carriages.size() == 1) {
            return w;
        }
        for (int i = 1; i < carriages.size(); ++i) {
            if (i == carriages.size() - 1 && train.doubleEnded) {
                w += icon.getIconWidth(-2) + 1;
                break;
            }
            Carriage carriage = carriages.get(i);
            w += icon.getIconWidth(carriage.bogeySpacing) + 1;
        }
        return w;
    }

    public void tick() {
        super.tick();
        if (this.blockEntity.computerBehaviour.hasAttachedComputer()) {
            this.minecraft.setScreen((Screen)new ComputerScreen(this.title, () -> Component.literal((String)this.station.name), this::renderAdditional, (Screen)this, this.blockEntity.computerBehaviour::hasAttachedComputer));
        }
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop;
        this.background.render(graphics, x, y);
        this.renderAdditional(graphics, mouseX, mouseY, partialTicks, x, y, this.background);
    }

    private void renderAdditional(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop, AllGuiTextures background) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ((PoseTransformStack)((PoseTransformStack)msr.pushPose().translate((float)(guiLeft + background.getWidth() + 4), (float)(guiTop + background.getHeight() + 4), 100.0f).scale(40.0f)).rotateXDegrees(-22.0f)).rotateYDegrees(63.0f);
        GuiGameElement.of((BlockState)((BlockState)this.blockEntity.getBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)))).render(graphics);
        if (this.blockEntity.resolveFlagAngle()) {
            msr.translate(0.0625f, -1.1875f, -0.75f);
            StationRenderer.transformFlag(msr, this.blockEntity, partialTicks, 180, false);
            GuiGameElement.of((PartialModel)this.getFlag(partialTicks)).render(graphics);
        }
        ms.popPose();
    }

    protected abstract PartialModel getFlag(float var1);

    protected Train getImminent() {
        return this.blockEntity.imminentTrain == null ? null : CreateClient.RAILWAYS.trains.get(this.blockEntity.imminentTrain);
    }

    protected boolean trainPresent() {
        return this.blockEntity.trainPresent;
    }
}
