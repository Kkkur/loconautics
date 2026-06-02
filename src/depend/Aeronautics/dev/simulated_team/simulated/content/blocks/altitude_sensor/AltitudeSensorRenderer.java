/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.kinetics.base.HorizontalKineticBlock
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.ryanhcode.sable.util.SableDistUtil
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package dev.simulated_team.simulated.content.blocks.altitude_sensor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.sable.util.SableDistUtil;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlock;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class AltitudeSensorRenderer
extends SmartBlockEntityRenderer<AltitudeSensorBlockEntity> {
    public AltitudeSensorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static float calculateLinearDial(float minHeight, float maxHeight, float height) {
        float fraction = (height - minHeight) / (maxHeight - minHeight);
        return Math.min(Math.max(fraction, 0.0f), 1.0f);
    }

    public static void render(BlockState blockState, int tickCount, float dialValue, float visualHeight, PoseStack poseStack, PoseStack contraptionPose, Matrix4f worldLight, MultiBufferSource bufferSource, int light) {
        boolean isRadial;
        Level level = SableDistUtil.getClientLevel();
        VertexConsumer vb = bufferSource.getBuffer(RenderType.cutout());
        SuperByteBuffer indicator = CachedBuffers.partial((PartialModel)SimPartialModels.ALTITUDE_SENSOR_INDICATOR, (BlockState)blockState);
        PartialModel box = SimPartialModels.ALTITUDE_SENSOR_LINEAR_CASE;
        PartialModel dial = SimPartialModels.ALTITUDE_SENSOR_LINEAR_HAND;
        boolean bl = isRadial = blockState.getValue(AltitudeSensorBlock.DIAL) == AltitudeSensorBlock.FaceType.RADIAL;
        if (isRadial) {
            box = SimPartialModels.ALTITUDE_SENSOR_RADIAL_CASE;
            dial = SimPartialModels.ALTITUDE_SENSOR_RADIAL_HAND;
        }
        SuperByteBuffer face = CachedBuffers.partial((PartialModel)box, (BlockState)blockState);
        SuperByteBuffer dialBuffer = CachedBuffers.partial((PartialModel)dial, (BlockState)blockState);
        Direction direction = (Direction)blockState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
        if (contraptionPose != null) {
            face.transform(contraptionPose);
            dialBuffer.transform(contraptionPose);
            indicator.transform(contraptionPose);
        }
        if (isRadial) {
            dialBuffer.rotateCentered(-((float)((double)visualHeight * Math.PI / 2.0)), direction);
        } else {
            dialBuffer.translate(0.0f, (dialValue * 8.0f - 4.0f) / 16.0f, 0.0f);
        }
        AttachFace attachFace = (AttachFace)blockState.getValue((Property)AltitudeSensorBlock.FACE);
        float attachFaceAngle = attachFace == AttachFace.WALL ? 90.0f : (attachFace == AttachFace.CEILING ? 180.0f : 0.0f);
        float time = (float)tickCount + AnimationTickHolder.getPartialTicks();
        float wobbleAngle = (float)(-Math.sin((double)time * 0.8) * Math.exp((double)(-time) / 3.5)) * 0.7f;
        float yRot = !direction.getAxis().equals((Object)Direction.Axis.Z) ? (float)Math.toRadians(((Direction)blockState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)).getOpposite().toYRot()) : (float)Math.toRadians(((Direction)blockState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)).toYRot());
        ((SuperByteBuffer)face.rotateCentered((float)((double)yRot + Math.PI), Direction.UP)).rotateCentered(wobbleAngle, Direction.WEST);
        ((SuperByteBuffer)dialBuffer.rotateCentered((float)((double)yRot + Math.PI), Direction.UP)).rotateCentered(wobbleAngle, Direction.WEST);
        ((SuperByteBuffer)indicator.rotateCentered((float)((double)yRot + Math.PI), Direction.UP)).rotateCentered((float)Math.toRadians(attachFaceAngle), Direction.WEST);
        if (worldLight != null) {
            face.useLevelLight((BlockAndTintGetter)level, new Matrix4f((Matrix4fc)worldLight));
            dialBuffer.useLevelLight((BlockAndTintGetter)level, new Matrix4f((Matrix4fc)worldLight));
            indicator.useLevelLight((BlockAndTintGetter)level, new Matrix4f((Matrix4fc)worldLight));
        }
        face.light(light);
        dialBuffer.light(light);
        indicator.light(light);
        int color = SimColors.redstone(dialValue);
        indicator.color(color);
        face.renderInto(poseStack, vb);
        dialBuffer.renderInto(poseStack, vb);
        indicator.renderInto(poseStack, vb);
    }

    protected void renderSafe(AltitudeSensorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe((SmartBlockEntity)be, partialTicks, ms, buffer, light, overlay);
        AltitudeSensorRenderer.render(be.getBlockState(), be.tickCount, be.getValue(), be.getVisualHeight(partialTicks), ms, null, null, buffer, light);
    }
}
