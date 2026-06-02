/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.event.InputEvent$InteractionKeyMappingTriggered
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocationPacket;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.InputEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class TrainRelocator {
    static WeakReference<CarriageContraptionEntity> hoveredEntity = new WeakReference<Object>(null);
    static UUID relocatingTrain;
    static BlockPos relocatingOrigin;
    static int relocatingEntityId;
    static BlockPos lastHoveredPos;
    static BezierTrackPointLocation lastHoveredBezierSegment;
    static Boolean lastHoveredResult;
    static List<Vec3> toVisualise;

    public static boolean isRelocating() {
        return relocatingTrain != null;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void onClicked(InputEvent.InteractionKeyMappingTriggered event) {
        if (relocatingTrain == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        if (!player.canInteractWithBlock(relocatingOrigin, 24.0) || player.isShiftKeyDown()) {
            relocatingTrain = null;
            player.displayClientMessage((Component)CreateLang.translateDirect("train.relocate.abort", new Object[0]).withStyle(ChatFormatting.RED), true);
            return;
        }
        if (player.isPassenger()) {
            return;
        }
        if (mc.level == null) {
            return;
        }
        Train relocating = TrainRelocator.getRelocating((LevelAccessor)mc.level);
        if (relocating != null) {
            Boolean relocate = TrainRelocator.relocateClient(relocating, false);
            if (relocate != null && relocate.booleanValue()) {
                relocatingTrain = null;
            }
            if (relocate != null) {
                event.setCanceled(true);
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public static Boolean relocateClient(Train relocating, boolean simulate) {
        BlockState blockState;
        Block block;
        TrackBlockOutline.BezierPointSelection bezierSelection;
        Vec3 offset;
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult)) {
            return null;
        }
        BlockHitResult blockhit = (BlockHitResult)hitResult;
        BlockPos blockPos = blockhit.getBlockPos();
        BezierTrackPointLocation hoveredBezier = null;
        boolean upsideDown = relocating.carriages.get(0).leadingBogey().isUpsideDown();
        Vec3 vec3 = offset = upsideDown ? new Vec3(0.0, -0.5, 0.0) : Vec3.ZERO;
        if (simulate && toVisualise != null && lastHoveredResult != null) {
            for (int i = 0; i < toVisualise.size() - 1; ++i) {
                Vec3 vec1 = toVisualise.get(i).add(offset);
                Vec3 vec2 = toVisualise.get(i + 1).add(offset);
                Outliner.getInstance().showLine((Object)Pair.of((Object)relocating, (Object)i), vec1.add(0.0, (double)-0.925f, 0.0), vec2.add(0.0, (double)-0.925f, 0.0)).colored(lastHoveredResult != false || i != toVisualise.size() - 2 ? 9817409 : 15359019).disableLineNormals().lineWidth(i % 2 == 1 ? 0.16666667f : 0.25f);
            }
        }
        if ((bezierSelection = TrackBlockOutline.result) != null) {
            blockPos = bezierSelection.blockEntity().getBlockPos();
            hoveredBezier = bezierSelection.loc();
        }
        if (simulate) {
            if (lastHoveredPos != null && lastHoveredPos.equals((Object)blockPos) && Objects.equals(lastHoveredBezierSegment, hoveredBezier)) {
                return lastHoveredResult;
            }
            lastHoveredPos = blockPos;
            lastHoveredBezierSegment = hoveredBezier;
            toVisualise = null;
        }
        if (!((block = (blockState = mc.level.getBlockState(blockPos)).getBlock()) instanceof ITrackBlock)) {
            lastHoveredResult = null;
            return null;
        }
        ITrackBlock track = (ITrackBlock)block;
        Vec3 lookAngle = mc.player.getLookAngle();
        boolean direction = bezierSelection != null && lookAngle.dot(bezierSelection.direction()) < 0.0;
        boolean result = TrainRelocator.relocate(relocating, (Level)mc.level, blockPos, hoveredBezier, direction, lookAngle, true);
        if (!simulate && result) {
            relocating.carriages.forEach(c -> c.forEachPresentEntity(e -> {
                e.nonDamageTicks = 10;
            }));
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new TrainRelocationPacket(relocatingTrain, blockPos, lookAngle, relocatingEntityId, direction, hoveredBezier));
        }
        lastHoveredResult = result;
        return lastHoveredResult;
    }

    public static boolean relocate(Train train, Level level, BlockPos pos, BezierTrackPointLocation bezier, boolean bezierDirection, Vec3 lookAngle, boolean simulate) {
        TrackGraphLocation graphLocation;
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return false;
        }
        ITrackBlock track = (ITrackBlock)block;
        Pair<Vec3, Direction.AxisDirection> nearestTrackAxis = track.getNearestTrackAxis((BlockGetter)level, pos, blockState, lookAngle);
        TrackGraphLocation trackGraphLocation = bezier != null ? TrackGraphHelper.getBezierGraphLocationAt(level, pos, bezierDirection ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE, bezier) : (graphLocation = TrackGraphHelper.getGraphLocationAt(level, pos, (Direction.AxisDirection)nearestTrackAxis.getSecond(), (Vec3)nearestTrackAxis.getFirst()));
        if (graphLocation == null) {
            return false;
        }
        TrackGraph graph = graphLocation.graph;
        TrackNode node1 = graph.locateNode((TrackNodeLocation)((Object)graphLocation.edge.getFirst()));
        TrackNode node2 = graph.locateNode((TrackNodeLocation)((Object)graphLocation.edge.getSecond()));
        TrackEdge edge = graph.getConnectionsFrom(node1).get(node2);
        if (edge == null) {
            return false;
        }
        TravellingPoint probe = new TravellingPoint(node1, node2, edge, graphLocation.position, false);
        TravellingPoint.IEdgePointListener ignoreSignals = probe.ignoreEdgePoints();
        TravellingPoint.ITurnListener ignoreTurns = probe.ignoreTurns();
        ArrayList recordedLocations = new ArrayList();
        ArrayList recordedVecs = new ArrayList();
        Consumer<TravellingPoint> recorder = tp -> {
            recordedLocations.add(Pair.of((Object)Couple.create((Object)tp.node1, (Object)tp.node2), (Object)tp.position));
            recordedVecs.add(tp.getPosition(graph));
        };
        TravellingPoint.ITrackSelector steer = probe.steer(TravellingPoint.SteerDirection.NONE, track.getUpNormal((BlockGetter)level, pos, blockState));
        MutableBoolean blocked = new MutableBoolean(false);
        MutableBoolean portal = new MutableBoolean(false);
        MutableInt blockingIndex = new MutableInt(0);
        train.forEachTravellingPointBackwards((tp, d) -> {
            if (blocked.booleanValue()) {
                return;
            }
            probe.travel(graph, (double)d, steer, ignoreSignals, ignoreTurns, $ -> {
                portal.setTrue();
                return true;
            });
            recorder.accept(probe);
            if (probe.blocked || portal.booleanValue()) {
                blocked.setTrue();
                return;
            }
            blockingIndex.increment();
        });
        if (level.isClientSide && simulate && !recordedVecs.isEmpty()) {
            toVisualise = new ArrayList<Vec3>();
            toVisualise.add((Vec3)recordedVecs.get(0));
        }
        for (int i = 0; i < recordedVecs.size() - 1; ++i) {
            boolean collided;
            Vec3 vec1 = (Vec3)recordedVecs.get(i);
            Vec3 vec2 = (Vec3)recordedVecs.get(i + 1);
            boolean blocking = i >= blockingIndex.intValue() - 1;
            boolean bl = collided = !blocked.booleanValue() && train.findCollidingTrain(level, vec1, vec2, (ResourceKey<Level>)level.dimension()) != null;
            if (level.isClientSide && simulate) {
                toVisualise.add(vec2);
            }
            if (!collided && !blocking) continue;
            return false;
        }
        if (blocked.booleanValue()) {
            return false;
        }
        if (simulate) {
            return true;
        }
        train.leaveStation();
        train.derailed = false;
        train.navigation.waitingForSignal = null;
        train.occupiedSignalBlocks.clear();
        train.graph = graph;
        train.speed = 0.0;
        train.migratingPoints.clear();
        train.cancelStall();
        if (train.navigation.destination != null) {
            train.navigation.cancelNavigation();
        }
        train.forEachTravellingPoint(tp -> {
            Pair last = (Pair)recordedLocations.remove(recordedLocations.size() - 1);
            tp.node1 = (TrackNode)((Couple)last.getFirst()).getFirst();
            tp.node2 = (TrackNode)((Couple)last.getFirst()).getSecond();
            tp.position = (Double)last.getSecond();
            tp.edge = graph.getConnectionsFrom(tp.node1).get(tp.node2);
        });
        for (Carriage carriage : train.carriages) {
            carriage.updateContraptionAnchors();
        }
        train.status.successfulMigration();
        train.collectInitiallyOccupiedSignalBlocks();
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void visualise(Train train, int i, Vec3 v1, Vec3 v2, boolean valid) {
        Outliner.getInstance().showLine((Object)Pair.of((Object)train, (Object)i), v1.add(0.0, (double)-0.825f, 0.0), v2.add(0.0, (double)-0.825f, 0.0)).colored(valid ? 9817409 : 15359019).disableLineNormals().lineWidth(i % 2 == 1 ? 0.16666667f : 0.25f);
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        if (player.isPassenger()) {
            return;
        }
        if (mc.level == null) {
            return;
        }
        if (relocatingTrain != null) {
            AbstractContraptionEntity ce;
            Train relocating = TrainRelocator.getRelocating((LevelAccessor)mc.level);
            if (relocating == null) {
                relocatingTrain = null;
                return;
            }
            Entity entity = mc.level.getEntity(relocatingEntityId);
            if (entity instanceof AbstractContraptionEntity && Math.abs((ce = (AbstractContraptionEntity)entity).getPosition(0.0f).subtract(ce.getPosition(1.0f)).lengthSqr()) > 9.765625E-4) {
                player.displayClientMessage((Component)CreateLang.translateDirect("train.cannot_relocate_moving", new Object[0]).withStyle(ChatFormatting.RED), true);
                relocatingTrain = null;
                return;
            }
            if (!AllItems.WRENCH.isIn(player.getMainHandItem())) {
                player.displayClientMessage((Component)CreateLang.translateDirect("train.relocate.abort", new Object[0]).withStyle(ChatFormatting.RED), true);
                relocatingTrain = null;
                return;
            }
            if (!player.canInteractWithBlock(relocatingOrigin, 24.0)) {
                player.displayClientMessage((Component)CreateLang.translateDirect("train.relocate.too_far", new Object[0]).withStyle(ChatFormatting.RED), true);
                return;
            }
            Boolean success = TrainRelocator.relocateClient(relocating, true);
            if (success == null) {
                player.displayClientMessage((Component)CreateLang.translateDirect("train.relocate", relocating.name), true);
            } else if (success.booleanValue()) {
                player.displayClientMessage((Component)CreateLang.translateDirect("train.relocate.valid", new Object[0]).withStyle(ChatFormatting.GREEN), true);
            } else {
                player.displayClientMessage((Component)CreateLang.translateDirect("train.relocate.invalid", new Object[0]).withStyle(ChatFormatting.RED), true);
            }
            return;
        }
        Couple<Vec3> rayInputs = ContraptionHandlerClient.getRayInputs(player);
        Vec3 origin = (Vec3)rayInputs.getFirst();
        Vec3 target = (Vec3)rayInputs.getSecond();
        CarriageContraptionEntity currentEntity = (CarriageContraptionEntity)((Object)hoveredEntity.get());
        if (currentEntity != null) {
            if (ContraptionHandlerClient.rayTraceContraption(origin, target, currentEntity) != null) {
                return;
            }
            hoveredEntity = new WeakReference<Object>(null);
        }
        AABB aabb = new AABB(origin, target);
        List intersectingContraptions = mc.level.getEntitiesOfClass(CarriageContraptionEntity.class, aabb);
        for (CarriageContraptionEntity contraptionEntity : intersectingContraptions) {
            if (ContraptionHandlerClient.rayTraceContraption(origin, target, contraptionEntity) == null) continue;
            hoveredEntity = new WeakReference<CarriageContraptionEntity>(contraptionEntity);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public static boolean carriageWrenched(Vec3 vec3, CarriageContraptionEntity entity) {
        Train train = TrainRelocator.getTrainFromEntity(entity);
        if (train == null) {
            return false;
        }
        relocatingOrigin = BlockPos.containing((Position)vec3);
        relocatingTrain = train.id;
        relocatingEntityId = entity.getId();
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static boolean addToTooltip(List<Component> tooltip, boolean shiftKeyDown) {
        Train train = TrainRelocator.getTrainFromEntity((CarriageContraptionEntity)((Object)hoveredEntity.get()));
        if (train != null && train.derailed) {
            TooltipHelper.addHint(tooltip, "hint.derailed_train", new Object[0]);
            return true;
        }
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    private static Train getRelocating(LevelAccessor level) {
        return relocatingTrain == null ? null : Create.RAILWAYS.sided((LevelAccessor)level).trains.get(relocatingTrain);
    }

    private static Train getTrainFromEntity(CarriageContraptionEntity carriageContraptionEntity) {
        if (carriageContraptionEntity == null) {
            return null;
        }
        Carriage carriage = carriageContraptionEntity.getCarriage();
        if (carriage == null) {
            return null;
        }
        return carriage.train;
    }
}
