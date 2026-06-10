package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.lycoris.loconautics.client.LoconauticsSpriteShifts;
import com.lycoris.loconautics.content.steelcable.SteelCableStrandRenderer;
import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.rope.rope_winch.RopeWinchBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.rope_winch.RopeWinchRenderer;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.RopeStrandRenderer;import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.index.SimSpriteShifts;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.lycoris.loconautics.mixin.client.RopeWinchBlockEntityAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When the winch's strand is a steel cable, swap in our partial models and
 * sprite shift instead of Simulated's rope textures.
 *
 * We inject into renderComponents (the method that draws the shaft + coil)
 * and cancel it, then re-run the same logic with our assets.
 * The strand itself is already handled by RopeConnectorRendererMixin /
 * SteelCableStrandRenderer — here we only need to fix the winch body.
 */
@Mixin(value = RopeWinchRenderer.class, remap = false)
public class RopeWinchRendererMixin {

    @Inject(
            method = "renderComponents(Ldev/simulated_team/simulated/content/blocks/rope/rope_winch/RopeWinchBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void loconautics$steelCableWinchComponents(
            RopeWinchBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay,
            CallbackInfo ci
    ) {
        // Only intercept when this winch's strand is a steel cable
        var holder = be.getRopeHolder();
        var clientStrand = holder.getClientStrand();
        if (clientStrand == null || !SteelCableTracker.isSteelCable(clientStrand.getUuid())) return;

        ms.pushPose();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        BlockState state = be.getBlockState();

        // Shaft — our model, identical geometry to Simulated's, uses Create's axis textures
        SuperByteBuffer shaft = CachedBuffers.partial(
                (PartialModel) LoconauticsPartialModels.STEEL_CABLE_WINCH_SHAFT, state);

        // Coil — our model + our winch_coil texture
        SuperByteBuffer ropeCoil = CachedBuffers.partial(
                (PartialModel) LoconauticsPartialModels.STEEL_CABLE_WINCH_COIL, state);

        Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf((KineticBlockEntity) be);
        float angle = KineticBlockEntityRenderer.getAngleForBe(
                (KineticBlockEntity) be, (BlockPos) be.getBlockPos(), axis);

        KineticBlockEntityRenderer.kineticRotationTransform(shaft, (KineticBlockEntity) be, axis, angle, light);
        loconautics$transform(shaft, state, true).renderInto(ms, vb);

        if (holder.isAttached() || be.isVirtual() && holder.renderAttached) {
            ropeCoil.light(light);
            Direction facing = state.getValue(DirectionalKineticBlock.FACING);
            float speed = facing == Direction.DOWN
                    ? (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1.0f : -1.0f)
                    : (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE
                    == state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)
                    ? 1.0f : -1.0f);

            // Use our sprite shift so the coil scrolls with steel cable textures
            AbstractPulleyRenderer.scrollCoil(ropeCoil,
                    (SpriteShiftEntry) LoconauticsSpriteShifts.STEEL_CABLE_WINCH_COIL,
                    ((RopeWinchBlockEntityAccessor) be).loconautics$getClientAngle().getValue(partialTicks), speed);

            loconautics$transform(ropeCoil, state, true).renderInto(ms, vb);
        }

        ms.popPose();

        // Strand — already handled by SteelCableStrandRenderer via RopeConnectorRendererMixin,
        // but the winch calls RopeStrandRenderer directly, so redirect it here too.
        SteelCableStrandRenderer.render(be, holder, partialTicks, ms, buffer);

        ci.cancel();
    }

    /** Copied from RopeWinchRenderer — applies facing/axis rotations to a buffer. */
    private static SuperByteBuffer loconautics$transform(SuperByteBuffer buffer, BlockState state, boolean axisDirectionMatters) {
        Direction facing = state.getValue(DirectionalKineticBlock.FACING);
        float zRotLast = axisDirectionMatters
                && state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)
                ^ facing.getAxis() == Direction.Axis.Z ? 90.0f : 0.0f;
        float yRot = AngleHelper.horizontalAngle(facing)
                + (state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)
                || facing.getAxis() != Direction.Axis.Y ? 0.0f : 90.0f);
        float zRot = facing == Direction.UP ? 270.0f : (facing == Direction.DOWN ? 90.0f : 0.0f);
        buffer.rotateCentered((float) ((double) (zRot / 180.0f) * Math.PI), Direction.SOUTH);
        buffer.rotateCentered((float) ((double) (yRot / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float) ((double) (zRotLast / 180.0f) * Math.PI), Direction.SOUTH);
        return buffer;
    }
}