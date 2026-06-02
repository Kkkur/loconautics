/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.gametest.framework.GameTestAssertPosException
 *  net.minecraft.gametest.framework.GameTestHelper
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.gametest;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SimulatedGameTestHelper {
    public static <T extends BlockEntity> void assertExtraKinetics(GameTestHelper helper, BlockPos pos, BiPredicate<T, KineticBlockEntity> predicate, BiFunction<T, KineticBlockEntity, String> exceptionMessage) {
        BlockEntity t = helper.getBlockEntity(pos);
        if (!predicate.test(t, ((ExtraKinetics)t).getExtraKinetics())) {
            throw new GameTestAssertPosException(exceptionMessage.apply(t, ((ExtraKinetics)t).getExtraKinetics()), helper.absolutePos(pos), pos, helper.getTick());
        }
    }

    public static <T extends KineticBlockEntity> void assertKineticsSpeed(GameTestHelper helper, BlockPos pos, ToDoubleFunction<T> speed, double delta) {
        helper.assertBlockEntityData(pos, be -> Math.abs((double)Math.abs(be.getSpeed()) - Math.abs(speed.applyAsDouble(be))) < delta, () -> {
            KineticBlockEntity be = (KineticBlockEntity)helper.getBlockEntity(pos);
            return "Expected %.2f speed, got %.2f".formatted(Math.abs(speed.applyAsDouble(be)), Float.valueOf(Math.abs(be.getSpeed())));
        });
    }

    public static <T extends KineticBlockEntity> void assertKineticsSpeed(GameTestHelper helper, BlockPos pos, ToDoubleFunction<T> speed) {
        SimulatedGameTestHelper.assertKineticsSpeed(helper, pos, speed, 1.0E-6);
    }

    public static void assertKineticsSpeed(GameTestHelper helper, BlockPos pos, double speed, double delta) {
        SimulatedGameTestHelper.assertKineticsSpeed(helper, pos, be -> speed, delta);
    }

    public static void assertKineticsSpeed(GameTestHelper helper, BlockPos pos, double speed) {
        SimulatedGameTestHelper.assertKineticsSpeed(helper, pos, be -> speed, 1.0E-6);
    }

    public static <T extends KineticBlockEntity> void assertExtraKineticsSpeed(GameTestHelper helper, BlockPos pos, ToDoubleBiFunction<T, KineticBlockEntity> speed, ToDoubleBiFunction<T, KineticBlockEntity> extraSpeed, double delta) {
        SimulatedGameTestHelper.assertExtraKinetics(helper, pos, (blockEntity, extraKinetics) -> Math.abs((double)Math.abs(blockEntity.getSpeed()) - speed.applyAsDouble((Object)blockEntity, (KineticBlockEntity)extraKinetics)) < delta && extraKinetics != null && Math.abs((double)Math.abs(extraKinetics.getSpeed()) - Math.abs(extraSpeed.applyAsDouble((Object)blockEntity, (KineticBlockEntity)extraKinetics))) < delta, (blockEntity, extraKinetics) -> {
            if (extraKinetics == null) {
                return "Expected extra kinetics, got null";
            }
            double speedValue = Math.abs(speed.applyAsDouble((Object)blockEntity, (KineticBlockEntity)extraKinetics));
            double extraSpeedValue = Math.abs(extraSpeed.applyAsDouble((Object)blockEntity, (KineticBlockEntity)extraKinetics));
            if (Math.abs((double)Math.abs(blockEntity.getSpeed()) - speedValue) >= delta) {
                return "Expected %.2f speed, got %.2f".formatted(speedValue, Float.valueOf(Math.abs(blockEntity.getSpeed())));
            }
            return "Expected %.2f extra kinetics speed, got %.2f".formatted(Math.abs(extraSpeedValue), Float.valueOf(Math.abs(extraKinetics.getSpeed())));
        });
    }

    public static <T extends KineticBlockEntity> void assertExtraKineticsSpeed(GameTestHelper helper, BlockPos pos, ToDoubleBiFunction<T, KineticBlockEntity> speed, ToDoubleBiFunction<T, KineticBlockEntity> extraSpeed) {
        SimulatedGameTestHelper.assertExtraKineticsSpeed(helper, pos, speed, extraSpeed, 1.0E-6);
    }

    public static void assertExtraKineticsSpeed(GameTestHelper helper, BlockPos pos, double speed, double extraSpeed, double delta) {
        SimulatedGameTestHelper.assertExtraKineticsSpeed(helper, pos, (be, ebe) -> speed, (be, ebe) -> extraSpeed, 1.0E-6);
    }

    public static void assertExtraKineticsSpeed(GameTestHelper helper, BlockPos pos, double speed, double extraSpeed) {
        SimulatedGameTestHelper.assertExtraKineticsSpeed(helper, pos, (be, ebe) -> speed, (be, ebe) -> extraSpeed, 1.0E-6);
    }
}
