/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.CTType;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CTTypeRegistry {
    private static final Map<ResourceLocation, CTType> TYPES = new HashMap<ResourceLocation, CTType>();

    public static void register(CTType type) {
        ResourceLocation id = type.getId();
        if (TYPES.containsKey(id)) {
            throw new IllegalArgumentException("Tried to override CTType registration for id '" + String.valueOf(id) + "'. This is not supported!");
        }
        TYPES.put(id, type);
    }

    @Nullable
    public static CTType get(ResourceLocation id) {
        return TYPES.get(id);
    }
}
