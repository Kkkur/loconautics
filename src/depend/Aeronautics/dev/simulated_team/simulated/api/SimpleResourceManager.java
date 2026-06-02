/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  foundry.veil.api.CodecReloadListener
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.resources.FileToIdConverter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.util.profiling.ProfilerFiller
 */
package dev.simulated_team.simulated.api;

import com.mojang.serialization.Codec;
import dev.simulated_team.simulated.service.ServiceUtil;
import foundry.veil.api.CodecReloadListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class SimpleResourceManager<T>
extends CodecReloadListener<T> {
    private static final Registry REGISTRY = ServiceUtil.load(Registry.class);
    private final Map<ResourceLocation, T> entries = new Object2ObjectOpenHashMap();
    private final Map<T, ResourceLocation> toId = new Object2ObjectOpenHashMap();
    private final List<T> sortedValues = new ObjectArrayList();
    private boolean canSort = false;

    public static <T> SimpleResourceManager<T> create(Codec<T> codec, ResourceLocation path) {
        SimpleResourceManager<T> manager = new SimpleResourceManager<T>(codec, path.getNamespace() + "/" + path.getPath());
        REGISTRY.registerListener((PreparableReloadListener)manager);
        return manager;
    }

    private SimpleResourceManager(Codec<T> codec, String path) {
        super(codec, FileToIdConverter.json((String)path));
    }

    public SimpleResourceManager<T> sorted() {
        this.canSort = true;
        return this;
    }

    public T get(ResourceLocation id) {
        return this.entries.get(id);
    }

    public ResourceLocation getId(T t) {
        return this.toId.get(t);
    }

    public Set<Map.Entry<ResourceLocation, T>> entrySet() {
        return this.entries.entrySet();
    }

    public Collection<T> entries() {
        return this.entries.values();
    }

    public List<T> sortedEntries() {
        return this.sortedValues;
    }

    protected void apply(Map<ResourceLocation, T> map, ResourceManager manager, ProfilerFiller profiler) {
        this.entries.clear();
        this.entries.putAll(map);
        this.toId.clear();
        map.forEach((key, value) -> this.toId.put((T)value, (ResourceLocation)key));
        if (this.canSort) {
            this.sortedValues.clear();
            this.sortedValues.addAll(map.values().stream().sorted().toList());
        }
    }

    public static interface Registry {
        public void registerListener(PreparableReloadListener var1);
    }
}
