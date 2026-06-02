/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.crank;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ValveHandleBlockEntity
extends HandCrankBlockEntity {
    public ScrollValueBehaviour angleInput;
    public int cooldown;
    protected int startAngle;
    protected int targetAngle;
    protected int totalUseTicks;

    public ValveHandleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.angleInput = new ValveHandleScrollValueBehaviour(this).between(-180, 180);
        behaviours.add(this.angleInput);
        this.angleInput.onlyActiveWhen(this::showValue);
        this.angleInput.setValue(45);
    }

    @Override
    protected boolean clockwise() {
        return this.angleInput.getValue() < 0 ^ this.backwards;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("TotalUseTicks", this.totalUseTicks);
        compound.putInt("StartAngle", this.startAngle);
        compound.putInt("TargetAngle", this.targetAngle);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.totalUseTicks = compound.getInt("TotalUseTicks");
        this.startAngle = compound.getInt("StartAngle");
        this.targetAngle = compound.getInt("TargetAngle");
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inUse == 0 && this.cooldown > 0) {
            --this.cooldown;
        }
        this.independentAngle = this.level.isClientSide() ? this.getIndependentAngle(0.0f) : 0.0f;
    }

    @Override
    public float getIndependentAngle(float partialTicks) {
        if (this.inUse == 0 && this.source != null && this.getSpeed() != 0.0f) {
            return AngleHelper.deg((double)KineticBlockEntityRenderer.getAngleForBe(this, this.worldPosition, KineticBlockEntityRenderer.getRotationAxisOf(this)));
        }
        int step = this.getBlockState().getOptionalValue((Property)ValveHandleBlock.FACING).orElse(Direction.SOUTH).getAxisDirection().getStep();
        return (this.inUse > 0 && this.totalUseTicks > 0 ? Mth.lerp((float)(Math.min((float)this.totalUseTicks, (float)(this.totalUseTicks - this.inUse) + partialTicks) / (float)this.totalUseTicks), (float)this.startAngle, (float)this.targetAngle) : (float)this.targetAngle) * (float)(this.backwards ? -1 : 1) * (float)step;
    }

    public boolean showValue() {
        return this.inUse == 0;
    }

    public boolean activate(boolean sneak) {
        if (this.getTheoreticalSpeed() != 0.0f) {
            return false;
        }
        if (this.inUse > 0 || this.cooldown > 0) {
            return false;
        }
        if (this.level.isClientSide) {
            return true;
        }
        int value = this.angleInput.getValue();
        int target = Math.abs(value);
        int rotationSpeed = ((ValveHandleBlock)AllBlocks.COPPER_VALVE_HANDLE.get()).getRotationSpeed();
        double degreesPerTick = KineticBlockEntity.convertToAngular(rotationSpeed);
        this.inUse = (int)Math.ceil((double)target / degreesPerTick) + 2;
        this.startAngle = (int)(this.independentAngle % 90.0f + 360.0f) % 90;
        this.targetAngle = Math.round((float)(this.startAngle + (target > 135 ? 180 : 90) * Mth.sign((double)value)) / 90.0f) * 90;
        this.totalUseTicks = this.inUse;
        this.backwards = sneak;
        this.sequenceContext = SequencedGearshiftBlockEntity.SequenceContext.fromGearshift(SequencerInstructions.TURN_ANGLE, rotationSpeed, target);
        this.updateGeneratedRotation();
        this.cooldown = 4;
        return true;
    }

    @Override
    protected void copySequenceContextFrom(KineticBlockEntity sourceBE) {
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public SuperByteBuffer getRenderedHandle() {
        return CachedBuffers.block((BlockState)this.getBlockState());
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean shouldRenderShaft() {
        return false;
    }

    public static class ValveHandleScrollValueBehaviour
    extends ScrollValueBehaviour {
        public ValveHandleScrollValueBehaviour(SmartBlockEntity be) {
            super((Component)CreateLang.translateDirect("kinetics.valve_handle.rotated_angle", new Object[0]), be, new ValveHandleValueBox());
            this.withFormatter(v -> String.valueOf(Math.abs(v)) + CreateLang.translateDirect("generic.unit.degrees", new Object[0]).getString());
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ImmutableList rows = ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD), (Object)Component.literal((String)"\u27f2").withStyle(ChatFormatting.BOLD));
            return new ValueSettingsBoard(this.label, 180, 45, (List<Component>)rows, new ValueSettingsFormatter(this::formatValue));
        }

        @Override
        public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlHeld) {
            int value = Math.max(1, valueSetting.value());
            if (!valueSetting.equals(this.getValueSettings())) {
                this.playFeedbackSound(this);
            }
            this.setValue(valueSetting.row() == 0 ? -value : value);
        }

        @Override
        public ValueSettingsBehaviour.ValueSettings getValueSettings() {
            return new ValueSettingsBehaviour.ValueSettings(this.value < 0 ? 0 : 1, Math.abs(this.value));
        }

        public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
            return CreateLang.number(Math.max(1, Math.abs(settings.value()))).add(CreateLang.translateDirect("generic.unit.degrees", new Object[0])).component();
        }

        @Override
        public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
            if (this.getWorld().isClientSide) {
                return;
            }
            BlockState blockState = this.blockEntity.getBlockState();
            Block block = blockState.getBlock();
            if (block instanceof ValveHandleBlock) {
                ValveHandleBlock vhb = (ValveHandleBlock)block;
                vhb.clicked(this.getWorld(), this.getPos(), blockState, player, hand);
            }
        }
    }

    public static class ValveHandleValueBox
    extends ValueBoxTransform.Sided {
        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction == state.getValue((Property)ValveHandleBlock.FACING);
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)4.5);
        }

        @Override
        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
        }
    }
}
