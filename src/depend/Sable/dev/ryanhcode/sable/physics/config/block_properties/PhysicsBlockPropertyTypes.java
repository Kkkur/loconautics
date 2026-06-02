/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  foundry.veil.platform.registry.RegistrationProvider
 *  foundry.veil.platform.registry.RegistryObject
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.sable.physics.config.block_properties;

import com.mojang.serialization.Codec;
import dev.ryanhcode.sable.Sable;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class PhysicsBlockPropertyTypes {
    public static final ResourceKey<Registry<PhysicsBlockPropertyType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey((ResourceLocation)Sable.sablePath("physics_block_properties"));
    private static final RegistrationProvider<PhysicsBlockPropertyType<?>> VANILLA_PROVIDER = RegistrationProvider.get(REGISTRY_KEY, (String)"sable");
    private static final Registry<PhysicsBlockPropertyType<?>> REGISTRY = VANILLA_PROVIDER.asVanillaRegistry();
    public static final RegistryObject<PhysicsBlockPropertyType<Double>> MASS = PhysicsBlockPropertyTypes.register(Sable.sablePath("mass"), Codec.DOUBLE, 1.0);
    public static final RegistryObject<PhysicsBlockPropertyType<Vec3>> INERTIA = PhysicsBlockPropertyTypes.register(Sable.sablePath("inertia"), Vec3.CODEC, null);
    public static final RegistryObject<PhysicsBlockPropertyType<Double>> VOLUME = PhysicsBlockPropertyTypes.register(Sable.sablePath("volume"), Codec.DOUBLE, 1.0);
    public static final RegistryObject<PhysicsBlockPropertyType<Double>> RESTITUTION = PhysicsBlockPropertyTypes.register(Sable.sablePath("restitution"), Codec.DOUBLE, 0.0);
    public static final RegistryObject<PhysicsBlockPropertyType<Double>> FRICTION = PhysicsBlockPropertyTypes.register(Sable.sablePath("friction"), Codec.DOUBLE, 1.0);
    public static final RegistryObject<PhysicsBlockPropertyType<Boolean>> FRAGILE = PhysicsBlockPropertyTypes.register(Sable.sablePath("fragile"), Codec.BOOL, false);
    public static final RegistryObject<PhysicsBlockPropertyType<ResourceLocation>> FLOATING_MATERIAL = PhysicsBlockPropertyTypes.register(Sable.sablePath("floating_material"), ResourceLocation.CODEC, null);
    public static final RegistryObject<PhysicsBlockPropertyType<Double>> FLOATING_SCALE = PhysicsBlockPropertyTypes.register(Sable.sablePath("floating_scale"), Codec.DOUBLE, 1.0);

    public static void register() {
    }

    private static <T> RegistryObject<PhysicsBlockPropertyType<T>> register(ResourceLocation id, Codec<T> codec, T defaultValue) {
        if (REGISTRY.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate physics block property: %s".formatted(id));
        }
        return VANILLA_PROVIDER.register(id, () -> new PhysicsBlockPropertyType<Object>(REGISTRY.size(), codec, defaultValue));
    }

    public static int count() {
        return REGISTRY.size();
    }

    public static Codec<Object> getPropertyCodec(ResourceLocation id) {
        PhysicsBlockPropertyType property = (PhysicsBlockPropertyType)REGISTRY.get(id);
        if (property != null) {
            return property.codec;
        }
        throw new IllegalArgumentException("Unknown physics block property: %s".formatted(id));
    }

    public static PhysicsBlockPropertyType<?> getPropertyType(ResourceLocation id) {
        PhysicsBlockPropertyType property = (PhysicsBlockPropertyType)REGISTRY.get(id);
        if (property != null) {
            return property;
        }
        throw new IllegalArgumentException("Unknown physics block property: %s".formatted(id));
    }

    public record PhysicsBlockPropertyType<T>(int id, Codec<T> codec, T defaultValue) {
    }
}
