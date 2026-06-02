/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.simibubi.create.foundation.gui.RemovedGuiUtils
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup$PointForce
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.plot.LevelPlot
 *  foundry.veil.api.client.render.VeilLevelPerspectiveRenderer
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.framebuffer.AdvancedFbo
 *  foundry.veil.api.client.render.post.PostPipeline
 *  foundry.veil.api.client.render.post.PostPipeline$Context
 *  foundry.veil.api.client.render.post.PostProcessingManager
 *  foundry.veil.api.network.VeilPacketManager
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.lang.LangBuilder
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.Registry
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector2d
 *  org.joml.Vector2dc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.lwjgl.opengl.GL11
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.diagram.DiagramConfig;
import dev.simulated_team.simulated.content.entities.diagram.DiagramEntity;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramButton;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramForceGroupToggle;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramStickyNote;
import dev.simulated_team.simulated.content.entities.diagram.screen.ForceClusterFinder;
import dev.simulated_team.simulated.content.entities.diagram.screen.Greeble;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.index.SimResourceManagers;
import dev.simulated_team.simulated.network.packets.contraption_diagram.DiagramDataPacket;
import dev.simulated_team.simulated.network.packets.contraption_diagram.DiagramSaveConfigPacket;
import dev.simulated_team.simulated.network.packets.contraption_diagram.RequestDiagramDataPacket;
import dev.simulated_team.simulated.util.SimpleSubLevelGroupRenderer;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class DiagramScreen
extends AbstractSimiScreen {
    public static int UPDATE_REQUEST_INTERVAL = 10;
    public static final Color TEXT_COLOR = new Color(79, 82, 87);
    public static final Color BUTTON_COLOR = new Color(109, 113, 119);
    public static final Color DULL_BUTTON_COLOR = new Color(181, 177, 168);
    public static final Color BG_COLOR = new Color(247, 240, 221);
    private static final int TOOLTIP_LABEL_COLOR = -4025475;
    private static final int MIN_ARROW_SIZE_PX = 6;
    public static final float PAPER_SLIDE_SPEED = 0.4f;
    public static final float TAB_SLIDE_SPEED = 0.1f;
    public static final int MAX_PAPER_OFFSET = SimGUITextures.DIAGRAM_PAPER.width + 3;
    public static final int MIN_PAPER_OFFSET = 10;
    private static final Vector3d LOCAL_CAMERA_POSITION = new Vector3d();
    private static final Vector3d CAMERA_POSITION = new Vector3d();
    private static final Matrix4f PROJECTION_MAT = new Matrix4f();
    public static final Quaternionf LOCAL_ORIENTATION = new Quaternionf();
    private static final Vector2d MAGNIFYING_CENTER = new Vector2d();
    private static final Vector2d MAGNIFYING_MAX = new Vector2d();
    private static final Vector2d MAGNIFYING_MIN = new Vector2d();
    private static final int MIN_MAGNIFICATION_PIXELS = 3;
    public static final SimGUITextures DIAGRAM_TEXTURE = SimGUITextures.DIAGRAM;
    public static final float FPS = 12.0f;
    private final DiagramEntity diagram;
    public final ClientSubLevel subLevel;
    protected DiagramConfig config;
    private boolean configDirty = false;
    private final List<DiagramForceGroupToggle> forceToggleWidgets = new ObjectArrayList();
    private AdvancedFbo fbo;
    private AdvancedFbo outlineFbo;
    private AdvancedFbo finalFbo;
    private float renderTime = 12.0f;
    private boolean paperVisible = false;
    private float lastPaperOffset = 10.0f;
    private float paperOffset = 10.0f;
    private float lastTabOffset = 0.0f;
    private float tabOffset = 0.0f;
    public final List<FormattedText> tooltipList = new ArrayList<FormattedText>();
    @Nullable
    private DiagramDataPacket serverData = null;
    private float viewportRadius;
    private int ticksWithoutUpdate = 0;
    private DiagramButton turnUpButton;
    private DiagramButton turnDownButton;
    private DiagramButton mergeButton;
    private boolean magnifying = false;
    private DiagramStickyNote note;

    public DiagramScreen(DiagramEntity diagramEntity, ClientSubLevel subLevel) {
        this.diagram = diagramEntity;
        this.subLevel = subLevel;
    }

    public static void open(DiagramEntity diagramEntity, DiagramConfig config, SubLevel subLevel) {
        Minecraft minecraft = Minecraft.getInstance();
        DiagramScreen screen = new DiagramScreen(diagramEntity, (ClientSubLevel)subLevel);
        screen.config = config;
        screen.updateViewportOrientation();
        minecraft.setScreen((Screen)screen);
        minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.VILLAGER_WORK_CARTOGRAPHER, (float)1.0f));
    }

    private void updateViewportOrientation() {
        this.renderTime = Float.MAX_VALUE;
        LOCAL_ORIENTATION.identity().rotateY((float)Math.toRadians(this.config.yaw())).rotateX((float)Math.toRadians(this.config.pitch()));
    }

    private void freeFramebuffers() {
        if (this.note != null) {
            this.note.free();
        }
        if (this.fbo != null) {
            this.fbo.free();
            this.fbo = null;
            this.outlineFbo.free();
            this.outlineFbo = null;
            this.finalFbo.free();
            this.finalFbo = null;
        }
    }

    public void onClose() {
        super.onClose();
        this.freeFramebuffers();
    }

    protected void init() {
        super.init();
        this.freeFramebuffers();
        this.fbo = AdvancedFbo.withSize((int)DiagramScreen.DIAGRAM_TEXTURE.width, (int)DiagramScreen.DIAGRAM_TEXTURE.height).addColorTextureBuffer().setDepthTextureBuffer().build(true);
        this.outlineFbo = AdvancedFbo.withSize((int)DiagramScreen.DIAGRAM_TEXTURE.width, (int)DiagramScreen.DIAGRAM_TEXTURE.height).addColorTextureBuffer().build(true);
        this.finalFbo = AdvancedFbo.withSize((int)DiagramScreen.DIAGRAM_TEXTURE.width, (int)DiagramScreen.DIAGRAM_TEXTURE.height).addColorTextureBuffer().build(true);
        int diagramX = this.width / 2 - DiagramScreen.DIAGRAM_TEXTURE.width / 2;
        int diagramY = this.height / 2 - DiagramScreen.DIAGRAM_TEXTURE.height / 2;
        this.note = new DiagramStickyNote(this, diagramX, diagramY, (Component)Component.empty(), () -> {});
        this.note.create(this.config.getNoteConfigs());
        if (this.subLevel.isRemoved()) {
            this.onClose();
            return;
        }
        this.renderContents((SubLevel)this.subLevel, 0.0f);
        for (int i = 0; i < 1; ++i) {
            this.addGreebles(diagramX, diagramY);
        }
        DiagramButton forceButton = new DiagramButton(SimGUITextures.DIAGRAM_ICON_FORCES, diagramX + 9, diagramY + 9, (Component)Component.empty(), () -> {
            this.paperVisible = !this.paperVisible;
            this.minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.BOOK_PAGE_TURN, (float)1.0f));
        }).setDiagramTooltip(() -> SimLang.translate("contraption_diagram.toggle_paper", new Object[0]).component());
        this.mergeButton = new DiagramButton(this.getMergeIcon(), diagramX + 9, diagramY + 9 + 20, (Component)Component.empty(), () -> {
            this.config.setMergeForces(!this.config.mergeForces());
            this.mergeButton.setTexture(this.getMergeIcon());
            this.setConfigDirty();
        }).setDiagramTooltip(() -> SimLang.translate("contraption_diagram.merge_forces", new Object[0]).color(-4025475).add(SimLang.translate(this.config.mergeForces() ? "contraption_diagram.merged" : "contraption_diagram.unmerged", new Object[0]).color(-1)).component());
        DiagramButton centerOfMassButton = new DiagramButton(SimGUITextures.DIAGRAM_ICON_COM_TOGGLE, diagramX + 9, diagramY + 9 + 40, (Component)Component.empty(), () -> {
            this.config.setDisplayCenterOfMass(!this.config.displayCenterOfMass());
            this.setConfigDirty();
        }).setDiagramTooltip(() -> SimLang.translate("contraption_diagram.center_of_mass", new Object[0]).color(-4025475).add(SimLang.translate(this.config.displayCenterOfMass() ? "contraption_diagram.shown" : "contraption_diagram.hidden", new Object[0]).color(-1)).component());
        DiagramButton massButton = new DiagramButton(SimGUITextures.DIAGRAM_ICON_MASS, diagramX + 9, diagramY + 9 + 60, (Component)Component.empty(), () -> {}).setDiagramTooltip(() -> {
            String massString = this.serverData != null ? String.format("%,.2f", this.serverData.mass()) : "---";
            return SimLang.translate("contraption_diagram.total_mass", new Object[0]).color(-4025475).add(SimLang.translate("contraption_diagram.mass", massString).color(-1)).component();
        });
        massButton.active = false;
        this.addRenderableWidget((GuiEventListener)forceButton);
        this.addRenderableWidget((GuiEventListener)centerOfMassButton);
        this.addRenderableWidget((GuiEventListener)massButton);
        this.addRenderableWidget((GuiEventListener)this.mergeButton);
        this.addRotationGizmo(diagramX, diagramY);
        this.addForceToggleWidgets(diagramX, diagramY);
        this.addWidget((GuiEventListener)this.note);
    }

    private void addRotationGizmo(int diagramX, int diagramY) {
        this.turnUpButton = new DiagramButton(SimGUITextures.DIAGRAM_ICON_TURN_UP, diagramX + 236, diagramY + 8, (Component)Component.empty(), () -> this.rotateDiagram(0, -1));
        this.turnDownButton = new DiagramButton(SimGUITextures.DIAGRAM_ICON_TURN_DOWN, diagramX + 236, diagramY + 8 + 14, (Component)Component.empty(), () -> this.rotateDiagram(0, 1));
        DiagramButton turnLeftButton = new DiagramButton(SimGUITextures.DIAGRAM_ICON_TURN_LEFT, diagramX + 228, diagramY + 12, (Component)Component.empty(), () -> this.rotateDiagram(1, 0));
        DiagramButton turnRightButton = new DiagramButton(SimGUITextures.DIAGRAM_ICON_TURN_RIGHT, diagramX + 243, diagramY + 12, (Component)Component.empty(), () -> this.rotateDiagram(-1, 0));
        this.addRenderableWidget((GuiEventListener)this.turnUpButton);
        this.addRenderableWidget((GuiEventListener)this.turnDownButton);
        this.addRenderableWidget((GuiEventListener)turnLeftButton);
        this.addRenderableWidget((GuiEventListener)turnRightButton);
    }

    private void rotateDiagram(int yawSteps, int pitchSteps) {
        if (this.config.pitch() > 45.0) {
            yawSteps = -yawSteps;
        }
        this.config.setYaw(this.config.yaw() + (double)((float)yawSteps * 90.0f));
        this.config.setPitch(Mth.clamp((double)(this.config.pitch() + (double)((float)pitchSteps * 90.0f)), (double)-90.0, (double)90.0));
        this.minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.VILLAGER_WORK_CARTOGRAPHER, (float)1.0f));
        this.updateViewportOrientation();
        this.setConfigDirty();
    }

    private void addForceToggleWidgets(int diagramX, int diagramY) {
        Registry forceGroups = ForceGroups.REGISTRY;
        this.forceToggleWidgets.clear();
        int i = 0;
        for (ForceGroup forceGroup : forceGroups) {
            int yOffset = 11 * (i + 1) - 1;
            int xOffset = -MAX_PAPER_OFFSET - 6;
            DiagramForceGroupToggle widget = new DiagramForceGroupToggle(this, forceGroup, diagramX + xOffset, diagramY + yOffset);
            this.addWidget((GuiEventListener)widget);
            this.forceToggleWidgets.add(widget);
            ++i;
        }
    }

    private HashMap<ResourceLocation, Tuple<Greeble, ArrayList<Greeble.TextureSlice>>> genGreebleSet(RandomSource random) {
        HashMap<ResourceLocation, Tuple<Greeble, ArrayList<Greeble.TextureSlice>>> greebleSet = new HashMap<ResourceLocation, Tuple<Greeble, ArrayList<Greeble.TextureSlice>>>();
        for (Map.Entry<ResourceLocation, Greeble> entry : SimResourceManagers.GREEBLE.entrySet()) {
            greebleSet.put(entry.getKey(), (Tuple<Greeble, ArrayList<Greeble.TextureSlice>>)new Tuple((Object)entry.getValue(), entry.getValue().shuffled()));
        }
        return greebleSet;
    }

    private ResourceLocation randomGreeble(RandomSource random) {
        float weightSum = 0.0f;
        for (Greeble greeble : SimResourceManagers.GREEBLE.entries()) {
            weightSum += greeble.weight();
        }
        float weight = random.nextFloat() * weightSum;
        for (Map.Entry<ResourceLocation, Greeble> greeble : SimResourceManagers.GREEBLE.entrySet()) {
            if (!((weight -= greeble.getValue().weight()) <= 0.0f)) continue;
            return greeble.getKey();
        }
        throw new RuntimeException();
    }

    private void addGreebles(int diagramX, int diagramY) {
        RandomSource random = this.subLevel.getLevel().getRandom();
        HashMap<ResourceLocation, Tuple<Greeble, ArrayList<Greeble.TextureSlice>>> greebleSet = this.genGreebleSet(random);
        ObjectArrayList placed = new ObjectArrayList();
        placed.add(new AABB(0.0, 0.0, 0.0, 26.0, 66.0, 1.0));
        placed.add(new AABB(227.0, 8.0, 0.0, 250.0, 28.0, 1.0));
        int padding = 10;
        int greebles = 8;
        this.finalFbo.bindRead();
        for (int i = 0; i < 8; ++i) {
            ResourceLocation greebleID = this.randomGreeble(random);
            Greeble greeble = SimResourceManagers.GREEBLE.get(greebleID);
            ArrayList slices = (ArrayList)greebleSet.get(greebleID).getB();
            if (slices.isEmpty()) continue;
            Greeble.TextureSlice slice = (Greeble.TextureSlice)slices.removeFirst();
            int x = random.nextInt(10, DiagramScreen.DIAGRAM_TEXTURE.width - slice.width() - 10);
            int y = random.nextInt(10, DiagramScreen.DIAGRAM_TEXTURE.height - slice.height() - 10);
            AABB box = new AABB((double)x, (double)y, 0.0, (double)(x + slice.width()), (double)(y + slice.height()), 1.0);
            boolean intersects = false;
            for (AABB aabb : placed) {
                if (!box.intersects(aabb)) continue;
                intersects = true;
                break;
            }
            if (intersects || this.aabbInFramebuffer(box)) continue;
            placed.add(box);
            this.addRenderableOnly(new GreebleRenderable(x + diagramX, y + diagramY, greeble.width(), greeble.height(), greeble.texture(), slice));
        }
        AdvancedFbo.unbind();
    }

    private boolean aabbInFramebuffer(AABB aabb) {
        int minX = (int)aabb.minX;
        int minY = (int)((double)DiagramScreen.DIAGRAM_TEXTURE.height - aabb.minY);
        int maxX = (int)aabb.maxX;
        int maxY = (int)((double)DiagramScreen.DIAGRAM_TEXTURE.height - aabb.maxY);
        int width = Math.abs(maxX - minX);
        int height = Math.abs(maxY - minY);
        int length = width * height;
        int[] buffer = new int[length];
        GL11.glReadPixels((int)minX, (int)(minY - height), (int)width, (int)height, (int)6408, (int)5121, (int[])buffer);
        for (int i = 0; i < length; ++i) {
            int color = buffer[i] >> 24;
            if (color == 0) continue;
            return true;
        }
        return false;
    }

    private void renderContents(SubLevel subLevel, float partialTicks) {
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!(this.renderTime >= 1.6666666f)) {
            this.renderTime += minecraft.getTimer().getRealtimeDeltaTicks();
            return;
        }
        this.renderTime = 0.0f;
        if (this.fbo == null) {
            return;
        }
        float zNear = 0.1f;
        LevelPlot plot = subLevel.getPlot();
        BoundingBox3ic plotBounds = plot.getBoundingBox();
        float radius = Math.max(Math.max(plotBounds.maxX() - plotBounds.minX(), plotBounds.maxY() - plotBounds.minY()), plotBounds.maxZ() - plotBounds.minZ()) + 1;
        radius *= 0.55f;
        this.viewportRadius = radius = Math.max(radius, 2.0f);
        Vector3d plotBoundsCenter = new Vector3d((double)(plotBounds.minX() + plotBounds.maxX() + 1) / 2.0, (double)(plotBounds.minY() + plotBounds.maxY() + 1) / 2.0, (double)(plotBounds.minZ() + plotBounds.maxZ() + 1) / 2.0);
        float aspect = (float)DiagramScreen.DIAGRAM_TEXTURE.width / (float)DiagramScreen.DIAGRAM_TEXTURE.height;
        PROJECTION_MAT.identity().ortho(-radius * aspect, radius * aspect, -radius, radius, 0.1f, radius * 2.0f);
        LOCAL_CAMERA_POSITION.set((Vector3dc)plotBoundsCenter.add((Vector3dc)LOCAL_ORIENTATION.transform(new Vector3d(0.0, 0.0, (double)radius))));
        Pose3dc renderPose = ((ClientSubLevel)subLevel).renderPose(partialTicks);
        renderPose.transformPosition(CAMERA_POSITION.set((Vector3dc)LOCAL_CAMERA_POSITION));
        DiagramScreen.draw(subLevel, partialTicks, LOCAL_ORIENTATION, PROJECTION_MAT, CAMERA_POSITION, DiagramScreen.DIAGRAM_TEXTURE.width, DiagramScreen.DIAGRAM_TEXTURE.height, this.fbo, this.outlineFbo, this.finalFbo, 0.25f, 1.0f, 3026994, 0x696965);
    }

    public static void draw(SubLevel subLevel, float partialTicks, Quaternionf localOrientation, Matrix4f projMatrix, Vector3d cameraPos, float inWidth, float inHeight, AdvancedFbo fbo, AdvancedFbo outlineFbo, AdvancedFbo finalFbo, float paletteOffset, float fadeScale, int lineColor, int lineShadowColor) {
        fbo.bind(true);
        fbo.clear();
        Pose3dc renderPose = ((ClientSubLevel)subLevel).renderPose(partialTicks);
        Quaternionf orientation = new Quaternionf(renderPose.orientation()).conjugate();
        orientation.premul((Quaternionfc)localOrientation.conjugate(new Quaternionf()));
        SimpleSubLevelGroupRenderer.renderChain(subLevel, fbo, new Matrix4f(), projMatrix, cameraPos, orientation, partialTicks);
        PostProcessingManager manager = VeilRenderSystem.renderer().getPostProcessingManager();
        PostPipeline pipeline = manager.getPipeline(Simulated.path("diagram"));
        if (pipeline != null) {
            Color LINE_SHADOW_COLOR = new Color(lineShadowColor);
            Color LINE_COLOR = new Color(lineColor);
            pipeline.getUniformSafe((CharSequence)"LineColor").setVector((float)LINE_COLOR.getRed() / 255.0f, (float)LINE_COLOR.getGreen() / 255.0f, (float)LINE_COLOR.getBlue() / 255.0f, 1.0f);
            pipeline.getUniformSafe((CharSequence)"LineShadowColor").setVector((float)LINE_SHADOW_COLOR.getRed() / 255.0f, (float)LINE_SHADOW_COLOR.getGreen() / 255.0f, (float)LINE_SHADOW_COLOR.getBlue() / 255.0f, 1.0f);
            pipeline.getUniformSafe((CharSequence)"InSize").setVector(inWidth, inHeight);
            pipeline.getUniformSafe((CharSequence)"PaletteOffset").setFloat(paletteOffset);
            pipeline.getUniformSafe((CharSequence)"FadeScale").setFloat(fadeScale);
        }
        PostPipeline.Context context = manager.getPostPipelineContext();
        context.setFramebuffer(Simulated.path("diagram"), fbo);
        context.setFramebuffer(Simulated.path("diagram_outlined"), outlineFbo);
        context.setFramebuffer(Simulated.path("diagram_final"), finalFbo);
        manager.runPipeline(pipeline, false);
    }

    protected void renderWindowBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(0, 0, this.width, this.height, -10, 0x4FFFFFFF);
    }

    public void tick() {
        super.tick();
        if (this.subLevel.isRemoved() || this.diagram.isRemoved()) {
            this.onClose();
            return;
        }
        if (this.configDirty) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new DiagramSaveConfigPacket(this.diagram.getId(), this.config)});
            this.configDirty = false;
        }
        if (this.ticksWithoutUpdate++ > UPDATE_REQUEST_INTERVAL) {
            this.ticksWithoutUpdate = 0;
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new RequestDiagramDataPacket(this.subLevel.getUniqueId())});
        }
        this.lastPaperOffset = this.paperOffset;
        this.paperOffset = Mth.lerp((float)0.4f, (float)this.paperOffset, (float)(this.paperVisible ? (float)MAX_PAPER_OFFSET : 10.0f));
        this.lastTabOffset = this.tabOffset;
        this.tabOffset = Mth.lerp((float)(this.paperVisible ? 0.4f : 0.1f), (float)this.tabOffset, (float)(this.paperVisible ? 1.0f : 0.0f));
        this.note.tick();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean widgetPress = super.mouseClicked(mouseX, mouseY, button);
        boolean withinNote = this.note.contains(mouseX, mouseY);
        if (withinNote || !widgetPress && this.contains(mouseX, mouseY)) {
            MAGNIFYING_CENTER.set(mouseX, mouseY);
        }
        return widgetPress;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean parent = super.mouseReleased(mouseX, mouseY, button);
        this.updateNote(mouseX, mouseY, parent);
        MAGNIFYING_CENTER.set(0.0, 0.0);
        MAGNIFYING_MAX.set(0.0, 0.0);
        return parent;
    }

    private void updateNote(double mouseX, double mouseY, boolean widgetRelease) {
        if (MAGNIFYING_CENTER.distanceSquared((Vector2dc)MAGNIFYING_MAX) < 9.0) {
            return;
        }
        this.updateMagnificationBox(mouseX, mouseY);
        if (this.note.contains(DiagramScreen.MAGNIFYING_CENTER.x, DiagramScreen.MAGNIFYING_CENTER.y)) {
            if (this.pointsWithinNote(MAGNIFYING_MAX, MAGNIFYING_MIN)) {
                this.note.handleInternalUpdate(MAGNIFYING_MAX, MAGNIFYING_MIN);
                this.minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.VILLAGER_WORK_CARTOGRAPHER, (float)1.0f));
            }
            return;
        }
        if (!this.pointsWithinDiagram(MAGNIFYING_MAX, MAGNIFYING_MIN) || widgetRelease) {
            return;
        }
        int diagramX = this.width / 2 - DiagramScreen.DIAGRAM_TEXTURE.width / 2;
        int diagramY = this.height / 2 - DiagramScreen.DIAGRAM_TEXTURE.height / 2;
        this.config.getNoteConfigs().setNoteYaw(this.config.yaw());
        this.config.getNoteConfigs().setNotePitch(this.config.pitch());
        this.config.getNoteConfigs().setActive(true);
        this.note.updateCurrentScope((Vector2dc)MAGNIFYING_MAX.sub((double)diagramX, (double)diagramY, new Vector2d()), (Vector2dc)MAGNIFYING_MIN.sub((double)diagramX, (double)diagramY, new Vector2d()), (Vector3dc)LOCAL_CAMERA_POSITION, (Matrix4fc)PROJECTION_MAT);
        this.note.activate();
        this.setConfigDirty();
        this.magnifying = false;
        this.minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.VILLAGER_WORK_CARTOGRAPHER, (float)1.0f));
    }

    private void updateMagnificationBox(double mouseX, double mouseY) {
        MAGNIFYING_MAX.set(mouseX, mouseY);
        MAGNIFYING_MAX.sub((Vector2dc)MAGNIFYING_CENTER);
        MAGNIFYING_MAX.absolute();
        double max = Math.max(DiagramScreen.MAGNIFYING_MAX.x, DiagramScreen.MAGNIFYING_MAX.y);
        MAGNIFYING_MAX.set(max, max);
        MAGNIFYING_MAX.negate(MAGNIFYING_MIN).add((Vector2dc)MAGNIFYING_CENTER);
        MAGNIFYING_MAX.add((Vector2dc)MAGNIFYING_CENTER);
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack ps = graphics.pose();
        if (this.subLevel.isRemoved() || this.diagram.isRemoved()) {
            this.onClose();
            return;
        }
        this.note.renderWidget(graphics, mouseX, mouseY, partialTicks);
        this.renderContents((SubLevel)this.subLevel, partialTicks);
        if (this.turnDownButton != null && this.turnUpButton != null) {
            this.turnDownButton.active = this.config.pitch() < 45.0;
            this.turnDownButton.visible = this.turnDownButton.active;
            this.turnUpButton.active = this.config.pitch() > -45.0;
            this.turnUpButton.visible = this.turnUpButton.active;
        }
        ps.pushPose();
        for (DiagramForceGroupToggle widget : this.forceToggleWidgets) {
            widget.active = this.paperVisible;
            widget.updateForceState(this.serverData);
            widget.renderTab(graphics, mouseX, mouseY, partialTicks);
        }
        int diagramX = this.width / 2 - DiagramScreen.DIAGRAM_TEXTURE.width / 2;
        int diagramY = this.height / 2 - DiagramScreen.DIAGRAM_TEXTURE.height / 2;
        ps.pushPose();
        ps.translate((float)diagramX, (float)diagramY, 0.0f);
        ps.translate(-this.getPaperOffset(partialTicks), 0.0f, 0.0f);
        SimGUITextures.DIAGRAM_PAPER.render(graphics, 0, 0);
        ps.popPose();
        for (DiagramForceGroupToggle widget : this.forceToggleWidgets) {
            widget.render(graphics, mouseX, mouseY, partialTicks);
        }
        ps.translate((float)diagramX, (float)diagramY, 0.0f);
        DIAGRAM_TEXTURE.render(graphics, 0, 0);
        DiagramScreen.renderFBO(graphics, this.finalFbo, DiagramScreen.DIAGRAM_TEXTURE.width, DiagramScreen.DIAGRAM_TEXTURE.height);
        String text = this.subLevel.getName();
        ps.pushPose();
        ps.translate(0.0f, 0.0f, 1.0f);
        if (text != null && !text.isEmpty()) {
            int footerW = this.font.width(text);
            int n = DiagramScreen.DIAGRAM_TEXTURE.width - footerW - 7;
            int n2 = DiagramScreen.DIAGRAM_TEXTURE.height - 5;
            Objects.requireNonNull(this.font);
            graphics.fill(n, n2 - 9, DiagramScreen.DIAGRAM_TEXTURE.width - 4, DiagramScreen.DIAGRAM_TEXTURE.height - 3, BG_COLOR.getRGB());
            int n3 = DiagramScreen.DIAGRAM_TEXTURE.width - footerW - 5;
            int n4 = DiagramScreen.DIAGRAM_TEXTURE.height - 3;
            Objects.requireNonNull(this.font);
            graphics.drawString(this.font, text, n3, n4 - 9, TEXT_COLOR.getRGB(), false);
        }
        ps.popPose();
        this.renderArrows(graphics, mouseX, mouseY, diagramX, diagramY, (Quaternionfc)LOCAL_ORIENTATION, (Vector3dc)LOCAL_CAMERA_POSITION, (Matrix4fc)PROJECTION_MAT, DiagramScreen.DIAGRAM_TEXTURE.width, DiagramScreen.DIAGRAM_TEXTURE.height);
        if (this.config.displayCenterOfMass()) {
            this.renderCenterOfMass(graphics);
        }
        ps.popPose();
    }

    protected void renderWindowForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack ps = graphics.pose();
        this.renderMagnificationHighlight(graphics, mouseX, mouseY, ps);
        if (!this.tooltipList.isEmpty()) {
            DiagramScreen.renderTooltip(graphics, mouseX, mouseY, this.tooltipList);
        }
        this.tooltipList.clear();
        super.renderWindowForeground(graphics, mouseX, mouseY, partialTicks);
    }

    private void renderMagnificationHighlight(GuiGraphics graphics, int mouseX, int mouseY, PoseStack ps) {
        boolean initiallyWithinNote = this.note.contains(DiagramScreen.MAGNIFYING_CENTER.x, DiagramScreen.MAGNIFYING_CENTER.y);
        this.updateMagnificationBox(mouseX, mouseY);
        if (MAGNIFYING_CENTER.distanceSquared((Vector2dc)MAGNIFYING_MAX) < 9.0) {
            return;
        }
        if (initiallyWithinNote || this.contains(DiagramScreen.MAGNIFYING_CENTER.x, DiagramScreen.MAGNIFYING_CENTER.y)) {
            boolean valid;
            ps.pushPose();
            ps.translate(0.0f, 0.0f, 1.0f);
            Vector2d min = new Vector2d((Vector2dc)MAGNIFYING_MIN);
            Vector2d max = new Vector2d((Vector2dc)MAGNIFYING_MAX);
            boolean bl = initiallyWithinNote ? this.note.contains(min.x, min.y) && this.note.contains(max.x, max.y) : (valid = this.contains(min.x, min.y) && this.contains(max.x, max.y));
            if (initiallyWithinNote) {
                this.note.clamp(min);
                this.note.clamp(max);
            } else {
                this.clamp(min);
                this.clamp(max);
            }
            double startX = min.x;
            double startY = min.y;
            double endX = max.x;
            double endY = max.y;
            int fillColor = valid ? 1090518268 : 0x40AAAAAA;
            int color = valid ? -1862270977 : -1862292822;
            graphics.fill((int)startX, (int)startY, (int)endX, (int)endY, fillColor);
            graphics.hLine((int)startX, (int)endX, (int)startY, color);
            graphics.hLine((int)startX, (int)endX, (int)endY, color);
            graphics.vLine((int)startX, (int)startY, (int)endY, color);
            graphics.vLine((int)endX, (int)startY, (int)endY, color);
            ps.popPose();
        }
    }

    public boolean pointsWithinNote(Vector2d target, Vector2d inverse) {
        return this.note.contains(target.x, target.y) && this.note.contains(inverse.x, inverse.y);
    }

    public boolean pointsWithinDiagram(Vector2d target, Vector2d inverse) {
        return this.contains(target.x, target.y) && this.contains(inverse.x, inverse.y);
    }

    public boolean contains(double x, double y) {
        return (x -= (double)((float)this.width / 2.0f - (float)DiagramScreen.DIAGRAM_TEXTURE.width / 2.0f)) > 0.0 && x < (double)DiagramScreen.DIAGRAM_TEXTURE.width && (y -= (double)((float)this.height / 2.0f - (float)DiagramScreen.DIAGRAM_TEXTURE.height / 2.0f)) > 0.0 && y < (double)DiagramScreen.DIAGRAM_TEXTURE.height;
    }

    public Vector2d clamp(Vector2d dest) {
        float minX = (float)this.width / 2.0f - (float)DiagramScreen.DIAGRAM_TEXTURE.width / 2.0f;
        float minY = (float)this.height / 2.0f - (float)DiagramScreen.DIAGRAM_TEXTURE.height / 2.0f;
        dest.max((Vector2dc)new Vector2d((double)minX, (double)minY));
        dest.min((Vector2dc)new Vector2d((double)(minX + (float)DiagramScreen.DIAGRAM_TEXTURE.width - 1.0f), (double)(minY + (float)DiagramScreen.DIAGRAM_TEXTURE.height - 1.0f)));
        return dest;
    }

    public static void renderFBO(GuiGraphics graphics, AdvancedFbo fbo, int width, int height) {
        int id = fbo.getColorTextureAttachment(0).getId();
        RenderSystem.setShaderTexture((int)0, (int)id);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float x1 = 0.0f;
        float y1 = 0.0f;
        bufferbuilder.addVertex(matrix4f, 0.0f, 0.0f, 0.0f).setUv(0.0f, 1.0f).setColor(-1);
        bufferbuilder.addVertex(matrix4f, 0.0f, (float)height, 0.0f).setUv(0.0f, 0.0f).setColor(-1);
        bufferbuilder.addVertex(matrix4f, (float)width, (float)height, 0.0f).setUv(1.0f, 0.0f).setColor(-1);
        bufferbuilder.addVertex(matrix4f, (float)width, 0.0f, 0.0f).setUv(1.0f, 1.0f).setColor(-1);
        BufferUploader.drawWithShader((MeshData)bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public void renderArrows(GuiGraphics graphics, int mouseX, int mouseY, int areaOriginX, int areaOriginY, Quaternionfc orientation, Vector3dc cameraPos, Matrix4fc projMatrix, int areaWidth, int areaHeight) {
        if (this.serverData != null) {
            ForceGroup group;
            double maxArrowLengthSquared = 0.0;
            HashMap<ForceGroup, List<ForceClusterFinder.Cluster>> clusters = new HashMap<ForceGroup, List<ForceClusterFinder.Cluster>>();
            for (ResourceLocation groupId : this.config.enabledForceGroups()) {
                group = (ForceGroup)ForceGroups.REGISTRY.get(groupId);
                assert (group != null);
                List<QueuedForceGroup.PointForce> forces = this.serverData.forces().get(group);
                if (forces == null) continue;
                List<ForceClusterFinder.Cluster> cluster = this.config.mergeForces() ? ForceClusterFinder.getMergedClusters(forces) : ForceClusterFinder.passThrough(forces);
                clusters.put(group, cluster);
                for (ForceClusterFinder.Cluster force : cluster) {
                    maxArrowLengthSquared = Math.max(maxArrowLengthSquared, force.force().lengthSquared());
                }
            }
            for (ResourceLocation groupId : this.config.enabledForceGroups()) {
                group = (ForceGroup)ForceGroups.REGISTRY.get(groupId);
                assert (group != null);
                List cluster = (List)clusters.get(group);
                if (cluster == null) continue;
                for (ForceClusterFinder.Cluster force : cluster) {
                    this.renderForceArrow(graphics, group, force, Math.sqrt(maxArrowLengthSquared), mouseX - areaOriginX, mouseY - areaOriginY, this.tooltipList, orientation, cameraPos, projMatrix, areaWidth, areaHeight);
                }
            }
        }
    }

    private void renderForceArrow(GuiGraphics graphics, ForceGroup forceGroup, ForceClusterFinder.Cluster pointForce, double maxArrowLength, int mouseX, int mouseY, List<FormattedText> tooltipLines, Quaternionfc orientation, Vector3dc cameraPos, Matrix4fc projMatrix, int areaWidth, int areaHeight) {
        boolean displayTooltip;
        float arrowLength;
        double forceMagnitude = pointForce.force().length();
        if (forceMagnitude <= 0.01 || (double)this.viewportRadius == 0.0) {
            return;
        }
        Vector3d globalFirstDir = pointForce.force().normalize(new Vector3d());
        Vector3d forceOffset = globalFirstDir.mul(Math.max(0.25, forceMagnitude / maxArrowLength) * (double)this.viewportRadius * 0.5, new Vector3d());
        Vector2d originCoords = DiagramScreen.getScreenCoords(new Vector3d((Vector3dc)pointForce.pos()), orientation, cameraPos, projMatrix, areaWidth, areaHeight);
        if (!this.canDrawArrowAt((int)originCoords.x, (int)originCoords.y, areaWidth, areaHeight)) {
            return;
        }
        Vector2d mousePos = new Vector2d((double)mouseX, (double)mouseY);
        int color = 0xFF000000 | forceGroup.color();
        int shadowColor = -396578;
        double facingDot = orientation.transformInverse((Vector3dc)globalFirstDir, new Vector3d()).dot(OrientedBoundingBox3d.FORWARD);
        if (Math.abs(facingDot) > 0.85) {
            PoseStack ps = graphics.pose();
            ps.pushPose();
            ps.translate(0.0, 0.0, 1.0);
            Vector2d vector2d = new Vector2d();
            if (mousePos.sub((Vector2dc)originCoords, vector2d).lengthSquared() < 64.0) {
                DiagramScreen.addForceArrowTooltip(forceGroup, pointForce.groupSize().getValue(), forceMagnitude, color, tooltipLines);
            }
            if (facingDot < 0.0) {
                SimGUITextures.DIAGRAM_ICON_ARROW_IN_PAGE_SHADOW.render(graphics, (int)originCoords.x - 8, (int)originCoords.y - 8, new Color(-396578));
                SimGUITextures.DIAGRAM_ICON_ARROW_IN_PAGE.render(graphics, (int)originCoords.x - 8, (int)originCoords.y - 8, new Color(color));
            } else {
                SimGUITextures.DIAGRAM_ICON_ARROW_OUT_PAGE_SHADOW.render(graphics, (int)originCoords.x - 8, (int)originCoords.y - 8, new Color(-396578));
                SimGUITextures.DIAGRAM_ICON_ARROW_OUT_PAGE.render(graphics, (int)originCoords.x - 8, (int)originCoords.y - 8, new Color(color));
            }
            ps.popPose();
            return;
        }
        Vector2d resultCoords = DiagramScreen.getScreenCoords(pointForce.pos().add((Vector3dc)forceOffset, new Vector3d()), orientation, cameraPos, projMatrix, areaWidth, areaHeight);
        Vector2d arrowDir = resultCoords.sub((Vector2dc)originCoords, new Vector2d());
        arrowDir.div((double)arrowLength);
        for (arrowLength = (float)arrowDir.length(); arrowLength > 0.0f && !this.canDrawArrowAt((int)resultCoords.x, (int)resultCoords.y, areaWidth, areaHeight); arrowLength -= 3.0f) {
            resultCoords.fma(-3.0, (Vector2dc)arrowDir);
        }
        int x1 = (int)originCoords.x();
        int y1 = (int)originCoords.y();
        int x2 = (int)resultCoords.x();
        int y2 = (int)resultCoords.y();
        MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();
        VertexConsumer builder = bufferSource.getBuffer(RenderType.gui());
        Matrix4f pose = graphics.pose().last().pose();
        Vector2d arrowLeft = new Vector2d(-arrowDir.y(), arrowDir.x()).mul(4.0);
        Vector2d arrowRight = new Vector2d(arrowDir.y(), -arrowDir.x()).mul(4.0);
        float headLen = 6.0f;
        boolean drawArrow = originCoords.distanceSquared((Vector2dc)resultCoords) > 36.0;
        double distanceAlongLine = mousePos.sub((Vector2dc)originCoords, new Vector2d()).dot((Vector2dc)arrowDir);
        distanceAlongLine = Mth.clamp((double)distanceAlongLine, (double)0.0, (double)arrowLength);
        boolean bl = displayTooltip = new Vector2d((Vector2dc)originCoords).fma(distanceAlongLine, (Vector2dc)arrowDir).distance((Vector2dc)mousePos) < 5.0;
        if (displayTooltip) {
            DiagramScreen.addForceArrowTooltip(forceGroup, pointForce.groupSize().getValue(), forceMagnitude, color, tooltipLines);
        }
        boolean z = true;
        int inflation = 3;
        builder.addVertex(pose, (float)x1 - (float)inflation, (float)y1 - (float)inflation, 1.0f).setColor(-396578);
        builder.addVertex(pose, (float)x1 - (float)inflation, (float)y1 + 1.0f + (float)inflation, 1.0f).setColor(-396578);
        builder.addVertex(pose, (float)x1 + 1.0f + (float)inflation, (float)y1 + 1.0f + (float)inflation, 1.0f).setColor(-396578);
        builder.addVertex(pose, (float)x1 + 1.0f + (float)inflation, (float)y1 - (float)inflation, 1.0f).setColor(-396578);
        if (drawArrow) {
            DiagramScreen.drawLine(builder, pose, x2, y2, (int)((double)x2 - arrowDir.x * 6.0 + arrowLeft.x), (int)((double)y2 - arrowDir.y * 6.0 + arrowLeft.y), -396578, 1);
            DiagramScreen.drawLine(builder, pose, x2, y2, (int)((double)x2 - arrowDir.x * 6.0 + arrowRight.x), (int)((double)y2 - arrowDir.y * 6.0 + arrowRight.y), -396578, 1);
            DiagramScreen.drawLine(builder, pose, x1, y1, x2, y2, -396578, 1);
            DiagramScreen.drawLine(builder, pose, x2, y2, (int)((double)x2 - arrowDir.x * 6.0 + arrowLeft.x), (int)((double)y2 - arrowDir.y * 6.0 + arrowLeft.y), color, 0);
            DiagramScreen.drawLine(builder, pose, x2, y2, (int)((double)x2 - arrowDir.x * 6.0 + arrowRight.x), (int)((double)y2 - arrowDir.y * 6.0 + arrowRight.y), color, 0);
            DiagramScreen.drawLine(builder, pose, x1, y1, x2, y2, color, 0);
        }
        inflation = 2;
        builder.addVertex(pose, (float)x1 - (float)inflation, (float)y1 - (float)inflation, 1.0f).setColor(color);
        builder.addVertex(pose, (float)x1 - (float)inflation, (float)y1 + 1.0f + (float)inflation, 1.0f).setColor(color);
        builder.addVertex(pose, (float)x1 + 1.0f + (float)inflation, (float)y1 + 1.0f + (float)inflation, 1.0f).setColor(color);
        builder.addVertex(pose, (float)x1 + 1.0f + (float)inflation, (float)y1 - (float)inflation, 1.0f).setColor(color);
    }

    private static void addForceArrowTooltip(ForceGroup forceGroup, int forceCount, double forceMagnitude, int color, List<FormattedText> tooltipLines) {
        LangBuilder forceNameText = SimLang.builder().add(forceGroup.name()).color(color);
        LangBuilder forceMagnitudeText = SimLang.translate("contraption_diagram.force_arrow_magnitude", String.format("%,.2f", forceMagnitude)).color(-1);
        if (forceCount > 1) {
            tooltipLines.add((FormattedText)SimLang.translate("contraption_diagram.merged_force_arrow", SimLang.translate("contraption_diagram.merging_numeral", Integer.toString(forceCount)).color(-1), forceNameText, forceMagnitudeText).color(-4025475).component());
        } else {
            tooltipLines.add((FormattedText)SimLang.translate("contraption_diagram.force_arrow", forceNameText, forceMagnitudeText).color(-4025475).component());
        }
    }

    private boolean canDrawArrowAt(int x, int y, int width, int height) {
        int padding = 8;
        return x >= 8 && x < width - 8 && y >= 8 && y < height - 8;
    }

    private static void drawLine(VertexConsumer builder, Matrix4f pose, int x1, int y1, int x2, int y2, int color, int inflation) {
        boolean z = true;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            builder.addVertex(pose, (float)x1 - (float)inflation, (float)y1 - (float)inflation, 1.0f).setColor(color);
            builder.addVertex(pose, (float)x1 - (float)inflation, (float)y1 + 1.0f + (float)inflation, 1.0f).setColor(color);
            builder.addVertex(pose, (float)x1 + 1.0f + (float)inflation, (float)y1 + 1.0f + (float)inflation, 1.0f).setColor(color);
            builder.addVertex(pose, (float)x1 + 1.0f + (float)inflation, (float)y1 - (float)inflation, 1.0f).setColor(color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 >= dx) continue;
            err += dx;
            y1 += sy;
        }
    }

    private SimGUITextures getMergeIcon() {
        return this.config.mergeForces() ? SimGUITextures.DIAGRAM_ICON_FORCES_MERGED : SimGUITextures.DIAGRAM_ICON_FORCES_SEPARATED;
    }

    public float getPaperOffset(float partialTicks) {
        return Mth.lerp((float)partialTicks, (float)this.lastPaperOffset, (float)this.paperOffset);
    }

    public float getTabOffset(float partialTicks) {
        return Mth.lerp((float)partialTicks, (float)this.lastTabOffset, (float)this.tabOffset);
    }

    private void renderCenterOfMass(GuiGraphics graphics) {
        Vector3d centerOfMass = new Vector3d((Vector3dc)this.subLevel.logicalPose().rotationPoint());
        Vector2d screenCoords = DiagramScreen.getScreenCoords(centerOfMass, (Quaternionfc)LOCAL_ORIENTATION, (Vector3dc)LOCAL_CAMERA_POSITION, (Matrix4fc)PROJECTION_MAT, DiagramScreen.DIAGRAM_TEXTURE.width, DiagramScreen.DIAGRAM_TEXTURE.height);
        SimGUITextures tex = SimGUITextures.DIAGRAM_ICON_COM;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(screenCoords.x - 8.0, screenCoords.y - 8.0, 0.0);
        graphics.blit(tex.location, 0, 0, 5, (float)tex.startX, (float)tex.startY, tex.width, tex.height, tex.texWidth, tex.texHeight);
        pose.popPose();
    }

    public static Vector2d getScreenCoords(Vector3d plotSpacePoint, Quaternionfc orientation, Vector3dc localPosition, Matrix4fc projMatrix, int width, int height) {
        plotSpacePoint.sub(localPosition);
        orientation.transformInverse(plotSpacePoint);
        Vector4f clipSpace = new Vector4f((float)plotSpacePoint.x, (float)plotSpacePoint.y, (float)plotSpacePoint.z, 1.0f);
        clipSpace.mul(projMatrix);
        clipSpace.div(clipSpace.w);
        double projectedX = (clipSpace.x() * 0.5f + 0.5f) * (float)width;
        double projectedY = (-clipSpace.y() * 0.5f + 0.5f) * (float)height;
        return new Vector2d(projectedX, projectedY);
    }

    public static Vector3d getPlotCoords(Vector2dc diagramSpacePoint, Quaternionfc orientation, Vector3dc localPosition, Matrix4fc projMatrix, int width, int height) {
        Vector3d clipSpace = new Vector3d(2.0 * diagramSpacePoint.x() / (double)width - 1.0, 1.0 - 2.0 * diagramSpacePoint.y() / (double)height, 0.0);
        Vector3d point = clipSpace.sub((Vector3fc)projMatrix.getTranslation(new Vector3f())).div((double)projMatrix.m00(), (double)projMatrix.m11(), (double)projMatrix.m22());
        orientation.transform(point);
        point.add(localPosition);
        return point;
    }

    public void updateData(DiagramDataPacket data) {
        this.serverData = data;
    }

    public static void renderTooltip(GuiGraphics guiGraphics, int x, int y, List<FormattedText> lines) {
        Font font = Minecraft.getInstance().font;
        Color colorBackground = new Color(-12766678);
        Color colorBorderTop = new Color(-10663878);
        Color colorBorderBot = new Color(-10663878);
        RemovedGuiUtils.drawHoveringText((GuiGraphics)guiGraphics, lines, (int)x, (int)y, (int)guiGraphics.guiWidth(), (int)guiGraphics.guiHeight(), (int)-1, (int)colorBackground.getRGB(), (int)colorBorderTop.getRGB(), (int)colorBorderBot.getRGB(), (Font)font);
    }

    public void setConfigDirty() {
        this.configDirty = true;
    }

    public record GreebleRenderable(int x, int y, int width, int height, ResourceLocation texture, Greeble.TextureSlice slice) implements Renderable
    {
        public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
            guiGraphics.blit(this.texture, this.x, this.y, (float)this.slice.x(), (float)this.slice.y(), this.slice.width(), this.slice.height(), this.width, this.height);
        }
    }
}
