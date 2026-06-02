/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Math
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.system;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassTracker;
import dev.ryanhcode.sable.api.physics.object.ArbitraryPhysicsObject;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.mixinterface.toast.SableToastableServer;
import dev.ryanhcode.sable.physics.config.PhysicsConfigData;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.platform.SableEventPublishPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.sublevel.system.ticket.PhysicsChunkTicketManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelPhysicsSystem
implements SubLevelObserver {
    public static final int DEFAULT_RESIDENT_CAPACITY = 8;
    public static final boolean USE_TICKETS_FOR_QUERIES = false;
    public static SubLevelPhysicsSystem currentlySteppingSystem;
    private final PhysicsPipeline pipeline;
    private final ServerLevel level;
    private final Object2IntMap<UUID> punchCooldowns = new Object2IntOpenHashMap();
    private final PhysicsConfigData config = new PhysicsConfigData();
    private final PhysicsChunkTicketManager ticketManager = new PhysicsChunkTicketManager();
    private final Pose3d storagePose = new Pose3d();
    private final Collection<ArbitraryPhysicsObject> arbitraryObjects = new ObjectOpenHashSet();
    private boolean paused;
    private int currentSubstep;

    public SubLevelPhysicsSystem(ServerLevel level) {
        this.level = level;
        this.pipeline = Sable.createPhysicsPipeline(this.level);
    }

    public static SubLevelPhysicsSystem get(Level level) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container instanceof ServerSubLevelContainer) {
            ServerSubLevelContainer serverContainer = (ServerSubLevelContainer)container;
            return serverContainer.physicsSystem();
        }
        return null;
    }

    @NotNull
    public static SubLevelPhysicsSystem require(Level level) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container instanceof ServerSubLevelContainer) {
            ServerSubLevelContainer serverContainer = (ServerSubLevelContainer)container;
            return Objects.requireNonNull(serverContainer.physicsSystem());
        }
        throw new IllegalArgumentException("Sub-level container not found");
    }

    public static SubLevelPhysicsSystem getCurrentlySteppingSystem() {
        if (currentlySteppingSystem == null) {
            throw new IllegalStateException("No physics system is currently stepping");
        }
        return currentlySteppingSystem;
    }

    public void initialize() {
        Vector3d gravity = new Vector3d((Vector3dc)DimensionPhysicsData.getGravity((Level)this.level));
        double universalDrag = DimensionPhysicsData.getUniversalDrag(this.level);
        this.pipeline.init((Vector3dc)gravity, universalDrag);
        this.pipeline.updateConfigFrom(this.config);
    }

    public void onConfigUpdated() {
        this.pipeline.updateConfigFrom(this.config);
    }

    @Override
    public void onSubLevelAdded(SubLevel subLevel) {
        if (!(subLevel instanceof ServerSubLevel)) {
            throw new UnsupportedOperationException("Client sub-levels are not supported by the physics system. How did we end up here?");
        }
        ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
        this.pipeline.add(serverSubLevel, (Pose3dc)serverSubLevel.logicalPose());
    }

    @Override
    public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
        if (!(subLevel instanceof ServerSubLevel)) {
            throw new UnsupportedOperationException("Client sub-levels are not supported by the physics system");
        }
        ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
        this.pipeline.remove(serverSubLevel);
    }

    @Override
    public void tick(SubLevelContainer sidelessContainer) {
        ServerSubLevelContainer container = (ServerSubLevelContainer)sidelessContainer;
        this.tickPunchCooldowns();
        this.ticketManager.update(this.level, container, this, this.pipeline, 0.05);
        for (ServerSubLevel subLevel : container.getAllSubLevels()) {
            subLevel.updateLastPose();
            for (BlockEntitySubLevelActor actor : subLevel.getPlot().getBlockEntityActors()) {
                actor.sable$tick(subLevel);
            }
        }
        this.pipeline.tick();
        if (!this.paused) {
            currentlySteppingSystem = this;
            try {
                this.tickPipelinePhysics(container);
            }
            catch (Exception e) {
                CrashReport crashReport = CrashReport.forThrowable((Throwable)e, (String)"Sable ticking physics");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Current physics state");
                crashReportCategory.setDetail("Dimension", (Object)this.level.dimension());
                throw new ReportedException(crashReport);
            }
            currentlySteppingSystem = null;
        }
    }

    private void tickPipelinePhysics(ServerSubLevelContainer container) {
        this.pipeline.prePhysicsTicks();
        this.currentSubstep = 0;
        while (this.currentSubstep < this.config.substepsPerTick) {
            double substepTimeStep = 0.05 / (double)this.config.substepsPerTick;
            for (ServerSubLevel subLevel : container.getAllSubLevels()) {
                if (subLevel.isRemoved()) continue;
                subLevel.prePhysicsTickBegin();
            }
            for (ServerSubLevel subLevel : container.getAllSubLevels()) {
                if (subLevel.isRemoved()) continue;
                subLevel.updateMergedMassData((float)this.getPartialPhysicsTick());
            }
            for (ServerSubLevel subLevel : container.getAllSubLevels()) {
                if (subLevel.isRemoved()) continue;
                subLevel.prePhysicsTick(this, this.getPhysicsHandle(subLevel), substepTimeStep);
            }
            SableEventPublishPlatform.INSTANCE.prePhysicsTick(this, substepTimeStep);
            for (ServerSubLevel subLevel : container.getAllSubLevels()) {
                if (subLevel.isRemoved()) continue;
                subLevel.applyQueuedForces(this, this.getPhysicsHandle(subLevel), substepTimeStep);
            }
            this.pipeline.physicsTick(substepTimeStep);
            container.processSubLevelRemovals();
            this.updateAllPoses(container);
            SableEventPublishPlatform.INSTANCE.postPhysicsTick(this, substepTimeStep);
            ++this.currentSubstep;
        }
        this.pipeline.postPhysicsTicks();
        this.currentSubstep = this.config.substepsPerTick;
    }

    private void updateAllPoses(ServerSubLevelContainer container) {
        for (ServerSubLevel subLevel : container.getAllSubLevels()) {
            if (subLevel.isRemoved()) continue;
            this.updatePose(subLevel);
        }
    }

    public void updatePose(ServerSubLevel serverSubLevel) {
        this.pipeline.readPose(serverSubLevel, this.storagePose);
        Vector3d position = this.storagePose.position();
        Quaterniond orientation = this.storagePose.orientation();
        if (Double.isNaN(position.x) || Double.isNaN(position.y) || Double.isNaN(position.z) || Double.isNaN(orientation.x) || Double.isNaN(orientation.y) || Double.isNaN(orientation.z) || Double.isNaN(orientation.w)) {
            Sable.LOGGER.info("Invalid position {} or orientation {} received for sub-level {} from pipeline.", new Object[]{this.storagePose.position(), this.storagePose.orientation(), serverSubLevel});
            if (!this.recoverSubLevel(serverSubLevel)) {
                return;
            }
            this.pipeline.readPose(serverSubLevel, this.storagePose);
        }
        Pose3d logicalPose = serverSubLevel.logicalPose();
        logicalPose.position().set((Vector3dc)this.storagePose.position());
        logicalPose.orientation().set((Quaterniondc)this.storagePose.orientation());
        logicalPose.position().sub(serverSubLevel.lastPose().position(), serverSubLevel.latestLinearVelocity);
        Quaterniond difference = logicalPose.orientation().difference(serverSubLevel.lastPose().orientation(), new Quaterniond()).conjugate();
        Vector3d angularVelocity = serverSubLevel.latestAngularVelocity.set(difference.x, difference.y, difference.z);
        if (angularVelocity.lengthSquared() <= 1.0E-15) {
            angularVelocity.mul(2.0 / difference.w);
        } else {
            angularVelocity.normalize().mul(2.0 * Math.safeAcos((double)difference.w));
        }
        serverSubLevel.latestLinearVelocity.mul(20.0);
        serverSubLevel.latestAngularVelocity.mul(20.0);
    }

    public boolean recoverSubLevel(ServerSubLevel serverSubLevel) {
        Sable.LOGGER.info("Attempting to recover physics state for sub-level {}. Removing and re-adding from pipeline.", (Object)serverSubLevel);
        MinecraftServer server = this.level.getServer();
        if (server instanceof SableToastableServer) {
            SableToastableServer toastable = (SableToastableServer)server;
            toastable.sable$reportSubLevelPhysicsFailure(serverSubLevel);
        }
        this.pipeline.remove(serverSubLevel);
        this.pipeline.add(serverSubLevel, (Pose3dc)serverSubLevel.logicalPose());
        if (serverSubLevel.getMassTracker().getCenterOfMass() == null) {
            Sable.LOGGER.info("Sub-level recovery added sub-level to pipeline, but center of mass is null. Aborting and removing sub-level.");
            SubLevelContainer.getContainer(this.level).removeSubLevel(serverSubLevel, SubLevelRemovalReason.REMOVED);
            return false;
        }
        ServerLevelPlot plot = serverSubLevel.getPlot();
        for (PlotChunkHolder holder : plot.getLoadedChunks()) {
            LevelChunk chunk = holder.getChunk();
            ChunkPos global = chunk.getPos();
            LevelChunkSection[] levelChunkSections = chunk.getSections();
            for (int i = 0; i < chunk.getSectionsCount(); ++i) {
                LevelChunkSection section = levelChunkSections[i];
                if (section.hasOnlyAir()) continue;
                int sectionY = chunk.getSectionYFromSectionIndex(i);
                this.pipeline.handleChunkSectionAddition(section, global.x, sectionY, global.z, true);
            }
        }
        return true;
    }

    private void tickPunchCooldowns() {
        ObjectIterator punchCooldownIter = this.punchCooldowns.object2IntEntrySet().iterator();
        while (punchCooldownIter.hasNext()) {
            Object2IntMap.Entry entry = (Object2IntMap.Entry)punchCooldownIter.next();
            int cooldown = entry.getIntValue() - 1;
            if (cooldown <= 0) {
                punchCooldownIter.remove();
                continue;
            }
            entry.setValue(cooldown);
        }
    }

    public boolean tryPunch(UUID player, int cooldownAttempt) {
        int cooldown = this.punchCooldowns.getOrDefault((Object)player, 0);
        if (cooldown > 0) {
            return false;
        }
        int newCooldown = Math.max((int)SableConfig.SUB_LEVEL_PUNCH_COOLDOWN_TICKS.getAsInt(), (int)cooldownAttempt);
        this.punchCooldowns.put((Object)player, newCooldown);
        return true;
    }

    public PhysicsPipeline getPipeline() {
        return this.pipeline;
    }

    public RigidBodyHandle getPhysicsHandle(@NotNull ServerSubLevel subLevel) {
        return new RigidBodyHandle(Objects.requireNonNull(subLevel), this);
    }

    public void handleBlockChange(SectionPos sectionPos, LevelChunkSection section, int localX, int localY, int localZ, BlockState oldState, BlockState newState) {
        ChunkPos chunk = sectionPos.chunk();
        LevelPlot plot = ((SubLevelContainerHolder)this.level).sable$getPlotContainer().getPlot(chunk);
        if (plot != null) {
            this.ticketManager.addSectionIfNotTracked(this.level, section, sectionPos, this.pipeline);
        }
        int x = (sectionPos.x() << 4) + localX;
        int y = (sectionPos.y() << 4) + localY;
        int z = (sectionPos.z() << 4) + localZ;
        SubLevel subLevel = Sable.HELPER.getContaining((Level)this.level, sectionPos);
        BlockPos globalBlockPos = new BlockPos(x, y, z);
        this.updateMassDataFromBlockChange(subLevel, globalBlockPos, oldState, newState, true);
        this.pipeline.handleBlockChange(sectionPos, section, localX, localY, localZ, oldState, newState);
        this.wakeUpObjectsAt(x, y, z);
    }

    public void wakeUpObjectsAt(int x, int y, int z) {
        BoundingBox3d bounds = new BoundingBox3d((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
        bounds.expand(0.1, bounds);
        Iterable<SubLevel> intersectingSubLevels = Sable.HELPER.getAllIntersecting((Level)this.level, (BoundingBox3dc)bounds);
        for (SubLevel intersectingSubLevel : intersectingSubLevels) {
            if (!(intersectingSubLevel instanceof ServerSubLevel)) continue;
            ServerSubLevel intersectingServerSubLevel = (ServerSubLevel)intersectingSubLevel;
            this.pipeline.wakeUp(intersectingServerSubLevel);
        }
        if (this.arbitraryObjects.isEmpty()) {
            return;
        }
        BoundingBox3d objectBounds = new BoundingBox3d();
        for (ArbitraryPhysicsObject object : this.arbitraryObjects) {
            object.getBoundingBox(objectBounds);
            if (!objectBounds.intersects((BoundingBox3dc)bounds)) continue;
            object.wakeUp();
        }
    }

    public void updateMassDataFromBlockChange(SubLevel subLevel, BlockPos globalBlockPos, BlockState oldState, BlockState newState, boolean notifyPipeline) {
        if (subLevel instanceof ServerSubLevel) {
            double mass;
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            Vec3 oldInertia = oldState.isAir() ? null : PhysicsBlockPropertyHelper.getInertia((BlockGetter)this.level, globalBlockPos, oldState);
            Vec3 inertia = newState.isAir() ? null : PhysicsBlockPropertyHelper.getInertia((BlockGetter)this.level, globalBlockPos, newState);
            double oldMass = oldState.isAir() ? 0.0 : PhysicsBlockPropertyHelper.getMass((BlockGetter)this.level, globalBlockPos, oldState);
            double d = mass = newState.isAir() ? 0.0 : PhysicsBlockPropertyHelper.getMass((BlockGetter)this.level, globalBlockPos, newState);
            if (mass != oldMass || newState != oldState && (oldMass != 0.0 || mass != 0.0) || oldInertia != inertia) {
                Level level = subLevel.getLevel();
                MassTracker massTracker = serverSubLevel.getSelfMassTracker();
                if (mass != 0.0) {
                    massTracker.addBlockMass((BlockGetter)level, newState, globalBlockPos, mass, inertia);
                }
                if (oldMass != 0.0) {
                    massTracker.addBlockMass((BlockGetter)level, oldState, globalBlockPos, -oldMass, oldInertia);
                }
                if (!subLevel.isRemoved() && massTracker.isInvalid()) {
                    serverSubLevel.getPlot().destroyAllBlocks();
                    serverSubLevel.markRemoved();
                    return;
                }
                if (notifyPipeline) {
                    serverSubLevel.updateMergedMassData((float)this.getPartialPhysicsTick());
                    this.pipeline.onStatsChanged(serverSubLevel);
                }
            }
        }
    }

    public double getPartialPhysicsTick() {
        return (double)(this.currentSubstep + 1) / (double)this.config.substepsPerTick;
    }

    public boolean getPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Iterable<SubLevel> queryIntersecting(BoundingBox3dc bounds) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        assert (container != null) : "No sub-level container found for level that somehow also has a physics system";
        return container.queryIntersecting(bounds);
    }

    public PhysicsConfigData getConfig() {
        return this.config;
    }

    public Iterable<ArbitraryPhysicsObject> getArbitraryObjects() {
        return this.arbitraryObjects;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public PhysicsChunkTicketManager getTicketManager() {
        return this.ticketManager;
    }

    public void addObject(ArbitraryPhysicsObject object) {
        if (this.arbitraryObjects.add(object)) {
            object.onAddition(this);
        }
    }

    public void removeObject(ArbitraryPhysicsObject object) {
        if (this.arbitraryObjects.remove(object)) {
            object.onRemoved();
        }
    }

    public int getNextRuntimeID() {
        return this.pipeline.getNextRuntimeID();
    }
}
