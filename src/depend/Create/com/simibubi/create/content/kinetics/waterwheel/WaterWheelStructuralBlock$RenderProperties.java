/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelStructuralBlock;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;

public static class WaterWheelStructuralBlock.RenderProperties
implements IClientBlockExtensions,
MultiPosDestructionHandler {
    public boolean addDestroyEffects(BlockState state, Level Level2, BlockPos pos, ParticleEngine manager) {
        return true;
    }

    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        if (target instanceof BlockHitResult) {
            BlockHitResult bhr = (BlockHitResult)target;
            BlockPos targetPos = bhr.getBlockPos();
            WaterWheelStructuralBlock waterWheelStructuralBlock = (WaterWheelStructuralBlock)AllBlocks.WATER_WHEEL_STRUCTURAL.get();
            if (waterWheelStructuralBlock.stillValid((BlockGetter)level, targetPos, state, false)) {
                manager.crack(WaterWheelStructuralBlock.getMaster((BlockGetter)level, targetPos, state), bhr.getDirection());
            }
            return true;
        }
        return super.addHitEffects(state, level, target, manager);
    }

    @Override
    @Nullable
    public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
        WaterWheelStructuralBlock waterWheelStructuralBlock = (WaterWheelStructuralBlock)AllBlocks.WATER_WHEEL_STRUCTURAL.get();
        if (!waterWheelStructuralBlock.stillValid((BlockGetter)level, pos, blockState, false)) {
            return null;
        }
        HashSet<BlockPos> set = new HashSet<BlockPos>();
        set.add(WaterWheelStructuralBlock.getMaster((BlockGetter)level, pos, blockState));
        return set;
    }
}
