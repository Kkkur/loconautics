/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={CropBlock.class})
public interface CropBlockAccessor {
    @Invoker(value="getAgeProperty")
    public IntegerProperty create$callGetAgeProperty();
}
