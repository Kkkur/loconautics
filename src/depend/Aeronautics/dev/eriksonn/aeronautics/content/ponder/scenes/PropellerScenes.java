/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$EffectInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$SpecialInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.simulated_team.simulated.ponder.SceneScheduler
 *  dev.simulated_team.simulated.ponder.SceneScheduler$Sequence
 *  dev.simulated_team.simulated.ponder.SmoothMovementUtils
 *  dev.simulated_team.simulated.ponder.instructions.AirflowAABBInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomParrotFlappingInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomParrotSectionLockInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomToggleBaseShadowInstruction
 *  dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.ParrotPose$FacePointOfInterestPose
 *  net.createmod.ponder.api.element.ParrotPose$FlappyPose
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.scene.OverlayInstructions
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.api.scene.SelectionUtil
 *  net.createmod.ponder.api.scene.VectorUtil
 *  net.createmod.ponder.foundation.instruction.AnimateParrotInstruction
 *  net.createmod.ponder.foundation.instruction.DisplayWorldSectionInstruction
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.createmod.ponder.foundation.instruction.RotateSceneInstruction
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.CustomGyroBearingTiltInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerParticleSpawningInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.RedstoneSignalInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.SetPropellerSailsInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.TickingStoppingInstruction;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.ponder.SceneScheduler;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.AirflowAABBInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomParrotFlappingInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomParrotSectionLockInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomToggleBaseShadowInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.createmod.ponder.foundation.instruction.AnimateParrotInstruction;
import net.createmod.ponder.foundation.instruction.DisplayWorldSectionInstruction;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PropellerScenes {
    public static void propellerBearingSize(SceneBuilder builder, SceneBuildingUtil util) {
        ElementLink currentSail;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        CreateSceneBuilder.SpecialInstructions special = scene.special();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        scene.title("propeller_bearing_size", "Constructing Propellers using Propeller Bearings");
        scene.configureBasePlate(1, 1, 5);
        scene.setSceneOffsetY(-3.0f);
        scene.scaleSceneView(0.8f);
        world.showSection(select.layer(0), Direction.UP);
        scene.idle(10);
        BlockPos propellerPos = new BlockPos(3, 2, 3);
        Selection shafts1 = select.fromTo(6, 1, 3, 3, 1, 3);
        world.showSection(shafts1, Direction.DOWN);
        scene.idle(10);
        world.showSection(select.position(propellerPos), Direction.DOWN);
        scene.idle(5);
        ElementLink propellerCenterSection = world.showIndependentSection(select.position(propellerPos.above()), Direction.DOWN);
        scene.idle(5);
        ArrayList<ElementLink> sails = new ArrayList<ElementLink>();
        for (int i = 0; i < 4; ++i) {
            Direction dir = Direction.fromYRot((double)(i * 90));
            Vec3i pos = dir.getNormal().offset(0, 2, 0);
            ElementLink currentSail2 = world.showIndependentSection(select.position(propellerPos.offset(pos)), dir.getOpposite());
            sails.add(currentSail2);
            world.moveSection(currentSail2, new Vec3(0.0, -1.0, 0.0), 0);
            world.configureCenterOfRotation(currentSail2, Vec3.atCenterOf((Vec3i)propellerPos));
            scene.idle(2);
        }
        scene.idle(5);
        overlay.showOutlineWithText(select.position(propellerPos.above()), 60).colored(PonderPalette.GREEN).pointAt(vector.blockSurface(propellerPos.above(), Direction.UP)).placeNearTarget().attachKeyFrame().text("Propeller Bearings attach to the block in front of them");
        scene.idle(65);
        PropellerRotateInstruction propellerRotation = new PropellerRotateInstruction(propellerPos, (ElementLink<WorldSectionElement>)propellerCenterSection, Direction.UP, -32.0f, 4.0f);
        scene.addInstruction((PonderInstruction)propellerRotation);
        for (ElementLink sail : sails) {
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.AddSection(propellerRotation, (ElementLink<WorldSectionElement>)sail));
        }
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotation, null, 1.5f, -5.0f, 1.5f, false));
        scene.idle(10);
        ElementLink flappyBirb = special.createBirb(vector.topOf(propellerPos.above()), ParrotPose.FlappyPose::new);
        scene.idle(2);
        TickingInstruction[] parrotInstructions = new TickingInstruction[]{AnimateParrotInstruction.rotate((ElementLink)flappyBirb, (Vec3)new Vec3(0.0, -4300.0, 0.0), (int)1000), new CustomParrotFlappingInstruction(flappyBirb, 1.0f, 1000)};
        for (int i = 0; i < 2; ++i) {
            scene.addInstruction((PonderInstruction)parrotInstructions[i]);
        }
        special.moveParrot(flappyBirb, vector.of(0.0, 1.0, 0.0), 30);
        Vec3 propellerCenter = new Vec3(3.5, 3.5, 3.5);
        scene.idle(50);
        int i = 0;
        for (ElementLink sail : sails) {
            Direction dir = Direction.fromYRot((double)(i * 90));
            world.hideIndependentSection(sail, dir);
            ++i;
        }
        scene.idle(15);
        sails.clear();
        for (i = 0; i < 4; ++i) {
            Direction dir = Direction.fromYRot((double)(i * 90));
            BlockPos base = propellerPos.offset(0, 1, 0);
            Vec3i pos1 = dir.getNormal();
            Vec3i pos2 = dir.getNormal().cross(new Vec3i(0, -1, 0));
            ElementLink currentSupport = world.showIndependentSection(select.fromTo(base.offset(pos1), base.offset(pos1.multiply(2))), dir.getOpposite());
            currentSail = world.showIndependentSection(select.fromTo(base.offset(pos1.offset(pos2)), base.offset(pos1.multiply(3).offset(pos2))), dir.getOpposite());
            sails.add(currentSail);
            world.configureCenterOfRotation(currentSail, Vec3.atCenterOf((Vec3i)propellerPos));
            world.configureCenterOfRotation(currentSupport, Vec3.atCenterOf((Vec3i)propellerPos));
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.AddSection(propellerRotation, (ElementLink<WorldSectionElement>)currentSail));
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.AddSection(propellerRotation, (ElementLink<WorldSectionElement>)currentSupport));
        }
        scene.idle(3);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotation, null, 5.0f, -5.0f, 3.5f, false));
        special.moveParrot(flappyBirb, vector.of(0.0, 2.0, 0.0), 30);
        scene.idle(5);
        AABB bb = new AABB(propellerCenter, propellerCenter).inflate(3.5, 0.0, 3.5);
        overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, (Object)bb, bb, 3);
        scene.idle(3);
        overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, (Object)bb, bb.expandTowards(0.0, 6.0, 0.0), 70);
        scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.WHITE, bb.expandTowards(0.0, 6.0, 0.0), 70, Direction.UP, 5.0f, 3.0f));
        overlay.showText(65).pointAt(new Vec3(0.0, 4.0, 2.5)).attachKeyFrame().placeNearTarget().text("Propellers with more sails are more efficient at producing Thrust");
        scene.idle(75);
        i = 0;
        for (ElementLink sail : sails) {
            Direction dir = Direction.fromYRot((double)(i * 90 + 180));
            world.hideIndependentSection(sail, dir);
            ++i;
        }
        scene.idle(18);
        sails.clear();
        for (i = 0; i < 4; ++i) {
            Direction dir = Direction.fromYRot((double)(i * 90));
            BlockPos base = propellerPos.offset(0, 2, 0);
            Vec3i pos1 = dir.getNormal();
            Vec3i pos2 = dir.getNormal().cross(new Vec3i(0, -1, 0));
            currentSail = world.showIndependentSection(select.fromTo(base.offset(pos1.offset(pos2)), base.offset(pos1.multiply(3).offset(pos2))), dir.getClockWise());
            sails.add(currentSail);
            world.moveSection(currentSail, new Vec3(0.0, -1.0, 0.0), 0);
            world.configureCenterOfRotation(currentSail, Vec3.atCenterOf((Vec3i)propellerPos));
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.AddSection(propellerRotation, (ElementLink<WorldSectionElement>)currentSail));
        }
        scene.idle(10);
        overlay.showText(80).pointAt(new Vec3(0.0, 4.0, 2.5)).attachKeyFrame().placeNearTarget().colored(PonderPalette.BLUE).text("Any structure can count as a valid Propeller, as long as it has at least 2 valid sail-like blocks");
        scene.idle(100);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(propellerRotation, 30.0f));
        scene.idle(10);
        for (i = 0; i < 2; ++i) {
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(parrotInstructions[i]));
        }
        special.moveParrot(flappyBirb, vector.of(0.0, -3.0, 0.0), 30);
    }

    /*
     * WARNING - void declaration
     */
    public static void propellerBearingThrust(SceneBuilder builder, SceneBuildingUtil util, boolean smolPropeller) {
        AABB bb;
        int[] currentBBSelection;
        int[] nArray;
        void objectArray;
        ElementLink[] elementLinkArray;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        CreateSceneBuilder.SpecialInstructions special = scene.special();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        scene.title("propeller_bearing_thrust", "Moving Simulated Contraptions using Propellers");
        scene.configureBasePlate(0, 0, 12);
        world.multiplyKineticSpeed(select.everywhere(), 1.0f);
        scene.setSceneOffsetY(-2.0f);
        scene.scaleSceneView(0.6f);
        ElementLink ground = world.showIndependentSection(select.fromTo(0, 0, 0, 11, 0, 11), Direction.UP);
        scene.idle(10);
        ElementLink airship = world.showIndependentSection(select.fromTo(3, 3, 7, 10, 3, 11), Direction.DOWN);
        scene.idle(2);
        ElementLink airshipSmall = world.showIndependentSection(select.fromTo(4, 4, 2, 6, 4, 2), Direction.DOWN);
        SceneScheduler sceneScheduler = new SceneScheduler((SceneBuilder)scene);
        SceneScheduler.Sequence seq1 = sceneScheduler.get(0);
        SceneScheduler.Sequence seq2 = sceneScheduler.get(1);
        Supplier<WorldSectionElement> airshipSupplier = () -> (WorldSectionElement)scene.getScene().resolve(airship);
        Supplier<WorldSectionElement> smallAirshipSupplier = () -> (WorldSectionElement)scene.getScene().resolve(airshipSmall);
        for (int x = 8; x >= 3; --x) {
            seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.position(x, 4, 9), airshipSupplier));
            seq1.idle(2);
        }
        seq1.idle(3);
        seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.fromTo(4, 4, 7, 9, 4, 8), airshipSupplier));
        seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.fromTo(4, 4, 10, 9, 4, 11), airshipSupplier));
        seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.position(9, 4, 9), airshipSupplier));
        seq1.idle(4);
        seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.fromTo(4, 5, 7, 8, 7, 7), airshipSupplier));
        seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.fromTo(4, 5, 11, 8, 7, 11), airshipSupplier));
        scene.idle(4);
        seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.fromTo(4, 7, 8, 7, 7, 10), airshipSupplier));
        seq1.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.fromTo(3, 8, 7, 9, 11, 11), airshipSupplier));
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.WEST, select.fromTo(7, 4, 1, 9, 4, 3), smallAirshipSupplier));
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.WEST, select.fromTo(6, 4, 1, 5, 4, 1), smallAirshipSupplier));
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.WEST, select.fromTo(6, 4, 3, 5, 4, 3), smallAirshipSupplier));
        seq2.idle(4);
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.EAST, select.fromTo(2, 4, 1, 3, 4, 3), smallAirshipSupplier));
        seq2.idle(4);
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.SOUTH, select.fromTo(3, 4, 0, 7, 4, 0), smallAirshipSupplier));
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.NORTH, select.fromTo(3, 4, 4, 7, 4, 4), smallAirshipSupplier));
        seq2.idle(4);
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.DOWN, select.fromTo(3, 5, 1, 7, 5, 3), smallAirshipSupplier));
        seq2.addInstruction((PonderInstruction)new DisplayWorldSectionInstruction(15, Direction.UP, select.fromTo(3, 3, 1, 7, 3, 3), smallAirshipSupplier));
        sceneScheduler.run();
        scene.idle(2);
        ElementLink propeller = world.showIndependentSection(select.fromTo(2, 2, 7, 2, 6, 11), Direction.EAST);
        world.showSectionAndMerge(select.position(2, 4, 0).add(select.position(2, 4, 4)), Direction.EAST, airshipSmall);
        if (smolPropeller) {
            ElementLink[] elementLinkArray2 = new ElementLink[1];
            elementLinkArray = elementLinkArray2;
            elementLinkArray2[0] = airshipSmall;
        } else {
            ElementLink[] elementLinkArray3 = new ElementLink[2];
            elementLinkArray3[0] = airship;
            elementLinkArray = elementLinkArray3;
            elementLinkArray3[1] = propeller;
        }
        ElementLink[] shipPieces = elementLinkArray;
        scene.idle(5);
        ElementLink flappyBird = special.createBirb(new Vec3(7.5, 4.5, 9.5), ParrotPose.FacePointOfInterestPose::new);
        special.movePointOfInterest(new Vec3(1000.0, 0.0, 6.0));
        scene.addInstruction((PonderInstruction)new CustomParrotSectionLockInstruction(shipPieces[0], flappyBird, new Vec3(9.5, 4.7, 12.5), 800));
        BlockPos propellerPos = new BlockPos(3, 4, 9);
        scene.addInstruction((PonderInstruction)new SetPropellerSailsInstruction(propellerPos, 8.0f));
        PropellerRotateInstruction propellerRotation = new PropellerRotateInstruction(propellerPos, (ElementLink<WorldSectionElement>)propeller, Direction.WEST, -32.0f, 8.0f);
        scene.addInstruction((PonderInstruction)propellerRotation);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotation, (ElementLink<WorldSectionElement>)airship, 2.0f, -6.0f, 2.0f, false));
        scene.idle(10);
        AABB[] bbs = new AABB[]{new AABB(new Vec3(2.5, 4.5, 0.5), new Vec3(2.5, 4.5, 0.5)).inflate(0.0, 1.0, 1.0), new AABB(new Vec3(2.5, 4.5, 4.5), new Vec3(2.5, 4.5, 4.5)).inflate(0.0, 1.0, 1.0), new AABB(new Vec3(2.5, 4.5, 9.5), new Vec3(2.5, 4.5, 9.5)).inflate(0.0, 2.5, 2.5)};
        AABB[] bbs2 = new AABB[]{new AABB(new Vec3(2.5, 4.5, 4.0), new Vec3(2.5, 4.5, 4.0)).inflate(0.0, 1.0, 1.0), new AABB(new Vec3(2.5, 4.5, 8.0), new Vec3(2.5, 4.5, 8.0)).inflate(0.0, 1.0, 1.0), new AABB(new Vec3(2.5, 4.5, 6.0), new Vec3(2.5, 4.5, 6.0)).inflate(0.0, 2.5, 2.5)};
        Vec3[] bbInfo = new Vec3[]{new Vec3(2.0, 4.0, 1.5), new Vec3(2.0, 4.0, 1.5), new Vec3(6.0, 5.0, 2.0)};
        for (int i = 0; i < bbs.length; ++i) {
            AABB bb2 = bbs[i];
            scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.WHITE, bb2.expandTowards(-bbInfo[i].x, 0.0, 0.0), 90, Direction.WEST, (float)bbInfo[i].y, (float)bbInfo[i].z));
        }
        PropellerParticleSpawningInstruction propParticles1 = new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)airshipSmall, new BlockPos(2, 4, 0), Direction.WEST, 1000, 1.0f, 5.0f, 1.0f, false);
        scene.addInstruction((PonderInstruction)propParticles1);
        PropellerParticleSpawningInstruction propParticles2 = new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)airshipSmall, new BlockPos(2, 4, 4), Direction.WEST, 1000, 1.0f, 5.0f, 1.0f, false);
        scene.addInstruction((PonderInstruction)propParticles2);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(propellerPos.east(5), true, false));
        scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(propellerPos.east(2), propellerPos.east(3)), 8));
        scene.idle(15);
        overlay.showText(60).pointAt(new Vec3(1.5, 4.0, 8.0)).attachKeyFrame().placeNearTarget().text("Several types of propellers can produce Thrust");
        scene.idle(70);
        if (smolPropeller) {
            world.hideIndependentSection(airship, Direction.UP);
            world.hideIndependentSection(propeller, Direction.UP);
            special.hideElement(flappyBird, Direction.UP);
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(propellerRotation, 20.0f));
        } else {
            world.hideIndependentSection(airshipSmall, Direction.UP);
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles1));
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles2));
        }
        scene.idle(10);
        ElementLink[] elementLinkArray4 = shipPieces;
        int n = elementLinkArray4.length;
        boolean bl = false;
        while (objectArray < n) {
            ElementLink shipPiece = elementLinkArray4[objectArray];
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)shipPiece, (Vec3)new Vec3(0.0, 0.0, smolPropeller ? 3.5 : -3.5), (int)20, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
            ++objectArray;
        }
        Vec3 pointingPos = smolPropeller ? new Vec3(2.5, 5.0, 8.0) : new Vec3(3.5, 5.0, 6.0);
        scene.idle(25);
        overlay.showText(60).pointAt(pointingPos).attachKeyFrame().placeNearTarget().text("Thrust can move simulated contraptions");
        scene.idle(15);
        for (ElementLink shipPiece2 : shipPieces) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)shipPiece2, (Vec3)new Vec3(4.0, 0.0, 0.0), (int)70, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(60);
        scene.world().multiplyKineticSpeed(select.everywhere(), -1.0f);
        if (!smolPropeller) {
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetRotationRate(propellerRotation, 32.0f));
        } else {
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles1));
            propParticles1 = new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)airshipSmall, new BlockPos(2, 4, 0), Direction.EAST, 1000, 1.0f, 5.0f, 1.0f, false);
            scene.addInstruction((PonderInstruction)propParticles1);
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles2));
            propParticles2 = new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)airshipSmall, new BlockPos(2, 4, 4), Direction.EAST, 1000, 1.0f, 5.0f, 1.0f, false);
            scene.addInstruction((PonderInstruction)propParticles2);
        }
        scene.idle(10);
        overlay.showText(60).placeNearTarget().attachKeyFrame().pointAt(pointingPos.add(4.0, 0.0, 0.0)).text("Reversing the rotation direction reverses the Thrust");
        if (smolPropeller) {
            int[] nArray2 = new int[2];
            nArray2[0] = 0;
            nArray = nArray2;
            nArray2[1] = 1;
        } else {
            int[] nArray3 = new int[1];
            nArray = nArray3;
            nArray3[0] = 2;
        }
        for (int i : currentBBSelection = nArray) {
            scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.WHITE, bbs2[i].move(4.0, 0.0, 0.0).expandTowards(bbInfo[i].x, 0.0, 0.0), 60, Direction.EAST, (float)bbInfo[i].y, (float)bbInfo[i].z, true, true));
        }
        scene.idle(10);
        ElementLink[] elementLinkArray5 = shipPieces;
        int shipPiece = elementLinkArray5.length;
        for (int shipPiece2 = 0; shipPiece2 < shipPiece; ++shipPiece2) {
            ElementLink shipPiece3 = elementLinkArray5[shipPiece2];
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)shipPiece3, (Vec3)new Vec3(-3.0, 0.0, 0.0), (int)70, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(80);
        pointingPos = pointingPos.add(1.0, 0.0, 0.0);
        if (smolPropeller) {
            Vec3 vec3 = pointingPos;
            overlay.showText(60).placeNearTarget().pointAt(vec3).attachKeyFrame().text("The Thrust can also be reversed using a wrench");
            scene.idle(20);
            overlay.showControls(vec3.add(-0.5, -0.5, -4.0), Pointing.LEFT, 20).withItem(AllItems.WRENCH.asStack());
            scene.idle(10);
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles1));
            propParticles1 = new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)airshipSmall, new BlockPos(2, 4, 0), Direction.WEST, 1000, 1.0f, 5.0f, 1.0f, false);
            scene.addInstruction((PonderInstruction)propParticles1);
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles2));
            propParticles2 = new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)airshipSmall, new BlockPos(2, 4, 4), Direction.WEST, 1000, 1.0f, 5.0f, 1.0f, false);
            scene.addInstruction((PonderInstruction)propParticles2);
            scene.world().modifyBlocks(select.fromTo(2, 4, 0, 2, 4, 4), state -> state.hasProperty((Property)BasePropellerBlock.REVERSED) ? (BlockState)state.setValue((Property)BasePropellerBlock.REVERSED, (Comparable)Boolean.valueOf(true)) : state, false);
        } else {
            Vec3 vec3 = pointingPos.add(0.1, 0.0, 0.0);
            AABB bb3 = new AABB(vec3.x - 0.1, vec3.y, vec3.z - 0.1, vec3.x + 0.1, vec3.y, vec3.z + 0.1);
            overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, (Object)bb3, bb3, 60);
            overlay.showText(60).placeNearTarget().pointAt(vec3).attachKeyFrame().text("The Thrust can also be reversed using the value box");
            scene.idle(20);
            overlay.showControls(vec3, Pointing.DOWN, 20).rightClick();
            scene.idle(10);
        }
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotation, (ElementLink<WorldSectionElement>)airship, 2.0f, 6.0f, 2.0f, false));
        for (int i : currentBBSelection) {
            scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.WHITE, bbs2[i].move(1.0, 0.0, 0.0).expandTowards(-bbInfo[i].x, 0.0, 0.0), 60, Direction.WEST, (float)bbInfo[i].y, (float)bbInfo[i].z, true, true));
        }
        scene.idle(30);
        float f = 53.0f;
        int movementDuration = 353;
        scene.addInstruction((PonderInstruction)new CustomToggleBaseShadowInstruction());
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground, (Vec3)new Vec3(-53.0, 0.0, 0.0), (int)movementDuration, (FloatUnaryOperator)SmoothMovementUtils.asymptoticAcceleration((float)15.0f)));
        ElementLink ground2 = world.showIndependentSection(select.fromTo(12, 0, 2, 55, 1, 9), Direction.WEST);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground2, (Vec3)new Vec3(-53.0, 0.0, 0.0), (int)movementDuration, (FloatUnaryOperator)SmoothMovementUtils.asymptoticAcceleration((float)15.0f)));
        scene.idle(40);
        movementDuration -= 40;
        scene.rotateCameraY(35.0f);
        scene.idle(20);
        movementDuration -= 20;
        world.hideIndependentSection(ground, null);
        for (int i : currentBBSelection) {
            bb = bbs2[i].move(1.0, 0.0, 0.0);
            overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, (Object)bb, bb, 3);
        }
        scene.idle(3);
        for (int i : currentBBSelection) {
            bb = bbs2[i].move(1.0, 0.0, 0.0);
            overlay.chaseBoundingBoxOutline(PonderPalette.WHITE, (Object)bb, bb.expandTowards(-6.0, 0.0, 0.0), 210);
            scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.WHITE, bb.expandTowards(-6.0, 0.0, 0.0), 120, Direction.WEST, 5.0f, 2.0f, true, true));
        }
        scene.idle(10);
        movementDuration -= 13;
        AABB bb1 = new AABB(new Vec3(12.0, 1.5, 3.0), new Vec3(12.0, 1.5, 10.5));
        AABB bb2 = new AABB(new Vec3(12.0, 1.5, 10.5), new Vec3(12.0, 8.0, 10.5));
        overlay.chaseBoundingBoxOutline(PonderPalette.BLUE, (Object)bb1, bb1, 1);
        overlay.chaseBoundingBoxOutline(PonderPalette.BLUE, (Object)bb2, bb2, 1);
        scene.idle(1);
        overlay.chaseBoundingBoxOutline(PonderPalette.BLUE, (Object)bb1, bb1.expandTowards(-15.0, 0.0, 0.0), 175);
        overlay.chaseBoundingBoxOutline(PonderPalette.BLUE, (Object)bb2, bb2.expandTowards(-15.0, 0.0, 0.0), 175);
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.BLUE, bb1.expandTowards(-15.0, 0.0, 0.0), 170, Direction.WEST, 3.15f, 3.0f, false, false));
        scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.BLUE, bb2.expandTowards(-15.0, 0.0, 0.0), 170, Direction.WEST, 3.15f, 3.0f, false, false));
        scene.idle(10);
        movementDuration -= 16;
        Vec3 pointingPos2 = smolPropeller ? pointingPos.add(0.0, 0.75, 0.0) : pointingPos.add(-1.0, 2.0, 0.0);
        overlay.showText(75).pointAt(pointingPos2).attachKeyFrame().placeNearTarget().text("If the Propeller is moving through the air, it cannot push as hard, as the air is already moving");
        scene.idle(50);
        scene.rotateCameraY(-35.0f);
        scene.idle(35);
        movementDuration -= 85;
        overlay.showText(60).pointAt(pointingPos2).attachKeyFrame().placeNearTarget().colored(PonderPalette.RED).text("...and hence its Thrust output decreases");
        scene.idle(5);
        for (int i : currentBBSelection) {
            AABB bb4 = bbs2[i].move(1.0, 0.0, 0.0);
            scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.RED, bb4.expandTowards(-6.0, 0.0, 0.0), 90, Direction.WEST, 5.0f, 4.0f, true, false));
        }
        scene.idle(65);
        movementDuration -= 70;
        overlay.showControls(pointingPos, Pointing.DOWN, 60).withItem(AllItems.GOGGLES.asStack());
        scene.idle(6);
        overlay.showText(80).text("The Propeller's Thrust and Airflow can be inspected with Engineer's Goggles").attachKeyFrame().colored(PonderPalette.BLUE).pointAt(pointingPos).placeNearTarget();
        scene.idle((movementDuration -= 6) - 40);
        ElementLink ground3 = world.showIndependentSection(select.fromTo(0, 0, 0, 11, 0, 11), null);
        world.moveSection(ground3, new Vec3(9.4, 0.0, 0.0), 0);
        world.moveSection(ground3, new Vec3(-6.4, 0.0, 0.0), 40);
        scene.idle(20);
        world.multiplyKineticSpeed(select.layer(4), 0.0f);
        if (smolPropeller) {
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles1));
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(propParticles2));
        } else {
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(propellerRotation, 30.0f));
        }
        world.hideIndependentSection(ground2, null);
        float slowdownSpeed = 3.0f;
        float slowdownDistance = 3.0f;
        int slowdownTime = 40;
        for (ElementLink shipPiece4 : shipPieces) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)shipPiece4, (Vec3)new Vec3(-2.0, 0.0, 0.0), (int)60, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground3, (Vec3)new Vec3(-3.0, 0.0, 0.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(25);
        scene.addInstruction((PonderInstruction)new CustomToggleBaseShadowInstruction());
    }

    public static void gyroBearingStabilize(SceneBuilder builder, SceneBuildingUtil util) {
        Vector3d[] lines;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("gyroscopic_propeller_bearing_stabilize", "Stabilizing helicopters using Gyroscopic Propeller Bearings");
        scene.configureBasePlate(0, 0, 9);
        scene.setSceneOffsetY(-2.0f);
        scene.scaleSceneView(0.8f);
        world.showSection(select.layer(0), Direction.UP);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(new BlockPos(4, 3, 3), true, true));
        world.multiplyKineticSpeed(select.everywhere(), 1.0f);
        ElementLink ship = world.showIndependentSection(select.fromTo(2, 1, 2, 6, 2, 6), Direction.DOWN);
        scene.idle(5);
        world.showSectionAndMerge(select.fromTo(3, 3, 3, 4, 3, 3), Direction.DOWN, ship);
        scene.idle(3);
        world.showSectionAndMerge(select.fromTo(3, 3, 4, 4, 3, 4), Direction.DOWN, ship);
        scene.idle(3);
        world.showSectionAndMerge(select.position(3, 3, 5), Direction.DOWN, ship);
        world.showSectionAndMerge(select.position(4, 3, 5), Direction.DOWN, ship);
        scene.idle(3);
        BlockPos propellerPos = new BlockPos(4, 4, 4);
        world.showSectionAndMerge(select.position(propellerPos), Direction.DOWN, ship);
        scene.idle(3);
        ElementLink propellerSection = world.showIndependentSection(select.fromTo(1, 5, 1, 7, 5, 7), Direction.DOWN);
        scene.idle(5);
        overlay.showOutlineWithText(select.position(propellerPos), 70).text("Normal Propeller Bearings may be unsuitable for helicopters").attachKeyFrame().colored(PonderPalette.RED).pointAt(Vec3.atCenterOf((Vec3i)propellerPos).add(0.0, 0.0, 0.5)).placeNearTarget();
        scene.idle(60);
        float revolutions = 2.5f;
        int duration = (int)(36.0f * revolutions);
        PropellerRotateInstruction propellerRotate = new PropellerRotateInstruction(propellerPos, (ElementLink<WorldSectionElement>)propellerSection, Direction.UP, 32.0f, 8.0f);
        scene.addInstruction((PonderInstruction)propellerRotate);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotate, (ElementLink<WorldSectionElement>)propellerSection, 4.0f, -5.0f, 3.0f, false));
        world.configureCenterOfRotation(propellerSection, new Vec3(4.5, 3.0, 4.5));
        world.configureCenterOfRotation(ship, new Vec3(4.5, 3.0, 4.5));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)ship, (Vec3)new Vec3(0.0, 0.0, 160.0), (int)70, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)propellerSection, (Vec3)new Vec3(0.0, 0.0, 160.0), (int)70, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ship, (Vec3)new Vec3(0.0, 2.5, 0.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)propellerSection, (Vec3)new Vec3(0.0, 2.5, 0.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ship, (Vec3)new Vec3(-12.0, 0.0, 0.0), (int)70, (FloatUnaryOperator)SmoothMovementUtils.cubicRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)propellerSection, (Vec3)new Vec3(-12.0, 0.0, 0.0), (int)70, (FloatUnaryOperator)SmoothMovementUtils.cubicRise()));
        scene.idle(40);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ship, (Vec3)new Vec3(0.0, -3.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)propellerSection, (Vec3)new Vec3(0.0, -3.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        scene.idle(10);
        world.hideIndependentSection(ship, null);
        world.hideIndependentSection(propellerSection, null);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(propellerRotate, 30.0f));
        scene.idle(30);
        ship = world.showIndependentSection(select.fromTo(2, 1, 2, 6, 2, 6).add(select.fromTo(3, 3, 3, 4, 3, 4)), Direction.DOWN);
        world.showSection(select.position(propellerPos), Direction.DOWN);
        ElementLink chainSection = world.showIndependentSection(select.fromTo(3, 3, 5, 4, 3, 5), Direction.DOWN);
        propellerSection = world.showIndependentSection(select.fromTo(1, 5, 1, 7, 5, 7), Direction.DOWN);
        scene.idle(10);
        world.moveSection(propellerSection, new Vec3(0.0, 1.25, 0.0), 15);
        scene.idle(10);
        world.hideSection(select.position(propellerPos), Direction.UP);
        scene.idle(20);
        propellerPos = propellerPos.east();
        ElementLink bearingSection = world.showIndependentSection(select.position(propellerPos), Direction.DOWN);
        world.moveSection(bearingSection, new Vec3(-1.0, 0.0, 0.0), 0);
        scene.idle(15);
        world.moveSection(propellerSection, new Vec3(0.0, -1.25, 0.0), 15);
        scene.idle(10);
        overlay.showOutlineWithText(select.position(propellerPos.west()), 70).text("Gyroscopic Propeller Bearings should be used in this situation").attachKeyFrame().colored(PonderPalette.GREEN).pointAt(Vec3.atCenterOf((Vec3i)propellerPos.west()).add(0.0, 0.0, 0.5)).placeNearTarget();
        scene.idle(60);
        revolutions = 7.0f;
        duration = (int)(36.0f * revolutions);
        propellerRotate = new PropellerRotateInstruction(propellerPos, (ElementLink<WorldSectionElement>)propellerSection, Direction.UP, 32.0f, 8.0f);
        scene.addInstruction((PonderInstruction)propellerRotate);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotate, propellerPos.west().above(), (ElementLink<WorldSectionElement>)propellerSection, 4.0f, -5.0f, 3.0f, false));
        scene.idle(10);
        ElementLink[] allSections = new ElementLink[]{ship, bearingSection, propellerSection, chainSection};
        ElementLink[] nonPropellerSections = new ElementLink[]{ship, bearingSection, chainSection};
        scene.idle(30);
        duration -= 40;
        Vec3 totalMotion = new Vec3(-0.5, 1.5, 0.0);
        for (ElementLink section : allSections) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)section, (Vec3)totalMotion, (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        Vec3 pivotPoint = Vec3.atCenterOf((Vec3i)propellerPos).add(-1.0, 0.25, 0.0);
        for (ElementLink section : nonPropellerSections) {
            world.configureCenterOfRotation(section, pivotPoint);
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)section, (Vec3)new Vec3(0.0, 0.0, 10.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.addInstruction((PonderInstruction)new CustomGyroBearingTiltInstruction((ElementLink<WorldSectionElement>)ship, propellerPos, 50, true));
        world.configureCenterOfRotation(bearingSection, Vec3.atCenterOf((Vec3i)propellerPos).add(0.0, 0.25, 0.0));
        scene.idle(45);
        duration -= 45;
        Vec3 propellerCenter = pivotPoint.add(totalMotion).add(0.0, 0.75, 0.0);
        AABB bb = new AABB(propellerCenter, propellerCenter).inflate(0.5, 0.1, 0.5);
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)bb, bb, 3);
        scene.idle(3);
        duration -= 3;
        overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, (Object)bb, bb.inflate(3.0, 0.0, 3.0), 100);
        double cos = Math.cos(Math.toRadians(12.0));
        double sin = Math.sin(Math.toRadians(12.0));
        Matrix3d m = new Matrix3d(cos, sin, 0.0, -sin, cos, 0.0, 0.0, 0.0, 1.0);
        double s = 1.5;
        double h1 = -0.25;
        double h2 = -0.35;
        double h3 = -2.5;
        for (Vector3d line : lines = new Vector3d[]{new Vector3d(-1.5, -0.25, -1.5), new Vector3d(-1.5, -0.25, 1.5), new Vector3d(-1.5, -0.25, 1.5), new Vector3d(1.5, -0.25, 1.5), new Vector3d(1.5, -0.25, 1.5), new Vector3d(1.5, -0.25, -1.5), new Vector3d(1.5, -0.25, -1.5), new Vector3d(-1.5, -0.25, -1.5), new Vector3d(-1.5, -0.35, -1.5), new Vector3d(-1.5, -2.5, -1.5), new Vector3d(-1.5, -0.35, 1.5), new Vector3d(-1.5, -2.5, 1.5), new Vector3d(1.5, -0.35, -1.5), new Vector3d(1.5, -2.5, -1.5), new Vector3d(1.5, -0.35, 1.5), new Vector3d(1.5, -2.5, 1.5)}) {
            m.transform(line.add(-0.0625, 0.0, 0.0)).add(pivotPoint.x, pivotPoint.y, pivotPoint.z).add(-0.5, 1.5, 0.0);
        }
        for (int i = 0; i < lines.length; i += 2) {
            overlay.showBigLine(PonderPalette.RED, JOMLConversion.toMojang((Vector3dc)lines[i]), JOMLConversion.toMojang((Vector3dc)lines[i + 1]), 100);
        }
        scene.idle(5);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(35.0f, 35.0f, true));
        scene.idle(20);
        duration -= 25;
        overlay.showText(80).text("The Gyroscopic Propeller Bearing attempts to keep the propeller upright").attachKeyFrame().colored(PonderPalette.WHITE).pointAt(pivotPoint.add(totalMotion).add(-0.501, 0.75, 0.0)).placeNearTarget();
        scene.idle(60);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(-35.0f, -35.0f, true));
        scene.idle(50);
        duration -= 90;
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(propellerRotate, 30.0f));
        for (ElementLink section : allSections) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)section, (Vec3)totalMotion.scale(-1.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        for (ElementLink section : nonPropellerSections) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)section, (Vec3)new Vec3(0.0, 0.0, -10.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.addInstruction((PonderInstruction)new CustomGyroBearingTiltInstruction((ElementLink<WorldSectionElement>)ship, propellerPos, 50, true));
    }

    public static void gyroBearingIsland(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        CreateSceneBuilder.SpecialInstructions special = scene.special();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        scene.title("gyroscopic_propeller_bearing_island", "Stabilizing using upside down Gyroscopic Propeller Bearings");
        scene.configureBasePlate(0, 0, 9);
        scene.setSceneOffsetY(-3.0f);
        scene.scaleSceneView(0.8f);
        world.showSection(select.layer(0), Direction.UP);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(new BlockPos(2, 8, 3), true, true));
        scene.idle(5);
        world.multiplyKineticSpeed(select.everywhere(), 1.0f);
        ElementLink propellerSection = world.showIndependentSection(select.layer(3), Direction.DOWN);
        world.moveSection(propellerSection, new Vec3(0.0, -1.0, 0.0), 0);
        BlockPos bearingPos = new BlockPos(4, 4, 4);
        scene.idle(4);
        Selection engineSection = select.position(3, 5, 5).add(select.fromTo(2, 6, 5, 6, 6, 6)).add(select.fromTo(3, 6, 3, 4, 6, 4)).add(select.fromTo(5, 6, 2, 6, 6, 7)).add(select.fromTo(2, 6, 7, 2, 7, 7));
        Selection carpet = select.fromTo(1, 6, 2, 2, 6, 4).add(select.fromTo(2, 6, 1, 3, 6, 2));
        Selection house = select.fromTo(5, 8, 1, 5, 9, 1).add(select.fromTo(4, 8, 2, 6, 9, 6).add(select.fromTo(2, 8, 4, 3, 9, 7)));
        world.setKineticSpeed(select.position(bearingPos), 0.0f);
        ElementLink islandSection = world.showIndependentSection(select.position(bearingPos), Direction.DOWN);
        world.moveSection(islandSection, new Vec3(0.0, -1.0, 0.0), 0);
        scene.idle(5);
        overlay.showText(60).text("When Gyroscopic Propeller Bearings are facing downwards...").attachKeyFrame().colored(PonderPalette.WHITE).pointAt(Vec3.atCenterOf((Vec3i)bearingPos.below())).placeNearTarget();
        scene.idle(65);
        world.showSectionAndMerge(select.layers(4, 2).substract(select.position(bearingPos)).substract(engineSection).add(carpet), Direction.DOWN, islandSection);
        scene.idle(5);
        world.showSectionAndMerge(engineSection, Direction.DOWN, islandSection);
        world.multiplyKineticSpeed(engineSection, 1.0f);
        scene.idle(3);
        world.configureCenterOfRotation(propellerSection, new Vec3(4.5, 4.25, 4.5));
        world.configureCenterOfRotation(islandSection, new Vec3(4.5, 4.25, 4.5));
        PropellerRotateInstruction propellerRotate = new PropellerRotateInstruction(bearingPos, (ElementLink<WorldSectionElement>)propellerSection, Direction.DOWN, 32.0f, 16.0f);
        scene.addInstruction((PonderInstruction)propellerRotate);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotate, bearingPos.below(), (ElementLink<WorldSectionElement>)propellerSection, 4.0f, 5.0f, 4.0f, true));
        scene.idle(3);
        world.showSectionAndMerge(select.layer(6).substract(engineSection).substract(carpet), Direction.DOWN, islandSection);
        scene.idle(5);
        world.showSectionAndMerge(select.layers(7, 3).substract(house), Direction.DOWN, islandSection);
        scene.idle(5);
        world.showSectionAndMerge(select.layer(10).add(house), Direction.DOWN, islandSection);
        ElementLink flappyBird = special.createBirb(new Vec3(1.5, 8.6, 1.5), ParrotPose.FacePointOfInterestPose::new);
        special.movePointOfInterest(new Vec3(1000.0, 0.0, 6.0));
        scene.addInstruction((PonderInstruction)new CustomParrotSectionLockInstruction(islandSection, flappyBird, new Vec3(1.5, 8.6, 1.5), 200));
        scene.idle(15);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(35.0f, 35.0f, true));
        scene.idle(40);
        scene.addInstruction((PonderInstruction)new CustomGyroBearingTiltInstruction((ElementLink<WorldSectionElement>)islandSection, bearingPos, 200, true));
        special.movePointOfInterest(Vec3.atCenterOf((Vec3i)bearingPos));
        float n = 2.0f;
        FloatUnaryOperator angleFunction = t -> (float)Math.sin(Math.PI * 2 * (double)SmoothMovementUtils.cubicSmoothing().apply(t));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)islandSection, (Vec3)new Vec3(0.0, 0.0, 4.0), (int)150, (FloatUnaryOperator)angleFunction));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)propellerSection, (Vec3)new Vec3(0.0, 0.0, 10.0), (int)150, (FloatUnaryOperator)angleFunction));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)islandSection, (Vec3)new Vec3(0.2, 0.0, 0.0), (int)150, (FloatUnaryOperator)angleFunction));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)propellerSection, (Vec3)new Vec3(0.2, 0.0, 0.0), (int)150, (FloatUnaryOperator)angleFunction));
        scene.idle(20);
        overlay.showText(60).text("...they can stabilize a top-heavy Structure").attachKeyFrame().colored(PonderPalette.WHITE).pointAt(Vec3.atCenterOf((Vec3i)bearingPos.below()).add(0.001, 0.001, 0.001)).placeNearTarget();
        scene.idle(90);
        special.movePointOfInterest(new Vec3(1000.0, 0.0, 6.0));
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(-35.0f, -35.0f, true));
        scene.idle(20);
        scene.markAsFinished();
    }

    public static void gyroBearingRedstone(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        OverlayInstructions overlay = scene.overlay();
        scene.title("gyroscopic_propeller_bearing_redstone", "Controlling Gyroscopic Propeller Bearings with Redstone");
        scene.configureBasePlate(0, 0, 9);
        scene.setSceneOffsetY(-2.5f);
        scene.scaleSceneView(0.8f);
        world.showSection(util.select().layer(0), Direction.UP);
        world.multiplyKineticSpeed(util.select().everywhere(), 1.0f);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(new BlockPos(4, 2, 5), true, true));
        scene.idle(5);
        BlockPos bearingPos = new BlockPos(4, 3, 4);
        ElementLink airship = world.showIndependentSection(util.select().fromTo(2, 1, 1, 7, 1, 7).substract(util.select().position(3, 1, 4)), Direction.DOWN);
        scene.idle(6);
        world.showSectionAndMerge(util.select().fromTo(7, 2, 4, 6, 2, 4), Direction.DOWN, airship);
        scene.idle(2);
        world.showSectionAndMerge(util.select().position(5, 2, 4), Direction.DOWN, airship);
        scene.idle(2);
        world.showSectionAndMerge(util.select().position(4, 2, 5), Direction.DOWN, airship);
        ElementLink valve = world.showIndependentSection(util.select().position(4, 2, 3), Direction.DOWN);
        scene.idle(4);
        world.showSectionAndMerge(util.select().fromTo(2, 2, 1, 6, 2, 7).substract(util.select().fromTo(3, 2, 2, 5, 2, 6)).substract(util.select().position(6, 2, 4)), Direction.DOWN, airship);
        scene.idle(4);
        ElementLink pivotSection = world.showIndependentSection(util.select().fromTo(3, 1, 4, 3, 3, 4).add(util.select().position(4, 2, 4)), Direction.DOWN);
        scene.idle(4);
        ElementLink flappyBird = scene.special().createBirb(new Vec3(5.5, 2.6, 4.5), ParrotPose.FacePointOfInterestPose::new);
        scene.special().movePointOfInterest(new Vec3(30.0, 4.0, 4.0));
        scene.addInstruction((PonderInstruction)new CustomParrotSectionLockInstruction(airship, flappyBird, new Vec3(5.5, 2.6, 4.5), 200));
        world.showSectionAndMerge(util.select().position(bearingPos), Direction.DOWN, pivotSection);
        scene.idle(4);
        ElementLink propellerSection = world.showIndependentSection(util.select().layer(4), Direction.DOWN);
        ElementLink[] allSections = new ElementLink[]{airship, valve, pivotSection, propellerSection};
        scene.idle(20);
        PropellerRotateInstruction propellerRotate = new PropellerRotateInstruction(bearingPos, (ElementLink<WorldSectionElement>)propellerSection, Direction.UP, 32.0f, 8.0f);
        scene.addInstruction((PonderInstruction)propellerRotate);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotate, (ElementLink<WorldSectionElement>)propellerSection, 3.0f, -4.0f, 2.5f, false));
        scene.idle(5);
        for (ElementLink section : allSections) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)section, (Vec3)new Vec3(0.0, 2.0, 0.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        }
        scene.idle(35);
        scene.special().movePointOfInterest(new Vec3(4.5, 4.5, 3.5));
        scene.idle(15);
        world.configureCenterOfRotation(propellerSection, new Vec3(4.5, 1.5, 4.5));
        world.configureCenterOfRotation(pivotSection, new Vec3(3.5, 1.5, 4.5));
        world.setKineticSpeed(util.select().fromTo(4, 1, 3, 4, 1, 5), -16.0f);
        world.setKineticSpeed(util.select().position(3, 1, 5), 16.0f);
        world.modifyBlockEntityNBT(util.select().position(3, 1, 5), KineticBlockEntity.class, nbt -> nbt.getCompound("SwivelCog").putFloat("Speed", 16.0f));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)valve, (Vec3)new Vec3(0.0, 90.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)propellerSection, (Vec3)new Vec3(0.0, 0.0, 90.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)propellerSection, (Vec3)new Vec3(-1.0, 1.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)pivotSection, (Vec3)new Vec3(0.0, 0.0, 90.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        scene.idle(30);
        world.setKineticSpeed(util.select().position(4, 2, 3), 0.0f);
        world.setKineticSpeed(util.select().fromTo(4, 1, 3, 4, 1, 5), 0.0f);
        world.setKineticSpeed(util.select().position(3, 1, 5), 0.0f);
        world.modifyBlockEntityNBT(util.select().position(3, 1, 5), KineticBlockEntity.class, nbt -> nbt.getCompound("SwivelCog").putFloat("Speed", 0.0f));
        scene.idle(20);
        scene.special().movePointOfInterest(new Vec3(1.5, 4.5, 4.5));
        world.multiplyKineticSpeed(util.select().everywhere(), -1.0f);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetRotationRate(propellerRotate, -32.0f));
        world.configureCenterOfRotation(propellerSection, new Vec3(4.5, 3.75, 4.5));
        world.moveSection(propellerSection, new Vec3(-2.25, -2.25, 0.0), 0);
        scene.addInstruction((PonderInstruction)new CustomGyroBearingTiltInstruction((ElementLink<WorldSectionElement>)pivotSection, bearingPos, 20, false));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)propellerSection, (Vec3)new Vec3(0.0, 0.0, -12.0), (int)20, (FloatUnaryOperator)SmoothMovementUtils.linear()));
        scene.idle(10);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(35.0f, 35.0f, true));
        scene.idle(10);
        overlay.showBigLine(PonderPalette.RED, new Vec3(1.25, 4.5, 4.5), new Vec3(1.25, 4.5, 4.5).add(new Vec3(-Math.cos(Math.toRadians(12.0)), Math.sin(Math.toRadians(12.0)), 0.0).scale(3.5)), 135);
        scene.idle(10);
        overlay.showText(60).text("Sometimes the upwards Tilt can be undesired").attachKeyFrame().colored(PonderPalette.WHITE).pointAt(new Vec3(1.251, 4.51, 4.51)).placeNearTarget();
        scene.idle(50);
        scene.addInstruction((PonderInstruction)new RotateSceneInstruction(-35.0f, -35.0f, true));
        scene.idle(30);
        world.showSectionAndMerge(util.select().fromTo(1, 1, 2, 1, 2, 3), Direction.DOWN, airship);
        scene.special().movePointOfInterest(new Vec3(1.5, 3.5, 1.5));
        scene.idle(25);
        world.toggleRedstonePower(util.select().fromTo(1, 2, 2, 1, 2, 3));
        effects.indicateRedstone(new BlockPos(1, 4, 2));
        scene.addInstruction((PonderInstruction)new CustomGyroBearingTiltInstruction((ElementLink<WorldSectionElement>)pivotSection, bearingPos, 20, false, true));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.rotate((ElementLink)propellerSection, (Vec3)new Vec3(0.0, 0.0, 12.0), (int)20, (FloatUnaryOperator)SmoothMovementUtils.linear()));
        scene.idle(20);
        overlay.showText(60).text("Applying a Redstone signal will reset the Tilt to zero").attachKeyFrame().colored(PonderPalette.WHITE).pointAt(new Vec3(1.25, 4.5, 4.5)).placeNearTarget();
        overlay.showBigLine(PonderPalette.GREEN, new Vec3(1.25, 4.5, 4.5).add(new Vec3(-3.5, 0.0, 0.0)), new Vec3(1.25, 4.5, 4.5), 60);
        scene.idle(30);
    }
}
