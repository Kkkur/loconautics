/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.GameRules$BooleanValue
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.worldgen;

import dev.simulated_team.simulated.content.worldgen.SimulatedWorldPreset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

public class AirshipReadyPreset
extends SimulatedWorldPreset {
    public AirshipReadyPreset(ResourceLocation id, @Nullable Component description) {
        super(id, description);
    }

    @Override
    public void modifyGameRules(GameRules gameRules) {
        ((GameRules.BooleanValue)gameRules.getRule(GameRules.RULE_DOMOBSPAWNING)).set(false, null);
        ((GameRules.BooleanValue)gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING)).set(false, null);
        ((GameRules.BooleanValue)gameRules.getRule(GameRules.RULE_WEATHER_CYCLE)).set(false, null);
        ((GameRules.BooleanValue)gameRules.getRule(GameRules.RULE_DAYLIGHT)).set(false, null);
    }
}
