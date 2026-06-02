/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public static class AllArmInteractionPointTypes.BeltType
extends ArmInteractionPointType {
    @Override
    public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
        return AllBlocks.BELT.has(state) && !(level.getBlockState(pos.above()).getBlock() instanceof BeltTunnelBlock) && BeltBlock.canTransportObjects(state);
    }

    @Override
    public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
        return new AllArmInteractionPointTypes.BeltPoint(this, level, pos, state);
    }
}
