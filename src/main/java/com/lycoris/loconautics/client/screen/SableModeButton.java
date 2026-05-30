package com.lycoris.loconautics.client.screen;

import com.lycoris.loconautics.network.AssembleAsPhysicsTrainPacket;

import com.simibubi.create.foundation.gui.widget.IconButton;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Factory for the "Assemble as Physics Train" button shown on the station assembly screen.
 *
 * <p>A square {@link IconButton} (18x18) carrying our custom {@link SableIcon} train texture.
 * Pressing it sends {@link AssembleAsPhysicsTrainPacket} to the server.
 */
public final class SableModeButton {

    private SableModeButton() {
    }

    /**
     * Creates a configured button.
     *
     * @param x          left pixel
     * @param y          top pixel
     * @param stationPos position of the station block entity (sent to the server on press)
     */
    public static IconButton create(int x, int y, BlockPos stationPos) {
        IconButton button = new IconButton(x, y, SableIcon.SABLE_BUTTON);
        button.setToolTip(Component.translatable("loconautics.station.assemble_physics_train"));
        button.withCallback(() ->
                PacketDistributor.sendToServer(new AssembleAsPhysicsTrainPacket(stationPos)));
        return button;
    }
}
