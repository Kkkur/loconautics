/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.backend.engine.DrawManager
 *  dev.engine_room.flywheel.backend.engine.EngineImpl
 *  dev.engine_room.flywheel.backend.engine.LightStorage
 *  net.minecraft.world.level.LevelAccessor
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.flywheel;

import dev.engine_room.flywheel.backend.engine.DrawManager;
import dev.engine_room.flywheel.backend.engine.EngineImpl;
import dev.engine_room.flywheel.backend.engine.LightStorage;
import dev.ryanhcode.sable.neoforge.compatibility.flywheel.SableFlywheelLightStorage;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EngineImpl.class})
public class EngineImplMixin {
    @Shadow
    @Final
    @Mutable
    private LightStorage lightStorage;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void sable$replaceLightStorage(LevelAccessor level, DrawManager drawManager, int maxOriginDistance, CallbackInfo ci) {
        this.lightStorage.delete();
        this.lightStorage = new SableFlywheelLightStorage(level);
    }
}
