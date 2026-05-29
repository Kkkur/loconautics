/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.shapes.CollisionContext
 */
package dev.ryanhcode.sable.api.physics.collider;

import dev.ryanhcode.sable.physics.impl.SableCollisionContextImpl;
import net.minecraft.world.phys.shapes.CollisionContext;

public interface SableCollisionContext
extends CollisionContext {
    public static SableCollisionContext get() {
        return SableCollisionContextImpl.INSTANCE;
    }
}
