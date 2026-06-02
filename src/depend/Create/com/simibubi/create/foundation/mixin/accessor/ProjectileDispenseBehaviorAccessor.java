/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.dispenser.ProjectileDispenseBehavior
 *  net.minecraft.world.item.ProjectileItem
 *  net.minecraft.world.item.ProjectileItem$DispenseConfig
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.item.ProjectileItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ProjectileDispenseBehavior.class})
public interface ProjectileDispenseBehaviorAccessor {
    @Accessor(value="projectileItem")
    public ProjectileItem create$getProjectileItem();

    @Accessor(value="dispenseConfig")
    public ProjectileItem.DispenseConfig create$getDispenseConfig();
}
