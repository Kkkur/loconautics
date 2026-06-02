/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.funnel.AbstractDirectionalFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FunnelBlock
extends AbstractDirectionalFunnelBlock {
    public static final BooleanProperty EXTRACTING = BooleanProperty.create((String)"extracting");

    public FunnelBlock(BlockBehaviour.Properties p_i48415_1_) {
        super(p_i48415_1_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)EXTRACTING, (Comparable)Boolean.valueOf(false)));
    }

    public abstract BlockState getEquivalentBeltFunnel(BlockGetter var1, BlockPos var2, BlockState var3);

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        boolean sneak = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        state = (BlockState)state.setValue((Property)EXTRACTING, (Comparable)Boolean.valueOf(!sneak));
        for (Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate = (BlockState)state.setValue((Property)FACING, (Comparable)direction.getOpposite());
            if (!blockstate.canSurvive((LevelReader)context.getLevel(), context.getClickedPos())) continue;
            return (BlockState)blockstate.setValue((Property)POWERED, (Comparable)((Boolean)state.getValue((Property)POWERED)));
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{EXTRACTING}));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        BeltFunnelBlock bfb;
        Block block = newState.getBlock();
        if (block instanceof BeltFunnelBlock && (bfb = (BeltFunnelBlock)block).isOfSameType(this)) {
            return;
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean shouldntInsertItem;
        boolean bl = shouldntInsertItem = AllBlocks.MECHANICAL_ARM.isIn(stack) || !this.canInsertIntoFunnel(state);
        if (AllItems.WRENCH.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (hitResult.getDirection() == FunnelBlock.getFunnelFacing(state) && !shouldntInsertItem) {
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, be -> {
                    ItemStack toInsert = stack.copy();
                    ItemStack remainder = FunnelBlock.tryInsert(level, pos, toInsert, false);
                    if (!ItemStack.matches((ItemStack)remainder, (ItemStack)toInsert) || remainder.getCount() != stack.getCount()) {
                        player.setItemInHand(hand, remainder);
                    }
                });
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            world.setBlockAndUpdate(context.getClickedPos(), (BlockState)state.cycle((Property)EXTRACTING));
        }
        return InteractionResult.SUCCESS;
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide) {
            return;
        }
        ItemStack stack = ItemHelper.fromItemEntity(entityIn);
        if (stack.isEmpty()) {
            return;
        }
        if (!this.canInsertIntoFunnel(state)) {
            return;
        }
        Direction direction = FunnelBlock.getFunnelFacing(state);
        Vec3 openPos = VecHelper.getCenterOf((Vec3i)pos).add(Vec3.atLowerCornerOf((Vec3i)direction.getNormal()).scale(entityIn instanceof ItemEntity ? -0.25 : -0.125));
        Vec3 diff = entityIn.position().subtract(openPos);
        double projectedDiff = direction.getAxis().choose(diff.x, diff.y, diff.z);
        if (projectedDiff < 0.0 == (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE)) {
            return;
        }
        float yOffset = direction == Direction.UP ? 0.25f : (direction == Direction.DOWN ? -0.5f : -0.5f);
        FilteringBehaviour filter = BlockEntityBehaviour.get((BlockGetter)worldIn, pos, FilteringBehaviour.TYPE);
        if (filter.test(stack) && !PackageEntity.centerPackage(entityIn, openPos.add(0.0, (double)yOffset, 0.0))) {
            return;
        }
        ItemStack remainder = FunnelBlock.tryInsert(worldIn, pos, stack, false);
        if (remainder.isEmpty()) {
            entityIn.discard();
        }
        if (remainder.getCount() < stack.getCount() && entityIn instanceof ItemEntity) {
            ((ItemEntity)entityIn).setItem(remainder);
        }
    }

    protected boolean canInsertIntoFunnel(BlockState state) {
        return (Boolean)state.getValue((Property)POWERED) == false && (Boolean)state.getValue((Property)EXTRACTING) == false;
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        return facing == Direction.DOWN ? AllShapes.FUNNEL_CEILING : (facing == Direction.UP ? AllShapes.FUNNEL_FLOOR : AllShapes.FUNNEL_WALL.get(facing));
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext)context).getEntity() instanceof ItemEntity && this.getFacing(state).getAxis().isHorizontal()) {
            return AllShapes.FUNNEL_COLLISION.get(this.getFacing(state));
        }
        return this.getShape(state, world, pos, context);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState p_196271_3_, LevelAccessor world, BlockPos pos, BlockPos p_196271_6_) {
        this.updateWater(world, state, pos);
        if (this.getFacing(state).getAxis().isVertical() || direction != Direction.DOWN) {
            return state;
        }
        BlockState equivalentFunnel = ProperWaterloggedBlock.withWater(world, this.getEquivalentBeltFunnel(null, null, state), pos);
        if (BeltFunnelBlock.isOnValidBelt(equivalentFunnel, (LevelReader)world, pos)) {
            return (BlockState)equivalentFunnel.setValue(BeltFunnelBlock.SHAPE, (Comparable)((Object)BeltFunnelBlock.getShapeForPosition((BlockGetter)world, pos, this.getFacing(state), (Boolean)state.getValue((Property)EXTRACTING))));
        }
        return state;
    }
}
