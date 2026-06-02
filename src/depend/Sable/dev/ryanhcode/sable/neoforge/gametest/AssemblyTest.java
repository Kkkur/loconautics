/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  net.minecraft.core.BlockPos
 *  net.minecraft.gametest.framework.GameTest
 *  net.minecraft.gametest.framework.GameTestAssertPosException
 *  net.minecraft.gametest.framework.GameTestHelper
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.gametest.GameTestHolder
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3i
 */
package dev.ryanhcode.sable.neoforge.gametest;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

@GameTestHolder(value="sable")
public final class AssemblyTest {
    @GameTest(template="brittlebreak")
    public static void testBrittleBreaking(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer(level);
        if (plotContainer == null) {
            throw new IllegalStateException("Plot container not found in level");
        }
        SubLevelPhysicsSystem physicsSystem = plotContainer.physicsSystem();
        if (physicsSystem == null) {
            throw new IllegalStateException("Plot container does not have physics");
        }
        BlockPos min = helper.absolutePos(new BlockPos(0, 1, 0));
        BlockPos max = helper.absolutePos(new BlockPos(2, 3, 2));
        BoundingBox3i bounds = new BoundingBox3i(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
        ArrayList<BlockState> expectedStates = new ArrayList<BlockState>(bounds.volume());
        for (BlockPos pos : BlockPos.betweenClosed((BlockPos)min, (BlockPos)max)) {
            expectedStates.add(level.getBlockState(pos));
        }
        ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, min, BlockPos.betweenClosed((BlockPos)min, (BlockPos)max), (BoundingBox3ic)bounds);
        physicsSystem.getPipeline().teleport(subLevel, (Vector3dc)new Vector3d((double)min.getX() + (double)(1 + max.getX() - min.getX()) / 2.0, (double)min.getY() + (double)(1 + max.getY() - min.getY()) / 2.0, (double)min.getZ() + (double)(1 + max.getZ() - min.getZ()) / 2.0), (Quaterniondc)helper.getTestRotation().rotation().transformation().getNormalizedRotation(new Quaterniond()));
        helper.runAtTickTime(10L, () -> {
            Vector3i expectedSize;
            ServerLevel plot = subLevel.getLevel();
            BoundingBox3ic sublevelBounds = subLevel.getPlot().getBoundingBox();
            Vector3i actualSize = sublevelBounds.size(new Vector3i());
            if (actualSize.equals((Object)(expectedSize = bounds.size(new Vector3i())))) {
                int i = 0;
                for (BlockPos pos : BlockPos.betweenClosed((int)sublevelBounds.minX(), (int)sublevelBounds.minY(), (int)sublevelBounds.minZ(), (int)sublevelBounds.maxX(), (int)sublevelBounds.maxY(), (int)sublevelBounds.maxZ())) {
                    BlockState expected = (BlockState)expectedStates.get(i);
                    if (!plot.getBlockState(pos).equals(expected)) {
                        throw new GameTestAssertPosException("Expected %s".formatted(expected.getBlock().getName().getString()), pos, pos, helper.getTick());
                    }
                    ++i;
                }
                helper.succeed();
            } else {
                helper.fail("Expected %dx%dx%d region, got %dx%dx%d".formatted(expectedSize.x(), expectedSize.y(), expectedSize.z(), actualSize.x(), actualSize.y(), actualSize.z()));
            }
        });
    }
}
