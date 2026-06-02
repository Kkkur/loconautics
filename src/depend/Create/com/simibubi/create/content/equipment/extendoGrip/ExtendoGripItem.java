/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Multimap
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.InputEvent$InteractionKeyMappingTriggered
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
 *  net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent
 *  net.neoforged.neoforge.event.entity.player.AttackEntityEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.neoforged.neoforge.event.level.BlockEvent$BreakEvent
 *  net.neoforged.neoforge.event.level.BlockEvent$EntityPlaceEvent
 *  net.neoforged.neoforge.event.tick.EntityTickEvent$Pre
 */
package com.simibubi.create.content.equipment.extendoGrip;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripInteractionPacket;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItemRenderer;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class ExtendoGripItem
extends Item {
    public static final int MAX_DAMAGE = 200;
    public static final AttributeModifier singleRangeAttributeModifier = new AttributeModifier(Create.asResource("single_range_attribute_modifier"), 3.0, AttributeModifier.Operation.ADD_VALUE);
    public static final AttributeModifier doubleRangeAttributeModifier = new AttributeModifier(Create.asResource("double_range_attribute_modifier"), 5.0, AttributeModifier.Operation.ADD_VALUE);
    private static final Supplier<Multimap<Holder<Attribute>, AttributeModifier>> rangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of((Object)Attributes.BLOCK_INTERACTION_RANGE, (Object)singleRangeAttributeModifier));
    private static final Supplier<Multimap<Holder<Attribute>, AttributeModifier>> doubleRangeModifier = Suppliers.memoize(() -> ImmutableMultimap.of((Object)Attributes.BLOCK_INTERACTION_RANGE, (Object)doubleRangeAttributeModifier));
    private static DamageSource lastActiveDamageSource;
    public static final String EXTENDO_MARKER = "createExtendo";
    public static final String DUAL_EXTENDO_MARKER = "createDualExtendo";

    public ExtendoGripItem(Item.Properties properties) {
        super(properties.durability(200));
    }

    @SubscribeEvent
    public static void holdingExtendoGripIncreasesRange(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        CompoundTag persistentData = player.getPersistentData();
        boolean inOff = AllItems.EXTENDO_GRIP.isIn(player.getOffhandItem());
        boolean inMain = AllItems.EXTENDO_GRIP.isIn(player.getMainHandItem());
        boolean holdingDualExtendo = inOff && inMain;
        boolean holdingExtendo = inOff ^ inMain;
        boolean bl = !holdingDualExtendo;
        boolean wasHoldingExtendo = persistentData.contains(EXTENDO_MARKER);
        boolean wasHoldingDualExtendo = persistentData.contains(DUAL_EXTENDO_MARKER);
        if ((holdingExtendo &= bl) != wasHoldingExtendo) {
            if (!holdingExtendo) {
                player.getAttributes().removeAttributeModifiers(rangeModifier.get());
                persistentData.remove(EXTENDO_MARKER);
            } else {
                AllAdvancements.EXTENDO_GRIP.awardTo(player);
                player.getAttributes().addTransientAttributeModifiers(rangeModifier.get());
                persistentData.putBoolean(EXTENDO_MARKER, true);
            }
        }
        if (holdingDualExtendo != wasHoldingDualExtendo) {
            if (!holdingDualExtendo) {
                player.getAttributes().removeAttributeModifiers(doubleRangeModifier.get());
                persistentData.remove(DUAL_EXTENDO_MARKER);
            } else {
                AllAdvancements.EXTENDO_GRIP_DUAL.awardTo(player);
                player.getAttributes().addTransientAttributeModifiers(doubleRangeModifier.get());
                persistentData.putBoolean(DUAL_EXTENDO_MARKER, true);
            }
        }
    }

    @SubscribeEvent
    public static void addReachToJoiningPlayersHoldingExtendo(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(DUAL_EXTENDO_MARKER)) {
            player.getAttributes().addTransientAttributeModifiers(doubleRangeModifier.get());
        } else if (persistentData.contains(EXTENDO_MARKER)) {
            player.getAttributes().addTransientAttributeModifiers(rangeModifier.get());
        }
    }

    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void dontMissEntitiesWhenYouHaveHighReachDistance(InputEvent.InteractionKeyMappingTriggered event) {
        AABB AABB2;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.level == null || player == null) {
            return;
        }
        if (!ExtendoGripItem.isHoldingExtendoGrip((Player)player)) {
            return;
        }
        if (mc.hitResult instanceof BlockHitResult && mc.hitResult.getType() != HitResult.Type.MISS) {
            return;
        }
        double d0 = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        if (!player.isCreative()) {
            d0 -= 0.5;
        }
        Vec3 Vector3d = player.getEyePosition(AnimationTickHolder.getPartialTicks());
        Vec3 Vector3d1 = player.getViewVector(1.0f);
        Vec3 Vector3d2 = Vector3d.add(Vector3d1.x * d0, Vector3d1.y * d0, Vector3d1.z * d0);
        EntityHitResult entityraytraceresult = ProjectileUtil.getEntityHitResult((Entity)player, (Vec3)Vector3d, (Vec3)Vector3d2, (AABB)(AABB2 = player.getBoundingBox().expandTowards(Vector3d1.scale(d0)).inflate(1.0, 1.0, 1.0)), e -> !e.isSpectator() && e.isPickable(), (double)(d0 * d0));
        if (entityraytraceresult != null) {
            Entity entity1 = entityraytraceresult.getEntity();
            Vec3 Vector3d3 = entityraytraceresult.getLocation();
            double d2 = Vector3d.distanceToSqr(Vector3d3);
            if (d2 < d0 * d0 || mc.hitResult == null || mc.hitResult.getType() == HitResult.Type.MISS) {
                mc.hitResult = entityraytraceresult;
                if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrame) {
                    mc.crosshairPickEntity = entity1;
                }
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void consumeDurabilityOnBlockBreak(BlockEvent.BreakEvent event) {
        ExtendoGripItem.findAndDamageExtendoGrip(event.getPlayer());
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void consumeDurabilityOnPlace(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            ExtendoGripItem.findAndDamageExtendoGrip((Player)entity);
        }
    }

    private static void findAndDamageExtendoGrip(Player player) {
        if (player == null) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }
        EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
        ItemStack extendo = player.getMainHandItem();
        if (!AllItems.EXTENDO_GRIP.isIn(extendo)) {
            extendo = player.getOffhandItem();
            equipmentSlot = EquipmentSlot.OFFHAND;
        }
        if (!AllItems.EXTENDO_GRIP.isIn(extendo)) {
            return;
        }
        if (!BacktankUtil.canAbsorbDamage((LivingEntity)player, ExtendoGripItem.maxUses())) {
            extendo.hurtAndBreak(1, (LivingEntity)player, equipmentSlot);
        }
    }

    public boolean isBarVisible(ItemStack stack) {
        return BacktankUtil.isBarVisible(stack, ExtendoGripItem.maxUses());
    }

    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth(stack, ExtendoGripItem.maxUses());
    }

    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor(stack, ExtendoGripItem.maxUses());
    }

    private static int maxUses() {
        return (Integer)AllConfigs.server().equipment.maxExtendoGripActions.get();
    }

    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }

    @SubscribeEvent
    public static void bufferLivingAttackEvent(LivingIncomingDamageEvent event) {
        lastActiveDamageSource = event.getSource();
        DamageSource source = event.getSource();
        if (source == null) {
            return;
        }
        Entity trueSource = source.getEntity();
        if (trueSource instanceof Player) {
            ExtendoGripItem.findAndDamageExtendoGrip((Player)trueSource);
        }
    }

    @SubscribeEvent
    public static void attacksByExtendoGripHaveMoreKnockback(LivingKnockBackEvent event) {
        if (lastActiveDamageSource == null) {
            return;
        }
        Entity entity = lastActiveDamageSource.getDirectEntity();
        lastActiveDamageSource = null;
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (!ExtendoGripItem.isHoldingExtendoGrip(player)) {
            return;
        }
        event.setStrength(event.getStrength() + 2.0f);
    }

    private static boolean isUncaughtClientInteraction(Entity entity, Entity target) {
        if (entity.distanceToSqr(target) < 36.0) {
            return false;
        }
        if (!entity.level().isClientSide) {
            return false;
        }
        return entity instanceof Player;
    }

    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void notifyServerOfLongRangeAttacks(AttackEntityEvent event) {
        Entity target;
        Player entity = event.getEntity();
        if (!ExtendoGripItem.isUncaughtClientInteraction((Entity)entity, target = event.getTarget())) {
            return;
        }
        Player player = entity;
        if (ExtendoGripItem.isHoldingExtendoGrip(player)) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ExtendoGripInteractionPacket(target));
        }
    }

    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void notifyServerOfLongRangeInteractions(PlayerInteractEvent.EntityInteract event) {
        Entity target;
        Player entity = event.getEntity();
        if (!ExtendoGripItem.isUncaughtClientInteraction((Entity)entity, target = event.getTarget())) {
            return;
        }
        Player player = entity;
        if (ExtendoGripItem.isHoldingExtendoGrip(player)) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ExtendoGripInteractionPacket(target, event.getHand()));
        }
    }

    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void notifyServerOfLongRangeSpecificInteractions(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity target;
        Player entity = event.getEntity();
        if (!ExtendoGripItem.isUncaughtClientInteraction((Entity)entity, target = event.getTarget())) {
            return;
        }
        if (ExtendoGripItem.isHoldingExtendoGrip(entity)) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ExtendoGripInteractionPacket(target, event.getHand(), event.getLocalPos()));
        }
    }

    public static boolean isHoldingExtendoGrip(Player player) {
        boolean inOff = AllItems.EXTENDO_GRIP.isIn(player.getOffhandItem());
        boolean inMain = AllItems.EXTENDO_GRIP.isIn(player.getMainHandItem());
        boolean holdingGrip = inOff || inMain;
        return holdingGrip;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new ExtendoGripItemRenderer()));
    }
}
