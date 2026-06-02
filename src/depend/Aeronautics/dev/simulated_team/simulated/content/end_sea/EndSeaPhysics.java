/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.physics.mass.MassData
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.end_sea;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record EndSeaPhysics(ResourceLocation dimension, Optional<Integer> priority, double startY, double depthGradient, double drag) {
    public static final Codec<EndSeaPhysics> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("dimension").forGetter(EndSeaPhysics::dimension), (App)Codec.optionalField((String)"priority", (Codec)Codec.INT, (boolean)true).forGetter(EndSeaPhysics::priority), (App)Codec.DOUBLE.fieldOf("start_y").forGetter(EndSeaPhysics::startY), (App)Codec.DOUBLE.optionalFieldOf("depth_gradient", (Object)1.0).forGetter(EndSeaPhysics::depthGradient), (App)Codec.DOUBLE.optionalFieldOf("drag", (Object)1.0).forGetter(EndSeaPhysics::drag)).apply((Applicative)instance, EndSeaPhysics::new));
    public static final StreamCodec<ByteBuf, EndSeaPhysics> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public void physicsTick(double substepTimeStep, ServerLevel level) {
        BoundingBox3d bounds = new BoundingBox3d(-3.0E7, -10000.0, -3.0E7, 3.0E7, 10000.0, 3.0E7);
        Iterable intersecting = Sable.HELPER.getAllIntersecting((Level)level, (BoundingBox3dc)bounds);
        SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer((ServerLevel)level).physicsSystem();
        Vector3d tempLinearVelocity = new Vector3d();
        Vector3d tempAngularVelocity = new Vector3d();
        for (SubLevel subLevel : intersecting) {
            MassData massTracker;
            Vector3dc centerOfMass;
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            Pose3d pose = subLevel.logicalPose();
            Vector3d pos = pose.position();
            double depth = (this.startY - pos.y) * this.depthGradient;
            if (depth < 0.0 || (centerOfMass = (massTracker = serverSubLevel.getMassTracker()).getCenterOfMass()) == null) continue;
            RigidBodyHandle handle = physicsSystem.getPhysicsHandle(serverSubLevel);
            Vector3d gravity = DimensionPhysicsData.getGravity((Level)level, (Vector3dc)pos);
            QueuedForceGroup dragGroup = serverSubLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.DRAG.get());
            QueuedForceGroup levitationGroup = serverSubLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.LEVITATION.get());
            Vector3d linearDrag = handle.getLinearVelocity(tempLinearVelocity).mul(-substepTimeStep * 3.5 * this.drag);
            Vector3d angularDrag = handle.getAngularVelocity(tempAngularVelocity).mul(-substepTimeStep * 3.0 * this.drag);
            pose.transformNormalInverse(linearDrag).mul(massTracker.getMass());
            massTracker.getInertiaTensor().transform(pose.transformNormalInverse(angularDrag));
            dragGroup.recordPointForce((Vector3dc)new Vector3d(centerOfMass), (Vector3dc)linearDrag);
            dragGroup.getForceTotal().applyLinearAndAngularImpulse((Vector3dc)linearDrag, (Vector3dc)angularDrag);
            Vector3d levitationImpulse = pose.transformNormalInverse(gravity.negate(new Vector3d()).mul(Math.signum(this.depthGradient) * depth * substepTimeStep * massTracker.getMass()));
            levitationGroup.applyAndRecordPointForce(centerOfMass, (Vector3dc)levitationImpulse);
            for (UUID uuid : serverSubLevel.getTrackingPlayers()) {
                Player player = level.getPlayerByUUID(uuid);
                if (player == null || Sable.HELPER.getTrackingSubLevel((Entity)player) != subLevel) continue;
                SimAdvancements.CALL_OF_THE_VOID.awardTo(player);
            }
        }
    }
}
