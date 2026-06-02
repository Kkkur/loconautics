/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SableAssembleCommands {
    public static final int DEFAULT_CONNECTED_ASSEMBLY_CAPACITY = 256000;

    public static void register(LiteralArgumentBuilder<CommandSourceStack> sableBuilder, CommandBuildContext buildContext) {
        sableBuilder.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"assemble").then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"shatter").then(Commands.literal((String)"sub_level").then(Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.subLevels()).executes(SableAssembleCommands::executeShatterSubLevelCommand)))).then(((LiteralArgumentBuilder)Commands.literal((String)"connected").executes(ctx -> SableAssembleCommands.executeShatterConnected((CommandContext<CommandSourceStack>)ctx, BlockPos.containing((Position)((CommandSourceStack)ctx.getSource()).getPosition().subtract(0.0, 1.0, 0.0)), 256000))).then(((RequiredArgumentBuilder)Commands.argument((String)"from", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> SableAssembleCommands.executeShatterConnected((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"from"), 256000))).then(Commands.argument((String)"capacity", (ArgumentType)IntegerArgumentType.integer((int)1, (int)25600000)).executes(ctx -> SableAssembleCommands.executeShatterConnected((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"from"), IntegerArgumentType.getInteger((CommandContext)ctx, (String)"capacity"))))))).then(Commands.literal((String)"sphere").then(((RequiredArgumentBuilder)Commands.argument((String)"radius", (ArgumentType)IntegerArgumentType.integer((int)0, (int)128)).executes(ctx -> SableAssembleCommands.executeShatterSphereCommand((CommandContext<CommandSourceStack>)ctx, BlockPos.containing((Position)((CommandSourceStack)ctx.getSource()).getPosition())))).then(Commands.argument((String)"origin", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> SableAssembleCommands.executeShatterSphereCommand((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"origin"))))))).then(Commands.literal((String)"cube").then(((RequiredArgumentBuilder)Commands.argument((String)"range", (ArgumentType)IntegerArgumentType.integer((int)0, (int)128)).executes(ctx -> SableAssembleCommands.executeShatterCubeCommand((CommandContext<CommandSourceStack>)ctx, BlockPos.containing((Position)((CommandSourceStack)ctx.getSource()).getPosition())))).then(Commands.argument((String)"origin", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> SableAssembleCommands.executeShatterCubeCommand((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"origin"))))))).then(Commands.literal((String)"area").then(Commands.argument((String)"from", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"to", (ArgumentType)BlockPosArgument.blockPos()).executes(SableAssembleCommands::executeShatterAreaCommand)))))).then(Commands.literal((String)"area").then(Commands.argument((String)"from", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"to", (ArgumentType)BlockPosArgument.blockPos()).executes(SableAssembleCommands::executeAssembleAreaCommand))))).then(((LiteralArgumentBuilder)Commands.literal((String)"connected").executes(ctx -> SableAssembleCommands.executeAssembleConnectedCommand((CommandContext<CommandSourceStack>)ctx, BlockPos.containing((Position)((CommandSourceStack)ctx.getSource()).getPosition().subtract(0.0, 1.0, 0.0)), 256000))).then(((RequiredArgumentBuilder)Commands.argument((String)"from", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> SableAssembleCommands.executeAssembleConnectedCommand((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"from"), 256000))).then(Commands.argument((String)"capacity", (ArgumentType)IntegerArgumentType.integer((int)1, (int)25600000)).executes(ctx -> SableAssembleCommands.executeAssembleConnectedCommand((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"from"), IntegerArgumentType.getInteger((CommandContext)ctx, (String)"capacity"))))))).then(Commands.literal((String)"sphere").then(((RequiredArgumentBuilder)Commands.argument((String)"radius", (ArgumentType)IntegerArgumentType.integer((int)0, (int)256)).executes(ctx -> SableAssembleCommands.executeAssembleSphereCommand((CommandContext<CommandSourceStack>)ctx, BlockPos.containing((Position)((CommandSourceStack)ctx.getSource()).getPosition())))).then(Commands.argument((String)"origin", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> SableAssembleCommands.executeAssembleSphereCommand((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"origin"))))))).then(Commands.literal((String)"cube").then(((RequiredArgumentBuilder)Commands.argument((String)"range", (ArgumentType)IntegerArgumentType.integer((int)0, (int)256)).executes(ctx -> SableAssembleCommands.executeAssembleCubeCommand((CommandContext<CommandSourceStack>)ctx, BlockPos.containing((Position)((CommandSourceStack)ctx.getSource()).getPosition())))).then(Commands.argument((String)"origin", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> SableAssembleCommands.executeAssembleCubeCommand((CommandContext<CommandSourceStack>)ctx, BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"origin")))))));
    }

    private static int executeShatterConnected(CommandContext<CommandSourceStack> ctx, BlockPos assemblyOrigin, int assemblyCapacity) throws CommandSyntaxException {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        SubLevelAssemblyHelper.GatherResult result = SubLevelAssemblyHelper.gatherConnectedBlocks(assemblyOrigin, level, assemblyCapacity, null);
        if (result.assemblyState() != SubLevelAssemblyHelper.GatherResult.State.SUCCESS) {
            CommandSourceStack commandSourceStack = (CommandSourceStack)ctx.getSource();
            commandSourceStack.sendFailure((Component)Component.translatable((String)(switch (result.assemblyState()) {
                case SubLevelAssemblyHelper.GatherResult.State.TOO_MANY_BLOCKS -> "commands.sable.sub_level.shatter.connected.too_many_blocks";
                case SubLevelAssemblyHelper.GatherResult.State.NO_BLOCKS -> "commands.sable.sub_level.shatter.no_blocks";
                default -> throw new IllegalStateException("Unexpected value: " + String.valueOf((Object)result.assemblyState()));
            }), (Object[])new Object[]{result.assemblyState() == SubLevelAssemblyHelper.GatherResult.State.TOO_MANY_BLOCKS ? assemblyCapacity : 0}));
            return 0;
        }
        int blocksShattered = SableAssembleCommands.shatterBlocks(result.blocks(), level);
        if (blocksShattered == 0) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.shatter.no_blocks"));
            return 0;
        }
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.shatter.connected.success", (Object[])new Object[]{blocksShattered}), true);
        return blocksShattered;
    }

    private static int executeShatterSubLevelCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level");
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        IntStream shatteredAmounts = subLevels.stream().filter(subLevel -> {
            int solidBlockCount = 0;
            Iterator it = BlockPos.betweenClosedStream((BoundingBox)subLevel.getPlot().getBoundingBox().toMojang()).iterator();
            while (it.hasNext()) {
                BlockPos pos = (BlockPos)it.next();
                if (!VoxelNeighborhoodState.isSolid((BlockGetter)level, pos, level.getBlockState(pos)) || ++solidBlockCount <= 1) continue;
                return true;
            }
            return false;
        }).map(subLevel -> subLevel.getPlot().getBoundingBox()).mapToInt(bounds -> SableAssembleCommands.shatterBoundingBox(bounds, level));
        int blocksShattered = 0;
        int sublevelsShattered = 0;
        PrimitiveIterator.OfInt it = shatteredAmounts.iterator();
        while (it.hasNext()) {
            int i = it.next();
            blocksShattered += i;
            ++sublevelsShattered;
        }
        if (sublevelsShattered == 0) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.shatter.sub_level.only_single_block"));
            return 0;
        }
        int finalSublevelsShattered = sublevelsShattered;
        int finalBlocksShattered = blocksShattered;
        if (sublevelsShattered == 1) {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.shatter.sub_level.success", (Object[])new Object[]{Component.translatable((String)"commands.sable.sub_level"), finalBlocksShattered}), true);
        } else {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.shatter.sub_level.success", (Object[])new Object[]{Component.translatable((String)"commands.sable.sub_levels", (Object[])new Object[]{finalSublevelsShattered}), finalBlocksShattered}), true);
        }
        return blocksShattered;
    }

    private static int executeShatterAreaCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        BoundingBox3i boundingBox = new BoundingBox3i(BlockPosArgument.getLoadedBlockPos(ctx, (String)"from"), BlockPosArgument.getLoadedBlockPos(ctx, (String)"to"));
        int blocksShattered = SableAssembleCommands.shatterBoundingBox((BoundingBox3ic)boundingBox, level);
        if (blocksShattered == 0) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.shatter.no_blocks"));
            return 0;
        }
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.shatter.region.success", (Object[])new Object[]{blocksShattered}), true);
        return blocksShattered;
    }

    private static int executeShatterSphereCommand(CommandContext<CommandSourceStack> ctx, BlockPos origin) {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        int radius = IntegerArgumentType.getInteger(ctx, (String)"radius");
        BoundingBox boundingBox = BoundingBox.fromCorners((Vec3i)origin.offset(-radius, -radius, -radius), (Vec3i)origin.offset(radius, radius, radius));
        int radiusSquared = radius * radius;
        List<BlockPos> blocks = BlockPos.betweenClosedStream((BoundingBox)boundingBox).map(BlockPos::immutable).toList();
        ArrayList<BlockPos> blocksInRadius = new ArrayList<BlockPos>();
        for (BlockPos blockPos : blocks) {
            if (origin.distSqr((Vec3i)blockPos) > (double)radiusSquared) continue;
            blocksInRadius.add(blockPos);
        }
        int blocksShattered = SableAssembleCommands.shatterBlocks(blocksInRadius, level);
        if (blocksShattered == 0) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.shatter.no_blocks"));
            return 0;
        }
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.shatter.radius.success", (Object[])new Object[]{blocksShattered}), true);
        return blocksShattered;
    }

    private static int executeShatterCubeCommand(CommandContext<CommandSourceStack> ctx, BlockPos origin) {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        int radius = IntegerArgumentType.getInteger(ctx, (String)"range");
        BoundingBox3i boundingBox = new BoundingBox3i(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius));
        int blocksShattered = SableAssembleCommands.shatterBoundingBox((BoundingBox3ic)boundingBox, level);
        if (blocksShattered == 0) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.shatter.no_blocks"));
            return 0;
        }
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.shatter.range.success", (Object[])new Object[]{blocksShattered}), true);
        return blocksShattered;
    }

    private static int shatterBoundingBox(BoundingBox3ic boundingBox, ServerLevel level) {
        return SableAssembleCommands.shatterBlocks(BlockPos.betweenClosedStream((BoundingBox)boundingBox.toMojang()).map(BlockPos::immutable).toList(), level);
    }

    private static int shatterBlocks(Collection<BlockPos> blocks, ServerLevel level) {
        for (BlockPos pos : blocks) {
            if (VoxelNeighborhoodState.isSolid((BlockGetter)level, pos, level.getBlockState(pos))) continue;
            level.destroyBlock(pos, true);
        }
        int shattered = 0;
        for (BlockPos anchor : blocks) {
            if (!SableAssembleCommands.shatterBlockToSubLevel(level, anchor)) continue;
            ++shattered;
        }
        return shattered;
    }

    private static boolean shatterBlockToSubLevel(ServerLevel level, BlockPos anchor) {
        if (!VoxelNeighborhoodState.isSolid((BlockGetter)level, anchor, level.getBlockState(anchor))) {
            return false;
        }
        BoundingBox3i bounds = new BoundingBox3i(anchor.getX(), anchor.getY(), anchor.getZ(), anchor.getX() + 1, anchor.getY() + 1, anchor.getZ() + 1);
        bounds.set(bounds.minX - 1, bounds.minY - 1, bounds.minZ - 1, bounds.maxX + 1, bounds.maxY + 1, bounds.maxZ + 1);
        SubLevelAssemblyHelper.assembleBlocks(level, anchor, List.of(anchor), (BoundingBox3ic)bounds);
        return true;
    }

    private static int executeAssembleAreaCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        BoundingBox boundingBox = BoundingBox.fromCorners((Vec3i)BlockPosArgument.getLoadedBlockPos(ctx, (String)"from"), (Vec3i)BlockPosArgument.getLoadedBlockPos(ctx, (String)"to"));
        List<BlockPos> blocks = BlockPos.betweenClosedStream((BoundingBox)boundingBox).map(BlockPos::immutable).toList();
        BlockPos anchor = blocks.getFirst();
        BoundingBox3i bounds = new BoundingBox3i(boundingBox);
        bounds.set(bounds.minX - 1, bounds.minY - 1, bounds.minZ - 1, bounds.maxX + 1, bounds.maxY + 1, bounds.maxZ + 1);
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, anchor, blocks, (BoundingBox3ic)bounds);
        if (subLevel.getMassTracker().isInvalid()) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.assemble.no_blocks"));
            return 0;
        }
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.assemble.region.success", (Object[])new Object[]{blocks.size()}), true);
        return 1;
    }

    private static int executeAssembleCubeCommand(CommandContext<CommandSourceStack> ctx, BlockPos origin) {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        int range = IntegerArgumentType.getInteger(ctx, (String)"range");
        BoundingBox boundingBox = BoundingBox.fromCorners((Vec3i)origin.offset(-range, -range, -range), (Vec3i)origin.offset(range, range, range));
        List<BlockPos> blocks = BlockPos.betweenClosedStream((BoundingBox)boundingBox).map(BlockPos::immutable).toList();
        BlockPos anchor = blocks.getFirst();
        BoundingBox3i bounds = new BoundingBox3i(boundingBox);
        bounds.set(bounds.minX - 1, bounds.minY - 1, bounds.minZ - 1, bounds.maxX + 1, bounds.maxY + 1, bounds.maxZ + 1);
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, anchor, blocks, (BoundingBox3ic)bounds);
        if (subLevel.getMassTracker().isInvalid()) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.assemble.no_blocks"));
            return 0;
        }
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.assemble.range.success", (Object[])new Object[]{blocks.size()}), true);
        return 1;
    }

    private static int executeAssembleConnectedCommand(CommandContext<CommandSourceStack> ctx, BlockPos assemblyOrigin, int assemblyCapacity) throws CommandSyntaxException {
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        SubLevelAssemblyHelper.GatherResult result = SubLevelAssemblyHelper.gatherConnectedBlocks(assemblyOrigin, level, assemblyCapacity, null);
        if (result.assemblyState() != SubLevelAssemblyHelper.GatherResult.State.SUCCESS) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)result.assemblyState().errorKey, (Object[])new Object[]{result.assemblyState() == SubLevelAssemblyHelper.GatherResult.State.TOO_MANY_BLOCKS ? assemblyCapacity : 0}));
            return 0;
        }
        SubLevelAssemblyHelper.assembleBlocks(level, assemblyOrigin, result.blocks(), (BoundingBox3ic)result.boundingBox());
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.assemble.connected.success", (Object[])new Object[]{result.blocks().size()}), true);
        return 1;
    }

    private static int executeAssembleSphereCommand(CommandContext<CommandSourceStack> ctx, BlockPos origin) {
        int radius = IntegerArgumentType.getInteger(ctx, (String)"radius");
        ServerLevel level = ((CommandSourceStack)ctx.getSource()).getLevel();
        HashSet<BlockPos> blocks = new HashSet<BlockPos>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int radiusSquared = radius * radius;
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos pos;
                    if (x * x + y * y + z * z > radiusSquared || !level.isLoaded(pos = origin.offset(x, y, z)) || level.getBlockState(pos).isAir()) continue;
                    blocks.add(pos);
                    minX = Math.min(minX, pos.getX());
                    minY = Math.min(minY, pos.getY());
                    minZ = Math.min(minZ, pos.getZ());
                    maxX = Math.max(maxX, pos.getX());
                    maxY = Math.max(maxY, pos.getY());
                    maxZ = Math.max(maxZ, pos.getZ());
                }
            }
        }
        if (blocks.isEmpty()) {
            ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.translatable((String)"commands.sable.sub_level.assemble.no_blocks"));
            return 0;
        }
        BoundingBox3i bounds = new BoundingBox3i(minX, minY, minZ, maxX, maxY, maxZ);
        SubLevelAssemblyHelper.assembleBlocks(level, origin, blocks, (BoundingBox3ic)bounds);
        int finalBlocksCount = blocks.size();
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.assemble.radius.success", (Object[])new Object[]{finalBlocksCount}), true);
        return 1;
    }
}
