/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 */
package com.simibubi.create;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public static enum AllTags.AllContraptionTypeTags {
    OPENS_CONTROLS,
    REQUIRES_VEHICLE_FOR_RENDER;

    public final TagKey<ContraptionType> tag;

    private AllTags.AllContraptionTypeTags() {
        this(AllTags.NameSpace.MOD);
    }

    private AllTags.AllContraptionTypeTags(AllTags.NameSpace namespace) {
        this(namespace, null);
    }

    private AllTags.AllContraptionTypeTags(AllTags.NameSpace namespace, String pathOverride) {
        this.tag = TagKey.create(CreateRegistries.CONTRAPTION_TYPE, (ResourceLocation)namespace.id(this, pathOverride));
    }

    public boolean matches(ContraptionType type) {
        return type.is(this.tag);
    }
}
