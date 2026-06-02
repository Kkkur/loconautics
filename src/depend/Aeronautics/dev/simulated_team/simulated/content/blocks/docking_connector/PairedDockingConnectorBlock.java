/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import com.mojang.serialization.MapCodec;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PairedDockingConnectorBlock
extends DirectionalBlock {
    public static final MapCodec<PairedDockingConnectorBlock> CODEC = PairedDockingConnectorBlock.simpleCodec(PairedDockingConnectorBlock::new);
    private static final VoxelShape[] SHAPES = new VoxelShape[]{PairedDockingConnectorBlock.box((double)0.0, (double)-16.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), PairedDockingConnectorBlock.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)32.0, (double)16.0), PairedDockingConnectorBlock.box((double)0.0, (double)0.0, (double)-16.0, (double)16.0, (double)16.0, (double)16.0), PairedDockingConnectorBlock.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)32.0), PairedDockingConnectorBlock.box((double)-16.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), PairedDockingConnectorBlock.box((double)0.0, (double)0.0, (double)0.0, (double)32.0, (double)16.0, (double)16.0)};

    public PairedDockingConnectorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    @NotNull
    protected VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES[((Direction)state.getValue((Property)FACING)).get3DDataValue()];
    }

    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @NotNull
    protected RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @NotNull
    protected BlockState updateShape(BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        if (facing != direction) {
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
        if (neighborState.is(SimBlocks.DOCKING_CONNECTOR) && neighborState.getValue((Property)BlockStateProperties.FACING) == facing.getOpposite()) {
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @NotNull
    public BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide()) {
            if (player.isCreative()) {
                BlockPos connectorPos = pos.relative((Direction)state.getValue((Property)FACING));
                BlockState connectorState = level.getBlockState(connectorPos);
                if (connectorState.is(SimBlocks.DOCKING_CONNECTOR)) {
                    level.setBlock(connectorPos, Blocks.AIR.defaultBlockState(), 3);
                }
            } else {
                PairedDockingConnectorBlock.dropResources((BlockState)state, (Level)level, (BlockPos)pos, null, (Entity)player, (ItemStack)player.getMainHandItem());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack tool) {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, tool);
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockState connectorBlock = level.getBlockState(pos.relative(facing));
        return connectorBlock.is(SimBlocks.DOCKING_CONNECTOR) && connectorBlock.getValue((Property)BlockStateProperties.FACING) == facing.getOpposite() && (Boolean)connectorBlock.getValue((Property)DockingConnectorBlock.POWERED) != false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{FACING}));
    }

    @Nullable
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return null;
    }

    @NotNull
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue((Property)FACING, (Comparable)rot.rotate((Direction)state.getValue((Property)FACING)));
    }

    @NotNull
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation((Direction)state.getValue((Property)FACING)));
    }

    @NotNull
    public ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return SimBlocks.DOCKING_CONNECTOR.asStack();
    }

    @NotNull
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}
