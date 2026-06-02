/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$EffectInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.Pointing
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
 *  net.createmod.ponder.foundation.element.TextWindowElement
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.createmod.ponder.foundation.instruction.RotateSceneInstruction
 *  net.createmod.ponder.foundation.instruction.TextInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.AbstractFurnaceBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.ponder.scenes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.analog_transmission.AnalogTransmissionBlockEntity;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import dev.simulated_team.simulated.content.blocks.symmetric_sail.SymmetricSailBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.mixin_interface.ponder.TextWindowElementExtension;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.ChasingLineInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateParrotInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomParrotFlappingInstruction;
import dev.simulated_team.simulated.ponder.instructions.OBBOutlineInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import dev.simulated_team.simulated.ponder.instructions.ScaleSceneInstruction;
import dev.simulated_team.simulated.ponder.instructions.ScrollingSceneInstruction;
import dev.simulated_team.simulated.ponder.instructions.SimAnimateBEInstruction;
import dev.simulated_team.simulated.ponder.instructions.TranslateYSceneInstruction;
import dev.simulated_team.simulated.ponder.instructions.WindstreamInstruction;
import dev.simulated_team.simulated.ponder.records.PonderLineRecord;
import dev.simulated_team.simulated.ponder.records.ScrollingSceneRecord;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.Pointing;
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
import net.createmod.ponder.foundation.element.TextWindowElement;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.createmod.ponder.foundation.instruction.TextInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SymmetricSailScenes {
    public static void symmetricSailMain(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("symmetric_sail", "Using Sails and Symmetric Sails on Simulated Contraptions");
        int offset = 87;
        scene.configureBasePlate(87, 0, 9);
        scene.addInstruction((PonderInstruction)new TranslateYSceneInstruction(-1.0f, 0));
        scene.addInstruction((PonderInstruction)new ScaleSceneInstruction(0.66f, 1));
        scene.removeShadow();
        BlockPos assembler = new BlockPos(88, 3, 4);
        BlockPos portableEngine = new BlockPos(92, 3, 4);
        BlockPos steeringWheelPos = new BlockPos(94, 4, 4);
        Selection steeringWheel = select.position(steeringWheelPos);
        BlockPos bearing = new BlockPos(94, 3, 4);
        Selection planeRudderSelection = select.fromTo(95, 2, 4, 96, 4, 4).add(select.position(bearing.below()));
        Selection planeBodySelection = select.fromTo(86, 1, 0, 97, 4, 8).substract(planeRudderSelection).substract(select.fromTo(95, 2, 3, 95, 2, 4));
        Selection baseplateStart = select.fromTo(87, 0, 0, 95, 0, 8);
        Selection baseplateLong = select.fromTo(0, 0, 0, 86, 0, 8);
        Selection baseplate2Long = select.fromTo(1, 1, 0, 80, 1, 7);
        ElementLink baseplate = scene.world().showIndependentSection(baseplateStart, Direction.UP);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(assembler, true, true));
        scene.idle(10);
        ElementLink planeBody = world.showIndependentSection(planeBodySelection, Direction.DOWN);
        ElementLink planeRudder = world.showIndependentSection(planeRudderSelection, Direction.DOWN);
        scene.special().movePointOfInterest(new BlockPos(0, 3, 4));
        ElementLink seatBirb = scene.special().createBirb(vector.of(86.5, 2.5, 4.5), ParrotPose.FacePointOfInterestPose::new);
        ArrayList<ElementLink> planeParts = new ArrayList<ElementLink>(List.of(planeBody, planeRudder));
        for (ElementLink planePart : planeParts) {
            world.configureCenterOfRotation(planePart, vector.centerOf(bearing));
        }
        scene.idle(1);
        scene.addInstruction((PonderInstruction)new CustomParrotFlappingInstruction((ElementLink<ParrotElement>)seatBirb));
        scene.idle(19);
        scene.overlay().showControls(vector.of(96.0, 3.5, 4.5), Pointing.UP, 70).withItem(SimBlocks.WHITE_SYMMETRIC_SAIL.asStack());
        scene.idle(2);
        overlay.showText(65).text("Symmetric Sail").pointAt(vector.of(96.0, 3.5, 4.5)).colored(PonderPalette.MEDIUM).placeNearTarget();
        scene.idle(20);
        scene.overlay().showControls(vector.centerOf(92, 2, 2), Pointing.DOWN, 48).withItem(AllBlocks.SAIL.asStack());
        scene.idle(2);
        overlay.showText(43).text("Regular Sail").pointAt(vector.centerOf(92, 2, 2)).colored(PonderPalette.MEDIUM).placeNearTarget();
        scene.idle(73);
        overlay.showText(90).text("When moving on a Simulated Contraption, Regular Sails provide Lift").pointAt(vector.topOf(91, 2, 4)).attachKeyFrame().placeNearTarget();
        scene.idle(60);
        ElementLink groundClose = scene.world().showIndependentSection(baseplateLong, Direction.UP);
        scene.idle(12);
        scene.overlay().showControls(vector.topOf(portableEngine), Pointing.DOWN, 20).withItem(AllItems.BLAZE_CAKE.asStack());
        scene.world().modifyBlockEntity(portableEngine, PortableEngineBlockEntity.class, be -> {
            be.openHatchOverride = true;
            be.hatchOpenTime = 1.0f;
            be.lastHatchOpenTime = 1.0f;
        });
        scene.idle(5);
        scene.world().modifyBlockEntity(portableEngine, PortableEngineBlockEntity.class, be -> {
            be.openHatchOverride = false;
            be.setCurrentBurnTime(1337);
            be.setSuperHeated(true);
        });
        world.cycleBlockProperty(portableEngine, (Property)AbstractFurnaceBlock.LIT);
        effects.emitParticles(vector.of(91.9, 3.2, 4.5), effects.simpleParticleEmitter((ParticleOptions)ParticleTypes.LAVA, Vec3.ZERO), 3.0f, 1);
        SymmetricSailScenes.setSymSailKinetics(scene, util, 64.0f);
        scene.idle(3);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)baseplate, new Vec3(5.0, 0.0, 0.0), 60, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundClose, new Vec3(5.0, 0.0, 0.0), 60, SmoothMovementUtils.quadraticRise()));
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)baseplate, new Vec3(15.0, 0.0, 0.0), 40, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundClose, new Vec3(15.0, 0.0, 0.0), 40, SmoothMovementUtils.quadraticRise()));
        scene.idle(40);
        ElementLink groundFar = scene.world().showIndependentSectionImmediately(baseplateLong.add(baseplateStart));
        ArrayList<ElementLink> groundParts = new ArrayList<ElementLink>(List.of(groundClose, groundFar));
        world.moveSection(groundFar, new Vec3(-76.0, 0.0, 0.0), 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)baseplate, new Vec3(96.0, 0.0, 0.0), 120, SmoothMovementUtils.linear()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundClose, new Vec3(96.0, 0.0, 0.0), 120, SmoothMovementUtils.linear()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundFar, new Vec3(96.0, 0.0, 0.0), 120, SmoothMovementUtils.linear()));
        for (ElementLink planePart : planeParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, 0.0, -12.0), 40, SmoothMovementUtils.quadraticRiseInOut()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)planePart, new Vec3(0.5, 0.5, 0.0), 120, SmoothMovementUtils.quadraticRiseInOut()));
        }
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, new Vec3(0.0, 1.75, 0.0), 40, SmoothMovementUtils.quadraticRiseInOut()));
        for (ElementLink groundPart : groundParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundPart, new Vec3(0.0, -1.0, 0.0), 80, SmoothMovementUtils.quadraticRiseInOut()));
        }
        scene.idle(20);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(35.0f, 35.0f, true));
        scene.addInstruction((PonderInstruction)new TranslateYSceneInstruction(-2.0f, 20));
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, new Vec3(0.4, 0.0, 0.0), 80, SmoothMovementUtils.cubicSmoothing()));
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, new Vec3(0.1, -0.4, 0.0), 80, SmoothMovementUtils.quadraticRiseInOut()));
        for (ElementLink planePart : planeParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, 0.0, 5.0), 80, SmoothMovementUtils.quadraticRiseInOut()));
        }
        scene.idle(60);
        overlay.showText(210).text("Lift is generated relative to the speed of the Sail, and is applied perpendicular to its surface").independent().attachKeyFrame();
        scene.idle(20);
        world.hideIndependentSection(baseplate, Direction.DOWN);
        world.showSectionAndMerge(baseplateStart, Direction.UP, groundClose);
        ScrollingSceneRecord scrollingScene = new ScrollingSceneRecord(scene, (ElementLink<WorldSectionElement>)groundClose, (ElementLink<WorldSectionElement>)groundFar, Direction.EAST, 96, 140);
        scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene, true));
        scene.idle(60);
        int arrowHoldTicks = 200;
        Vec3 arrowBase = vector.of(4.0, 3.5, 0.0);
        PonderLineRecord[] thrustArrow = SymmetricSailScenes.createArrow(arrowBase, 8.0f, 7.0f);
        int thrustArrowColor = ((ForceGroup)ForceGroups.PROPULSION.get()).color();
        SymmetricSailScenes.drawArrow(scene, thrustArrow, thrustArrowColor, 3, 20, 200);
        SymmetricSailScenes.linelessTextbox(scene, "Thrust", vector.of(1.0, 3.25, 3.0), PonderPalette.INPUT, 120);
        scene.idle(20);
        PonderLineRecord[] liftArrow = SymmetricSailScenes.createArrow(arrowBase, 3.0f, 97.0f);
        int liftArrowColor = ((ForceGroup)ForceGroups.LIFT.get()).color();
        SymmetricSailScenes.drawArrow(scene, liftArrow, liftArrowColor, 3, 20, 170);
        SymmetricSailScenes.linelessTextbox(scene, "Lift", vector.of(3.1, 5.25, 3.0), PonderPalette.INPUT, 90);
        scene.idle(20);
        PonderLineRecord[] gravityArrow = SymmetricSailScenes.createArrow(arrowBase, 3.0f, -90.0f);
        int gravityArrowColor = ((ForceGroup)ForceGroups.GRAVITY.get()).color();
        SymmetricSailScenes.drawArrow(scene, gravityArrow, gravityArrowColor, 3, 20, 140);
        SymmetricSailScenes.linelessTextbox(scene, "Gravity", vector.of(6.0, 2.5, 3.0), PonderPalette.GREEN, 60);
        scene.idle(10);
        scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene, false));
        scene.idle(60);
        overlay.showText(60).text("When enough Lift is generated...").placeNearTarget().colored(PonderPalette.GREEN).pointAt(arrowBase.add(87.0, 0.0, 0.0)).attachKeyFrame();
        scene.idle(60);
        PonderLineRecord[] netForceArrow = SymmetricSailScenes.createArrow(arrowBase, 7.0f, 0.0f);
        SymmetricSailScenes.lerpArrow(scene, liftArrow, netForceArrow, liftArrowColor, 20);
        SymmetricSailScenes.lerpArrow(scene, thrustArrow, netForceArrow, thrustArrowColor, 20);
        SymmetricSailScenes.lerpArrow(scene, gravityArrow, netForceArrow, gravityArrowColor, 20);
        scene.idle(15);
        SymmetricSailScenes.drawArrow(scene, netForceArrow, PonderPalette.INPUT.getColor(), 5, 10, 95);
        SymmetricSailScenes.linelessTextbox(scene, "Direction of Travel", vector.of(2.5, 3.0, 3.0), PonderPalette.INPUT, 100);
        scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene, true));
        scene.idle(20);
        SymmetricSailScenes.linelessTextbox(scene, "...Flight can be achieved", vector.of(3.05, 5.0, 3.0), PonderPalette.GREEN, 80);
        scene.idle(90);
        scene.addInstruction((PonderInstruction)new CustomParrotFlappingInstruction((ElementLink<ParrotElement>)seatBirb, 40.0f, 40));
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, vector.of(-3.0, 2.0, 0.0), 40, SmoothMovementUtils.quadraticRise()));
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, vector.of(0.0, 0.5, 0.0), 10, SmoothMovementUtils.quadraticRise()));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, vector.of(0.0, 50.0, 0.0), 0, SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(-35.0f, 35.0f, true));
        scene.addInstruction((PonderInstruction)new TranslateYSceneInstruction(-1.0f, 20));
        scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene, false));
        scene.idle(20);
        AABB bb1 = new AABB(95.5, 2.35, 4.35, 97.5, 5.35, 4.65);
        scene.addInstruction((PonderInstruction)new OBBOutlineInstruction(bb1, new Vec3(0.0, 0.0, 7.0), false, PonderPalette.RED, "rudderOutline", 80));
        overlay.showText(80).text("Unlike Regular Sails, Symmetric Sails only produce Drag").placeNearTarget().colored(PonderPalette.RED).pointAt(vector.centerOf(96, 4, 4)).attachKeyFrame();
        scene.idle(100);
        overlay.showText(80).text("Note that when moving parallel to the direction of motion, they have no effect").colored(PonderPalette.BLUE).placeNearTarget().pointAt(vector.centerOf(95, 4, 4));
        AABB bb2 = new AABB(7.5, 3.0, 3.0, 8.5, 6.0, 5.0);
        Vec3 windDir = vector.of(3.0, 0.0, 0.0);
        for (int i = 0; i < 20; ++i) {
            scene.addInstruction((PonderInstruction)new WindstreamInstruction(bb2, windDir, 1, PonderPalette.BLUE, String.valueOf(i), 10, 0));
            if (i == 5) {
                scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene, true));
            }
            scene.idle(4);
        }
        scene.idle(30);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planeRudder, new Vec3(0.0, 30.0, 0.0), 50, SmoothMovementUtils.linear()));
        world.rotateBearing(bearing, 30.0f, 50);
        world.setKineticSpeed(steeringWheel, 8.0f);
        scene.addInstruction((PonderInstruction)SimAnimateBEInstruction.steeringWheel(steeringWheelPos, -30.0f, 50));
        for (ElementLink planePart : planeParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, -35.0, 7.0), 80, SmoothMovementUtils.quadraticRiseInOut()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)planePart, new Vec3(-0.5, 0.0, 0.5), 80, SmoothMovementUtils.quadraticRiseInOut()));
        }
        for (ElementLink groundPart : groundParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundPart, new Vec3(0.0, 0.0, 40.0), 180, SmoothMovementUtils.quadraticRiseInOut()));
        }
        scene.idle(50);
        scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene, false));
        world.setKineticSpeed(steeringWheel, 0.0f);
        AABB bbWindBackground = new AABB(20.0, 0.0, 20.0, -20.0, -5.0, -20.0);
        AABB bbWindDrag = new AABB(8.0, 3.5, 3.0, 9.0, 5.5, 5.0);
        Vec3 angledWindDir = vector.of(2.0, 0.0, 1.4);
        Matrix3d windRotationMatrix = new Matrix3d().rotateY(-Math.PI / 90);
        PonderPalette dragColor = PonderPalette.RED;
        ElementLink ground2Close = scene.world().showIndependentSection(baseplate2Long, Direction.UP);
        ElementLink ground2Far = scene.world().showIndependentSection(baseplate2Long, Direction.UP);
        world.moveSection(ground2Close, vector.of(30.0, -2.0, -40.0), 0);
        world.moveSection(ground2Far, vector.of(100.0, -2.0, -40.0), 0);
        ScrollingSceneRecord scrollingScene2 = new ScrollingSceneRecord(scene, (ElementLink<WorldSectionElement>)ground2Close, (ElementLink<WorldSectionElement>)ground2Far, Direction.EAST, 70, 100);
        ArrayList<ElementLink> ground2Parts = new ArrayList<ElementLink>(List.of(ground2Close, ground2Far));
        for (int i = 0; i < 270; ++i) {
            if (i % 2 == 0) {
                scene.addInstruction((PonderInstruction)new WindstreamInstruction(bbWindBackground, angledWindDir.scale(5.0), 1, PonderPalette.INPUT, String.valueOf(i), 10, 0));
            }
            if (i == 10) {
                scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planeRudder, new Vec3(0.0, -30.0, 0.0), 20, SmoothMovementUtils.linear()));
                world.rotateBearing(bearing, -30.0f, 20);
                world.setKineticSpeed(steeringWheel, -16.0f);
                scene.addInstruction((PonderInstruction)SimAnimateBEInstruction.steeringWheel(steeringWheelPos, 30.0f, 20));
            }
            if (i == 20) {
                world.setKineticSpeed(steeringWheel, 0.0f);
                world.hideIndependentSection(groundClose, Direction.UP);
                world.hideIndependentSection(groundFar, Direction.UP);
            }
            if (i == 30) {
                scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planeRudder, new Vec3(0.0, 30.0, 0.0), 20, SmoothMovementUtils.linear()));
                world.rotateBearing(bearing, 30.0f, 20);
                world.setKineticSpeed(steeringWheel, 16.0f);
                scene.addInstruction((PonderInstruction)SimAnimateBEInstruction.steeringWheel(steeringWheelPos, -30.0f, 20));
            }
            if ((i > 30 && i < 100 || i > 150 && i < 210) && i % 2 == 0) {
                scene.addInstruction((PonderInstruction)new WindstreamInstruction(bbWindDrag, windDir, 1, dragColor, i + "b", 10, 0));
            }
            if (i == 40) {
                world.setKineticSpeed(steeringWheel, 0.0f);
                overlay.showText(120).text("When angled away from the direction of motion, Drag is applied").colored(PonderPalette.RED).placeNearTarget().attachKeyFrame().pointAt(vector.centerOf(95, 4, 4));
            }
            if (i == 100) {
                for (ElementLink planePart : planeParts) {
                    scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, -55.0, 0.0), 110, SmoothMovementUtils.quadraticRiseDual()));
                    scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)planePart, new Vec3(-2.0, 0.0, 0.0), 80, SmoothMovementUtils.cubicSmoothing()));
                }
            }
            if (145 > i && i >= 100) {
                windDir = JOMLConversion.toMojang((Vector3dc)windRotationMatrix.transform(JOMLConversion.toJOML((Position)windDir)));
                bbWindDrag = bbWindDrag.move(vector.of(-0.05, 0.0, 0.0));
            }
            if (i == 115) {
                dragColor = PonderPalette.BLUE;
            }
            if (128 > i && i >= 100) {
                angledWindDir = JOMLConversion.toMojang((Vector3dc)windRotationMatrix.transform(JOMLConversion.toJOML((Position)angledWindDir)));
            }
            if (i == 140) {
                scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planeRudder, new Vec3(0.0, -30.0, 0.0), 20, SmoothMovementUtils.linear()));
                world.rotateBearing(bearing, -30.0f, 20);
                world.setKineticSpeed(steeringWheel, -16.0f);
                scene.addInstruction((PonderInstruction)SimAnimateBEInstruction.steeringWheel(steeringWheelPos, 30.0f, 20));
            }
            if (i == 150) {
                world.setKineticSpeed(steeringWheel, 0.0f);
            }
            if (i == 160) {
                overlay.showText(100).text("This can be used for turning or stabilization surfaces").colored(PonderPalette.BLUE).placeNearTarget().attachKeyFrame().pointAt(vector.centerOf(92, 4, 6));
            }
            if (i >= 215 && i <= 260) {
                angledWindDir = JOMLConversion.toMojang((Vector3dc)windRotationMatrix.transform(JOMLConversion.toJOML((Position)angledWindDir)));
            }
            if (i == 220) {
                windRotationMatrix = new Matrix3d().rotateY(0.05235987755982988);
                scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planeRudder, new Vec3(0.0, -30.0, 0.0), 20, SmoothMovementUtils.linear()));
                world.rotateBearing(bearing, -30.0f, 20);
                world.setKineticSpeed(steeringWheel, -16.0f);
                scene.addInstruction((PonderInstruction)SimAnimateBEInstruction.steeringWheel(steeringWheelPos, 30.0f, 20));
                for (ElementLink planePart : planeParts) {
                    scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, 90.0, -7.0), 110, SmoothMovementUtils.cubicSmoothing()));
                    scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)planePart, new Vec3(2.5, 0.0, -0.5), 110, SmoothMovementUtils.cubicSmoothing()));
                }
                for (ElementLink groundPart : ground2Parts) {
                    scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundPart, new Vec3(0.0, 0.0, 40.0), 140, SmoothMovementUtils.quadraticRiseInOut()));
                }
                scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene2, false));
            }
            if (i == 230) {
                world.setKineticSpeed(steeringWheel, 0.0f);
            }
            if (i == 260) {
                scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planeRudder, new Vec3(0.0, 30.0, 0.0), 20, SmoothMovementUtils.linear()));
                world.rotateBearing(bearing, 30.0f, 20);
                world.setKineticSpeed(steeringWheel, 16.0f);
                scene.addInstruction((PonderInstruction)SimAnimateBEInstruction.steeringWheel(steeringWheelPos, -30.0f, 20));
            }
            scene.idle(2);
        }
        scene.addInstruction((PonderInstruction)new ScrollingSceneInstruction(scrollingScene2, true));
        world.setKineticSpeed(steeringWheel, 0.0f);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(0.0f, -70.0f, true));
        world.cycleBlockProperty(portableEngine, (Property)AbstractFurnaceBlock.LIT);
        SymmetricSailScenes.setSymSailKinetics(scene, util, 0.0f);
        scene.idle(30);
        for (ElementLink planePart : planeParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, 0.0, -7.0), 50, SmoothMovementUtils.quadraticRiseInOut()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, -2.0, 0.0), 80, SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(70);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)ground2Far, new Vec3(140.0, 0.0, 0.0), 0, SmoothMovementUtils.linear()));
        for (ElementLink groundPart : ground2Parts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)groundPart, new Vec3(55.5, 0.0, 0.0), 160, SmoothMovementUtils.quadraticRiseDual()));
        }
        for (ElementLink planePart : planeParts) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, 0.0, 14.0), 30, SmoothMovementUtils.quadraticRiseDual()));
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink<WorldSectionElement>)planePart, new Vec3(0.0, 0.5, 0.0), 30, SmoothMovementUtils.quadraticRiseDual()));
        }
        scene.special().movePointOfInterest(new BlockPos(86, 2, -5));
        scene.idle(160);
        scene.markAsFinished();
        scene.addInstruction((PonderInstruction)new CustomParrotFlappingInstruction((ElementLink<ParrotElement>)seatBirb, 5.0f, 60));
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, vector.of(3.0, -50.0, 15.0), 0, SmoothMovementUtils.linear()));
        scene.addInstruction((PonderInstruction)CustomAnimateParrotInstruction.move((ElementLink<ParrotElement>)seatBirb, vector.of(0.0, -5.0, -15.0), 60, SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(60);
        scene.addInstruction((PonderInstruction)new CustomParrotFlappingInstruction((ElementLink<ParrotElement>)seatBirb));
    }

    public static void setSymSailKinetics(CreateSceneBuilder scene, SceneBuildingUtil util, float rpm) {
        for (int i = 0; i < 2; ++i) {
            scene.world().modifyBlockEntityNBT(util.select().position(93, 2, 3 + 2 * i), AnalogTransmissionBlockEntity.class, nbt -> nbt.getCompound("ExtraCogwheel").putFloat("Speed", rpm));
        }
        Selection inverseKinetics = util.select().position(93, 2, 4);
        Selection kinetics = util.select().fromTo(92, 3, 4, 93, 3, 4).add(util.select().fromTo(93, 2, 5, 94, 2, 7)).substract(inverseKinetics);
        scene.world().setKineticSpeed(kinetics, rpm);
        scene.world().setKineticSpeed(inverseKinetics, -rpm);
    }

    public static PonderLineRecord[] createArrow(Vec3 origin, float length, float angle) {
        Vector3d jomlOrigin = JOMLConversion.toJOML((Position)origin.add(87.0, 0.0, 0.0));
        double radAngle = Math.toRadians(180.0f - angle);
        Matrix3d rotMatrix = new Matrix3d().rotateZ(radAngle);
        Vector3d shaftStart = new Vector3d().add((Vector3dc)jomlOrigin);
        Vector3d shaftEnd = rotMatrix.transform(new Vector3d((double)length, 0.0, 0.0)).add((Vector3dc)jomlOrigin);
        Vector3d pointStart = rotMatrix.transform(new Vector3d((double)length + 0.2, 0.0, 0.0)).add((Vector3dc)jomlOrigin);
        Vector3d p1End = rotMatrix.transform(new Vector3d((double)length - 0.3, 0.5, 0.0)).add((Vector3dc)jomlOrigin);
        Vector3d p2End = rotMatrix.transform(new Vector3d((double)length - 0.3, -0.5, 0.0)).add((Vector3dc)jomlOrigin);
        return new PonderLineRecord[]{new PonderLineRecord(JOMLConversion.toMojang((Vector3dc)shaftStart), JOMLConversion.toMojang((Vector3dc)shaftEnd)), new PonderLineRecord(JOMLConversion.toMojang((Vector3dc)pointStart), JOMLConversion.toMojang((Vector3dc)p1End)), new PonderLineRecord(JOMLConversion.toMojang((Vector3dc)pointStart), JOMLConversion.toMojang((Vector3dc)p2End))};
    }

    public static void drawArrow(CreateSceneBuilder scene, PonderLineRecord[] arrowSet, int color, int size, int lerpTicks, int holdTicks) {
        int lerpEach = lerpTicks / 2;
        scene.addInstruction((PonderInstruction)new ChasingLineInstruction(arrowSet[0], size, color, arrowSet[0].toString(), lerpEach, holdTicks + lerpEach, SmoothMovementUtils.quadraticRise()));
        scene.idle(lerpEach);
        scene.addInstruction((PonderInstruction)new ChasingLineInstruction(arrowSet[1], size, color, arrowSet[1].toString(), lerpEach, holdTicks, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)new ChasingLineInstruction(arrowSet[2], size, color, arrowSet[2].toString(), lerpEach, holdTicks, SmoothMovementUtils.quadraticRiseDual()));
    }

    public static void lerpArrow(CreateSceneBuilder scene, PonderLineRecord[] arrowSetOld, PonderLineRecord[] arrowSetNew, int color, int lerpTicks) {
        for (int i = 0; i < 3; ++i) {
            scene.addInstruction((PonderInstruction)new ChasingLineInstruction(arrowSetOld[i], arrowSetNew[i], 3, color, arrowSetOld[i].toString(), lerpTicks, 0, SmoothMovementUtils.cubicSmoothing()));
        }
    }

    public static void linelessTextbox(CreateSceneBuilder scene, String text, Vec3 position, PonderPalette color, int ticks) {
        TextWindowElement textWindowElement = new TextWindowElement();
        scene.addInstruction((PonderInstruction)new TextInstruction(textWindowElement, ticks));
        ((TextWindowElementExtension)textWindowElement).simulated$hidePointer();
        textWindowElement.builder(scene.getScene()).text(text).colored(color).pointAt(position.add(87.0, 0.0, 0.0)).placeNearTarget();
    }

    public static void symmetricSailWindmill(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("symmetric_sail_windmill", "Assembling Windmills using Symmetric Sails");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(0.9f);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);
        BlockPos bearingPos = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(bearingPos), Direction.DOWN);
        scene.idle(5);
        ElementLink plank = scene.world().showIndependentSection(util.select().position(bearingPos.above()), Direction.DOWN);
        scene.idle(10);
        for (int i = 0; i < 3; ++i) {
            for (Direction d : Iterate.horizontalDirections) {
                BlockPos location = bearingPos.above(i + 1).relative(d);
                scene.world().showSectionAndMerge(util.select().position(location), d.getOpposite(), plank);
                scene.idle(2);
            }
        }
        scene.overlay().showText(70).text("Symmetric Sails are handy blocks to create Windmills with").pointAt(util.vector().blockSurface(util.grid().at(1, 3, 2), Direction.WEST)).placeNearTarget().attachKeyFrame();
        scene.idle(80);
        scene.overlay().showOutlineWithText(util.select().position(bearingPos.above()), 80).colored(PonderPalette.GREEN).text("They will attach to blocks and each other without the need of Super Glue or Chassis Blocks").attachKeyFrame().placeNearTarget();
        scene.idle(40);
        scene.world().configureCenterOfRotation(plank, util.vector().centerOf(bearingPos));
        scene.world().rotateBearing(bearingPos, 180.0f, 75);
        scene.world().rotateSection(plank, 0.0, 180.0, 0.0, 75);
        scene.idle(76);
        scene.rotateCameraY(-30.0f);
        scene.idle(10);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 3, 1), Direction.NORTH), Pointing.RIGHT, 30).withItem(new ItemStack((ItemLike)Items.BLUE_DYE));
        scene.idle(7);
        scene.world().setBlock(util.grid().at(2, 3, 3), (BlockState)SimBlocks.DYED_SYMMETRIC_SAILS.get(DyeColor.BLUE).getDefaultState().setValue((Property)SymmetricSailBlock.AXIS, (Comparable)Direction.Axis.X), false);
        scene.idle(10);
        scene.overlay().showText(40).colored(PonderPalette.BLUE).text("Right-Click with Dye to paint them").attachKeyFrame().pointAt(util.vector().blockSurface(util.grid().at(2, 3, 1), Direction.WEST)).placeNearTarget();
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 3, 1), Direction.NORTH), Pointing.RIGHT, 30).withItem(new ItemStack((ItemLike)Items.BLUE_DYE));
        scene.idle(7);
        scene.world().replaceBlocks(util.select().fromTo(2, 2, 3, 2, 4, 3), (BlockState)SimBlocks.DYED_SYMMETRIC_SAILS.get(DyeColor.BLUE).getDefaultState().setValue((Property)SymmetricSailBlock.AXIS, (Comparable)Direction.Axis.X), false);
        scene.idle(20);
        scene.world().rotateBearing(bearingPos, 720.0f, 300);
        scene.world().rotateSection(plank, 0.0, 720.0, 0.0, 300);
    }
}
