/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector4f
 */
package dev.simulated_team.simulated.content.blocks.lasers.laser_pointer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.lasers.AbstractLaserRenderer;
import dev.simulated_team.simulated.content.blocks.lasers.LaserBehaviour;
import dev.simulated_team.simulated.content.blocks.lasers.laser_pointer.LaserPointerBlock;
import dev.simulated_team.simulated.content.blocks.lasers.laser_pointer.LaserPointerBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.index.SimRenderTypes;
import dev.simulated_team.simulated.util.SimColors;
import java.awt.Color;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector4f;

public class LaserPointerRenderer
extends AbstractLaserRenderer<LaserPointerBlockEntity> {
    public LaserPointerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(LaserPointerBlockEntity blockEntity, float partialTicks, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        Vector4f colors = this.getColors(blockEntity, partialTicks);
        boolean isDarkerThanDark = colors.x == 0.0f && colors.y == 0.0f && colors.z == 0.0f;
        SuperByteBuffer superBuffer = blockEntity.shouldCast() && !isDarkerThanDark ? CachedBuffers.partial((PartialModel)SimPartialModels.LASER_POINTER_LENS_ON, (BlockState)blockEntity.getBlockState()) : CachedBuffers.partial((PartialModel)SimPartialModels.LASER_POINTER_LENS_OFF, (BlockState)blockEntity.getBlockState());
        superBuffer.translate(0.5, 0.5, 0.5);
        superBuffer.rotateToFace((Direction)blockEntity.getBlockState().getValue((Property)LaserPointerBlock.FACING));
        superBuffer.translate(-0.5, -0.5, -0.5);
        if (blockEntity.shouldCast()) {
            superBuffer.light(0xF000F0);
        } else {
            superBuffer.light(light);
        }
        superBuffer.disableDiffuse();
        superBuffer.color((int)(colors.x * 255.0f), (int)(colors.z * 255.0f), (int)(colors.y * 255.0f), 255);
        superBuffer.renderInto(pose, buffer.getBuffer(SimRenderTypes.lens()));
        if (!isDarkerThanDark) {
            super.renderSafe(blockEntity, partialTicks, pose, buffer, light, overlay);
        }
    }

    @Override
    public float getLaserScale(LaserBehaviour laser) {
        return 0.48f;
    }

    @Override
    public Vector4f getColors(LaserPointerBlockEntity blockEntity, float partialTicks) {
        Color c = new Color(blockEntity.laserColor);
        if (blockEntity.isRainbow()) {
            float t;
            Vector3d baseLCh = SimColors.LabToLCh((Vector3dc)SimColors.toOklab(c));
            if (blockEntity.isVirtual()) {
                t = (float)((double)(Util.getMillis() % 5000L * 2L) * Math.PI / 5000.0);
            } else {
                long timeOff = blockEntity.getLevel().getGameTime();
                t = (float)((double)(((float)(timeOff % 100L) + partialTicks) * 2.0f) * Math.PI / 100.0);
            }
            c = SimColors.LChOklab(0.8f, 0.3f, (float)((double)t + baseLCh.z()));
        }
        return new Vector4f((float)c.getRed() / 255.0f, (float)c.getBlue() / 255.0f, (float)c.getGreen() / 255.0f, (float)blockEntity.getPower() / 60.0f);
    }
}
