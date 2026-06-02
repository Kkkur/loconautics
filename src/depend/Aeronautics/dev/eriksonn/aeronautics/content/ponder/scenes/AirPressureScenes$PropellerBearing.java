/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.SelectionUtil
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.scenes.AirPressureScenes;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public static class AirPressureScenes.PropellerBearing
implements AirPressureScenes.PressureItem {
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
