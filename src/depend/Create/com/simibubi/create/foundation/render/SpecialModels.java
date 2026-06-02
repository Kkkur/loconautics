/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.material.CardinalLightingMode
 *  dev.engine_room.flywheel.api.material.LightShader
 *  dev.engine_room.flywheel.api.material.Material
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.lib.material.LightShaders
 *  dev.engine_room.flywheel.lib.material.SimpleMaterial
 *  dev.engine_room.flywheel.lib.model.ModelUtil
 *  dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.util.RendererReloadCache
 *  net.minecraft.client.renderer.RenderType
 */
package com.simibubi.create.foundation.render;

import dev.engine_room.flywheel.api.material.CardinalLightingMode;
import dev.engine_room.flywheel.api.material.LightShader;
import dev.engine_room.flywheel.api.material.Material;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.lib.material.LightShaders;
import dev.engine_room.flywheel.lib.material.SimpleMaterial;
import dev.engine_room.flywheel.lib.model.ModelUtil;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import net.minecraft.client.renderer.RenderType;

public class SpecialModels {
    private static final RendererReloadCache<Key, Model> FLAT = new RendererReloadCache(it -> new BakedModelBuilder(it.partial.get()).materialFunc((renderType, shaded, ao) -> {
        Material material = ModelUtil.getMaterial((RenderType)renderType, (boolean)shaded, (boolean)ao);
        if (material == null) {
            return null;
        }
        return SimpleMaterial.builderOf((Material)material).light(it.light).cardinalLightingMode(shaded ? it.cardinalLightingMode : CardinalLightingMode.OFF).build();
    }).build());

    public static Model flatLit(PartialModel partial) {
        return (Model)FLAT.get((Object)new Key(partial, LightShaders.FLAT, CardinalLightingMode.ENTITY));
    }

    public static Model smoothLit(PartialModel partial) {
        return (Model)FLAT.get((Object)new Key(partial, LightShaders.SMOOTH, CardinalLightingMode.ENTITY));
    }

    public static Model flatChunk(PartialModel partial) {
        return (Model)FLAT.get((Object)new Key(partial, LightShaders.FLAT, CardinalLightingMode.CHUNK));
    }

    public static Model chunkDiffuse(PartialModel partial) {
        return (Model)FLAT.get((Object)new Key(partial, LightShaders.SMOOTH_WHEN_EMBEDDED, CardinalLightingMode.CHUNK));
    }

    private record Key(PartialModel partial, LightShader light, CardinalLightingMode cardinalLightingMode) {
    }
}
