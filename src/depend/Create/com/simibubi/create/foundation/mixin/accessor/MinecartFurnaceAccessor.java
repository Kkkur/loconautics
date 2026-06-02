/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.vehicle.MinecartFurnace
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.world.entity.vehicle.MinecartFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={MinecartFurnace.class})
public interface MinecartFurnaceAccessor {
    @Accessor(value="fuel")
    public int create$getFuel();

    @Accessor(value="fuel")
    public void create$setFuel(int var1);
}
