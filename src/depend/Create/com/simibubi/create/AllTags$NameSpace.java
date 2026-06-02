/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create;

import net.createmod.catnip.lang.Lang;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public static enum AllTags.NameSpace {
    MOD("create"),
    COMMON("c"),
    TIC("tconstruct"),
    QUARK("quark"),
    GS("galosphere"),
    CURIOS("curios");

    public final String id;

    private AllTags.NameSpace(String id) {
        this.id = id;
    }

    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)this.id, (String)path);
    }

    public ResourceLocation id(Enum<?> entry, @Nullable String pathOverride) {
        return this.id(pathOverride != null ? pathOverride : Lang.asId((String)entry.name()));
    }
}
