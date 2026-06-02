/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.packagerLink.RequestPromise;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RequestPromiseQueue {
    private Map<Item, List<RequestPromise>> promisesByItem = new IdentityHashMap<Item, List<RequestPromise>>();
    private Runnable onChanged;

    public RequestPromiseQueue(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public void add(RequestPromise promise) {
        this.promisesByItem.computeIfAbsent(promise.promisedStack.stack.getItem(), $ -> new LinkedList()).add(promise);
        this.onChanged.run();
    }

    public void setOnChanged(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public int getTotalPromisedAndRemoveExpired(ItemStack stack, int expiryTime) {
        int promised = 0;
        List<RequestPromise> list = this.promisesByItem.get(stack.getItem());
        if (list == null) {
            return promised;
        }
        Iterator<RequestPromise> iterator = list.iterator();
        while (iterator.hasNext()) {
            RequestPromise promise = iterator.next();
            if (!ItemStack.isSameItemSameComponents((ItemStack)promise.promisedStack.stack, (ItemStack)stack)) continue;
            if (expiryTime != -1 && promise.ticksExisted >= expiryTime) {
                iterator.remove();
                this.onChanged.run();
                continue;
            }
            promised += promise.promisedStack.count;
        }
        return promised;
    }

    public void forceClear(ItemStack stack) {
        List<RequestPromise> list = this.promisesByItem.get(stack.getItem());
        if (list == null) {
            return;
        }
        Iterator<RequestPromise> iterator = list.iterator();
        while (iterator.hasNext()) {
            RequestPromise promise = iterator.next();
            if (!ItemStack.isSameItemSameComponents((ItemStack)promise.promisedStack.stack, (ItemStack)stack)) continue;
            iterator.remove();
            this.onChanged.run();
        }
        if (list.isEmpty()) {
            this.promisesByItem.remove(stack.getItem());
        }
    }

    public void itemEnteredSystem(ItemStack stack, int amount) {
        List<RequestPromise> list = this.promisesByItem.get(stack.getItem());
        if (list == null) {
            return;
        }
        Iterator<RequestPromise> iterator = list.iterator();
        while (iterator.hasNext()) {
            RequestPromise requestPromise = iterator.next();
            if (!ItemStack.isSameItemSameComponents((ItemStack)requestPromise.promisedStack.stack, (ItemStack)stack)) continue;
            int toSubtract = Math.min(amount, requestPromise.promisedStack.count);
            amount -= toSubtract;
            requestPromise.promisedStack.count -= toSubtract;
            if (requestPromise.promisedStack.count <= 0) {
                iterator.remove();
                this.onChanged.run();
            }
            if (amount > 0) continue;
            break;
        }
        if (list.isEmpty()) {
            this.promisesByItem.remove(stack.getItem());
        }
    }

    public List<RequestPromise> flatten(boolean sorted) {
        ArrayList<RequestPromise> all = new ArrayList<RequestPromise>();
        this.promisesByItem.forEach((key, list) -> all.addAll((Collection<RequestPromise>)list));
        if (sorted) {
            all.sort(RequestPromise.ageComparator());
        }
        return all;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("List", (Tag)CatnipCodecUtils.encode((Codec)Codec.list(RequestPromise.CODEC), (HolderLookup.Provider)registries, this.flatten(false)).orElseThrow());
        return tag;
    }

    public static RequestPromiseQueue read(CompoundTag tag, HolderLookup.Provider registries, Runnable onChanged) {
        RequestPromiseQueue queue = new RequestPromiseQueue(onChanged);
        List promises = CatnipCodecUtils.decode((Codec)Codec.list(RequestPromise.CODEC), (HolderLookup.Provider)registries, (Tag)tag.get("List")).orElse(List.of());
        for (RequestPromise promise : promises) {
            queue.add(promise);
        }
        return queue;
    }

    public void tick() {
        this.promisesByItem.forEach((key, list) -> list.forEach(RequestPromise::tick));
    }

    public boolean isEmpty() {
        return this.promisesByItem.isEmpty();
    }
}
