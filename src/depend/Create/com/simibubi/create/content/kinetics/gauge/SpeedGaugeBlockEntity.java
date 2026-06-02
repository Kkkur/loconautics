/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.kinetics.gauge;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.List;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SpeedGaugeBlockEntity
extends GaugeBlockEntity {
    public AbstractComputerBehaviour computerBehaviour;

    public SpeedGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.SPEEDOMETER.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        if (this.computerBehaviour.hasAttachedComputer()) {
            this.computerBehaviour.prepareComputerEvent(this.makeComputerKineticsChangeEvent());
        }
        float speed = Math.abs(this.getSpeed());
        this.dialTarget = SpeedGaugeBlockEntity.getDialTarget(speed);
        this.color = Color.mixColors((int)IRotate.SpeedLevel.of(speed).getColor(), (int)0xFFFFFF, (float)0.25f);
        this.setChanged();
    }

    public static float getDialTarget(float speed) {
        speed = Math.abs(speed);
        float medium = ((Double)AllConfigs.server().kinetics.mediumSpeed.get()).floatValue();
        float fast = ((Double)AllConfigs.server().kinetics.fastSpeed.get()).floatValue();
        float max = ((Integer)AllConfigs.server().kinetics.maxRotationSpeed.get()).floatValue();
        float target = 0.0f;
        target = speed == 0.0f ? 0.0f : (speed < medium ? Mth.lerp((float)(speed / medium), (float)0.0f, (float)0.45f) : (speed < fast ? Mth.lerp((float)((speed - medium) / (fast - medium)), (float)0.45f, (float)0.75f) : Mth.lerp((float)((speed - fast) / (max - fast)), (float)0.75f, (float)1.125f)));
        return target;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        CreateLang.translate("gui.speedometer.title", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        IRotate.SpeedLevel.getFormattedSpeedText(this.speed, this.isOverStressed()).forGoggles(tooltip);
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }
}
