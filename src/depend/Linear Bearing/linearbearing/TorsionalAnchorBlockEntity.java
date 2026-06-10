/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.bearing.linearbearing;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity;
import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class TorsionalAnchorBlockEntity
extends GeneratingKineticBlockEntity {
    public static BlockEntityType<TorsionalAnchorBlockEntity> TYPE;
    protected float targetInjectedSpeed = 0.0f;
    protected boolean isDrivenBySpring = false;
    protected float currentStressMultiplier = 1.0f;
    private static Field partnerPosField;
    private static Field partnerSubLevelField;
    private static Field desiredLengthField;

    public TorsionalAnchorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        super.tick();
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        BlockState state = this.getBlockState();
        if (!state.hasProperty((Property)BlockStateProperties.FACING)) {
            return;
        }
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        BlockPos frontPos = this.worldPosition.relative(facing);
        BlockEntity frontBE = this.level.getBlockEntity(frontPos);
        float newInjectedSpeed = 0.0f;
        float calculatedMultiplier = 1.0f;
        if (frontBE instanceof SpringBlockEntity) {
            SpringBlockEntity spring = (SpringBlockEntity)frontBE;
            try {
                TorsionalAnchorBlockEntity targetAnchor;
                BlockPos partnerSpringPos = (BlockPos)partnerPosField.get(spring);
                UUID partnerSubLevel = (UUID)partnerSubLevelField.get(spring);
                double desiredLength = (Double)desiredLengthField.get(spring);
                if (partnerSpringPos != null && (targetAnchor = this.findAnchorAround(partnerSpringPos, partnerSubLevel)) != null) {
                    Vec3 posB;
                    Vec3 posA;
                    double actualDistance;
                    double stretch;
                    BlockState targetState;
                    if (this.getSpeed() != 0.0f && !this.isDrivenBySpring && (targetState = targetAnchor.getBlockState()).hasProperty((Property)BlockStateProperties.FACING)) {
                        Direction targetFacing = (Direction)targetState.getValue((Property)BlockStateProperties.FACING);
                        newInjectedSpeed = facing == targetFacing.getOpposite() ? -this.getSpeed() : this.getSpeed();
                    }
                    if ((stretch = Math.max(0.0, (actualDistance = Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(this.level, (Position)(posA = Vec3.atCenterOf((Vec3i)spring.getBlockPos())), (Position)(posB = Vec3.atCenterOf((Vec3i)partnerSpringPos))))) - desiredLength)) > 0.0) {
                        calculatedMultiplier = 1.0f + (float)(stretch * 0.5);
                    }
                }
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            this.updateInjectedSpeedOnTarget(newInjectedSpeed);
        } else if (this.isDrivenBySpring) {
            this.targetInjectedSpeed = 0.0f;
            this.isDrivenBySpring = false;
            this.updateGeneratedRotation();
        }
        if (this.currentStressMultiplier != calculatedMultiplier) {
            this.currentStressMultiplier = calculatedMultiplier;
            this.updateGeneratedRotation();
        }
    }

    private void updateInjectedSpeedOnTarget(float newSpeed) {
        BlockState state = this.getBlockState();
        if (!state.hasProperty((Property)BlockStateProperties.FACING)) {
            return;
        }
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        BlockPos frontPos = this.worldPosition.relative(facing);
        BlockEntity frontBE = this.level.getBlockEntity(frontPos);
        if (frontBE instanceof SpringBlockEntity) {
            SpringBlockEntity spring = (SpringBlockEntity)frontBE;
            try {
                TorsionalAnchorBlockEntity targetAnchor;
                BlockPos partnerSpringPos = (BlockPos)partnerPosField.get(spring);
                UUID partnerSubLevel = (UUID)partnerSubLevelField.get(spring);
                if (partnerSpringPos != null && (targetAnchor = this.findAnchorAround(partnerSpringPos, partnerSubLevel)) != null && Math.abs(targetAnchor.targetInjectedSpeed - newSpeed) > 0.001f) {
                    targetAnchor.targetInjectedSpeed = newSpeed;
                    targetAnchor.isDrivenBySpring = newSpeed != 0.0f;
                    targetAnchor.updateGeneratedRotation();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private TorsionalAnchorBlockEntity findAnchorAround(BlockPos springPos, UUID subLevelUUID) {
        SubLevelContainer container;
        SubLevel subLevel;
        Level targetLevel = this.level;
        if (subLevelUUID != null && (subLevel = (container = SubLevelContainer.getContainer((Level)this.level)).getSubLevel(subLevelUUID)) != null) {
            targetLevel = subLevel.getLevel();
        }
        if (targetLevel == null) {
            return null;
        }
        for (Direction dir : Direction.values()) {
            Direction anchorFacing;
            TorsionalAnchorBlockEntity anchor;
            BlockState anchorState;
            BlockPos checkPos = springPos.relative(dir);
            BlockEntity checkBE = targetLevel.getBlockEntity(checkPos);
            if (!(checkBE instanceof TorsionalAnchorBlockEntity) || !(anchorState = (anchor = (TorsionalAnchorBlockEntity)checkBE).getBlockState()).hasProperty((Property)BlockStateProperties.FACING) || !checkPos.relative(anchorFacing = (Direction)anchorState.getValue((Property)BlockStateProperties.FACING)).equals((Object)springPos)) continue;
            return anchor;
        }
        return null;
    }

    public boolean isSource() {
        return this.isDrivenBySpring && this.targetInjectedSpeed != 0.0f;
    }

    public float getGeneratedSpeed() {
        return this.isDrivenBySpring ? this.targetInjectedSpeed : 0.0f;
    }

    public float calculateAddedStressCapacity() {
        if (this.isSource()) {
            float baseCapacity = 32.0f;
            float dynamicCapacity = baseCapacity / this.currentStressMultiplier;
            return dynamicCapacity * Math.abs(this.getGeneratedSpeed());
        }
        return 0.0f;
    }

    static {
        try {
            partnerPosField = SpringBlockEntity.class.getDeclaredField("partnerPos");
            partnerPosField.setAccessible(true);
            partnerSubLevelField = SpringBlockEntity.class.getDeclaredField("partnerSubLevel");
            partnerSubLevelField.setAccessible(true);
            desiredLengthField = SpringBlockEntity.class.getDeclaredField("desiredLength");
            desiredLengthField.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
