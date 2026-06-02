/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectLists
 *  net.createmod.catnip.data.WorldAttached
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class ContraptionHandler {
    public static WorldAttached<Map<Integer, WeakReference<AbstractContraptionEntity>>> loadedContraptions = new WorldAttached($ -> new HashMap());
    static WorldAttached<List<AbstractContraptionEntity>> queuedAdditions = new WorldAttached($ -> ObjectLists.synchronize((ObjectList)new ObjectArrayList()));

    public static void tick(Level world) {
        Map map = (Map)loadedContraptions.get((LevelAccessor)world);
        List queued = (List)queuedAdditions.get((LevelAccessor)world);
        for (AbstractContraptionEntity contraptionEntity : queued) {
            map.put(contraptionEntity.getId(), new WeakReference<AbstractContraptionEntity>(contraptionEntity));
        }
        queued.clear();
        Collection values = map.values();
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            WeakReference weakReference = (WeakReference)iterator.next();
            AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity)((Object)weakReference.get());
            if (contraptionEntity == null || !contraptionEntity.isAliveOrStale()) {
                iterator.remove();
                continue;
            }
            if (!contraptionEntity.isAlive()) {
                --contraptionEntity.staleTicks;
                continue;
            }
            ContraptionCollider.collideEntities(contraptionEntity);
        }
    }

    public static void addSpawnedContraptionsToCollisionList(Entity entity, Level world) {
        if (entity instanceof AbstractContraptionEntity) {
            ((List)queuedAdditions.get((LevelAccessor)world)).add((AbstractContraptionEntity)entity);
        }
    }

    public static void entitiesWhoJustDismountedGetSentToTheRightLocation(LivingEntity entityLiving, Level world) {
        if (!world.isClientSide) {
            return;
        }
        CompoundTag data = entityLiving.getPersistentData();
        if (!data.contains("ContraptionDismountLocation")) {
            return;
        }
        Vec3 position = VecHelper.readNBT((ListTag)data.getList("ContraptionDismountLocation", 6));
        if (entityLiving.getVehicle() == null) {
            entityLiving.absMoveTo(position.x, position.y, position.z, entityLiving.getYRot(), entityLiving.getXRot());
        }
        data.remove("ContraptionDismountLocation");
        entityLiving.setOnGround(false);
    }
}
