/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public static class AllArmInteractionPointTypes.FunnelPoint
extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public AllArmInteractionPointTypes.FunnelPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        Direction funnelFacing = FunnelBlock.getFunnelFacing(this.cachedState);
        Vec3i normal = funnelFacing != null ? funnelFacing.getNormal() : Vec3i.ZERO;
        return VecHelper.getCenterOf((Vec3i)this.pos).add(Vec3.atLowerCornerOf((Vec3i)normal).scale((double)-0.15f));
    }

    @Override
    protected Direction getInteractionDirection() {
        Direction funnelFacing = FunnelBlock.getFunnelFacing(this.cachedState);
        return funnelFacing != null ? funnelFacing.getOpposite() : Direction.UP;
    }

    @Override
    public void updateCachedState() {
        BlockState oldState = this.cachedState;
        super.updateCachedState();
        if (oldState != this.cachedState) {
            this.cachedAngles = null;
        }
    }

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        BlockEntity blockEntity;
        FilteringBehaviour filtering = BlockEntityBehaviour.get((BlockGetter)this.level, this.pos, FilteringBehaviour.TYPE);
        InvManipulationBehaviour inserter = BlockEntityBehaviour.get((BlockGetter)this.level, this.pos, InvManipulationBehaviour.TYPE);
        if (this.cachedState.getOptionalValue((Property)BlockStateProperties.POWERED).orElse(false).booleanValue()) {
            return stack;
        }
        if (inserter == null) {
            return stack;
        }
        if (filtering != null && !filtering.test(stack)) {
            return stack;
        }
        if (simulate) {
            inserter.simulate();
        }
        ItemStack insert = inserter.insert(stack);
        if (!simulate && insert.getCount() != stack.getCount() && (blockEntity = this.level.getBlockEntity(this.pos)) instanceof FunnelBlockEntity) {
            FunnelBlockEntity funnelBlockEntity = (FunnelBlockEntity)blockEntity;
            funnelBlockEntity.onTransfer(stack);
            if (funnelBlockEntity.hasFlap()) {
                funnelBlockEntity.flap(true);
            }
        }
        return insert;
    }
}
