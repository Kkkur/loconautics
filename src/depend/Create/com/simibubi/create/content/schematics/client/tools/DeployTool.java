/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.outliner.AABBOutline
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.schematics.client.SchematicTransformation;
import com.simibubi.create.content.schematics.client.tools.PlacementToolBase;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.AABBOutline;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DeployTool
extends PlacementToolBase {
    @Override
    public void init() {
        super.init();
        this.selectionRange = -1;
    }

    @Override
    public void updateSelection() {
        if (this.schematicHandler.isActive() && this.selectionRange == -1) {
            this.selectionRange = (int)(this.schematicHandler.getBounds().getCenter().length() / 2.0);
            this.selectionRange = Mth.clamp((int)this.selectionRange, (int)1, (int)100);
        }
        this.selectIgnoreBlocks = AllKeys.ACTIVATE_TOOL.isPressed();
        super.updateSelection();
    }

    @Override
    public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        super.renderTool(ms, buffer, camera);
        if (this.selectedPos == null) {
            return;
        }
        ms.pushPose();
        float pt = AnimationTickHolder.getPartialTicks();
        double x = Mth.lerp((double)pt, (double)this.lastChasingSelectedPos.x, (double)this.chasingSelectedPos.x);
        double y = Mth.lerp((double)pt, (double)this.lastChasingSelectedPos.y, (double)this.chasingSelectedPos.y);
        double z = Mth.lerp((double)pt, (double)this.lastChasingSelectedPos.z, (double)this.chasingSelectedPos.z);
        SchematicTransformation transformation = this.schematicHandler.getTransformation();
        AABB bounds = this.schematicHandler.getBounds();
        Vec3 center = bounds.getCenter();
        Vec3 rotationOffset = transformation.getRotationOffset(true);
        int centerX = (int)center.x;
        int centerZ = (int)center.z;
        double xOrigin = bounds.getXsize() / 2.0;
        double zOrigin = bounds.getZsize() / 2.0;
        Vec3 origin = new Vec3(xOrigin, 0.0, zOrigin);
        ms.translate(x - (double)centerX - camera.x, y - camera.y, z - (double)centerZ - camera.z);
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(origin)).translate(rotationOffset)).rotateYDegrees(transformation.getCurrentRotation())).translateBack(rotationOffset)).translateBack(origin);
        AABBOutline outline = this.schematicHandler.getOutline();
        outline.render(ms, buffer, Vec3.ZERO, pt);
        outline.getParams().clearTextures();
        ms.popPose();
    }

    @Override
    public boolean handleMouseWheel(double delta) {
        if (!this.selectIgnoreBlocks) {
            return super.handleMouseWheel(delta);
        }
        this.selectionRange = (int)((double)this.selectionRange + delta);
        this.selectionRange = Mth.clamp((int)this.selectionRange, (int)1, (int)100);
        return true;
    }

    @Override
    public boolean handleRightClick() {
        if (this.selectedPos == null) {
            return super.handleRightClick();
        }
        Vec3 center = this.schematicHandler.getBounds().getCenter();
        BlockPos target = this.selectedPos.offset(-((int)center.x), 0, -((int)center.z));
        ItemStack item = this.schematicHandler.getActiveSchematicItem();
        if (item != null) {
            item.set(AllDataComponents.SCHEMATIC_DEPLOYED, (Object)true);
            item.set(AllDataComponents.SCHEMATIC_ANCHOR, (Object)target);
            this.schematicHandler.getTransformation().startAt(target);
        }
        this.schematicHandler.getTransformation().moveTo(target);
        this.schematicHandler.markDirty();
        this.schematicHandler.deploy();
        return true;
    }
}
