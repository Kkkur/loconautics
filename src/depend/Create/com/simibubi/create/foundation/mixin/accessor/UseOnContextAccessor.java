/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.phys.BlockHitResult
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={UseOnContext.class})
public interface UseOnContextAccessor {
    @Invoker(value="getHitResult")
    public BlockHitResult create$getHitResult();
}
