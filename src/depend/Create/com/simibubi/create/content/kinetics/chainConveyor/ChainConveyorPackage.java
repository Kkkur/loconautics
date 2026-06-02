/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.google.common.cache.Cache;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.foundation.utility.TickBasedCache;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class ChainConveyorPackage {
    public static final AtomicInteger netIdGenerator = new AtomicInteger();
    private static final int ticksUntilExpired = 30;
    public static final WorldAttached<Cache<Integer, ChainConveyorPackagePhysicsData>> physicsDataCache = new WorldAttached($ -> new TickBasedCache(30, true));
    public float chainPosition;
    public ItemStack item;
    public int netId;
    public boolean justFlipped;
    public Vec3 worldPosition;
    public float yaw;
    private ChainConveyorPackagePhysicsData physicsData;

    public ChainConveyorPackage(float chainPosition, ItemStack item) {
        this(chainPosition, item, netIdGenerator.incrementAndGet());
    }

    public ChainConveyorPackage(float chainPosition, ItemStack item, int netId) {
        this.chainPosition = chainPosition;
        this.item = item;
        this.netId = netId;
        this.physicsData = null;
    }

    public CompoundTag writeToClient(HolderLookup.Provider registries) {
        CompoundTag tag = this.write(registries);
        tag.putInt("NetID", this.netId);
        return tag;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putFloat("Position", this.chainPosition);
        compoundTag.put("Item", this.item.saveOptional(registries));
        return compoundTag;
    }

    public static ChainConveyorPackage read(CompoundTag compoundTag, HolderLookup.Provider registries) {
        float pos = compoundTag.getFloat("Position");
        ItemStack item = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)compoundTag.getCompound("Item"));
        if (compoundTag.contains("NetID")) {
            return new ChainConveyorPackage(pos, item, compoundTag.getInt("NetID"));
        }
        return new ChainConveyorPackage(pos, item);
    }

    public ChainConveyorPackagePhysicsData physicsData(LevelAccessor level) {
        if (this.physicsData == null) {
            try {
                this.physicsData = (ChainConveyorPackagePhysicsData)((Cache)physicsDataCache.get(level)).get((Object)this.netId, () -> new ChainConveyorPackagePhysicsData(this, this.worldPosition));
                return this.physicsData;
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        ((Cache)physicsDataCache.get(level)).getIfPresent((Object)this.netId);
        return this.physicsData;
    }

    public class ChainConveyorPackagePhysicsData {
        public Vec3 targetPos = null;
        public Vec3 prevTargetPos = null;
        public Vec3 prevPos = null;
        public Vec3 pos = null;
        public Vec3 motion = Vec3.ZERO;
        public int lastTick = AnimationTickHolder.getTicks();
        public float yaw;
        public float prevYaw;
        public boolean flipped;
        public ResourceLocation modelKey;
        public WeakReference<ChainConveyorBlockEntity> beReference;

        public ChainConveyorPackagePhysicsData(ChainConveyorPackage this$0, Vec3 serverPosition) {
        }

        public boolean shouldTick() {
            if (this.lastTick == AnimationTickHolder.getTicks()) {
                return false;
            }
            this.lastTick = AnimationTickHolder.getTicks();
            return true;
        }

        public void setBE(ChainConveyorBlockEntity ccbe) {
            if (this.beReference == null || this.beReference.get() != ccbe) {
                this.beReference = new WeakReference<ChainConveyorBlockEntity>(ccbe);
            }
        }
    }
}
