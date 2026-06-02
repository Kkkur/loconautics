/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniondc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.impl.rapier.constraint.generic;

import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.RapierConstraintHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

@ApiStatus.Internal
public class RapierGenericConstraintHandle
extends RapierConstraintHandle
implements GenericConstraintHandle {
    private static final int FRAME_SIDE_FIRST = 0;
    private static final int FRAME_SIDE_SECOND = 1;

    public static RapierGenericConstraintHandle create(ServerLevel serverLevel, @Nullable ServerSubLevel sublevelA, @Nullable ServerSubLevel sublevelB, GenericConstraintConfiguration config) {
        int sceneID = Rapier3D.getID(serverLevel);
        int lockedAxesMask = 0;
        for (ConstraintJointAxis axis : config.lockedAxes()) {
            lockedAxesMask |= 1 << axis.ordinal();
        }
        long handle = Rapier3D.addGenericConstraint(sceneID, sublevelA == null ? -1 : Rapier3D.getID(sublevelA), sublevelB == null ? -1 : Rapier3D.getID(sublevelB), config.pos1().x(), config.pos1().y(), config.pos1().z(), config.orientation1().x(), config.orientation1().y(), config.orientation1().z(), config.orientation1().w(), config.pos2().x(), config.pos2().y(), config.pos2().z(), config.orientation2().x(), config.orientation2().y(), config.orientation2().z(), config.orientation2().w(), lockedAxesMask);
        return new RapierGenericConstraintHandle(sceneID, handle);
    }

    public RapierGenericConstraintHandle(int sceneID, long handle) {
        super(sceneID, handle);
    }

    @Override
    public void setFrame1(Vector3dc localPosition, Quaterniondc localRotation) {
        Rapier3D.setConstraintFrame(this.sceneID, this.handle, 0, localPosition.x(), localPosition.y(), localPosition.z(), localRotation.x(), localRotation.y(), localRotation.z(), localRotation.w());
    }

    @Override
    public void setFrame2(Vector3dc localPosition, Quaterniondc localRotation) {
        Rapier3D.setConstraintFrame(this.sceneID, this.handle, 1, localPosition.x(), localPosition.y(), localPosition.z(), localRotation.x(), localRotation.y(), localRotation.z(), localRotation.w());
    }
}
