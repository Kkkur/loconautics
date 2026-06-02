/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class TranslatingContraption
extends Contraption {
    protected Set<BlockPos> cachedColliders;
    protected Direction cachedColliderDirection;

    public Set<BlockPos> getOrCreateColliders(Level world, Direction movementDirection) {
        if (this.getBlocks() == null) {
            return Collections.emptySet();
        }
        if (this.cachedColliders == null || this.cachedColliderDirection != movementDirection) {
            this.cachedColliderDirection = movementDirection;
            this.cachedColliders = this.createColliders(world, movementDirection);
        }
        return this.cachedColliders;
    }

    public Set<BlockPos> createColliders(Level world, Direction movementDirection) {
        HashSet<BlockPos> colliders = new HashSet<BlockPos>();
        for (StructureTemplate.StructureBlockInfo info : this.getBlocks().values()) {
            BlockPos offsetPos = info.pos().relative(movementDirection);
            if (info.state().getCollisionShape((BlockGetter)world, offsetPos).isEmpty() || this.getBlocks().containsKey(offsetPos) && !this.getBlocks().get(offsetPos).state().getCollisionShape((BlockGetter)world, offsetPos).isEmpty()) continue;
            colliders.add(info.pos());
        }
        return colliders;
    }

    @Override
    public void removeBlocksFromWorld(Level world, BlockPos offset) {
        int count = this.blocks.size();
        super.removeBlocksFromWorld(world, offset);
        if (count != this.blocks.size()) {
            this.cachedColliders = null;
        }
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return (Boolean)AllConfigs.server().kinetics.stabiliseStableContraptions.get();
    }
}
