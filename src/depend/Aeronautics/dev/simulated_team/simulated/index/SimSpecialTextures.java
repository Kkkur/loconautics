/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.Simulated;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.resources.ResourceLocation;

public enum SimSpecialTextures implements BindableTexture
{
    HONEY_GLUE("honey_glue.png");

    public static final String ASSET_PATH = "textures/special/";
    private final ResourceLocation location;

    private SimSpecialTextures(String filename) {
        this.location = Simulated.path(ASSET_PATH + filename);
    }

    public ResourceLocation getLocation() {
        return this.location;
    }
}
