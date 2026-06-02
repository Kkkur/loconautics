/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.tick.EntityTickEvent$Pre
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class DivingBootsItem
extends BaseArmorItem {
    public static final EquipmentSlot SLOT = EquipmentSlot.FEET;
    public static final ArmorItem.Type TYPE = ArmorItem.Type.BOOTS;

    public DivingBootsItem(Holder<ArmorMaterial> material, Item.Properties properties, ResourceLocation textureLoc) {
        super(material, TYPE, properties, textureLoc);
    }

    public static boolean isWornBy(Entity entity) {
        return !DivingBootsItem.getWornItem(entity).isEmpty();
    }

    public static ItemStack getWornItem(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return ItemStack.EMPTY;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        ItemStack stack = livingEntity.getItemBySlot(SLOT);
        if (!(stack.getItem() instanceof DivingBootsItem)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @SubscribeEvent
    public static void accelerateDescentUnderwater(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity entity2 = (LivingEntity)entity;
        if (!DivingBootsItem.affects(entity2)) {
            return;
        }
        Vec3 motion = entity2.getDeltaMovement();
        boolean isJumping = entity2.jumping;
        entity2.setOnGround(entity2.onGround() || entity2.verticalCollision);
        if (isJumping && entity2.onGround()) {
            motion = motion.add(0.0, 0.5, 0.0);
            entity2.setOnGround(false);
        } else {
            motion = motion.add(0.0, (double)-0.05f, 0.0);
        }
        float multiplier = 1.3f;
        if (motion.multiply(1.0, 0.0, 1.0).length() < (double)0.145f && (entity2.zza > 0.0f || entity2.xxa != 0.0f) && !entity2.isShiftKeyDown()) {
            motion = motion.multiply((double)multiplier, 1.0, (double)multiplier);
        }
        entity2.setDeltaMovement(motion);
    }

    protected static boolean affects(LivingEntity entity) {
        if (!DivingBootsItem.isWornBy((Entity)entity)) {
            entity.getPersistentData().remove("HeavyBoots");
            return false;
        }
        NBTHelper.putMarker((CompoundTag)entity.getPersistentData(), (String)"HeavyBoots");
        if (!entity.isInWater()) {
            return false;
        }
        if (entity.getPose() == Pose.SWIMMING) {
            return false;
        }
        if (entity instanceof Player) {
            Player playerEntity = (Player)entity;
            if (playerEntity.getAbilities().flying) {
                return false;
            }
        }
        return true;
    }

    public static Vec3 getMovementMultiplier(LivingEntity entity) {
        double vMultiplier;
        double yMotion = entity.getDeltaMovement().y;
        double d = vMultiplier = yMotion < 0.0 ? Math.max(0.0, 2.5 - Math.abs(yMotion) * 2.0) : 1.0;
        if (!entity.onGround()) {
            if (entity.jumping && entity.getPersistentData().contains("LavaGrounded")) {
                boolean eyeInFluid = entity.isEyeInFluid(FluidTags.LAVA);
                vMultiplier = yMotion == 0.0 ? 0.0 : (eyeInFluid ? 1.0 : 0.5) / yMotion;
            } else if (yMotion > 0.0) {
                vMultiplier = 1.3;
            }
            entity.getPersistentData().remove("LavaGrounded");
            return new Vec3(1.75, vMultiplier, 1.75);
        }
        entity.getPersistentData().putBoolean("LavaGrounded", true);
        double hMultiplier = entity.isSprinting() ? 1.85 : 1.75;
        return new Vec3(hMultiplier, vMultiplier, hMultiplier);
    }
}
