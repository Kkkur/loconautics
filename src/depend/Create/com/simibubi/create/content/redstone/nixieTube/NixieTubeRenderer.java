/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.gui.font.glyphs.BakedGlyph
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.redstone.nixieTube;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.nixieTube.DoubleFaceAttachedBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.DyeHelper;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class NixieTubeRenderer
extends SafeBlockEntityRenderer<NixieTubeBlockEntity> {
    private static final int GLOW_VIEW_DISTANCE = 96;

    public NixieTubeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(NixieTubeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        BlockState blockState = be.getBlockState();
        DoubleFaceAttachedBlock.DoubleAttachFace face = (DoubleFaceAttachedBlock.DoubleAttachFace)((Object)blockState.getValue((Property)NixieTubeBlock.FACE));
        float yRot = AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue((Property)NixieTubeBlock.FACING))) - 90.0f + (float)(face == DoubleFaceAttachedBlock.DoubleAttachFace.WALL_REVERSED ? 180 : 0);
        float xRot = face == DoubleFaceAttachedBlock.DoubleAttachFace.WALL ? -90.0f : (face == DoubleFaceAttachedBlock.DoubleAttachFace.WALL_REVERSED ? 90.0f : 0.0f);
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)msr.center()).rotateYDegrees(yRot)).rotateZDegrees(xRot)).uncenter();
        if (be.signalState != null || be.computerSignal != null) {
            this.renderAsSignal(be, partialTicks, ms, buffer, light, overlay);
            ms.popPose();
            return;
        }
        msr.center();
        float height = face == DoubleFaceAttachedBlock.DoubleAttachFace.CEILING ? 5.0f : 3.0f;
        float scale = 0.05f;
        Couple<String> s = be.getDisplayedStrings();
        DyeColor color = NixieTubeBlock.colorOf(be.getBlockState());
        RandomSource random = be.getLevel().getRandom();
        ms.pushPose();
        ms.translate(-0.25f, 0.0f, 0.0f);
        ms.scale(scale, -scale, scale);
        NixieTubeRenderer.drawTube(ms, buffer, (String)s.getFirst(), height, color, random);
        ms.popPose();
        ms.pushPose();
        ms.translate(0.25f, 0.0f, 0.0f);
        ms.scale(scale, -scale, scale);
        NixieTubeRenderer.drawTube(ms, buffer, (String)s.getSecond(), height, color, random);
        ms.popPose();
        ms.popPose();
    }

    public static void drawTube(PoseStack ms, MultiBufferSource buffer, String c, float height, DyeColor color, RandomSource random) {
        Font fontRenderer = Minecraft.getInstance().font;
        float charWidth = fontRenderer.width(c);
        float shadowOffset = 0.5f;
        float flicker = random.nextFloat();
        Couple<Integer> couple = DyeHelper.getDyeColors(color);
        int brightColor = (Integer)couple.getFirst();
        int darkColor = (Integer)couple.getSecond();
        int flickeringBrightColor = Color.mixColors((int)brightColor, (int)darkColor, (float)(flicker / 4.0f));
        ms.pushPose();
        ms.translate((charWidth - shadowOffset) / -2.0f, -height, 0.0f);
        NixieTubeRenderer.drawInWorldString(ms, buffer, c, flickeringBrightColor);
        ms.pushPose();
        ms.translate(shadowOffset, shadowOffset, -0.0625f);
        NixieTubeRenderer.drawInWorldString(ms, buffer, c, darkColor);
        ms.popPose();
        ms.popPose();
        ms.pushPose();
        ms.scale(-1.0f, 1.0f, 1.0f);
        ms.translate((charWidth - shadowOffset) / -2.0f, -height, 0.0f);
        NixieTubeRenderer.drawInWorldString(ms, buffer, c, darkColor);
        ms.pushPose();
        ms.translate(-shadowOffset, shadowOffset, -0.0625f);
        NixieTubeRenderer.drawInWorldString(ms, buffer, c, Color.mixColors((int)darkColor, (int)0, (float)0.35f));
        ms.popPose();
        ms.popPose();
    }

    public static void drawInWorldString(PoseStack ms, MultiBufferSource buffer, String c, int color) {
        Font fontRenderer = Minecraft.getInstance().font;
        fontRenderer.drawInBatch(c, 0.0f, 0.0f, color, false, ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        if (buffer instanceof MultiBufferSource.BufferSource) {
            BakedGlyph texturedglyph = fontRenderer.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            ((MultiBufferSource.BufferSource)buffer).endBatch(texturedglyph.renderType(Font.DisplayMode.NORMAL));
        }
    }

    private void renderAsSignal(NixieTubeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        Direction facing = NixieTubeBlock.getFacing(blockState);
        Vec3 observerVec = Minecraft.getInstance().cameraEntity.getEyePosition(partialTicks);
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        if (facing == Direction.DOWN) {
            ((PoseTransformStack)((PoseTransformStack)msr.center()).rotateZDegrees(180.0f)).uncenter();
        }
        boolean invertTubes = facing == Direction.DOWN || blockState.getValue((Property)NixieTubeBlock.FACE) == DoubleFaceAttachedBlock.DoubleAttachFace.WALL_REVERSED;
        CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_PANEL, (BlockState)blockState).light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        ms.pushPose();
        ms.translate(0.5f, 0.46875f, 0.5f);
        float renderTime = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        Vec3 lampVec = Vec3.atCenterOf((Vec3i)be.getBlockPos());
        Vec3 diff = lampVec.subtract(observerVec);
        if (be.signalState != null) {
            for (boolean first : Iterate.trueAndFalse) {
                if (first && !be.signalState.isRedLight(renderTime) || !first && !be.signalState.isGreenLight(renderTime) && !be.signalState.isYellowLight(renderTime)) continue;
                boolean flip = first == invertTubes;
                boolean yellow = be.signalState.isYellowLight(renderTime);
                ms.pushPose();
                ms.translate(flip ? 0.25f : -0.25f, 0.0f, 0.0f);
                if (diff.lengthSqr() < 9216.0) {
                    boolean vert = first ^ facing.getAxis().isHorizontal();
                    float longSide = yellow ? 1.0f : 4.0f;
                    float longSideGlow = yellow ? 2.0f : 5.125f;
                    ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_WHITE_CUBE, (BlockState)blockState).light(0xF000F0).disableDiffuse().scale(vert ? longSide : 1.0f, vert ? 1.0f : longSide, 1.0f)).renderInto(ms, buffer.getBuffer(RenderType.translucent()));
                    ((SuperByteBuffer)CachedBuffers.partial((PartialModel)(first ? AllPartialModels.SIGNAL_RED_GLOW : (yellow ? AllPartialModels.SIGNAL_YELLOW_GLOW : AllPartialModels.SIGNAL_WHITE_GLOW)), (BlockState)blockState).light(0xF000F0).disableDiffuse().scale(vert ? longSideGlow : 2.0f, vert ? 2.0f : longSideGlow, 2.0f)).renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
                }
                ((SuperByteBuffer)CachedBuffers.partial((PartialModel)(first ? AllPartialModels.SIGNAL_RED : (yellow ? AllPartialModels.SIGNAL_YELLOW : AllPartialModels.SIGNAL_WHITE)), (BlockState)blockState).light(0xF000F0).disableDiffuse().scale(1.0625f)).renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
                ms.popPose();
            }
        } else if (be.computerSignal != null) {
            for (boolean first : Iterate.trueAndFalse) {
                NixieTubeBlockEntity.ComputerSignal.TubeDisplay tubeDisplay;
                NixieTubeBlockEntity.ComputerSignal.TubeDisplay tubeDisplay2 = tubeDisplay = first ? be.computerSignal.first : be.computerSignal.second;
                if (tubeDisplay.blinkPeriod == 0 || tubeDisplay.blinkPeriod > 1 && renderTime % (float)tubeDisplay.blinkPeriod < (float)tubeDisplay.blinkOffTime) continue;
                boolean flip = first == invertTubes;
                ms.pushPose();
                ms.translate(flip ? 0.25f : -0.25f, 0.0f, 0.0f);
                if (diff.lengthSqr() < 9216.0) {
                    boolean horiz = facing.getAxis().isHorizontal();
                    float width = horiz ? (float)tubeDisplay.glowWidth : (float)tubeDisplay.glowHeight;
                    float height = horiz ? (float)tubeDisplay.glowHeight : (float)tubeDisplay.glowWidth;
                    ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_COMPUTER_WHITE_CUBE, (BlockState)blockState).light(0xF000F0).disableDiffuse().scale(width, height, 1.0f)).renderInto(ms, buffer.getBuffer(RenderType.translucent()));
                    ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_COMPUTER_WHITE_GLOW, (BlockState)blockState).light(0xF000F0).color(Math.min((tubeDisplay.r & 0xFF) * 6 + 256 >> 3, 255), Math.min((tubeDisplay.g & 0xFF) * 6 + 256 >> 3, 255), Math.min((tubeDisplay.b & 0xFF) * 6 + 256 >> 3, 255), 255).disableDiffuse().scale(width + 1.125f, height + 1.125f, 2.0f)).renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
                }
                ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_COMPUTER_WHITE_BASE, (BlockState)blockState).light(0xF000F0).color(12, 12, 12, 255).disableDiffuse().scale(1.078125f)).renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
                ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.SIGNAL_COMPUTER_WHITE, (BlockState)blockState).light(0xF000F0).color((int)tubeDisplay.r, (int)tubeDisplay.g, (int)tubeDisplay.b, 255).disableDiffuse().scale(1.0625f)).renderInto(ms, buffer.getBuffer(RenderTypes.additive()));
                ms.popPose();
            }
        }
        ms.popPose();
    }

    public int getViewDistance() {
        return 128;
    }
}
