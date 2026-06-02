/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.physics.impl.rapier.constraint.free;

import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.RapierConstraintHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class RapierFreeConstraintHandle
extends RapierConstraintHandle
implements FreeConstraintHandle {
    public static RapierFreeConstraintHandle create(ServerLevel serverLevel, @Nullable ServerSubLevel sublevelA, @Nullable ServerSubLevel sublevelB, FreeConstraintConfiguration config) {
        int sceneID = Rapier3D.getID(serverLevel);
        long handle = Rapier3D.addFreeConstraint(sceneID, sublevelA == null ? -1 : Rapier3D.getID(sublevelA), sublevelB == null ? -1 : Rapier3D.getID(sublevelB), config.pos1().x(), config.pos1().y(), config.pos1().z(), config.pos2().x(), config.pos2().y(), config.pos2().z(), config.orientation().x(), config.orientation().y(), config.orientation().z(), config.orientation().w());
        return new RapierFreeConstraintHandle(sceneID, handle);
    }

    public RapierFreeConstraintHandle(int sceneID, long handle) {
        super(sceneID, handle);
    }
}
