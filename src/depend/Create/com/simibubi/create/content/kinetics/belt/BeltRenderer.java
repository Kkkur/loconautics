/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.belt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.ShadowRenderHelper;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.Random;
import java.util.function.Supplier;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BeltRenderer
extends SafeBlockEntityRenderer<BeltBlockEntity> {
    public BeltRenderer(BlockEntityRendererProvider.Context context) {
    }

    public boolean shouldRenderOffScreen(BeltBlockEntity be) {
        return be.isController();
    }

    @Override
    protected void renderSafe(BeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            BlockState blockState = be.getBlockState();
            if (!AllBlocks.BELT.has(blockState)) {
                return;
            }
            BeltSlope beltSlope = (BeltSlope)((Object)blockState.getValue(BeltBlock.SLOPE));
            BeltPart part = (BeltPart)((Object)blockState.getValue(BeltBlock.PART));
            Direction facing = (Direction)blockState.getValue(BeltBlock.HORIZONTAL_FACING);
            Direction.AxisDirection axisDirection = facing.getAxisDirection();
            boolean downward = beltSlope == BeltSlope.DOWNWARD;
            boolean upward = beltSlope == BeltSlope.UPWARD;
            boolean diagonal = downward || upward;
            boolean start = part == BeltPart.START;
            boolean end = part == BeltPart.END;
            boolean sideways = beltSlope == BeltSlope.SIDEWAYS;
            boolean alongX = facing.getAxis() == Direction.Axis.X;
            PoseStack localTransforms = new PoseStack();
            PoseTransformStack msr = TransformStack.of((PoseStack)localTransforms);
            VertexConsumer vb = buffer.getBuffer(RenderType.solid());
            float renderTick = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
            ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)msr.center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing) + (float)(upward ? 180 : 0) + (float)(sideways ? 270 : 0))).rotateZDegrees(sideways ? 90.0f : 0.0f)).rotateXDegrees(!diagonal && beltSlope != BeltSlope.HORIZONTAL ? 90.0f : 0.0f)).uncenter();
            if (downward || beltSlope == BeltSlope.VERTICAL && axisDirection == Direction.AxisDirection.POSITIVE) {
                boolean b = start;
                start = end;
                end = b;
            }
            DyeColor color = be.color.orElse(null);
            for (boolean bottom : Iterate.trueAndFalse) {
                PartialModel beltPartial = BeltRenderer.getBeltPartial(diagonal, start, end, bottom);
                SuperByteBuffer beltBuffer = CachedBuffers.partial((PartialModel)beltPartial, (BlockState)blockState).light(light);
                SpriteShiftEntry spriteShift = BeltRenderer.getSpriteShiftEntry(color, diagonal, bottom);
                float speed = be.getSpeed();
                if (speed != 0.0f || be.color.isPresent()) {
                    float time = renderTick * (float)axisDirection.getStep();
                    if (diagonal && downward ^ alongX || !sideways && !diagonal && alongX || sideways && axisDirection == Direction.AxisDirection.NEGATIVE) {
                        speed = -speed;
                    }
                    float scrollMult = diagonal ? 0.375f : 0.5f;
                    float spriteSize = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
                    double scroll = (double)(speed * time) / 504.0 + (bottom ? 0.5 : 0.0);
                    scroll -= Math.floor(scroll);
                    scroll = scroll * (double)spriteSize * (double)scrollMult;
                    beltBuffer.shiftUVScrolling(spriteShift, (float)scroll);
                }
                ((SuperByteBuffer)beltBuffer.transform(localTransforms)).renderInto(ms, vb);
                if (diagonal) break;
            }
            if (be.hasPulley()) {
                Direction dir = sideways ? Direction.UP : ((Direction)blockState.getValue(BeltBlock.HORIZONTAL_FACING)).getClockWise();
                Supplier<PoseStack> matrixStackSupplier = () -> {
                    PoseStack stack = new PoseStack();
                    PoseTransformStack stacker = TransformStack.of((PoseStack)stack);
                    stacker.center();
                    if (dir.getAxis() == Direction.Axis.X) {
                        stacker.rotateYDegrees(90.0f);
                    }
                    if (dir.getAxis() == Direction.Axis.Y) {
                        stacker.rotateXDegrees(90.0f);
                    }
                    stacker.rotateXDegrees(90.0f);
                    stacker.uncenter();
                    return stack;
                };
                SuperByteBuffer superBuffer = CachedBuffers.partialDirectional((PartialModel)AllPartialModels.BELT_PULLEY, (BlockState)blockState, (Direction)dir, matrixStackSupplier);
                KineticBlockEntityRenderer.standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb);
            }
        }
        this.renderItems(be, partialTicks, ms, buffer, light, overlay);
    }

    public static SpriteShiftEntry getSpriteShiftEntry(DyeColor color, boolean diagonal, boolean bottom) {
        if (color != null) {
            return (diagonal ? AllSpriteShifts.DYED_DIAGONAL_BELTS : (bottom ? AllSpriteShifts.DYED_OFFSET_BELTS : AllSpriteShifts.DYED_BELTS)).get(color);
        }
        return diagonal ? AllSpriteShifts.BELT_DIAGONAL : (bottom ? AllSpriteShifts.BELT_OFFSET : AllSpriteShifts.BELT);
    }

    public static PartialModel getBeltPartial(boolean diagonal, boolean start, boolean end, boolean bottom) {
        if (diagonal) {
            if (start) {
                return AllPartialModels.BELT_DIAGONAL_START;
            }
            if (end) {
                return AllPartialModels.BELT_DIAGONAL_END;
            }
            return AllPartialModels.BELT_DIAGONAL_MIDDLE;
        }
        if (bottom) {
            if (start) {
                return AllPartialModels.BELT_START_BOTTOM;
            }
            if (end) {
                return AllPartialModels.BELT_END_BOTTOM;
            }
            return AllPartialModels.BELT_MIDDLE_BOTTOM;
        }
        if (start) {
            return AllPartialModels.BELT_START;
        }
        if (end) {
            return AllPartialModels.BELT_END;
        }
        return AllPartialModels.BELT_MIDDLE;
    }

    protected void renderItems(BeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!be.isController()) {
            return;
        }
        if (be.beltLength == 0) {
            return;
        }
        ms.pushPose();
        Direction beltFacing = be.getBeltFacing();
        Vec3i directionVec = beltFacing.getNormal();
        Vec3 beltStartOffset = Vec3.atLowerCornerOf((Vec3i)directionVec).scale(-0.5).add(0.5, 0.9375, 0.5);
        ms.translate(beltStartOffset.x, beltStartOffset.y, beltStartOffset.z);
        BeltSlope slope = (BeltSlope)((Object)be.getBlockState().getValue(BeltBlock.SLOPE));
        int verticality = slope == BeltSlope.DOWNWARD ? -1 : (slope == BeltSlope.UPWARD ? 1 : 0);
        boolean slopeAlongX = beltFacing.getAxis() == Direction.Axis.X;
        boolean onContraption = be.getLevel() instanceof WrappedLevel;
        BeltInventory inventory = be.getInventory();
        for (TransportedItemStack transported : inventory.getTransportedItems()) {
            this.renderItem(be, partialTicks, ms, buffer, light, overlay, beltFacing, directionVec, slope, verticality, slopeAlongX, onContraption, transported, beltStartOffset);
        }
        if (inventory.getLazyClientItem() != null) {
            this.renderItem(be, partialTicks, ms, buffer, light, overlay, beltFacing, directionVec, slope, verticality, slopeAlongX, onContraption, inventory.getLazyClientItem(), beltStartOffset);
        }
        ms.popPose();
    }

    private void renderItem(BeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, Direction beltFacing, Vec3i directionVec, BeltSlope slope, int verticality, boolean slopeAlongX, boolean onContraption, TransportedItemStack transported, Vec3 beltStartOffset) {
        int stackLight;
        boolean alongX;
        boolean tiltForward;
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        float offset = Mth.lerp((float)partialTicks, (float)transported.prevBeltPosition, (float)transported.beltPosition);
        float sideOffset = Mth.lerp((float)partialTicks, (float)transported.prevSideOffset, (float)transported.sideOffset);
        float verticalMovement = verticality;
        if (be.getSpeed() == 0.0f) {
            offset = transported.beltPosition;
            sideOffset = transported.sideOffset;
        }
        verticalMovement = (double)offset < 0.5 ? 0.0f : (float)verticality * (Math.min(offset, (float)be.beltLength - 0.5f) - 0.5f);
        Vec3 offsetVec = Vec3.atLowerCornerOf((Vec3i)directionVec).scale((double)offset);
        if (verticalMovement != 0.0f) {
            offsetVec = offsetVec.add(0.0, (double)verticalMovement, 0.0);
        }
        boolean onSlope = slope != BeltSlope.HORIZONTAL && Mth.clamp((float)offset, (float)0.5f, (float)((float)be.beltLength - 0.5f)) == offset;
        boolean bl = tiltForward = (slope == BeltSlope.DOWNWARD ^ beltFacing.getAxisDirection() == Direction.AxisDirection.POSITIVE) == (beltFacing.getAxis() == Direction.Axis.Z);
        float slopeAngle = onSlope ? (tiltForward ? -45.0f : 45.0f) : 0.0f;
        Vec3 itemPos = beltStartOffset.add((double)be.getBlockPos().getX(), (double)be.getBlockPos().getY(), (double)be.getBlockPos().getZ()).add(offsetVec);
        if (this.shouldCullItem(itemPos, be.getLevel())) {
            return;
        }
        ms.pushPose();
        TransformStack.of((PoseStack)ms).nudge(transported.angle);
        ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);
        boolean bl2 = alongX = beltFacing.getClockWise().getAxis() == Direction.Axis.X;
        if (!alongX) {
            sideOffset *= -1.0f;
        }
        ms.translate(alongX ? sideOffset : 0.0f, 0.0f, alongX ? 0.0f : sideOffset);
        if (onContraption) {
            stackLight = light;
        } else {
            int segment = (int)Math.floor(offset);
            mutablePos.set((Vec3i)be.getBlockPos()).move(directionVec.getX() * segment, verticality * segment, directionVec.getZ() * segment);
            stackLight = LevelRenderer.getLightColor((BlockAndTintGetter)be.getLevel(), (BlockPos)mutablePos);
        }
        boolean renderUpright = BeltHelper.isItemUpright(transported.stack);
        BakedModel bakedModel = itemRenderer.getModel(transported.stack, be.getLevel(), null, 0);
        boolean blockItem = bakedModel.isGui3d();
        int count = 0;
        if (be.getLevel() instanceof PonderLevel || mc.player.getEyePosition(1.0f).distanceTo(itemPos) < 16.0) {
            count = Mth.log2((int)transported.stack.getCount()) / 2;
        }
        Random r = new Random(transported.angle);
        boolean slopeShadowOnly = renderUpright && onSlope;
        float slopeOffset = 0.125f;
        if (slopeShadowOnly) {
            ms.pushPose();
        }
        if (!renderUpright || slopeShadowOnly) {
            ms.mulPose((slopeAlongX ? Axis.ZP : Axis.XP).rotationDegrees(slopeAngle));
        }
        if (onSlope) {
            ms.translate(0.0f, slopeOffset, 0.0f);
        }
        ms.pushPose();
        ms.translate(0.0f, -0.12f, 0.0f);
        ShadowRenderHelper.renderShadow(ms, buffer, 0.75f, 0.2f);
        ms.popPose();
        if (slopeShadowOnly) {
            ms.popPose();
            ms.translate(0.0f, slopeOffset, 0.0f);
        }
        if (renderUpright) {
            Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 vectorForOffset = BeltHelper.getVectorForOffset(be, offset);
            Vec3 diff = vectorForOffset.subtract(cameraPosition);
            float yRot = (float)(Mth.atan2((double)diff.x, (double)diff.z) + Math.PI);
            ms.mulPose(Axis.YP.rotation(yRot));
            ms.translate(0.0, 0.09375, 0.0625);
        }
        for (int i = 0; i <= count; ++i) {
            ms.pushPose();
            boolean box = PackageItem.isPackage(transported.stack);
            ms.mulPose(Axis.YP.rotationDegrees((float)transported.angle));
            if (!blockItem && !renderUpright) {
                ms.translate(0.0, -0.09375, 0.0);
                ms.mulPose(Axis.XP.rotationDegrees(90.0f));
            }
            if (blockItem && !box) {
                ms.translate(r.nextFloat() * 0.0625f * (float)i, 0.0f, r.nextFloat() * 0.0625f * (float)i);
            }
            if (box) {
                ms.translate(0.0f, 0.25f, 0.0f);
                ms.scale(1.5f, 1.5f, 1.5f);
            } else {
                ms.scale(0.5f, 0.5f, 0.5f);
            }
            itemRenderer.render(transported.stack, ItemDisplayContext.FIXED, false, ms, buffer, stackLight, overlay, bakedModel);
            ms.popPose();
            if (!renderUpright) {
                if (!blockItem) {
                    ms.mulPose(Axis.YP.rotationDegrees(10.0f));
                }
                ms.translate(0.0, blockItem ? 0.015625 : 0.0625, 0.0);
                continue;
            }
            ms.translate(0.0f, 0.0f, -0.0625f);
        }
        ms.popPose();
    }
}
