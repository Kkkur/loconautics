/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.backend.engine.LightDataCollector
 *  dev.engine_room.flywheel.backend.engine.LightStorage
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.flywheel;

import dev.engine_room.flywheel.backend.engine.LightDataCollector;
import dev.engine_room.flywheel.backend.engine.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={LightStorage.class})
public interface LightStorageAccessor {
    @Accessor
    public LightDataCollector getCollector();

    @Accessor
    public void setNeedsLutRebuild(boolean var1);
}
