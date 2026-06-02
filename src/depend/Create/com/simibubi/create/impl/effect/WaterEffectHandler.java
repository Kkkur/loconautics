/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.AbstractCandleBlock
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.impl.effect;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class WaterEffectHandler
implements OpenPipeEffectHandler {
    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        if (level.getGameTime() % 5L != 0L) {
            return;
        }
        List entities = level.getEntities((Entity)null, area, Entity::isOnFire);
        for (Entity entity : entities) {
            entity.clearFire();
        }
        BlockPos.betweenClosedStream((AABB)area).forEach(pos -> WaterEffectHandler.dowseFire(level, pos));
    }

    private static void dowseFire(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.FIRE)) {
            level.removeBlock(pos, false);
        } else if (AbstractCandleBlock.isLit((BlockState)state)) {
            AbstractCandleBlock.extinguish(null, (BlockState)state, (LevelAccessor)level, (BlockPos)pos);
        } else if (CampfireBlock.isLitCampfire((BlockState)state)) {
            level.levelEvent(1009, pos, 0);
            CampfireBlock.dowse(null, (LevelAccessor)level, (BlockPos)pos, (BlockState)state);
            level.setBlockAndUpdate(pos, (BlockState)state.setValue((Property)CampfireBlock.LIT, (Comparable)Boolean.valueOf(false)));
        }
    }
}
