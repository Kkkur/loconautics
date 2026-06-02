/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.redstone.link;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneLinkBlock
extends WrenchableDirectionalBlock
implements IBE<RedstoneLinkBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty RECEIVER = BooleanProperty.create((String)"receiver");

    public RedstoneLinkBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)RECEIVER, (Comparable)Boolean.valueOf(false)));
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        Direction blockFacing = (Direction)state.getValue((Property)FACING);
        if (fromPos.equals((Object)pos.relative(blockFacing.getOpposite())) && !this.canSurvive(state, (LevelReader)level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        if (!level.getBlockTicks().willTickThisTick(pos, (Object)this)) {
            level.scheduleTick(pos, (Block)this, 1);
        }
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource r) {
        this.updateTransmittedSignal(state, (Level)level, pos);
        if (((Boolean)state.getValue((Property)RECEIVER)).booleanValue()) {
            return;
        }
        Direction attachedFace = ((Direction)state.getValue((Property)FACING)).getOpposite();
        BlockPos attachedPos = pos.relative(attachedFace);
        level.blockUpdated(pos, level.getBlockState(pos).getBlock());
        level.blockUpdated(attachedPos, level.getBlockState(attachedPos).getBlock());
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() == oldState.getBlock() || isMoving) {
            return;
        }
        this.updateTransmittedSignal(state, worldIn, pos);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    public void updateTransmittedSignal(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        if (((Boolean)state.getValue((Property)RECEIVER)).booleanValue()) {
            return;
        }
        int power = RedstoneLinkBlock.getPower(level, state, pos);
        int powerFromPanels = this.getBlockEntityOptional((BlockGetter)level, pos).map(be -> {
            if (be.panelSupport == null) {
                return 0;
            }
            Boolean tri = be.panelSupport.shouldBePoweredTristate();
            if (tri == null) {
                return -1;
            }
            return tri != false ? 15 : 0;
        }).orElse(0);
        if (powerFromPanels == -1) {
            return;
        }
        power = Math.max(power, powerFromPanels);
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != power > 0) {
            level.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
        int transmit = power;
        this.withBlockEntityDo((BlockGetter)level, pos, be -> be.transmit(transmit));
    }

    private static int getPower(Level level, BlockState state, BlockPos pos) {
        int power = 0;
        for (Direction direction : Iterate.directions) {
            power = Math.max(level.getSignal(pos.relative(direction), direction), power);
        }
        for (Direction direction : Iterate.directions) {
            if (((Direction)state.getValue((Property)FACING)).getOpposite() == direction) continue;
            power = Math.max(level.getSignal(pos.relative(direction), Direction.UP), power);
        }
        return power;
    }

    public boolean isSignalSource(BlockState state) {
        return (Boolean)state.getValue((Property)POWERED) != false && (Boolean)state.getValue((Property)RECEIVER) != false;
    }

    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (side != blockState.getValue((Property)FACING)) {
            return 0;
        }
        return this.getSignal(blockState, blockAccess, pos, side);
    }

    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (!((Boolean)state.getValue((Property)RECEIVER)).booleanValue()) {
            return 0;
        }
        return this.getBlockEntityOptional(blockAccess, pos).map(RedstoneLinkBlockEntity::getReceivedSignal).orElse(0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED, RECEIVER});
        super.createBlockStateDefinition(builder);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() && this.toggleMode(state, level, pos) == InteractionResult.SUCCESS) {
            level.scheduleTick(pos, (Block)this, 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult toggleMode(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return this.onBlockEntityUse((BlockGetter)level, pos, be -> {
            Boolean wasReceiver = (Boolean)state.getValue((Property)RECEIVER);
            boolean blockPowered = level.hasNeighborSignal(pos);
            level.setBlock(pos, (BlockState)((BlockState)state.cycle((Property)RECEIVER)).setValue((Property)POWERED, (Comparable)Boolean.valueOf(blockPowered)), 3);
            be.transmit(wasReceiver != false ? 0 : RedstoneLinkBlock.getPower(level, state, pos));
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (this.toggleMode(state, context.getLevel(), context.getClickedPos()) == InteractionResult.SUCCESS) {
            context.getLevel().scheduleTick(context.getClickedPos(), (Block)this, 1);
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction _targetedFace) {
        return originalState;
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side != null;
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos neighbourPos = pos.relative(((Direction)state.getValue((Property)FACING)).getOpposite());
        BlockState neighbour = worldIn.getBlockState(neighbourPos);
        return !neighbour.canBeReplaced();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        state = (BlockState)state.setValue((Property)FACING, (Comparable)context.getClickedFace());
        return state;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.REDSTONE_BRIDGE.get((Direction)state.getValue((Property)FACING));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<RedstoneLinkBlockEntity> getBlockEntityClass() {
        return RedstoneLinkBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RedstoneLinkBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.REDSTONE_LINK.get();
    }
}
