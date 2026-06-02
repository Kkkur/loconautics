/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.gametest.framework.GameTest
 *  net.minecraft.gametest.framework.GameTestAssertException
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.monster.Zombie
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FarmBlock
 *  net.minecraft.world.level.block.LeverBlock
 *  net.minecraft.world.level.block.RedstoneLampBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.infrastructure.gametest.tests;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.infrastructure.gametest.CreateGameTestHelper;
import com.simibubi.create.infrastructure.gametest.GameTestGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

@GameTestGroup(path="fluids")
public class TestFluids {
    @GameTest(template="hose_pulley_transfer", timeoutTicks=400)
    public static void hosePulleyTransfer(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(7, 7, 5);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            helper.assertSecondsPassed(15);
            BlockPos filledLowerCorner = new BlockPos(2, 3, 2);
            BlockPos filledUpperCorner = new BlockPos(4, 5, 4);
            BlockPos.betweenClosed((BlockPos)filledLowerCorner, (BlockPos)filledUpperCorner).forEach(pos -> helper.assertBlockPresent(Blocks.WATER, (BlockPos)pos));
            BlockPos emptiedLowerCorner = new BlockPos(8, 3, 2);
            BlockPos emptiedUpperCorner = new BlockPos(10, 5, 4);
            BlockPos.betweenClosed((BlockPos)emptiedLowerCorner, (BlockPos)emptiedUpperCorner).forEach(pos -> helper.assertBlockPresent(Blocks.AIR, (BlockPos)pos));
            BlockPos pulleyPos = new BlockPos(4, 7, 3);
            IFluidHandler storage = helper.fluidStorageAt(pulleyPos);
            if (storage instanceof HosePulleyFluidHandler) {
                HosePulleyFluidHandler hose = (HosePulleyFluidHandler)storage;
                SmartFluidTank internalTank = hose.getInternalTank();
                if (!internalTank.drain(1, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
                    helper.fail("Pulley not empty");
                }
            } else {
                helper.fail("Not a pulley");
            }
        });
    }

    @GameTest(template="in_world_pumping_out")
    public static void inWorldPumpingOut(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(4, 3, 3);
        BlockPos basin = new BlockPos(5, 2, 2);
        BlockPos output = new BlockPos(2, 2, 2);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.WATER, output);
            helper.assertTankEmpty(basin);
        });
    }

    @GameTest(template="in_world_pumping_in")
    public static void inWorldPumpingIn(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(4, 3, 3);
        BlockPos basin = new BlockPos(5, 2, 2);
        BlockPos water = new BlockPos(2, 2, 2);
        FluidStack expectedResult = new FluidStack((Fluid)Fluids.WATER, 1000);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, water);
            helper.assertFluidPresent(expectedResult, basin);
        });
    }

    @GameTest(template="steam_engine")
    public static void steamEngine(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(4, 3, 3);
        helper.pullLever(lever);
        BlockPos stressometer = new BlockPos(5, 2, 5);
        BlockPos speedometer = new BlockPos(4, 2, 5);
        helper.succeedWhen(() -> {
            StressGaugeBlockEntity stress = (StressGaugeBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.STRESSOMETER.get(), stressometer);
            SpeedGaugeBlockEntity speed = (SpeedGaugeBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.SPEEDOMETER.get(), speedometer);
            float capacity = stress.getNetworkCapacity();
            helper.assertCloseEnoughTo(capacity, 2048.0);
            float rotationSpeed = Mth.abs((float)speed.getSpeed());
            helper.assertCloseEnoughTo(rotationSpeed, 16.0);
        });
    }

    @GameTest(template="3_pipe_combine", timeoutTicks=400)
    public static void threePipeCombine(CreateGameTestHelper helper) {
        BlockPos tank1Pos = new BlockPos(5, 2, 1);
        BlockPos tank2Pos = tank1Pos.south();
        BlockPos tank3Pos = tank2Pos.south();
        long initialContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos);
        BlockPos pumpPos = new BlockPos(2, 2, 2);
        helper.flipBlock(pumpPos);
        helper.succeedWhen(() -> {
            helper.assertSecondsPassed(13);
            helper.assertTanksEmpty(tank1Pos, tank2Pos, tank3Pos);
            BlockPos outputTankPos = new BlockPos(1, 2, 2);
            long moved = helper.getFluidInTanks(outputTankPos);
            if (moved != initialContents) {
                helper.fail("Wrong amount of fluid amount. expected [%s], got [%s]".formatted(initialContents, moved));
            }
        });
    }

    @GameTest(template="3_pipe_split", timeoutTicks=200)
    public static void threePipeSplit(CreateGameTestHelper helper) {
        BlockPos pumpPos = new BlockPos(2, 2, 2);
        BlockPos tank1Pos = new BlockPos(5, 2, 1);
        BlockPos tank2Pos = tank1Pos.south();
        BlockPos tank3Pos = tank2Pos.south();
        BlockPos outputTankPos = new BlockPos(1, 2, 2);
        long totalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos, outputTankPos);
        helper.flipBlock(pumpPos);
        helper.succeedWhen(() -> {
            long newTotalContents;
            helper.assertSecondsPassed(7);
            FluidStack contents = helper.getTankContents(outputTankPos);
            if (!contents.isEmpty()) {
                helper.fail("Tank not empty: " + contents.getAmount());
            }
            if ((newTotalContents = helper.getFluidInTanks(tank1Pos, tank2Pos, tank3Pos)) != totalContents) {
                helper.fail("Wrong total fluid amount. expected [%s], got [%s]".formatted(totalContents, newTotalContents));
            }
        });
    }

    @GameTest(template="large_waterwheel", timeoutTicks=200)
    public static void largeWaterwheel(CreateGameTestHelper helper) {
        BlockPos wheel = new BlockPos(4, 3, 2);
        BlockPos leftEnd = new BlockPos(6, 2, 2);
        BlockPos rightEnd = new BlockPos(2, 2, 2);
        List<BlockPos> edges = List.of(new BlockPos(4, 5, 1), new BlockPos(4, 5, 3));
        BlockPos openLever = new BlockPos(3, 8, 1);
        BlockPos leftLever = new BlockPos(5, 7, 1);
        TestFluids.waterwheel(helper, wheel, 4.0f, 512.0f, leftEnd, rightEnd, edges, openLever, leftLever);
    }

    @GameTest(template="small_waterwheel", timeoutTicks=200)
    public static void smallWaterwheel(CreateGameTestHelper helper) {
        BlockPos wheel = new BlockPos(3, 2, 2);
        BlockPos leftEnd = new BlockPos(4, 2, 2);
        BlockPos rightEnd = new BlockPos(2, 2, 2);
        List<BlockPos> edges = List.of(new BlockPos(3, 3, 1), new BlockPos(3, 3, 3));
        BlockPos openLever = new BlockPos(2, 6, 1);
        BlockPos leftLever = new BlockPos(4, 5, 1);
        TestFluids.waterwheel(helper, wheel, 8.0f, 256.0f, leftEnd, rightEnd, edges, openLever, leftLever);
    }

    private static void waterwheel(CreateGameTestHelper helper, BlockPos wheel, float expectedRpm, float expectedSU, BlockPos leftEnd, BlockPos rightEnd, List<BlockPos> edges, BlockPos openLever, BlockPos leftLever) {
        BlockPos speedometer = wheel.north();
        BlockPos stressometer = wheel.south();
        helper.pullLever(openLever);
        helper.succeedWhen(() -> {
            edges.forEach(pos -> helper.assertBlockNotPresent(Blocks.WATER, (BlockPos)pos));
            helper.assertBlockPresent(Blocks.WATER, rightEnd);
            if (!((Boolean)helper.getBlockState(leftLever).getValue((Property)LeverBlock.POWERED)).booleanValue()) {
                helper.assertBlockPresent(Blocks.WATER, leftEnd);
                helper.assertSpeedometerSpeed(speedometer, 0.0f);
                helper.assertStressometerCapacity(stressometer, 0.0f);
                helper.powerLever(leftLever);
                helper.fail("Entering step 2");
            } else {
                helper.assertBlockNotPresent(Blocks.WATER, leftEnd);
                helper.assertSpeedometerSpeed(speedometer, expectedRpm);
                helper.assertStressometerCapacity(stressometer, expectedSU);
            }
        });
    }

    @GameTest(template="waterwheel_materials", timeoutTicks=300)
    public static void waterwheelMaterials(CreateGameTestHelper helper) {
        List planks = BuiltInRegistries.BLOCK.getOrCreateTag(BlockTags.PLANKS).stream().map(Holder::value).map(ItemLike::asItem).collect(Collectors.toCollection(ArrayList::new));
        List<BlockPos> chests = List.of(new BlockPos(6, 4, 2), new BlockPos(6, 4, 3));
        List<BlockPos> deployers = chests.stream().map(pos -> pos.below(2)).toList();
        helper.runAfterDelay(3L, () -> chests.forEach(chest -> planks.forEach(plank -> ItemHandlerHelper.insertItem((IItemHandler)helper.itemStorageAt((BlockPos)chest), (ItemStack)new ItemStack((ItemLike)plank), (boolean)false))));
        BlockPos smallWheel = new BlockPos(4, 2, 2);
        BlockPos largeWheel = new BlockPos(3, 3, 3);
        BlockPos lever = new BlockPos(5, 3, 1);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            Item plank = (Item)planks.get(0);
            if (!(plank instanceof BlockItem)) {
                throw new GameTestAssertException(String.valueOf(BuiltInRegistries.ITEM.getKey((Object)plank)) + " is not a BlockItem");
            }
            BlockItem blockItem = (BlockItem)plank;
            Block block = blockItem.getBlock();
            WaterWheelBlockEntity smallWheelBe = (WaterWheelBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.WATER_WHEEL.get(), smallWheel);
            if (!smallWheelBe.material.is(block)) {
                helper.fail("Small waterwheel has not consumed " + String.valueOf(BuiltInRegistries.ITEM.getKey((Object)plank)));
            }
            WaterWheelBlockEntity largeWheelBe = (WaterWheelBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.LARGE_WATER_WHEEL.get(), largeWheel);
            if (!largeWheelBe.material.is(block)) {
                helper.fail("Large waterwheel has not consumed " + String.valueOf(BuiltInRegistries.ITEM.getKey((Object)plank)));
            }
            planks.remove(0);
            deployers.forEach(pos -> {
                IItemHandler handler = helper.itemStorageAt((BlockPos)pos);
                for (int i = 0; i < handler.getSlots(); ++i) {
                    handler.extractItem(i, Integer.MAX_VALUE, false);
                }
            });
            if (!planks.isEmpty()) {
                helper.fail("Not all planks have been consumed");
            }
        });
    }

    @GameTest(template="smart_observer_pipes")
    public static void smartObserverPipes(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(3, 3, 1);
        BlockPos output = new BlockPos(3, 4, 4);
        BlockPos tankOutput = new BlockPos(1, 2, 4);
        FluidStack expected = new FluidStack((Fluid)Fluids.WATER, 2000);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            helper.assertFluidPresent(expected, tankOutput);
            helper.assertBlockPresent(Blocks.DIAMOND_BLOCK, output);
        });
    }

    @GameTest(template="threshold_switch", timeoutTicks=400)
    public static void thresholdSwitch(CreateGameTestHelper helper) {
        BlockPos leftHandle = new BlockPos(4, 2, 4);
        BlockPos leftValve = new BlockPos(4, 2, 3);
        BlockPos leftTank = new BlockPos(5, 2, 3);
        BlockPos rightHandle = new BlockPos(2, 2, 4);
        BlockPos rightValve = new BlockPos(2, 2, 3);
        BlockPos rightTank = new BlockPos(1, 2, 3);
        BlockPos drainHandle = new BlockPos(3, 3, 2);
        BlockPos drainValve = new BlockPos(3, 3, 1);
        BlockPos lamp = new BlockPos(1, 3, 1);
        BlockPos tank = new BlockPos(2, 2, 1);
        helper.succeedWhen(() -> {
            if (!((Boolean)helper.getBlockState(leftValve).getValue((Property)FluidValveBlock.ENABLED)).booleanValue()) {
                ((ValveHandleBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.VALVE_HANDLE.get(), leftHandle)).activate(false);
                helper.fail("Entering step 2");
            } else if (!((Boolean)helper.getBlockState(rightValve).getValue((Property)FluidValveBlock.ENABLED)).booleanValue()) {
                helper.assertFluidPresent(FluidStack.EMPTY, leftTank);
                helper.assertBlockProperty(lamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(false));
                ((ValveHandleBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.VALVE_HANDLE.get(), rightHandle)).activate(false);
                helper.fail("Entering step 3");
            } else if (!((Boolean)helper.getBlockState(drainValve).getValue((Property)FluidValveBlock.ENABLED)).booleanValue()) {
                helper.assertFluidPresent(FluidStack.EMPTY, rightTank);
                helper.assertBlockProperty(lamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(true));
                ((ValveHandleBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.VALVE_HANDLE.get(), drainHandle)).activate(false);
                helper.fail("Entering step 4");
            } else {
                helper.assertTankEmpty(tank);
                helper.assertBlockProperty(lamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(false));
            }
        });
    }

    @GameTest(template="open_pipes")
    public static void openPipes(CreateGameTestHelper helper) {
        BlockPos effects = new BlockPos(2, 4, 2);
        BlockPos removers = new BlockPos(3, 5, 2);
        BlockPos firstSeat = new BlockPos(4, 2, 1);
        BlockPos secondSeat = firstSeat.south(2);
        Zombie firstZombie = (Zombie)helper.spawn(EntityType.ZOMBIE, firstSeat);
        Zombie secondZombie = (Zombie)helper.spawn(EntityType.ZOMBIE, secondSeat);
        helper.pullLever(effects);
        MutableBoolean stage1 = new MutableBoolean(true);
        helper.succeedWhen(() -> {
            if (stage1.booleanValue()) {
                helper.assertTrue(firstZombie.isOnFire(), "not ignited");
                helper.assertFalse(secondZombie.getActiveEffects().isEmpty(), "no effects");
                stage1.setFalse();
                helper.pullLever(effects);
                helper.pullLever(removers);
                helper.fail("switching stages");
            } else {
                helper.assertFalse(firstZombie.isOnFire(), "not extinguished");
                helper.assertTrue(secondZombie.getActiveEffects().isEmpty(), "has effects");
            }
        });
    }

    @GameTest(template="spouting", timeoutTicks=200)
    public static void spouting(CreateGameTestHelper helper) {
        BlockPos farmland = new BlockPos(3, 2, 3);
        BlockPos depot = new BlockPos(5, 2, 1);
        helper.pullLever(2, 3, 2);
        ItemStack waterBottle = PotionContents.createItemStack((Item)Items.POTION, (Holder)Potions.WATER);
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.LAVA_CAULDRON, 3, 2, 1);
            helper.assertBlockProperty(farmland, (Property)FarmBlock.MOISTURE, Integer.valueOf(7));
            helper.assertBlockPresent(Blocks.MUD, farmland.east(1));
            helper.assertBlockPresent(Blocks.MUD, farmland.east(2));
            helper.assertBlockPresent(Blocks.MUD, farmland.east(3));
            helper.assertBlockPresent(Blocks.WATER_CAULDRON, farmland.east(4));
            helper.assertContainerContains(depot, Items.WATER_BUCKET);
            helper.assertContainerContains(depot.east(1), waterBottle);
            helper.assertContainerContains(depot.east(2), Items.GRASS_BLOCK);
        });
    }
}
