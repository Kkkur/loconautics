package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.network.packets.AnalogControllerInputPacket;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.createmod.catnip.animation.LerpedFloat;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Block entity for the Analog Controller.
 *
 * Power range: [-negativeCap, +maxPower] (single continuous integer, no mode flag).
 *
 * W held  → step power up   by 1 every RAISE_TICKS
 * S held  → step power down by 1 every LOWER_TICKS (= RAISE_TICKS + 30)
 * Shift   → toggle lock; while locked, decay is disabled
 * Scroll  → adjusts maxPower (absolute: caps both +maxPower and -maxPower)
 *
 * Decay: always active (mounted or not), nudges power one step toward 0
 * every DECAY_TICKS (configurable). Lock disables it entirely.
 *
 * Network output:
 *   power > 0 → send power on forward freq; backward freq silent
 *   power < 0 → send abs(power) on forward freq AND binary 15 on backward freq
 *   power == 0 → both freqs silent
 *
 * powered blockstate: true iff power != 0
 */
public class AnalogControllerBlockEntity extends SmartBlockEntity implements MenuProvider {

    // ------------------------------------------------------------------ constants

    /** Ticks between each W raise step. */
    private static final int RAISE_TICKS = 10;

    /** Ticks between each S lower step (slower feel than raising). */
    private static final int LOWER_TICKS = RAISE_TICKS + 30; // 40

    /** Maximum positive redstone signal. */
    private static final int MAX_POWER = 15;

    // ------------------------------------------------------------------ client animation

    /** Lerps toward abs(currentPower) / 15 — drives the speed/throttle lever. */
    public final LerpedFloat animSpeed = LerpedFloat.linear().startWithValue(0.0);

    /** Lerps toward 0.5 (centre) — reserved for steering. */
    public final LerpedFloat animSteer = LerpedFloat.linear().startWithValue(0.5);

    /** Lerps toward 1 when mounted, 0 when idle. */
    public final LerpedFloat animEquip = LerpedFloat.linear().startWithValue(0.0);

    // ------------------------------------------------------------------ state

    @Nullable
    private UUID currentUser;

    /** While locked, decay is disabled and power is frozen. */
    private boolean locked = false;

    private boolean raisingSignal  = false;
    private boolean loweringSignal = false;
    /** Space (jump) held — used as the "hold to approach/park at a station" control, mirroring Create. */
    private boolean spaceSignal    = false;

    private int raiseTimer = RAISE_TICKS;
    private int lowerTimer = LOWER_TICKS;

    /**
     * Current power level. Negative = reverse.
     * Range: [-negativeCap .. +maxPower]
     */
    private int currentPower = 0;

    /**
     * Absolute cap set by scroll wheel.
     * Forward:  0 .. +maxPower
     * Backward: 0 .. -maxPower  (symmetric)
     */
    private int maxPower = MAX_POWER;

    // ------------------------------------------------------------------ frequency

    private net.minecraft.world.item.ItemStack frequencyFirst =
            net.minecraft.world.item.ItemStack.EMPTY;
    private net.minecraft.world.item.ItemStack frequencySecond =
            net.minecraft.world.item.ItemStack.EMPTY;

    /** Backward frequency — receives binary ON (15) when power is negative. */
    private net.minecraft.world.item.ItemStack frequencyBackFirst =
            net.minecraft.world.item.ItemStack.EMPTY;
    private net.minecraft.world.item.ItemStack frequencyBackSecond =
            net.minecraft.world.item.ItemStack.EMPTY;

    @Nullable private AnalogLinkEntry linkEntry;
    @Nullable private AnalogLinkEntry backLinkEntry;

    // ------------------------------------------------------------------ constructor

    public AnalogControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) return;
        updateNetwork(level);
        updateBlockState(level, getBlockState());
    }

    // ------------------------------------------------------------------ tick

    public static void tick(Level level, BlockPos pos, BlockState state,
                            AnalogControllerBlockEntity be) {
        if (level.isClientSide) {
            be.clientTick();
            return;
        }
        be.serverTick(level, state);
    }

    private void clientTick() {
        float targetSpeed = Math.abs(currentPower) / 15.0f;
        float targetEquip = hasUser() ? 1.0f : 0.0f;

        animSpeed.chase(targetSpeed, 0.15, LerpedFloat.Chaser.EXP);
        animSteer.chase(0.5,         0.15, LerpedFloat.Chaser.EXP);
        animEquip.chase(targetEquip, 0.1, LerpedFloat.Chaser.EXP);

        animSpeed.tickChaser();
        animSteer.tickChaser();
        animEquip.tickChaser();
    }

    private void serverTick(Level level, BlockState state) {
        // --- station lockout: if this controller lives inside a Sable train that is stopping/stopped at a
        // station, the operator has no control. Zero the power (if needed) and skip everything else this tick. ---
        com.lycoris.loconautics.allsable.SableTrain train = sableTrainAt(level);
        if (train != null && train.isAtStation()) {
            if (currentPower != 0) {
                currentPower = 0;
                updateNetwork(level);
                updateBlockState(level, state);
                setChanged();
                sendData();
            }
            return;
        }

        // --- sanity: disconnect stale user ---
        if (currentUser != null) {
            Player player = level.getPlayerByUUID(currentUser);
            if (player == null || !playerInRange(player)) {
                disconnectUser(level);
            }
        }

        int previousPower = currentPower;

        // Decay timer from config (falls back to DECAY_TICKS constant if config not yet loaded)
        int decayTicks = getDecayTicks();
        int negativeCap = getNegativeCap();

        // ---- decay (always runs unless locked) ----
        // maxPower is the scroll threshold (symmetric: +maxPower forward, -maxPower backward).
        // negativeCap is the hard config floor S can never exceed.
        // Decay always moves toward 0, but if power is outside bounds, it targets the bound first.
        // While a key is actively holding power at its cap, decay is frozen at the cap (not 0).
        int backwardCap = -Math.min(maxPower, negativeCap);
        int decayTarget;
        if      (currentPower > maxPower)            decayTarget = maxPower;
        else if (currentPower < backwardCap)          decayTarget = backwardCap;
        else if (raisingSignal && currentPower > 0)  decayTarget = maxPower;
        else if (loweringSignal && currentPower < 0) decayTarget = backwardCap;
        else                                          decayTarget = 0;

        if (!locked && currentPower != decayTarget) {
            decayCounter--;
            if (decayCounter <= 0) {
                decayCounter = decayTicks;
                currentPower += (currentPower > decayTarget) ? -1 : 1;
            }
        } else if (locked || currentPower == decayTarget) {
            decayCounter = decayTicks;
        }

        // ---- W / S (only while mounted) ----
        if (currentUser != null) {
            boolean bothHeld = raisingSignal && loweringSignal;

            if (!bothHeld) {
                if (raisingSignal) {
                    // W blocked if at or above threshold
                    if (currentPower < maxPower) {
                        raiseTimer--;
                        if (raiseTimer <= 0) {
                            raiseTimer = RAISE_TICKS;
                            currentPower = Math.min(currentPower + 1, maxPower);
                        }
                        // Only suppress decay when within normal range (not being dragged down by threshold)
                        if (currentPower <= maxPower) decayCounter = decayTicks;
                    }
                    // If currentPower > maxPower, decay runs freely — don't touch decayCounter
                }

                if (loweringSignal) {
                    // S blocked if at or below the backward threshold
                    if (currentPower > backwardCap) {
                        lowerTimer--;
                        if (lowerTimer <= 0) {
                            if (currentPower > 0) {
                                lowerTimer = RAISE_TICKS + 5; // braking: faster
                            } else {
                                lowerTimer = LOWER_TICKS;     // going into/deeper reverse: slower
                            }
                            currentPower = Math.max(currentPower - 1, backwardCap);
                        }
                        if (currentPower >= backwardCap) decayCounter = decayTicks;
                    }
                    // If currentPower < backwardCap, decay runs freely toward it
                }
            }
        }

        // ---- propagate if changed ----
        if (currentPower != previousPower) {
            updateNetwork(level);
            updateBlockState(level, state);
            setChanged();
            sendData();
        }
    }

    // Persistent decay counter (not saved to NBT — resets on load, which is fine)
    private int decayCounter = LOWER_TICKS; // reasonable default; recalculated each tick

    private int getDecayTicks() {
        try {
            return com.lycoris.loconautics.Config.ANALOG_DECAY_TICKS.get();
        } catch (Exception e) {
            return 30;
        }
    }

    private int getNegativeCap() {
        try {
            return com.lycoris.loconautics.Config.ANALOG_NEGATIVE_CAP.get();
        } catch (Exception e) {
            return 5;
        }
    }

    // ------------------------------------------------------------------ user management

    private static final long TOGGLE_DEBOUNCE_TICKS = 3;
    private long lastToggleTick = -TOGGLE_DEBOUNCE_TICKS;

    public void toggleUser(Player player) {
        if (level == null) return;
        long now = level.getGameTime();
        if (now - lastToggleTick < TOGGLE_DEBOUNCE_TICKS) return;
        lastToggleTick = now;

        if (currentUser != null) {
            if (currentUser.equals(player.getUUID())) disconnectUser(level);
        } else {
            connectUser(player);
        }
    }

    private void connectUser(Player player) {
        currentUser    = player.getUUID();
        raisingSignal  = false;
        loweringSignal = false;
        spaceSignal    = false;
        raiseTimer     = RAISE_TICKS;
        lowerTimer     = LOWER_TICKS;
        updateBlockState(level, getBlockState());
        setChanged();
        sendData();
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            sp.displayClientMessage(
                    Component.translatable("block.loconautics.analog_controller.start"), true);
            com.lycoris.loconautics.network.LoconauticsNetwork.sendMount(sp, true, worldPosition);
        }
    }

    private void disconnectUser(Level level) {
        if (currentUser != null) {
            Player player = level.getPlayerByUUID(currentUser);
            if (player instanceof ServerPlayer sp) {
                sp.displayClientMessage(
                        Component.translatable("block.loconautics.analog_controller.stop"), true);
                com.lycoris.loconautics.network.LoconauticsNetwork.sendMount(
                        sp, false, net.minecraft.core.BlockPos.ZERO);
            }
        }
        currentUser    = null;
        raisingSignal  = false;
        loweringSignal = false;
        spaceSignal    = false;
        updateBlockState(level, getBlockState());
        setChanged();
        sendData();
    }

    // ------------------------------------------------------------------ key events

    public void onKeyEvent(UUID user, int keyIndex, boolean pressed) {
        if (!user.equals(currentUser)) return;
        switch (keyIndex) {
            case 0 -> { // W → raise
                raisingSignal = pressed;
                if (pressed) { loweringSignal = false; }
            }
            case 1 -> { // S → lower
                boolean wasLowering = loweringSignal;
                loweringSignal = pressed;
                if (pressed) {
                    raisingSignal = false;
                    // Only prime the timer on the initial press, not on keepalive packets
                    if (!wasLowering) {
                        lowerTimer = (currentPower > 0) ? RAISE_TICKS + 5 : LOWER_TICKS;
                    }
                }
            }
            case 4 -> { // Space → hold to approach / park at a station (read by SableTrainDriver)
                spaceSignal = pressed;
            }
            case 5 -> { // Shift → toggle lock
                if (pressed) {
                    locked = !locked;
                    setChanged();
                    sendData();
                }
            }
        }
    }

    /** True while the operator holds Space (the "approach/park at station" control). */
    public boolean isSpaceHeld() {
        return spaceSignal;
    }

    /** True while the operator holds W (raise / depart). */
    public boolean isForwardHeld() {
        return raisingSignal;
    }

    // ------------------------------------------------------------------ network

    private void updateNetwork(Level level) {
        if (level == null || level.isClientSide) return;

        RedstoneLinkNetworkHandler.Frequency fwdFirst  =
                RedstoneLinkNetworkHandler.Frequency.of(frequencyFirst);
        RedstoneLinkNetworkHandler.Frequency fwdSecond =
                RedstoneLinkNetworkHandler.Frequency.of(frequencySecond);
        RedstoneLinkNetworkHandler.Frequency backFirst  =
                RedstoneLinkNetworkHandler.Frequency.of(frequencyBackFirst);
        RedstoneLinkNetworkHandler.Frequency backSecond =
                RedstoneLinkNetworkHandler.Frequency.of(frequencyBackSecond);

        boolean hasFwdFreq  = fwdFirst  != RedstoneLinkNetworkHandler.Frequency.EMPTY
                || fwdSecond != RedstoneLinkNetworkHandler.Frequency.EMPTY;
        boolean hasBackFreq = backFirst  != RedstoneLinkNetworkHandler.Frequency.EMPTY
                || backSecond != RedstoneLinkNetworkHandler.Frequency.EMPTY;

        int fwdPower  = (currentPower != 0) ? Math.abs(currentPower) : 0;
        boolean goingBack = currentPower < 0;

        // Build new entries before removing old ones — avoids zero-power gap in network
        AnalogLinkEntry newLink = null;
        AnalogLinkEntry newBackLink = null;

        if (hasFwdFreq && fwdPower > 0) {
            newLink = new AnalogLinkEntry(worldPosition,
                    Couple.create(fwdFirst, fwdSecond), fwdPower);
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, newLink);
        }

        if (hasBackFreq && goingBack) {
            newBackLink = new AnalogLinkEntry(worldPosition,
                    Couple.create(backFirst, backSecond), 15);
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, newBackLink);
        }

        // Now remove old entries
        if (linkEntry != null) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, linkEntry);
        }
        if (backLinkEntry != null) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, backLinkEntry);
        }

        linkEntry     = newLink;
        backLinkEntry = newBackLink;
    }

    private void updateBlockState(Level level, BlockState state) {
        boolean active  = currentUser != null;
        boolean powered = currentPower != 0;
        int power = Math.abs(currentPower);

        BlockState newState = state
                .setValue(AnalogControllerBlock.POWER,   power)
                .setValue(AnalogControllerBlock.ACTIVE,  active)
                .setValue(AnalogControllerBlock.POWERED, powered);

        if (!newState.equals(state)) {
            level.setBlockAndUpdate(worldPosition, newState);
            level.updateNeighborsAt(worldPosition, newState.getBlock());
        }
    }

    public void onRemoved() {
        if (level == null) return;
        if (currentUser != null && !level.isClientSide) {
            Player player = level.getPlayerByUUID(currentUser);
            if (player instanceof ServerPlayer sp) {
                com.lycoris.loconautics.network.LoconauticsNetwork.sendMount(
                        sp, false, net.minecraft.core.BlockPos.ZERO);
            }
            currentUser = null;
        }
        if (linkEntry != null) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, linkEntry);
            linkEntry = null;
        }
        if (backLinkEntry != null) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, backLinkEntry);
            backLinkEntry = null;
        }
    }

    // ------------------------------------------------------------------ frequency GUI

    public void setFrequency(net.minecraft.world.item.ItemStack first,
                             net.minecraft.world.item.ItemStack second) {
        this.frequencyFirst  = first.copy();
        this.frequencySecond = second.copy();
        if (level != null && !level.isClientSide) updateNetwork(level);
        setChanged();
        sendData();
    }

    public void setBackwardFrequency(net.minecraft.world.item.ItemStack first,
                                     net.minecraft.world.item.ItemStack second) {
        this.frequencyBackFirst  = first.copy();
        this.frequencyBackSecond = second.copy();
        if (level != null && !level.isClientSide) updateNetwork(level);
        setChanged();
        sendData();
    }

    public net.minecraft.world.item.ItemStack getFrequencyFirst()      { return frequencyFirst; }
    public net.minecraft.world.item.ItemStack getFrequencySecond()     { return frequencySecond; }
    public net.minecraft.world.item.ItemStack getFrequencyBackFirst()  { return frequencyBackFirst; }
    public net.minecraft.world.item.ItemStack getFrequencyBackSecond() { return frequencyBackSecond; }

    // ------------------------------------------------------------------ accessors

    public int     getCurrentPower()    { return currentPower; }
    public boolean isLocked()           { return locked; }
    public boolean hasUser()            { return currentUser != null; }
    public int     getMaxPower()        { return maxPower; }

    @Nullable
    public UUID getCurrentUser() { return currentUser; }

    public void onScroll(UUID user, int delta) {
        if (!user.equals(currentUser)) return;
        maxPower = Math.max(0, Math.min(MAX_POWER, maxPower + delta));
        setChanged();
        sendData();
    }

    /**
     * Resolves the {@link com.lycoris.loconautics.allsable.SableTrain} this controller lives inside, or
     * {@code null} if it isn't in a train sub-level. The controller's {@code level} is the parent level that
     * hosts every Sable sub-level as a plot region; we find the plot covering this block's position, read its
     * sub-level UUID, and map that to a registered train.
     */
    @Nullable
    private com.lycoris.loconautics.allsable.SableTrain sableTrainAt(Level level) {
        dev.ryanhcode.sable.api.sublevel.SubLevelContainer container =
                dev.ryanhcode.sable.api.sublevel.SubLevelContainer.getContainer(level);
        if (container == null) {
            return null;
        }
        dev.ryanhcode.sable.sublevel.plot.LevelPlot plot =
                container.getPlot(new net.minecraft.world.level.ChunkPos(worldPosition));
        if (plot == null) {
            return null;
        }
        dev.ryanhcode.sable.sublevel.SubLevel sub = plot.getSubLevel();
        if (sub == null) {
            return null;
        }
        return com.lycoris.loconautics.allsable.SableTrainRegistry.bySubLevel(sub.getUniqueId());
    }

    private boolean playerInRange(Player player) {
        if (level == null) return true;
        double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) * 2.0;
        net.minecraft.world.phys.Vec3 blockWorld = dev.ryanhcode.sable.Sable.HELPER.projectOutOfSubLevel(
                level, net.minecraft.world.phys.Vec3.atCenterOf(worldPosition));
        return blockWorld.distanceToSqr(player.position()) < range * range;
    }

    // ------------------------------------------------------------------ MenuProvider

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.loconautics.analog_controller");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AnalogControllerMenu(LoconauticsRegistries.ANALOG_CONTROLLER_MENU.get(), id, inventory, this);
    }

    @Override
    public void sendToMenu(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
        net.minecraft.network.chat.ComponentSerialization.TRUSTED_STREAM_CODEC
                .encode(buffer, Component.translatable(AnalogControllerMenu.FORWARD_KEY));
        net.minecraft.network.chat.ComponentSerialization.TRUSTED_STREAM_CODEC
                .encode(buffer, Component.translatable(AnalogControllerMenu.BACKWARD_KEY));
        super.sendToMenu(buffer);
    }

    // ------------------------------------------------------------------ NBT

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("Power",    currentPower);
        tag.putBoolean("Locked", locked);
        tag.putInt("MaxPower", maxPower);
        if (currentUser != null) tag.putUUID("User", currentUser);
        if (clientPacket) tag.putBoolean("HasUser", currentUser != null);

        CompoundTag freq = new CompoundTag();
        freq.put("First",      frequencyFirst.saveOptional(registries));
        freq.put("Second",     frequencySecond.saveOptional(registries));
        freq.put("BackFirst",  frequencyBackFirst.saveOptional(registries));
        freq.put("BackSecond", frequencyBackSecond.saveOptional(registries));
        tag.put("Frequency", freq);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        currentPower = tag.getInt("Power");
        locked       = tag.getBoolean("Locked");
        maxPower     = tag.contains("MaxPower") ? tag.getInt("MaxPower") : MAX_POWER;
        // On client packets, restore HasUser so animEquip works; use a sentinel UUID since
        // the actual UUID is not needed client-side (only hasUser() matters for rendering).
        if (clientPacket && tag.contains("HasUser")) {
            currentUser = tag.getBoolean("HasUser") ? java.util.UUID.randomUUID() : null;
        } else {
            currentUser = null; // never restore stale user UUID on server
        }

        if (tag.contains("Frequency")) {
            CompoundTag freq = tag.getCompound("Frequency");
            frequencyFirst      = net.minecraft.world.item.ItemStack.parseOptional(registries, freq.getCompound("First"));
            frequencySecond     = net.minecraft.world.item.ItemStack.parseOptional(registries, freq.getCompound("Second"));
            frequencyBackFirst  = net.minecraft.world.item.ItemStack.parseOptional(registries, freq.getCompound("BackFirst"));
            frequencyBackSecond = net.minecraft.world.item.ItemStack.parseOptional(registries, freq.getCompound("BackSecond"));
        }
    }

    // ------------------------------------------------------------------ AnalogLinkEntry

    public static class AnalogLinkEntry implements IRedstoneLinkable {

        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;
        private final int power;

        public AnalogLinkEntry(BlockPos pos,
                               Couple<RedstoneLinkNetworkHandler.Frequency> key,
                               int power) {
            this.pos   = pos;
            this.key   = key;
            this.power = power;
        }

        @Override public int     getTransmittedStrength()          { return power; }
        @Override public void    setReceivedStrength(int strength) {}
        @Override public boolean isListening()                     { return false; }
        @Override public boolean isAlive()                         { return true; }
        @Override public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() { return key; }
        @Override public BlockPos getLocation()                    { return pos; }
    }
}