/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.foundation.item.LayeredArmorItem;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public static class BacktankItem.Layered
extends BacktankItem
implements LayeredArmorItem {
    public BacktankItem.Layered(Holder<ArmorMaterial> material, Item.Properties properties, ResourceLocation textureLoc, Supplier<BacktankItem.BacktankBlockItem> placeable) {
        super(material, properties, textureLoc, placeable);
    }

    @Override
    public String getArmorTextureLocation(LivingEntity entity, EquipmentSlot slot, ItemStack stack, int layer) {
        return String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d.png", this.textureLoc.getNamespace(), this.textureLoc.getPath(), layer);
    }
}
