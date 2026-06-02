/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

private static class MinecartController.StallData {
    Vec3 position;
    Vec3 motion;
    float yaw;
    float pitch;

    private MinecartController.StallData() {
    }

    MinecartController.StallData(AbstractMinecart entity) {
        this.position = entity.position();
        this.motion = entity.getDeltaMovement();
        this.yaw = entity.getYRot();
        this.pitch = entity.getXRot();
        this.tick(entity);
    }

    void tick(AbstractMinecart entity) {
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setYRot(this.yaw);
        entity.setXRot(this.pitch);
    }

    void release(AbstractMinecart entity) {
        entity.setDeltaMovement(this.motion);
    }

    CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Pos", (Tag)VecHelper.writeNBT((Vec3)this.position));
        nbt.put("Motion", (Tag)VecHelper.writeNBT((Vec3)this.motion));
        nbt.putFloat("Yaw", this.yaw);
        nbt.putFloat("Pitch", this.pitch);
        return nbt;
    }

    static MinecartController.StallData read(CompoundTag nbt) {
        MinecartController.StallData stallData = new MinecartController.StallData();
        stallData.position = VecHelper.readNBT((ListTag)nbt.getList("Pos", 6));
        stallData.motion = VecHelper.readNBT((ListTag)nbt.getList("Motion", 6));
        stallData.yaw = nbt.getFloat("Yaw");
        stallData.pitch = nbt.getFloat("Pitch");
        return stallData;
    }
}
