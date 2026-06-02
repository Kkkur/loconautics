/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.network.packets;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlock;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity;
import dev.simulated_team.simulated.content.items.spring.SpringItem;
import dev.simulated_team.simulated.index.SimBlocks;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public record PlaceSpringPacket(BlockPos parentPos, BlockPos childPos, Direction parentFacing, Direction childFacing, InteractionHand hand) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<PlaceSpringPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("place_spring"));
    public static StreamCodec<RegistryFriendlyByteBuf, PlaceSpringPacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, packet -> packet.hand().ordinal(), (StreamCodec)BlockPos.STREAM_CODEC, PlaceSpringPacket::parentPos, (StreamCodec)BlockPos.STREAM_CODEC, PlaceSpringPacket::childPos, (StreamCodec)Direction.STREAM_CODEC, PlaceSpringPacket::parentFacing, (StreamCodec)Direction.STREAM_CODEC, PlaceSpringPacket::childFacing, (hand, parentPos, childPos, parentFacing, childFacing) -> new PlaceSpringPacket((BlockPos)parentPos, (BlockPos)childPos, (Direction)parentFacing, (Direction)childFacing, hand == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND));

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        Level level = ctx.level();
        BlockPos parentRelative = this.parentPos().relative(this.parentFacing);
        BlockPos childRelative = this.childPos().relative(this.childFacing);
        ItemStack spring = player.getItemInHand(this.hand);
        double distanceSquared = Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)parentRelative.getCenter(), (Position)childRelative.getCenter());
        if (!(spring.getItem() instanceof SpringItem) || distanceSquared > 100.0) {
            return;
        }
        SpringBlockEntity controllerSpring = this.addSpring(level, parentRelative, childRelative, this.parentFacing(), true, (float)distanceSquared);
        SpringBlockEntity partnerSpring = this.addSpring(level, childRelative, parentRelative, this.childFacing(), false, (float)distanceSquared);
        if (controllerSpring == null || partnerSpring == null) {
            level.setBlockAndUpdate(parentRelative, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(childRelative, Blocks.AIR.defaultBlockState());
            return;
        }
        double distance = Math.clamp(Math.sqrt(distanceSquared) + 1.0, 1.0, 9.0);
        controllerSpring.setDesiredLength(distance);
        partnerSpring.setDesiredLength(distance);
        if (!player.hasInfiniteMaterials()) {
            spring.shrink(1);
        }
    }

    private SpringBlockEntity addSpring(Level level, BlockPos placedPos, BlockPos childPos, Direction facing, boolean controller, float distance) {
        BlockState newState = SimBlocks.SPRING.getDefaultState();
        if (level.setBlockAndUpdate(placedPos, (BlockState)newState.setValue((Property)SpringBlock.FACING, (Comparable)facing))) {
            SpringBlockEntity parentSpring = (SpringBlockEntity)level.getBlockEntity(placedPos);
            parentSpring.setController(controller);
            SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)childPos);
            parentSpring.setPartnerPos(childPos, subLevel != null ? subLevel.getUniqueId() : null);
            parentSpring.notifyUpdate();
            return parentSpring;
        }
        return null;
    }
}
