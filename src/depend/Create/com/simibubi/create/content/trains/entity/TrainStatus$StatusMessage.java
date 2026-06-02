/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.trains.entity;

import java.util.Arrays;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public record TrainStatus.StatusMessage(Component[] messages) {
    public void displayToPlayer(Player player) {
        Arrays.stream(this.messages).forEach(messages -> player.displayClientMessage(messages, false));
    }
}
