/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.stats.Stats
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.source.StatTrackingDisplaySource;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

public class DeathCounterDisplaySource
extends StatTrackingDisplaySource {
    @Override
    protected int updatedScoreOf(ServerPlayer player) {
        return player.getStats().getValue(Stats.CUSTOM.get((Object)Stats.DEATHS));
    }

    @Override
    protected String getTranslationKey() {
        return "player_deaths";
    }

    @Override
    protected String getObjectiveName() {
        return "deaths";
    }

    @Override
    protected Component getObjectiveDisplayName() {
        return CreateLang.translateDirect("display_source.scoreboard.objective.deaths", new Object[0]);
    }
}
