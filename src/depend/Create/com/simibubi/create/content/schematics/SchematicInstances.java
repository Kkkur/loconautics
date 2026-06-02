/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  net.createmod.catnip.data.WorldAttached
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.schematics;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.SchematicItem;
import java.util.concurrent.TimeUnit;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class SchematicInstances {
    private static final WorldAttached<Cache<Integer, SchematicLevel>> LOADED_SCHEMATICS = new WorldAttached($ -> CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build());

    @Nullable
    public static SchematicLevel get(Level world, ItemStack schematic) {
        int hash;
        Cache map = (Cache)LOADED_SCHEMATICS.get((LevelAccessor)world);
        SchematicLevel ifPresent = (SchematicLevel)map.getIfPresent((Object)(hash = SchematicInstances.getHash(schematic)));
        if (ifPresent != null) {
            return ifPresent;
        }
        SchematicLevel loadWorld = SchematicInstances.loadWorld(world, schematic);
        if (loadWorld == null) {
            return null;
        }
        map.put((Object)hash, (Object)loadWorld);
        return loadWorld;
    }

    private static SchematicLevel loadWorld(Level wrapped, ItemStack schematic) {
        if (schematic == null || !schematic.has(AllDataComponents.SCHEMATIC_FILE)) {
            return null;
        }
        if (!schematic.has(AllDataComponents.SCHEMATIC_DEPLOYED)) {
            return null;
        }
        StructureTemplate activeTemplate = SchematicItem.loadSchematic(wrapped, schematic);
        if (activeTemplate.getSize().equals((Object)Vec3i.ZERO)) {
            return null;
        }
        BlockPos anchor = (BlockPos)schematic.get(AllDataComponents.SCHEMATIC_ANCHOR);
        SchematicLevel world = new SchematicLevel(anchor, wrapped);
        StructurePlaceSettings settings = SchematicItem.getSettings(schematic);
        activeTemplate.placeInWorld((ServerLevelAccessor)world, anchor, anchor, settings, wrapped.getRandom(), 2);
        StructureTransform transform = new StructureTransform(settings.getRotationPivot(), Direction.Axis.Y, settings.getRotation(), settings.getMirror());
        for (BlockEntity be : world.getBlockEntities()) {
            transform.apply(be);
        }
        return world;
    }

    public static void clearHash(ItemStack schematic) {
        if (schematic == null || !schematic.has(AllDataComponents.SCHEMATIC_FILE)) {
            return;
        }
        schematic.remove(AllDataComponents.SCHEMATIC_HASH);
    }

    public static int getHash(ItemStack schematic) {
        if (schematic == null || !schematic.has(AllDataComponents.SCHEMATIC_FILE)) {
            return -1;
        }
        if (!schematic.has(AllDataComponents.SCHEMATIC_HASH)) {
            schematic.set(AllDataComponents.SCHEMATIC_HASH, (Object)schematic.getComponentsPatch().hashCode());
        }
        return (Integer)schematic.getOrDefault(AllDataComponents.SCHEMATIC_HASH, (Object)-1);
    }
}
