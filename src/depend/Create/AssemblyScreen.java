/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainIconType;
import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationEditPacket;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.content.trains.station.TrainEditPacket;
import com.simibubi.create.content.trains.station.WideIconButton;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.lang.ref.WeakReference;
import java.util.List;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class AssemblyScreen
extends AbstractStationScreen {
    private IconButton quitAssembly;
    private IconButton toggleAssemblyButton;
    private List<ResourceLocation> iconTypes;
    private ScrollInput iconTypeScroll;

    public AssemblyScreen(StationBlockEntity be, GlobalStation station) {
        super(be, station);
        this.background = AllGuiTextures.STATION_ASSEMBLING;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        int by = y + this.background.getHeight() - 24;
        Renderable widget = (Renderable)this.renderables.get(0);
        if (widget instanceof IconButton) {
            IconButton ib = (IconButton)widget;
            ib.setIcon(AllIcons.I_PRIORITY_VERY_LOW);
            ib.setToolTip((Component)CreateLang.translateDirect("station.close", new Object[0]));
        }
        this.iconTypes = TrainIconType.REGISTRY.keySet().stream().toList();
        this.iconTypeScroll = new ScrollInput(x + 4, y + 17, 162, 14).titled(CreateLang.translateDirect("station.icon_type", new Object[0]));
        this.iconTypeScroll.withRange(0, this.iconTypes.size());
        this.iconTypeScroll.withStepFunction(ctx -> -this.iconTypeScroll.standardStep().apply((ScrollValueBehaviour.StepContext)ctx).intValue());
        this.iconTypeScroll.calling(s -> {
            Train train = (Train)this.displayedTrain.get();
            if (train != null) {
                train.icon = TrainIconType.byId(this.iconTypes.get((int)s));
            }
        });
        this.iconTypeScroll.visible = false;
        this.iconTypeScroll.active = false;
        this.addRenderableWidget((GuiEventListener)this.iconTypeScroll);
        this.toggleAssemblyButton = new WideIconButton(x + 94, by, AllGuiTextures.I_ASSEMBLE_TRAIN);
        this.toggleAssemblyButton.active = false;
        this.toggleAssemblyButton.setToolTip((Component)CreateLang.translateDirect("station.assemble_train", new Object[0]));
        this.toggleAssemblyButton.withCallback(() -> CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.tryAssemble(this.blockEntity.getBlockPos())));
        this.quitAssembly = new IconButton(x + 73, by, AllIcons.I_DISABLE);
        this.quitAssembly.active = true;
        this.quitAssembly.setToolTip((Component)CreateLang.translateDirect("station.cancel", new Object[0]));
        this.quitAssembly.withCallback(() -> {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.configure(this.blockEntity.getBlockPos(), false, this.station.name, null));
            this.minecraft.setScreen((Screen)new StationScreen(this.blockEntity, this.station));
        });
        this.addRenderableWidget((GuiEventListener)this.toggleAssemblyButton);
        this.addRenderableWidget((GuiEventListener)this.quitAssembly);
        this.tickTrainDisplay();
    }

    @Override
    public void tick() {
        super.tick();
        this.tickTrainDisplay();
        Train train = (Train)this.displayedTrain.get();
        boolean bl = this.toggleAssemblyButton.active = this.blockEntity.bogeyCount > 0 || train != null;
        if (train != null) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.configure(this.blockEntity.getBlockPos(), false, this.station.name, null));
            this.minecraft.setScreen((Screen)new StationScreen(this.blockEntity, this.station));
            for (Carriage carriage : train.carriages) {
                carriage.updateConductors();
            }
        }
    }

    private void tickTrainDisplay() {
        if (this.getImminent() == null) {
            this.displayedTrain = new WeakReference<Object>(null);
            this.quitAssembly.active = true;
            this.iconTypeScroll.visible = false;
            this.iconTypeScroll.active = false;
            this.toggleAssemblyButton.setToolTip((Component)CreateLang.translateDirect("station.assemble_train", new Object[0]));
            this.toggleAssemblyButton.setIcon(AllGuiTextures.I_ASSEMBLE_TRAIN);
            this.toggleAssemblyButton.withCallback(() -> CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.tryAssemble(this.blockEntity.getBlockPos())));
        } else {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)StationEditPacket.configure(this.blockEntity.getBlockPos(), false, this.station.name, null));
            this.minecraft.setScreen((Screen)new StationScreen(this.blockEntity, this.station));
        }
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindow(graphics, mouseX, mouseY, partialTicks);
        int x = this.guiLeft;
        int y = this.guiTop;
        MutableComponent header = CreateLang.translateDirect("station.assembly_title", new Object[0]);
        graphics.drawString(this.font, (Component)header, x + this.background.getWidth() / 2 - this.font.width((FormattedText)header) / 2, y + 4, 926259, false);
        AssemblyException lastAssemblyException = this.blockEntity.lastException;
        if (lastAssemblyException != null) {
            MutableComponent text = CreateLang.translateDirect("station.failed", new Object[0]);
            graphics.drawString(this.font, (Component)text, x + 97 - this.font.width((FormattedText)text) / 2, y + 47, 0x775B5B, false);
            int offset = 0;
            if (this.blockEntity.failedCarriageIndex != -1) {
                graphics.drawString(this.font, (Component)CreateLang.translateDirect("station.carriage_number", this.blockEntity.failedCarriageIndex), x + 30, y + 67, 0x7A7A7A, false);
                offset += 10;
            }
            graphics.drawWordWrap(this.font, (FormattedText)lastAssemblyException.component, x + 30, y + 67 + offset, 134, 0x775B5B);
            graphics.drawWordWrap(this.font, (FormattedText)CreateLang.translateDirect("station.retry", new Object[0]), x + 30, y + 67 + (offset += this.font.split((FormattedText)lastAssemblyException.component, 134).size() * 9 + 5), 134, 0x7A7A7A);
            return;
        }
        int bogeyCount = this.blockEntity.bogeyCount;
        MutableComponent text = CreateLang.translateDirect(bogeyCount == 0 ? "station.no_bogeys" : (bogeyCount == 1 ? "station.one_bogey" : "station.more_bogeys"), bogeyCount);
        graphics.drawString(this.font, (Component)text, x + 97 - this.font.width((FormattedText)text) / 2, y + 47, 0x7A7A7A, false);
        graphics.drawWordWrap(this.font, (FormattedText)CreateLang.translateDirect("station.how_to", new Object[0]), x + 28, y + 62, 134, 0x7A7A7A);
        graphics.drawWordWrap(this.font, (FormattedText)CreateLang.translateDirect("station.how_to_1", new Object[0]), x + 28, y + 94, 134, 0x7A7A7A);
        graphics.drawWordWrap(this.font, (FormattedText)CreateLang.translateDirect("station.how_to_2", new Object[0]), x + 28, y + 117, 138, 0x7A7A7A);
    }

    public void removed() {
        super.removed();
        Train train = (Train)this.displayedTrain.get();
        if (train != null) {
            ResourceLocation iconId = this.iconTypes.get(this.iconTypeScroll.getState());
            train.icon = TrainIconType.byId(iconId);
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new TrainEditPacket.Serverbound(train.id, "", iconId, train.mapColorIndex));
        }
    }

    @Override
    protected PartialModel getFlag(float partialTicks) {
        return AllPartialModels.STATION_ASSEMBLE;
    }
}
