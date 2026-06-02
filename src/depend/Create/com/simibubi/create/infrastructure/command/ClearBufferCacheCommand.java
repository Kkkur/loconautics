/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  net.createmod.ponder.PonderClient
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.CreateClient;
import net.createmod.ponder.PonderClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ClearBufferCacheCommand {
    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal((String)"clearRenderBuffers").executes(ctx -> {
            PonderClient.invalidateRenderers();
            CreateClient.invalidateRenderers();
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Cleared rendering buffers."), true);
            return 1;
        });
    }
}
