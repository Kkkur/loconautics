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
import dev.simulated_team.simulated.content.blocks.merging_glue.MergingGlueBlock;
import dev.simulated_team.simulated.content.blocks.merging_glue.MergingGlueBlockEntity;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.service.SimConfigService;
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

public record PlaceMergingGluePacket(BlockPos parentPos, BlockPos childPos, Direction parentFacing, Direction childFacing, InteractionHand hand) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<PlaceMergingGluePacket> TYPE = new CustomPacketPayload.Type(Simulated.path("place_merging_glue"));
    public static StreamCodec<RegistryFriendlyByteBuf, PlaceMergingGluePacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, packet -> packet.hand().ordinal(), (StreamCodec)BlockPos.STREAM_CODEC, PlaceMergingGluePacket::parentPos, (StreamCodec)BlockPos.STREAM_CODEC, PlaceMergingGluePacket::childPos, (StreamCodec)Direction.STREAM_CODEC, PlaceMergingGluePacket::parentFacing, (StreamCodec)Direction.STREAM_CODEC, PlaceMergingGluePacket::childFacing, (hand, parentPos, childPos, parentFacing, childFacing) -> new PlaceMergingGluePacket((BlockPos)parentPos, (BlockPos)childPos, (Direction)parentFacing, (Direction)childFacing, hand == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND));

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        Level level = ctx.level();
        ItemStack glue = player.getItemInHand(this.hand);
        double distanceSquared = Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)this.parentPos.getCenter(), (Position)this.childPos.getCenter());
        float mergingGlueRange = SimConfigService.INSTANCE.server().assembly.mergingGlueRange.getF();
        if (!glue.is(SimTags.Items.MERGING_GLUE) || distanceSquared > (double)(mergingGlueRange * mergingGlueRange)) {
            return;
        }
        BlockPos parentRelative = this.parentPos().relative(this.parentFacing);
        BlockPos childRelative = this.childPos().relative(this.childFacing);
        SubLevel parentSubLevel = Sable.HELPER.getContaining(level, (Vec3i)parentRelative);
        SubLevel childSubLevel = Sable.HELPER.getContaining(level, (Vec3i)childRelative);
        if (parentSubLevel == null || childSubLevel == null) {
            return;
        }
        MergingGlueBlockEntity controller = this.addMergingGlue(level, parentRelative, childRelative, this.parentFacing(), true, (float)distanceSquared);
        MergingGlueBlockEntity partner = this.addMergingGlue(level, childRelative, parentRelative, this.childFacing(), false, (float)distanceSquared);
        if (controller == null || partner == null) {
            level.setBlockAndUpdate(parentRelative, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(childRelative, Blocks.AIR.defaultBlockState());
            return;
        }
        controller.startControlling(partner);
    }

    private MergingGlueBlockEntity addMergingGlue(Level level, BlockPos placedPos, BlockPos childPos, Direction facing, boolean controller, float distance) {
        BlockState newState = SimBlocks.MERGING_GLUE.getDefaultState();
        if (level.setBlockAndUpdate(placedPos, (BlockState)newState.setValue((Property)MergingGlueBlock.FACING, (Comparable)facing))) {
            MergingGlueBlockEntity parentSpring = (MergingGlueBlockEntity)level.getBlockEntity(placedPos);
            if (parentSpring == null) {
                return null;
            }
            parentSpring.setPartnerPos(childPos);
            parentSpring.notifyUpdate();
            return parentSpring;
        }
        return null;
    }
}
