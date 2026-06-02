/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.sublevel;

import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import dev.ryanhcode.sable.api.physics.mass.MassTracker;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.floating_block.FloatingClusterContainer;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public interface KinematicContraption {
    public void sable$getLocalBounds(BoundingBox3i var1);

    public BlockGetter sable$blockGetter();

    public MassTracker sable$getMassTracker();

    public Vector3dc sable$getPosition(double var1);

    public Quaterniond sable$getOrientation(double var1);

    public Map<BlockPos, BlockSubLevelLiftProvider.LiftProviderContext> sable$liftProviders();

    public FloatingClusterContainer sable$getFloatingClusterContainer();

    public boolean sable$shouldCollide();

    public boolean sable$isValid();

    default public Vector3dc sable$getPosition() {
        return this.sable$getPosition(1.0);
    }

    default public Quaterniond sable$getOrientation() {
        return this.sable$getOrientation(1.0);
    }

    default public Pose3d sable$getLocalPose(Pose3d dest, double partialTick) {
        dest.rotationPoint().set(this.sable$getMassTracker().getCenterOfMass());
        dest.position().set(this.sable$getPosition(partialTick));
        dest.orientation().set((Quaterniondc)this.sable$getOrientation(partialTick));
        dest.scale().set(JOMLConversion.ONE);
        return dest;
    }
}
