/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
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
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.ticks.LevelTickAccess
 */
package com.simibubi.create.content.trains.display;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTickAccess;

public class FlapDisplayBlock
extends HorizontalKineticBlock
implements IBE<FlapDisplayBlockEntity>,
IWrenchable,
ICogWheel,
SimpleWaterloggedBlock {
    public static final BooleanProperty UP = BooleanProperty.create((String)"up");
    public static final BooleanProperty DOWN = BooleanProperty.create((String)"down");
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public FlapDisplayBlock(BlockBehaviour.Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)UP, (Comparable)Boolean.valueOf(false))).setValue((Property)DOWN, (Comparable)Boolean.valueOf(false))).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{UP, DOWN, BlockStateProperties.WATERLOGGED}));
    }

    @Override
    public IRotate.SpeedLevel getMinimumRequiredSpeedLevel() {
        return IRotate.SpeedLevel.MEDIUM;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();
        BlockPos placedOnPos = clickedPos.relative(face.getOpposite());
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(placedOnPos);
        BlockState stateForPlacement = this.defaultBlockState();
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        if (blockState.getBlock() != this || context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            stateForPlacement = super.getStateForPlacement(context);
        } else {
            Direction otherFacing = (Direction)blockState.getValue(HORIZONTAL_FACING);
            stateForPlacement = (BlockState)stateForPlacement.setValue(HORIZONTAL_FACING, (Comparable)otherFacing);
        }
        return this.updateColumn(level, clickedPos, (BlockState)stateForPlacement.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER)), true);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (placementHelper.matchesItem(stack)) {
            return placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        FlapDisplayBlockEntity flapBE = (FlapDisplayBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        if (flapBE == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if ((flapBE = flapBE.getController()) == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        double yCoord = hitResult.getLocation().add((Vec3)Vec3.atLowerCornerOf((Vec3i)hitResult.getDirection().getOpposite().getNormal()).scale((double)0.125)).y;
        int lineIndex = flapBE.getLineIndexAt(yCoord);
        if (stack.isEmpty()) {
            if (!flapBE.isSpeedRequirementFulfilled()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            flapBE.applyTextManually(lineIndex, null);
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.getItem() == Items.GLOW_INK_SAC) {
            if (!level.isClientSide) {
                level.playSound(null, pos, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                flapBE.setGlowing(lineIndex);
            }
            return ItemInteractionResult.SUCCESS;
        }
        boolean display = stack.getItem() == Items.NAME_TAG && stack.has(DataComponents.CUSTOM_NAME) || AllBlocks.CLIPBOARD.isIn(stack);
        DyeColor dye = DyeColor.getColor((ItemStack)stack);
        if (!display && dye == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (dye == null && !flapBE.isSpeedRequirementFulfilled()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        Component customName = (Component)stack.get(DataComponents.CUSTOM_NAME);
        if (display) {
            if (AllBlocks.CLIPBOARD.isIn(stack)) {
                List<ClipboardEntry> entries = ClipboardEntry.getLastViewedEntries(stack);
                int line = lineIndex;
                for (ClipboardEntry entry : entries) {
                    for (String string : entry.text.getString().split("\n")) {
                        flapBE.applyTextManually(line++, (Component)Component.literal((String)string));
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
            flapBE.applyTextManually(lineIndex, customName);
        }
        if (dye != null) {
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
            flapBE.setColour(lineIndex, dye);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.FLAP_DISPLAY.get((Direction)pState.getValue(HORIZONTAL_FACING));
    }

    @Override
    public Class<FlapDisplayBlockEntity> getBlockEntityClass() {
        return FlapDisplayBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FlapDisplayBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.FLAP_DISPLAY.get();
    }

    @Override
    public float getParticleTargetRadius() {
        return 0.85f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 0.75f;
    }

    private BlockState updateColumn(Level level, BlockPos pos, BlockState state, boolean present) {
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        Direction.Axis axis = this.getConnectionAxis(state);
        for (Direction connection : Iterate.directionsInAxis((Direction.Axis)Direction.Axis.Y)) {
            boolean connect = true;
            block1: for (Direction movement : Iterate.directionsInAxis((Direction.Axis)axis)) {
                currentPos.set((Vec3i)pos);
                for (int i = 0; i < 1000 && level.isLoaded((BlockPos)currentPos); ++i) {
                    BlockState other1 = currentPos.equals((Object)pos) ? state : level.getBlockState((BlockPos)currentPos);
                    BlockState other2 = level.getBlockState(currentPos.relative(connection));
                    boolean col1 = this.canConnect(state, other1);
                    boolean col2 = this.canConnect(state, other2);
                    currentPos.move(movement);
                    if (!col1 && !col2) continue block1;
                    if (col1 && col2) continue;
                    connect = false;
                    break block1;
                }
            }
            state = FlapDisplayBlock.setConnection(state, connection, connect);
        }
        return state;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        if (pOldState.getBlock() == this) {
            return;
        }
        LevelTickAccess blockTicks = pLevel.getBlockTicks();
        if (!blockTicks.hasScheduledTick(pPos, (Object)this)) {
            pLevel.scheduleTick(pPos, (Block)this, 1);
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getBlock() != this) {
            return;
        }
        BlockPos belowPos = pPos.relative(Direction.fromAxisAndDirection((Direction.Axis)this.getConnectionAxis(pState), (Direction.AxisDirection)Direction.AxisDirection.NEGATIVE));
        BlockState belowState = pLevel.getBlockState(belowPos);
        if (!this.canConnect(pState, belowState)) {
            KineticBlockEntity.switchToBlockState((Level)pLevel, pPos, this.updateColumn((Level)pLevel, pPos, pState, true));
        }
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, FlapDisplayBlockEntity::updateControllerStatus);
    }

    public BlockState updateShape(BlockState state, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        return this.updatedShapeInner(state, pDirection, pNeighborState, pLevel, pCurrentPos);
    }

    private BlockState updatedShapeInner(BlockState state, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            pLevel.scheduleTick(pCurrentPos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)pLevel));
        }
        if (!this.canConnect(state, pNeighborState)) {
            return FlapDisplayBlock.setConnection(state, pDirection, false);
        }
        if (pDirection.getAxis() == this.getConnectionAxis(state)) {
            return (BlockState)this.withPropertiesOf(pNeighborState).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)));
        }
        return FlapDisplayBlock.setConnection(state, pDirection, FlapDisplayBlock.getConnection(pNeighborState, pDirection.getOpposite()));
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    protected boolean canConnect(BlockState state, BlockState other) {
        return other.getBlock() == this && state.getValue(HORIZONTAL_FACING) == other.getValue(HORIZONTAL_FACING);
    }

    protected Direction.Axis getConnectionAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getClockWise().getAxis();
    }

    public static boolean getConnection(BlockState state, Direction side) {
        BooleanProperty property = side == Direction.DOWN ? DOWN : (side == Direction.UP ? UP : null);
        return property != null && (Boolean)state.getValue((Property)property) != false;
    }

    public static BlockState setConnection(BlockState state, Direction side, boolean connect) {
        BooleanProperty property;
        Object object = side == Direction.DOWN ? DOWN : (property = side == Direction.UP ? UP : null);
        if (property != null) {
            state = (BlockState)state.setValue((Property)property, (Comparable)Boolean.valueOf(connect));
        }
        return state;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        if (pIsMoving || pNewState.getBlock() == this) {
            return;
        }
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)this.getConnectionAxis(pState))) {
            BlockPos relative = pPos.relative(d);
            BlockState adjacent = pLevel.getBlockState(relative);
            if (!this.canConnect(pState, adjacent)) continue;
            KineticBlockEntity.switchToBlockState(pLevel, relative, this.updateColumn(pLevel, relative, adjacent, false));
        }
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.DISPLAY_BOARD.isIn(arg_0);
        }

        public Predicate<BlockState> getStatePredicate() {
            return arg_0 -> AllBlocks.DISPLAY_BOARD.has(arg_0);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)((Direction)state.getValue(HORIZONTAL_FACING)).getAxis(), dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
            return directions.isEmpty() ? PlacementOffset.fail() : PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> ((FlapDisplayBlock)AllBlocks.DISPLAY_BOARD.get()).updateColumn(world, pos.relative((Direction)directions.get(0)), (BlockState)s.setValue(HorizontalKineticBlock.HORIZONTAL_FACING, (Comparable)((Direction)state.getValue(HORIZONTAL_FACING))), true));
        }
    }
}
