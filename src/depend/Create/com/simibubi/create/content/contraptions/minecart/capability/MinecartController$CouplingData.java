/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import java.util.UUID;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

private static class MinecartController.CouplingData {
    private UUID mainCartID;
    private UUID connectedCartID;
    private float length;
    private boolean contraption;

    public MinecartController.CouplingData(UUID mainCartID, UUID connectedCartID, float length, boolean contraption) {
        this.mainCartID = mainCartID;
        this.connectedCartID = connectedCartID;
        this.length = length;
        this.contraption = contraption;
    }

    void flip() {
        UUID swap = this.mainCartID;
        this.mainCartID = this.connectedCartID;
        this.connectedCartID = swap;
    }

    CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Main", (Tag)NbtUtils.createUUID((UUID)this.mainCartID));
        nbt.put("Connected", (Tag)NbtUtils.createUUID((UUID)this.connectedCartID));
        nbt.putFloat("Length", this.length);
        nbt.putBoolean("Contraption", this.contraption);
        return nbt;
    }

    static MinecartController.CouplingData read(CompoundTag nbt) {
        UUID mainCartID = NbtUtils.loadUUID((Tag)NBTHelper.getINBT((CompoundTag)nbt, (String)"Main"));
        UUID connectedCartID = NbtUtils.loadUUID((Tag)NBTHelper.getINBT((CompoundTag)nbt, (String)"Connected"));
        float length = nbt.getFloat("Length");
        boolean contraption = nbt.getBoolean("Contraption");
        return new MinecartController.CouplingData(mainCartID, connectedCartID, length, contraption);
    }

    public UUID idOfCart(boolean main) {
        return main ? this.mainCartID : this.connectedCartID;
    }
}
