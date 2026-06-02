/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.block.Block
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.foundation.item.LayeredArmorItem;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BacktankItem
extends BaseArmorItem {
    public static final EquipmentSlot SLOT = EquipmentSlot.CHEST;
    public static final ArmorItem.Type TYPE = ArmorItem.Type.CHESTPLATE;
    public static final int BAR_COLOR = 0xEFEFEF;
    private final Supplier<BacktankBlockItem> blockItem;

    public BacktankItem(Holder<ArmorMaterial> material, Item.Properties properties, ResourceLocation textureLoc, Supplier<BacktankBlockItem> placeable) {
        super(material, TYPE, properties, textureLoc);
        this.blockItem = placeable;
    }

    @Nullable
    public static BacktankItem getWornBy(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return null;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        Item item = livingEntity.getItemBySlot(SLOT).getItem();
        if (!(item instanceof BacktankItem)) {
            return null;
        }
        BacktankItem item2 = (BacktankItem)item;
        return item2;
    }

    public InteractionResult useOn(UseOnContext ctx) {
        return this.blockItem.get().useOn(ctx);
    }

    public boolean isEnchantable(ItemStack p_77616_1_) {
        return true;
    }

    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.MENDING) || enchantment.is(Enchantments.UNBREAKING)) {
            return false;
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f * Mth.clamp((float)((float)BacktankItem.getRemainingAir(stack) / (float)BacktankUtil.maxAir(stack)), (float)0.0f, (float)1.0f));
    }

    public int getBarColor(ItemStack stack) {
        return 0xEFEFEF;
    }

    public Block getBlock() {
        return this.blockItem.get().getBlock();
    }

    public static int getRemainingAir(ItemStack stack) {
        return (Integer)stack.getOrDefault(AllDataComponents.BACKTANK_AIR, (Object)0);
    }

    public static class BacktankBlockItem
    extends BlockItem {
        private final Supplier<Item> actualItem;

        public BacktankBlockItem(Block block, Supplier<Item> actualItem, Item.Properties properties) {
            super(block, properties);
            this.actualItem = actualItem;
        }

        public String getDescriptionId() {
            return this.getOrCreateDescriptionId();
        }

        public Item getActualItem() {
            return this.actualItem.get();
        }
    }

    public static class Layered
    extends BacktankItem
    implements LayeredArmorItem {
        public Layered(Holder<ArmorMaterial> material, Item.Properties properties, ResourceLocation textureLoc, Supplier<BacktankBlockItem> placeable) {
            super(material, properties, textureLoc, placeable);
        }

        @Override
        public String getArmorTextureLocation(LivingEntity entity, EquipmentSlot slot, ItemStack stack, int layer) {
            return String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d.png", this.textureLoc.getNamespace(), this.textureLoc.getPath(), layer);
        }
    }
}
