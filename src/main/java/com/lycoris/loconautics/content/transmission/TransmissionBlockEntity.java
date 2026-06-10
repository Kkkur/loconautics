package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Block entity for the Transmission.
 *
 * Two redstone inputs (received via Create's redstone link network):
 *   Speed frequency     → analog signal 0–15 → controls output RPM
 *   Direction frequency → binary ON/OFF       → when ON, reverses output direction
 *
 * The block has two shaft faces along its rotation axis. Whichever side has a
 * live rotation source becomes the input; the other becomes the output at the
 * redstone-controlled absolute RPM.
 *
 * Bidirectionality is resolved via {@link #propagateRotationTo}:
 *   - Propagating toward the output face → return ratio that yields targetRPM.
 *   - Propagating toward the input face  → return 1.0 (plain shaft passthrough).
 *   - Both faces live on same network    → return 0.0 (disengage, avoid loop break).
 *   - Both faces live, different networks → prefer the network with more SU headroom.
 *
 * When speed signal == 0 → output = 0 (disengaged).
 */
public class TransmissionBlockEntity extends KineticBlockEntity implements MenuProvider {

    // ------------------------------------------------------------------ state

    /** Current speed redstone signal received from the link network (0–15). */
    private int redstonePower = 0;

    /** Whether the direction-override signal is active. */
    private boolean directionActive = false;

    // ------------------------------------------------------------------ frequency slots

    private ItemStack speedFreqFirst  = ItemStack.EMPTY;
    private ItemStack speedFreqSecond = ItemStack.EMPTY;
    private ItemStack dirFreqFirst    = ItemStack.EMPTY;
    private ItemStack dirFreqSecond   = ItemStack.EMPTY;

    // ------------------------------------------------------------------ link receiver entries

    @Nullable private SpeedLinkReceiver     speedReceiver;
    @Nullable private DirectionLinkReceiver dirReceiver;

    // ------------------------------------------------------------------ constructor

    public TransmissionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ------------------------------------------------------------------ lifecycle

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) return;
        registerReceivers();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        unregisterReceivers();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unregisterReceivers();
    }

    // ------------------------------------------------------------------ stress

    @Override
    public float calculateStressApplied() {
        float impact = Config.TRANSMISSION_SU_IMPACT.get().floatValue();
        this.lastStressApplied = impact;
        return impact;
    }

    // ------------------------------------------------------------------ propagation

    /**
     * Called by Create's {@link com.simibubi.create.content.kinetics.RotationPropagator}
     * whenever it evaluates whether and how fast rotation moves from {@code this} to
     * {@code target}.
     *
     * Returns the speed multiplier such that:
     *   targetSpeed = thisSpeed * multiplier
     *
     * For our output face we want an absolute target RPM, so:
     *   multiplier = targetRPM / thisSpeed
     *
     * For the input face (propagating back toward the source) we return 1.0 so the
     * input network sees us as a plain shaft connection.
     */
    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom,
                                     BlockState stateTo, BlockPos diff,
                                     boolean connectedViaAxes, boolean connectedViaCogs) {
        if (!connectedViaAxes) return 0f;
        if (redstonePower == 0) return 0f;

        float mySpeed = getTheoreticalSpeed();

        // Determine which face the target is on
        Direction facing = stateFrom.getValue(TransmissionBlock.FACING);
        Direction towardTarget = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());

        boolean targetIsOnOutputFace = isOutputFace(towardTarget, facing);

        if (!targetIsOnOutputFace) {
            // Target is on our input side — propagate as a plain shaft
            return mySpeed == 0f ? 0f : 1f;
        }

        // Target is on our output side — impose our absolute target RPM
        if (mySpeed == 0f) return 0f;

        // Edge case: both neighbors live on the same network — disengage to avoid loop
        KineticBlockEntity inputNeighbor = getNeighborOn(facing.getOpposite());
        if (inputNeighbor != null
                && inputNeighbor.hasNetwork()
                && target.hasNetwork()
                && inputNeighbor.getOrCreateNetwork().id != null
                && inputNeighbor.getOrCreateNetwork().id.equals(target.getOrCreateNetwork().id)) {
            return 0f;
        }

        float targetRPM = computeTargetRPM(mySpeed);
        return targetRPM / mySpeed;
    }

    /**
     * Computes the signed absolute output RPM from the redstone signal,
     * taking direction override and input sign into account.
     */
    private float computeTargetRPM(float inputSpeed) {
        float magnitude = (float) Math.ceil(redstonePower * 256.0 / 15.0);
        // Apply direction: directionActive flips relative to input, otherwise follows input
        float sign = Math.signum(inputSpeed);
        if (directionActive) sign = -sign;
        return sign * magnitude;
    }

    /**
     * Returns true if {@code face} is the output face given the current source.
     *
     * If we have a known source, the output face is the face that does NOT point
     /**
     * Returns true if {@code face} is the output face given the current source.
     * Package-visible so RotationPropagatorMixin can call it.
     *
     * If we have a known source, the output face is the face that does NOT point
     * toward the source. Otherwise falls back to SU headroom heuristic.
     */
    boolean isOutputFace(Direction face, Direction blockFacing) {
        if (face.getAxis() != blockFacing.getAxis()) return false;

        if (hasSource()) {
            // source tells us which face rotation enters from
            BlockPos sourcePos = source;
            Direction towardSource = Direction.getNearest(
                    sourcePos.getX() - worldPosition.getX(),
                    sourcePos.getY() - worldPosition.getY(),
                    sourcePos.getZ() - worldPosition.getZ());
            return face != towardSource;
        }

        // No source yet — pick output as the face with LESS headroom (weaker network),
        // so the stronger network drives us. If undecidable, default to the FACING direction.
        KineticBlockEntity faceA = getNeighborOn(blockFacing);
        KineticBlockEntity faceB = getNeighborOn(blockFacing.getOpposite());
        float headroomA = headroom(faceA);
        float headroomB = headroom(faceB);

        // The face with MORE headroom becomes the input (it can afford to drive us).
        // So if the queried face has LESS headroom, it is the output.
        if (face == blockFacing) {
            return headroomA <= headroomB;
        } else {
            return headroomB <= headroomA;
        }
    }

    private float headroom(@Nullable KineticBlockEntity be) {
        if (be == null || !be.hasNetwork()) return 0f;
        // Use overstressed as a binary proxy: overstressed = 0 headroom, healthy = 1
        // This is sufficient for picking the stronger network as input.
        return be.isOverStressed() ? 0f : 1f;
    }

    @Nullable
    private KineticBlockEntity getNeighborOn(Direction dir) {
        if (level == null) return null;
        BlockPos neighborPos = worldPosition.relative(dir);
        if (level.getBlockEntity(neighborPos) instanceof KineticBlockEntity kbe) {
            return kbe;
        }
        return null;
    }

    // ------------------------------------------------------------------ power API

    private boolean updatingPower = false;
    private boolean pendingRotationUpdate = false;

    /**
     * Called whenever either received signal changes — from link network receivers
     * or from {@link TransmissionBlock#neighborChanged} as a fallback.
     */
    public void setRedstonePower(int speedPower, boolean dirActive) {
        if (updatingPower) return;
        updatingPower = true;
        try {
            boolean changed = this.redstonePower != speedPower || this.directionActive != dirActive;
            if (!changed) return;

            this.redstonePower  = speedPower;
            this.directionActive = dirActive;

            updateStageBlockstate(speedPower, dirActive);

            // Full re-propagation: detach, clear source, re-attach
            detachKinetics();
            removeSource();
            attachKinetics();

            setChanged();
        } finally {
            updatingPower = false;
        }
    }

    // ------------------------------------------------------------------ tick

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        if (pendingRotationUpdate) {
            pendingRotationUpdate = false;
            detachKinetics();
            removeSource();
            attachKinetics();
        }
    }

    // ------------------------------------------------------------------ frequency GUI

    public void setSpeedFrequency(ItemStack first, ItemStack second) {
        this.speedFreqFirst  = first.copy();
        this.speedFreqSecond = second.copy();
        if (level != null && !level.isClientSide) reregisterReceivers();
        setChanged();
        sendData();
    }

    public void setDirectionFrequency(ItemStack first, ItemStack second) {
        this.dirFreqFirst  = first.copy();
        this.dirFreqSecond = second.copy();
        if (level != null && !level.isClientSide) reregisterReceivers();
        setChanged();
        sendData();
    }

    public ItemStack getSpeedFreqFirst()  { return speedFreqFirst; }
    public ItemStack getSpeedFreqSecond() { return speedFreqSecond; }
    public ItemStack getDirFreqFirst()    { return dirFreqFirst; }
    public ItemStack getDirFreqSecond()   { return dirFreqSecond; }

    // ------------------------------------------------------------------ link network

    private void registerReceivers() {
        if (level == null || level.isClientSide) return;

        RedstoneLinkNetworkHandler.Frequency sf1 = RedstoneLinkNetworkHandler.Frequency.of(speedFreqFirst);
        RedstoneLinkNetworkHandler.Frequency sf2 = RedstoneLinkNetworkHandler.Frequency.of(speedFreqSecond);
        RedstoneLinkNetworkHandler.Frequency df1 = RedstoneLinkNetworkHandler.Frequency.of(dirFreqFirst);
        RedstoneLinkNetworkHandler.Frequency df2 = RedstoneLinkNetworkHandler.Frequency.of(dirFreqSecond);

        boolean hasSpeed = sf1 != RedstoneLinkNetworkHandler.Frequency.EMPTY
                || sf2 != RedstoneLinkNetworkHandler.Frequency.EMPTY;
        boolean hasDir   = df1 != RedstoneLinkNetworkHandler.Frequency.EMPTY
                || df2 != RedstoneLinkNetworkHandler.Frequency.EMPTY;

        if (hasSpeed) {
            speedReceiver = new SpeedLinkReceiver(worldPosition, Couple.create(sf1, sf2));
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, speedReceiver);
        }
        if (hasDir) {
            dirReceiver = new DirectionLinkReceiver(worldPosition, Couple.create(df1, df2));
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, dirReceiver);
        }
    }

    private void unregisterReceivers() {
        if (level == null) return;
        if (speedReceiver != null) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, speedReceiver);
            speedReceiver = null;
        }
        if (dirReceiver != null) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, dirReceiver);
            dirReceiver = null;
        }
    }

    private void reregisterReceivers() {
        unregisterReceivers();
        registerReceivers();
    }

    // ------------------------------------------------------------------ internals

    private void updateStageBlockstate(int power, boolean dirActive) {
        if (level == null || level.isClientSide) return;
        BlockState current = getBlockState();
        int stage = TransmissionBlock.powerToStage(power);
        BlockState updated = current
                .setValue(TransmissionBlock.STAGE, stage)
                .setValue(TransmissionBlock.DIRECTION_ACTIVE, dirActive);
        if (!updated.equals(current)) {
            level.setBlock(worldPosition, updated, 2);
        }
    }

    // ------------------------------------------------------------------ client helpers

    /**
     * The signed absolute output RPM this Transmission targets.
     * Used by the mixin and renderer/visual.
     * Returns 0 if disengaged (signal == 0 or no input speed).
     */
    public float getTargetRPM() {
        if (redstonePower == 0) return 0f;
        float mySpeed = getTheoreticalSpeed();
        if (mySpeed == 0f) return 0f;
        return computeTargetRPM(mySpeed);
    }

    /** @see #getTargetRPM() — alias for renderer */
    public float getTargetSpeed() {
        return getTargetRPM();
    }

    // ------------------------------------------------------------------ MenuProvider

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.loconautics.transmission");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TransmissionMenu(LoconauticsRegistries.TRANSMISSION_MENU.get(), id, inventory, this);
    }

    @Override
    public void sendToMenu(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.TRUSTED_STREAM_CODEC
                .encode(buffer, Component.translatable(TransmissionMenu.SPEED_KEY));
        ComponentSerialization.TRUSTED_STREAM_CODEC
                .encode(buffer, Component.translatable(TransmissionMenu.DIRECTION_KEY));
        super.sendToMenu(buffer);
    }

    // ------------------------------------------------------------------ NBT

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("RedstonePower", redstonePower);
        tag.putBoolean("DirectionActive", directionActive);

        CompoundTag freq = new CompoundTag();
        freq.put("SpeedFirst",  speedFreqFirst.saveOptional(registries));
        freq.put("SpeedSecond", speedFreqSecond.saveOptional(registries));
        freq.put("DirFirst",    dirFreqFirst.saveOptional(registries));
        freq.put("DirSecond",   dirFreqSecond.saveOptional(registries));
        tag.put("Frequency", freq);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        redstonePower   = tag.getInt("RedstonePower");
        directionActive = tag.contains("DirectionActive") && tag.getBoolean("DirectionActive");

        if (tag.contains("Frequency")) {
            CompoundTag freq = tag.getCompound("Frequency");
            speedFreqFirst  = ItemStack.parseOptional(registries, freq.getCompound("SpeedFirst"));
            speedFreqSecond = ItemStack.parseOptional(registries, freq.getCompound("SpeedSecond"));
            dirFreqFirst    = ItemStack.parseOptional(registries, freq.getCompound("DirFirst"));
            dirFreqSecond   = ItemStack.parseOptional(registries, freq.getCompound("DirSecond"));
        }
    }

    @Override
    public void addBehaviours(List<com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour> behaviours) {
        // no standard Create behaviours needed
    }

    // ------------------------------------------------------------------ Inner: speed receiver

    private class SpeedLinkReceiver implements IRedstoneLinkable {

        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;

        SpeedLinkReceiver(BlockPos pos, Couple<RedstoneLinkNetworkHandler.Frequency> key) {
            this.pos = pos;
            this.key = key;
        }

        @Override public int getTransmittedStrength() { return 0; }
        @Override public void setReceivedStrength(int strength) { setRedstonePower(strength, directionActive); }
        @Override public boolean isListening() { return true; }
        @Override public boolean isAlive() { return !isRemoved(); }
        @Override public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() { return key; }
        @Override public BlockPos getLocation() { return pos; }
    }

    // ------------------------------------------------------------------ Inner: direction receiver

    private class DirectionLinkReceiver implements IRedstoneLinkable {

        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;

        DirectionLinkReceiver(BlockPos pos, Couple<RedstoneLinkNetworkHandler.Frequency> key) {
            this.pos = pos;
            this.key = key;
        }

        @Override public int getTransmittedStrength() { return 0; }
        @Override public void setReceivedStrength(int strength) { setRedstonePower(redstonePower, strength > 0); }
        @Override public boolean isListening() { return true; }
        @Override public boolean isAlive() { return !isRemoved(); }
        @Override public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() { return key; }
        @Override public BlockPos getLocation() { return pos; }
    }
}