/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteShapes;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractChuteBlock
extends Block
implements IWrenchable,
IBE<ChuteBlockEntity> {
    public AbstractChuteBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    public static boolean isChute(BlockState state) {
        return state.getBlock() instanceof AbstractChuteBlock;
    }

    public static boolean isOpenChute(BlockState state) {
        return AbstractChuteBlock.isChute(state) && ((AbstractChuteBlock)state.getBlock()).isOpen(state);
    }

    public static boolean isTransparentChute(BlockState state) {
        return AbstractChuteBlock.isChute(state) && ((AbstractChuteBlock)state.getBlock()).isTransparent(state);
    }

    @Nullable
    public static Direction getChuteFacing(BlockState state) {
        return !AbstractChuteBlock.isChute(state) ? null : ((AbstractChuteBlock)state.getBlock()).getFacing(state);
    }

    public Direction getFacing(BlockState state) {
        return Direction.DOWN;
    }

    public boolean isOpen(BlockState state) {
        return true;
    }

    public boolean isTransparent(BlockState state) {
        return false;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        ItemStack stack = ItemHelper.fromItemEntity(entityIn);
        if (stack.isEmpty()) {
            return;
        }
        if (entityIn.level().isClientSide) {
            return;
        }
        if (!entityIn.isAlive()) {
            return;
        }
        BlockPos pos = BlockPos.containing((Position)entityIn.position().add(0.0, 0.5, 0.0)).below();
        DirectBeltInputBehaviour input = BlockEntityBehaviour.get((BlockGetter)entityIn.level(), pos, DirectBeltInputBehaviour.TYPE);
        if (input == null) {
            return;
        }
        if (!input.canInsertFromSide(Direction.UP)) {
            return;
        }
        if (!PackageEntity.centerPackage(entityIn, Vec3.atBottomCenterOf((Vec3i)pos.above()))) {
            return;
        }
        ItemStack remainder = input.handleInsertion(stack, Direction.UP, false);
        if (remainder.isEmpty()) {
            entityIn.discard();
            if (entityIn instanceof PackageEntity) {
                PackageEntity box = (PackageEntity)entityIn;
                Player player = (Player)box.tossedBy.get();
                if (player != null) {
                    AllAdvancements.PACKAGE_CHUTE_THROW.awardTo(player);
                }
            }
        } else if (remainder.getCount() < stack.getCount() && entityIn instanceof ItemEntity) {
            ((ItemEntity)entityIn).setItem(remainder);
        }
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        this.withBlockEntityDo((BlockGetter)world, pos, ChuteBlockEntity::onAdded);
        this.updateDiagonalNeighbour(state, world, pos);
    }

    protected void updateDiagonalNeighbour(BlockState state, Level world, BlockPos pos) {
        BlockState stateToUpdate;
        if (!AbstractChuteBlock.isChute(state)) {
            return;
        }
        AbstractChuteBlock block = (AbstractChuteBlock)state.getBlock();
        Direction facing = block.getFacing(state);
        BlockPos toUpdate = pos.below();
        if (facing.getAxis().isHorizontal()) {
            toUpdate = toUpdate.relative(facing.getOpposite());
        }
        if (AbstractChuteBlock.isChute(stateToUpdate = world.getBlockState(toUpdate)) && !world.getBlockTicks().hasScheduledTick(toUpdate, (Object)stateToUpdate.getBlock())) {
            world.scheduleTick(toUpdate, stateToUpdate.getBlock(), 1);
        }
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, world, pos, newState);
        if (state.is(newState.getBlock())) {
            return;
        }
        this.updateDiagonalNeighbour(state, world, pos);
        for (Direction direction : Iterate.horizontalDirections) {
            BlockPos toUpdate = pos.above().relative(direction);
            BlockState stateToUpdate = world.getBlockState(toUpdate);
            if (!AbstractChuteBlock.isChute(stateToUpdate) || world.getBlockTicks().hasScheduledTick(toUpdate, (Object)stateToUpdate.getBlock())) continue;
            world.scheduleTick(toUpdate, stateToUpdate.getBlock(), 1);
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockState updated = this.updateChuteState(pState, pLevel.getBlockState(pPos.above()), (BlockGetter)pLevel, pPos);
        if (pState != updated) {
            pLevel.setBlockAndUpdate(pPos, updated);
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState above, LevelAccessor world, BlockPos pos, BlockPos p_196271_6_) {
        if (direction != Direction.UP) {
            return state;
        }
        return this.updateChuteState(state, above, (BlockGetter)world, pos);
    }

    public void neighborChanged(BlockState p_220069_1_, Level world, BlockPos pos, Block p_220069_4_, BlockPos neighbourPos, boolean p_220069_6_) {
        if (pos.below().equals((Object)neighbourPos)) {
            this.withBlockEntityDo((BlockGetter)world, pos, ChuteBlockEntity::blockBelowChanged);
        }
    }

    public abstract BlockState updateChuteState(BlockState var1, BlockState var2, BlockGetter var3, BlockPos var4);

    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return ChuteShapes.getShape(p_220053_1_);
    }

    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        return ChuteShapes.getCollisionShape(p_220071_1_);
    }

    @Override
    public Class<ChuteBlockEntity> getBlockEntityClass() {
        return ChuteBlockEntity.class;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> {
            if (be.item.isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            player.getInventory().placeItemBackInInventory(be.item);
            be.setItem(ItemStack.EMPTY);
            return ItemInteractionResult.SUCCESS;
        });
    }
}
