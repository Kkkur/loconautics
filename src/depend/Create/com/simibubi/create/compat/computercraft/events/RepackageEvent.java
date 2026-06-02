/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.events;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RepackageEvent
implements ComputerEvent {
    @NotNull
    public ItemStack box;
    public int count;

    public RepackageEvent(@NotNull ItemStack box, int count) {
        this.box = box;
        this.count = count;
    }
}
