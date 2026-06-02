/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.turntable;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.turntable.TurntableBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurntableBlock
extends KineticBlock
implements IBE<TurntableBlockEntity> {
    public TurntableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.TURNTABLE_SHAPE;
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity e) {
        if (!e.onGround()) {
            return;
        }
        if (e.getDeltaMovement().y > 0.0) {
            return;
        }
        if (e.getY() < (double)((float)pos.getY() + 0.5f)) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            float speed = be.getSpeed() * 3.0f / 10.0f;
            if (speed == 0.0f) {
                return;
            }
            Level world = e.getCommandSenderWorld();
            if (world.isClientSide && e instanceof Player && worldIn.getBlockState(e.blockPosition()) != state) {
                Vec3 origin = VecHelper.getCenterOf((Vec3i)pos);
                Vec3 offset = e.position().subtract(origin);
                offset = VecHelper.rotate((Vec3)offset, (double)(Mth.clamp((float)speed, (float)-16.0f, (float)16.0f) / 1.0f), (Direction.Axis)Direction.Axis.Y);
                Vec3 movement = origin.add(offset).subtract(e.position());
                e.setDeltaMovement(e.getDeltaMovement().add(movement));
                e.hurtMarked = true;
            }
            if (e instanceof Player) {
                return;
            }
            if (world.isClientSide) {
                return;
            }
            if (e instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)e;
                float diff = e.getYHeadRot() - speed;
                livingEntity.setNoActionTime(20);
                e.setYBodyRot(diff);
                e.setYHeadRot(diff);
                e.setOnGround(false);
                e.hurtMarked = true;
            }
            e.setYRot(e.getYRot() - speed);
        });
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<TurntableBlockEntity> getBlockEntityClass() {
        return TurntableBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TurntableBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.TURNTABLE.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
