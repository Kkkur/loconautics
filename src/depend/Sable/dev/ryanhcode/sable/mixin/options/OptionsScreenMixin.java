/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.layouts.LinearLayout
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.options.OptionsScreen
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.options;

import dev.ryanhcode.sable.config.SubLevelSettingsScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={OptionsScreen.class})
public abstract class OptionsScreenMixin
extends Screen {
    @Shadow
    @Final
    private Options options;

    protected OptionsScreenMixin(Component component) {
        super(component);
    }

    @Inject(method={"createOnlineButton"}, at={@At(value="RETURN")}, cancellable=true)
    public void sable$createSableButton(CallbackInfoReturnable<LayoutElement> cir) {
        if (this.minecraft.level == null || !this.minecraft.hasSingleplayerServer()) {
            return;
        }
        LinearLayout layout = LinearLayout.vertical();
        Button sableButton = Button.builder((Component)SubLevelSettingsScreen.TITLE, event -> this.minecraft.setScreen((Screen)new SubLevelSettingsScreen(this, this.options, SubLevelSettingsScreen.TITLE))).pos(0, 30).size(150, 20).build();
        layout.addChild((LayoutElement)cir.getReturnValue());
        layout.spacing(5);
        layout.addChild((LayoutElement)sableButton);
        cir.setReturnValue((Object)layout);
    }
}
