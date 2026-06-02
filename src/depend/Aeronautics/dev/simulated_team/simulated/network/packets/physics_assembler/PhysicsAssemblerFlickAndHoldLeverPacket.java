/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.network.packets.physics_assembler;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import foundry.veil.api.network.handler.ClientPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record PhysicsAssemblerFlickAndHoldLeverPacket(BlockPos pos, boolean flicked) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<PhysicsAssemblerFlickAndHoldLeverPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("flick_assembler_lever"));
    public static StreamCodec<ByteBuf, PhysicsAssemblerFlickAndHoldLeverPacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, PhysicsAssemblerFlickAndHoldLeverPacket::pos, (StreamCodec)ByteBufCodecs.BOOL, PhysicsAssemblerFlickAndHoldLeverPacket::flicked, PhysicsAssemblerFlickAndHoldLeverPacket::new);

    @NotNull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ClientPacketContext context) {
        Level level = context.level();
        assert (level != null);
        BlockEntity blockEntity = level.getBlockEntity(this.pos);
        if (blockEntity instanceof PhysicsAssemblerBlockEntity) {
            PhysicsAssemblerBlockEntity blockEntity2 = (PhysicsAssemblerBlockEntity)blockEntity;
            blockEntity2.clientFlickLeverTo(this.flicked);
            blockEntity2.setClientHoldLeverInPlace(true);
        }
    }
}
