/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.model.data.ModelData
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.FakeTrackBlock;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntityTilt;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.IMergeableBE;
import com.simibubi.create.foundation.blockEntity.RemoveBlockEntityPacket;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;

public class TrackBlockEntity
extends SmartBlockEntity
implements TransformableBlockEntity,
IMergeableBE {
    Map<BlockPos, BezierConnection> connections = new HashMap<BlockPos, BezierConnection>();
    boolean cancelDrops;
    public Pair<ResourceKey<Level>, BlockPos> boundLocation;
    public TrackBlockEntityTilt tilt;

    public TrackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(100);
        this.tilt = new TrackBlockEntityTilt(this);
    }

    public Map<BlockPos, BezierConnection> getConnections() {
        return this.connections;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!this.level.isClientSide && this.hasInteractableConnections()) {
            this.registerToCurveInteraction();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.tilt.undoSmoothing();
    }

    @Override
    public void lazyTick() {
        for (BezierConnection connection : this.connections.values()) {
            if (!connection.isPrimary()) continue;
            this.manageFakeTracksAlong(connection, false);
        }
    }

    public void validateConnections() {
        HashSet<BlockPos> invalid = new HashSet<BlockPos>();
        for (Map.Entry<BlockPos, BezierConnection> entry : this.connections.entrySet()) {
            TrackBlockEntity trackBE;
            BezierConnection bc;
            block10: {
                BlockPos key;
                block9: {
                    BlockEntity blockEntity;
                    key = entry.getKey();
                    if (!key.equals((Object)(bc = entry.getValue()).getKey()) || !this.worldPosition.equals(bc.bePositions.getFirst())) {
                        invalid.add(key);
                        continue;
                    }
                    BlockState blockState = this.level.getBlockState(key);
                    Block block = blockState.getBlock();
                    if (block instanceof ITrackBlock) {
                        ITrackBlock trackBlock = (ITrackBlock)block;
                        if (!((Boolean)blockState.getValue((Property)TrackBlock.HAS_BE)).booleanValue()) {
                            for (Vec3 v : trackBlock.getTrackAxes((BlockGetter)this.level, key, blockState)) {
                                Vec3 bcEndAxis;
                                if (!(v.distanceTo(bcEndAxis = (Vec3)bc.axes.getSecond()) < 9.765625E-4) && !(v.distanceTo(bcEndAxis.scale(-1.0)) < 9.765625E-4)) continue;
                                this.level.setBlock(key, (BlockState)blockState.setValue((Property)TrackBlock.HAS_BE, (Comparable)Boolean.valueOf(true)), 3);
                            }
                        }
                    }
                    if (!((blockEntity = this.level.getBlockEntity(key)) instanceof TrackBlockEntity)) break block9;
                    trackBE = (TrackBlockEntity)blockEntity;
                    if (!blockEntity.isRemoved()) break block10;
                }
                invalid.add(key);
                continue;
            }
            if (trackBE.connections.containsKey(this.worldPosition)) continue;
            trackBE.addConnection(bc.secondary());
            trackBE.tilt.tryApplySmoothing();
        }
        for (BlockPos blockPos : invalid) {
            this.removeConnection(blockPos);
        }
    }

    public void addConnection(BezierConnection connection) {
        if (this.connections.containsKey(connection.getKey()) && connection.equalsSansMaterial(this.connections.get(connection.getKey()))) {
            return;
        }
        this.connections.put(connection.getKey(), connection);
        this.level.scheduleTick(this.worldPosition, this.getBlockState().getBlock(), 1);
        this.notifyUpdate();
        if (connection.isPrimary()) {
            this.manageFakeTracksAlong(connection, false);
        }
    }

    public void removeConnection(BlockPos target) {
        Level level;
        if (this.isTilted()) {
            this.tilt.captureSmoothingHandles();
        }
        BezierConnection removed = this.connections.remove(target);
        this.notifyUpdate();
        if (removed != null) {
            this.manageFakeTracksAlong(removed, true);
        }
        if (!this.connections.isEmpty() || this.getBlockState().getOptionalValue(TrackBlock.SHAPE).orElse(TrackShape.NONE).isPortal()) {
            return;
        }
        BlockState blockState = this.level.getBlockState(this.worldPosition);
        if (blockState.hasProperty((Property)TrackBlock.HAS_BE)) {
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)blockState.setValue((Property)TrackBlock.HAS_BE, (Comparable)Boolean.valueOf(false)));
        }
        if ((level = this.level) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CatnipServices.NETWORK.sendToClientsTrackingChunk(serverLevel, new ChunkPos(this.worldPosition), (CustomPacketPayload)new RemoveBlockEntityPacket(this.worldPosition));
        }
    }

    public void removeInboundConnections(boolean dropAndDiscard) {
        Level level;
        for (BezierConnection bezierConnection : this.connections.values()) {
            BlockEntity blockEntity = this.level.getBlockEntity(bezierConnection.getKey());
            if (!(blockEntity instanceof TrackBlockEntity)) {
                return;
            }
            TrackBlockEntity tbe = (TrackBlockEntity)blockEntity;
            tbe.removeConnection((BlockPos)bezierConnection.bePositions.getFirst());
            if (!dropAndDiscard) continue;
            if (!this.cancelDrops) {
                bezierConnection.spawnItems(this.level);
            }
            bezierConnection.spawnDestroyParticles(this.level);
        }
        if (dropAndDiscard && (level = this.level) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CatnipServices.NETWORK.sendToClientsTrackingChunk(serverLevel, new ChunkPos(this.worldPosition), (CustomPacketPayload)new RemoveBlockEntityPacket(this.worldPosition));
        }
    }

    public void bind(ResourceKey<Level> boundDimension, BlockPos boundLocation) {
        this.boundLocation = Pair.of(boundDimension, (Object)boundLocation);
        this.setChanged();
    }

    public boolean isTilted() {
        return this.tilt.smoothingAngle.isPresent();
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        this.writeTurns(tag, true);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        this.writeTurns(tag, false);
        if (this.isTilted()) {
            tag.putDouble("Smoothing", this.tilt.smoothingAngle.get().doubleValue());
        }
        if (this.boundLocation == null) {
            return;
        }
        tag.put("BoundLocation", NbtUtils.writeBlockPos((BlockPos)((BlockPos)this.boundLocation.getSecond())));
        tag.putString("BoundDimension", ((ResourceKey)this.boundLocation.getFirst()).location().toString());
    }

    private void writeTurns(CompoundTag tag, boolean restored) {
        ListTag listTag = new ListTag();
        for (BezierConnection bezierConnection : this.connections.values()) {
            listTag.add((Object)(restored ? this.tilt.restoreToOriginalCurve(bezierConnection.clone()) : bezierConnection).write(this.worldPosition));
        }
        tag.put("Connections", (Tag)listTag);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.connections.clear();
        for (Tag t : tag.getList("Connections", 10)) {
            if (!(t instanceof CompoundTag)) {
                return;
            }
            BezierConnection connection = new BezierConnection((CompoundTag)t, this.worldPosition);
            this.connections.put(connection.getKey(), connection);
        }
        boolean smoothingPreviously = this.tilt.smoothingAngle.isPresent();
        this.tilt.smoothingAngle = Optional.ofNullable(tag.contains("Smoothing") ? Double.valueOf(tag.getDouble("Smoothing")) : null);
        if (smoothingPreviously != this.tilt.smoothingAngle.isPresent() && clientPacket) {
            this.requestModelDataUpdate();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 16);
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate((BlockEntity)this));
        if (this.hasInteractableConnections()) {
            this.registerToCurveInteraction();
        } else {
            this.removeFromCurveInteraction();
        }
        if (tag.contains("BoundLocation")) {
            this.boundLocation = Pair.of((Object)ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)ResourceLocation.parse((String)tag.getString("BoundDimension"))), (Object)NBTHelper.readBlockPos((CompoundTag)tag, (String)"BoundLocation"));
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return AABB.INFINITE;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void accept(BlockEntity other) {
        if (other instanceof TrackBlockEntity) {
            TrackBlockEntity track = (TrackBlockEntity)other;
            this.connections.putAll(track.connections);
        }
        this.validateConnections();
        this.level.scheduleTick(this.worldPosition, this.getBlockState().getBlock(), 1);
    }

    public boolean hasInteractableConnections() {
        for (BezierConnection connection : this.connections.values()) {
            if (!connection.isPrimary()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        HashMap<BlockPos, BezierConnection> restoredConnections = new HashMap<BlockPos, BezierConnection>();
        for (Map.Entry<BlockPos, BezierConnection> entry : this.connections.entrySet()) {
            restoredConnections.put(entry.getKey(), this.tilt.restoreToOriginalCurve(this.tilt.restoreToOriginalCurve(entry.getValue().secondary()).secondary()));
        }
        this.connections = restoredConnections;
        this.tilt.smoothingAngle = Optional.empty();
        if (transform.rotationAxis != Direction.Axis.Y) {
            return;
        }
        HashMap<BlockPos, BezierConnection> transformedConnections = new HashMap<BlockPos, BezierConnection>();
        for (Map.Entry<BlockPos, BezierConnection> entry : this.connections.entrySet()) {
            BezierConnection newConnection = entry.getValue();
            newConnection.normals.replace(transform::applyWithoutOffsetUncentered);
            newConnection.axes.replace(transform::applyWithoutOffsetUncentered);
            BlockPos diff = ((BlockPos)newConnection.bePositions.getSecond()).subtract((Vec3i)newConnection.bePositions.getFirst());
            newConnection.bePositions.setSecond((Object)BlockPos.containing((Position)Vec3.atCenterOf((Vec3i)((Vec3i)newConnection.bePositions.getFirst())).add(transform.applyWithoutOffsetUncentered(Vec3.atLowerCornerOf((Vec3i)diff)))));
            Vec3 beVec = Vec3.atLowerCornerOf((Vec3i)this.worldPosition);
            Vec3 teCenterVec = beVec.add(0.5, 0.5, 0.5);
            Vec3 start = (Vec3)newConnection.starts.getFirst();
            Vec3 startToBE = start.subtract(teCenterVec);
            Vec3 endToStart = ((Vec3)newConnection.starts.getSecond()).subtract(start);
            startToBE = transform.applyWithoutOffsetUncentered(startToBE).add(teCenterVec);
            endToStart = transform.applyWithoutOffsetUncentered(endToStart).add(startToBE);
            newConnection.starts.setFirst((Object)new TrackNodeLocation(startToBE).getLocation());
            newConnection.starts.setSecond((Object)new TrackNodeLocation(endToStart).getLocation());
            BlockPos newTarget = newConnection.getKey();
            transformedConnections.put(newTarget, newConnection);
        }
        this.connections = transformedConnections;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.level.isClientSide) {
            this.removeFromCurveInteraction();
        }
    }

    @Override
    public void remove() {
        super.remove();
        for (BezierConnection connection : this.connections.values()) {
            this.manageFakeTracksAlong(connection, true);
        }
        if (this.boundLocation != null && this.level instanceof ServerLevel) {
            ServerLevel otherLevel = this.level.getServer().getLevel((ResourceKey)this.boundLocation.getFirst());
            if (otherLevel == null) {
                return;
            }
            if (AllTags.AllBlockTags.TRACKS.matches(otherLevel.getBlockState((BlockPos)this.boundLocation.getSecond()))) {
                otherLevel.destroyBlock((BlockPos)this.boundLocation.getSecond(), false);
            }
        }
    }

    private void registerToCurveInteraction() {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::registerToCurveInteractionUnsafe);
    }

    private void removeFromCurveInteraction() {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::removeFromCurveInteractionUnsafe);
    }

    public ModelData getModelData() {
        if (!this.isTilted()) {
            return super.getModelData();
        }
        return ModelData.builder().with(TrackBlockEntityTilt.ASCENDING_PROPERTY, (Object)this.tilt.smoothingAngle.get()).build();
    }

    @OnlyIn(value=Dist.CLIENT)
    private void registerToCurveInteractionUnsafe() {
        ((Map)TrackBlockOutline.TRACKS_WITH_TURNS.get((LevelAccessor)this.level)).put(this.worldPosition, this);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void removeFromCurveInteractionUnsafe() {
        ((Map)TrackBlockOutline.TRACKS_WITH_TURNS.get((LevelAccessor)this.level)).remove(this.worldPosition);
    }

    public void manageFakeTracksAlong(BezierConnection bc, boolean remove) {
        Map<Pair<Integer, Integer>, Double> yLevels = bc.rasterise();
        for (Map.Entry<Pair<Integer, Integer>, Double> entry : yLevels.entrySet()) {
            double yValue = entry.getValue();
            int floor = Mth.floor((double)yValue);
            BlockPos targetPos = new BlockPos(((Integer)entry.getKey().getFirst()).intValue(), floor, ((Integer)entry.getKey().getSecond()).intValue());
            targetPos = targetPos.offset((Vec3i)bc.bePositions.getFirst()).above(1);
            BlockState stateAtPos = this.level.getBlockState(targetPos);
            boolean present = AllBlocks.FAKE_TRACK.has(stateAtPos);
            if (remove) {
                if (!present) continue;
                this.level.removeBlock(targetPos, false);
                continue;
            }
            FluidState fluidState = stateAtPos.getFluidState();
            if (!fluidState.isEmpty() && !fluidState.isSourceOfType((Fluid)Fluids.WATER)) continue;
            if (!present && stateAtPos.canBeReplaced()) {
                this.level.setBlock(targetPos, ProperWaterloggedBlock.withWater((LevelAccessor)this.level, AllBlocks.FAKE_TRACK.getDefaultState(), targetPos), 3);
            }
            FakeTrackBlock.keepAlive((LevelAccessor)this.level, targetPos);
        }
    }
}
