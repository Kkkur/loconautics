/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$ItemPickerMenu
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.creative_tab_sections;

import dev.simulated_team.simulated.registrate.simulated_tab.SimulatedCreativeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CreativeModeInventoryScreen.ItemPickerMenu.class})
public abstract class ItemPickerMenuMixin {
    @Shadow
    protected abstract int getRowIndexForScroll(float var1);

    @Inject(method={"scrollTo"}, at={@At(value="HEAD")})
    private void simulated$scrollTo(float f, CallbackInfo ci) {
        SimulatedCreativeTab.CURRENT_ROW = this.getRowIndexForScroll(f);
    }
}
