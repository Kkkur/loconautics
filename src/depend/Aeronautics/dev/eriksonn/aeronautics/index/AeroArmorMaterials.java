/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.platform.registry.RegistrationProvider
 *  foundry.veil.platform.registry.RegistryObject
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.ArmorMaterial$Layer
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 */
package dev.eriksonn.aeronautics.index;

import dev.eriksonn.aeronautics.Aeronautics;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class AeroArmorMaterials {
    private static final RegistrationProvider<ArmorMaterial> REGISTRY = RegistrationProvider.get((ResourceKey)Registries.ARMOR_MATERIAL, (String)"aeronautics");
    public static final RegistryObject<ArmorMaterial> AVIATORS_GOGGLES = REGISTRY.register("aviators_goggles", () -> new ArmorMaterial((Map)new Object2ObjectOpenHashMap<ArmorItem.Type, Integer>(){
        {
            this.put(ArmorItem.Type.HELMET, 1);
        }
    }, 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of((ItemLike[])new ItemLike[]{Items.LEATHER}), List.of(new ArmorMaterial.Layer(Aeronautics.path("aviators_goggles"))), 0.0f, 0.0f));

    public static void init() {
    }
}
