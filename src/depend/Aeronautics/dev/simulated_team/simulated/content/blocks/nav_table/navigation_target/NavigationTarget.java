/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 */
package dev.simulated_team.simulated.content.blocks.nav_table.navigation_target;

import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlock;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.index.SimDataComponents;
import dev.simulated_team.simulated.util.SimMathUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;

public interface NavigationTarget {
    @Nullable
    public Vec3 getTarget(NavTableBlockEntity var1, ItemStack var2);

    default public float getDeadzone() {
        return 2.0f;
    }

    default public float getMaxRange() {
        return 200.0f;
    }

    default public float getModulatingRange() {
        return 200.0f;
    }

    default public int getRedstoneStrength(NavTableBlockEntity navBE, Direction direction, ItemStack self) {
        return this.calculateSideStrength(navBE, direction, self);
    }

    default public int calculateModulatingStrength(NavTableBlockEntity navBE, ItemStack self) {
        Vec3 currentTarget = navBE.getTargetPosition(false);
        if (currentTarget == null) {
            return 0;
        }
        Vec3 target = navBE.getTargetPosition(true);
        Vec3 navPos = navBE.getProjectedSelfPos();
        double distance = target.distanceTo(navPos);
        return (int)Math.round(((double)this.getModulatingRange() - distance) * (double)(15.0f / this.getModulatingRange()));
    }

    default public int calculateSideStrength(NavTableBlockEntity navBE, Direction direction, ItemStack self) {
        Vec3 currentTarget = navBE.getTargetPosition(false);
        if (currentTarget == null) {
            return 0;
        }
        Direction facing = (Direction)navBE.getBlockState().getValue((Property)NavTableBlock.FACING);
        Vec3i normal = facing.getNormal();
        Vec3 projectedTarget = navBE.getTargetPosition(true);
        Vec3 navPos = navBE.getProjectedSelfPos();
        Vec3 differenceVec = projectedTarget.subtract(navPos);
        Quaterniond worldshellRot = navBE.getSublevelRot();
        differenceVec = SimMathUtils.rotateQuat(differenceVec, worldshellRot);
        Vec3 projectedPos = NavigationTarget.getPlaneProjectedPos(differenceVec, normal);
        double distance = projectedPos.length();
        if (this.getMaxRange() > 0.0f && distance > (double)this.getMaxRange() - 1.0E-4) {
            return 0;
        }
        if (distance < (double)this.getDeadzone() - 1.0E-4) {
            return 0;
        }
        double dot = -projectedPos.dot(Vec3.atLowerCornerOf((Vec3i)direction.getNormal())) / distance;
        return (int)(Math.asin(dot) / Math.PI * 30.0 + 0.5);
    }

    default public double distanceToTarget(NavTableBlockEntity blockEntity) {
        Vec3 targetPosition = blockEntity.getTargetPosition(true);
        if (targetPosition != null) {
            return blockEntity.getProjectedSelfPos().distanceTo(targetPosition);
        }
        return -1.0;
    }

    default public void onInsert(ItemStack itemStack, NavTableBlockEntity be, Player player) {
    }

    public static Vec3 getPlaneProjectedPos(Vec3 targetPos, Vec3i normal) {
        double dot = targetPos.dot(Vec3.atLowerCornerOf((Vec3i)normal));
        return targetPos.subtract(Vec3.atLowerCornerOf((Vec3i)normal).scale(dot));
    }

    @Nullable
    public static NavigationTarget ofStack(ItemStack itemStack) {
        return (NavigationTarget)itemStack.get(SimDataComponents.TARGET);
    }
}
