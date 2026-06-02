/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.ArmorMaterial$Layer
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class AllArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create((ResourceKey)Registries.ARMOR_MATERIAL, (String)"create");
    public static final Holder<ArmorMaterial> COPPER = AllArmorMaterials.register("copper", new int[]{2, 4, 3, 1, 4}, 7, AllSoundEvents.COPPER_ARMOR_EQUIP.getMainEventHolder(), 0.0f, 0.0f, () -> Ingredient.of((ItemLike[])new ItemLike[]{Items.COPPER_INGOT}));
    public static final Holder<ArmorMaterial> CARDBOARD = AllArmorMaterials.register("cardboard", new int[]{1, 1, 1, 1, 2}, 4, (Holder<SoundEvent>)SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of((ItemLike[])new ItemLike[]{AllItems.CARDBOARD}));

    private static Holder<ArmorMaterial> register(String name, int[] defense, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(Create.asResource(name)));
        return AllArmorMaterials.register(name, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairIngredient, list);
    }

    private static Holder<ArmorMaterial> register(String name, int[] defense, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngridient, List<ArmorMaterial.Layer> layers) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class);
        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, defense[armoritem$type.ordinal()]);
        }
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial((Map)enummap, enchantmentValue, equipSound, repairIngridient, layers, toughness, knockbackResistance));
    }

    @ApiStatus.Internal
    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
