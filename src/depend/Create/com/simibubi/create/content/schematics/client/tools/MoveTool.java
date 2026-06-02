/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client.tools;

import com.simibubi.create.content.schematics.client.SchematicTransformation;
import com.simibubi.create.content.schematics.client.tools.PlacementToolBase;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class MoveTool
extends PlacementToolBase {
    @Override
    public void init() {
        super.init();
        this.renderSelectedFace = true;
    }

    @Override
    public void updateSelection() {
        super.updateSelection();
    }

    @Override
    public boolean handleMouseWheel(double delta) {
        if (!this.schematicSelected || !this.selectedFace.getAxis().isHorizontal()) {
            return true;
        }
        SchematicTransformation transformation = this.schematicHandler.getTransformation();
        Vec3 vec = Vec3.atLowerCornerOf((Vec3i)this.selectedFace.getNormal()).scale(-Math.signum(delta));
        vec = vec.multiply((double)transformation.getMirrorModifier(Direction.Axis.X), 1.0, (double)transformation.getMirrorModifier(Direction.Axis.Z));
        vec = VecHelper.rotate((Vec3)vec, (double)transformation.getRotationTarget(), (Direction.Axis)Direction.Axis.Y);
        transformation.move((int)vec.x, 0, (int)vec.z);
        this.schematicHandler.markDirty();
        return true;
    }
}
