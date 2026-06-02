/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption;

import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

public final class ContraptionType {
    public final Supplier<? extends Contraption> factory;
    public final Holder.Reference<ContraptionType> holder = CreateBuiltInRegistries.CONTRAPTION_TYPE.createIntrusiveHolder((Object)this);

    public ContraptionType(Supplier<? extends Contraption> factory) {
        this.factory = factory;
    }

    public boolean is(TagKey<ContraptionType> tag) {
        return this.holder.is(tag);
    }

    @Nullable
    public static Contraption fromType(String typeId) {
        ContraptionType legacy = AllContraptionTypes.BY_LEGACY_NAME.get(typeId);
        if (legacy != null) {
            return legacy.factory.get();
        }
        ResourceLocation id = ResourceLocation.tryParse((String)typeId);
        ContraptionType type = (ContraptionType)CreateBuiltInRegistries.CONTRAPTION_TYPE.get(id);
        return type == null ? null : type.factory.get();
    }
}
