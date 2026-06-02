/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.api.registry;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.Create;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class CreateRegistries {
    public static final ResourceKey<Registry<ArmInteractionPointType>> ARM_INTERACTION_POINT_TYPE = CreateRegistries.key("arm_interaction_point_type");
    public static final ResourceKey<Registry<FanProcessingType>> FAN_PROCESSING_TYPE = CreateRegistries.key("fan_processing_type");
    public static final ResourceKey<Registry<ItemAttributeType>> ITEM_ATTRIBUTE_TYPE = CreateRegistries.key("item_attribute_type");
    public static final ResourceKey<Registry<DisplaySource>> DISPLAY_SOURCE = CreateRegistries.key("display_source");
    public static final ResourceKey<Registry<DisplayTarget>> DISPLAY_TARGET = CreateRegistries.key("display_target");
    public static final ResourceKey<Registry<MountedItemStorageType<?>>> MOUNTED_ITEM_STORAGE_TYPE = CreateRegistries.key("mounted_item_storage_type");
    public static final ResourceKey<Registry<MountedFluidStorageType<?>>> MOUNTED_FLUID_STORAGE_TYPE = CreateRegistries.key("mounted_fluid_storage_type");
    public static final ResourceKey<Registry<ContraptionType>> CONTRAPTION_TYPE = CreateRegistries.key("contraption_type");
    public static final ResourceKey<Registry<PackagePortTargetType>> PACKAGE_PORT_TARGET_TYPE = CreateRegistries.key("package_port_target_type");
    public static final ResourceKey<Registry<PotatoCannonProjectileType>> POTATO_PROJECTILE_TYPE = CreateRegistries.key("potato_projectile/type");
    public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileRenderMode>>> POTATO_PROJECTILE_RENDER_MODE = CreateRegistries.key("potato_projectile/render_mode");
    public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileEntityHitAction>>> POTATO_PROJECTILE_ENTITY_HIT_ACTION = CreateRegistries.key("potato_projectile/entity_hit_action");
    public static final ResourceKey<Registry<MapCodec<? extends PotatoProjectileBlockHitAction>>> POTATO_PROJECTILE_BLOCK_HIT_ACTION = CreateRegistries.key("potato_projectile/block_hit_action");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey((ResourceLocation)Create.asResource(name));
    }

    private CreateRegistries() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }
}
