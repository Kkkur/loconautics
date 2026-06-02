/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.UnmodifiableView
 */
package com.simibubi.create.content.trains.bogey;

import com.simibubi.create.Create;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

public final class BogeySizes {
    private static final Map<ResourceLocation, BogeySize> BOGEY_SIZES = new HashMap<ResourceLocation, BogeySize>();
    private static final List<BogeySize> SORTED_INCREASING = new ArrayList<BogeySize>();
    private static final List<BogeySize> SORTED_DECREASING = new ArrayList<BogeySize>();
    private static final @UnmodifiableView Map<ResourceLocation, BogeySize> BOGEY_SIZES_VIEW = Collections.unmodifiableMap(BOGEY_SIZES);
    private static final @UnmodifiableView List<BogeySize> SORTED_INCREASING_VIEW = Collections.unmodifiableList(SORTED_INCREASING);
    private static final @UnmodifiableView List<BogeySize> SORTED_DECREASING_VIEW = Collections.unmodifiableList(SORTED_DECREASING);
    public static final BogeySize SMALL = new BogeySize(Create.asResource("small"), 0.40625f);
    public static final BogeySize LARGE = new BogeySize(Create.asResource("large"), 0.78125f);

    private BogeySizes() {
    }

    public static void register(BogeySize size) {
        ResourceLocation id = size.id();
        if (BOGEY_SIZES.containsKey(id)) {
            throw new IllegalArgumentException();
        }
        BOGEY_SIZES.put(id, size);
        SORTED_INCREASING.add(size);
        SORTED_DECREASING.add(size);
        SORTED_INCREASING.sort(Comparator.comparing(BogeySize::wheelRadius));
        SORTED_DECREASING.sort(Comparator.comparing(BogeySize::wheelRadius).reversed());
    }

    public static @UnmodifiableView Map<ResourceLocation, BogeySize> all() {
        return BOGEY_SIZES_VIEW;
    }

    public static @UnmodifiableView List<BogeySize> allSortedIncreasing() {
        return SORTED_INCREASING_VIEW;
    }

    public static @UnmodifiableView List<BogeySize> allSortedDecreasing() {
        return SORTED_DECREASING_VIEW;
    }

    @ApiStatus.Internal
    public static void init() {
    }

    static {
        BogeySizes.register(SMALL);
        BogeySizes.register(LARGE);
    }

    public record BogeySize(ResourceLocation id, float wheelRadius) {
        public BogeySize nextBySize() {
            List<BogeySize> values = BogeySizes.allSortedIncreasing();
            int ordinal = values.indexOf(this);
            return values.get((ordinal + 1) % values.size());
        }
    }
}
