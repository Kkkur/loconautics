/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.GlobalPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class LogisticsNetwork {
    public UUID id;
    public RequestPromiseQueue panelPromises;
    public Set<GlobalPos> totalLinks;
    public Set<GlobalPos> loadedLinks;
    public UUID owner;
    public boolean locked;

    public LogisticsNetwork(UUID networkId) {
        this.id = networkId;
        this.panelPromises = new RequestPromiseQueue(Create.LOGISTICS::markDirty);
        this.totalLinks = new HashSet<GlobalPos>();
        this.loadedLinks = new HashSet<GlobalPos>();
        this.owner = null;
        this.locked = false;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", this.id);
        tag.put("Promises", (Tag)this.panelPromises.write(registries));
        tag.put("Links", (Tag)NBTHelper.writeCompoundList(this.totalLinks, p -> {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Pos", NbtUtils.writeBlockPos((BlockPos)p.pos()));
            if (p.dimension() != Level.OVERWORLD) {
                NBTHelper.writeResourceLocation((CompoundTag)nbt, (String)"Dim", (ResourceLocation)p.dimension().location());
            }
            return nbt;
        }));
        if (this.owner != null) {
            tag.putUUID("Owner", this.owner);
        }
        tag.putBoolean("Locked", this.locked);
        return tag;
    }

    public static LogisticsNetwork read(CompoundTag tag, HolderLookup.Provider registries) {
        LogisticsNetwork network = new LogisticsNetwork(tag.getUUID("Id"));
        network.panelPromises = RequestPromiseQueue.read(tag.getCompound("Promises"), registries, Create.LOGISTICS::markDirty);
        NBTHelper.iterateCompoundList((ListTag)tag.getList("Links", 10), nbt -> network.totalLinks.add(GlobalPos.of((ResourceKey)(nbt.contains("Dim") ? ResourceKey.create((ResourceKey)Registries.DIMENSION, (ResourceLocation)NBTHelper.readResourceLocation((CompoundTag)nbt, (String)"Dim")) : Level.OVERWORLD), (BlockPos)NBTHelper.readBlockPos((CompoundTag)nbt, (String)"Pos"))));
        network.owner = tag.contains("Owner") ? tag.getUUID("Owner") : null;
        network.locked = tag.getBoolean("Locked");
        return network;
    }
}
