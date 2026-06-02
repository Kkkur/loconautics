/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.projectile.ThrownEgg
 *  net.minecraft.world.entity.projectile.ThrownPotion
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.ProjectileImpactEvent
 */
package com.simibubi.create.content.processing.burner;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber
public class BlazeBurnerHandler {
    @SubscribeEvent
    public static void onThrowableImpact(ProjectileImpactEvent event) {
        BlazeBurnerHandler.thrownEggsGetEatenByBurner(event);
        BlazeBurnerHandler.splashExtinguishesBurner(event);
    }

    public static void thrownEggsGetEatenByBurner(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!(projectile instanceof ThrownEgg)) {
            return;
        }
        if (event.getRayTraceResult().getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockEntity blockEntity = projectile.level().getBlockEntity(BlockPos.containing((Position)event.getRayTraceResult().getLocation()));
        if (!(blockEntity instanceof BlazeBurnerBlockEntity)) {
            return;
        }
        BlazeBurnerBlockEntity heater = (BlazeBurnerBlockEntity)blockEntity;
        event.setCanceled(true);
        projectile.setDeltaMovement(Vec3.ZERO);
        projectile.discard();
        Level world = projectile.level();
        if (world.isClientSide) {
            return;
        }
        if (!heater.isCreative() && heater.activeFuel != BlazeBurnerBlockEntity.FuelType.SPECIAL) {
            heater.activeFuel = BlazeBurnerBlockEntity.FuelType.NORMAL;
            heater.remainingBurnTime = Mth.clamp((int)(heater.remainingBurnTime + 80), (int)0, (int)10000);
            heater.updateBlockState();
            heater.notifyUpdate();
        }
        AllSoundEvents.BLAZE_MUNCH.playOnServer(world, (Vec3i)heater.getBlockPos());
    }

    public static void splashExtinguishesBurner(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (projectile.level().isClientSide) {
            return;
        }
        if (!(projectile instanceof ThrownPotion)) {
            return;
        }
        ThrownPotion entity = (ThrownPotion)projectile;
        if (event.getRayTraceResult().getType() != HitResult.Type.BLOCK) {
            return;
        }
        ItemStack stack = entity.getItem();
        PotionContents potionContents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null && potionContents.is(Potions.WATER) && !potionContents.hasEffects()) {
            BlockHitResult result = (BlockHitResult)event.getRayTraceResult();
            Level world = entity.level();
            Direction face = result.getDirection();
            BlockPos pos = result.getBlockPos().relative(face);
            BlazeBurnerHandler.extinguishLitBurners(world, pos, face);
            BlazeBurnerHandler.extinguishLitBurners(world, pos.relative(face.getOpposite()), face);
            for (Direction face1 : Direction.Plane.HORIZONTAL) {
                BlazeBurnerHandler.extinguishLitBurners(world, pos.relative(face1), face1);
            }
        }
    }

    private static void extinguishLitBurners(Level world, BlockPos pos, Direction direction) {
        BlockState state = world.getBlockState(pos);
        if (AllBlocks.LIT_BLAZE_BURNER.has(state)) {
            world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            world.setBlockAndUpdate(pos, AllBlocks.BLAZE_BURNER.getDefaultState());
        }
    }
}
