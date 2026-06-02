/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Tuple
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.multiloader.tanks;

import dev.simulated_team.simulated.multiloader.tanks.CFluidType;
import dev.simulated_team.simulated.service.SimFluidService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

public class SingleTank {
    public long amount;
    public final long capacity;
    public CFluidType type = CFluidType.BLANK;

    public SingleTank(int capacity) {
        this.capacity = SimFluidService.INSTANCE.mbToLoaderUnits(capacity);
    }

    public static long calculateInsert(SingleTank tank, CFluidType insertedType, long maxAmount) {
        if (insertedType.equals(tank.type) || tank.type.isBlank()) {
            return Math.min(maxAmount, tank.capacity - tank.amount);
        }
        return 0L;
    }

    public static void applyInsert(SingleTank tank, CFluidType insertedType, long insertedAmount) {
        tank.type = insertedType;
        tank.amount += insertedAmount;
    }

    public static long calculateExtract(SingleTank tank, CFluidType extractedType, long maxAmount) {
        if (extractedType.equals(tank.type)) {
            return Math.min(maxAmount, tank.amount);
        }
        return 0L;
    }

    public static void applyExtract(SingleTank tank, long extractedAmount) {
        tank.amount -= extractedAmount;
        if (tank.amount == 0L) {
            tank.type = CFluidType.BLANK;
        }
    }

    public long insert(CFluidType insertedType, long maxAmount, boolean simulate, @Nullable Runnable beforeApply) {
        long v = SingleTank.calculateInsert(this, insertedType, maxAmount);
        if (!simulate) {
            if (beforeApply != null) {
                beforeApply.run();
            }
            SingleTank.applyInsert(this, insertedType, v);
        }
        return v;
    }

    public final long insert(CFluidType insertedType, long maxAmount, boolean simulate) {
        return this.insert(insertedType, maxAmount, simulate, null);
    }

    public long extract(CFluidType extractedType, long maxAmount, boolean simulate, @Nullable Runnable beforeApply) {
        long v = SingleTank.calculateExtract(this, extractedType, maxAmount);
        if (!simulate) {
            if (beforeApply != null) {
                beforeApply.run();
            }
            SingleTank.applyExtract(this, v);
        }
        return v;
    }

    public final long extract(CFluidType insertedType, long maxAmount, boolean simulate) {
        return this.extract(insertedType, maxAmount, simulate, null);
    }

    public void read(CompoundTag tag) {
        this.amount = tag.getInt("Amount");
        this.type = CFluidType.read(tag.getCompound("Variant"));
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Amount", this.amount);
        tag.put("Variant", (Tag)this.type.write());
        return tag;
    }

    public Tuple<CFluidType, Long> createSnapshot() {
        return new Tuple((Object)this.type, (Object)this.amount);
    }

    public void readSnapshot(CFluidType type, long amount) {
        this.type = type;
        this.amount = amount;
    }
}
