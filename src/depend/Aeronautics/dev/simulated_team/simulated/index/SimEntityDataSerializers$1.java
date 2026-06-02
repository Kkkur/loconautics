/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.index;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

class SimEntityDataSerializers.1
implements EntityDataSerializer<Vec3> {
    SimEntityDataSerializers.1() {
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, Vec3> codec() {
        return new StreamCodec<RegistryFriendlyByteBuf, Vec3>(this){

            public Vec3 decode(RegistryFriendlyByteBuf object) {
                return object.readVec3();
            }

            public void encode(RegistryFriendlyByteBuf object, Vec3 object2) {
                object.writeVec3(object2);
            }
        };
    }

    public Vec3 copy(Vec3 object) {
        return new Vec3(object.x, object.y, object.z);
    }
}
