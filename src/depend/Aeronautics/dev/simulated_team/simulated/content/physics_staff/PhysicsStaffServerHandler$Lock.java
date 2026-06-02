/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.physics_staff;

import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

private record PhysicsStaffServerHandler.Lock(@NotNull UUID subLevel, @Nullable PhysicsConstraintHandle handle) {
    private void remove() {
        if (this.handle != null) {
            this.handle.remove();
        }
    }
}
