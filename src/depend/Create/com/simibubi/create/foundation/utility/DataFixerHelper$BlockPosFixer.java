/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.serialization.Dynamic
 */
package com.simibubi.create.foundation.utility;

import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;

public record DataFixerHelper.BlockPosFixer(DSL.TypeReference reference, String id, Map<String, String> renames, Function<Dynamic<?>, Dynamic<?>> customFixer) {
}
