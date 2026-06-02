/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.simibubi.create.content.contraptions.actors.roller.PaveTask;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.track.BezierConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TrackPaverV2 {
    public static void pave(PaveTask task, TrackGraph graph, TrackEdge edge, double from, double to) {
        if (edge.isTurn()) {
            TrackPaverV2.paveCurve(task, edge.getTurn(), from, to);
            return;
        }
        Vec3 location1 = edge.node1.getLocation().getLocation();
        Vec3 location2 = edge.node2.getLocation().getLocation();
        Vec3 diff = location2.subtract(location1);
        Vec3 direction = VecHelper.clampComponentWise((Vec3)diff, (float)1.0f);
        int extent = (int)Math.round((to - from) / direction.length());
        double length = edge.getLength();
        BlockPos pos = BlockPos.containing((Position)edge.getPosition(graph, Mth.clamp((double)from, (double)0.0625, (double)(length - 0.0625)) / length).subtract(0.0, diff.y != 0.0 ? 1.0 : 0.5, 0.0));
        TrackPaverV2.paveStraight(task, pos, direction, extent);
    }

    public static void paveStraight(PaveTask task, BlockPos startPos, Vec3 direction, int extent) {
        HashSet<BlockPos> toPlaceOn = new HashSet<BlockPos>();
        Vec3 start = VecHelper.getCenterOf((Vec3i)startPos);
        Vec3 mainNormal = direction.cross(new Vec3(0.0, 1.0, 0.0));
        Vec3 normalizedDirection = direction.normalize();
        boolean isDiagonalTrack = direction.multiply(1.0, 0.0, 1.0).length() > 1.125;
        double r1 = (Double)task.getHorizontalInterval().getFirst();
        int flip = (int)Math.signum(r1);
        double r2 = r1 + (double)flip;
        if (isDiagonalTrack) {
            r1 /= (double)Mth.SQRT_OF_TWO;
            r2 /= (double)Mth.SQRT_OF_TWO;
        }
        int currentOffset = (int)(Math.abs(r1) * 2.0 + 0.5);
        int nextOffset = (int)(Math.abs(r2) * 2.0 + 0.5);
        for (int i = 0; i < extent; ++i) {
            boolean placeSides;
            Vec3 offset = direction.scale((double)i);
            Vec3 mainPos = start.add(offset.x, offset.y, offset.z);
            Vec3 targetVec = mainPos.add(mainNormal.scale((double)(flip * (int)((double)currentOffset / 2.0))));
            if (!isDiagonalTrack) {
                toPlaceOn.add(BlockPos.containing((Position)targetVec));
                continue;
            }
            boolean placeRow = currentOffset % 2 == 0 || nextOffset % 2 == 1;
            boolean bl = placeSides = currentOffset % 2 == 1 || nextOffset % 2 == 0;
            if (placeSides) {
                for (int side : Iterate.positiveAndNegative) {
                    Vec3 sideOffset = normalizedDirection.scale((double)side).add(mainNormal.normalize().scale((double)flip)).scale(0.5);
                    toPlaceOn.add(BlockPos.containing((Position)targetVec.add(sideOffset)));
                }
            }
            if (!placeRow) continue;
            if (Math.abs(currentOffset % 2) == 1) {
                targetVec = mainPos.add(mainNormal.scale((double)(flip * (int)((double)(currentOffset + 1) / 2.0))));
            }
            toPlaceOn.add(BlockPos.containing((Position)targetVec));
        }
        toPlaceOn.forEach(task::put);
    }

    public static void paveCurve(PaveTask task, BezierConnection bc, double from, double to) {
        HashMap<Pair, Double> yLevels = new HashMap<Pair, Double>();
        HashMap<Pair, Double> tLevels = new HashMap<Pair, Double>();
        BlockPos bePosition = (BlockPos)bc.bePositions.getFirst();
        double radius = -((Double)task.getHorizontalInterval().getFirst()).doubleValue();
        double r1 = radius - 0.575;
        double r2 = radius + 0.575;
        double handleLength = bc.getHandleLength();
        Vec3 start = ((Vec3)bc.starts.getFirst()).subtract(Vec3.atLowerCornerOf((Vec3i)bePosition)).add(0.0, 0.1875, 0.0);
        Vec3 end = ((Vec3)bc.starts.getSecond()).subtract(Vec3.atLowerCornerOf((Vec3i)bePosition)).add(0.0, 0.1875, 0.0);
        Vec3 startHandle = ((Vec3)bc.axes.getFirst()).scale(handleLength).add(start);
        Vec3 endHandle = ((Vec3)bc.axes.getSecond()).scale(handleLength).add(end);
        Vec3 startNormal = (Vec3)bc.normals.getFirst();
        Vec3 endNormal = (Vec3)bc.normals.getSecond();
        int segCount = bc.getSegmentCount();
        float[] lut = bc.getStepLUT();
        double localFrom = from / bc.getLength();
        double localTo = to / bc.getLength();
        for (int i = 0; i < segCount; ++i) {
            float t1;
            float t = i == segCount ? 1.0f : (float)i * lut[i] / (float)segCount;
            float f = t1 = i + 1 == segCount ? 1.0f : (float)(i + 1) * lut[i + 1] / (float)segCount;
            if ((double)t1 < localFrom || (double)t > localTo) continue;
            Vec3 vt = VecHelper.bezier((Vec3)start, (Vec3)end, (Vec3)startHandle, (Vec3)endHandle, (float)t);
            Vec3 vNormal = startNormal.equals((Object)endNormal) ? startNormal : VecHelper.slerp((float)t, (Vec3)startNormal, (Vec3)endNormal);
            Vec3 hNormal = vNormal.cross(VecHelper.bezierDerivative((Vec3)start, (Vec3)end, (Vec3)startHandle, (Vec3)endHandle, (float)t).normalize()).normalize();
            vt = vt.add(vNormal.scale((double)-1.175f));
            Vec3 vt1 = VecHelper.bezier((Vec3)start, (Vec3)end, (Vec3)startHandle, (Vec3)endHandle, (float)t1);
            Vec3 vNormal1 = startNormal.equals((Object)endNormal) ? startNormal : VecHelper.slerp((float)t1, (Vec3)startNormal, (Vec3)endNormal);
            Vec3 hNormal1 = vNormal1.cross(VecHelper.bezierDerivative((Vec3)start, (Vec3)end, (Vec3)startHandle, (Vec3)endHandle, (float)t1).normalize()).normalize();
            vt1 = vt1.add(vNormal1.scale((double)-1.175f));
            Vec3 a3 = vt.add(hNormal.scale(r2));
            Vec3 b3 = vt1.add(hNormal1.scale(r2));
            Vec3 c3 = vt1.add(hNormal1.scale(r1));
            Vec3 d3 = vt.add(hNormal.scale(r1));
            Vec2 a = TrackPaverV2.vec2(a3);
            Vec2 b = TrackPaverV2.vec2(b3);
            Vec2 c = TrackPaverV2.vec2(c3);
            Vec2 d = TrackPaverV2.vec2(d3);
            AABB aabb = new AABB(a3, b3).minmax(new AABB(c3, d3));
            double y = vt.add((Vec3)vt1).y / 2.0;
            int scanX = Mth.floor((double)aabb.minX);
            while ((double)scanX <= aabb.maxX) {
                int scanZ = Mth.floor((double)aabb.minZ);
                while ((double)scanZ <= aabb.maxZ) {
                    Pair key;
                    Vec2 p = new Vec2((float)scanX + 0.5f, (float)scanZ + 0.5f);
                    if ((TrackPaverV2.isInTriangle(a, b, c, p) || TrackPaverV2.isInTriangle(a, c, d, p)) && (!yLevels.containsKey(key = Pair.of((Object)scanX, (Object)scanZ)) || (Double)yLevels.get(key) > y)) {
                        yLevels.put(key, y);
                        tLevels.put(key, (double)(t + t1) / 2.0);
                    }
                    ++scanZ;
                }
                ++scanX;
            }
        }
        for (Map.Entry entry : yLevels.entrySet()) {
            double yValue = (Double)entry.getValue();
            int floor = Mth.floor((double)yValue);
            BlockPos targetPos = new BlockPos(((Integer)((Pair)entry.getKey()).getFirst()).intValue(), floor, ((Integer)((Pair)entry.getKey()).getSecond()).intValue()).offset((Vec3i)bePosition);
            task.put(targetPos.getX(), targetPos.getZ(), (float)targetPos.getY() + (yValue - (double)floor >= 0.5 ? 0.5f : 0.0f));
        }
    }

    private static Vec2 vec2(Vec3 vec3) {
        return new Vec2((float)vec3.x, (float)vec3.z);
    }

    private static boolean isInTriangle(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        float pcx = p.x - c.x;
        float pcy = p.y - c.y;
        float cbx = c.x - b.x;
        float bcy = b.y - c.y;
        float d = bcy * (a.x - c.x) + cbx * (a.y - c.y);
        float s = bcy * pcx + cbx * pcy;
        float t = (c.y - a.y) * pcx + (a.x - c.x) * pcy;
        return d < 0.0f ? s <= 0.0f && t <= 0.0f && s + t >= d : s >= 0.0f && t >= 0.0f && s + t <= d;
    }

    public static double lineToPointDiff2d(Vec3 l1, Vec3 l2, Vec3 p) {
        return Math.abs((l2.x - l1.x) * (l1.z - p.z) - (l1.x - p.x) * (l2.z - l1.z));
    }
}
