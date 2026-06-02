/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.physics.PhysicsPipeline
 *  dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle
 *  dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  foundry.veil.api.network.VeilPacketManager
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.physics_staff;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.config.server.physics.SimPhysics;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItem;
import dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffDragSessionsPacket;
import dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffLocksPacket;
import dev.simulated_team.simulated.service.SimConfigService;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PhysicsStaffServerHandler
extends SavedData {
    public static final String ID = "simulated_physics_staff_lock_data";
    private final Map<UUID, Lock> locks = new Object2ObjectOpenHashMap();
    private final Map<UUID, DragSession> draggingSessions = new Object2ObjectOpenHashMap();
    private ServerLevel level;
    private boolean draggingSessionsDirty = false;

    public PhysicsStaffServerHandler() {
        this(null);
    }

    public PhysicsStaffServerHandler(LevelAccessor level) {
        this.level = (ServerLevel)level;
    }

    public static void sendAllData(Player player) {
        MinecraftServer server = player.getServer();
        assert (server != null);
        for (ServerLevel level : server.getAllLevels()) {
            PhysicsStaffServerHandler handler = PhysicsStaffServerHandler.get(level);
            VeilPacketManager.player((ServerPlayer)((ServerPlayer)player)).sendPacket(new CustomPacketPayload[]{new PhysicsStaffLocksPacket((ResourceKey<Level>)level.dimension(), handler.locks.keySet()), PhysicsStaffServerHandler.makeSessionsPacket(level, handler)});
        }
    }

    @NotNull
    private static PhysicsStaffDragSessionsPacket makeSessionsPacket(ServerLevel level, PhysicsStaffServerHandler handler) {
        ObjectArrayList sessions = new ObjectArrayList(handler.draggingSessions.size());
        for (Map.Entry<UUID, DragSession> entry : handler.draggingSessions.entrySet()) {
            sessions.add(Pair.of((Object)entry.getKey(), (Object)entry.getValue().plotAnchor));
        }
        return new PhysicsStaffDragSessionsPacket((ResourceKey<Level>)level.dimension(), (List<Pair<UUID, Vector3d>>)sessions);
    }

    private static FixedConstraintHandle addConstraint(ServerSubLevelContainer container, ServerSubLevel subLevel) {
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        PhysicsPipeline pipeline = physicsSystem.getPipeline();
        FixedConstraintHandle handle = (FixedConstraintHandle)pipeline.addConstraint(null, subLevel, (PhysicsConstraintConfiguration)new FixedConstraintConfiguration((Vector3dc)subLevel.logicalPose().position(), (Vector3dc)subLevel.logicalPose().rotationPoint(), (Quaterniondc)subLevel.logicalPose().orientation()));
        return handle;
    }

    private static PhysicsStaffServerHandler create(ServerLevel level, CompoundTag nbt, HolderLookup.Provider registries) {
        PhysicsStaffServerHandler sd = new PhysicsStaffServerHandler((LevelAccessor)level);
        sd.loadLocks(nbt.getList(ID, 11));
        return sd;
    }

    public static PhysicsStaffServerHandler get(ServerLevel level) {
        PhysicsStaffServerHandler data = (PhysicsStaffServerHandler)level.getChunkSource().getDataStorage().computeIfAbsent(new SavedData.Factory(PhysicsStaffServerHandler::new, (nbt, lookup) -> PhysicsStaffServerHandler.create(level, nbt, lookup), null), ID);
        data.level = level;
        return data;
    }

    public void tick() {
        Iterator<Map.Entry<UUID, DragSession>> iter = this.draggingSessions.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, DragSession> entry = iter.next();
            DragSession session = entry.getValue();
            Player player = this.level.getPlayerByUUID(entry.getKey());
            if (player == null || !PhysicsStaffItem.isHolding(player)) {
                session.onRemoved();
                iter.remove();
                this.markDraggingSessionsDirty();
                continue;
            }
            session.tick();
            if (!session.isMarkedForRemoval()) continue;
            session.onRemoved();
            iter.remove();
            this.markDraggingSessionsDirty();
        }
        if (this.draggingSessionsDirty) {
            this.sendDragSessionsToClients();
            this.draggingSessionsDirty = false;
        }
    }

    private void markDraggingSessionsDirty() {
        this.draggingSessionsDirty = true;
    }

    public void physicsTick(SubLevelPhysicsSystem physicsSystem) {
        for (DragSession session : this.draggingSessions.values()) {
            session.physicsTick(physicsSystem);
        }
    }

    public void toggleLock(UUID uuid) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)this.level);
        ServerSubLevel subLevel = (ServerSubLevel)container.getSubLevel(uuid);
        if (subLevel == null) {
            return;
        }
        Lock existingLock = this.locks.get(uuid);
        if (existingLock != null) {
            this.locks.remove(uuid);
            existingLock.remove();
            this.setLocksDirty();
            return;
        }
        FixedConstraintHandle handle = PhysicsStaffServerHandler.addConstraint(container, subLevel);
        this.locks.put(uuid, new Lock(uuid, (PhysicsConstraintHandle)handle));
        this.setLocksDirty();
    }

    private void setLocksDirty() {
        this.setDirty(true);
        this.sendLocksToClients();
    }

    private void sendLocksToClients() {
        VeilPacketManager.all((MinecraftServer)this.level.getServer()).sendPacket(new CustomPacketPayload[]{new PhysicsStaffLocksPacket((ResourceKey<Level>)this.level.dimension(), this.locks.keySet())});
    }

    private void sendDragSessionsToClients() {
        VeilPacketManager.all((MinecraftServer)this.level.getServer()).sendPacket(new CustomPacketPayload[]{PhysicsStaffServerHandler.makeSessionsPacket(this.level, this)});
    }

    @NotNull
    public CompoundTag save(CompoundTag tag, // Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider) {
        ListTag tags = new ListTag();
        this.saveLocks(tags);
        tag.put(ID, (Tag)tags);
        return tag;
    }

    private void loadLocks(ListTag list) {
        for (Tag tag : list) {
            UUID uuid = NbtUtils.loadUUID((Tag)tag);
            this.locks.put(uuid, new Lock(uuid, null));
        }
    }

    private void saveLocks(ListTag list) {
        list.addAll(this.locks.keySet().stream().map(NbtUtils::createUUID).toList());
    }

    public boolean isLocked(SubLevel subLevel) {
        Lock lock = this.locks.get(subLevel.getUniqueId());
        return lock != null && lock.handle != null && lock.handle.isValid();
    }

    @ApiStatus.Internal
    public void applyLockIfNeeded(SubLevel subLevel) {
        Lock lock = this.locks.get(subLevel.getUniqueId());
        if (!(lock == null || lock.handle != null && lock.handle.isValid())) {
            ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)this.level);
            assert (container != null);
            FixedConstraintHandle handle = PhysicsStaffServerHandler.addConstraint(container, (ServerSubLevel)subLevel);
            this.locks.put(lock.subLevel(), new Lock(lock.subLevel(), (PhysicsConstraintHandle)handle));
        }
    }

    public void removeLock(SubLevel subLevel) {
        Lock removedLock = this.locks.remove(subLevel.getUniqueId());
        if (removedLock != null) {
            removedLock.remove();
            this.setLocksDirty();
        }
    }

    public void drag(UUID playerUUID, UUID subLevelUUID, Vector3dc globalAnchor, Vector3dc localAnchor, Quaterniondc orientation) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)this.level);
        SubLevel subLevel = container.getSubLevel(subLevelUUID);
        if (subLevel == null) {
            return;
        }
        this.removeLock(subLevel);
        DragSession session = this.draggingSessions.get(playerUUID);
        if (session == null) {
            session = new DragSession(playerUUID, (ServerSubLevel)subLevel);
            this.draggingSessions.put(playerUUID, session);
            this.markDraggingSessionsDirty();
        }
        session.playerRelativeGoal.set(globalAnchor);
        session.plotAnchor.set(localAnchor);
        session.orientation.set(orientation);
    }

    public void stopDragging(UUID playerUUID) {
        DragSession session = this.draggingSessions.remove(playerUUID);
        if (session != null) {
            this.markDraggingSessionsDirty();
            session.onRemoved();
        }
    }

    private static class DragSession {
        private final UUID playerUUID;
        private final Vector3d plotAnchor = new Vector3d();
        private final Vector3d playerRelativeGoal = new Vector3d();
        private final Vector3d localGoal = new Vector3d();
        private final Quaterniond orientation = new Quaterniond();
        private final ServerSubLevel subLevel;
        private boolean markedForRemoval = false;
        private PhysicsConstraintHandle constraint = null;

        private DragSession(UUID playerUUID, ServerSubLevel subLevel) {
            this.playerUUID = playerUUID;
            this.subLevel = subLevel;
        }

        private void tick() {
            if (this.subLevel.isRemoved()) {
                this.markForRemoval();
            }
        }

        private void physicsTick(SubLevelPhysicsSystem physicsSystem) {
            if (this.subLevel.isRemoved()) {
                return;
            }
            if (this.constraint != null) {
                this.constraint.remove();
                this.constraint = null;
            }
            this.attachConstraint(physicsSystem);
            Player player = this.subLevel.getLevel().getPlayerByUUID(this.playerUUID);
            SimPhysics config = SimConfigService.INSTANCE.server().physics;
            if (player != null && this.constraint != null) {
                float angularStiffness = config.physicsStaffAngularStiffness.getF();
                float angularDamping = config.physicsStaffAngularDamping.getF();
                float linearStiffness = config.physicsStaffLinearStiffness.getF();
                float linearDamping = config.physicsStaffLinearDamping.getF();
                for (ConstraintJointAxis angularAxis : ConstraintJointAxis.ANGULAR) {
                    this.constraint.setMotor(angularAxis, 0.0, (double)angularStiffness, (double)angularDamping, false, 0.0);
                }
                double partialTick = physicsSystem.getPartialPhysicsTick();
                double eyePosX = Mth.lerp((double)partialTick, (double)player.xOld, (double)player.getX());
                double eyePosY = Mth.lerp((double)partialTick, (double)player.yOld, (double)player.getY()) + (double)player.getEyeHeight();
                double eyePosZ = Mth.lerp((double)partialTick, (double)player.zOld, (double)player.getZ());
                this.localGoal.set((Vector3dc)this.playerRelativeGoal).add(eyePosX, eyePosY, eyePosZ);
                this.orientation.transformInverse(this.localGoal);
                this.constraint.setMotor(ConstraintJointAxis.LINEAR_X, this.localGoal.x(), (double)linearStiffness, (double)linearDamping, false, 0.0);
                this.constraint.setMotor(ConstraintJointAxis.LINEAR_Y, this.localGoal.y(), (double)linearStiffness, (double)linearDamping, false, 0.0);
                this.constraint.setMotor(ConstraintJointAxis.LINEAR_Z, this.localGoal.z(), (double)linearStiffness, (double)linearDamping, false, 0.0);
            }
        }

        private void attachConstraint(SubLevelPhysicsSystem physicsSystem) {
            PhysicsPipeline pipeline = physicsSystem.getPipeline();
            FreeConstraintConfiguration config = new FreeConstraintConfiguration(JOMLConversion.ZERO, (Vector3dc)this.plotAnchor, (Quaterniondc)this.orientation);
            this.constraint = pipeline.addConstraint(null, this.subLevel, (PhysicsConstraintConfiguration)config);
        }

        public boolean isMarkedForRemoval() {
            return this.markedForRemoval;
        }

        public void markForRemoval() {
            this.markedForRemoval = true;
        }

        public void onRemoved() {
            if (this.constraint != null) {
                this.constraint.remove();
            }
            this.constraint = null;
        }
    }

    private record Lock(@NotNull UUID subLevel, @Nullable PhysicsConstraintHandle handle) {
        private void remove() {
            if (this.handle != null) {
                this.handle.remove();
            }
        }
    }
}
