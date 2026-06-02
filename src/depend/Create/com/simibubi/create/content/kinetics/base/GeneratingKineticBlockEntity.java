/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GeneratingKineticBlockEntity
extends KineticBlockEntity {
    public boolean reActivateSource;

    public GeneratingKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void notifyStressCapacityChange(float capacity) {
        this.getOrCreateNetwork().updateCapacityFor(this, capacity);
    }

    @Override
    public void removeSource() {
        if (this.hasSource() && this.isSource()) {
            this.reActivateSource = true;
        }
        super.removeSource();
    }

    @Override
    public void setSource(BlockPos source) {
        super.setSource(source);
        BlockEntity blockEntity = this.level.getBlockEntity(source);
        if (!(blockEntity instanceof KineticBlockEntity)) {
            return;
        }
        KineticBlockEntity sourceBE = (KineticBlockEntity)blockEntity;
        if (this.reActivateSource && Math.abs(sourceBE.getSpeed()) >= Math.abs(this.getGeneratedSpeed())) {
            this.reActivateSource = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.reActivateSource) {
            this.updateGeneratedRotation();
            this.reActivateSource = false;
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (!IRotate.StressImpact.isEnabled()) {
            return added;
        }
        float stressBase = this.calculateAddedStressCapacity();
        if (Mth.equal((float)stressBase, (float)0.0f)) {
            return added;
        }
        CreateLang.translate("gui.goggles.generator_stats", new Object[0]).forGoggles(tooltip);
        CreateLang.translate("tooltip.capacityProvided", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        float speed = this.getTheoreticalSpeed();
        if (speed != this.getGeneratedSpeed() && speed != 0.0f) {
            stressBase *= this.getGeneratedSpeed() / speed;
        }
        float stressTotal = Math.abs(stressBase * speed);
        CreateLang.number(stressTotal).translate("generic.unit.stress", new Object[0]).style(ChatFormatting.AQUA).space().add(CreateLang.translate("gui.goggles.at_current_speed", new Object[0]).style(ChatFormatting.DARK_GRAY)).forGoggles(tooltip, 1);
        return true;
    }

    public void updateGeneratedRotation() {
        float speed = this.getGeneratedSpeed();
        float prevSpeed = this.speed;
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (prevSpeed != speed) {
            IRotate.SpeedLevel levelafter;
            IRotate.SpeedLevel levelBefore;
            if (!this.hasSource() && (levelBefore = IRotate.SpeedLevel.of(this.speed)) != (levelafter = IRotate.SpeedLevel.of(speed))) {
                this.effects.queueRotationIndicators();
            }
            this.applyNewSpeed(prevSpeed, speed);
        }
        if (this.hasNetwork() && speed != 0.0f) {
            KineticNetwork network = this.getOrCreateNetwork();
            this.notifyStressCapacityChange(this.calculateAddedStressCapacity());
            this.getOrCreateNetwork().updateStressFor(this, this.calculateStressApplied());
            network.updateStress();
        }
        this.onSpeedChanged(prevSpeed);
        this.sendData();
    }

    public void applyNewSpeed(float prevSpeed, float speed) {
        if (speed == 0.0f) {
            if (this.hasSource()) {
                this.notifyStressCapacityChange(0.0f);
                this.getOrCreateNetwork().updateStressFor(this, this.calculateStressApplied());
                return;
            }
            this.detachKinetics();
            this.setSpeed(0.0f);
            this.setNetwork(null);
            return;
        }
        if (prevSpeed == 0.0f) {
            this.setSpeed(speed);
            this.setNetwork(this.createNetworkId());
            this.attachKinetics();
            return;
        }
        if (this.hasSource()) {
            if (Math.abs(prevSpeed) >= Math.abs(speed)) {
                if (Math.signum(prevSpeed) != Math.signum(speed)) {
                    this.level.destroyBlock(this.worldPosition, true);
                }
                return;
            }
            this.detachKinetics();
            this.setSpeed(speed);
            this.source = null;
            this.setNetwork(this.createNetworkId());
            this.attachKinetics();
            return;
        }
        this.detachKinetics();
        this.setSpeed(speed);
        this.attachKinetics();
    }

    public Long createNetworkId() {
        return this.worldPosition.asLong();
    }
}
