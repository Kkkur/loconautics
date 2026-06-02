/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.infrastructure.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import java.util.ArrayList;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class TunnelScenes {
    public static void andesite(SceneBuilder builder, SceneBuildingUtil util) {
        int i;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("andesite_tunnel", "Using Andesite Tunnels");
        scene.configureBasePlate(0, 0, 5);
        scene.world().cycleBlockProperty(util.grid().at(2, 1, 2), (Property)BeltBlock.CASING);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(4, 1, 5, 4, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(4, 1, 2, 0, 1, 2), Direction.SOUTH);
        scene.idle(10);
        ArrayList<ElementLink> tunnels = new ArrayList<ElementLink>(3);
        for (i = 0; i < 3; ++i) {
            tunnels.add(scene.world().showIndependentSection(util.select().position(1 + i, 2, 4), Direction.DOWN));
            scene.world().moveSection((ElementLink)tunnels.get(i), util.vector().of(0.0, 0.0, -2.0), 0);
            scene.idle(4);
        }
        for (i = 0; i < 3; ++i) {
            scene.world().cycleBlockProperty(util.grid().at(1 + i, 1, 2), (Property)BeltBlock.CASING);
            scene.world().modifyBlockEntityNBT(util.select().position(1 + i, 1, 2), BeltBlockEntity.class, nbt -> NBTHelper.writeEnum((CompoundTag)nbt, (String)"Casing", (Enum)BeltBlockEntity.CasingType.ANDESITE), true);
            scene.idle(4);
        }
        scene.overlay().showText(60).attachKeyFrame().pointAt(util.vector().topOf(util.grid().at(1, 2, 2))).placeNearTarget().text("Andesite Tunnels can be used to cover up your belts");
        scene.idle(70);
        for (i = 0; i < 3; ++i) {
            scene.world().cycleBlockProperty(util.grid().at(1 + i, 1, 2), (Property)BeltBlock.CASING);
            scene.world().hideIndependentSection((ElementLink)tunnels.get(i), Direction.UP);
            scene.idle(4);
        }
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(2, 1, 0, 0, 1, 1), Direction.SOUTH);
        scene.idle(10);
        scene.world().showSection(util.select().position(2, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().cycleBlockProperty(util.grid().at(2, 1, 2), (Property)BeltBlock.CASING);
        scene.overlay().showText(60).attachKeyFrame().pointAt(util.vector().blockSurface(util.grid().at(2, 2, 2), Direction.NORTH)).placeNearTarget().text("Whenever an Andesite Tunnel has connections to the sides...");
        scene.idle(70);
        scene.overlay().showControls(util.vector().topOf(util.grid().at(4, 1, 2)), Pointing.DOWN, 20).withItem(new ItemStack((ItemLike)Items.COPPER_INGOT));
        scene.idle(7);
        scene.world().createItemOnBelt(util.grid().at(4, 1, 2), Direction.UP, new ItemStack((ItemLike)Items.COPPER_INGOT, 64));
        scene.idle(40);
        scene.world().multiplyKineticSpeed(util.select().everywhere(), 0.0625f);
        scene.overlay().showText(80).attachKeyFrame().text("...they will split exactly one item off of any passing stacks").pointAt(util.vector().blockSurface(util.grid().at(2, 1, 0), Direction.WEST)).placeNearTarget();
        scene.idle(90);
        scene.overlay().showText(80).text("The remainder will continue on its path").pointAt(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.UP)).placeNearTarget();
        scene.idle(90);
        scene.world().multiplyKineticSpeed(util.select().everywhere(), 16.0f);
    }

    public static void brass(SceneBuilder builder, SceneBuildingUtil util) {
        int i;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("brass_tunnel", "Using Brass Tunnels");
        scene.configureBasePlate(1, 0, 5);
        scene.world().cycleBlockProperty(util.grid().at(3, 1, 2), (Property)BeltBlock.CASING);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(5, 1, 5, 5, 1, 3), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(5, 1, 2, 1, 1, 2), Direction.SOUTH);
        scene.idle(10);
        ArrayList<ElementLink> tunnels = new ArrayList<ElementLink>(3);
        for (i = 0; i < 3; ++i) {
            tunnels.add(scene.world().showIndependentSection(util.select().position(2 + i, 2, 4), Direction.DOWN));
            scene.world().moveSection((ElementLink)tunnels.get(i), util.vector().of(0.0, 0.0, -2.0), 0);
            scene.idle(4);
        }
        for (i = 0; i < 3; ++i) {
            scene.world().cycleBlockProperty(util.grid().at(2 + i, 1, 2), (Property)BeltBlock.CASING);
            scene.world().modifyBlockEntityNBT(util.select().position(2 + i, 1, 2), BeltBlockEntity.class, nbt -> NBTHelper.writeEnum((CompoundTag)nbt, (String)"Casing", (Enum)BeltBlockEntity.CasingType.BRASS), true);
            scene.idle(4);
        }
        scene.overlay().showText(60).attachKeyFrame().pointAt(util.vector().topOf(util.grid().at(2, 2, 2))).placeNearTarget().text("Brass Tunnels can be used to cover up your belts");
        scene.idle(70);
        for (i = 0; i < 3; ++i) {
            scene.world().cycleBlockProperty(util.grid().at(2 + i, 1, 2), (Property)BeltBlock.CASING);
            scene.world().hideIndependentSection((ElementLink)tunnels.get(i), Direction.UP);
            scene.idle(4);
        }
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 0, 1, 1, 1), Direction.SOUTH);
        scene.idle(10);
        scene.world().showSection(util.select().position(3, 2, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().cycleBlockProperty(util.grid().at(3, 1, 2), (Property)BeltBlock.CASING);
        scene.idle(10);
        BlockPos tunnelPos = util.grid().at(3, 2, 2);
        for (Direction d : Iterate.horizontalDirections) {
            if (d == Direction.SOUTH) continue;
            Vec3 filter = TunnelScenes.getTunnelFilterVec(tunnelPos, d);
            scene.overlay().showFilterSlotInput(filter, d, 40);
            scene.idle(3);
        }
        scene.overlay().showText(60).attachKeyFrame().pointAt(TunnelScenes.getTunnelFilterVec(tunnelPos, Direction.WEST)).placeNearTarget().text("Brass Tunnels have filter slots on each open side");
        scene.idle(70);
        scene.rotateCameraY(70.0f);
        scene.idle(20);
        Vec3 tunnelFilterVec = TunnelScenes.getTunnelFilterVec(tunnelPos, Direction.EAST);
        scene.overlay().showFilterSlotInput(tunnelFilterVec, Direction.EAST, 10);
        scene.overlay().showText(60).attachKeyFrame().pointAt(tunnelFilterVec).placeNearTarget().text("Filters on inbound connections simply block non-matching items");
        ItemStack copper = new ItemStack((ItemLike)Items.COPPER_INGOT);
        Class<BrassTunnelBlockEntity> tunnelClass = BrassTunnelBlockEntity.class;
        scene.world().modifyBlockEntity(tunnelPos, tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.EAST, copper));
        scene.overlay().showControls(tunnelFilterVec, Pointing.DOWN, 30).withItem(copper);
        ItemStack zinc = AllItems.ZINC_INGOT.asStack();
        scene.world().createItemOnBelt(util.grid().at(5, 1, 2), Direction.EAST, zinc);
        scene.idle(70);
        scene.world().multiplyKineticSpeed(util.select().everywhere(), -2.0f);
        scene.idle(20);
        scene.rotateCameraY(-70.0f);
        scene.world().multiplyKineticSpeed(util.select().everywhere(), -0.5f);
        scene.idle(20);
        scene.world().modifyBlockEntity(tunnelPos, tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.EAST, ItemStack.EMPTY));
        tunnelFilterVec = TunnelScenes.getTunnelFilterVec(tunnelPos, Direction.NORTH);
        scene.overlay().showFilterSlotInput(tunnelFilterVec, Direction.NORTH, 40);
        tunnelFilterVec = TunnelScenes.getTunnelFilterVec(tunnelPos, Direction.WEST);
        scene.overlay().showFilterSlotInput(tunnelFilterVec, Direction.WEST, 40);
        scene.overlay().showText(60).attachKeyFrame().pointAt(tunnelFilterVec).placeNearTarget().text("Filters on outbound connections can be used to sort items by type");
        scene.idle(70);
        scene.overlay().showControls(tunnelFilterVec, Pointing.LEFT, 30).withItem(copper);
        scene.world().modifyBlockEntity(tunnelPos, tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.WEST, copper));
        scene.idle(4);
        tunnelFilterVec = TunnelScenes.getTunnelFilterVec(tunnelPos, Direction.NORTH);
        scene.overlay().showControls(tunnelFilterVec, Pointing.RIGHT, 30).withItem(zinc);
        scene.world().modifyBlockEntity(tunnelPos, tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.NORTH, zinc));
        scene.world().multiplyKineticSpeed(util.select().everywhere(), 1.5f);
        for (int i2 = 0; i2 < 6; ++i2) {
            scene.world().createItemOnBelt(util.grid().at(5, 1, 2), Direction.EAST, i2 % 2 == 0 ? zinc : copper);
            scene.idle(12);
        }
        scene.idle(30);
        scene.world().modifyBlockEntity(tunnelPos, tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.NORTH, ItemStack.EMPTY));
        scene.world().modifyBlockEntity(tunnelPos, tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.WEST, ItemStack.EMPTY));
        scene.idle(10);
        Vec3 tunnelTop = util.vector().topOf(tunnelPos);
        scene.overlay().showCenteredScrollInput(tunnelPos, Direction.UP, 120);
        scene.overlay().showText(120).attachKeyFrame().pointAt(tunnelTop).placeNearTarget().text("Whenever a passing item has multiple valid exits, the distribution mode will decide how to handle it");
        for (int i3 = 0; i3 < 3; ++i3) {
            scene.idle(40);
            scene.world().createItemOnBelt(util.grid().at(5, 1, 2), Direction.EAST, AllItems.BRASS_INGOT.asStack(63));
        }
        scene.idle(30);
        scene.world().hideSection(util.select().position(3, 2, 2), Direction.UP);
        scene.idle(5);
        scene.world().hideSection(util.select().fromTo(5, 1, 2, 1, 1, 0), Direction.UP);
        scene.idle(15);
        ElementLink newBelt = scene.world().showIndependentSection(util.select().fromTo(3, 3, 2, 0, 3, 4).add(util.select().fromTo(5, 3, 3, 4, 3, 3)), Direction.DOWN);
        scene.world().moveSection(newBelt, util.vector().of(0.0, -2.0, -1.0), 0);
        scene.idle(15);
        for (int i4 = 0; i4 < 3; ++i4) {
            scene.idle(4);
            scene.world().showSectionAndMerge(util.select().position(3, 4, 2 + i4), Direction.DOWN, newBelt);
        }
        scene.overlay().showOutlineWithText(util.select().fromTo(3, 1, 1, 3, 2, 3), 80).attachKeyFrame().placeNearTarget().text("Brass Tunnels on parallel belts will form a group");
        scene.idle(90);
        ItemStack item1 = new ItemStack((ItemLike)Items.CARROT);
        ItemStack item2 = new ItemStack((ItemLike)Items.HONEY_BOTTLE);
        ItemStack item3 = new ItemStack((ItemLike)Items.SWEET_BERRIES);
        tunnelFilterVec = TunnelScenes.getTunnelFilterVec(tunnelPos, Direction.WEST);
        BlockPos newTunnelPos = tunnelPos.above(2).south();
        scene.overlay().showControls(tunnelFilterVec.add(0.0, 0.0, -1.0), Pointing.RIGHT, 20).withItem(item1);
        scene.world().modifyBlockEntity(newTunnelPos.north(), tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.WEST, item1));
        scene.idle(4);
        scene.overlay().showControls(tunnelFilterVec, Pointing.DOWN, 20).withItem(item2);
        scene.world().modifyBlockEntity(newTunnelPos, tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.WEST, item2));
        scene.idle(4);
        scene.overlay().showControls(tunnelFilterVec.add(0.0, 0.0, 1.0), Pointing.LEFT, 20).withItem(item3);
        scene.world().modifyBlockEntity(newTunnelPos.south(), tunnelClass, be -> ((FilteringBehaviour)be.getBehaviour(SidedFilteringBehaviour.TYPE)).setFilter(Direction.WEST, item3));
        scene.idle(30);
        scene.overlay().showText(80).pointAt(tunnelTop).placeNearTarget().text("Incoming Items will now be distributed across all connected exits");
        scene.idle(90);
        BlockPos beltPos = util.grid().at(5, 3, 3);
        Vec3 m = util.vector().of(0.0, 0.1, 0.0);
        Vec3 spawn = util.vector().centerOf(util.grid().at(5, 3, 2));
        scene.world().createItemEntity(spawn, m, item1);
        scene.idle(12);
        scene.world().createItemOnBelt(beltPos, Direction.UP, item1);
        scene.world().modifyEntities(ItemEntity.class, Entity::discard);
        scene.world().createItemEntity(spawn, m, item2);
        scene.idle(12);
        scene.world().createItemOnBelt(beltPos, Direction.UP, item2);
        scene.world().modifyEntities(ItemEntity.class, Entity::discard);
        scene.world().createItemEntity(spawn, m, item3);
        scene.idle(12);
        scene.world().createItemOnBelt(beltPos, Direction.UP, item3);
        scene.world().modifyEntities(ItemEntity.class, Entity::discard);
        scene.idle(50);
        scene.world().showSectionAndMerge(util.select().position(3, 5, 2), Direction.DOWN, newBelt);
        scene.overlay().showText(80).pointAt(util.vector().blockSurface(tunnelPos.above().north(), Direction.WEST)).placeNearTarget().text("For this, items can also be inserted into the Tunnel block directly");
        scene.idle(20);
        beltPos = util.grid().at(3, 3, 3);
        spawn = util.vector().centerOf(util.grid().at(3, 5, 1));
        scene.world().createItemEntity(spawn, m, item1);
        scene.idle(12);
        scene.world().createItemOnBelt(beltPos, Direction.EAST, item1);
        scene.world().modifyEntities(ItemEntity.class, Entity::discard);
        scene.world().createItemEntity(spawn, m, item2);
        scene.idle(12);
        scene.world().createItemOnBelt(beltPos, Direction.EAST, item2);
        scene.world().modifyEntities(ItemEntity.class, Entity::discard);
        scene.world().createItemEntity(spawn, m, item3);
        scene.idle(12);
        scene.world().createItemOnBelt(beltPos, Direction.EAST, item3);
        scene.world().modifyEntities(ItemEntity.class, Entity::discard);
        scene.idle(30);
    }

    protected static Vec3 getTunnelFilterVec(BlockPos pos, Direction d) {
        return VecHelper.getCenterOf((Vec3i)pos).add(Vec3.atLowerCornerOf((Vec3i)d.getNormal()).scale(0.5)).add(0.0, 0.3, 0.0);
    }

    public static void brassModes(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("brass_tunnel_modes", "Distribution Modes of the Brass Tunnel");
        scene.configureBasePlate(0, 1, 5);
        BlockState barrier = Blocks.BARRIER.defaultBlockState();
        scene.world().setBlock(util.grid().at(1, 1, 0), barrier, false);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(1, 1, 1, 5, 1, 5).add(util.select().fromTo(3, 2, 5, 1, 2, 5)), Direction.DOWN);
        scene.idle(10);
        for (int i = 0; i < 3; ++i) {
            scene.world().showSection(util.select().position(3 - i, 2, 3), Direction.DOWN);
            scene.idle(4);
        }
        Vec3 tunnelTop = util.vector().topOf(util.grid().at(2, 2, 3));
        scene.overlay().showControls(tunnelTop, Pointing.DOWN, 80).rightClick();
        scene.idle(7);
        scene.overlay().showCenteredScrollInput(util.grid().at(2, 2, 3), Direction.UP, 120);
        scene.overlay().showText(120).attachKeyFrame().pointAt(tunnelTop).placeNearTarget().text("The distribution behaviour of Brass Tunnels can be configured");
        scene.idle(130);
        Class<BrassTunnelBlockEntity> tunnelClass = BrassTunnelBlockEntity.class;
        ElementLink blockage = scene.world().showIndependentSection(util.select().position(4, 1, 0), Direction.UP);
        scene.world().moveSection(blockage, util.vector().of(-3.0, 0.0, 0.0), 0);
        Vec3 modeVec = util.vector().of(4.0, 2.5, 3.0);
        scene.overlay().showControls(modeVec, Pointing.RIGHT, 140).showing((ScreenElement)AllIcons.I_TUNNEL_SPLIT);
        ElementLink blockage2 = null;
        for (int i = 0; i < 32; ++i) {
            if (i < 30) {
                scene.world().createItemOnBelt(util.grid().at(1, 1, 5), Direction.EAST, new ItemStack((ItemLike)Items.SNOWBALL, 12));
            }
            scene.idle(i > 8 ? 30 : 40);
            if (i == 0) {
                scene.overlay().showText(80).attachKeyFrame().pointAt(tunnelTop).placeNearTarget().text("'Split' will attempt to distribute the stack evenly between available outputs");
            }
            if (i == 2) {
                scene.overlay().showText(60).text("If an output is unable to take more items, it will be skipped").pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.UP)).placeNearTarget().colored(PonderPalette.GREEN);
            }
            if (i == 4) {
                scene.overlay().showControls(modeVec, Pointing.RIGHT, 140).showing((ScreenElement)AllIcons.I_TUNNEL_FORCED_SPLIT);
                scene.world().modifyBlockEntity(util.grid().at(1, 2, 3), tunnelClass, be -> ((ScrollValueBehaviour)be.getBehaviour(ScrollOptionBehaviour.TYPE)).setValue(BrassTunnelBlockEntity.SelectionMode.FORCED_SPLIT.ordinal()));
            }
            if (i == 5) {
                scene.overlay().showText(80).attachKeyFrame().text("'Forced Split' will never skip outputs, and instead wait until they are free").pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.UP)).placeNearTarget().colored(PonderPalette.RED);
                scene.idle(60);
                scene.world().moveSection(blockage, util.vector().of(-1.0, 0.0, 0.0), 10);
                scene.world().setBlock(util.grid().at(1, 1, 0), Blocks.AIR.defaultBlockState(), false);
                scene.world().multiplyKineticSpeed(util.select().everywhere(), 1.5f);
            }
            if (i == 7) {
                scene.world().modifyBlockEntity(util.grid().at(1, 2, 3), tunnelClass, be -> ((ScrollValueBehaviour)be.getBehaviour(ScrollOptionBehaviour.TYPE)).setValue(BrassTunnelBlockEntity.SelectionMode.ROUND_ROBIN.ordinal()));
                scene.overlay().showControls(modeVec, Pointing.RIGHT, 140).showing((ScreenElement)AllIcons.I_TUNNEL_ROUND_ROBIN);
                scene.overlay().showText(80).attachKeyFrame().pointAt(tunnelTop).placeNearTarget().text("'Round Robin' keeps stacks whole, and cycles through outputs iteratively");
            }
            if (i == 7) {
                scene.world().moveSection(blockage, util.vector().of(1.0, 0.0, 0.0), 10);
                scene.world().setBlock(util.grid().at(1, 1, 0), barrier, false);
            }
            if (i == 13) {
                scene.overlay().showText(60).text("Once Again, if an output is unable to take more items, it will be skipped").placeNearTarget().pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.UP)).colored(PonderPalette.GREEN);
            }
            if (i == 15) {
                scene.overlay().showControls(modeVec, Pointing.RIGHT, 140).showing((ScreenElement)AllIcons.I_TUNNEL_FORCED_ROUND_ROBIN);
                scene.world().modifyBlockEntity(util.grid().at(1, 2, 3), tunnelClass, be -> ((ScrollValueBehaviour)be.getBehaviour(ScrollOptionBehaviour.TYPE)).setValue(BrassTunnelBlockEntity.SelectionMode.FORCED_ROUND_ROBIN.ordinal()));
            }
            if (i == 16) {
                scene.overlay().showText(50).attachKeyFrame().placeNearTarget().text("'Forced Round Robin' never skips outputs").pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.UP)).colored(PonderPalette.RED);
                scene.idle(30);
                scene.world().moveSection(blockage, util.vector().of(-1.0, 0.0, 0.0), 10);
                scene.world().setBlock(util.grid().at(1, 1, 0), Blocks.AIR.defaultBlockState(), false);
            }
            if (i == 19) {
                scene.overlay().showControls(modeVec, Pointing.RIGHT, 140).showing((ScreenElement)AllIcons.I_TUNNEL_PREFER_NEAREST);
                scene.world().modifyBlockEntity(util.grid().at(1, 2, 3), tunnelClass, be -> ((ScrollValueBehaviour)be.getBehaviour(ScrollOptionBehaviour.TYPE)).setValue(BrassTunnelBlockEntity.SelectionMode.PREFER_NEAREST.ordinal()));
                scene.world().moveSection(blockage, util.vector().of(1.0, 0.0, 0.0), 10);
                scene.world().setBlock(util.grid().at(1, 1, 0), barrier, false);
                scene.overlay().showText(70).attachKeyFrame().text("'Prefer Nearest' prioritizes the outputs closest to the items' input location").pointAt(util.vector().blockSurface(util.grid().at(1, 1, 2), Direction.UP)).placeNearTarget().colored(PonderPalette.GREEN);
            }
            if (i == 21) {
                scene.world().setBlock(util.grid().at(2, 1, 0), Blocks.BARRIER.defaultBlockState(), false);
                blockage2 = scene.world().showIndependentSection(util.select().position(4, 1, 0), Direction.UP);
                scene.world().moveSection(blockage2, util.vector().of(-2.0, 0.0, 0.0), 0);
            }
            if (i == 25) {
                scene.world().hideIndependentSection(blockage, Direction.DOWN);
                scene.world().setBlock(util.grid().at(1, 1, 0), Blocks.AIR.defaultBlockState(), false);
                scene.world().hideIndependentSection(blockage2, Direction.DOWN);
                scene.world().setBlock(util.grid().at(2, 1, 0), Blocks.AIR.defaultBlockState(), false);
            }
            if (i == 26) {
                scene.overlay().showControls(modeVec, Pointing.RIGHT, 140).showing((ScreenElement)AllIcons.I_TUNNEL_RANDOMIZE);
                scene.world().modifyBlockEntity(util.grid().at(1, 2, 3), tunnelClass, be -> ((ScrollValueBehaviour)be.getBehaviour(ScrollOptionBehaviour.TYPE)).setValue(BrassTunnelBlockEntity.SelectionMode.RANDOMIZE.ordinal()));
            }
            if (i != 27) continue;
            scene.overlay().showText(70).attachKeyFrame().text("'Randomize' will distribute whole stacks to randomly picked outputs").pointAt(tunnelTop).placeNearTarget();
        }
        scene.world().hideSection(util.select().fromTo(3, 2, 5, 1, 2, 5), Direction.UP);
        scene.idle(10);
        scene.overlay().showControls(modeVec, Pointing.RIGHT, 140).showing((ScreenElement)AllIcons.I_TUNNEL_SYNCHRONIZE);
        scene.world().modifyBlockEntity(util.grid().at(1, 2, 3), tunnelClass, be -> ((ScrollValueBehaviour)be.getBehaviour(ScrollOptionBehaviour.TYPE)).setValue(BrassTunnelBlockEntity.SelectionMode.SYNCHRONIZE.ordinal()));
        scene.idle(30);
        scene.overlay().showText(70).attachKeyFrame().text("'Synchronize Inputs' is a unique setting for Brass Tunnels").pointAt(tunnelTop).placeNearTarget();
        ItemStack item1 = new ItemStack((ItemLike)Items.CARROT);
        ItemStack item2 = new ItemStack((ItemLike)Items.HONEY_BOTTLE);
        ItemStack item3 = AllItems.POLISHED_ROSE_QUARTZ.asStack();
        scene.world().createItemOnBelt(util.grid().at(3, 1, 4), Direction.UP, item1);
        scene.world().createItemOnBelt(util.grid().at(2, 1, 4), Direction.UP, item2);
        scene.world().createItemOnBelt(util.grid().at(3, 1, 5), Direction.SOUTH, item1);
        scene.world().createItemOnBelt(util.grid().at(2, 1, 5), Direction.SOUTH, item2);
        scene.idle(80);
        scene.world().createItemOnBelt(util.grid().at(2, 1, 5), Direction.SOUTH, item2);
        scene.rotateCameraY(-90.0f);
        scene.idle(20);
        scene.world().multiplyKineticSpeed(util.select().everywhere(), 0.5f);
        scene.overlay().showText(70).text("Items are only allowed past if every tunnel in the group has one waiting").pointAt(util.vector().blockSurface(util.grid().at(2, 1, 4), Direction.UP)).placeNearTarget().colored(PonderPalette.OUTPUT);
        scene.idle(60);
        scene.world().createItemOnBelt(util.grid().at(1, 1, 5), Direction.SOUTH, item3);
        scene.idle(90);
        scene.rotateCameraY(90.0f);
        scene.overlay().showText(100).text("This ensures that all affected belts supply items at the same rate").pointAt(util.vector().blockSurface(util.grid().at(1, 2, 3), Direction.WEST)).placeNearTarget().colored(PonderPalette.GREEN);
    }
}
