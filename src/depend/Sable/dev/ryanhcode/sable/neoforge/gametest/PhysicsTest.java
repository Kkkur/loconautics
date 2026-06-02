/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.gametest.framework.GameTest
 *  net.minecraft.gametest.framework.GameTestAssertPosException
 *  net.minecraft.gametest.framework.GameTestHelper
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.neoforged.neoforge.gametest.GameTestHolder
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.neoforge.gametest;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.neoforge.gametest.SableTestHelper;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@GameTestHolder(value="sable")
public final class PhysicsTest {
    @GameTest(template="continuouscollision")
    public static void testContinuousCollision(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer(level);
        if (plotContainer == null) {
            throw new IllegalStateException("Plot container not found in level");
        }
        SubLevelPhysicsSystem physicsSystem = plotContainer.physicsSystem();
        if (physicsSystem == null) {
            throw new IllegalStateException("Plot container does not have physics");
        }
        ServerSubLevel subLevel = SableTestHelper.spawnSingleBlockSubLevel(plotContainer, (Vector3dc)SableTestHelper.absolutePosition(helper, (Vector3dc)new Vector3d(2.5, 4.0, 1.5)), Blocks.GLASS.defaultBlockState());
        RigidBodyHandle handle = physicsSystem.getPhysicsHandle(subLevel);
        Vector3d impulse = SableTestHelper.absoluteDirection(helper, (Vector3dc)new Vector3d(0.0, 10.0, 20.0));
        helper.startSequence().thenExecuteAfter(10, () -> handle.applyLinearImpulse((Vector3dc)impulse)).thenExecuteFor(40, () -> {
            Vector3d globalPos = subLevel.logicalPose().position();
            Vector3d localPos = SableTestHelper.localPosition(helper, (Vector3dc)globalPos);
            if (localPos.z >= 9.0 || !SableTestHelper.isInBounds(helper, (Vector3dc)globalPos)) {
                helper.fail("Sublevel passed through wall", BlockPos.containing((double)localPos.x, (double)localPos.y, (double)localPos.z));
            }
        }).thenSucceed();
    }

    @GameTest(template="gravity", required=false)
    public static void testGravity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer(level);
        if (plotContainer == null) {
            throw new IllegalStateException("Plot container not found in level");
        }
        SubLevelPhysicsSystem physicsSystem = plotContainer.physicsSystem();
        if (physicsSystem == null) {
            throw new IllegalStateException("Plot container does not have physics");
        }
        Vector3d spawnPos = SableTestHelper.absolutePosition(helper, (Vector3dc)new Vector3d(2.5, 12.0, 2.5));
        ServerSubLevel subLevel = SableTestHelper.spawnSingleBlockSubLevel(plotContainer, (Vector3dc)spawnPos, Blocks.DIAMOND_BLOCK.defaultBlockState());
        helper.runAfterDelay(20L, () -> PhysicsTest.lambda$testGravity$2(subLevel, helper, (Vector3dc)spawnPos, physicsSystem));
    }

    @GameTest(template="snag", attempts=10, requiredSuccesses=10, required=false)
    public static void testSnag(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer(level);
        if (plotContainer == null) {
            throw new IllegalStateException("Plot container not found in level");
        }
        SubLevelPhysicsSystem physicsSystem = plotContainer.physicsSystem();
        if (physicsSystem == null) {
            throw new IllegalStateException("Plot container does not have physics");
        }
        Vector3d spawnPos = SableTestHelper.absolutePosition(helper, (Vector3dc)new Vector3d(13.0, 3.5, 3.5));
        ServerSubLevel subLevel = SableTestHelper.spawnSingleBlockSubLevel(plotContainer, (Vector3dc)spawnPos, Blocks.DIAMOND_BLOCK.defaultBlockState());
        RigidBodyHandle handle = physicsSystem.getPhysicsHandle(subLevel);
        Vector3d impulse = SableTestHelper.absoluteDirection(helper, (Vector3dc)new Vector3d(-60.0, 0.0, 0.0));
        helper.startSequence().thenExecuteAfter(10, () -> handle.applyLinearImpulse((Vector3dc)impulse)).thenExecuteFor(40, () -> {
            Vector3d globalPos = subLevel.logicalPose().position();
            Vector3d localPos = SableTestHelper.localPosition(helper, (Vector3dc)globalPos);
            if (localPos.x <= 9.0 && SableTestHelper.isInBounds(helper, (Vector3dc)globalPos)) {
                helper.succeed();
            }
        }).thenFail(() -> {
            Vector3d position = subLevel.logicalPose().position();
            BlockPos globalPos = BlockPos.containing((double)position.x(), (double)position.y(), (double)position.z());
            return new GameTestAssertPosException("Sub-level got stuck", globalPos, helper.relativePos(globalPos), helper.getTick());
        });
    }

    private static /* synthetic */ void lambda$testGravity$2(ServerSubLevel subLevel, GameTestHelper helper, Vector3dc spawnPos, SubLevelPhysicsSystem physicsSystem) {
        Vector3d delta;
        RigidBodyHandle handle;
        Vector3d linearVelocity;
        if (subLevel.isRemoved()) {
            helper.fail("Sublevel was removed");
            return;
        }
        Vector3d gravity = DimensionPhysicsData.getGravity((Level)helper.getLevel(), spawnPos);
        if (!gravity.equals((Vector3dc)(linearVelocity = (handle = physicsSystem.getPhysicsHandle(subLevel)).getLinearVelocity(new Vector3d())), 0.01)) {
            Vector3d localPos = SableTestHelper.localPosition(helper, spawnPos);
            helper.fail("Sublevel velocity didn't follow gravity: Delta: " + gravity.distance((Vector3dc)linearVelocity), BlockPos.containing((double)localPos.x, (double)localPos.y, (double)localPos.z));
            return;
        }
        Vector3d expectedDelta = gravity.mul(0.5, new Vector3d());
        if (!expectedDelta.equals((Vector3dc)(delta = subLevel.logicalPose().position().sub(spawnPos, new Vector3d())), 0.01)) {
            Vector3d localPos = SableTestHelper.localPosition(helper, spawnPos);
            helper.fail("Sublevel position didn't follow gravity. Delta: " + expectedDelta.distance((Vector3dc)delta), BlockPos.containing((double)localPos.x, (double)localPos.y, (double)localPos.z));
        }
        helper.succeed();
    }
}
