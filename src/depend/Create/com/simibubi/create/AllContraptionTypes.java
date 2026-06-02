/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create;

import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.ClockworkContraption;
import com.simibubi.create.content.contraptions.bearing.StabilizedContraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.gantry.GantryContraption;
import com.simibubi.create.content.contraptions.mounted.MountedContraption;
import com.simibubi.create.content.contraptions.piston.PistonContraption;
import com.simibubi.create.content.contraptions.pulley.PulleyContraption;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class AllContraptionTypes {
    public static final Map<String, ContraptionType> BY_LEGACY_NAME = new HashMap<String, ContraptionType>();
    public static final Holder.Reference<ContraptionType> PISTON = AllContraptionTypes.register("piston", PistonContraption::new);
    public static final Holder.Reference<ContraptionType> BEARING = AllContraptionTypes.register("bearing", BearingContraption::new);
    public static final Holder.Reference<ContraptionType> PULLEY = AllContraptionTypes.register("pulley", PulleyContraption::new);
    public static final Holder.Reference<ContraptionType> CLOCKWORK = AllContraptionTypes.register("clockwork", ClockworkContraption::new);
    public static final Holder.Reference<ContraptionType> MOUNTED = AllContraptionTypes.register("mounted", MountedContraption::new);
    public static final Holder.Reference<ContraptionType> STABILIZED = AllContraptionTypes.register("stabilized", StabilizedContraption::new);
    public static final Holder.Reference<ContraptionType> GANTRY = AllContraptionTypes.register("gantry", GantryContraption::new);
    public static final Holder.Reference<ContraptionType> CARRIAGE = AllContraptionTypes.register("carriage", CarriageContraption::new);
    public static final Holder.Reference<ContraptionType> ELEVATOR = AllContraptionTypes.register("elevator", ElevatorContraption::new);

    private static Holder.Reference<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);
        BY_LEGACY_NAME.put(name, type);
        return Registry.registerForHolder(CreateBuiltInRegistries.CONTRAPTION_TYPE, (ResourceLocation)Create.asResource(name), (Object)type);
    }

    public static void init() {
    }
}
