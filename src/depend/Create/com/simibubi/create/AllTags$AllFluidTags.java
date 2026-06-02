/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 */
package com.simibubi.create;

import com.simibubi.create.AllTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public static enum AllTags.AllFluidTags {
    BOTTOMLESS_ALLOW(AllTags.NameSpace.MOD, "bottomless/allow"),
    BOTTOMLESS_DENY(AllTags.NameSpace.MOD, "bottomless/deny"),
    FAN_PROCESSING_CATALYSTS_BLASTING(AllTags.NameSpace.MOD, "fan_processing_catalysts/blasting"),
    FAN_PROCESSING_CATALYSTS_HAUNTING(AllTags.NameSpace.MOD, "fan_processing_catalysts/haunting"),
    FAN_PROCESSING_CATALYSTS_SMOKING(AllTags.NameSpace.MOD, "fan_processing_catalysts/smoking"),
    FAN_PROCESSING_CATALYSTS_SPLASHING(AllTags.NameSpace.MOD, "fan_processing_catalysts/splashing"),
    TEA(AllTags.NameSpace.COMMON),
    CHOCOLATE(AllTags.NameSpace.COMMON),
    CREOSOTE(AllTags.NameSpace.COMMON);

    public final TagKey<Fluid> tag;

    private AllTags.AllFluidTags() {
        this(AllTags.NameSpace.MOD);
    }

    private AllTags.AllFluidTags(AllTags.NameSpace namespace) {
        this(namespace, null);
    }

    private AllTags.AllFluidTags(AllTags.NameSpace namespace, String pathOverride) {
        this.tag = TagKey.create((ResourceKey)Registries.FLUID, (ResourceLocation)namespace.id(this, pathOverride));
    }

    public boolean matches(Fluid fluid) {
        return fluid.is(this.tag);
    }

    public boolean matches(FluidState state) {
        return state.is(this.tag);
    }
}
