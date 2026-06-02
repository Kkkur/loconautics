/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.processing.AssemblyOperatorUseContext;
import com.simibubi.create.foundation.block.IBE;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeployerBlock
extends DirectionalAxisKineticBlock
implements IBE<DeployerBlockEntity> {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public DeployerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.DEPLOYER_INTERACTION.get((Direction)state.getValue((Property)FACING));
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_12PX.get((Direction)state.getValue((Property)FACING));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Vec3 normal = Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)FACING)).getNormal());
        Vec3 location = context.getClickLocation().subtract(Vec3.atCenterOf((Vec3i)context.getClickedPos()).subtract(normal.scale(0.5))).multiply(normal);
        if (location.length() > 0.75) {
            if (!context.getLevel().isClientSide) {
                this.withBlockEntityDo((BlockGetter)context.getLevel(), context.getClickedPos(), DeployerBlockEntity::changeMode);
            }
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (placer instanceof ServerPlayer) {
            this.withBlockEntityDo((BlockGetter)worldIn, pos, dbe -> {
                dbe.owner = placer.getUUID();
            });
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && !state.is(newState.getBlock())) {
            this.withBlockEntityDo((BlockGetter)worldIn, pos, DeployerBlockEntity::discardPlayer);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldByPlayer = stack.copy();
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(heldByPlayer) && placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)heldByPlayer.getItem(), player, hand, hitResult).consumesAction()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (AllItems.WRENCH.isIn(heldByPlayer)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        Vec3 normal = Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)FACING)).getNormal());
        Vec3 location = hitResult.getLocation().subtract(Vec3.atCenterOf((Vec3i)pos).subtract(normal.scale(0.5))).multiply(normal);
        if (location.length() < 0.75) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, be -> {
            ItemStack heldByDeployer = be.player.getMainHandItem().copy();
            if (heldByDeployer.isEmpty() && heldByPlayer.isEmpty()) {
                return;
            }
            player.setItemInHand(hand, heldByDeployer);
            be.player.setItemInHand(InteractionHand.MAIN_HAND, heldByPlayer);
            be.notifyUpdate();
        });
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public Class<DeployerBlockEntity> getBlockEntityClass() {
        return DeployerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DeployerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.DEPLOYER.get();
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        this.withBlockEntityDo((BlockGetter)world, pos, DeployerBlockEntity::redstoneUpdate);
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        this.withBlockEntityDo((BlockGetter)world, pos, DeployerBlockEntity::redstoneUpdate);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        if (context instanceof AssemblyOperatorUseContext) {
            return Direction.DOWN;
        }
        return super.getFacingForPlacement(context);
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.DEPLOYER.isIn(arg_0);
        }

        public Predicate<BlockState> getStatePredicate() {
            return arg_0 -> AllBlocks.DEPLOYER.has(arg_0);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)).getAxis(), dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> (BlockState)((BlockState)s.setValue((Property)DirectionalKineticBlock.FACING, (Comparable)((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)))).setValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE, (Comparable)((Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE))));
        }
    }
}
