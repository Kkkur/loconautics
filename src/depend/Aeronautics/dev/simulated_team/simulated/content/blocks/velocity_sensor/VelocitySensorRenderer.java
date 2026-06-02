/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionfc
 */
package dev.simulated_team.simulated.content.blocks.velocity_sensor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlock;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionfc;

public class VelocitySensorRenderer
extends SafeBlockEntityRenderer<VelocitySensorBlockEntity> {
    public VelocitySensorRenderer(BlockEntityRendererProvider.Context context) {
    }

    protected void renderSafe(VelocitySensorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        BlockState state = be.getBlockState();
        SuperByteBuffer diode = CachedBuffers.partial((PartialModel)SimPartialModels.VELOCITY_SENSOR_DIODE, (BlockState)state);
        SuperByteBuffer fan = CachedBuffers.partial((PartialModel)SimPartialModels.VELOCITY_SENSOR_FAN, (BlockState)state);
        boolean front = (Integer)state.getValue((Property)VelocitySensorBlock.POWERED) == 1;
        boolean axis = (Boolean)state.getValue((Property)VelocitySensorBlock.AXIS_ALONG_FIRST_COORDINATE);
        front = switch ((Direction)state.getValue((Property)VelocitySensorBlock.FACING)) {
            default -> throw new MatchException(null, null);
            case Direction.NORTH, Direction.EAST -> {
                if (!front) {
                    yield true;
                }
                yield false;
            }
            case Direction.SOUTH, Direction.DOWN -> {
                if (axis == front) {
                    yield true;
                }
                yield false;
            }
            case Direction.WEST -> {
                if (axis != front) {
                    yield true;
                }
                yield false;
            }
            case Direction.UP -> front;
        };
        float signalStrength = (float)be.getRedstoneStrength() / 15.0f;
        int color = SimColors.redstone(signalStrength);
        this.transform(diode, state);
        diode.light(light).color(front ? color : SimColors.REDSTONE_OFF).renderInto(ms, vb);
        this.transform(diode, state);
        diode.rotateCenteredDegrees(180.0f, Direction.Axis.Y);
        diode.light(light).color(front ? SimColors.REDSTONE_OFF : color).renderInto(ms, vb);
        this.transform((SuperByteBuffer)fan.rotateCentered(be.getFanAngle(partialTicks), AbstractDirectionalAxisBlock.getDirectionOfAxis(state)), state);
        fan.light(light).renderInto(ms, vb);
    }

    private void transform(SuperByteBuffer diode, BlockState state) {
        Direction dir = (Direction)state.getValue((Property)VelocitySensorBlock.FACING);
        boolean axis = (Boolean)state.getValue((Property)VelocitySensorBlock.AXIS_ALONG_FIRST_COORDINATE);
        if (axis == (dir.getStepX() == 0)) {
            diode.rotateCenteredDegrees(90.0f, (Direction)state.getValue((Property)VelocitySensorBlock.FACING));
        }
        diode.rotateCentered((Quaternionfc)dir.getRotation());
    }
}
