/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.kinetics.gauge;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.GaugeShaper;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class GaugeBlock
extends DirectionalAxisKineticBlock
implements IBE<GaugeBlockEntity> {
    public static final GaugeShaper GAUGE = GaugeShaper.make();
    protected Type type;

    public static GaugeBlock speed(BlockBehaviour.Properties properties) {
        return new GaugeBlock(properties, Type.SPEED);
    }

    public static GaugeBlock stress(BlockBehaviour.Properties properties) {
        return new GaugeBlock(properties, Type.STRESS);
    }

    protected GaugeBlock(BlockBehaviour.Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos placedOnPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);
        Block block = placedOnState.getBlock();
        if (block instanceof IRotate && ((IRotate)block).hasShaftTowards((LevelReader)world, placedOnPos, placedOnState, face)) {
            boolean lookPositive;
            BlockState toPlace = this.defaultBlockState();
            Direction horizontalFacing = context.getHorizontalDirection();
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            boolean bl = lookPositive = nearestLookingDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            toPlace = face.getAxis() == Direction.Axis.X ? (BlockState)((BlockState)toPlace.setValue((Property)FACING, (Comparable)(lookPositive ? Direction.NORTH : Direction.SOUTH))).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(true)) : (face.getAxis() == Direction.Axis.Y ? (BlockState)((BlockState)toPlace.setValue((Property)FACING, (Comparable)horizontalFacing.getOpposite())).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(horizontalFacing.getAxis() == Direction.Axis.X)) : (BlockState)((BlockState)toPlace.setValue((Property)FACING, (Comparable)(lookPositive ? Direction.WEST : Direction.EAST))).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(false)));
            return toPlace;
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    @Override
    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection().getAxis() != Direction.Axis.X;
    }

    public boolean shouldRenderHeadOnFace(Level world, BlockPos pos, BlockState state, Direction face) {
        if (face.getAxis().isVertical()) {
            return false;
        }
        if (face == ((Direction)state.getValue((Property)FACING)).getOpposite()) {
            return false;
        }
        if (face.getAxis() == this.getRotationAxis(state)) {
            return false;
        }
        if (this.getRotationAxis(state) == Direction.Axis.Y && face != state.getValue((Property)FACING)) {
            return false;
        }
        return Block.shouldRenderFace((BlockState)state, (BlockGetter)world, (BlockPos)pos, (Direction)face, (BlockPos)pos.relative(face)) || world instanceof WrappedLevel;
    }

    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be == null || !(be instanceof GaugeBlockEntity)) {
            return;
        }
        GaugeBlockEntity gaugeBE = (GaugeBlockEntity)be;
        if (gaugeBE.dialTarget == 0.0f) {
            return;
        }
        int color = gaugeBE.color;
        for (Direction face : Iterate.directions) {
            int particleCount;
            if (!this.shouldRenderHeadOnFace(worldIn, pos, stateIn, face)) continue;
            Vector3f rgb = new Color(color).asVectorF();
            Vec3 faceVec = Vec3.atLowerCornerOf((Vec3i)face.getNormal());
            Direction positiveFacing = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)face.getAxis());
            Vec3 positiveFaceVec = Vec3.atLowerCornerOf((Vec3i)positiveFacing.getNormal());
            int n = particleCount = gaugeBE.dialTarget > 1.0f ? 4 : 1;
            if (particleCount == 1 && rand.nextFloat() > 0.25f) continue;
            for (int i = 0; i < particleCount; ++i) {
                Vec3 mul = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)rand, (float)0.25f).multiply(new Vec3(1.0, 1.0, 1.0).subtract(positiveFaceVec)).normalize().scale((double)0.3f);
                Vec3 offset = VecHelper.getCenterOf((Vec3i)pos).add(faceVec.scale(0.55)).add(mul);
                worldIn.addParticle((ParticleOptions)new DustParticleOptions(rgb, 1.0f), offset.x, offset.y, offset.z, mul.x, mul.y, mul.z);
            }
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return GAUGE.get((Direction)state.getValue((Property)FACING), (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE));
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof GaugeBlockEntity) {
            GaugeBlockEntity gaugeBlockEntity = (GaugeBlockEntity)be;
            return Mth.ceil((float)Mth.clamp((float)(gaugeBlockEntity.dialTarget * 14.0f), (float)0.0f, (float)15.0f));
        }
        return 0;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<GaugeBlockEntity> getBlockEntityClass() {
        return GaugeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GaugeBlockEntity> getBlockEntityType() {
        return this.type == Type.SPEED ? (BlockEntityType)AllBlockEntityTypes.SPEEDOMETER.get() : (BlockEntityType)AllBlockEntityTypes.STRESSOMETER.get();
    }

    public static enum Type implements StringRepresentable
    {
        SPEED,
        STRESS;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
