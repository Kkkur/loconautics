/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.contraption;

import com.simibubi.create.impl.contraption.BlockMovementChecksImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMovementChecks {
    public static void registerMovementNecessaryCheck(MovementNecessaryCheck check) {
        BlockMovementChecksImpl.registerMovementNecessaryCheck(check);
    }

    public static void registerMovementAllowedCheck(MovementAllowedCheck check) {
        BlockMovementChecksImpl.registerMovementAllowedCheck(check);
    }

    public static void registerBrittleCheck(BrittleCheck check) {
        BlockMovementChecksImpl.registerBrittleCheck(check);
    }

    public static void registerAttachedCheck(AttachedCheck check) {
        BlockMovementChecksImpl.registerAttachedCheck(check);
    }

    public static void registerNotSupportiveCheck(NotSupportiveCheck check) {
        BlockMovementChecksImpl.registerNotSupportiveCheck(check);
    }

    public static boolean isMovementNecessary(BlockState state, Level world, BlockPos pos) {
        return BlockMovementChecksImpl.isMovementNecessary(state, world, pos);
    }

    public static boolean isMovementAllowed(BlockState state, Level world, BlockPos pos) {
        return BlockMovementChecksImpl.isMovementAllowed(state, world, pos);
    }

    public static boolean isBrittle(BlockState state) {
        return BlockMovementChecksImpl.isBrittle(state);
    }

    public static boolean isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
        return BlockMovementChecksImpl.isBlockAttachedTowards(state, world, pos, direction);
    }

    public static boolean isNotSupportive(BlockState state, Direction facing) {
        return BlockMovementChecksImpl.isNotSupportive(state, facing);
    }

    private BlockMovementChecks() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }

    @FunctionalInterface
    public static interface MovementNecessaryCheck {
        public CheckResult isMovementNecessary(BlockState var1, Level var2, BlockPos var3);
    }

    @FunctionalInterface
    public static interface MovementAllowedCheck {
        public CheckResult isMovementAllowed(BlockState var1, Level var2, BlockPos var3);
    }

    @FunctionalInterface
    public static interface BrittleCheck {
        public CheckResult isBrittle(BlockState var1);
    }

    @FunctionalInterface
    public static interface AttachedCheck {
        public CheckResult isBlockAttachedTowards(BlockState var1, Level var2, BlockPos var3, Direction var4);
    }

    @FunctionalInterface
    public static interface NotSupportiveCheck {
        public CheckResult isNotSupportive(BlockState var1, Direction var2);
    }

    public static enum CheckResult {
        SUCCESS,
        FAIL,
        PASS;


        public boolean toBoolean() {
            if (this == PASS) {
                throw new IllegalStateException("PASS does not have a boolean value");
            }
            return this == SUCCESS;
        }

        public static CheckResult of(boolean b) {
            return b ? SUCCESS : FAIL;
        }

        public static CheckResult of(Boolean b) {
            return b == null ? PASS : (b != false ? SUCCESS : FAIL);
        }
    }
}
