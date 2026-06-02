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
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.foundation.mixin.accessor.FallingBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public record AllPotatoProjectileBlockHitActions.PlaceBlockOnGround(Holder<Block> block) implements PotatoProjectileBlockHitAction
{
    public static final MapCodec<AllPotatoProjectileBlockHitActions.PlaceBlockOnGround> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(AllPotatoProjectileBlockHitActions.PlaceBlockOnGround::block)).apply((Applicative)instance, AllPotatoProjectileBlockHitActions.PlaceBlockOnGround::new));

    public AllPotatoProjectileBlockHitActions.PlaceBlockOnGround(Block block) {
        this((Holder<Block>)block.builtInRegistryHolder());
    }

    @Override
    public boolean execute(LevelAccessor levelAccessor, ItemStack projectile, BlockHitResult ray) {
        Level l;
        if (levelAccessor.isClientSide()) {
            return true;
        }
        BlockPos hitPos = ray.getBlockPos();
        if (levelAccessor instanceof Level && !(l = (Level)levelAccessor).isLoaded(hitPos)) {
            return true;
        }
        Direction face = ray.getDirection();
        BlockPos placePos = hitPos.relative(face);
        if (!levelAccessor.getBlockState(placePos).canBeReplaced()) {
            return false;
        }
        if (face == Direction.UP) {
            levelAccessor.setBlock(placePos, ((Block)this.block.value()).defaultBlockState(), 3);
        } else if (levelAccessor instanceof Level) {
            Level level = (Level)levelAccessor;
            double y = ray.getLocation().y - 0.5;
            if (!level.isEmptyBlock(placePos.above())) {
                y = Math.min(y, (double)placePos.getY());
            }
            if (!level.isEmptyBlock(placePos.below())) {
                y = Math.max(y, (double)placePos.getY());
            }
            FallingBlockEntity falling = FallingBlockEntityAccessor.create$callInit(level, (double)placePos.getX() + 0.5, y, (double)placePos.getZ() + 0.5, ((Block)this.block.value()).defaultBlockState());
            falling.time = 1;
            level.addFreshEntity((Entity)falling);
        }
        return true;
    }

    @Override
    public MapCodec<? extends PotatoProjectileBlockHitAction> codec() {
        return CODEC;
    }
}
