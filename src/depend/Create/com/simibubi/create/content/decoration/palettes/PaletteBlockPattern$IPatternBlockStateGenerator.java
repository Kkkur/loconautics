/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import java.util.function.Function;

@FunctionalInterface
static interface PaletteBlockPattern.IPatternBlockStateGenerator
extends Function<PaletteBlockPattern, Function<String, PaletteBlockPattern.IBlockStateProvider>> {
}
