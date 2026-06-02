/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.advancements.CriterionTrigger
 *  net.minecraft.advancements.CriterionTrigger$Listener
 *  net.minecraft.advancements.CriterionTriggerInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.PlayerAdvancements
 *  net.minecraft.server.level.ServerPlayer
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.data.advancements;

import com.google.common.collect.Maps;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class SimulatedCriterionTriggerBase<T extends Instance>
implements CriterionTrigger<T> {
    private final ResourceLocation id;
    protected final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> listeners = Maps.newHashMap();

    public SimulatedCriterionTriggerBase(ResourceLocation id) {
        this.id = id;
    }

    public void addPlayerListener(PlayerAdvancements pPlayerAdvancements, CriterionTrigger.Listener<T> pListener) {
        Set playerListeners = this.listeners.computeIfAbsent(pPlayerAdvancements, k -> new HashSet());
        playerListeners.add(pListener);
    }

    public void removePlayerListener(PlayerAdvancements pPlayerAdvancements, CriterionTrigger.Listener<T> pListener) {
        Set<CriterionTrigger.Listener<T>> playerListeners = this.listeners.get(pPlayerAdvancements);
        if (playerListeners != null) {
            playerListeners.remove(pListener);
            if (playerListeners.isEmpty()) {
                this.listeners.remove(pPlayerAdvancements);
            }
        }
    }

    public void removePlayerListeners(PlayerAdvancements pPlayerAdvancements) {
        this.listeners.remove(pPlayerAdvancements);
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
    implements CriterionTriggerInstance {
        private final ResourceLocation id;

        public Instance(ResourceLocation id) {
            this.id = id;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        protected abstract boolean test(@Nullable List<Supplier<Object>> var1);
    }
}
