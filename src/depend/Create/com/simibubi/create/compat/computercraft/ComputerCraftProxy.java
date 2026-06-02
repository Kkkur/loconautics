/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.compat.computercraft;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.FallbackComputerBehaviour;
import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import java.util.function.Function;

public class ComputerCraftProxy {
    private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> fallbackFactory;
    private static Function<SmartBlockEntity, ? extends AbstractComputerBehaviour> computerFactory;

    public static void register() {
        fallbackFactory = FallbackComputerBehaviour::new;
        Mods.COMPUTERCRAFT.executeIfInstalled(() -> ComputerCraftProxy::registerWithDependency);
    }

    private static void registerWithDependency() {
        computerFactory = ComputerBehaviour::new;
        ComputerBehaviour.registerItemDetailProviders();
    }

    public static AbstractComputerBehaviour behaviour(SmartBlockEntity sbe) {
        if (computerFactory == null) {
            return fallbackFactory.apply(sbe);
        }
        return computerFactory.apply(sbe);
    }
}
