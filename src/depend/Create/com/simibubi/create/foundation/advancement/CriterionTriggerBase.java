/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.advancements.CriterionTrigger
 *  net.minecraft.advancements.CriterionTrigger$Listener
 *  net.minecraft.advancements.critereon.SimpleCriterionTrigger$SimpleInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.PlayerAdvancements
 *  net.minecraft.server.level.ServerPlayer
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.advancement;

import com.google.common.collect.Maps;
import com.simibubi.create.Create;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CriterionTriggerBase<T extends Instance>
implements CriterionTrigger<T> {
    private final ResourceLocation id;
    protected final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> listeners = Maps.newHashMap();

    public CriterionTriggerBase(String id) {
        this.id = Create.asResource(id);
    }

    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, CriterionTrigger.Listener<T> listener) {
        Set playerListeners = this.listeners.computeIfAbsent(playerAdvancementsIn, k -> new HashSet());
        playerListeners.add(listener);
    }

    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, CriterionTrigger.Listener<T> listener) {
        Set<CriterionTrigger.Listener<T>> playerListeners = this.listeners.get(playerAdvancementsIn);
        if (playerListeners != null) {
            playerListeners.remove(listener);
            if (playerListeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    protected void trigger(ServerPlayer player, @Nullable List<Supplier<Object>> suppliers) {
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        Set<CriterionTrigger.Listener<T>> playerListeners = this.listeners.get(playerAdvancements);
        if (playerListeners != null) {
            LinkedList<CriterionTrigger.Listener<T>> list = new LinkedList<CriterionTrigger.Listener<T>>();
            for (CriterionTrigger.Listener<T> listener2 : playerListeners) {
                if (!((Instance)listener2.trigger()).test(suppliers)) continue;
                list.add(listener2);
            }
            list.forEach(listener -> listener.run(playerAdvancements));
        }
    }

    public static abstract class Instance
    implements SimpleCriterionTrigger.SimpleInstance {
        protected abstract boolean test(@Nullable List<Supplier<Object>> var1);
    }
}
