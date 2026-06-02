/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.PlaceExtendedCurvePacket;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackPlacement;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(value={Dist.CLIENT})
public class TrackBlockItem
extends BlockItem {
    public TrackBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isShiftKeyDown() && this.isFoil(stack)) {
            return TrackBlockItem.clearSelection(stack, level, player);
        }
        return super.use(level, player, usedHand);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        SoundType soundtype;
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        if (player == null) {
            return super.useOn(pContext);
        }
        if (pContext.getHand() == InteractionHand.OFF_HAND) {
            return super.useOn(pContext);
        }
        Vec3 lookAngle = player.getLookAngle();
        if (!this.isFoil(stack)) {
            TrackBlockEntity tbe;
            TrackBlock track;
            Block block = state.getBlock();
            if (block instanceof TrackBlock && (track = (TrackBlock)block).getTrackAxes((BlockGetter)level, pos, state).size() > 1) {
                if (!level.isClientSide) {
                    player.displayClientMessage((Component)CreateLang.translateDirect("track.junction_start", new Object[0]).withStyle(ChatFormatting.RED), true);
                }
                return InteractionResult.SUCCESS;
            }
            block = level.getBlockEntity(pos);
            if (block instanceof TrackBlockEntity && (tbe = (TrackBlockEntity)block).isTilted()) {
                if (!level.isClientSide) {
                    player.displayClientMessage((Component)CreateLang.translateDirect("track.turn_start", new Object[0]).withStyle(ChatFormatting.RED), true);
                }
                return InteractionResult.SUCCESS;
            }
            if (TrackBlockItem.select((LevelAccessor)level, pos, lookAngle, stack)) {
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            return super.useOn(pContext);
        }
        if (player.isShiftKeyDown()) {
            return TrackBlockItem.clearSelection(stack, level, player).getResult();
        }
        boolean placing = !(state.getBlock() instanceof ITrackBlock);
        boolean extend = (Boolean)stack.getOrDefault(AllDataComponents.TRACK_EXTENDED_CURVE, (Object)false);
        stack.remove(AllDataComponents.TRACK_EXTENDED_CURVE);
        if (placing) {
            if (!state.canBeReplaced()) {
                pos = pos.relative(pContext.getClickedFace());
            }
            if ((state = this.getPlacementState(pContext)) == null) {
                return InteractionResult.FAIL;
            }
        }
        ItemStack offhandItem = player.getOffhandItem();
        boolean hasGirder = AllBlocks.METAL_GIRDER.isIn(offhandItem);
        TrackPlacement.PlacementInfo info = TrackPlacement.tryConnect(level, player, pos, state, stack, hasGirder, extend);
        if (info.message != null && !level.isClientSide) {
            player.displayClientMessage((Component)CreateLang.translateDirect(info.message, new Object[0]), true);
        }
        if (!info.valid) {
            AllSoundEvents.DENY.playFrom((Entity)player, 1.0f, 1.0f);
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        stack = player.getMainHandItem();
        if (AllTags.AllBlockTags.TRACKS.matches(stack)) {
            stack.remove(AllDataComponents.TRACK_CONNECTING_FROM);
            stack.remove(AllDataComponents.TRACK_EXTENDED_CURVE);
            player.setItemInHand(pContext.getHand(), stack);
        }
        if ((soundtype = state.getSoundType()) != null) {
            level.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
        }
        return InteractionResult.SUCCESS;
    }

    public static InteractionResultHolder<ItemStack> clearSelection(ItemStack stack, Level level, Player player) {
        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
        } else {
            player.displayClientMessage((Component)CreateLang.translateDirect("track.selection_cleared", new Object[0]), true);
            stack.remove(AllDataComponents.TRACK_CONNECTING_FROM);
        }
        return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide);
    }

    public BlockState getPlacementState(UseOnContext pContext) {
        return this.getPlacementState(this.updatePlacementContext(new BlockPlaceContext(pContext)));
    }

    public static boolean select(LevelAccessor world, BlockPos pos, Vec3 lookVec, ItemStack heldItem) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return false;
        }
        ITrackBlock track = (ITrackBlock)block;
        Pair<Vec3, Direction.AxisDirection> nearestTrackAxis = track.getNearestTrackAxis((BlockGetter)world, pos, blockState, lookVec);
        Vec3 axis = ((Vec3)nearestTrackAxis.getFirst()).scale(nearestTrackAxis.getSecond() == Direction.AxisDirection.POSITIVE ? -1.0 : 1.0);
        Vec3 end = track.getCurveStart((BlockGetter)world, pos, blockState, axis);
        Vec3 normal = track.getUpNormal((BlockGetter)world, pos, blockState).normalize();
        heldItem.set(AllDataComponents.TRACK_CONNECTING_FROM, (Object)new TrackPlacement.ConnectingFrom(pos, axis, normal, end));
        return true;
    }

    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void sendExtenderPacket(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!event.getLevel().isClientSide) {
            return;
        }
        if (!AllTags.AllBlockTags.TRACKS.matches(stack)) {
            return;
        }
        if (Minecraft.getInstance().options.keySprint.isDown()) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new PlaceExtendedCurvePacket(event.getHand() == InteractionHand.MAIN_HAND, true));
        }
    }

    public boolean isFoil(ItemStack stack) {
        return stack.has(AllDataComponents.TRACK_CONNECTING_FROM);
    }
}
