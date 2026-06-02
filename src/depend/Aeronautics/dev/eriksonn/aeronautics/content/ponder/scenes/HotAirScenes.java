/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock
 *  com.simibubi.create.content.processing.burner.BlazeBurnerBlock
 *  com.simibubi.create.content.processing.burner.BlazeBurnerBlock$HeatLevel
 *  com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  dev.simulated_team.simulated.ponder.SmoothMovementUtils
 *  dev.simulated_team.simulated.ponder.instructions.ChaseAABBWithLinkInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction
 *  dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.api.scene.WorldInstructions
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.createmod.ponder.foundation.instruction.RotateSceneInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerBlock;
import dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent.SteamVentBlock;
import dev.eriksonn.aeronautics.content.ponder.instructions.RedstoneSignalInstruction;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.ChaseAABBWithLinkInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.util.ArrayList;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.WorldInstructions;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HotAirScenes {
    public static void hotAirBurner(SceneBuilder scene, SceneBuildingUtil util, boolean isVent) {
        ElementLink shipLink;
        String gasTitle;
        String pluralTitle = isVent ? "Steam Vents" : "Hot Air Burners";
        String singularTitle = isVent ? "Steam Vent" : "Hot Air Burner";
        String string = gasTitle = isVent ? "Steam" : "Hot Air";
        if (isVent) {
            scene.title("steam_vent", "Generating Lift using the Steam Vent");
        } else {
            scene.title("hot_air_burner", "Generating Lift using the Hot Air Burner");
        }
        scene.scaleSceneView(0.7f);
        scene.setSceneOffsetY(-2.0f);
        scene.configureBasePlate(0, 0, 9);
        BlockPos burnerPos = new BlockPos(4, 3, 4);
        BlockPos secondBurnerPos = new BlockPos(5, 3, 4);
        BlockPos assemblerPos = new BlockPos(6, 3, 4);
        Selection blazeSelection = util.select().fromTo(3, 1, 3, 5, 1, 3);
        WorldInstructions world = scene.world();
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assemblerPos, true, true));
        world.showSection(util.select().layer(0), Direction.UP);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(9.0f, 0.0f, true));
        scene.idle(10);
        ArrayList<ElementLink> links = new ArrayList<ElementLink>();
        ElementLink blazeLink = null;
        if (isVent) {
            blazeLink = world.showIndependentSection(blazeSelection, Direction.DOWN);
            links.add(blazeLink);
            shipLink = world.showIndependentSection(util.select().fromTo(1, 1, 3, 8, 2, 6).substract(blazeSelection), Direction.DOWN);
            links.add(shipLink);
            CreateSceneBuilder createScene = new CreateSceneBuilder(scene.getScene().builder());
            createScene.world().setKineticSpeed(util.select().position(7, 3, 5), -64.0f);
        } else {
            shipLink = world.showIndependentSection(util.select().fromTo(1, 1, 3, 8, 2, 6), Direction.DOWN);
            links.add(shipLink);
        }
        scene.idle(5);
        world.showSectionAndMerge(util.select().position(burnerPos.west(2)), Direction.DOWN, shipLink);
        scene.idle(2);
        ElementLink leverLink = world.showIndependentSection(util.select().position(burnerPos.west()), Direction.DOWN);
        links.add(leverLink);
        scene.idle(2);
        ElementLink burnerLink = world.showIndependentSection(util.select().position(burnerPos), Direction.DOWN);
        links.add(burnerLink);
        scene.idle(2);
        world.showSectionAndMerge(util.select().position(burnerPos.east(2)), Direction.DOWN, shipLink);
        world.showSectionAndMerge(util.select().position(burnerPos.offset(2, 0, 1)), Direction.DOWN, shipLink);
        world.showSectionAndMerge(util.select().position(burnerPos.offset(3, 0, 1)), Direction.DOWN, shipLink);
        scene.idle(2);
        world.showSectionAndMerge(util.select().fromTo(1, 3, 4, 1, 5, 4), Direction.DOWN, shipLink);
        world.showSectionAndMerge(util.select().fromTo(7, 3, 4, 7, 5, 4), Direction.DOWN, shipLink);
        scene.idle(3);
        for (int i = 0; i < 3; ++i) {
            world.showSectionAndMerge(util.select().fromTo(1, 6 + i, 3, 7, 6 + i, 6), Direction.DOWN, shipLink);
            world.showSection(util.select().fromTo(2, 6 + i, 2, 6, 6 + i, 2), Direction.DOWN);
            scene.idle(4);
        }
        scene.idle(2);
        scene.overlay().showText(80).text(pluralTitle + " generate Lift by filling Balloons with " + gasTitle).attachKeyFrame().pointAt(Vec3.atCenterOf((Vec3i)burnerPos).add(0.0, 0.0, 0.5)).placeNearTarget();
        scene.idle(95);
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(burnerPos, burnerPos.west()), 8));
        scene.effects().indicateRedstone(burnerPos.west());
        scene.idle(15);
        scene.overlay().showText(70).text("When Powered with Redstone, the " + singularTitle + " will gradually fill the Balloon above itself").attachKeyFrame().pointAt(Vec3.atCenterOf((Vec3i)burnerPos).add(-0.5, 0.0, 0.0)).placeNearTarget();
        scene.idle(30);
        world.hideSection(util.select().fromTo(2, 6, 2, 6, 7, 2), Direction.NORTH);
        scene.idle(30);
        Vec3 v = util.vector().blockSurface(burnerPos, Direction.UP);
        AABB bb = new AABB(v, v);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb.expandTowards(0.0, 3.5, 0.0), 30);
        scene.idle(10);
        v = util.vector().blockSurface(burnerPos.offset(0, 4, 0), Direction.DOWN);
        AABB bb2 = new AABB(v, v);
        AABB bbFull = bb2.inflate(2.5, 1.0, 1.5);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bb2, 1, 4.0));
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull, 160, 4.0));
        scene.idle(30);
        double shipOffset = 1.5;
        for (ElementLink elementLink : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)elementLink, (Vec3)new Vec3(0.0, 1.5, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(30);
        if (isVent) {
            scene.overlay().showText(80).text("Like Steam Engines, sufficient Heat, Water, and Boiler Space are required").attachKeyFrame().colored(PonderPalette.INPUT).pointAt(util.vector().of(6.0, 3.0, 5.5).add(0.0, shipOffset, 0.0)).placeNearTarget();
            scene.idle(100);
        }
        Vec3 blockSurface = util.vector().blockSurface(burnerPos, Direction.NORTH).add(0.0, shipOffset - 0.1875, 0.0);
        scene.overlay().showFilterSlotInput(blockSurface, Direction.NORTH, 75);
        scene.idle(5);
        scene.overlay().showText(60).pointAt(blockSurface).placeNearTarget().attachKeyFrame().text("The input panel can be used to select the maximum " + gasTitle + " output");
        scene.idle(65);
        scene.overlay().showControls(blockSurface, Pointing.RIGHT, 10).rightClick();
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull.contract(0.0, -0.5, 0.0), 120, 4.0));
        scene.idle(10);
        for (ElementLink elementLink : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)elementLink, (Vec3)new Vec3(0.0, -0.5, 0.0), (int)20, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(20);
        scene.overlay().showText(70).text("Adjusting the Redstone Input will scale the selected output proportionally").attachKeyFrame().pointAt(Vec3.atCenterOf((Vec3i)burnerPos).add(-1.0, shipOffset -= 0.5, 0.0)).placeNearTarget();
        scene.idle(80);
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(burnerPos, burnerPos.west()), 4));
        scene.effects().indicateRedstone(burnerPos.west().offset(0, 1, 0));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull.contract(0.0, -1.0, 0.0), 80, 4.0));
        scene.idle(10);
        for (ElementLink elementLink : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)elementLink, (Vec3)new Vec3(0.0, -1.0, 0.0), (int)20, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        shipOffset -= 1.0;
        scene.idle(30);
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(burnerPos, burnerPos.west()), 12));
        scene.effects().indicateRedstone(burnerPos.west());
        scene.idle(10);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull.expandTowards(0.0, -1.0, 0.0), 80, 4.0));
        scene.idle(15);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull, 120, 4.0));
        scene.idle(1);
        v = v.add(0.0, -2.0, 0.0);
        AABB aABB = new AABB(v, v).inflate(2.5, 0.0, 1.5);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)aABB, aABB, 1, 4.0));
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.RED, (Object)aABB, aABB.expandTowards(0.0, 1.0, 0.0), 90, 4.0));
        scene.idle(10);
        for (ElementLink elementLink : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)elementLink, (Vec3)new Vec3(0.0, 1.5, 0.0), (int)25, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(20);
        scene.overlay().showText(70).text("The Lift Force is limited by the size of the Balloon").attachKeyFrame().colored(PonderPalette.RED).pointAt(Vec3.atCenterOf((Vec3i)burnerPos).add(0.0, 2.0 + (shipOffset += 1.5), 0.0)).placeNearTarget();
        scene.idle(90);
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(burnerPos, burnerPos.west()), 0));
        scene.effects().indicateRedstone(burnerPos.west().offset(0, 2, 0));
        scene.idle(10);
        for (ElementLink elementLink : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)elementLink, (Vec3)new Vec3(0.0, -1.5, 0.0), (int)15, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(15);
        for (ElementLink elementLink : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)elementLink, (Vec3)new Vec3(0.0, 0.2, 0.0), (int)5, (FloatUnaryOperator)SmoothMovementUtils.quadraticJump()));
        }
        scene.idle(15);
        world.moveSection(leverLink, new Vec3(0.0, 0.0, -1.0), 10);
        world.moveSection(burnerLink, new Vec3(-1.0, 0.0, 0.0), 10);
        scene.idle(10);
        world.showSectionAndMerge(util.select().fromTo(secondBurnerPos, secondBurnerPos.north()), Direction.DOWN, shipLink);
        scene.idle(15);
        if (isVent) {
            world.hideIndependentSection(blazeLink, Direction.EAST);
            scene.idle(20);
            Selection selection = util.select().position(4, 1, 3);
            world.setBlocks(blazeSelection.substract(selection), (BlockState)AllBlocks.BLAZE_BURNER.getDefaultState().setValue((Property)BlazeBurnerBlock.HEAT_LEVEL, (Comparable)BlazeBurnerBlock.HeatLevel.KINDLED), false);
            for (BlockPos blockPos : blazeSelection.substract(selection)) {
                world.modifyBlockEntity(blockPos, BlazeBurnerBlockEntity.class, SmartBlockEntity::markVirtual);
            }
            world.setBlocks(selection, (BlockState)((BlockState)Blocks.DARK_OAK_STAIRS.defaultBlockState().setValue((Property)StairBlock.HALF, (Comparable)Half.TOP)).setValue((Property)StairBlock.FACING, (Comparable)Direction.SOUTH), false);
            world.showSectionAndMerge(blazeSelection, Direction.EAST, shipLink);
            scene.idle(20);
        }
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(burnerPos, burnerPos.west()), 6));
        scene.effects().indicateRedstone(burnerPos.west().north());
        scene.idle(10);
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(secondBurnerPos, secondBurnerPos.north()), 6));
        scene.effects().indicateRedstone(secondBurnerPos.north());
        scene.idle(15);
        v = util.vector().blockSurface(burnerPos.west(), Direction.UP);
        bb = new AABB(v, v);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb.expandTowards(0.0, 3.5, 0.0), 20);
        v = util.vector().blockSurface(secondBurnerPos, Direction.UP);
        AABB aABB2 = new AABB(v, v);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)aABB2, aABB2, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)aABB2, aABB2.expandTowards(0.0, 3.5, 0.0), 20);
        scene.idle(8);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bb2, 1, 4.0));
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull, 80, 4.0));
        scene.idle(10);
        for (ElementLink elementLink : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)elementLink, (Vec3)new Vec3(0.0, 1.5, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(30);
        scene.overlay().showText(70).text("Multiple " + pluralTitle + " can combine their output into a single Balloon").attachKeyFrame().pointAt(Vec3.atCenterOf((Vec3i)burnerPos.west()).add(0.0, shipOffset, 0.0)).placeNearTarget();
        scene.idle(100);
        if (isVent) {
            scene.overlay().showText(90).text("Note an adequate power level is needed for multiple Steam Vents to run at full capacity").attachKeyFrame().colored(PonderPalette.RED).pointAt(Vec3.atCenterOf((Vec3i)new BlockPos(3, 1, 3)).add(-0.5, shipOffset, 0.0)).placeNearTarget();
            scene.idle(120);
        }
        if (isVent) {
            scene.overlay().showControls(util.vector().blockSurface(burnerPos.west(), Direction.NORTH).add(0.0, shipOffset, 0.0), Pointing.RIGHT, 30).withItem(new ItemStack((ItemLike)AllItems.IRON_SHEET.get()));
            scene.idle(5);
            scene.world().setBlock(burnerPos, (BlockState)((BlockState)AeroBlocks.STEAM_VENT.getDefaultState().setValue(SteamVentBlock.VARIANT, (Comparable)((Object)SteamVentBlock.Variant.IRON))).setValue((Property)SteamVentBlock.FACING, (Comparable)Direction.WEST), true);
            scene.idle(5);
            scene.overlay().showText(80).text("For aesthetic purposes, the metal can be changed with an Iron Sheet").attachKeyFrame().pointAt(util.vector().blockSurface(burnerPos.west(), Direction.WEST).add(0.0, shipOffset, 0.0)).placeNearTarget();
        } else {
            scene.overlay().showControls(util.vector().blockSurface(burnerPos.west(), Direction.NORTH).add(0.0, shipOffset, 0.0), Pointing.RIGHT, 30).withItem(new ItemStack((ItemLike)Blocks.SOUL_SAND.asItem()));
            scene.idle(5);
            scene.world().setBlock(burnerPos, (BlockState)AeroBlocks.HOT_AIR_BURNER.getDefaultState().setValue(HotAirBurnerBlock.VARIANT, (Comparable)((Object)HotAirBurnerBlock.Variant.SOUL_FIRE)), true);
            scene.idle(5);
            scene.overlay().showText(80).text("For aesthetic purposes, the fire type can be changed with a Soul-Infused item").attachKeyFrame().pointAt(util.vector().blockSurface(burnerPos.west(), Direction.WEST).add(0.0, shipOffset, 0.0)).placeNearTarget();
        }
        scene.idle(30);
    }

    public static void envelope(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("envelope", "Constructing Balloons");
        scene.scaleSceneView(0.7f);
        scene.setSceneOffsetY(-2.0f);
        scene.configureBasePlate(0, 0, 9);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(9.0f, 0.0f, true));
        BlockPos burnerPos = new BlockPos(4, 2, 4);
        BlockPos envelopePos = new BlockPos(1, 6, 4);
        BlockPos shaftPos = new BlockPos(0, 7, 4);
        BlockPos encasedShaftPos = new BlockPos(1, 6, 0);
        scene.idle(10);
        ArrayList<ElementLink> links = new ArrayList<ElementLink>();
        ElementLink shipLink = scene.world().showIndependentSection(util.select().layer(1), Direction.DOWN);
        links.add(shipLink);
        scene.idle(5);
        for (int i = 0; i < 4; ++i) {
            int k = i == 3 ? 1 : 0;
            scene.world().showSectionAndMerge(util.select().position(burnerPos.offset(i - 2 + k, 0, 0)), Direction.DOWN, shipLink);
            scene.idle(2 + k);
        }
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(util.grid().at(6, 2, 4), true, true));
        scene.world().showSectionAndMerge(util.select().fromTo(1, 3, 2, 1, 6, 2), Direction.DOWN, shipLink);
        scene.world().showSectionAndMerge(util.select().fromTo(7, 3, 2, 7, 6, 2), Direction.DOWN, shipLink);
        scene.world().showSectionAndMerge(util.select().fromTo(1, 3, 6, 1, 6, 6), Direction.DOWN, shipLink);
        scene.world().showSectionAndMerge(util.select().fromTo(7, 3, 6, 7, 6, 6), Direction.DOWN, shipLink);
        scene.world().showSectionAndMerge(util.select().fromTo(1, 2, 2, 7, 2, 6).substract(util.select().fromTo(2, 2, 3, 6, 2, 5)), Direction.DOWN, shipLink);
        scene.idle(3);
        ElementLink shaftLink = null;
        for (int i = 0; i < 4; ++i) {
            Selection sel = util.select().fromTo(1, 5 + i, 3, 7, 5 + i, 6);
            if (i == 1) {
                sel = sel.substract(util.select().position(envelopePos));
                shaftLink = scene.world().showIndependentSection(util.select().position(envelopePos), Direction.DOWN);
                links.add(shaftLink);
            }
            scene.world().showSectionAndMerge(sel, Direction.DOWN, shipLink);
            scene.world().showSection(util.select().fromTo(2, 5 + i, 2, 6, 5 + i, 2), Direction.DOWN);
            scene.idle(4);
        }
        scene.idle(2);
        scene.overlay().showText(80).text("Envelope Blocks enclose a volume that generates Lift when filled with Hot Air").attachKeyFrame().pointAt(Vec3.atCenterOf((Vec3i)burnerPos).add(0.0, 4.0, 0.5)).placeNearTarget();
        scene.idle(75);
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(burnerPos, burnerPos.west()), 8));
        scene.effects().indicateRedstone(burnerPos.west());
        scene.idle(15);
        scene.world().hideSection(util.select().fromTo(2, 5, 2, 6, 7, 2), Direction.NORTH);
        scene.idle(20);
        Vec3 v = util.vector().blockSurface(burnerPos, Direction.UP);
        AABB bb = new AABB(v, v);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb.expandTowards(0.0, 4.5, 0.0), 30);
        scene.idle(10);
        for (int i = 0; i < 3; ++i) {
            v = util.vector().blockSurface(burnerPos, Direction.DOWN).add(0.0, 5.5 - (double)i, 0.0);
            AABB bb2 = new AABB(v, v);
            AABB bbFull = bb2.inflate(2.5 - (double)(i == 0 ? 1 : 0), 0.5, 1.5);
            scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bb2, 1, 4.0));
            scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull, 160 - 12 * i, 4.0));
            scene.idle(12);
        }
        scene.overlay().showText(70).text("Hot Air sources fill Balloons from the top downwards").attachKeyFrame().pointAt(Vec3.atCenterOf((Vec3i)burnerPos).add(0.0, 4.0, 0.5)).placeNearTarget();
        scene.idle(75);
        for (ElementLink link : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(0.0, 1.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(35);
        scene.world().hideIndependentSection(shaftLink, Direction.WEST);
        links.remove(shaftLink);
        scene.idle(20);
        shaftLink = scene.world().showIndependentSection(util.select().position(encasedShaftPos.above()), Direction.EAST);
        links.add(shaftLink);
        scene.world().moveSection(shaftLink, new Vec3(0.0, 0.0, 4.0), 0);
        scene.idle(10);
        v = util.vector().blockSurface(burnerPos.above(), Direction.UP);
        bb = new AABB(v, v);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)bb, bb.expandTowards(0.0, 4.5, 0.0), 30);
        scene.idle(10);
        AABB bb2 = null;
        AABB bbFull = null;
        for (int i = 0; i < 2; ++i) {
            v = util.vector().blockSurface(burnerPos, Direction.DOWN).add(0.0, 5.5 - (double)i, 0.0);
            bb2 = new AABB(v, v);
            bbFull = bb2.inflate(2.5 - (double)(i == 0 ? 1 : 0), 0.5, 1.5);
            scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bb2, 1, 4.0));
            scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb2, bbFull, i == 0 ? 100 : 27, 4.0));
            scene.idle(12);
        }
        AABB bb3 = new AABB(v.add(-2.5, 0.0, 0.0), v.add(-2.5, 0.0, 0.0)).inflate(0.0, 0.5, 0.5);
        AABB bbFull3 = bb3.expandTowards(-2.0, 0.0, 0.0);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb3, bb3, 1, 4.0));
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.INPUT, (Object)bb3, bbFull3, 15, 4.0));
        scene.idle(15);
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.RED, (Object)bb3, bbFull3.contract(0.0, -1.0, 0.0), 10, 4.0));
        scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(shipLink, PonderPalette.RED, (Object)bb2, bbFull.contract(0.0, -1.0, 0.0), 10, 4.0));
        scene.idle(5);
        scene.overlay().showText(60).text("Hot Air must be contained by Airtight Blocks").attachKeyFrame().colored(PonderPalette.RED).pointAt(Vec3.atCenterOf((Vec3i)envelopePos).add(-0.5, 1.0, 0.0)).placeNearTarget();
        scene.idle(60);
        scene.world().hideIndependentSection(shaftLink, Direction.WEST);
        links.remove(shaftLink);
        scene.idle(25);
        shaftLink = scene.world().showIndependentSection(util.select().position(shaftPos), Direction.DOWN);
        links.add(shaftLink);
        scene.idle(10);
        BlockEntry envelopeEncased = AeroBlocks.ENVELOPE_ENCASED_SHAFTS.get(DyeColor.WHITE);
        ItemStack envelopeItem = AeroBlocks.WHITE_ENVELOPE_BLOCK.asStack();
        scene.overlay().showControls(util.vector().topOf(shaftPos), Pointing.DOWN, 20).rightClick().withItem(envelopeItem);
        scene.idle(7);
        scene.world().setBlocks(util.select().position(shaftPos), (BlockState)envelopeEncased.getDefaultState().setValue((Property)EncasedShaftBlock.AXIS, (Comparable)Direction.Axis.X), true);
        CreateSceneBuilder createScene = new CreateSceneBuilder(scene.getScene().builder());
        createScene.world().setKineticSpeed(util.select().position(shaftPos), 32.0f);
        scene.idle(10);
        scene.world().moveSection(shaftLink, new Vec3(1.0, 0.0, 0.0), 10);
        scene.idle(12);
        scene.overlay().showText(60).text("Shafts can be encased with Envelope Blocks to make them airtight").attachKeyFrame().pointAt(Vec3.atCenterOf((Vec3i)shaftPos).add(0.5, 0.0, 0.0)).placeNearTarget();
        scene.idle(50);
        scene.rotateCameraY(55.0f);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(15.0f, 0.0f, true));
        scene.idle(20);
        ElementLink engine = scene.world().showIndependentSection(util.select().fromTo(2, 5, 0, 2, 6, 0), Direction.WEST);
        scene.world().moveSection(engine, new Vec3(0.0, 1.0, 4.0), 0);
        scene.world().showSectionAndMerge(util.select().position(encasedShaftPos.west()), Direction.EAST, engine);
        scene.idle(15);
        AABB aabb = new AABB(new BlockPos(2, 6, 4)).expandTowards(0.0, 1.0, 0.0);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, (Object)aabb, aabb, 90);
        scene.idle(15);
        scene.overlay().showText(60).text("Blocks inside the Balloon will reduce the available Volume").attachKeyFrame().colored(PonderPalette.RED).pointAt(Vec3.atCenterOf((Vec3i)envelopePos.above()).add(1.0, 0.0, 0.0)).placeNearTarget();
        scene.idle(70);
        scene.world().showSectionAndMerge(util.select().fromTo(2, 5, 2, 6, 7, 2), Direction.SOUTH, shipLink);
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(4, 7, 2), Direction.NORTH), Pointing.RIGHT, 30).withItem(new ItemStack((ItemLike)Items.BLUE_DYE));
        scene.idle(7);
        scene.world().setBlock(new BlockPos(4, 6, 2), AeroBlocks.DYED_ENVELOPE_BLOCKS.get(DyeColor.BLUE).getDefaultState(), false);
        scene.idle(10);
        scene.overlay().showText(40).colored(PonderPalette.BLUE).text("Right-Click with Dye to paint them").attachKeyFrame().pointAt(util.vector().blockSurface(util.grid().at(4, 7, 2), Direction.WEST)).placeNearTarget();
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(4, 7, 2), Direction.NORTH), Pointing.RIGHT, 20).withItem(new ItemStack((ItemLike)Items.BLUE_DYE));
        scene.idle(7);
        scene.world().setBlocks(util.select().fromTo(5, 6, 2, 3, 6, 2).add(util.select().fromTo(4, 5, 2, 4, 7, 2)), AeroBlocks.DYED_ENVELOPE_BLOCKS.get(DyeColor.BLUE).getDefaultState(), false);
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(4, 7, 2), Direction.NORTH), Pointing.RIGHT, 20).withItem(new ItemStack((ItemLike)Items.BLUE_DYE));
        scene.idle(7);
        scene.world().modifyBlocks(util.select().layers(5, 4), state -> {
            if (state.is(AeroBlocks.WHITE_ENVELOPE_BLOCK)) {
                return AeroBlocks.DYED_ENVELOPE_BLOCKS.get(DyeColor.BLUE).getDefaultState();
            }
            return state;
        }, false);
    }
}
