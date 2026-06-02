/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.lasers.laser_sensor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorBlock;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.util.SimColors;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class LaserSensorBlockEntity
extends SmartBlockEntity
implements Clearable {
    public int currentPower = 0;
    public int nextPower = 0;
    public int trackedPower = 0;
    private int updateCooldown = 0;
    private static final int MAX_COOLDOWN = 3;
    private FilteringBehaviour filter;
    public double closestHitDistance = Double.MAX_VALUE;
    private double trackedHitDistance = Double.MAX_VALUE;

    public LaserSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.filter = new FilteringBehaviour((SmartBlockEntity)this, (ValueBoxTransform)new FilterValueBoxTransform()).withPredicate(this::isItemValidFilter);
        behaviours.add((BlockEntityBehaviour)this.filter);
    }

    private boolean isItemValidFilter(ItemStack itemStack) {
        return itemStack.getItem() instanceof DyeItem || itemStack.is(SimTags.Items.LASER_POINTER_LENS) || itemStack.is(SimTags.Items.LASER_POINTER_RAINBOW);
    }

    public void tick() {
        super.tick();
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.currentPower != this.nextPower) {
            if (this.currentPower == 0 == (this.nextPower == 0)) {
                this.currentPower = this.nextPower;
                this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)LaserSensorBlock.POWERED, (Comparable)Boolean.valueOf(this.nextPower > 0)));
                this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
            } else if (this.updateCooldown <= 0) {
                this.currentPower = this.nextPower;
                this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)LaserSensorBlock.POWERED, (Comparable)Boolean.valueOf(this.nextPower > 0)));
                this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
                boolean powered = (Boolean)this.getBlockState().getValue((Property)LaserSensorBlock.POWERED);
                if (powered) {
                    SimAdvancements.MY_EYE.awardToNearby(this.getBlockPos(), this.getLevel());
                }
                this.updateCooldown = 3;
            }
        }
        if (this.updateCooldown > 0) {
            --this.updateCooldown;
        }
        this.nextPower = this.trackedPower;
        this.trackedPower = 0;
        this.closestHitDistance = this.trackedHitDistance;
        this.trackedHitDistance = Double.MAX_VALUE;
    }

    public void updateFromPointer(double distance, int directPower) {
        this.trackedHitDistance = Math.min(distance, this.trackedHitDistance);
        if (directPower > this.trackedPower) {
            this.trackedPower = directPower;
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.currentPower = tag.getInt("CurrentPower");
        this.updateCooldown = Math.clamp((long)tag.getInt("UpdateCooldown"), 0, 3);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("CurrentPower", this.currentPower);
        tag.putInt("UpdateCooldown", this.updateCooldown);
    }

    public void clearContent() {
        this.filter.setFilter(ItemStack.EMPTY);
    }

    public boolean filterColor(int testColor, boolean rainbow) {
        ItemStack stack = this.filter.getFilter();
        boolean rainbowItem = stack.is(SimTags.Items.LASER_POINTER_RAINBOW);
        if (stack.isEmpty()) {
            return true;
        }
        if (rainbowItem && rainbow) {
            return true;
        }
        if (rainbowItem != rainbow) {
            return false;
        }
        Item item = stack.getItem();
        int color = -1;
        if (stack.is(SimTags.Items.LASER_POINTER_LENS)) {
            color = SimColors.MEDIA_OURPLE;
        } else if (item instanceof DyeItem) {
            DyeItem dyeItem = (DyeItem)item;
            color = dyeItem.getDyeColor().getTextColor();
        }
        return testColor == color;
    }

    private static class FilterValueBoxTransform
    extends ValueBoxTransform.Sided {
        private FilterValueBoxTransform() {
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return (switch ((AttachFace)state.getValue((Property)LaserSensorBlock.TARGET)) {
                case AttachFace.FLOOR, AttachFace.CEILING -> (Direction)state.getValue((Property)LaserSensorBlock.FACING);
                default -> Direction.UP;
            }).getAxis() == direction.getAxis();
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            Direction facing = (Direction)state.getValue((Property)LaserSensorBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y) {
                return;
            }
            if (this.getSide() != Direction.UP) {
                return;
            }
            TransformStack.of((PoseStack)ms).rotateZDegrees(-AngleHelper.horizontalAngle((Direction)facing) + 180.0f);
        }
    }
}
