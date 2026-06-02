/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package dev.ryanhcode.sable.physics.floating_block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FloatingBlockMaterial(boolean preventSelfLift, boolean scaleWithPressure, boolean scaleWithGravity, double liftStrength, double transitionSpeed, double slowVerticalFriction, double fastVerticalFriction, double slowHorizontalFriction, double fastHorizontalFriction) {
    public static final Codec<FloatingBlockMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("prevent_self_lift", (Object)false).forGetter(FloatingBlockMaterial::preventSelfLift), (App)Codec.BOOL.optionalFieldOf("scale_with_pressure", (Object)false).forGetter(FloatingBlockMaterial::scaleWithPressure), (App)Codec.BOOL.optionalFieldOf("scale_friction_with_gravity", (Object)false).forGetter(FloatingBlockMaterial::scaleWithGravity), (App)Codec.DOUBLE.fieldOf("lift_strength").forGetter(FloatingBlockMaterial::liftStrength), (App)Codec.DOUBLE.optionalFieldOf("transition_speed", (Object)0.0).forGetter(FloatingBlockMaterial::transitionSpeed), (App)Codec.DOUBLE.optionalFieldOf("slow_vertical_friction", (Object)0.0).forGetter(FloatingBlockMaterial::slowVerticalFriction), (App)Codec.DOUBLE.optionalFieldOf("fast_vertical_friction", (Object)0.0).forGetter(FloatingBlockMaterial::fastVerticalFriction), (App)Codec.DOUBLE.optionalFieldOf("slow_horizontal_friction", (Object)0.0).forGetter(FloatingBlockMaterial::slowHorizontalFriction), (App)Codec.DOUBLE.optionalFieldOf("fast_horizontal_friction", (Object)0.0).forGetter(FloatingBlockMaterial::fastHorizontalFriction)).apply((Applicative)instance, FloatingBlockMaterial::new));
    public static final StreamCodec<ByteBuf, FloatingBlockMaterial> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
