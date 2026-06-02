/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.CrashReport
 *  net.minecraft.ReportedException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.impl.rapier;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.mass.MassTracker;
import dev.ryanhcode.sable.api.physics.object.box.BoxHandle;
import dev.ryanhcode.sable.api.physics.object.box.BoxPhysicsObject;
import dev.ryanhcode.sable.api.physics.object.rope.RopeHandle;
import dev.ryanhcode.sable.api.physics.object.rope.RopePhysicsObject;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import dev.ryanhcode.sable.physics.config.PhysicsConfigData;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import dev.ryanhcode.sable.physics.impl.rapier.box.RapierBoxHandle;
import dev.ryanhcode.sable.physics.impl.rapier.collider.RapierVoxelColliderBakery;
import dev.ryanhcode.sable.physics.impl.rapier.collider.RapierVoxelColliderData;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.fixed.RapierFixedConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.free.RapierFreeConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.generic.RapierGenericConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.rotary.RapierRotaryConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.rope.RapierRopeHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.util.LevelAccelerator;
import dev.ryanhcode.sable.util.SableMathUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RapierPhysicsPipeline
implements PhysicsPipeline {
    private static final double DISTANCE_THRESHOLD = 1.0E-7;
    private static final double ANGULAR_THRESHOLD = 1.0E-7;
    private final ServerLevel level;
    private final LevelAccelerator accelerator;
    private final RapierVoxelColliderBakery colliderBakery;
    private final Int2ObjectMap<ServerSubLevel> activeSubLevels = new Int2ObjectArrayMap();
    private final Object2ObjectMap<KinematicContraption, TrackedKinematicContraption> activeContraptions = new Object2ObjectOpenHashMap();
    private final Long2LongOpenHashMap recentCollisions = new Long2LongOpenHashMap();
    private final int sceneId;
    private final double[] cache;

    public RapierPhysicsPipeline(ServerLevel level) {
        this.level = level;
        this.accelerator = new LevelAccelerator((Level)level);
        this.colliderBakery = new RapierVoxelColliderBakery(this.accelerator);
        this.recentCollisions.defaultReturnValue(-1L);
        this.sceneId = Rapier3D.getID(this.level);
        this.cache = new double[7];
    }

    private static int packBlockState(VoxelNeighborhoodState state, int colliderID) {
        return state.byteRepresentation() | colliderID << 16;
    }

    @Override
    public void init(Vector3dc gravity, double universalDrag) {
        try {
            Rapier3D.initialize(this.sceneId, gravity.x(), gravity.y(), gravity.z(), universalDrag);
        }
        catch (UnsatisfiedLinkError e) {
            Sable.LOGGER.error("Sable has failed to link with the natives for its Rapier pipeline. Please report with system details to https://github.com/ryanhcode/sable/issues");
            CrashReport crashReport = CrashReport.forThrowable((Throwable)e, (String)"Sable linking with Rapier natives");
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public void dispose() {
        Rapier3D.dispose();
    }

    @Override
    public void prePhysicsTicks() {
        double timeStep = 0.05;
        Rapier3D.tick(this.sceneId, 0.05);
    }

    @Override
    public void physicsTick(double timeStep) {
        this.updateContraptionPoses();
        Rapier3D.step(this.sceneId, timeStep);
    }

    private void updateContraptionPoses() {
        for (KinematicContraption contraption : this.activeContraptions.keySet()) {
            TrackedKinematicContraption trackedContraption = (TrackedKinematicContraption)this.activeContraptions.get((Object)contraption);
            SubLevelPhysicsSystem system = SubLevelPhysicsSystem.require((Level)this.level);
            double partialPhysicsTick = system.getPartialPhysicsTick();
            SubLevel mountSubLevel = Sable.HELPER.getContaining((Level)this.level, contraption.sable$getPosition());
            Vector3dc parentCenterOfMass = mountSubLevel != null ? ((ServerSubLevel)mountSubLevel).getMassTracker().getCenterOfMass() : JOMLConversion.ZERO;
            Vector3d lastPosition = new Vector3d(contraption.sable$getPosition(partialPhysicsTick - 1.0));
            Quaterniond lastOrientation = new Quaterniond((Quaterniondc)contraption.sable$getOrientation(partialPhysicsTick - 1.0));
            Vector3d pos = new Vector3d(contraption.sable$getPosition(partialPhysicsTick));
            Quaterniond rot = contraption.sable$getOrientation(partialPhysicsTick);
            Vector3d linVel = pos.sub((Vector3dc)lastPosition, new Vector3d());
            Vector3d angVel = SableMathUtils.getAngularVelocity((Quaterniondc)lastOrientation, (Quaterniondc)rot, new Vector3d());
            linVel.mul(20.0);
            angVel.mul(20.0);
            rot.transformInverse(linVel);
            rot.transformInverse(angVel);
            pos.sub(parentCenterOfMass);
            if (!(pos.distanceSquared((Vector3dc)trackedContraption.lastUploadedPosition()) > 9.999999999999998E-15 || linVel.distanceSquared((Vector3dc)trackedContraption.lastUploadedLinVel()) > 9.999999999999998E-15 || angVel.distanceSquared((Vector3dc)trackedContraption.lastUploadedAngVel()) > 9.999999999999998E-15)) {
                Quaterniond quaterniond = new Quaterniond();
                if (!(rot.div((Quaterniondc)trackedContraption.lastUploadedOrientation(), quaterniond).angle() > 9.999999999999998E-15)) continue;
            }
            MassTracker massTracker = contraption.sable$getMassTracker();
            Vector3dc centerOfMass = massTracker.getCenterOfMass();
            double[] centerOfMassArray = new double[]{centerOfMass.x(), centerOfMass.y(), centerOfMass.z()};
            double[] poseArray = new double[]{pos.x(), pos.y(), pos.z(), rot.x(), rot.y(), rot.z(), rot.w()};
            double[] velocityArray = new double[]{linVel.x(), linVel.y(), linVel.z(), angVel.x(), angVel.y(), angVel.z()};
            Rapier3D.setKinematicContraptionTransform(this.sceneId, trackedContraption.id(), centerOfMassArray, poseArray, velocityArray);
            trackedContraption.lastUploadedPosition().set((Vector3dc)pos);
            trackedContraption.lastUploadedLinVel().set((Vector3dc)linVel);
            trackedContraption.lastUploadedAngVel().set((Vector3dc)angVel);
            trackedContraption.lastUploadedOrientation().set((Quaterniondc)rot);
        }
    }

    @Override
    public void postPhysicsTicks() {
        this.processCollisionEffects();
    }

    private void processCollisionEffects() {
        this.recentCollisions.long2LongEntrySet().removeIf(entry -> this.level.getGameTime() - entry.getLongValue() > 2L);
        Vector3d localPointA = new Vector3d();
        Vector3d localPointB = new Vector3d();
        Vector3d localNormalA = new Vector3d();
        Vector3d localNormalB = new Vector3d();
        Vector3d globalPointA = new Vector3d();
        Vector3d globalPointB = new Vector3d();
        double[] collisions = Rapier3D.clearCollisions(this.sceneId);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos cornerPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < collisions.length / 15; ++i) {
            long exists;
            Pose3d pose;
            BlockState stateA;
            int startIndex = i * 15;
            int idA = (int)collisions[startIndex];
            int idB = (int)collisions[startIndex + 1];
            double forceAmount = collisions[startIndex + 2];
            localNormalA.set(collisions[startIndex + 3], collisions[startIndex + 4], collisions[startIndex + 5]);
            localNormalB.set(collisions[startIndex + 6], collisions[startIndex + 7], collisions[startIndex + 8]);
            localPointA.set(collisions[startIndex + 9], collisions[startIndex + 10], collisions[startIndex + 11]);
            localPointB.set(collisions[startIndex + 12], collisions[startIndex + 13], collisions[startIndex + 14]);
            ServerSubLevel subLevelA = (ServerSubLevel)this.activeSubLevels.get(idA);
            ServerSubLevel subLevelB = (ServerSubLevel)this.activeSubLevels.get(idB);
            double minMass = Math.min(subLevelA != null ? subLevelA.getMassTracker().getMass() : Double.MAX_VALUE, subLevelB != null ? subLevelB.getMassTracker().getMass() : Double.MAX_VALUE);
            if (!(forceAmount > 25.0 * minMass)) continue;
            BlockState stateB = stateA = Blocks.STONE.defaultBlockState();
            if (subLevelA != null) {
                pose = subLevelA.logicalPose();
                pos.set(localPointA.x + pose.rotationPoint().x, localPointA.y + pose.rotationPoint().y, localPointA.z + pose.rotationPoint().z);
                cornerPos.set(localPointA.x + pose.rotationPoint().x + 0.5, localPointA.y + pose.rotationPoint().y + 0.5, localPointA.z + pose.rotationPoint().z + 0.5);
                exists = this.recentCollisions.put(cornerPos.asLong(), this.level.getGameTime());
                if (exists != -1L) continue;
                stateA = this.accelerator.getBlockState((BlockPos)pos);
            }
            if (subLevelB != null) {
                pose = subLevelB.logicalPose();
                pos.set(localPointB.x + pose.rotationPoint().x, localPointB.y + pose.rotationPoint().y, localPointB.z + pose.rotationPoint().z);
                cornerPos.set(localPointB.x + pose.rotationPoint().x + 0.5, localPointB.y + pose.rotationPoint().y + 0.5, localPointB.z + pose.rotationPoint().z + 0.5);
                exists = this.recentCollisions.put(cornerPos.asLong(), this.level.getGameTime());
                if (exists != -1L) continue;
                stateB = this.accelerator.getBlockState((BlockPos)pos);
            }
            globalPointA.set((Vector3dc)localPointA);
            globalPointB.set((Vector3dc)localPointB);
            if (subLevelA != null) {
                pose = subLevelA.logicalPose();
                pose.orientation().transform(globalPointA).add((Vector3dc)pose.position());
            }
            if (subLevelB != null) {
                pose = subLevelB.logicalPose();
                pose.orientation().transform(globalPointB).add((Vector3dc)pose.position());
            }
            BlockState state = stateB;
            this.level.sendParticles((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, state), globalPointA.x, globalPointA.y, globalPointA.z, 2, 0.0, 0.0, 0.0, 0.1);
            Vec3 position = JOMLConversion.toMojang((Vector3dc)globalPointA);
            float volumeScale = 0.4f;
            SoundType soundType = state.getSoundType();
            this.level.playSound(null, position.x, position.y, position.z, soundType.getStepSound(), SoundSource.BLOCKS, 0.080000006f, (float)(0.39999999999999997 + Math.random() * 0.4));
            this.level.playSound(null, position.x, position.y, position.z, soundType.getHitSound(), SoundSource.BLOCKS, 0.080000006f, (float)(Math.random() * 0.4));
            this.level.playSound(null, position.x, position.y, position.z, soundType.getPlaceSound(), SoundSource.BLOCKS, 0.080000006f, (float)(0.3 + Math.random() * 0.4));
        }
    }

    @Override
    public void tick() {
        this.accelerator.clearCache();
    }

    @Override
    public void add(ServerSubLevel subLevel, Pose3dc pose) {
        Vector3dc pos = pose.position();
        Quaterniondc rot = pose.orientation();
        subLevel.buildMassTracker();
        int id = Rapier3D.getID(subLevel);
        Rapier3D.createSubLevel(this.sceneId, id, new double[]{pos.x(), pos.y(), pos.z(), rot.x(), rot.y(), rot.z(), rot.w()});
        subLevel.updateMergedMassData(1.0f);
        Vector3dc centerOfMass = subLevel.getMassTracker().getCenterOfMass();
        if (centerOfMass != null) {
            subLevel.logicalPose().rotationPoint().set(centerOfMass);
            this.onStatsChanged(subLevel);
        }
        this.activeSubLevels.put(Rapier3D.getID(subLevel), (Object)subLevel);
    }

    @Override
    public void remove(ServerSubLevel subLevel) {
        Rapier3D.removeSubLevel(this.sceneId, Rapier3D.getID(subLevel));
        this.activeSubLevels.remove(Rapier3D.getID(subLevel));
    }

    @Override
    public void add(KinematicContraption contraption) {
        record UploadingContraptionChunk(int[] data) {
        }
        if (this.activeContraptions.containsKey((Object)contraption)) {
            throw new IllegalStateException("Contraption " + String.valueOf(contraption) + " is already present in pipeline");
        }
        int id = this.getNextRuntimeID();
        this.activeContraptions.put((Object)contraption, (Object)new TrackedKinematicContraption(new Vector3d(), new Quaterniond(), new Vector3d(), new Vector3d(), id));
        SubLevel mountSubLevel = Sable.HELPER.getContaining((Level)this.level, contraption.sable$getPosition());
        int mountId = mountSubLevel != null ? Rapier3D.getID((ServerSubLevel)mountSubLevel) : -1;
        BoundingBox3i localBounds = new BoundingBox3i();
        contraption.sable$getLocalBounds(localBounds);
        Vector3dc pos = contraption.sable$getPosition();
        Quaterniond rot = contraption.sable$getOrientation();
        double[] pose = new double[]{pos.x(), pos.y(), pos.z(), rot.x(), rot.y(), rot.z(), rot.w()};
        Rapier3D.createKinematicContraption(this.sceneId, mountId, id, pose);
        Long2ObjectOpenHashMap chunks = new Long2ObjectOpenHashMap();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int x = localBounds.minX(); x <= localBounds.maxX(); ++x) {
            for (int z = localBounds.minZ(); z <= localBounds.maxZ(); ++z) {
                for (int y = localBounds.minY(); y <= localBounds.maxY(); ++y) {
                    BlockState blockState = contraption.sable$blockGetter().getBlockState((BlockPos)blockPos.set(x, y, z));
                    if (blockState.isAir()) continue;
                    SectionPos sectionPos = SectionPos.of((BlockPos)blockPos);
                    UploadingContraptionChunk chunk = (UploadingContraptionChunk)chunks.computeIfAbsent(sectionPos.asLong(), longPos -> new UploadingContraptionChunk(new int[4096]));
                    VoxelNeighborhoodState state = VoxelNeighborhoodState.CORNER;
                    RapierVoxelColliderData colliderData = this.colliderBakery.getPhysicsDataForBlock(blockState);
                    int index = (x & 0xF) + ((z & 0xF) << 4) + ((y & 0xF) << 8);
                    int colliderValue = colliderData == null ? 0 : colliderData.handle() + 1;
                    chunk.data[index] = RapierPhysicsPipeline.packBlockState(state, colliderValue);
                }
            }
        }
        if (contraption.sable$shouldCollide()) {
            for (Long2ObjectMap.Entry entry : chunks.long2ObjectEntrySet()) {
                SectionPos sectionPos = SectionPos.of((long)entry.getLongKey());
                UploadingContraptionChunk chunk = (UploadingContraptionChunk)entry.getValue();
                Rapier3D.addKinematicContraptionChunkSection(this.sceneId, id, sectionPos.x(), sectionPos.y(), sectionPos.z(), chunk.data());
            }
        }
        Rapier3D.setLocalBounds(this.sceneId, id, localBounds.minX, localBounds.minY, localBounds.minZ, localBounds.maxX, localBounds.maxY, localBounds.maxZ);
    }

    @Override
    public void remove(KinematicContraption contraption) {
        TrackedKinematicContraption removed = (TrackedKinematicContraption)this.activeContraptions.remove((Object)contraption);
        if (removed == null) {
            return;
        }
        Rapier3D.removeKinematicContraption(this.sceneId, removed.id());
    }

    @Override
    public Pose3d readPose(ServerSubLevel body, Pose3d dest) {
        Rapier3D.getPose(this.sceneId, Rapier3D.getID(body), this.cache);
        dest.position().set(this.cache[0], this.cache[1], this.cache[2]);
        dest.orientation().set(this.cache[3], this.cache[4], this.cache[5], this.cache[6]);
        return dest;
    }

    @Override
    public RopeHandle addRope(RopePhysicsObject rope) {
        return RapierRopeHandle.create(this.sceneId, rope.getCollisionRadius(), rope.getPoints());
    }

    @Override
    public BoxHandle addBox(BoxPhysicsObject box) {
        return RapierBoxHandle.create(this.sceneId, box.getPose(), box.getHalfExtents(), box.getMass());
    }

    @Override
    public void handleChunkSectionAddition(LevelChunkSection section, int x, int y, int z, boolean uploadDataIfGlobal) {
        LevelPlot plot;
        this.accelerator.clearCache();
        int[] array = new int[4096];
        SectionPos sectionPos = SectionPos.of((int)x, (int)y, (int)z);
        if (!section.hasOnlyAir()) {
            LevelChunk chunk = this.accelerator.getChunk(x, z);
            for (int bx = 0; bx < 16; ++bx) {
                for (int bz = 0; bz < 16; ++bz) {
                    for (int by = 0; by < 16; ++by) {
                        BlockPos globalPos = new BlockPos(bx, by, bz).offset(sectionPos.minBlockX(), sectionPos.minBlockY(), sectionPos.minBlockZ());
                        VoxelNeighborhoodState state = VoxelNeighborhoodState.getState(this.accelerator, globalPos, chunk);
                        RapierVoxelColliderData colliderData = this.colliderBakery.getPhysicsDataForBlock(this.accelerator.getBlockState(globalPos));
                        int index = bx + (bz << 4) + (by << 8);
                        int colliderValue = colliderData == null ? 0 : colliderData.handle() + 1;
                        array[index] = RapierPhysicsPipeline.packBlockState(state, colliderValue);
                    }
                }
            }
        }
        boolean global = (plot = SubLevelContainer.getContainer(this.level).getPlot(x, z)) == null;
        int id = -1;
        if (plot != null && uploadDataIfGlobal) {
            id = Rapier3D.getID((ServerSubLevel)plot.getSubLevel());
        }
        Rapier3D.addChunk(this.sceneId, x, y, z, array, global, id);
    }

    @Override
    public void handleChunkSectionRemoval(int x, int y, int z) {
        Rapier3D.removeChunk(this.sceneId, x, y, z, !SubLevelContainer.getContainer(this.level).inBounds(x, z));
    }

    @Override
    public void handleBlockChange(SectionPos sectionPos, LevelChunkSection chunk, int x, int y, int z, BlockState oldState, BlockState newState) {
        x = (sectionPos.x() << 4) + x;
        y = (sectionPos.y() << 4) + y;
        z = (sectionPos.z() << 4) + z;
        BlockPos globalBlockPos = new BlockPos(x, y, z);
        for (Direction dir : Direction.values()) {
            BlockPos pos = globalBlockPos.relative(dir);
            VoxelNeighborhoodState state = VoxelNeighborhoodState.getState(this.accelerator, pos, null);
            RapierVoxelColliderData colliderData = this.colliderBakery.getPhysicsDataForBlock(this.level.getBlockState(pos));
            int colliderValue = colliderData == null ? 0 : colliderData.handle() + 1;
            Rapier3D.changeBlock(this.sceneId, pos.getX(), pos.getY(), pos.getZ(), RapierPhysicsPipeline.packBlockState(state, colliderValue));
        }
        VoxelNeighborhoodState state = VoxelNeighborhoodState.getState(this.accelerator, globalBlockPos, null);
        RapierVoxelColliderData colliderData = this.colliderBakery.getPhysicsDataForBlock(newState);
        int colliderValue = colliderData == null ? 0 : colliderData.handle() + 1;
        Rapier3D.changeBlock(this.sceneId, x, y, z, RapierPhysicsPipeline.packBlockState(state, colliderValue));
    }

    @Override
    public void onStatsChanged(@NotNull ServerSubLevel serverSubLevel) {
        BoundingBox3ic plotBounds = serverSubLevel.getPlot().getBoundingBox();
        int id = Rapier3D.getID(serverSubLevel);
        Vector3dc centerOfMass = serverSubLevel.getMassTracker().getCenterOfMass();
        if (centerOfMass != null) {
            Rapier3D.setCenterOfMass(this.sceneId, id, centerOfMass.x(), centerOfMass.y(), centerOfMass.z());
            Rapier3D.setMassPropertiesFrom(this.sceneId, id, serverSubLevel.getMassTracker());
        }
        Rapier3D.setLocalBounds(this.sceneId, id, plotBounds.minX(), plotBounds.minY(), plotBounds.minZ(), plotBounds.maxX(), plotBounds.maxY(), plotBounds.maxZ());
    }

    @Override
    public void teleport(PhysicsPipelineBody body, Vector3dc position, Quaterniondc orientation) {
        Rapier3D.teleportObject(this.sceneId, Rapier3D.getID(body), position.x(), position.y(), position.z(), orientation.x(), orientation.y(), orientation.z(), orientation.w());
        if (body instanceof ServerSubLevel) {
            ServerSubLevel subLevel = (ServerSubLevel)body;
            subLevel.logicalPose().position().set(position);
            subLevel.logicalPose().orientation().set(orientation);
        }
    }

    @Override
    public void applyImpulse(PhysicsPipelineBody body, Vector3dc position, Vector3dc force) {
        Vector3dc centerOfMass = body.getMassTracker().getCenterOfMass();
        Rapier3D.applyForce(this.sceneId, Rapier3D.getID(body), position.x() - centerOfMass.x(), position.y() - centerOfMass.y(), position.z() - centerOfMass.z(), force.x(), force.y(), force.z(), true);
    }

    @Override
    public void applyLinearAndAngularImpulse(PhysicsPipelineBody body, Vector3dc force, Vector3dc torque, boolean wakeUp) {
        Rapier3D.applyForceAndTorque(this.sceneId, Rapier3D.getID(body), force.x(), force.y(), force.z(), torque.x(), torque.y(), torque.z(), wakeUp);
    }

    @Override
    public void addLinearAndAngularVelocity(PhysicsPipelineBody body, Vector3dc linearVelocity, Vector3dc angularVelocity) {
        Rapier3D.addLinearAngularVelocities(this.sceneId, Rapier3D.getID(body), linearVelocity.x(), linearVelocity.y(), linearVelocity.z(), angularVelocity.x(), angularVelocity.y(), angularVelocity.z(), true);
    }

    @Override
    public Vector3d getLinearVelocity(PhysicsPipelineBody body, Vector3d dest) {
        Rapier3D.getLinearVelocity(this.sceneId, Rapier3D.getID(body), this.cache);
        return dest.set(this.cache);
    }

    @Override
    public Vector3d getAngularVelocity(PhysicsPipelineBody body, Vector3d dest) {
        Rapier3D.getAngularVelocity(this.sceneId, Rapier3D.getID(body), this.cache);
        return dest.set(this.cache);
    }

    @Override
    public void wakeUp(PhysicsPipelineBody body) {
        Rapier3D.wakeUpObject(this.sceneId, Rapier3D.getID(body));
    }

    @Override
    public <T extends PhysicsConstraintHandle> T addConstraint(@Nullable ServerSubLevel sublevelA, @Nullable ServerSubLevel sublevelB, PhysicsConstraintConfiguration<T> configuration) {
        if (sublevelA == null && sublevelB == null) {
            Sable.LOGGER.error("Cannot add a constraint between the static world and static world");
            return null;
        }
        if (sublevelA == sublevelB) {
            Sable.LOGGER.error("Cannot add a constraint between a sub-level and itself");
            return null;
        }
        if (configuration instanceof RotaryConstraintConfiguration) {
            RotaryConstraintConfiguration config = (RotaryConstraintConfiguration)configuration;
            return (T)RapierRotaryConstraintHandle.create(this.level, sublevelA, sublevelB, config);
        }
        if (configuration instanceof FixedConstraintConfiguration) {
            FixedConstraintConfiguration config = (FixedConstraintConfiguration)configuration;
            return (T)RapierFixedConstraintHandle.create(this.level, sublevelA, sublevelB, config);
        }
        if (configuration instanceof FreeConstraintConfiguration) {
            FreeConstraintConfiguration config = (FreeConstraintConfiguration)configuration;
            return (T)RapierFreeConstraintHandle.create(this.level, sublevelA, sublevelB, config);
        }
        if (configuration instanceof GenericConstraintConfiguration) {
            GenericConstraintConfiguration config = (GenericConstraintConfiguration)configuration;
            return (T)RapierGenericConstraintHandle.create(this.level, sublevelA, sublevelB, config);
        }
        Sable.LOGGER.error("Unknown constraint configuration type: {}", (Object)configuration.getClass().getName());
        return null;
    }

    @Override
    public void updateConfigFrom(PhysicsConfigData data) {
        Rapier3D.configFrequencyAndDamping(data.contactSpringFrequency, data.contactSpringDampingRatio);
        Rapier3D.configSolverIterations(data.solverIterations, data.pgsIterations, data.stabilizationIterations);
        Rapier3D.configMinIslandSize(data.minDynamicBodiesPerIsland);
    }

    @Override
    public int getNextRuntimeID() {
        return Rapier3D.nextBodyID();
    }

    private record TrackedKinematicContraption(Vector3d lastUploadedPosition, Quaterniond lastUploadedOrientation, Vector3d lastUploadedLinVel, Vector3d lastUploadedAngVel, int id) {
    }
}
