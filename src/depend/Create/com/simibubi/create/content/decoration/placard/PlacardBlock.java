/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.decoration.placard;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.decoration.placard.PlacardBlockEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PlacardBlock
extends FaceAttachedHorizontalDirectionalBlock
implements ProperWaterloggedBlock,
IBE<PlacardBlockEntity>,
SpecialBlockItemRequirement,
IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final MapCodec<PlacardBlock> CODEC = PlacardBlock.simpleCodec(PlacardBlock::new);

    public PlacardBlock(BlockBehaviour.Properties p_53182_) {
        super(p_53182_);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACE, FACING, WATERLOGGED, POWERED}));
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return PlacardBlock.canAttachLenient(pLevel, pPos, PlacardBlock.getConnectedDirection((BlockState)pState).getOpposite());
    }

    public static boolean canAttachLenient(LevelReader pReader, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.relative(pDirection);
        return !pReader.getBlockState(blockpos).getCollisionShape((BlockGetter)pReader, blockpos).isEmpty();
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        if (stateForPlacement == null) {
            return null;
        }
        if (stateForPlacement.getValue((Property)FACE) == AttachFace.FLOOR) {
            stateForPlacement = (BlockState)stateForPlacement.setValue((Property)FACING, (Comparable)((Direction)stateForPlacement.getValue((Property)FACING)).getOpposite());
        }
        return this.withWater(stateForPlacement, pContext);
    }

    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return (Boolean)pBlockState.getValue((Property)POWERED) != false ? 15 : 0;
    }

    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return (Boolean)pBlockState.getValue((Property)POWERED) != false && PlacardBlock.getConnectedDirection((BlockState)pBlockState) == pSide ? 15 : 0;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.PLACARD.get(PlacardBlock.getConnectedDirection((BlockState)pState));
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        ItemStack inHand = player.getItemInHand(hand);
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, pte -> {
            ItemStack inBlock = pte.getHeldItem();
            if (!player.mayBuild() || inHand.isEmpty() || !inBlock.isEmpty()) {
                boolean test;
                if (inBlock.isEmpty()) {
                    return ItemInteractionResult.FAIL;
                }
                if (inHand.isEmpty()) {
                    return ItemInteractionResult.FAIL;
                }
                if (((Boolean)state.getValue((Property)POWERED)).booleanValue()) {
                    return ItemInteractionResult.FAIL;
                }
                boolean bl = test = inBlock.getItem() instanceof FilterItem ? FilterItemStack.of(inBlock).test(level, inHand) : ItemStack.isSameItemSameComponents((ItemStack)inHand, (ItemStack)inBlock);
                if (!test) {
                    AllSoundEvents.DENY.play(level, null, (Vec3i)pos, 1.0f, 1.0f);
                    return ItemInteractionResult.SUCCESS;
                }
                AllSoundEvents.CONFIRM.play(level, null, (Vec3i)pos, 1.0f, 1.0f);
                level.setBlock(pos, (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true)), 3);
                PlacardBlock.updateNeighbours(state, level, pos);
                pte.poweredTicks = 19;
                pte.notifyUpdate();
                return ItemInteractionResult.SUCCESS;
            }
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
            pte.setHeldItem(inHand.copyWithCount(1));
            if (!player.isCreative()) {
                inHand.shrink(1);
                if (inHand.isEmpty()) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    public static Direction connectedDirection(BlockState state) {
        return PlacardBlock.getConnectedDirection((BlockState)state);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean blockChanged;
        boolean bl = blockChanged = !pState.is(pNewState.getBlock());
        if (!pIsMoving && blockChanged && ((Boolean)pState.getValue((Property)POWERED)).booleanValue()) {
            PlacardBlock.updateNeighbours(pState, pLevel, pPos);
        }
        if (pState.hasBlockEntity() && (blockChanged || !pNewState.hasBlockEntity())) {
            if (!pIsMoving) {
                this.withBlockEntityDo((BlockGetter)pLevel, pPos, be -> Block.popResource((Level)pLevel, (BlockPos)pPos, (ItemStack)be.getHeldItem()));
            }
            pLevel.removeBlockEntity(pPos);
        }
    }

    public static void updateNeighbours(BlockState pState, Level pLevel, BlockPos pPos) {
        pLevel.updateNeighborsAt(pPos, pState.getBlock());
        pLevel.updateNeighborsAt(pPos.relative(PlacardBlock.getConnectedDirection((BlockState)pState).getOpposite()), pState.getBlock());
    }

    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (pLevel.isClientSide) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, pte -> {
            ItemStack heldItem = pte.getHeldItem();
            if (heldItem.isEmpty()) {
                return;
            }
            pPlayer.getInventory().placeItemBackInInventory(heldItem);
            pLevel.playSound(null, pPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
            pte.setHeldItem(ItemStack.EMPTY);
        });
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        PlacardBlockEntity pbe;
        ItemStack heldItem;
        ItemStack placardStack = AllBlocks.PLACARD.asStack();
        if (be instanceof PlacardBlockEntity && !(heldItem = (pbe = (PlacardBlockEntity)be).getHeldItem()).isEmpty()) {
            return new ItemRequirement(List.of(new ItemRequirement.StackRequirement(placardStack, ItemRequirement.ItemUseType.CONSUME), new ItemRequirement.StrictNbtStackRequirement(heldItem, ItemRequirement.ItemUseType.CONSUME)));
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, placardStack);
    }

    @Override
    public Class<PlacardBlockEntity> getBlockEntityClass() {
        return PlacardBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PlacardBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.PLACARD.get();
    }

    @NotNull
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
