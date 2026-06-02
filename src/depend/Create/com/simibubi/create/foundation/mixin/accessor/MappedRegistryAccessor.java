/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2IntMap
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.MappedRegistry
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={MappedRegistry.class})
public interface MappedRegistryAccessor<T> {
    @Accessor
    public Reference2IntMap<T> getToId();

    @Accessor
    public Map<T, Holder.Reference<T>> getByValue();
}
