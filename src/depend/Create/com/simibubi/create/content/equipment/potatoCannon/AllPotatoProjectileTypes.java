/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.data.worldgen.BootstrapContext
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.food.Foods
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileBlockHitActions;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileEntityHitActions;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class AllPotatoProjectileTypes {
    public static final ResourceKey<PotatoCannonProjectileType> FALLBACK = ResourceKey.create(CreateRegistries.POTATO_PROJECTILE_TYPE, (ResourceLocation)Create.asResource("fallback"));

    public static void bootstrap(BootstrapContext<PotatoCannonProjectileType> ctx) {
        AllPotatoProjectileTypes.register(ctx, "fallback", new PotatoCannonProjectileType.Builder().damage(0).build());
        AllPotatoProjectileTypes.register(ctx, "potato", new PotatoCannonProjectileType.Builder().damage(5).reloadTicks(15).velocity(1.25f).knockback(1.5f).renderTumbling().onBlockHit(new AllPotatoProjectileBlockHitActions.PlantCrop(Blocks.POTATOES)).addItems(new ItemLike[]{Items.POTATO}).build());
        AllPotatoProjectileTypes.register(ctx, "baked_potato", new PotatoCannonProjectileType.Builder().damage(5).reloadTicks(15).velocity(1.25f).knockback(0.5f).renderTumbling().preEntityHit(AllPotatoProjectileEntityHitActions.SetOnFire.seconds(3)).addItems(new ItemLike[]{Items.BAKED_POTATO}).build());
        AllPotatoProjectileTypes.register(ctx, "carrot", new PotatoCannonProjectileType.Builder().damage(4).reloadTicks(12).velocity(1.45f).knockback(0.3f).renderTowardMotion(140, 1.0f).soundPitch(1.5f).onBlockHit(new AllPotatoProjectileBlockHitActions.PlantCrop(Blocks.CARROTS)).addItems(new ItemLike[]{Items.CARROT}).build());
        AllPotatoProjectileTypes.register(ctx, "golden_carrot", new PotatoCannonProjectileType.Builder().damage(12).reloadTicks(15).velocity(1.45f).knockback(0.5f).renderTowardMotion(140, 2.0f).soundPitch(1.5f).addItems(new ItemLike[]{Items.GOLDEN_CARROT}).build());
        AllPotatoProjectileTypes.register(ctx, "sweet_berry", new PotatoCannonProjectileType.Builder().damage(3).reloadTicks(10).knockback(0.1f).velocity(1.05f).renderTumbling().splitInto(3).soundPitch(1.25f).addItems(new ItemLike[]{Items.SWEET_BERRIES}).build());
        AllPotatoProjectileTypes.register(ctx, "glow_berry", new PotatoCannonProjectileType.Builder().damage(2).reloadTicks(10).knockback(0.05f).velocity(1.05f).renderTumbling().splitInto(2).soundPitch(1.2f).onEntityHit(new AllPotatoProjectileEntityHitActions.PotionEffect((Holder<MobEffect>)MobEffects.GLOWING, 1, 200, false)).addItems(new ItemLike[]{Items.GLOW_BERRIES}).build());
        AllPotatoProjectileTypes.register(ctx, "chocolate_berry", new PotatoCannonProjectileType.Builder().damage(4).reloadTicks(10).knockback(0.2f).velocity(1.05f).renderTumbling().splitInto(3).soundPitch(1.25f).addItems((ItemLike)AllItems.CHOCOLATE_BERRIES.get()).build());
        AllPotatoProjectileTypes.register(ctx, "poison_potato", new PotatoCannonProjectileType.Builder().damage(5).reloadTicks(15).knockback(0.05f).velocity(1.25f).renderTumbling().onEntityHit(new AllPotatoProjectileEntityHitActions.PotionEffect((Holder<MobEffect>)MobEffects.POISON, 1, 160, true)).addItems(new ItemLike[]{Items.POISONOUS_POTATO}).build());
        AllPotatoProjectileTypes.register(ctx, "chorus_fruit", new PotatoCannonProjectileType.Builder().damage(3).reloadTicks(15).velocity(1.2f).knockback(0.05f).renderTumbling().onEntityHit(new AllPotatoProjectileEntityHitActions.ChorusTeleport(20.0)).addItems(new ItemLike[]{Items.CHORUS_FRUIT}).build());
        AllPotatoProjectileTypes.register(ctx, "apple", new PotatoCannonProjectileType.Builder().damage(5).reloadTicks(10).velocity(1.45f).knockback(0.5f).renderTumbling().soundPitch(1.1f).addItems(new ItemLike[]{Items.APPLE}).build());
        AllPotatoProjectileTypes.register(ctx, "honeyed_apple", new PotatoCannonProjectileType.Builder().damage(6).reloadTicks(15).velocity(1.35f).knockback(0.1f).renderTumbling().soundPitch(1.1f).onEntityHit(new AllPotatoProjectileEntityHitActions.PotionEffect((Holder<MobEffect>)MobEffects.MOVEMENT_SLOWDOWN, 2, 160, true)).addItems((ItemLike)AllItems.HONEYED_APPLE.get()).build());
        AllPotatoProjectileTypes.register(ctx, "golden_apple", new PotatoCannonProjectileType.Builder().damage(1).reloadTicks(100).velocity(1.45f).knockback(0.05f).renderTumbling().soundPitch(1.1f).onEntityHit(AllPotatoProjectileEntityHitActions.CureZombieVillager.INSTANCE).addItems(new ItemLike[]{Items.GOLDEN_APPLE}).build());
        AllPotatoProjectileTypes.register(ctx, "enchanted_golden_apple", new PotatoCannonProjectileType.Builder().damage(1).reloadTicks(100).velocity(1.45f).knockback(0.05f).renderTumbling().soundPitch(1.1f).onEntityHit(new AllPotatoProjectileEntityHitActions.FoodEffects(Foods.ENCHANTED_GOLDEN_APPLE, false)).addItems(new ItemLike[]{Items.ENCHANTED_GOLDEN_APPLE}).build());
        AllPotatoProjectileTypes.register(ctx, "beetroot", new PotatoCannonProjectileType.Builder().damage(2).reloadTicks(5).velocity(1.6f).knockback(0.1f).renderTowardMotion(140, 2.0f).soundPitch(1.6f).addItems(new ItemLike[]{Items.BEETROOT}).build());
        AllPotatoProjectileTypes.register(ctx, "melon_slice", new PotatoCannonProjectileType.Builder().damage(3).reloadTicks(8).knockback(0.1f).velocity(1.45f).renderTumbling().soundPitch(1.5f).addItems(new ItemLike[]{Items.MELON_SLICE}).build());
        AllPotatoProjectileTypes.register(ctx, "glistering_melon", new PotatoCannonProjectileType.Builder().damage(5).reloadTicks(8).knockback(0.1f).velocity(1.45f).renderTumbling().soundPitch(1.5f).onEntityHit(new AllPotatoProjectileEntityHitActions.PotionEffect((Holder<MobEffect>)MobEffects.GLOWING, 1, 100, true)).addItems(new ItemLike[]{Items.GLISTERING_MELON_SLICE}).build());
        AllPotatoProjectileTypes.register(ctx, "melon_block", new PotatoCannonProjectileType.Builder().damage(8).reloadTicks(20).knockback(2.0f).velocity(0.95f).renderTumbling().soundPitch(0.9f).onBlockHit(new AllPotatoProjectileBlockHitActions.PlaceBlockOnGround(Blocks.MELON)).addItems(new ItemLike[]{Blocks.MELON}).build());
        AllPotatoProjectileTypes.register(ctx, "pumpkin_block", new PotatoCannonProjectileType.Builder().damage(6).reloadTicks(15).knockback(2.0f).velocity(0.95f).renderTumbling().soundPitch(0.9f).onBlockHit(new AllPotatoProjectileBlockHitActions.PlaceBlockOnGround(Blocks.PUMPKIN)).addItems(new ItemLike[]{Blocks.PUMPKIN}).build());
        AllPotatoProjectileTypes.register(ctx, "pumpkin_pie", new PotatoCannonProjectileType.Builder().damage(7).reloadTicks(15).knockback(0.05f).velocity(1.1f).renderTumbling().sticky().soundPitch(1.1f).addItems(new ItemLike[]{Items.PUMPKIN_PIE}).build());
        AllPotatoProjectileTypes.register(ctx, "cake", new PotatoCannonProjectileType.Builder().damage(8).reloadTicks(15).knockback(0.1f).velocity(1.1f).renderTumbling().sticky().addItems(new ItemLike[]{Items.CAKE}).build());
        AllPotatoProjectileTypes.register(ctx, "blaze_cake", new PotatoCannonProjectileType.Builder().damage(15).reloadTicks(20).knockback(0.3f).velocity(1.1f).renderTumbling().sticky().preEntityHit(AllPotatoProjectileEntityHitActions.SetOnFire.seconds(12)).addItems((ItemLike)AllItems.BLAZE_CAKE.get()).build());
        AllPotatoProjectileTypes.register(ctx, "fish", new PotatoCannonProjectileType.Builder().damage(4).knockback(0.6f).velocity(1.3f).renderTowardMotion(140, 1.0f).sticky().soundPitch(1.3f).addItems(new ItemLike[]{Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.TROPICAL_FISH}).build());
        AllPotatoProjectileTypes.register(ctx, "pufferfish", new PotatoCannonProjectileType.Builder().damage(4).knockback(0.4f).velocity(1.1f).renderTowardMotion(140, 1.0f).sticky().onEntityHit(new AllPotatoProjectileEntityHitActions.FoodEffects(Foods.PUFFERFISH, false)).soundPitch(1.1f).addItems(new ItemLike[]{Items.PUFFERFISH}).build());
        AllPotatoProjectileTypes.register(ctx, "suspicious_stew", new PotatoCannonProjectileType.Builder().damage(3).reloadTicks(40).knockback(0.2f).velocity(0.8f).renderTowardMotion(140, 1.0f).dropStack(Items.BOWL.getDefaultInstance()).onEntityHit(AllPotatoProjectileEntityHitActions.SuspiciousStew.INSTANCE).addItems(new ItemLike[]{Items.SUSPICIOUS_STEW}).build());
    }

    private static void register(BootstrapContext<PotatoCannonProjectileType> ctx, String name, PotatoCannonProjectileType type) {
        ctx.register(ResourceKey.create(CreateRegistries.POTATO_PROJECTILE_TYPE, (ResourceLocation)Create.asResource(name)), (Object)type);
    }
}
