/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.network.VeilPacketManager
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.command.SableAssembleCommands;
import dev.ryanhcode.sable.command.SableConfigCommands;
import dev.ryanhcode.sable.command.SableJointCommands;
import dev.ryanhcode.sable.command.SablePhysicsCommands;
import dev.ryanhcode.sable.command.SableSpawnCommands;
import dev.ryanhcode.sable.command.SableStorageCommands;
import dev.ryanhcode.sable.command.SableSubLevelCommands;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundEnterGizmoPacket;
import dev.ryanhcode.sable.network.packets.udp.SableUDPEchoPacket;
import dev.ryanhcode.sable.network.udp.SableUDPServer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import foundry.veil.api.network.VeilPacketManager;
import java.util.Collection;
import java.util.Formatter;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SableCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder sableBuilder = (LiteralArgumentBuilder)Commands.literal((String)"sable").requires(commandSourceStack -> commandSourceStack.hasPermission(2));
        SablePhysicsCommands.register((LiteralArgumentBuilder<CommandSourceStack>)sableBuilder, buildContext);
        SableSpawnCommands.register((LiteralArgumentBuilder<CommandSourceStack>)sableBuilder, buildContext);
        SableSubLevelCommands.register((LiteralArgumentBuilder<CommandSourceStack>)sableBuilder, buildContext);
        SableAssembleCommands.register((LiteralArgumentBuilder<CommandSourceStack>)sableBuilder, buildContext);
        SableStorageCommands.register((LiteralArgumentBuilder<CommandSourceStack>)sableBuilder, buildContext);
        LiteralArgumentBuilder debugBuilder = Commands.literal((String)"debug");
        SableJointCommands.register((LiteralArgumentBuilder<CommandSourceStack>)debugBuilder, buildContext);
        SableConfigCommands.register((LiteralArgumentBuilder<CommandSourceStack>)debugBuilder, buildContext);
        sableBuilder.then(debugBuilder.then(Commands.literal((String)"udp_test").executes(ctx -> {
            SableUDPServer server = SableUDPServer.getServer(((CommandSourceStack)ctx.getSource()).getServer());
            if (server != null) {
                server.sendUDPPacket(((CommandSourceStack)ctx.getSource()).getPlayerOrException(), new SableUDPEchoPacket("Skibidi Toilet"), true);
            }
            return 1;
        })));
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)sableBuilder.then(Commands.literal((String)"engage_gizmo").executes(SableCommand::executeEnableGizmoCommand))).then(((LiteralArgumentBuilder)Commands.literal((String)"paused").executes(SableCommand::executeTogglePhysicsPausedCommand)).then(Commands.argument((String)"paused", (ArgumentType)BoolArgumentType.bool()).executes(SableCommand::executeSetPhysicsPausedCommand)))).then(Commands.literal((String)"info").then(Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.subLevels()).executes(ctx -> {
            CommandSourceStack source = (CommandSourceStack)ctx.getSource();
            ServerSubLevelContainer container = SableCommandHelper.requireSubLevelContainer(source);
            Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels((CommandContext<CommandSourceStack>)ctx, "sub_level");
            if (subLevels.isEmpty()) {
                throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
            }
            source.sendSuccess(() -> Component.translatable((String)"commands.sable.info.count", (Object[])new Object[]{subLevels.size()}), false);
            for (ServerSubLevel subLevel : subLevels) {
                Pose3d pose = subLevel.logicalPose();
                source.sendSuccess(() -> SableCommand.lambda$register$3((Pose3dc)pose, subLevel), false);
                source.sendSuccess(() -> SableCommand.lambda$register$4((Pose3dc)pose), false);
                source.sendSuccess(() -> SableCommand.lambda$register$5((Pose3dc)pose), false);
                source.sendSuccess(() -> Component.translatable((String)"commands.sable.info.mass", (Object[])new Object[]{subLevel.getMassTracker().getMass()}), false);
                SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
                RigidBodyHandle handle = physicsSystem.getPhysicsHandle(subLevel);
                source.sendSuccess(() -> {
                    Vector3d pos = handle.getLinearVelocity(new Vector3d());
                    return Component.translatable((String)"commands.sable.info.linear_velocity", (Object[])new Object[]{pos.x(), pos.y(), pos.z()});
                }, false);
                source.sendSuccess(() -> {
                    Vector3d pos = handle.getAngularVelocity(new Vector3d());
                    return Component.translatable((String)"commands.sable.info.angular_velocity", (Object[])new Object[]{pos.x(), pos.y(), pos.z()});
                }, false);
            }
            return subLevels.size();
        })));
        dispatcher.register(sableBuilder);
    }

    private static int executeEnableGizmoCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        SableCommandHelper.requireSubLevelPhysicsSystem(ctx).setPaused(true);
        VeilPacketManager.player((ServerPlayer)player).sendPacket(new CustomPacketPayload[]{new ClientboundEnterGizmoPacket()});
        return 1;
    }

    private static int executeTogglePhysicsPausedCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        boolean pause = !SableCommandHelper.requireSubLevelPhysicsSystem(ctx).getPaused();
        SableCommandHelper.requireSubLevelPhysicsSystem(ctx).setPaused(pause);
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.physics.paused_toggled.success", (Object[])new Object[]{Boolean.toString(pause)}), true);
        return 1;
    }

    private static int executeSetPhysicsPausedCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        boolean pause = BoolArgumentType.getBool(ctx, (String)"paused");
        SableCommandHelper.requireSubLevelPhysicsSystem(ctx).setPaused(pause);
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.physics.paused.success", (Object[])new Object[]{Boolean.toString(pause)}), true);
        return 1;
    }

    private static /* synthetic */ Component lambda$register$5(Pose3dc pose) {
        Quaterniondc orientation = pose.orientation();
        return Component.translatable((String)"commands.sable.info.orientation", (Object[])new Object[]{orientation.x(), orientation.y(), orientation.z(), orientation.w()});
    }

    private static /* synthetic */ Component lambda$register$4(Pose3dc pose) {
        Vector3dc pos = pose.position();
        return Component.translatable((String)"commands.sable.info.position", (Object[])new Object[]{pos.x(), pos.y(), pos.z()});
    }

    private static /* synthetic */ Component lambda$register$3(Pose3dc pose, ServerSubLevel subLevel) {
        Vector3dc pos = pose.position();
        MutableComponent component = Component.translatable((String)"commands.sable.info.name", (Object[])new Object[]{Component.literal((String)(subLevel.getName() != null ? subLevel.getName() : subLevel.getUniqueId().toString()))});
        ResourceLocation dimension = subLevel.getLevel().dimension().location();
        GlobalSavedSubLevelPointer pointer = subLevel.getLastSerializationPointer();
        MutableComponent fileId = Component.translatable((String)"commands.sable.info.name.tooltip", (Object[])new Object[]{pointer != null ? pointer.toString() : "None yet"});
        component.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, new Formatter().format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f", dimension, pos.x(), pos.y(), pos.z()).toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Object)fileId)).withColor(ChatFormatting.GRAY));
        return component;
    }
}
