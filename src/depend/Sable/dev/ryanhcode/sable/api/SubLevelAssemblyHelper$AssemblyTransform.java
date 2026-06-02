/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.sable.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public static class SubLevelAssemblyHelper.AssemblyTransform {
    private final BlockPos anchorPos;
    private final BlockPos resultingAnchorPos;
    private final int angle;
    private final Rotation rotation;
    private final ServerLevel resultingLevel;

    public SubLevelAssemblyHelper.AssemblyTransform(BlockPos anchorPos, BlockPos resultingAnchorPos, int angle, Rotation rotation, ServerLevel resultingLevel) {
        this.anchorPos = anchorPos;
        this.resultingAnchorPos = resultingAnchorPos;
        this.angle = angle;
        this.rotation = rotation;
        this.resultingLevel = resultingLevel;
    }

    public Vec3 apply(Vec3 pos) {
        pos = pos.subtract(this.anchorPos.getCenter()).yRot((float)((double)this.angle * Math.PI / 2.0)).add(this.resultingAnchorPos.getCenter());
        return pos;
    }

    public BlockPos apply(BlockPos pos) {
        return BlockPos.containing((Position)this.apply(pos.getCenter()));
    }

    public BlockState apply(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof BellBlock) {
            if (state.getValue((Property)BlockStateProperties.BELL_ATTACHMENT) == BellAttachType.DOUBLE_WALL) {
                state = (BlockState)state.setValue((Property)BlockStateProperties.BELL_ATTACHMENT, (Comparable)BellAttachType.SINGLE_WALL);
            }
            return (BlockState)state.setValue((Property)BellBlock.FACING, (Comparable)this.rotation.rotate((Direction)state.getValue((Property)BellBlock.FACING)));
        }
        return state.rotate(this.rotation);
    }

    public ServerLevel getLevel() {
        return this.resultingLevel;
    }

    public Rotation getRotation() {
        return this.rotation;
    }
}
