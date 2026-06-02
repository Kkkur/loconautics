/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.SelectionUtil
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.content.ponder.instructions.RedstoneSignalInstruction;
import dev.eriksonn.aeronautics.content.ponder.scenes.AirPressureScenes;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public static class AirPressureScenes.Burner
implements AirPressureScenes.HoveringPressureItem {
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
