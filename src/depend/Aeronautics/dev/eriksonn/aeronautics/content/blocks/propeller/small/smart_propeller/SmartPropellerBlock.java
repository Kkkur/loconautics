/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionResult
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
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.eriksonn.aeronautics.index.AeroBlockShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmartPropellerBlock
extends HorizontalAxisKineticBlock
implements IBE<SmartPropellerBlockEntity> {
    public static final BooleanProperty REVERSED = BasePropellerBlock.REVERSED;
    public static final BooleanProperty CEILING = BooleanProperty.create((String)"ceiling");

    public SmartPropellerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue((Property)REVERSED, (Comparable)Boolean.valueOf(false))).setValue((Property)CEILING, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{REVERSED});
        builder.add(new Property[]{CEILING});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state;
        Direction.Axis axis = (Direction.Axis)(state = super.getStateForPlacement(context)).getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS);
        return (BlockState)((BlockState)state.setValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS, (Comparable)(axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X))).setValue((Property)CEILING, (Comparable)Boolean.valueOf(context.getClickedFace() == Direction.DOWN));
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return ((Boolean)state.getValue((Property)CEILING)).booleanValue() ? face == Direction.UP : face == Direction.DOWN;
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace() == ((Boolean)state.getValue((Property)CEILING) != false ? Direction.DOWN : Direction.UP)) {
            state = (BlockState)state.cycle((Property)REVERSED);
            context.getLevel().setBlock(context.getClickedPos(), state, 3);
            IWrenchable.playRotateSound((Level)context.getLevel(), (BlockPos)context.getClickedPos());
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (((Boolean)pState.getValue((Property)CEILING)).booleanValue()) {
            return AeroBlockShapes.SMART_PROPELLER_CEILING.get((Direction.Axis)pState.getValue(HORIZONTAL_AXIS));
        }
        return AeroBlockShapes.SMART_PROPELLER.get((Direction.Axis)pState.getValue(HORIZONTAL_AXIS));
    }

    public Class<SmartPropellerBlockEntity> getBlockEntityClass() {
        return SmartPropellerBlockEntity.class;
    }

    public BlockEntityType<? extends SmartPropellerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.SMART_PROPELLER.get();
    }
}
