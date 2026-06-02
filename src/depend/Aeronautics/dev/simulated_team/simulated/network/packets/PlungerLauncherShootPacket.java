/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItemRenderer;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public class PlungerLauncherShootPacket
implements CustomPacketPayload {
    public static CustomPacketPayload.Type<PlungerLauncherShootPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("plunger_launcher_shoot"));
    public static final StreamCodec<ByteBuf, PlungerLauncherShootPacket> CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.HAND, packet -> packet.hand, PlungerLauncherShootPacket::new);
    protected final InteractionHand hand;

    public PlungerLauncherShootPacket(InteractionHand hand) {
        this.hand = hand;
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PacketContext context) {
        Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();
        if (renderViewEntity == null) {
            return;
        }
        PlungerLauncherItemRenderer.RenderHandler handler = SimulatedClient.PLUNGER_LAUNCHER_RENDER_HANDLER;
        handler.basicShoot(this.hand);
        handler.playSound(this.hand, context.player().position());
    }
}
