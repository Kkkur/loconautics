/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.AABBOutline
 *  net.createmod.catnip.render.BindableTexture
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.schematics.client.SchematicHandler;
import com.simibubi.create.content.schematics.client.SchematicTransformation;
import com.simibubi.create.content.schematics.client.tools.ISchematicTool;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.AABBOutline;
import net.createmod.catnip.render.BindableTexture;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class SchematicToolBase
implements ISchematicTool {
    protected SchematicHandler schematicHandler;
    protected BlockPos selectedPos;
    protected Vec3 chasingSelectedPos;
    protected Vec3 lastChasingSelectedPos;
    protected boolean selectIgnoreBlocks;
    protected int selectionRange;
    protected boolean schematicSelected;
    protected boolean renderSelectedFace;
    protected Direction selectedFace;

    @Override
    public void init() {
        this.schematicHandler = CreateClient.SCHEMATIC_HANDLER;
        this.selectedPos = null;
        this.selectedFace = null;
        this.schematicSelected = false;
        this.chasingSelectedPos = Vec3.ZERO;
        this.lastChasingSelectedPos = Vec3.ZERO;
    }

    @Override
    public void updateSelection() {
        this.updateTargetPos();
        if (this.selectedPos == null) {
            return;
        }
        this.lastChasingSelectedPos = this.chasingSelectedPos;
        Vec3 target = Vec3.atLowerCornerOf((Vec3i)this.selectedPos);
        if (target.distanceTo(this.chasingSelectedPos) < 0.001953125) {
            this.chasingSelectedPos = target;
            return;
        }
        this.chasingSelectedPos = this.chasingSelectedPos.add(target.subtract(this.chasingSelectedPos).scale(0.5));
    }

    public void updateTargetPos() {
        boolean snap;
        LocalPlayer player = Minecraft.getInstance().player;
        if (this.schematicHandler.isDeployed()) {
            Vec3 end;
            SchematicTransformation transformation = this.schematicHandler.getTransformation();
            AABB localBounds = this.schematicHandler.getBounds();
            Vec3 traceOrigin = player.getEyePosition();
            Vec3 start = transformation.toLocalSpace(traceOrigin);
            RaycastHelper.PredicateTraceResult result = RaycastHelper.rayTraceUntil(start, end = transformation.toLocalSpace(RaycastHelper.getTraceTarget((Player)player, 70.0, traceOrigin)), pos -> localBounds.contains(VecHelper.getCenterOf((Vec3i)pos)));
            this.schematicSelected = !result.missed();
            this.selectedFace = this.schematicSelected ? result.getFacing() : null;
        }
        boolean bl = snap = this.selectedPos == null;
        if (this.selectIgnoreBlocks) {
            float pt = AnimationTickHolder.getPartialTicks();
            this.selectedPos = BlockPos.containing((Position)player.getEyePosition(pt).add(player.getLookAngle().scale((double)this.selectionRange)));
            if (snap) {
                this.lastChasingSelectedPos = this.chasingSelectedPos = Vec3.atLowerCornerOf((Vec3i)this.selectedPos);
            }
            return;
        }
        this.selectedPos = null;
        BlockHitResult trace = RaycastHelper.rayTraceRange(player.level(), (Player)player, 75.0);
        if (trace == null || trace.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos hit = BlockPos.containing((Position)trace.getLocation());
        boolean replaceable = player.level().getBlockState(hit).canBeReplaced();
        if (trace.getDirection().getAxis().isVertical() && !replaceable) {
            hit = hit.relative(trace.getDirection());
        }
        this.selectedPos = hit;
        if (snap) {
            this.lastChasingSelectedPos = this.chasingSelectedPos = Vec3.atLowerCornerOf((Vec3i)this.selectedPos);
        }
    }

    @Override
    public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
    }

    @Override
    public void renderOverlay(Gui gui, GuiGraphics graphics, float partialTicks, int width, int height) {
    }

    @Override
    public void renderOnSchematic(PoseStack ms, SuperRenderTypeBuffer buffer) {
        if (!this.schematicHandler.isDeployed()) {
            return;
        }
        ms.pushPose();
        AABBOutline outline = this.schematicHandler.getOutline();
        if (this.renderSelectedFace) {
            outline.getParams().highlightFace(this.selectedFace).withFaceTextures((BindableTexture)AllSpecialTextures.CHECKERED, (BindableTexture)(AllKeys.ctrlDown() ? AllSpecialTextures.HIGHLIGHT_CHECKERED : AllSpecialTextures.CHECKERED));
        }
        outline.getParams().colored(6850245).withFaceTexture((BindableTexture)AllSpecialTextures.CHECKERED).lineWidth(0.0625f);
        outline.render(ms, buffer, Vec3.ZERO, AnimationTickHolder.getPartialTicks());
        outline.getParams().clearTextures();
        ms.popPose();
    }
}
