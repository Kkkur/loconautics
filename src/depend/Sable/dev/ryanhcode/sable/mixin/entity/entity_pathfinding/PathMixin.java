/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.pathfinder.Path
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_pathfinding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinterface.entity.pathfinding.PathExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Path.class})
public class PathMixin
implements PathExtension {
    @Unique
    private Level sable$level;
    @Unique
    private boolean sable$project;

    @Inject(method={"getNextEntityPos"}, at={@At(value="RETURN")}, cancellable=true)
    private void sable$getNextEntityPos(Entity entity, CallbackInfoReturnable<Vec3> cir) {
        if (!this.sable$project) {
            return;
        }
        cir.setReturnValue((Object)Sable.HELPER.projectOutOfSubLevel(entity.level(), (Vec3)cir.getReturnValue()));
    }

    @Inject(method={"getNextNodePos"}, at={@At(value="RETURN")}, cancellable=true)
    private void sable$getNextNodePos(CallbackInfoReturnable<BlockPos> cir) {
        if (!this.sable$project) {
            return;
        }
        BlockPos blockPos = (BlockPos)cir.getReturnValue();
        SubLevel subLevel = Sable.HELPER.getContaining(this.sable$level, (Vec3i)blockPos);
        if (subLevel == null) {
            return;
        }
        BlockPos global = BlockPos.containing((Position)subLevel.logicalPose().transformPosition(blockPos.getCenter()));
        cir.setReturnValue((Object)global);
    }

    @Inject(method={"getNodePos"}, at={@At(value="RETURN")}, cancellable=true)
    private void sable$getNodePos(int i, CallbackInfoReturnable<BlockPos> cir) {
        if (!this.sable$project) {
            return;
        }
        BlockPos blockPos = (BlockPos)cir.getReturnValue();
        SubLevel subLevel = Sable.HELPER.getContaining(this.sable$level, (Vec3i)blockPos);
        if (subLevel == null) {
            return;
        }
        BlockPos global = BlockPos.containing((Position)subLevel.logicalPose().transformPosition(blockPos.getCenter()));
        cir.setReturnValue((Object)global);
    }

    @Override
    public void sable$setLocalPath(Level level, boolean project) {
        this.sable$level = level;
        this.sable$project = project;
    }
}
