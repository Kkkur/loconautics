/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.BlockFace
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.FloatTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.FluidNetwork;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Optional;
import java.util.function.Predicate;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.BlockFace;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

public class PipeConnection {
    public Direction side;
    Couple<Float> pressure;
    Optional<FlowSource> source;
    Optional<FlowSource> previousSource;
    Optional<Flow> flow;
    boolean particleSplashNextTick;
    Optional<FluidNetwork> network;
    public static final int MAX_PARTICLE_RENDER_DISTANCE = 20;
    public static final int SPLASH_PARTICLE_AMOUNT = 1;
    public static final float IDLE_PARTICLE_SPAWN_CHANCE = 0.001f;
    public static final float RIM_RADIUS = 0.265625f;

    public PipeConnection(Direction side) {
        this.side = side;
        this.pressure = Couple.create(() -> Float.valueOf(0.0f));
        this.flow = Optional.empty();
        this.previousSource = Optional.empty();
        this.source = Optional.empty();
        this.network = Optional.empty();
        this.particleSplashNextTick = false;
    }

    public FluidStack getProvidedFluid() {
        FluidStack empty = FluidStack.EMPTY;
        if (!this.hasFlow()) {
            return empty;
        }
        Flow flow = this.flow.get();
        if (!flow.inbound) {
            return empty;
        }
        if (!flow.complete) {
            return empty;
        }
        return flow.fluid;
    }

    public boolean flipFlowsIfPressureReversed() {
        if (!this.hasFlow()) {
            return false;
        }
        boolean singlePressure = this.comparePressure() != 0.0f && (this.getInboundPressure() == 0.0f || this.getOutwardPressure() == 0.0f);
        Flow flow = this.flow.get();
        if (!singlePressure || this.comparePressure() < 0.0f == flow.inbound) {
            return false;
        }
        boolean bl = flow.inbound = !flow.inbound;
        if (!flow.complete) {
            this.flow = Optional.empty();
        }
        return true;
    }

    public void manageSource(Level world, BlockPos pos, BlockEntity blockEntity) {
        if (!this.source.isPresent() && !this.determineSource(world, pos)) {
            return;
        }
        FlowSource flowSource = this.source.get();
        flowSource.manageSource(world, blockEntity);
    }

    public boolean manageFlows(Level world, BlockPos pos, FluidStack internalFluid, Predicate<FluidStack> extractionPredicate) {
        FluidStack provided;
        Optional<FluidNetwork> retainedNetwork = this.network;
        this.network = Optional.empty();
        if (!this.source.isPresent() && !this.determineSource(world, pos)) {
            return false;
        }
        FlowSource flowSource = this.source.get();
        if (!this.hasFlow()) {
            if (!this.hasPressure()) {
                return false;
            }
            boolean prioritizeInbound = this.comparePressure() < 0.0f;
            for (boolean trueFalse : Iterate.trueAndFalse) {
                boolean inbound;
                boolean bl = inbound = prioritizeInbound == trueFalse;
                if (((Float)this.pressure.get(inbound)).floatValue() == 0.0f || !this.tryStartingNewFlow(inbound, inbound ? flowSource.provideFluid(extractionPredicate) : internalFluid)) continue;
                return true;
            }
            return false;
        }
        Flow flow = this.flow.get();
        FluidStack fluidStack = provided = flow.inbound ? flowSource.provideFluid(extractionPredicate) : internalFluid;
        if (!this.hasPressure() || provided.isEmpty() || !FluidStack.isSameFluidSameComponents((FluidStack)provided, (FluidStack)flow.fluid)) {
            this.flow = Optional.empty();
            return true;
        }
        if (flow.inbound != this.comparePressure() < 0.0f) {
            boolean inbound;
            boolean bl = inbound = !flow.inbound;
            if (inbound && !provided.isEmpty() || !inbound && !internalFluid.isEmpty()) {
                FluidPropagator.resetAffectedFluidNetworks(world, pos, this.side);
                this.tryStartingNewFlow(inbound, inbound ? flowSource.provideFluid(extractionPredicate) : internalFluid);
                return true;
            }
        }
        flowSource.whileFlowPresent(world, flow.inbound);
        if (!flowSource.isEndpoint()) {
            return false;
        }
        if (!flow.inbound) {
            return false;
        }
        this.network = retainedNetwork;
        if (!this.hasNetwork()) {
            this.network = Optional.of(new FluidNetwork(world, new BlockFace(pos, this.side), flowSource::provideHandler));
        }
        this.network.get().tick();
        return false;
    }

    private boolean tryStartingNewFlow(boolean inbound, FluidStack providedFluid) {
        if (providedFluid.isEmpty()) {
            return false;
        }
        Flow flow = new Flow(this, inbound, providedFluid);
        this.flow = Optional.of(flow);
        return true;
    }

    public boolean determineSource(Level world, BlockPos pos) {
        BlockPos relative = pos.relative(this.side);
        if (world.getChunk(relative.getX() >> 4, relative.getZ() >> 4, ChunkStatus.FULL, false) == null) {
            return false;
        }
        BlockFace location = new BlockFace(pos, this.side);
        if (FluidPropagator.isOpenEnd((BlockGetter)world, pos, this.side)) {
            this.source = this.previousSource.orElse(null) instanceof OpenEndedPipe ? this.previousSource : Optional.of(new OpenEndedPipe(location));
            return true;
        }
        if (FluidPropagator.hasFluidCapability((BlockGetter)world, location.getConnectedPos(), this.side.getOpposite())) {
            this.source = Optional.of(new FlowSource.FluidHandler(location));
            return true;
        }
        FluidTransportBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, relative, FluidTransportBehaviour.TYPE);
        this.source = Optional.of(behaviour == null ? new FlowSource.Blocked(location) : new FlowSource.OtherPipe(location));
        return true;
    }

    public void tickFlowProgress(Level world, BlockPos pos) {
        if (!this.hasFlow()) {
            return;
        }
        Flow flow = this.flow.get();
        if (flow.fluid.isEmpty()) {
            return;
        }
        if (world.isClientSide) {
            if (!this.source.isPresent()) {
                this.determineSource(world, pos);
            }
            this.spawnParticles(world, pos, flow.fluid);
            if (this.particleSplashNextTick) {
                this.spawnSplashOnRim(world, pos, flow.fluid);
            }
            this.particleSplashNextTick = false;
        }
        float flowSpeed = 0.03125f + Mth.clamp((float)(((Float)this.pressure.get(flow.inbound)).floatValue() / 128.0f), (float)0.0f, (float)1.0f) * 31.0f / 32.0f;
        flow.progress.setValue((double)Math.min(flow.progress.getValue() + flowSpeed, 1.0f));
        if (flow.progress.getValue() >= 1.0f) {
            flow.complete = true;
        }
    }

    public void serializeNBT(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        CompoundTag connectionData = new CompoundTag();
        tag.put(this.side.getName(), (Tag)connectionData);
        if (this.hasPressure()) {
            ListTag pressureData = new ListTag();
            pressureData.add((Object)FloatTag.valueOf((float)this.getInboundPressure()));
            pressureData.add((Object)FloatTag.valueOf((float)this.getOutwardPressure()));
            connectionData.put("Pressure", (Tag)pressureData);
        }
        if (this.hasOpenEnd()) {
            connectionData.put("OpenEnd", (Tag)((OpenEndedPipe)this.source.get()).serializeNBT(registries));
        }
        if (this.hasFlow()) {
            CompoundTag flowData = new CompoundTag();
            Flow flow = this.flow.get();
            flowData.put("Fluid", flow.fluid.saveOptional(registries));
            flowData.putBoolean("In", flow.inbound);
            if (!flow.complete) {
                flowData.put("Progress", (Tag)flow.progress.writeNBT());
            }
            connectionData.put("Flow", (Tag)flowData);
        }
    }

    private boolean hasOpenEnd() {
        return this.source.orElse(null) instanceof OpenEndedPipe;
    }

    public void deserializeNBT(CompoundTag tag, HolderLookup.Provider registries, BlockPos blockEntityPos, boolean clientPacket) {
        CompoundTag connectionData = tag.getCompound(this.side.getName());
        if (connectionData.contains("Pressure")) {
            ListTag pressureData = connectionData.getList("Pressure", 5);
            this.pressure = Couple.create((Object)Float.valueOf(pressureData.getFloat(0)), (Object)Float.valueOf(pressureData.getFloat(1)));
        } else {
            this.pressure.replace(f -> Float.valueOf(0.0f));
        }
        this.source = Optional.empty();
        if (connectionData.contains("OpenEnd")) {
            this.source = Optional.of(OpenEndedPipe.fromNBT(connectionData.getCompound("OpenEnd"), registries, blockEntityPos));
        }
        if (connectionData.contains("Flow")) {
            CompoundTag flowData = connectionData.getCompound("Flow");
            FluidStack fluid = FluidStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)flowData.getCompound("Fluid"));
            boolean inbound = flowData.getBoolean("In");
            if (this.flow.isEmpty()) {
                this.flow = Optional.of(new Flow(this, inbound, fluid));
                if (clientPacket) {
                    this.particleSplashNextTick = true;
                }
            }
            Flow flow = this.flow.get();
            flow.fluid = fluid;
            flow.inbound = inbound;
            boolean bl = flow.complete = !flowData.contains("Progress");
            if (!flow.complete) {
                flow.progress.readNBT(flowData.getCompound("Progress"), clientPacket);
            } else {
                if (flow.progress.getValue() == 0.0f) {
                    flow.progress.startWithValue(1.0);
                }
                flow.progress.setValue(1.0);
            }
        } else {
            this.flow = Optional.empty();
        }
    }

    public float comparePressure() {
        return this.getOutwardPressure() - this.getInboundPressure();
    }

    public void wipePressure() {
        this.pressure.replace(f -> Float.valueOf(0.0f));
        if (this.source.isPresent()) {
            this.previousSource = this.source;
        }
        this.source = Optional.empty();
        this.resetNetwork();
    }

    public FluidStack provideOutboundFlow() {
        if (!this.hasFlow()) {
            return FluidStack.EMPTY;
        }
        Flow flow = this.flow.get();
        if (!flow.complete || flow.inbound) {
            return FluidStack.EMPTY;
        }
        return flow.fluid;
    }

    public void addPressure(boolean inbound, float pressure) {
        this.pressure = this.pressure.mapWithContext((f, in) -> Float.valueOf(in == inbound ? f.floatValue() + pressure : f.floatValue()));
    }

    public Couple<Float> getPressure() {
        return this.pressure;
    }

    public boolean hasPressure() {
        return this.getInboundPressure() != 0.0f || this.getOutwardPressure() != 0.0f;
    }

    private float getOutwardPressure() {
        return ((Float)this.pressure.getSecond()).floatValue();
    }

    private float getInboundPressure() {
        return ((Float)this.pressure.getFirst()).floatValue();
    }

    public boolean hasFlow() {
        return this.flow.isPresent();
    }

    public boolean hasNetwork() {
        return this.network.isPresent();
    }

    public void resetNetwork() {
        this.network.ifPresent(FluidNetwork::reset);
    }

    public void spawnSplashOnRim(Level world, BlockPos pos, FluidStack fluid) {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.spawnSplashOnRimInner(world, pos, fluid));
    }

    public void spawnParticles(Level world, BlockPos pos, FluidStack fluid) {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.spawnParticlesInner(world, pos, fluid));
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnParticlesInner(Level level, BlockPos pos, FluidStack fluid) {
        if (level == Minecraft.getInstance().level && !PipeConnection.isRenderEntityWithinDistance(pos)) {
            return;
        }
        if (this.hasOpenEnd()) {
            this.spawnPouringLiquid(level, pos, fluid, 1);
        } else if (level.random.nextFloat() < 0.001f) {
            this.spawnRimParticles(level, pos, fluid, 1);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnSplashOnRimInner(Level world, BlockPos pos, FluidStack fluid) {
        if (world == Minecraft.getInstance().level && !PipeConnection.isRenderEntityWithinDistance(pos)) {
            return;
        }
        this.spawnRimParticles(world, pos, fluid, 1);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnRimParticles(Level world, BlockPos pos, FluidStack fluid, int amount) {
        if (this.hasOpenEnd()) {
            this.spawnPouringLiquid(world, pos, fluid, amount);
            return;
        }
        ParticleOptions particle = FluidFX.getDrippingParticle(fluid);
        FluidFX.spawnRimParticles(world, pos, this.side, amount, particle, 0.265625f);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnPouringLiquid(Level world, BlockPos pos, FluidStack fluid, int amount) {
        ParticleOptions particle = FluidFX.getFluidParticle(fluid);
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)this.side.getNormal());
        if (!this.hasFlow()) {
            return;
        }
        Flow flow = this.flow.get();
        FluidFX.spawnPouringLiquid(world, pos, amount, particle, 0.265625f, directionVec, flow.inbound);
    }

    @OnlyIn(value=Dist.CLIENT)
    public static boolean isRenderEntityWithinDistance(BlockPos pos) {
        Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();
        if (renderViewEntity == null) {
            return false;
        }
        Vec3 center = VecHelper.getCenterOf((Vec3i)pos);
        return !(renderViewEntity.position().distanceTo(center) > 20.0);
    }

    public class Flow {
        public boolean complete;
        public boolean inbound;
        public LerpedFloat progress;
        public FluidStack fluid;

        public Flow(PipeConnection this$0, boolean inbound, FluidStack fluid) {
            this.inbound = inbound;
            this.fluid = fluid;
            this.progress = LerpedFloat.linear().startWithValue(0.0);
            this.complete = false;
        }
    }
}
