/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$EffectInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.scene.OverlayInstructions
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.api.scene.SelectionUtil
 *  net.createmod.ponder.api.scene.VectorUtil
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.RedStoneWireBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.ponder.scenes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlock;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SwivelBearingScenes {
    public static void setSwivelCogKineticSpeed(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos swivelPos, Float rpm) {
        scene.world().modifyBlock(swivelPos, s -> (BlockState)s.setValue((Property)SwivelBearingBlock.ASSEMBLED, (Comparable)Boolean.valueOf(true)), false);
        scene.world().modifyBlockEntityNBT(util.select().position(swivelPos), SwivelBearingBlockEntity.class, nbt -> nbt.getCompound("SwivelCog").putFloat("Speed", rpm.floatValue()));
    }

    public static void swivelBearingIntro(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("swivel_bearing_intro", "Moving Structures using the Swivel Bearing");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.setSceneOffsetY(-0.5f);
        world.showSection(select.position(5, 0, 3), Direction.UP);
        BlockPos swivelBearing = new BlockPos(2, 1, 2);
        Selection bearingPlatform = select.fromTo(0, 2, 2, 4, 3, 2);
        Selection bearingRedstoneDecor = select.fromTo(0, 4, 2, 4, 4, 2);
        Selection lampAndLever = select.position(0, 4, 2).add(select.position(4, 4, 2));
        Selection cogs = select.fromTo(3, 1, 2, 4, 1, 3);
        Selection kinetics = select.position(swivelBearing).add(select.position(4, 1, 3));
        Selection inverseKinetics = select.position(3, 1, 2).add(select.position(5, 0, 3));
        scene.idle(10);
        world.showSection(cogs, Direction.DOWN);
        scene.idle(10);
        world.showSection(select.position(swivelBearing), Direction.DOWN);
        scene.idle(20);
        overlay.showText(80).text("Swivel Bearings attach to the block in front of them").pointAt(vector.topOf(swivelBearing)).colored(PonderPalette.GREEN).placeNearTarget();
        AABB bb1 = AABB.unitCubeFromLowerCorner((Vec3)new Vec3(2.0, 2.0, 2.0));
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)bb1, bb1, 90);
        scene.idle(70);
        ElementLink contraption = scene.world().showIndependentSectionImmediately(select.position(2, 2, 2));
        world.moveSection(contraption, new Vec3(0.0, -1.0, 0.0), 0);
        scene.world().showSectionAndMerge(select.position(2, 3, 2), Direction.DOWN, contraption);
        scene.idle(10);
        scene.effects().superGlue(swivelBearing.above(), Direction.DOWN, true);
        world.showSectionAndMerge(bearingPlatform.substract(select.fromTo(2, 2, 2, 2, 3, 2)), Direction.DOWN, contraption);
        scene.idle(10);
        world.showSectionAndMerge(bearingRedstoneDecor, Direction.DOWN, contraption);
        scene.idle(10);
        scene.overlay().showControls(util.vector().centerOf(4, 2, 2), Pointing.RIGHT, 40).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        scene.idle(5);
        AABB bb2 = new AABB(util.grid().at(4, 2, 2));
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb2, bb2, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb2, bb2.expandTowards(-4.0, 1.0, 0.0), 80);
        scene.idle(10);
        overlay.showText(70).text("Use Super Glue or Honey Glue to select a group of blocks").pointAt(vector.centerOf(0, 2, 2)).colored(PonderPalette.OUTPUT).attachKeyFrame().placeNearTarget();
        scene.idle(90);
        world.setKineticSpeed(kinetics, 8.0f);
        world.setKineticSpeed(inverseKinetics, -8.0f);
        SwivelBearingScenes.setSwivelCogKineticSpeed(scene, util, swivelBearing, Float.valueOf(8.0f));
        world.rotateSection(contraption, 0.0, 245.0, 0.0, 100);
        overlay.showText(80).text("When powered via the cog, it will assemble into a Simulated Contraption").pointAt(vector.topOf(swivelBearing.above())).attachKeyFrame().placeNearTarget();
        scene.idle(100);
        world.toggleRedstonePower(lampAndLever);
        effects.indicateRedstone(new BlockPos(3, 3, 0));
        int i = 0;
        while (i < 3) {
            int finalI = i++;
            scene.world().modifyBlock(new BlockPos(1 + finalI, 4, 2), s -> (BlockState)s.setValue((Property)RedStoneWireBlock.POWER, (Comparable)Integer.valueOf(15 - finalI)), false);
        }
        world.rotateSection(contraption, 0.0, 565.0, 0.0, 235);
        scene.markAsFinished();
        scene.idle(235);
        world.setKineticSpeed(kinetics, 0.0f);
        world.setKineticSpeed(inverseKinetics, 0.0f);
        SwivelBearingScenes.setSwivelCogKineticSpeed(scene, util, swivelBearing, Float.valueOf(0.0f));
    }

    public static void swivelBearingUnlocking(SceneBuilder builder, SceneBuildingUtil util) {
        int direction;
        int i;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("swivel_bearing_unlocking", "Unlocking Swivel Bearings");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.setSceneOffsetY(-1.0f);
        world.showSection(select.position(1, 0, 5), Direction.UP);
        BlockPos swivelBearing = new BlockPos(2, 4, 2);
        BlockPos leverPos = new BlockPos(1, 3, 2);
        Selection pendulum = select.fromTo(2, 2, 0, 2, 4, 0);
        Selection kinetics = select.position(1, 0, 5).add(select.position(2, 2, 2));
        Selection inverseKinetics = select.fromTo(2, 1, 2, 2, 1, 5).add(select.position(2, 3, 2));
        scene.idle(10);
        world.showSection(select.fromTo(2, 1, 2, 2, 1, 5), Direction.DOWN);
        scene.idle(5);
        world.showSection(select.position(2, 2, 2), Direction.DOWN);
        scene.idle(5);
        world.showSection(select.fromTo(1, 3, 2, 2, 3, 2), Direction.DOWN);
        scene.idle(5);
        world.showSection(select.position(2, 4, 2), Direction.DOWN);
        scene.idle(20);
        ElementLink contraption = world.showIndependentSectionImmediately(select.position(2, 4, 1));
        world.moveSection(contraption, new Vec3(-0.005, 0.01, 0.99), 0);
        SwivelBearingScenes.setSwivelCogKineticSpeed(scene, util, swivelBearing, Float.valueOf(0.0f));
        world.configureCenterOfRotation(contraption, vector.centerOf(swivelBearing));
        world.showSectionAndMerge(pendulum, Direction.SOUTH, contraption);
        scene.idle(20);
        world.setKineticSpeed(kinetics, 8.0f);
        world.setKineticSpeed(inverseKinetics, -8.0f);
        SwivelBearingScenes.setSwivelCogKineticSpeed(scene, util, swivelBearing, Float.valueOf(8.0f));
        world.rotateSection(contraption, 0.0, 0.0, 45.0, 20);
        scene.idle(20);
        world.setKineticSpeed(kinetics, 0.0f);
        world.setKineticSpeed(inverseKinetics, 0.0f);
        SwivelBearingScenes.setSwivelCogKineticSpeed(scene, util, swivelBearing, Float.valueOf(0.0f));
        scene.idle(20);
        overlay.showText(80).text("When provided with Redstone Power...").pointAt(vector.of(1.75, 3.5, 2.5)).attachKeyFrame().colored(PonderPalette.INPUT).placeNearTarget();
        scene.idle(40);
        world.toggleRedstonePower(select.position(leverPos));
        effects.indicateRedstone(leverPos);
        for (i = 1; i < 5; ++i) {
            direction = 1 - 2 * (i % 2);
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)contraption, new Vec3(0.0, 0.0, (double)(direction * (90 - 18 * i))), 20, SmoothMovementUtils.quadraticRiseInOut()));
            if (i == 4) {
                overlay.showText(80).text("...the Swivel Bearing unlocks, spinning freely").pointAt(vector.centerOf(2, 2, 1)).colored(PonderPalette.OUTPUT).placeNearTarget();
            }
            scene.idle(20);
        }
        for (i = 1; i < 3; ++i) {
            direction = 1 - 2 * (i % 2);
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)contraption, new Vec3(0.0, 0.0, (double)(direction * (18 - 6 * i))), 20, SmoothMovementUtils.cubicSmoothing()));
            scene.idle(20);
        }
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)contraption, new Vec3(0.0, 0.0, -3.0), 20, SmoothMovementUtils.cubicSmoothing()));
        scene.idle(40);
        Vec3 valuePanelPos = new Vec3(2.5, 5.0, 2.85);
        overlay.showText(80).text("This behavior can be configured using the value panel").pointAt(valuePanelPos).attachKeyFrame().placeNearTarget();
        scene.overlay().showControls(valuePanelPos, Pointing.DOWN, 80).rightClick();
        overlay.showFilterSlotInput(valuePanelPos, Direction.UP, 80);
        scene.idle(40);
        scene.markAsFinished();
    }

    public static void swivelBearingPassthrough(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        scene.title("swivel_bearing_passthrough", "Passing Rotation through a Swivel Bearing");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos swivelBearing = new BlockPos(2, 3, 2);
        BlockPos thatOneShaftThatIHate = new BlockPos(2, 1, 2);
        BlockPos speedometer = new BlockPos(0, 1, 2);
        Selection mainShaftLeft = select.fromTo(3, 1, 2, 4, 1, 2);
        Selection mainShaftRight = select.fromTo(0, 1, 2, 1, 1, 2);
        Selection backShaft = select.fromTo(5, 1, 2, 5, 3, 2);
        Selection contraptionBase = select.position(1, 3, 2);
        Selection shaftKinetics = select.fromTo(0, 3, 0, 0, 3, 1).add(select.fromTo(0, 1, 2, 4, 1, 2)).add(select.fromTo(1, 2, 2, 5, 3, 2));
        Selection shaftInverseKinetics = select.fromTo(5, 1, 2, 5, 2, 2).add(select.position(0, 3, 2));
        Selection cogKinetics = select.position(5, 1, 3);
        Selection cogInverseKinetics = select.fromTo(2, 2, 3, 5, 2, 3);
        Selection cogShaft = select.position(5, 1, 3).add(cogInverseKinetics);
        scene.idle(10);
        ElementLink mainShaftLeftLink = world.showIndependentSection(mainShaftLeft, Direction.DOWN);
        ElementLink mainShaftRightLink = world.showIndependentSection(mainShaftRight, Direction.DOWN);
        world.showSection(select.position(thatOneShaftThatIHate), Direction.DOWN);
        ElementLink backShaftLink = world.showIndependentSection(backShaft, Direction.DOWN);
        world.moveSection(backShaftLink, vector.of(0.0, -2.0, 0.0), 0);
        world.setKineticSpeed(shaftKinetics, 32.0f);
        world.setKineticSpeed(shaftInverseKinetics, -32.0f);
        scene.idle(20);
        world.hideSection(select.position(thatOneShaftThatIHate), Direction.SOUTH);
        scene.idle(15);
        ElementLink swivelLink = world.showIndependentSection(select.position(swivelBearing), Direction.SOUTH);
        world.moveSection(swivelLink, vector.of(0.0, -2.0, 0.0), 0);
        ArrayList<ElementLink> linkedStructures = new ArrayList<ElementLink>(List.of(swivelLink, backShaftLink, mainShaftLeftLink, mainShaftRightLink));
        scene.idle(20);
        overlay.showText(80).text("Rotational power via the Shaft passes directly through the Swivel Bearing").pointAt(vector.centerOf(speedometer)).attachKeyFrame().placeNearTarget();
        scene.idle(87);
        scene.idle(13);
        for (ElementLink structure : linkedStructures) {
            world.moveSection(structure, vector.of(0.0, 1.0, 0.0), 10);
        }
        scene.idle(10);
        world.hideIndependentSection(mainShaftRightLink, Direction.UP);
        scene.idle(5);
        world.moveSection(world.showIndependentSection(cogShaft, Direction.NORTH), vector.of(0.0, -1.0, 0.0), 0);
        world.setBlock(thatOneShaftThatIHate, AllBlocks.ANDESITE_SCAFFOLD.getDefaultState(), false);
        world.showIndependentSection(select.position(thatOneShaftThatIHate), Direction.SOUTH);
        scene.idle(10);
        ElementLink contraption = world.showIndependentSectionImmediately(contraptionBase);
        world.configureCenterOfRotation(contraption, vector.centerOf(swivelBearing));
        world.moveSection(contraption, vector.of(1.0, -1.0, 0.0), 0);
        scene.rotateCameraY(-90.0f);
        for (int i = 0; i < 3; ++i) {
            world.showSectionAndMerge(select.position(0, 3, 2 - i), Direction.DOWN, contraption);
            scene.idle(5);
        }
        scene.overlay().showControls(util.vector().centerOf(1, 2, 0), Pointing.RIGHT, 40).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        scene.idle(5);
        AABB bb2 = new AABB(util.grid().at(1, 2, 0));
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb2, bb2, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb2, bb2.expandTowards(0.0, 0.0, 2.0), 40);
        scene.idle(40);
        world.setKineticSpeed(cogKinetics, 8.0f);
        world.setKineticSpeed(cogInverseKinetics, -8.0f);
        SwivelBearingScenes.setSwivelCogKineticSpeed(scene, util, swivelBearing, Float.valueOf(8.0f));
        world.rotateSection(contraption, 135.0, 0.0, 0.0, 54);
        scene.idle(54);
        world.setKineticSpeed(cogKinetics, 0.0f);
        world.setKineticSpeed(cogInverseKinetics, 0.0f);
        SwivelBearingScenes.setSwivelCogKineticSpeed(scene, util, swivelBearing, Float.valueOf(0.0f));
        overlay.showText(160).text("Rotation remains uninterrupted as the Swivel Bearing turns").pointAt(vector.of(1.5, 3.0, 3.0)).attachKeyFrame().placeNearTarget();
        scene.idle(60);
    }
}
