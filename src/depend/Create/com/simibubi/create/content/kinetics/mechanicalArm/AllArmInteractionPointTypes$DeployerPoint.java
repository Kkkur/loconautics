/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public static class AllArmInteractionPointTypes.DeployerPoint
extends ArmInteractionPoint {
    public AllArmInteractionPointTypes.DeployerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Direction getInteractionDirection() {
        return this.cachedState.getOptionalValue((Property)DeployerBlock.FACING).orElse(Direction.UP).getOpposite();
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        return super.getInteractionPositionVector().add(Vec3.atLowerCornerOf((Vec3i)this.getInteractionDirection().getNormal()).scale((double)0.65f));
    }

    @Override
    public void updateCachedState() {
        BlockState oldState = this.cachedState;
        super.updateCachedState();
        if (oldState != this.cachedState) {
            this.cachedAngles = null;
        }
    }
}
