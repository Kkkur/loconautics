/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.EntityGetter
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.block_placement;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={EntityGetter.class})
public interface EntityGetterMixin {
    @Shadow
    public List<Entity> getEntities(@Nullable Entity var1, AABB var2);

    @Shadow
    public List<? extends Player> players();

    @Overwrite
    default public boolean isUnobstructed(@Nullable Entity pEntity, VoxelShape voxelShape) {
        if (voxelShape.isEmpty()) {
            return true;
        }
        for (Entity entity : this.getEntities(pEntity, voxelShape.bounds())) {
            AABB entityBounds = entity.getBoundingBox();
            boolean fine = Shapes.joinIsNotEmpty((VoxelShape)voxelShape, (VoxelShape)Shapes.create((AABB)entityBounds), (BooleanOp)BooleanOp.AND);
            BoundingBox3d queryBounds = new BoundingBox3d(entityBounds);
            queryBounds.expand(1.5, queryBounds);
            Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting(entity.level(), (BoundingBox3dc)queryBounds);
            for (SubLevel subLevel : intersecting) {
                if (fine) continue;
                BoundingBox3d bb = new BoundingBox3d(entityBounds);
                bb.transformInverse((Pose3dc)subLevel.logicalPose(), bb);
                bb.expand(-0.046875, bb);
                if (!Shapes.joinIsNotEmpty((VoxelShape)voxelShape, (VoxelShape)Shapes.create((AABB)bb.toMojang()), (BooleanOp)BooleanOp.AND)) continue;
                fine = true;
            }
            if (entity.isRemoved() || !entity.blocksBuilding || pEntity != null && entity.isPassengerOfSameVehicle(pEntity) || !fine) continue;
            return false;
        }
        return true;
    }
}
