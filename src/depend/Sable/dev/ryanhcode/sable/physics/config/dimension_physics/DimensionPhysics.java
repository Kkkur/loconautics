/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3f
 */
package dev.ryanhcode.sable.physics.config.dimension_physics;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.physics.config.dimension_physics.BezierResourceFunction;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public record DimensionPhysics(ResourceLocation dimension, int priority, Optional<Float> universalDrag, Optional<Vector3f> baseGravity, Optional<Double> basePressure, Optional<BezierResourceFunction> pressureFunction, Optional<Vector3f> magneticNorth) {
    public static final Vector3f DEFAULT_GRAVITY = new Vector3f(0.0f, -11.0f, 0.0f);
    public static final Vector3f DEFAULT_MAGNETIC_NORTH = new Vector3f(0.0f, 0.0f, 0.0f);
    public static final double DEFAULT_PRESSURE = 1.0;
    private static final float DEFAULT_UNIVERSAL_DRAG = 0.09f;
    public static final Codec<DimensionPhysics> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("dimension").forGetter(DimensionPhysics::dimension), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).optionalFieldOf("priority", (Object)1000).forGetter(DimensionPhysics::priority), (App)Codec.optionalField((String)"universal_drag", (Codec)Codec.FLOAT, (boolean)false).forGetter(DimensionPhysics::universalDrag), (App)Codec.optionalField((String)"base_gravity", (Codec)ExtraCodecs.VECTOR3F, (boolean)false).forGetter(DimensionPhysics::baseGravity), (App)Codec.optionalField((String)"base_pressure", (Codec)Codec.DOUBLE, (boolean)false).forGetter(DimensionPhysics::basePressure), (App)Codec.optionalField((String)"pressure_function", BezierResourceFunction.CODEC, (boolean)false).forGetter(DimensionPhysics::pressureFunction), (App)Codec.optionalField((String)"magnetic_north", (Codec)ExtraCodecs.VECTOR3F, (boolean)false).forGetter(DimensionPhysics::magneticNorth)).apply(Applicative.unbox((App)instance), DimensionPhysics::new));

    public static DimensionPhysics createDefault(Level level) {
        double seaLevel = level.getSeaLevel();
        double currentAltitude = level.dimensionType().minY();
        double maxAltitude = currentAltitude + (double)level.dimensionType().logicalHeight();
        double baseSlope = -0.004;
        double maxPressure = 1.5;
        double maxStep = 200.0;
        double smoothingAltitude = maxAltitude - 40.0;
        currentAltitude = Math.max(currentAltitude, Math.log(1.5) / -0.004 + seaLevel);
        BezierResourceFunction pressureFunction = new BezierResourceFunction();
        while (true) {
            double currentPressure = Math.exp(-0.004 * (currentAltitude - seaLevel));
            double currentSlope = currentPressure * -0.004;
            pressureFunction.addPoint(new BezierResourceFunction.BezierPoint(currentAltitude, currentPressure, currentSlope));
            if (currentAltitude < seaLevel && currentAltitude + 200.0 >= seaLevel) {
                currentAltitude = seaLevel;
                continue;
            }
            if (currentAltitude < smoothingAltitude && currentAltitude + 200.0 >= smoothingAltitude) {
                currentAltitude = smoothingAltitude;
                continue;
            }
            if (currentAltitude >= smoothingAltitude) break;
            currentAltitude += 200.0;
        }
        double smoothingPressure = pressureFunction.getPoints().get(pressureFunction.pointSize() - 1).value();
        double finalSlope = -2.0 * smoothingPressure / (maxAltitude - smoothingAltitude);
        pressureFunction.addPoint(new BezierResourceFunction.BezierPoint(maxAltitude, 0.0, finalSlope));
        Vector3f north = level.dimensionType().natural() ? DEFAULT_MAGNETIC_NORTH : new Vector3f(0.0f, 0.0f, 0.0f);
        return new DimensionPhysics(level.dimension().location(), 0, Optional.of(Float.valueOf(0.09f)), Optional.of(DEFAULT_GRAVITY), Optional.of(1.0), Optional.of(pressureFunction), Optional.of(north));
    }
}
