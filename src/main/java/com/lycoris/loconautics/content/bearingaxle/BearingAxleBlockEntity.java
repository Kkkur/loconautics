package com.lycoris.loconautics.content.bearingaxle;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.foundation.LoconauticsLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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

    /** Gear multiplier (1/2/4/8): in REALISTIC mode it raises the maximum pullable mass proportionally, and in
     *  BOTH modes it multiplies the SU stress impact proportionally. Stored as the multiplier itself; the scroll
     *  UI ({@link #gearScroll}) holds the exponent (0..3). */
    private int gearMultiplier = 1;
    /** Wrench-scroll UI for {@link #gearMultiplier}. Holds the exponent 0..3; the displayed value is 1/2/4/8. */
    private ScrollValueBehaviour gearScroll;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        gearScroll = new ScrollValueBehaviour(
                LoconauticsLang.translate("gui.goggles.gear_ratio").component(),
                this, new GearValueBox());
        gearScroll.between(0, 3); // exponent: 0->x1, 1->x2, 2->x4, 3->x8
        gearScroll.value = 0;
        gearScroll.withFormatter(exp -> "x" + (1 << exp));
        gearScroll.withCallback(exp -> onGearChanged(1 << exp));
        behaviours.add(gearScroll);
    }

    private void onGearChanged(int multiplier) {
        if (multiplier == this.gearMultiplier) {
            return;
        }
        this.gearMultiplier = multiplier;
        pushStressToNetwork();
    }

    /** Current gear multiplier (1/2/4/8). Kept in sync with the scroll UI's exponent. */
    public int getMultiplier() {
        if (gearScroll != null) {
            gearMultiplier = 1 << gearScroll.getValue();
        }
        return gearMultiplier;
    }

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
        pushStressToNetwork();
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
        pushStressToNetwork();
    }

    /**
     * Recomputes this axle's stress impact and pushes it into the kinetic network. Shared by every input that
     * changes the impact (train mass, slope, gear multiplier).
     *
     * <p>{@code calculateStressApplied()} refreshes {@code lastStressApplied} from the new state. We must push that
     * value into the network with {@code updateStressFor(this, stress)}: the network sums each member's STORED
     * stress (set only when a member is added or via updateStressFor) — {@code updateNetwork()}/{@code
     * calculateStress()} re-read that stale stored value and never our new impact, so a heavier train or higher
     * gear never raised stress. {@code updateStressFor} overwrites the stored value, recalculates and syncs.
     */
    private void pushStressToNetwork() {
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
     * Formula: (BASE_IMPACT + (totalMassKg / MASS_DIVISOR) + slope) * gearMultiplier
     *   BASE_IMPACT    — flat base cost always present regardless of mass (default 1.0 SU)
     *   MASS_DIVISOR   — kg per 1 SU of added impact (default 500 kg/SU)
     *   slope          — extra SU per degree of rail incline (gated by slopeEffects)
     *   gearMultiplier — x1/x2/x4/x8: a higher gear raises the mass cap but costs proportionally more SU
     *
     * Example: 5000 kg train, x1, defaults → (1.0 + 5000/500) * 1 = 11.0 SU at 1 RPM. At x4 → 44.0 SU.
     */
    @Override
    public float calculateStressApplied() {
        double divisor = Config.MASS_DIVISOR.get();
        double base    = Config.BASE_IMPACT.get();
        // Slope term: extra stress added purely from the rail incline angle (SU per degree). The steeper the angle,
        // the more stress the axle consumes. Gated by the slope-effects config.
        double slope = Config.SLOPE_EFFECTS_ENABLED.get()
                ? Config.SLOPE_STRESS_FACTOR.get() * slopeAngleDeg
                : 0.0;
        double impact = (base + (totalMassKg / divisor) + slope) * getMultiplier();
        this.lastStressApplied = (float) impact;
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
        double cap = Config.BASE_MAX_PULLABLE_MASS.get() * getMultiplier();
        boolean overCap = realistic && totalMassKg > cap;

        // Train Weight (red when over the realistic pullable cap, gold when carrying, grey when empty)
        ChatFormatting weightColor = overCap ? ChatFormatting.RED
                : (totalMassKg > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY);
        LoconauticsLang.translate("gui.goggles.train_weight")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.number(totalMassKg)
                .translate("gui.goggles.unit_kg")
                .style(weightColor)
                .forGoggles(tooltip, 1);

        // Gear Ratio (x1/x2/x4/x8)
        LoconauticsLang.translate("gui.goggles.gear_ratio")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        LoconauticsLang.builder().text("x" + getMultiplier())
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);

        // Max Pullable Mass — REALISTIC mode only (ARCADE has no cap)
        if (realistic) {
            LoconauticsLang.translate("gui.goggles.max_pullable")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            LoconauticsLang.number(cap)
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
        // The gear multiplier is also stored by the scroll behaviour (as its exponent); persist the resolved
        // value here so client goggles and external readers see it without decoding the behaviour.
        tag.putInt("GearMultiplier", getMultiplier());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        totalMassKg = tag.getDouble("TotalMassKg");
        slopeAngleDeg = tag.getDouble("SlopeAngle");
        if (tag.contains("GearMultiplier")) {
            int m = tag.getInt("GearMultiplier");
            gearMultiplier = (m == 1 || m == 2 || m == 4 || m == 8) ? m : 1;
        }
    }

    // ---------------------------------------------------------------------------
    // Boilerplate
    // ---------------------------------------------------------------------------

    public BearingAxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Value-box placement for the gear-ratio scroll UI: on any face perpendicular to the axle's rotation axis. */
    private static class GearValueBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8.0, 8.0, 12.5);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() != state.getValue(RotatedPillarBlock.AXIS);
        }
    }
}
