/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package dev.ryanhcode.sable.mixin.respawn_point.sleeping;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={ServerPlayer.class})
public abstract class ServerPlayerMixin
extends Entity {
    public ServerPlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Overwrite
    private boolean isReachableBedBlock(BlockPos blockPos) {
        Vec3 bedPos = Vec3.atBottomCenterOf((Vec3i)blockPos);
        Vec3 pos = this.position();
        SubLevel subLevel = Sable.HELPER.getContaining(this.level(), (Vec3i)blockPos);
        if (subLevel != null) {
            pos = subLevel.logicalPose().transformPositionInverse(pos);
        }
        return Math.abs(pos.x - bedPos.x()) <= 3.0 && Math.abs(pos.y - bedPos.y()) <= 2.0 && Math.abs(pos.z - bedPos.z()) <= 3.0;
    }
}
