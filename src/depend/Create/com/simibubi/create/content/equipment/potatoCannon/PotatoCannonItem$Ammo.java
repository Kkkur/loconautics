/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import net.minecraft.world.item.ItemStack;

public record PotatoCannonItem.Ammo(ItemStack stack, PotatoCannonProjectileType type) {
}
