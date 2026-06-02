/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.simulated_team.simulated.mixin.diagram;

import com.mojang.blaze3d.platform.NativeImage;
import dev.simulated_team.simulated.mixin_interface.diagram.LightTextureExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={LightTexture.class})
public abstract class LightTextureMixin
implements LightTextureExtension {
    @Shadow
    private boolean updateLightTexture;
    @Shadow
    @Final
    private DynamicTexture lightTexture;
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private NativeImage lightPixels;

    @Shadow
    protected static void clampColor(Vector3f color) {
    }

    @Shadow
    protected abstract float notGamma(float var1);

    @Unique
    private static float simulated$getBrightness(int lightLevel) {
        float f = (float)lightLevel / 15.0f;
        return f / (4.0f - 3.0f * f);
    }

    @Override
    public void simulated$makeDiagramLightTexture(float brightnessMultiplier) {
        Vector3f color = new Vector3f();
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                float brightness = LightTextureMixin.simulated$getBrightness(y) * 0.6f + 0.15f;
                float brightnessG = brightness * ((brightness * 0.6f + 0.4f) * 0.6f + 0.4f);
                float brightnessB = brightness * (brightness * brightness * 0.6f + 0.4f);
                color.set(brightness, brightnessG, brightnessB);
                color.lerp((Vector3fc)new Vector3f(0.99f, 1.12f, 1.0f), 0.25f);
                LightTextureMixin.clampColor(color);
                float gamma = 0.55f;
                Vector3f notGamma = new Vector3f(this.notGamma(color.x), this.notGamma(color.y), this.notGamma(color.z));
                color.lerp((Vector3fc)notGamma, Math.max(0.0f, 0.55f));
                color.lerp((Vector3fc)new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                LightTextureMixin.clampColor(color);
                color.mul(255.0f);
                color.mul(brightnessMultiplier);
                int r = (int)color.x();
                int g = (int)color.y();
                int b = (int)color.z();
                this.lightPixels.setPixelRGBA(y, x, 0xFF000000 | b << 16 | g << 8 | r);
            }
        }
        this.updateLightTexture = true;
        this.lightTexture.upload();
    }
}
