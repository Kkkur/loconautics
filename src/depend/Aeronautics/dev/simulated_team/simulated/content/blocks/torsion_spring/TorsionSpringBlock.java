/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.context.BlockPlaceContext
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
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.torsion_spring;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.api.IDirectionalAnalogOutput;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorsionSpringBlock
extends DirectionalKineticBlock
implements IBE<TorsionSpringBlockEntity>,
ExtraKinetics.ExtraKineticsBlock,
IDirectionalAnalogOutput {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public TorsionSpringBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{POWERED}));
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getOpposite() == state.getValue((Property)FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }

    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SimBlockShapes.TORSION_SPRING.get((Direction)blockState.getValue((Property)FACING));
    }

    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl);
        boolean signal = level.hasNeighborSignal(blockPos);
        if (signal != (Boolean)blockState.getValue((Property)POWERED)) {
            level.setBlock(blockPos, (BlockState)blockState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(signal)), 2);
            this.withBlockEntityDo((BlockGetter)level, blockPos, TorsionSpringBlockEntity::onSignalChanged);
        }
    }

    protected boolean hasAnalogOutputSignal(BlockState blockState) {
        return ((Direction)blockState.getValue((Property)FACING)).getAxis().isHorizontal();
    }

    @Override
    public int getAnalogOutputSignalFrom(BlockState blockState, Level level, BlockPos blockPos, Direction dir) {
        Direction facing = (Direction)blockState.getValue((Property)FACING);
        TorsionSpringBlockEntity be = (TorsionSpringBlockEntity)this.getBlockEntity((BlockGetter)level, blockPos);
        float frac = Mth.clamp((float)(be.getAngle() / (float)be.angleInput.getValue()), (float)-1.0f, (float)1.0f);
        if ((double)Math.abs(be.getAngle()) < 0.99) {
            return 0;
        }
        int value = (int)((frac < 0.0f ? Math.floor(frac * 15.0f) : Math.ceil(frac * 15.0f)) * (double)(facing.getStepX() == 1 || facing.getStepZ() == 1 ? -1 : 1));
        if (facing.getClockWise() == dir && value > 0) {
            return value;
        }
        if (facing.getCounterClockWise() == dir && value < 0) {
            return -value;
        }
        return 0;
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    public Class<TorsionSpringBlockEntity> getBlockEntityClass() {
        return TorsionSpringBlockEntity.class;
    }

    public BlockEntityType<? extends TorsionSpringBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.TORSION_SPRING.get();
    }

    @Override
    public IRotate getExtraKineticsRotationConfiguration() {
        return TorsionSpringBlockEntity.Output.CONFIG;
    }
}
