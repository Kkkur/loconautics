/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 */
package dev.eriksonn.aeronautics.content.components;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.eriksonn.aeronautics.content.particle.LevititeSparklePartcleData;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

public record Levitating(Float dragFraction, Optional<ParticleOptions> particle) {
    public static final Codec<Levitating> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.FLOAT.optionalFieldOf("drag_fraction", (Object)Float.valueOf(0.93f)).forGetter(Levitating::dragFraction), (App)ParticleTypes.CODEC.lenientOptionalFieldOf("particle").forGetter(Levitating::particle)).apply((Applicative)i, Levitating::new));
    public static final Levitating DEFAULT = new Levitating(Float.valueOf(0.93f), Optional.empty());
    public static final Levitating END_STONE = new Levitating(Float.valueOf(0.85f), Optional.empty());
    public static final Levitating LEVITITE = new Levitating(Float.valueOf(0.93f), Optional.of(new LevititeSparklePartcleData(9424022)));
    public static final Levitating PEARLESCENT_LEVITITE = new Levitating(Float.valueOf(0.93f), Optional.of(new LevititeSparklePartcleData(15521489)));
}
