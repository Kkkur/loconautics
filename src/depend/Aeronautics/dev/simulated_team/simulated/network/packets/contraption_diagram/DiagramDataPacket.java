/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup$PointForce
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package dev.simulated_team.simulated.network.packets.contraption_diagram;

import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramScreen;
import dev.simulated_team.simulated.util.SimCodecUtil;
import foundry.veil.api.network.handler.ClientPacketContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DiagramDataPacket(Map<ForceGroup, List<QueuedForceGroup.PointForce>> forces, double mass) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<DiagramDataPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("diagram_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DiagramDataPacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.map(Object2ObjectOpenHashMap::new, SimCodecUtil.STREAM_FORCE_GROUP, (StreamCodec)SimCodecUtil.STREAM_POINT_FORCE.apply(ByteBufCodecs.list())), DiagramDataPacket::forces, (StreamCodec)ByteBufCodecs.DOUBLE, DiagramDataPacket::mass, DiagramDataPacket::new);

    public void handle(ClientPacketContext context) {
        DiagramDataPacket.handle(this);
    }

    private static void handle(DiagramDataPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen instanceof DiagramScreen) {
            DiagramScreen diagramScreen = (DiagramScreen)screen;
            diagramScreen.updateData(packet);
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
