/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.entity.entity_tracking;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets={"net.minecraft.server.level.ChunkMap$TrackedEntity"})
public class TrackedEntityMixin {
    @Redirect(method={"updatePlayer"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;position()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 sable$trackSubLevelEntities(Entity instance) {
        Vec3 pos = instance.position();
        SubLevel subLevel = Sable.HELPER.getContaining(instance.level(), (Position)pos);
        if (subLevel != null) {
            return subLevel.logicalPose().transformPosition(pos);
        }
        return instance.position();
    }
}
