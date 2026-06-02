/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.tags.TagsProvider$TagAppender
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagBuilder
 */
package com.simibubi.create.foundation.data;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;

public static class TagGen.CreateTagAppender<T>
extends TagsProvider.TagAppender<T> {
    private final Function<T, ResourceKey<T>> keyExtractor;

    public TagGen.CreateTagAppender(TagBuilder pBuilder, Function<T, ResourceKey<T>> pKeyExtractor) {
        super(pBuilder);
        this.keyExtractor = pKeyExtractor;
    }

    public TagGen.CreateTagAppender<T> add(T entry) {
        this.add(this.keyExtractor.apply(entry));
        return this;
    }

    @SafeVarargs
    public final TagGen.CreateTagAppender<T> add(T ... entries) {
        Stream.of(entries).map(this.keyExtractor).forEach(arg_0 -> ((TagGen.CreateTagAppender)this).add(arg_0));
        return this;
    }
}
