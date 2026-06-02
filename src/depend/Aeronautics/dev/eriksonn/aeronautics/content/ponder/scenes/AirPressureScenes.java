/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$SpecialInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  dev.simulated_team.simulated.ponder.SmoothMovementUtils
 *  dev.simulated_team.simulated.ponder.instructions.AltitudeSensorVisualHeightInstruction$Linear
 *  dev.simulated_team.simulated.ponder.instructions.ChaseAABBWithLinkInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomMoveBaseShadowInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomToggleBaseShadowInstruction
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
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerParticleSpawningInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.RedstoneSignalInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.TickingStoppingInstruction;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.AltitudeSensorVisualHeightInstruction;
import dev.simulated_team.simulated.ponder.instructions.ChaseAABBWithLinkInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomMoveBaseShadowInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomToggleBaseShadowInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.util.function.UnaryOperator;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AirPressureScenes {
    public static void airPressure(SceneBuilder builder, SceneBuildingUtil util, PressureItem pressureItem) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        CreateSceneBuilder.SpecialInstructions special = scene.special();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        scene.title(pressureItem.getSceneId(), pressureItem.getSceneTitle());
        scene.configureBasePlate(2, 2, 5);
        scene.setSceneOffsetY((float)pressureItem.getSceneOffset() - 1.0f);
        scene.scaleSceneView(0.9f);
        Selection plate = select.fromTo(3, 0, 3, 5, 0, 5);
        Selection groundSelection = select.fromTo(2, 0, 2, 6, 0, 6).substract(plate);
        ElementLink ground = world.showIndependentSection(groundSelection, Direction.UP);
        world.showSection(plate, Direction.UP);
        scene.idle(10);
        Vec3 pointingPos = pressureItem.getPointingPos();
        pressureItem.setup(scene, util);
        scene.idle(10);
        overlay.showText(60).pointAt(pointingPos).attachKeyFrame().placeNearTarget().text(pressureItem.getItemName() + " are affected by Air Pressure");
        scene.idle(50);
        for (int i = 0; i < 8; ++i) {
            double y = (Math.exp((float)i / 3.0f) - 1.0) * 1.3 + 1.0;
            AABB outline = new AABB(2.0, y, 2.0, 7.0, y, 7.0);
            scene.addInstruction((PonderInstruction)new ChaseAABBWithLinkInstruction(ground, PonderPalette.INPUT, (Object)outline, outline, 120 - i * 4, 4.0));
            scene.idle(2);
        }
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground, (Vec3)new Vec3(0.0, -8.0, 0.0), (int)60, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
        scene.addInstruction((PonderInstruction)CustomMoveBaseShadowInstruction.delta((Vec3)new Vec3(0.0, -4.0, 0.0), (int)30, (UnaryOperator)SmoothMovementUtils.quadraticRise()));
        scene.idle(15);
        world.hideIndependentSection(ground, Direction.DOWN);
        scene.addInstruction((PonderInstruction)new CustomToggleBaseShadowInstruction());
        scene.idle(15);
        ElementLink cloudsSection = world.showIndependentSection(select.fromTo(1, 0, 7, 6, 2, 9).add(select.fromTo(7, 0, 0, 9, 2, 9)), Direction.DOWN);
        world.moveSection(cloudsSection, new Vec3(0.0, 4.0, 0.0), 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)cloudsSection, (Vec3)new Vec3(0.0, -4.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(50);
        overlay.showText(60).pointAt(pointingPos).attachKeyFrame().placeNearTarget().text(pressureItem.getForceName() + " weakens as Altitude increases");
        scene.idle(50);
        if (pressureItem instanceof HoveringPressureItem) {
            HoveringPressureItem hoverItem = (HoveringPressureItem)pressureItem;
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)cloudsSection, (Vec3)new Vec3(0.0, 1.0, 0.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
            scene.idle(40);
            overlay.showText(75).pointAt(pointingPos).attachKeyFrame().placeNearTarget().text("Simulated Contraptions will settle when " + pressureItem.getForceName() + " and Weight are in balance");
            scene.idle(90);
            overlay.showText(70).pointAt(pointingPos).attachKeyFrame().placeNearTarget().text("Changing " + pressureItem.getForceName() + " thus allows changing Altitude");
            scene.idle(20);
            hoverItem.increasePower(scene, util);
            scene.idle(10);
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)cloudsSection, (Vec3)new Vec3(0.0, -1.0, 0.0), (int)40, (FloatUnaryOperator)SmoothMovementUtils.cubicSmoothing()));
            scene.idle(80);
        } else {
            scene.idle(40);
        }
        BlockPos sensorPos = pressureItem.getSensorPos();
        Vec3 sensorPointing = sensorPos.getCenter();
        world.showSection(select.position(sensorPos), Direction.DOWN);
        scene.addInstruction((PonderInstruction)new AltitudeSensorVisualHeightInstruction.Linear(sensorPos, 0, 0.5f, 0.5f, f -> f));
        scene.idle(10);
        overlay.showText(70).pointAt(sensorPointing).attachKeyFrame().placeNearTarget().text("The precise Air Pressure can be read from an Altitude Sensor");
        scene.idle(80);
        pressureItem.decreasePower(scene, util);
        scene.idle(10);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)cloudsSection, (Vec3)new Vec3(0.0, 4.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        scene.addInstruction((PonderInstruction)new AltitudeSensorVisualHeightInstruction.Linear(sensorPos, 60, 0.5f, -0.5f, SmoothMovementUtils.quadraticRiseInOut()));
        scene.idle(15);
        world.hideIndependentSection(cloudsSection, Direction.UP);
        scene.idle(15);
        ground = world.showIndependentSection(groundSelection, Direction.UP);
        world.moveSection(ground, new Vec3(0.0, -4.0, 0.0), 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground, (Vec3)new Vec3(0.0, 4.0, 0.0), (int)30, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)new CustomToggleBaseShadowInstruction());
        scene.addInstruction((PonderInstruction)CustomMoveBaseShadowInstruction.delta((Vec3)new Vec3(0.0, 4.0, 0.0), (int)30, (UnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(5);
    }

    public static interface PressureItem {
        default public int getSceneOffset() {
            return 0;
        }

        public String getSceneId();

        public String getSceneTitle();

        public void setup(CreateSceneBuilder var1, SceneBuildingUtil var2);

        public String getItemName();

        public String getForceName();

        public Vec3 getPointingPos();

        public BlockPos getSensorPos();

        public void decreasePower(CreateSceneBuilder var1, SceneBuildingUtil var2);
    }

    public static interface HoveringPressureItem
    extends PressureItem {
        public void increasePower(CreateSceneBuilder var1, SceneBuildingUtil var2);
    }

    public static class Vent
    extends Burner {
        @Override
        public String getSceneId() {
            return "vent_pressure";
        }

        @Override
        public String getSceneTitle() {
            return "Effect of Air Pressure on Steam Vents";
        }

        @Override
        public String getItemName() {
            return "Steam Vents";
        }
    }

    public static class Burner
    implements HoveringPressureItem {
        @Override
        public int getSceneOffset() {
            return -1;
        }

        @Override
        public String getSceneId() {
            return "burner_pressure";
        }

        @Override
        public String getSceneTitle() {
            return "Effect of Air Pressure on Hot Air Burners";
        }

        @Override
        public void setup(CreateSceneBuilder scene, SceneBuildingUtil util) {
            CreateSceneBuilder.WorldInstructions world = scene.world();
            SelectionUtil select = util.select();
            world.showSection(select.position(4, 1, 4), Direction.DOWN);
            scene.idle(5);
            world.showSection(select.position(3, 1, 4), Direction.DOWN);
            scene.idle(5);
            for (int i = 1; i < 4; ++i) {
                world.showSection(select.position(3, i, 3).add(select.position(3, i, 5)).add(select.position(5, i, 5)).add(select.position(5, i, 3)), Direction.DOWN);
                scene.idle(2);
            }
            world.showSection(select.fromTo(2, 4, 2, 6, 7, 6), Direction.DOWN);
            scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(4, 1, 4, 3, 1, 4), 8));
        }

        @Override
        public String getItemName() {
            return "Hot Air Burners";
        }

        @Override
        public String getForceName() {
            return "Lift";
        }

        @Override
        public Vec3 getPointingPos() {
            return new Vec3(4.5, 1.5, 4.5);
        }

        @Override
        public BlockPos getSensorPos() {
            return new BlockPos(4, 1, 3);
        }

        @Override
        public void increasePower(CreateSceneBuilder scene, SceneBuildingUtil util) {
            scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(4, 1, 4, 3, 1, 4), 12));
        }

        @Override
        public void decreasePower(CreateSceneBuilder scene, SceneBuildingUtil util) {
            scene.addInstruction((PonderInstruction)new RedstoneSignalInstruction(util.select().fromTo(4, 1, 4, 3, 1, 4), 0));
        }
    }

    public static class Miniprop
    implements PressureItem {
        PropellerParticleSpawningInstruction particles1;
        PropellerParticleSpawningInstruction particles2;

        @Override
        public String getSceneId() {
            return "miniprop_pressure";
        }

        @Override
        public String getSceneTitle() {
            return "Effect of Air Pressure on Propellers";
        }

        @Override
        public void setup(CreateSceneBuilder scene, SceneBuildingUtil util) {
            CreateSceneBuilder.WorldInstructions world = scene.world();
            SelectionUtil select = util.select();
            world.showSection(select.position(4, 1, 4), Direction.DOWN);
            scene.idle(5);
            world.showSection(select.position(3, 1, 5), Direction.DOWN);
            world.showSection(select.position(5, 1, 3), Direction.DOWN);
            scene.idle(5);
            world.showSection(select.position(3, 2, 5), Direction.DOWN);
            world.showSection(select.position(5, 2, 3), Direction.DOWN);
            this.particles1 = new PropellerParticleSpawningInstruction(null, new BlockPos(3, 2, 5), Direction.DOWN, 1000, 1.0f, 4.0f, 1.0f);
            this.particles2 = new PropellerParticleSpawningInstruction(null, new BlockPos(5, 2, 3), Direction.DOWN, 1000, 1.0f, 4.0f, 1.0f);
            scene.addInstruction((PonderInstruction)this.particles1);
            scene.addInstruction((PonderInstruction)this.particles2);
        }

        @Override
        public String getItemName() {
            return "Propellers";
        }

        @Override
        public String getForceName() {
            return "Thrust";
        }

        @Override
        public Vec3 getPointingPos() {
            return new Vec3(3.5, 2.5, 5.5);
        }

        @Override
        public BlockPos getSensorPos() {
            return new BlockPos(3, 1, 3);
        }

        @Override
        public void decreasePower(CreateSceneBuilder scene, SceneBuildingUtil util) {
            scene.world().multiplyKineticSpeed(util.select().everywhere(), 0.0f);
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(this.particles1));
            scene.addInstruction((PonderInstruction)new TickingStoppingInstruction(this.particles2));
        }
    }

    public static class GyroBearing
    extends PropellerBearing
    implements HoveringPressureItem {
        @Override
        public String getSceneId() {
            return "gyro_pressure";
        }

        @Override
        public String getSceneTitle() {
            return "Effect of Air Pressure on Gyroscopic Propeller Bearings";
        }

        @Override
        public String getItemName() {
            return "Gyroscopic Propeller Bearings";
        }

        @Override
        public void increasePower(CreateSceneBuilder scene, SceneBuildingUtil util) {
            scene.world().multiplyKineticSpeed(util.select().everywhere(), 1.5f);
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetRotationRate(this.propellerRotate, 48.0f));
        }
    }

    public static class PropellerBearing
    implements PressureItem {
        PropellerRotateInstruction propellerRotate;

        @Override
        public String getSceneId() {
            return "propeller_pressure";
        }

        @Override
        public String getSceneTitle() {
            return "Effect of Air Pressure on Propeller Bearings";
        }

        @Override
        public void setup(CreateSceneBuilder scene, SceneBuildingUtil util) {
            CreateSceneBuilder.WorldInstructions world = scene.world();
            SelectionUtil select = util.select();
            world.showSection(select.position(4, 1, 4).add(select.position(5, 1, 5)), Direction.DOWN);
            scene.idle(5);
            world.showSection(select.fromTo(5, 1, 4, 5, 2, 4), Direction.DOWN);
            world.showSection(select.fromTo(3, 1, 4, 3, 2, 4), Direction.DOWN);
            scene.idle(5);
            BlockPos bearingPos = new BlockPos(4, 2, 4);
            world.showSection(select.position(bearingPos), Direction.DOWN);
            scene.idle(5);
            ElementLink propeller = world.showIndependentSection(select.fromTo(2, 3, 2, 6, 3, 6), Direction.DOWN);
            this.propellerRotate = new PropellerRotateInstruction(bearingPos, (ElementLink<WorldSectionElement>)propeller, Direction.UP, 32.0f, 8.0f);
            scene.addInstruction((PonderInstruction)this.propellerRotate);
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(this.propellerRotate, null, 2.0f, -6.0f, 2.0f, false));
        }

        @Override
        public String getItemName() {
            return "Propeller Bearings";
        }

        @Override
        public String getForceName() {
            return "Thrust";
        }

        @Override
        public Vec3 getPointingPos() {
            return new Vec3(4.5, 2.5, 4.5);
        }

        @Override
        public void decreasePower(CreateSceneBuilder scene, SceneBuildingUtil util) {
            scene.world().multiplyKineticSpeed(util.select().everywhere(), 0.0f);
            scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(this.propellerRotate, 30.0f));
        }

        @Override
        public BlockPos getSensorPos() {
            return new BlockPos(3, 1, 3);
        }
    }
}
