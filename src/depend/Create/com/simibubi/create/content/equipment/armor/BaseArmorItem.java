/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.ArmorMaterial$Layer
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.armor;

import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BaseArmorItem
extends ArmorItem {
    protected final ResourceLocation textureLoc;

    public BaseArmorItem(Holder<ArmorMaterial> armorMaterial, ArmorItem.Type type, Item.Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, type, properties.stacksTo(1));
        this.textureLoc = textureLoc;
    }

    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.parse((String)String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d.png", this.textureLoc.getNamespace(), this.textureLoc.getPath(), slot == EquipmentSlot.LEGS ? 2 : 1));
    }
}
