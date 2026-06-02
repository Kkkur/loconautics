/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$EffectInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  dev.simulated_team.simulated.content.blocks.analog_transmission.AnalogTransmissionBlockEntity
 *  dev.simulated_team.simulated.ponder.SmoothMovementUtils
 *  dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction
 *  dev.simulated_team.simulated.ponder.instructions.OffsetBreakParticlesInstruction
 *  dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction
 *  dev.simulated_team.simulated.ponder.instructions.TranslateYSceneInstruction
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
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
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.offroad.content.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlockEntity;
import dev.ryanhcode.offroad.content.ponder.instructions.ChangeBoreheadAndContraptionSpeedInstruction;
import dev.ryanhcode.offroad.content.ponder.instructions.StopBoreheadBearingAndContraptionInstruction;
import dev.simulated_team.simulated.content.blocks.analog_transmission.AnalogTransmissionBlockEntity;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.OffsetBreakParticlesInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import dev.simulated_team.simulated.ponder.instructions.TranslateYSceneInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
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
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BoreheadBearingScenes {
    public static void boreheadIntro(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        scene.title("borehead_bearing_intro", "Using Borehead Bearings and Rock Cutting Wheels");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        Selection borehead = select.fromTo(util.grid().at(1, 2, 2), util.grid().at(3, 2, 2));
        Selection boreheadCenter = select.position(util.grid().at(2, 2, 2));
        BlockPos boreheadBearing = util.grid().at(2, 2, 3);
        BlockPos funnel = util.grid().at(2, 2, 4);
        Selection positiveKinetics = select.fromTo(util.grid().at(0, 2, 3), util.grid().at(1, 4, 3));
        Selection negativeKinetics = select.fromTo(util.grid().at(5, 0, 3), util.grid().at(3, 4, 3));
        Selection gearboxKinetics = select.fromTo(util.grid().at(0, 4, 3), util.grid().at(4, 4, 3));
        Selection wallLocation = select.fromTo(util.grid().at(1, 1, 1), util.grid().at(3, 3, 1));
        world.showSection(select.position(boreheadBearing.below()), Direction.UP);
        scene.idle(15);
        world.showSection(select.position(boreheadBearing), Direction.DOWN);
        scene.idle(5);
        world.showSection(select.position(util.grid().at(3, 2, 3)), Direction.DOWN);
        scene.idle(3);
        world.showSection(select.position(util.grid().at(4, 2, 3)), Direction.DOWN);
        scene.idle(3);
        ElementLink belt = world.showIndependentSection(select.fromTo(util.grid().at(5, 1, 3), util.grid().at(5, 4, 3)), Direction.DOWN);
        world.moveSection(belt, vector.of(0.0, -2.0, 0.0), 0);
        scene.idle(20);
        scene.overlay().showOutlineWithText(boreheadCenter, 60).colored(PonderPalette.GREEN).pointAt(util.vector().blockSurface(boreheadBearing, Direction.WEST)).attachKeyFrame().placeNearTarget().text("Borehead Bearings attach to the block in front of them");
        scene.idle(50);
        ElementLink boreheadLink = world.showIndependentSection(boreheadCenter, Direction.SOUTH);
        scene.idle(10);
        world.showSectionAndMerge(borehead.substract(boreheadCenter), Direction.SOUTH, boreheadLink);
        scene.idle(5);
        scene.effects().superGlue(boreheadBearing.north(), Direction.SOUTH, true);
        scene.idle(25);
        scene.overlay().showText(100).pointAt(boreheadCenter.getCenter().add(0.0, 0.5, 0.0)).attachKeyFrame().placeNearTarget().text("When a Storage Container and Rock Cutting Wheels are present, it can be used as a Drill");
        scene.idle(80);
        world.setKineticSpeed(positiveKinetics, 64.0f);
        world.setKineticSpeed(negativeKinetics, -64.0f);
        ChangeBoreheadAndContraptionSpeedInstruction vehicle32Instr = new ChangeBoreheadAndContraptionSpeedInstruction(boreheadBearing, (ElementLink<WorldSectionElement>)boreheadLink, ChangeBoreheadAndContraptionSpeedInstruction.RotationAxis.Z, -64.0f);
        scene.addInstruction((PonderInstruction)vehicle32Instr);
        BoreheadBearingScenes.spinRockCutters(scene, util, util.grid().at(1, 2, 2), util.grid().at(3, 2, 2), 5, 1000);
        scene.idle(40);
        scene.overlay().showText(100).pointAt(boreheadCenter.getCenter().add(-1.0, 0.0, 0.0)).colored(PonderPalette.GREEN).placeNearTarget().text("Rock Cutting Wheels will automatically attach to blocks and each other without need of Super Glue");
        scene.idle(120);
        scene.rotateCameraY(-90.0f);
        scene.idle(20);
        world.hideSection(select.fromTo(util.grid().at(3, 2, 3), util.grid().at(4, 2, 3)), Direction.DOWN);
        scene.idle(10);
        ElementLink gearboxKineticsLink = world.showIndependentSection(gearboxKinetics, Direction.DOWN);
        world.moveSection(gearboxKineticsLink, vector.of(0.0, -2.0, 0.0), 0);
        for (BlockPos rotatingKinetics : select.fromTo(0, 2, 3, 4, 2, 3).substract(select.position(boreheadBearing))) {
            effects.rotationDirectionIndicator(rotatingKinetics);
        }
        scene.idle(20);
        scene.overlay().showText(80).pointAt(vector.blockSurface(boreheadBearing, Direction.SOUTH)).attachKeyFrame().placeNearTarget().colored(PonderPalette.INPUT).text("The Borehead Bearing reverses the direction of Rotation...");
        scene.idle(100);
        world.setKineticSpeed(positiveKinetics, 128.0f);
        world.setKineticSpeed(negativeKinetics.add(select.position(boreheadBearing)), -128.0f);
        BoreheadBearingScenes.spinRockCutters(scene, util, util.grid().at(1, 2, 2), util.grid().at(3, 2, 2), 15, 1000);
        for (BlockPos rotatingKinetics : select.fromTo(0, 2, 3, 4, 2, 3).substract(select.position(boreheadBearing))) {
            effects.rotationDirectionIndicator(rotatingKinetics);
        }
        scene.idle(20);
        scene.overlay().showText(80).pointAt(vector.topOf(boreheadBearing).add(0.0, 0.0, -0.5)).attachKeyFrame().placeNearTarget().colored(PonderPalette.OUTPUT).text("...and spins at 0.25x the provided Rotational Speed");
        scene.idle(100);
        scene.rotateCameraY(90.0f);
        scene.idle(40);
        scene.overlay().showText(120).pointAt(vector.centerOf(util.grid().at(2, 2, 1))).attachKeyFrame().placeNearTarget().text("The mining speed depends on the Rotational Input...");
        for (int i = 0; i < 4; ++i) {
            if (i == 2) {
                scene.overlay().showText(100).pointAt(vector.centerOf(util.grid().at(2, 2, 1))).attachKeyFrame().placeNearTarget().text("...and drops are collected into the drill Storage automatically");
            }
            Selection stoneWall = select.fromTo(util.grid().at(1, 1 + 4 * i, 1), util.grid().at(3, 3 + 4 * i, 1));
            ElementLink stoneWallLink = world.showIndependentSection(stoneWall, Direction.SOUTH);
            world.moveSection(stoneWallLink, vector.of(0.0, (double)(-4 * i), 0.0), 0);
            scene.idle(5);
            for (int j = 0; j < (i < 3 ? 9 : 6); ++j) {
                scene.idle(5);
                for (BlockPos stoneBlock : stoneWall) {
                    world.incrementBlockBreakingProgress(stoneBlock.immutable());
                }
            }
            scene.idle(5);
            if (i >= 3) continue;
            world.replaceBlocks(stoneWall, Blocks.AIR.defaultBlockState(), false);
            for (BlockPos blockPos : wallLocation) {
                scene.addInstruction((PonderInstruction)new OffsetBreakParticlesInstruction(new AABB(blockPos), Blocks.STONE.defaultBlockState()));
            }
            scene.idle(20);
        }
        BoreheadBearingScenes.spinRockCutters(scene, util, util.grid().at(1, 2, 2), util.grid().at(3, 2, 2), 0, 0);
        world.modifyBlockEntity(boreheadBearing, BoreheadBearingBlockEntity.class, be -> be.setStalled(true));
        for (BlockPos wallBlock : wallLocation) {
            effects.emitParticles(Vec3.atLowerCornerOf((Vec3i)wallBlock), effects.particleEmitterWithinBlockSpace((ParticleOptions)ParticleTypes.CLOUD, new Vec3(0.0, 0.0, 0.0)), 5.0f, 2);
        }
        overlay.showOutline(PonderPalette.RED, (Object)"full", wallLocation, 60);
        scene.idle(20);
        scene.overlay().showText(100).pointAt(vector.centerOf(util.grid().at(2, 2, 1))).colored(PonderPalette.RED).attachKeyFrame().placeNearTarget().text("When the drill Storage is full, it will be unable to continue breaking blocks");
        scene.idle(120);
        scene.rotateCameraY(-90.0f);
        scene.idle(20);
        world.showSection(select.position(funnel), Direction.NORTH);
        scene.idle(10);
        Item[] collectedItems = new Item[]{Items.COBBLESTONE, Items.COBBLESTONE, AllItems.RAW_ZINC.asItem(), Items.RAW_GOLD, Items.DIAMOND};
        BoreheadBearingScenes.spinRockCutters(scene, util, util.grid().at(1, 2, 2), util.grid().at(3, 2, 2), 15, 1000);
        world.modifyBlockEntity(boreheadBearing, BoreheadBearingBlockEntity.class, be -> be.setStalled(false));
        scene.overlay().showText(100).pointAt(vector.centerOf(funnel)).attachKeyFrame().placeNearTarget().text("To remedy this, items can be extracted directly from the Borehead Bearing");
        Selection lastWall = select.fromTo(util.grid().at(1, 13, 1), util.grid().at(3, 15, 1));
        for (int i = 0; i < 5; ++i) {
            if (i < 3) {
                for (BlockPos stoneBlock : lastWall) {
                    world.incrementBlockBreakingProgress(stoneBlock.immutable());
                }
            }
            if (i == 3) {
                world.replaceBlocks(lastWall, Blocks.AIR.defaultBlockState(), false);
                for (BlockPos blockPos : wallLocation) {
                    scene.addInstruction((PonderInstruction)new OffsetBreakParticlesInstruction(new AABB(blockPos), Blocks.STONE.defaultBlockState()));
                }
            }
            world.flapFunnel(funnel, true);
            scene.world().createItemEntity(Vec3.atBottomCenterOf((Vec3i)funnel), vector.of(0.0, 0.0, 0.0), new ItemStack((ItemLike)collectedItems[i]).copyWithCount(32));
            scene.idle(10);
        }
    }

    public static void boreheadExcavating(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        scene.title("borehead_bearing_excavating", "Excavating Using Rock Cutting Wheels");
        scene.setSceneOffsetY(-2.0f);
        scene.scaleSceneView(0.75f);
        BlockPos boreheadBearing = new BlockPos(4, 3, 9);
        Selection lever = select.position(3, 2, 12);
        Selection borehead = select.fromTo(3, 2, 8, 5, 4, 8);
        Selection miningVehicle = select.fromTo(3, 1, 8, 5, 4, 12).substract(borehead);
        Selection positiveRockWheels = select.position(3, 3, 8).add(select.position(4, 2, 8));
        Selection negativeRockWheels = select.position(5, 3, 8).add(select.position(4, 5, 8));
        Selection vehicleBelts = select.fromTo(3, 1, 8, 5, 1, 12);
        Selection otherKinetics = select.fromTo(4, 1, 10, 4, 3, 12);
        Selection transPos = select.position(4, 2, 9);
        Selection tunnel = select.fromTo(0, 1, 6, 8, 8, 12).substract(miningVehicle).substract(borehead);
        Selection minedBlocks = select.fromTo(3, 1, 7, 5, 5, 7).add(select.fromTo(6, 2, 7, 2, 4, 7));
        scene.configureBasePlate(0, 0, 9);
        world.showSection(tunnel.add(select.layer(0)), Direction.UP);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(new BlockPos(4, 4, 10), true, true));
        world.toggleRedstonePower(lever);
        ElementLink bodyLink = world.showIndependentSectionImmediately(miningVehicle);
        ElementLink boreheadLink = world.showIndependentSectionImmediately(select.fromTo(3, 3, 8, 5, 3, 8));
        ElementLink[] vehicleLink = new ElementLink[]{bodyLink, boreheadLink};
        world.rotateSection(boreheadLink, 0.0, 0.0, 180.0, 0);
        ChangeBoreheadAndContraptionSpeedInstruction vehicle128Instr = new ChangeBoreheadAndContraptionSpeedInstruction(boreheadBearing, (ElementLink<WorldSectionElement>)boreheadLink, ChangeBoreheadAndContraptionSpeedInstruction.RotationAxis.Z, -128.0f);
        scene.addInstruction((PonderInstruction)vehicle128Instr);
        scene.idle(20);
        for (int i = 0; i < 10; ++i) {
            scene.idle(2);
            for (BlockPos minedBlock : minedBlocks) {
                world.incrementBlockBreakingProgress(minedBlock.immutable());
            }
        }
        BoreheadBearingScenes.spinRockCutters(scene, positiveRockWheels, negativeRockWheels, 10, 48);
        for (ElementLink part : vehicleLink) {
            world.moveSection(part, vector.of(0.0, 0.0, -3.0), 48);
        }
        world.setKineticSpeed(vehicleBelts, -32.0f);
        world.setKineticSpeed(otherKinetics, 32.0f);
        world.modifyBlockEntityNBT(transPos, AnalogTransmissionBlockEntity.class, nbt -> nbt.getCompound("ExtraCogwheel").putFloat("Speed", -32.0f));
        scene.idle(48);
        world.setKineticSpeed(vehicleBelts, 0.0f);
        scene.world().modifyBlockEntityNBT(select.position(3, 2, 10), AnalogLeverBlockEntity.class, nbt -> nbt.putInt("State", 15));
        world.toggleRedstonePower(lever);
        for (ElementLink part : vehicleLink) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)part, (Vec3)new Vec3(0.0, 0.0, -0.25), (int)10, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        }
        scene.addInstruction((PonderInstruction)new StopBoreheadBearingAndContraptionInstruction(boreheadBearing, vehicle128Instr, false));
        scene.idle(30);
        overlay.showText(100).text("Unlike regular Drills, Rock Cutting Wheels break blocks in a range around themselves").pointAt(new Vec3(4.5, 3.5, 5.0)).attachKeyFrame().placeNearTarget();
        scene.idle(120);
    }

    public static void boreheadEfficiency(SceneBuilder builder, SceneBuildingUtil util) {
        int i;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        scene.title("borehead_bearing_efficiency", "Borehead Bearing Efficiency");
        scene.addInstruction((PonderInstruction)new TranslateYSceneInstruction(-1.0f, 0));
        scene.scaleSceneView(0.75f);
        Selection firstRock = select.fromTo(util.grid().at(1, 1, 3), util.grid().at(5, 5, 4));
        Selection secondRock = select.fromTo(util.grid().at(1, 1, 0), util.grid().at(5, 5, 1));
        Selection bigBore = select.fromTo(util.grid().at(0, 3, 6), util.grid().at(6, 5, 6));
        Selection rightBore = select.fromTo(util.grid().at(0, 3, 6), util.grid().at(2, 5, 6));
        Selection leftBore = select.fromTo(util.grid().at(4, 3, 6), util.grid().at(6, 5, 6));
        Selection bigBoreCenter = select.position(util.grid().at(3, 4, 6));
        Selection singleBelts = select.fromTo(util.grid().at(2, 6, 5), util.grid().at(4, 6, 7));
        Selection doubleBelts = select.fromTo(util.grid().at(0, 6, 7), util.grid().at(6, 6, 9)).substract(singleBelts);
        Selection positiveKinetics = select.fromTo(util.grid().at(2, 0, 7), util.grid().at(4, 3, 7)).add(select.fromTo(util.grid().at(5, 4, 9), util.grid().at(6, 4, 9))).add(select.fromTo(util.grid().at(0, 0, 9), util.grid().at(0, 3, 9))).add(select.fromTo(util.grid().at(0, 6, 7), util.grid().at(0, 6, 9)));
        Selection negativeKinetics = select.fromTo(util.grid().at(6, 0, 9), util.grid().at(6, 3, 9)).add(select.fromTo(util.grid().at(0, 4, 7), util.grid().at(4, 4, 9))).add(select.fromTo(util.grid().at(6, 6, 7), util.grid().at(6, 6, 9)));
        Selection singleKinetics = select.fromTo(util.grid().at(2, 0, 7), util.grid().at(4, 4, 7));
        Selection doubleKinetics = select.fromTo(util.grid().at(0, 0, 9), util.grid().at(6, 4, 9));
        BlockPos singleBorehead = util.grid().at(3, 4, 7);
        BlockPos[] doubleBorehead = new BlockPos[]{util.grid().at(5, 4, 9), util.grid().at(1, 4, 9)};
        BlockPos[] rockCutters = new BlockPos[4];
        for (int i2 = 0; i2 < 4; ++i2) {
            rockCutters[i2] = util.grid().at(2 * i2, 4, 6);
        }
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();
        scene.idle(10);
        ElementLink singleKineticsLink = world.showIndependentSection(singleKinetics.substract(select.position(singleBorehead)), Direction.DOWN);
        ElementLink singleBeltsLink = world.showIndependentSectionImmediately(singleBelts);
        world.moveSection(singleBeltsLink, vector.of(0.0, -7.0, 0.0), 0);
        scene.idle(7);
        world.showSectionAndMerge(select.position(singleBorehead), Direction.DOWN, singleKineticsLink);
        scene.idle(7);
        ElementLink bigBearingLink = world.showIndependentSection(bigBore, Direction.SOUTH);
        scene.idle(10);
        world.setKineticSpeed(positiveKinetics, 64.0f);
        world.setKineticSpeed(negativeKinetics, -64.0f);
        ChangeBoreheadAndContraptionSpeedInstruction singleBoreInstr = new ChangeBoreheadAndContraptionSpeedInstruction(singleBorehead, (ElementLink<WorldSectionElement>)bigBearingLink, ChangeBoreheadAndContraptionSpeedInstruction.RotationAxis.Z, -64.0f);
        scene.addInstruction((PonderInstruction)singleBoreInstr);
        for (i = 1; i <= 8; ++i) {
            scene.idle(4);
            BoreheadBearingScenes.spinRockCutters(scene, select.position(rockCutters[0]).add(select.position(rockCutters[1])), select.position(rockCutters[2]).add(select.position(rockCutters[3])), i, 1000);
        }
        world.showSection(firstRock, Direction.SOUTH);
        for (i = 0; i < 10; ++i) {
            scene.idle(15);
            for (BlockPos stoneBlock : firstRock) {
                world.incrementBlockBreakingProgress(stoneBlock.immutable());
            }
            if (i != 1) continue;
            overlay.showText(100).text("The more blocks a Borehead Bearing attempts to break, the slower it will operate").colored(PonderPalette.RED).pointAt(vector.centerOf(util.grid().at(3, 3, 4))).attachKeyFrame().placeNearTarget();
        }
        scene.idle(7);
        world.setKineticSpeed(positiveKinetics, 0.0f);
        world.setKineticSpeed(negativeKinetics, 0.0f);
        BoreheadBearingScenes.spinRockCutters(scene, select.position(rockCutters[0]).add(select.position(rockCutters[1])), select.position(rockCutters[2]).add(select.position(rockCutters[3])), 0, 0);
        scene.idle(30);
        scene.rotateCameraY(-90.0f);
        scene.idle(1);
        scene.addInstruction((PonderInstruction)new TranslateYSceneInstruction(1.0f, 20, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, Direction.SOUTH, bigBearingLink));
        ElementLink boreCenterLink = world.showIndependentSectionImmediately(bigBoreCenter);
        ElementLink leftBoreLink = world.showIndependentSectionImmediately(leftBore);
        ElementLink rightBoreLink = world.showIndependentSectionImmediately(rightBore);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)singleKineticsLink, (Vec3)new Vec3(0.0, -1.0, 0.0), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)boreCenterLink, (Vec3)new Vec3(0.0, -1.0, 0.0), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)leftBoreLink, (Vec3)new Vec3(0.0, -1.0, 0.0), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)rightBoreLink, (Vec3)new Vec3(0.0, -1.0, 0.0), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(20);
        world.hideIndependentSection(singleBeltsLink, Direction.SOUTH);
        world.hideIndependentSection(singleKineticsLink, Direction.SOUTH);
        world.hideIndependentSection(boreCenterLink, Direction.SOUTH);
        scene.idle(20);
        ElementLink doubleKineticsLink = world.showIndependentSection(doubleKinetics, Direction.NORTH);
        ElementLink doubleBeltLink = world.showIndependentSection(doubleBelts, Direction.NORTH);
        world.moveSection(doubleKineticsLink, vector.of(0.0, -1.0, -2.0), 0);
        world.moveSection(doubleBeltLink, vector.of(0.0, -7.0, -2.0), 0);
        scene.idle(10);
        world.setKineticSpeed(positiveKinetics, 64.0f);
        world.setKineticSpeed(negativeKinetics, -64.0f);
        ChangeBoreheadAndContraptionSpeedInstruction leftBoreInstr = new ChangeBoreheadAndContraptionSpeedInstruction(doubleBorehead[0], (ElementLink<WorldSectionElement>)leftBoreLink, ChangeBoreheadAndContraptionSpeedInstruction.RotationAxis.Z, 64.0f);
        scene.addInstruction((PonderInstruction)leftBoreInstr);
        ChangeBoreheadAndContraptionSpeedInstruction rightBoreInstr = new ChangeBoreheadAndContraptionSpeedInstruction(doubleBorehead[1], (ElementLink<WorldSectionElement>)rightBoreLink, ChangeBoreheadAndContraptionSpeedInstruction.RotationAxis.Z, -64.0f);
        scene.addInstruction((PonderInstruction)rightBoreInstr);
        for (int i3 = 1; i3 <= 8; ++i3) {
            scene.idle(4);
            BoreheadBearingScenes.spinRockCutters(scene, select.position(rockCutters[1]).add(select.position(rockCutters[2])), select.position(rockCutters[0]).add(select.position(rockCutters[3])), -i3, 1000);
        }
        scene.rotateCameraY(90.0f);
        scene.idle(1);
        scene.addInstruction((PonderInstruction)new TranslateYSceneInstruction(-0.5f, 20, SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)doubleBeltLink, (Vec3)new Vec3(0.62, 0.0, 0.18), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)doubleKineticsLink, (Vec3)new Vec3(0.5, 0.0, 0.25), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)leftBoreLink, (Vec3)new Vec3(0.5, 0.0, 0.25), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)rightBoreLink, (Vec3)new Vec3(0.5, 0.0, 0.25), (int)19, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(30);
        ElementLink secondRockLink = world.showIndependentSection(secondRock, Direction.SOUTH);
        world.moveSection(secondRockLink, vector.of(0.0, 0.0, 3.0), 0);
        for (int i4 = 0; i4 < 9; ++i4) {
            scene.idle(5);
            for (BlockPos stoneBlock : secondRock) {
                world.incrementBlockBreakingProgress(stoneBlock.immutable());
            }
            if (i4 != 1) continue;
            overlay.showText(80).text("Using multiple Borehead Bearings can offset this").colored(PonderPalette.GREEN).pointAt(vector.centerOf(util.grid().at(3, 3, 4))).attachKeyFrame().placeNearTarget();
        }
        scene.addInstruction((PonderInstruction)new FadeOutOfSceneInstruction(0, Direction.SOUTH, secondRockLink));
        for (BlockPos blockPos : firstRock) {
            scene.addInstruction((PonderInstruction)new OffsetBreakParticlesInstruction(new AABB(blockPos), Blocks.STONE.defaultBlockState()));
        }
    }

    public static void spinRockCutters(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos positiveWheel, BlockPos negativeWheel, int speed, int duration) {
        BoreheadBearingScenes.spinRockCutters(scene, util.select().position(positiveWheel), util.select().position(negativeWheel), speed, duration);
    }

    public static void spinRockCutters(CreateSceneBuilder scene, Selection positiveWheels, Selection negativeWheels, int speed, int duration) {
        CreateSceneBuilder.WorldInstructions world = scene.world();
        for (BlockPos rockWheel : positiveWheels) {
            world.modifyBlockEntity(rockWheel, RockCuttingWheelBlockEntity.class, be -> {
                be.setAnimatedSpeed(-speed);
                be.setMaxDuration(duration);
            });
        }
        for (BlockPos rockWheel : negativeWheels) {
            world.modifyBlockEntity(rockWheel, RockCuttingWheelBlockEntity.class, be -> {
                be.setAnimatedSpeed(speed);
                be.setMaxDuration(duration);
            });
        }
    }
}
