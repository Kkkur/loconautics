/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public static abstract class ValueBoxTransform.Dual
extends ValueBoxTransform {
    protected boolean first;

    public ValueBoxTransform.Dual(boolean first) {
        this.first = first;
    }

    public boolean isFirst() {
        return this.first;
    }

    public static Pair<ValueBoxTransform, ValueBoxTransform> makeSlots(Function<Boolean, ? extends ValueBoxTransform.Dual> factory) {
        return Pair.of((Object)factory.apply(true), (Object)factory.apply(false));
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = this.getLocalOffset(level, pos, state);
        if (offset == null) {
            return false;
        }
        return localHit.distanceTo(offset) < (double)(this.scale / 3.5f);
    }
}
