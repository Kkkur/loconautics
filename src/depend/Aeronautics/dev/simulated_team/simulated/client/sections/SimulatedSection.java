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
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.ExtraCodecs
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.client.sections;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.simulated_team.simulated.Simulated;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.Colorc;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

public record SimulatedSection(int priority, Title title, ResourceLocation sprite, boolean animateOnHover) implements Comparable<SimulatedSection>
{
    private static final ResourceLocation DEFAULT_BANNER = Simulated.path("default_banner");
    public static final Codec<SimulatedSection> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ExtraCodecs.POSITIVE_INT.fieldOf("priority").orElse((Object)0).forGetter(SimulatedSection::priority), (App)Title.CODEC.fieldOf("title").forGetter(SimulatedSection::title), (App)ResourceLocation.CODEC.fieldOf("sprite").orElse((Object)DEFAULT_BANNER).forGetter(SimulatedSection::sprite), (App)Codec.BOOL.fieldOf("only_animate_on_hover").orElse((Object)false).forGetter(SimulatedSection::animateOnHover)).apply((Applicative)instance, SimulatedSection::new));

    @Override
    public int compareTo(@NotNull SimulatedSection other) {
        return (int)Math.signum(this.priority() - other.priority());
    }

    public record Title(Component text, Colorc color, Optional<Colorc> secondaryColor, Colorc background) {
        public static final Codec<Colorc> COLOR_CODEC = Color.ARGB_INT_CODEC.xmap(i -> new Color(i.intValue(), true), Colorc::argb);
        public static final Codec<Title> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ComponentSerialization.CODEC.fieldOf("text").forGetter(Title::text), (App)COLOR_CODEC.fieldOf("color").orElse((Object)new Color(-1, true)).forGetter(Title::color), (App)COLOR_CODEC.optionalFieldOf("secondary_color").forGetter(Title::secondaryColor), (App)COLOR_CODEC.fieldOf("background").orElse((Object)new Color(-1442840576, true)).forGetter(Title::background)).apply((Applicative)instance, Title::new));
    }
}
