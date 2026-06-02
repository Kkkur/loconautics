/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.scores.Objective
 *  net.minecraft.world.scores.ScoreHolder
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.scores.criteria.ObjectiveCriteria
 *  net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.ScoreboardDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import java.util.stream.Stream;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class StatTrackingDisplaySource
extends ScoreboardDisplaySource {
    @Override
    protected Stream<IntAttached<MutableComponent>> provideEntries(DisplayLinkContext context, int maxRows) {
        Level level = context.blockEntity().getLevel();
        if (!(level instanceof ServerLevel)) {
            return Stream.empty();
        }
        ServerLevel sLevel = (ServerLevel)level;
        String name = "create_auto_" + this.getObjectiveName();
        Scoreboard scoreboard = level.getScoreboard();
        if (scoreboard.getObjective(name) == null) {
            scoreboard.addObjective(name, ObjectiveCriteria.DUMMY, this.getObjectiveDisplayName(), ObjectiveCriteria.RenderType.INTEGER, false, null);
        }
        Objective objective = scoreboard.getObjective(name);
        sLevel.getServer().getPlayerList().getPlayers().forEach(s -> scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly((String)s.getScoreboardName()), objective).set(this.updatedScoreOf((ServerPlayer)s)));
        return this.showScoreboard(sLevel, name, maxRows);
    }

    protected abstract String getObjectiveName();

    protected abstract Component getObjectiveDisplayName();

    protected abstract int updatedScoreOf(ServerPlayer var1);

    @Override
    protected boolean valueFirst() {
        return false;
    }

    @Override
    protected boolean shortenNumbers(DisplayLinkContext context) {
        return false;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
    }
}
