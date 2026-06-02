/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.world.item.ItemStack
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.google.common.cache.Cache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.TickBasedCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public class LogisticsManager {
    private static Random r = new Random();
    public static final Cache<UUID, InventorySummary> ACCURATE_SUMMARIES = new TickBasedCache<UUID, InventorySummary>(1, false);
    public static final Cache<UUID, InventorySummary> SUMMARIES = new TickBasedCache<UUID, InventorySummary>(20, false);

    public static InventorySummary getSummaryOfNetwork(UUID freqId, boolean accurate) {
        try {
            Cache<UUID, InventorySummary> cacheToUse = accurate ? ACCURATE_SUMMARIES : SUMMARIES;
            return (InventorySummary)cacheToUse.get((Object)freqId, () -> LogisticsManager.createSummaryOfNetwork(freqId));
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            return InventorySummary.EMPTY;
        }
    }

    private static InventorySummary createSummaryOfNetwork(UUID freqId) {
        InventorySummary summaryOfLinks = new InventorySummary();
        HashSet<InventoryIdentifier> processedInventories = new HashSet<InventoryIdentifier>();
        for (LogisticallyLinkedBehaviour link : LogisticallyLinkedBehaviour.getAllPresent(freqId, false)) {
            InventorySummary summary;
            InventoryIdentifier currentInventoryId = LogisticsManager.getInventoryIdentifierFromLink(link);
            if (currentInventoryId != null && !processedInventories.add(currentInventoryId) || (summary = link.getSummary(null)) == InventorySummary.EMPTY) continue;
            ++summaryOfLinks.contributingLinks;
            summaryOfLinks.add(summary);
        }
        return summaryOfLinks;
    }

    public static int getStockOf(UUID freqId, ItemStack stack, @Nullable IdentifiedInventory ignoredHandler) {
        int sum = 0;
        for (LogisticallyLinkedBehaviour link : LogisticallyLinkedBehaviour.getAllPresent(freqId, false)) {
            sum += link.getSummary(ignoredHandler).getCountOf(stack);
        }
        return sum;
    }

    public static boolean broadcastPackageRequest(UUID freqId, LogisticallyLinkedBehaviour.RequestType type, PackageOrderWithCrafts order, @Nullable IdentifiedInventory ignoredHandler, String address) {
        if (order.isEmpty()) {
            return false;
        }
        Multimap<PackagerBlockEntity, PackagingRequest> requests = LogisticsManager.findPackagersForRequest(freqId, order, ignoredHandler, address);
        for (PackagerBlockEntity packager : requests.keySet()) {
            if (!packager.isTooBusyFor(type)) continue;
            return false;
        }
        LogisticsManager.performPackageRequests(requests);
        return true;
    }

    public static Multimap<PackagerBlockEntity, PackagingRequest> findPackagersForRequest(UUID freqId, PackageOrderWithCrafts order, @Nullable IdentifiedInventory ignoredHandler, String address) {
        ArrayList<BigItemStack> stacks = new ArrayList<BigItemStack>();
        for (BigItemStack stack : order.stacks()) {
            if (stack.stack.isEmpty() || stack.count <= 0) continue;
            stacks.add(stack);
        }
        HashMultimap requests = HashMultimap.create();
        Collection<LogisticallyLinkedBehaviour> allAvailableLinks = LogisticallyLinkedBehaviour.getAllPresent(freqId, true);
        HashMap<InventoryIdentifier, List> linksByInventory = new HashMap<InventoryIdentifier, List>();
        ArrayList<LogisticallyLinkedBehaviour> availableLinks = new ArrayList<LogisticallyLinkedBehaviour>();
        for (LogisticallyLinkedBehaviour link : allAvailableLinks) {
            InventoryIdentifier inventoryId = LogisticsManager.getInventoryIdentifierFromLink(link);
            if (inventoryId != null) {
                linksByInventory.computeIfAbsent(inventoryId, k -> new ArrayList()).add(link);
                continue;
            }
            availableLinks.add(link);
        }
        for (List linkGroup : linksByInventory.values()) {
            if (linkGroup.isEmpty()) continue;
            LogisticallyLinkedBehaviour selectedLink = (LogisticallyLinkedBehaviour)linkGroup.get(r.nextInt(linkGroup.size()));
            availableLinks.add(selectedLink);
        }
        ArrayList<LogisticallyLinkedBehaviour> usedLinks = new ArrayList<LogisticallyLinkedBehaviour>();
        MutableBoolean finalLinkTracker = new MutableBoolean(false);
        PackageOrderWithCrafts context = order;
        int orderId = r.nextInt();
        block3: for (int i = 0; i < stacks.size(); ++i) {
            BigItemStack entry = (BigItemStack)stacks.get(i);
            int remainingCount = entry.count;
            boolean finalEntry = i == stacks.size() - 1;
            ItemStack requestedItem = entry.stack;
            for (LogisticallyLinkedBehaviour link : availableLinks) {
                Pair<PackagerBlockEntity, PackagingRequest> request;
                int usedIndex = usedLinks.indexOf(link);
                int linkIndex = usedIndex == -1 ? usedLinks.size() : usedIndex;
                MutableBoolean isFinalLink = new MutableBoolean(false);
                if (linkIndex == usedLinks.size() - 1) {
                    isFinalLink = finalLinkTracker;
                }
                if ((request = link.processRequest(requestedItem, remainingCount, address, linkIndex, isFinalLink, orderId, context, ignoredHandler)) == null) continue;
                requests.put((Object)((PackagerBlockEntity)request.getFirst()), (Object)((PackagingRequest)request.getSecond()));
                int processedCount = ((PackagingRequest)request.getSecond()).getCount();
                if (processedCount > 0 && usedIndex == -1) {
                    context = null;
                    usedLinks.add(link);
                    finalLinkTracker = isFinalLink;
                }
                if ((remainingCount -= processedCount) > 0) continue;
                if (!finalEntry) continue block3;
                finalLinkTracker.setTrue();
                continue block3;
            }
        }
        return requests;
    }

    @Nullable
    private static InventoryIdentifier getInventoryIdentifierFromLink(LogisticallyLinkedBehaviour link) {
        SmartBlockEntity smartBlockEntity = link.blockEntity;
        if (!(smartBlockEntity instanceof PackagerLinkBlockEntity)) {
            return null;
        }
        PackagerLinkBlockEntity plbe = (PackagerLinkBlockEntity)smartBlockEntity;
        PackagerBlockEntity packager = plbe.getPackager();
        if (packager == null || !packager.targetInventory.hasInventory()) {
            return null;
        }
        IdentifiedInventory identifiedInventory = packager.targetInventory.getIdentifiedInventory();
        InventoryIdentifier result = identifiedInventory != null ? identifiedInventory.identifier() : null;
        return result;
    }

    public static void performPackageRequests(Multimap<PackagerBlockEntity, PackagingRequest> requests) {
        Map asMap = requests.asMap();
        for (Map.Entry entry : asMap.entrySet()) {
            ArrayList<PackagingRequest> queuedRequests = new ArrayList<PackagingRequest>((Collection)entry.getValue());
            PackagerBlockEntity packager = (PackagerBlockEntity)entry.getKey();
            if (!queuedRequests.isEmpty()) {
                packager.flashLink();
            }
            for (int i = 0; i < 100 && !queuedRequests.isEmpty(); ++i) {
                packager.attemptToSend(queuedRequests);
            }
            packager.triggerStockCheck();
            packager.notifyUpdate();
        }
    }
}
