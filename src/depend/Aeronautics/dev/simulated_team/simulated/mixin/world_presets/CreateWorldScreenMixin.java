/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
 *  net.minecraft.client.gui.screens.worldselection.WorldCreationContext
 *  net.minecraft.client.gui.screens.worldselection.WorldCreationUiState
 *  net.minecraft.core.Holder
 *  net.minecraft.core.LayeredRegistryAccess
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.RegistryLayer
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.LevelSettings
 *  net.minecraft.world.level.dimension.end.EndDragonFight$Data
 *  net.minecraft.world.level.storage.PrimaryLevelData$SpecialWorldProperty
 *  net.minecraft.world.level.storage.WorldData
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.simulated_team.simulated.mixin.world_presets;

import com.mojang.serialization.Lifecycle;
import dev.simulated_team.simulated.content.worldgen.SimulatedWorldPreset;
import dev.simulated_team.simulated.index.SimWorldPresets;
import dev.simulated_team.simulated.mixin_interface.PrimaryLevelDataExtension;
import java.util.Optional;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={CreateWorldScreen.class})
public abstract class CreateWorldScreenMixin {
    @Shadow
    @Final
    private WorldCreationUiState uiState;

    @Shadow
    public abstract WorldCreationUiState getUiState();

    @Inject(method={"createNewWorld"}, at={@At(value="HEAD")}, remap=false)
    private void simulated$createNewWorld(PrimaryLevelData.SpecialWorldProperty specialWorldProperty, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, Lifecycle lifecycle, CallbackInfo ci) {
        Holder preset = this.getUiState().getWorldType().preset();
        ResourceLocation location = ((ResourceKey)preset.unwrapKey().get()).location();
        SimulatedWorldPreset simPreset = SimWorldPresets.PRESETS.get(location);
        if (simPreset != null) {
            GameRules gameRules = this.uiState.getGameRules();
            simPreset.modifyGameRules(gameRules);
        }
    }

    @Inject(method={"createNewWorld"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;createWorldOpenFlows()Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;", shift=At.Shift.BEFORE)}, locals=LocalCapture.CAPTURE_FAILHARD, remap=false)
    private void simulated$createNewWorld2(PrimaryLevelData.SpecialWorldProperty specialWorldProperty, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, Lifecycle lifecycle, CallbackInfo ci, Optional optional, boolean bl, WorldCreationContext worldCreationContext, LevelSettings levelSettings, WorldData worldData) {
        ((PrimaryLevelDataExtension)worldData).setPreset(((ResourceKey)this.uiState.getWorldType().preset().unwrapKey().get()).location());
        if (this.getUiState().getWorldType().preset().is(SimWorldPresets.END_SEA.id())) {
            ((PrimaryLevelDataExtension)worldData).setEndDragonFight(new EndDragonFight.Data(false, true, true, false, Optional.empty(), Optional.empty(), Optional.empty()));
        }
    }
}
