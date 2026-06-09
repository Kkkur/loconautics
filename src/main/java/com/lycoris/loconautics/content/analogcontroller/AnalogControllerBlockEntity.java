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
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Block entity for the Analog Controller.
 *
 * Key responsibilities:
 *  1. Track which player is currently "mounted" (has right-clicked to use it).
 *  2. Receive key events from {@link AnalogControllerInputPacket} and adjust signal:
 *       W (key 0) → raise signal by 1 (up to 15)
 *       S (key 1) → lower signal by 1 (down to 0)
 *       Shift (key 5) → toggle lock; while locked, signal doesn't decay
 *  3. If not locked and user is idle (no W/S), decay signal by 1 every DECAY_TICKS ticks.
 *  4. Broadcast signal via Create's {@link RedstoneLinkNetworkHandler} on the frequency
 *     configured in the GUI (two item slots, like a standard redstone link).
 *  5. Also write the power value into the block state so adjacent blocks see a vanilla
 *     comparator-compatible output.
 */
public class AnalogControllerBlockEntity extends SmartBlockEntity implements MenuProvider {

    // ------------------------------------------------------------------ constants

    /** Ticks between each W raise step (~0.5 seconds). */
    private static final int RAISE_TICKS  = 10;

    /** Ticks between each S lower step (~0.5 seconds). */
    private static final int LOWER_TICKS  = 10;

    /** Ticks between automatic signal decay steps (~1 second). */
    private static final int DECAY_TICKS  = 20;

    /** Maximum redstone signal value. */
    private static final int MAX_POWER = 15;

    // ------------------------------------------------------------------ state

    /** UUID of the player currently using this controller, or null. */
    @Nullable
    private UUID currentUser;

    /** Whether the signal is locked (won't decay). Toggled by Shift while mounted. */
    private boolean locked = false;

    /** Whether W is currently held (raise signal). */
    private boolean raisingSignal = false;

    /** Whether S is currently held (lower signal). */
    private boolean loweringSignal = false;

    /** Countdown to next raise step. */
    private int raiseTimer = RAISE_TICKS;

    /** Countdown to next lower step. */
    private int lowerTimer = LOWER_TICKS;

    /** Countdown to next decay step. */
    private int decayTimer = DECAY_TICKS;

    /** Current redstone power (0-15). */
    private int currentPower = 0;

    /** Maximum allowed power — set by scroll wheel, shown as the throttle cap. */
    private int maxPower = MAX_POWER;

    // ------------------------------------------------------------------ frequency

    /**
     * The two frequency slots for the redstone link network key.
     * Stored as ItemStacks in NBT; exposed via the GUI.
     */
    private net.minecraft.world.item.ItemStack frequencyFirst =
            net.minecraft.world.item.ItemStack.EMPTY;
    private net.minecraft.world.item.ItemStack frequencySecond =
            net.minecraft.world.item.ItemStack.EMPTY;

    /** Backward-direction frequency (binary: ON when in backward mode). */
    private net.minecraft.world.item.ItemStack frequencyBackFirst =
            net.minecraft.world.item.ItemStack.EMPTY;
    private net.minecraft.world.item.ItemStack frequencyBackSecond =
            net.minecraft.world.item.ItemStack.EMPTY;

    /**
     * The linkable delegate that sits in the Create network and transmits our signal.
     * Re-created whenever the frequency or power changes.
     */
    @Nullable
    private AnalogLinkEntry linkEntry;

    // ------------------------------------------------------------------ constructor

    public AnalogControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // No standard Create behaviours needed; we manage the network manually.
    }

    // ------------------------------------------------------------------ tick

    /**
     * Static ticker target (called from {@link AnalogControllerBlock#getTicker}).
     */
    public static void tick(Level level, BlockPos pos, BlockState state,
                            AnalogControllerBlockEntity be) {
        be.serverTick(level, state);
    }

    private void serverTick(Level level, BlockState state) {
        if (level.isClientSide) return;

        // --- sanity check: disconnect stale user ---
        if (currentUser != null) {
            Player player = level.getPlayerByUUID(currentUser);
            if (player == null || !playerInRange(player)) {
                com.lycoris.loconautics.core.LoconauticsConstants.LOGGER.info(
                        "[analog] sanity-disconnect: playerNull={} inRange={}",
                        player == null, player != null && playerInRange(player));
                disconnectUser(level);
            }
        }

        int previousPower = currentPower;

        // --- apply held keys at rate-limited intervals ---
        if (raisingSignal) {
            // W is blocked if locked and already at or above the throttle cap
            boolean blockedByLock = locked && currentPower >= maxPower;
            if (!blockedByLock) {
                raiseTimer--;
                if (raiseTimer <= 0) {
                    raiseTimer = RAISE_TICKS;
                    currentPower = Math.min(currentPower + 1, maxPower);
                }
            }
            decayTimer = DECAY_TICKS; // reset decay whenever player is actively pushing
        }

        if (loweringSignal) {
            lowerTimer--;
            if (lowerTimer <= 0) {
                lowerTimer = LOWER_TICKS;
                currentPower = Math.max(currentPower - 1, 0);
            }
            decayTimer = DECAY_TICKS;
        }

        // --- decay runs when not actively controlling ---
        // When locked: decays down to maxPower (the throttle cap), not to 0.
        // When unlocked: decays all the way to 0.
        if (!raisingSignal && !loweringSignal) {
            int decayFloor = locked ? maxPower : 0;
            if (currentPower > decayFloor) {
                decayTimer--;
                if (decayTimer <= 0) {
                    decayTimer = DECAY_TICKS;
                    currentPower = Math.max(currentPower - 1, decayFloor);
                }
            }
        }

        // --- propagate to network if changed ---
        if (currentPower != previousPower) {
            updateNetwork(level);
            updateBlockState(level, state);
            setChanged();
            sendData();
        }
    }

    // ------------------------------------------------------------------ user management

    /** Debounce for the mount toggle (ticks). A click on a block INSIDE a Sable sub-level can be delivered
     *  twice in one click (e.g. both hands), which would mount then instantly dismount — swallow the echo. */
    private static final long TOGGLE_DEBOUNCE_TICKS = 3;
    private long lastToggleTick = -TOGGLE_DEBOUNCE_TICKS;

    public void toggleUser(Player player) {
        if (level == null) return;
        long now = level.getGameTime();
        com.lycoris.loconautics.core.LoconauticsConstants.LOGGER.info(
                "[analog-BE] toggleUser: player={} currentUser={} now={} lastToggle={} debounced={}",
                player.getUUID(), currentUser, now, lastToggleTick, now - lastToggleTick < TOGGLE_DEBOUNCE_TICKS);
        if (now - lastToggleTick < TOGGLE_DEBOUNCE_TICKS) {
            return;
        }
        lastToggleTick = now;
        if (currentUser != null) {
            if (currentUser.equals(player.getUUID())) {
                disconnectUser(level);
            }
        } else {
            connectUser(player);
        }
    }

    private void connectUser(Player player) {
        currentUser = player.getUUID();
        // locked intentionally NOT reset — player may have locked before dismounting
        raisingSignal = false;
        loweringSignal = false;
        raiseTimer = 0;
        lowerTimer = 0;
        decayTimer = DECAY_TICKS;
        updateBlockState(level, getBlockState());
        setChanged();
        sendData(); // always send so client HUD gets initial power+locked state
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            sp.displayClientMessage(
                    Component.translatable("block.loconautics.analog_controller.start"),
                    true
            );
            com.lycoris.loconautics.network.LoconauticsNetwork.sendMount(sp, true, worldPosition);
        }
    }

    private void disconnectUser(Level level) {
        if (currentUser != null) {
            Player player = level.getPlayerByUUID(currentUser);
            if (player instanceof ServerPlayer sp) {
                sp.displayClientMessage(
                        Component.translatable("block.loconautics.analog_controller.stop"),
                        true
                );
                com.lycoris.loconautics.network.LoconauticsNetwork.sendMount(
                        sp, false, net.minecraft.core.BlockPos.ZERO);
            }
        }
        currentUser = null;
        // locked intentionally preserved — block remembers lock state between uses
        raisingSignal = false;
        loweringSignal = false;
        // Signal stays at its last value — player can lock it before dismounting.
        updateBlockState(level, getBlockState());
        setChanged();
        sendData();
    }

    // ------------------------------------------------------------------ key events

    /**
     * Called from {@link AnalogControllerInputPacket} on the server thread.
     *
     * @param user    UUID of the sender
     * @param keyIndex Create controls index: 0=W, 1=S, 4=Space, 5=Shift
     * @param pressed  true = key down, false = key up
     */
    public void onKeyEvent(UUID user, int keyIndex, boolean pressed) {
        com.lycoris.loconautics.core.LoconauticsConstants.LOGGER.info(
                "[analog] key keyIndex={} pressed={} fromCurrentUser={}", keyIndex, pressed, user.equals(currentUser));
        if (!user.equals(currentUser)) return;

        switch (keyIndex) {
            case 0 -> { // W → raise
                raisingSignal = pressed;
                if (pressed) loweringSignal = false;
                // raiseTimer intentionally NOT reset on release — keeps counting so
                // spamming W cannot bypass the rate limit
            }
            case 1 -> { // S → lower
                loweringSignal = pressed;
                if (pressed) raisingSignal = false;
                // lowerTimer intentionally NOT reset on release
            }
            case 5 -> { // Shift → toggle lock on key-down only
                if (pressed) {
                    locked = !locked;
                    setChanged();
                    sendData();
                }
            }
            // Other keys ignored
        }
    }

    // ------------------------------------------------------------------ network

    /**
     * Pushes the current power value through the Create redstone link network under
     * the configured frequency.
     */
    private void updateNetwork(Level level) {
        if (level == null || level.isClientSide) return;

        // Remove old entry if present
        if (linkEntry != null) {
            Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, linkEntry);
        }

        // Don't transmit at power 0
        if (currentPower <= 0) {
            linkEntry = null;
            return;
        }

        RedstoneLinkNetworkHandler.Frequency first =
                RedstoneLinkNetworkHandler.Frequency.of(frequencyFirst);
        RedstoneLinkNetworkHandler.Frequency second =
                RedstoneLinkNetworkHandler.Frequency.of(frequencySecond);

        // Both slots empty → no frequency configured, don't transmit
        if (first == RedstoneLinkNetworkHandler.Frequency.EMPTY
                && second == RedstoneLinkNetworkHandler.Frequency.EMPTY) {
            linkEntry = null;
            return;
        }

        linkEntry = new AnalogLinkEntry(worldPosition, Couple.create(first, second), currentPower);
        Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, linkEntry);
    }

    private void updateBlockState(Level level, BlockState state) {
        boolean active = currentUser != null;
        boolean powered = currentPower > 0;
        int power = currentPower;

        BlockState newState = state
                .setValue(AnalogControllerBlock.POWER, power)
                .setValue(AnalogControllerBlock.ACTIVE, active)
                .setValue(AnalogControllerBlock.POWERED, powered);

        if (!newState.equals(state)) {
            level.setBlockAndUpdate(worldPosition, newState);
            level.updateNeighborsAt(worldPosition, newState.getBlock());
        }
    }

    public void onRemoved() {
        if (level == null) return;
        // Send the dismount packet directly -- do NOT call disconnectUser() here.
        // disconnectUser() calls updateBlockState() -> level.setBlockAndUpdate(), which
        // would re-place the block that was just destroyed, causing it to reappear.
        // We only need to notify the mounted client; all other server-side state is
        // already being discarded along with this block entity.
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
    }

    // ------------------------------------------------------------------ frequency GUI

    public void setFrequency(net.minecraft.world.item.ItemStack first,
                             net.minecraft.world.item.ItemStack second) {
        this.frequencyFirst = first.copy();
        this.frequencySecond = second.copy();
        if (level != null && !level.isClientSide) {
            updateNetwork(level);
        }
        setChanged();
        sendData();
    }

    public void setBackwardFrequency(net.minecraft.world.item.ItemStack first,
                                     net.minecraft.world.item.ItemStack second) {
        this.frequencyBackFirst = first.copy();
        this.frequencyBackSecond = second.copy();
        if (level != null && !level.isClientSide) {
            updateNetwork(level);
        }
        setChanged();
        sendData();
    }

    public net.minecraft.world.item.ItemStack getFrequencyFirst() { return frequencyFirst; }
    public net.minecraft.world.item.ItemStack getFrequencySecond() { return frequencySecond; }
    public net.minecraft.world.item.ItemStack getFrequencyBackFirst() { return frequencyBackFirst; }
    public net.minecraft.world.item.ItemStack getFrequencyBackSecond() { return frequencyBackSecond; }

    // ------------------------------------------------------------------ accessors

    public int getCurrentPower() { return currentPower; }

    public boolean isLocked() { return locked; }

    public boolean hasUser() { return currentUser != null; }

    @Nullable
    public UUID getCurrentUser() { return currentUser; }

    public int getMaxPower() { return maxPower; }

    /** Called when the player scrolls while mounted. Delta: +1 or -1. */
    public void onScroll(UUID user, int delta) {
        if (!user.equals(currentUser)) return;
        maxPower = Math.max(0, Math.min(MAX_POWER, maxPower + delta));
        // Only snap currentPower down immediately when NOT locked.
        // When locked, decay will bring it down to the new maxPower gradually.
        if (!locked && currentPower > maxPower) {
            currentPower = maxPower;
            updateNetwork(level);
            updateBlockState(level, getBlockState());
        }
        setChanged();
        sendData();
    }

    private boolean playerInRange(Player player) {
        if (level == null) {
            return true;
        }
        double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) * 2.0;
        // The controller may live inside a Sable sub-level, where worldPosition is a far-away plot-grid
        // coordinate — so a raw distance to the player always reads "out of range" and instantly dismounts
        // them. Project the block's position OUT of the sub-level to its real-world location first. When the
        // block isn't in a sub-level, projectOutOfSubLevel returns the point unchanged (normal behaviour).
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

    // ------------------------------------------------------------------ NBT

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("Power", currentPower);
        tag.putBoolean("Locked", locked);
        tag.putInt("MaxPower", maxPower);
        if (currentUser != null) {
            tag.putUUID("User", currentUser);
        }

        CompoundTag freq = new CompoundTag();
        freq.put("First", frequencyFirst.saveOptional(registries));
        freq.put("Second", frequencySecond.saveOptional(registries));
        freq.put("BackFirst", frequencyBackFirst.saveOptional(registries));
        freq.put("BackSecond", frequencyBackSecond.saveOptional(registries));
        tag.put("Frequency", freq);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        currentPower = tag.getInt("Power");
        locked = tag.getBoolean("Locked");
        maxPower = tag.contains("MaxPower") ? tag.getInt("MaxPower") : MAX_POWER;
        // Intentionally NOT restoring currentUser — if the chunk unloads mid-ride
        // the player is already disconnected, restoring a stale UUID would lock the
        // controller and prevent anyone from mounting it again.
        currentUser = null;

        if (tag.contains("Frequency")) {
            CompoundTag freq = tag.getCompound("Frequency");
            frequencyFirst = net.minecraft.world.item.ItemStack.parseOptional(
                    registries, freq.getCompound("First"));
            frequencySecond = net.minecraft.world.item.ItemStack.parseOptional(
                    registries, freq.getCompound("Second"));
            frequencyBackFirst = net.minecraft.world.item.ItemStack.parseOptional(
                    registries, freq.getCompound("BackFirst"));
            frequencyBackSecond = net.minecraft.world.item.ItemStack.parseOptional(
                    registries, freq.getCompound("BackSecond"));
        }
    }

    // ------------------------------------------------------------------ Inner: link entry

    /**
     * A lightweight {@link IRedstoneLinkable} transmitter that carries our variable power
     * level (0-15) into the Create redstone link network.
     *
     * Unlike the typewriter's {@code KeyboardEntry} (which only sends 15 or 0), this one
     * sends whatever {@code power} is set to, enabling true analog control.
     */
    public static class AnalogLinkEntry implements IRedstoneLinkable {

        private final BlockPos pos;
        private final Couple<RedstoneLinkNetworkHandler.Frequency> key;
        private final int power;

        public AnalogLinkEntry(BlockPos pos,
                               Couple<RedstoneLinkNetworkHandler.Frequency> key,
                               int power) {
            this.pos = pos;
            this.key = key;
            this.power = power;
        }

        @Override
        public int getTransmittedStrength() {
            return power;
        }

        @Override
        public void setReceivedStrength(int strength) {
            // Transmitter only; ignore.
        }

        @Override
        public boolean isListening() {
            return false;
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
            return key;
        }

        @Override
        public BlockPos getLocation() {
            return pos;
        }
    }
}