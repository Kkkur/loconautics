/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.EquipmentSlot$Type
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber
public final class NetheriteDivingHandler {
    public static final String NETHERITE_DIVING_BITS_KEY = "CreateNetheriteDivingBits";
    public static final String FIRE_IMMUNE_KEY = "CreateFireImmune";

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        EquipmentSlot slot = event.getSlot();
        if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
            return;
        }
        LivingEntity entity = event.getEntity();
        ItemStack to = event.getTo();
        if (slot == EquipmentSlot.HEAD) {
            if (NetheriteDivingHandler.isNetheriteDivingHelmet(to)) {
                NetheriteDivingHandler.setBit(entity, slot);
            } else {
                NetheriteDivingHandler.clearBit(entity, slot);
            }
        } else if (slot == EquipmentSlot.CHEST) {
            if (NetheriteDivingHandler.isNetheriteBacktank(to) && BacktankUtil.hasAirRemaining(to)) {
                NetheriteDivingHandler.setBit(entity, slot);
            } else {
                NetheriteDivingHandler.clearBit(entity, slot);
            }
        } else if (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) {
            if (NetheriteDivingHandler.isNetheriteArmor(to)) {
                NetheriteDivingHandler.setBit(entity, slot);
            } else {
                NetheriteDivingHandler.clearBit(entity, slot);
            }
        }
    }

    public static boolean isNetheriteDivingHelmet(ItemStack stack) {
        return stack.getItem() instanceof DivingHelmetItem && NetheriteDivingHandler.isNetheriteArmor(stack);
    }

    public static boolean isNetheriteBacktank(ItemStack stack) {
        return stack.is(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag) && NetheriteDivingHandler.isNetheriteArmor(stack);
    }

    public static boolean isNetheriteArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && stack.has(DataComponents.FIRE_RESISTANT);
    }

    public static void setBit(LivingEntity entity, EquipmentSlot slot) {
        CompoundTag nbt = entity.getPersistentData();
        byte bits = nbt.getByte(NETHERITE_DIVING_BITS_KEY);
        if ((bits & 0xF) == 15) {
            return;
        }
        bits = (byte)(bits | 1 << slot.getIndex());
        nbt.putByte(NETHERITE_DIVING_BITS_KEY, bits);
        if ((bits & 0xF) == 15) {
            NetheriteDivingHandler.setFireImmune(entity, true);
        }
    }

    public static void clearBit(LivingEntity entity, EquipmentSlot slot) {
        CompoundTag nbt = entity.getPersistentData();
        if (!nbt.contains(NETHERITE_DIVING_BITS_KEY)) {
            return;
        }
        byte bits = nbt.getByte(NETHERITE_DIVING_BITS_KEY);
        boolean prevFullSet = (bits & 0xF) == 15;
        bits = (byte)(bits & ~(1 << slot.getIndex()));
        nbt.putByte(NETHERITE_DIVING_BITS_KEY, bits);
        if (prevFullSet) {
            NetheriteDivingHandler.setFireImmune(entity, false);
        }
    }

    public static void setFireImmune(LivingEntity entity, boolean fireImmune) {
        entity.getPersistentData().putBoolean(FIRE_IMMUNE_KEY, fireImmune);
    }
}
