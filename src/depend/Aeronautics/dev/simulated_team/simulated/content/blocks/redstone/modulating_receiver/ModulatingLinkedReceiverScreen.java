/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.simibubi.create.foundation.gui.AllIcons
 *  com.simibubi.create.foundation.gui.widget.IconButton
 *  com.simibubi.create.foundation.gui.widget.ScrollInput
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverBlock;
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.network.packets.ConfigureModulatingLinkedRecieverPacket;
import dev.simulated_team.simulated.util.SimColors;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ModulatingLinkedReceiverScreen
extends AbstractSimiScreen {
    private final ModulatingLinkedReceiverBlockEntity be;
    private final SimGUITextures background;
    private IconButton confirmButton;
    private ScrollInput minScroll;
    private ScrollInput maxScroll;
    private int lastModification;

    public ModulatingLinkedReceiverScreen(ModulatingLinkedReceiverBlockEntity be) {
        super((Component)SimLang.translate("gui.modulating_linked_receiver.title", new Object[0]).component());
        this.be = be;
        this.background = SimGUITextures.MODULATINGLINK;
        this.lastModification = -1;
    }

    public static void open(ModulatingLinkedReceiverBlockEntity be) {
        ScreenOpener.open((Screen)new ModulatingLinkedReceiverScreen(be));
    }

    public boolean isThisBlock(BlockPos pos) {
        return this.be.getBlockPos().equals((Object)pos);
    }

    protected void init() {
        this.setWindowSize(this.background.width, this.background.height);
        this.setWindowOffset(-20, 0);
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        this.confirmButton = new IconButton(x + this.background.width - 33, y + this.background.height - 24, (ScreenElement)AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.onClose());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.minScroll = new ScrollInput(x + 55, y + 47, 26, 16);
        this.maxScroll = new ScrollInput(x + 132, y + 47, 26, 16);
        this.minScroll.calling(value -> {
            this.be.minRange = value;
            this.be.maxRange = Math.max(this.be.maxRange, value);
            this.maxScroll.setState(this.be.maxRange);
            this.lastModification = 0;
        });
        this.maxScroll.calling(value -> {
            this.be.maxRange = value;
            this.be.minRange = Math.min(this.be.minRange, value);
            this.minScroll.setState(this.be.minRange);
            this.lastModification = 0;
        });
        this.minScroll.withRange(1, 257).titled(SimLang.translate("gui.modulating_linked_receiver.minimum_range", new Object[0]).component()).withShiftStep(10).setState(this.be.minRange).onChanged();
        this.maxScroll.withRange(1, 257).titled(SimLang.translate("gui.modulating_linked_receiver.minimum_range", new Object[0]).component()).withShiftStep(10).setState(this.be.maxRange).onChanged();
        this.addRenderableWidgets((GuiEventListener[])new ScrollInput[]{this.minScroll});
        this.addRenderableWidgets((GuiEventListener[])new ScrollInput[]{this.maxScroll});
    }

    public static int distanceGuiOffset(float value, float maxValue, float width, float smoothing) {
        return Math.round(width * (value - 1.0f) * (smoothing + maxValue - 1.0f) / ((maxValue - 1.0f) * (smoothing + value - 1.0f)));
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop;
        PoseStack ms = graphics.pose();
        this.background.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + (this.background.width - 8) / 2 - this.font.width((FormattedText)this.title) / 2, y + 4, SimColors.TITLE_DARK_RED, false);
        int currentX = 22;
        this.label(graphics, currentX, 25, (Component)SimLang.translate("gui.modulating_linked_receiver.min", new Object[0]).component());
        String text = Integer.toString(this.be.minRange);
        int stringWidth = this.font.width(text);
        this.label(graphics, currentX + 34 + (12 - stringWidth / 2), 25, (Component)Component.literal((String)text));
        this.label(graphics, currentX += 77, 25, (Component)SimLang.translate("gui.modulating_linked_receiver.max", new Object[0]).component());
        text = Integer.toString(this.be.maxRange);
        stringWidth = this.font.width(text);
        this.label(graphics, currentX + 34 + (12 - stringWidth / 2), 25, (Component)Component.literal((String)text));
        int bandStart = 37;
        int bandEnd = 156;
        int bandWidth = 119;
        float smoothing = 20.0f;
        float maxDistance = 256.0f;
        int minPos = 37 + ModulatingLinkedReceiverScreen.distanceGuiOffset(this.be.minRange, 256.0f, 119.0f, 20.0f);
        int maxPos = 37 + ModulatingLinkedReceiverScreen.distanceGuiOffset(this.be.maxRange, 256.0f, 119.0f, 20.0f);
        SimGUITextures sprite = SimGUITextures.MODULATINGLINK_POWERED_LANE;
        sprite.bind();
        graphics.blit(sprite.location, x + 37 + 1, y + 25, sprite.startX, sprite.startY, minPos - 37, sprite.height);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float imageSize = 256.0f;
        float uvx1 = (float)(sprite.startX + minPos - 37) / 256.0f;
        float uvx2 = (float)(sprite.startX + maxPos - 37) / 256.0f;
        float uvy1 = (float)sprite.startY / 256.0f;
        float uvy2 = (float)(sprite.startY + sprite.height) / 256.0f;
        float px1 = x + minPos;
        float px2 = x + maxPos;
        float py1 = y + 25;
        float py2 = y + 25 + sprite.height;
        bufferbuilder.addVertex(ms.last().pose(), px2, py1, 0.0f).setUv(uvx2, uvy1).setColor(1.0f, 1.0f, 1.0f, 0.0f);
        bufferbuilder.addVertex(ms.last().pose(), px1, py1, 0.0f).setUv(uvx1, uvy1).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        bufferbuilder.addVertex(ms.last().pose(), px1, py2, 0.0f).setUv(uvx1, uvy2).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        bufferbuilder.addVertex(ms.last().pose(), px2, py2, 0.0f).setUv(uvx2, uvy2).setColor(1.0f, 1.0f, 1.0f, 0.0f);
        BufferUploader.drawWithShader((MeshData)bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
        SimGUITextures.MODULATINGLINK_MARKER.render(graphics, x + minPos, y + 23);
        SimGUITextures.MODULATINGLINK_MARKER.render(graphics, x + maxPos, y + 23);
        if (this.be.getClientDistance(partialTicks) < (double)ModulatingLinkedReceiverBlockEntity.RANGE_LIMIT) {
            int sourcePos = 37 + ModulatingLinkedReceiverScreen.distanceGuiOffset((float)this.be.getClientDistance(partialTicks), 256.0f, 119.0f, 20.0f);
            SimGUITextures.MODULATINGLINK_TARGET.render(graphics, x + sourcePos, y + 16);
        }
        float minPos2 = 5.5f * ((float)(this.be.minRange - 1) * 275.0f) / (255.0f * (20.0f + (float)this.be.minRange - 1.0f));
        float maxPos2 = 5.5f * ((float)(this.be.maxRange - 1) * 275.0f) / (255.0f * (20.0f + (float)this.be.maxRange - 1.0f));
        for (boolean bottom : Iterate.trueAndFalse) {
            PoseTransformStack msr = TransformStack.of((PoseStack)ms);
            ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)msr.pushPose()).translate((float)(x + this.background.width + 4), (float)(y + this.background.height + 4), 100.0f).scale(40.0f)).rotateXDegrees(-22.0f)).rotateYDegrees(63.0f);
            if (!bottom) {
                msr.translate(0.0, -0.03125, 0.0);
            }
            msr.translate(0.0, (double)(-(bottom ? minPos2 : maxPos2)) / 16.0, 0.0);
            GuiGameElement.of((PartialModel)SimPartialModels.MODULATING_RECEIVER_PLATE).render(graphics);
            msr.popPose();
        }
        ms.pushPose();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)msr.pushPose()).translate((float)(x + this.background.width + 4), (float)(y + this.background.height + 4), 100.0f).scale(40.0f)).rotateXDegrees(-22.0f)).rotateYDegrees(63.0f);
        GuiGameElement.of((BlockState)((BlockState)this.be.getBlockState().setValue((Property)ModulatingLinkedReceiverBlock.FACING, (Comparable)Direction.UP))).render(graphics);
        msr.popPose();
    }

    private void label(GuiGraphics graphics, int x, int y, Component text) {
        graphics.drawString(this.font, text, this.guiLeft + x, this.guiTop + 26 + y, 0xFFFFEE);
    }

    public void tick() {
        super.tick();
        if (this.lastModification >= 0) {
            ++this.lastModification;
        }
        if (this.lastModification >= 20) {
            this.lastModification = -1;
            this.send();
        }
    }

    public void removed() {
        this.send();
    }

    protected void send() {
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new ConfigureModulatingLinkedRecieverPacket(this.be.getBlockPos(), this.minScroll.getState(), this.maxScroll.getState())});
    }
}
