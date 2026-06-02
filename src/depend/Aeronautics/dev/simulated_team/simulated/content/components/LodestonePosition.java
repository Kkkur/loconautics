/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.GlobalPos
 */
package dev.simulated_team.simulated.content.components;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;

public record LodestonePosition(long id, GlobalPos associatedPos) {
    public static final Codec<LodestonePosition> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.LONG.fieldOf("ID").forGetter(LodestonePosition::id), (App)GlobalPos.CODEC.fieldOf("POS").forGetter(LodestonePosition::associatedPos)).apply((Applicative)i, LodestonePosition::new));
}
