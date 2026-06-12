package com.lycoris.loconautics.content.bearingaxle;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.foundation.LoconauticsLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
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
     * Gear multiplier selector, wrench-scrolled in-world. Stores the exponent (0..3); the actual multiplier is
     * {@code 1 << value} → x1/x2/x4/x8 (see {@link #getMultiplier()}). In REALISTIC mode the multiplier raises the
     * maximum pullable consist mass proportionally; in both modes it scales the axle's SU stress impact by the same
     * factor. Its value is persisted and synced by Create's behaviour serialization (NBT key {@code ScrollValue}).
     */
    private ScrollValueBehaviour gearRatio;

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

    /** The active gear multiplier: x1/x2/x4/x8 (defaults to 1 before behaviours are attached). */
    public int getMultiplier() {
        return gearRatio == null ? 1 : (1 << gearRatio.getValue());
    }

    /**
     * Recomputes and pushes the stress impact through the network after the gear multiplier changes. BEs inside Sable
     * sub-levels don't tick, so we do here what {@code tick()} would — exactly the workaround {@link #setTrainMass}
     * uses. {@link ScrollValueBehaviour#setValue} already follows the callback with {@code setChanged()/sendData()}.
     */
    private void onGearChanged() {
        float stress = calculateStressApplied();
        if (this.hasNetwork()) {
            this.getOrCreateNetwork().updateStressFor(this, stress);
        }
        this.networkDirty = false;
    }

    // ---------------------------------------------------------------------------
    // Network stress readout — used by the boiler burn-rate formula and the
    // goggle tooltip. Both values are safe to call from any server-side context.
    // Reads the stress/capacity fields pushed by KineticBlockEntity.updateFromNetwork(),
    // mirroring exactly how StressGaugeBlockEntity.getNetworkStress() works.
    // ---------------------------------------------------------------------------

    /**
     * Returns the kinetic network's current stress load in absolute SU.
     * Reads the {@code stress} field pushed to this BE by
     * {@code KineticBlockEntity.updateFromNetwork()}.
     */
    public float getNetworkStressAbsolute() {
        return stress;
    }

    /**
     * Returns the fraction of network capacity currently consumed
     * (stress / capacity). 1.0 = fully loaded; >1.0 = overstressed.
     * Returns 0 when capacity is zero (network not yet active).
     *
     * <p>This is the primary input to the boiler burn-rate formula alongside
     * {@link #getTrainMass()}.
     */
    public float getNetworkStressRatio() {
        if (capacity <= 0f) return 0f;
        return stress / capacity;
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
        // the more stress the axle consumes.
        double slope = Config.SLOPE_EFFECTS_ENABLED.get()
                ? Config.SLOPE_STRESS_FACTOR.get() * slopeAngleDeg
                : 0.0;
        // The gear multiplier scales the WHOLE impact: a higher gear hauls a proportionally larger mass cap and
        // costs proportionally more SU. Identical in both physics modes (only the mass cap differs by mode).
        this.lastStressApplied = (float) ((base + (totalMassKg / divisor) + slope) * getMultiplier());
        return this.lastStressApplied;
    }

    // ---------------------------------------------------------------------------
    // Goggle tooltip
    // ---------------------------------------------------------------------------

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        LoconauticsLang.translate("gui.goggles.bearing_axle_stats").forGoggles(tooltip);

        boolean realistic = Config.PHYSICS_MODE.get() == Config.PhysicsMode.REALISTIC;
        double maxPullable = Config.BASE_MAX_PULLABLE_MASS.get() * getMultiplier();
        boolean overCap = realistic && totalMassKg > maxPullable;

        // Gear Ratio (x1/x2/x4/x8)
        LoconauticsLang.translate("gui.goggles.gear_ratio")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.builder().text("x" + getMultiplier())
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);

        // Train Weight — red when over the (multiplier-scaled) cap in REALISTIC mode.
        LoconauticsLang.translate("gui.goggles.train_weight")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(totalMassKg)
                .translate("gui.goggles.unit_kg")
                .style(overCap ? ChatFormatting.RED
                        : totalMassKg > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip, 1);

        // Max Pullable (REALISTIC mode only)
        if (realistic) {
            LoconauticsLang.translate("gui.goggles.max_pullable")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            LoconauticsLang.number(maxPullable)
                    .translate("gui.goggles.unit_kg")
                    .style(overCap ? ChatFormatting.RED : ChatFormatting.GREEN)
                    .forGoggles(tooltip, 1);
        }

        // Stress Impact (at 1 RPM)
        float impact = calculateStressApplied();
        LoconauticsLang.translate("gui.goggles.stress_impact")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(impact)
                .translate("gui.goggles.unit_su")
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);

        // Network Stress (absolute SU)
        float stressAbs = getNetworkStressAbsolute();
        LoconauticsLang.translate("gui.goggles.network_stress")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(stressAbs)
                .translate("gui.goggles.unit_su")
                .style(stressAbs > 0 ? ChatFormatting.YELLOW : ChatFormatting.DARK_GRAY)
                .forGoggles(tooltip, 1);

        // Network Stress Ratio (load / capacity)
        float stressRatio = getNetworkStressRatio();
        LoconauticsLang.translate("gui.goggles.network_stress_ratio")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(stressRatio * 100f)
                .translate("gui.goggles.unit_percent")
                .style(stressRatio >= 1f ? ChatFormatting.RED
                        : stressRatio >= 0.75f ? ChatFormatting.GOLD
                        : ChatFormatting.GREEN)
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
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // Wrench-scrolled gear multiplier (x1/x2/x4/x8), stored as the exponent 0..3. Recomputes stress on change.
        gearRatio = new ScrollValueBehaviour(
                LoconauticsLang.translate("gui.goggles.gear_ratio").component(),
                this, new CenteredSideValueBoxTransform());
        gearRatio.between(0, 3);
        gearRatio.withFormatter(exp -> "x" + (1 << exp));
        gearRatio.requiresWrench();
        gearRatio.withCallback(exp -> onGearChanged());
        behaviours.add(gearRatio);
    }
}