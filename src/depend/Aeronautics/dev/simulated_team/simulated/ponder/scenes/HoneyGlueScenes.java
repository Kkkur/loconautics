/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock
 *  com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock$PistonState
 *  com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock
 *  com.simibubi.create.content.kinetics.transmission.ClutchBlock
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$EffectInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.catnip.theme.Color
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.scene.OverlayInstructions
 *  net.createmod.ponder.api.scene.PositionUtil
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.api.scene.SelectionUtil
 *  net.createmod.ponder.api.scene.VectorUtil
 *  net.createmod.ponder.foundation.element.InputWindowElement
 *  net.createmod.ponder.foundation.instruction.AnimateWorldSectionInstruction
 *  net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.createmod.ponder.foundation.instruction.ShowInputInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LeverBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.ponder.scenes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.elements.KeybindWindowElement;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.theme.Color;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.PositionUtil;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.createmod.ponder.foundation.instruction.AnimateWorldSectionInstruction;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.createmod.ponder.foundation.instruction.ShowInputInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HoneyGlueScenes {
    private static KeybindWindowElement.Builder showControls(SceneBuilder builder, Vec3 sceneSpace, Pointing direction, int duration) {
        KeybindWindowElement inputWindowElement = new KeybindWindowElement(sceneSpace, direction);
        builder.addInstruction((PonderInstruction)new ShowInputInstruction((InputWindowElement)inputWindowElement, duration));
        return inputWindowElement.builder();
    }

    public static void honeyGlueIntro(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        SelectionUtil select = util.select();
        OverlayInstructions overlay = scene.overlay();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        VectorUtil vector = util.vector();
        PositionUtil grid = util.grid();
        scene.title("honey_glue_intro", "Attaching blocks using Honey Glue");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        BlockPos assembler = grid.at(3, 2, 1);
        Selection structure1 = select.fromTo(3, 1, 1, 3, 1, 3);
        Selection structure2 = select.fromTo(1, 2, 3, 3, 3, 3);
        Selection assembledStructure = select.fromTo(3, 1, 1, 1, 3, 3);
        BlockPos largeCog = grid.at(2, 0, 5);
        Selection kineticShafts = select.fromTo(1, 1, 2, 1, 1, 5);
        BlockPos pistonBase = grid.at(1, 1, 2);
        BlockPos movedBlock = grid.at(3, 1, 2);
        BlockPos movedBlockElement = grid.at(3, 2, 2);
        Selection pistonPole = select.fromTo(1, 2, 2, 3, 2, 2);
        world.showSection(structure1, Direction.DOWN);
        world.showSection(structure2, Direction.DOWN);
        scene.idle(15);
        overlay.showText(70).attachKeyFrame().text("Honey Glue is a convenient alternative to Super Glue for Simulated Contraptions").placeNearTarget().pointAt(vector.blockSurface(new BlockPos(3, 2, 2), Direction.WEST));
        scene.idle(80);
        overlay.showText(65).attachKeyFrame().colored(PonderPalette.OUTPUT).text("Clicking two endpoints creates a new 'glued' area...").placeNearTarget().pointAt(vector.blockSurface(new BlockPos(3, 2, 2), Direction.WEST));
        overlay.showControls(vector.centerOf(3, 1, 1), Pointing.RIGHT, 20).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        scene.idle(6);
        effects.emitParticles(new Vec3(3.0, 1.0, 1.0), effects.particleEmitterWithinBlockSpace((ParticleOptions)new DustParticleOptions(new Color(255, 232, 142).asVectorF(), 1.0f), Vec3.ZERO), 10.0f, 2);
        scene.idle(24);
        overlay.showControls(vector.centerOf(3, 3, 3), Pointing.RIGHT, 20).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        scene.idle(6);
        AABB bb = AABB.unitCubeFromLowerCorner((Vec3)new Vec3(3.0, 1.0, 1.0));
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb, bb, 0);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb, bb.expandTowards(0.0, 2.0, 2.0), 100);
        scene.idle(40);
        overlay.showControls(vector.centerOf(2, 2, 2), Pointing.LEFT, 20).withItem(SimItems.HONEY_GLUE.asStack()).scroll().whileCTRL();
        scene.idle(10);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb, bb.expandTowards(-1.0, 2.0, 2.0), 15);
        scene.idle(15);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)bb, bb.expandTowards(-2.0, 2.0, 2.0), 190);
        scene.idle(20);
        overlay.showText(50).colored(PonderPalette.OUTPUT).text("...which can be expanded or retracted via Ctrl-Scrolling").placeNearTarget().pointAt(new Vec3(2.0, 3.0, 2.0));
        scene.idle(70);
        world.hideSection(structure2, Direction.UP);
        scene.idle(20);
        scene.addKeyframe();
        HoneyGlueScenes.showControls(builder, vector.blockSurface(new BlockPos(1, 3, 3), Direction.NORTH), Pointing.RIGHT, 80).withItem(SimItems.HONEY_GLUE.asStack()).rightClick().keybind("simulated.key.alt");
        AABB whitebb = new AABB(new Vec3(1.05, 3.95, 3.05), new Vec3(1.05, 3.95, 3.05));
        overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, (Object)whitebb, whitebb, 0);
        overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, (Object)whitebb, whitebb.expandTowards(0.9, -0.9, 0.9), 80);
        overlay.showText(80).colored(PonderPalette.WHITE).text("Alternatively, hold ALT to place an endpoint midair").placeNearTarget().pointAt(vector.blockSurface(new BlockPos(1, 3, 3), Direction.WEST));
        scene.idle(100);
        world.showSection(structure2, Direction.DOWN);
        world.showSection(select.position(largeCog), Direction.DOWN);
        world.showSection(select.position(new BlockPos(2, 1, 2)), Direction.DOWN);
        world.showSection(kineticShafts, Direction.DOWN);
        ElementLink pistonPoleElement = world.showIndependentSection(pistonPole, Direction.DOWN);
        world.moveSection(pistonPoleElement, vector.of(0.0, -1.0, 0.0), 0);
        scene.idle(20);
        overlay.showText(40).attachKeyFrame().colored(PonderPalette.GREEN).text("Unlike Super Glue...").placeNearTarget().pointAt(vector.topOf(3, 1, 2));
        scene.idle(60);
        world.setBlock(movedBlock, Blocks.BARRIER.defaultBlockState(), false);
        world.setBlock(movedBlockElement, Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        world.modifyBlock(pistonBase, s -> (BlockState)s.setValue((Property)MechanicalPistonBlock.STATE, (Comparable)MechanicalPistonBlock.PistonState.MOVING), false);
        world.setBlock(new BlockPos(1, 2, 2), (BlockState)AllBlocks.PISTON_EXTENSION_POLE.getDefaultState().setValue((Property)PistonExtensionPoleBlock.FACING, (Comparable)Direction.WEST), false);
        world.setKineticSpeed(kineticShafts, 32.0f);
        world.setKineticSpeed(select.position(largeCog), -16.0f);
        world.moveSection(pistonPoleElement, vector.of(-1.0, 0.0, 0.0), 20);
        scene.idle(10);
        overlay.showText(70).colored(PonderPalette.OUTPUT).text("...Honey Glue does not attach to animated Contraptions").placeNearTarget().pointAt(vector.topOf(1, 1, 2));
        scene.idle(70);
        world.setKineticSpeed(kineticShafts, -32.0f);
        world.setKineticSpeed(select.position(largeCog), 16.0f);
        world.moveSection(pistonPoleElement, vector.of(1.0, 0.0, 0.0), 20);
        scene.idle(20);
        world.setBlock(movedBlock, Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        world.setBlock(movedBlockElement, Blocks.AIR.defaultBlockState(), false);
        world.setBlock(new BlockPos(1, 2, 2), Blocks.AIR.defaultBlockState(), false);
        world.modifyBlock(pistonBase, s -> (BlockState)s.setValue((Property)MechanicalPistonBlock.STATE, (Comparable)MechanicalPistonBlock.PistonState.EXTENDED), false);
        scene.idle(20);
        world.hideIndependentSection(pistonPoleElement, Direction.UP);
        world.hideSection(kineticShafts, Direction.UP);
        world.hideSection(select.position(largeCog), Direction.UP);
        scene.idle(20);
        world.showSection(select.position(assembler), Direction.DOWN);
        scene.idle(20);
        overlay.showText(50).attachKeyFrame().text("Instead, it exclusively attaches to Simulated Contraptions").placeNearTarget().pointAt(vector.centerOf(assembler));
        scene.idle(60);
        overlay.showControls(vector.topOf(assembler), Pointing.DOWN, 25).rightClick();
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assembler, true, false));
        scene.idle(5);
        world.setBlock(new BlockPos(2, 2, 2), Blocks.AIR.defaultBlockState(), false);
        world.setBlocks(kineticShafts, Blocks.AIR.defaultBlockState(), false);
        ElementLink assembledElement = world.makeSectionIndependent(assembledStructure);
        world.configureCenterOfRotation(assembledElement, new Vec3(3.0, 1.0, 2.0));
        scene.idle(5);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)assembledElement, new Vec3(0.0, 0.0, 45.0), 30, SmoothMovementUtils.cubicRise()));
        scene.idle(30);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)assembledElement, new Vec3(0.0, 0.0, -3.0), 7, SmoothMovementUtils.quadraticJump()));
    }

    public static void honeyGlueSuperGlue(SceneBuilder builder, SceneBuildingUtil util) {
        ElementLink[] assembledCarParts;
        AABB bb;
        Vec3[] bearings;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        SelectionUtil select = util.select();
        OverlayInstructions overlay = scene.overlay();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        VectorUtil vector = util.vector();
        PositionUtil grid = util.grid();
        scene.title("honey_glue_super_glue", "Using Honey Glue with Super Glue");
        scene.configureBasePlate(0, 0, 9);
        scene.setSceneOffsetY(-1.0f);
        scene.scaleSceneView(0.7f);
        scene.showBasePlate();
        BlockPos carAssembler = grid.at(4, 4, 2);
        BlockPos carLever = grid.at(4, 5, 5);
        BlockPos carPortable = grid.at(4, 4, 6);
        BlockPos carClutch = grid.at(4, 4, 5);
        BlockPos carGearbox = grid.at(4, 4, 4);
        Selection carChassis = select.fromTo(3, 3, 1, 5, 5, 7);
        BoundingBox carFull = new BoundingBox(2, 2, 1, 6, 5, 7);
        BlockPos carMinCorner = new BlockPos(carFull.minX(), carFull.minY(), carFull.minZ());
        BlockPos carMaxCorner = new BlockPos(carFull.maxX(), carFull.maxY() - 1, carFull.maxZ());
        BoundingBox wheel1 = new BoundingBox(2, 2, 1, 2, 4, 3);
        BoundingBox wheel2 = new BoundingBox(2, 2, 5, 2, 4, 7);
        BoundingBox wheel3 = new BoundingBox(6, 2, 1, 6, 4, 3);
        BoundingBox wheel4 = new BoundingBox(6, 2, 5, 6, 4, 7);
        Selection platformLeft = select.fromTo(5, 6, 2, 7, 7, 4);
        Selection platformLeftStatic = select.fromTo(5, 6, 5, 7, 6, 6);
        Selection platformLeftFull = select.fromTo(5, 6, 2, 7, 7, 6);
        Selection platformRight = select.fromTo(1, 6, 2, 3, 7, 6);
        world.setKineticSpeed(select.position(carPortable), -32.0f);
        world.showSection(util.select().fromTo(carFull.minX(), carFull.minY(), carFull.minZ(), carFull.maxX(), carFull.maxY(), carFull.maxZ()), Direction.DOWN);
        scene.idle(10);
        BoundingBox[] wheels = new BoundingBox[]{wheel1, wheel2, wheel3, wheel4};
        for (int i = 0; i < wheels.length; ++i) {
            BoundingBox wheel = wheels[i];
            AABB bb2 = new AABB(new BlockPos(wheel.minX(), wheel.minY(), wheel.minZ()));
            overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)bb2, bb2, 5);
            overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)bb2, bb2.expandTowards(0.0, 2.0, 2.0), 105 - i * 5);
            scene.idle(5);
        }
        scene.idle(10);
        overlay.showControls(vector.centerOf(carMinCorner), Pointing.RIGHT, 10).withItem(AllItems.SUPER_GLUE.asStack()).rightClick();
        effects.indicateSuccess(carMinCorner);
        scene.idle(15);
        overlay.showControls(vector.centerOf(carMaxCorner), Pointing.RIGHT, 10).withItem(AllItems.SUPER_GLUE.asStack()).rightClick();
        AABB carGlueCorner = new AABB(vector.centerOf(carMinCorner).subtract(0.55, 0.55, 0.55), vector.centerOf(carMinCorner).add(0.55, 0.55, 0.55));
        AABB carGlueFull = new AABB(vector.centerOf(carMinCorner).subtract(0.5, 0.5, 0.5), vector.centerOf(carMaxCorner).add(0.5, 0.5, 0.5));
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)carGlueCorner, carGlueCorner, 5);
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)carGlueCorner, carGlueCorner.expandTowards(4.0, 2.0, 6.0), 60);
        scene.idle(20);
        overlay.showText(50).attachKeyFrame().text("For more complex Simulated Contraptions...").placeNearTarget().pointAt(carFull.getCenter().getCenter());
        scene.idle(40);
        overlay.showControls(vector.centerOf(carAssembler), Pointing.DOWN, 10).rightClick();
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(carAssembler, true, false));
        scene.idle(5);
        ElementLink failureCar = world.makeSectionIndependent(util.select().fromTo(carFull.minX(), carFull.minY(), carFull.minZ(), carFull.maxX(), carFull.maxY(), carFull.maxZ()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)failureCar, new Vec3(0.0, -1.0, 0.0), 15, SmoothMovementUtils.cubicRise()));
        scene.idle(15);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)failureCar, new Vec3(0.0, 0.05, 0.0), 4, SmoothMovementUtils.quadraticJump()));
        scene.idle(10);
        overlay.showControls(vector.centerOf(new BlockPos(4, 4, 5)), Pointing.DOWN, 10).rightClick();
        scene.idle(6);
        effects.indicateRedstone(new BlockPos(4, 3, 5));
        world.modifyBlock(carLever, s -> (BlockState)s.setValue((Property)LeverBlock.POWERED, (Comparable)Boolean.FALSE), false);
        world.modifyBlock(carClutch, s -> (BlockState)s.setValue((Property)ClutchBlock.POWERED, (Comparable)Boolean.FALSE), false);
        world.setKineticSpeed(select.position(carGearbox), -32.0f);
        for (Vec3 vec3 : bearings = new Vec3[]{new Vec3(3.0, 2.0, 2.0), new Vec3(5.0, 2.0, 2.0), new Vec3(3.0, 2.0, 6.0), new Vec3(5.0, 2.0, 6.0)}) {
            effects.emitParticles(vec3, effects.particleEmitterWithinBlockSpace((ParticleOptions)ParticleTypes.CLOUD, new Vec3(0.0, 0.0, 0.0)), 10.0f, 1);
            bb = new AABB(vec3, vec3.add(new Vec3(1.0, 1.0, 1.0)));
            overlay.chaseBoundingBoxOutline(PonderPalette.RED, (Object)bb, bb, 80);
        }
        scene.idle(20);
        overlay.showText(60).attachKeyFrame().text("...Super Glue may form undesired connections").placeNearTarget().pointAt(vector.blockSurface(new BlockPos(5, 2, 2), Direction.SOUTH));
        scene.idle(70);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)failureCar, new Vec3(0.0, 1.0, 0.0), 10, SmoothMovementUtils.linear()));
        world.modifyBlock(carLever, s -> (BlockState)s.setValue((Property)LeverBlock.POWERED, (Comparable)Boolean.TRUE), false);
        world.modifyBlock(carClutch, s -> (BlockState)s.setValue((Property)ClutchBlock.POWERED, (Comparable)Boolean.TRUE), false);
        world.setKineticSpeed(select.position(carGearbox), 0.0f);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(carAssembler, false, true));
        scene.idle(10);
        for (Vec3 vec3 : wheels) {
            bb = new AABB(new BlockPos(vec3.minX(), vec3.minY(), vec3.minZ()));
            overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)bb, bb.expandTowards(0.0, 2.0, 2.0), 125);
        }
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)carGlueFull, carGlueFull, 16);
        scene.idle(10);
        overlay.showControls(carFull.getCenter().getCenter(), Pointing.DOWN, 10).withItem(AllItems.SUPER_GLUE.asStack()).leftClick();
        scene.idle(30);
        overlay.showText(75).attachKeyFrame().text("Honey Glue may be preferable in this scenario").placeNearTarget().pointAt(carFull.getCenter().getCenter());
        scene.idle(20);
        overlay.showControls(vector.centerOf(carMinCorner), Pointing.RIGHT, 10).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        effects.emitParticles(vector.centerOf(carMinCorner), effects.particleEmitterWithinBlockSpace((ParticleOptions)new DustParticleOptions(new Color(255, 232, 142).asVectorF(), 1.0f), Vec3.ZERO), 10.0f, 2);
        scene.idle(25);
        overlay.showControls(vector.centerOf(carMaxCorner), Pointing.RIGHT, 10).withItem(SimItems.HONEY_GLUE.asStack()).rightClick();
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)carGlueCorner, carGlueCorner, 5);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)carGlueCorner, carGlueCorner.expandTowards(4.0, 2.0, 6.0), 40);
        scene.idle(45);
        overlay.showControls(vector.centerOf(carAssembler), Pointing.DOWN, 10).rightClick();
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(carAssembler, true, false));
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, null, failureCar));
        ElementLink assembledChassis = world.showIndependentSectionImmediately(carChassis);
        ElementLink assembledWheel1 = world.showIndependentSectionImmediately(select.fromTo(wheel1.minX(), wheel1.minY(), wheel1.minZ(), wheel1.maxX(), wheel1.maxY(), wheel1.maxZ()));
        ElementLink assembledWheel2 = world.showIndependentSectionImmediately(select.fromTo(wheel2.minX(), wheel2.minY(), wheel2.minZ(), wheel2.maxX(), wheel2.maxY(), wheel2.maxZ()));
        ElementLink elementLink = world.showIndependentSectionImmediately(select.fromTo(wheel3.minX(), wheel3.minY(), wheel3.minZ(), wheel3.maxX(), wheel3.maxY(), wheel3.maxZ()));
        ElementLink assembledWheel4 = world.showIndependentSectionImmediately(select.fromTo(wheel4.minX(), wheel4.minY(), wheel4.minZ(), wheel4.maxX(), wheel4.maxY(), wheel4.maxZ()));
        ElementLink[] assembledWheels = new ElementLink[]{assembledWheel1, assembledWheel2, elementLink, assembledWheel4};
        for (ElementLink elementLink2 : assembledCarParts = new ElementLink[]{assembledChassis, assembledWheel1, assembledWheel2, elementLink, assembledWheel4}) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)elementLink2, new Vec3(0.0, -1.0, 0.0), 15, SmoothMovementUtils.cubicRise()));
        }
        scene.idle(15);
        for (ElementLink elementLink3 : assembledCarParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)elementLink3, new Vec3(0.0, 0.05, 0.0), 4, SmoothMovementUtils.quadraticJump()));
            scene.addInstruction((PonderInstruction)AnimateWorldSectionInstruction.move((ElementLink)elementLink3, (Vec3)new Vec3(-0.2, 0.25, -0.28), (int)0));
        }
        scene.idle(10);
        overlay.showControls(vector.centerOf(new BlockPos(4, 4, 5)), Pointing.DOWN, 10).rightClick();
        scene.idle(6);
        effects.indicateRedstone(new BlockPos(4, 3, 5));
        world.modifyBlock(carLever, s -> (BlockState)s.setValue((Property)LeverBlock.POWERED, (Comparable)Boolean.FALSE), false);
        world.modifyBlock(carClutch, s -> (BlockState)s.setValue((Property)ClutchBlock.POWERED, (Comparable)Boolean.FALSE), false);
        world.setKineticSpeed(select.position(carGearbox), -32.0f);
        for (ElementLink elementLink4 : bearings) {
            world.rotateBearing(new BlockPos((int)elementLink4.x, (int)elementLink4.y + 1, (int)elementLink4.z), -360.0f, 75);
        }
        for (ElementLink elementLink5 : assembledWheels) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)elementLink5, new Vec3(-360.0, 0.0, 0.0), 75, SmoothMovementUtils.linear()));
        }
        for (ElementLink elementLink6 : assembledCarParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)elementLink6, new Vec3(0.0, 0.0, -4.0), 30, SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(30);
        float n = 0.5f;
        FloatUnaryOperator angleFunctionDown = t -> (float)Math.sin(1.5707963267948966 * (double)SmoothMovementUtils.cubicRise().apply(t));
        FloatUnaryOperator angleFunctionHorizontal = t -> (float)Math.sin(1.5707963267948966 * (double)SmoothMovementUtils.linear().apply(t));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembledChassis, new Vec3(0.0, 0.0, -3.0), 30, angleFunctionHorizontal));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembledChassis, new Vec3(0.0, -3.0, 0.0), 30, angleFunctionDown));
        for (ElementLink carPart : assembledCarParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)carPart, new Vec3(0.0, 0.0, -4.0), 30, SmoothMovementUtils.linear()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)carPart, new Vec3(-45.0, 0.0, 0.0), 30, SmoothMovementUtils.quadraticRise()));
        }
        ElementLink[] elementLinkArray = new ElementLink[]{assembledWheel1, elementLink};
        ElementLink[] backWheels = new ElementLink[]{assembledWheel2, assembledWheel4};
        for (ElementLink wheel : elementLinkArray) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, 0.0, -2.65), 30, angleFunctionHorizontal));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, -4.3, 0.0), 30, angleFunctionDown));
        }
        for (ElementLink wheel : backWheels) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, 0.0, -2.75), 30, angleFunctionHorizontal));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)wheel, new Vec3(0.0, -1.45, 0.0), 30, angleFunctionDown));
        }
        scene.idle(5);
        for (ElementLink carPart : assembledCarParts) {
            world.hideIndependentSection(carPart, Direction.DOWN);
        }
        scene.idle(30);
        ElementLink assembledPlatformLeftFull = world.showIndependentSection(platformLeftFull, Direction.DOWN);
        ElementLink assembledPlatformRight = world.showIndependentSection(platformRight, Direction.DOWN);
        scene.addInstruction((PonderInstruction)AnimateWorldSectionInstruction.move((ElementLink)assembledPlatformRight, (Vec3)new Vec3(0.0, -3.0, 0.0), (int)0));
        scene.addInstruction((PonderInstruction)AnimateWorldSectionInstruction.move((ElementLink)assembledPlatformLeftFull, (Vec3)new Vec3(0.0, -3.0, 0.0), (int)0));
        scene.idle(20);
        overlay.showText(75).attachKeyFrame().text("When assembled, Honey Glue will attach to overlapping Super Glue").placeNearTarget().pointAt(vector.topOf(new BlockPos(2, 3, 4)));
        scene.idle(40);
        AABB honey1 = new AABB(3.0, 3.0, 6.0, 4.0, 4.0, 7.0);
        AABB slime1 = new AABB(3.0, 3.0, 4.0, 4.0, 4.0, 5.0);
        AABB honey2 = new AABB(7.0, 3.0, 4.0, 8.0, 4.0, 5.0);
        AABB slime2 = new AABB(7.0, 3.0, 6.0, 8.0, 4.0, 7.0);
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)honey1, honey1, 0);
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)honey1, honey1.expandTowards(-2.0, 0.0, -2.0), 40);
        scene.idle(20);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)slime1, slime1, 0);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)slime1, slime1.expandTowards(-2.0, 0.0, -2.0), 20);
        scene.idle(20);
        overlay.showControls(vector.topOf(2, 4, 3), Pointing.DOWN, 10).rightClick();
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(new BlockPos(2, 7, 3), true, false));
        scene.idle(5);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembledPlatformRight, new Vec3(0.0, -2.0, 0.0), 20, SmoothMovementUtils.quadraticRise()));
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembledPlatformRight, new Vec3(0.0, 0.05, 0.0), 4, SmoothMovementUtils.quadraticJump()));
        scene.idle(30);
        overlay.showText(75).colored(PonderPalette.RED).text("However, Super Glue will not attach to overlapping Honey Glue").placeNearTarget().pointAt(vector.topOf(new BlockPos(6, 3, 4)));
        scene.idle(40);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)slime2, slime2, 0);
        overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, (Object)slime2, slime2.expandTowards(-2.0, 0.0, -2.0), 40);
        scene.idle(20);
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)honey2, honey2, 0);
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)honey2, honey2.expandTowards(-2.0, 0.0, -2.0), 20);
        scene.idle(20);
        overlay.showControls(vector.topOf(6, 4, 3), Pointing.DOWN, 10).rightClick();
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(new BlockPos(6, 7, 3), true, false));
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, null, assembledPlatformLeftFull));
        ElementLink assembledPlatformLeft = world.showIndependentSectionImmediately(platformLeft);
        ElementLink staticPlatformLeft = world.showIndependentSectionImmediately(platformLeftStatic);
        scene.addInstruction((PonderInstruction)AnimateWorldSectionInstruction.move((ElementLink)assembledPlatformLeft, (Vec3)new Vec3(0.0, -3.0, 0.0), (int)0));
        scene.addInstruction((PonderInstruction)AnimateWorldSectionInstruction.move((ElementLink)staticPlatformLeft, (Vec3)new Vec3(0.0, -3.0, 0.0), (int)0));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembledPlatformLeft, new Vec3(0.0, -2.0, 0.0), 20, SmoothMovementUtils.quadraticRise()));
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)assembledPlatformLeft, new Vec3(0.0, 0.05, 0.0), 4, SmoothMovementUtils.quadraticJump()));
    }
}
