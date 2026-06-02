/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.NeutralMob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.WrappedGoal
 *  net.minecraft.world.entity.ai.goal.target.TargetGoal
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.EntityEvent$Size
 *  net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent
 *  net.neoforged.neoforge.event.entity.living.LivingEvent$LivingVisibilityEvent
 *  net.neoforged.neoforge.event.tick.EntityTickEvent$Pre
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.content.equipment.armor.CardboardArmorItem;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class CardboardArmorHandler {
    @SubscribeEvent
    public static void playerHitboxChangesWhenHidingAsBox(EntityEvent.Size event) {
        float scale;
        Entity entity = event.getEntity();
        if (!entity.isAddedToLevel()) {
            return;
        }
        if (!CardboardArmorHandler.testForStealth(entity)) {
            return;
        }
        if (entity instanceof LivingEntity) {
            LivingEntity le = (LivingEntity)entity;
            scale = le.getScale();
        } else {
            scale = 1.0f;
        }
        event.setNewSize(EntityDimensions.fixed((float)(0.6f * scale), (float)(0.8f * scale)).withEyeHeight(0.6f * scale));
        if (!entity.level().isClientSide() && entity instanceof Player) {
            Player p = (Player)entity;
            AllAdvancements.CARDBOARD_ARMOR.awardTo(p);
        }
    }

    @SubscribeEvent
    public static void playerChangesEquipment(LivingEquipmentChangeEvent event) {
        Player player;
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player && (player = (Player)livingEntity).getPose() == Pose.CROUCHING && (CardboardArmorHandler.isCardboardArmor(player.getItemBySlot(EquipmentSlot.HEAD)) || CardboardArmorHandler.isCardboardArmor(player.getItemBySlot(EquipmentSlot.CHEST)) || CardboardArmorHandler.isCardboardArmor(player.getItemBySlot(EquipmentSlot.LEGS)) || CardboardArmorHandler.isCardboardArmor(player.getItemBySlot(EquipmentSlot.FEET))) && !player.level().isClientSide()) {
            Pose pose = player.getPose();
            player.setPose(pose == Pose.CROUCHING ? Pose.STANDING : Pose.CROUCHING);
            player.setPose(pose);
        }
    }

    @SubscribeEvent
    public static void playersStealthWhenWearingCardboard(LivingEvent.LivingVisibilityEvent event) {
        LivingEntity entity = event.getEntity();
        if (!CardboardArmorHandler.testForStealth((Entity)entity)) {
            return;
        }
        event.modifyVisibility(0.0);
    }

    @SubscribeEvent
    public static void mobsMayLoseTargetWhenItIsWearingCardboard(EntityTickEvent.Pre event) {
        Level tg;
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity entity2 = (LivingEntity)entity;
        if (entity2.tickCount % 16 != 0) {
            return;
        }
        if (!(entity2 instanceof Mob)) {
            return;
        }
        Mob mob = (Mob)entity2;
        if (CardboardArmorHandler.testForStealth((Entity)mob.getTarget())) {
            mob.setTarget(null);
            if (mob.targetSelector != null) {
                for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) {
                    Goal goal2;
                    if (!goal.isRunning() || !((goal2 = goal.getGoal()) instanceof TargetGoal)) continue;
                    tg = (TargetGoal)goal2;
                    tg.stop();
                }
            }
        }
        if (entity2 instanceof NeutralMob) {
            NeutralMob nMob = (NeutralMob)entity2;
            tg = entity2.level();
            if (tg instanceof ServerLevel) {
                ServerLevel sl = (ServerLevel)tg;
                UUID uuid = nMob.getPersistentAngerTarget();
                if (uuid != null && CardboardArmorHandler.testForStealth(sl.getEntity(uuid))) {
                    nMob.stopBeingAngry();
                }
            }
        }
        if (CardboardArmorHandler.testForStealth((Entity)mob.getLastHurtByMob())) {
            mob.setLastHurtByMob(null);
            mob.setLastHurtByPlayer(null);
        }
    }

    public static boolean testForStealth(Entity entityIn) {
        if (!(entityIn instanceof LivingEntity)) {
            return false;
        }
        LivingEntity entity = (LivingEntity)entityIn;
        if (entity.getPose() != Pose.CROUCHING) {
            return false;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.getAbilities().flying) {
                return false;
            }
        }
        if (!CardboardArmorHandler.isCardboardArmor(entity.getItemBySlot(EquipmentSlot.HEAD))) {
            return false;
        }
        if (!CardboardArmorHandler.isCardboardArmor(entity.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
        }
        if (!CardboardArmorHandler.isCardboardArmor(entity.getItemBySlot(EquipmentSlot.LEGS))) {
            return false;
        }
        return CardboardArmorHandler.isCardboardArmor(entity.getItemBySlot(EquipmentSlot.FEET));
    }

    public static boolean isCardboardArmor(ItemStack stack) {
        return stack.getItem() instanceof CardboardArmorItem;
    }
}
