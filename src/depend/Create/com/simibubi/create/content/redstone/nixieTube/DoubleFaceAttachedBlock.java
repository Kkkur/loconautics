/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.nixieTube;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleFaceAttachedBlock
extends HorizontalDirectionalBlock {
    public static final MapCodec<DoubleFaceAttachedBlock> CODEC = DoubleFaceAttachedBlock.simpleCodec(DoubleFaceAttachedBlock::new);
    public static final EnumProperty<DoubleAttachFace> FACE = EnumProperty.create((String)"double_face", DoubleAttachFace.class);

    public DoubleFaceAttachedBlock(BlockBehaviour.Properties p_53182_) {
        super(p_53182_);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for (Direction direction : pContext.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, (Comparable)((Object)(direction == Direction.UP ? DoubleAttachFace.CEILING : DoubleAttachFace.FLOOR)))).setValue((Property)FACING, (Comparable)pContext.getHorizontalDirection());
            } else {
                Vec3 lookAngle;
                Vec3 n = Vec3.atLowerCornerOf((Vec3i)direction.getClockWise().getNormal());
                DoubleAttachFace face = DoubleAttachFace.WALL;
                if (pContext.getPlayer() != null && (lookAngle = pContext.getPlayer().getLookAngle()).dot(n) < 0.0) {
                    face = DoubleAttachFace.WALL_REVERSED;
                }
                blockstate = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, (Comparable)((Object)face))).setValue((Property)FACING, (Comparable)direction.getOpposite());
            }
            if (!blockstate.canSurvive((LevelReader)pContext.getLevel(), pContext.getClickedPos())) continue;
            return blockstate;
        }
        return null;
    }

    protected static Direction getConnectedDirection(BlockState pState) {
        switch (((DoubleAttachFace)((Object)pState.getValue(FACE))).ordinal()) {
            case 3: {
                return Direction.DOWN;
            }
            case 0: {
                return Direction.UP;
            }
        }
        return (Direction)pState.getValue((Property)FACING);
    }

    @NotNull
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public static enum DoubleAttachFace implements StringRepresentable
    {
        FLOOR("floor"),
        WALL("wall"),
        WALL_REVERSED("wall_reversed"),
        CEILING("ceiling");

        private final String name;

        private DoubleAttachFace(String p_61311_) {
            this.name = p_61311_;
        }

        public String getSerializedName() {
            return this.name;
        }

        public int xRot() {
            return this == FLOOR ? 0 : (this == CEILING ? 180 : 90);
        }
    }
}
