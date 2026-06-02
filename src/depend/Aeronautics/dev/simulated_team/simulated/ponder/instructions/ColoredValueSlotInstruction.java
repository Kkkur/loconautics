/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.ponder.instructions.OBBOutlineInstruction;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ColoredValueSlotInstruction
extends PonderInstruction {
    public ColoredValueSlotInstruction(SceneBuilder scene, Vec3 location, Direction side, Vec3 rotation, PonderPalette color, int duration) {
        Vec3 vec = location.add(Vec3.atLowerCornerOf((Vec3i)side.getNormal()).scale(-0.0234375));
        Vec3 expands = VecHelper.axisAlingedPlaneOf((Direction)side).scale(0.0859375);
        AABB point = new AABB(vec, vec);
        AABB expanded = point.inflate(expands.x, expands.y, expands.z);
        scene.addInstruction((PonderInstruction)new OBBOutlineInstruction(expanded, rotation, false, color, expanded.toString(), duration));
    }

    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
    }
}
