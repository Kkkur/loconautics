/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.RandomizableContainer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.decoration.HangingEntity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniondc
 *  org.joml.Vector2i
 *  org.joml.Vector2ic
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.platform.SableAssemblyPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import dev.ryanhcode.sable.sublevel.tracking_points.TrackingPoint;
import dev.ryanhcode.sable.util.BoundedBitVolume3i;
import dev.ryanhcode.sable.util.LevelAccelerator;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelAssemblyHelper {
    public static ServerSubLevel assembleBlocks(ServerLevel level, BlockPos anchor, Iterable<BlockPos> blocks, BoundingBox3ic bounds) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        assert (container != null);
        SubLevel containingSubLevel = Sable.HELPER.getContaining((Level)level, (Vec3i)anchor);
        Pose3d pose = new Pose3d();
        pose.position().set((double)anchor.getX() + 0.5, (double)anchor.getY() + 0.5, (double)anchor.getZ() + 0.5);
        if (containingSubLevel != null) {
            Pose3d containingPose = containingSubLevel.logicalPose();
            containingPose.transformPosition(pose.position());
            pose.orientation().set((Quaterniondc)containingPose.orientation());
        }
        ServerSubLevel subLevel = (ServerSubLevel)container.allocateNewSubLevel(pose);
        ServerLevelPlot plot = subLevel.getPlot();
        plot.newEmptyChunk(plot.getCenterChunk());
        BlockPos plotAnchor = plot.getCenterBlock();
        AssemblyTransform transform = new AssemblyTransform(anchor, plotAnchor, 0, Rotation.NONE, level);
        SubLevelAssemblyHelper.moveOtherStuff(level, transform, blocks, bounds);
        SubLevelAssemblyHelper.moveBlocks(level, transform, blocks);
        Vector3dc centerOfMass = subLevel.getMassTracker().getCenterOfMass();
        Vec3 subLevelCenter = Vec3.atLowerCornerOf((Vec3i)anchor);
        if (centerOfMass != null) {
            subLevelCenter = subLevelCenter.subtract(Vec3.atLowerCornerOf((Vec3i)plotAnchor)).add(centerOfMass.x(), centerOfMass.y(), centerOfMass.z());
        } else {
            subLevel.logicalPose().rotationPoint().set((double)plotAnchor.getX() + 0.5, (double)plotAnchor.getY() + 0.5, (double)plotAnchor.getZ() + 0.5);
        }
        subLevel.logicalPose().position().set(subLevelCenter.x, subLevelCenter.y, subLevelCenter.z);
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        PhysicsPipeline pipeline = physicsSystem.getPipeline();
        if (containingSubLevel != null) {
            SubLevelAssemblyHelper.kickFromContainingSubLevel(level, physicsSystem, pipeline, subLevel, containingSubLevel);
        }
        pipeline.teleport(subLevel, (Vector3dc)subLevel.logicalPose().position(), (Quaterniondc)subLevel.logicalPose().orientation());
        subLevel.updateLastPose();
        SubLevelAssemblyHelper.moveTrackingPoints(level, bounds, subLevel, transform);
        return subLevel;
    }

    @ApiStatus.Internal
    public static void kickFromContainingSubLevel(ServerLevel level, SubLevelPhysicsSystem physicsSystem, PhysicsPipeline pipeline, ServerSubLevel subLevel, SubLevel containingSubLevel) {
        Pose3d originalPose = new Pose3d((Pose3dc)subLevel.logicalPose());
        Vector3d velocity = Sable.HELPER.getVelocity((Level)level, (Vector3dc)subLevel.logicalPose().position(), new Vector3d());
        RigidBodyHandle containingHandle = physicsSystem.getPhysicsHandle((ServerSubLevel)containingSubLevel);
        pipeline.addLinearAndAngularVelocity(subLevel, (Vector3dc)velocity, containingHandle.getAngularVelocity());
        Pose3d containingPose = containingSubLevel.logicalPose();
        containingPose.transformPosition(subLevel.logicalPose().position());
        subLevel.setSplitFrom((ServerSubLevel)containingSubLevel, originalPose);
    }

    @NotNull
    public static GatherResult gatherConnectedBlocks(BlockPos gatherOrigin, ServerLevel level, int maximumBlocksToAssemble, @Nullable FrontierPredicate frontierPredicate) {
        LinkedHashSet<Pair> frontier = new LinkedHashSet<Pair>(4096);
        ObjectOpenHashSet blocks = new ObjectOpenHashSet(1024);
        LevelAccelerator accelerator = new LevelAccelerator((Level)level);
        BlockState gatherOriginState = accelerator.getBlockState(gatherOrigin);
        if (gatherOriginState.isAir()) {
            return new GatherResult(null, 0, null, GatherResult.State.NO_BLOCKS);
        }
        frontier.add(Pair.of((Object)gatherOrigin, (Object)gatherOriginState));
        int minX = gatherOrigin.getX();
        int minY = gatherOrigin.getY();
        int minZ = gatherOrigin.getZ();
        int maxX = gatherOrigin.getX();
        int maxY = gatherOrigin.getY();
        int maxZ = gatherOrigin.getZ();
        int blockCount = 0;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        while (!frontier.isEmpty()) {
            Pair pair = (Pair)frontier.removeFirst();
            BlockPos pos = (BlockPos)pair.key();
            if (++blockCount > maximumBlocksToAssemble) {
                return new GatherResult(null, blockCount, null, GatherResult.State.TOO_MANY_BLOCKS);
            }
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
            blocks.add(pos);
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; ++z) {
                        BlockPos.MutableBlockPos candidate;
                        int absTotal;
                        if (x == 0 && y == 0 && z == 0 || (absTotal = Math.abs(x) + Math.abs(y) + Math.abs(z)) == 3 || frontier.contains(candidate = mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z))) continue;
                        Direction direction = absTotal == 1 ? Direction.fromDelta((int)x, (int)y, (int)z) : null;
                        BlockState candidateState = accelerator.getBlockState((BlockPos)candidate);
                        if (candidateState.isAir() || frontierPredicate != null && !frontierPredicate.isValidConnection(pos, (BlockState)pair.second(), (BlockPos)candidate, candidateState, direction) || blocks.contains(candidate)) continue;
                        frontier.add(Pair.of((Object)candidate.immutable(), (Object)candidateState));
                    }
                }
            }
        }
        BoundingBox3i bounds = new BoundingBox3i(minX, minY, minZ, maxX, maxY, maxZ);
        if (blocks.isEmpty()) {
            return new GatherResult(null, blockCount, null, GatherResult.State.NO_BLOCKS);
        }
        return new GatherResult((Set<BlockPos>)blocks, blockCount, bounds, GatherResult.State.SUCCESS);
    }

    public static void moveTrackingPoints(ServerLevel level, BoundingBox3ic bounds, ServerSubLevel subLevel, AssemblyTransform transform) {
        SubLevelTrackingPointSavedData data = SubLevelTrackingPointSavedData.getOrLoad(level);
        Iterable<Pair<UUID, TrackingPoint>> points = data.getAllTrackingPoints(bounds);
        for (Pair<UUID, TrackingPoint> entry : points) {
            UUID key = (UUID)entry.key();
            TrackingPoint point = new TrackingPoint(subLevel != null, subLevel != null ? subLevel.getUniqueId() : null, subLevel != null ? subLevel.getLastSerializationPointer() : null, JOMLConversion.toJOML((Position)transform.apply(JOMLConversion.toMojang((Vector3dc)((TrackingPoint)entry.value()).point()))), ((TrackingPoint)entry.value()).globalPlaceholderPosition());
            data.setTrackingPoint(key, point);
        }
    }

    public static void moveOtherStuff(ServerLevel level, AssemblyTransform transform, Iterable<BlockPos> blocks, BoundingBox3ic bounds) {
        List entities = level.getEntitiesOfClass(Entity.class, bounds.toAABB().inflate(2.0));
        boolean needsBitSet = SubLevelAssemblyHelper.needsBitSet(level, bounds, entities);
        if (!needsBitSet) {
            return;
        }
        BoundedBitVolume3i volume = BoundedBitVolume3i.fromBlocks(blocks);
        assert (volume != null);
        for (Entity entity : entities) {
            boolean moveEntity = false;
            if (entity instanceof HangingEntity) {
                HangingEntity hangingEntity = (HangingEntity)entity;
                moveEntity = BlockPos.betweenClosedStream((AABB)hangingEntity.calculateSupportBox()).anyMatch(blockPos -> volume.getOccupied(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            }
            if (!moveEntity) continue;
            entity.setPos(transform.apply(entity.position()));
        }
    }

    private static boolean needsBitSet(ServerLevel level, BoundingBox3ic bounds, List<Entity> entities) {
        return !entities.isEmpty();
    }

    public static void moveBlocks(ServerLevel level, AssemblyTransform transform, Iterable<BlockPos> blocks) {
        BlockState subLevelState;
        ServerLevel resultingLevel = transform.resultingLevel;
        LevelAccelerator accelerator = new LevelAccelerator((Level)level);
        LevelAccelerator resultingAccelerator = new LevelAccelerator((Level)resultingLevel);
        ArrayList<BlockState> states = new ArrayList<BlockState>();
        BlockPos firstBlock = null;
        Vector2i chunkBoundsMin = null;
        Vector2i chunkBoundsMax = null;
        for (BlockPos block : blocks) {
            if (firstBlock == null) {
                firstBlock = block;
            }
            ChunkPos chunk = new ChunkPos(transform.apply(block));
            Vector2i jomlChunkPos = new Vector2i(chunk.x, chunk.z);
            if (chunkBoundsMin == null) {
                chunkBoundsMin = new Vector2i((Vector2ic)jomlChunkPos);
                chunkBoundsMax = new Vector2i((Vector2ic)jomlChunkPos);
            }
            chunkBoundsMin.min((Vector2ic)jomlChunkPos);
            chunkBoundsMax.max((Vector2ic)jomlChunkPos);
        }
        SubLevel subLevel = Sable.HELPER.getContaining((Level)level, (Vec3i)transform.apply(firstBlock));
        if (subLevel != null) {
            LevelPlot plot = subLevel.getPlot();
            for (int chunkX = chunkBoundsMin.x; chunkX <= chunkBoundsMax.x; ++chunkX) {
                for (int chunkZ = chunkBoundsMin.y; chunkZ <= chunkBoundsMax.y; ++chunkZ) {
                    if (plot.getChunkHolder(plot.toLocal(new ChunkPos(chunkX, chunkZ))) != null) continue;
                    plot.newEmptyChunk(new ChunkPos(chunkX, chunkZ));
                }
            }
        }
        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace((Level)resultingLevel, true);
        for (BlockPos block : blocks) {
            BlockState state = accelerator.getBlockState(block);
            BlockPos newPos = transform.apply(block);
            try {
                Block block2;
                BlockState subLevelState2 = transform.apply(state);
                Block block3 = state.getBlock();
                if (block3 instanceof BlockSubLevelAssemblyListener) {
                    BlockSubLevelAssemblyListener listener = (BlockSubLevelAssemblyListener)block3;
                    listener.beforeMove(level, resultingLevel, state, block, newPos);
                }
                BlockEntity blockEntity = level.getBlockEntity(block);
                CompoundTag tag = null;
                if (blockEntity != null) {
                    tag = blockEntity.saveWithFullMetadata((HolderLookup.Provider)level.registryAccess());
                    tag.putInt("x", newPos.getX());
                    tag.putInt("y", newPos.getY());
                    tag.putInt("z", newPos.getZ());
                }
                if (blockEntity instanceof RandomizableContainer) {
                    RandomizableContainer container = (RandomizableContainer)blockEntity;
                    container.setLootTable(null);
                }
                if (blockEntity instanceof Clearable) {
                    Clearable clearable = (Clearable)blockEntity;
                    clearable.clearContent();
                }
                LevelChunk chunk = resultingAccelerator.getChunk(SectionPos.blockToSectionCoord((int)newPos.getX()), SectionPos.blockToSectionCoord((int)newPos.getZ()));
                chunk.setBlockState(newPos, subLevelState2, true);
                states.add(subLevelState2);
                BlockEntity newBlockEntity = resultingLevel.getBlockEntity(newPos);
                if (newBlockEntity != null && tag != null) {
                    newBlockEntity.loadWithComponents(tag, (HolderLookup.Provider)level.registryAccess());
                }
                if (!((block2 = state.getBlock()) instanceof BlockSubLevelAssemblyListener)) continue;
                BlockSubLevelAssemblyListener listener = (BlockSubLevelAssemblyListener)block2;
                listener.afterMove(level, resultingLevel, state, block, newPos);
            }
            catch (Exception e) {
                Sable.LOGGER.error("Failed to move block {} at {} to {}", new Object[]{state, block, newPos, e});
            }
        }
        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace((Level)resultingLevel, false);
        int i = 0;
        for (BlockPos untransformed : blocks) {
            BlockPos pos = transform.apply(untransformed);
            try {
                LevelChunk levelchunk = resultingAccelerator.getChunk(SectionPos.blockToSectionCoord((int)pos.getX()), SectionPos.blockToSectionCoord((int)pos.getZ()));
                BlockState subLevelState3 = (BlockState)states.get(i);
                SubLevelAssemblyHelper.markAndNotifyBlock((Level)resultingLevel, pos, levelchunk, Blocks.AIR.defaultBlockState(), subLevelState3, 3, 512);
            }
            catch (Exception e) {
                Sable.LOGGER.error("Failed to mark & notify block {} (untransformed = {})", new Object[]{pos, untransformed, e});
            }
            ++i;
        }
        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace((Level)resultingLevel, true);
        for (BlockPos block : blocks) {
            subLevelState = Blocks.AIR.defaultBlockState();
            try {
                LevelChunk chunk = accelerator.getChunk(SectionPos.blockToSectionCoord((int)block.getX()), SectionPos.blockToSectionCoord((int)block.getZ()));
                chunk.setBlockState(block, subLevelState, true);
            }
            catch (Exception e) {
                Sable.LOGGER.error("Failed to destroy old block during assembly {}", (Object)block, (Object)e);
            }
        }
        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace((Level)resultingLevel, false);
        for (BlockPos block : blocks) {
            subLevelState = Blocks.AIR.defaultBlockState();
            resultingLevel.sendBlockUpdated(block, Blocks.STONE.defaultBlockState(), subLevelState, 3);
        }
    }

    public static void markAndNotifyBlock(Level level, BlockPos pPos, @Nullable LevelChunk levelchunk, BlockState oldState, BlockState newState, int pFlags, int pRecursionLeft) {
        Block block = newState.getBlock();
        BlockState worldState = level.getBlockState(pPos);
        if (worldState == newState) {
            if (oldState != worldState) {
                level.setBlocksDirty(pPos, oldState, worldState);
            }
            if ((pFlags & 2) != 0 && levelchunk.getFullStatus() != null && levelchunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING)) {
                level.sendBlockUpdated(pPos, oldState, newState, pFlags);
            }
            if ((pFlags & 1) != 0) {
                level.blockUpdated(pPos, oldState.getBlock());
                if (newState.hasAnalogOutputSignal()) {
                    level.updateNeighbourForOutputSignal(pPos, block);
                }
            }
            if ((pFlags & 0x10) == 0 && pRecursionLeft > 0) {
                int i = pFlags & 0xFFFFFFDE;
                oldState.updateIndirectNeighbourShapes((LevelAccessor)level, pPos, i, pRecursionLeft - 1);
                newState.updateNeighbourShapes((LevelAccessor)level, pPos, i, pRecursionLeft - 1);
                newState.updateIndirectNeighbourShapes((LevelAccessor)level, pPos, i, pRecursionLeft - 1);
            }
            level.onBlockStateChange(pPos, oldState, worldState);
        }
    }

    public static class AssemblyTransform {
        private final BlockPos anchorPos;
        private final BlockPos resultingAnchorPos;
        private final int angle;
        private final Rotation rotation;
        private final ServerLevel resultingLevel;

        public AssemblyTransform(BlockPos anchorPos, BlockPos resultingAnchorPos, int angle, Rotation rotation, ServerLevel resultingLevel) {
            this.anchorPos = anchorPos;
            this.resultingAnchorPos = resultingAnchorPos;
            this.angle = angle;
            this.rotation = rotation;
            this.resultingLevel = resultingLevel;
        }

        public Vec3 apply(Vec3 pos) {
            pos = pos.subtract(this.anchorPos.getCenter()).yRot((float)((double)this.angle * Math.PI / 2.0)).add(this.resultingAnchorPos.getCenter());
            return pos;
        }

        public BlockPos apply(BlockPos pos) {
            return BlockPos.containing((Position)this.apply(pos.getCenter()));
        }

        public BlockState apply(BlockState state) {
            Block block = state.getBlock();
            if (block instanceof BellBlock) {
                if (state.getValue((Property)BlockStateProperties.BELL_ATTACHMENT) == BellAttachType.DOUBLE_WALL) {
                    state = (BlockState)state.setValue((Property)BlockStateProperties.BELL_ATTACHMENT, (Comparable)BellAttachType.SINGLE_WALL);
                }
                return (BlockState)state.setValue((Property)BellBlock.FACING, (Comparable)this.rotation.rotate((Direction)state.getValue((Property)BellBlock.FACING)));
            }
            return state.rotate(this.rotation);
        }

        public ServerLevel getLevel() {
            return this.resultingLevel;
        }

        public Rotation getRotation() {
            return this.rotation;
        }
    }

    public record GatherResult(@Nullable Set<BlockPos> blocks, int checkedBlocks, @Nullable BoundingBox3i boundingBox, State assemblyState) {

        public static enum State {
            SUCCESS("commands.sable.sub_level.assemble.connected.success"),
            TOO_MANY_BLOCKS("commands.sable.sub_level.assemble.connected.too_many_blocks"),
            NO_BLOCKS("commands.sable.sub_level.assemble.no_blocks");

            public final String errorKey;

            private State(String errorKey) {
                this.errorKey = errorKey;
            }
        }
    }

    @FunctionalInterface
    public static interface FrontierPredicate {
        public boolean isValidConnection(BlockPos var1, BlockState var2, BlockPos var3, BlockState var4, @Nullable Direction var5);
    }
}
