/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.material.CardinalLightingMode
 *  dev.engine_room.flywheel.api.material.LightShader
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 */
package com.simibubi.create.foundation.render;

import dev.engine_room.flywheel.api.material.CardinalLightingMode;
import dev.engine_room.flywheel.api.material.LightShader;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

private record SpecialModels.Key(PartialModel partial, LightShader light, CardinalLightingMode cardinalLightingMode) {
}
