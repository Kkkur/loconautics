/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.index.SimEntityDataSerializers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

class SimEntityDataSerializers.1
implements StreamCodec<RegistryFriendlyByteBuf, Vec3> {
    SimEntityDataSerializers.1(SimEntityDataSerializers.1 this$0) {
    }

    public Vec3 decode(RegistryFriendlyByteBuf object) {
        return object.readVec3();
    }

    public void encode(RegistryFriendlyByteBuf object, Vec3 object2) {
        object.writeVec3(object2);
    }
}
