/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.extension;

import dev.ryanhcode.sable.mixinterface.EntityExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Entity.class})
public abstract class EntityMixin
implements EntityExtension {
    @Shadow
    private Vec3 position;
    @Shadow
    private BlockPos blockPosition;
    @Shadow
    private AABB bb;

    @Shadow
    protected abstract AABB makeBoundingBox();

    @Shadow
    protected abstract Vec3 collide(Vec3 var1);

    @Override
    public void sable$setPosSuperRaw(Vec3 pos) {
        this.position = pos;
        this.blockPosition = BlockPos.containing((Position)pos);
        this.bb = this.makeBoundingBox();
    }

    @Override
    public Vec3 sable$vanillaCollide(Vec3 vec3) {
        return this.collide(vec3);
    }
}
