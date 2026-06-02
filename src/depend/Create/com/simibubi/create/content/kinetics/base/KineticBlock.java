/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class KineticBlock
extends Block
implements IRotate {
    public KineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof KineticBlockEntity) {
            KineticBlockEntity kineticBlockEntity = (KineticBlockEntity)blockEntity;
            kineticBlockEntity.preventSpeedUpdate = 0;
            if (oldState.getBlock() != state.getBlock()) {
                return;
            }
            if (state.hasBlockEntity() != oldState.hasBlockEntity()) {
                return;
            }
            if (!this.areStatesKineticallyEquivalent(oldState, state)) {
                return;
            }
            kineticBlockEntity.preventSpeedUpdate = 2;
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return false;
    }

    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (oldState.getBlock() != newState.getBlock()) {
            return false;
        }
        return this.getRotationAxis(newState) == this.getRotationAxis(oldState);
    }

    public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
        if (worldIn.isClientSide()) {
            return;
        }
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (!(blockEntity instanceof KineticBlockEntity)) {
            return;
        }
        KineticBlockEntity kbe = (KineticBlockEntity)blockEntity;
        if (kbe.preventSpeedUpdate > 0) {
            return;
        }
        kbe.warnOfMovement();
        kbe.clearKineticInformation();
        kbe.updateSpeed = true;
    }

    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        AdvancementBehaviour.setPlacedBy(worldIn, pos, placer);
        if (worldIn.isClientSide) {
            return;
        }
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (!(blockEntity instanceof KineticBlockEntity)) {
            return;
        }
        KineticBlockEntity kbe = (KineticBlockEntity)blockEntity;
        kbe.effects.queueRotationIndicators();
    }

    public float getParticleTargetRadius() {
        return 0.65f;
    }

    public float getParticleInitialRadius() {
        return 0.75f;
    }
}
