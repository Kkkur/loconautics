/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 */
package com.simibubi.create.foundation.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.codec.CreateCodecs;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class ItemSlots {
    public static final Codec<ItemSlots> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap(CreateCodecs.boundedIntStr(0), (Codec)ItemStack.CODEC).fieldOf("items").forGetter(ItemSlots::toBoxedMap), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter(ItemSlots::getSize)).apply((Applicative)instance, ItemSlots::deserialize));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSlots> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.map(HashMap::new, (StreamCodec)ByteBufCodecs.INT, (StreamCodec)ItemStack.STREAM_CODEC), ItemSlots::toBoxedMap, (StreamCodec)ByteBufCodecs.INT, ItemSlots::getSize, ItemSlots::deserialize);
    private final Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap();
    private int size = 0;

    public void set(int slot, ItemStack stack) {
        if (slot < 0) {
            throw new IllegalArgumentException("Slot must be positive");
        }
        if (!stack.isEmpty()) {
            this.map.put(slot, (Object)stack);
            this.size = Math.max(this.size, slot + 1);
        }
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        if (size <= this.getHighestSlot()) {
            throw new IllegalStateException("cannot set size to below the highest slot");
        }
        this.size = size;
    }

    public void forEach(SlotConsumer consumer) {
        for (Int2ObjectMap.Entry entry : this.map.int2ObjectEntrySet()) {
            consumer.accept(entry.getIntKey(), (ItemStack)entry.getValue());
        }
    }

    private int getHighestSlot() {
        return this.map.keySet().intStream().max().orElse(-1);
    }

    public <T extends IItemHandlerModifiable> T toHandler(IntFunction<T> factory) {
        IItemHandlerModifiable handler = (IItemHandlerModifiable)factory.apply(this.size);
        this.forEach((arg_0, arg_1) -> ((IItemHandlerModifiable)handler).setStackInSlot(arg_0, arg_1));
        return (T)handler;
    }

    public static ItemSlots fromHandler(IItemHandler handler) {
        ItemSlots slots = new ItemSlots();
        slots.setSize(handler.getSlots());
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            slots.set(i, stack.copy());
        }
        return slots;
    }

    public Map<Integer, ItemStack> toBoxedMap() {
        HashMap<Integer, ItemStack> map = new HashMap<Integer, ItemStack>();
        this.forEach(map::put);
        return map;
    }

    public static ItemSlots fromBoxedMap(Map<Integer, ItemStack> map) {
        ItemSlots slots = new ItemSlots();
        map.forEach(slots::set);
        return slots;
    }

    public static Codec<ItemSlots> maxSizeCodec(int maxSize) {
        return CODEC.validate(slots -> slots.size <= maxSize ? DataResult.success((Object)slots) : DataResult.error(() -> "Slots above maximum of " + maxSize));
    }

    private static ItemSlots deserialize(Map<Integer, ItemStack> map, int size) {
        ItemSlots slots = ItemSlots.fromBoxedMap(map);
        slots.setSize(size);
        return slots;
    }

    @FunctionalInterface
    public static interface SlotConsumer {
        public void accept(int var1, ItemStack var2);
    }
}
