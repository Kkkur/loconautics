/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LadderBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.decoration;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MetalLadderBlock
extends LadderBlock
implements IWrenchable {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public MetalLadderBlock(BlockBehaviour.Properties p_54345_) {
        super(p_54345_);
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean supportsExternalFaceHiding(BlockState state) {
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
        if (pDirection != null && pDirection.getAxis().isHorizontal()) {
            return pAdjacentBlockState.isAir() || !pAdjacentBlockState.blocksMotion();
        }
        return pDirection == Direction.UP && pAdjacentBlockState.getBlock() instanceof LadderBlock;
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return AllShapes.SIX_VOXEL_POLE.get(Direction.Axis.Y);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!pState.canSurvive((LevelReader)pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState otherState = pLevel.getBlockState(pPos.relative(Direction.UP));
        return super.canSurvive(pState, pLevel, pPos) || otherState.is((Block)this) && ((Direction)pState.getValue((Property)FACING)).equals((Object)otherState.getValue((Property)FACING));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        IPlacementHelper helper = PlacementHelpers.get((int)placementHelperId);
        if (helper.matchesItem(stack)) {
            return helper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem && ((BlockItem)i.getItem()).getBlock() instanceof MetalLadderBlock;
        }

        public Predicate<BlockState> getStatePredicate() {
            return s -> s.getBlock() instanceof LadderBlock;
        }

        public int attachedLadders(Level world, BlockPos pos, Direction direction) {
            BlockPos checkPos = pos.relative(direction);
            BlockState state = world.getBlockState(checkPos);
            int count = 0;
            while (this.getStatePredicate().test(state)) {
                ++count;
                checkPos = checkPos.relative(direction);
                state = world.getBlockState(checkPos);
            }
            return count;
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            int ladders;
            AttributeInstance reach;
            Direction dir = player.getXRot() < 0.0f ? Direction.UP : Direction.DOWN;
            int range = (Integer)AllConfigs.server().equipment.placementAssistRange.get();
            if (player != null && (reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE)) != null && reach.hasModifier(ExtendoGripItem.singleRangeAttributeModifier.id())) {
                range += 4;
            }
            if ((ladders = this.attachedLadders(world, pos, dir)) >= range) {
                return PlacementOffset.fail();
            }
            BlockPos newPos = pos.relative(dir, ladders + 1);
            BlockState newState = world.getBlockState(newPos);
            if (!state.canSurvive((LevelReader)world, newPos)) {
                return PlacementOffset.fail();
            }
            if (newState.canBeReplaced()) {
                return PlacementOffset.success((Vec3i)newPos, bState -> (BlockState)bState.setValue((Property)LadderBlock.FACING, (Comparable)((Direction)state.getValue((Property)LadderBlock.FACING))));
            }
            return PlacementOffset.fail();
        }
    }
}
