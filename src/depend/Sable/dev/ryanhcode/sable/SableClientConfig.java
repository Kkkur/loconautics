/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$BooleanValue
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  net.neoforged.neoforge.common.ModConfigSpec$DoubleValue
 *  net.neoforged.neoforge.common.ModConfigSpec$EnumValue
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixin.config.GameRendererAccessor;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.render.dynamic_shade.SableDynamicDirectionalShading;
import dev.ryanhcode.sable.render.sky_light_shadow.SableSkyLightShadows;
import dev.ryanhcode.sable.render.water_occlusion.WaterOcclusionRenderer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

public final class SableClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue SUB_LEVEL_DYNAMIC_SHADING;
    public static final ModConfigSpec.BooleanValue SUB_LEVEL_WATER_OCCLUSION;
    public static final ModConfigSpec.BooleanValue SUB_LEVEL_SKYLIGHT_SHADOWS;
    public static final ModConfigSpec.DoubleValue INTERPOLATION_DELAY;
    public static final ModConfigSpec.EnumValue<SubLevelRenderer.SelectedRenderer> SELECTED_RENDERER;
    public static final ModConfigSpec.DoubleValue ZOOM_SENSITIVITY;

    @ApiStatus.Internal
    public static void onUpdate(boolean notify) {
        boolean reloadShaders = false;
        boolean reloadChunks = false;
        if (SableDynamicDirectionalShading.isEnabled() != SUB_LEVEL_DYNAMIC_SHADING.getAsBoolean()) {
            SableDynamicDirectionalShading.setIsEnabled(SUB_LEVEL_DYNAMIC_SHADING.getAsBoolean());
            reloadShaders = true;
            reloadChunks = true;
        }
        if (SableSkyLightShadows.isEnabled() != SUB_LEVEL_SKYLIGHT_SHADOWS.getAsBoolean()) {
            SableSkyLightShadows.setIsEnabled(SUB_LEVEL_SKYLIGHT_SHADOWS.getAsBoolean());
            reloadShaders = true;
        }
        if (WaterOcclusionRenderer.isEnabled() != SUB_LEVEL_WATER_OCCLUSION.getAsBoolean()) {
            WaterOcclusionRenderer.setIsEnabled(SUB_LEVEL_WATER_OCCLUSION.getAsBoolean());
            reloadShaders = true;
        }
        Minecraft.getInstance().execute(() -> SubLevelRenderer.setImpl((SubLevelRenderer.SelectedRenderer)((Object)((Object)SELECTED_RENDERER.get()))));
        if (notify) {
            if (reloadShaders) {
                VeilRenderSystem.renderer().getVanillaShaderCompiler().reload(((GameRendererAccessor)Minecraft.getInstance().gameRenderer).getShaders().values());
            }
            if (reloadChunks) {
                Minecraft.getInstance().execute(() -> {
                    VeilRenderSystem.rebuildChunks();
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null) {
                        SubLevelContainer plotContainer = ((SubLevelContainerHolder)level).sable$getPlotContainer();
                        for (SubLevel subLevel : plotContainer.getAllSubLevels()) {
                            ((ClientSubLevel)subLevel).getRenderData().rebuild();
                        }
                    }
                });
            }
        }
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        SUB_LEVEL_DYNAMIC_SHADING = builder.comment("Whether sub-levels should apply block shading dynamically").define("sub_level_dynamic_shading", true);
        SUB_LEVEL_WATER_OCCLUSION = builder.comment("Whether sub-levels can occlude the water surface").define("sub_level_water_occlusion", true);
        SUB_LEVEL_SKYLIGHT_SHADOWS = builder.comment("Whether sub-levels should cast a shadow on the world").define("sub_level_skylight_shadows", false);
        INTERPOLATION_DELAY = builder.comment("The distance back in game-ticks that the snapshot interpolation should operate").defineInRange("sub_level_snapshot_interpolation_delay_ticks", 1.5, 0.0, 100.0);
        SELECTED_RENDERER = builder.comment("The renderer to use for sub-levels").defineEnum("sub_level_renderer", (Enum)SubLevelRenderer.DEFAULT, (Enum[])((SubLevelRenderer.SelectedRenderer[])Arrays.stream(SubLevelRenderer.SelectedRenderer.values()).filter(SubLevelRenderer.SelectedRenderer::isSupported).toArray(SubLevelRenderer.SelectedRenderer[]::new)));
        ZOOM_SENSITIVITY = builder.comment("The zoom sensitivity for sub-level camera types").defineInRange("sub_level_zoom_sensitivity", 0.2, 0.0, 100.0);
        SPEC = builder.build();
    }
}
