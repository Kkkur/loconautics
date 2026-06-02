/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.world.item.ArmorItem$Type
 */
package dev.eriksonn.aeronautics.index;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.item.ArmorItem;

static class AeroArmorMaterials.1
extends Object2ObjectOpenHashMap<ArmorItem.Type, Integer> {
    AeroArmorMaterials.1() {
        this.put(ArmorItem.Type.HELMET, 1);
    }
}
