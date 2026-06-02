/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.SpriteContents$Ticker
 */
package dev.simulated_team.simulated.mixin_interface;

import net.minecraft.client.renderer.texture.SpriteContents;

public interface SpriteContentsExtension {
    public SpriteContents.Ticker simulated$getTicker();

    public void simulated$setTicker(SpriteContents.Ticker var1);
}
