package com.lycoris.loconautics.content.bearingaxle;

import com.lycoris.loconautics.foundation.LoconauticsLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BearingAxleBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    // ---------------------------------------------------------------------------
    // Mass field — physics friend hooks in here via setTrainMass()
    // ---------------------------------------------------------------------------

    private double totalMassKg = 0.0;

    // Ticks between stress recalculations (20 ticks = 1 second)
    private static final int STRESS_UPDATE_INTERVAL = 20;
    private int stressUpdateTicker = 0;

    /**
     * Called by the physics assembly pipeline (Phase 5) after train mass is known,
     * and whenever mass changes at runtime (blocks added/removed from the train).
     * Immediately marks the network dirty so stress updates next tick.
     */
    public void setTrainMass(double massKg) {
        this.totalMassKg = massKg;
        this.lastStressApplied = calculateStressApplied();
        this.networkDirty = true;
        this.setChanged();
    }

    public double getTrainMass() {
        return totalMassKg;
    }

    // ---------------------------------------------------------------------------
    // Stress — formula left open for Phase 5 / config wiring
    // ---------------------------------------------------------------------------

    /**
     * Stress impact at 1 RPM. Full SU = impact * abs(speed).
     *
     * TODO Phase 5: replace placeholder with mass-based formula:
     *   return (float) Math.max(Config.BASE_IMPACT.get(), totalMassKg / Config.MASS_DIVISOR.get());
     */
    @Override
    public float calculateStressApplied() {
        // Placeholder: flat 4.0 SU impact (matches BlockStressValues registration in Loconautics.java)
        // Replace this body in Phase 5 once Config and mass wiring are done.
        this.lastStressApplied = 4.0f;
        return this.lastStressApplied;
    }

    // ---------------------------------------------------------------------------
    // Tick — periodic stress refresh for real-time mass changes
    // ---------------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide)
            return;

        if (++stressUpdateTicker >= STRESS_UPDATE_INTERVAL) {
            stressUpdateTicker = 0;
            float newStress = calculateStressApplied();
            if (newStress != lastStressApplied) {
                lastStressApplied = newStress;
                networkDirty = true;
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Goggle tooltip
    // ---------------------------------------------------------------------------

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        LoconauticsLang.translate("gui.goggles.bearing_axle_stats").forGoggles(tooltip);

        // Train Weight
        LoconauticsLang.translate("gui.goggles.train_weight")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(totalMassKg)
                .translate("gui.goggles.unit_kg")
                .style(totalMassKg > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip, 1);

        // Current SU (live)
        float currentSU = calculateStressApplied() * Math.abs(getSpeed());
        LoconauticsLang.translate("gui.goggles.current_su")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(currentSU)
                .translate("gui.goggles.unit_su")
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);

        // RPM (live)
        LoconauticsLang.translate("gui.goggles.rpm")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(Math.abs(getSpeed()))
                .translate("gui.goggles.unit_rpm")
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);

        return true;
    }

    // ---------------------------------------------------------------------------
    // NBT
    // ---------------------------------------------------------------------------

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putDouble("TotalMassKg", totalMassKg);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        totalMassKg = tag.getDouble("TotalMassKg");
    }

    // ---------------------------------------------------------------------------
    // Boilerplate
    // ---------------------------------------------------------------------------

    public BearingAxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // behaviours added in future phases
    }
}