/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.redstone.link.controller;

import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.BlockPos;

static class LinkedControllerServerHandler.ManualFrequencyEntry
extends IntAttached<Couple<RedstoneLinkNetworkHandler.Frequency>>
implements IRedstoneLinkable {
    private BlockPos pos;

    public LinkedControllerServerHandler.ManualFrequencyEntry(BlockPos pos, Couple<RedstoneLinkNetworkHandler.Frequency> second) {
        super(Integer.valueOf(30), second);
        this.pos = pos;
    }

    public void updatePosition(BlockPos pos) {
        this.pos = pos;
        this.setFirst(30);
    }

    @Override
    public int getTransmittedStrength() {
        return this.isAlive() ? 15 : 0;
    }

    @Override
    public boolean isAlive() {
        return (Integer)this.getFirst() > 0;
    }

    @Override
    public BlockPos getLocation() {
        return this.pos;
    }

    @Override
    public void setReceivedStrength(int power) {
    }

    @Override
    public boolean isListening() {
        return false;
    }

    @Override
    public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
        return (Couple)this.getSecond();
    }
}
