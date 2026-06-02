/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 */
package com.simibubi.create;

import com.simibubi.create.AllTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public static enum AllTags.AllEntityTags {
    BLAZE_BURNER_CAPTURABLE,
    IGNORE_SEAT;

    public final TagKey<EntityType<?>> tag;

    private AllTags.AllEntityTags() {
        this(AllTags.NameSpace.MOD);
    }

    private AllTags.AllEntityTags(AllTags.NameSpace namespace) {
        this(namespace, null);
    }

    private AllTags.AllEntityTags(AllTags.NameSpace namespace, String pathOverride) {
        this.tag = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)namespace.id(this, pathOverride));
    }

    public boolean matches(EntityType<?> type) {
        return type.is(this.tag);
    }

    public boolean matches(Entity entity) {
        return this.matches(entity.getType());
    }
}
