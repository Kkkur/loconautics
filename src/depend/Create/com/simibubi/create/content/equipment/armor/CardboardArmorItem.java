/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.Item$Properties
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class CardboardArmorItem
extends BaseArmorItem {
    public CardboardArmorItem(ArmorItem.Type type, Item.Properties properties) {
        super(AllArmorMaterials.CARDBOARD, type, properties, Create.asResource("cardboard"));
    }
}
