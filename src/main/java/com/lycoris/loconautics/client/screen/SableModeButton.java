package com.lycoris.loconautics.client.screen;

import com.lycoris.loconautics.network.AssembleAsPhysicsTrainPacket;

import com.simibubi.create.content.trains.station.WideIconButton;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Factory for the "Assemble as Physics Train" button shown on the station assembly screen.
 *
 * <p>A {@link WideIconButton} (26x18) carrying our custom 24x16 {@link SableIcon} train texture, to
 * match the look of the vanilla assemble button. Pressing it sends
 * {@link AssembleAsPhysicsTrainPacket} to the server.
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
    public static WideIconButton create(int x, int y, BlockPos stationPos) {
        WideIconButton button = new WideIconButton(x, y, SableIcon.SABLE_BUTTON);
        button.setToolTip(Component.translatable("loconautics.station.assemble_physics_train"));
        button.withCallback(() ->
                PacketDistributor.sendToServer(new AssembleAsPhysicsTrainPacket(stationPos)));
        return button;
    }
}
