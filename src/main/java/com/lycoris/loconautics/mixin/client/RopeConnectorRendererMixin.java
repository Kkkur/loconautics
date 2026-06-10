package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.content.steelcable.SteelCableStrandRenderer;
import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlock;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorRenderer;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts {@link RopeConnectorRenderer#renderSafe} and redirects to
 * {@link SteelCableStrandRenderer} when the strand was created by the Steel Cable item.
 *
 * Also renders the connector knot on the owning connector, mirroring the knot block
 * in Simulated's renderSafe that we bypass. Uses SimPartialModels.ROPE_CONNECTOR_KNOT
 * (the connector block's own knot geometry) — NOT STEEL_CABLE_KNOT which is the
 * inline strand knot and has completely different geometry/positioning.
 */
@Mixin(value = RopeConnectorRenderer.class, remap = false)
public class RopeConnectorRendererMixin {

    @Inject(
            method = "renderSafe",
            at = @At("HEAD"),
            cancellable = true
    )
    private void loconautics$redirectSteelCableRender(
            RopeConnectorBlockEntity be,
            float partialTicks,
            PoseStack ms,
            MultiBufferSource buffer,
            int light,
            int overlay,
            CallbackInfo ci
    ) {
        RopeStrandHolderBehavior holder = be.getRopeHolder();

        // Only intercept on the owning connector — non-owners have no strand to identify
        if (!holder.ownsRope()) return;

        ClientRopeStrand clientStrand = holder.getClientStrand();
        if (clientStrand == null) return;

        if (!SteelCableTracker.isSteelCable(clientStrand.getUuid())) return;

        // Render the strand with our textures
        SteelCableStrandRenderer.render(be, holder, partialTicks, ms, buffer);

        // Render the connector knot — same model as Simulated uses for the knob
        // on the block itself. STEEL_CABLE_KNOT is the inline strand knot and has
        // different geometry; the connector knob uses ROPE_CONNECTOR_KNOT.
        if (holder.isAttached() || be.isVirtual() && holder.renderAttached) {
            SuperByteBuffer knot = CachedBuffers.partialFacing(
                    (PartialModel) SimPartialModels.ROPE_CONNECTOR_KNOT,
                    (BlockState) AllBlocks.ROPE.getDefaultState(),
                    Direction.NORTH);

            BlockPos blockPos = be.getBlockPos();
            BlockState state = be.getBlockState();
            Vec3 attachmentPoint = be.getVisualAttachmentPoint(blockPos, state);
            Direction facing = state.getValue(RopeConnectorBlock.FACING);

            boolean axisAlongFirstCoordinate = state.getValue(RopeConnectorBlock.AXIS_ALONG_FIRST_COORDINATE);
            float zRotLast = axisAlongFirstCoordinate ^ facing.getAxis() == Direction.Axis.Z ? 90.0f : 0.0f;
            float yRot = AngleHelper.horizontalAngle(facing)
                    + (axisAlongFirstCoordinate || facing.getAxis() != Direction.Axis.Y ? 0.0f : 90.0f);
            float zRot = facing == Direction.UP ? 270.0f : (facing == Direction.DOWN ? 90.0f : 0.0f);

            SuperByteBuffer knotBuffer = knot.light(light);
            knotBuffer.translate(attachmentPoint.subtract(blockPos.getCenter()));
            knotBuffer.rotateCentered((float) ((double) (zRot / 180.0f) * Math.PI), Direction.SOUTH);
            knotBuffer.rotateCentered((float) ((double) (yRot / 180.0f) * Math.PI), Direction.UP);
            knotBuffer.rotateCentered((float) ((double) (zRotLast / 180.0f) * Math.PI), Direction.SOUTH);
            knotBuffer.rotateCentered(1.5707964f, Direction.UP);
            knotBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }

        ci.cancel();
    }
}