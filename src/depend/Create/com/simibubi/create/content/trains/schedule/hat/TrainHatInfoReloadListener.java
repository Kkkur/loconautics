/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.FileToIdConverter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.animal.SnowGolem
 *  net.minecraft.world.phys.Vec3
 *  org.slf4j.Logger
 */
package com.simibubi.create.content.trains.schedule.hat;

import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.hat.TrainHatInfo;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class TrainHatInfoReloadListener {
    private static final Map<EntityType<?>, TrainHatInfo> ENTITY_INFO_MAP = new HashMap();
    public static final String HAT_INFO_DIRECTORY = "train_hat_info";
    public static final ResourceManagerReloadListener LISTENER = TrainHatInfoReloadListener::registerOffsetOverrides;
    private static final TrainHatInfo DEFAULT = new TrainHatInfo("", 0, Vec3.ZERO, 1.0f);

    private static void registerOffsetOverrides(ResourceManager manager) {
        ENTITY_INFO_MAP.clear();
        FileToIdConverter converter = FileToIdConverter.json((String)HAT_INFO_DIRECTORY);
        converter.listMatchingResources(manager).forEach((location, resource) -> {
            String[] splitPath = location.getPath().split("/");
            ResourceLocation entityName = ResourceLocation.fromNamespaceAndPath((String)location.getNamespace(), (String)splitPath[splitPath.length - 1].replace(".json", ""));
            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(entityName)) {
                Create.LOGGER.error("Failed to load train hat info for entity {} as it does not exist.", (Object)entityName);
                return;
            }
            try (BufferedReader reader = resource.openAsReader();){
                JsonObject json = GsonHelper.parse((Reader)reader);
                ENTITY_INFO_MAP.put((EntityType)BuiltInRegistries.ENTITY_TYPE.get(entityName), (TrainHatInfo)TrainHatInfo.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)json).resultOrPartial(arg_0 -> ((Logger)Create.LOGGER).error(arg_0)).orElseThrow());
            }
            catch (Exception e) {
                Create.LOGGER.error("Failed to read train hat info for entity {}!", (Object)entityName, (Object)e);
            }
        });
        Create.LOGGER.info("Loaded {} train hat configurations.", (Object)ENTITY_INFO_MAP.size());
    }

    public static TrainHatInfo getHatInfoFor(Entity entity) {
        SnowGolem snowGolem;
        if (entity instanceof SnowGolem && (snowGolem = (SnowGolem)entity).hasPumpkin()) {
            return new TrainHatInfo("", 0, new Vec3(0.0, -3.0, 0.0), 1.18f);
        }
        return ENTITY_INFO_MAP.getOrDefault(entity.getType(), DEFAULT);
    }
}
