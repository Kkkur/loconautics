/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 */
package com.simibubi.create.foundation.blockEntity;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;

public class ComparatorUtil {
    public static int fractionToRedstoneLevel(double frac) {
        return Mth.floor((double)Mth.clamp((double)(frac * 14.0 + (double)(frac > 0.0 ? 1 : 0)), (double)0.0, (double)15.0));
    }

    public static int levelOfSmartFluidTank(BlockGetter world, BlockPos pos) {
        SmartFluidTankBehaviour fluidBehaviour = BlockEntityBehaviour.get(world, pos, SmartFluidTankBehaviour.TYPE);
        if (fluidBehaviour == null) {
            return 0;
        }
        SmartFluidTank primaryHandler = fluidBehaviour.getPrimaryHandler();
        double fillFraction = (double)primaryHandler.getFluid().getAmount() / (double)primaryHandler.getCapacity();
        return ComparatorUtil.fractionToRedstoneLevel(fillFraction);
    }
}
