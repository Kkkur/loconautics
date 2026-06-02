/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.lasers;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractLaserBlockEntity
extends SmartBlockEntity {
    public AbstractLaserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract Direction getDirection();

    public Vec3i getNormal() {
        return this.getDirection().getNormal();
    }

    public abstract float getLaserRange();

    public abstract boolean shouldCast();

    public Couple<Vec3> gatherStartAndEnd() {
        Vec3i normal = this.getNormal();
        Vec3 start = Vec3.atCenterOf((Vec3i)this.worldPosition).add(Vec3.atLowerCornerOf((Vec3i)normal).scale(0.5));
        Vec3 end = start.add(Vec3.atLowerCornerOf((Vec3i)normal).scale((double)this.getLaserRange()));
        return Couple.create((Object)start, (Object)end);
    }

    public AABB getRenderBoundingBox() {
        int range = (int)this.getLaserRange();
        Vec3i normal = this.getNormal();
        return new AABB(this.getBlockPos()).expandTowards(Vec3.atLowerCornerOf((Vec3i)normal.multiply(range)));
    }
}
