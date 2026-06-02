/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlock;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@EventBusSubscriber
public class StockTickerInteractionHandler {
    @SubscribeEvent
    public static void interactWithLogisticsManager(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        if (player == null || entity == null) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        Level level = event.getLevel();
        BlockPos targetPos = StockTickerInteractionHandler.getStockTickerPosition(entity);
        if (targetPos == null) {
            return;
        }
        if (StockTickerInteractionHandler.interactWithLogisticsManagerAt(player, level, targetPos)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    public static boolean interactWithLogisticsManagerAt(Player player, Level level, BlockPos targetPos) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (AllItems.SHOPPING_LIST.isIn(mainHandItem)) {
            StockTickerInteractionHandler.interactWithShop(player, level, targetPos, mainHandItem);
            return true;
        }
        if (level.isClientSide()) {
            return true;
        }
        BlockEntity blockEntity = level.getBlockEntity(targetPos);
        if (!(blockEntity instanceof StockTickerBlockEntity)) {
            return false;
        }
        StockTickerBlockEntity stbe = (StockTickerBlockEntity)blockEntity;
        if (!stbe.behaviour.mayInteract(player)) {
            player.displayClientMessage((Component)CreateLang.translate("stock_keeper.locked", new Object[0]).style(ChatFormatting.RED).component(), true);
            return true;
        }
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            boolean showLockOption = stbe.behaviour.mayAdministrate(player) && Create.LOGISTICS.isLockable(stbe.behaviour.freqId);
            boolean isCurrentlyLocked = Create.LOGISTICS.isLocked(stbe.behaviour.freqId);
            sp.openMenu((MenuProvider)new StockTickerBlockEntity.RequestMenuProvider(stbe), buf -> {
                buf.writeBoolean(showLockOption);
                buf.writeBoolean(isCurrentlyLocked);
                buf.writeBlockPos(targetPos);
            });
            stbe.getRecentSummary().divideAndSendTo(sp, targetPos);
        }
        return true;
    }

    /*
     * WARNING - void declaration
     */
    private static void interactWithShop(Player player, Level level, BlockPos targetPos, ItemStack mainHandItem) {
        void var12_16;
        if (level.isClientSide()) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(targetPos);
        if (!(blockEntity instanceof StockTickerBlockEntity)) {
            return;
        }
        StockTickerBlockEntity tickerBE = (StockTickerBlockEntity)blockEntity;
        ShoppingListItem.ShoppingList list = ShoppingListItem.getList(mainHandItem);
        if (list == null) {
            return;
        }
        if (!tickerBE.behaviour.freqId.equals(list.shopNetwork())) {
            AllSoundEvents.DENY.playOnServer(level, (Vec3i)player.blockPosition());
            CreateLang.translate("stock_keeper.wrong_network", new Object[0]).style(ChatFormatting.RED).sendStatus(player);
            return;
        }
        Couple<InventorySummary> bakeEntries = list.bakeEntries((LevelAccessor)level, null);
        InventorySummary paymentEntries = (InventorySummary)bakeEntries.getSecond();
        InventorySummary orderEntries = (InventorySummary)bakeEntries.getFirst();
        PackageOrder order = new PackageOrder(orderEntries.getStacksByCount());
        tickerBE.getAccurateSummary();
        InventorySummary recentSummary = tickerBE.getRecentSummary();
        for (BigItemStack bigItemStack : order.stacks()) {
            if (recentSummary.getCountOf(bigItemStack.stack) >= bigItemStack.count) continue;
            AllSoundEvents.DENY.playOnServer(level, (Vec3i)player.blockPosition());
            CreateLang.translate("stock_keeper.stock_level_too_low", new Object[0]).style(ChatFormatting.RED).sendStatus(player);
            return;
        }
        int occupiedSlots = 0;
        for (BigItemStack entry : paymentEntries.getStacksByCount()) {
            occupiedSlots += Mth.ceil((float)((float)entry.count / (float)entry.stack.getMaxStackSize()));
        }
        boolean bl = false;
        while (var12_16 < tickerBE.receivedPayments.getSlots()) {
            if (tickerBE.receivedPayments.getStackInSlot((int)var12_16).isEmpty()) {
                --occupiedSlots;
            }
            ++var12_16;
        }
        if (occupiedSlots > 0) {
            AllSoundEvents.DENY.playOnServer(level, (Vec3i)player.blockPosition());
            CreateLang.translate("stock_keeper.cash_register_full", new Object[0]).style(ChatFormatting.RED).sendStatus(player);
            return;
        }
        for (boolean simulate : Iterate.trueAndFalse) {
            InventorySummary tally = paymentEntries.copy();
            ArrayList<ItemStack> toTransfer = new ArrayList<ItemStack>();
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                int countOf;
                ItemStack item = player.getInventory().getItem(i);
                if (item.isEmpty() || (countOf = tally.getCountOf(item)) == 0) continue;
                int toRemove = Math.min(item.getCount(), countOf);
                tally.add(item, -toRemove);
                if (simulate) continue;
                int newStackSize = item.getCount() - toRemove;
                player.getInventory().setItem(i, newStackSize == 0 ? ItemStack.EMPTY : item.copyWithCount(newStackSize));
                toTransfer.add(item.copyWithCount(toRemove));
            }
            if (simulate && tally.getTotalCount() != 0) {
                AllSoundEvents.DENY.playOnServer(level, (Vec3i)player.blockPosition());
                CreateLang.translate("stock_keeper.too_broke", new Object[0]).style(ChatFormatting.RED).sendStatus(player);
                return;
            }
            if (simulate) continue;
            toTransfer.forEach(s -> ItemHandlerHelper.insertItemStacked((IItemHandler)tickerBE.receivedPayments, (ItemStack)s, (boolean)false));
        }
        tickerBE.broadcastPackageRequest(LogisticallyLinkedBehaviour.RequestType.PLAYER, order, null, ShoppingListItem.getAddress(mainHandItem));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        if (!order.isEmpty()) {
            AllSoundEvents.STOCK_TICKER_TRADE.playOnServer(level, (Vec3i)tickerBE.getBlockPos());
        }
    }

    public static BlockPos getStockTickerPosition(Entity entity) {
        Entity rootVehicle = entity.getRootVehicle();
        if (!(rootVehicle instanceof SeatEntity)) {
            return null;
        }
        if (!(entity instanceof LivingEntity)) {
            return null;
        }
        if (AllEntityTypes.PACKAGE.is(entity)) {
            return null;
        }
        BlockPos pos = entity.blockPosition();
        int stations = 0;
        BlockPos targetPos = null;
        for (Direction d : Iterate.horizontalDirections) {
            for (int y : Iterate.zeroAndOne) {
                BlockPos workstationPos = pos.relative(d).above(y);
                if (!(entity.level().getBlockState(workstationPos).getBlock() instanceof StockTickerBlock)) continue;
                targetPos = workstationPos;
                ++stations;
            }
        }
        if (stations != 1) {
            return null;
        }
        return targetPos;
    }
}
