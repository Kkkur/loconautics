/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BubbleColumnBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WaterWheelBlockEntity
extends GeneratingKineticBlockEntity {
    public static final Map<Direction.Axis, Set<BlockPos>> SMALL_OFFSETS = new EnumMap<Direction.Axis, Set<BlockPos>>(Direction.Axis.class);
    public static final Map<Direction.Axis, Set<BlockPos>> LARGE_OFFSETS = new EnumMap<Direction.Axis, Set<BlockPos>>(Direction.Axis.class);
    public int flowScore;
    public BlockState material = Blocks.SPRUCE_PLANKS.defaultBlockState();

    public WaterWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(60);
    }

    protected int getSize() {
        return 1;
    }

    protected Set<BlockPos> getOffsetsToCheck() {
        return (this.getSize() == 1 ? SMALL_OFFSETS : LARGE_OFFSETS).get(this.getAxis());
    }

    public ItemInteractionResult applyMaterialIfValid(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        BlockItem blockItem = (BlockItem)item;
        BlockState material = blockItem.getBlock().defaultBlockState();
        if (material == this.material) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!material.is(BlockTags.PLANKS)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (this.level.isClientSide() && !this.isVirtual()) {
            return ItemInteractionResult.SUCCESS;
        }
        this.material = material;
        this.notifyUpdate();
        this.level.levelEvent(2001, this.worldPosition, Block.getId((BlockState)material));
        return ItemInteractionResult.SUCCESS;
    }

    protected Direction.Axis getAxis() {
        Direction.Axis axis = Direction.Axis.X;
        BlockState blockState = this.getBlockState();
        Block block = blockState.getBlock();
        if (block instanceof IRotate) {
            IRotate irotate = (IRotate)block;
            axis = irotate.getRotationAxis(blockState);
        }
        return axis;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        this.determineAndApplyFlowScore();
    }

    public void determineAndApplyFlowScore() {
        Vec3 wheelPlane = Vec3.atLowerCornerOf((Vec3i)new Vec3i(1, 1, 1).subtract(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.getAxis()).getNormal()));
        int flowScore = 0;
        boolean lava = false;
        for (BlockPos blockPos : this.getOffsetsToCheck()) {
            Vec3 normal;
            Vec3 positiveMotion;
            double dot;
            BlockPos targetPos = blockPos.offset((Vec3i)this.worldPosition);
            Vec3 flowAtPos = this.getFlowVectorAtPosition(targetPos).multiply(wheelPlane);
            lava |= FluidHelper.isLava(this.level.getFluidState(targetPos).getType());
            if (flowAtPos.lengthSqr() == 0.0 || !(Math.abs(dot = (flowAtPos = flowAtPos.normalize()).dot(positiveMotion = VecHelper.rotate((Vec3)(normal = Vec3.atLowerCornerOf((Vec3i)blockPos).normalize()), (double)90.0, (Direction.Axis)this.getAxis()))) > 0.5)) continue;
            flowScore = (int)((double)flowScore + Math.signum(dot));
        }
        if (flowScore != 0 && !this.level.isClientSide()) {
            this.award(lava ? AllAdvancements.LAVA_WHEEL : AllAdvancements.WATER_WHEEL);
        }
        this.setFlowScoreAndUpdate(flowScore);
    }

    public Vec3 getFlowVectorAtPosition(BlockPos pos) {
        FluidState fluid = this.level.getFluidState(pos);
        Vec3 vec = fluid.getFlow((BlockGetter)this.level, pos);
        BlockState blockState = this.level.getBlockState(pos);
        if (blockState.getBlock() == Blocks.BUBBLE_COLUMN) {
            vec = new Vec3(0.0, (Boolean)blockState.getValue((Property)BubbleColumnBlock.DRAG_DOWN) != false ? -1.0 : 1.0, 0.0);
        }
        return vec;
    }

    public void setFlowScoreAndUpdate(int score) {
        if (this.flowScore == score) {
            return;
        }
        this.flowScore = score;
        this.updateGeneratedRotation();
        this.setChanged();
    }

    private void redraw() {
        if (!this.isVirtual()) {
            this.requestModelDataUpdate();
        }
        if (this.hasLevel()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
            this.level.getChunkSource().getLightEngine().checkBlock(this.worldPosition);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.LAVA_WHEEL, AllAdvancements.WATER_WHEEL);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.flowScore = compound.getInt("FlowScore");
        BlockState prevMaterial = this.material;
        if (!compound.contains("Material")) {
            return;
        }
        this.material = NbtUtils.readBlockState(this.blockHolderGetter(), (CompoundTag)compound.getCompound("Material"));
        if (this.material.isAir()) {
            this.material = Blocks.SPRUCE_PLANKS.defaultBlockState();
        }
        if (clientPacket && prevMaterial != this.material) {
            this.redraw();
        }
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        tag.put("Material", (Tag)NbtUtils.writeBlockState((BlockState)this.material));
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("FlowScore", this.flowScore);
        compound.put("Material", (Tag)NbtUtils.writeBlockState((BlockState)this.material));
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate((double)this.getSize());
    }

    @Override
    public float getGeneratedSpeed() {
        return Mth.clamp((int)this.flowScore, (int)-1, (int)1) * 8 / this.getSize();
    }

    static {
        for (Direction.Axis axis : Iterate.axes) {
            HashSet<BlockPos> offsets = new HashSet<BlockPos>();
            for (Direction d : Iterate.directions) {
                if (d.getAxis() == axis) continue;
                offsets.add(BlockPos.ZERO.relative(d));
            }
            SMALL_OFFSETS.put(axis, offsets);
            offsets = new HashSet();
            for (Direction d : Iterate.directions) {
                if (d.getAxis() == axis) continue;
                BlockPos centralOffset = BlockPos.ZERO.relative(d, 2);
                offsets.add(centralOffset);
                for (Direction d2 : Iterate.directions) {
                    if (d2.getAxis() == axis || d2.getAxis() == d.getAxis()) continue;
                    offsets.add(centralOffset.relative(d2));
                }
            }
            LARGE_OFFSETS.put(axis, offsets);
        }
    }
}
