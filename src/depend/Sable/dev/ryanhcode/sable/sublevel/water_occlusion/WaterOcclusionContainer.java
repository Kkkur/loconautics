/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.sublevel.water_occlusion;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinterface.water_occlusion.WaterOcclusionContainerHolder;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionRegion;
import dev.ryanhcode.sable.util.BoundedBitVolume3i;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class WaterOcclusionContainer<T extends WaterOcclusionRegion> {
    protected final Set<T> regions = new ObjectOpenHashSet();
    private final Level level;

    public WaterOcclusionContainer(Level level) {
        this.level = level;
    }

    @Nullable
    public static WaterOcclusionContainer<?> getContainer(Level level) {
        if (level instanceof WaterOcclusionContainerHolder) {
            WaterOcclusionContainerHolder holder = (WaterOcclusionContainerHolder)level;
            return holder.sable$getWaterOcclusionContainer();
        }
        return null;
    }

    public boolean isOccluded(Vec3 location) {
        ActiveSableCompanion helper = Sable.HELPER;
        for (WaterOcclusionRegion region : this.regions) {
            Vec3 localLocation;
            BoundedBitVolume3i bitSet = region.getVolume();
            SubLevel subLevel = helper.getContaining(this.level, (Vec3i)bitSet.getMinBlockPos());
            Vec3 vec3 = localLocation = subLevel != null ? subLevel.logicalPose().transformPositionInverse(location) : location;
            if (!bitSet.getOccupied(Mth.floor((double)localLocation.x), Mth.floor((double)localLocation.y), Mth.floor((double)localLocation.z))) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public T getOccludingRegion(Vec3 location) {
        ActiveSableCompanion helper = Sable.HELPER;
        for (WaterOcclusionRegion region : this.regions) {
            Vec3 localLocation;
            BoundedBitVolume3i bitSet = region.getVolume();
            SubLevel subLevel = helper.getContaining(this.level, (Vec3i)bitSet.getMinBlockPos());
            Vec3 vec3 = localLocation = subLevel != null ? subLevel.logicalPose().transformPositionInverse(location) : location;
            if (!bitSet.getOccupied(Mth.floor((double)localLocation.x), Mth.floor((double)localLocation.y), Mth.floor((double)localLocation.z))) continue;
            return (T)region;
        }
        return null;
    }

    public void markDirty(BlockPos pos) {
        block0: for (WaterOcclusionRegion region : this.regions) {
            BoundedBitVolume3i bitSet = region.getVolume();
            for (Direction direction : Direction.values()) {
                BlockPos rel = pos.relative(direction);
                if (!bitSet.getOccupied(rel.getX(), rel.getY(), rel.getZ())) continue;
                region.markDirty();
                continue block0;
            }
        }
    }

    public abstract void removeRegion(WaterOcclusionRegion var1);

    public abstract WaterOcclusionRegion addRegion(BoundedBitVolume3i var1);

    public Set<T> getRegions() {
        return this.regions;
    }
}
