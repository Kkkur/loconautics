/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.SpriteContents$Ticker
 *  net.minecraft.client.renderer.texture.SpriteTicker
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.creative_tab_sections;

import dev.simulated_team.simulated.mixin_interface.SpriteContentsExtension;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SpriteContents.class})
public class SpriteContentsMixin
implements SpriteContentsExtension {
    @Unique
    private SpriteContents.Ticker simulated$ticker = null;

    @Override
    public SpriteContents.Ticker simulated$getTicker() {
        return this.simulated$ticker;
    }

    @Override
    public void simulated$setTicker(SpriteContents.Ticker ticker) {
        this.simulated$ticker = ticker;
    }

    @Inject(method={"createTicker"}, at={@At(value="RETURN")})
    private void simulated$createTicker(CallbackInfoReturnable<SpriteTicker> cir) {
        this.simulated$setTicker((SpriteContents.Ticker)cir.getReturnValue());
    }
}
