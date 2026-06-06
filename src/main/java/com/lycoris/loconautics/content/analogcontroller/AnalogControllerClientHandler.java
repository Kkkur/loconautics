package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.network.packets.AnalogControllerInputPacket;
import com.simibubi.create.foundation.utility.ControlsUtil;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
    private static final int ESC_KEY = 256;   // GLFW_KEY_ESCAPE

    private static BlockPos mountedPos = null;
    private static Collection<Integer> currentlyPressed = new HashSet<>();
    private static int packetCooldown = 0;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("AnalogControllerClientHandler");

    // ------------------------------------------------------------------ public API

    public static void startControlling(BlockPos pos) {
        LOGGER.info("startControlling called, pos={}", pos);
        mountedPos = pos;
        currentlyPressed.clear();
        packetCooldown = 0;
        Minecraft.getInstance().player.displayClientMessage(
                Component.translatable("block.loconautics.analog_controller.start"),
                true
        );
    }

    /**
     * Called to forcibly release control (e.g. player moves away, dismounts).
     */
    public static void stopControlling() {
        if (mountedPos == null) return;

        // Restore key states that we suppressed
        ControlsUtil.getControls().forEach(kb -> kb.setDown(ControlsUtil.isActuallyPressed(kb)));

        // Tell server we released everything
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

    /**
     * Tick the handler. Must be called every client tick.
     */
    public static void tick() {
        if (mountedPos == null) return;

        // Escape → dismount
        if (com.mojang.blaze3d.platform.InputConstants.isKeyDown(
                Minecraft.getInstance().getWindow().getWindow(), ESC_KEY)) {
            stopControlling();
            // Let server know via zero-press packet
            CatnipServices.NETWORK.sendToServer(
                    new AnalogControllerInputPacket(new HashSet<>(), false, mountedPos == null
                            ? BlockPos.ZERO : mountedPos)
            );
            return;
        }

        // Suppress game processing of our control keys
        List<KeyMapping> controls = ControlsUtil.getControls();
        HashSet<Integer> pressedKeys = new HashSet<>();
        for (int i = 0; i < controls.size(); i++) {
            if (ControlsUtil.isActuallyPressed(controls.get(i))) {
                pressedKeys.add(i);
            }
        }

        // Diff
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

        // Keepalive
        if (packetCooldown <= 0 && !pressedKeys.isEmpty()) {
            CatnipServices.NETWORK.sendToServer(
                    new AnalogControllerInputPacket(pressedKeys, true, mountedPos)
            );
            packetCooldown = PACKET_RATE;
        }

        if (packetCooldown > 0) packetCooldown--;

        currentlyPressed = pressedKeys;

        // Suppress key events from reaching the game
        controls.forEach(kb -> kb.setDown(false));
    }
}