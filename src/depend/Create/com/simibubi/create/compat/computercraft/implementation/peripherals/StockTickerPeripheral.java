/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.detail.VanillaDetailRegistries
 *  dan200.computercraft.api.lua.IArguments
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StockTickerPeripheral
extends SyncedPeripheral<StockTickerBlockEntity> {
    public StockTickerPeripheral(StockTickerBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread=true)
    public final Map<Integer, Map<String, ?>> stock(Optional<Boolean> detailed) {
        HashMap result = new HashMap();
        int i = 0;
        for (BigItemStack entry : ((StockTickerBlockEntity)this.blockEntity).getAccurateSummary().getStacks()) {
            HashMap<String, Integer> details = new HashMap<String, Integer>(detailed.isPresent() && detailed.get() != false ? VanillaDetailRegistries.ITEM_STACK.getDetails((Object)entry.stack) : VanillaDetailRegistries.ITEM_STACK.getBasicDetails((Object)entry.stack));
            details.put("count", entry.count);
            result.put(++i, details);
        }
        return result;
    }

    @LuaFunction(mainThread=true)
    public final Map<String, ?> getStockItemDetail(int slot) throws LuaException {
        return ComputerUtil.getItemDetail(((StockTickerBlockEntity)this.blockEntity).getAccurateSummary(), slot);
    }

    @LuaFunction(mainThread=true)
    public final int requestFiltered(String address, IArguments filters) throws LuaException {
        ArrayList<BigItemStack> validItems = new ArrayList<BigItemStack>();
        int totalItemsSent = 0;
        List<BigItemStack> stock = ((StockTickerBlockEntity)this.blockEntity).getAccurateSummary().getStacks();
        block0: for (int i = 1; i < filters.count(); ++i) {
            Iterator iterator = filters.get(i);
            if (!(iterator instanceof Map)) {
                throw new LuaException("Filter must be a table");
            }
            Map filterTable = (Map)((Object)iterator);
            for (Object key : filterTable.keySet()) {
                if (key instanceof String) continue;
                throw new LuaException("Filter keys must be strings");
            }
            Map filter = filterTable;
            int itemsRequested = Integer.MAX_VALUE;
            if (filterTable.containsKey("_requestCount")) {
                Object requestCount = filterTable.get("_requestCount");
                filterTable.remove("_requestCount");
                if (requestCount instanceof Number) {
                    itemsRequested = ((Number)requestCount).intValue();
                    if (itemsRequested < 1) {
                        throw new LuaException("_requestCount must be a positive number or nil for no limit");
                    }
                } else {
                    throw new LuaException("_requestCount must be a positive number or nil for no limit");
                }
            }
            for (BigItemStack entry : stock) {
                int foundItems = ComputerUtil.bigItemStackToLuaTableFilter(entry, filter);
                if (foundItems > 0) {
                    int toTake = Math.min(foundItems, itemsRequested);
                    itemsRequested -= toTake;
                    totalItemsSent += toTake;
                    BigItemStack requestedItem = new BigItemStack(entry.stack.copy(), toTake);
                    entry.count -= toTake;
                    validItems.add(requestedItem);
                }
                if (itemsRequested > 0) continue;
                continue block0;
            }
        }
        PackageOrder order = new PackageOrder(validItems);
        ((StockTickerBlockEntity)this.blockEntity).broadcastPackageRequest(LogisticallyLinkedBehaviour.RequestType.RESTOCK, order, null, address);
        return totalItemsSent;
    }

    @LuaFunction(mainThread=true)
    public Map<Integer, Map<String, ?>> list() {
        return ComputerUtil.list(((StockTickerBlockEntity)this.blockEntity).getReceivedPaymentsHandler());
    }

    @LuaFunction(mainThread=true)
    public Map<String, ?> getItemDetail(int slot) throws LuaException {
        return ComputerUtil.getItemDetail(((StockTickerBlockEntity)this.blockEntity).getReceivedPaymentsHandler(), slot);
    }

    @NotNull
    public String getType() {
        return "Create_StockTicker";
    }

    @Nullable
    public Object getTarget() {
        return ((StockTickerBlockEntity)this.blockEntity).getReceivedPaymentsHandler();
    }
}
