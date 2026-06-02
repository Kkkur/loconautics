/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.bus.api.Event
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.AllFluids;
import com.simibubi.create.api.event.PipeCollisionEvent;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

@EventBusSubscriber
public class FluidReactions {
    public static void handlePipeFlowCollision(Level level, BlockPos pos, FluidStack fluid, FluidStack fluid2) {
        Fluid f1 = fluid.getFluid();
        Fluid f2 = fluid2.getFluid();
        AdvancementBehaviour.tryAward((BlockGetter)level, pos, AllAdvancements.CROSS_STREAMS);
        BlockHelper.destroyBlock(level, pos, 1.0f);
        PipeCollisionEvent.Flow event = new PipeCollisionEvent.Flow(level, pos, f1, f2, null);
        NeoForge.EVENT_BUS.post((Event)event);
        if (event.getState() != null) {
            level.setBlockAndUpdate(pos, event.getState());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void handlePipeFlowCollisionFallback(PipeCollisionEvent.Flow event) {
        BlockState lavaInteraction;
        Fluid f1 = event.getFirstFluid();
        Fluid f2 = event.getSecondFluid();
        if (f1 == Fluids.WATER && f2 == Fluids.LAVA || f2 == Fluids.WATER && f1 == Fluids.LAVA) {
            event.setState(Blocks.COBBLESTONE.defaultBlockState());
        } else if (f1 == Fluids.LAVA && FluidHelper.hasBlockState(f2)) {
            BlockState lavaInteraction2 = AllFluids.getLavaInteraction(FluidHelper.convertToFlowing(f2).defaultFluidState());
            if (lavaInteraction2 != null) {
                event.setState(lavaInteraction2);
            }
        } else if (f2 == Fluids.LAVA && FluidHelper.hasBlockState(f1) && (lavaInteraction = AllFluids.getLavaInteraction(FluidHelper.convertToFlowing(f1).defaultFluidState())) != null) {
            event.setState(lavaInteraction);
        }
    }

    public static void handlePipeSpillCollision(Level level, BlockPos pos, Fluid pipeFluid, FluidState worldFluid) {
        Fluid pf = FluidHelper.convertToStill(pipeFluid);
        Fluid wf = worldFluid.getType();
        PipeCollisionEvent.Spill event = new PipeCollisionEvent.Spill(level, pos, wf, pf, null);
        NeoForge.EVENT_BUS.post((Event)event);
        if (event.getState() != null) {
            level.setBlockAndUpdate(pos, event.getState());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void handlePipeSpillCollisionFallback(PipeCollisionEvent.Spill event) {
        BlockState lavaInteraction;
        Fluid pf = event.getPipeFluid();
        Fluid wf = event.getWorldFluid();
        if (FluidHelper.isTag(pf, (TagKey<Fluid>)FluidTags.WATER) && wf == Fluids.LAVA) {
            event.setState(Blocks.OBSIDIAN.defaultBlockState());
        } else if (pf == Fluids.WATER && wf == Fluids.FLOWING_LAVA) {
            event.setState(Blocks.COBBLESTONE.defaultBlockState());
        } else if (pf == Fluids.LAVA && wf == Fluids.WATER) {
            event.setState(Blocks.STONE.defaultBlockState());
        } else if (pf == Fluids.LAVA && wf == Fluids.FLOWING_WATER) {
            event.setState(Blocks.COBBLESTONE.defaultBlockState());
        }
        if (pf == Fluids.LAVA) {
            BlockState lavaInteraction2 = AllFluids.getLavaInteraction(wf.defaultFluidState());
            if (lavaInteraction2 != null) {
                event.setState(lavaInteraction2);
            }
        } else if (wf == Fluids.FLOWING_LAVA && FluidHelper.hasBlockState(pf) && (lavaInteraction = AllFluids.getLavaInteraction(FluidHelper.convertToFlowing(pf).defaultFluidState())) != null) {
            event.setState(lavaInteraction);
        }
    }
}
