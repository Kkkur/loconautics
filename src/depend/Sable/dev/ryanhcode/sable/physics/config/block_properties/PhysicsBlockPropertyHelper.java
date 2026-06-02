/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.physics.config.block_properties;

import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import dev.ryanhcode.sable.physics.config.FloatingBlockMaterialDataHandler;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PhysicsBlockPropertyHelper {
    public static double getMass(BlockGetter level, BlockPos pos, BlockState state) {
        boolean solid = VoxelNeighborhoodState.isSolid(level, pos, state);
        if (!solid) {
            return 0.0;
        }
        return (Double)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.MASS.get());
    }

    @Nullable
    public static Vec3 getInertia(BlockGetter level, BlockPos pos, BlockState state) {
        boolean solid = VoxelNeighborhoodState.isSolid(level, pos, state);
        if (!solid) {
            return null;
        }
        return (Vec3)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.INERTIA.get());
    }

    public static double getFriction(BlockState state) {
        return (Double)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FRICTION.get());
    }

    public static double getVolume(BlockState state) {
        return (Double)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.VOLUME.get());
    }

    public static double getRestitution(BlockState state) {
        return (Double)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.RESTITUTION.get());
    }

    public static double getFloatingScale(BlockState state) {
        return (Double)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FLOATING_SCALE.get());
    }

    public static FloatingBlockMaterial getFloatingMaterial(BlockState state) {
        ResourceLocation location = (ResourceLocation)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FLOATING_MATERIAL.get());
        if (location == null) {
            return null;
        }
        return FloatingBlockMaterialDataHandler.allMaterials.get(location);
    }
}
