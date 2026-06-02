/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  dev.ryanhcode.sable.api.physics.PhysicsPipeline
 *  dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle
 *  dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.object.rope.RopeHandle$AttachmentPoint
 *  dev.ryanhcode.sable.api.physics.object.rope.RopePhysicsObject
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  dev.ryanhcode.sable.sublevel.system.ticket.PhysicsChunkTicketManager
 *  foundry.veil.api.util.CodecUtil
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.server;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.object.rope.RopeHandle;
import dev.ryanhcode.sable.api.physics.object.rope.RopePhysicsObject;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.sublevel.system.ticket.PhysicsChunkTicketManager;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import foundry.veil.api.util.CodecUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ServerRopeStrand
extends RopePhysicsObject {
    public static final Codec<ServerRopeStrand> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("uuid").forGetter(ServerRopeStrand::getUUID), (App)CodecUtil.VECTOR3D_CODEC.listOf().fieldOf("points").forGetter(strand -> List.copyOf(strand.getPoints())), (App)RopeAttachment.CODEC.listOf().fieldOf("attachments").forGetter(strand -> List.copyOf(strand.attachments.values())), (App)Codec.DOUBLE.fieldOf("extension_goal").forGetter(strand -> strand.extensionGoal)).apply((Applicative)instance, (uuid, points, attachments, extensionGoal) -> {
        ObjectArrayList newPoints = new ObjectArrayList();
        for (Vector3dc point : points) {
            newPoints.add((Object)new Vector3d(point));
        }
        ServerRopeStrand strand = new ServerRopeStrand((UUID)uuid, (Collection<Vector3d>)newPoints);
        for (RopeAttachment attachment : attachments) {
            strand.attachments.put(attachment.point(), attachment);
        }
        strand.extensionGoal = extensionGoal;
        strand.lastExtensionGoal = extensionGoal;
        return strand;
    }));
    public static final double SEGMENT_LENGTH = 1.0;
    private final UUID uuid;
    private final Map<RopeAttachmentPoint, RopeAttachment> attachments = new Object2ObjectOpenHashMap();
    private final List<Vector3d> lastNetworkedPoints = new ObjectArrayList();
    private int lastPointCount = 0;
    @Nullable
    private PhysicsConstraintHandle constraint = null;
    private double lastExtensionGoal = 1.0;
    private double extensionGoal = 1.0;
    private double lastFirstSegmentExtension = 1.0;
    private boolean attachmentsDirty;
    public boolean networkingStopped;
    private final Set<UUID> trackingPlayers = new ObjectOpenHashSet();

    public ServerRopeStrand(UUID uuid, Collection<Vector3d> points) {
        super(points, 0.125);
        this.uuid = uuid;
    }

    public boolean needsSync() {
        if (this.lastNetworkedPoints.size() != this.points.size()) {
            return true;
        }
        double threshold = Mth.square((double)0.00625);
        for (int i = 0; i < this.points.size(); ++i) {
            if (!(((Vector3d)this.points.get(i)).distanceSquared((Vector3dc)this.lastNetworkedPoints.get(i)) > threshold)) continue;
            return true;
        }
        return false;
    }

    public void justSynced() {
        this.lastNetworkedPoints.clear();
        for (Vector3d point : this.points) {
            this.lastNetworkedPoints.add(new Vector3d((Vector3dc)point));
        }
    }

    public void updateFirstSegmentExtension(double extensionGoal) {
        this.lastExtensionGoal = this.extensionGoal;
        this.extensionGoal = extensionGoal;
    }

    public Set<UUID> getTrackingPlayers() {
        return this.trackingPlayers;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean isOwnerLoaded(ServerLevel level) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)level);
        assert (container != null);
        RopeAttachment attachment = this.attachments.get((Object)RopeAttachmentPoint.START);
        UUID subLevelID = attachment.subLevelID();
        if (subLevelID != null && container.getSubLevel(subLevelID) == null) {
            return false;
        }
        BlockPos blockPos = attachment.blockAttachment();
        return PhysicsChunkTicketManager.isChunkLoadedEnough((ServerLevel)level, (int)(blockPos.getX() >> 4), (int)(blockPos.getZ() >> 4));
    }

    public boolean areAttachmentsLoaded(ServerLevel level) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)level);
        assert (container != null);
        Collection<RopeAttachment> attachments = this.attachments.values();
        for (RopeAttachment attachment : attachments) {
            UUID subLevelID = attachment.subLevelID();
            if (subLevelID != null) {
                return container.getSubLevel(subLevelID) != null;
            }
            BlockPos blockPos = attachment.blockAttachment();
            if (PhysicsChunkTicketManager.isChunkLoadedEnough((ServerLevel)level, (int)(blockPos.getX() >> 4), (int)(blockPos.getZ() >> 4))) continue;
            return false;
        }
        return true;
    }

    public double getCurrentExtension() {
        double totalExtension = 0.0;
        for (int i = 0; i < this.points.size() - 1; ++i) {
            Vector3d a = (Vector3d)this.points.get(i);
            Vector3d b = (Vector3d)this.points.get(i + 1);
            totalExtension += a.distance((Vector3dc)b);
        }
        return totalExtension;
    }

    public void addAttachment(ServerLevel level, RopeAttachmentPoint point, RopeAttachment ropeAttachment) {
        this.attachments.put(point, ropeAttachment);
        this.removeConstraints();
        if (this.isActive()) {
            this.applyAttachment(ropeAttachment, level);
        }
    }

    public void removeConstraints() {
        if (this.constraint != null) {
            this.constraint.remove();
        }
        this.constraint = null;
    }

    public void reattachConstraints(ServerLevel level) {
        ServerSubLevel subLevelB;
        this.removeConstraints();
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)level);
        assert (container != null);
        RopeAttachment start = this.attachments.get((Object)RopeAttachmentPoint.START);
        RopeAttachment end = this.attachments.get((Object)RopeAttachmentPoint.END);
        if (start == null || end == null) {
            return;
        }
        UUID idA = start.subLevelID();
        UUID idB = end.subLevelID();
        ServerSubLevel subLevelA = idA != null ? (ServerSubLevel)container.getSubLevel(idA) : null;
        ServerSubLevel serverSubLevel = subLevelB = idB != null ? (ServerSubLevel)container.getSubLevel(idB) : null;
        if (subLevelA == subLevelB) {
            return;
        }
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        PhysicsPipeline pipeline = physicsSystem.getPipeline();
        FreeConstraintConfiguration config = new FreeConstraintConfiguration((Vector3dc)JOMLConversion.toJOML((Position)start.blockAttachment().getCenter()), (Vector3dc)JOMLConversion.toJOML((Position)end.blockAttachment().getCenter()), (Quaterniondc)new Quaterniond());
        this.constraint = pipeline.addConstraint(subLevelA, subLevelB, (PhysicsConstraintConfiguration)config);
        for (ConstraintJointAxis angularAxis : ConstraintJointAxis.ANGULAR) {
            this.constraint.setMotor(angularAxis, 0.0, 0.0, 1.3, false, 0.0);
        }
        for (ConstraintJointAxis linearAxis : ConstraintJointAxis.LINEAR) {
            this.constraint.setMotor(linearAxis, 0.0, 0.0, 0.25, false, 0.0);
        }
    }

    public Iterable<RopeAttachment> getAttachments() {
        return this.attachments.values();
    }

    public RopeAttachment getAttachment(RopeAttachmentPoint point) {
        return this.attachments.get((Object)point);
    }

    public void onRemoved() {
        super.onRemoved();
        this.removeConstraints();
    }

    public void onUnloaded(SubLevelHoldingChunkMap holdingChunkMap, ChunkPos chunkPos) {
        super.onUnloaded(holdingChunkMap, chunkPos);
        this.removeConstraints();
    }

    public void onAddition(SubLevelPhysicsSystem physicsSystem) {
        super.onAddition(physicsSystem);
        this.setFirstSegmentLength(this.extensionGoal);
        ServerLevel level = physicsSystem.getLevel();
        for (RopeAttachment attachment : this.attachments.values()) {
            this.applyAttachment(attachment, level);
        }
    }

    private void applyAttachment(RopeAttachment attachment, ServerLevel level) {
        RopeAttachmentPoint point = attachment.point();
        RopeHandle.AttachmentPoint sableAttachmentPoint = point == RopeAttachmentPoint.END ? RopeHandle.AttachmentPoint.END : RopeHandle.AttachmentPoint.START;
        BlockPos blockAttachment = attachment.blockAttachment();
        BlockEntity blockEntity = level.getBlockEntity(blockAttachment);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity smartBlockEntity = (SmartBlockEntity)blockEntity;
        RopeStrandHolderBehavior ropeHolder = (RopeStrandHolderBehavior)smartBlockEntity.getBehaviour(RopeStrandHolderBehavior.TYPE);
        if (ropeHolder == null) {
            return;
        }
        Vector3d attachmentPoint = JOMLConversion.toJOML((Position)ropeHolder.getAttachmentPoint());
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)level);
        SubLevel subLevel = attachment.subLevelID() != null ? Objects.requireNonNull(container.getSubLevel(attachment.subLevelID())) : null;
        this.setAttachment(sableAttachmentPoint, (Vector3dc)attachmentPoint, (ServerSubLevel)subLevel);
    }

    public double getExtension() {
        return this.extensionGoal;
    }

    public void prePhysicsTick(SubLevelPhysicsSystem physicsSystem, ServerLevel level, double timeStep) {
        double extension;
        if (this.constraint == null || !this.constraint.isValid()) {
            this.reattachConstraints(physicsSystem.getLevel());
        }
        if (this.points.size() != this.lastPointCount) {
            this.lastExtensionGoal = this.extensionGoal;
            this.lastPointCount = this.points.size();
        }
        if (!Mth.equal((double)(extension = Mth.lerp((double)physicsSystem.getPartialPhysicsTick(), (double)this.lastExtensionGoal, (double)this.extensionGoal)), (double)this.lastFirstSegmentExtension)) {
            this.setFirstSegmentLength(extension);
            this.lastFirstSegmentExtension = extension;
        }
        if (this.attachmentsDirty) {
            for (RopeAttachment attachment : this.attachments.values()) {
                this.applyAttachment(attachment, level);
            }
            this.attachmentsDirty = false;
        }
    }

    public void removeFirstPoint() {
        super.removeFirstPoint();
        this.attachmentsDirty = true;
    }

    public void addPoint(Vector3dc position) {
        super.addPoint(position);
        this.attachmentsDirty = true;
    }
}
