/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.block.IHaveBigOutline
 *  com.simibubi.create.foundation.block.ProperWaterloggedBlock
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.steering_wheel;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.api.IDirectionalAnalogOutput;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimClickInteractions;
import dev.simulated_team.simulated.util.QuietUse;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SteeringWheelBlock
extends HorizontalDirectionalBlock
implements IBE<SteeringWheelBlockEntity>,
ProperWaterloggedBlock,
IRotate,
IHaveBigOutline,
QuietUse,
IDirectionalAnalogOutput {
    public static final BooleanProperty ON_FLOOR = BooleanProperty.create((String)"on_floor");
    public static final MapCodec<SteeringWheelBlock> CODEC = SteeringWheelBlock.simpleCodec(SteeringWheelBlock::new);

    public SteeringWheelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)ON_FLOOR, (Comparable)Boolean.valueOf(true)));
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Player player;
        EntityCollisionContext entityContext;
        Entity entity;
        boolean onFloor = (Boolean)state.getValue((Property)ON_FLOOR);
        Direction facing = (Direction)state.getValue((Property)FACING);
        if (context instanceof EntityCollisionContext && (entity = (entityContext = (EntityCollisionContext)context).getEntity()) instanceof Player && (player = (Player)entity).isLocalPlayer()) {
            VoxelShape wheel = (onFloor ? SimBlockShapes.STEERING_WHEEL_FLOOR : SimBlockShapes.STEERING_WHEEL_CEILING).get(facing);
            VoxelShape mount = SimBlockShapes.STEERING_WHEEL_MOUNT.get(facing);
            return SteeringWheelBlock.lookingAtWheel(player, pos, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), wheel, mount) ? wheel : mount;
        }
        if (((Boolean)state.getValue((Property)ON_FLOOR)).booleanValue()) {
            return SimBlockShapes.STEERING_WHEEL_FULL_FLOOR.get(facing);
        }
        return SimBlockShapes.STEERING_WHEEL_FULL_CEILING.get(facing);
    }

    public static boolean lookingAtWheel(Player player, BlockPos pos, float pt, BlockState state) {
        boolean onFloor = (Boolean)state.getValue((Property)ON_FLOOR);
        Direction facing = (Direction)state.getValue((Property)FACING);
        VoxelShape wheel = (onFloor ? SimBlockShapes.STEERING_WHEEL_FLOOR : SimBlockShapes.STEERING_WHEEL_CEILING).get(facing);
        VoxelShape mount = SimBlockShapes.STEERING_WHEEL_MOUNT.get(facing);
        return SteeringWheelBlock.lookingAtWheel(player, pos, pt, wheel, mount);
    }

    public static boolean lookingAtWheel(Player player, BlockPos pos, float pt, VoxelShape wheel, VoxelShape mount) {
        Vec3 from = player.getEyePosition(pt);
        Vec3 to = from.add(player.getViewVector(pt).scale(player.blockInteractionRange()));
        SubLevel subLevel = Sable.HELPER.getContaining(player.level(), (Vec3i)pos);
        if (subLevel != null) {
            Pose3d pose;
            if (subLevel instanceof ClientSubLevel) {
                ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
                pose = clientSubLevel.renderPose(pt);
            } else {
                pose = subLevel.logicalPose();
            }
            from = pose.transformPositionInverse(from);
            to = pose.transformPositionInverse(to);
        }
        BlockHitResult wheelResult = wheel.clip(from, to, pos);
        BlockHitResult mountResult = mount.clip(from, to, pos);
        if (wheelResult == null || wheelResult.getType() == HitResult.Type.MISS) {
            return false;
        }
        if (mountResult == null || mountResult.getType() == HitResult.Type.MISS) {
            return true;
        }
        return wheelResult.getLocation().distanceTo(from) < mountResult.getLocation().distanceTo(from);
    }

    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (((Boolean)state.getValue((Property)ON_FLOOR)).booleanValue()) {
            return SimBlockShapes.STEERING_WHEEL_FULL_FLOOR.get((Direction)state.getValue((Property)FACING));
        }
        return SimBlockShapes.STEERING_WHEEL_FULL_CEILING.get((Direction)state.getValue((Property)FACING));
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SimBlockShapes.STEERING_WHEEL_MOUNT.get((Direction)state.getValue((Property)FACING));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{WATERLOGGED}).add(new Property[]{FACING}).add(new Property[]{ON_FLOOR}));
    }

    @Override
    @Nullable
    public InteractionResult quietUse(Player player, InteractionHand hand, BlockPos pos, BlockState state) {
        if (SteeringWheelBlock.lookingAtWheel(player, pos, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), state)) {
            return this.getBlockEntityOptional((BlockGetter)player.level(), pos).map(be -> {
                if (!(be.held || be.isMaterialValid(player.getItemInHand(hand)) || be.angleInput.testHit(this.getPlayerHitLocation()))) {
                    SimClickInteractions.STEERING_WHEEL_MANAGER.startHold(player.level(), player, pos);
                    return InteractionResult.SUCCESS;
                }
                return null;
            }).orElse(null);
        }
        return null;
    }

    public Vec3 getPlayerHitLocation() {
        return Minecraft.getInstance().hitResult.getLocation();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> be.applyMaterialIfValid(stack));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState defaultState = this.withWater(this.defaultBlockState(), context);
        boolean floor = switch (context.getClickedFace()) {
            case Direction.UP -> true;
            case Direction.DOWN -> false;
            default -> {
                Direction verticalLookDir = Arrays.stream(context.getNearestLookingDirections()).filter(d -> d.getAxis().isVertical()).findFirst().get();
                if (verticalLookDir == Direction.DOWN) {
                    yield true;
                }
                yield false;
            }
        };
        Direction horizontalLookDir = Arrays.stream(context.getNearestLookingDirections()).filter(d -> d.getAxis().isHorizontal()).findFirst().get();
        return (BlockState)((BlockState)defaultState.setValue((Property)FACING, (Comparable)horizontalLookDir.getOpposite())).setValue((Property)ON_FLOOR, (Comparable)Boolean.valueOf(floor));
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    protected boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignalFrom(BlockState blockState, Level level, BlockPos blockPos, Direction dir) {
        Direction facing = (Direction)blockState.getValue((Property)FACING);
        SteeringWheelBlockEntity be = (SteeringWheelBlockEntity)this.getBlockEntity((BlockGetter)level, blockPos);
        float frac = Mth.clamp((float)(be.targetAngleToUpdate / (float)be.angleInput.getValue()), (float)-1.0f, (float)1.0f);
        if (facing == dir) {
            return be.held ? 15 : 0;
        }
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

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == ((Boolean)state.getValue((Property)ON_FLOOR) != false ? Direction.DOWN : Direction.UP);
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    public Class<SteeringWheelBlockEntity> getBlockEntityClass() {
        return SteeringWheelBlockEntity.class;
    }

    public BlockEntityType<? extends SteeringWheelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.STEERING_WHEEL.get();
    }
}
