/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  xaero.map.MapProcessor
 *  xaero.map.gui.GuiMap
 */
package com.simibubi.create.foundation.mixin.compat.xaeros;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;

@Mixin(value={GuiMap.class}, remap=false)
public interface XaeroFullscreenMapAccessor {
    @Accessor(value="cameraX")
    public double create$getCameraX();

    @Accessor(value="cameraZ")
    public double create$getCameraZ();

    @Accessor(value="scale")
    public double create$getScale();

    @Accessor(value="mapProcessor")
    public MapProcessor create$getMapProcessor();
}
