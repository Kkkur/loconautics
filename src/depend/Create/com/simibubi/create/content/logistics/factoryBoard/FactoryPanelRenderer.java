/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.List;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class FactoryPanelRenderer
extends SmartBlockEntityRenderer<FactoryPanelBlockEntity> {
    public FactoryPanelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FactoryPanelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        for (FactoryPanelBehaviour behaviour : be.panels.values()) {
            if (!behaviour.isActive()) continue;
            if (behaviour.getAmount() > 0) {
                FactoryPanelRenderer.renderBulb(behaviour, partialTicks, ms, buffer, light, overlay);
            }
            for (FactoryPanelConnection connection : behaviour.targetedBy.values()) {
                FactoryPanelRenderer.renderPath(behaviour, connection, partialTicks, ms, buffer, light, overlay);
            }
            for (FactoryPanelConnection connection : behaviour.targetedByLinks.values()) {
                FactoryPanelRenderer.renderPath(behaviour, connection, partialTicks, ms, buffer, light, overlay);
            }
        }
    }

    public static void renderBulb(FactoryPanelBehaviour behaviour, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = behaviour.blockEntity.getBlockState();
        float xRot = FactoryPanelBlock.getXRot(blockState) + 1.5707964f;
        float yRot = FactoryPanelBlock.getYRot(blockState);
        float glow = behaviour.bulb.getValue(partialTicks);
        boolean missingAddress = behaviour.isMissingAddress();
        PartialModel partial = behaviour.redstonePowered || missingAddress ? AllPartialModels.FACTORY_PANEL_RED_LIGHT : AllPartialModels.FACTORY_PANEL_LIGHT;
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)partial, (BlockState)blockState).rotateCentered(yRot, Direction.UP)).rotateCentered(xRot, Direction.EAST)).rotateCentered((float)Math.PI, Direction.UP)).translate((double)behaviour.slot.xOffset * 0.5, 0.0, (double)behaviour.slot.yOffset * 0.5)).light(glow > 0.125f ? 0xF000F0 : light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.translucent()));
        if (glow < 0.125f) {
            return;
        }
        glow = (float)(1.0 - 2.0 * Math.pow(glow - 0.75f, 2.0));
        glow = Mth.clamp((float)glow, (float)-1.0f, (float)1.0f);
        int color = (int)(200.0f * glow);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)partial, (BlockState)blockState).rotateCentered(yRot, Direction.UP)).rotateCentered(xRot, Direction.EAST)).rotateCentered((float)Math.PI, Direction.UP)).translate((double)behaviour.slot.xOffset * 0.5, 0.0, (double)behaviour.slot.yOffset * 0.5)).light(0xF000F0).color(color, color, color, 255).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
    }

    public static void renderPath(FactoryPanelBehaviour behaviour, FactoryPanelConnection connection, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = behaviour.blockEntity.getBlockState();
        List<Direction> path = connection.getPath(behaviour.getWorld(), blockState, behaviour.getPanelPosition());
        float xRot = FactoryPanelBlock.getXRot(blockState) + 1.5707964f;
        float yRot = FactoryPanelBlock.getYRot(blockState);
        float glow = behaviour.bulb.getValue(partialTicks);
        FactoryPanelSupportBehaviour sbe = FactoryPanelBehaviour.linkAt((BlockAndTintGetter)behaviour.getWorld(), connection);
        boolean displayLinkMode = sbe != null && sbe.blockEntity instanceof DisplayLinkBlockEntity;
        boolean redstoneLinkMode = sbe != null && sbe.blockEntity instanceof RedstoneLinkBlockEntity;
        boolean pathReversed = sbe != null && !sbe.isOutput();
        int color = 0;
        float yOffset = 0.0f;
        boolean success = connection.success;
        boolean dots = false;
        if (displayLinkMode) {
            color = 3971154;
            dots = true;
        } else if (redstoneLinkMode) {
            color = pathReversed ? (behaviour.count == 0 ? 0x888898 : (behaviour.satisfied ? 0xEF0000 : 5767425)) : (behaviour.redstonePowered ? 0xEF0000 : 5767425);
            yOffset = 0.5f;
        } else {
            color = behaviour.getIngredientStatusColor();
            yOffset = 1.0f;
            yOffset += behaviour.promisedSatisfied ? 1.0f : (behaviour.satisfied ? 0.0f : 2.0f);
            if (!behaviour.redstonePowered && !behaviour.waitingForNetwork && glow > 0.0f && !behaviour.satisfied) {
                float p = 1.0f - (1.0f - glow) * (1.0f - glow);
                color = Color.mixColors((int)color, (int)(success ? 15397612 : 15033675), (float)p);
                if (!behaviour.satisfied && !behaviour.promisedSatisfied) {
                    yOffset += (float)(success ? 1 : 2) * p;
                }
            }
        }
        float currentX = 0.0f;
        float currentZ = 0.0f;
        for (int i = 0; i < path.size(); ++i) {
            boolean isArrowSegment;
            Direction direction = path.get(i);
            if (!pathReversed) {
                currentX = (float)((double)currentX + (double)direction.getStepX() * 0.5);
                currentZ = (float)((double)currentZ + (double)direction.getStepZ() * 0.5);
            }
            boolean bl = pathReversed ? i == path.size() - 1 : (isArrowSegment = i == 0);
            PartialModel partial = (dots ? AllPartialModels.FACTORY_PANEL_DOTTED : (isArrowSegment ? AllPartialModels.FACTORY_PANEL_ARROWS : AllPartialModels.FACTORY_PANEL_LINES)).get(pathReversed ? direction : direction.getOpposite());
            SuperByteBuffer connectionSprite = (SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)partial, (BlockState)blockState).rotateCentered(yRot, Direction.UP)).rotateCentered(xRot, Direction.EAST)).rotateCentered((float)Math.PI, Direction.UP)).translate((double)behaviour.slot.xOffset * 0.5 + 0.25, 0.0, (double)behaviour.slot.yOffset * 0.5 + 0.25)).translate(currentX, (yOffset + (float)(direction.get2DDataValue() % 2) * 0.125f) / 512.0f, currentZ);
            if (!(displayLinkMode || redstoneLinkMode || behaviour.isMissingAddress() || behaviour.waitingForNetwork || behaviour.satisfied || behaviour.redstonePowered)) {
                connectionSprite.shiftUV(AllSpriteShifts.FACTORY_PANEL_CONNECTIONS);
            }
            connectionSprite.color(color).light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
            if (!pathReversed) continue;
            currentX = (float)((double)currentX + (double)direction.getStepX() * 0.5);
            currentZ = (float)((double)currentZ + (double)direction.getStepZ() * 0.5);
        }
    }
}
