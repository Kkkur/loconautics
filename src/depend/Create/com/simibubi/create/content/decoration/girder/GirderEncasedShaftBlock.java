/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.decoration.girder;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GirderEncasedShaftBlock
extends HorizontalAxisKineticBlock
implements IBE<KineticBlockEntity>,
SimpleWaterloggedBlock,
IWrenchable,
SpecialBlockItemRequirement {
    public static final BooleanProperty TOP = GirderBlock.TOP;
    public static final BooleanProperty BOTTOM = GirderBlock.BOTTOM;

    public GirderEncasedShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)super.defaultBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)TOP, (Comparable)Boolean.valueOf(false))).setValue((Property)BOTTOM, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{TOP, BOTTOM, BlockStateProperties.WATERLOGGED}));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.GIRDER_BEAM_SHAFT.get((Direction.Axis)pState.getValue(HORIZONTAL_AXIS));
    }

    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.or((VoxelShape)super.getBlockSupportShape(pState, pReader, pPos), (VoxelShape)AllShapes.EIGHT_VOXEL_POLE.get(Direction.Axis.Y));
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)AllBlocks.METAL_GIRDER.getDefaultState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)((Boolean)originalState.getValue((Property)BlockStateProperties.WATERLOGGED)))).setValue((Property)GirderBlock.X, (Comparable)Boolean.valueOf(originalState.getValue(HORIZONTAL_AXIS) == Direction.Axis.Z))).setValue((Property)GirderBlock.Z, (Comparable)Boolean.valueOf(originalState.getValue(HORIZONTAL_AXIS) == Direction.Axis.X))).setValue(GirderBlock.AXIS, (Comparable)(originalState.getValue(HORIZONTAL_AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X))).setValue((Property)GirderBlock.BOTTOM, (Comparable)((Boolean)originalState.getValue((Property)BOTTOM)))).setValue((Property)GirderBlock.TOP, (Comparable)((Boolean)originalState.getValue((Property)TOP)));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult onWrenched = super.onWrenched(state, context);
        Player player = context.getPlayer();
        if (onWrenched == InteractionResult.SUCCESS && player != null && !player.isCreative()) {
            player.getInventory().placeItemBackInInventory(AllBlocks.SHAFT.asStack());
        }
        return onWrenched;
    }

    @Override
    public Class<KineticBlockEntity> getBlockEntityClass() {
        return KineticBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ENCASED_SHAFT.get();
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        BooleanProperty updateProperty;
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        BooleanProperty booleanProperty = updateProperty = direction == Direction.UP ? TOP : BOTTOM;
        if (direction.getAxis().isVertical()) {
            if (world.getBlockState(pos.relative(direction)).getBlockSupportShape((BlockGetter)world, pos.relative(direction)).isEmpty()) {
                state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(false));
            }
            return GirderBlock.updateVerticalProperty(world, pos, state, (Property<Boolean>)updateProperty, neighbourState, direction);
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState ifluidstate = level.getFluidState(pos);
        BlockState state = super.getStateForPlacement(context);
        return (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return ItemRequirement.of(AllBlocks.SHAFT.getDefaultState(), be).union(ItemRequirement.of(AllBlocks.METAL_GIRDER.getDefaultState(), be));
    }
}
