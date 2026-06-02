/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity
 *  com.simibubi.create.content.kinetics.press.PressingBehaviour$Mode
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$EffectInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.catnip.theme.Color
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.ParrotElement
 *  net.createmod.ponder.api.element.ParrotPose$FacePointOfInterestPose
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.scene.OverlayInstructions
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.api.scene.SelectionUtil
 *  net.createmod.ponder.api.scene.VectorUtil
 *  net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.simulated_team.simulated.client.BlockPropertiesTooltip;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateParrotInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomMoveBaseShadowInstruction;
import dev.simulated_team.simulated.ponder.instructions.OBBOutlineInstruction;
import dev.simulated_team.simulated.ponder.instructions.OffsetBreakParticlesInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import dev.simulated_team.simulated.service.SimConfigService;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.theme.Color;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PhysicsAssemblerScenes {
    public static void physicsAssemblerIntro(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("physics_assembler_intro", "Assembling Simulated Contraptions");
        scene.configureBasePlate(0, 0, 5);
        scene.setSceneOffsetY(-1.0f);
        ElementLink baseplate = world.showIndependentSection(select.layer(0), Direction.UP);
        BlockPos assembler1 = new BlockPos(3, 2, 1);
        BlockPos assembler2 = new BlockPos(1, 2, 2);
        Selection structure = select.fromTo(3, 1, 1, 1, 4, 3);
        world.modifyBlockEntity(assembler2, PhysicsAssemblerBlockEntity.class, be -> {
            be.clientFlickLeverTo(true);
            be.jerkLever();
        });
        scene.idle(5);
        ElementLink structureIntro1 = world.showIndependentSection(select.fromTo(3, 1, 1, 3, 1, 3), Direction.DOWN);
        scene.idle(5);
        ElementLink structureIntro2 = world.showIndependentSection(select.fromTo(3, 2, 3, 3, 3, 3), Direction.DOWN);
        scene.idle(5);
        ElementLink structureIntro3 = world.showIndependentSection(select.fromTo(1, 2, 3, 2, 3, 3).add(select.position(3, 4, 3)), Direction.DOWN);
        scene.idle(10);
        ElementLink structureIntro4 = world.showIndependentSection(select.position(assembler1), Direction.DOWN);
        scene.idle(15);
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, null, structureIntro1));
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, null, structureIntro2));
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, null, structureIntro3));
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, null, structureIntro4));
        ElementLink assembledStructure = world.showIndependentSectionImmediately(structure.substract(select.position(assembler2)));
        scene.idle(5);
        overlay.showText(80).text("The Physics Assembler assembles blocks into Simulated Contraptions").pointAt(vector.centerOf(assembler1)).placeNearTarget();
        scene.idle(100);
        scene.addKeyframe();
        overlay.showControls(vector.centerOf(3, 3, 2), Pointing.RIGHT, 20).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        scene.overlay().showOutline(PonderPalette.OUTPUT, (Object)"honey_glue", select.fromTo(3, 1, 1, 3, 1, 3).add(select.fromTo(3, 2, 3, 3, 4, 3)).add(select.position(2, 3, 3)).add(select.position(1, 2, 3)), 100);
        scene.idle(20);
        overlay.showText(80).text("Use Super Glue or Honey Glue to select a group of blocks...").placeNearTarget().colored(PonderPalette.OUTPUT).pointAt(vector.blockSurface(new BlockPos(2, 3, 3), Direction.WEST));
        scene.idle(100);
        overlay.showControls(vector.centerOf(3, 2, 1), Pointing.DOWN, 20).rightClick();
        overlay.showText(90).text("...then hold right-click and pull the lever to assemble").placeNearTarget().colored(PonderPalette.OUTPUT).attachKeyFrame().pointAt(vector.blockSurface(new BlockPos(3, 2, 1), Direction.WEST));
        world.configureCenterOfRotation(assembledStructure, new Vec3(3.0, 1.0, 3.0));
        scene.idle(3);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assembler1, true, false));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)assembledStructure, new Vec3(0.0, 0.0, 27.0), 20, SmoothMovementUtils.cubicRise()));
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)assembledStructure, new Vec3(0.0, 0.0, -3.0), 7, SmoothMovementUtils.quadraticJump()));
        scene.idle(7);
        scene.idle(80);
        overlay.showLine(PonderPalette.INPUT, new Vec3(1.1, 2.4, 3.0), new Vec3(1.1, 2.4, 4.0), 80);
        overlay.showText(80).text("Note that blocks along diagonal edges are included").colored(PonderPalette.INPUT).placeNearTarget().pointAt(new Vec3(1.1, 2.4, 3.5));
        scene.idle(110);
        world.setBlock(assembler1, Blocks.AIR.defaultBlockState(), false);
        scene.addInstruction((PonderInstruction)new OffsetBreakParticlesInstruction(AABB.unitCubeFromLowerCorner((Vec3)new Vec3(2.5, 2.0, 1.0)), SimBlocks.PHYSICS_ASSEMBLER.getDefaultState()));
        scene.idle(20);
        overlay.showText(80).text("Once assembled, the original Physics Assembler is no longer needed...").placeNearTarget().attachKeyFrame().pointAt(vector.blockSurface(new BlockPos(3, 2, 1), Direction.WEST));
        scene.idle(100);
        ElementLink assembler2Section = world.showIndependentSection(select.position(assembler2), Direction.DOWN);
        world.configureCenterOfRotation(assembler2Section, new Vec3(3.0, 1.0, 3.0));
        world.rotateSection(assembler2Section, 0.0, 0.0, 27.0, 0);
        overlay.showText(80).text("...and any Physics Assembler can be used for disassembly").placeNearTarget().attachKeyFrame().pointAt(vector.centerOf(new BlockPos(0, 1, 2)));
        scene.idle(40);
        overlay.showControls(new Vec3(1.0, 2.0, 2.5), Pointing.DOWN, 20).rightClick();
        scene.idle(3);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assembler2, false, false));
        scene.idle(5);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)assembledStructure, new Vec3(0.0, 0.0, -27.0), 25, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)assembler2Section, new Vec3(0.0, 0.0, -27.0), 25, SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(25);
        scene.markAsFinished();
        scene.idle(100);
        overlay.showControls(vector.centerOf(4, 0, 0), Pointing.RIGHT, 10).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        effects.emitParticles(vector.centerOf(4, 0, 0), effects.particleEmitterWithinBlockSpace((ParticleOptions)new DustParticleOptions(new Color(255, 232, 142).asVectorF(), 1.0f), Vec3.ZERO), 10.0f, 2);
        AABB funnyGlue = new AABB(new BlockPos(4, 0, 0));
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)funnyGlue, funnyGlue, 20);
        scene.idle(20);
        overlay.showControls(vector.blockSurface(new BlockPos(0, 4, 4), Direction.WEST), Pointing.LEFT, 10).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)funnyGlue, funnyGlue.expandTowards(-4.0, 4.0, 4.0), 30);
        scene.idle(30);
        overlay.showControls(vector.centerOf(1, 2, 2), Pointing.DOWN, 10).rightClick();
        scene.idle(3);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assembler2, true, false));
        scene.idle(10);
        scene.idle(5);
        scene.addInstruction((PonderInstruction)CustomMoveBaseShadowInstruction.delta(new Vec3(0.0, -20.0, 0.0), 55, (UnaryOperator<Float>)SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembledStructure, new Vec3(0.0, -20.0, 0.0), 55, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembler2Section, new Vec3(0.0, -20.0, 0.0), 55, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)baseplate, new Vec3(0.0, -20.0, 0.0), 55, SmoothMovementUtils.quadraticRise()));
    }

    public static void physicsAssemblerSimulatedContraptions(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("physics_assembler_simulated_contraptions", "Interacting with Simulated Contraptions");
        scene.configureBasePlate(0, 0, 15);
        scene.scaleSceneView(0.5f);
        scene.showBasePlate();
        BlockPos[] depot = new BlockPos[]{new BlockPos(10, 1, 4), new BlockPos(7, 1, 4), new BlockPos(4, 1, 4)};
        BlockPos iceCarAssembler = new BlockPos(6, 2, 8);
        BlockPos iceCarPortable = new BlockPos(7, 3, 8);
        Selection iceCarLeftRedstone = select.fromTo(9, 2, 7, 9, 3, 7);
        Selection iceCarRightRedstone = select.fromTo(9, 2, 9, 9, 3, 9);
        Selection iceCarLeftKinetics = select.fromTo(9, 2, 7, 10, 2, 7);
        Selection iceCarRightKinetics = select.fromTo(9, 2, 9, 10, 2, 9);
        Selection iceCarSelection = select.fromTo(5, 1, 6, 10, 3, 10);
        BlockPos carAssembler = new BlockPos(7, 6, 6);
        BlockPos carPortable = new BlockPos(7, 6, 10);
        BlockPos[] carBearing = new BlockPos[]{new BlockPos(6, 5, 6), new BlockPos(8, 5, 6), new BlockPos(6, 5, 10), new BlockPos(8, 5, 10)};
        Selection[] carWheelSelection = new Selection[]{select.fromTo(5, 4, 5, 5, 6, 7), select.fromTo(9, 4, 5, 9, 6, 7), select.fromTo(5, 4, 9, 5, 6, 11), select.fromTo(9, 4, 9, 9, 6, 11)};
        Selection carBodySelection = select.fromTo(6, 5, 6, 8, 7, 10);
        BlockPos pressmobileAssembler = new BlockPos(8, 9, 9);
        BlockPos pressmobilePortable = new BlockPos(8, 10, 8);
        BlockPos pressmobilePress = new BlockPos(8, 10, 3);
        Selection pressmobileSelection = select.fromTo(4, 8, 3, 10, 10, 10);
        BlockPos gliderAssembler = new BlockPos(6, 12, 7);
        BlockPos gliderPortable = new BlockPos(9, 12, 7);
        Selection gliderRedstone = select.fromTo(10, 12, 6, 10, 12, 7);
        Selection gliderSelection = select.fromTo(3, 11, 4, 12, 13, 10);
        world.setKineticSpeed(select.position(iceCarPortable), 32.0f);
        world.setKineticSpeed(select.position(carPortable), -32.0f);
        world.setKineticSpeed(select.position(pressmobilePortable), -32.0f);
        world.setKineticSpeed(select.position(gliderPortable), -32.0f);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(iceCarAssembler, true, true));
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(carAssembler, true, true));
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(pressmobileAssembler, true, true));
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(gliderAssembler, true, true));
        scene.idle(10);
        ElementLink iceCar = world.showIndependentSection(iceCarSelection, Direction.WEST);
        world.rotateSection(iceCar, 0.0, -60.0, 0.0, 0);
        world.moveSection(iceCar, vector.of(5.0, 0.0, 3.0), 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)iceCar, new Vec3(-3.0, 0.0, 0.0), 60, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)iceCar, new Vec3(-10.0, 0.0, 0.0), 60, SmoothMovementUtils.quadraticRiseInOut()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)iceCar, new Vec3(5.0, 0.0, 0.0), 75, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)iceCar, new Vec3(0.0, 0.0, -9.0), 45, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)iceCar, new Vec3(0.0, 200.0, 0.0), 45, SmoothMovementUtils.cubicSmoothing()));
        scene.idle(10);
        world.toggleRedstonePower(iceCarRightRedstone);
        world.setKineticSpeed(iceCarRightKinetics, -128.0f);
        effects.indicateRedstone(new BlockPos(12, 2, 9));
        scene.idle(20);
        world.toggleRedstonePower(iceCarRightRedstone);
        world.setKineticSpeed(iceCarRightKinetics, 128.0f);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)iceCar, new Vec3(0.0, 0.0, 15.0), 45, SmoothMovementUtils.quadraticRise()));
        scene.idle(15);
        effects.indicateRedstone(new BlockPos(1, 3, 4));
        world.toggleRedstonePower(iceCarLeftRedstone);
        world.setKineticSpeed(iceCarLeftKinetics, -128.0f);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)iceCar, new Vec3(0.0, -45.0, 0.0), 45, SmoothMovementUtils.cubicSmoothing()));
        scene.idle(10);
        world.toggleRedstonePower(iceCarLeftRedstone);
        world.setKineticSpeed(iceCarLeftKinetics, 128.0f);
        world.hideIndependentSection(iceCar, Direction.SOUTH);
        ElementLink carBody = world.showIndependentSection(carBodySelection, Direction.DOWN);
        ArrayList<ElementLink> carWheel = new ArrayList<ElementLink>(List.of());
        for (Selection wheel : carWheelSelection) {
            carWheel.add(world.showIndependentSection(wheel, Direction.DOWN));
        }
        ArrayList<ElementLink> carParts = new ArrayList<ElementLink>(List.of(carBody));
        carParts.addAll(carWheel);
        for (ElementLink part : carParts) {
            world.moveSection(part, vector.of(2.0, -2.0, 2.5), 0);
        }
        for (BlockPos bearing : carBearing) {
            world.rotateBearing(bearing, -360.0f, 75);
        }
        for (ElementLink wheel : carWheel) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)wheel, new Vec3(-360.0, 0.0, 0.0), 75, SmoothMovementUtils.linear()));
        }
        for (ElementLink carPart : carParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)carPart, new Vec3(0.0, 0.0, -4.0), 30, SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(30);
        for (ElementLink part : carParts) {
            world.moveSection(part, new Vec3(0.0, 0.0, -6.0), 25);
        }
        scene.idle(15);
        ElementLink glider = world.showIndependentSection(gliderSelection, Direction.EAST);
        world.moveSection(glider, vector.of(-10.0, -3.0, 4.0), 0);
        world.rotateSection(glider, 0.0, 180.0, -10.0, 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)glider, new Vec3(4.0, -2.0, 0.0), 15, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)glider, new Vec3(0.0, 0.0, -5.0), 15, SmoothMovementUtils.quadraticRiseInOut()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)glider, new Vec3(0.0, 0.0, 10.0), 60, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(10);
        float n = 0.5f;
        FloatUnaryOperator angleFunctionDown = t -> (float)Math.sin(1.5707963267948966 * (double)SmoothMovementUtils.cubicRise().apply(t));
        FloatUnaryOperator angleFunctionHorizontal = t -> (float)Math.sin(1.5707963267948966 * (double)SmoothMovementUtils.linear().apply(t));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)carBody, new Vec3(0.0, 0.0, -3.0), 30, angleFunctionHorizontal));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)carBody, new Vec3(0.0, -3.0, 0.0), 30, angleFunctionDown));
        for (ElementLink carPart : carParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)carPart, new Vec3(0.0, 0.0, -4.0), 30, SmoothMovementUtils.linear()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)carPart, new Vec3(-45.0, 0.0, 0.0), 30, SmoothMovementUtils.quadraticRise()));
        }
        ElementLink[] frontWheels = new ElementLink[]{(ElementLink)carWheel.get(0), (ElementLink)carWheel.get(1)};
        ElementLink[] backWheels = new ElementLink[]{(ElementLink)carWheel.get(2), (ElementLink)carWheel.get(3)};
        for (ElementLink wheel : frontWheels) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, 0.0, -2.65), 30, angleFunctionHorizontal));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, -4.3, 0.0), 30, angleFunctionDown));
        }
        for (ElementLink wheel : backWheels) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, 0.0, -2.75), 30, angleFunctionHorizontal));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, -1.45, 0.0), 30, angleFunctionDown));
        }
        scene.idle(5);
        for (ElementLink carPart : carParts) {
            world.hideIndependentSection(carPart, Direction.DOWN);
        }
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)glider, new Vec3(25.0, -1.0, 0.0), 80, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)glider, new Vec3(0.0, -3.0, 0.0), 20, SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)glider, new Vec3(0.0, 0.0, -10.0), 80, SmoothMovementUtils.quadraticRise()));
        scene.idle(10);
        world.hideIndependentSection(glider, Direction.EAST);
        scene.idle(10);
        ElementLink pressmobile = world.showIndependentSection(pressmobileSelection, Direction.DOWN);
        world.moveSection(pressmobile, vector.of(2.0, -7.0, 1.0), 0);
        scene.special().movePointOfInterest(new BlockPos(0, 3, 8));
        ElementLink seatBirb = scene.special().createBirb(vector.of(8.0, 3.0, 9.0), ParrotPose.FacePointOfInterestPose::new);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)pressmobile, new Vec3(-0.66, 0.0, 0.0), 20, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, new Vec3(-0.66, 0.0, 0.0), 20, SmoothMovementUtils.quadraticRise()));
        Class<MechanicalPressBlockEntity> type = MechanicalPressBlockEntity.class;
        ItemStack iron = new ItemStack((ItemLike)Items.IRON_INGOT);
        ItemStack sheet = AllItems.IRON_SHEET.asStack();
        world.showSection(util.select().position(depot[1]), Direction.DOWN);
        scene.world().createItemOnBeltLike(depot[1], Direction.NORTH, iron);
        scene.idle(20);
        scene.addKeyframe();
        world.moveSection(pressmobile, vector.of(-10.0, 0.0, 0.0), 160);
        scene.special().moveParrot(seatBirb, vector.of(-10.0, 0.0, 0.0), 160);
        scene.idle(20);
        scene.world().modifyBlockEntity(pressmobilePress, type, pte -> pte.getPressingBehaviour().start(PressingBehaviour.Mode.BELT));
        scene.idle(15);
        scene.world().modifyBlockEntity(pressmobilePress, type, pte -> pte.getPressingBehaviour().makePressingParticleEffect(vector.centerOf(depot[1]).add(0.0, 0.5, 0.0), iron));
        scene.world().removeItemsFromBelt(depot[1]);
        scene.world().createItemOnBeltLike(depot[1], Direction.UP, sheet);
        scene.idle(30);
        ElementLink glider2 = world.showIndependentSection(gliderSelection, Direction.EAST);
        world.moveSection(glider2, vector.of(15.0, -8.0, -10.0), 0);
        world.rotateSection(glider2, 0.0, 30.0, -5.0, 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)glider2, new Vec3(-15.0, -2.0, 11.0), 55, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)glider2, new Vec3(0.0, 0.0, -5.0), 25, SmoothMovementUtils.quadraticRiseDual()));
        world.hideSection(util.select().position(depot[1]), Direction.UP);
        world.hideIndependentSection(pressmobile, Direction.UP);
        scene.special().hideElement(seatBirb, Direction.UP);
        scene.idle(25);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)glider2, new Vec3(0.0, 0.0, 10.0), 25, SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(40);
        overlay.showControls(vector.of(10.0, 2.5, 7.0), Pointing.DOWN, 20).rightClick();
        scene.idle(5);
        effects.indicateRedstone(new BlockPos(6, 6, 2));
        world.toggleRedstonePower(gliderRedstone);
        scene.idle(20);
        overlay.showText(120).text("Simulated Contraptions have physics, and remain completely interactable when assembled").pointAt(vector.centerOf(7, 2, 7));
        scene.idle(60);
        scene.markAsFinished();
    }

    public static void physicsAssemblerBlockProperties(SceneBuilder builder, SceneBuildingUtil util) {
        String configText;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("physics_assembler_block_properties", "Physical properties of blocks");
        scene.configureBasePlate(0, 0, 5);
        scene.setSceneOffsetY(-0.5f);
        scene.showBasePlate();
        BlockPos assemblerPos = new BlockPos(2, 3, 2);
        BlockPos blockRightPos = new BlockPos(0, 3, 2);
        BlockPos blockLeftPos = new BlockPos(4, 3, 2);
        Selection balanceBar = select.fromTo(2, 1, 0, 2, 1, 4);
        Selection scaleSelection = select.fromTo(0, 2, 2, 4, 2, 2);
        scene.idle(5);
        world.showSection(balanceBar, Direction.DOWN);
        scene.idle(5);
        ElementLink scale = world.showIndependentSection(scaleSelection, Direction.DOWN);
        scene.idle(5);
        ElementLink assembler = world.showIndependentSection(util.select().position(assemblerPos), Direction.DOWN);
        scene.idle(10);
        overlay.showControls(vector.topOf(assemblerPos), Pointing.DOWN, 20).rightClick();
        scene.idle(3);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assemblerPos, true, false));
        scene.idle(10);
        scene.idle(20);
        ElementLink blockLeft = world.showIndependentSectionImmediately(util.select().position(blockLeftPos));
        ElementLink blockRight = world.showIndependentSectionImmediately(util.select().position(blockRightPos));
        List<ElementLink> scaleParts = List.of(scale, assembler, blockLeft, blockRight);
        world.moveSection(blockLeft, vector.of(0.0, 2.0, 0.0), 0);
        world.moveSection(blockRight, vector.of(0.0, 2.0, 0.0), 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockLeft, new Vec3(0.0, -2.0, 0.0), 15, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockRight, new Vec3(0.0, -2.0, 0.0), 15, SmoothMovementUtils.quadraticRise()));
        scene.idle(15);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockLeft, new Vec3(0.0, 0.05, 0.0), 4, SmoothMovementUtils.quadraticJump()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockRight, new Vec3(0.0, 0.05, 0.0), 4, SmoothMovementUtils.quadraticJump()));
        scene.idle(10);
        overlay.showText(65).text("Some blocks may have different weights...").pointAt(vector.centerOf(blockRightPos)).placeNearTarget();
        scene.idle(55);
        world.setBlock(blockLeftPos, Blocks.OAK_PLANKS.defaultBlockState(), true);
        scene.idle(2);
        world.setBlock(blockRightPos, Blocks.IRON_BLOCK.defaultBlockState(), true);
        for (ElementLink scalePart : scaleParts) {
            world.configureCenterOfRotation(scalePart, vector.topOf(2, 1, 2));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)scalePart, new Vec3(0.0, 0.0, 23.0), 30, SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(30);
        for (ElementLink scalePart : scaleParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)scalePart, new Vec3(0.0, 0.0, -2.0), 15, SmoothMovementUtils.quadraticJump()));
        }
        scene.overlay().showText(50).text("Light").colored(PonderPalette.MEDIUM).placeNearTarget().pointAt(vector.of(3.9, 4.0, 2.5));
        scene.overlay().showText(50).text("Super Heavy").colored(PonderPalette.MEDIUM).placeNearTarget().pointAt(vector.of(0.2, 2.4, 2.5));
        scene.idle(70);
        world.hideIndependentSection(assembler, Direction.UP);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockLeft, new Vec3(-4.0, -1.7, 0.0), 60, SmoothMovementUtils.quadraticRise()));
        for (int i = 0; i < 5; ++i) {
            world.setBlock(new BlockPos(4 - i, 2, 2), Blocks.ICE.defaultBlockState(), true);
            scene.idle(2);
        }
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockRight, new Vec3(-2.0, -1.0, 0.0), 40, SmoothMovementUtils.quadraticRise()));
        overlay.showText(110).text("...or other special properties").pointAt(vector.centerOf(1, 2, 2)).placeNearTarget();
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)blockRight, new Vec3(0.0, 0.0, 45.0), 30, SmoothMovementUtils.cubicRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockRight, new Vec3(0.0, 1.5, 0.0), 30, SmoothMovementUtils.cubicRise()));
        scene.idle(15);
        world.hideIndependentSection(blockRight, Direction.DOWN);
        scene.idle(25);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockLeft, new Vec3(-1.1, -0.5, 0.0), 15, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)blockLeft, new Vec3(0.0, 0.0, 45.0), 15, SmoothMovementUtils.cubicRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)blockLeft, new Vec3(0.5, -1.5, 0.0), 15, SmoothMovementUtils.cubicRise()));
        world.hideIndependentSection(blockLeft, Direction.DOWN);
        scene.overlay().showText(60).text("Slippery").colored(PonderPalette.MEDIUM).placeNearTarget().pointAt(vector.of(3.5, 3.5, 2.5));
        scene.idle(20);
        scene.overlay().showText(40).text("Fragile").colored(PonderPalette.MEDIUM).placeNearTarget().pointAt(vector.of(2.5, 3.0, 2.5));
        scene.idle(60);
        BlockPropertiesTooltip.Condition configValue = SimConfigService.INSTANCE.clientLoaded() ? (BlockPropertiesTooltip.Condition)((Object)SimConfigService.INSTANCE.client().itemConfig.displayProperties.get()) : null;
        BlockPropertiesTooltip.Condition condition = configValue;
        int n = 0;
        switch (SwitchBootstraps.enumSwitch("enumSwitch", new Object[]{"ALWAYS", "SHIFT", "GOGGLES", "SHIFT_GOGGLES", "NEVER"}, (BlockPropertiesTooltip.Condition)condition, n)) {
            case 0: {
                String string = "property_tooltip_always";
                break;
            }
            case 1: {
                String string = "property_tooltip_shift";
                break;
            }
            case 2: {
                String string = "property_tooltip_goggles";
                break;
            }
            case 3: {
                String string = "property_tooltip_shift_goggles";
                break;
            }
            case 4: {
                String string = "property_tooltip_never";
                break;
            }
            default: {
                String string = configText = "property_tooltip_how";
            }
        }
        if (configValue == BlockPropertiesTooltip.Condition.GOGGLES || configValue == BlockPropertiesTooltip.Condition.SHIFT_GOGGLES) {
            overlay.showControls(vector.centerOf(2, 2, 2), Pointing.DOWN, 160).withItem(AllItems.GOGGLES.asStack());
        }
        scene.overlay().showText(160).sharedText(configText).attachKeyFrame().placeNearTarget().pointAt(vector.centerOf(2, 2, 2));
        scene.idle(60);
        scene.markAsFinished();
    }

    public static void physicsAssemblerSubLevelSplitting(SceneBuilder builder, SceneBuildingUtil util) {
        ElementLink raftHalf;
        int side;
        int i;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("physics_assembler_sub_level_splitting", "Splitting and merging Simulated Contraptions");
        scene.configureBasePlate(0, 0, 9);
        scene.scaleSceneView(0.8f);
        scene.showBasePlate();
        Selection baseplateHole = select.fromTo(1, 0, 1, 7, 0, 7);
        Selection baseplateWalls = select.fromTo(1, 1, 8, 8, 8, 8).add(select.fromTo(8, 1, 1, 8, 8, 8));
        Selection baseplateWater = select.fromTo(1, 7, 1, 8, 7, 8);
        Selection raftFull = select.fromTo(2, 1, 2, 6, 3, 6);
        Selection raftR = select.fromTo(2, 1, 2, 4, 3, 6);
        Selection raftL = select.fromTo(5, 1, 2, 6, 3, 6);
        Selection raftCenter = raftFull.substract(raftL).substract(raftR);
        BlockPos assembler = new BlockPos(6, 3, 4);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assembler, true, true));
        ElementLink baseplateWallLink = scene.world().showIndependentSection(baseplateWalls, Direction.UP);
        world.moveSection(baseplateWallLink, new Vec3(0.0, -7.0, 0.0), 0);
        scene.idle(15);
        world.hideSection(baseplateHole, Direction.DOWN);
        scene.idle(25);
        ElementLink baseplateWaterLink = scene.world().showIndependentSection(baseplateWater, Direction.UP);
        world.moveSection(baseplateWaterLink, new Vec3(0.0, -7.0, 0.0), 0);
        scene.idle(15);
        ElementLink raftFullLink = scene.world().showIndependentSection(raftFull, Direction.NORTH);
        world.moveSection(raftFullLink, new Vec3(0.0, 2.0, 0.0), 0);
        world.rotateSection(raftFullLink, 15.0, 0.0, 0.0, 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(0.0, -10.0, 0.0), 30, SmoothMovementUtils.quadraticRise()));
        scene.idle(15);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(-25.0, 0.0, 10.0), 25, SmoothMovementUtils.quadraticRiseInOut()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(0.0, 5.7, 0.0), 15, SmoothMovementUtils.quadraticRise()));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(0.0, 1.25, 0.0), 28, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(15.0, 0.0, -15.0), 40, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(0.0, -0.75, 0.0), 50, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(-7.5, 0.0, 7.5), 40, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(30);
        overlay.showText(80).text("Disconnected sections of Simulated Contraptions will split off").placeNearTarget().pointAt(vector.centerOf(new BlockPos(4, 1, 4)));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(0.0, 0.25, 0.0), 40, SmoothMovementUtils.quadraticRiseInOut()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(2.5, 0.0, -5.0), 40, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(40);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(0.0, -0.1, 0.0), 40, SmoothMovementUtils.quadraticRiseInOut()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftFullLink, new Vec3(0.0, 0.0, 1.0), 40, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(10);
        for (int i2 = 0; i2 < 5; ++i2) {
            scene.idle(5);
            world.setBlock(new BlockPos(4, 2, 6 - i2), Blocks.AIR.defaultBlockState(), false);
            scene.addInstruction((PonderInstruction)new OffsetBreakParticlesInstruction(AABB.unitCubeFromLowerCorner((Vec3)new Vec3(4.0, 0.6, (double)(6 - i2))), Blocks.STRIPPED_OAK_LOG.defaultBlockState()));
        }
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, null, raftFullLink));
        ElementLink raftLLink = world.showIndependentSectionImmediately(raftL);
        ElementLink raftRLink = world.showIndependentSectionImmediately(raftR);
        List<ElementLink> raftParts = List.of(raftLLink, raftRLink);
        world.moveSection(raftLLink, new Vec3(0.0, -1.69, 0.0), 0);
        world.moveSection(raftRLink, new Vec3(0.0, -1.625, 0.0), 0);
        for (i = 0; i < 2; ++i) {
            side = i * 2 - 1;
            raftHalf = raftParts.get(i);
            world.rotateSection(raftHalf, 0.0, 0.0, -1.5, 0);
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftHalf, new Vec3(0.0, 0.0, (double)(5 * side)), 40, SmoothMovementUtils.quadraticRiseInOut()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftHalf, new Vec3(0.0, 0.0, (double)(5 * side)), 80, SmoothMovementUtils.cubicSmoothing()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftHalf, new Vec3(-0.15 * (double)side, -0.1, 0.0), 100, SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(80);
        overlay.showText(200).text("A Slime Ball can be used to merge any two Simulated Contraptions").colored(PonderPalette.GREEN).placeNearTarget().attachKeyFrame().pointAt(vector.of(4.5, 1.0, 4.5));
        scene.idle(60);
        overlay.showControls(vector.of(5.2, 1.4, 4.5), Pointing.DOWN, 20).withItem(Items.SLIME_BALL.getDefaultInstance()).rightClick();
        scene.idle(6);
        scene.addInstruction((PonderInstruction)new OBBOutlineInstruction(new AABB(5.0, 0.2, 4.1, 5.5, 1.2, 4.8), vector.of(0.0, 0.0, 10.0), false, PonderPalette.GREEN, "slime1", 45));
        scene.idle(25);
        overlay.showControls(vector.of(3.8, 1.4, 4.5), Pointing.DOWN, 20).withItem(Items.SLIME_BALL.getDefaultInstance()).rightClick();
        scene.idle(6);
        scene.addInstruction((PonderInstruction)new OBBOutlineInstruction(new AABB(3.5, 0.2, 4.1, 4.0, 1.2, 4.8), vector.of(0.0, 0.0, -10.0), false, PonderPalette.GREEN, "slime2", 15));
        scene.overlay().showLine(PonderPalette.GREEN, new Vec3(3.5, 0.5, 4.2), new Vec3(5.5, 1.2, 4.3), 30);
        scene.overlay().showBigLine(PonderPalette.GREEN, new Vec3(3.8, 1.1, 4.5), new Vec3(5.5, 0.8, 4.4), 30);
        scene.overlay().showLine(PonderPalette.GREEN, new Vec3(3.5, 1.2, 4.7), new Vec3(5.5, 1.2, 4.7), 30);
        scene.idle(10);
        for (i = 0; i < 2; ++i) {
            side = i * 2 - 1;
            raftHalf = raftParts.get(i);
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)raftHalf, new Vec3(0.0, 0.0, (double)(-10 * side)), 30, SmoothMovementUtils.quadraticRise()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftHalf, new Vec3(0.65 * (double)side, 0.1, 0.0), 30, SmoothMovementUtils.quadraticRise()));
        }
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftRLink, new Vec3(0.0, -0.02, 0.0), 30, SmoothMovementUtils.quadraticRise()));
        scene.idle(30);
        for (ElementLink raftHalf2 : raftParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftHalf2, new Vec3(0.0, -0.3, -0.01), 50, SmoothMovementUtils.quadraticRiseDual()));
        }
        scene.idle(40);
        scene.markAsFinished();
        for (ElementLink raftHalf3 : raftParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)raftHalf3, new Vec3(0.0, 0.2, 0.0), 80, SmoothMovementUtils.quadraticRiseInOut()));
        }
    }
}
