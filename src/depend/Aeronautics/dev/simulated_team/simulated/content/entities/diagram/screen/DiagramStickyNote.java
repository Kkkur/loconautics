/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.plot.ClientLevelPlot
 *  foundry.veil.api.client.render.VeilLevelPerspectiveRenderer
 *  foundry.veil.api.client.render.framebuffer.AdvancedFbo
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector2d
 *  org.joml.Vector2dc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.ClientLevelPlot;
import dev.simulated_team.simulated.content.entities.diagram.DiagramConfig;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramButton;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramScreen;
import dev.simulated_team.simulated.index.SimGUITextures;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class DiagramStickyNote
extends DiagramButton {
    private static final SimGUITextures NOTE_TEXTURE = SimGUITextures.DIAGRAM_STICKY_NOTE;
    private static final int SUBLEVEL_RENDER_WIDTH_PIXELS = 88;
    private static final int SUBLEVEL_RENDER_HEIGHT_PIXELS = 88;
    private static final int SUBLEVEL_RENDER_X_OFFSET = 8;
    private static final int SUBLEVEL_RENDER_Y_OFFSET = 7;
    public static final int MAX_OFFSET = DiagramStickyNote.NOTE_TEXTURE.width;
    public static final int MIN_OFFSET = 9;
    private static final Vector3d NOTE_LOCAL_CAM_POS = new Vector3d();
    private static final Vector3d NOTE_CAMERA_POS = new Vector3d();
    private static final Matrix4f NOTE_PROJ_MAT = new Matrix4f();
    private static final Quaternionf NOTE_ORIENTATION = new Quaternionf();
    private DiagramScreen parent;
    private float lastOffset = 9.0f;
    private float currentOffset = 9.0f;
    private AdvancedFbo fbo;
    private AdvancedFbo outlineFbo;
    private AdvancedFbo finalFbo;
    private float renderTime = 0.0f;
    private final int renderXStart;

    public DiagramStickyNote(DiagramScreen parent, int diagramX, int diagramY, Component message, Runnable onClick) {
        super(NOTE_TEXTURE, 0, diagramY + 5, message, onClick);
        this.renderXStart = diagramX + SimGUITextures.DIAGRAM.width - DiagramStickyNote.NOTE_TEXTURE.width + 9;
        this.setX(this.renderXStart);
        this.parent = parent;
    }

    public void tick() {
        this.lastOffset = this.currentOffset;
        float target = 9.0f;
        if (this.active) {
            target = MAX_OFFSET - 8;
        }
        this.currentOffset = Mth.lerp((float)0.4f, (float)this.currentOffset, (float)target);
        this.setX((int)((float)this.renderXStart + this.currentOffset));
    }

    private float lerpedOffset(float pt) {
        return Mth.lerp((float)pt, (float)this.lastOffset, (float)this.currentOffset);
    }

    public void activate() {
        if (!this.active) {
            Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.BOOK_PAGE_TURN, (float)1.0f));
            this.active = true;
        }
    }

    public void deactivate() {
        this.active = false;
    }

    public void create(DiagramConfig.NoteConfigs noteConfigs) {
        this.fbo = AdvancedFbo.withSize((int)88, (int)88).addColorTextureBuffer().setDepthTextureBuffer().build(true);
        this.outlineFbo = AdvancedFbo.withSize((int)88, (int)88).addColorTextureBuffer().build(true);
        this.finalFbo = AdvancedFbo.withSize((int)88, (int)88).addColorTextureBuffer().build(true);
        this.active = noteConfigs.isActive();
        this.updateOrientation();
        if (this.active) {
            this.lastOffset = this.currentOffset = (float)(MAX_OFFSET - 8);
        }
        this.visible = true;
    }

    public void free() {
        this.deactivate();
        NOTE_ORIENTATION.set(0.0f, 0.0f, 0.0f, 0.0f);
        if (this.fbo != null) {
            this.fbo.free();
            this.fbo = null;
            this.outlineFbo.free();
            this.outlineFbo = null;
            this.finalFbo.free();
            this.finalFbo = null;
        }
        this.parent = null;
    }

    public void updateCurrentScope(Vector2dc start, Vector2dc end, Vector3dc localPosition, Matrix4fc projMatrix) {
        this.updateOrientation();
        int width = DiagramScreen.DIAGRAM_TEXTURE.width;
        int height = DiagramScreen.DIAGRAM_TEXTURE.height;
        Vector3d startPlotSpace = DiagramScreen.getPlotCoords(start, (Quaternionfc)NOTE_ORIENTATION, localPosition, projMatrix, width, height);
        Vector3d endPlotSpace = DiagramScreen.getPlotCoords(end, (Quaternionfc)NOTE_ORIENTATION, localPosition, projMatrix, width, height);
        this.parent.config.getNoteConfigs().getNoteScope().set(startPlotSpace.x, startPlotSpace.y, startPlotSpace.z, endPlotSpace.x, endPlotSpace.y, endPlotSpace.z);
    }

    public void handleInternalUpdate(Vector2d magnifyingTarget, Vector2d inverseTarget) {
        magnifyingTarget.sub((double)this.getSublevelRenderX(), (double)this.getSublevelRenderY());
        inverseTarget.sub((double)this.getSublevelRenderX(), (double)this.getSublevelRenderY());
        int width = 88;
        int height = 88;
        Vector3d startPlotSpace = DiagramScreen.getPlotCoords((Vector2dc)magnifyingTarget, (Quaternionfc)NOTE_ORIENTATION, (Vector3dc)NOTE_LOCAL_CAM_POS, (Matrix4fc)NOTE_PROJ_MAT, 88, 88);
        Vector3d endPlotSpace = DiagramScreen.getPlotCoords((Vector2dc)inverseTarget, (Quaternionfc)NOTE_ORIENTATION, (Vector3dc)NOTE_LOCAL_CAM_POS, (Matrix4fc)NOTE_PROJ_MAT, 88, 88);
        this.parent.config.getNoteConfigs().getNoteScope().set(startPlotSpace.x, startPlotSpace.y, startPlotSpace.z, endPlotSpace.x, endPlotSpace.y, endPlotSpace.z);
    }

    private void updateOrientation() {
        this.renderTime = 100.0f;
        NOTE_ORIENTATION.identity().rotateY((float)Math.toRadians(this.parent.config.getNoteConfigs().getNoteYaw())).rotateX((float)Math.toRadians(this.parent.config.getNoteConfigs().getNotePitch()));
    }

    public boolean contains(double x, double y) {
        if (!this.active) {
            return false;
        }
        return (x -= (double)this.getSublevelRenderX()) > 0.0 && x < 88.0 && (y -= (double)this.getSublevelRenderY()) > 0.0 && y < 88.0;
    }

    public Vector2d clamp(Vector2d dest) {
        float minX = this.getSublevelRenderX();
        float minY = this.getSublevelRenderY();
        dest.max((Vector2dc)new Vector2d((double)minX, (double)minY));
        dest.min((Vector2dc)new Vector2d((double)(minX + 88.0f), (double)(minY + 88.0f)));
        return dest;
    }

    private float getSublevelRenderX() {
        return (float)this.renderXStart + this.currentOffset + 8.0f;
    }

    private float getSublevelRenderY() {
        return this.getY() + 7;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack ps = guiGraphics.pose();
        ps.pushPose();
        float currentX = (float)this.renderXStart + this.lerpedOffset(partialTicks);
        int currentY = this.getY();
        ps.translate(currentX, (float)currentY, 0.0f);
        SimGUITextures.DIAGRAM_STICKY_NOTE.render(guiGraphics, 0, 0);
        if (this.active) {
            ps.pushPose();
            ps.translate(8.0f, 7.0f, 0.0f);
            if (!VeilLevelPerspectiveRenderer.isRenderingPerspective() && this.fbo != null) {
                this.populateFBO(partialTicks);
                DiagramScreen.renderFBO(guiGraphics, this.finalFbo, 88, 88);
            }
            this.parent.renderArrows(guiGraphics, mouseX, mouseY, (int)currentX + 8, currentY + 7, (Quaternionfc)NOTE_ORIENTATION, (Vector3dc)NOTE_LOCAL_CAM_POS, (Matrix4fc)NOTE_PROJ_MAT, 88, 88);
            MultiBufferSource.BufferSource bufferSource = guiGraphics.bufferSource();
            bufferSource.endBatch();
            this.renderCustomCOM(guiGraphics, ps);
            ps.popPose();
        }
        ps.popPose();
    }

    public void populateFBO(float partialTicks) {
        if (!(this.renderTime >= 1.6666666f)) {
            this.renderTime += Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
            return;
        }
        this.renderTime = 0.0f;
        float zNear = 0.1f;
        ClientLevelPlot plot = this.parent.subLevel.getPlot();
        BoundingBox3ic plotBounds = plot.getBoundingBox();
        float maxDistance = Math.max(Math.max(plotBounds.maxX() - plotBounds.minX(), plotBounds.maxY() - plotBounds.minY()), plotBounds.maxZ() - plotBounds.minZ()) + 1;
        BoundingBox3i scopeBounds = new BoundingBox3i(this.parent.config.getNoteConfigs().getNoteScope());
        float radius = Math.max(Math.max(scopeBounds.maxX() - scopeBounds.minX(), scopeBounds.maxY() - scopeBounds.minY()), scopeBounds.maxZ() - scopeBounds.minZ()) + 1;
        radius *= 0.55f;
        radius = Math.max(radius, 1.0f);
        Vector3d plotBoundsCenter = new Vector3d((double)(scopeBounds.minX() + scopeBounds.maxX() + 1) / 2.0, (double)(scopeBounds.minY() + scopeBounds.maxY() + 1) / 2.0, (double)(scopeBounds.minZ() + scopeBounds.maxZ() + 1) / 2.0);
        float aspect = 1.0f;
        NOTE_PROJ_MAT.identity().ortho(-radius * 1.0f, radius * 1.0f, -radius, radius, 0.1f, maxDistance * 2.0f);
        NOTE_LOCAL_CAM_POS.set((Vector3dc)plotBoundsCenter.add((Vector3dc)NOTE_ORIENTATION.transform(new Vector3d(0.0, 0.0, (double)maxDistance))));
        Pose3dc renderPose = this.parent.subLevel.renderPose(partialTicks);
        renderPose.transformPosition(NOTE_CAMERA_POS.set((Vector3dc)NOTE_LOCAL_CAM_POS));
        DiagramScreen.draw((SubLevel)this.parent.subLevel, partialTicks, NOTE_ORIENTATION, NOTE_PROJ_MAT, NOTE_CAMERA_POS, 88.0f, 88.0f, this.fbo, this.outlineFbo, this.finalFbo, 0.75f, 1.15f, 7235661, 5854270);
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    private void renderCustomCOM(GuiGraphics guiGraphics, PoseStack stack) {
        if (this.parent.config.displayCenterOfMass()) {
            stack.pushPose();
            Vector3d centerOfMass = new Vector3d((Vector3dc)this.parent.subLevel.logicalPose().rotationPoint());
            Vector2d screenCoords = DiagramScreen.getScreenCoords(centerOfMass, (Quaternionfc)NOTE_ORIENTATION, (Vector3dc)NOTE_LOCAL_CAM_POS, (Matrix4fc)NOTE_PROJ_MAT, 88, 88);
            SimGUITextures tex = SimGUITextures.DIAGRAM_ICON_COM_TINY;
            double comOffsetX = screenCoords.x - 8.0;
            double comOffsetY = screenCoords.y - 8.0;
            if (comOffsetY > 0.0 && comOffsetX > 0.0 && comOffsetY < 88.0 && comOffsetX < 88.0) {
                stack.translate(comOffsetX, comOffsetY, 0.0);
                guiGraphics.blit(tex.location, 0, 0, 5, (float)tex.startX, (float)tex.startY, tex.width, tex.height, tex.texWidth, tex.texHeight);
            } else {
                float centerX = 44.0f;
                float centerY = 44.0f;
                Vector2d target = new Vector2d(screenCoords.x() - 44.0, screenCoords.y - 44.0).normalize();
                ((PoseTransformStack)TransformStack.of((PoseStack)stack).translate(44.0f, 44.0f, 0.0f).rotate((float)Math.atan2(target.x, -target.y), Direction.Axis.Z)).translate(-8.0f, -8.0f, 0.0f).translate(0.0f, -40.0f, 0.0f);
                tex = SimGUITextures.DIAGRAM_ICON_COM_ARROW;
                guiGraphics.blit(tex.location, 0, 0, 5, (float)tex.startX, (float)tex.startY, tex.width, tex.height, tex.texWidth, tex.texHeight);
            }
            stack.popPose();
        }
    }
}
