/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.physics.impl.rapier.constraint.rotary;

import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.RapierConstraintHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class RapierRotaryConstraintHandle
extends RapierConstraintHandle
implements RotaryConstraintHandle {
    public static RapierRotaryConstraintHandle create(ServerLevel serverLevel, @Nullable ServerSubLevel sublevelA, @Nullable ServerSubLevel sublevelB, RotaryConstraintConfiguration config) {
        int sceneID = Rapier3D.getID(serverLevel);
        long handle = Rapier3D.addRotaryConstraint(sceneID, sublevelA == null ? -1 : Rapier3D.getID(sublevelA), sublevelB == null ? -1 : Rapier3D.getID(sublevelB), config.pos1().x(), config.pos1().y(), config.pos1().z(), config.pos2().x(), config.pos2().y(), config.pos2().z(), config.normal1().x(), config.normal1().y(), config.normal1().z(), config.normal2().x(), config.normal2().y(), config.normal2().z());
        return new RapierRotaryConstraintHandle(sceneID, handle);
    }

    public RapierRotaryConstraintHandle(int sceneID, long handle) {
        super(sceneID, handle);
    }
}
