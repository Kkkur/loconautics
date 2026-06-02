/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.BlockUtil$FoundRectangle
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.portal.PortalShape
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.portal;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.BlockUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract EntityDimensions getDimensions(Pose var1);

    @Shadow
    public abstract Pose getPose();

    @Shadow
    public abstract Level level();

    @Overwrite
    public Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle foundRectangle) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.level(), (Vec3i)foundRectangle.minCorner);
        Vec3 position = this.position();
        if (subLevel != null) {
            position = subLevel.logicalPose().transformPositionInverse(position);
        }
        return PortalShape.getRelativePosition((BlockUtil.FoundRectangle)foundRectangle, (Direction.Axis)axis, (Vec3)position, (EntityDimensions)this.getDimensions(this.getPose()));
    }
}
