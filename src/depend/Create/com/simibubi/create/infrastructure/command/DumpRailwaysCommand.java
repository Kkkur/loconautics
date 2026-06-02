/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.station.GlobalStation;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DumpRailwaysCommand {
    private static final int white = ChatFormatting.WHITE.getColor();
    private static final int blue = 11192544;
    private static final int darkBlue = 8955319;
    private static final int darkerBlue = 7046804;
    private static final int darkestBlue = 5466997;
    private static final int bright = 0xFFEFEF;
    private static final int orange = 16756064;

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"trains").requires(cs -> cs.hasPermission(2))).executes(ctx -> {
            CommandSourceStack source = (CommandSourceStack)ctx.getSource();
            DumpRailwaysCommand.fillReport(source.getLevel(), source.getPosition(), (s, f) -> source.sendSuccess(() -> Component.literal((String)s).withStyle(st -> st.withColor(f.intValue())), false), c -> source.sendSuccess(() -> c, false));
            return 1;
        });
    }

    static void fillReport(ServerLevel level, Vec3 location, BiConsumer<String, Integer> chat, Consumer<Component> chatRaw) {
        GlobalRailwayManager railways = Create.RAILWAYS;
        chat.accept("", white);
        chat.accept("-+------<< Train Summary: >>------+-", white);
        int graphCount = railways.trackNetworks.size();
        chat.accept("Track Networks: " + graphCount, 11192544);
        chat.accept("Signal Groups: " + railways.signalEdgeGroups.size(), 11192544);
        int trainCount = railways.trains.size();
        chat.accept("Trains: " + trainCount, 11192544);
        chat.accept("", white);
        List<TrackGraph> nearest = railways.trackNetworks.values().stream().sorted((tg1, tg2) -> Float.compare(tg1.distanceToLocationSqr((Level)level, location), tg2.distanceToLocationSqr((Level)level, location))).limit(5L).toList();
        if (graphCount > 0) {
            chat.accept("Nearest Graphs: ", 16756064);
            chat.accept("", white);
            for (TrackGraph graph : nearest) {
                Collection<GlobalStation> stations;
                chat.accept(graph.id.toString().substring(0, 5) + " with " + graph.getNodes().size() + " Nodes", white);
                Collection<SignalBoundary> signals = graph.getPoints(EdgePointType.SIGNAL);
                if (!signals.isEmpty()) {
                    chat.accept(" -> " + signals.size() + " Signals", 11192544);
                }
                if ((stations = graph.getPoints(EdgePointType.STATION)).isEmpty()) continue;
                chat.accept(" -> " + stations.size() + " Stations", 11192544);
            }
            chat.accept("", white);
            if (graphCount > 5) {
                chat.accept("[...]", white);
                chat.accept("", white);
            }
        }
        List<Train> nearestTrains = railways.trains.values().stream().sorted((t1, t2) -> Float.compare(t1.distanceToLocationSqr((Level)level, location), t2.distanceToLocationSqr((Level)level, location))).limit(5L).toList();
        if (trainCount > 0 && !nearestTrains.isEmpty()) {
            chat.accept("Nearest Trains: ", 16756064);
            chat.accept("", white);
            for (Train train : nearestTrains) {
                GlobalStation currentStation;
                chat.accept(String.format("\u252c%1$s: %2$s, %3$d Wagons", train.id.toString().substring(0, 5), train.name.getString(), train.carriages.size()), 0xFFEFEF);
                if (train.derailed) {
                    chat.accept("\u251c\u2500Derailed", 16756064);
                } else if (train.graph != null) {
                    chat.accept("\u251c\u2500On Track: " + train.graph.id.toString().substring(0, 5), 11192544);
                }
                LivingEntity owner = train.getOwner((Level)level);
                if (owner != null) {
                    chat.accept("\u251c\u2500Owned by " + owner.getName().getString(), 11192544);
                }
                if ((currentStation = train.getCurrentStation()) != null) {
                    chat.accept("\u251c\u2500Waiting at: " + currentStation.name, 11192544);
                } else if (train.navigation.destination != null) {
                    chat.accept("\u251c\u2500Travelling to " + train.navigation.destination.name + " (" + Mth.floor((double)train.navigation.distanceToDestination) + "m away)", 8955319);
                }
                ScheduleRuntime runtime = train.runtime;
                if (runtime.getSchedule() != null) {
                    chat.accept("\u251c\u2500Schedule, Entry " + runtime.currentEntry + ", " + (runtime.paused ? "Paused" : runtime.state.name().replaceAll("_", " ")), runtime.paused ? 8955319 : 11192544);
                } else {
                    chat.accept("\u251c\u2500Idle, No Schedule", 8955319);
                }
                List<ResourceKey<Level>> presentDimensions = train.getPresentDimensions();
                if (presentDimensions.size() > 1) {
                    chat.accept("\u251c\u2500Travelling between Dimensions:", 7046804);
                }
                presentDimensions.forEach(key -> chat.accept("\u251c\u2500In %1$s near [%2$s]".formatted(key.location(), train.getPositionInDimension((ResourceKey<Level>)key).get().toShortString()), 7046804));
                chatRaw.accept(DumpRailwaysCommand.createTeleportButton(train));
                chatRaw.accept(DumpRailwaysCommand.createDeleteButton(train));
                chat.accept("", white);
            }
            if (trainCount > 5) {
                chat.accept("[...]", white);
                chat.accept("", white);
            }
        }
        chat.accept("-+--------------------------------+-", white);
    }

    private static Component createDeleteButton(Train train) {
        return Component.literal((String)"\u2514\u2500").withStyle(style -> style.withColor(11192544)).append((Component)ComponentUtils.wrapInSquareBrackets((Component)Component.literal((String)"Remove").withStyle(style -> style.withColor(16756064))).withStyle(style -> style.withColor(11192544).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c train remove " + train.id.toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Object)Component.literal((String)"Click to remove ").append(train.name)))));
    }

    private static Component createTeleportButton(Train train) {
        return Component.literal((String)"\u251c\u2500").withStyle(style -> style.withColor(8955319)).append((Component)ComponentUtils.wrapInSquareBrackets((Component)Component.literal((String)"Teleport").withStyle(style -> style.withColor(16756064))).withStyle(style -> style.withColor(8955319).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c train tp " + train.id.toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Object)Component.literal((String)"Click to teleport to ").append(train.name)))));
    }
}
