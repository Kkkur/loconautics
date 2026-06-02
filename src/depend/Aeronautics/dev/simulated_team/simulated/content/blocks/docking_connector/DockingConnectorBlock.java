/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.block.WrenchableDirectionalBlock
 *  dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import com.google.common.collect.Maps;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlocks;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DockingConnectorBlock
extends WrenchableDirectionalBlock
implements IBE<DockingConnectorBlockEntity>,
BlockSubLevelAssemblyListener {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    private static final VoxelShape UP_OPEN_AABB = Block.box((double)0.0, (double)15.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
    private static final VoxelShape DOWN_OPEN_AABB = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)1.0, (double)16.0);
    private static final VoxelShape WEST_OPEN_AABB = Block.box((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)16.0, (double)16.0);
    private static final VoxelShape EAST_OPEN_AABB = Block.box((double)15.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
    private static final VoxelShape NORTH_OPEN_AABB = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)1.0);
    private static final VoxelShape SOUTH_OPEN_AABB = Block.box((double)0.0, (double)0.0, (double)15.0, (double)16.0, (double)16.0, (double)16.0);
    private static final Map<Direction, VoxelShape> OPEN_SHAPE_BY_DIRECTION = (Map)Util.make((Object)Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.NORTH, NORTH_OPEN_AABB);
        enumMap.put(Direction.EAST, EAST_OPEN_AABB);
        enumMap.put(Direction.SOUTH, SOUTH_OPEN_AABB);
        enumMap.put(Direction.WEST, WEST_OPEN_AABB);
        enumMap.put(Direction.UP, UP_OPEN_AABB);
        enumMap.put(Direction.DOWN, DOWN_OPEN_AABB);
    });

    public DockingConnectorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)EXTENDED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED, EXTENDED});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction nearestLookingDirection = context.getNearestLookingDirection();
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            nearestLookingDirection = nearestLookingDirection.getOpposite();
        }
        return (BlockState)((BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())))).setValue((Property)FACING, (Comparable)nearestLookingDirection.getOpposite());
    }

    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        BlockPos pairedConnectorPos;
        boolean blockChanged;
        boolean bl = blockChanged = !state.is(newState.getBlock());
        if (((Boolean)state.getValue((Property)POWERED)).booleanValue() && (blockChanged || state.getValue((Property)FACING) != newState.getValue((Property)FACING)) && level.getBlockState(pairedConnectorPos = pos.relative((Direction)state.getValue((Property)FACING))).is(SimBlocks.PAIRED_DOCKING_CONNECTOR)) {
            level.removeBlock(pairedConnectorPos, isMoving);
        }
        if (blockChanged) {
            level.getBlockEntity(pos, (BlockEntityType)SimBlockEntityTypes.DOCKING_CONNECTOR.get()).ifPresent(connector -> Containers.dropContents((Level)level, (BlockPos)pos, (Container)connector.inventory));
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide()) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, DockingConnectorBlockEntity::updateSignal);
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != level.hasNeighborSignal(pos)) {
            level.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
    }

    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        DockingConnectorBlockEntity be;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof DockingConnectorBlockEntity && !(be = (DockingConnectorBlockEntity)blockEntity).isRetracted() ? Shapes.create((AABB)be.getBoundingBox(state)) : Shapes.block();
    }

    @NotNull
    public VoxelShape getBlockSupportShape(@NotNull BlockState state, BlockGetter level, @NotNull BlockPos pos) {
        return Shapes.block();
    }

    public Class<DockingConnectorBlockEntity> getBlockEntityClass() {
        return DockingConnectorBlockEntity.class;
    }

    public BlockEntityType<? extends DockingConnectorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.DOCKING_CONNECTOR.get();
    }

    protected boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        DockingConnectorBlockEntity be = (DockingConnectorBlockEntity)this.getBlockEntity((BlockGetter)pLevel, pPos);
        if (!be.isExtended()) {
            return 0;
        }
        if (be.hasOtherConnector()) {
            return 15;
        }
        return Math.min(14, Math.max(0, 14 - (int)(14.0 * be.closestPairDistance / 4.0)));
    }

    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        DockingConnectorBlockEntity be;
        BlockEntity blockEntity = originLevel.getBlockEntity(oldPos);
        if (blockEntity instanceof DockingConnectorBlockEntity && (be = (DockingConnectorBlockEntity)blockEntity).hasOtherConnector() && (blockEntity = originLevel.getBlockEntity(be.otherConnectorPosition)) instanceof DockingConnectorBlockEntity) {
            DockingConnectorBlockEntity connected = (DockingConnectorBlockEntity)blockEntity;
            if (connected.otherConnectorPosition.equals((Object)oldPos) && (blockEntity = resultingLevel.getBlockEntity(newPos)) instanceof DockingConnectorBlockEntity) {
                DockingConnectorBlockEntity newBe = (DockingConnectorBlockEntity)blockEntity;
                be.unDock();
                connected.unDock();
                newBe.unDock();
                newBe.pairTo(connected);
                resultingLevel.blockEvent(newPos, (Block)this, 1, 0);
            }
        }
    }
}
