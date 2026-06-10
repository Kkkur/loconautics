package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
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
 * Transmission Block Entity.
 *
 * <p>Extends {@link SplitShaftBlockEntity} so Create's {@code RotationPropagator}
 * calls {@link #getRotationSpeedModifier(Direction)} when computing the speed conveyed
 * toward each neighbour. The input half passes through at 1:1; the output half is scaled
 * to deliver exactly {@link #computeTargetRPM()} regardless of the input speed.
 *
 * <p>Two independent redstone-link receivers:
 * <ul>
 *   <li><b>Speed</b>  — analog signal 0–15, maps to 0 / 18–256 RPM</li>
 *   <li><b>Direction</b> — binary signal &gt;0 inverts the output shaft</li>
 * </ul>
 *
 * <p>Stress: pure consumer. Draws SU from the input network proportional to output RPM.
 * Provides zero capacity.
 */
public class TransmissionBlockEntity extends SplitShaftBlockEntity implements MenuProvider {

    // ------------------------------------------------------------------ frequency slots

    private ItemStack speedFreqFirst  = ItemStack.EMPTY;
    private ItemStack speedFreqSecond = ItemStack.EMPTY;
    private ItemStack dirFreqFirst    = ItemStack.EMPTY;
    private ItemStack dirFreqSecond   = ItemStack.EMPTY;

    // ------------------------------------------------------------------ runtime state

    /** Analog signal 0–15 received on the speed link. 0 = disengaged. */
    private int redstonePower = 0;

    /** True when the direction link has a non-zero signal (flip output shaft). */
    private boolean directionActive = false;

    // ------------------------------------------------------------------ link entries

    @Nullable private SpeedLinkReceiver     speedReceiver;
    @Nullable private DirectionLinkReceiver dirReceiver;

    // ------------------------------------------------------------------ constructor

    public TransmissionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // No additional behaviours needed.
    }

    // ------------------------------------------------------------------ lifecycle

    @Override
    public void initialize() {
        super.initialize();
        if (level == null || level.isClientSide) return;
        registerReceivers();
    }

    @Override
    public void invalidate() {
        unregisterReceivers();
        super.invalidate();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unregisterReceivers();
    }

    // ------------------------------------------------------------------ kinetics

    /**
     * Returns the rotation-speed modifier for the given face.
     *
     * <ul>
     *   <li>Source face → 1.0 (pass-through, input network speed unchanged)</li>
     *   <li>Output face, signal 0 → 0.0 (disengaged)</li>
     *   <li>Output face, signal &gt;0 → {@code ±targetRPM / |inputSpeed|}
     *       so the propagator sets the output network to exactly {@code targetRPM}</li>
     * </ul>
     */
    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) return 1.0f;
        if (face == getSourceFacing()) return 1.0f;

        float inputSpeed = getTheoreticalSpeed();
        if (inputSpeed == 0f) return 0f;

        float targetRPM = computeTargetRPM();
        if (targetRPM == 0f) return 0f; // disengaged

        float sign = directionActive ? -1f : 1f;
        return sign * targetRPM / Math.abs(inputSpeed);
    }

    // ------------------------------------------------------------------ stress

    @Override
    public float calculateStressApplied() {
        // Create multiplies this by abs(getTheoreticalSpeed()) internally.
        // getTheoreticalSpeed() returns the input network speed — the input network pays.
        try {
            return Config.TRANSMISSION_SU_IMPACT.get().floatValue();
        } catch (Exception e) {
            return 4.0f;
        }
    }

    @Override
    public float calculateAddedStressCapacity() {
        return 0f; // Transmission provides no capacity to any network
    }

    // ------------------------------------------------------------------ speed formula

    /**
     * Maps the current redstone signal to an absolute RPM target.
     *
     * <pre>
     *   signal 0   → 0 RPM  (disengaged)
     *   signal 1   → ceil(1  * 256 / 15) = 18  RPM
     *   signal 8   → ceil(8  * 256 / 15) = 137 RPM
     *   signal 15  → ceil(15 * 256 / 15) = 256 RPM
     * </pre>
     */
    private float computeTargetRPM() {
        if (redstonePower == 0) return 0f;
        return (float) Math.ceil(redstonePower * 256.0 / 15.0);
    }

    // ------------------------------------------------------------------ link receivers

    /**
     * Called by the speed link receiver when the analog signal changes.
     * Updates the STAGE blockstate, then detaches and re-attaches kinetics so the
     * propagator recomputes the output speed with the new ratio.
     */
    public void setRedstonePower(int power) {
        int clamped = Math.max(0, Math.min(15, power));
        if (this.redstonePower == clamped) return;
        this.redstonePower = clamped;
        updateStageBlockstate();
        onKineticStateChanged();
        setChanged();
        sendData();
    }

    /**
     * Called by the direction link receiver when the direction signal changes.
     */
    public void setDirectionActive(boolean active) {
        if (this.directionActive == active) return;
        this.directionActive = active;
        updateDirectionBlockstate();
        onKineticStateChanged();
        setChanged();
        sendData();
    }

    /**
     * Writes the visual {@code DIRECTION_ACTIVE} property to the blockstate
     * so the model reflects the current direction signal.
     */
    private void updateDirectionBlockstate() {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        BlockState updated = state.setValue(TransmissionBlock.DIRECTION_ACTIVE, directionActive);
        if (!updated.equals(state)) {
            level.setBlock(worldPosition, updated, 2);
        }
    }

    /**
     * Updates the STAGE blockstate property to reflect the current redstonePower.
     * STAGE is purely visual — logic lives on the BE.
     */
    private void updateStageBlockstate() {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        int stage = redstonePower == 0 ? 0 : Math.max(1, Math.min(5, 1 + (redstonePower - 1) * 5 / 14));
        BlockState updated = state.setValue(TransmissionBlock.STAGE, stage);
        if (!updated.equals(state)) {
            level.setBlock(worldPosition, updated, 2);
        }
    }

    /**
     * Detach kinetics and schedule a re-attach on the next tick, forcing the propagator
     * to recompute output speed with the updated modifier. Mirrors the Gearshift pattern.
     */
    private void onKineticStateChanged() {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        if (state.getBlock() instanceof TransmissionBlock block) {
            block.detachKinetics(level, worldPosition, true);
        }
    }

    // ------------------------------------------------------------------ network registration

    private void registerReceivers() {
        if (level == null || level.isClientSide) return;
        unregisterReceivers();

        RedstoneLinkNetworkHandler.Frequency sf1 = RedstoneLinkNetworkHandler.Frequency.of(speedFreqFirst);
        RedstoneLinkNetworkHandler.Frequency sf2 = RedstoneLinkNetworkHandler.Frequency.of(speedFreqSecond);
        RedstoneLinkNetworkHandler.Frequency df1 = RedstoneLinkNetworkHandler.Frequency.of(dirFreqFirst);
        RedstoneLinkNetworkHandler.Frequency df2 = RedstoneLinkNetworkHandler.Frequency.of(dirFreqSecond);

        boolean hasSpeed = sf1 != RedstoneLinkNetworkHandler.Frequency.EMPTY
                || sf2 != RedstoneLinkNetworkHandler.Frequency.EMPTY;
        boolean hasDir   = df1 != RedstoneLinkNetworkHandler.Frequency.EMPTY
                || df2 != RedstoneLinkNetworkHandler.Frequency.EMPTY;

        if (hasSpeed) {
            speedReceiver = new SpeedLinkReceiver(worldPosition, Couple.create(sf1, sf2), this);
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, speedReceiver);
        }
        if (hasDir) {
            dirReceiver = new DirectionLinkReceiver(worldPosition, Couple.create(df1, df2), this);
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

    // ------------------------------------------------------------------ frequency setters (GUI)

    public void setSpeedFrequency(ItemStack first, ItemStack second) {
        this.speedFreqFirst  = first.copy();
        this.speedFreqSecond = second.copy();
        if (level != null && !level.isClientSide) registerReceivers();
        setChanged();
        sendData();
    }

    public void setDirectionFrequency(ItemStack first, ItemStack second) {
        this.dirFreqFirst  = first.copy();
        this.dirFreqSecond = second.copy();
        if (level != null && !level.isClientSide) registerReceivers();
        setChanged();
        sendData();
    }

    public ItemStack getSpeedFreqFirst()  { return speedFreqFirst; }
    public ItemStack getSpeedFreqSecond() { return speedFreqSecond; }
    public ItemStack getDirFreqFirst()    { return dirFreqFirst; }
    public ItemStack getDirFreqSecond()   { return dirFreqSecond; }

    // ------------------------------------------------------------------ MenuProvider

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.loconautics.transmission");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TransmissionMenu(
                LoconauticsRegistries.TRANSMISSION_MENU.get(), id, inventory, this);
    }

    /**
     * Writes the two row labels into the buffer before the standard pos+NBT payload
     * so {@link TransmissionMenu}'s client constructor can decode them first.
     */
    @Override
    public void sendToMenu(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.TRUSTED_STREAM_CODEC
                .encode(buffer, Component.translatable(TransmissionMenu.SPEED_KEY));
        ComponentSerialization.TRUSTED_STREAM_CODEC
                .encode(buffer, Component.translatable(TransmissionMenu.DIRECTION_KEY));
        super.sendToMenu(buffer); // writes blockPos + updateTag (NBT)
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
        directionActive = tag.getBoolean("DirectionActive");

        if (tag.contains("Frequency")) {
            CompoundTag freq = tag.getCompound("Frequency");
            speedFreqFirst  = ItemStack.parseOptional(registries, freq.getCompound("SpeedFirst"));
            speedFreqSecond = ItemStack.parseOptional(registries, freq.getCompound("SpeedSecond"));
            dirFreqFirst    = ItemStack.parseOptional(registries, freq.getCompound("DirFirst"));
            dirFreqSecond   = ItemStack.parseOptional(registries, freq.getCompound("DirSecond"));
        }
    }

    // ================================================================== inner receivers

    /** Listens on the speed frequency; delivers analog signal to the Transmission. */
    public static class SpeedLinkReceiver implements IRedstoneLinkable {
        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;
        private final TransmissionBlockEntity be;

        public SpeedLinkReceiver(BlockPos pos,
                                 Couple<RedstoneLinkNetworkHandler.Frequency> key,
                                 TransmissionBlockEntity be) {
            this.pos = pos;
            this.key = key;
            this.be  = be;
        }

        @Override public int     getTransmittedStrength()                                          { return 0; }
        @Override public boolean isListening()                                                     { return true; }
        @Override public boolean isAlive()                                                         { return !be.isRemoved(); }
        @Override public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey()              { return key; }
        @Override public BlockPos getLocation()                                                    { return pos; }

        @Override
        public void setReceivedStrength(int strength) {
            be.setRedstonePower(strength);
        }
    }

    /** Listens on the direction frequency; flips the output shaft when signal &gt; 0. */
    public static class DirectionLinkReceiver implements IRedstoneLinkable {
        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;
        private final TransmissionBlockEntity be;

        public DirectionLinkReceiver(BlockPos pos,
                                     Couple<RedstoneLinkNetworkHandler.Frequency> key,
                                     TransmissionBlockEntity be) {
            this.pos = pos;
            this.key = key;
            this.be  = be;
        }

        @Override public int     getTransmittedStrength()                                          { return 0; }
        @Override public boolean isListening()                                                     { return true; }
        @Override public boolean isAlive()                                                         { return !be.isRemoved(); }
        @Override public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey()              { return key; }
        @Override public BlockPos getLocation()                                                    { return pos; }

        @Override
        public void setReceivedStrength(int strength) {
            be.setDirectionActive(strength > 0);
        }
    }
}