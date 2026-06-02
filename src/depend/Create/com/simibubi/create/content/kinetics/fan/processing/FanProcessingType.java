/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface FanProcessingType {
    public boolean isValidAt(Level var1, BlockPos var2);

    public int getPriority();

    public boolean canProcess(ItemStack var1, Level var2);

    @Nullable
    public List<ItemStack> process(ItemStack var1, Level var2);

    public void spawnProcessingParticles(Level var1, Vec3 var2);

    public void morphAirFlow(AirFlowParticleAccess var1, RandomSource var2);

    public void affectEntity(Entity var1, Level var2);

    @Nullable
    public static FanProcessingType parse(String str) {
        return (FanProcessingType)CreateBuiltInRegistries.FAN_PROCESSING_TYPE.get(ResourceLocation.tryParse((String)str));
    }

    @Nullable
    public static FanProcessingType getAt(Level level, BlockPos pos) {
        for (FanProcessingType type : FanProcessingTypeRegistry.SORTED_TYPES_VIEW) {
            if (!type.isValidAt(level, pos)) continue;
            return type;
        }
        return null;
    }

    public static interface AirFlowParticleAccess {
        public void setColor(int var1);

        public void setAlpha(float var1);

        public void spawnExtraParticle(ParticleOptions var1, float var2);
    }
}
