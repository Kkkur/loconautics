package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the Create cover and lever partial models on top of the static
 * Analog Controller block, matching the visual behaviour of Create's own
 * train controls (ControlsRenderer) but for a world-placed block entity.
 *
 * The lever angle is driven by the block entity's current power (0–15):
 *   - power 0  → lever fully back  (-45°)
 *   - power 15 → lever fully forward (+45°)
 */
public class AnalogControllerRenderer implements BlockEntityRenderer<AnalogControllerBlockEntity> {

    public AnalogControllerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(AnalogControllerBlockEntity be, float partialTick,
                       PoseStack ms, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(AnalogControllerBlock.FACING);
        float hAngle = 180.0f + AngleHelper.horizontalAngle(facing);
        int light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());

        // --- Cover ---
        SuperByteBuffer cover = CachedBuffers.partial(LoconauticsPartialModels.CONTROLS_COVER, state);
        cover.transform(ms)
                .center()
                .rotateYDegrees(hAngle)
                .uncenter()
                .light(light)
                .useLevelLight(be.getLevel())
                .renderInto(ms, buffers.getBuffer(RenderType.cutoutMipped()));

        // --- Lever ---
        // Map power 0-15 linearly to the same range Create uses for firstLever (0.0-1.0).
        float leverT = be.getCurrentPower() / 15.0f;
        // Replicate Create's formula: firstLever * 70 - 25, clamped to [-45, 45]
        float vAngle = Mth.clamp(leverT * 70.0f - 25.0f, -45.0f, 45.0f);

        SuperByteBuffer lever = CachedBuffers.partial(LoconauticsPartialModels.CONTROLS_LEVER, state);
        ms.pushPose();
        ((PoseTransformStack) TransformStack.of(ms)
                .center()
                .rotateYDegrees(hAngle)
                .translate(0.0f, 0.25f, 0.25f)
                .rotateXDegrees(vAngle - 45.0f)
                .rotateXDegrees(45.0f)
                .uncenter())
                .translate(0.0f, -0.375f, -0.1875f);
        lever.transform(ms)
                .light(light)
                .useLevelLight(be.getLevel())
                .renderInto(ms, buffers.getBuffer(RenderType.solid()));
        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(AnalogControllerBlockEntity be) {
        return false;
    }
}