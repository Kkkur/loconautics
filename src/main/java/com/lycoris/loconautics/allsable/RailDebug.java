package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;

import net.createmod.catnip.data.Pair;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Debug harness for the all-Sable rail math.
 *
 * <ul>
 *   <li><b>{@code /loconautics railtest [speed]}</b> (Phase 1) — look at a Create track and spawn a single
 *       {@link RailFollower} marker (END_ROD trail) that follows the rail at {@code speed} blocks/tick
 *       (default 0.15), bouncing at dead ends.</li>
 *   <li><b>{@code /loconautics railtest2 [spacing] [speed]}</b> (Phase 2a) — spawn a two-bogey
 *       {@link RailCarriage}: leading bogey (FLAME), trailing bogey (SOUL_FIRE_FLAME), carriage centre
 *       (END_ROD) and a forward arrow (CRIT) showing the derived orientation. Default spacing 3, speed 0.15.
 *       Parks at dead ends.</li>
 *   <li><b>{@code /loconautics railtest clear}</b> — remove all markers (followers + carriages).</li>
 * </ul>
 *
 * No sub-level / entity yet: this validates the rail math in isolation before Phase 2b drives a real body.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class RailDebug {

    private static final double DEFAULT_SPEED = 0.15;
    private static final double DEFAULT_SPACING = 3.0;
    private static final double REACH = 12.0;
    private static final int LOG_INTERVAL = 20;

    private static final List<ActiveFollower> FOLLOWERS = new ArrayList<>();
    private static final List<ActiveCarriage> CARRIAGES = new ArrayList<>();
    private static int tickCounter = 0;

    private RailDebug() {
    }

    private record ActiveFollower(ServerLevel level, RailFollower follower) {
    }

    private record ActiveCarriage(ServerLevel level, RailCarriage carriage) {
    }

    /** A track block the player is looking at, resolved to a placeable graph location. */
    private record Placement(ServerLevel level, TrackGraphLocation location, Vec3 upNormal) {
    }

    // ---------------------------------------------------------------------------------------------
    // Command
    // ---------------------------------------------------------------------------------------------

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("loconautics")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("railtest")
                        .executes(ctx -> startFollower(ctx.getSource().getPlayerOrException(), DEFAULT_SPEED))
                        .then(Commands.argument("speed", DoubleArgumentType.doubleArg(0.01, 5.0))
                                .executes(ctx -> startFollower(ctx.getSource().getPlayerOrException(),
                                        DoubleArgumentType.getDouble(ctx, "speed"))))
                        .then(Commands.literal("clear").executes(ctx -> {
                            int n = FOLLOWERS.size() + CARRIAGES.size();
                            FOLLOWERS.clear();
                            CARRIAGES.clear();
                            ctx.getSource().sendSuccess(() -> Component.literal("[rail] cleared " + n + " marker(s)"), false);
                            return n;
                        })))
                .then(Commands.literal("railtest2")
                        .executes(ctx -> startCarriage(ctx.getSource().getPlayerOrException(), DEFAULT_SPACING, DEFAULT_SPEED))
                        .then(Commands.argument("spacing", DoubleArgumentType.doubleArg(0.5, 32.0))
                                .executes(ctx -> startCarriage(ctx.getSource().getPlayerOrException(),
                                        DoubleArgumentType.getDouble(ctx, "spacing"), DEFAULT_SPEED))
                                .then(Commands.argument("speed", DoubleArgumentType.doubleArg(0.01, 5.0))
                                        .executes(ctx -> startCarriage(ctx.getSource().getPlayerOrException(),
                                                DoubleArgumentType.getDouble(ctx, "spacing"),
                                                DoubleArgumentType.getDouble(ctx, "speed"))))))
                .then(Commands.literal("sabletrain")
                        .executes(ctx -> SableTrainSpawner.spawn(ctx.getSource().getPlayerOrException(), DEFAULT_SPEED, false))
                        .then(Commands.argument("startspeed", DoubleArgumentType.doubleArg(-5.0, 5.0))
                                .executes(ctx -> SableTrainSpawner.spawn(ctx.getSource().getPlayerOrException(),
                                        DoubleArgumentType.getDouble(ctx, "startspeed"), false)))
                        .then(Commands.literal("physics")
                                .executes(ctx -> SableTrainSpawner.spawn(ctx.getSource().getPlayerOrException(), DEFAULT_SPEED, true))
                                .then(Commands.argument("pspeed", DoubleArgumentType.doubleArg(-5.0, 5.0))
                                        .executes(ctx -> SableTrainSpawner.spawn(ctx.getSource().getPlayerOrException(),
                                                DoubleArgumentType.getDouble(ctx, "pspeed"), true))))
                        .then(Commands.literal("speed")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(-5.0, 5.0))
                                        .executes(ctx -> {
                                            double v = DoubleArgumentType.getDouble(ctx, "value");
                                            int n = SableTrainSpawner.setSpeedAll(v);
                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                    "[sabletrain] set speed " + v + " on " + n + " train(s)"), false);
                                            return n;
                                        })))
                        .then(Commands.literal("addcar")
                                .executes(ctx -> SableTrainSpawner.addCar(ctx.getSource().getPlayerOrException())))
                        .then(Commands.literal("stop").executes(ctx -> {
                            int n = SableTrainSpawner.setSpeedAll(0.0);
                            ctx.getSource().sendSuccess(() -> Component.literal("[sabletrain] stopped " + n + " train(s)"), false);
                            return n;
                        }))
                        .then(Commands.literal("clear").executes(ctx -> {
                            int n = SableTrainSpawner.clear();
                            ctx.getSource().sendSuccess(() -> Component.literal("[sabletrain] cleared " + n + " train(s)"), false);
                            return n;
                        }))));
    }

    private static int startFollower(ServerPlayer player, double speed) throws CommandSyntaxException {
        Placement placement = locate(player);
        if (placement == null) {
            return 0;
        }
        RailFollower follower = RailFollower.at(placement.location(), placement.upNormal(), speed);
        if (follower == null) {
            player.sendSystemMessage(Component.literal("[rail] couldn't resolve the edge"));
            return 0;
        }
        FOLLOWERS.add(new ActiveFollower(placement.level(), follower));
        Vec3 start = follower.position();
        player.sendSystemMessage(Component.literal(String.format(
                "[rail] follower #%d at (%.1f, %.1f, %.1f) speed=%.2f", FOLLOWERS.size(), start.x, start.y, start.z, speed)));
        LoconauticsConstants.LOGGER.info("[rail] follower started at {} speed={}", start, speed);
        return 1;
    }

    private static int startCarriage(ServerPlayer player, double spacing, double speed) throws CommandSyntaxException {
        Placement placement = locate(player);
        if (placement == null) {
            return 0;
        }
        RailCarriage carriage = RailCarriage.at(placement.location(), placement.upNormal(), spacing, speed);
        if (carriage == null) {
            player.sendSystemMessage(Component.literal("[rail] couldn't resolve the edge"));
            return 0;
        }
        CARRIAGES.add(new ActiveCarriage(placement.level(), carriage));
        Vec3 c = carriage.tick(); // first tick to seat/report; spacing<=track ensures bogeys are placed
        double chord = carriage.leadingPos().distanceTo(carriage.trailingPos());
        player.sendSystemMessage(Component.literal(String.format(
                "[rail] carriage #%d centre=(%.1f, %.1f, %.1f) spacing=%.1f chord=%.2f speed=%.2f",
                CARRIAGES.size(), c.x, c.y, c.z, spacing, chord, speed)));
        LoconauticsConstants.LOGGER.info("[rail] carriage started centre={} spacing={} chord={} speed={}",
                c, spacing, fmt(chord), speed);
        return 1;
    }

    /** Raytrace the player's view onto a Create track and resolve a graph location, or {@code null} + msg. */
    private static Placement locate(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(REACH));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.MISS) {
            player.sendSystemMessage(Component.literal("[rail] look at a Create track (within " + (int) REACH + " blocks)"));
            return null;
        }
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ITrackBlock track)) {
            player.sendSystemMessage(Component.literal("[rail] that block isn't a track: " + state.getBlock()));
            return null;
        }
        Pair<Vec3, Direction.AxisDirection> axis = track.getNearestTrackAxis(level, pos, state, look);
        TrackGraphLocation location = TrackGraphHelper.getGraphLocationAt(level, pos, axis.getSecond(), axis.getFirst());
        if (location == null || location.graph == null) {
            player.sendSystemMessage(Component.literal("[rail] track at " + pos.toShortString() + " isn't in a graph yet"));
            return null;
        }
        return new Placement(level, location, track.getUpNormal(level, pos, state));
    }

    // ---------------------------------------------------------------------------------------------
    // Tick: advance each marker and draw it
    // ---------------------------------------------------------------------------------------------

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (FOLLOWERS.isEmpty() && CARRIAGES.isEmpty()) {
            return;
        }
        boolean log = (tickCounter++ % LOG_INTERVAL) == 0;

        Iterator<ActiveFollower> fit = FOLLOWERS.iterator();
        while (fit.hasNext()) {
            ActiveFollower a = fit.next();
            try {
                Vec3 p = a.follower().tick();
                dot(a.level(), ParticleTypes.END_ROD, p, 0.3);
                if (log) {
                    LoconauticsConstants.LOGGER.info("[rail] follower pos=({}, {}, {})", fmt(p.x), fmt(p.y), fmt(p.z));
                }
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("[rail] follower errored, removing", t);
                fit.remove();
            }
        }

        Iterator<ActiveCarriage> cit = CARRIAGES.iterator();
        while (cit.hasNext()) {
            ActiveCarriage a = cit.next();
            try {
                RailCarriage carriage = a.carriage();
                Vec3 center = carriage.tick();
                Vec3 lead = carriage.leadingPos();
                Vec3 trail = carriage.trailingPos();
                Vec3 fwd = carriage.forward();
                dot(a.level(), ParticleTypes.FLAME, lead, 0.3);
                dot(a.level(), ParticleTypes.SOUL_FIRE_FLAME, trail, 0.3);
                dot(a.level(), ParticleTypes.END_ROD, center, 0.3);
                // Forward arrow from the centre, so the derived orientation is visible.
                for (double d = 0.4; d <= 1.8; d += 0.4) {
                    dot(a.level(), ParticleTypes.CRIT, center.add(fwd.scale(d)), 0.3);
                }
                if (log) {
                    double chord = lead.distanceTo(trail);
                    LoconauticsConstants.LOGGER.info(
                            "[rail] carriage centre=({}, {}, {}) fwd=({}, {}, {}) chord={}{}",
                            fmt(center.x), fmt(center.y), fmt(center.z),
                            fmt(fwd.x), fmt(fwd.y), fmt(fwd.z), fmt(chord),
                            carriage.stopped() ? " STOPPED" : "");
                }
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("[rail] carriage errored, removing", t);
                cit.remove();
            }
        }
    }

    private static void dot(ServerLevel level, SimpleParticleType type, Vec3 p, double yOffset) {
        level.sendParticles(type, p.x, p.y + yOffset, p.z, 2, 0.05, 0.05, 0.05, 0.0);
    }

    private static String fmt(double d) {
        return String.format("%.2f", d);
    }
}
