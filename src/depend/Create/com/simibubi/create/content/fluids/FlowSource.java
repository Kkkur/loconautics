/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.capabilities.BlockCapability
 *  net.neoforged.neoforge.capabilities.BlockCapabilityCache
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.lang.ref.WeakReference;
import java.util.function.Predicate;
import net.createmod.catnip.math.BlockFace;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public abstract class FlowSource {
    private static final ICapabilityProvider<IFluidHandler> EMPTY = null;
    BlockFace location;

    public FlowSource(BlockFace location) {
        this.location = location;
    }

    public FluidStack provideFluid(Predicate<FluidStack> extractionPredicate) {
        @Nullable ICapabilityProvider<IFluidHandler> tankCache = this.provideHandler();
        if (tankCache == null) {
            return FluidStack.EMPTY;
        }
        IFluidHandler tank = tankCache.getCapability();
        if (tank == null) {
            return FluidStack.EMPTY;
        }
        FluidStack immediateFluid = tank.drain(1, IFluidHandler.FluidAction.SIMULATE);
        if (extractionPredicate.test(immediateFluid)) {
            return immediateFluid;
        }
        for (int i = 0; i < tank.getTanks(); ++i) {
            FluidStack contained = tank.getFluidInTank(i);
            if (contained.isEmpty() || !extractionPredicate.test(contained)) continue;
            FluidStack toExtract = contained.copy();
            toExtract.setAmount(1);
            return tank.drain(toExtract, IFluidHandler.FluidAction.SIMULATE);
        }
        return FluidStack.EMPTY;
    }

    public void keepAlive() {
    }

    public abstract boolean isEndpoint();

    public void manageSource(Level world, BlockEntity networkBE) {
    }

    public void whileFlowPresent(Level world, boolean pulling) {
    }

    @Nullable
    public ICapabilityProvider<IFluidHandler> provideHandler() {
        return EMPTY;
    }

    public static class Blocked
    extends FlowSource {
        public Blocked(BlockFace location) {
            super(location);
        }

        @Override
        public boolean isEndpoint() {
            return false;
        }
    }

    public static class OtherPipe
    extends FlowSource {
        WeakReference<FluidTransportBehaviour> cached;

        public OtherPipe(BlockFace location) {
            super(location);
        }

        @Override
        public void manageSource(Level world, BlockEntity networkBE) {
            if (this.cached != null && this.cached.get() != null && !((FluidTransportBehaviour)this.cached.get()).blockEntity.isRemoved()) {
                return;
            }
            this.cached = null;
            FluidTransportBehaviour fluidTransportBehaviour = BlockEntityBehaviour.get((BlockGetter)world, this.location.getConnectedPos(), FluidTransportBehaviour.TYPE);
            if (fluidTransportBehaviour != null) {
                this.cached = new WeakReference<FluidTransportBehaviour>(fluidTransportBehaviour);
            }
        }

        @Override
        public FluidStack provideFluid(Predicate<FluidStack> extractionPredicate) {
            if (this.cached == null || this.cached.get() == null) {
                return FluidStack.EMPTY;
            }
            FluidTransportBehaviour behaviour = (FluidTransportBehaviour)this.cached.get();
            FluidStack providedOutwardFluid = behaviour.getProvidedOutwardFluid(this.location.getOppositeFace());
            return extractionPredicate.test(providedOutwardFluid) ? providedOutwardFluid : FluidStack.EMPTY;
        }

        @Override
        public boolean isEndpoint() {
            return false;
        }
    }

    public static class FluidHandler
    extends FlowSource {
        @Nullable
        ICapabilityProvider<IFluidHandler> fluidHandlerCache = EMPTY;

        public FluidHandler(BlockFace location) {
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
}
