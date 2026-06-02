/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.FireworkRocketItem
 *  net.minecraft.world.item.FireworkStarItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.FireworkExplosion
 *  net.minecraft.world.item.component.Fireworks
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.item.filter.attribute.attributes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.FireworkStarItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ColorAttribute(DyeColor color) implements ItemAttribute
{
    public static final MapCodec<ColorAttribute> CODEC = DyeColor.CODEC.xmap(ColorAttribute::new, ColorAttribute::color).fieldOf("value");
    public static final StreamCodec<ByteBuf, ColorAttribute> STREAM_CODEC = DyeColor.STREAM_CODEC.map(ColorAttribute::new, ColorAttribute::color);

    private static Collection<DyeColor> findMatchingDyeColors(ItemStack stack) {
        DyeColor color = DyeColor.getColor((ItemStack)stack);
        if (color != null) {
            return Collections.singletonList(color);
        }
        HashSet<DyeColor> colors = new HashSet<DyeColor>();
        if (stack.has(DataComponents.FIREWORKS) && (stack.getItem() instanceof FireworkRocketItem || stack.getItem() instanceof FireworkStarItem)) {
            List explosions = ((Fireworks)stack.get(DataComponents.FIREWORKS)).explosions();
            for (FireworkExplosion explosion : explosions) {
                colors.addAll(ColorAttribute.getFireworkStarColors(explosion));
            }
        }
        Arrays.stream(DyeColor.values()).filter(c -> RegisteredObjectsHelper.getKeyOrThrow((Item)stack.getItem()).getPath().startsWith(c.getName() + "_")).forEach(colors::add);
        return colors;
    }

    private static Collection<DyeColor> getFireworkStarColors(FireworkExplosion explosion) {
        HashSet<DyeColor> colors = new HashSet<DyeColor>();
        Arrays.stream(explosion.colors().toIntArray()).mapToObj(DyeColor::byFireworkColor).forEach(colors::add);
        Arrays.stream(explosion.fadeColors().toIntArray()).mapToObj(DyeColor::byFireworkColor).forEach(colors::add);
        return colors;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack, Level level) {
        return ColorAttribute.findMatchingDyeColors(itemStack).stream().anyMatch(arg_0 -> this.color.equals(arg_0));
    }

    @Override
    public String getTranslationKey() {
        return "color";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{I18n.get((String)("color.minecraft." + this.color.getName()), (Object[])new Object[0])};
    }

    @Override
    public ItemAttributeType getType() {
        return AllItemAttributeTypes.HAS_COLOR;
    }

    public static class Type
    implements ItemAttributeType {
        @Override
        @NotNull
        public ItemAttribute createAttribute() {
            return new ColorAttribute(DyeColor.PURPLE);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            ArrayList<ItemAttribute> list = new ArrayList<ItemAttribute>();
            for (DyeColor color : ColorAttribute.findMatchingDyeColors(stack)) {
                list.add(new ColorAttribute(color));
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
