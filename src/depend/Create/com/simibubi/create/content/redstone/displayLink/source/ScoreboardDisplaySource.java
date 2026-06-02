/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.scores.Objective
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.stream.Stream;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;

public class ScoreboardDisplaySource
extends ValueListDisplaySource {
    @Override
    protected Stream<IntAttached<MutableComponent>> provideEntries(DisplayLinkContext context, int maxRows) {
        Level level = context.blockEntity().getLevel();
        if (!(level instanceof ServerLevel)) {
            return Stream.empty();
        }
        ServerLevel sLevel = (ServerLevel)level;
        String name = context.sourceConfig().getString("Objective");
        return this.showScoreboard(sLevel, name, maxRows);
    }

    protected Stream<IntAttached<MutableComponent>> showScoreboard(ServerLevel sLevel, String objectiveName, int maxRows) {
        Objective objective = sLevel.getScoreboard().getObjective(objectiveName);
        if (objective == null) {
            return this.notFound(objectiveName).stream();
        }
        return sLevel.getScoreboard().listPlayerScores(objective).stream().map(score -> IntAttached.with((int)score.value(), (Object)Component.literal((String)score.owner()).copy())).sorted(IntAttached.comparator()).limit(maxRows);
    }

    private ImmutableList<IntAttached<MutableComponent>> notFound(String objective) {
        return ImmutableList.of((Object)IntAttached.with((int)404, (Object)CreateLang.translateDirect("display_source.scoreboard.objective_not_found", objective)));
    }

    @Override
    protected String getTranslationKey() {
        return "scoreboard";
    }

    @Override
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        if (isFirstLine) {
            builder.addTextInput(0, 137, (e, t) -> {
                e.setValue("");
                t.withTooltip((List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("display_source.scoreboard.objective", new Object[0]).withStyle(s -> s.withColor(5476833)), (Object)CreateLang.translateDirect("gui.schedule.lmb_edit", new Object[0]).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})));
            }, "Objective");
        } else {
            this.addFullNumberConfig(builder);
        }
    }

    @Override
    protected boolean valueFirst() {
        return false;
    }
}
