package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.network.packets.AnalogControllerInputPacket;
import com.simibubi.create.foundation.utility.ControlsUtil;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.HashSet;

/**
 * Client-side handler for the Analog Controller.
 *
 * Mirrors {@code ControlsHandler} from Create, adapted for our stationary block:
 *  – Polls the same 6 Create key bindings every tick.
 *  – Sends {@link AnalogControllerInputPacket} to the server on key-state changes.
 *  – Throttles keepalive packets to every PACKET_RATE ticks.
 *  – Stops when the player presses Escape or moves out of range.
 *
 * Key indices (from {@link ControlsUtil#getControls()}):
 *   0 = W (forward)   → raise signal
 *   1 = S (back)      → lower signal
 *   2 = A (left)      → unused
 *   3 = D (right)     → unused
 *   4 = Space (jump)  → unused
 *   5 = Shift         → toggle lock
 */
@OnlyIn(Dist.CLIENT)
public class AnalogControllerClientHandler {

    private static final int PACKET_RATE = 5; // ticks between keepalive packets

    private static BlockPos mountedPos = null;
    private static Collection<Integer> currentlyPressed = new HashSet<>();
    private static int packetCooldown = 0;


    // ------------------------------------------------------------------ public API

    public static void startControlling(BlockPos pos) {
        mountedPos = pos;
        currentlyPressed.clear();
        packetCooldown = 0;
        Minecraft.getInstance().player.displayClientMessage(
                Component.translatable("block.loconautics.analog_controller.start"),
                true
        );
    }

    /**
     * Called when the CLIENT initiates dismount (ESC).
     * Sends a dismount packet to the server so it can update block state.
     */
    public static void stopControllingClient() {
        if (mountedPos == null) return;
        // Send dismount to server — it will call toggleUser and send back a mount=false packet
        CatnipServices.NETWORK.sendToServer(
                new com.lycoris.loconautics.network.packets.AnalogControllerDismountPacket(mountedPos)
        );
        stopControlling();
    }

    /**
     * Called when the SERVER initiated dismount (right-click, range check, etc).
     * Does NOT send a packet — server already knows.
     */
    public static void stopControlling() {
        if (mountedPos == null) return;

        // Release any suppressed keys
        ControlsUtil.getControls().forEach(kb -> kb.setDown(ControlsUtil.isActuallyPressed(kb)));

        // Tell server we released all held keys
        if (!currentlyPressed.isEmpty()) {
            CatnipServices.NETWORK.sendToServer(
                    new AnalogControllerInputPacket(currentlyPressed, false, mountedPos)
            );
        }

        Minecraft.getInstance().player.displayClientMessage(
                Component.translatable("block.loconautics.analog_controller.stop"),
                true
        );

        mountedPos = null;
        currentlyPressed.clear();
        packetCooldown = 0;
    }

    /** Whether the client is currently mounted on a controller. */
    public static boolean isControlling() {
        return mountedPos != null;
    }

    public static BlockPos getMountedPos() {
        return mountedPos;
    }

    // ------------------------------------------------------------------ tick (called from client mod event)

    // GLFW key codes for the Create controls we care about
    // Indices match ControlsUtil.getControls(): 0=W, 1=S, 2=A, 3=D, 4=Space, 5=Shift
    private static final int[] GLFW_KEY_FOR_INDEX = {
            87,  // W
            83,  // S
            65,  // A
            68,  // D
            32,  // Space
            340  // Left Shift
    };

    /**
     * Tick the handler. Must be called every client tick.
     */
    public static void tick() {
        if (mountedPos == null) return;

        Minecraft mc = Minecraft.getInstance();

        // --- Guard: stop controlling if the block no longer exists at mountedPos.
        // This covers the case where the block is broken while the player is mounted.
        // The server will already have sent a dismount packet via onRemoved(), but we
        // also check client-side so input is suppressed for at most one tick.
        if (mc.level != null) {
            net.minecraft.world.level.block.state.BlockState bs = mc.level.getBlockState(mountedPos);
            if (!(bs.getBlock() instanceof com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlock)) {
                // Block is gone — stop without sending a packet (server already handled it)
                stopControlling();
                return;
            }
        }

        long window = mc.getWindow().getWindow();

        // Poll W (0) and S (1) directly via GLFW — ControlsUtil.isActuallyPressed is
        // unreliable here because we call setDown(false) at the end of each tick, which
        // zeroes the internal state before the next poll.
        HashSet<Integer> pressedKeys = new HashSet<>();
        for (int i = 0; i < GLFW_KEY_FOR_INDEX.length; i++) {
            if (com.mojang.blaze3d.platform.InputConstants.isKeyDown(window, GLFW_KEY_FOR_INDEX[i])) {
                pressedKeys.add(i);
            }
        }

        // Diff against last tick
        HashSet<Integer> newKeys = new HashSet<>(pressedKeys);
        Collection<Integer> releasedKeys = new HashSet<>(currentlyPressed);
        newKeys.removeAll(currentlyPressed);
        releasedKeys.removeAll(pressedKeys);

        if (!releasedKeys.isEmpty()) {
            CatnipServices.NETWORK.sendToServer(
                    new AnalogControllerInputPacket(releasedKeys, false, mountedPos)
            );
        }

        if (!newKeys.isEmpty()) {
            CatnipServices.NETWORK.sendToServer(
                    new AnalogControllerInputPacket(newKeys, true, mountedPos)
            );
            packetCooldown = PACKET_RATE;
        }

        // Keepalive for held keys
        if (packetCooldown <= 0 && !pressedKeys.isEmpty()) {
            CatnipServices.NETWORK.sendToServer(
                    new AnalogControllerInputPacket(pressedKeys, true, mountedPos)
            );
            packetCooldown = PACKET_RATE;
        }

        if (packetCooldown > 0) packetCooldown--;

        currentlyPressed = pressedKeys;

        // Suppress key events from reaching the game
        ControlsUtil.getControls().forEach(kb -> kb.setDown(false));
    }
}