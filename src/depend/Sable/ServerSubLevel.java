/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  foundry.veil.api.network.VeilPacketManager$PacketSink
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.physics.mass.MassTracker;
import dev.ryanhcode.sable.api.physics.mass.MergedMassTracker;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundChangeSubLevelNamePacket;
import dev.ryanhcode.sable.physics.ReactionWheelManager;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockController;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.plot.heat.SubLevelHeatMapManager;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.util.LevelAccelerator;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ServerSubLevel
extends SubLevel
implements PhysicsPipelineBody {
    @ApiStatus.Internal
    public final Vector3d latestLinearVelocity = new Vector3d();
    @ApiStatus.Internal
    public final Vector3d latestAngularVelocity = new Vector3d();
    private final Set<UUID> trackingPlayers = new ObjectOpenHashSet();
    private final Pose3d lastNetworkedPose = new Pose3d();
    private final BoundingBox3i lastNetworkedBoundingBox = new BoundingBox3i();
    private final int runtimeId;
    private final SubLevelHeatMapManager heatMapManager = new SubLevelHeatMapManager(this);
    private final FloatingBlockController floatingBlockController = new FloatingBlockController(this);
    private final ReactionWheelManager reactionWheelManager = new ReactionWheelManager(this);
    @Nullable
    private Object2ObjectMap<ForceGroup, QueuedForceGroup> queuedForceGroups = null;
    private MergedMassTracker massTracker;
    private boolean lastNetworkedStopped = false;
    @Nullable
    private UUID splitFromSubLevel = null;
    @Nullable
    private Pose3d splitFromPose = null;
    @ApiStatus.Internal
    private GlobalSavedSubLevelPointer lastSerializationPointer = null;
    @Nullable
    private CompoundTag userDataTag;
    private boolean trackIndividualQueuedForces = false;

    public ServerSubLevel(ServerLevel level, int plotX, int plotY, Pose3d pose) {
        super((Level)level, plotX, plotY, pose);
        SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.get((Level)level);
        assert (physicsSystem != null);
        this.runtimeId = physicsSystem.getNextRuntimeID();
    }

    public Collection<UUID> getTrackingPlayers() {
        return this.trackingPlayers;
    }

    public VeilPacketManager.PacketSink playerSink() {
        return packet -> {
            for (UUID uuid : this.trackingPlayers) {
                ServerPlayer player = (ServerPlayer)this.getLevel().getPlayerByUUID(uuid);
                if (!(player instanceof ServerPlayer)) continue;
                player.connection.send(packet);
            }
        };
    }

    public Pose3d lastNetworkedPose() {
        return this.lastNetworkedPose;
    }

    public BoundingBox3i lastNetworkedBoundingBox() {
        return this.lastNetworkedBoundingBox;
    }

    @Override
    public int getRuntimeId() {
        return this.runtimeId;
    }

    @Override
    protected LevelPlot createPlot(SubLevelContainer plotContainer, int plotX, int plotY, int logPlotSize) {
        return new ServerLevelPlot(plotContainer, plotX, plotY, plotContainer.getLogPlotSize(), this);
    }

    @Override
    @ApiStatus.Internal
    public void onPlotBoundsChanged() {
        BoundingBox3ic bounds = this.getPlot().getBoundingBox();
        if (bounds == BoundingBox3i.EMPTY || bounds.volume() <= 0) {
            this.markRemoved();
        }
    }

    @Override
    @ApiStatus.Internal
    public void tick() {
        super.tick();
        this.updateBoundingBox();
        BoundingBox3dc bounds = this.boundingBox();
        if (!this.isRemoved() && (bounds.minY() < SableConfig.SUB_LEVEL_REMOVE_MIN.getAsDouble() || bounds.maxY() > SableConfig.SUB_LEVEL_REMOVE_MAX.getAsDouble())) {
            Sable.LOGGER.info("Sub-level {} has an extreme Y coordinate range, removing", (Object)this);
            this.markRemoved();
            return;
        }
        if (SableConfig.SUB_LEVEL_SPLITTING.getAsBoolean()) {
            this.heatMapManager.tick();
        }
    }

    @ApiStatus.Internal
    public boolean getLastNetworkedStopped() {
        return this.lastNetworkedStopped;
    }

    @ApiStatus.Internal
    public void setLastNetworkedStopped(boolean stopped) {
        this.lastNetworkedStopped = stopped;
    }

    @ApiStatus.Internal
    public void updateMergedMassData(float partialPhysicsTick) {
        if (this.massTracker != null) {
            this.massTracker.update(partialPhysicsTick);
        }
    }

    @ApiStatus.Internal
    public void prePhysicsTickBegin() {
        if (this.queuedForceGroups != null) {
            this.queuedForceGroups.values().forEach(QueuedForceGroup::reset);
        }
    }

    public void applyQueuedForces(SubLevelPhysicsSystem physicsSystem, RigidBodyHandle handle, double timeStep) {
        if (this.queuedForceGroups != null) {
            for (Map.Entry entry : this.queuedForceGroups.entrySet()) {
                QueuedForceGroup group = (QueuedForceGroup)entry.getValue();
                handle.applyForcesAndReset(group.getForceTotal());
            }
        }
    }

    @ApiStatus.Internal
    public void prePhysicsTick(SubLevelPhysicsSystem physicsSystem, RigidBodyHandle handle, double timeStep) {
        ServerLevelPlot plot = this.getPlot();
        for (BlockEntitySubLevelActor actor : plot.getBlockEntityActors()) {
            actor.sable$physicsTick(this, handle, timeStep);
        }
        ObjectCollection<BlockSubLevelLiftProvider.LiftProviderContext> liftProviders = plot.getLiftProviders();
        ObjectCollection<KinematicContraption> contraptions = plot.getContraptions();
        if (!liftProviders.isEmpty() || this.floatingBlockController.needsTicking() || this.reactionWheelManager.needsTicking() || !contraptions.isEmpty()) {
            boolean trackForces = this.isTrackingIndividualQueuedForces();
            Vector3d linearVelocity = handle.getLinearVelocity(new Vector3d());
            Vector3d angularVelocity = handle.getAngularVelocity(new Vector3d());
            Vector3d linearImpulse = new Vector3d();
            Vector3d angularImpulse = new Vector3d();
            List<Object> groups = trackForces ? BlockSubLevelLiftProvider.groupLiftProviders(liftProviders) : List.of();
            for (BlockSubLevelLiftProvider.LiftProviderContext context : liftProviders) {
                BlockSubLevelLiftProvider.LiftProviderGroup group = null;
                for (BlockSubLevelLiftProvider.LiftProviderGroup g : groups) {
                    if (!g.positions().contains(context.pos())) continue;
                    group = g;
                    break;
                }
                ((BlockSubLevelLiftProvider)context.state().getBlock()).sable$contributeLiftAndDrag(context, this, null, timeStep, (Vector3dc)linearVelocity, (Vector3dc)angularVelocity, linearImpulse, angularImpulse, group);
            }
            for (BlockSubLevelLiftProvider.LiftProviderGroup group : groups) {
                if (group.totalLift().lengthSquared() >= 1.0E-6) {
                    this.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.LIFT.get()).recordPointForce((Vector3dc)group.liftCenter().div(group.totalLiftStrength), (Vector3dc)group.totalLift());
                }
                if (!(group.totalDrag().lengthSquared() >= 1.0E-6)) continue;
                this.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.DRAG.get()).recordPointForce((Vector3dc)group.dragCenter().div(group.totalDragStrength), (Vector3dc)group.totalDrag());
            }
            if (!contraptions.isEmpty()) {
                Pose3d localContraptionPose = new Pose3d();
                for (KinematicContraption contraption : contraptions) {
                    Collection<BlockSubLevelLiftProvider.LiftProviderContext> contraptionProviders = contraption.sable$liftProviders().values();
                    contraption.sable$getLocalPose(localContraptionPose, physicsSystem.getPartialPhysicsTick());
                    List<Object> contraptionGroups = trackForces ? BlockSubLevelLiftProvider.groupLiftProviders(contraptionProviders) : List.of();
                    for (BlockSubLevelLiftProvider.LiftProviderContext context : contraptionProviders) {
                        BlockSubLevelLiftProvider.LiftProviderGroup group = null;
                        for (BlockSubLevelLiftProvider.LiftProviderGroup g : contraptionGroups) {
                            if (!g.positions().contains(context.pos())) continue;
                            group = g;
                            break;
                        }
                        ((BlockSubLevelLiftProvider)context.state().getBlock()).sable$contributeLiftAndDrag(context, this, localContraptionPose, timeStep, (Vector3dc)linearVelocity, (Vector3dc)angularVelocity, linearImpulse, angularImpulse, group);
                    }
                    for (BlockSubLevelLiftProvider.LiftProviderGroup group : contraptionGroups) {
                        if (group.totalLift().lengthSquared() >= 1.0E-6) {
                            this.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.LIFT.get()).recordPointForce((Vector3dc)group.liftCenter().div(group.totalLiftStrength), (Vector3dc)group.totalLift());
                        }
                        if (!(group.totalDrag().lengthSquared() >= 1.0E-6)) continue;
                        this.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.DRAG.get()).recordPointForce((Vector3dc)group.dragCenter().div(group.totalDragStrength), (Vector3dc)group.totalDrag());
                    }
                }
            }
            linearVelocity.fma(-0.47619047619047616 * timeStep, (Vector3dc)DimensionPhysicsData.getGravity((Level)this.getLevel()));
            this.floatingBlockController.physicsTick(physicsSystem.getPartialPhysicsTick(), timeStep, (Vector3dc)linearVelocity, (Vector3dc)angularVelocity, linearImpulse, angularImpulse);
            this.reactionWheelManager.physicsTick(handle);
            handle.applyLinearAndAngularImpulse((Vector3dc)linearImpulse, (Vector3dc)angularImpulse, false);
        }
    }

    public QueuedForceGroup getOrCreateQueuedForceGroup(ForceGroup forceGroup) {
        if (this.queuedForceGroups == null) {
            this.queuedForceGroups = new Object2ObjectOpenHashMap();
        }
        return (QueuedForceGroup)this.queuedForceGroups.computeIfAbsent((Object)forceGroup, fg -> new QueuedForceGroup(this));
    }

    public void deleteAllEntities() {
        this.getPlot().kickAllEntities();
    }

    @Override
    public void setName(@Nullable String name) {
        if (!Objects.equals(name, this.getName())) {
            this.playerSink().sendPacket(new CustomPacketPayload[]{new ClientboundChangeSubLevelNamePacket(this.getUniqueId(), name)});
        }
        super.setName(name);
    }

    public SubLevelHeatMapManager getHeatMapManager() {
        return this.heatMapManager;
    }

    public FloatingBlockController getFloatingBlockController() {
        return this.floatingBlockController;
    }

    public ReactionWheelManager getReactionWheelManager() {
        return this.reactionWheelManager;
    }

    public void setSplitFrom(ServerSubLevel containingSubLevel, Pose3d originalPose) {
        this.splitFromSubLevel = containingSubLevel.getUniqueId();
        this.splitFromPose = originalPose;
    }

    @Nullable
    public UUID getSplitFromSubLevel() {
        return this.splitFromSubLevel;
    }

    @Nullable
    public Pose3d getSplitFromPose() {
        return this.splitFromPose;
    }

    public void clearSplitFrom() {
        this.splitFromSubLevel = null;
        this.splitFromPose = null;
    }

    public ServerLevel getLevel() {
        return (ServerLevel)super.getLevel();
    }

    @Override
    public ServerLevelPlot getPlot() {
        return (ServerLevelPlot)super.getPlot();
    }

    @Override
    public MassData getMassTracker() {
        return this.massTracker;
    }

    @ApiStatus.Internal
    public void buildMassTracker() {
        MassTracker internalTracker = MassTracker.build(new LevelAccelerator((Level)this.getLevel()), this.getPlot().getBoundingBox());
        this.massTracker = new MergedMassTracker(this, internalTracker);
    }

    public MassTracker getSelfMassTracker() {
        return this.massTracker.getSelfMassTracker();
    }

    @ApiStatus.Internal
    public GlobalSavedSubLevelPointer getLastSerializationPointer() {
        return this.lastSerializationPointer;
    }

    @ApiStatus.Internal
    public void setLastSerializationPointer(GlobalSavedSubLevelPointer lastSerializationPointer) {
        this.lastSerializationPointer = lastSerializationPointer;
    }

    public void enableIndividualQueuedForcesTracking(boolean enable) {
        this.trackIndividualQueuedForces = enable;
    }

    public boolean isTrackingIndividualQueuedForces() {
        return this.trackIndividualQueuedForces;
    }

    @Nullable
    public Object2ObjectMap<ForceGroup, QueuedForceGroup> getQueuedForceGroups() {
        return this.queuedForceGroups;
    }

    @Nullable
    public CompoundTag getUserDataTag() {
        return this.userDataTag;
    }

    public void setUserDataTag(CompoundTag userDataTag) {
        this.userDataTag = userDataTag;
    }

    @Override
    public String toString() {
        return "ServerSubLevel" + super.toString();
    }
}
