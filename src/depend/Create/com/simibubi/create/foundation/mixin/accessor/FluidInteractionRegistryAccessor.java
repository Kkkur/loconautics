/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry$InteractionInformation
 *  net.neoforged.neoforge.fluids.FluidType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import java.util.List;
import java.util.Map;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={FluidInteractionRegistry.class})
public interface FluidInteractionRegistryAccessor {
    @Accessor(value="INTERACTIONS", remap=false)
    public static Map<FluidType, List<FluidInteractionRegistry.InteractionInformation>> getInteractions() {
        throw new AssertionError();
    }
}
