/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.debugInfo.ServerDebugInfoPacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class DebugInfoCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal((String)"debuginfo").executes(ctx -> {
            CommandSourceStack source = (CommandSourceStack)ctx.getSource();
            ServerPlayer player = source.getPlayerOrException();
            Create.lang().translate("command.debuginfo.sending", new Object[0]).sendChat((Player)player);
            CatnipServices.NETWORK.sendToClient(player, (CustomPacketPayload)new ServerDebugInfoPacket((Player)player));
            return 1;
        });
    }
}
