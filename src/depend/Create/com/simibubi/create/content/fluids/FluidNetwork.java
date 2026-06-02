/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity;
import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidNetwork {
    private static final int CYCLES_PER_TICK = 16;
    Level world;
    BlockFace start;
    Supplier<@Nullable ICapabilityProvider<IFluidHandler>> sourceSupplier;
    @Nullable
    ICapabilityProvider<IFluidHandler> source = null;
    int transferSpeed;
    int pauseBeforePropagation;
    List<BlockFace> queued;
    Set<Pair<BlockFace, PipeConnection>> frontier;
    Set<BlockPos> visited;
    FluidStack fluid;
    List<Pair<BlockFace, FlowSource>> targets;
    Map<BlockPos, WeakReference<FluidTransportBehaviour>> cache;

    public FluidNetwork(Level world, BlockFace location, Supplier<@Nullable ICapabilityProvider<IFluidHandler>> sourceSupplier) {
        this.world = world;
        this.start = location;
        this.sourceSupplier = sourceSupplier;
        this.fluid = FluidStack.EMPTY;
        this.frontier = new HashSet<Pair<BlockFace, PipeConnection>>();
        this.visited = new HashSet<BlockPos>();
        this.targets = new ArrayList<Pair<BlockFace, FlowSource>>();
        this.cache = new HashMap<BlockPos, WeakReference<FluidTransportBehaviour>>();
        this.queued = new ArrayList<BlockFace>();
        this.reset();
    }

    public void tick() {
        if (this.pauseBeforePropagation > 0) {
            --this.pauseBeforePropagation;
            return;
        }
        for (int cycle = 0; cycle < 16; ++cycle) {
            boolean shouldContinue = false;
            Iterator<BlockFace> iterator = this.queued.iterator();
            while (iterator.hasNext()) {
                BlockFace blockFace = iterator.next();
                if (!this.isPresent(blockFace)) continue;
                PipeConnection pipeConnection = this.get(blockFace);
                if (pipeConnection != null) {
                    if (blockFace.equals((Object)this.start)) {
                        this.transferSpeed = (int)Math.max(1.0f, ((Float)pipeConnection.pressure.get(true)).floatValue() / 2.0f);
                    }
                    this.frontier.add((Pair<BlockFace, PipeConnection>)Pair.of((Object)blockFace, (Object)pipeConnection));
                }
                iterator.remove();
            }
            iterator = this.frontier.iterator();
            while (iterator.hasNext()) {
                Pair pair = (Pair)iterator.next();
                BlockFace blockFace = (BlockFace)pair.getFirst();
                PipeConnection pipeConnection = (PipeConnection)pair.getSecond();
                if (!pipeConnection.hasFlow()) continue;
                PipeConnection.Flow flow = pipeConnection.flow.get();
                if (!this.fluid.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)flow.fluid, (FluidStack)this.fluid)) {
                    iterator.remove();
                    continue;
                }
                if (!flow.inbound) {
                    if (!(pipeConnection.comparePressure() >= 0.0f)) continue;
                    iterator.remove();
                    continue;
                }
                if (!flow.complete) continue;
                if (this.fluid.isEmpty()) {
                    this.fluid = flow.fluid;
                }
                boolean canRemove = true;
                for (Direction side : Iterate.directions) {
                    BlockFace adjacentLocation;
                    PipeConnection adjacent;
                    if (side == blockFace.getFace() || (adjacent = this.get(adjacentLocation = new BlockFace(blockFace.getPos(), side))) == null) continue;
                    if (!adjacent.hasFlow()) {
                        if (!adjacent.hasPressure() || !(((Float)adjacent.pressure.getSecond()).floatValue() > 0.0f)) continue;
                        canRemove = false;
                        continue;
                    }
                    PipeConnection.Flow outFlow = adjacent.flow.get();
                    if (outFlow.inbound) {
                        if (!(adjacent.comparePressure() > 0.0f)) continue;
                        canRemove = false;
                        continue;
                    }
                    if (!outFlow.complete) {
                        canRemove = false;
                        continue;
                    }
                    if (!adjacent.source.isPresent() && !adjacent.determineSource(this.world, blockFace.getPos())) {
                        canRemove = false;
                        continue;
                    }
                    if (adjacent.source.isPresent() && adjacent.source.get().isEndpoint()) {
                        this.targets.add((Pair<BlockFace, FlowSource>)Pair.of((Object)adjacentLocation, (Object)adjacent.source.get()));
                        continue;
                    }
                    if (!this.visited.add(adjacentLocation.getConnectedPos())) continue;
                    this.queued.add(adjacentLocation.getOpposite());
                    shouldContinue = true;
                }
                if (!canRemove) continue;
                iterator.remove();
            }
            if (!shouldContinue) break;
        }
        if (this.source == null) {
            this.source = this.sourceSupplier.get();
        }
        if (this.source == null) {
            return;
        }
        this.keepPortableFluidInterfaceEngaged();
        if (this.targets.isEmpty()) {
            return;
        }
        for (Pair<BlockFace, FlowSource> pair : this.targets) {
            PipeConnection pipeConnection;
            if (pair.getSecond() != null && this.world.getGameTime() % 40L != 0L || (pipeConnection = this.get((BlockFace)pair.getFirst())) == null) continue;
            pipeConnection.source.ifPresent(fs -> {
                if (fs.isEndpoint()) {
                    pair.setSecond(fs);
                }
            });
        }
        int flowSpeed = this.transferSpeed;
        IdentityHashMap<IFluidHandler, Integer> accumulatedFill = new IdentityHashMap<IFluidHandler, Integer>();
        for (boolean simulate : Iterate.trueAndFalse) {
            FluidStack genericExtract;
            IFluidHandler.FluidAction action;
            IFluidHandler.FluidAction fluidAction = action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            if (this.source == null) {
                return;
            }
            IFluidHandler sourceCap = this.source.getCapability();
            if (sourceCap == null) {
                return;
            }
            FluidStack transfer = FluidStack.EMPTY;
            for (int i = 0; i < sourceCap.getTanks(); ++i) {
                FluidStack contained = sourceCap.getFluidInTank(i);
                if (contained.isEmpty() || !FluidStack.isSameFluidSameComponents((FluidStack)contained, (FluidStack)this.fluid)) continue;
                FluidStack toExtract = FluidHelper.copyStackWithAmount(contained, flowSpeed);
                transfer = sourceCap.drain(toExtract, action);
                break;
            }
            if (transfer.isEmpty() && !(genericExtract = sourceCap.drain(flowSpeed, action)).isEmpty() && FluidStack.isSameFluidSameComponents((FluidStack)genericExtract, (FluidStack)this.fluid)) {
                transfer = genericExtract;
            }
            if (transfer.isEmpty()) {
                return;
            }
            if (simulate) {
                flowSpeed = transfer.getAmount();
            }
            ArrayList<Pair<BlockFace, FlowSource>> availableOutputs = new ArrayList<Pair<BlockFace, FlowSource>>(this.targets);
            block7: while (!availableOutputs.isEmpty() && transfer.getAmount() > 0) {
                int dividedTransfer = transfer.getAmount() / availableOutputs.size();
                int remainder = transfer.getAmount() % availableOutputs.size();
                Iterator iterator = availableOutputs.iterator();
                while (iterator.hasNext()) {
                    Pair pair = (Pair)iterator.next();
                    int toTransfer = dividedTransfer;
                    if (remainder > 0) {
                        ++toTransfer;
                        --remainder;
                    }
                    if (transfer.isEmpty()) continue block7;
                    @Nullable ICapabilityProvider<IFluidHandler> targetHandlerProvider = ((FlowSource)pair.getSecond()).provideHandler();
                    if (targetHandlerProvider == null) {
                        iterator.remove();
                        continue;
                    }
                    IFluidHandler targetHandler = targetHandlerProvider.getCapability();
                    if (targetHandler == null) {
                        iterator.remove();
                        continue;
                    }
                    int simulatedTransfer = toTransfer;
                    if (simulate) {
                        simulatedTransfer += accumulatedFill.getOrDefault(targetHandler, 0).intValue();
                    }
                    FluidStack divided = transfer.copy();
                    divided.setAmount(simulatedTransfer);
                    int fill = targetHandler.fill(divided, action);
                    if (simulate) {
                        accumulatedFill.put(targetHandler, fill);
                        fill -= simulatedTransfer - toTransfer;
                    }
                    transfer.setAmount(transfer.getAmount() - fill);
                    if (fill >= simulatedTransfer) continue;
                    iterator.remove();
                }
            }
            flowSpeed -= transfer.getAmount();
            transfer = FluidStack.EMPTY;
        }
    }

    private void keepPortableFluidInterfaceEngaged() {
        if (!(this.source instanceof PortableFluidInterfaceBlockEntity.InterfaceFluidHandler)) {
            return;
        }
        if (this.frontier.isEmpty()) {
            return;
        }
        ((PortableFluidInterfaceBlockEntity.InterfaceFluidHandler)((Object)this.source)).keepAlive();
    }

    public void reset() {
        this.frontier.clear();
        this.visited.clear();
        this.targets.clear();
        this.queued.clear();
        this.fluid = FluidStack.EMPTY;
        this.queued.add(this.start);
        this.pauseBeforePropagation = 2;
    }

    @Nullable
    private PipeConnection get(BlockFace location) {
        BlockPos pos = location.getPos();
        FluidTransportBehaviour fluidTransfer = this.getFluidTransfer(pos);
        if (fluidTransfer == null) {
            return null;
        }
        return fluidTransfer.getConnection(location.getFace());
    }

    private boolean isPresent(BlockFace location) {
        return this.world.isLoaded(location.getPos());
    }

    @Nullable
    private FluidTransportBehaviour getFluidTransfer(BlockPos pos) {
        FluidTransportBehaviour behaviour;
        WeakReference<FluidTransportBehaviour> weakReference = this.cache.get(pos);
        FluidTransportBehaviour fluidTransportBehaviour = behaviour = weakReference != null ? (FluidTransportBehaviour)weakReference.get() : null;
        if (behaviour != null && behaviour.blockEntity.isRemoved()) {
            behaviour = null;
        }
        if (behaviour == null && (behaviour = BlockEntityBehaviour.get((BlockGetter)this.world, pos, FluidTransportBehaviour.TYPE)) != null) {
            this.cache.put(pos, new WeakReference<FluidTransportBehaviour>(behaviour));
        }
        return behaviour;
    }
}
