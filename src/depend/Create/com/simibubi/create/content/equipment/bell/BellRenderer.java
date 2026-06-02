/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.equipment.bell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.bell.AbstractBellBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.Property;

public class BellRenderer<BE extends AbstractBellBlockEntity>
extends SafeBlockEntityRenderer<BE> {
    public BellRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(BE be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getBlockState();
        Direction facing = (Direction)state.getValue((Property)BellBlock.FACING);
        BellAttachType attachment = (BellAttachType)state.getValue((Property)BellBlock.ATTACHMENT);
        SuperByteBuffer bell = CachedBuffers.partial((PartialModel)((AbstractBellBlockEntity)be).getBellModel(), (BlockState)state);
        if (((AbstractBellBlockEntity)be).isRinging) {
            bell.rotateCentered(BellRenderer.getSwingAngle((float)((AbstractBellBlockEntity)be).ringingTicks + partialTicks), ((AbstractBellBlockEntity)be).ringDirection.getCounterClockWise());
        }
        float rY = AngleHelper.horizontalAngle((Direction)facing);
        if (attachment == BellAttachType.SINGLE_WALL || attachment == BellAttachType.DOUBLE_WALL) {
            rY += 90.0f;
        }
        bell.rotateCentered(AngleHelper.rad((double)rY), Direction.UP);
        bell.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }

    public static float getSwingAngle(float time) {
        float t = time / 1.5f;
        return 1.2f * Mth.sin((float)(t / (float)Math.PI)) / (2.5f + t / 3.0f);
    }
}
