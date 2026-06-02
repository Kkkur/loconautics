/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.RenderElement
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.createmod.catnip.theme.Color
 *  net.createmod.ponder.enums.PonderGuiTextures
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HopperBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 */
package com.simibubi.create.content.contraptions.wrench;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllKeys;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.wrench.NonVisualizationLevel;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchHandler;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenuSubmitPacket;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.gui.AllIcons;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.RenderElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.catnip.theme.Color;
import net.createmod.ponder.enums.PonderGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class RadialWrenchMenu
extends AbstractSimiScreen {
    public static final Map<Property<?>, String> VALID_PROPERTIES = new HashMap();
    public static final Set<ResourceLocation> BLOCK_BLACKLIST;
    private final BlockState state;
    private final BlockPos pos;
    @Nullable
    private final BlockEntity blockEntity;
    private final Level level;
    private final NonVisualizationLevel nonVisualizationLevel;
    private final List<Map.Entry<Property<?>, String>> propertiesForState;
    private final int innerRadius = 50;
    private final int outerRadius = 110;
    private int selectedPropertyIndex = 0;
    private List<BlockState> allStates = List.of();
    private String propertyLabel = "";
    private int ticksOpen;
    private int selectedStateIndex = 0;
    private final RenderElement iconScroll = RenderElement.of((ScreenElement)PonderGuiTextures.ICON_SCROLL);
    private final RenderElement iconUp = RenderElement.of((ScreenElement)AllIcons.I_PRIORITY_HIGH);
    private final RenderElement iconDown = RenderElement.of((ScreenElement)AllIcons.I_PRIORITY_LOW);

    public static void registerRotationProperty(Property<?> property, String label) {
        if (VALID_PROPERTIES.containsKey(property)) {
            return;
        }
        VALID_PROPERTIES.put(property, label);
    }

    public static void registerBlacklistedBlock(ResourceLocation location) {
        if (BLOCK_BLACKLIST.contains(location)) {
            return;
        }
        BLOCK_BLACKLIST.add(location);
    }

    public static Optional<RadialWrenchMenu> tryCreateFor(BlockState state, BlockPos pos, Level level) {
        if (BLOCK_BLACKLIST.contains(RegisteredObjectsHelper.getKeyOrThrow((Block)state.getBlock()))) {
            return Optional.empty();
        }
        List<Map.Entry<Property<?>, String>> propertiesForState = VALID_PROPERTIES.entrySet().stream().filter(entry -> state.hasProperty((Property)entry.getKey())).toList();
        if (propertiesForState.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new RadialWrenchMenu(state, pos, level, propertiesForState));
    }

    private RadialWrenchMenu(BlockState state, BlockPos pos, Level level, List<Map.Entry<Property<?>, String>> properties) {
        this.state = state;
        this.pos = pos;
        this.level = level;
        this.nonVisualizationLevel = new NonVisualizationLevel(level);
        this.blockEntity = level.getBlockEntity(pos);
        this.propertiesForState = properties;
        this.initForSelectedProperty();
    }

    private void initForSelectedProperty() {
        Map.Entry<Property<?>, String> entry = this.propertiesForState.get(this.selectedPropertyIndex);
        this.allStates = new ArrayList<BlockState>();
        this.cycleAllPropertyValues(this.state, entry.getKey(), this.allStates);
        this.propertyLabel = entry.getValue();
    }

    private void cycleAllPropertyValues(BlockState state, Property<?> property, List<BlockState> states) {
        Optional first = property.getPossibleValues().stream().findFirst();
        if (first.isEmpty()) {
            return;
        }
        int offset = 0;
        int safety = 100;
        while (safety-- > 0) {
            if (state.getValue(property).equals(first.get())) {
                offset = 99 - safety;
                break;
            }
            state = (BlockState)state.cycle(property);
        }
        safety = 100;
        while (safety-- > 0 && !states.contains(state)) {
            states.add(state);
            state = (BlockState)state.cycle(property);
        }
        this.selectedStateIndex = (offset = Mth.clamp((int)offset, (int)0, (int)(states.size() - 1))) == 0 ? 0 : states.size() - offset;
    }

    public void tick() {
        ++this.ticksOpen;
        if (!this.level.getBlockState(this.pos).is(this.state.getBlock())) {
            Minecraft.getInstance().setScreen(null);
        }
        super.tick();
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.width / 2;
        int y = this.height / 2;
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate((float)x, (float)y, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int mouseOffsetX = mouseX - this.width / 2;
        int mouseOffsetY = mouseY - this.height / 2;
        if (Mth.length((double)mouseOffsetX, (double)mouseOffsetY) > 45.0) {
            double theta = Mth.atan2((double)mouseOffsetX, (double)mouseOffsetY);
            float sectorSize = 360.0f / (float)this.allStates.size();
            this.selectedStateIndex = (int)Math.floor((-AngleHelper.deg((double)Mth.atan2((double)mouseOffsetX, (double)mouseOffsetY)) + 180.0f + sectorSize / 2.0f) % 360.0f / sectorSize);
            this.renderDirectionIndicator(graphics, theta);
        }
        this.renderRadialSectors(graphics);
        UIRenderHelper.streak((GuiGraphics)graphics, (float)0.0f, (int)0, (int)0, (int)32, (int)65, (Color)Color.BLACK.setAlpha(0.8f));
        UIRenderHelper.streak((GuiGraphics)graphics, (float)180.0f, (int)0, (int)0, (int)32, (int)65, (Color)Color.BLACK.setAlpha(0.8f));
        if (this.selectedPropertyIndex > 0) {
            this.iconScroll.at(-14.0f, -46.0f).render(graphics);
            this.iconUp.at(-1.0f, -46.0f).render(graphics);
            graphics.drawCenteredString(this.font, this.propertiesForState.get(this.selectedPropertyIndex - 1).getValue(), 0, -30, ((Color)UIRenderHelper.COLOR_TEXT.getFirst()).getRGB());
        }
        if (this.selectedPropertyIndex < this.propertiesForState.size() - 1) {
            this.iconScroll.at(-14.0f, 30.0f).render(graphics);
            this.iconDown.at(-1.0f, 30.0f).render(graphics);
            graphics.drawCenteredString(this.font, this.propertiesForState.get(this.selectedPropertyIndex + 1).getValue(), 0, 22, ((Color)UIRenderHelper.COLOR_TEXT.getFirst()).getRGB());
        }
        graphics.drawCenteredString(this.font, "Currently", 0, -13, ((Color)UIRenderHelper.COLOR_TEXT.getFirst()).getRGB());
        graphics.drawCenteredString(this.font, "Changing:", 0, -3, ((Color)UIRenderHelper.COLOR_TEXT.getFirst()).getRGB());
        graphics.drawCenteredString(this.font, this.propertyLabel, 0, 7, ((Color)UIRenderHelper.COLOR_TEXT.getFirst()).getRGB());
        ms.popPose();
    }

    private void renderRadialSectors(GuiGraphics graphics) {
        int sectors = this.allStates.size();
        if (sectors < 2) {
            return;
        }
        PoseStack poseStack = graphics.pose();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        float sectorAngle = 360.0f / (float)sectors;
        int sectorWidth = 60;
        poseStack.pushPose();
        for (int i = 0; i < sectors; ++i) {
            Color innerColor = Color.WHITE.setAlpha(0.05f);
            Color outerColor = Color.WHITE.setAlpha(0.3f);
            BlockState blockState = this.allStates.get(i);
            Property<?> property = this.propertiesForState.get(this.selectedPropertyIndex).getKey();
            poseStack.pushPose();
            if (i == this.selectedStateIndex) {
                innerColor.mixWith(new Color(0.8f, 0.8f, 0.2f, 0.2f), 0.5f);
                outerColor.mixWith(new Color(0.8f, 0.8f, 0.2f, 0.6f), 0.5f);
                UIRenderHelper.drawRadialSector((GuiGraphics)graphics, (float)112.0f, (float)113.0f, (float)(-(sectorAngle / 2.0f + 90.0f)), (float)sectorAngle, (Color)outerColor, (Color)outerColor);
            }
            UIRenderHelper.drawRadialSector((GuiGraphics)graphics, (float)50.0f, (float)110.0f, (float)(-(sectorAngle / 2.0f + 90.0f)), (float)sectorAngle, (Color)innerColor, (Color)outerColor);
            Color c = innerColor.copy().setAlpha(0.5f);
            UIRenderHelper.drawRadialSector((GuiGraphics)graphics, (float)47.0f, (float)48.0f, (float)(-(sectorAngle / 2.0f + 90.0f)), (float)sectorAngle, (Color)c, (Color)c);
            ((PoseTransformStack)TransformStack.of((PoseStack)poseStack).translateY(-((float)sectorWidth / 2.0f + 50.0f))).rotateZDegrees((float)(-i) * sectorAngle);
            poseStack.translate(0.0f, 0.0f, 100.0f);
            try {
                this.withLevel(this.blockEntity, (Level)this.nonVisualizationLevel, () -> GuiGameElement.of((BlockState)blockState, (BlockEntity)this.blockEntity).rotateBlock((double)player.getXRot(), (double)(player.getYRot() + 180.0f), 0.0).scale(24.0).at(-12.0f, 12.0f).render(graphics));
            }
            catch (Exception e) {
                Create.LOGGER.warn("Failed to render blockstate in RadialWrenchMenu", (Throwable)e);
                this.allStates.remove(i);
                this.selectedStateIndex = 0;
                return;
            }
            poseStack.translate(0.0f, 0.0f, 50.0f);
            if (i == this.selectedStateIndex) {
                graphics.drawCenteredString(this.font, blockState.getValue(property).toString(), 0, 15, ((Color)UIRenderHelper.COLOR_TEXT.getFirst()).getRGB());
            }
            poseStack.popPose();
            poseStack.pushPose();
            TransformStack.of((PoseStack)poseStack).rotateZDegrees(sectorAngle / 2.0f);
            poseStack.translate(0.0f, -70.0f, 10.0f);
            UIRenderHelper.angledGradient((GuiGraphics)graphics, (float)-90.0f, (int)0, (int)0, (float)0.5f, (float)(sectorWidth - 10), (Color)Color.WHITE.setAlpha(0.5f), (Color)Color.WHITE.setAlpha(0.15f));
            UIRenderHelper.angledGradient((GuiGraphics)graphics, (float)90.0f, (int)0, (int)0, (float)0.5f, (float)25.0f, (Color)Color.WHITE.setAlpha(0.5f), (Color)Color.WHITE.setAlpha(0.15f));
            poseStack.popPose();
            TransformStack.of((PoseStack)poseStack).rotateZDegrees(sectorAngle);
        }
        poseStack.popPose();
    }

    private void renderDirectionIndicator(GuiGraphics graphics, double theta) {
        PoseStack poseStack = graphics.pose();
        float r = 0.8f;
        float g = 0.8f;
        float b = 0.8f;
        poseStack.pushPose();
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).rotateZ((float)(-theta))).translateY(53.0f)).translateZ(15.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = poseStack.last().pose();
        bufferbuilder.addVertex(mat, 0.0f, 0.0f, 0.0f).setColor(r, g, b, 0.75f);
        bufferbuilder.addVertex(mat, 5.0f, -5.0f, 0.0f).setColor(r, g, b, 0.4f);
        bufferbuilder.addVertex(mat, 3.0f, -4.5f, 0.0f).setColor(r, g, b, 0.4f);
        bufferbuilder.addVertex(mat, 0.0f, -4.2f, 0.0f).setColor(r, g, b, 0.4f);
        bufferbuilder.addVertex(mat, -3.0f, -4.5f, 0.0f).setColor(r, g, b, 0.4f);
        bufferbuilder.addVertex(mat, -5.0f, -5.0f, 0.0f).setColor(r, g, b, 0.4f);
        BufferUploader.drawWithShader((MeshData)bufferbuilder.buildOrThrow());
        poseStack.popPose();
    }

    private void submitChange() {
        BlockState selectedState = this.allStates.get(this.selectedStateIndex);
        if (selectedState != this.state) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new RadialWrenchMenuSubmitPacket(this.pos, selectedState));
        }
        this.onClose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void withLevel(@Nullable BlockEntity blockEntity, Level newLevel, Runnable action) {
        boolean hasBlockEntity = blockEntity != null;
        Level originalLevel = null;
        if (hasBlockEntity) {
            originalLevel = blockEntity.getLevel();
            blockEntity.setLevel(newLevel);
        }
        try {
            action.run();
        }
        finally {
            if (hasBlockEntity) {
                blockEntity.setLevel(originalLevel);
            }
        }
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Color color = BACKGROUND_COLOR.scaleAlpha(Math.min(1.0f, ((float)this.ticksOpen + AnimationTickHolder.getPartialTicks()) / 20.0f));
        guiGraphics.fillGradient(0, 0, this.width, this.height, color.getRGB(), color.getRGB());
    }

    public boolean keyReleased(int code, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey((int)code, (int)scanCode);
        if (AllKeys.ROTATE_MENU.getKeybind().isActiveAndMatches(mouseKey)) {
            this.submitChange();
            return true;
        }
        return super.keyReleased(code, scanCode, modifiers);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.submitChange();
            return true;
        }
        if (pButton == 1) {
            this.onClose();
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.propertiesForState.size() < 2) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        int indexDelta = (int)Math.round(Math.signum(-scrollY));
        int newIndex = this.selectedPropertyIndex + indexDelta;
        if (newIndex < 0) {
            return false;
        }
        if (newIndex >= this.propertiesForState.size()) {
            return false;
        }
        this.selectedPropertyIndex = newIndex;
        this.initForSelectedProperty();
        return true;
    }

    public void removed() {
        RadialWrenchHandler.COOLDOWN = 2;
        super.removed();
    }

    static {
        RadialWrenchMenu.registerRotationProperty(RotatedPillarKineticBlock.AXIS, "Axis");
        RadialWrenchMenu.registerRotationProperty(DirectionalKineticBlock.FACING, "Facing");
        RadialWrenchMenu.registerRotationProperty(HorizontalAxisKineticBlock.HORIZONTAL_AXIS, "Axis");
        RadialWrenchMenu.registerRotationProperty(HorizontalKineticBlock.HORIZONTAL_FACING, "Facing");
        RadialWrenchMenu.registerRotationProperty(HopperBlock.FACING, "Facing");
        RadialWrenchMenu.registerRotationProperty(DirectedDirectionalBlock.TARGET, "Target");
        RadialWrenchMenu.registerRotationProperty(SequencedGearshiftBlock.VERTICAL, "Vertical");
        BLOCK_BLACKLIST = new HashSet<ResourceLocation>();
        RadialWrenchMenu.registerBlacklistedBlock(AllBlocks.LARGE_WATER_WHEEL.getId());
        RadialWrenchMenu.registerBlacklistedBlock(AllBlocks.WATER_WHEEL_STRUCTURAL.getId());
    }
}
