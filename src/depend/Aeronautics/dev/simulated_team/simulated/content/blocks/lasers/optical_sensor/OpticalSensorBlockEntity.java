/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.equipment.clipboard.ClipboardCloneable
 *  com.simibubi.create.content.redstone.DirectedDirectionalBlock
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.lasers.optical_sensor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.lasers.AbstractLaserBlockEntity;
import dev.simulated_team.simulated.content.blocks.lasers.LaserBehaviour;
import dev.simulated_team.simulated.content.blocks.lasers.optical_sensor.OpticalSensorBlock;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.service.SimFluidService;
import java.awt.Color;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class OpticalSensorBlockEntity
extends AbstractLaserBlockEntity
implements Clearable,
ClipboardCloneable {
    private FilteringBehaviour filter;
    private ScrollValueBehaviour range;
    public LaserBehaviour laser;
    private Block hitBlock = Blocks.AIR;
    private float rayDistance = this.getLaserRange();
    private float lastRayDistance = this.getLaserRange();
    private float opacity = 1.0f;

    public Block getHitBlock() {
        return this.hitBlock;
    }

    public float getHitBlockDistance() {
        if (this.hitBlock.defaultBlockState().isAir()) {
            return this.getLaserRange();
        }
        Vector3d pos = Sable.HELPER.projectOutOfSubLevel(this.getLevel(), JOMLConversion.atCenterOf((Vec3i)this.getBlockPos()));
        Vector3d hitPos = Sable.HELPER.projectOutOfSubLevel(this.getLevel(), JOMLConversion.toJOML((Position)this.laser.getBlockHitResult().getLocation()));
        return (float)pos.distance((Vector3dc)hitPos);
    }

    public boolean hasHit() {
        return !this.hitBlock.defaultBlockState().isAir();
    }

    public OpticalSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.filter = new FilteringBehaviour((SmartBlockEntity)this, (ValueBoxTransform)new FilterValueBoxTransform());
        behaviours.add((BlockEntityBehaviour)this.filter);
        int maxRange = (Integer)SimConfigService.INSTANCE.server().blocks.opticalSensorRange.get();
        this.range = new RangeScrollValueBehaviour((Component)SimLang.translate("optical_sensor.max_length", new Object[0]).component(), this, (ValueBoxTransform)new RangeValueBoxTransform()).between(1, maxRange);
        behaviours.add((BlockEntityBehaviour)this.range);
        this.range.value = maxRange;
        this.laser = new LaserBehaviour(this, this::gatherStartAndEnd, this::getLaserRange);
        behaviours.add(this.laser);
    }

    public void tick() {
        super.tick();
        if (!this.isVirtual()) {
            BlockHitResult context = this.laser.getBlockHitResult();
            if (context != null && this.hasLevel()) {
                this.rayDistance = (float)Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(this.level, (Position)this.laser.getLaserPositions().get().get(true), (Position)context.getLocation()));
                boolean shouldPower = this.checkFilter(context);
                if (this.lastRayDistance != this.rayDistance || (Boolean)this.getBlockState().getValue((Property)OpticalSensorBlock.POWERED) != shouldPower) {
                    this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)OpticalSensorBlock.POWERED, (Comparable)Boolean.valueOf(shouldPower)));
                    this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
                    this.invalidateRenderBoundingBox();
                }
                this.lastRayDistance = this.rayDistance;
            }
            if (SimFluidService.INSTANCE.getFluidInItem(this.filter.getFilter()) != null) {
                this.laser.setFluidCollide(ClipContext.Fluid.ANY);
            } else {
                this.laser.setFluidCollide(ClipContext.Fluid.NONE);
            }
        }
    }

    private boolean checkFilter(BlockHitResult context) {
        BlockState hitBlock = this.level.getBlockState(context.getBlockPos());
        FluidState hitFluid = this.level.getFluidState(context.getBlockPos());
        boolean passed = false;
        ItemStack filterItem = this.filter.getFilter();
        if (context.getType() != HitResult.Type.MISS) {
            if (!filterItem.isEmpty()) {
                if (!hitFluid.isEmpty()) {
                    Fluid fluidInItem = SimFluidService.INSTANCE.getFluidInItem(filterItem);
                    passed = fluidInItem.isSame(hitFluid.getType());
                } else {
                    passed = !hitBlock.isAir() && this.filter.test(new ItemStack((ItemLike)hitBlock.getBlock()));
                }
            } else {
                passed = true;
            }
        }
        this.hitBlock = passed ? hitBlock.getBlock() : Blocks.AIR;
        return passed;
    }

    @Override
    public Direction getDirection() {
        AttachFace target = (AttachFace)this.getBlockState().getValue((Property)OpticalSensorBlock.TARGET);
        if (target == AttachFace.CEILING) {
            return Direction.UP;
        }
        if (target == AttachFace.FLOOR) {
            return Direction.DOWN;
        }
        return (Direction)this.getBlockState().getValue((Property)OpticalSensorBlock.FACING);
    }

    @Override
    public boolean shouldCast() {
        return true;
    }

    @Override
    public float getLaserRange() {
        return this.range.getValue() + 1;
    }

    public void setRange(int blocks) {
        int max = (Integer)SimConfigService.INSTANCE.server().blocks.opticalSensorRange.get();
        this.range.setValue(Math.clamp((long)(blocks - 1), 1, max));
    }

    public float getRayDistance() {
        return this.rayDistance;
    }

    public boolean tryApplyDye(ItemStack item) {
        DyeItem dyeItem;
        Color color;
        Item item2 = item.getItem();
        if (item2 instanceof DyeItem && (color = new Color((dyeItem = (DyeItem)item2).getDyeColor().getTextColor())).getRed() == color.getGreen() && color.getGreen() == color.getBlue()) {
            this.opacity = (float)color.getRed() / 255.0f;
            this.opacity *= this.opacity;
            this.setChanged();
            this.sendData();
            return true;
        }
        return false;
    }

    public float getOpacity() {
        return this.opacity;
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.opacity = Math.clamp(tag.contains("Opacity") ? tag.getFloat("Opacity") : 1.0f, 0.0f, 1.0f);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("Opacity", this.opacity);
    }

    public void clearContent() {
        this.filter.setFilter(ItemStack.EMPTY);
    }

    public String getClipboardKey() {
        return "OpticalSensor";
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        tag.putFloat("Opacity", this.getOpacity());
        return true;
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (simulate) {
            return true;
        }
        this.opacity = tag.getFloat("Opacity");
        return true;
    }

    private static class FilterValueBoxTransform
    extends ValueBoxTransform.Sided {
        private FilterValueBoxTransform() {
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return (switch ((AttachFace)state.getValue((Property)OpticalSensorBlock.TARGET)) {
                case AttachFace.FLOOR, AttachFace.CEILING -> (Direction)state.getValue((Property)OpticalSensorBlock.FACING);
                default -> Direction.UP;
            }).getAxis() == direction.getAxis();
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            Direction facing = (Direction)state.getValue((Property)DirectedDirectionalBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y) {
                return;
            }
            if (this.getSide() != Direction.UP) {
                return;
            }
            TransformStack.of((PoseStack)ms).rotateZDegrees(-AngleHelper.horizontalAngle((Direction)facing) + 180.0f);
        }
    }

    private static class RangeScrollValueBehaviour
    extends ScrollValueBehaviour {
        public RangeScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
            super(label, be, slot);
        }

        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(this.label, this.max, 15, (List)ImmutableList.of((Object)Component.translatable((String)"simulated.unit.length_blocks")), new ValueSettingsFormatter(ValueSettingsBehaviour.ValueSettings::format));
        }
    }

    private static class RangeValueBoxTransform
    extends ValueBoxTransform.Sided {
        private RangeValueBoxTransform() {
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            DirectedDirectionalBlock.getTargetDirection((BlockState)state);
            return DirectedDirectionalBlock.getTargetDirection((BlockState)state).getOpposite() == direction;
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            Direction facing = (Direction)state.getValue((Property)DirectedDirectionalBlock.FACING);
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
