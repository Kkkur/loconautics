/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.LevelSettings
 *  net.minecraft.world.level.dimension.end.EndDragonFight$Data
 *  net.minecraft.world.level.levelgen.WorldOptions
 *  net.minecraft.world.level.levelgen.presets.WorldPresets
 *  net.minecraft.world.level.storage.PrimaryLevelData
 *  net.minecraft.world.level.storage.PrimaryLevelData$SpecialWorldProperty
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.world_presets;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import dev.simulated_team.simulated.mixin_interface.PrimaryLevelDataExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PrimaryLevelData.class})
public class PrimaryLevelDataMixin
implements PrimaryLevelDataExtension {
    @Unique
    private static final String simulated$WORLD_PRESET_KEY = "simulated:world_preset";
    @Shadow
    private EndDragonFight.Data endDragonFightData;
    private ResourceLocation simulated$worldPresetKey = WorldPresets.NORMAL.location();

    @Inject(method={"parse"}, at={@At(value="RETURN")}, remap=false)
    private static <T> void simulated$parse(Dynamic<T> dynamic, LevelSettings levelSettings, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        DataResult string = dynamic.get(simulated$WORLD_PRESET_KEY).asString();
        if (string.isSuccess()) {
            ((PrimaryLevelDataExtension)cir.getReturnValue()).setPreset(ResourceLocation.parse((String)((String)string.getOrThrow())));
        }
    }

    @Inject(method={"setTagData"}, at={@At(value="TAIL")}, remap=false)
    private void simulated$setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        compoundTag.putString(simulated$WORLD_PRESET_KEY, this.getPreset().toString());
    }

    @Override
    public ResourceLocation getPreset() {
        return this.simulated$worldPresetKey;
    }

    @Override
    public void setPreset(ResourceLocation resourceLocation) {
        this.simulated$worldPresetKey = resourceLocation;
    }

    @Override
    public void setEndDragonFight(EndDragonFight.Data endDragonFight) {
        this.endDragonFightData = endDragonFight;
    }
}
