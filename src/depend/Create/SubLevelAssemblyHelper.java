//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
import dev.ryanhcode.sable.platform.SableAssemblyPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
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
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelAssemblyHelper {
    public static ServerSubLevel assembleBlocks(ServerLevel level, BlockPos anchor, Iterable<BlockPos> blocks, BoundingBox3ic bounds) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);

        assert container != null;

        SubLevel containingSubLevel = Sable.HELPER.getContaining(level, anchor);
        Pose3d pose = new Pose3d();
        pose.position().set((double)anchor.getX() + (double)0.5F, (double)anchor.getY() + (double)0.5F, (double)anchor.getZ() + (double)0.5F);
        if (containingSubLevel != null) {
            Pose3d containingPose = containingSubLevel.logicalPose();
            containingPose.transformPosition(pose.position());
            pose.orientation().set(containingPose.orientation());
        }

        ServerSubLevel subLevel = (ServerSubLevel)container.allocateNewSubLevel(pose);
        LevelPlot plot = subLevel.getPlot();
        plot.newEmptyChunk(plot.getCenterChunk());
        BlockPos plotAnchor = plot.getCenterBlock();
        AssemblyTransform transform = new AssemblyTransform(anchor, plotAnchor, 0, Rotation.NONE, level);
        moveOtherStuff(level, transform, blocks, bounds);
        moveBlocks(level, transform, blocks);
        Vector3dc centerOfMass = subLevel.getMassTracker().getCenterOfMass();
        Vec3 subLevelCenter = Vec3.atLowerCornerOf(anchor);
        if (centerOfMass != null) {
            subLevelCenter = subLevelCenter.subtract(Vec3.atLowerCornerOf(plotAnchor)).add(centerOfMass.x(), centerOfMass.y(), centerOfMass.z());
        } else {
            subLevel.logicalPose().rotationPoint().set((double)plotAnchor.getX() + (double)0.5F, (double)plotAnchor.getY() + (double)0.5F, (double)plotAnchor.getZ() + (double)0.5F);
        }

        subLevel.logicalPose().position().set(subLevelCenter.x, subLevelCenter.y, subLevelCenter.z);
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        PhysicsPipeline pipeline = physicsSystem.getPipeline();
        if (containingSubLevel != null) {
            kickFromContainingSubLevel(level, physicsSystem, pipeline, subLevel, containingSubLevel);
        }

        pipeline.teleport(subLevel, subLevel.logicalPose().position(), subLevel.logicalPose().orientation());
        subLevel.updateLastPose();
        moveTrackingPoints(level, bounds, subLevel, transform);
        return subLevel;
    }

    @Internal
    public static void kickFromContainingSubLevel(ServerLevel level, SubLevelPhysicsSystem physicsSystem, PhysicsPipeline pipeline, ServerSubLevel subLevel, SubLevel containingSubLevel) {
        Pose3d originalPose = new Pose3d(subLevel.logicalPose());
        Vector3d velocity = Sable.HELPER.getVelocity(level, subLevel.logicalPose().position(), new Vector3d());
        RigidBodyHandle containingHandle = physicsSystem.getPhysicsHandle((ServerSubLevel)containingSubLevel);
        pipeline.addLinearAndAngularVelocity(subLevel, velocity, containingHandle.getAngularVelocity());
        Pose3d containingPose = containingSubLevel.logicalPose();
        containingPose.transformPosition(subLevel.logicalPose().position());
        subLevel.setSplitFrom((ServerSubLevel)containingSubLevel, originalPose);
    }

    public static @NotNull GatherResult gatherConnectedBlocks(BlockPos gatherOrigin, ServerLevel level, int maximumBlocksToAssemble, @Nullable FrontierPredicate frontierPredicate) {
        LinkedHashSet<Pair<BlockPos, BlockState>> frontier = new LinkedHashSet(4096);
        Set<BlockPos> blocks = new ObjectOpenHashSet(1024);
        LevelAccelerator accelerator = new LevelAccelerator(level);
        BlockState gatherOriginState = accelerator.getBlockState(gatherOrigin);
        if (gatherOriginState.isAir()) {
            return new GatherResult((Set)null, 0, (BoundingBox3i)null, SubLevelAssemblyHelper.GatherResult.State.NO_BLOCKS);
        } else {
            frontier.add(Pair.of(gatherOrigin, gatherOriginState));
            int minX = gatherOrigin.getX();
            int minY = gatherOrigin.getY();
            int minZ = gatherOrigin.getZ();
            int maxX = gatherOrigin.getX();
            int maxY = gatherOrigin.getY();
            int maxZ = gatherOrigin.getZ();
            int blockCount = 0;
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            while(!frontier.isEmpty()) {
                Pair<BlockPos, BlockState> pair = (Pair)frontier.removeFirst();
                BlockPos pos = (BlockPos)pair.key();
                ++blockCount;
                if (blockCount > maximumBlocksToAssemble) {
                    return new GatherResult((Set)null, blockCount, (BoundingBox3i)null, SubLevelAssemblyHelper.GatherResult.State.TOO_MANY_BLOCKS);
                }

                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                minZ = Math.min(minZ, pos.getZ());
                maxX = Math.max(maxX, pos.getX());
                maxY = Math.max(maxY, pos.getY());
                maxZ = Math.max(maxZ, pos.getZ());
                blocks.add(pos);

                for(int x = -1; x <= 1; ++x) {
                    for(int y = -1; y <= 1; ++y) {
                        for(int z = -1; z <= 1; ++z) {
                            if (x != 0 || y != 0 || z != 0) {
                                int absTotal = Math.abs(x) + Math.abs(y) + Math.abs(z);
                                if (absTotal != 3) {
                                    BlockPos candidate = mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                                    if (!frontier.contains(candidate)) {
                                        Direction direction = absTotal == 1 ? Direction.fromDelta(x, y, z) : null;
                                        BlockState candidateState = accelerator.getBlockState(candidate);
                                        if (!candidateState.isAir() && (frontierPredicate == null || frontierPredicate.isValidConnection(pos, (BlockState)pair.second(), candidate, candidateState, direction)) && !blocks.contains(candidate)) {
                                            frontier.add(Pair.of(candidate.immutable(), candidateState));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            BoundingBox3i bounds = new BoundingBox3i(minX, minY, minZ, maxX, maxY, maxZ);
            if (blocks.isEmpty()) {
                return new GatherResult((Set)null, blockCount, (BoundingBox3i)null, SubLevelAssemblyHelper.GatherResult.State.NO_BLOCKS);
            } else {
                return new GatherResult(blocks, blockCount, bounds, SubLevelAssemblyHelper.GatherResult.State.SUCCESS);
            }
        }
    }

    public static void moveTrackingPoints(ServerLevel level, BoundingBox3ic bounds, ServerSubLevel subLevel, AssemblyTransform transform) {
        SubLevelTrackingPointSavedData data = SubLevelTrackingPointSavedData.getOrLoad(level);

        for(Pair<UUID, TrackingPoint> entry : data.getAllTrackingPoints(bounds)) {
            UUID key = (UUID)entry.key();
            TrackingPoint point = new TrackingPoint(subLevel != null, subLevel != null ? subLevel.getUniqueId() : null, subLevel != null ? subLevel.getLastSerializationPointer() : null, JOMLConversion.toJOML(transform.apply(JOMLConversion.toMojang(((TrackingPoint)entry.value()).point()))), ((TrackingPoint)entry.value()).globalPlaceholderPosition());
            data.setTrackingPoint(key, point);
        }

    }

    public static void moveOtherStuff(ServerLevel level, AssemblyTransform transform, Iterable<BlockPos> blocks, BoundingBox3ic bounds) {
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, bounds.toAABB().inflate((double)2.0F));
        boolean needsBitSet = needsBitSet(level, bounds, entities);
        if (needsBitSet) {
            BoundedBitVolume3i volume = BoundedBitVolume3i.fromBlocks(blocks);

            assert volume != null;

            for(Entity entity : entities) {
                boolean moveEntity = false;
                if (entity instanceof HangingEntity) {
                    HangingEntity hangingEntity = (HangingEntity)entity;
                    moveEntity = BlockPos.betweenClosedStream(hangingEntity.calculateSupportBox()).anyMatch((blockPos) -> volume.getOccupied(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                }

                if (moveEntity) {
                    entity.setPos(transform.apply(entity.position()));
                }
            }

        }
    }

    private static boolean needsBitSet(ServerLevel level, BoundingBox3ic bounds, List<Entity> entities) {
        return !entities.isEmpty();
    }

    public static void moveBlocks(ServerLevel level, AssemblyTransform transform, Iterable<BlockPos> blocks) {
        ServerLevel resultingLevel = transform.resultingLevel;
        LevelAccelerator accelerator = new LevelAccelerator(level);
        LevelAccelerator resultingAccelerator = new LevelAccelerator(resultingLevel);
        List<BlockState> states = new ArrayList();
        BlockPos firstBlock = null;
        Vector2i chunkBoundsMin = null;
        Vector2i chunkBoundsMax = null;

        for(BlockPos block : blocks) {
            if (firstBlock == null) {
                firstBlock = block;
            }

            ChunkPos chunk = new ChunkPos(transform.apply(block));
            Vector2i jomlChunkPos = new Vector2i(chunk.x, chunk.z);
            if (chunkBoundsMin == null) {
                chunkBoundsMin = new Vector2i(jomlChunkPos);
                chunkBoundsMax = new Vector2i(jomlChunkPos);
            }

            chunkBoundsMin.min(jomlChunkPos);
            chunkBoundsMax.max(jomlChunkPos);
        }

        SubLevel subLevel = Sable.HELPER.getContaining(level, transform.apply(firstBlock));
        if (subLevel != null) {
            LevelPlot plot = subLevel.getPlot();

            for(int chunkX = chunkBoundsMin.x; chunkX <= chunkBoundsMax.x; ++chunkX) {
                for(int chunkZ = chunkBoundsMin.y; chunkZ <= chunkBoundsMax.y; ++chunkZ) {
                    if (plot.getChunkHolder(plot.toLocal(new ChunkPos(chunkX, chunkZ))) == null) {
                        plot.newEmptyChunk(new ChunkPos(chunkX, chunkZ));
                    }
                }
            }
        }

        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace(resultingLevel, true);

        for(BlockPos block : blocks) {
            BlockState state = accelerator.getBlockState(block);
            BlockPos newPos = transform.apply(block);

            try {
                BlockState subLevelState = transform.apply(state);
                Block var17 = state.getBlock();
                if (var17 instanceof BlockSubLevelAssemblyListener listener) {
                    listener.beforeMove(level, resultingLevel, state, block, newPos);
                }

                BlockEntity blockEntity = level.getBlockEntity(block);
                CompoundTag tag = null;
                if (blockEntity != null) {
                    tag = blockEntity.saveWithFullMetadata(level.registryAccess());
                    tag.putInt("x", newPos.getX());
                    tag.putInt("y", newPos.getY());
                    tag.putInt("z", newPos.getZ());
                }

                if (blockEntity instanceof RandomizableContainer container) {
                    container.setLootTable((ResourceKey)null);
                }

                if (blockEntity instanceof Clearable clearable) {
                    clearable.clearContent();
                }

                LevelChunk chunk = resultingAccelerator.getChunk(SectionPos.blockToSectionCoord(newPos.getX()), SectionPos.blockToSectionCoord(newPos.getZ()));
                chunk.setBlockState(newPos, subLevelState, true);
                states.add(subLevelState);
                BlockEntity newBlockEntity = resultingLevel.getBlockEntity(newPos);
                if (newBlockEntity != null && tag != null) {
                    newBlockEntity.loadWithComponents(tag, level.registryAccess());
                }

                Block var21 = state.getBlock();
                if (var21 instanceof BlockSubLevelAssemblyListener listener) {
                    listener.afterMove(level, resultingLevel, state, block, newPos);
                }
            } catch (Exception e) {
                Sable.LOGGER.error("Failed to move block {} at {} to {}", new Object[]{state, block, newPos, e});
            }
        }

        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace(resultingLevel, false);
        int i = 0;

        for(BlockPos untransformed : blocks) {
            BlockPos pos = transform.apply(untransformed);

            try {
                LevelChunk levelchunk = resultingAccelerator.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
                BlockState subLevelState = (BlockState)states.get(i);
                markAndNotifyBlock(resultingLevel, pos, levelchunk, Blocks.AIR.defaultBlockState(), subLevelState, 3, 512);
            } catch (Exception e) {
                Sable.LOGGER.error("Failed to mark & notify block {} (untransformed = {})", new Object[]{pos, untransformed, e});
            }

            ++i;
        }

        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace(resultingLevel, true);

        for(BlockPos block : blocks) {
            BlockState subLevelState = Blocks.AIR.defaultBlockState();

            try {
                LevelChunk chunk = accelerator.getChunk(SectionPos.blockToSectionCoord(block.getX()), SectionPos.blockToSectionCoord(block.getZ()));
                chunk.setBlockState(block, subLevelState, true);
            } catch (Exception e) {
                Sable.LOGGER.error("Failed to destroy old block during assembly {}", block, e);
            }
        }

        SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace(resultingLevel, false);

        for(BlockPos block : blocks) {
            BlockState subLevelState = Blocks.AIR.defaultBlockState();
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

            if ((pFlags & 16) == 0 && pRecursionLeft > 0) {
                int i = pFlags & -34;
                oldState.updateIndirectNeighbourShapes(level, pPos, i, pRecursionLeft - 1);
                newState.updateNeighbourShapes(level, pPos, i, pRecursionLeft - 1);
                newState.updateIndirectNeighbourShapes(level, pPos, i, pRecursionLeft - 1);
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
            pos = pos.subtract(this.anchorPos.getCenter()).yRot((float)((double)this.angle * Math.PI / (double)2.0F)).add(this.resultingAnchorPos.getCenter());
            return pos;
        }

        public BlockPos apply(BlockPos pos) {
            return BlockPos.containing(this.apply(pos.getCenter()));
        }

        public BlockState apply(BlockState state) {
            Block block = state.getBlock();
            if (block instanceof BellBlock) {
                if (state.getValue(BlockStateProperties.BELL_ATTACHMENT) == BellAttachType.DOUBLE_WALL) {
                    state = (BlockState)state.setValue(BlockStateProperties.BELL_ATTACHMENT, BellAttachType.SINGLE_WALL);
                }

                return (BlockState)state.setValue(BellBlock.FACING, this.rotation.rotate((Direction)state.getValue(BellBlock.FACING)));
            } else {
                return state.rotate(this.rotation);
            }
        }

        public ServerLevel getLevel() {
            return this.resultingLevel;
        }

        public Rotation getRotation() {
            return this.rotation;
        }
    }

    public static record GatherResult(@Nullable Set<BlockPos> blocks, int checkedBlocks, @Nullable BoundingBox3i boundingBox, State assemblyState) {
        public static enum State {
            SUCCESS("commands.sable.sub_level.assemble.connected.success"),
            TOO_MANY_BLOCKS("commands.sable.sub_level.assemble.connected.too_many_blocks"),
            NO_BLOCKS("commands.sable.sub_level.assemble.no_blocks");

            public final String errorKey;

            private State(final String errorKey) {
                this.errorKey = errorKey;
            }
        }
    }

    @FunctionalInterface
    public interface FrontierPredicate {
        boolean isValidConnection(BlockPos var1, BlockState var2, BlockPos var3, BlockState var4, @Nullable Direction var5);
    }
}
