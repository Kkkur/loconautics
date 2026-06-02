/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

static class EdgeInteractionRenderer.EdgeValueBoxTransform
extends ValueBoxTransform.Sided {
    private Vec3 add;

    public EdgeInteractionRenderer.EdgeValueBoxTransform(Vec3 add) {
        this.add = add;
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        return this.add;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        super.rotate(level, pos, state, ms);
    }
}
