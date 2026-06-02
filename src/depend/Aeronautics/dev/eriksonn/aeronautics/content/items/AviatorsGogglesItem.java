/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.armor.BaseArmorItem
 *  com.simibubi.create.content.equipment.goggles.GogglesItem
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.Item$Properties
 */
package dev.eriksonn.aeronautics.content.items;

import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.index.AeroArmorMaterials;
import dev.eriksonn.aeronautics.index.AeroItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class AviatorsGogglesItem
extends BaseArmorItem {
    public static final ArmorItem.Type TYPE = ArmorItem.Type.HELMET;
    private static final ResourceLocation TEXTURE = Aeronautics.path("aviators_goggles");

    public AviatorsGogglesItem(Item.Properties properties) {
        super(AeroArmorMaterials.AVIATORS_GOGGLES.asHolder(), TYPE, properties, TEXTURE);
        GogglesItem.addIsWearingPredicate(player -> AeroItems.AVIATORS_GOGGLES.isIn(player.getItemBySlot(EquipmentSlot.HEAD)));
    }
}
