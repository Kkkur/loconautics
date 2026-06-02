/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public record TrackMaterial.TrackModelHolder(PartialModel tie, PartialModel leftSegment, PartialModel rightSegment) {
    static final TrackMaterial.TrackModelHolder DEFAULT = new TrackMaterial.TrackModelHolder(AllPartialModels.TRACK_TIE, AllPartialModels.TRACK_SEGMENT_LEFT, AllPartialModels.TRACK_SEGMENT_RIGHT);
}
