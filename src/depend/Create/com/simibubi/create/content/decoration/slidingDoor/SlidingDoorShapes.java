/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.decoration.slidingDoor;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlidingDoorShapes {
    protected static final VoxelShape SE_AABB = Block.box((double)0.0, (double)0.0, (double)-13.0, (double)3.0, (double)16.0, (double)3.0);
    protected static final VoxelShape ES_AABB = Block.box((double)-13.0, (double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)3.0);
    protected static final VoxelShape NW_AABB = Block.box((double)13.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0, (double)29.0);
    protected static final VoxelShape WN_AABB = Block.box((double)13.0, (double)0.0, (double)13.0, (double)29.0, (double)16.0, (double)16.0);
    protected static final VoxelShape SW_AABB = Block.box((double)13.0, (double)0.0, (double)-13.0, (double)16.0, (double)16.0, (double)3.0);
    protected static final VoxelShape WS_AABB = Block.box((double)13.0, (double)0.0, (double)0.0, (double)29.0, (double)16.0, (double)3.0);
    protected static final VoxelShape NE_AABB = Block.box((double)0.0, (double)0.0, (double)13.0, (double)3.0, (double)16.0, (double)29.0);
    protected static final VoxelShape EN_AABB = Block.box((double)-13.0, (double)0.0, (double)13.0, (double)3.0, (double)16.0, (double)16.0);
    protected static final VoxelShape SE_AABB_FOLD = Block.box((double)0.0, (double)0.0, (double)-3.0, (double)9.0, (double)16.0, (double)3.0);
    protected static final VoxelShape ES_AABB_FOLD = Block.box((double)-3.0, (double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)9.0);
    protected static final VoxelShape NW_AABB_FOLD = Block.box((double)7.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0, (double)19.0);
    protected static final VoxelShape WN_AABB_FOLD = Block.box((double)13.0, (double)0.0, (double)7.0, (double)19.0, (double)16.0, (double)16.0);
    protected static final VoxelShape SW_AABB_FOLD = Block.box((double)7.0, (double)0.0, (double)-3.0, (double)16.0, (double)16.0, (double)3.0);
    protected static final VoxelShape WS_AABB_FOLD = Block.box((double)13.0, (double)0.0, (double)0.0, (double)19.0, (double)16.0, (double)9.0);
    protected static final VoxelShape NE_AABB_FOLD = Block.box((double)0.0, (double)0.0, (double)13.0, (double)9.0, (double)16.0, (double)19.0);
    protected static final VoxelShape EN_AABB_FOLD = Block.box((double)-3.0, (double)0.0, (double)7.0, (double)3.0, (double)16.0, (double)16.0);

    public static VoxelShape get(Direction facing, boolean hinge, boolean fold) {
        if (fold) {
            return switch (facing) {
                case Direction.SOUTH -> {
                    if (hinge) {
                        yield ES_AABB_FOLD;
                    }
                    yield WS_AABB_FOLD;
                }
                case Direction.WEST -> {
                    if (hinge) {
                        yield SW_AABB_FOLD;
                    }
                    yield NW_AABB_FOLD;
                }
                case Direction.NORTH -> {
                    if (hinge) {
                        yield WN_AABB_FOLD;
                    }
                    yield EN_AABB_FOLD;
                }
                default -> hinge ? NE_AABB_FOLD : SE_AABB_FOLD;
            };
        }
        return switch (facing) {
            case Direction.SOUTH -> {
                if (hinge) {
                    yield ES_AABB;
                }
                yield WS_AABB;
            }
            case Direction.WEST -> {
                if (hinge) {
                    yield SW_AABB;
                }
                yield NW_AABB;
            }
            case Direction.NORTH -> {
                if (hinge) {
                    yield WN_AABB;
                }
                yield EN_AABB;
            }
            default -> hinge ? NE_AABB : SE_AABB;
        };
    }
}
