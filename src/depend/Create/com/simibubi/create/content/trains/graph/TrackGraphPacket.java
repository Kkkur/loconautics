/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.graph.TrackGraph;
import java.util.UUID;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class TrackGraphPacket
implements ClientboundPacketPayload {
    public UUID graphId;
    public int netId;
    public boolean packetDeletesGraph;

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        this.handle(CreateClient.RAILWAYS, CreateClient.RAILWAYS.getOrCreateGraph(this.graphId, this.netId));
    }

    protected abstract void handle(GlobalRailwayManager var1, TrackGraph var2);
}
