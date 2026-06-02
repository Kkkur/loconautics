/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HumanoidModel$ArmPose
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CustomArmPoseItem {
    @Nullable
    public HumanoidModel.ArmPose getArmPose(ItemStack var1, AbstractClientPlayer var2, InteractionHand var3);
}
