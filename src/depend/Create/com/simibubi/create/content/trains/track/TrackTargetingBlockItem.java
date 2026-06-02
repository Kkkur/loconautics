/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.nullness.NonNullBiFunction
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.CurvedTrackSelectionPacket;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import java.util.List;
import java.util.function.BiConsumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;

public class TrackTargetingBlockItem
extends BlockItem {
    private EdgePointType<?> type;

    public static <T extends Block> NonNullBiFunction<? super T, Item.Properties, TrackTargetingBlockItem> ofType(EdgePointType<?> type) {
        return (b, p) -> new TrackTargetingBlockItem((Block)b, (Item.Properties)p, type);
    }

    public TrackTargetingBlockItem(Block pBlock, Item.Properties pProperties, EdgePointType<?> type) {
        super(pBlock, pProperties);
        this.type = type;
    }

    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown() && stack.has(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS)) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.displayClientMessage((Component)CreateLang.translateDirect("track_target.clear", new Object[0]), true);
            stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS);
            stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_DIRECTION);
            stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
            AllSoundEvents.CONTROLLER_CLICK.play(level, null, (Vec3i)pos, 1.0f, 0.5f);
            return InteractionResult.SUCCESS;
        }
        Block block = state.getBlock();
        if (block instanceof ITrackBlock) {
            ITrackBlock track = (ITrackBlock)block;
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            Vec3 lookAngle = player.getLookAngle();
            boolean front = track.getNearestTrackAxis((BlockGetter)level, pos, state, lookAngle).getSecond() == Direction.AxisDirection.POSITIVE;
            EdgePointType<?> type = this.getType(stack);
            MutableObject result = new MutableObject(null);
            TrackTargetingBlockItem.withGraphLocation(level, pos, front, null, type, (overlap, location) -> result.setValue((Object)overlap));
            if (((OverlapResult)((Object)result.getValue())).feedback != null) {
                player.displayClientMessage((Component)CreateLang.translateDirect(((OverlapResult)((Object)result.getValue())).feedback, new Object[0]).withStyle(ChatFormatting.RED), true);
                AllSoundEvents.DENY.play(level, null, (Vec3i)pos, 0.5f, 1.0f);
                return InteractionResult.FAIL;
            }
            stack.set(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS, (Object)pos);
            stack.set(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_DIRECTION, (Object)front);
            stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
            player.displayClientMessage((Component)CreateLang.translateDirect("track_target.set", new Object[0]), true);
            AllSoundEvents.CONTROLLER_CLICK.play(level, null, (Vec3i)pos, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        if (!stack.has(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS)) {
            player.displayClientMessage((Component)CreateLang.translateDirect("track_target.missing", new Object[0]).withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        CompoundTag blockEntityData = new CompoundTag();
        blockEntityData.putBoolean("TargetDirection", ((Boolean)stack.getOrDefault(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_DIRECTION, (Object)false)).booleanValue());
        BlockPos selectedPos = (BlockPos)stack.get(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS);
        BlockPos placedPos = pos.relative(pContext.getClickedFace(), state.canBeReplaced() ? 0 : 1);
        boolean bezier = stack.has(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
        if (!selectedPos.closerThan((Vec3i)placedPos, bezier ? (double)((Integer)AllConfigs.server().trains.maxTrackPlacementLength.get() + 16) : 16.0)) {
            player.displayClientMessage((Component)CreateLang.translateDirect("track_target.too_far", new Object[0]).withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        if (bezier) {
            BezierTrackPointLocation bezierTrackPointLocation = (BezierTrackPointLocation)stack.get(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
            CompoundTag bezierNbt = new CompoundTag();
            bezierNbt.putInt("Segment", bezierTrackPointLocation.segment());
            bezierNbt.put("Key", NbtUtils.writeBlockPos((BlockPos)bezierTrackPointLocation.curveTarget().subtract((Vec3i)placedPos)));
            blockEntityData.put("Bezier", (Tag)bezierNbt);
        }
        blockEntityData.put("TargetTrack", NbtUtils.writeBlockPos((BlockPos)selectedPos.subtract((Vec3i)placedPos)));
        blockEntityData.putString("id", BuiltInRegistries.ITEM.getKey((Object)stack.getItem()).toString());
        BlockEntity.addEntityType((CompoundTag)blockEntityData, ((IBE)this.getBlock()).getBlockEntityType());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.of((CompoundTag)blockEntityData));
        stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS);
        stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_DIRECTION);
        stack.remove(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
        InteractionResult useOn = super.useOn(pContext);
        stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        if (level.isClientSide || useOn == InteractionResult.FAIL) {
            return useOn;
        }
        ItemStack itemInHand = player.getItemInHand(pContext.getHand());
        if (!itemInHand.isEmpty()) {
            itemInHand.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_POS);
            itemInHand.remove(AllDataComponents.TRACK_TARGETING_ITEM_SELECTED_DIRECTION);
            itemInHand.remove(AllDataComponents.TRACK_TARGETING_ITEM_BEZIER);
        }
        player.displayClientMessage((Component)CreateLang.translateDirect("track_target.success", new Object[0]).withStyle(ChatFormatting.GREEN), true);
        if (this.type == EdgePointType.SIGNAL) {
            AllAdvancements.SIGNAL.awardTo(player);
        }
        return useOn;
    }

    public EdgePointType<?> getType(ItemStack stack) {
        return this.type;
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean useOnCurve(TrackBlockOutline.BezierPointSelection selection, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        TrackBlockEntity be = selection.blockEntity();
        BezierTrackPointLocation loc = selection.loc();
        boolean front = player.getLookAngle().dot(selection.direction()) < 0.0;
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new CurvedTrackSelectionPacket(be.getBlockPos(), loc.curveTarget(), front, loc.segment(), player.getInventory().selected));
        return true;
    }

    public static void withGraphLocation(Level level, BlockPos pos, boolean front, BezierTrackPointLocation targetBezier, EdgePointType<?> type, BiConsumer<OverlapResult, TrackGraphLocation> callback) {
        TrackGraphLocation location;
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof ITrackBlock)) {
            callback.accept(OverlapResult.NO_TRACK, null);
            return;
        }
        ITrackBlock track = (ITrackBlock)block;
        List<Vec3> trackAxes = track.getTrackAxes((BlockGetter)level, pos, state);
        if (targetBezier == null && trackAxes.size() > 1) {
            callback.accept(OverlapResult.JUNCTION, null);
            return;
        }
        Direction.AxisDirection targetDirection = front ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        TrackGraphLocation trackGraphLocation = location = targetBezier != null ? TrackGraphHelper.getBezierGraphLocationAt(level, pos, targetDirection, targetBezier) : TrackGraphHelper.getGraphLocationAt(level, pos, targetDirection, trackAxes.get(0));
        if (location == null) {
            callback.accept(OverlapResult.NO_TRACK, null);
            return;
        }
        Couple nodes = location.edge.map(location.graph::locateNode);
        TrackEdge edge = location.graph.getConnection((Couple<TrackNode>)nodes);
        if (edge == null) {
            return;
        }
        EdgeData edgeData = edge.getEdgeData();
        double edgePosition = location.position;
        for (TrackEdgePoint edgePoint : edgeData.getPoints()) {
            double otherEdgePosition = edgePoint.getLocationOn(edge);
            double distance = Math.abs(edgePosition - otherEdgePosition);
            if (distance > 0.75 || edgePoint.canCoexistWith(type, front) && distance < 0.25) continue;
            callback.accept(OverlapResult.OCCUPIED, location);
            return;
        }
        callback.accept(OverlapResult.VALID, location);
    }

    public static enum OverlapResult {
        VALID,
        OCCUPIED("track_target.occupied"),
        JUNCTION("track_target.no_junctions"),
        NO_TRACK("track_target.invalid");

        public String feedback;

        private OverlapResult() {
        }

        private OverlapResult(String feedback) {
            this.feedback = feedback;
        }
    }
}
