/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.game.DebugPackets
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.PipeBlock
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.ticks.TickPriority
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.pipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchableWithBracket;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockRotation;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.VanillaFluidTargets;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Arrays;
import java.util.Optional;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidPipeBlock
extends PipeBlock
implements SimpleWaterloggedBlock,
IWrenchableWithBracket,
IBE<FluidPipeBlockEntity>,
EncasableBlock,
TransformableBlock {
    private static final VoxelShape OCCLUSION_BOX = Block.box((double)4.0, (double)4.0, (double)4.0, (double)12.0, (double)12.0, (double)12.0);
    public static final MapCodec<FluidPipeBlock> CODEC = FluidPipeBlock.simpleCodec(FluidPipeBlock::new);

    public FluidPipeBlock(BlockBehaviour.Properties properties) {
        super(0.25f, properties);
        this.registerDefaultState((BlockState)super.defaultBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (this.tryRemoveBracket(context)) {
            return InteractionResult.SUCCESS;
        }
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Direction.Axis axis = this.getAxis((BlockGetter)world, pos, state);
        if (axis == null) {
            Vec3 clickLocation = context.getClickLocation().subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            double closest = 3.4028234663852886E38;
            Direction argClosest = Direction.UP;
            for (Direction direction : Iterate.directions) {
                Vec3 centerOf;
                double distance;
                if (clickedFace.getAxis() == direction.getAxis() || !((distance = (centerOf = Vec3.atCenterOf((Vec3i)direction.getNormal())).distanceToSqr(clickLocation)) < closest)) continue;
                closest = distance;
                argClosest = direction;
            }
            axis = argClosest.getAxis();
        }
        if (clickedFace.getAxis() == axis) {
            return InteractionResult.PASS;
        }
        if (!world.isClientSide) {
            this.withBlockEntityDo((BlockGetter)world, pos, fpte -> fpte.getBehaviour(FluidTransportBehaviour.TYPE).interfaces.values().stream().filter(pc -> pc != null && pc.hasFlow()).findAny().ifPresent($ -> AllAdvancements.GLASS_PIPE.awardTo(context.getPlayer())));
            FluidTransportBehaviour.cacheFlows((LevelAccessor)world, pos);
            world.setBlockAndUpdate(pos, (BlockState)((BlockState)AllBlocks.GLASS_FLUID_PIPE.getDefaultState().setValue((Property)GlassFluidPipeBlock.AXIS, (Comparable)axis)).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED))));
            FluidTransportBehaviour.loadFlows((LevelAccessor)world, pos);
        }
        return InteractionResult.SUCCESS;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult result = this.tryEncase(state, level, pos, stack, player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public BlockState getAxisState(Direction.Axis axis) {
        BlockState defaultState = this.defaultBlockState();
        for (Direction d : Iterate.directions) {
            defaultState = (BlockState)defaultState.setValue((Property)PROPERTY_BY_DIRECTION.get(d), (Comparable)Boolean.valueOf(d.getAxis() == axis));
        }
        return defaultState;
    }

    @Nullable
    private Direction.Axis getAxis(BlockGetter world, BlockPos pos, BlockState state) {
        return FluidPropagator.getStraightPipeAxis(state);
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean blockTypeChanged;
        boolean bl = blockTypeChanged = state.getBlock() != newState.getBlock();
        if (blockTypeChanged && !world.isClientSide) {
            FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
        }
        if (state != newState && !isMoving) {
            this.removeBracket((BlockGetter)world, pos, true).ifPresent(stack -> Block.popResource((Level)world, (BlockPos)pos, (ItemStack)stack));
        }
        if (state.hasBlockEntity() && (blockTypeChanged || !newState.hasBlockEntity())) {
            world.removeBlockEntity(pos);
        }
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (world.isClientSide) {
            return;
        }
        if (state != oldState) {
            world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
        }
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
        DebugPackets.sendNeighborsUpdatePacket((Level)world, (BlockPos)pos);
        Direction d = FluidPropagator.validateNeighbourChange(state, world, pos, otherBlock, neighborPos, isMoving);
        if (d == null) {
            return;
        }
        if (!FluidPipeBlock.isOpenAt(state, d)) {
            return;
        }
        world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
    }

    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource r) {
        FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
    }

    public static boolean isPipe(BlockState state) {
        return state.getBlock() instanceof FluidPipeBlock;
    }

    public static boolean canConnectTo(BlockAndTintGetter world, BlockPos neighbourPos, BlockState neighbour, Direction direction) {
        if (FluidPropagator.hasFluidCapability((BlockGetter)world, neighbourPos, direction.getOpposite())) {
            return true;
        }
        if (VanillaFluidTargets.canProvideFluidWithoutCapability(neighbour)) {
            return true;
        }
        FluidTransportBehaviour transport = BlockEntityBehaviour.get((BlockGetter)world, neighbourPos, FluidTransportBehaviour.TYPE);
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get((BlockGetter)world, neighbourPos, BracketedBlockEntityBehaviour.TYPE);
        if (FluidPipeBlock.isPipe(neighbour)) {
            return bracket == null || !bracket.isBracketPresent() || FluidPropagator.getStraightPipeAxis(neighbour) == direction.getAxis();
        }
        if (transport == null) {
            return false;
        }
        return transport.canHaveFlowToward(neighbour, direction.getOpposite());
    }

    public static boolean shouldDrawRim(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos offsetPos = pos.relative(direction);
        BlockState facingState = world.getBlockState(offsetPos);
        if (facingState.getBlock() instanceof EncasedPipeBlock) {
            return true;
        }
        if (!FluidPipeBlock.isPipe(facingState)) {
            return true;
        }
        return !FluidPipeBlock.canConnectTo(world, offsetPos, facingState, direction);
    }

    public static boolean isOpenAt(BlockState state, Direction direction) {
        return (Boolean)state.getValue((Property)PROPERTY_BY_DIRECTION.get(direction));
    }

    public static boolean isCornerOrEndPipe(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        return FluidPipeBlock.isPipe(state) && FluidPropagator.getStraightPipeAxis(state) == null && !FluidPipeBlock.shouldDrawCasing(world, pos, state);
    }

    public static boolean shouldDrawCasing(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        if (!FluidPipeBlock.isPipe(state)) {
            return false;
        }
        for (Direction.Axis axis : Iterate.axes) {
            int connections = 0;
            for (Direction direction : Iterate.directions) {
                if (direction.getAxis() == axis || !FluidPipeBlock.isOpenAt(state, direction)) continue;
                ++connections;
            }
            if (connections <= 2) continue;
            return true;
        }
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, UP, DOWN, BlockStateProperties.WATERLOGGED});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState FluidState2 = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)this.updateBlockState(this.defaultBlockState(), context.getNearestLookingDirection(), null, (BlockAndTintGetter)context.getLevel(), context.getClickedPos()).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(FluidState2.getType() == Fluids.WATER));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        if (FluidPipeBlock.isOpenAt(state, direction) && neighbourState.hasProperty((Property)BlockStateProperties.WATERLOGGED)) {
            world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
        }
        return this.updateBlockState(state, direction, direction.getOpposite(), (BlockAndTintGetter)world, pos);
    }

    public BlockState updateBlockState(BlockState state, Direction preferredDirection, @Nullable Direction ignore, BlockAndTintGetter world, BlockPos pos) {
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get((BlockGetter)world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (bracket != null && bracket.isBracketPresent()) {
            return state;
        }
        BlockState prevState = state;
        int prevStateSides = (int)Arrays.stream(Iterate.directions).map(PROPERTY_BY_DIRECTION::get).filter(arg_0 -> ((BlockState)prevState).getValue(arg_0)).count();
        for (Direction d : Iterate.directions) {
            if (d == ignore) continue;
            boolean shouldConnect = FluidPipeBlock.canConnectTo(world, pos.relative(d), world.getBlockState(pos.relative(d)), d);
            state = (BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(d), (Comparable)Boolean.valueOf(shouldConnect));
        }
        Direction connectedDirection = null;
        for (Direction d : Iterate.directions) {
            if (!FluidPipeBlock.isOpenAt(state, d)) continue;
            if (connectedDirection != null) {
                return state;
            }
            connectedDirection = d;
        }
        if (connectedDirection != null) {
            return (BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(connectedDirection.getOpposite()), (Comparable)Boolean.valueOf(true));
        }
        if (prevStateSides == 2) {
            return prevState;
        }
        return (BlockState)((BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(preferredDirection), (Comparable)Boolean.valueOf(true))).setValue((Property)PROPERTY_BY_DIRECTION.get(preferredDirection.getOpposite()), (Comparable)Boolean.valueOf(true));
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext) {
        BracketedBlockEntityBehaviour behaviour = BracketedBlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (behaviour == null) {
            return Optional.empty();
        }
        BlockState bracket = behaviour.removeBracket(inOnReplacedContext);
        if (bracket == null) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack((ItemLike)bracket.getBlock()));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<FluidPipeBlockEntity> getBlockEntityClass() {
        return FluidPipeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.FLUID_PIPE.get();
    }

    public boolean supportsExternalFaceHiding(BlockState state) {
        return false;
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return OCCLUSION_BOX;
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return FluidPipeBlockRotation.rotate(pState, pRotation);
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return FluidPipeBlockRotation.mirror(pState, pMirror);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return FluidPipeBlockRotation.transform(state, transform);
    }

    @NotNull
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }
}
