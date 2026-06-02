/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.impl.registrate;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

private record CreateRegistrateRegistrationCallbackImpl.CallbackImpl<R, T extends R>(ResourceKey<? extends Registry<R>> registry, ResourceLocation id, NonNullConsumer<? super T> callback) {
    public void addToRegistrate(CreateRegistrate registrate) {
        registrate.addRegisterCallback(this.id.getPath(), this.registry, this.callback);
    }
}
