/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.MapItem
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.level.saveddata.maps.MapItemSavedData
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.depot.SharedDepotBlockMethods;
import com.simibubi.create.content.trains.station.AssemblyScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationMapData;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class StationBlock
extends Block
implements IBE<StationBlockEntity>,
IWrenchable,
ProperWaterloggedBlock {
    public static final BooleanProperty ASSEMBLING = BooleanProperty.create((String)"assembling");

    public StationBlock(BlockBehaviour.Properties p_54120_) {
        super(p_54120_);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)ASSEMBLING, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{ASSEMBLING, WATERLOGGED}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.withWater(super.getStateForPlacement(pContext), pContext);
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return this.getBlockEntityOptional((BlockGetter)pLevel, pPos).map(ste -> ste.trainPresent ? 15 : 0).orElse(0);
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        SharedDepotBlockMethods.onLanded(worldIn, entityIn);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null || player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (AllItems.WRENCH.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.getItem() == Items.FILLED_MAP) {
            return this.onBlockEntityUseItemOn((BlockGetter)level, pos, station -> {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (station.getStation() == null || station.getStation().getId() == null) {
                    return ItemInteractionResult.FAIL;
                }
                MapItemSavedData savedData = MapItem.getSavedData((ItemStack)stack, (Level)level);
                if (!(savedData instanceof StationMapData)) {
                    return ItemInteractionResult.FAIL;
                }
                StationMapData stationMapData = (StationMapData)savedData;
                if (!stationMapData.toggleStation((LevelAccessor)level, pos, (StationBlockEntity)station)) {
                    return ItemInteractionResult.FAIL;
                }
                return ItemInteractionResult.SUCCESS;
            });
        }
        InteractionResult result = this.onBlockEntityUse((BlockGetter)level, pos, station -> {
            ItemStack autoSchedule = station.getAutoSchedule();
            if (autoSchedule.isEmpty()) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.getInventory().placeItemBackInInventory(autoSchedule.copy());
            station.depotBehaviour.removeHeldItem();
            station.notifyUpdate();
            AllSoundEvents.playItemPickup(player);
            return InteractionResult.SUCCESS;
        });
        if (result == InteractionResult.PASS) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.withBlockEntityDo((BlockGetter)level, pos, be -> this.displayScreen((StationBlockEntity)be, player)));
        }
        return ItemInteractionResult.SUCCESS;
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void displayScreen(StationBlockEntity be, Player player) {
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        GlobalStation station = be.getStation();
        BlockState blockState = be.getBlockState();
        if (station == null || blockState == null) {
            return;
        }
        boolean assembling = blockState.getBlock() == this && (Boolean)blockState.getValue((Property)ASSEMBLING) != false;
        ScreenOpener.open((Screen)(assembling ? new AssemblyScreen(be, station) : new StationScreen(be, station)));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.STATION;
    }

    @Override
    public Class<StationBlockEntity> getBlockEntityClass() {
        return StationBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends StationBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.TRACK_STATION.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
