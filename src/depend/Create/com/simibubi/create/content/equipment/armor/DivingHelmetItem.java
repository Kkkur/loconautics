/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$RegistryLookup
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 *  net.minecraft.world.item.enchantment.ItemEnchantments$Mutable
 *  net.minecraft.world.level.Level
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.living.LivingBreatheEvent
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

@EventBusSubscriber
public class DivingHelmetItem
extends BaseArmorItem {
    public static final EquipmentSlot SLOT = EquipmentSlot.HEAD;
    public static final ArmorItem.Type TYPE = ArmorItem.Type.HELMET;

    public DivingHelmetItem(Holder<ArmorMaterial> material, Item.Properties properties, ResourceLocation textureLoc) {
        super(material, TYPE, properties, textureLoc);
    }

    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.AQUA_AFFINITY)) {
            return false;
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.AQUA_AFFINITY)) {
            return 1;
        }
        return super.getEnchantmentLevel(stack, enchantment);
    }

    public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(super.getAllEnchantments(stack, lookup));
        enchants.set((Holder)lookup.getOrThrow(Enchantments.AQUA_AFFINITY), 1);
        return enchants.toImmutable();
    }

    public static boolean isWornBy(Entity entity) {
        return !DivingHelmetItem.getWornItem(entity).isEmpty();
    }

    public static ItemStack getWornItem(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return ItemStack.EMPTY;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        ItemStack stack = livingEntity.getItemBySlot(SLOT);
        if (!(stack.getItem() instanceof DivingHelmetItem)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @SubscribeEvent
    public static void breatheUnderwater(LivingBreatheEvent event) {
        ItemStack helmet;
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide) {
            entity.getPersistentData().remove("VisualBacktankAir");
        }
        if ((helmet = DivingHelmetItem.getWornItem((Entity)entity)).isEmpty()) {
            return;
        }
        boolean lavaDiving = entity.isInLava();
        if (!helmet.has(DataComponents.FIRE_RESISTANT) && lavaDiving) {
            return;
        }
        if (event.canBreathe() && !lavaDiving) {
            return;
        }
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(entity);
        if (backtanks.isEmpty()) {
            return;
        }
        if (lavaDiving) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)entity;
                AllAdvancements.DIVING_SUIT_LAVA.awardTo((Player)sp);
            }
            if (backtanks.stream().noneMatch(backtank -> backtank.has(DataComponents.FIRE_RESISTANT))) {
                return;
            }
        }
        float visualBacktankAir = 0.0f;
        for (ItemStack stack : backtanks) {
            visualBacktankAir += (float)BacktankUtil.getAir(stack);
        }
        if (level.isClientSide) {
            entity.getPersistentData().putInt("VisualBacktankAir", Math.round(visualBacktankAir));
        }
        if (level.getGameTime() % 20L == 0L) {
            BacktankUtil.consumeAir(entity, backtanks.get(0), 1);
        }
        if (lavaDiving) {
            return;
        }
        if (entity instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)entity;
            AllAdvancements.DIVING_SUIT.awardTo((Player)sp);
        }
        event.setCanBreathe(true);
        event.setRefillAirAmount(entity.getMaxAirSupply());
    }
}
