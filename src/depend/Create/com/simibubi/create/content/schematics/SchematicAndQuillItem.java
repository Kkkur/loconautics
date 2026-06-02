/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.DoubleTag
 *  net.minecraft.nbt.IntTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import java.util.Iterator;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SchematicAndQuillItem
extends Item {
    public SchematicAndQuillItem(Item.Properties properties) {
        super(properties);
    }

    public static void replaceStructureVoidWithAir(CompoundTag nbt) {
        String air = RegisteredObjectsHelper.getKeyOrThrow((Block)Blocks.AIR).toString();
        String structureVoid = RegisteredObjectsHelper.getKeyOrThrow((Block)Blocks.STRUCTURE_VOID).toString();
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("palette", 10), c -> {
            if (c.contains("Name") && c.getString("Name").equals(structureVoid)) {
                c.putString("Name", air);
            }
        });
    }

    public static void clampGlueBoxes(Level level, AABB aabb, CompoundTag nbt) {
        ListTag listtag = nbt.getList("entities", 10).copy();
        Iterator iterator = listtag.iterator();
        while (iterator.hasNext()) {
            CompoundTag compoundtag;
            Tag tag = (Tag)iterator.next();
            if (!(tag instanceof CompoundTag) || !(compoundtag = (CompoundTag)tag).contains("nbt") || !ResourceLocation.parse((String)compoundtag.getCompound("nbt").getString("id")).equals((Object)AllEntityTypes.SUPER_GLUE.getId())) continue;
            iterator.remove();
        }
        for (SuperGlueEntity entity : SuperGlueEntity.collectCropped(level, aabb)) {
            Vec3 vec3 = new Vec3(entity.getX() - aabb.minX, entity.getY() - aabb.minY, entity.getZ() - aabb.minZ);
            CompoundTag compoundtag = new CompoundTag();
            entity.save(compoundtag);
            BlockPos blockpos = BlockPos.containing((Position)vec3);
            CompoundTag entityTag = new CompoundTag();
            entityTag.put("pos", (Tag)SchematicAndQuillItem.newDoubleList(vec3.x, vec3.y, vec3.z));
            entityTag.put("blockPos", (Tag)SchematicAndQuillItem.newIntegerList(blockpos.getX(), blockpos.getY(), blockpos.getZ()));
            entityTag.put("nbt", (Tag)compoundtag.copy());
            listtag.add((Object)entityTag);
        }
        nbt.put("entities", (Tag)listtag);
    }

    private static ListTag newIntegerList(int ... pValues) {
        ListTag listtag = new ListTag();
        for (int i : pValues) {
            listtag.add((Object)IntTag.valueOf((int)i));
        }
        return listtag;
    }

    private static ListTag newDoubleList(double ... pValues) {
        ListTag listtag = new ListTag();
        for (double d0 : pValues) {
            listtag.add((Object)DoubleTag.valueOf((double)d0));
        }
        return listtag;
    }
}
