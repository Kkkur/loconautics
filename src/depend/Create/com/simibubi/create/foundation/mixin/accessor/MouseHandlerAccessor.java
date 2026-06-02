/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MouseHandler
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={MouseHandler.class})
public interface MouseHandlerAccessor {
    @Accessor(value="xpos")
    public void create$setXPos(double var1);

    @Accessor(value="ypos")
    public void create$setYPos(double var1);
}
