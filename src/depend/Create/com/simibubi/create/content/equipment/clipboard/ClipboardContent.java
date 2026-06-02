/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.equipment.clipboard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ClipboardContent(ClipboardOverrides.ClipboardType type, List<List<ClipboardEntry>> pages, boolean readOnly, int previouslyOpenedPage, Optional<CompoundTag> copiedValues) {
    public static final ClipboardContent EMPTY = new ClipboardContent(ClipboardOverrides.ClipboardType.EMPTY, List.of(), false, 0, Optional.empty());
    public static final Codec<List<List<ClipboardEntry>>> PAGES_CODEC = ClipboardEntry.CODEC.listOf().listOf();
    public static final StreamCodec<RegistryFriendlyByteBuf, List<List<ClipboardEntry>>> PAGES_STREAM_CODEC = CatnipStreamCodecBuilders.list((StreamCodec)CatnipStreamCodecBuilders.list(ClipboardEntry.STREAM_CODEC));
    public static final Codec<ClipboardContent> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ClipboardOverrides.ClipboardType.CODEC.fieldOf("type").forGetter(ClipboardContent::type), (App)PAGES_CODEC.fieldOf("pages").forGetter(ClipboardContent::pages), (App)Codec.BOOL.fieldOf("read_only").forGetter(ClipboardContent::readOnly), (App)Codec.INT.fieldOf("previously_opened_page").forGetter(ClipboardContent::previouslyOpenedPage), (App)CompoundTag.CODEC.optionalFieldOf("copied_values").forGetter(ClipboardContent::copiedValues)).apply((Applicative)instance, ClipboardContent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClipboardContent> STREAM_CODEC = StreamCodec.composite(ClipboardOverrides.ClipboardType.STREAM_CODEC, ClipboardContent::type, PAGES_STREAM_CODEC, ClipboardContent::pages, (StreamCodec)ByteBufCodecs.BOOL, ClipboardContent::readOnly, (StreamCodec)ByteBufCodecs.VAR_INT, ClipboardContent::previouslyOpenedPage, (StreamCodec)ByteBufCodecs.optional((StreamCodec)ByteBufCodecs.COMPOUND_TAG), ClipboardContent::copiedValues, ClipboardContent::new);

    public ClipboardContent(ClipboardOverrides.ClipboardType type, List<List<ClipboardEntry>> pages, boolean readOnly) {
        this(type, pages, readOnly, 0, Optional.empty());
    }

    public ClipboardContent setType(ClipboardOverrides.ClipboardType type) {
        return new ClipboardContent(type, this.pages, this.readOnly, this.previouslyOpenedPage, this.copiedValues);
    }

    public ClipboardContent setPages(List<List<ClipboardEntry>> pages) {
        return new ClipboardContent(this.type, pages, this.readOnly, this.previouslyOpenedPage, this.copiedValues);
    }

    public ClipboardContent setReadOnly(boolean readOnly) {
        return new ClipboardContent(this.type, this.pages, readOnly, this.previouslyOpenedPage, this.copiedValues);
    }

    public ClipboardContent setPreviouslyOpenedPage(int previouslyOpenedPage) {
        return new ClipboardContent(this.type, this.pages, this.readOnly, previouslyOpenedPage, this.copiedValues);
    }

    public ClipboardContent setCopiedValues(CompoundTag copiedValues) {
        return new ClipboardContent(this.type, this.pages, this.readOnly, this.previouslyOpenedPage, Optional.of(copiedValues));
    }
}
