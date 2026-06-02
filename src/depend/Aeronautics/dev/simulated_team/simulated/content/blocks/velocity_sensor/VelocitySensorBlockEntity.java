/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.velocity_sensor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlock;
import dev.simulated_team.simulated.data.SimLang;
import java.lang.ref.WeakReference;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class VelocitySensorBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    private float adjustedVelocity = 0.0f;
    private int signedRedstoneStrength = 0;
    private Vector3dc currentNormal;
    private WeakReference<SubLevel> subLevelReference;
    private VelocitySensorScrollValueBehaviour maxSpeed;
    private final LerpedFloat fanSpeed = LerpedFloat.linear().chase(0.0, 0.5, LerpedFloat.Chaser.EXP);
    private float fanAngle = 0.0f;
    private float oldFanAngle = 0.0f;

    public VelocitySensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.subLevelReference = new WeakReference<Object>(null);
        this.currentNormal = new Vector3d();
    }

    public void addBehaviours(List<BlockEntityBehaviour> list) {
        this.maxSpeed = new VelocitySensorScrollValueBehaviour((Component)SimLang.translate("velocity_sensor.description", new Object[0]).component(), this, (ValueBoxTransform)new VelocitySensorValueBoxTransform());
        this.maxSpeed.between(1, 50);
        this.maxSpeed.value = 10;
        this.maxSpeed.withFormatter(value -> value + " m/s");
        list.add((BlockEntityBehaviour)this.maxSpeed);
    }

    public void initialize() {
        super.initialize();
        this.subLevelReference = new WeakReference<SubLevel>(Sable.HELPER.getContaining(this.getLevel(), (Vec3i)this.worldPosition));
    }

    public void tick() {
        this.currentNormal = JOMLConversion.toJOML((Position)Vec3.atLowerCornerOf((Vec3i)AbstractDirectionalAxisBlock.getDirectionOfAxis(this.getBlockState()).getNormal()));
        super.tick();
        SubLevel subLevel = (SubLevel)this.subLevelReference.get();
        int redstoneStrengthBefore = this.signedRedstoneStrength;
        if (!this.level.isClientSide) {
            if (subLevel != null) {
                float dot = (float)this.getGlobalVelocity().dot((Vector3dc)subLevel.logicalPose().transformNormal(this.currentNormal, new Vector3d()));
                this.adjustedVelocity = (double)Math.abs(dot) > 0.05 ? dot : 0.0f;
                this.signedRedstoneStrength = (int)Math.clamp(this.getAdjustedVelocity() / (float)this.maxSpeed.getValue() * 15.0f, -15.0f, 15.0f);
            } else {
                this.adjustedVelocity = 0.0f;
                this.signedRedstoneStrength = 0;
            }
            if (redstoneStrengthBefore != this.signedRedstoneStrength) {
                int power = this.signedRedstoneStrength == 0 ? 0 : (this.signedRedstoneStrength < 0 ? 1 : 2);
                this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)VelocitySensorBlock.POWERED, (Comparable)Integer.valueOf(power)));
                Direction axisDir = AbstractDirectionalAxisBlock.getDirectionOfAxis(this.getBlockState());
                this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
                this.level.updateNeighborsAt(this.worldPosition.relative(axisDir), this.getBlockState().getBlock());
                this.level.updateNeighborsAt(this.worldPosition.relative(axisDir.getOpposite()), this.getBlockState().getBlock());
            }
            this.sendData();
        } else {
            this.fanSpeed.updateChaseTarget(Mth.clamp((float)(this.getAdjustedVelocity() / (float)this.maxSpeed.getValue()), (float)-1.0f, (float)1.0f));
            this.fanSpeed.tickChaser();
            this.oldFanAngle = this.fanAngle;
            this.fanAngle += this.fanSpeed.getValue();
        }
    }

    public ScrollValueBehaviour getMaxSpeed() {
        return this.maxSpeed;
    }

    public float getFanAngle(float pt) {
        return Mth.lerp((float)pt, (float)this.oldFanAngle, (float)this.fanAngle);
    }

    private Vector3d getGlobalVelocity() {
        SubLevel subLevel = (SubLevel)this.subLevelReference.get();
        if (subLevel == null) {
            return new Vector3d();
        }
        Vector3d jomlPos = JOMLConversion.toJOML((Position)this.worldPosition.getCenter());
        return subLevel.logicalPose().transformPosition((Vector3dc)jomlPos, new Vector3d()).sub((Vector3dc)subLevel.lastPose().transformPosition((Vector3dc)jomlPos, new Vector3d()), jomlPos).mul(20.0);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("AdjustedVelocity", this.getAdjustedVelocity());
        tag.putInt("SignedRedstoneStrength", this.signedRedstoneStrength);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.adjustedVelocity = tag.getFloat("AdjustedVelocity");
        this.signedRedstoneStrength = Mth.clamp((int)-15, (int)15, (int)tag.getInt("SignedRedstoneStrength"));
    }

    public Vector3dc getCurrentNormal() {
        return this.currentNormal;
    }

    public int getRedstoneStrength() {
        return Mth.abs((int)this.signedRedstoneStrength);
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (this.subLevelReference.get() != null) {
            SimLang.number(Math.abs(this.getAdjustedVelocity())).text(" m/s").forGoggles(tooltip);
        }
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    public float getAdjustedVelocity() {
        return this.adjustedVelocity;
    }

    public static class VelocitySensorScrollValueBehaviour
    extends ScrollValueBehaviour {
        private boolean towards;

        public VelocitySensorScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
            super(label, be, slot);
        }

        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ImmutableList rows = ImmutableList.of((Object)SimLang.translate("velocity_sensor.selection.away", new Object[0]).component(), (Object)SimLang.translate("velocity_sensor.selection.towards", new Object[0]).component());
            return new ValueSettingsBoard(this.label, this.max, 10, (List)rows, new ValueSettingsFormatter(this::formatValue));
        }

        public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
            return SimLang.number(settings.value()).component().append(" m/s");
        }

        public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlDown) {
            super.setValueSettings(player, valueSetting, ctrlDown);
            this.towards = valueSetting.row() == 1;
        }

        public int getValue() {
            return super.getValue() * (this.towards ? 1 : -1);
        }

        public ValueSettingsBehaviour.ValueSettings getValueSettings() {
            return new ValueSettingsBehaviour.ValueSettings(this.towards ? 1 : 0, this.value);
        }

        public boolean isTowards() {
            return this.towards;
        }

        public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
            this.towards = nbt.getBoolean("ScrollValueTowards");
            super.read(nbt, registries, clientPacket);
        }

        public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
            nbt.putBoolean("ScrollValueTowards", this.towards);
            super.write(nbt, registries, clientPacket);
        }
    }

    private static class VelocitySensorValueBoxTransform
    extends ValueBoxTransform.Sided {
        private VelocitySensorValueBoxTransform() {
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)12.8);
        }

        public float getScale() {
            return 0.35f;
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return AbstractDirectionalAxisBlock.getAxis(state) == direction.getAxis();
        }
    }
}
