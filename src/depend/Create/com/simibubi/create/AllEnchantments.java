/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.critereon.ItemPredicate$Builder
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.worldgen.BootstrapContext
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlotGroup
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.Enchantment$Builder
 *  net.minecraft.world.item.enchantment.Enchantment$Cost
 *  net.minecraft.world.item.enchantment.Enchantment$EnchantmentDefinition
 *  net.minecraft.world.item.enchantment.EnchantmentEffectComponents
 *  net.minecraft.world.item.enchantment.LevelBasedValue
 *  net.minecraft.world.item.enchantment.effects.SetValue
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.storage.loot.predicates.MatchTool
 */
package com.simibubi.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class AllEnchantments {
    public static final ResourceKey<Enchantment> POTATO_RECOVERY = AllEnchantments.key("potato_recovery");
    public static final ResourceKey<Enchantment> CAPACITY = AllEnchantments.key("capacity");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create((ResourceKey)Registries.ENCHANTMENT, (ResourceLocation)Create.asResource(name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter itemHolderGetter = context.lookup(Registries.ITEM);
        AllEnchantments.register(context, POTATO_RECOVERY, Enchantment.enchantment((Enchantment.EnchantmentDefinition)Enchantment.definition((HolderSet)HolderSet.direct((Holder[])new Holder[]{AllItems.POTATO_CANNON}), (int)10, (int)3, (Enchantment.Cost)Enchantment.dynamicCost((int)15, (int)15), (Enchantment.Cost)Enchantment.dynamicCost((int)45, (int)15), (int)1, (EquipmentSlotGroup[])new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND})).withEffect(EnchantmentEffectComponents.AMMO_USE, (Object)new SetValue((LevelBasedValue)LevelBasedValue.perLevel((float)0.0f, (float)33.333332f)), MatchTool.toolMatches((ItemPredicate.Builder)ItemPredicate.Builder.item().of(new ItemLike[0]))));
        AllEnchantments.register(context, CAPACITY, Enchantment.enchantment((Enchantment.EnchantmentDefinition)Enchantment.definition((HolderSet)itemHolderGetter.getOrThrow(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag), (int)10, (int)3, (Enchantment.Cost)Enchantment.dynamicCost((int)15, (int)15), (Enchantment.Cost)Enchantment.dynamicCost((int)45, (int)15), (int)1, (EquipmentSlotGroup[])new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND})));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, (Object)builder.build(key.location()));
    }
}
