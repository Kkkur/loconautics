/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  org.slf4j.Logger
 */
package com.simibubi.create.api.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.hat.TrainHatInfo;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class TrainHatInfoProvider
implements DataProvider {
    protected final Map<ResourceLocation, TrainHatInfo> trainHatOffsets = new HashMap<ResourceLocation, TrainHatInfo>();
    private final PackOutput.PathProvider path;

    public TrainHatInfoProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.path = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "train_hat_info");
    }

    protected abstract void createOffsets();

    protected void makeInfoFor(EntityType<?> type, Vec3 offset) {
        this.makeInfoFor(type, offset, "", 0, 1.0f);
    }

    protected void makeInfoFor(EntityType<?> type, Vec3 offset, String part) {
        this.makeInfoFor(type, offset, part, 0, 1.0f);
    }

    protected void makeInfoFor(EntityType<?> type, Vec3 offset, float scale) {
        this.makeInfoFor(type, offset, "", 0, scale);
    }

    protected void makeInfoFor(EntityType<?> type, Vec3 offset, String part, float scale) {
        this.makeInfoFor(type, offset, part, 0, scale);
    }

    protected void makeInfoFor(EntityType<?> type, Vec3 offset, String part, int cubeIndex, float scale) {
        this.trainHatOffsets.put(BuiltInRegistries.ENTITY_TYPE.getKey(type), new TrainHatInfo(part, cubeIndex, offset, scale));
    }

    public CompletableFuture<?> run(CachedOutput output) {
        this.trainHatOffsets.clear();
        this.createOffsets();
        return CompletableFuture.allOf((CompletableFuture[])this.trainHatOffsets.entrySet().stream().map(entry -> DataProvider.saveStable((CachedOutput)output, (JsonElement)((JsonElement)TrainHatInfo.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)((TrainHatInfo)entry.getValue())).resultOrPartial(arg_0 -> ((Logger)Create.LOGGER).error(arg_0)).orElseThrow()), (Path)this.path.json((ResourceLocation)entry.getKey()))).toArray(CompletableFuture[]::new));
    }

    public String getName() {
        return "Create Train Hat Information";
    }
}
