/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.Affine
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public interface ITrackBlock {
    public Vec3 getUpNormal(BlockGetter var1, BlockPos var2, BlockState var3);

    public List<Vec3> getTrackAxes(BlockGetter var1, BlockPos var2, BlockState var3);

    public Vec3 getCurveStart(BlockGetter var1, BlockPos var2, BlockState var3, Vec3 var4);

    default public int getYOffsetAt(BlockGetter world, BlockPos pos, BlockState state, Vec3 end) {
        return 0;
    }

    public BlockState getBogeyAnchor(BlockGetter var1, BlockPos var2, BlockState var3);

    public boolean trackEquals(BlockState var1, BlockState var2);

    default public BlockState overlay(BlockGetter world, BlockPos pos, BlockState existing, BlockState placed) {
        return existing;
    }

    default public double getElevationAtCenter(BlockGetter world, BlockPos pos, BlockState state) {
        return this.isSlope(world, pos, state) ? 0.5 : 0.0;
    }

    public static Collection<TrackNodeLocation.DiscoveredLocation> walkConnectedTracks(BlockGetter worldIn, TrackNodeLocation location, boolean linear) {
        BlockGetter blockGetter;
        if (location != null && worldIn instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)worldIn;
            blockGetter = sl.getServer().getLevel(location.dimension);
        } else {
            blockGetter = worldIn;
        }
        BlockGetter world = blockGetter;
        ArrayList<TrackNodeLocation.DiscoveredLocation> list = new ArrayList<TrackNodeLocation.DiscoveredLocation>();
        for (BlockPos blockPos : location.allAdjacent()) {
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (!(block instanceof ITrackBlock)) continue;
            ITrackBlock track = (ITrackBlock)block;
            list.addAll(track.getConnected(world, blockPos, blockState, linear, location));
        }
        return list;
    }

    default public Collection<TrackNodeLocation.DiscoveredLocation> getConnected(BlockGetter worldIn, BlockPos pos, BlockState state, boolean linear, @Nullable TrackNodeLocation connectedTo) {
        BlockGetter blockGetter;
        if (connectedTo != null && worldIn instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)worldIn;
            blockGetter = sl.getServer().getLevel(connectedTo.dimension);
        } else {
            blockGetter = worldIn;
        }
        BlockGetter world = blockGetter;
        Vec3 center = Vec3.atBottomCenterOf((Vec3i)pos).add(0.0, this.getElevationAtCenter(world, pos, state), 0.0);
        ArrayList<TrackNodeLocation.DiscoveredLocation> list = new ArrayList<TrackNodeLocation.DiscoveredLocation>();
        TrackShape shape = (TrackShape)((Object)state.getValue(TrackBlock.SHAPE));
        List<Vec3> trackAxes = this.getTrackAxes(world, pos, state);
        trackAxes.forEach(axis -> {
            BiFunction<Double, Boolean, Vec3> offsetFactory = (d, b) -> axis.scale(b != false ? d : -d.doubleValue()).add(center);
            Function<Boolean, ResourceKey<Level>> dimensionFactory = b -> {
                ResourceKey resourceKey;
                if (world instanceof Level) {
                    Level l = (Level)world;
                    resourceKey = l.dimension();
                } else {
                    resourceKey = Level.OVERWORLD;
                }
                return resourceKey;
            };
            Function<Vec3, Integer> yOffsetFactory = v -> this.getYOffsetAt(world, pos, state, (Vec3)v);
            ITrackBlock.addToListIfConnected(connectedTo, list, offsetFactory, b -> shape.getNormal(), dimensionFactory, yOffsetFactory, axis, null, (b, v) -> ITrackBlock.getMaterialSimple(world, v));
        });
        return list;
    }

    public static TrackMaterial getMaterialSimple(BlockGetter world, Vec3 pos) {
        return ITrackBlock.getMaterialSimple(world, pos, TrackMaterial.ANDESITE);
    }

    public static TrackMaterial getMaterialSimple(BlockGetter world, Vec3 pos, TrackMaterial defaultMaterial) {
        Block block;
        if (defaultMaterial == null) {
            defaultMaterial = TrackMaterial.ANDESITE;
        }
        if (world != null && (block = world.getBlockState(BlockPos.containing((Position)pos)).getBlock()) instanceof ITrackBlock) {
            ITrackBlock track = (ITrackBlock)block;
            return track.getMaterial();
        }
        return defaultMaterial;
    }

    public static void addToListIfConnected(@Nullable TrackNodeLocation fromEnd, Collection<TrackNodeLocation.DiscoveredLocation> list, BiFunction<Double, Boolean, Vec3> offsetFactory, Function<Boolean, Vec3> normalFactory, Function<Boolean, ResourceKey<Level>> dimensionFactory, Function<Vec3, Integer> yOffsetFactory, Vec3 axis, BezierConnection viaTurn, BiFunction<Boolean, Vec3, TrackMaterial> materialFactory) {
        Vec3 firstOffset = offsetFactory.apply(0.5, true);
        TrackNodeLocation.DiscoveredLocation firstLocation = new TrackNodeLocation.DiscoveredLocation(dimensionFactory.apply(true), firstOffset).viaTurn(viaTurn).materialA(materialFactory.apply(true, offsetFactory.apply(0.0, true))).materialB(materialFactory.apply(true, offsetFactory.apply(1.0, true))).withNormal(normalFactory.apply(true)).withDirection(axis).withYOffset(yOffsetFactory.apply(firstOffset));
        Vec3 secondOffset = offsetFactory.apply(0.5, false);
        TrackNodeLocation.DiscoveredLocation secondLocation = new TrackNodeLocation.DiscoveredLocation(dimensionFactory.apply(false), secondOffset).viaTurn(viaTurn).materialA(materialFactory.apply(false, offsetFactory.apply(0.0, false))).materialB(materialFactory.apply(false, offsetFactory.apply(1.0, false))).withNormal(normalFactory.apply(false)).withDirection(axis).withYOffset(yOffsetFactory.apply(secondOffset));
        if (!firstLocation.dimension.equals(secondLocation.dimension)) {
            firstLocation.forceNode();
            secondLocation.forceNode();
        }
        boolean skipFirst = false;
        boolean skipSecond = false;
        if (fromEnd != null) {
            boolean equalsFirst = firstLocation.equals((Object)fromEnd);
            boolean equalsSecond = secondLocation.equals((Object)fromEnd);
            if (!equalsFirst && !equalsSecond) {
                return;
            }
            if (equalsFirst) {
                skipFirst = true;
            }
            if (equalsSecond) {
                skipSecond = true;
            }
        }
        if (!skipFirst) {
            list.add(firstLocation);
        }
        if (!skipSecond) {
            list.add(secondLocation);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public <Self extends Affine<Self>> PartialModel prepareTrackOverlay(Affine<Self> var1, BlockGetter var2, BlockPos var3, BlockState var4, BezierTrackPointLocation var5, Direction.AxisDirection var6, TrackTargetingBehaviour.RenderedTrackOverlayType var7);

    @OnlyIn(value=Dist.CLIENT)
    public PartialModel prepareAssemblyOverlay(BlockGetter var1, BlockPos var2, BlockState var3, Direction var4, PoseStack var5);

    default public boolean isSlope(BlockGetter world, BlockPos pos, BlockState state) {
        return this.getTrackAxes((BlockGetter)world, (BlockPos)pos, (BlockState)state).get((int)0).y != 0.0;
    }

    default public Pair<Vec3, Direction.AxisDirection> getNearestTrackAxis(BlockGetter world, BlockPos pos, BlockState state, Vec3 lookVec) {
        Vec3 best = null;
        double bestDiff = Double.MAX_VALUE;
        for (Vec3 vec3 : this.getTrackAxes(world, pos, state)) {
            for (int opposite : Iterate.positiveAndNegative) {
                double distanceTo = vec3.normalize().distanceTo(lookVec.scale((double)opposite));
                if (distanceTo > bestDiff) continue;
                bestDiff = distanceTo;
                best = vec3;
            }
        }
        return Pair.of(best, (Object)(lookVec.dot(best.multiply(1.0, 0.0, 1.0).normalize()) < 0.0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE));
    }

    public TrackMaterial getMaterial();
}
