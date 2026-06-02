/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.trains.entity;

import com.google.common.collect.Streams;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TrainStatus {
    Train train;
    public boolean navigation;
    public boolean track;
    public boolean conductor;
    List<StatusMessage> queued = new ArrayList<StatusMessage>();

    public TrainStatus(Train train) {
        this.train = train;
    }

    public void failedNavigation() {
        if (this.navigation) {
            return;
        }
        this.displayInformation("no_path", false, new Object[0]);
        this.navigation = true;
    }

    public void failedNavigationNoTarget(String filter) {
        if (this.navigation) {
            return;
        }
        this.displayInformation("no_match", false, filter);
        this.navigation = true;
    }

    public void failedPackageNoTarget(String address) {
        if (this.navigation) {
            return;
        }
        this.displayInformation("no_package_target", false, address);
        this.navigation = true;
    }

    public void successfulNavigation() {
        if (!this.navigation) {
            return;
        }
        this.displayInformation("navigation_success", true, new Object[0]);
        this.navigation = false;
    }

    public void foundConductor() {
        if (!this.conductor) {
            return;
        }
        this.displayInformation("found_driver", true, new Object[0]);
        this.conductor = false;
    }

    public void missingConductor() {
        if (this.conductor) {
            return;
        }
        this.displayInformation("missing_driver", false, new Object[0]);
        this.conductor = true;
    }

    public void missingCorrectConductor() {
        if (this.conductor) {
            return;
        }
        this.displayInformation("opposite_driver", false, new Object[0]);
        this.conductor = true;
    }

    public void manualControls() {
        this.displayInformation("paused_for_manual", true, new Object[0]);
    }

    public void failedMigration() {
        if (this.track) {
            return;
        }
        this.displayInformation("track_missing", false, new Object[0]);
        this.track = true;
    }

    public void highStress() {
        if (this.track) {
            return;
        }
        this.displayInformation("coupling_stress", false, new Object[0]);
        this.track = true;
    }

    public void doublePortal() {
        if (this.track) {
            return;
        }
        this.displayInformation("double_portal", false, new Object[0]);
        this.track = true;
    }

    public void endOfTrack() {
        if (this.track) {
            return;
        }
        this.displayInformation("end_of_track", false, new Object[0]);
        this.track = true;
    }

    public void crash() {
        MutableComponent component = Component.literal((String)" - ").withStyle(ChatFormatting.GRAY).append((Component)CreateLang.translateDirect("train.status.collision", new Object[0]).withStyle(st -> st.withColor(16765876)));
        List<ResourceKey<Level>> presentDimensions = this.train.getPresentDimensions();
        Stream<Component> locationComponents = presentDimensions.stream().map(key -> Component.literal((String)" - ").withStyle(ChatFormatting.GRAY).append((Component)CreateLang.translateDirect("train.status.collision.where", key.location().toString(), this.train.getPositionInDimension((ResourceKey<Level>)key).get().toShortString()).withStyle(style -> style.withColor(16765876))));
        this.addMessage(new StatusMessage((Component[])Streams.concat((Stream[])new Stream[]{Stream.of(component), locationComponents}).toArray(Component[]::new)));
    }

    public void successfulMigration() {
        if (!this.track) {
            return;
        }
        this.displayInformation("back_on_track", true, new Object[0]);
        this.track = false;
    }

    public void trackOK() {
        this.track = false;
    }

    public void tick(Level level) {
        if (this.queued.isEmpty()) {
            return;
        }
        LivingEntity owner = this.train.getOwner(level);
        if (owner == null) {
            return;
        }
        if (owner instanceof Player) {
            Player player = (Player)owner;
            player.displayClientMessage((Component)CreateLang.translateDirect("train.status", this.train.name).withStyle(ChatFormatting.GOLD), false);
            this.queued.forEach(message -> message.displayToPlayer(player));
        }
        this.queued.clear();
    }

    public void displayInformation(String key, boolean itsAGoodThing, Object ... args) {
        MutableComponent component = Component.literal((String)" - ").withStyle(ChatFormatting.GRAY).append((Component)CreateLang.translateDirect("train.status." + key, args).withStyle(st -> st.withColor(itsAGoodThing ? 14019778 : 16765876)));
        this.addMessage(new StatusMessage(new Component[]{component}));
    }

    public void addMessage(StatusMessage message) {
        this.queued.add(message);
        if (this.queued.size() > 3) {
            this.queued.remove(0);
        }
    }

    public void newSchedule() {
        this.navigation = false;
        this.conductor = false;
    }

    public record StatusMessage(Component[] messages) {
        public void displayToPlayer(Player player) {
            Arrays.stream(this.messages).forEach(messages -> player.displayClientMessage(messages, false));
        }
    }
}
