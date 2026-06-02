/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.glue.SuperGlueEntity
 *  com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.super_glue;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SuperGlueSelectionHandler.class})
public class SuperGlueSelectionHandlerMixin {
    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/glue/SuperGlueEntity;getBoundingBox()Lnet/minecraft/world/phys/AABB;", ordinal=0))
    private AABB sable$projectBoundingBox(SuperGlueEntity instance) {
        SubLevel subLevel = Sable.HELPER.getContaining((Entity)instance);
        if (subLevel != null) {
            BoundingBox3d bb = new BoundingBox3d(instance.getBoundingBox());
            return bb.transform((Pose3dc)subLevel.logicalPose(), bb).toMojang();
        }
        return instance.getBoundingBox();
    }
}
