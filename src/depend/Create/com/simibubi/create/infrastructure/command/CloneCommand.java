/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.pattern.BlockInWorld
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.infrastructure.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CloneCommand {
    private static final Dynamic2CommandExceptionType CLONE_TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((arg1, arg2) -> Component.translatable((String)"commands.clone.toobig", (Object[])new Object[]{arg1, arg2}));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"clone").requires(cs -> cs.hasPermission(2))).then(Commands.argument((String)"begin", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"end", (ArgumentType)BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument((String)"destination", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.literal((String)"skipBlocks").executes(ctx -> CloneCommand.doClone((CommandSourceStack)ctx.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"begin"), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"end"), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"destination"), false)))).executes(ctx -> CloneCommand.doClone((CommandSourceStack)ctx.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"begin"), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"end"), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"destination"), true)))))).executes(ctx -> {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Clones all blocks as well as super glue from the specified area to the target destination"), true);
            return 1;
        });
    }

    private static int doClone(CommandSourceStack source, BlockPos begin, BlockPos end, BlockPos destination, boolean cloneBlocks) throws CommandSyntaxException {
        int limit;
        BoundingBox sourceArea = BoundingBox.fromCorners((Vec3i)begin, (Vec3i)end);
        BlockPos destinationEnd = destination.offset(sourceArea.getLength());
        BoundingBox destinationArea = BoundingBox.fromCorners((Vec3i)destination, (Vec3i)destinationEnd);
        ServerLevel world = source.getLevel();
        int i = sourceArea.getXSpan() * sourceArea.getYSpan() * sourceArea.getZSpan();
        if (i > (limit = world.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT))) {
            throw CLONE_TOO_BIG_EXCEPTION.create((Object)limit, (Object)i);
        }
        if (!world.hasChunksAt(begin, end) || !world.hasChunksAt(destination, destinationEnd)) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
        BlockPos diffToTarget = new BlockPos(destinationArea.minX() - sourceArea.minX(), destinationArea.minY() - sourceArea.minY(), destinationArea.minZ() - sourceArea.minZ());
        int blockPastes = cloneBlocks ? CloneCommand.cloneBlocks(sourceArea, world, diffToTarget) : 0;
        int gluePastes = CloneCommand.cloneGlue(sourceArea, world, diffToTarget);
        if (cloneBlocks) {
            source.sendSuccess(() -> Component.literal((String)("Successfully cloned " + blockPastes + " Blocks")), true);
        }
        source.sendSuccess(() -> Component.literal((String)("Successfully applied glue " + gluePastes + " times")), true);
        return blockPastes + gluePastes;
    }

    private static int cloneGlue(BoundingBox sourceArea, ServerLevel world, BlockPos diffToTarget) {
        int gluePastes = 0;
        AABB bb = new AABB((double)sourceArea.minX(), (double)sourceArea.minY(), (double)sourceArea.minZ(), (double)(sourceArea.maxX() + 1), (double)(sourceArea.maxY() + 1), (double)(sourceArea.maxZ() + 1));
        for (SuperGlueEntity g : SuperGlueEntity.collectCropped((Level)world, bb)) {
            g.setPos(g.position().add(Vec3.atLowerCornerOf((Vec3i)diffToTarget)));
            world.addFreshEntity((Entity)g);
            ++gluePastes;
        }
        return gluePastes;
    }

    private static int cloneBlocks(BoundingBox sourceArea, ServerLevel world, BlockPos diffToTarget) {
        BlockEntity be;
        int blockPastes = 0;
        ArrayList blocks = Lists.newArrayList();
        ArrayList beBlocks = Lists.newArrayList();
        for (int z = sourceArea.minZ(); z <= sourceArea.maxZ(); ++z) {
            for (int y = sourceArea.minY(); y <= sourceArea.maxY(); ++y) {
                for (int x = sourceArea.minX(); x <= sourceArea.maxX(); ++x) {
                    BlockPos currentPos = new BlockPos(x, y, z);
                    BlockPos newPos = currentPos.offset((Vec3i)diffToTarget);
                    BlockInWorld cached = new BlockInWorld((LevelReader)world, currentPos, false);
                    BlockState state = cached.getState();
                    BlockEntity be2 = world.getBlockEntity(currentPos);
                    if (be2 != null) {
                        CompoundTag nbt = be2.saveWithFullMetadata((HolderLookup.Provider)world.registryAccess());
                        beBlocks.add(new StructureTemplate.StructureBlockInfo(newPos, state, nbt));
                        continue;
                    }
                    blocks.add(new StructureTemplate.StructureBlockInfo(newPos, state, null));
                }
            }
        }
        ArrayList allBlocks = Lists.newArrayList();
        allBlocks.addAll(blocks);
        allBlocks.addAll(beBlocks);
        List reverse = Lists.reverse((List)allBlocks);
        for (StructureTemplate.StructureBlockInfo info : reverse) {
            be = world.getBlockEntity(info.pos());
            Clearable.tryClear((Object)be);
            world.setBlock(info.pos(), Blocks.BARRIER.defaultBlockState(), 2);
        }
        for (StructureTemplate.StructureBlockInfo info : allBlocks) {
            if (!world.setBlock(info.pos(), info.state(), 2)) continue;
            ++blockPastes;
        }
        for (StructureTemplate.StructureBlockInfo info : beBlocks) {
            be = world.getBlockEntity(info.pos());
            if (be != null && info.nbt() != null) {
                info.nbt().putInt("x", info.pos().getX());
                info.nbt().putInt("y", info.pos().getY());
                info.nbt().putInt("z", info.pos().getZ());
                be.loadWithComponents(info.nbt(), (HolderLookup.Provider)world.registryAccess());
                be.setChanged();
            }
            world.setBlock(info.pos(), info.state(), 2);
        }
        for (StructureTemplate.StructureBlockInfo info : reverse) {
            world.blockUpdated(info.pos(), info.state().getBlock());
        }
        world.getBlockTicks().copyArea(sourceArea, (Vec3i)diffToTarget);
        return blockPastes;
    }
}
