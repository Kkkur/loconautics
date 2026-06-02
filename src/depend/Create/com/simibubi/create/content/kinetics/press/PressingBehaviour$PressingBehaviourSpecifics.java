/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.kinetics.press;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import java.util.List;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public static interface PressingBehaviour.PressingBehaviourSpecifics {
    public boolean tryProcessInBasin(boolean var1);

    public boolean tryProcessOnBelt(TransportedItemStack var1, List<ItemStack> var2, boolean var3);

    public boolean tryProcessInWorld(ItemEntity var1, boolean var2);

    public boolean canProcessInBulk();

    public void onPressingCompleted();

    public int getParticleAmount();

    public float getKineticSpeed();
}
