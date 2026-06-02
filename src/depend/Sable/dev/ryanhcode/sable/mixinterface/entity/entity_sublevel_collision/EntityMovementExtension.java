/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision;

import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface EntityMovementExtension {
    public SubLevelEntityCollision.CollisionInfo sable$getCollisionInfo();

    public SubLevel sable$getTrackingSubLevel();

    public UUID sable$getLastTrackingSubLevelID();

    public void sable$setPosField(Vec3 var1);

    public void sable$setTrackingSubLevel(SubLevel var1);

    public void sable$setLastTrackingSubLevelID(UUID var1);

    public BlockPos sable$getInBlockStatePos();
}
