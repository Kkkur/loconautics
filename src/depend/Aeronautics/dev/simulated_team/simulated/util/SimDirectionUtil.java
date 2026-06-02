/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 */
package dev.simulated_team.simulated.util;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class SimDirectionUtil {
    public static final Direction[] VALUES = Direction.values();
    public static Direction[] X_AXIS_PLANE = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    public static Direction[] Y_AXIS_PLANE = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    public static Direction[] Z_AXIS_PLANE = new Direction[]{Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST};
    public static BlockPos[] CUBIC_OFFSET = (BlockPos[])BlockPos.betweenClosedStream((int)-1, (int)-1, (int)-1, (int)1, (int)1, (int)1).map(BlockPos::immutable).toArray(BlockPos[]::new);

    public static Direction[] getSurroundingDirections(Direction.Axis axis) {
        return switch (axis) {
            default -> throw new MatchException(null, null);
            case Direction.Axis.X -> X_AXIS_PLANE;
            case Direction.Axis.Y -> Y_AXIS_PLANE;
            case Direction.Axis.Z -> Z_AXIS_PLANE;
        };
    }

    public static Direction[] getDirectionsExcept(Direction dirToIgnore) {
        return (Direction[])Arrays.stream(Direction.values()).filter(d -> d != dirToIgnore).toArray(Direction[]::new);
    }

    public static Direction directionFromNormal(Vec3i normal) {
        for (Direction dir : Direction.values()) {
            if (!dir.getNormal().equals((Object)normal)) continue;
            return dir;
        }
        return Direction.UP;
    }
}
