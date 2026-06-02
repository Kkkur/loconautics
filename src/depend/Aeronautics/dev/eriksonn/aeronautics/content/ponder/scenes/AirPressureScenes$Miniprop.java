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
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerParticleSpawningInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.TickingStoppingInstruction;
import dev.eriksonn.aeronautics.content.ponder.scenes.AirPressureScenes;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public static class AirPressureScenes.Miniprop
implements AirPressureScenes.PressureItem {
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
