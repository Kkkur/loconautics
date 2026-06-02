/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.packager.repackager;

import com.google.common.collect.Lists;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PackageRepackageHelper {
    protected Map<Integer, List<ItemStack>> collectedPackages = new HashMap<Integer, List<ItemStack>>();

    public void clear() {
        this.collectedPackages.clear();
    }

    public boolean isFragmented(ItemStack box) {
        return box.has(AllDataComponents.PACKAGE_ORDER_DATA);
    }

    public int addPackageFragment(ItemStack box) {
        int collectedOrderId = PackageItem.getOrderId(box);
        if (collectedOrderId == -1) {
            return -1;
        }
        List collectedOrder = this.collectedPackages.computeIfAbsent(collectedOrderId, $ -> Lists.newArrayList());
        collectedOrder.add(box);
        if (!this.isOrderComplete(collectedOrderId)) {
            return -1;
        }
        return collectedOrderId;
    }

    /*
     * Unable to fully structure code
     */
    public List<BigItemStack> repack(int orderId, RandomSource r) {
        exportingPackages = new ArrayList<BigItemStack>();
        address = "";
        orderContext = null;
        summary = new InventorySummary();
        for (ItemStack box : this.collectedPackages.get(orderId)) {
            address = PackageItem.getAddress(box);
            if (box.has(AllDataComponents.PACKAGE_ORDER_DATA) && (context = ((PackageItem.PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA)).orderContext()) != null && !context.isEmpty()) {
                orderContext = context;
            }
            contents = PackageItem.getContents(box);
            for (slot = 0; slot < contents.getSlots(); ++slot) {
                summary.add(contents.getStackInSlot(slot));
            }
        }
        orderedStacks = new ArrayList<BigItemStack>();
        if (orderContext != null) {
            packagesSplitByRecipe = this.repackBasedOnRecipes(summary, orderContext, address, r);
            exportingPackages.addAll(packagesSplitByRecipe);
            if (packagesSplitByRecipe.isEmpty()) {
                for (BigItemStack stack : orderContext.stacks()) {
                    orderedStacks.add(new BigItemStack(stack.stack, stack.count));
                }
            }
        }
        allItems = summary.getStacks();
        outputSlots = new ArrayList<ItemStack>();
        block3: while (true) {
            allItems.removeIf((Predicate<BigItemStack>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$repack$1(com.simibubi.create.content.logistics.BigItemStack ), (Lcom/simibubi/create/content/logistics/BigItemStack;)Z)());
            if (allItems.isEmpty()) break;
            targetedEntry = null;
            if (!orderedStacks.isEmpty()) {
                targetedEntry = (BigItemStack)orderedStacks.remove(0);
            }
            var11_15 = allItems.iterator();
            block4: while (true) {
                if (!var11_15.hasNext()) continue block3;
                entry = var11_15.next();
                targetAmount = entry.count;
                if (targetAmount == 0) continue;
                if (targetedEntry != null) {
                    targetAmount = targetedEntry.count;
                    if (!ItemStack.isSameItemSameComponents((ItemStack)entry.stack, (ItemStack)targetedEntry.stack)) continue;
                }
                while (true) {
                    if (targetAmount > 0) ** break;
                    continue block3;
                    removedAmount = Math.min(Math.min(targetAmount, entry.stack.getMaxStackSize()), entry.count);
                    if (removedAmount != 0) ** break;
                    continue block4;
                    output = entry.stack.copyWithCount(removedAmount);
                    targetAmount -= removedAmount;
                    if (targetedEntry != null) {
                        targetedEntry.count = targetAmount;
                    }
                    entry.count -= removedAmount;
                    outputSlots.add(output);
                }
                break;
            }
            break;
        }
        currentSlot = 0;
        target = new ItemStackHandler(9);
        for (ItemStack item : outputSlots) {
            target.setStackInSlot(currentSlot++, item);
            if (currentSlot < 9) continue;
            exportingPackages.add(new BigItemStack(PackageItem.containing(target), 1));
            target = new ItemStackHandler(9);
            currentSlot = 0;
        }
        for (slot = 0; slot < target.getSlots(); ++slot) {
            if (target.getStackInSlot(slot).isEmpty()) continue;
            exportingPackages.add(new BigItemStack(PackageItem.containing(target), 1));
            break;
        }
        for (BigItemStack box : exportingPackages) {
            PackageItem.addAddress(box.stack, address);
        }
        for (i = 0; i < exportingPackages.size(); ++i) {
            box = (BigItemStack)exportingPackages.get(i);
            isfinal = i == exportingPackages.size() - 1;
            v0 = outboundOrderContext = isfinal != false && orderContext != null ? orderContext : null;
            if (PackageItem.getOrderId(box.stack) != -1) continue;
            PackageItem.setOrder(box.stack, orderId, 0, true, 0, true, outboundOrderContext);
        }
        return exportingPackages;
    }

    private boolean isOrderComplete(int orderId) {
        boolean finalLinkReached = false;
        block0: for (int linkCounter = 0; linkCounter < 1000 && !finalLinkReached; ++linkCounter) {
            block1: for (int packageCounter = 0; packageCounter < 1000; ++packageCounter) {
                for (ItemStack box : this.collectedPackages.get(orderId)) {
                    PackageItem.PackageOrderData data = (PackageItem.PackageOrderData)box.get(AllDataComponents.PACKAGE_ORDER_DATA);
                    if (linkCounter != data.linkIndex() || packageCounter != data.fragmentIndex()) continue;
                    finalLinkReached = data.isFinalLink();
                    if (!data.isFinal()) continue block1;
                    continue block0;
                }
                return false;
            }
        }
        return true;
    }

    protected List<BigItemStack> repackBasedOnRecipes(InventorySummary summary, PackageOrderWithCrafts order, String address, RandomSource r) {
        if (order.orderedCrafts().isEmpty()) {
            return List.of();
        }
        ArrayList<BigItemStack> packages = new ArrayList<BigItemStack>();
        for (PackageOrderWithCrafts.CraftingEntry craftingEntry : order.orderedCrafts()) {
            int packagesToCreate = 0;
            block1: for (int i = 0; i < craftingEntry.count(); ++i) {
                for (BigItemStack required : craftingEntry.pattern().stacks()) {
                    if (required.stack.isEmpty()) continue;
                    if (summary.getCountOf(required.stack) <= 0) break block1;
                    summary.add(required.stack, -1);
                }
                ++packagesToCreate;
            }
            ItemStackHandler target = new ItemStackHandler(9);
            List<BigItemStack> stacks = craftingEntry.pattern().stacks();
            for (int currentSlot = 0; currentSlot < Math.min(stacks.size(), target.getSlots()); ++currentSlot) {
                target.setStackInSlot(currentSlot, stacks.get((int)currentSlot).stack.copyWithCount(1));
            }
            ItemStack box = PackageItem.containing(target);
            PackageItem.setOrder(box, r.nextInt(), 0, true, 0, true, PackageOrderWithCrafts.singleRecipe(craftingEntry.pattern().stacks()));
            packages.add(new BigItemStack(box, packagesToCreate));
        }
        return packages;
    }

    private static /* synthetic */ boolean lambda$repack$1(BigItemStack e) {
        return e.count == 0;
    }
}
