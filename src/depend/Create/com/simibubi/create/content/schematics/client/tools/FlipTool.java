/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.outliner.AABBOutline
 *  net.createmod.catnip.render.BindableTexture
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.schematics.client.tools.PlacementToolBase;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.AABBOutline;
import net.createmod.catnip.render.BindableTexture;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlipTool
extends PlacementToolBase {
    private AABBOutline outline = new AABBOutline(new AABB(BlockPos.ZERO));

    @Override
    public void init() {
        super.init();
        this.renderSelectedFace = false;
    }

    @Override
    public boolean handleRightClick() {
        this.mirror();
        return true;
    }

    @Override
    public boolean handleMouseWheel(double delta) {
        this.mirror();
        return true;
    }

    @Override
    public void updateSelection() {
        super.updateSelection();
    }

    private void mirror() {
        if (this.schematicSelected && this.selectedFace.getAxis().isHorizontal()) {
            this.schematicHandler.getTransformation().flip(this.selectedFace.getAxis());
            this.schematicHandler.markDirty();
        }
    }

    @Override
    public void renderOnSchematic(PoseStack ms, SuperRenderTypeBuffer buffer) {
        if (!this.schematicSelected || !this.selectedFace.getAxis().isHorizontal()) {
            super.renderOnSchematic(ms, buffer);
            return;
        }
        Direction facing = this.selectedFace.getClockWise();
        AABB bounds = this.schematicHandler.getBounds();
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)facing.getAxis()).getNormal());
        Vec3 boundsSize = new Vec3(bounds.getXsize(), bounds.getYsize(), bounds.getZsize());
        Vec3 vec = boundsSize.multiply(directionVec);
        bounds = bounds.contract(vec.x, vec.y, vec.z).inflate(1.0 - directionVec.x, 1.0 - directionVec.y, 1.0 - directionVec.z);
        bounds = bounds.move(directionVec.scale(0.5).multiply(boundsSize));
        this.outline.setBounds(bounds);
        AllSpecialTextures tex = AllSpecialTextures.CHECKERED;
        this.outline.getParams().lineWidth(0.0625f).disableLineNormals().colored(0xDDDDDD).withFaceTextures((BindableTexture)tex, (BindableTexture)tex);
        this.outline.render(ms, buffer, Vec3.ZERO, AnimationTickHolder.getPartialTicks());
        super.renderOnSchematic(ms, buffer);
    }
}
