/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.gametest.framework.GameTest
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.EnchantedBookItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.EnchantmentInstance
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RedstoneLampBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.infrastructure.gametest.tests;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.infrastructure.gametest.CreateGameTestHelper;
import com.simibubi.create.infrastructure.gametest.GameTestGroup;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@GameTestGroup(path="items")
public class TestItems {
    @GameTest(template="andesite_tunnel_split")
    public static void andesiteTunnelSplit(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(2, 6, 2);
        helper.pullLever(lever);
        Map<BlockPos, ItemStack> outputs = Map.of(new BlockPos(2, 2, 1), new ItemStack((ItemLike)AllItems.BRASS_INGOT.get(), 1), new BlockPos(3, 2, 1), new ItemStack((ItemLike)AllItems.BRASS_INGOT.get(), 1), new BlockPos(4, 2, 2), new ItemStack((ItemLike)AllItems.BRASS_INGOT.get(), 3));
        helper.succeedWhen(() -> outputs.forEach(helper::assertContainerContains));
    }

    @GameTest(template="arm_multi_output", timeoutTicks=200)
    public static void armMultiOutput(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(2, 3, 1);
        BlockPos[] blazeBurners = (BlockPos[])IntStream.rangeClosed(6, 8).boxed().flatMap(x -> IntStream.rangeClosed(1, 3).mapToObj(z -> new BlockPos(x.intValue(), 2, z))).toArray(BlockPos[]::new);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            for (BlockPos pos : blazeBurners) {
                helper.assertBlockState(pos, state -> state.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.KINDLED, () -> "Blaze burner isn't lit!");
            }
        });
    }

    @GameTest(template="arm_purgatory", timeoutTicks=200)
    public static void armPurgatory(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(2, 3, 2);
        BlockPos depot1Pos = new BlockPos(3, 2, 1);
        DepotBlockEntity depot1 = (DepotBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.DEPOT.get(), depot1Pos);
        BlockPos depot2Pos = new BlockPos(1, 2, 1);
        DepotBlockEntity depot2 = (DepotBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.DEPOT.get(), depot2Pos);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            helper.assertSecondsPassed(5);
            ItemStack held1 = depot1.getHeldItem();
            boolean held1Empty = held1.isEmpty();
            int held1Count = held1.getCount();
            ItemStack held2 = depot2.getHeldItem();
            boolean held2Empty = held2.isEmpty();
            int held2Count = held2.getCount();
            if (held1Empty && held2Empty) {
                helper.fail("No item present");
            }
            if (!held1Empty && held1Count != 1) {
                helper.fail("Unexpected count on depot 1: " + held1Count);
            }
            if (!held2Empty && held2Count != 1) {
                helper.fail("Unexpected count on depot 2: " + held2Count);
            }
        });
    }

    @GameTest(template="attribute_filters", timeoutTicks=200)
    public static void attributeFilters(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(2, 3, 1);
        BlockPos end = new BlockPos(11, 2, 2);
        Holder.Reference PROTECTION_ENCHANT = helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PROTECTION);
        Map<BlockPos, ItemStack> outputs = Map.of(new BlockPos(3, 2, 1), new ItemStack((ItemLike)AllBlocks.BRASS_BLOCK.get()), new BlockPos(4, 2, 1), new ItemStack((ItemLike)Items.APPLE), new BlockPos(5, 2, 1), new ItemStack((ItemLike)Items.WATER_BUCKET), new BlockPos(6, 2, 1), EnchantedBookItem.createForEnchantment((EnchantmentInstance)new EnchantmentInstance((Holder)PROTECTION_ENCHANT, 1)), new BlockPos(7, 2, 1), (ItemStack)Util.make((Object)new ItemStack((ItemLike)Items.NETHERITE_SWORD), s -> s.setDamageValue(1)), new BlockPos(8, 2, 1), new ItemStack((ItemLike)Items.IRON_HELMET), new BlockPos(9, 2, 1), new ItemStack((ItemLike)Items.COAL), new BlockPos(10, 2, 1), new ItemStack((ItemLike)Items.POTATO));
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            outputs.forEach(helper::assertContainerContains);
            helper.assertContainerEmpty(end);
        });
    }

    @GameTest(template="belt_coaster", timeoutTicks=200)
    public static void beltCoaster(CreateGameTestHelper helper) {
        BlockPos input = new BlockPos(1, 5, 6);
        BlockPos output = new BlockPos(3, 8, 6);
        BlockPos lever = new BlockPos(1, 5, 5);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            long remainingItems;
            long outputItems = helper.getTotalItems(output);
            if (outputItems != 27L) {
                helper.fail("Expected 27 items, got " + outputItems);
            }
            if ((remainingItems = helper.getTotalItems(input)) != 2L) {
                helper.fail("Expected 2 items remaining, got " + remainingItems);
            }
        });
    }

    @GameTest(template="brass_tunnel_filtering")
    public static void brassTunnelFiltering(CreateGameTestHelper helper) {
        Map<BlockPos, ItemStack> outputs = Map.of(new BlockPos(3, 2, 2), new ItemStack((ItemLike)Items.COPPER_INGOT, 13), new BlockPos(4, 2, 3), new ItemStack((ItemLike)AllItems.ZINC_INGOT.get(), 4), new BlockPos(4, 2, 4), new ItemStack((ItemLike)Items.IRON_INGOT, 2), new BlockPos(4, 2, 5), new ItemStack((ItemLike)Items.GOLD_INGOT, 24), new BlockPos(3, 2, 6), new ItemStack((ItemLike)Items.DIAMOND, 17));
        BlockPos lever = new BlockPos(2, 3, 2);
        helper.pullLever(lever);
        helper.succeedWhen(() -> outputs.forEach(helper::assertContainerContains));
    }

    @GameTest(template="brass_tunnel_prefer_nearest", timeoutTicks=200)
    public static void brassTunnelPreferNearest(CreateGameTestHelper helper) {
        List<BlockPos> tunnels = List.of(new BlockPos(3, 3, 1), new BlockPos(3, 3, 2), new BlockPos(3, 3, 3));
        List<BlockPos> out = List.of(new BlockPos(5, 2, 1), new BlockPos(5, 2, 2), new BlockPos(5, 2, 3));
        BlockPos lever = new BlockPos(2, 3, 2);
        helper.pullLever(lever);
        tunnels.forEach(tunnel -> helper.setTunnelMode((BlockPos)tunnel, BrassTunnelBlockEntity.SelectionMode.PREFER_NEAREST));
        helper.succeedWhen(() -> out.forEach(pos -> helper.assertContainerContains((BlockPos)pos, (ItemLike)AllBlocks.BRASS_CASING.get())));
    }

    @GameTest(template="brass_tunnel_round_robin", timeoutTicks=200)
    public static void brassTunnelRoundRobin(CreateGameTestHelper helper) {
        List<BlockPos> outputs = List.of(new BlockPos(7, 3, 1), new BlockPos(7, 3, 2), new BlockPos(7, 3, 3));
        TestItems.brassTunnelModeTest(helper, BrassTunnelBlockEntity.SelectionMode.ROUND_ROBIN, outputs);
    }

    @GameTest(template="brass_tunnel_split")
    public static void brassTunnelSplit(CreateGameTestHelper helper) {
        List<BlockPos> outputs = List.of(new BlockPos(7, 2, 1), new BlockPos(7, 2, 2), new BlockPos(7, 2, 3));
        TestItems.brassTunnelModeTest(helper, BrassTunnelBlockEntity.SelectionMode.SPLIT, outputs);
    }

    private static void brassTunnelModeTest(CreateGameTestHelper helper, BrassTunnelBlockEntity.SelectionMode mode, List<BlockPos> outputs) {
        BlockPos lever = new BlockPos(2, 3, 2);
        List<BlockPos> tunnels = List.of(new BlockPos(3, 3, 1), new BlockPos(3, 3, 2), new BlockPos(3, 3, 3));
        helper.pullLever(lever);
        tunnels.forEach(tunnel -> helper.setTunnelMode((BlockPos)tunnel, mode));
        helper.succeedWhen(() -> {
            long items = 0L;
            for (BlockPos out : outputs) {
                helper.assertContainerContains(out, (ItemLike)AllBlocks.BRASS_CASING.get());
                items += helper.getTotalItems(out);
            }
            if (items != 10L) {
                helper.fail("expected 10 items, got " + items);
            }
        });
    }

    @GameTest(template="brass_tunnel_sync_input", timeoutTicks=200)
    public static void brassTunnelSyncInput(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(1, 3, 2);
        List<BlockPos> redstoneBlocks = List.of(new BlockPos(3, 4, 1), new BlockPos(3, 4, 2), new BlockPos(3, 4, 3));
        List<BlockPos> tunnels = List.of(new BlockPos(5, 3, 1), new BlockPos(5, 3, 2), new BlockPos(5, 3, 3));
        List<BlockPos> outputs = List.of(new BlockPos(7, 2, 1), new BlockPos(7, 2, 2), new BlockPos(7, 2, 3));
        helper.pullLever(lever);
        tunnels.forEach(tunnel -> helper.setTunnelMode((BlockPos)tunnel, BrassTunnelBlockEntity.SelectionMode.SYNCHRONIZE));
        helper.succeedWhen(() -> {
            if (helper.secondsPassed() < 9L) {
                helper.setBlock((BlockPos)redstoneBlocks.get(0), Blocks.AIR);
                helper.assertSecondsPassed(3);
                outputs.forEach(helper::assertContainerEmpty);
                helper.setBlock((BlockPos)redstoneBlocks.get(1), Blocks.AIR);
                helper.assertSecondsPassed(6);
                outputs.forEach(helper::assertContainerEmpty);
                helper.setBlock((BlockPos)redstoneBlocks.get(2), Blocks.AIR);
                helper.assertSecondsPassed(9);
            } else {
                outputs.forEach(out -> helper.assertContainerContains((BlockPos)out, (ItemLike)AllBlocks.BRASS_CASING.get()));
            }
        });
    }

    @GameTest(template="smart_observer_belt_and_funnel", timeoutTicks=200)
    public static void smartObserverBeltAndFunnel(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(6, 3, 2);
        List<BlockPos> targets = List.of(new BlockPos(5, 2, 1), new BlockPos(2, 4, 6));
        List<BlockPos> overflows = List.of(new BlockPos(6, 2, 1), new BlockPos(1, 3, 6));
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            helper.assertSecondsPassed(9);
            targets.forEach(pos -> helper.assertBlockPresent(Blocks.DIAMOND_BLOCK, (BlockPos)pos));
            overflows.forEach(pos -> helper.assertBlockPresent(Blocks.AIR, (BlockPos)pos));
        });
    }

    @GameTest(template="smart_observer_chutes")
    public static void smartObserverChutes(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(1, 5, 2);
        BlockPos output = new BlockPos(1, 5, 3);
        helper.pullLever(lever);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.DIAMOND_BLOCK, output));
    }

    @GameTest(template="smart_observer_counting")
    public static void smartObserverCounting(CreateGameTestHelper helper) {
        BlockPos chest = new BlockPos(3, 2, 1);
        long totalChestItems = helper.getTotalItems(chest);
        BlockPos chestNixiePos = new BlockPos(2, 3, 1);
        NixieTubeBlockEntity chestNixie = (NixieTubeBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.NIXIE_TUBE.get(), chestNixiePos);
        BlockPos doubleChest = new BlockPos(2, 2, 3);
        long totalDoubleChestItems = helper.getTotalItems(doubleChest);
        BlockPos doubleChestNixiePos = new BlockPos(1, 3, 3);
        NixieTubeBlockEntity doubleChestNixie = (NixieTubeBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.NIXIE_TUBE.get(), doubleChestNixiePos);
        helper.succeedWhen(() -> {
            String doubleChestNixieText;
            long doubleChestNixieReading;
            String chestNixieText = chestNixie.getFullText().getString();
            long chestNixieReading = Long.parseLong(chestNixieText);
            if (chestNixieReading != totalChestItems) {
                helper.fail("Chest nixie detected %s, expected %s".formatted(chestNixieReading, totalChestItems));
            }
            if ((doubleChestNixieReading = Long.parseLong(doubleChestNixieText = doubleChestNixie.getFullText().getString())) != totalDoubleChestItems) {
                helper.fail("Double chest nixie detected %s, expected %s".formatted(doubleChestNixieReading, totalDoubleChestItems));
            }
        });
    }

    @GameTest(template="smart_observer_filtered_storage")
    public static void smartObserverFilteredStorage(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(2, 3, 1);
        BlockPos leftLamp = new BlockPos(3, 2, 3);
        BlockPos rightLamp = new BlockPos(1, 2, 3);
        helper.pullLever(lever);
        helper.succeedWhen(() -> {
            helper.assertBlockProperty(leftLamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(true));
            helper.assertBlockProperty(rightLamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(false));
        });
    }

    @GameTest(template="smart_observer_storage")
    public static void smartObserverStorage(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(1, 3, 2);
        BlockPos lamp = new BlockPos(1, 2, 3);
        helper.pullLever(lever);
        helper.succeedWhen(() -> helper.assertBlockProperty(lamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(true)));
    }

    @GameTest(template="depot_display", timeoutTicks=200)
    public static void depotDisplay(CreateGameTestHelper helper) {
        BlockPos displayPos = new BlockPos(5, 3, 1);
        List<DepotBlockEntity> depots = Stream.of(new BlockPos(2, 2, 1), new BlockPos(1, 2, 1)).map(pos -> (DepotBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.DEPOT.get(), (BlockPos)pos)).toList();
        List<BlockPos> levers = List.of(new BlockPos(2, 5, 0), new BlockPos(1, 5, 0));
        levers.forEach(arg_0 -> ((CreateGameTestHelper)helper).pullLever(arg_0));
        FlapDisplayBlockEntity display = ((FlapDisplayBlockEntity)helper.getBlockEntity((BlockEntityType)AllBlockEntityTypes.FLAP_DISPLAY.get(), displayPos)).getController();
        helper.succeedWhen(() -> {
            for (int i = 0; i < 2; ++i) {
                FlapDisplayLayout line = display.getLines().get(i);
                MutableComponent textComponent = Component.empty();
                line.getSections().stream().map(FlapDisplaySection::getText).forEach(arg_0 -> ((MutableComponent)textComponent).append(arg_0));
                String text = textComponent.getString().toLowerCase(Locale.ROOT).trim();
                DepotBlockEntity depot = (DepotBlockEntity)depots.get(i);
                ItemStack item = depot.getHeldItem();
                String name = BuiltInRegistries.ITEM.getKey((Object)item.getItem()).getPath();
                if (name.equals(text)) continue;
                helper.fail("Text mismatch: wanted [" + name + "], got: " + text);
            }
        });
    }

    @GameTest(template="threshold_switch")
    public static void thresholdSwitch(CreateGameTestHelper helper) {
        BlockPos chest = new BlockPos(1, 2, 1);
        BlockPos lamp = new BlockPos(2, 3, 1);
        helper.assertBlockProperty(lamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(false));
        IItemHandler chestStorage = helper.itemStorageAt(chest);
        for (int i = 0; i < 18; ++i) {
            ItemHandlerHelper.insertItem((IItemHandler)chestStorage, (ItemStack)new ItemStack((ItemLike)Items.DIAMOND, 64), (boolean)false);
        }
        helper.succeedWhen(() -> helper.assertBlockProperty(lamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(true)));
    }

    @GameTest(template="storages", timeoutTicks=200)
    public static void storages(CreateGameTestHelper helper) {
        BlockPos lever = new BlockPos(12, 3, 2);
        BlockPos startChest = new BlockPos(13, 3, 1);
        Object2LongMap<Item> originalContent = helper.getItemContent(startChest);
        BlockPos endShulker = new BlockPos(1, 3, 1);
        helper.pullLever(lever);
        helper.succeedWhen(() -> helper.assertContentPresent(originalContent, endShulker));
    }

    @GameTest(template="vault_comparator_output")
    public static void vaultComparatorOutput(CreateGameTestHelper helper) {
        BlockPos smallInput = new BlockPos(1, 4, 1);
        BlockPos smallNixie = new BlockPos(3, 2, 1);
        helper.assertNixiePower(smallNixie, 0);
        helper.whenSecondsPassed(1, () -> helper.spawnItems(smallInput, Items.BREAD, 576));
        BlockPos medInput = new BlockPos(1, 5, 4);
        BlockPos medNixie = new BlockPos(4, 2, 4);
        helper.assertNixiePower(medNixie, 0);
        helper.whenSecondsPassed(2, () -> helper.spawnItems(medInput, Items.BREAD, 4928));
        BlockPos bigInput = new BlockPos(1, 6, 8);
        BlockPos bigNixie = new BlockPos(5, 2, 7);
        helper.assertNixiePower(bigNixie, 0);
        helper.whenSecondsPassed(3, () -> helper.spawnItems(bigInput, Items.BREAD, 15360));
        helper.succeedWhen(() -> {
            helper.assertNixiePower(smallNixie, 7);
            helper.assertNixiePower(medNixie, 7);
            helper.assertNixiePower(bigNixie, 7);
        });
    }

    @GameTest(template="depot_comparator_output")
    public static void depotComparatorOutput(CreateGameTestHelper helper) {
        BlockPos swordNixie = new BlockPos(7, 2, 1);
        BlockPos diamondNixie = new BlockPos(5, 2, 1);
        BlockPos fullPearlNixie = new BlockPos(3, 2, 1);
        BlockPos halfPearlNixie = new BlockPos(1, 2, 1);
        helper.succeedWhen(() -> {
            helper.assertNixiePower(swordNixie, 15);
            helper.assertNixiePower(diamondNixie, 15);
            helper.assertNixiePower(fullPearlNixie, 15);
            helper.assertNixiePower(halfPearlNixie, 8);
        });
    }

    @GameTest(template="fan_processing", timeoutTicks=200)
    public static void fanProcessing(CreateGameTestHelper helper) {
        BlockPos.betweenClosed((BlockPos)new BlockPos(2, 7, 3), (BlockPos)new BlockPos(11, 7, 3)).forEach(pos -> helper.setBlock((BlockPos)pos, Blocks.REDSTONE_WIRE));
        helper.pullLever(1, 7, 3);
        List<BlockPos> lamps = List.of(new BlockPos(1, 2, 1), new BlockPos(5, 2, 1), new BlockPos(7, 2, 1), new BlockPos(9, 2, 1), new BlockPos(11, 2, 1));
        helper.succeedWhen(() -> {
            for (BlockPos lamp : lamps) {
                helper.assertBlockProperty(lamp, (Property)RedstoneLampBlock.LIT, Boolean.valueOf(true));
            }
        });
    }
}
