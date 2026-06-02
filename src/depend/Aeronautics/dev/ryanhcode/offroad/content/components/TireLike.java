/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.Create
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.offroad.content.components;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.Create;
import dev.ryanhcode.offroad.Offroad;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record TireLike(float radius, Vec3 rotation, Vec3 offset, Optional<ResourceLocation> model) {
    public static final Codec<TireLike> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.FLOAT.optionalFieldOf("radius", (Object)Float.valueOf(1.0f)).forGetter(TireLike::radius), (App)Vec3.CODEC.optionalFieldOf("rotation", (Object)new Vec3(90.0, 0.0, 0.0)).forGetter(TireLike::rotation), (App)Vec3.CODEC.optionalFieldOf("offset", (Object)new Vec3(0.0, 0.0, 0.0)).forGetter(TireLike::offset), (App)ResourceLocation.CODEC.optionalFieldOf("model").forGetter(TireLike::model)).apply((Applicative)i, TireLike::new));
    public static final TireLike SMALL_TIRE = new TireLike(0.75f);
    public static final TireLike TIRE = new TireLike(0.96875f);
    public static final TireLike LARGE_TIRE = new TireLike(1.25f);
    public static final TireLike MONSTROUS_TIRE = new TireLike(2.0f);
    public static final TireLike CRUSHING_WHEEL = new TireLike(1.0f);
    public static final TireLike WATER_WHEEL = new TireLike(1.0f);
    public static final TireLike FLYWHEEL = new TireLike(1.375f);
    public static final TireLike LARGE_WATER_WHEEL = new TireLike(2.4375f);
    public static final TireLike ROCKCUTTING_WHEEL = new TireLike(0.8f, new Vec3(90.0, 0.0, 0.0), Vec3.ZERO, Offroad.path("block/rockcutting_wheel/wheel"));
    public static final TireLike MECHANICAL_ROLLER = new TireLike(0.7f, Vec3.ZERO, new Vec3(0.0, -0.5, 0.0), Create.asResource((String)"block/mechanical_roller/wheel"));

    public TireLike(float radius, Vec3 rotation, Vec3 offset, @Nullable ResourceLocation model) {
        this(radius, rotation, offset, Optional.ofNullable(model));
    }

    public TireLike(float radius) {
        this(radius, new Vec3(90.0, 0.0, 0.0), new Vec3(0.0, 0.0, 0.0), Optional.empty());
    }

    public TireLike(float radius, ResourceLocation model) {
        this(radius, new Vec3(90.0, 0.0, 0.0), new Vec3(0.0, 0.0, 0.0), Optional.of(model));
    }
}
