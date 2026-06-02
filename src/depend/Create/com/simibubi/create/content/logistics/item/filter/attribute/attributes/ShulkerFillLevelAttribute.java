/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ShulkerBoxBlock
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.foundation.utility.CreateLang;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ShulkerFillLevelAttribute(ShulkerLevels levels) implements ItemAttribute
{
    public static final MapCodec<ShulkerFillLevelAttribute> CODEC = ShulkerLevels.CODEC.xmap(ShulkerFillLevelAttribute::new, ShulkerFillLevelAttribute::levels).fieldOf("value");
    public static final StreamCodec<ByteBuf, ShulkerFillLevelAttribute> STREAM_CODEC = ShulkerLevels.STREAM_CODEC.map(ShulkerFillLevelAttribute::new, ShulkerFillLevelAttribute::levels);

    @Override
    public boolean appliesTo(ItemStack stack, Level level) {
        return this.levels != null && this.levels.canApply(stack);
    }

    @Override
    public String getTranslationKey() {
        return "shulker_level";
    }

    @Override
    public Object[] getTranslationParameters() {
        String parameter = "";
        if (this.levels != null) {
            parameter = CreateLang.translateDirect("item_attributes." + this.getTranslationKey() + "." + this.levels.key, new Object[0]).getString();
        }
        return new Object[]{parameter};
    }

    @Override
    public ItemAttributeType getType() {
        return AllItemAttributeTypes.SHULKER_FILL_LEVEL;
    }

    static enum ShulkerLevels implements StringRepresentable
    {
        EMPTY("empty", amount -> amount == 0),
        PARTIAL("partial", amount -> amount > 0 && amount < Integer.MAX_VALUE),
        FULL("full", amount -> amount == Integer.MAX_VALUE);

        public static final Codec<ShulkerLevels> CODEC;
        public static final StreamCodec<ByteBuf, ShulkerLevels> STREAM_CODEC;
        private final Predicate<Integer> requiredSize;
        private final String key;

        private ShulkerLevels(String key, Predicate<Integer> requiredSize) {
            this.key = key;
            this.requiredSize = requiredSize;
        }

        @Nullable
        public static ShulkerLevels fromKey(String key) {
            return Arrays.stream(ShulkerLevels.values()).filter(shulkerLevels -> shulkerLevels.key.equals(key)).findFirst().orElse(null);
        }

        private static boolean isShulker(ItemStack stack) {
            return Block.byItem((Item)stack.getItem()) instanceof ShulkerBoxBlock;
        }

        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }

        public boolean canApply(ItemStack testStack) {
            if (!ShulkerLevels.isShulker(testStack)) {
                return false;
            }
            ItemContainerContents contents = (ItemContainerContents)testStack.getOrDefault(DataComponents.CONTAINER, (Object)ItemContainerContents.EMPTY);
            if (contents == ItemContainerContents.EMPTY) {
                return this.requiredSize.test(0);
            }
            if (testStack.has(DataComponents.CONTAINER_LOOT)) {
                return false;
            }
            if (contents.getSlots() > 0) {
                int rawSize = contents.getSlots();
                if (rawSize < 27) {
                    return this.requiredSize.test(rawSize);
                }
                NonNullList inventory = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
                contents.copyInto(inventory);
                boolean isFull = inventory.stream().allMatch(itemStack -> !itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxStackSize());
                return this.requiredSize.test(isFull ? Integer.MAX_VALUE : rawSize);
            }
            return this.requiredSize.test(0);
        }

        static {
            CODEC = StringRepresentable.fromValues(ShulkerLevels::values);
            STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(ShulkerLevels.class);
        }
    }

    public static class Type
    implements ItemAttributeType {
        @Override
        @NotNull
        public ItemAttribute createAttribute() {
            return new ShulkerFillLevelAttribute(null);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            ArrayList<ItemAttribute> list = new ArrayList<ItemAttribute>();
            for (ShulkerLevels shulkerLevels : ShulkerLevels.values()) {
                if (!shulkerLevels.canApply(stack)) continue;
                list.add(new ShulkerFillLevelAttribute(shulkerLevels));
            }
            return list;
        }

        @Override
        public MapCodec<? extends ItemAttribute> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
