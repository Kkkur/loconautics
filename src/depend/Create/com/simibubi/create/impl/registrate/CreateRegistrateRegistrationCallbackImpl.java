/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.impl.registrate;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class CreateRegistrateRegistrationCallbackImpl {
    private static final Map<String, Either<List<CallbackImpl<?, ?>>, CreateRegistrate>> CALLBACKS = new HashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void provideRegistrate(CreateRegistrate registrate) {
        Map<String, Either<List<CallbackImpl<?, ?>>, CreateRegistrate>> map = CALLBACKS;
        synchronized (map) {
            String modid = registrate.getModid();
            Either<List<CallbackImpl<?, ?>>, CreateRegistrate> either = CALLBACKS.remove(modid);
            if (either != null) {
                Optional optionalCallbacks = either.left();
                if (optionalCallbacks.isEmpty()) {
                    throw new IllegalArgumentException("Tried to register a duplicate CreateRegistrate instance for mod ID: " + modid);
                }
                for (CallbackImpl callback : (List)optionalCallbacks.get()) {
                    callback.addToRegistrate(registrate);
                }
            }
            CALLBACKS.put(modid, Either.right((Object)((Object)registrate)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <R, T extends R> void register(ResourceKey<? extends Registry<R>> registry, ResourceLocation id, NonNullConsumer<? super T> callback) {
        Either either;
        CallbackImpl callbackImpl = new CallbackImpl(registry, id, callback);
        Map<String, Either<List<CallbackImpl<?, ?>>, CreateRegistrate>> map = CALLBACKS;
        synchronized (map) {
            either = CALLBACKS.computeIfAbsent(id.getNamespace(), k -> Either.left(new ArrayList()));
            either.ifLeft(callbacks -> callbacks.add(callbackImpl));
        }
        either.ifRight(callbackImpl::addToRegistrate);
    }

    private record CallbackImpl<R, T extends R>(ResourceKey<? extends Registry<R>> registry, ResourceLocation id, NonNullConsumer<? super T> callback) {
        public void addToRegistrate(CreateRegistrate registrate) {
            registrate.addRegisterCallback(this.id.getPath(), this.registry, this.callback);
        }
    }
}
