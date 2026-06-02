/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.particle.ICustomParticleData
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.foundation.particle.ICustomParticleData;
import dev.eriksonn.aeronautics.content.particle.AirPoofParticleData;
import dev.eriksonn.aeronautics.content.particle.GustParticleData;
import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticleData;
import dev.eriksonn.aeronautics.content.particle.LevititeSparklePartcleData;
import dev.eriksonn.aeronautics.content.particle.PropellerAirParticleData;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public enum AeroParticleTypes {
    PROPELLER_AIR_FLOW(PropellerAirParticleData::new),
    HOT_AIR_EMBER(HotAirEmberParticleData::new),
    LEVITITE_SPARKLE(LevititeSparklePartcleData::new),
    GUST(GustParticleData::new),
    AIR_POOF(AirPoofParticleData::new);

    public final ParticleEntry<?> entry;

    private <D extends ParticleOptions> AeroParticleTypes(Supplier<? extends ICustomParticleData<D>> typeFactory) {
        this.entry = new ParticleEntry(typeFactory);
    }

    public static void init() {
    }

    public static void registerClientParticles(Consumer<ParticleEntry<?>> consume) {
        for (AeroParticleTypes value : AeroParticleTypes.values()) {
            consume.accept(value.entry);
        }
    }

    public ParticleType<?> get() {
        return this.entry.object;
    }

    public static class ParticleEntry<D extends ParticleOptions> {
        private final Supplier<? extends ICustomParticleData<D>> typeFactory;
        private final ParticleType<D> object;

        public ParticleEntry(Supplier<? extends ICustomParticleData<D>> typeFactory) {
            this.typeFactory = typeFactory;
            this.object = this.typeFactory.get().createType();
        }

        public Supplier<? extends ICustomParticleData<D>> getTypeFactory() {
            return this.typeFactory;
        }

        public ParticleType<D> getObject() {
            return this.object;
        }
    }
}
