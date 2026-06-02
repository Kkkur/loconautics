/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.simibubi.create.content.equipment.goggles.GoggleConfigScreen;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class OverlayConfigCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"overlay").requires(cs -> cs.hasPermission(0))).then(Commands.literal((String)"reset").executes(ctx -> {
            AllConfigs.client().overlayOffsetX.set((Object)0);
            AllConfigs.client().overlayOffsetY.set((Object)0);
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Create Goggle Overlay has been reset to default position"), true);
            return 1;
        }))).executes(ctx -> {
            ScreenOpener.open((Screen)new GoggleConfigScreen());
            return 1;
        });
    }
}
