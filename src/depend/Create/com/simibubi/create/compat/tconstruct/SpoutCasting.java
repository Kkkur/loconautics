/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 */
package com.simibubi.create.compat.tconstruct;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public enum SpoutCasting implements BlockSpoutingBehaviour
{
    INSTANCE;


    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
        if (!this.enabled()) {
            return 0;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return 0;
        }
        IFluidHandler handler = (IFluidHandler)level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), (Object)Direction.UP);
        if (handler == null) {
            return 0;
        }
        if (handler.getTanks() != 1) {
            return 0;
        }
        if (!handler.isFluidValid(0, availableFluid)) {
            return 0;
        }
        FluidStack containedFluid = handler.getFluidInTank(0);
        if (!containedFluid.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)containedFluid, (FluidStack)availableFluid)) {
            return 0;
        }
        int amount = availableFluid.getAmount();
        if (amount < 1000 && handler.fill(FluidHelper.copyStackWithAmount(availableFluid, amount + 1), IFluidHandler.FluidAction.SIMULATE) > amount) {
            return 0;
        }
        return handler.fill(availableFluid, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean enabled() {
        return (Boolean)AllConfigs.server().recipes.allowCastingBySpout.get();
    }
}
