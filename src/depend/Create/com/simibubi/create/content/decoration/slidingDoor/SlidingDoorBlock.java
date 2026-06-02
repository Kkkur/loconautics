/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockSetType
 *  net.minecraft.world.level.block.state.properties.BlockSetType$PressurePlateSensitivity
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DoorHingeSide
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration.slidingDoor;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SlidingDoorBlock
extends DoorBlock
implements IWrenchable,
IBE<SlidingDoorBlockEntity>,
IHaveBigOutline {
    public static final Supplier<BlockSetType> TRAIN_SET_TYPE = () -> new BlockSetType("create:train", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.NETHERITE_BLOCK, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);
    public static final Supplier<BlockSetType> GLASS_SET_TYPE = () -> new BlockSetType("create:glass", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.GLASS, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);
    public static final Supplier<BlockSetType> STONE_SET_TYPE = () -> new BlockSetType("create:stone", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.STONE, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);
    public static final BooleanProperty VISIBLE = BooleanProperty.create((String)"visible");
    private final boolean folds;

    public static SlidingDoorBlock metal(BlockBehaviour.Properties properties, boolean folds) {
        return new SlidingDoorBlock(properties, TRAIN_SET_TYPE.get(), folds);
    }

    public static SlidingDoorBlock glass(BlockBehaviour.Properties properties, boolean folds) {
        return new SlidingDoorBlock(properties, GLASS_SET_TYPE.get(), folds);
    }

    public static SlidingDoorBlock stone(BlockBehaviour.Properties properties, boolean folds) {
        return new SlidingDoorBlock(properties, STONE_SET_TYPE.get(), folds);
    }

    public SlidingDoorBlock(BlockBehaviour.Properties properties, BlockSetType type, boolean folds) {
        super(type, properties);
        this.folds = folds;
    }

    public boolean isFoldingDoor() {
        return this.folds;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{VISIBLE}));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (!((Boolean)pState.getValue((Property)OPEN)).booleanValue() && (((Boolean)pState.getValue((Property)VISIBLE)).booleanValue() || pLevel instanceof ContraptionWorld)) {
            return super.getShape(pState, pLevel, pPos, pContext);
        }
        Direction direction = (Direction)pState.getValue((Property)FACING);
        boolean hinge = pState.getValue((Property)HINGE) == DoorHingeSide.RIGHT;
        return SlidingDoorShapes.get(direction, hinge, this.isFoldingDoor());
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pState.getValue((Property)HALF) == DoubleBlockHalf.LOWER || pLevel.getBlockState(pPos.below()).is((Block)this);
    }

    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return this.getShape(pState, pLevel, pPos, CollisionContext.empty());
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        if (stateForPlacement != null && ((Boolean)stateForPlacement.getValue((Property)OPEN)).booleanValue()) {
            return (BlockState)((BlockState)stateForPlacement.setValue((Property)OPEN, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false));
        }
        return stateForPlacement;
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is((Block)this)) {
            this.deferUpdate((LevelAccessor)pLevel, pPos);
        }
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        BlockState blockState = super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        if (blockState.isAir()) {
            return blockState;
        }
        DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)blockState.getValue((Property)HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is((Block)this) && pFacingState.getValue((Property)HALF) != doubleblockhalf ? (BlockState)blockState.setValue((Property)VISIBLE, (Comparable)((Boolean)pFacingState.getValue((Property)VISIBLE))) : Blocks.AIR.defaultBlockState();
        }
        return blockState;
    }

    public void setOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open) {
        if (!state.is((Block)this)) {
            return;
        }
        if ((Boolean)state.getValue((Property)OPEN) == open) {
            return;
        }
        BlockState changedState = (BlockState)state.setValue((Property)OPEN, (Comparable)Boolean.valueOf(open));
        if (open) {
            changedState = (BlockState)changedState.setValue((Property)VISIBLE, (Comparable)Boolean.valueOf(false));
        }
        level.setBlock(pos, changedState, 10);
        DoorHingeSide hinge = (DoorHingeSide)changedState.getValue((Property)HINGE);
        Direction facing = (Direction)changedState.getValue((Property)FACING);
        BlockPos otherPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        BlockState otherDoor = level.getBlockState(otherPos);
        if (SlidingDoorBlock.isDoubleDoor(changedState, hinge, facing, otherDoor)) {
            this.setOpen(entity, level, otherDoor, otherPos, open);
        }
        this.playSound(entity, level, pos, open);
        level.gameEvent(entity, (Holder)(open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean lower = pState.getValue((Property)HALF) == DoubleBlockHalf.LOWER;
        boolean isPowered = SlidingDoorBlock.isDoorPowered(pLevel, pPos, pState);
        if (this.defaultBlockState().is(pBlock)) {
            return;
        }
        if (isPowered == (Boolean)pState.getValue((Property)POWERED)) {
            return;
        }
        SlidingDoorBlockEntity be = (SlidingDoorBlockEntity)this.getBlockEntity((BlockGetter)pLevel, lower ? pPos : pPos.below());
        if (be != null && be.deferUpdate) {
            return;
        }
        BlockState changedState = (BlockState)((BlockState)pState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(isPowered))).setValue((Property)OPEN, (Comparable)Boolean.valueOf(isPowered));
        if (isPowered) {
            changedState = (BlockState)changedState.setValue((Property)VISIBLE, (Comparable)Boolean.valueOf(false));
        }
        if (isPowered != (Boolean)pState.getValue((Property)OPEN)) {
            this.playSound(null, pLevel, pPos, isPowered);
            pLevel.gameEvent(null, (Holder)(isPowered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pPos);
            DoorHingeSide hinge = (DoorHingeSide)changedState.getValue((Property)HINGE);
            Direction facing = (Direction)changedState.getValue((Property)FACING);
            BlockPos otherPos = pPos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
            BlockState otherDoor = pLevel.getBlockState(otherPos);
            if (SlidingDoorBlock.isDoubleDoor(changedState, hinge, facing, otherDoor)) {
                otherDoor = (BlockState)((BlockState)otherDoor.setValue((Property)POWERED, (Comparable)Boolean.valueOf(isPowered))).setValue((Property)OPEN, (Comparable)Boolean.valueOf(isPowered));
                if (isPowered) {
                    otherDoor = (BlockState)otherDoor.setValue((Property)VISIBLE, (Comparable)Boolean.valueOf(false));
                }
                pLevel.setBlock(otherPos, otherDoor, 2);
            }
        }
        pLevel.setBlock(pPos, changedState, 2);
    }

    public static boolean isDoorPowered(Level pLevel, BlockPos pPos, BlockState state) {
        boolean lower = state.getValue((Property)HALF) == DoubleBlockHalf.LOWER;
        DoorHingeSide hinge = (DoorHingeSide)state.getValue((Property)HINGE);
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockPos otherPos = pPos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        BlockState otherDoor = pLevel.getBlockState(otherPos);
        if (SlidingDoorBlock.isDoubleDoor((BlockState)state.cycle((Property)OPEN), hinge, facing, otherDoor) && (pLevel.hasNeighborSignal(otherPos) || pLevel.hasNeighborSignal(otherPos.relative(lower ? Direction.UP : Direction.DOWN)))) {
            return true;
        }
        return pLevel.hasNeighborSignal(pPos) || pLevel.hasNeighborSignal(pPos.relative(lower ? Direction.UP : Direction.DOWN));
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        boolean isOpen = (Boolean)(state = (BlockState)state.cycle((Property)OPEN)).getValue((Property)OPEN);
        if (isOpen) {
            state = (BlockState)state.setValue((Property)VISIBLE, (Comparable)Boolean.valueOf(false));
        }
        level.setBlock(pos, state, 10);
        level.gameEvent((Entity)player, (Holder)(this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
        DoorHingeSide hinge = (DoorHingeSide)state.getValue((Property)HINGE);
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockPos otherPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());
        BlockState otherDoor = level.getBlockState(otherPos);
        if (SlidingDoorBlock.isDoubleDoor(state, hinge, facing, otherDoor)) {
            this.useWithoutItem(otherDoor, level, otherPos, player, hitResult);
        } else if (isOpen) {
            this.playSound((Entity)player, level, pos, true);
            level.gameEvent((Entity)player, (Holder)GameEvent.BLOCK_OPEN, pos);
        }
        return InteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    public void deferUpdate(LevelAccessor level, BlockPos pos) {
        this.withBlockEntityDo((BlockGetter)level, pos, sdte -> {
            sdte.deferUpdate = true;
        });
    }

    public static boolean isDoubleDoor(BlockState pState, DoorHingeSide hinge, Direction facing, BlockState otherDoor) {
        return otherDoor.getBlock() == pState.getBlock() && otherDoor.getValue((Property)HINGE) != hinge && otherDoor.getValue((Property)FACING) == facing && otherDoor.getValue((Property)OPEN) != pState.getValue((Property)OPEN) && otherDoor.getValue((Property)HALF) == pState.getValue((Property)HALF);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return (Boolean)pState.getValue((Property)VISIBLE) != false ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED;
    }

    private void playSound(@Nullable Entity pSource, Level pLevel, BlockPos pPos, boolean pIsOpening) {
        pLevel.playSound(pSource, pPos, pIsOpening ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0f, pLevel.getRandom().nextFloat() * 0.1f + 0.9f);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue((Property)HALF) == DoubleBlockHalf.UPPER) {
            return null;
        }
        return IBE.super.newBlockEntity(pos, state);
    }

    @Override
    public Class<SlidingDoorBlockEntity> getBlockEntityClass() {
        return SlidingDoorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SlidingDoorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.SLIDING_DOOR.get();
    }
}
