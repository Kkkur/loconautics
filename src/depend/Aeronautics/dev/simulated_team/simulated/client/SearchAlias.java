/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.util.ExtraCodecs$TagOrElementLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.client;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.simulated_team.simulated.index.SimResourceManagers;
import dev.simulated_team.simulated.util.SimCodecUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record SearchAlias(List<String> terms, List<ExtraCodecs.TagOrElementLocation> results) {
    public static final Codec<List<String>> TERM_CODEC = SimCodecUtil.withAlternative(Codec.STRING.xmap(List::of, List::getFirst), Codec.STRING.listOf());
    public static final Codec<SearchAlias> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)TERM_CODEC.fieldOf("term").forGetter(SearchAlias::terms), (App)ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("results").forGetter(SearchAlias::results)).apply((Applicative)instance, SearchAlias::new));

    public static List<String> getAliases(ItemStack stack) {
        ArrayList<String> aliases = new ArrayList<String>();
        for (SearchAlias entry : SimResourceManagers.SEARCH_ALIAS.entries()) {
            if (!entry.match(stack)) continue;
            aliases.addAll(entry.terms());
        }
        return aliases;
    }

    public boolean match(ItemStack stack) {
        for (ExtraCodecs.TagOrElementLocation result : this.results()) {
            ResourceLocation key;
            TagKey tag;
            if (!(result.tag() ? stack.is(tag = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)result.id())) : (key = BuiltInRegistries.ITEM.getKey((Object)stack.getItem())).equals((Object)result.id()))) continue;
            return true;
        }
        return false;
    }

    public List<ItemStack> getItems() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (ExtraCodecs.TagOrElementLocation result : this.results()) {
            if (result.tag()) {
                TagKey tag = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)result.id());
                BuiltInRegistries.ITEM.getTag(tag).ifPresent(set -> {
                    for (Holder holder : set) {
                        if (!holder.isBound()) continue;
                        list.add(((Item)holder.value()).getDefaultInstance());
                    }
                });
                continue;
            }
            Item item = (Item)BuiltInRegistries.ITEM.get(result.id());
            list.add(item.getDefaultInstance());
        }
        return list;
    }
}
