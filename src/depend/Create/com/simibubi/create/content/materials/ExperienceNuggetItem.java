/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.materials;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExperienceNuggetItem
extends Item {
    public ExperienceNuggetItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            pLevel.playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 0.5f, 1.0f);
            return InteractionResultHolder.consume((Object)itemInHand);
        }
        int amountUsed = pPlayer.isShiftKeyDown() ? 1 : itemInHand.getCount();
        int total = Mth.ceil((float)(3.0f * (float)amountUsed));
        int maxOrbs = amountUsed == 1 ? 1 : 5;
        int valuePer = Math.max(1, 1 + total / maxOrbs);
        for (int i = 0; i < maxOrbs; ++i) {
            int value = Math.min(valuePer, total - i * valuePer);
            if (value == 0) continue;
            Vec3 offset = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)pLevel.random, (float)1.0f).normalize();
            Vec3 look = pPlayer.getLookAngle();
            Vec3 motion = look.scale(0.2).add(0.0, 0.2, 0.0).add(offset.scale(0.1));
            Vec3 cross = look.cross(VecHelper.rotate((Vec3)new Vec3(-0.75, 0.0, 0.0), (double)(-pPlayer.getYRot()), (Direction.Axis)Direction.Axis.Y));
            Vec3 global = offset.add(pPlayer.getPosition(1.0f));
            global = pPlayer.getEyePosition().add(look.scale(0.5)).add(cross);
            ExperienceOrb xp = new ExperienceOrb(pLevel, global.x, global.y, global.z, value);
            xp.setDeltaMovement(motion);
            pLevel.addFreshEntity((Entity)xp);
        }
        itemInHand.shrink(amountUsed);
        if (!itemInHand.isEmpty()) {
            return InteractionResultHolder.success((Object)itemInHand);
        }
        pPlayer.setItemInHand(pUsedHand, ItemStack.EMPTY);
        return InteractionResultHolder.consume((Object)itemInHand);
    }
}
