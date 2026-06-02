/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package dev.simulated_team.simulated.api.sound;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.simulated_team.simulated.api.sound.SoundFile;
import java.util.List;
import java.util.Optional;

public record SoundDefinition(boolean replace, Optional<String> subtitle, List<SoundFile> sounds) {
    public static final Codec<SoundDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("replace", (Object)false).forGetter(SoundDefinition::replace), (App)Codec.STRING.optionalFieldOf("subtitle").orElse(Optional.empty()).forGetter(SoundDefinition::subtitle), (App)SoundFile.FULL_CODEC.listOf().optionalFieldOf("sounds", List.of()).forGetter(SoundDefinition::sounds)).apply((Applicative)instance, SoundDefinition::new));
}
