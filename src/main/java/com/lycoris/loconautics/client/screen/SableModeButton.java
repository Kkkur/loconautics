package com.lycoris.loconautics.client.screen;

import com.lycoris.loconautics.network.AssembleAsPhysicsTrainPacket;

import com.simibubi.create.content.trains.station.WideIconButton;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Factory for the "Assemble as Physics Train" button shown on the station assembly screen.
 *
 * <p>Built on Create's {@link WideIconButton} so it matches the look of the vanilla assemble button.
 * Uses a placeholder icon for now (I_ASSEMBLE_TRAIN); a dedicated texture is swapped in later.
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
    public static WideIconButton create(int x, int y, BlockPos stationPos) {
        // TODO: replace AllGuiTextures.I_ASSEMBLE_TRAIN with a dedicated Loconautics texture.
        WideIconButton button = new WideIconButton(x, y, AllGuiTextures.I_ASSEMBLE_TRAIN);
        button.setToolTip(Component.translatable("loconautics.station.assemble_physics_train"));
        button.withCallback(() ->
                PacketDistributor.sendToServer(new AssembleAsPhysicsTrainPacket(stationPos)));
        return button;
    }
}
