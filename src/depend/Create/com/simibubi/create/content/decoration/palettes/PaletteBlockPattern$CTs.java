/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTType;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public static enum PaletteBlockPattern.CTs {
    PILLAR(AllCTTypes.RECTANGLE, s -> PaletteBlockPattern.toLocation(s, "pillar")),
    CAP(AllCTTypes.OMNIDIRECTIONAL, s -> PaletteBlockPattern.toLocation(s, "cap")),
    LAYERED(AllCTTypes.HORIZONTAL_KRYPPERS, s -> PaletteBlockPattern.toLocation(s, "layered"));

    public CTType type;
    private Function<String, ResourceLocation> srcFactory;
    private Function<String, ResourceLocation> targetFactory;

    private PaletteBlockPattern.CTs(CTType type, Function<String, ResourceLocation> factory) {
        this(type, factory, factory);
    }

    private PaletteBlockPattern.CTs(CTType type, Function<String, ResourceLocation> srcFactory, Function<String, ResourceLocation> targetFactory) {
        this.type = type;
        this.srcFactory = srcFactory;
        this.targetFactory = targetFactory;
    }
}
