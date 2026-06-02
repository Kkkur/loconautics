/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  net.minecraft.core.BlockPos
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasHolder;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import java.util.List;
import net.minecraft.core.BlockPos;

public record SavedBalloon(BoundingBox3i bounds, BlockPos controllerPos, List<LiftingGasHolder> gasData) {
    public static final Codec<SavedBalloon> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BoundingBox3i.CODEC.fieldOf("bounds").forGetter(SavedBalloon::bounds), (App)BlockPos.CODEC.fieldOf("pos").forGetter(SavedBalloon::controllerPos), (App)LiftingGasHolder.CODEC.listOf().fieldOf("gasData").forGetter(SavedBalloon::gasData)).apply((Applicative)instance, SavedBalloon::new));
}
