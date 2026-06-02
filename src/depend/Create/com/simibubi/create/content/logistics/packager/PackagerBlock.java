/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.common.util.FakePlayer
 */
package com.simibubi.create.content.logistics.packager;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;

public class PackagerBlock
extends WrenchableDirectionalBlock
implements IBE<PackagerBlockEntity>,
IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LINKED = BooleanProperty.create((String)"linked");

    public PackagerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        BlockState defaultBlockState = this.defaultBlockState();
        if (defaultBlockState.hasProperty((Property)LINKED)) {
            defaultBlockState = (BlockState)defaultBlockState.setValue((Property)LINKED, (Comparable)Boolean.valueOf(false));
        }
        this.registerDefaultState((BlockState)defaultBlockState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing = null;
        for (Direction face : context.getNearestLookingDirections()) {
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos().relative(face));
            if (be instanceof PackagerBlockEntity || be == null || !be.hasLevel() || be.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), null) == null) continue;
            preferredFacing = face.getOpposite();
            break;
        }
        Player player = context.getPlayer();
        if (preferredFacing == null) {
            Direction facing = context.getNearestLookingDirection();
            Direction direction = preferredFacing = player != null && player.isShiftKeyDown() ? facing : facing.getOpposite();
        }
        if (player != null && !(player instanceof FakePlayer) && AllBlocks.PORTABLE_STORAGE_INTERFACE.has(context.getLevel().getBlockState(context.getClickedPos().relative(preferredFacing.getOpposite())))) {
            CreateLang.translate("packager.no_portable_storage", new Object[0]).sendStatus(player);
            return null;
        }
        return (BlockState)((BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())))).setValue((Property)FACING, (Comparable)preferredFacing);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (AllItems.WRENCH.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (AllBlocks.FACTORY_GAUGE.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(!AllBlocks.STOCK_LINK.isIn(stack) || state.hasProperty((Property)LINKED) && ((Boolean)state.getValue((Property)LINKED)).booleanValue())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (AllBlocks.PACKAGE_FROGPORT.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (this.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> {
            if (be.heldBox.isEmpty()) {
                if (be.animationTicks > 0) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (PackageItem.isPackage(stack)) {
                    if (level.isClientSide()) {
                        return ItemInteractionResult.SUCCESS;
                    }
                    if (!be.unwrapBox(stack.copy(), true)) {
                        return ItemInteractionResult.SUCCESS;
                    }
                    be.unwrapBox(stack.copy(), false);
                    be.triggerStockCheck();
                    stack.shrink(1);
                    AllSoundEvents.DEPOT_PLOP.playOnServer(level, (Vec3i)pos);
                    if (stack.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.SUCCESS;
            }
            if (be.animationTicks > 0) {
                return ItemInteractionResult.SUCCESS;
            }
            if (!level.isClientSide()) {
                player.getInventory().placeItemBackInInventory(be.heldBox.copy());
                AllSoundEvents.playItemPickup(player);
                be.heldBox = ItemStack.EMPTY;
                be.notifyUpdate();
            }
            return ItemInteractionResult.SUCCESS;
        }).consumesAction()) {
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{POWERED, LINKED}));
    }

    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        if (neighbor.relative(state.getOptionalValue((Property)FACING).orElse(Direction.UP)).equals((Object)pos)) {
            this.withBlockEntityDo((BlockGetter)level, pos, PackagerBlockEntity::triggerStockCheck);
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean previouslyPowered;
        if (worldIn.isClientSide) {
            return;
        }
        InvManipulationBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)worldIn, pos, InvManipulationBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.onNeighborChanged(fromPos);
        }
        if ((previouslyPowered = ((Boolean)state.getValue((Property)POWERED)).booleanValue()) == worldIn.hasNeighborSignal(pos)) {
            return;
        }
        worldIn.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        if (!previouslyPowered) {
            this.withBlockEntityDo((BlockGetter)worldIn, pos, PackagerBlockEntity::activate);
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public Class<PackagerBlockEntity> getBlockEntityClass() {
        return PackagerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.PACKAGER.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return this.getBlockEntityOptional((BlockGetter)pLevel, pPos).map(pbe -> {
            boolean empty = pbe.inventory.getStackInSlot(0).isEmpty();
            if (pbe.animationTicks != 0) {
                empty = false;
            }
            return empty ? 0 : 15;
        }).orElse(0);
    }
}
