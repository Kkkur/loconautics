/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.EntityTypeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.SwordItem
 *  net.minecraft.world.item.Tier
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.LogicalSide
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.event.entity.player.AttackEntityEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock$Action
 */
package com.simibubi.create.content.equipment.tool;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.tool.AllToolMaterials;
import com.simibubi.create.content.equipment.tool.CardboardSwordItemRenderer;
import com.simibubi.create.content.equipment.tool.KnockbackPacket;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import java.util.function.Consumer;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class CardboardSwordItem
extends SwordItem {
    public CardboardSwordItem(Item.Properties pProperties) {
        super((Tier)AllToolMaterials.CARDBOARD, pProperties);
    }

    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.getKey() == Enchantments.KNOCKBACK;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        ItemEnchantments enchants = (ItemEnchantments)book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, (Object)ItemEnchantments.EMPTY);
        for (Holder enchantment : enchants.keySet()) {
            if (enchantment.getKey() == Enchantments.KNOCKBACK) continue;
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void cardboardSwordsMakeNoiseOnClick(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        if (!AllItems.CARDBOARD_SWORD.isIn(itemStack)) {
            return;
        }
        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
            return;
        }
        if (event.getSide() == LogicalSide.CLIENT) {
            AllSoundEvents.CARDBOARD_SWORD.playAt(event.getLevel(), (Vec3i)event.getPos(), 0.5f, 1.85f, false);
        } else {
            AllSoundEvents.CARDBOARD_SWORD.play(event.getLevel(), event.getEntity(), (Vec3i)event.getPos(), 0.5f, 1.85f);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void cardboardSwordsCannotHurtYou(AttackEntityEvent event) {
        LivingEntity target;
        Player attacker = event.getEntity();
        Entity entity = event.getTarget();
        if (!(entity instanceof LivingEntity) || (target = (LivingEntity)entity).getType().is(EntityTypeTags.ARTHROPOD)) {
            return;
        }
        ItemStack stack = attacker.getItemInHand(InteractionHand.MAIN_HAND);
        if (!AllItems.CARDBOARD_SWORD.isIn(stack)) {
            return;
        }
        AllSoundEvents.CARDBOARD_SWORD.playFrom((Entity)attacker, 0.75f, 1.85f);
        event.setCanceled(true);
        float knockbackStrength = (float)(attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + 2.0);
        Level level = attacker.level();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            knockbackStrength = EnchantmentHelper.modifyKnockback((ServerLevel)serverLevel, (ItemStack)stack, (Entity)target, (DamageSource)serverLevel.damageSources().playerAttack(attacker), (float)knockbackStrength);
        }
        if (attacker.isSprinting() && attacker.getAttackStrengthScale(0.5f) > 0.9f) {
            knockbackStrength += 1.0f;
        }
        if (knockbackStrength <= 0.0f) {
            return;
        }
        float yRot = attacker.getYRot();
        CardboardSwordItem.knockback(target, knockbackStrength, yRot);
        boolean targetIsPlayer = target instanceof Player;
        MobCategory targetType = target.getClassification(false);
        if (target instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)target;
            CatnipServices.NETWORK.sendToClient(sp, (CustomPacketPayload)new KnockbackPacket(yRot, knockbackStrength));
        }
        if (!(targetType != MobCategory.MISC && targetType != MobCategory.CREATURE || targetIsPlayer)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 9, true, false, false));
        }
        attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6, 1.0, 0.6));
        attacker.setSprinting(false);
    }

    public static void knockback(LivingEntity target, double knockbackStrength, float yRot) {
        target.stopRiding();
        target.knockback(knockbackStrength * 0.5, (double)Mth.sin((float)(yRot * ((float)Math.PI / 180))), (double)(-Mth.cos((float)(yRot * ((float)Math.PI / 180)))));
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create((Item)this, new CardboardSwordItemRenderer()));
    }
}
