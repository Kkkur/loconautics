package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders the Create cover and two lever partial models on top of the static
 * Analog Controller block.
 *
 * Left lever  (first)  → current power (0–15): firstLever  * 70 - 25  clamped [-45, 45]
 * Right lever (second) → throttle cap  (0–15): secondLever * 15        clamped [-45, 45]
 *
 * All transforms use pure SuperByteBuffer methods (rotateCentered / rotate / translate).
 * The ms.pushPose + TransformStack + transform(ms) chain from ControlsRenderer is
 * contraption-specific and does not work for world-placed block entity renderers.
 *
 * Lever pivot chain derived from ControlsRenderer (after expanding center/uncenter):
 *   rotateCentered(hAngle, UP)           — face the right direction
 *   translate(0, 0.25, 0.25)            — move to lever hinge in rotated block space
 *   rotate(vAngle - 45°, EAST)          — tilt part 1 (around current origin)
 *   translate(0, yOffset, 0)            — equip slide (up when mounted, down when not)
 *   rotate(45°, EAST)                   — tilt part 2  (net rotation = vAngle)
 *   translate(0, -0.375, -0.1875)      — net position correction (uncenter + offset)
 *   translate(xOffset, 0, 0)            — left lever (0) vs right lever (0.375)
 */

 // TODO: fix the lever position, only graphical, not logical.

public class AnalogControllerRenderer implements BlockEntityRenderer<AnalogControllerBlockEntity> {

    // Per-renderer-instance animated values (one renderer per BE in the view frustum)
    private final LerpedFloat speedLever = LerpedFloat.linear();
    private final LerpedFloat steerLever = LerpedFloat.linear();
    private final LerpedFloat equipAnim  = LerpedFloat.linear();

    public AnalogControllerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(AnalogControllerBlockEntity be, float partialTick,
                       PoseStack ms, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(AnalogControllerBlock.FACING);
        // Controls face opposite to the FACING property — same +180 offset as Create.
        float hAngleRad = (float) Math.toRadians(180.0f + AngleHelper.horizontalAngle(facing));
        int light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());

        // ---- Chase animated targets each render frame ----
        speedLever.chase(be.getCurrentPower() / 15.0, 0.2, LerpedFloat.Chaser.EXP);
        steerLever.chase(be.getMaxPower()     / 15.0, 0.2, LerpedFloat.Chaser.EXP);
        equipAnim .chase(be.hasUser() ? 1.0 : 0.0,   0.2, LerpedFloat.Chaser.EXP);

        speedLever.tickChaser();
        steerLever.tickChaser();
        equipAnim .tickChaser();

        float animSpeed = speedLever.getValue(partialTick);
        float animSteer = steerLever.getValue(partialTick);
        float animEquip = equipAnim .getValue(partialTick);

        // yOffset: -0.15 when unmounted (holstered), +0.05 when mounted (equipped)
        float yOffset = Mth.lerp(animEquip * animEquip, -0.15f, 0.05f);

        // ---- Cover (only needs facing rotation) ----
        CachedBuffers.partial(LoconauticsPartialModels.CONTROLS_COVER, state)
                .rotateCentered(hAngleRad, Direction.UP)
                .light(light)
                .renderInto(ms, buffers.getBuffer(RenderType.cutoutMipped()));

        // ---- Two levers ----
        for (boolean first : new boolean[]{true, false}) {
            float leverVal = first ? animSpeed : animSteer;
            float vAngle   = Mth.clamp(
                    first ? leverVal * 70.0f - 25.0f
                            : leverVal * 15.0f,
                    -45.0f, 45.0f);
            float xOff = first ? 0.0f : 0.375f;

            CachedBuffers.partial(LoconauticsPartialModels.CONTROLS_LEVER, state)
                    .rotateCentered(hAngleRad, Direction.UP)
                    .translate(0.0f, 0.25f, 0.25f)
                    .rotate((float) Math.toRadians(vAngle - 45.0f), Direction.EAST)
                    .translate(0.0f, yOffset, 0.0f)
                    .rotate((float) Math.toRadians(45.0f), Direction.EAST)
                    .translate(0.0f, -0.375f, -0.1875f)
                    .translate(xOff, 0.0f, 0.0f)
                    .light(light)
                    .renderInto(ms, buffers.getBuffer(RenderType.solid()));
        }
    }

    @Override
    public boolean shouldRenderOffScreen(AnalogControllerBlockEntity be) {
        return false;
    }
}