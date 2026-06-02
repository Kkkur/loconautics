/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.eriksonn.aeronautics.index.AeroArmInteractionPoints;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public static class AeroArmInteractionPoints.MountedPotatoCannonType
extends ArmInteractionPointType {
    public boolean canCreatePoint(Level var1, BlockPos var2, BlockState var3) {
        return AeroBlocks.MOUNTED_POTATO_CANNON.has(var1.getBlockState(var2));
    }

    @Nullable
    public ArmInteractionPoint createPoint(Level var1, BlockPos var2, BlockState var3) {
        return new AeroArmInteractionPoints.MountedPotatoCannonPoint(this, var1, var2, var3);
    }
}
