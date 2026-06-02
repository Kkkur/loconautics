/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.monster.ZombieVillager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.Foods
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.EntityHitResult
 *  net.neoforged.neoforge.common.util.FakePlayer
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileEntityHitActions;
import java.util.UUID;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;

public static enum AllPotatoProjectileEntityHitActions.CureZombieVillager implements PotatoProjectileEntityHitAction
{
    INSTANCE;

    private static final AllPotatoProjectileEntityHitActions.FoodEffects EFFECT;
    private static final GameProfile ZOMBIE_CONVERTER_NAME;
    private static final WorldAttached<FakePlayer> ZOMBIE_CONVERTERS;
    public static final MapCodec<AllPotatoProjectileEntityHitActions.CureZombieVillager> CODEC;

    @Override
    public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
        ZombieVillager zombieVillager;
        Entity entity = ray.getEntity();
        Level world = entity.level();
        if (!(entity instanceof ZombieVillager) || !(zombieVillager = (ZombieVillager)entity).hasEffect(MobEffects.WEAKNESS)) {
            return EFFECT.execute(projectile, ray, type);
        }
        if (world.isClientSide) {
            return false;
        }
        FakePlayer dummy = (FakePlayer)ZOMBIE_CONVERTERS.get((LevelAccessor)world);
        dummy.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)Items.GOLDEN_APPLE, 1));
        zombieVillager.mobInteract((Player)dummy, InteractionHand.MAIN_HAND);
        return true;
    }

    @Override
    public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
        return CODEC;
    }

    static {
        EFFECT = new AllPotatoProjectileEntityHitActions.FoodEffects(Foods.GOLDEN_APPLE, false);
        ZOMBIE_CONVERTER_NAME = new GameProfile(UUID.fromString("be12d3dc-27d3-4992-8c97-66be53fd49c5"), "Converter");
        ZOMBIE_CONVERTERS = new WorldAttached(w -> new FakePlayer((ServerLevel)w, ZOMBIE_CONVERTER_NAME));
        CODEC = MapCodec.unit((Object)INSTANCE);
    }
}
