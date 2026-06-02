/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry$FluidInteraction
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry$HasFluidInteraction
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry$InteractionInformation
 *  net.neoforged.neoforge.fluids.FluidType
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.drill;

import com.simibubi.create.content.kinetics.drill.CobbleGenLevel;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.mixin.accessor.FluidInteractionRegistryAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class CobbleGenOptimisation {
    static CobbleGenLevel cachedLevel;

    @Nullable
    public static CobbleGenBlockConfiguration getConfig(LevelAccessor level, BlockPos drillPos, Direction drillDirection) {
        ArrayList<BlockState> list = new ArrayList<BlockState>();
        for (Direction side : Iterate.directions) {
            Level l;
            BlockPos relative = drillPos.relative(drillDirection).relative(side);
            if (level instanceof Level && !(l = (Level)level).isLoaded(relative)) {
                return null;
            }
            list.add(level.getBlockState(relative));
        }
        return new CobbleGenBlockConfiguration(list);
    }

    public static BlockState determineOutput(ServerLevel level, BlockPos pos, CobbleGenBlockConfiguration config) {
        ServerLevel owLevel;
        Map<FluidType, List<FluidInteractionRegistry.InteractionInformation>> interactions = FluidInteractionRegistryAccessor.getInteractions();
        HashMap<FluidType, Pair> presentFluidTypes = new HashMap<FluidType, Pair>();
        for (int i = 0; i < Iterate.directions.length && config.statesAroundDrill.size() > i; ++i) {
            FluidState fluidState = config.statesAroundDrill.get(i).getFluidState();
            FluidType fluidType = fluidState.getFluidType();
            if (fluidType.isAir() || interactions.get(fluidType) == null) continue;
            presentFluidTypes.put(fluidType, Pair.of((Object)Iterate.directions[i], (Object)fluidState));
        }
        FluidInteractionRegistry.FluidInteraction interaction = null;
        Pair affected = null;
        block1: for (Map.Entry type : presentFluidTypes.entrySet()) {
            List<FluidInteractionRegistry.InteractionInformation> list = interactions.get(type.getKey());
            FluidState state = FluidHelper.convertToFlowing(((FluidState)((Pair)type.getValue()).getSecond()).getType()).defaultFluidState();
            if (list == null) continue;
            for (Direction d : Iterate.horizontalDirections) {
                for (FluidInteractionRegistry.InteractionInformation information : list) {
                    if (d == ((Pair)type.getValue()).getFirst()) continue;
                    BlockPos relative = pos.relative(d);
                    FluidInteractionRegistry.HasFluidInteraction predicate = information.predicate();
                    if (!predicate.test((Level)level, pos, relative, state)) continue;
                    interaction = information.interaction();
                    affected = Pair.of((Object)d, (Object)state);
                    break block1;
                }
            }
        }
        if ((owLevel = level.getServer().getLevel(Level.OVERWORLD)) == null) {
            owLevel = level;
        }
        if (cachedLevel == null || cachedLevel.getLevel() != owLevel) {
            cachedLevel = new CobbleGenLevel((Level)level);
        }
        BlockState result = Blocks.AIR.defaultBlockState();
        if (interaction == null) {
            return result;
        }
        interaction.interact((Level)cachedLevel, pos, pos.relative((Direction)affected.getFirst()), (FluidState)affected.getSecond());
        BlockState output = CobbleGenOptimisation.cachedLevel.blocksAdded.getOrDefault(pos, result);
        cachedLevel.clear();
        return output;
    }

    public static void invalidateWorld(LevelAccessor world) {
        if (cachedLevel != null && cachedLevel.getLevel() == world) {
            cachedLevel = null;
        }
    }

    public record CobbleGenBlockConfiguration(List<BlockState> statesAroundDrill) {
    }
}
