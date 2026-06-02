/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.simibubi.create.content.processing.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

public class ProcessingOutput {
    public static final ProcessingOutput EMPTY = new ProcessingOutput(ItemStack.EMPTY, 1.0f);
    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessingOutput> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.registry((ResourceKey)Registries.ITEM), i -> i.item, (StreamCodec)ByteBufCodecs.INT, i -> i.count, (StreamCodec)DataComponentPatch.STREAM_CODEC, i -> i.patch, (StreamCodec)ByteBufCodecs.FLOAT, i -> Float.valueOf(i.chance), ProcessingOutput::new);
    private final Item item;
    private final int count;
    private final DataComponentPatch patch;
    private final float chance;
    private ResourceLocation datagenOutput;
    @Deprecated(since="6.0.3", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    private static final Codec<Either<ItemStack, Pair<ResourceLocation, Integer>>> ITEM_CODEC_OLD = Codec.either((Codec)ItemStack.SINGLE_ITEM_CODEC, (Codec)ResourceLocation.CODEC.comapFlatMap(loc -> DataResult.error(() -> "Compat cannot be deserialized"), Pair::getFirst));
    @Deprecated(since="6.0.3", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static final Codec<ProcessingOutput> CODEC_OLD = RecordCodecBuilder.create(i -> i.group((App)ITEM_CODEC_OLD.fieldOf("item").forGetter(s -> s.datagenOutput != null ? Either.right((Object)Pair.of((Object)s.datagenOutput, (Object)s.count)) : Either.left((Object)s.item.getDefaultInstance())), (App)ExtraCodecs.intRange((int)1, (int)99).optionalFieldOf("count", (Object)1).forGetter(s -> s.count), (App)ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("chance", (Object)Float.valueOf(1.0f)).forGetter(s -> Float.valueOf(s.chance))).apply((Applicative)i, (item, count, chance) -> (ProcessingOutput)item.map(stack -> new ProcessingOutput(stack.getItem(), (int)count, stack.getComponentsPatch(), chance.floatValue()), compat -> new ProcessingOutput((ResourceLocation)compat.getFirst(), (int)((Integer)compat.getSecond()), chance.floatValue()))));
    private static final Codec<Either<Item, ResourceLocation>> ITEM_CODEC = Codec.either((Codec)BuiltInRegistries.ITEM.byNameCodec(), (Codec)ResourceLocation.CODEC);
    public static final Codec<ProcessingOutput> CODEC_NEW = RecordCodecBuilder.create(i -> i.group((App)ITEM_CODEC.fieldOf("id").forGetter(s -> {
        if (s.datagenOutput != null) {
            return Either.right((Object)s.datagenOutput);
        }
        return Either.left((Object)s.item);
    }), (App)ExtraCodecs.intRange((int)1, (int)99).optionalFieldOf("count", (Object)1).forGetter(s -> s.count), (App)DataComponentPatch.CODEC.optionalFieldOf("components", (Object)DataComponentPatch.EMPTY).forGetter(s -> s.patch), (App)ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("chance", (Object)Float.valueOf(1.0f)).forGetter(s -> Float.valueOf(s.chance))).apply((Applicative)i, (item, count, components, chance) -> (ProcessingOutput)item.map(stack -> new ProcessingOutput((Item)stack, (int)count, (DataComponentPatch)components, chance.floatValue()), compat -> new ProcessingOutput((ResourceLocation)compat, (int)count, chance.floatValue()))));
    @Deprecated(since="6.0.3", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static final Codec<ProcessingOutput> CODEC = Codec.withAlternative(CODEC_NEW, CODEC_OLD);

    public ProcessingOutput(ItemStack stack, float chance) {
        this(stack.getItem(), stack.getCount(), stack.getComponentsPatch(), chance);
    }

    public ProcessingOutput(Item item, int count, float chance) {
        this(item, count, DataComponentPatch.EMPTY, chance);
    }

    public ProcessingOutput(Item item, int count, DataComponentPatch patch, float chance) {
        this.item = item;
        this.count = count;
        this.patch = patch;
        this.chance = chance;
    }

    public ProcessingOutput(ResourceLocation item, int count, float chance) {
        this(item, count, DataComponentPatch.EMPTY, chance);
    }

    public ProcessingOutput(ResourceLocation item, int count, DataComponentPatch patch, float chance) {
        this.item = Items.AIR;
        this.datagenOutput = item;
        this.count = count;
        this.patch = patch;
        this.chance = chance;
    }

    private ItemStack getStack(int count) {
        ItemStack stack = new ItemStack((ItemLike)this.item, count);
        if (!this.patch.isEmpty()) {
            stack.applyComponents(this.patch);
        }
        return stack;
    }

    public ItemStack getStack() {
        return this.getStack(this.count);
    }

    public float getChance() {
        return this.chance;
    }

    public ItemStack rollOutput(RandomSource randomSource) {
        if (this.chance < 1.0f) {
            int count = this.count;
            for (int roll = 0; roll < this.count; ++roll) {
                if (!(randomSource.nextFloat() > this.chance)) continue;
                --count;
            }
            if (count == 0) {
                return ItemStack.EMPTY;
            }
            return this.getStack(count);
        }
        return this.getStack();
    }
}
