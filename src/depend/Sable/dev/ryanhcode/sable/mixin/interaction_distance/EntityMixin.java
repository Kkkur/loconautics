/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.interaction_distance;

import dev.ryanhcode.sable.Sable;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract Level level();

    @Overwrite
    public float distanceTo(Entity entity) {
        Level level = this.level();
        double distanceSquared = Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)this.position(), (Position)entity.position());
        return (float)Math.sqrt(distanceSquared);
    }

    @Overwrite
    public double distanceToSqr(double x, double y, double z) {
        Level level = this.level();
        return Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)this.position(), x, y, z);
    }

    @Overwrite
    public double distanceToSqr(Vec3 pos) {
        Level level = this.level();
        return Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)this.position(), (Position)pos);
    }
}
