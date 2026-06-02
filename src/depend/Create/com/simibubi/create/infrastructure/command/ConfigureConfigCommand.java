/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public abstract class ConfigureConfigCommand {
    protected final String commandLiteral;

    ConfigureConfigCommand(String commandLiteral) {
        this.commandLiteral = commandLiteral;
    }

    ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)this.commandLiteral).requires(cs -> cs.hasPermission(0))).then(Commands.literal((String)"on").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
            this.sendPacket(player, String.valueOf(true));
            return 1;
        }))).then(Commands.literal((String)"off").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
            this.sendPacket(player, String.valueOf(false));
            return 1;
        }))).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
            this.sendPacket(player, "info");
            return 1;
        });
    }

    protected abstract void sendPacket(ServerPlayer var1, String var2);
}
