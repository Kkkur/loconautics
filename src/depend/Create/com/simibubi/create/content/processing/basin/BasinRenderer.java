/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.NeoForgeCatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.processing.basin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BasinRenderer
extends SmartBlockEntityRenderer<BasinBlockEntity> {
    public BasinRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BasinBlockEntity basin, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(basin, partialTicks, ms, buffer, light, overlay);
        float fluidLevel = this.renderFluids(basin, partialTicks, ms, buffer, light, overlay);
        float level = Mth.clamp((float)(fluidLevel - 0.3f), (float)0.125f, (float)0.6f);
        ms.pushPose();
        BlockPos pos = basin.getBlockPos();
        ms.translate(0.5, (double)0.2f, 0.5);
        TransformStack.of((PoseStack)ms).rotateYDegrees(basin.ingredientRotation.getValue(partialTicks));
        RandomSource r = RandomSource.create((long)pos.hashCode());
        Vec3 baseVector = new Vec3(0.125, (double)level, 0.0);
        IItemHandlerModifiable inv = basin.itemCapability;
        if (inv == null) {
            inv = new ItemStackHandler();
        }
        int itemCount = 0;
        for (int slot = 0; slot < inv.getSlots(); ++slot) {
            if (inv.getStackInSlot(slot).isEmpty()) continue;
            ++itemCount;
        }
        if (itemCount == 1) {
            baseVector = new Vec3(0.0, (double)level, 0.0);
        }
        float anglePartition = 360.0f / (float)itemCount;
        for (int slot = 0; slot < inv.getSlots(); ++slot) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            ms.pushPose();
            if (fluidLevel > 0.0f) {
                ms.translate(0.0f, (Mth.sin((float)(AnimationTickHolder.getRenderTime((LevelAccessor)basin.getLevel()) / 12.0f + anglePartition * (float)itemCount)) + 1.5f) * 1.0f / 32.0f, 0.0f);
            }
            Vec3 itemPosition = VecHelper.rotate((Vec3)baseVector, (double)(anglePartition * (float)itemCount), (Direction.Axis)Direction.Axis.Y);
            ms.translate(itemPosition.x, itemPosition.y, itemPosition.z);
            ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(anglePartition * (float)itemCount + 35.0f)).rotateXDegrees(65.0f);
            for (int i = 0; i <= stack.getCount() / 8; ++i) {
                ms.pushPose();
                Vec3 vec = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)0.0625f);
                ms.translate(vec.x, vec.y, vec.z);
                this.renderItem(ms, buffer, light, overlay, stack);
                ms.popPose();
            }
            ms.popPose();
            --itemCount;
        }
        ms.popPose();
        BlockState blockState = basin.getBlockState();
        if (!(blockState.getBlock() instanceof BasinBlock)) {
            return;
        }
        Direction direction = (Direction)blockState.getValue((Property)BasinBlock.FACING);
        if (direction == Direction.DOWN) {
            return;
        }
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)direction.getNormal());
        Vec3 outVec = VecHelper.getCenterOf((Vec3i)BlockPos.ZERO).add(directionVec.scale(0.55).subtract(0.0, 0.5, 0.0));
        boolean outToBasin = basin.getLevel().getBlockState(basin.getBlockPos().relative(direction)).getBlock() instanceof BasinBlock;
        for (IntAttached<ItemStack> intAttached : basin.visualizedOutputItems) {
            float progress = 1.0f - ((float)((Integer)intAttached.getFirst()).intValue() - partialTicks) / 10.0f;
            if (!outToBasin && progress > 0.35f) continue;
            ms.pushPose();
            ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(outVec)).translate(new Vec3(0.0, (double)Math.max(-0.55f, -(progress * progress * 2.0f)), 0.0))).translate(directionVec.scale((double)(progress * 0.5f)))).rotateYDegrees(AngleHelper.horizontalAngle((Direction)direction))).rotateXDegrees(progress * 180.0f);
            this.renderItem(ms, buffer, light, overlay, (ItemStack)intAttached.getValue());
            ms.popPose();
        }
    }

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, (Level)mc.level, 0);
    }

    protected float renderFluids(BasinBlockEntity basin, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        SmartFluidTankBehaviour inputFluids = basin.getBehaviour(SmartFluidTankBehaviour.INPUT);
        SmartFluidTankBehaviour outputFluids = basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT);
        SmartFluidTankBehaviour[] tanks = new SmartFluidTankBehaviour[]{inputFluids, outputFluids};
        float totalUnits = basin.getTotalFluidUnits(partialTicks);
        if (totalUnits < 1.0f) {
            return 0.0f;
        }
        float fluidLevel = Mth.clamp((float)(totalUnits / 2000.0f), (float)0.0f, (float)1.0f);
        fluidLevel = 1.0f - (1.0f - fluidLevel) * (1.0f - fluidLevel);
        float xMin = 0.125f;
        float xMax = 0.125f;
        float yMin = 0.125f;
        float yMax = 0.125f + 0.75f * fluidLevel;
        float zMin = 0.125f;
        float zMax = 0.875f;
        for (SmartFluidTankBehaviour behaviour : tanks) {
            if (behaviour == null) continue;
            for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
                float units;
                FluidStack renderedFluid = tankSegment.getRenderedFluid();
                if (renderedFluid.isEmpty() || (units = tankSegment.getTotalUnits(partialTicks)) < 1.0f) continue;
                float partial = Mth.clamp((float)(units / totalUnits), (float)0.0f, (float)1.0f);
                NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)renderedFluid, xMin, 0.125f, 0.125f, xMax += partial * 12.0f / 16.0f, yMax, 0.875f, buffer, ms, light, false, false);
                xMin = xMax;
            }
        }
        return yMax;
    }

    public int getViewDistance() {
        return 16;
    }
}
