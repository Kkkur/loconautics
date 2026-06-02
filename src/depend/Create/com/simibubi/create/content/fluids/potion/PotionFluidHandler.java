/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffectUtil
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.PotionItem
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.item.component.ItemAttributeModifiers
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.content.fluids.potion;

import com.google.common.collect.Lists;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.foundation.fluid.FluidHelper;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class PotionFluidHandler {
    private static final Component NO_EFFECT = Component.translatable((String)"effect.none").withStyle(ChatFormatting.GRAY);

    public static boolean isPotionItem(ItemStack stack) {
        return stack.getItem() instanceof PotionItem && !(stack.getCraftingRemainingItem().getItem() instanceof BucketItem) && !AllTags.AllItemTags.NOT_POTION.matches(stack);
    }

    public static Pair<FluidStack, ItemStack> emptyPotion(ItemStack stack, boolean simulate) {
        FluidStack fluid = PotionFluidHandler.getFluidFromPotionItem(stack);
        if (!simulate) {
            stack.shrink(1);
        }
        return Pair.of((Object)fluid, (Object)new ItemStack((ItemLike)Items.GLASS_BOTTLE));
    }

    public static SizedFluidIngredient potionIngredient(Holder<Potion> potion, int amount) {
        FluidStack stack = FluidHelper.copyStackWithAmount(PotionFluidHandler.getFluidFromPotionItem(PotionContents.createItemStack((Item)Items.POTION, potion)), amount);
        return new SizedFluidIngredient(DataComponentFluidIngredient.of((boolean)false, (FluidStack)stack), amount);
    }

    public static FluidStack getFluidFromPotionItem(ItemStack stack) {
        PotionContents potion = (PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY);
        PotionFluid.BottleType bottleTypeFromItem = PotionFluidHandler.bottleTypeFromItem(stack.getItem());
        if (potion.is(Potions.WATER) && potion.customEffects().isEmpty() && bottleTypeFromItem == PotionFluid.BottleType.REGULAR) {
            return new FluidStack((Fluid)Fluids.WATER, 250);
        }
        FluidStack fluid = PotionFluidHandler.getFluidFromPotion(potion, bottleTypeFromItem, 250);
        fluid.set(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, (Object)bottleTypeFromItem);
        return fluid;
    }

    public static FluidStack getFluidFromPotion(PotionContents potionContents, PotionFluid.BottleType bottleType, int amount) {
        if (potionContents.is(Potions.WATER) && bottleType == PotionFluid.BottleType.REGULAR) {
            return new FluidStack((Fluid)Fluids.WATER, amount);
        }
        return PotionFluid.of(amount, potionContents, bottleType);
    }

    public static PotionFluid.BottleType bottleTypeFromItem(Item item) {
        if (item == Items.LINGERING_POTION) {
            return PotionFluid.BottleType.LINGERING;
        }
        if (item == Items.SPLASH_POTION) {
            return PotionFluid.BottleType.SPLASH;
        }
        return PotionFluid.BottleType.REGULAR;
    }

    public static ItemLike itemFromBottleType(PotionFluid.BottleType type) {
        return switch (type) {
            case PotionFluid.BottleType.LINGERING -> Items.LINGERING_POTION;
            case PotionFluid.BottleType.SPLASH -> Items.SPLASH_POTION;
            default -> Items.POTION;
        };
    }

    public static int getRequiredAmountForFilledBottle(ItemStack stack, FluidStack availableFluid) {
        return 250;
    }

    public static ItemStack fillBottle(ItemStack stack, FluidStack availableFluid) {
        ItemStack potionStack = new ItemStack(PotionFluidHandler.itemFromBottleType((PotionFluid.BottleType)((Object)availableFluid.getOrDefault(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, (Object)PotionFluid.BottleType.REGULAR))));
        potionStack.set(DataComponents.POTION_CONTENTS, (Object)((PotionContents)availableFluid.get(DataComponents.POTION_CONTENTS)));
        return potionStack;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void addPotionTooltip(FluidStack fs, Consumer<Component> tooltipAdder, float durationFactor) {
        PotionContents contents = (PotionContents)fs.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY);
        Iterable effects = contents.getAllEffects();
        ArrayList list = Lists.newArrayList();
        boolean flag = true;
        for (MobEffectInstance mobeffectinstance : effects) {
            flag = false;
            MutableComponent mutablecomponent = Component.translatable((String)mobeffectinstance.getDescriptionId());
            Holder holder = mobeffectinstance.getEffect();
            ((MobEffect)holder.value()).createModifiers(mobeffectinstance.getAmplifier(), (h, m) -> list.add(Pair.of((Object)h, (Object)m)));
            if (mobeffectinstance.getAmplifier() > 0) {
                mutablecomponent.append(" ").append(Component.translatable((String)("potion.potency." + mobeffectinstance.getAmplifier())).getString());
            }
            if (!mobeffectinstance.endsWithin(20)) {
                mutablecomponent.append(" (").append(MobEffectUtil.formatDuration((MobEffectInstance)mobeffectinstance, (float)durationFactor, (float)Minecraft.getInstance().level.tickRateManager().tickrate())).append(")");
            }
            tooltipAdder.accept((Component)mutablecomponent.withStyle(((MobEffect)holder.value()).getCategory().getTooltipFormatting()));
        }
        if (flag) {
            tooltipAdder.accept(NO_EFFECT);
        }
        if (!list.isEmpty()) {
            tooltipAdder.accept(CommonComponents.EMPTY);
            tooltipAdder.accept((Component)Component.translatable((String)"potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            for (Pair pair : list) {
                AttributeModifier attributemodifier = (AttributeModifier)pair.getSecond();
                double d1 = attributemodifier.amount();
                double d0 = attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL ? attributemodifier.amount() : attributemodifier.amount() * 100.0;
                if (d1 > 0.0) {
                    tooltipAdder.accept((Component)Component.translatable((String)("attribute.modifier.plus." + attributemodifier.operation().id()), (Object[])new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable((String)((Attribute)((Holder)pair.getFirst()).value()).getDescriptionId())}).withStyle(ChatFormatting.BLUE));
                    continue;
                }
                if (!(d1 < 0.0)) continue;
                tooltipAdder.accept((Component)Component.translatable((String)("attribute.modifier.take." + attributemodifier.operation().id()), (Object[])new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d0 *= -1.0), Component.translatable((String)((Attribute)((Holder)pair.getFirst()).value()).getDescriptionId())}).withStyle(ChatFormatting.RED));
            }
        }
    }
}
