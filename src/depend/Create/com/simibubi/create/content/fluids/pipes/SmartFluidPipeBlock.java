/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.network.protocol.game.DebugPackets
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.ticks.TickPriority
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.fluids.pipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.IAxisPipe;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

public class SmartFluidPipeBlock
extends FaceAttachedHorizontalDirectionalBlock
implements IBE<SmartFluidPipeBlockEntity>,
IAxisPipe,
IWrenchable,
ProperWaterloggedBlock {
    public static final MapCodec<SmartFluidPipeBlock> CODEC = SmartFluidPipeBlock.simpleCodec(SmartFluidPipeBlock::new);

    public SmartFluidPipeBlock(BlockBehaviour.Properties p_i48339_1_) {
        super(p_i48339_1_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACE, FACING, WATERLOGGED});
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState stateForPlacement = super.getStateForPlacement(ctx);
        Direction.Axis prefferedAxis = null;
        BlockPos pos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        for (Direction side : Iterate.directions) {
            if (!this.prefersConnectionTo((LevelReader)world, pos, side)) continue;
            if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                prefferedAxis = null;
                break;
            }
            prefferedAxis = side.getAxis();
        }
        if (prefferedAxis == Direction.Axis.Y) {
            stateForPlacement = (BlockState)((BlockState)stateForPlacement.setValue((Property)FACE, (Comparable)AttachFace.WALL)).setValue((Property)FACING, (Comparable)((Direction)stateForPlacement.getValue((Property)FACING)).getOpposite());
        } else if (prefferedAxis != null) {
            if (stateForPlacement.getValue((Property)FACE) == AttachFace.WALL) {
                stateForPlacement = (BlockState)stateForPlacement.setValue((Property)FACE, (Comparable)AttachFace.FLOOR);
            }
            for (Direction direction : ctx.getNearestLookingDirections()) {
                if (direction.getAxis() != prefferedAxis) continue;
                stateForPlacement = (BlockState)stateForPlacement.setValue((Property)FACING, (Comparable)direction.getOpposite());
            }
        }
        return this.withWater(stateForPlacement, ctx);
    }

    protected boolean prefersConnectionTo(LevelReader reader, BlockPos pos, Direction facing) {
        BlockPos offset = pos.relative(facing);
        BlockState blockState = reader.getBlockState(offset);
        return FluidPipeBlock.canConnectTo((BlockAndTintGetter)reader, offset, blockState, facing);
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean blockTypeChanged;
        boolean bl = blockTypeChanged = state.getBlock() != newState.getBlock();
        if (blockTypeChanged && !world.isClientSide) {
            FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
        }
        IBE.onRemove(state, world, pos, newState);
    }

    public boolean canSurvive(BlockState p_196260_1_, LevelReader p_196260_2_, BlockPos p_196260_3_) {
        return true;
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (world.isClientSide) {
            return;
        }
        if (state != oldState) {
            world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
        }
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
        DebugPackets.sendNeighborsUpdatePacket((Level)world, (BlockPos)pos);
        Direction d = FluidPropagator.validateNeighbourChange(state, world, pos, otherBlock, neighborPos, isMoving);
        if (d == null) {
            return;
        }
        if (!SmartFluidPipeBlock.isOpenAt(state, d)) {
            return;
        }
        world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
    }

    public static boolean isOpenAt(BlockState state, Direction d) {
        return d.getAxis() == SmartFluidPipeBlock.getPipeAxis(state);
    }

    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource r) {
        FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
    }

    protected static Direction.Axis getPipeAxis(BlockState state) {
        return state.getValue((Property)FACE) == AttachFace.WALL ? Direction.Axis.Y : ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        AttachFace face = (AttachFace)state.getValue((Property)FACE);
        VoxelShaper shape = face == AttachFace.FLOOR ? AllShapes.SMART_FLUID_PIPE_FLOOR : (face == AttachFace.CEILING ? AllShapes.SMART_FLUID_PIPE_CEILING : AllShapes.SMART_FLUID_PIPE_WALL);
        return shape.get((Direction)state.getValue((Property)FACING));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    @Override
    public Direction.Axis getAxis(BlockState state) {
        return SmartFluidPipeBlock.getPipeAxis(state);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    @Override
    public Class<SmartFluidPipeBlockEntity> getBlockEntityClass() {
        return SmartFluidPipeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmartFluidPipeBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.SMART_FLUID_PIPE.get();
    }

    @NotNull
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
