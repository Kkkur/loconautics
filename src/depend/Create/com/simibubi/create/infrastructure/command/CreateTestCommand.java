/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.schematics.SchematicExport;
import com.simibubi.create.content.schematics.client.SchematicAndQuillHandler;
import com.simibubi.create.foundation.utility.CreatePaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CreateTestCommand {
    private static final Path gametests = CreatePaths.GAME_DIR.getParent().resolve("src/main/resources/data/create/structure/gametest").toAbsolutePath();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal((String)"test").then(Commands.literal((String)"export").then(Commands.argument((String)"path", (ArgumentType)StringArgumentType.greedyString()).suggests(CreateTestCommand::getSuggestions).executes(ctx -> CreateTestCommand.handleExport((CommandSourceStack)ctx.getSource(), ((CommandSourceStack)ctx.getSource()).getLevel(), StringArgumentType.getString((CommandContext)ctx, (String)"path")))));
    }

    private static int handleExport(CommandSourceStack source, ServerLevel level, String path) {
        SchematicAndQuillHandler handler = CreateClient.SCHEMATIC_AND_QUILL_HANDLER;
        if (handler.firstPos == null || handler.secondPos == null) {
            source.sendFailure((Component)Component.literal((String)"You must select an area with the Schematic and Quill first."));
            return 0;
        }
        SchematicExport.SchematicExportResult result = SchematicExport.saveSchematic(gametests, path, true, (Level)level, handler.firstPos, handler.secondPos);
        if (result == null) {
            source.sendFailure((Component)Component.literal((String)"Failed to export, check logs").withStyle(ChatFormatting.RED));
        } else {
            CreateTestCommand.sendSuccess(source, "Successfully exported test!", ChatFormatting.GREEN);
            CreateTestCommand.sendSuccess(source, "Overwritten: " + result.overwritten(), ChatFormatting.AQUA);
            CreateTestCommand.sendSuccess(source, "File: " + String.valueOf(result.file()), ChatFormatting.GRAY);
        }
        return 0;
    }

    private static void sendSuccess(CommandSourceStack source, String text, ChatFormatting color) {
        source.sendSuccess(() -> Component.literal((String)text).withStyle(color), true);
    }

    private static CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String path = builder.getRemaining();
        if (!path.contains("/") || path.contains("..")) {
            return CreateTestCommand.findInDir(gametests, builder);
        }
        int lastSlash = path.lastIndexOf("/");
        Path subDir = gametests.resolve(path.substring(0, lastSlash));
        if (Files.exists(subDir, new LinkOption[0])) {
            CreateTestCommand.findInDir(subDir, builder);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> findInDir(Path dir, SuggestionsBuilder builder) {
        try (Stream<Path> paths = Files.list(dir);){
            paths.filter(p -> Files.isDirectory(p, new LinkOption[0]) || p.toString().endsWith(".nbt")).forEach(path -> {
                Object file = path.toString().replaceAll("\\\\", "/").substring(gametests.toString().length() + 1);
                if (Files.isDirectory(path, new LinkOption[0])) {
                    file = (String)file + "/";
                }
                builder.suggest((String)file);
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }
}
