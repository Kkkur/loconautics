/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.neoforge.common.SpecialPlantable
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.SpecialPlantable;

public record AllPotatoProjectileBlockHitActions.PlantCrop(Holder<Block> cropBlock) implements PotatoProjectileBlockHitAction
{
    public static final MapCodec<AllPotatoProjectileBlockHitActions.PlantCrop> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(AllPotatoProjectileBlockHitActions.PlantCrop::cropBlock)).apply((Applicative)instance, AllPotatoProjectileBlockHitActions.PlantCrop::new));

    public AllPotatoProjectileBlockHitActions.PlantCrop(Block cropBlock) {
        this((Holder<Block>)cropBlock.builtInRegistryHolder());
    }

    @Override
    public boolean execute(LevelAccessor level, ItemStack projectile, BlockHitResult ray) {
        Level l;
        if (level.isClientSide()) {
            return true;
        }
        BlockPos hitPos = ray.getBlockPos();
        if (level instanceof Level && !(l = (Level)level).isLoaded(hitPos)) {
            return true;
        }
        Direction face = ray.getDirection();
        if (face != Direction.UP) {
            return false;
        }
        BlockPos placePos = hitPos.relative(face);
        if (!level.getBlockState(placePos).canBeReplaced()) {
            return false;
        }
        Object object = this.cropBlock.value();
        if (!(object instanceof SpecialPlantable)) {
            return false;
        }
        SpecialPlantable specialPlantable = (SpecialPlantable)object;
        if (specialPlantable.canPlacePlantAtPosition(projectile, (LevelReader)level, placePos, null)) {
            specialPlantable.spawnPlantAtPosition(projectile, level, placePos, null);
        }
        return true;
    }

    @Override
    public MapCodec<? extends PotatoProjectileBlockHitAction> codec() {
        return CODEC;
    }
}
