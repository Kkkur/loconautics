/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataSerializer
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.network.syncher.EntityDataSerializer;

public interface SimEntityDataSerialization {
    public static final SimEntityDataSerialization INSTANCE = ServiceUtil.load(SimEntityDataSerialization.class);

    public <A, T extends EntityDataSerializer<A>> void registerDataSerializer(String var1, T var2);
}
