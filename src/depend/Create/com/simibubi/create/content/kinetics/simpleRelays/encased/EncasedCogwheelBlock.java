/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 */
package com.simibubi.create.content.kinetics.simpleRelays.encased;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import java.util.function.Supplier;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EncasedCogwheelBlock
extends RotatedPillarKineticBlock
implements ICogWheel,
IBE<SimpleKineticBlockEntity>,
SpecialBlockItemRequirement,
TransformableBlock,
EncasedBlock {
    public static final BooleanProperty TOP_SHAFT = BooleanProperty.create((String)"top_shaft");
    public static final BooleanProperty BOTTOM_SHAFT = BooleanProperty.create((String)"bottom_shaft");
    protected final boolean isLarge;
    private final Supplier<Block> casing;

    public EncasedCogwheelBlock(BlockBehaviour.Properties properties, boolean large, Supplier<Block> casing) {
        super(properties);
        this.isLarge = large;
        this.casing = casing;
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)TOP_SHAFT, (Comparable)Boolean.valueOf(false))).setValue((Property)BOTTOM_SHAFT, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{TOP_SHAFT, BOTTOM_SHAFT}));
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (target instanceof BlockHitResult) {
            return ((BlockHitResult)target).getDirection().getAxis() != this.getRotationAxis(state) ? (this.isLarge ? AllBlocks.LARGE_COGWHEEL.asStack() : AllBlocks.COGWHEEL.asStack()) : this.getCasing().asItem().getDefaultInstance();
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placedOn = context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (ICogWheel.isSmallCog(placedOn)) {
            stateForPlacement = (BlockState)stateForPlacement.setValue((Property)AXIS, (Comparable)((IRotate)placedOn.getBlock()).getRotationAxis(placedOn));
        }
        return stateForPlacement;
    }

    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
        return pState.getBlock() == pAdjacentBlockState.getBlock() && pState.getValue((Property)AXIS) == pAdjacentBlockState.getValue((Property)AXIS);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace().getAxis() != state.getValue((Property)AXIS)) {
            return super.onWrenched(state, context);
        }
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos();
        KineticBlockEntity.switchToBlockState(level, pos, (BlockState)state.cycle((Property)(context.getClickedFace().getAxisDirection() == Direction.AxisDirection.POSITIVE ? TOP_SHAFT : BOTTOM_SHAFT)));
        IWrenchable.playRotateSound(level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        originalState = this.swapShaftsForRotation(originalState, Rotation.CLOCKWISE_90, targetedFace.getAxis());
        return (BlockState)originalState.setValue(RotatedPillarKineticBlock.AXIS, (Comparable)VoxelShaper.axisAsFace((Direction.Axis)((Direction.Axis)originalState.getValue(RotatedPillarKineticBlock.AXIS))).getClockWise(targetedFace.getAxis()).getAxis());
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        context.getLevel().levelEvent(2001, context.getClickedPos(), Block.getId((BlockState)state));
        KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(), (BlockState)(this.isLarge ? AllBlocks.LARGE_COGWHEEL : AllBlocks.COGWHEEL).getDefaultState().setValue((Property)AXIS, (Comparable)((Direction.Axis)state.getValue((Property)AXIS))));
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue((Property)AXIS) && (Boolean)state.getValue((Property)(face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? TOP_SHAFT : BOTTOM_SHAFT)) != false;
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        if (newState.getBlock() instanceof EncasedCogwheelBlock && oldState.getBlock() instanceof EncasedCogwheelBlock) {
            if (newState.getValue((Property)TOP_SHAFT) != oldState.getValue((Property)TOP_SHAFT)) {
                return false;
            }
            if (newState.getValue((Property)BOTTOM_SHAFT) != oldState.getValue((Property)BOTTOM_SHAFT)) {
                return false;
            }
        }
        return super.areStatesKineticallyEquivalent(oldState, newState);
    }

    @Override
    public boolean isSmallCog() {
        return !this.isLarge;
    }

    @Override
    public boolean isLargeCog() {
        return this.isLarge;
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return CogWheelBlock.isValidCogwheelPosition(ICogWheel.isLargeCog(state), worldIn, pos, (Direction.Axis)state.getValue((Property)AXIS));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AXIS);
    }

    public BlockState swapShafts(BlockState state) {
        boolean bottom = (Boolean)state.getValue((Property)BOTTOM_SHAFT);
        boolean top = (Boolean)state.getValue((Property)TOP_SHAFT);
        state = (BlockState)state.setValue((Property)BOTTOM_SHAFT, (Comparable)Boolean.valueOf(top));
        state = (BlockState)state.setValue((Property)TOP_SHAFT, (Comparable)Boolean.valueOf(bottom));
        return state;
    }

    public BlockState swapShaftsForRotation(BlockState state, Rotation rotation, Direction.Axis rotationAxis) {
        boolean clockwise;
        if (rotation == Rotation.NONE) {
            return state;
        }
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        if (axis == rotationAxis) {
            return state;
        }
        if (rotation == Rotation.CLOCKWISE_180) {
            return this.swapShafts(state);
        }
        boolean bl = clockwise = rotation == Rotation.CLOCKWISE_90;
        if (rotationAxis == Direction.Axis.X ? axis == Direction.Axis.Z && !clockwise || axis == Direction.Axis.Y && clockwise : (rotationAxis == Direction.Axis.Y ? axis == Direction.Axis.X && !clockwise || axis == Direction.Axis.Z && clockwise : rotationAxis == Direction.Axis.Z && (axis == Direction.Axis.Y && !clockwise || axis == Direction.Axis.X && clockwise))) {
            return this.swapShafts(state);
        }
        return state;
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        if (axis == Direction.Axis.X && mirror == Mirror.FRONT_BACK || axis == Direction.Axis.Z && mirror == Mirror.LEFT_RIGHT) {
            return this.swapShafts(state);
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        state = this.swapShaftsForRotation(state, rotation, Direction.Axis.Y);
        return super.rotate(state, rotation);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = this.mirror(state, transform.mirror);
        }
        if (transform.rotationAxis == Direction.Axis.Y) {
            return this.rotate(state, transform.rotation);
        }
        state = this.swapShaftsForRotation(state, transform.rotation, transform.rotationAxis);
        state = (BlockState)state.setValue((Property)AXIS, (Comparable)transform.rotateAxis((Direction.Axis)state.getValue((Property)AXIS)));
        return state;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return ItemRequirement.of(this.isLarge ? AllBlocks.LARGE_COGWHEEL.getDefaultState() : AllBlocks.COGWHEEL.getDefaultState(), be);
    }

    @Override
    public Class<SimpleKineticBlockEntity> getBlockEntityClass() {
        return SimpleKineticBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SimpleKineticBlockEntity> getBlockEntityType() {
        return this.isLarge ? (BlockEntityType)AllBlockEntityTypes.ENCASED_LARGE_COGWHEEL.get() : (BlockEntityType)AllBlockEntityTypes.ENCASED_COGWHEEL.get();
    }

    @Override
    public Block getCasing() {
        return this.casing.get();
    }

    @Override
    public void handleEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand, BlockHitResult ray) {
        BlockState encasedState = (BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)((Direction.Axis)state.getValue((Property)AXIS)));
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)((Direction.Axis)state.getValue((Property)AXIS)))) {
            IRotate def;
            BlockState adjacentState = level.getBlockState(pos.relative(d));
            Block block = adjacentState.getBlock();
            if (!(block instanceof IRotate) || !(def = (IRotate)block).hasShaftTowards((LevelReader)level, pos.relative(d), adjacentState, d.getOpposite())) continue;
            encasedState = (BlockState)encasedState.cycle((Property)(d.getAxisDirection() == Direction.AxisDirection.POSITIVE ? TOP_SHAFT : BOTTOM_SHAFT));
        }
        KineticBlockEntity.switchToBlockState(level, pos, encasedState);
    }
}
