/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.protomanly.pmweather.PMWeather
 *  dev.protomanly.pmweather.weather.WindEngine
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.javafmlmod.FMLModContainer
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.pmweather;

import dev.protomanly.pmweather.PMWeather;
import dev.protomanly.pmweather.weather.WindEngine;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PMWeather.class})
public class PMWeatherMixin {
    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void init(FMLModContainer container, IEventBus bus, Dist dist, CallbackInfo ci) {
        SubLevelHelper.registerWindProvider((position, level) -> JOMLConversion.toJOML((Position)WindEngine.getWind((Vec3)JOMLConversion.toMojang((Vector3dc)position), (Level)level)));
    }
}
