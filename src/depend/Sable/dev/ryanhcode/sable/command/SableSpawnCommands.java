/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.commands.arguments.blocks.BlockStateArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintHandle;
import dev.ryanhcode.sable.api.physics.object.rope.RopeHandle;
import dev.ryanhcode.sable.api.physics.object.rope.RopePhysicsObject;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.EmbeddedPlotLevelAccessor;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.util.SchematicLoader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SableSpawnCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TEMPLATES = (commandContext, suggestionsBuilder) -> {
        MinecraftServer server = ((CommandSourceStack)commandContext.getSource()).getServer();
        return SchematicLoader.getSchematics(server).thenCompose(schematics -> {
            String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
            SharedSuggestionProvider.filterResources((Iterable)schematics, (String)remaining, resourceLocation -> resourceLocation, resourceLocation -> {
                String path = resourceLocation.getPath();
                suggestionsBuilder.suggest(path.substring("schematics/".length(), path.length() - ".nbt".length()));
            });
            return suggestionsBuilder.buildFuture();
        });
    };
    private static final BlockState DEFAULT_SPAWN_BLOCKSTATE = Blocks.STONE.defaultBlockState();

    public static void register(LiteralArgumentBuilder<CommandSourceStack> sableBuilder, CommandBuildContext buildContext) {
        sableBuilder.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"spawn").then(Commands.literal((String)"jenga").then((ArgumentBuilder)SableSpawnCommands.namedSpawnFinale(Commands.argument((String)"height", (ArgumentType)IntegerArgumentType.integer((int)1, (int)256)), SableSpawnCommands::spawnJenga)))).then(Commands.literal((String)"clone").then((ArgumentBuilder)SableSpawnCommands.namedSpawnFinale(Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.singleSubLevel()), SableSpawnCommands::cloneSubLevel)))).then(Commands.literal((String)"sphere").then(((RequiredArgumentBuilder)Commands.argument((String)"radius", (ArgumentType)IntegerArgumentType.integer((int)2, (int)200)).executes(ctx -> SableSpawnCommands.spawnSphere((CommandContext<CommandSourceStack>)ctx, DEFAULT_SPAWN_BLOCKSTATE, null))).then((ArgumentBuilder)SableSpawnCommands.namedSpawnFinale(Commands.argument((String)"block", (ArgumentType)BlockStateArgument.block((CommandBuildContext)buildContext)), (ctx, name) -> SableSpawnCommands.spawnSphere((CommandContext<CommandSourceStack>)ctx, BlockStateArgument.getBlock((CommandContext)ctx, (String)"block").getState(), name)))))).then(Commands.literal((String)"schematic").then(Commands.argument((String)"name", (ArgumentType)StringArgumentType.string()).suggests(SUGGEST_TEMPLATES).executes(SableSpawnCommands::executeSpawnSchematicCommand)))).then(Commands.literal((String)"joint_test").executes(SableSpawnCommands::executeSpawnJointTestCommand))).then((ArgumentBuilder)SableSpawnCommands.namedSpawnFinale(Commands.literal((String)"slope_test"), SableSpawnCommands::spawnSlopeTest))).then(Commands.literal((String)"rope_test").executes(SableSpawnCommands::executeSpawnRopeTestCommand))).then(Commands.literal((String)"grid").then(((RequiredArgumentBuilder)Commands.argument((String)"sideLength", (ArgumentType)IntegerArgumentType.integer((int)1, (int)32)).executes(ctx -> SableSpawnCommands.spawnGrid((CommandContext<CommandSourceStack>)ctx, DEFAULT_SPAWN_BLOCKSTATE, null))).then((ArgumentBuilder)SableSpawnCommands.namedSpawnFinale(Commands.argument((String)"block", (ArgumentType)BlockStateArgument.block((CommandBuildContext)buildContext)), (ctx, name) -> SableSpawnCommands.spawnGrid((CommandContext<CommandSourceStack>)ctx, BlockStateArgument.getBlock((CommandContext)ctx, (String)"block").getState(), name)))))).then(((LiteralArgumentBuilder)Commands.literal((String)"block").executes(ctx -> SableSpawnCommands.spawnBlock((CommandContext<CommandSourceStack>)ctx, DEFAULT_SPAWN_BLOCKSTATE, null))).then((ArgumentBuilder)SableSpawnCommands.namedSpawnFinale(Commands.argument((String)"block", (ArgumentType)BlockStateArgument.block((CommandBuildContext)buildContext)), (ctx, name) -> SableSpawnCommands.spawnBlock((CommandContext<CommandSourceStack>)ctx, BlockStateArgument.getBlock((CommandContext)ctx, (String)"block").getState(), name))))).then(Commands.literal((String)"platform").then(((RequiredArgumentBuilder)Commands.argument((String)"size", (ArgumentType)IntegerArgumentType.integer((int)1, (int)32)).executes(ctx -> SableSpawnCommands.spawnPlatform((CommandContext<CommandSourceStack>)ctx, DEFAULT_SPAWN_BLOCKSTATE, null))).then((ArgumentBuilder)SableSpawnCommands.namedSpawnFinale(Commands.argument((String)"block", (ArgumentType)BlockStateArgument.block((CommandBuildContext)buildContext)), (ctx, name) -> SableSpawnCommands.spawnPlatform((CommandContext<CommandSourceStack>)ctx, BlockStateArgument.getBlock((CommandContext)ctx, (String)"block").getState(), name))))));
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T namedSpawnFinale(T builder, NamedSpawnInvoker<CommandSourceStack> invoker) {
        builder.executes(ctx -> invoker.run(ctx, null));
        builder.then(Commands.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(ctx -> invoker.run(ctx, StringArgumentType.getString((CommandContext)ctx, (String)"name"))));
        return builder;
    }

    private static int spawnJenga(CommandContext<CommandSourceStack> ctx, @Nullable String name) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer container = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 pos = Vec3.atCenterOf((Vec3i)BlockPos.containing((Position)source.getPosition()));
        int height = IntegerArgumentType.getInteger(ctx, (String)"height");
        for (int yOffset = 0; yOffset < height; ++yOffset) {
            Direction.Axis axis = yOffset % 2 == 0 ? Direction.Axis.X : Direction.Axis.Z;
            Direction.Axis perpendicular = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            for (int index = -1; index <= 1; ++index) {
                Pose3d pose = new Pose3d();
                Vector3d position = pose.position();
                position.set(pos.x, pos.y, pos.z);
                if (index != 0) {
                    position.add((Vector3dc)JOMLConversion.atLowerCornerOf((Vec3i)Direction.get((Direction.AxisDirection)(index == 1 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE), (Direction.Axis)axis).getNormal()));
                }
                position.add(0.0, (double)yOffset, 0.0);
                Vector3d positionBackup = new Vector3d((Vector3dc)position);
                SubLevel subLevel = container.allocateNewSubLevel(pose);
                subLevel.setName(name);
                LevelPlot plot = subLevel.getPlot();
                ChunkPos center = plot.getCenterChunk();
                plot.newEmptyChunk(center);
                EmbeddedPlotLevelAccessor accessor = plot.getEmbeddedLevelAccessor();
                accessor.setBlock(BlockPos.ZERO, Blocks.SPRUCE_PLANKS.defaultBlockState(), 3);
                for (int block = -1; block <= 1; ++block) {
                    BlockPos blockPos = BlockPos.ZERO.relative(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)perpendicular), block);
                    BlockState state = Blocks.OAK_PLANKS.defaultBlockState();
                    if (index == 0) {
                        state = Blocks.SPRUCE_PLANKS.defaultBlockState();
                    }
                    accessor.setBlock(blockPos, state, 3);
                }
                subLevel.logicalPose().position().set((Vector3dc)positionBackup);
                subLevel.updateLastPose();
            }
        }
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"jenga"}), false);
        return 1;
    }

    private static int cloneSubLevel(CommandContext<CommandSourceStack> ctx, @Nullable String name) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        ServerSubLevel toClone = SubLevelArgumentType.getSingleSubLevel(ctx, "sub_level");
        BoundingBox3dc worldBounds = toClone.boundingBox();
        double height = worldBounds.maxY() - worldBounds.minY();
        CompoundTag tag = toClone.getPlot().save();
        ServerSubLevel subLevel = (ServerSubLevel)plotContainer.allocateNewSubLevel(new Pose3d(toClone.logicalPose().position().add(0.0, height * 1.2 + 2.0, 0.0, new Vector3d()), new Quaterniond(), new Vector3d(0.0), new Vector3d(1.0)));
        ServerLevelPlot plot = subLevel.getPlot();
        plot.load(tag);
        subLevel.updateLastPose();
        if (name != null) {
            subLevel.setName(name);
        }
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.clone.success"), false);
        return 1;
    }

    private static int spawnSphere(CommandContext<CommandSourceStack> ctx, BlockState material, @Nullable String name) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 playerPos = source.getPosition();
        playerPos = Vec3.atCenterOf((Vec3i)BlockPos.containing((Position)playerPos));
        Pose3d pose = new Pose3d();
        pose.position().set(playerPos.x, playerPos.y, playerPos.z);
        SubLevel subLevel = plotContainer.allocateNewSubLevel(pose);
        subLevel.setName(name);
        LevelPlot plot = subLevel.getPlot();
        ChunkPos center = plot.getCenterChunk();
        int radius = IntegerArgumentType.getInteger(ctx, (String)"radius");
        int radiusChunks = (radius + 8) / 16;
        for (int x = -radiusChunks; x <= radiusChunks; ++x) {
            for (int z = -radiusChunks; z <= radiusChunks; ++z) {
                plot.newEmptyChunk(new ChunkPos(center.x + x, center.z + z));
            }
        }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                for (int y = -radius; y <= radius; ++y) {
                    pos.set(x, y, z);
                    if (!(pos.distSqr((Vec3i)BlockPos.ZERO) <= (double)(radius * radius))) continue;
                    plot.getEmbeddedLevelAccessor().setBlock((BlockPos)pos, material, 3);
                }
            }
        }
        subLevel.updateLastPose();
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"sphere"}), false);
        return 1;
    }

    private static int executeSpawnSchematicCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerLevel level = source.getLevel();
        StructureTemplate template = SchematicLoader.loadSchematic(level, ResourceLocation.fromNamespaceAndPath((String)"sable", (String)StringArgumentType.getString(ctx, (String)"name")));
        if (template == null) {
            source.sendFailure((Component)Component.translatable((String)"commands.sable.place_schematic.failure"));
            return 0;
        }
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 spawnPos = source.getPosition();
        Pose3d pose = new Pose3d();
        pose.position().set(spawnPos.x, spawnPos.y, spawnPos.z);
        SubLevel sublevel = plotContainer.allocateNewSubLevel(pose);
        LevelPlot plot = sublevel.getPlot();
        ChunkPos center = plot.getCenterChunk();
        BoundingBox bounds = template.getBoundingBox(BlockPos.ZERO, Rotation.NONE, BlockPos.ZERO, Mirror.NONE);
        int minChunkX = bounds.minX() >> 4;
        int minChunkZ = bounds.minZ() >> 4;
        int maxChunkX = bounds.maxX() >> 4;
        int maxChunkZ = bounds.maxZ() >> 4;
        for (int x = minChunkX; x <= maxChunkX; ++x) {
            for (int z = minChunkZ; z <= maxChunkZ; ++z) {
                plot.newEmptyChunk(new ChunkPos(center.x + x, center.z + z));
            }
        }
        EmbeddedPlotLevelAccessor embedded = plot.getEmbeddedLevelAccessor();
        template.placeInWorld((ServerLevelAccessor)embedded, BlockPos.ZERO, BlockPos.ZERO, new StructurePlaceSettings(), RandomSource.create(), 3);
        sublevel.updateLastPose();
        sublevel.logicalPose().position().set(spawnPos.x, spawnPos.y, spawnPos.z);
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.place_schematic.success"), false);
        return 1;
    }

    private static int executeSpawnRopeTestCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        SubLevelPhysicsSystem system = SableCommandHelper.requireSubLevelPhysicsSystem(plotContainer);
        Vec3 playerPos = Vec3.atCenterOf((Vec3i)BlockPos.containing((Position)source.getPosition()));
        ObjectArrayList points = new ObjectArrayList();
        for (int i = 0; i < 10; ++i) {
            points.add(JOMLConversion.toJOML((Position)playerPos).add((double)i, 0.0, 0.0));
        }
        RopePhysicsObject object = new RopePhysicsObject((Collection<Vector3d>)points, 0.25);
        system.addObject(object);
        object.setAttachment(RopeHandle.AttachmentPoint.START, (Vector3dc)JOMLConversion.toJOML((Position)playerPos), null);
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"rope_test"}), false);
        return 1;
    }

    private static int executeSpawnJointTestCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 playerPos = Vec3.atCenterOf((Vec3i)BlockPos.containing((Position)source.getPosition()));
        Pose3d pose1 = new Pose3d();
        pose1.position().set(playerPos.x, playerPos.y, playerPos.z);
        Pose3d pose2 = new Pose3d();
        pose2.position().set(playerPos.x, playerPos.y + 1.0, playerPos.z);
        ServerSubLevel subLevelA = (ServerSubLevel)plotContainer.allocateNewSubLevel(pose1);
        ServerSubLevel subLevelB = (ServerSubLevel)plotContainer.allocateNewSubLevel(pose2);
        ServerLevelPlot plotA = subLevelA.getPlot();
        ServerLevelPlot plotB = subLevelB.getPlot();
        plotA.newEmptyChunk(plotA.getCenterChunk());
        plotA.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, Blocks.STONE.defaultBlockState(), 3);
        plotB.newEmptyChunk(plotB.getCenterChunk());
        plotB.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, Blocks.STONE.defaultBlockState(), 3);
        RotaryConstraintConfiguration config = new RotaryConstraintConfiguration((Vector3dc)JOMLConversion.atBottomCenterOf((Vec3i)plotA.getCenterBlock().above().above()), (Vector3dc)JOMLConversion.atBottomCenterOf((Vec3i)plotB.getCenterBlock()), (Vector3dc)JOMLConversion.atLowerCornerOf((Vec3i)Direction.UP.getNormal()), (Vector3dc)JOMLConversion.atLowerCornerOf((Vec3i)Direction.UP.getNormal()));
        RotaryConstraintHandle handle = SableCommandHelper.requireSubLevelPhysicsSystem(plotContainer).getPipeline().addConstraint(subLevelA, subLevelB, config);
        handle.setContactsEnabled(false);
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"joint_test"}), false);
        return 1;
    }

    private static int spawnSlopeTest(CommandContext<CommandSourceStack> ctx, @Nullable String name) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 playerPos = Vec3.atCenterOf((Vec3i)BlockPos.containing((Position)source.getPosition()));
        int gridSize = 9;
        double yawRange = Math.toRadians(90.0);
        double pitchRange = Math.toRadians(90.0);
        int rad = 3;
        int spacing = 8;
        for (int xo = 0; xo <= 9; ++xo) {
            for (int zo = 0; zo <= 9; ++zo) {
                Pose3d pose1 = new Pose3d();
                pose1.position().set(playerPos.x, playerPos.y, playerPos.z);
                ServerSubLevel subLevel = (ServerSubLevel)plotContainer.allocateNewSubLevel(pose1);
                subLevel.setName(name);
                ServerLevelPlot plotA = subLevel.getPlot();
                BlockState block = Blocks.END_STONE.defaultBlockState();
                plotA.newEmptyChunk(plotA.getCenterChunk());
                for (int lx = -3; lx < 3; ++lx) {
                    for (int lz = -3; lz < 3; ++lz) {
                        plotA.getEmbeddedLevelAccessor().setBlock(new BlockPos(lx, 0, lz), block, 3);
                    }
                }
                Vector3d pos = new Vector3d(playerPos.x + (double)(xo * 8), playerPos.y, playerPos.z + (double)(zo * 8));
                Quaterniond orientation = new Quaterniond();
                orientation.rotateY((double)xo * yawRange / 9.0);
                orientation.rotateX((double)zo * pitchRange / 9.0);
                SableCommandHelper.requireSubLevelPhysicsPipeline(ctx).teleport(subLevel, (Vector3dc)pos, (Quaterniondc)orientation);
            }
        }
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"slope_test"}), false);
        return 1;
    }

    private static int spawnGrid(CommandContext<CommandSourceStack> ctx, BlockState material, @Nullable String name) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 playerPos = source.getPosition();
        int sideLength = IntegerArgumentType.getInteger(ctx, (String)"sideLength");
        Vec3[] positions = new Vec3[sideLength * sideLength * sideLength];
        for (int x = 0; x < sideLength; ++x) {
            for (int z = 0; z < sideLength; ++z) {
                for (int y = 0; y < sideLength; ++y) {
                    positions[x * sideLength * sideLength + z * sideLength + y] = new Vec3((double)x, (double)y, (double)z).scale(2.1).add(playerPos);
                }
            }
        }
        for (Vec3 subLevelPos : positions) {
            Pose3d pose = new Pose3d();
            pose.position().set(subLevelPos.x, subLevelPos.y, subLevelPos.z);
            SubLevel subLevel = plotContainer.allocateNewSubLevel(pose);
            subLevel.setName(name);
            LevelPlot plot = subLevel.getPlot();
            ChunkPos center = plot.getCenterChunk();
            plot.newEmptyChunk(center);
            plot.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, material, 3);
            subLevel.updateLastPose();
        }
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"grid"}), false);
        return 1;
    }

    private static int spawnBlock(CommandContext<CommandSourceStack> ctx, BlockState material, @Nullable String name) throws CommandSyntaxException {
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 playerPos = source.getPosition();
        Pose3d pose = new Pose3d();
        pose.position().set(playerPos.x, playerPos.y, playerPos.z);
        SubLevel subLevel = plotContainer.allocateNewSubLevel(pose);
        subLevel.setName(name);
        LevelPlot plot = subLevel.getPlot();
        ChunkPos center = plot.getCenterChunk();
        plot.newEmptyChunk(center);
        plot.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, material, 3);
        subLevel.updateLastPose();
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"block"}), false);
        return 1;
    }

    private static int spawnPlatform(CommandContext<CommandSourceStack> ctx, BlockState material, @Nullable String name) throws CommandSyntaxException {
        int z;
        int x;
        CommandSourceStack source = (CommandSourceStack)ctx.getSource();
        ServerSubLevelContainer plotContainer = SableCommandHelper.requireSubLevelContainer(ctx);
        Vec3 playerPos = source.getPosition();
        Pose3d pose = new Pose3d();
        pose.position().set(playerPos.x, playerPos.y, playerPos.z);
        SubLevel subLevel = plotContainer.allocateNewSubLevel(pose);
        subLevel.setName(name);
        LevelPlot plot = subLevel.getPlot();
        ChunkPos center = plot.getCenterChunk();
        int size = IntegerArgumentType.getInteger(ctx, (String)"size");
        int radiusChunks = (size + 8) / 16;
        for (x = -radiusChunks; x <= radiusChunks; ++x) {
            for (z = -radiusChunks; z <= radiusChunks; ++z) {
                plot.newEmptyChunk(new ChunkPos(center.x + x, center.z + z));
            }
        }
        for (x = -size; x <= size; ++x) {
            for (z = -size; z <= size; ++z) {
                plot.getEmbeddedLevelAccessor().setBlock(new BlockPos(x, 0, z), material, 2);
            }
        }
        subLevel.updateLastPose();
        SableCommandHelper.requireSubLevelPhysicsPipeline(ctx).teleport((ServerSubLevel)subLevel, (Vector3dc)new Vector3d(playerPos.x, playerPos.y, playerPos.z), (Quaterniondc)pose.orientation());
        source.sendSuccess(() -> Component.translatable((String)"commands.sable.spawn.success", (Object[])new Object[]{"platform"}), false);
        return 1;
    }

    @FunctionalInterface
    private static interface NamedSpawnInvoker<S> {
        public int run(CommandContext<S> var1, @Nullable String var2) throws CommandSyntaxException;
    }
}
