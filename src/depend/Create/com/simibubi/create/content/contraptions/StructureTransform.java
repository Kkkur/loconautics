/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

public class StructureTransform {
    public static final StreamCodec<ByteBuf, StructureTransform> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, i -> i.offset, (StreamCodec)ByteBufCodecs.INT, i -> i.angle, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.AXIS), i -> i.rotationAxis, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.ROTATION), i -> i.rotation, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.MIRROR), i -> i.mirror, StructureTransform::new);
    public Direction.Axis rotationAxis;
    public BlockPos offset;
    public int angle;
    public Rotation rotation;
    public Mirror mirror;

    private StructureTransform(BlockPos offset, int angle, Direction.Axis axis, Rotation rotation, Mirror mirror) {
        this.offset = offset;
        this.angle = angle;
        this.rotationAxis = axis;
        this.rotation = rotation;
        this.mirror = mirror;
    }

    public StructureTransform(BlockPos offset, Direction.Axis axis, Rotation rotation, Mirror mirror) {
        this(offset, rotation == Rotation.NONE ? 0 : (4 - rotation.ordinal()) * 90, axis, rotation, mirror);
    }

    public StructureTransform(BlockPos offset, float xRotation, float yRotation, float zRotation) {
        this.offset = offset;
        if (xRotation != 0.0f) {
            this.rotationAxis = Direction.Axis.X;
            this.angle = Math.round(xRotation / 90.0f) * 90;
        }
        if (yRotation != 0.0f) {
            this.rotationAxis = Direction.Axis.Y;
            this.angle = Math.round(yRotation / 90.0f) * 90;
        }
        if (zRotation != 0.0f) {
            this.rotationAxis = Direction.Axis.Z;
            this.angle = Math.round(zRotation / 90.0f) * 90;
        }
        this.angle %= 360;
        if (this.angle < -90) {
            this.angle += 360;
        }
        this.rotation = Rotation.NONE;
        if (this.angle == -90 || this.angle == 270) {
            this.rotation = Rotation.CLOCKWISE_90;
        }
        if (this.angle == 90) {
            this.rotation = Rotation.COUNTERCLOCKWISE_90;
        }
        if (this.angle == 180) {
            this.rotation = Rotation.CLOCKWISE_180;
        }
        this.mirror = Mirror.NONE;
    }

    public Vec3 applyWithoutOffsetUncentered(Vec3 localVec) {
        Vec3 vec = localVec;
        if (this.mirror != null) {
            vec = VecHelper.mirror((Vec3)vec, (Mirror)this.mirror);
        }
        if (this.rotationAxis != null) {
            vec = VecHelper.rotate((Vec3)vec, (double)this.angle, (Direction.Axis)this.rotationAxis);
        }
        return vec;
    }

    public Vec3 applyWithoutOffset(Vec3 localVec) {
        Vec3 vec = localVec;
        if (this.mirror != null) {
            vec = VecHelper.mirrorCentered((Vec3)vec, (Mirror)this.mirror);
        }
        if (this.rotationAxis != null) {
            vec = VecHelper.rotateCentered((Vec3)vec, (double)this.angle, (Direction.Axis)this.rotationAxis);
        }
        return vec;
    }

    public Vec3 unapplyWithoutOffset(Vec3 globalVec) {
        Vec3 vec = globalVec;
        if (this.rotationAxis != null) {
            vec = VecHelper.rotateCentered((Vec3)vec, (double)(-this.angle), (Direction.Axis)this.rotationAxis);
        }
        if (this.mirror != null) {
            vec = VecHelper.mirrorCentered((Vec3)vec, (Mirror)this.mirror);
        }
        return vec;
    }

    public Vec3 apply(Vec3 localVec) {
        return this.applyWithoutOffset(localVec).add(Vec3.atLowerCornerOf((Vec3i)this.offset));
    }

    public BlockPos applyWithoutOffset(BlockPos localPos) {
        return BlockPos.containing((Position)this.applyWithoutOffset(VecHelper.getCenterOf((Vec3i)localPos)));
    }

    public BlockPos apply(BlockPos localPos) {
        return this.applyWithoutOffset(localPos).offset((Vec3i)this.offset);
    }

    public BlockPos unapply(BlockPos globalPos) {
        return this.unapplyWithoutOffset(globalPos.subtract((Vec3i)this.offset));
    }

    public BlockPos unapplyWithoutOffset(BlockPos globalPos) {
        return BlockPos.containing((Position)this.unapplyWithoutOffset(VecHelper.getCenterOf((Vec3i)globalPos)));
    }

    public void apply(BlockEntity be) {
        MovedBlockTransformerRegistries.BlockEntityTransformer transformer = MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.get(be.getType());
        if (transformer != null) {
            transformer.transform(be, this);
        } else if (be instanceof TransformableBlockEntity) {
            TransformableBlockEntity itbe = (TransformableBlockEntity)be;
            itbe.transform(be, this);
        }
    }

    public BlockState apply(BlockState state) {
        boolean halfTurn;
        Block block = state.getBlock();
        MovedBlockTransformerRegistries.BlockTransformer transformer = MovedBlockTransformerRegistries.BLOCK_TRANSFORMERS.get(block);
        if (transformer != null) {
            return transformer.transform(state, this);
        }
        if (block instanceof TransformableBlock) {
            TransformableBlock transformable = (TransformableBlock)block;
            return transformable.transform(state, this);
        }
        if (this.mirror != null) {
            state = state.mirror(this.mirror);
        }
        if (this.rotationAxis == Direction.Axis.Y) {
            if (block instanceof BellBlock) {
                if (state.getValue((Property)BlockStateProperties.BELL_ATTACHMENT) == BellAttachType.DOUBLE_WALL) {
                    state = (BlockState)state.setValue((Property)BlockStateProperties.BELL_ATTACHMENT, (Comparable)BellAttachType.SINGLE_WALL);
                }
                return (BlockState)state.setValue((Property)BellBlock.FACING, (Comparable)this.rotation.rotate((Direction)state.getValue((Property)BellBlock.FACING)));
            }
            return state.rotate(this.rotation);
        }
        if (block instanceof FaceAttachedHorizontalDirectionalBlock) {
            Direction forcedAxis;
            DirectionProperty facingProperty = FaceAttachedHorizontalDirectionalBlock.FACING;
            EnumProperty faceProperty = FaceAttachedHorizontalDirectionalBlock.FACE;
            Direction stateFacing = (Direction)state.getValue((Property)facingProperty);
            AttachFace stateFace = (AttachFace)state.getValue((Property)faceProperty);
            boolean z = this.rotationAxis == Direction.Axis.Z;
            Direction direction = forcedAxis = z ? Direction.WEST : Direction.SOUTH;
            if (stateFacing.getAxis() == this.rotationAxis && stateFace == AttachFace.WALL) {
                return state;
            }
            for (int i = 0; i < this.rotation.ordinal(); ++i) {
                stateFace = (AttachFace)state.getValue((Property)faceProperty);
                stateFacing = (Direction)state.getValue((Property)facingProperty);
                boolean b = state.getValue((Property)faceProperty) == AttachFace.CEILING;
                state = (BlockState)state.setValue((Property)facingProperty, (Comparable)(b ? forcedAxis : forcedAxis.getOpposite()));
                state = stateFace != AttachFace.WALL ? (BlockState)state.setValue((Property)faceProperty, (Comparable)AttachFace.WALL) : (stateFacing.getAxisDirection() == (z ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE) ? (BlockState)state.setValue((Property)faceProperty, (Comparable)AttachFace.FLOOR) : (BlockState)state.setValue((Property)faceProperty, (Comparable)AttachFace.CEILING));
            }
            return state;
        }
        boolean bl = halfTurn = this.rotation == Rotation.CLOCKWISE_180;
        if (block instanceof StairBlock) {
            state = this.transformStairs(state, halfTurn);
            return state;
        }
        if (state.hasProperty((Property)BlockStateProperties.FACING)) {
            state = (BlockState)state.setValue((Property)BlockStateProperties.FACING, (Comparable)this.rotateFacing((Direction)state.getValue((Property)BlockStateProperties.FACING)));
        } else if (state.hasProperty((Property)BlockStateProperties.AXIS)) {
            state = (BlockState)state.setValue((Property)BlockStateProperties.AXIS, (Comparable)this.rotateAxis((Direction.Axis)state.getValue((Property)BlockStateProperties.AXIS)));
        } else if (halfTurn) {
            Direction stateFacing;
            if (state.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING) && (stateFacing = (Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getAxis() == this.rotationAxis) {
                return state;
            }
            if ((state = state.rotate(this.rotation)).hasProperty((Property)SlabBlock.TYPE) && state.getValue((Property)SlabBlock.TYPE) != SlabType.DOUBLE) {
                state = (BlockState)state.setValue((Property)SlabBlock.TYPE, (Comparable)(state.getValue((Property)SlabBlock.TYPE) == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM));
            }
        }
        return state;
    }

    protected BlockState transformStairs(BlockState state, boolean halfTurn) {
        if (((Direction)state.getValue((Property)StairBlock.FACING)).getAxis() != this.rotationAxis) {
            for (int i = 0; i < this.rotation.ordinal(); ++i) {
                Direction direction = (Direction)state.getValue((Property)StairBlock.FACING);
                Half half = (Half)state.getValue((Property)StairBlock.HALF);
                state = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ^ half == Half.BOTTOM ^ direction.getAxis() == Direction.Axis.Z ? (BlockState)state.cycle((Property)StairBlock.HALF) : (BlockState)state.setValue((Property)StairBlock.FACING, (Comparable)direction.getOpposite());
            }
        } else if (halfTurn) {
            state = (BlockState)state.cycle((Property)StairBlock.HALF);
        }
        return state;
    }

    public Direction mirrorFacing(Direction facing) {
        if (this.mirror != null) {
            return this.mirror.mirror(facing);
        }
        return facing;
    }

    public Direction.Axis rotateAxis(Direction.Axis axis) {
        Direction facing = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        return this.rotateFacing(facing).getAxis();
    }

    public Direction rotateFacing(Direction facing) {
        for (int i = 0; i < this.rotation.ordinal(); ++i) {
            facing = facing.getClockWise(this.rotationAxis);
        }
        return facing;
    }
}
