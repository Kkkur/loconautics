package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.lycoris.loconautics.content.steelcable.SteelCableStrandRenderer;
import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.lycoris.loconautics.mixin.RopeStrandHolderBehaviorAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlock;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorRenderer;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts {@link RopeConnectorRenderer#renderSafe} for both the owning and
 * non-owning connector when the strand is a Steel Cable.
 *
 * - Owning connector:     renders the full strand + steel knot, cancels original.
 * - Non-owning connector: renders only the steel knot (strand already drawn by owner),
 *                         cancels original so the rope knot is never drawn.
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

        // Use attachedRopeID for steel cable detection — it is set on BOTH the owning
        // and non-owning connector.  getClientStrand() returns null on the non-owning
        // end, so relying on it caused an early return before ci.cancel(), which let
        // the original renderer run and draw the brown rope-knot texture on that end.
        UUID attachedID = ((RopeStrandHolderBehaviorAccessor) holder).loconautics$getAttachedRopeID();
        if (attachedID == null) return;
        if (!SteelCableTracker.isSteelCable(attachedID)) return;

        // Owning connector: render the full strand with our textures.
        // Non-owning connector: strand is already rendered by the owner, skip it.
        if (holder.ownsRope()) {
            ClientRopeStrand clientStrand = holder.getClientStrand();
            if (clientStrand != null) {
                SteelCableStrandRenderer.render(be, holder, partialTicks, ms, buffer);
            }
        }

        // Both connectors: render steel knot instead of the default rope knot.
        if (holder.isAttached() || be.isVirtual() && holder.renderAttached) {
            SuperByteBuffer knot = CachedBuffers.partialFacing(
                    LoconauticsPartialModels.STEEL_CABLE_CONNECTOR_KNOT,
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