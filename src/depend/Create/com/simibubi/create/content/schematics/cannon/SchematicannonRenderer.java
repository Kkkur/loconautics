/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.ponder.render.VirtualRenderHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.schematics.cannon.LaunchedItem;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.render.VirtualRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SchematicannonRenderer
extends SafeBlockEntityRenderer<SchematicannonBlockEntity> {
    public SchematicannonRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SchematicannonBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        boolean blocksLaunching;
        boolean bl = blocksLaunching = !blockEntity.flyingBlocks.isEmpty();
        if (blocksLaunching) {
            SchematicannonRenderer.renderLaunchedBlocks(blockEntity, partialTicks, ms, buffer, light, overlay);
        }
        if (VisualizationManager.supportsVisualization((LevelAccessor)blockEntity.getLevel())) {
            return;
        }
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        double[] cannonAngles = SchematicannonRenderer.getCannonAngles(blockEntity, pos, partialTicks);
        double yaw = cannonAngles[0];
        double pitch = cannonAngles[1];
        double recoil = SchematicannonRenderer.getRecoil(blockEntity, partialTicks);
        ms.pushPose();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer connector = CachedBuffers.partial((PartialModel)AllPartialModels.SCHEMATICANNON_CONNECTOR, (BlockState)state);
        connector.translate(0.5f, 0.0f, 0.5f);
        connector.rotate((float)((yaw + 90.0) / 180.0 * Math.PI), Direction.UP);
        connector.translate(-0.5f, 0.0f, -0.5f);
        connector.light(light).renderInto(ms, vb);
        SuperByteBuffer pipe = CachedBuffers.partial((PartialModel)AllPartialModels.SCHEMATICANNON_PIPE, (BlockState)state);
        pipe.translate(0.5f, 0.9375f, 0.5f);
        pipe.rotate((float)((yaw + 90.0) / 180.0 * Math.PI), Direction.UP);
        pipe.rotate((float)(pitch / 180.0 * Math.PI), Direction.SOUTH);
        pipe.translate(-0.5f, -0.9375f, -0.5f);
        pipe.translate(0.0, -recoil / 100.0, 0.0);
        pipe.light(light).renderInto(ms, vb);
        ms.popPose();
    }

    public static double[] getCannonAngles(SchematicannonBlockEntity blockEntity, BlockPos pos, float partialTicks) {
        double pitch;
        double yaw;
        BlockPos target = blockEntity.printer.getCurrentTarget();
        if (target != null) {
            Vec3 diff = Vec3.atLowerCornerOf((Vec3i)target.subtract((Vec3i)pos));
            if (blockEntity.previousTarget != null) {
                diff = Vec3.atLowerCornerOf((Vec3i)blockEntity.previousTarget).add(Vec3.atLowerCornerOf((Vec3i)target.subtract((Vec3i)blockEntity.previousTarget)).scale((double)partialTicks)).subtract(Vec3.atLowerCornerOf((Vec3i)pos));
            }
            double diffX = diff.x();
            double diffZ = diff.z();
            yaw = Mth.atan2((double)diffX, (double)diffZ);
            yaw = yaw / Math.PI * 180.0;
            float distance = Mth.sqrt((float)((float)(diffX * diffX + diffZ * diffZ)));
            double yOffset = 0.0f + distance * 2.0f;
            pitch = Mth.atan2((double)distance, (double)(diff.y() * 3.0 + yOffset));
            pitch = pitch / Math.PI * 180.0 + 10.0;
        } else {
            yaw = blockEntity.defaultYaw;
            pitch = 40.0;
        }
        return new double[]{yaw, pitch};
    }

    public static double getRecoil(SchematicannonBlockEntity blockEntity, float partialTicks) {
        double recoil = 0.0;
        for (LaunchedItem launched : blockEntity.flyingBlocks) {
            if (launched.ticksRemaining == 0 || !((float)(launched.ticksRemaining + 1) - partialTicks > (float)(launched.totalTicks - 10))) continue;
            recoil = Math.max(recoil, (double)((float)(launched.ticksRemaining + 1) - partialTicks - (float)launched.totalTicks + 10.0f));
        }
        return recoil;
    }

    private static void renderLaunchedBlocks(SchematicannonBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        for (LaunchedItem launched : blockEntity.flyingBlocks) {
            if (launched.ticksRemaining == 0) continue;
            Vec3 start = Vec3.atCenterOf((Vec3i)blockEntity.getBlockPos().above());
            Vec3 target = Vec3.atCenterOf((Vec3i)launched.target);
            Vec3 distance = target.subtract(start);
            double yDifference = target.y - start.y;
            double throwHeight = Math.sqrt(distance.lengthSqr()) * (double)0.6f + yDifference;
            Vec3 cannonOffset = distance.add(0.0, throwHeight, 0.0).normalize().scale(2.0);
            start = start.add(cannonOffset);
            yDifference = target.y - start.y;
            float progress = ((float)launched.totalTicks - ((float)(launched.ticksRemaining + 1) - partialTicks)) / (float)launched.totalTicks;
            Vec3 blockLocationXZ = target.subtract(start).scale((double)progress).multiply(1.0, 0.0, 1.0);
            float t = progress;
            double yOffset = (double)(2.0f * (1.0f - t) * t) * throwHeight + (double)(t * t) * yDifference;
            Vec3 blockLocation = blockLocationXZ.add(0.5, yOffset + 1.5, 0.5).add(cannonOffset);
            ms.pushPose();
            ms.translate(blockLocation.x, blockLocation.y, blockLocation.z);
            ms.translate(0.125f, 0.125f, 0.125f);
            ms.mulPose(Axis.YP.rotationDegrees(360.0f * t));
            ms.mulPose(Axis.XP.rotationDegrees(360.0f * t));
            ms.translate(-0.125f, -0.125f, -0.125f);
            if (launched instanceof LaunchedItem.ForBlockState) {
                BlockState state = launched instanceof LaunchedItem.ForBelt ? AllBlocks.SHAFT.getDefaultState() : ((LaunchedItem.ForBlockState)launched).state;
                float scale = 0.3f;
                ms.scale(scale, scale, scale);
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, ms, buffer, light, overlay, VirtualRenderHelper.VIRTUAL_DATA, null);
            } else if (launched instanceof LaunchedItem.ForEntity) {
                float scale = 1.2f;
                ms.scale(scale, scale, scale);
                Minecraft.getInstance().getItemRenderer().renderStatic(launched.stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, blockEntity.getLevel(), 0);
            }
            ms.popPose();
            if (launched.ticksRemaining != launched.totalTicks || !blockEntity.firstRenderTick) continue;
            start = start.subtract(0.5, 0.5, 0.5);
            blockEntity.firstRenderTick = false;
            for (int i = 0; i < 10; ++i) {
                RandomSource r = blockEntity.getLevel().getRandom();
                double sX = cannonOffset.x * (double)0.01f;
                double sY = (cannonOffset.y + 1.0) * (double)0.01f;
                double sZ = cannonOffset.z * (double)0.01f;
                double rX = (double)r.nextFloat() - sX * 40.0;
                double rY = (double)r.nextFloat() - sY * 40.0;
                double rZ = (double)r.nextFloat() - sZ * 40.0;
                blockEntity.getLevel().addParticle((ParticleOptions)ParticleTypes.CLOUD, start.x + rX, start.y + rY, start.z + rZ, sX, sY, sZ);
            }
        }
    }

    public boolean shouldRenderOffScreen(SchematicannonBlockEntity blockEntity) {
        return true;
    }

    public int getViewDistance() {
        return 128;
    }
}
