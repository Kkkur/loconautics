/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlockEntity;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelStructuralBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class LargeWaterWheelBlock
extends RotatedPillarKineticBlock
implements IBE<LargeWaterWheelBlockEntity> {
    public static final BooleanProperty EXTENSION = BooleanProperty.create((String)"extension");

    public LargeWaterWheelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)EXTENSION, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{EXTENSION}));
    }

    public Direction.Axis getAxisForPlacement(BlockPlaceContext context) {
        return (Direction.Axis)super.getStateForPlacement(context).getValue((Property)AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        BlockPos pos = context.getClickedPos();
        Direction.Axis axis = (Direction.Axis)stateForPlacement.getValue((Property)AXIS);
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    BlockState occupiedState;
                    BlockPos offset;
                    if (axis.choose(x, y, z) != 0 || (offset = new BlockPos(x, y, z)).equals((Object)BlockPos.ZERO) || (occupiedState = context.getLevel().getBlockState(pos.offset((Vec3i)offset))).canBeReplaced()) continue;
                    return null;
                }
            }
        }
        if (context.getLevel().getBlockState(pos.relative(Direction.fromAxisAndDirection((Direction.Axis)axis, (Direction.AxisDirection)Direction.AxisDirection.NEGATIVE))).is((Block)this)) {
            stateForPlacement = (BlockState)stateForPlacement.setValue((Property)EXTENSION, (Comparable)Boolean.valueOf(true));
        }
        return stateForPlacement;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, wwt -> wwt.applyMaterialIfValid(stack));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection != Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)pState.getValue((Property)AXIS)), (Direction.AxisDirection)Direction.AxisDirection.NEGATIVE)) {
            return pState;
        }
        return (BlockState)pState.setValue((Property)EXTENSION, (Comparable)Boolean.valueOf(pNeighborState.is((Block)this)));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.getBlockTicks().hasScheduledTick(pos, (Object)this)) {
            level.scheduleTick(pos, (Block)this, 1);
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        Direction.Axis axis = (Direction.Axis)pState.getValue((Property)AXIS);
        for (Direction side : Iterate.directions) {
            if (side.getAxis() == axis) continue;
            boolean[] blArray = Iterate.falseAndTrue;
            int n = blArray.length;
            for (int i = 0; i < n; ++i) {
                BlockState requiredStructure;
                Direction targetSide;
                boolean secondary;
                BlockPos structurePos = (secondary ? pPos.relative(side) : pPos).relative(targetSide = (secondary = blArray[i]) ? side.getClockWise(axis) : side);
                BlockState occupiedState = pLevel.getBlockState(structurePos);
                if (occupiedState == (requiredStructure = (BlockState)AllBlocks.WATER_WHEEL_STRUCTURAL.getDefaultState().setValue((Property)WaterWheelStructuralBlock.FACING, (Comparable)targetSide.getOpposite()))) continue;
                if (!occupiedState.canBeReplaced()) {
                    pLevel.destroyBlock(pPos, false);
                    return;
                }
                pLevel.setBlockAndUpdate(structurePos, requiredStructure);
            }
        }
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, WaterWheelBlockEntity::determineAndApplyFlowScore);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntityType<? extends LargeWaterWheelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.LARGE_WATER_WHEEL.get();
    }

    @Override
    public Class<LargeWaterWheelBlockEntity> getBlockEntityClass() {
        return LargeWaterWheelBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == this.getRotationAxis(state);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AXIS);
    }

    @Override
    public float getParticleTargetRadius() {
        return 2.5f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 2.25f;
    }

    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return false;
    }
}
