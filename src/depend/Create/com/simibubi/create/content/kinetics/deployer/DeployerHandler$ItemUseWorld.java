/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.deployer;

import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

private static final class DeployerHandler.ItemUseWorld
extends WrappedLevel
implements ServerLevelAccessor {
    private final Direction face;
    private final BlockPos pos;
    boolean rayMode = false;

    private DeployerHandler.ItemUseWorld(ServerLevel level, Direction face, BlockPos pos) {
        super((Level)level);
        this.face = face;
        this.pos = pos;
    }

    public ServerLevel getLevel() {
        return (ServerLevel)this.level;
    }

    public BlockHitResult clip(ClipContext context) {
        this.rayMode = true;
        BlockHitResult rayTraceBlocks = super.clip(context);
        this.rayMode = false;
        return rayTraceBlocks;
    }

    public BlockState getBlockState(BlockPos position) {
        if (this.rayMode && (this.pos.relative(this.face.getOpposite(), 3).equals((Object)position) || this.pos.relative(this.face.getOpposite(), 1).equals((Object)position))) {
            return Blocks.BEDROCK.defaultBlockState();
        }
        return this.level.getBlockState(position);
    }
}
