/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.platform.registry.RegistrationProvider
 *  foundry.veil.platform.registry.RegistryObject
 *  net.minecraft.core.Registry
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.ryanhcode.sable.api.physics.force;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ForceGroups {
    public static final ResourceKey<Registry<ForceGroup>> REGISTRY_KEY = ResourceKey.createRegistryKey((ResourceLocation)Sable.sablePath("force_groups"));
    private static final RegistrationProvider<ForceGroup> VANILLA_PROVIDER = RegistrationProvider.get(REGISTRY_KEY, (String)"sable");
    public static final Registry<ForceGroup> REGISTRY = VANILLA_PROVIDER.asVanillaRegistry();
    public static final RegistryObject<ForceGroup> GRAVITY = VANILLA_PROVIDER.register(Sable.sablePath("gravity"), () -> new ForceGroup((Component)Component.translatable((String)"force_group.sable.gravity"), null, 2190933, false));
    public static final RegistryObject<ForceGroup> DRAG = VANILLA_PROVIDER.register(Sable.sablePath("drag"), () -> new ForceGroup((Component)Component.translatable((String)"force_group.sable.drag"), null, 8605489, false));
    public static final RegistryObject<ForceGroup> LEVITATION = VANILLA_PROVIDER.register(Sable.sablePath("levitation"), () -> new ForceGroup((Component)Component.translatable((String)"force_group.sable.levitation"), null, 7554176, true));
    public static final RegistryObject<ForceGroup> BALLOON_LIFT = VANILLA_PROVIDER.register(Sable.sablePath("balloon_lift"), () -> new ForceGroup((Component)Component.translatable((String)"force_group.sable.balloon_lift"), null, 13788222, true));
    public static final RegistryObject<ForceGroup> PROPULSION = VANILLA_PROVIDER.register(Sable.sablePath("propulsion"), () -> new ForceGroup((Component)Component.translatable((String)"force_group.sable.propulsion"), null, 5930143, true));
    public static final RegistryObject<ForceGroup> LIFT = VANILLA_PROVIDER.register(Sable.sablePath("lift"), () -> new ForceGroup((Component)Component.translatable((String)"force_group.sable.lift"), null, 9221830, true));
    public static final RegistryObject<ForceGroup> MAGNETIC_FORCE = VANILLA_PROVIDER.register(Sable.sablePath("magnetic_force"), () -> new ForceGroup((Component)Component.translatable((String)"force_group.sable.magnetic_force"), null, 14701379, false));

    public static void register() {
    }

    public static int count() {
        return REGISTRY.size();
    }
}
