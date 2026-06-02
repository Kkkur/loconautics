/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagBuilder
 *  net.minecraft.tags.TagKey
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public static class TagGen.CreateTagsProvider<T> {
    private final RegistrateTagsProvider<T> provider;
    private final Function<T, ResourceKey<T>> keyExtractor;

    public TagGen.CreateTagsProvider(RegistrateTagsProvider<T> provider, Function<T, Holder.Reference<T>> refExtractor) {
        this.provider = provider;
        this.keyExtractor = refExtractor.andThen(Holder.Reference::key);
    }

    public TagGen.CreateTagAppender<T> tag(TagKey<T> tag) {
        TagBuilder tagbuilder = this.getOrCreateRawBuilder(tag);
        return new TagGen.CreateTagAppender<T>(tagbuilder, this.keyExtractor);
    }

    public TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
        return this.provider.addTag(tag).getInternalBuilder();
    }
}
