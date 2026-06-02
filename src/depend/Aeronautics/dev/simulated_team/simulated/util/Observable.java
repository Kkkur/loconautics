/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.VeilPacketManager
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Player
 */
package dev.simulated_team.simulated.util;

import dev.simulated_team.simulated.network.packets.BlockEntityObservedPacket;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface Observable {
    default public void onObserved(Player player) {
    }

    default public void sendObserved(BlockPos pos) {
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new BlockEntityObservedPacket(pos)});
    }
}
