/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.redstone.link;

import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public static class LinkBehaviour.SlotPositioning {
    Function<BlockState, Pair<Vec3, Vec3>> offsets;
    Function<BlockState, Vec3> rotation;
    float scale;

    public LinkBehaviour.SlotPositioning(Function<BlockState, Pair<Vec3, Vec3>> offsetsForState, Function<BlockState, Vec3> rotationForState) {
        this.offsets = offsetsForState;
        this.rotation = rotationForState;
        this.scale = 1.0f;
    }

    public LinkBehaviour.SlotPositioning scale(float scale) {
        this.scale = scale;
        return this;
    }
}
