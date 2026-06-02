/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.jetbrains.annotations.ApiStatus$OverrideOnly
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics;

import dev.ryanhcode.sable.api.physics.PhysicsPipelineBody;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.object.box.BoxHandle;
import dev.ryanhcode.sable.api.physics.object.box.BoxPhysicsObject;
import dev.ryanhcode.sable.api.physics.object.rope.RopeHandle;
import dev.ryanhcode.sable.api.physics.object.rope.RopePhysicsObject;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.physics.config.PhysicsConfigData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface PhysicsPipeline {
    public void init(Vector3dc var1, double var2);

    public void dispose();

    public void prePhysicsTicks();

    public void physicsTick(double var1);

    public void postPhysicsTicks();

    public void tick();

    public void add(ServerSubLevel var1, Pose3dc var2);

    public void remove(ServerSubLevel var1);

    public void add(KinematicContraption var1);

    public void remove(KinematicContraption var1);

    @ApiStatus.OverrideOnly
    public Pose3d readPose(ServerSubLevel var1, Pose3d var2);

    @ApiStatus.OverrideOnly
    public RopeHandle addRope(RopePhysicsObject var1);

    public BoxHandle addBox(BoxPhysicsObject var1);

    public void handleChunkSectionAddition(LevelChunkSection var1, int var2, int var3, int var4, boolean var5);

    public void handleChunkSectionRemoval(int var1, int var2, int var3);

    public void handleBlockChange(SectionPos var1, LevelChunkSection var2, int var3, int var4, int var5, BlockState var6, BlockState var7);

    default public void onStatsChanged(@NotNull ServerSubLevel serverSubLevel) {
    }

    public void teleport(PhysicsPipelineBody var1, Vector3dc var2, Quaterniondc var3);

    public void applyImpulse(PhysicsPipelineBody var1, Vector3dc var2, Vector3dc var3);

    public void applyLinearAndAngularImpulse(PhysicsPipelineBody var1, Vector3dc var2, Vector3dc var3, boolean var4);

    default public void addLinearAndAngularVelocity(PhysicsPipelineBody body, Vector3dc linearVelocity, Vector3dc angularVelocity) {
    }

    default public void resetVelocity(PhysicsPipelineBody body) {
        this.addLinearAndAngularVelocity(body, (Vector3dc)this.getLinearVelocity(body, new Vector3d()).negate(), (Vector3dc)this.getAngularVelocity(body, new Vector3d()).negate());
    }

    default public Vector3d getLinearVelocity(PhysicsPipelineBody body, Vector3d dest) {
        return dest.zero();
    }

    default public Vector3d getAngularVelocity(PhysicsPipelineBody body, Vector3d dest) {
        return dest.zero();
    }

    public void wakeUp(PhysicsPipelineBody var1);

    default public <T extends PhysicsConstraintHandle> T addConstraint(@Nullable ServerSubLevel sublevelA, @Nullable ServerSubLevel sublevelB, PhysicsConstraintConfiguration<T> configuration) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @ApiStatus.OverrideOnly
    default public void updateConfigFrom(PhysicsConfigData data) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int getNextRuntimeID();
}
