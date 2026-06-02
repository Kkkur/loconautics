/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.gametest.framework.GameTest
 *  net.minecraft.gametest.framework.GameTestAssertException
 *  net.minecraft.gametest.framework.GameTestAssertPosException
 *  net.minecraft.gametest.framework.GameTestHelper
 *  net.minecraft.gametest.framework.GameTestSequence
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.LeverBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.gametest.GameTestHolder
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.gametest;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlockEntity;
import dev.simulated_team.simulated.gametest.SimulatedGameTestHelper;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.gametest.GameTestHolder;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@GameTestHolder(value="simulated")
public class ExtraKineticsTest {
    @GameTest
    public static void analogTransmission(GameTestHelper helper) {
        AnalogLeverBlockEntity leverBE = (AnalogLeverBlockEntity)helper.getBlockEntity(new BlockPos(1, 2, 1));
        GameTestSequence sequence = helper.startSequence();
        for (int i = 0; i < 16; ++i) {
            sequence.thenExecuteAfter(1, () -> {
                switch (leverBE.getState()) {
                    case 0: {
                        SimulatedGameTestHelper.assertKineticsSpeed(helper, new BlockPos(1, 2, 2), 16.0);
                        break;
                    }
                    case 15: {
                        SimulatedGameTestHelper.assertKineticsSpeed(helper, new BlockPos(1, 2, 2), 0.0);
                        break;
                    }
                    default: {
                        SimulatedGameTestHelper.assertKineticsSpeed(helper, new BlockPos(1, 2, 2), be -> 16.0f / be.getRotationModifier());
                    }
                }
            }).thenExecuteAfter(1, () -> leverBE.changeState(false));
        }
        sequence.thenSucceed();
    }

    @GameTest
    public static void analogTransmissionReverse(GameTestHelper helper) {
        AnalogLeverBlockEntity leverBE = (AnalogLeverBlockEntity)helper.getBlockEntity(new BlockPos(1, 2, 1));
        GameTestSequence sequence = helper.startSequence();
        for (int i = 0; i < 16; ++i) {
            sequence.thenExecuteAfter(1, () -> {
                switch (leverBE.getState()) {
                    case 0: {
                        SimulatedGameTestHelper.assertExtraKineticsSpeed(helper, new BlockPos(1, 2, 2), 16.0, 16.0);
                        break;
                    }
                    case 15: {
                        SimulatedGameTestHelper.assertExtraKineticsSpeed(helper, new BlockPos(1, 2, 2), 16.0, 0.0);
                        break;
                    }
                    default: {
                        SimulatedGameTestHelper.assertExtraKineticsSpeed(helper, new BlockPos(1, 2, 2), (be, ebe) -> 16.0, (be, ebe) -> 16.0f * be.getRotationModifier());
                    }
                }
            }).thenExecuteAfter(1, () -> leverBE.changeState(false));
        }
        sequence.thenSucceed();
    }

    @GameTest
    public static void swivelBearing(GameTestHelper helper) {
        helper.startSequence().thenExecuteAfter(1, () -> SimulatedGameTestHelper.assertExtraKineticsSpeed(helper, new BlockPos(2, 3, 2), 64.0, -32.0)).thenIdle(20).thenExecute(() -> {
            int count = 0;
            SubLevel subLevel = null;
            for (SubLevel l : Sable.HELPER.getAllIntersecting((Level)helper.getLevel(), (BoundingBox3dc)new BoundingBox3d(helper.getBounds()))) {
                ++count;
                subLevel = l;
            }
            if (count != 1) {
                throw new GameTestAssertException("Expected 1 sub-level, found " + count);
            }
            KineticBlockEntity be = (KineticBlockEntity)Objects.requireNonNull(subLevel.getLevel().getBlockEntity(subLevel.getPlot().getCenterBlock()));
            if ((double)Math.abs(Math.abs(be.getSpeed()) - 64.0f) >= 1.0E-6) {
                Vector3d pos = subLevel.logicalPose().position();
                throw new GameTestAssertPosException("Expected %.2f speed, got %.2f".formatted(Float.valueOf(64.0f), Float.valueOf(Math.abs(be.getSpeed()))), BlockPos.containing((double)pos.x, (double)pos.y, (double)pos.z), BlockPos.containing((Position)helper.relativeVec(JOMLConversion.toMojang((Vector3dc)pos))), helper.getTick());
            }
        }).thenSucceed();
    }

    @GameTest
    public static void torsionSpring(GameTestHelper helper) {
        helper.startSequence().thenExecuteAfter(1, () -> SimulatedGameTestHelper.assertKineticsSpeed(helper, new BlockPos(2, 2, 3), 32.0)).thenExecuteAfter(15, () -> helper.assertBlockEntityData(new BlockPos(2, 2, 3), be -> Math.abs(be.getAngle()) == 90.0f, () -> "Expected 90 degrees, got %.0f".formatted(Float.valueOf(Math.abs(((TorsionSpringBlockEntity)helper.getBlockEntity(new BlockPos(2, 2, 3))).getAngle()))))).thenExecuteAfter(1, () -> helper.setBlock(1, 2, 2, (BlockState)helper.getBlockState(new BlockPos(1, 2, 2)).setValue((Property)LeverBlock.POWERED, (Comparable)Boolean.valueOf(true)))).thenExecuteAfter(15, () -> helper.assertBlockEntityData(new BlockPos(2, 2, 3), be -> be.getAngle() == 0.0f, () -> "Expected 0 degrees, got %.0f".formatted(Float.valueOf(Math.abs(((TorsionSpringBlockEntity)helper.getBlockEntity(new BlockPos(2, 2, 3))).getAngle()))))).thenSucceed();
    }
}
