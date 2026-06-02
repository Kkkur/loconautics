/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  net.createmod.catnip.command.CatnipCommands
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.simibubi.create.infrastructure.command.CameraAngleCommand;
import com.simibubi.create.infrastructure.command.CameraDistanceCommand;
import com.simibubi.create.infrastructure.command.ClearBufferCacheCommand;
import com.simibubi.create.infrastructure.command.CloneCommand;
import com.simibubi.create.infrastructure.command.CouplingCommand;
import com.simibubi.create.infrastructure.command.CreateTestCommand;
import com.simibubi.create.infrastructure.command.DebugInfoCommand;
import com.simibubi.create.infrastructure.command.DumpRailwaysCommand;
import com.simibubi.create.infrastructure.command.FabulousWarningCommand;
import com.simibubi.create.infrastructure.command.FixLightingCommand;
import com.simibubi.create.infrastructure.command.GlueCommand;
import com.simibubi.create.infrastructure.command.HighlightCommand;
import com.simibubi.create.infrastructure.command.OverlayConfigCommand;
import com.simibubi.create.infrastructure.command.PassengerCommand;
import com.simibubi.create.infrastructure.command.ReplaceInCommandBlocksCommand;
import com.simibubi.create.infrastructure.command.ToggleDebugCommand;
import com.simibubi.create.infrastructure.command.TrainCommand;
import net.createmod.catnip.command.CatnipCommands;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AllCommands {
    public static void registerClient(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> util = AllCommands.buildClientUtilityCommands();
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"create").requires(cs -> cs.hasPermission(0))).then(ToggleDebugCommand.register())).then(FabulousWarningCommand.register())).then(OverlayConfigCommand.register())).then(FixLightingCommand.register())).then(util);
        LiteralCommandNode createRoot = dispatcher.register(root);
        createRoot.addChild((CommandNode)CatnipCommands.buildRedirect((String)"u", util));
        CatnipCommands.createOrAddToShortcut(dispatcher, (String)"c", (LiteralCommandNode)createRoot);
    }

    private static LiteralCommandNode<CommandSourceStack> buildClientUtilityCommands() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"util").then(ClearBufferCacheCommand.register())).then(CameraDistanceCommand.register())).then(CameraAngleCommand.register())).build();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> util = AllCommands.buildUtilityCommands();
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"create").requires(cs -> cs.hasPermission(0))).then(DumpRailwaysCommand.register())).then(DebugInfoCommand.register())).then(HighlightCommand.register())).then(PassengerCommand.register())).then(CouplingCommand.register())).then(CloneCommand.register())).then(TrainCommand.register())).then(GlueCommand.register())).then(util);
        if (CatnipServices.PLATFORM.isDevelopmentEnvironment() && CatnipServices.PLATFORM.getEnv().isClient()) {
            root.then(CreateTestCommand.register());
        }
        LiteralCommandNode createRoot = dispatcher.register(root);
        createRoot.addChild((CommandNode)CatnipCommands.buildRedirect((String)"u", util));
        CatnipCommands.createOrAddToShortcut(dispatcher, (String)"c", (LiteralCommandNode)createRoot);
    }

    private static LiteralCommandNode<CommandSourceStack> buildUtilityCommands() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"util").then(ReplaceInCommandBlocksCommand.register())).build();
    }
}
