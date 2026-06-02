/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.network.packets.physics_assembler;

import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import dev.simulated_team.simulated.index.SimSoundEvents;
import foundry.veil.api.network.handler.ClientPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record PhysicsAssemblerFailedPacket(BlockPos pos) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<PhysicsAssemblerFailedPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("assembler_failed"));
    public static StreamCodec<ByteBuf, PhysicsAssemblerFailedPacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, PhysicsAssemblerFailedPacket::pos, PhysicsAssemblerFailedPacket::new);

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
            blockEntity2.clientFlickLeverTo(Sable.HELPER.getContaining(level, (Vec3i)this.pos) != null);
            blockEntity2.setClientHoldLeverInPlace(false);
            SimSoundEvents.ASSEMBLER_FAIL.playAt(level, (Vec3i)this.pos, 1.0f, 1.0f, false);
        }
    }
}
