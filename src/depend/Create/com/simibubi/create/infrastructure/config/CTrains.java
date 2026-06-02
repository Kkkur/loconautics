/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 *  net.createmod.catnip.config.ConfigBase$ConfigGroup
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package com.simibubi.create.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class CTrains
extends ConfigBase {
    public final ConfigBase.ConfigBool trainsCauseDamage = this.b(true, "trainsCauseDamage", new String[]{Comments.trainsCauseDamage});
    public final ConfigBase.ConfigInt maxTrackPlacementLength = this.i(32, 16, 128, "maxTrackPlacementLength", new String[]{Comments.maxTrackPlacementLength});
    public final ConfigBase.ConfigInt maxAssemblyLength = this.i(128, 5, 512, "maxAssemblyLength", new String[]{Comments.maxAssemblyLength});
    public final ConfigBase.ConfigInt maxBogeyCount = this.i(20, 1, 200, "maxBogeyCount", new String[]{Comments.maxBogeyCount});
    public final ConfigBase.ConfigFloat manualTrainSpeedModifier = this.f(0.75f, 0.0f, "manualTrainSpeedModifier", new String[]{Comments.manualTrainSpeedModifier});
    public final ConfigBase.ConfigGroup trainStats = this.group(1, "trainStats", new String[]{"Standard Trains"});
    public final ConfigBase.ConfigFloat trainTopSpeed = this.f(28.0f, 0.0f, "trainTopSpeed", new String[]{Comments.mps, Comments.trainTopSpeed});
    public final ConfigBase.ConfigFloat trainTurningTopSpeed = this.f(14.0f, 0.0f, "trainTurningTopSpeed", new String[]{Comments.mps, Comments.trainTurningTopSpeed});
    public final ConfigBase.ConfigFloat trainAcceleration = this.f(3.0f, 0.0f, "trainAcceleration", new String[]{Comments.acc, Comments.trainAcceleration});
    public final ConfigBase.ConfigGroup poweredTrainStats = this.group(1, "poweredTrainStats", new String[]{"Powered Trains"});
    public final ConfigBase.ConfigFloat poweredTrainTopSpeed = this.f(40.0f, 0.0f, "poweredTrainTopSpeed", new String[]{Comments.mps, Comments.poweredTrainTopSpeed});
    public final ConfigBase.ConfigFloat poweredTrainTurningTopSpeed = this.f(20.0f, 0.0f, "poweredTrainTurningTopSpeed", new String[]{Comments.mps, Comments.poweredTrainTurningTopSpeed});
    public final ConfigBase.ConfigFloat poweredTrainAcceleration = this.f(3.0f, 0.0f, "poweredTrainAcceleration", new String[]{Comments.acc, Comments.poweredTrainAcceleration});

    public String getName() {
        return "trains";
    }

    private static class Comments {
        static String mps = "[in Blocks/Second]";
        static String acc = "[in Blocks/Second\u00b2]";
        static String trainTopSpeed = "The top speed of any assembled Train.";
        static String trainTurningTopSpeed = "The top speed of Trains during a turn.";
        static String trainAcceleration = "The acceleration of any assembled Train.";
        static String poweredTrainTopSpeed = "The top speed of powered Trains.";
        static String poweredTrainTurningTopSpeed = "The top speed of powered Trains during a turn.";
        static String poweredTrainAcceleration = "The acceleration of powered Trains.";
        static String trainsCauseDamage = "Whether moving Trains can hurt colliding mobs and players.";
        static String maxTrackPlacementLength = "Maximum length of track that can be placed as one batch or turn.";
        static String maxAssemblyLength = "Maximum length of a Train Stations' assembly track.";
        static String maxBogeyCount = "Maximum amount of bogeys assembled as a single Train.";
        static String manualTrainSpeedModifier = "Relative speed of a manually controlled Train compared to a Scheduled one.";

        private Comments() {
        }
    }
}
