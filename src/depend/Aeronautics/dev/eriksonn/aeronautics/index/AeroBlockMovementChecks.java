/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.BlockMovementChecks
 *  com.simibubi.create.api.contraption.BlockMovementChecks$CheckResult
 *  dev.simulated_team.simulated.index.SimBlockMovementChecks
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import dev.eriksonn.aeronautics.content.blocks.hot_air.envelope.Envelope;
import dev.simulated_team.simulated.index.SimBlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

public class AeroBlockMovementChecks {
    private static BlockMovementChecks.CheckResult isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
        return AeroBlockMovementChecks.isBlockAttachedTowards(state, world, pos, BlockPos.ZERO.relative(direction));
    }

    private static BlockMovementChecks.CheckResult isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, BlockPos direction) {
        if (state.getBlock() instanceof Envelope && world.getBlockState(pos.offset((Vec3i)direction)).getBlock() instanceof Envelope) {
            return BlockMovementChecks.CheckResult.SUCCESS;
        }
        return BlockMovementChecks.CheckResult.PASS;
    }

    @ApiStatus.Internal
    public static void init() {
        BlockMovementChecks.registerAttachedCheck(AeroBlockMovementChecks::isBlockAttachedTowards);
        SimBlockMovementChecks.registerAttachedCheck(AeroBlockMovementChecks::isBlockAttachedTowards);
    }
}
