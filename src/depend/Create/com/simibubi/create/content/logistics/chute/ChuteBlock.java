/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import java.util.HashMap;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

public class ChuteBlock
extends AbstractChuteBlock
implements ProperWaterloggedBlock {
    public static final Property<Shape> SHAPE = EnumProperty.create((String)"shape", Shape.class);
    public static final DirectionProperty FACING = BlockStateProperties.FACING_HOPPER;

    public ChuteBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(SHAPE, (Comparable)((Object)Shape.NORMAL))).setValue((Property)FACING, (Comparable)Direction.DOWN)).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    public Direction getFacing(BlockState state) {
        return (Direction)state.getValue((Property)FACING);
    }

    @Override
    public boolean isOpen(BlockState state) {
        return state.getValue((Property)FACING) == Direction.DOWN || state.getValue(SHAPE) == Shape.INTERSECTION;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return state.getValue(SHAPE) == Shape.WINDOW;
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        boolean down;
        Shape shape = (Shape)((Object)state.getValue(SHAPE));
        boolean bl = down = state.getValue((Property)FACING) == Direction.DOWN;
        if (shape == Shape.INTERSECTION) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (shape == Shape.ENCASED) {
            level.setBlockAndUpdate(context.getClickedPos(), (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.NORMAL)));
            level.levelEvent(2001, context.getClickedPos(), Block.getId((BlockState)AllBlocks.INDUSTRIAL_IRON_BLOCK.getDefaultState()));
            return InteractionResult.SUCCESS;
        }
        if (down) {
            level.setBlockAndUpdate(context.getClickedPos(), (BlockState)state.setValue(SHAPE, (Comparable)((Object)(shape != Shape.NORMAL ? Shape.NORMAL : Shape.WINDOW))));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Shape shape = (Shape)((Object)state.getValue(SHAPE));
        if (!AllBlocks.INDUSTRIAL_IRON_BLOCK.isIn(stack)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (shape == Shape.INTERSECTION || shape == Shape.ENCASED) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (player == null || level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        level.setBlockAndUpdate(pos, (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.ENCASED)));
        level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS, 0.5f, 1.05f);
        return ItemInteractionResult.SUCCESS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.withWater(super.getStateForPlacement(ctx), ctx);
        Direction face = ctx.getClickedFace();
        if (face.getAxis().isHorizontal() && !ctx.isSecondaryUseActive()) {
            Level world = ctx.getLevel();
            BlockPos pos = ctx.getClickedPos();
            return this.updateChuteState((BlockState)state.setValue((Property)FACING, (Comparable)face), world.getBlockState(pos.above()), (BlockGetter)world, pos);
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState above, LevelAccessor world, BlockPos pos, BlockPos p_196271_6_) {
        this.updateWater(world, state, pos);
        return super.updateShape(state, direction, above, world, pos, p_196271_6_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(new Property[]{SHAPE, FACING, WATERLOGGED}));
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState above = world.getBlockState(pos.above());
        return !ChuteBlock.isChute(above) || ChuteBlock.getChuteFacing(above) == Direction.DOWN;
    }

    @Override
    public BlockState updateChuteState(BlockState state, BlockState above, BlockGetter world, BlockPos pos) {
        boolean noConnections;
        BlockState target;
        boolean vertical;
        if (!(state.getBlock() instanceof ChuteBlock)) {
            return state;
        }
        HashMap<BlockState, Boolean> connections = new HashMap<BlockState, Boolean>();
        int amtConnections = 0;
        Direction facing = (Direction)state.getValue((Property)FACING);
        boolean bl = vertical = facing == Direction.DOWN;
        if (!vertical && !ChuteBlock.isChute(target = world.getBlockState(pos.below().relative(facing.getOpposite())))) {
            return (BlockState)((BlockState)state.setValue((Property)FACING, (Comparable)Direction.DOWN)).setValue(SHAPE, (Comparable)((Object)Shape.NORMAL));
        }
        for (BlockState direction : Iterate.horizontalDirections) {
            BlockState diagonalInputChute = world.getBlockState(pos.above().relative((Direction)direction));
            boolean value = diagonalInputChute.getBlock() instanceof ChuteBlock && diagonalInputChute.getValue((Property)FACING) == direction;
            connections.put(direction, value);
            if (!value) continue;
            ++amtConnections;
        }
        boolean bl2 = noConnections = amtConnections == 0;
        if (vertical) {
            return (BlockState)state.setValue(SHAPE, (Comparable)((Object)(noConnections ? (state.getValue(SHAPE) == Shape.INTERSECTION ? Shape.NORMAL : (Shape)((Object)state.getValue(SHAPE))) : Shape.INTERSECTION)));
        }
        if (noConnections) {
            return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.INTERSECTION));
        }
        if (((Boolean)connections.get(Direction.NORTH)).booleanValue() && ((Boolean)connections.get(Direction.SOUTH)).booleanValue()) {
            return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.INTERSECTION));
        }
        if (((Boolean)connections.get(Direction.EAST)).booleanValue() && ((Boolean)connections.get(Direction.WEST)).booleanValue()) {
            return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.INTERSECTION));
        }
        if (amtConnections == 1 && ((Boolean)connections.get(facing)).booleanValue() && ChuteBlock.getChuteFacing(above) != Direction.DOWN && (!(above.getBlock() instanceof FunnelBlock) || FunnelBlock.getFunnelFacing(above) != Direction.DOWN)) {
            return (BlockState)state.setValue(SHAPE, (Comparable)((Object)(state.getValue(SHAPE) == Shape.ENCASED ? Shape.ENCASED : Shape.NORMAL)));
        }
        return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.INTERSECTION));
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return (BlockState)pState.setValue((Property)FACING, (Comparable)pRot.rotate((Direction)pState.getValue((Property)FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation((Direction)pState.getValue((Property)FACING)));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockEntityType<? extends ChuteBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CHUTE.get();
    }

    public static enum Shape implements StringRepresentable
    {
        INTERSECTION,
        WINDOW,
        NORMAL,
        ENCASED;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
