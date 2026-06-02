/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.entity.entity_collision;

import dev.ryanhcode.sable.mixinterface.entity.entity_collision.EntityExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={CollisionContext.class})
public interface CollisionContextMixin {
    @Shadow
    public static CollisionContext empty() {
        return null;
    }

    @Overwrite
    public static CollisionContext of(Entity entity) {
        return entity != null ? ((EntityExtension)entity).sable$getCollisionContext() : CollisionContextMixin.empty();
    }
}
