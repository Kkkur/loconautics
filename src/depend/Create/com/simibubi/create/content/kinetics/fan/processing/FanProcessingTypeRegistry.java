/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ReferenceArrayList
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.UnmodifiableView
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

public class FanProcessingTypeRegistry {
    private static final List<FanProcessingType> SORTED_TYPES = new ReferenceArrayList();
    public static final @UnmodifiableView List<FanProcessingType> SORTED_TYPES_VIEW = Collections.unmodifiableList(SORTED_TYPES);

    @ApiStatus.Internal
    public static void init() {
        SORTED_TYPES.clear();
        CreateBuiltInRegistries.FAN_PROCESSING_TYPE.forEach(SORTED_TYPES::add);
        SORTED_TYPES.sort((t1, t2) -> t2.getPriority() - t1.getPriority());
    }
}
