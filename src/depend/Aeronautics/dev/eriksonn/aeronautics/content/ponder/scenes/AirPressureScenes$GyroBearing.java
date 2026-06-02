/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.scenes.AirPressureScenes;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.PonderInstruction;

public static class AirPressureScenes.GyroBearing
extends AirPressureScenes.PropellerBearing
implements AirPressureScenes.HoveringPressureItem {
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
