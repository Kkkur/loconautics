/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConnectedTextureBehaviour {
    @Nullable
    public CTSpriteShiftEntry getShift(BlockState state, RandomSource rand, Direction direction, @NotNull TextureAtlasSprite sprite) {
        return this.getShift(state, direction, sprite);
    }

    @Nullable
    public abstract CTSpriteShiftEntry getShift(BlockState var1, Direction var2, @NotNull TextureAtlasSprite var3);

    @Nullable
    public abstract CTType getDataType(BlockAndTintGetter var1, BlockPos var2, BlockState var3, Direction var4);

    public boolean buildContextForOccludedDirections() {
        return false;
    }

    protected boolean isBeingBlocked(BlockState state, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        BlockPos blockingPos = otherPos.relative(face);
        BlockState blockState = reader.getBlockState(pos);
        BlockState blockingState = reader.getBlockState(blockingPos);
        if (!Block.isFaceFull((VoxelShape)blockingState.getShape((BlockGetter)reader, blockingPos), (Direction)face.getOpposite())) {
            return false;
        }
        if (face.getAxis().choose(pos.getX(), pos.getY(), pos.getZ()) != face.getAxis().choose(otherPos.getX(), otherPos.getY(), otherPos.getZ())) {
            return false;
        }
        return this.connectsTo(state, this.getCTBlockState(reader, blockState, face.getOpposite(), pos.relative(face), blockingPos), reader, pos, blockingPos, face);
    }

    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face, Direction primaryOffset, Direction secondaryOffset) {
        return this.connectsTo(state, other, reader, pos, otherPos, face);
    }

    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return !this.isBeingBlocked(state, reader, pos, otherPos, face) && state.getBlock() == other.getBlock();
    }

    private boolean testConnection(BlockAndTintGetter reader, BlockPos currentPos, BlockState connectiveCurrentState, Direction textureSide, Direction horizontal, Direction vertical, int sh, int sv) {
        BlockState trueCurrentState = reader.getBlockState(currentPos);
        BlockPos targetPos = currentPos.relative(horizontal, sh).relative(vertical, sv);
        BlockState connectiveTargetState = this.getCTBlockState(reader, trueCurrentState, textureSide, currentPos, targetPos);
        return this.connectsTo(connectiveCurrentState, connectiveTargetState, reader, currentPos, targetPos, textureSide, sh == 0 ? null : (sh == -1 ? horizontal.getOpposite() : horizontal), sv == 0 ? null : (sv == -1 ? vertical.getOpposite() : vertical));
    }

    public BlockState getCTBlockState(BlockAndTintGetter reader, BlockState reference, Direction face, BlockPos fromPos, BlockPos toPos) {
        BlockState blockState = reader.getBlockState(toPos);
        return blockState.getAppearance(reader, toPos, face, reference, fromPos);
    }

    protected boolean reverseUVs(BlockState state, Direction face) {
        return false;
    }

    protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
        return this.reverseUVs(state, face);
    }

    protected boolean reverseUVsVertically(BlockState state, Direction face) {
        return this.reverseUVs(state, face);
    }

    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis axis = face.getAxis();
        return axis.isHorizontal() ? Direction.UP : Direction.NORTH;
    }

    protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis axis = face.getAxis();
        return axis == Direction.Axis.X ? Direction.SOUTH : Direction.WEST;
    }

    public CTContext buildContext(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face, ContextRequirement requirement) {
        boolean positive = face.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        Direction h = this.getRightDirection(reader, pos, state, face);
        Direction v = this.getUpDirection(reader, pos, state, face);
        Direction direction = h = positive ? h.getOpposite() : h;
        if (face == Direction.DOWN) {
            v = v.getOpposite();
            h = h.getOpposite();
        }
        Direction horizontal = h;
        Direction vertical = v;
        boolean flipH = this.reverseUVsHorizontally(state, face);
        boolean flipV = this.reverseUVsVertically(state, face);
        int sh = flipH ? -1 : 1;
        int sv = flipV ? -1 : 1;
        CTContext context = new CTContext();
        if (requirement.up) {
            context.up = this.testConnection(reader, pos, state, face, horizontal, vertical, 0, sv);
        }
        if (requirement.down) {
            context.down = this.testConnection(reader, pos, state, face, horizontal, vertical, 0, -sv);
        }
        if (requirement.left) {
            context.left = this.testConnection(reader, pos, state, face, horizontal, vertical, -sh, 0);
        }
        if (requirement.right) {
            context.right = this.testConnection(reader, pos, state, face, horizontal, vertical, sh, 0);
        }
        if (requirement.topLeft) {
            boolean bl = context.topLeft = context.up && context.left && this.testConnection(reader, pos, state, face, horizontal, vertical, -sh, sv);
        }
        if (requirement.topRight) {
            boolean bl = context.topRight = context.up && context.right && this.testConnection(reader, pos, state, face, horizontal, vertical, sh, sv);
        }
        if (requirement.bottomLeft) {
            boolean bl = context.bottomLeft = context.down && context.left && this.testConnection(reader, pos, state, face, horizontal, vertical, -sh, -sv);
        }
        if (requirement.bottomRight) {
            context.bottomRight = context.down && context.right && this.testConnection(reader, pos, state, face, horizontal, vertical, sh, -sv);
        }
        return context;
    }

    public static class CTContext {
        public static final CTContext EMPTY = new CTContext();
        public boolean up;
        public boolean down;
        public boolean left;
        public boolean right;
        public boolean topLeft;
        public boolean topRight;
        public boolean bottomLeft;
        public boolean bottomRight;
    }

    public static class ContextRequirement {
        public final boolean up;
        public final boolean down;
        public final boolean left;
        public final boolean right;
        public final boolean topLeft;
        public final boolean topRight;
        public final boolean bottomLeft;
        public final boolean bottomRight;

        public ContextRequirement(boolean up, boolean down, boolean left, boolean right, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private boolean up;
            private boolean down;
            private boolean left;
            private boolean right;
            private boolean topLeft;
            private boolean topRight;
            private boolean bottomLeft;
            private boolean bottomRight;

            public Builder up() {
                this.up = true;
                return this;
            }

            public Builder down() {
                this.down = true;
                return this;
            }

            public Builder left() {
                this.left = true;
                return this;
            }

            public Builder right() {
                this.right = true;
                return this;
            }

            public Builder topLeft() {
                this.topLeft = true;
                return this;
            }

            public Builder topRight() {
                this.topRight = true;
                return this;
            }

            public Builder bottomLeft() {
                this.bottomLeft = true;
                return this;
            }

            public Builder bottomRight() {
                this.bottomRight = true;
                return this;
            }

            public Builder horizontal() {
                this.left();
                this.right();
                return this;
            }

            public Builder vertical() {
                this.up();
                this.down();
                return this;
            }

            public Builder axisAligned() {
                this.horizontal();
                this.vertical();
                return this;
            }

            public Builder corners() {
                this.topLeft();
                this.topRight();
                this.bottomLeft();
                this.bottomRight();
                return this;
            }

            public Builder all() {
                this.axisAligned();
                this.corners();
                return this;
            }

            public ContextRequirement build() {
                return new ContextRequirement(this.up, this.down, this.left, this.right, this.topLeft, this.topRight, this.bottomLeft, this.bottomRight);
            }
        }
    }

    public static abstract class Base
    extends ConnectedTextureBehaviour {
        @Override
        @Nullable
        public abstract CTSpriteShiftEntry getShift(BlockState var1, Direction var2, @Nullable TextureAtlasSprite var3);

        @Override
        @Nullable
        public CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
            CTSpriteShiftEntry shift = this.getShift(state, direction, null);
            if (shift == null) {
                return null;
            }
            return shift.getType();
        }
    }
}
