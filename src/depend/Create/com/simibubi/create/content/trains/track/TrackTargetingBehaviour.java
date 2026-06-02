/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class TrackTargetingBehaviour<T extends TrackEdgePoint>
extends BlockEntityBehaviour {
    public static final BehaviourType<TrackTargetingBehaviour<?>> TYPE = new BehaviourType();
    private BlockPos targetTrack;
    private BezierTrackPointLocation targetBezier;
    private Direction.AxisDirection targetDirection;
    private UUID id;
    private Vec3 prevDirection;
    private Vec3 rotatedDirection;
    private CompoundTag migrationData;
    private EdgePointType<T> edgePointType;
    private T edgePoint;
    private boolean orthogonal;

    public TrackTargetingBehaviour(SmartBlockEntity be, EdgePointType<T> edgePointType) {
        super(be);
        this.edgePointType = edgePointType;
        this.targetDirection = Direction.AxisDirection.POSITIVE;
        this.targetTrack = BlockPos.ZERO;
        this.id = UUID.randomUUID();
        this.migrationData = null;
        this.orthogonal = false;
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putUUID("Id", this.id);
        nbt.put("TargetTrack", NbtUtils.writeBlockPos((BlockPos)this.targetTrack));
        nbt.putBoolean("Ortho", this.orthogonal);
        nbt.putBoolean("TargetDirection", this.targetDirection == Direction.AxisDirection.POSITIVE);
        if (this.rotatedDirection != null) {
            nbt.put("RotatedAxis", (Tag)VecHelper.writeNBT((Vec3)this.rotatedDirection));
        }
        if (this.prevDirection != null) {
            nbt.put("PrevAxis", (Tag)VecHelper.writeNBT((Vec3)this.prevDirection));
        }
        if (this.migrationData != null && !clientPacket) {
            nbt.put("Migrate", (Tag)this.migrationData);
        }
        if (this.targetBezier != null) {
            CompoundTag bezierNbt = new CompoundTag();
            bezierNbt.putInt("Segment", this.targetBezier.segment());
            bezierNbt.put("Key", NbtUtils.writeBlockPos((BlockPos)this.targetBezier.curveTarget().subtract((Vec3i)this.getPos())));
            nbt.put("Bezier", (Tag)bezierNbt);
        }
        super.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        this.id = nbt.contains("Id") ? nbt.getUUID("Id") : UUID.randomUUID();
        this.targetTrack = NBTHelper.readBlockPos((CompoundTag)nbt, (String)"TargetTrack");
        this.targetDirection = nbt.getBoolean("TargetDirection") ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        this.orthogonal = nbt.getBoolean("Ortho");
        if (nbt.contains("PrevAxis")) {
            this.prevDirection = VecHelper.readNBT((ListTag)nbt.getList("PrevAxis", 6));
        }
        if (nbt.contains("RotatedAxis")) {
            this.rotatedDirection = VecHelper.readNBT((ListTag)nbt.getList("RotatedAxis", 6));
        }
        if (nbt.contains("Migrate")) {
            this.migrationData = nbt.getCompound("Migrate");
        }
        if (clientPacket) {
            this.edgePoint = null;
        }
        if (nbt.contains("Bezier")) {
            CompoundTag bezierNbt = nbt.getCompound("Bezier");
            BlockPos key = NBTHelper.readBlockPos((CompoundTag)bezierNbt, (String)"Key");
            this.targetBezier = new BezierTrackPointLocation(key.offset((Vec3i)this.getPos()), bezierNbt.getInt("Segment"));
        }
        super.read(nbt, registries, clientPacket);
    }

    @Nullable
    public T getEdgePoint() {
        return this.edgePoint;
    }

    public void invalidateEdgePoint(CompoundTag migrationData) {
        this.migrationData = migrationData;
        this.edgePoint = null;
        this.blockEntity.sendData();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.edgePoint == null) {
            this.edgePoint = this.createEdgePoint();
        }
    }

    public T createEdgePoint() {
        Level level = this.getWorld();
        boolean isClientSide = level.isClientSide;
        if (this.migrationData == null || isClientSide) {
            for (TrackGraph trackGraph : Create.RAILWAYS.sided((LevelAccessor)level).trackNetworks.values()) {
                T point = trackGraph.getPoint(this.edgePointType, this.id);
                if (point == null) continue;
                return point;
            }
        }
        if (isClientSide) {
            return null;
        }
        if (!this.hasValidTrack()) {
            return null;
        }
        TrackGraphLocation loc = this.determineGraphLocation();
        if (loc == null) {
            return null;
        }
        TrackGraph graph = loc.graph;
        TrackNode node1 = graph.locateNode((TrackNodeLocation)((Object)loc.edge.getFirst()));
        TrackNode node2 = graph.locateNode((TrackNodeLocation)((Object)loc.edge.getSecond()));
        TrackEdge edge = graph.getConnectionsFrom(node1).get(node2);
        if (edge == null) {
            return null;
        }
        T point = this.edgePointType.create();
        boolean front = this.getTargetDirection() == Direction.AxisDirection.POSITIVE;
        this.prevDirection = edge.getDirectionAt(loc.position).scale(front ? -1.0 : 1.0);
        if (this.rotatedDirection != null) {
            double dot = this.prevDirection.dot(this.rotatedDirection);
            if (dot < (double)-0.85f) {
                this.rotatedDirection = null;
                this.targetDirection = this.targetDirection.opposite();
                return null;
            }
            this.rotatedDirection = null;
        }
        double length = edge.getLength();
        CompoundTag data = this.migrationData;
        this.migrationData = null;
        this.orthogonal = this.targetBezier == null;
        Vec3 direction = edge.getDirection(true);
        int nonZeroComponents = 0;
        for (Direction.Axis axis : Iterate.axes) {
            nonZeroComponents += direction.get(axis) != 0.0 ? 1 : 0;
        }
        this.orthogonal &= nonZeroComponents <= 1;
        EdgeData signalData = edge.getEdgeData();
        if (signalData.hasPoints()) {
            for (EdgePointType<?> otherType : EdgePointType.TYPES.values()) {
                Object otherPoint = signalData.get(otherType, loc.position);
                if (otherPoint == null) continue;
                if (otherType != this.edgePointType) {
                    if (((TrackEdgePoint)otherPoint).canCoexistWith(this.edgePointType, front)) continue;
                    return null;
                }
                if (!((TrackEdgePoint)otherPoint).canMerge()) {
                    return null;
                }
                ((TrackEdgePoint)otherPoint).blockEntityAdded(this.blockEntity, front);
                this.id = ((TrackEdgePoint)otherPoint).getId();
                this.blockEntity.notifyUpdate();
                return (T)otherPoint;
            }
        }
        if (data != null) {
            ((TrackEdgePoint)point).read(data, (HolderLookup.Provider)level.registryAccess(), true, DimensionPalette.read(data));
        }
        ((TrackEdgePoint)point).setId(this.id);
        boolean reverseEdge = front || point instanceof SingleBlockEntityEdgePoint;
        ((TrackEdgePoint)point).setLocation((Couple<TrackNodeLocation>)(reverseEdge ? loc.edge : loc.edge.swap()), reverseEdge ? loc.position : length - loc.position);
        ((TrackEdgePoint)point).blockEntityAdded(this.blockEntity, front);
        loc.graph.addPoint(this.edgePointType, point);
        this.blockEntity.sendData();
        return point;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.edgePoint != null && !this.getWorld().isClientSide) {
            ((TrackEdgePoint)this.edgePoint).blockEntityRemoved(this.getPos(), this.getTargetDirection() == Direction.AxisDirection.POSITIVE);
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public boolean isOnCurve() {
        return this.targetBezier != null;
    }

    public boolean isOrthogonal() {
        return this.orthogonal;
    }

    public boolean hasValidTrack() {
        return this.getTrackBlockState().getBlock() instanceof ITrackBlock;
    }

    public ITrackBlock getTrack() {
        return (ITrackBlock)this.getTrackBlockState().getBlock();
    }

    public BlockState getTrackBlockState() {
        return this.getWorld().getBlockState(this.getGlobalPosition());
    }

    public BlockPos getGlobalPosition() {
        return this.targetTrack.offset((Vec3i)this.blockEntity.getBlockPos());
    }

    public BlockPos getPositionForMapMarker() {
        BlockEntity blockEntity;
        BlockPos target = this.targetTrack.offset((Vec3i)this.blockEntity.getBlockPos());
        if (this.targetBezier != null && (blockEntity = this.getWorld().getBlockEntity(target)) instanceof TrackBlockEntity) {
            TrackBlockEntity tbe = (TrackBlockEntity)blockEntity;
            BezierConnection bc = tbe.getConnections().get(this.targetBezier.curveTarget());
            if (bc == null) {
                return target;
            }
            double length = Mth.floor((double)(bc.getLength() * 2.0));
            int seg = this.targetBezier.segment() + 1;
            double t = (double)seg / length;
            return BlockPos.containing((Position)bc.getPosition(t));
        }
        return target;
    }

    public Direction.AxisDirection getTargetDirection() {
        return this.targetDirection;
    }

    public BezierTrackPointLocation getTargetBezier() {
        return this.targetBezier;
    }

    public TrackGraphLocation determineGraphLocation() {
        Level level = this.getWorld();
        BlockPos pos = this.getGlobalPosition();
        BlockState state = this.getTrackBlockState();
        ITrackBlock track = this.getTrack();
        List<Vec3> trackAxes = track.getTrackAxes((BlockGetter)level, pos, state);
        Direction.AxisDirection targetDirection = this.getTargetDirection();
        return this.targetBezier != null ? TrackGraphHelper.getBezierGraphLocationAt(level, pos, targetDirection, this.targetBezier) : TrackGraphHelper.getGraphLocationAt(level, pos, targetDirection, trackAxes.get(0));
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void render(LevelAccessor level, BlockPos pos, Direction.AxisDirection direction, BezierTrackPointLocation bezier, PoseStack ms, MultiBufferSource buffer, int light, int overlay, RenderedTrackOverlayType type, float scale) {
        if (level instanceof SchematicLevel && !(level instanceof PonderLevel)) {
            return;
        }
        BlockState trackState = level.getBlockState(pos);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return;
        }
        ITrackBlock track = (ITrackBlock)block;
        ms.pushPose();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        PartialModel partial = track.prepareTrackOverlay(msr, (BlockGetter)level, pos, trackState, bezier, direction, type);
        if (partial != null) {
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)partial, (BlockState)trackState).translate(0.5, 0.0, 0.5)).scale(scale)).translate(-0.5, 0.0, -0.5)).light(LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)pos)).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        }
        ms.popPose();
    }

    public void transform(BlockEntity be, StructureTransform transform) {
        this.id = UUID.randomUUID();
        this.targetTrack = transform.applyWithoutOffset(this.targetTrack);
        if (this.prevDirection != null) {
            this.rotatedDirection = transform.applyWithoutOffsetUncentered(this.prevDirection);
        }
        if (this.targetBezier != null) {
            this.targetBezier = new BezierTrackPointLocation(transform.applyWithoutOffset(this.targetBezier.curveTarget().subtract((Vec3i)this.getPos())).offset((Vec3i)this.getPos()), this.targetBezier.segment());
        }
        this.blockEntity.notifyUpdate();
    }

    public static enum RenderedTrackOverlayType {
        STATION,
        SIGNAL,
        DUAL_SIGNAL,
        OBSERVER;

    }
}
