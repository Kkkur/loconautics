/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.kinetics.deployer;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ItemApplicationRecipeParams
extends ProcessingRecipeParams {
    public static MapCodec<ItemApplicationRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ItemApplicationRecipeParams.codec(ItemApplicationRecipeParams::new).forGetter(Function.identity()), (App)Codec.BOOL.optionalFieldOf("keep_held_item", (Object)false).forGetter(ItemApplicationRecipeParams::keepHeldItem)).apply((Applicative)instance, (params, keepHeldItem) -> {
        params.keepHeldItem = keepHeldItem;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, ItemApplicationRecipeParams> STREAM_CODEC = ItemApplicationRecipeParams.streamCodec(ItemApplicationRecipeParams::new);
    protected boolean keepHeldItem;

    protected final boolean keepHeldItem() {
        return this.keepHeldItem;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.BOOL.encode((Object)buffer, (Object)this.keepHeldItem);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        this.keepHeldItem = (Boolean)ByteBufCodecs.BOOL.decode((Object)buffer);
    }
}
