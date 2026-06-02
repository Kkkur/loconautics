/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.api.behaviour.spouting;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public enum CauldronSpoutingBehavior implements BlockSpoutingBehaviour
{
    INSTANCE;

    public static final SimpleRegistry<Fluid, CauldronInfo> CAULDRON_INFO;

    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
        CauldronInfo info = CAULDRON_INFO.get(availableFluid.getFluid());
        if (info == null) {
            return 0;
        }
        if (availableFluid.getAmount() < info.amount) {
            return 0;
        }
        if (!simulate) {
            level.setBlockAndUpdate(pos, info.cauldron);
        }
        return info.amount;
    }

    static {
        CAULDRON_INFO = (SimpleRegistry)Util.make(() -> {
            SimpleRegistry<FlowingFluid, CauldronInfo> registry = SimpleRegistry.create();
            registry.register(Fluids.WATER, new CauldronInfo(250, Blocks.WATER_CAULDRON));
            registry.register(Fluids.LAVA, new CauldronInfo(1000, Blocks.LAVA_CAULDRON));
            return registry;
        });
    }

    public record CauldronInfo(int amount, BlockState cauldron) {
        public CauldronInfo(int amount, Block block) {
            this(amount, block.defaultBlockState());
        }
    }
}
