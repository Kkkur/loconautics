/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.monster.Shulker
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.level.pathfinder.PathType
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.actors.seat;

import com.google.common.base.Optional;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SeatBlock
extends Block
implements ProperWaterloggedBlock {
    protected final DyeColor color;

    public SeatBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{WATERLOGGED}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.withWater(super.getStateForPlacement(pContext), pContext);
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public void fallOn(Level p_152426_, BlockState p_152427_, BlockPos p_152428_, Entity p_152429_, float p_152430_) {
        super.fallOn(p_152426_, p_152427_, p_152428_, p_152429_, p_152430_ * 0.5f);
    }

    public void updateEntityAfterFallOn(BlockGetter reader, Entity entity) {
        BlockPos pos = entity.blockPosition();
        if (entity instanceof Player || !(entity instanceof LivingEntity) || !SeatBlock.canBePickedUp(entity) || SeatBlock.isSeatOccupied(entity.level(), pos)) {
            if (entity.isSuppressingBounce()) {
                super.updateEntityAfterFallOn(reader, entity);
                return;
            }
            Vec3 vec3 = entity.getDeltaMovement();
            if (vec3.y < 0.0) {
                double d0 = entity instanceof LivingEntity ? 1.0 : 0.8;
                entity.setDeltaMovement(vec3.x, -vec3.y * (double)0.66f * d0, vec3.z);
            }
            return;
        }
        if (reader.getBlockState(pos).getBlock() != this) {
            return;
        }
        SeatBlock.sitDown(entity.level(), pos, entity);
    }

    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return PathType.RAIL;
    }

    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return AllShapes.SEAT;
    }

    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext ctx) {
        EntityCollisionContext ecc;
        Entity entity;
        if (ctx instanceof EntityCollisionContext && (entity = (ecc = (EntityCollisionContext)ctx).getEntity()) instanceof Player) {
            Player player = (Player)entity;
            return AllShapes.SEAT_COLLISION_PLAYERS;
        }
        return AllShapes.SEAT_COLLISION;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || player instanceof FakePlayer) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        DyeColor color = DyeColor.getColor((ItemStack)stack);
        if (color != null && color != this.color) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            BlockState newState = BlockHelper.copyProperties(state, AllBlocks.SEATS.get(color).getDefaultState());
            level.setBlockAndUpdate(pos, newState);
            return ItemInteractionResult.SUCCESS;
        }
        List seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
        if (!seats.isEmpty()) {
            SeatEntity seatEntity = (SeatEntity)((Object)seats.get(0));
            List passengers = seatEntity.getPassengers();
            if (!passengers.isEmpty() && passengers.get(0) instanceof Player) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide) {
                seatEntity.ejectPassengers();
                player.startRiding((Entity)seatEntity);
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        SeatBlock.sitDown(level, pos, (Entity)SeatBlock.getLeashed(level, player).or((Object)player));
        return ItemInteractionResult.SUCCESS;
    }

    public static boolean isSeatOccupied(Level world, BlockPos pos) {
        return !world.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).isEmpty();
    }

    public static Optional<Entity> getLeashed(Level level, Player player) {
        List entities = player.level().getEntities((Entity)null, player.getBoundingBox().inflate(10.0), e -> true);
        for (Entity e2 : entities) {
            Mob mob;
            if (!(e2 instanceof Mob) || (mob = (Mob)e2).getLeashHolder() != player || !SeatBlock.canBePickedUp(e2)) continue;
            return Optional.of((Object)mob);
        }
        return Optional.absent();
    }

    public static boolean canBePickedUp(Entity passenger) {
        if (passenger instanceof Shulker) {
            return false;
        }
        if (passenger instanceof Player) {
            return false;
        }
        if (AllTags.AllEntityTags.IGNORE_SEAT.matches(passenger)) {
            return false;
        }
        if (!((Boolean)AllConfigs.server().logistics.seatHostileMobs.get()).booleanValue() && !passenger.getType().getCategory().isFriendly()) {
            return false;
        }
        return passenger instanceof LivingEntity;
    }

    public static void sitDown(Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        SeatEntity seat = new SeatEntity(level);
        seat.setPos((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
        level.addFreshEntity((Entity)seat);
        entity.startRiding((Entity)seat, true);
        if (entity instanceof TamableAnimal) {
            TamableAnimal ta = (TamableAnimal)entity;
            ta.setInSittingPose(true);
        }
    }

    public DyeColor getColor() {
        return this.color;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
