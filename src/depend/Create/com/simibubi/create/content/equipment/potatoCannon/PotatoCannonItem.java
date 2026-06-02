/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.HumanoidModel$ArmPose
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.HolderLookup$RegistryLookup
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ProjectileWeaponItem
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.CreateClient;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItemRenderer;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonPacket;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.GlobalRegistryAccess;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class PotatoCannonItem
extends ProjectileWeaponItem
implements CustomArmPoseItem {
    private static final Predicate<ItemStack> AMMO_PREDICATE = s -> PotatoCannonProjectileType.getTypeForItem(GlobalRegistryAccess.getOrThrow(), s.getItem()).isPresent();

    public PotatoCannonItem(Item.Properties properties) {
        super(properties);
    }

    @Nullable
    public static Ammo getAmmo(Player player, ItemStack heldStack) {
        ItemStack ammoStack = player.getProjectile(heldStack);
        if (ammoStack.isEmpty()) {
            return null;
        }
        return PotatoCannonProjectileType.getTypeForItem(player.level().registryAccess(), ammoStack.getItem()).map(r -> new Ammo(ammoStack, (PotatoCannonProjectileType)r.value())).orElse(null);
    }

    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
    }

    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
    }

    public InteractionResult useOn(UseOnContext context) {
        return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (ShootableGadgetItemMethods.shouldSwap(player, heldStack, hand, s -> s.getItem() instanceof PotatoCannonItem)) {
            return InteractionResultHolder.fail((Object)heldStack);
        }
        Ammo ammo = PotatoCannonItem.getAmmo(player, heldStack);
        if (ammo == null) {
            return InteractionResultHolder.pass((Object)heldStack);
        }
        ItemStack ammoStack = ammo.stack();
        PotatoCannonProjectileType projectileType = ammo.type();
        if (level.isClientSide) {
            CreateClient.POTATO_CANNON_RENDER_HANDLER.dontAnimateItem(hand);
            return InteractionResultHolder.success((Object)heldStack);
        }
        Vec3 barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(player, hand == InteractionHand.MAIN_HAND, new Vec3(0.75, (double)-0.15f, 1.5));
        Vec3 correction = ShootableGadgetItemMethods.getGunBarrelVec(player, hand == InteractionHand.MAIN_HAND, new Vec3((double)-0.05f, 0.0, 0.0)).subtract(player.position().add(0.0, (double)player.getEyeHeight(), 0.0));
        Vec3 lookVec = player.getLookAngle();
        Vec3 motion = lookVec.add(correction).normalize().scale(2.0).scale((double)projectileType.velocityMultiplier());
        float soundPitch = projectileType.soundPitch() + (level.getRandom().nextFloat() - 0.5f) / 4.0f;
        boolean spray = projectileType.split() > 1;
        Vec3 sprayBase = VecHelper.rotate((Vec3)new Vec3(0.0, 0.1, 0.0), (double)(360.0f * level.getRandom().nextFloat()), (Direction.Axis)Direction.Axis.Z);
        float sprayChange = 360.0f / (float)projectileType.split();
        ItemStack ammoStackCopy = ammoStack.copy();
        for (int i = 0; i < projectileType.split(); ++i) {
            PotatoProjectileEntity projectile = (PotatoProjectileEntity)AllEntityTypes.POTATO_PROJECTILE.create(level);
            projectile.setItem(ammoStackCopy);
            projectile.setEnchantmentEffectsFromCannon(heldStack);
            Vec3 splitMotion = motion;
            if (spray) {
                float imperfection = 40.0f * (level.getRandom().nextFloat() - 0.5f);
                Vec3 sprayOffset = VecHelper.rotate((Vec3)sprayBase, (double)((float)i * sprayChange + imperfection), (Direction.Axis)Direction.Axis.Z);
                splitMotion = splitMotion.add(VecHelper.lookAt((Vec3)sprayOffset, (Vec3)motion));
            }
            if (i != 0) {
                projectile.recoveryChance = 0.0f;
            }
            projectile.setPos(barrelPos.x, barrelPos.y, barrelPos.z);
            projectile.setDeltaMovement(splitMotion);
            projectile.setOwner((Entity)player);
            level.addFreshEntity((Entity)projectile);
        }
        if (!player.isCreative()) {
            ammoStack.shrink(1);
            if (ammoStack.isEmpty()) {
                player.getInventory().removeItem(ammoStack);
            }
        }
        if (!BacktankUtil.canAbsorbDamage((LivingEntity)player, PotatoCannonItem.maxUses())) {
            heldStack.hurtAndBreak(1, (LivingEntity)player, LivingEntity.getSlotForHand((InteractionHand)hand));
        }
        ShootableGadgetItemMethods.applyCooldown(player, heldStack, hand, s -> s.getItem() instanceof PotatoCannonItem, projectileType.reloadTicks());
        ShootableGadgetItemMethods.sendPackets(player, b -> new PotatoCannonPacket(barrelPos, lookVec.normalize(), ammoStack, hand, soundPitch, (boolean)b));
        return InteractionResultHolder.success((Object)heldStack);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            super.appendHoverText(stack, context, tooltip, flag);
            return;
        }
        Ammo ammo = PotatoCannonItem.getAmmo((Player)player, stack);
        if (ammo == null) {
            super.appendHoverText(stack, context, tooltip, flag);
            return;
        }
        ItemStack ammoStack = ammo.stack();
        PotatoCannonProjectileType type = ammo.type();
        HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            return;
        }
        HolderLookup.RegistryLookup lookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        int power = stack.getEnchantmentLevel((Holder)lookup.getOrThrow(Enchantments.POWER));
        int punch = stack.getEnchantmentLevel((Holder)lookup.getOrThrow(Enchantments.PUNCH));
        float additionalDamageMult = 1.0f + (float)power * 0.2f;
        float additionalKnockback = (float)punch * 0.5f;
        String _attack = "potato_cannon.ammo.attack_damage";
        String _reload = "potato_cannon.ammo.reload_ticks";
        String _knockback = "potato_cannon.ammo.knockback";
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add((Component)Component.translatable((String)ammoStack.getDescriptionId()).append((Component)Component.literal((String)":")).withStyle(ChatFormatting.GRAY));
        MutableComponent spacing = CommonComponents.space();
        ChatFormatting green = ChatFormatting.GREEN;
        ChatFormatting darkGreen = ChatFormatting.DARK_GREEN;
        float damageF = (float)type.damage() * additionalDamageMult;
        MutableComponent damage = Component.literal((String)(damageF == (float)Mth.floor((float)damageF) ? "" + Mth.floor((float)damageF) : "" + damageF));
        MutableComponent reloadTicks = Component.literal((String)("" + type.reloadTicks()));
        MutableComponent knockback = Component.literal((String)("" + (type.knockback() + additionalKnockback)));
        damage = damage.withStyle(additionalDamageMult > 1.0f ? green : darkGreen);
        knockback = knockback.withStyle(additionalKnockback > 0.0f ? green : darkGreen);
        reloadTicks = reloadTicks.withStyle(darkGreen);
        tooltip.add((Component)spacing.plainCopy().append((Component)CreateLang.translateDirect(_attack, damage).withStyle(darkGreen)));
        tooltip.add((Component)spacing.plainCopy().append((Component)CreateLang.translateDirect(_reload, reloadTicks).withStyle(darkGreen)));
        tooltip.add((Component)spacing.plainCopy().append((Component)CreateLang.translateDirect(_knockback, knockback).withStyle(darkGreen)));
    }

    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return false;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || newStack.getItem() != oldStack.getItem();
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return AMMO_PREDICATE;
    }

    public int getDefaultProjectileRange() {
        return 15;
    }

    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.INFINITY)) {
            return false;
        }
        if (enchantment.is(Enchantments.LOOTING)) {
            return true;
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    public boolean isBarVisible(ItemStack stack) {
        return BacktankUtil.isBarVisible(stack, PotatoCannonItem.maxUses());
    }

    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth(stack, PotatoCannonItem.maxUses());
    }

    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor(stack, PotatoCannonItem.maxUses());
    }

    private static int maxUses() {
        return (Integer)AllConfigs.server().equipment.maxPotatoCannonShots.get();
    }

    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    @Nullable
    public HumanoidModel.ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        if (!player.swinging) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return null;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create((Item)this, new PotatoCannonItemRenderer()));
    }

    public record Ammo(ItemStack stack, PotatoCannonProjectileType type) {
    }
}
