/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  foundry.veil.api.client.color.Color
 *  foundry.veil.api.client.color.Colorc
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentSerialization
 */
package dev.simulated_team.simulated.client.sections;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.Colorc;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record SimulatedSection.Title(Component text, Colorc color, Optional<Colorc> secondaryColor, Colorc background) {
    public static final Codec<Colorc> COLOR_CODEC = Color.ARGB_INT_CODEC.xmap(i -> new Color(i.intValue(), true), Colorc::argb);
    public static final Codec<SimulatedSection.Title> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ComponentSerialization.CODEC.fieldOf("text").forGetter(SimulatedSection.Title::text), (App)COLOR_CODEC.fieldOf("color").orElse((Object)new Color(-1, true)).forGetter(SimulatedSection.Title::color), (App)COLOR_CODEC.optionalFieldOf("secondary_color").forGetter(SimulatedSection.Title::secondaryColor), (App)COLOR_CODEC.fieldOf("background").orElse((Object)new Color(-1442840576, true)).forGetter(SimulatedSection.Title::background)).apply((Applicative)instance, SimulatedSection.Title::new));
}
