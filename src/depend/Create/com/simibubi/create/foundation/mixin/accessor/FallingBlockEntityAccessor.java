/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={FallingBlockEntity.class})
public interface FallingBlockEntityAccessor {
    @Invoker(value="<init>")
    public static FallingBlockEntity create$callInit(Level level, double x, double y, double z, BlockState state) {
        throw new AssertionError();
    }
}
