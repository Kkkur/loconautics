/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class StockTickerBlock
extends HorizontalDirectionalBlock
implements IBE<StockTickerBlockEntity>,
IWrenchable {
    public static final MapCodec<StockTickerBlock> CODEC = StockTickerBlock.simpleCodec(StockTickerBlock::new);

    public StockTickerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        boolean reverse = pContext.getPlayer() != null && pContext.getPlayer().isShiftKeyDown();
        return (BlockState)super.getStateForPlacement(pContext).setValue((Property)FACING, (Comparable)(reverse ? facing.getOpposite() : facing));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACING}));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof LogisticallyLinkedBlockItem) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, stbe -> {
            if (!stbe.behaviour.mayInteractMessage(player)) {
                return ItemInteractionResult.SUCCESS;
            }
            if (!level.isClientSide() && !stbe.receivedPayments.isEmpty()) {
                for (int i = 0; i < stbe.receivedPayments.getSlots(); ++i) {
                    player.getInventory().placeItemBackInInventory(stbe.receivedPayments.extractItem(i, stbe.receivedPayments.getStackInSlot(i).getCount(), false));
                }
                AllSoundEvents.playItemPickup(player);
                return ItemInteractionResult.SUCCESS;
            }
            if (player instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)player;
                if (stbe.isKeeperPresent()) {
                    sp.openMenu((MenuProvider)new StockTickerBlockEntity.CategoryMenuProvider((StockTickerBlockEntity)stbe), stbe.getBlockPos());
                } else {
                    CreateLang.translate("stock_ticker.keeper_missing", new Object[0]).sendStatus(player);
                }
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.STOCK_TICKER;
    }

    @OnlyIn(value=Dist.CLIENT)
    public PartialModel getHat(LevelAccessor level, BlockPos pos, LivingEntity keeper) {
        return AllPartialModels.LOGISTICS_HAT;
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    @Override
    public Class<StockTickerBlockEntity> getBlockEntityClass() {
        return StockTickerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends StockTickerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.STOCK_TICKER.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
