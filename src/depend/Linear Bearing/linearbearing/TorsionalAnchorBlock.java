/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.ModBlocks;
import com.bearing.linearbearing.TorsionalAnchorBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorsionalAnchorBlock
extends DirectionalKineticBlock
implements IBE<TorsionalAnchorBlockEntity>,
ICogWheel {
    public TorsionalAnchorBlock(BlockBehaviour.Properties properties) {
        super(properties.noOcclusion());
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        if (!state.hasProperty((Property)BlockStateProperties.FACING)) {
            return Direction.Axis.Y;
        }
        return ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getAxis();
    }

    public Predicate<BlockState> getStatePredicate() {
        return s -> !ICogWheel.isDedicatedCogWheel((Block)s.getBlock()) && ICogWheel.isSmallCog((BlockState)s);
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (!state.hasProperty((Property)BlockStateProperties.FACING)) {
            return false;
        }
        return face == ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getOpposite();
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.hasProperty((Property)BlockStateProperties.FACING)) {
            return Shapes.block();
        }
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        return switch (facing) {
            default -> throw new MatchException(null, null);
            case Direction.NORTH -> Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)6.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape)Block.box((double)4.0, (double)4.0, (double)0.0, (double)12.0, (double)12.0, (double)6.0));
            case Direction.SOUTH -> Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)10.0), (VoxelShape)Block.box((double)4.0, (double)4.0, (double)10.0, (double)12.0, (double)12.0, (double)16.0));
            case Direction.EAST -> Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)10.0, (double)16.0, (double)16.0), (VoxelShape)Block.box((double)10.0, (double)4.0, (double)4.0, (double)16.0, (double)12.0, (double)12.0));
            case Direction.WEST -> Shapes.or((VoxelShape)Block.box((double)6.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape)Block.box((double)0.0, (double)4.0, (double)4.0, (double)6.0, (double)12.0, (double)12.0));
            case Direction.UP -> Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)10.0, (double)16.0), (VoxelShape)Block.box((double)4.0, (double)10.0, (double)4.0, (double)12.0, (double)16.0, (double)12.0));
            case Direction.DOWN -> Shapes.or((VoxelShape)Block.box((double)0.0, (double)6.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape)Block.box((double)4.0, (double)0.0, (double)4.0, (double)12.0, (double)6.0, (double)12.0));
        };
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context == CollisionContext.empty()) {
            return Shapes.block();
        }
        return this.getShape(state, level, pos, context);
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            if (!player.isCreative()) {
                ItemStack dropStack = new ItemStack((ItemLike)this.asItem());
                if (!player.getInventory().add(dropStack)) {
                    player.drop(dropStack, false);
                }
            }
            level.destroyBlock(pos, false, (Entity)player);
        }
        return InteractionResult.SUCCESS;
    }

    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

    public Class<TorsionalAnchorBlockEntity> getBlockEntityClass() {
        return TorsionalAnchorBlockEntity.class;
    }

    public BlockEntityType<? extends TorsionalAnchorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)ModBlocks.TORSIONAL_ANCHOR_BE.get();
    }

    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    protected boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.getBlockEntityType().create(pos, state);
    }
}
