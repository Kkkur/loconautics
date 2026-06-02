/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.lang.LangBuilder
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
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
import com.simibubi.create.content.kinetics.gauge.GaugeObservedPacket;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.List;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class StressGaugeBlockEntity
extends GaugeBlockEntity {
    public AbstractComputerBehaviour computerBehaviour;
    static BlockPos lastSent;

    public StressGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.STRESSOMETER.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
        this.registerAwardables(behaviours, AllAdvancements.STRESSOMETER, AllAdvancements.STRESSOMETER_MAXED);
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        if (this.computerBehaviour.hasAttachedComputer()) {
            this.computerBehaviour.prepareComputerEvent(this.makeComputerKineticsChangeEvent());
        }
        this.dialTarget = !IRotate.StressImpact.isEnabled() ? 0.0f : (this.isOverStressed() ? 1.125f : (maxStress == 0.0f ? 0.0f : currentStress / maxStress));
        if (this.dialTarget > 0.0f) {
            this.color = this.dialTarget < 0.5f ? Color.mixColors((int)65280, (int)0xFFFF00, (float)(this.dialTarget * 2.0f)) : (this.dialTarget < 1.0f ? Color.mixColors((int)0xFFFF00, (int)0xFF0000, (float)(this.dialTarget * 2.0f - 1.0f)) : 0xFF0000);
        }
        this.sendData();
        this.setChanged();
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        if (this.getSpeed() == 0.0f) {
            this.dialTarget = 0.0f;
            this.setChanged();
            return;
        }
        this.updateFromNetwork(this.capacity, this.stress, this.getOrCreateNetwork().getSize());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!IRotate.StressImpact.isEnabled()) {
            return false;
        }
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        double capacity = this.getNetworkCapacity();
        double stressFraction = (double)this.getNetworkStress() / (capacity == 0.0 ? 1.0 : capacity);
        CreateLang.translate("gui.stressometer.title", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        if (this.getTheoreticalSpeed() == 0.0f) {
            CreateLang.text(TooltipHelper.makeProgressBar(3, 0)).translate("gui.stressometer.no_rotation", new Object[0]).style(ChatFormatting.DARK_GRAY).forGoggles(tooltip);
        } else {
            IRotate.StressImpact.getFormattedStressText(stressFraction).forGoggles(tooltip);
            CreateLang.translate("gui.stressometer.capacity", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
            double remainingCapacity = capacity - (double)this.getNetworkStress();
            LangBuilder su = CreateLang.translate("generic.unit.stress", new Object[0]);
            LangBuilder stressTip = CreateLang.number(remainingCapacity).add(su).style(IRotate.StressImpact.of(stressFraction).getRelativeColor());
            if (remainingCapacity != capacity) {
                stressTip.text(ChatFormatting.GRAY, " / ").add(CreateLang.number(capacity).add(su).style(ChatFormatting.DARK_GRAY));
            }
            stressTip.forGoggles(tooltip, 1);
        }
        if (!this.worldPosition.equals((Object)lastSent)) {
            lastSent = this.worldPosition;
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new GaugeObservedPacket(lastSent));
        }
        return true;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (clientPacket && this.worldPosition != null && this.worldPosition.equals((Object)lastSent)) {
            lastSent = null;
        }
    }

    public float getNetworkStress() {
        return this.stress;
    }

    public float getNetworkCapacity() {
        return this.capacity;
    }

    public void onObserved() {
        this.award(AllAdvancements.STRESSOMETER);
        if (Mth.equal((float)this.dialTarget, (float)1.0f)) {
            this.award(AllAdvancements.STRESSOMETER_MAXED);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }
}
