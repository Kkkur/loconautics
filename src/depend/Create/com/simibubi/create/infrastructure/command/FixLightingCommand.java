/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.neoforged.neoforge.common.NeoForgeConfig
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForgeConfig;

public class FixLightingCommand {
    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"fixLighting").requires(cs -> cs.hasPermission(0))).executes(ctx -> {
            NeoForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.set((Object)true);
            Minecraft.getInstance().levelRenderer.allChanged();
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"NeoForge's experimental block rendering pipeline is now enabled."), true);
            return 1;
        });
    }
}
