/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.capabilities.BlockCapability
 *  net.neoforged.neoforge.capabilities.BlockCapabilityCache
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.foundation.ICapabilityProvider;
import net.createmod.catnip.math.BlockFace;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public static class FlowSource.FluidHandler
extends FlowSource {
    @Nullable
    ICapabilityProvider<IFluidHandler> fluidHandlerCache = EMPTY;

    public FlowSource.FluidHandler(BlockFace location) {
        super(location);
    }

    @Override
    public void manageSource(Level level, BlockEntity networkBE) {
        BlockEntity blockEntity;
        if (this.fluidHandlerCache == null && (blockEntity = level.getBlockEntity(this.location.getConnectedPos())) != null) {
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                this.fluidHandlerCache = ICapabilityProvider.of(invalidate -> BlockCapabilityCache.create((BlockCapability)Capabilities.FluidHandler.BLOCK, (ServerLevel)serverLevel, (BlockPos)blockEntity.getBlockPos(), (Object)this.location.getOppositeFace(), () -> !networkBE.isRemoved(), () -> {
                    this.fluidHandlerCache = EMPTY;
                    invalidate.run();
                }));
            } else if (level instanceof PonderLevel) {
                this.fluidHandlerCache = ICapabilityProvider.of(() -> (IFluidHandler)level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), (Object)this.location.getOppositeFace()));
            }
        }
    }

    @Override
    @Nullable
    public ICapabilityProvider<IFluidHandler> provideHandler() {
        return this.fluidHandlerCache;
    }

    @Override
    public boolean isEndpoint() {
        return true;
    }
}
