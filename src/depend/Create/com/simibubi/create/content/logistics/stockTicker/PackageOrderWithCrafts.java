/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PackageOrderWithCrafts(PackageOrder orderedStacks, List<CraftingEntry> orderedCrafts) {
    public static final Codec<PackageOrderWithCrafts> CODEC = Codec.withAlternative((Codec)RecordCodecBuilder.create(i -> i.group((App)PackageOrder.CODEC.fieldOf("ordered_stacks").forGetter(PackageOrderWithCrafts::orderedStacks), (App)CraftingEntry.CODEC.listOf().fieldOf("ordered_crafts").forGetter(PackageOrderWithCrafts::orderedCrafts)).apply((Applicative)i, PackageOrderWithCrafts::new)), (Codec)RecordCodecBuilder.create(instance -> instance.group((App)Codec.list(BigItemStack.CODEC).fieldOf("entries").forGetter(PackageOrderWithCrafts::stacks)).apply((Applicative)instance, PackageOrderWithCrafts::simple)));
    public static final StreamCodec<RegistryFriendlyByteBuf, PackageOrderWithCrafts> STREAM_CODEC = StreamCodec.composite(PackageOrder.STREAM_CODEC, s -> s.orderedStacks, (StreamCodec)CatnipStreamCodecBuilders.list(CraftingEntry.STREAM_CODEC), s -> s.orderedCrafts, PackageOrderWithCrafts::new);

    public static PackageOrderWithCrafts empty() {
        return new PackageOrderWithCrafts(PackageOrder.empty(), List.of());
    }

    public static PackageOrderWithCrafts simple(List<BigItemStack> orderedStacks) {
        return new PackageOrderWithCrafts(new PackageOrder(orderedStacks), List.of());
    }

    public static PackageOrderWithCrafts singleRecipe(List<BigItemStack> pattern) {
        return new PackageOrderWithCrafts(PackageOrder.empty(), List.of(new CraftingEntry(new PackageOrder(pattern), 1)));
    }

    public static boolean hasCraftingInformation(PackageOrderWithCrafts context) {
        if (context == null) {
            return false;
        }
        return context.orderedCrafts.size() == 1;
    }

    public List<BigItemStack> getCraftingInformation() {
        return this.orderedCrafts.get((int)0).pattern.stacks();
    }

    public List<BigItemStack> stacks() {
        return this.orderedStacks.stacks();
    }

    public boolean isEmpty() {
        return this.orderedStacks.isEmpty();
    }

    public boolean orderedStacksMatchOrderedRecipes() {
        if (this.orderedCrafts.isEmpty()) {
            return false;
        }
        InventorySummary stacks = new InventorySummary();
        InventorySummary crafts = new InventorySummary();
        this.stacks().forEach(stacks::add);
        this.orderedCrafts.forEach(ce -> ce.pattern.stacks().forEach(bis -> crafts.add(new BigItemStack(bis.stack, bis.count * ce.count))));
        List<BigItemStack> stackEntries = stacks.getStacks();
        if (stackEntries.size() != crafts.getStacks().size()) {
            return false;
        }
        for (BigItemStack bis : stackEntries) {
            if (crafts.getCountOf(bis.stack) == bis.count) continue;
            return false;
        }
        return true;
    }

    public record CraftingEntry(PackageOrder pattern, int count) {
        public static final Codec<CraftingEntry> CODEC = RecordCodecBuilder.create(i -> i.group((App)PackageOrder.CODEC.fieldOf("pattern").forGetter(CraftingEntry::pattern), (App)Codec.INT.fieldOf("count").forGetter(CraftingEntry::count)).apply((Applicative)i, CraftingEntry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, CraftingEntry> STREAM_CODEC = StreamCodec.composite(PackageOrder.STREAM_CODEC, s -> s.pattern, (StreamCodec)ByteBufCodecs.VAR_INT, s -> s.count, CraftingEntry::new);
    }
}
