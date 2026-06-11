package com.lycoris.loconautics.content.bearingaxle;

import com.lycoris.loconautics.Config;
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
    // Mass — pushed by SableTrainDriver.updateAxleMass from the server game tick.
    // Block entities inside Sable sub-levels do not tick normally, so mass updates
    // come from the server game tick handler, not from this BE's own tick().
    // ---------------------------------------------------------------------------

    private double totalMassKg = 0.0;
    /** Rail incline (degrees) the axle currently sits on, pushed by {@code SableTrainDriver}. Adds slope stress. */
    private double slopeAngleDeg = 0.0;

    /**
     * Called by {@code SableTrainDriver.updateAxleMass} (every 10 ticks, when the summed block mass changes) to
     * update the train weight and recompute stress.
     *
     * <p>BEs inside Sable sub-levels do NOT tick, so the usual {@code networkDirty} → {@code tick()} refresh never
     * runs — that's why the weight previously only "took effect" when an RPM change happened to re-evaluate the
     * kinetic network. Here we do what {@code tick()} would: force the network to recompute stress NOW, and
     * {@code sendData()} so the client goggle tooltip updates live as blocks are placed/broken.
     */
    public void setTrainMass(double massKg) {
        if (massKg == this.totalMassKg) {
            return;
        }
        this.totalMassKg = massKg;
        // calculateStressApplied() refreshes lastStressApplied from the new mass. We must push that value into the
        // network with updateStressFor(this, stress): the network sums each member's STORED stress
        // (members.get(be)), set only when a member is added or via updateStressFor — updateNetwork()/calculateStress()
        // re-read that stale stored value and never our new impact, so the heavier train never raised stress (and so
        // never overstressed). updateStressFor overwrites the stored value, then recalculates + syncs to the source.
        float stress = calculateStressApplied();
        if (this.hasNetwork()) {
            this.getOrCreateNetwork().updateStressFor(this, stress);
        }
        this.networkDirty = false;
        this.setChanged();
        this.sendData();
    }

    public double getTrainMass() {
        return totalMassKg;
    }

    /**
     * Sets the rail incline (degrees) the axle sits on, pushed once per game tick by {@code SableTrainDriver}. A
     * steeper angle raises the axle's stress impact. Recomputes stress through the network the same way
     * {@link #setTrainMass} does, with an epsilon guard so a steady incline doesn't spam network updates.
     */
    public void setSlopeAngle(double degrees) {
        double a = Math.max(0.0, degrees);
        if (Math.abs(a - this.slopeAngleDeg) < 1.0e-3) {
            return;
        }
        this.slopeAngleDeg = a;
        float stress = calculateStressApplied();
        if (this.hasNetwork()) {
            this.getOrCreateNetwork().updateStressFor(this, stress);
        }
        this.networkDirty = false;
        this.setChanged();
        this.sendData();
    }

    // ---------------------------------------------------------------------------
    // Stress formula
    // ---------------------------------------------------------------------------

    /**
     * Stress impact at 1 RPM. Full SU = impact * abs(speed).
     *
     * Formula: BASE_IMPACT + (totalMassKg / MASS_DIVISOR)
     *   BASE_IMPACT  — flat base cost always present regardless of mass (default 1.0 SU)
     *   MASS_DIVISOR — kg per 1 SU of added impact (default 50 kg/SU)
     *
     * Example: 500 kg train, defaults → 1.0 + (500/50) = 11.0 SU at 1 RPM.
     */
    @Override
    public float calculateStressApplied() {
        double divisor = Config.MASS_DIVISOR.get();
        double base    = Config.BASE_IMPACT.get();
        // Slope term: extra stress added purely from the rail incline angle (SU per degree). The steeper the angle,
        // the more stress the axle consumes. Gated by the realism config.
        double slope = (Config.REALISM_ENABLED.get() && Config.SLOPE_EFFECTS_ENABLED.get())
                ? Config.SLOPE_STRESS_FACTOR.get() * slopeAngleDeg
                : 0.0;
        this.lastStressApplied = (float) (base + (totalMassKg / divisor) + slope);
        return this.lastStressApplied;
    }

    // ---------------------------------------------------------------------------
    // Goggle tooltip
    // ---------------------------------------------------------------------------

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        LoconauticsLang.translate("gui.goggles.bearing_axle_stats").forGoggles(tooltip);

        // Train Weight
        LoconauticsLang.translate("gui.goggles.train_weight")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(totalMassKg)
                .translate("gui.goggles.unit_kg")
                .style(totalMassKg > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip, 1);

        // Stress Impact (at 1 RPM)
        float impact = calculateStressApplied();
        LoconauticsLang.translate("gui.goggles.stress_impact")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(impact)
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
        // Sync the incline too, so the CLIENT goggle's calculateStressApplied() shows the slope stress term
        // (without this the client always read 0 and the stress impact never appeared to change on slopes).
        tag.putDouble("SlopeAngle", slopeAngleDeg);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        totalMassKg = tag.getDouble("TotalMassKg");
        slopeAngleDeg = tag.getDouble("SlopeAngle");
    }

    // ---------------------------------------------------------------------------
    // Boilerplate
    // ---------------------------------------------------------------------------

    public BearingAxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
}