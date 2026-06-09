package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
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
 * The two frequencies are configured via {@link TransmissionMenu} (shift+right-click).
 *
 * When speed signal == 0 → output = 0 (disengaged, shafts hidden).
 * When directionActive   → output direction is flipped relative to input shaft.
 */
public class TransmissionBlockEntity extends GeneratingKineticBlockEntity implements MenuProvider {

    // ------------------------------------------------------------------ state

    /** Current speed redstone signal received from the link network (0–15). */
    private int redstonePower = 0;

    /** Whether the direction-override signal is active (received from link network). */
    private boolean directionActive = false;

    /** Cached sign of the input shaft's speed (+1 or -1). */
    private int inputDirection = 1;

    /** Whether a live kinetic source was found on the input face. */
    private boolean hasLiveInput = false;

    // ------------------------------------------------------------------ frequency slots

    private ItemStack speedFreqFirst  = ItemStack.EMPTY;
    private ItemStack speedFreqSecond = ItemStack.EMPTY;
    private ItemStack dirFreqFirst    = ItemStack.EMPTY;
    private ItemStack dirFreqSecond   = ItemStack.EMPTY;

    // ------------------------------------------------------------------ link receiver entries

    /** Listener registered in the speed frequency's network. */
    @Nullable private SpeedLinkReceiver  speedReceiver;

    /** Listener registered in the direction frequency's network. */
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

    // ------------------------------------------------------------------ GeneratingKineticBlockEntity

    @Override
    public float getGeneratedSpeed() {
        if (redstonePower == 0) return 0f;
        if (!hasLiveInput) return 0f;
        float rpm = (float) Math.ceil(redstonePower * 256.0 / 15.0);
        int effectiveDir = directionActive ? -inputDirection : inputDirection;
        return effectiveDir >= 0 ? rpm : -rpm;
    }

    @Override
    public float calculateStressApplied() {
        return Config.TRANSMISSION_SU_IMPACT.get().floatValue();
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = Config.TRANSMISSION_SU_CAPACITY.get().floatValue();
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    // ------------------------------------------------------------------ tick

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        boolean wasLive = hasLiveInput;
        readInputDirection();
        if (hasLiveInput != wasLive || pendingRotationUpdate) {
            pendingRotationUpdate = false;
            updateGeneratedRotation();
            setChanged();
        }
    }

    // ------------------------------------------------------------------ power API (called by link receivers)

    private boolean updatingPower = false;
    private boolean pendingRotationUpdate = false;

    private void scheduleRotationUpdate() {
        pendingRotationUpdate = true;
    }

    /**
     * Called whenever either received signal changes — either from the link network
     * receivers ({@link SpeedLinkReceiver#setReceivedStrength},
     * {@link DirectionLinkReceiver#setReceivedStrength}) or from vanilla
     * {@link TransmissionBlock#neighborChanged} as a fallback.
     */
    public void setRedstonePower(int speedPower, boolean dirActive) {
        if (updatingPower) return;
        updatingPower = true;
        try {
            boolean wasDisengaged = this.redstonePower == 0;
            boolean dirChanged    = this.directionActive != dirActive;

            this.redstonePower  = speedPower;
            this.directionActive = dirActive;

            readInputDirection();
            updateStageBlockstate(speedPower, dirActive);

            if ((wasDisengaged && speedPower != 0) || dirChanged) {
                detachKinetics();
                scheduleRotationUpdate();
            } else {
                updateGeneratedRotation();
            }
            setChanged();
        } finally {
            updatingPower = false;
        }
    }

    // ------------------------------------------------------------------ frequency GUI

    public void setSpeedFrequency(ItemStack first, ItemStack second) {
        this.speedFreqFirst  = first.copy();
        this.speedFreqSecond = second.copy();
        if (level != null && !level.isClientSide) {
            reregisterReceivers();
        }
        setChanged();
        sendData();
    }

    public void setDirectionFrequency(ItemStack first, ItemStack second) {
        this.dirFreqFirst  = first.copy();
        this.dirFreqSecond = second.copy();
        if (level != null && !level.isClientSide) {
            reregisterReceivers();
        }
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

    private void readInputDirection() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        net.minecraft.core.Direction.Axis axis = state.getValue(TransmissionBlock.FACING).getAxis();

        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            if (dir.getAxis() != axis) continue;

            BlockPos neighbourPos = worldPosition.relative(dir);
            if (source != null && !neighbourPos.equals(source)) continue;

            if (level.getBlockEntity(neighbourPos) instanceof com.simibubi.create.content.kinetics.base.KineticBlockEntity kbe) {
                float neighbourSpeed = kbe.getSpeed();
                if (neighbourSpeed != 0) {
                    inputDirection = neighbourSpeed > 0 ? 1 : -1;
                    hasLiveInput = true;
                    return;
                }
            }
        }
        hasLiveInput = false;
    }

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

    // ------------------------------------------------------------------ MenuProvider

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.loconautics.transmission");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TransmissionMenu(LoconauticsRegistries.TRANSMISSION_MENU.get(), id, inventory, this);
    }

    /**
     * Writes the two row labels into the buffer before the standard pos+NBT payload
     * so {@link TransmissionMenu}'s client constructor can decode them.
     */
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
        tag.putInt("InputDirection", inputDirection);
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
        redstonePower    = tag.getInt("RedstonePower");
        inputDirection   = tag.contains("InputDirection") ? tag.getInt("InputDirection") : 1;
        directionActive  = tag.contains("DirectionActive") && tag.getBoolean("DirectionActive");

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

    /**
     * Listens on the speed frequency. Receives the analog signal (0–15) and passes
     * it to {@link TransmissionBlockEntity#setRedstonePower} together with the
     * current direction state.
     */
    private class SpeedLinkReceiver implements IRedstoneLinkable {

        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;

        SpeedLinkReceiver(BlockPos pos, Couple<RedstoneLinkNetworkHandler.Frequency> key) {
            this.pos = pos;
            this.key = key;
        }

        @Override
        public int getTransmittedStrength() { return 0; }

        @Override
        public void setReceivedStrength(int strength) {
            setRedstonePower(strength, directionActive);
        }

        @Override
        public boolean isListening() { return true; }

        @Override
        public boolean isAlive() { return !isRemoved(); }

        @Override
        public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() { return key; }

        @Override
        public BlockPos getLocation() { return pos; }
    }

    // ------------------------------------------------------------------ Inner: direction receiver

    /**
     * Listens on the direction frequency. Any signal > 0 activates direction-override;
     * 0 deactivates it.
     */
    private class DirectionLinkReceiver implements IRedstoneLinkable {

        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;

        DirectionLinkReceiver(BlockPos pos, Couple<RedstoneLinkNetworkHandler.Frequency> key) {
            this.pos = pos;
            this.key = key;
        }

        @Override
        public int getTransmittedStrength() { return 0; }

        @Override
        public void setReceivedStrength(int strength) {
            setRedstonePower(redstonePower, strength > 0);
        }

        @Override
        public boolean isListening() { return true; }

        @Override
        public boolean isAlive() { return !isRemoved(); }

        @Override
        public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() { return key; }

        @Override
        public BlockPos getLocation() { return pos; }
    }
}