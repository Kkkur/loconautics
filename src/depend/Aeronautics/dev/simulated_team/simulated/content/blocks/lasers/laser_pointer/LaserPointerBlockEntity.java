/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.equipment.clipboard.ClipboardCloneable
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.lasers.laser_pointer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.lasers.AbstractLaserBlockEntity;
import dev.simulated_team.simulated.content.blocks.lasers.laser_pointer.LaserPointerBlock;
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorBlockEntity;
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorInteractorBehaviour;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimLevelUtil;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LaserPointerBlockEntity
extends AbstractLaserBlockEntity
implements ClipboardCloneable {
    private ScrollValueBehaviour range;
    public LaserSensorInteractorBehaviour sensorInteraction;
    private boolean rainbow;
    public int laserColor = SimColors.MEDIA_OURPLE;
    protected int bestPower;
    public Vec3 currentHitPos = Vec3.ZERO;

    public LaserPointerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        int rangeMax = (Integer)SimConfigService.INSTANCE.server().blocks.laserPointerRange.get();
        this.range = new ScrollValueBehaviour((Component)SimLang.translate("laser_pointer.max_length", new Object[0]).component(), (SmartBlockEntity)this, (ValueBoxTransform)new RangeValueBoxTransform()).between(1, rangeMax);
        this.range.value = rangeMax;
        this.sensorInteraction = new LaserSensorInteractorBehaviour(this, this::gatherStartAndEnd, this::getLaserRange, this::getPower, this::matchesSensor);
        this.sensorInteraction.setShouldCast(this::shouldCast);
        behaviours.add(this.sensorInteraction);
        behaviours.add((BlockEntityBehaviour)this.range);
    }

    public void tick() {
        int currentPower;
        if (this.level == null || !SimLevelUtil.isAreaActuallyLoaded(this.level, this.worldPosition, 2)) {
            return;
        }
        if ((!this.level.isClientSide || this.isVirtual()) && (currentPower = this.level.getBestNeighborSignal(this.worldPosition)) != this.bestPower) {
            this.bestPower = currentPower;
            this.sendData();
        }
        super.tick();
        if (!this.shouldCast()) {
            this.currentHitPos = Vec3.ZERO;
            return;
        }
        if (!this.isVirtual()) {
            BlockHitResult context = this.sensorInteraction.getBlockHitResult();
            this.currentHitPos = context.getType() != HitResult.Type.MISS ? context.getLocation() : Vec3.ZERO;
        }
    }

    public boolean isAmethyst() {
        return this.laserColor == SimColors.MEDIA_OURPLE && !this.isRainbow();
    }

    public int getPower() {
        return (Boolean)this.getBlockState().getValue((Property)LaserPointerBlock.INVERTED) != false ? 15 - this.bestPower : this.bestPower;
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        this.laserColor = tag.contains("LaserColor", 99) ? tag.getInt("LaserColor") : SimColors.MEDIA_OURPLE;
        this.bestPower = tag.getInt("BestPower");
        this.rainbow = tag.getBoolean("Rainbow");
        this.currentHitPos = this.readHitPos(tag);
        super.read(tag, registries, clientPacket);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("LaserColor", this.laserColor);
        tag.putInt("BestPower", this.bestPower);
        tag.putBoolean("Rainbow", this.isRainbow());
        this.writeHitPos(tag);
        super.write(tag, registries, clientPacket);
    }

    private void writeHitPos(CompoundTag tag) {
        tag.put("HitPos", (Tag)VecHelper.writeNBT((Vec3)this.currentHitPos));
    }

    private Vec3 readHitPos(CompoundTag tag) {
        Vec3 currentHit = Vec3.ZERO;
        if (tag.contains("HitPos")) {
            currentHit = VecHelper.readNBT((ListTag)tag.getList("HitPos", 10));
        }
        return currentHit;
    }

    @Override
    public Direction getDirection() {
        return (Direction)this.getBlockState().getValue((Property)LaserPointerBlock.FACING);
    }

    @Override
    public float getLaserRange() {
        return this.range.value;
    }

    @Override
    public boolean shouldCast() {
        return this.getPower() != 0;
    }

    public void setLaserColor(int color) {
        this.laserColor = color;
        this.setChanged();
        this.sendData();
    }

    public int getLaserColor() {
        return this.laserColor;
    }

    public boolean matchesSensor(LaserSensorBlockEntity sensor) {
        return sensor.filterColor(this.laserColor, this.rainbow);
    }

    public String getClipboardKey() {
        return "LaserPointer";
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        tag.putInt("Color", this.laserColor);
        tag.putBoolean("Rainbow", this.isRainbow());
        return true;
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (simulate) {
            return true;
        }
        this.setLaserColor(tag.getInt("Color"));
        this.setRainbow(tag.getBoolean("Rainbow"));
        return true;
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
        if (!this.getLevel().isClientSide) {
            this.notifyUpdate();
        }
    }

    private static class RangeValueBoxTransform
    extends ValueBoxTransform.Sided {
        private RangeValueBoxTransform() {
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return ((Direction)state.getValue((Property)LaserPointerBlock.FACING)).getOpposite() == direction;
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            Direction facing = (Direction)state.getValue((Property)LaserPointerBlock.FACING);
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
