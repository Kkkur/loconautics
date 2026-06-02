/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.ContraptionType
 *  com.simibubi.create.api.registry.CreateBuiltInRegistries
 *  com.simibubi.create.content.contraptions.Contraption
 *  foundry.veil.platform.registry.RegistrationProvider
 *  foundry.veil.platform.registry.RegistryObject
 *  net.minecraft.core.Registry
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import dev.ryanhcode.offroad.content.contraptions.borehead_contraption.BoreheadBearingContraption;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import java.util.function.Supplier;
import net.minecraft.core.Registry;

public class OffroadContraptionTypes {
    private static final RegistrationProvider<ContraptionType> OFFROAD_CONTRAPTION_TYPES_REGISTRY = RegistrationProvider.get((Registry)CreateBuiltInRegistries.CONTRAPTION_TYPE, (String)"offroad");
    public static final RegistryObject<ContraptionType> BOREHEAD_CONTRAPTION_TYPE = OffroadContraptionTypes.register("borehead_contraption", BoreheadBearingContraption::new);

    private static RegistryObject<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        return OFFROAD_CONTRAPTION_TYPES_REGISTRY.register(name, () -> new ContraptionType(factory));
    }

    public static void init() {
    }
}
