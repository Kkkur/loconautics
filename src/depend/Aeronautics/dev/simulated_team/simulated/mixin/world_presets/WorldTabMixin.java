/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.CycleButton
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
 *  net.minecraft.client.gui.screens.worldselection.WorldCreationUiState
 *  net.minecraft.client.gui.screens.worldselection.WorldCreationUiState$WorldTypeEntry
 *  net.minecraft.core.Holder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.simulated_team.simulated.mixin.world_presets;

import dev.simulated_team.simulated.content.worldgen.SimulatedWorldPreset;
import dev.simulated_team.simulated.index.SimWorldPresets;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets={"net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab"})
public class WorldTabMixin {
    @Inject(method={"<init>"}, at={@At(value="TAIL")}, remap=false, locals=LocalCapture.CAPTURE_FAILHARD)
    private void simulated$init(CreateWorldScreen createWorldScreen, CallbackInfo ci, GridLayout.RowHelper rowHelper, CycleButton<WorldCreationUiState> cycleButton) {
        createWorldScreen.getUiState().addListener(worldCreationUiState -> {
            ResourceKey key;
            SimulatedWorldPreset simPreset;
            WorldCreationUiState.WorldTypeEntry worldType = worldCreationUiState.getWorldType();
            Holder preset = worldType.preset();
            if (preset != null && preset.unwrapKey().isPresent() && (simPreset = SimWorldPresets.PRESETS.get((key = (ResourceKey)preset.unwrapKey().get()).location())) != null && simPreset.description() != null) {
                cycleButton.setTooltip(Tooltip.create((Component)simPreset.description()));
            }
        });
    }
}
