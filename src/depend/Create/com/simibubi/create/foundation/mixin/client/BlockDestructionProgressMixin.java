/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.BlockDestructionProgress
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package com.simibubi.create.foundation.mixin.client;

import com.simibubi.create.foundation.block.render.BlockDestructionProgressExtension;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={BlockDestructionProgress.class})
public class BlockDestructionProgressMixin
implements BlockDestructionProgressExtension {
    @Unique
    private Set<BlockPos> create$extraPositions;

    @Override
    public Set<BlockPos> create$getExtraPositions() {
        return this.create$extraPositions;
    }

    @Override
    public void create$setExtraPositions(Set<BlockPos> positions) {
        this.create$extraPositions = positions;
    }
}
