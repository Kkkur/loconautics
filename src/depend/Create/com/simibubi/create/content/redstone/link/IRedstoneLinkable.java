/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.redstone.link;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;

public interface IRedstoneLinkable {
    public int getTransmittedStrength();

    public void setReceivedStrength(int var1);

    public boolean isListening();

    public boolean isAlive();

    public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey();

    public BlockPos getLocation();
}
