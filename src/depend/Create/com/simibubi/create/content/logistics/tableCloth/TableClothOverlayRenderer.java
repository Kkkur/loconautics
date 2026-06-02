/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.blueprint.BlueprintOverlayRenderer;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TableClothOverlayRenderer {
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult mouseOver = mc.hitResult;
        if (mouseOver == null) {
            return;
        }
        ItemStack heldItem = mc.player.getMainHandItem();
        if (mouseOver.getType() != HitResult.Type.ENTITY) {
            if (!(mouseOver instanceof BlockHitResult)) {
                return;
            }
            BlockHitResult bhr = (BlockHitResult)mouseOver;
            BlockEntity blockEntity = mc.level.getBlockEntity(bhr.getBlockPos());
            if (!(blockEntity instanceof TableClothBlockEntity)) {
                return;
            }
            TableClothBlockEntity dcbe = (TableClothBlockEntity)blockEntity;
            if (!dcbe.isShop()) {
                return;
            }
            if (AllBlocks.CLIPBOARD.isIn(heldItem)) {
                return;
            }
            if (dcbe.targetsPriceTag((Player)mc.player, bhr)) {
                return;
            }
            int alreadyPurchased = 0;
            ShoppingListItem.ShoppingList list = ShoppingListItem.getList(heldItem);
            if (list != null) {
                alreadyPurchased = list.getPurchases(dcbe.getBlockPos());
            }
            BlueprintOverlayRenderer.displayClothShop(dcbe, alreadyPurchased, list);
            return;
        }
        EntityHitResult entityRay = (EntityHitResult)mouseOver;
        if (!AllItems.SHOPPING_LIST.isIn(heldItem)) {
            return;
        }
        ShoppingListItem.ShoppingList list = ShoppingListItem.getList(heldItem);
        BlockPos stockTickerPosition = StockTickerInteractionHandler.getStockTickerPosition(entityRay.getEntity());
        if (list == null || stockTickerPosition == null) {
            return;
        }
        BlockEntity blockEntity = mc.level.getBlockEntity(stockTickerPosition);
        if (!(blockEntity instanceof StockTickerBlockEntity)) {
            return;
        }
        StockTickerBlockEntity tickerBE = (StockTickerBlockEntity)blockEntity;
        if (!tickerBE.behaviour.freqId.equals(list.shopNetwork())) {
            return;
        }
        BlueprintOverlayRenderer.displayShoppingList(list.bakeEntries((LevelAccessor)mc.level, null));
    }
}
