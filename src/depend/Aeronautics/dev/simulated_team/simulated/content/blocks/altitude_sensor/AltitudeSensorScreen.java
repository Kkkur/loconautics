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
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 */
package dev.simulated_team.simulated.content.blocks.altitude_sensor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.network.packets.ConfigureAltitudeSensorPacket;
import dev.simulated_team.simulated.util.SimColors;
import foundry.veil.api.network.VeilPacketManager;
import java.util.Objects;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class AltitudeSensorScreen
extends AbstractSimiScreen {
    private static final SimGUITextures BACKGROUND = SimGUITextures.ALTITUDE_SENSOR;
    private static final SimGUITextures BAR = SimGUITextures.ALTITUDE_SENSOR_BAR_LIT;
    private static final SimGUITextures GRABBY = SimGUITextures.ALTITUDE_SENSOR_GRABBY_THING;
    private final AltitudeSensorBlockEntity blockEntity;
    private final LerpedFloat visualHighSignal;
    private final LerpedFloat visualLowSignal;
    private final float lerpSpeed = 0.85f;
    private final int barCenterWidth = 8;
    private final int barWidth = 13;
    private final int barHeight = 200;
    private int barLeft = this.guiLeft + 3;
    private int barTop = this.guiTop + 3;
    private int rightBarLeft = this.guiLeft + 28;
    private int soundStep;
    private int ticksOpen;
    private float highSignal;
    private float lowSignal;
    boolean dragging;
    boolean draggingLeft;
    boolean draggingRight;

    public AltitudeSensorScreen(AltitudeSensorBlockEntity blockEntity) {
        super((Component)SimLang.translate("gui.altitude_sensor.title", new Object[0]).component());
        this.blockEntity = blockEntity;
        this.highSignal = blockEntity.highSignal;
        this.lowSignal = blockEntity.lowSignal;
        this.visualHighSignal = LerpedFloat.linear().startWithValue((double)this.highSignal);
        this.visualLowSignal = LerpedFloat.linear().startWithValue((double)this.lowSignal);
    }

    public static void open(AltitudeSensorBlockEntity blockEntity) {
        ScreenOpener.open((Screen)new AltitudeSensorScreen(blockEntity));
    }

    protected void init() {
        super.init();
        this.guiLeft -= AltitudeSensorScreen.BACKGROUND.width / 2;
        this.guiTop -= AltitudeSensorScreen.BACKGROUND.height / 2;
        this.barLeft = this.guiLeft + 3;
        this.barTop = this.guiTop + 3;
        this.rightBarLeft = this.guiLeft + 28;
    }

    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int a = (int)(80.0f * Math.min(1.0f, ((float)this.ticksOpen + AnimationTickHolder.getPartialTicks()) / 20.0f)) << 24;
        graphics.fillGradient(0, 0, this.width, this.height, 0x101010 | a, 0x101010 | a);
        BACKGROUND.render(graphics, this.guiLeft, this.guiTop);
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float visualHighPT = this.visualHighSignal.getValue(partialTicks);
        float visualLowPT = this.visualLowSignal.getValue(partialTicks);
        float invHighSignal = 1.0f - visualHighPT;
        float invLowSignal = 1.0f - visualLowPT;
        int middleBarWidth = 10;
        int x = this.width / 2 - 5;
        int y = this.height / 2 - this.barHeight / 2;
        Objects.requireNonNull(this);
        int highMax = (int)(visualHighPT * 200.0f);
        Objects.requireNonNull(this);
        int lowMax = (int)(visualLowPT * 200.0f);
        if (this.lowSignal > this.highSignal) {
            graphics.blit(AltitudeSensorScreen.BAR.location, x, y + AltitudeSensorScreen.BAR.height - highMax, AltitudeSensorScreen.BAR.startX, AltitudeSensorScreen.BAR.height - highMax - AltitudeSensorScreen.BAR.startY, AltitudeSensorScreen.BAR.width, AltitudeSensorScreen.BAR.height - (AltitudeSensorScreen.BAR.height - highMax));
        } else {
            graphics.blit(AltitudeSensorScreen.BAR.location, x, y, AltitudeSensorScreen.BAR.startX, AltitudeSensorScreen.BAR.startY, AltitudeSensorScreen.BAR.width, AltitudeSensorScreen.BAR.height - highMax);
        }
        PoseStack ps = graphics.pose();
        BAR.bind();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float imageSize = 256.0f;
        float uvx1 = (float)AltitudeSensorScreen.BAR.startX / 256.0f;
        float uvx2 = (float)(AltitudeSensorScreen.BAR.startX + AltitudeSensorScreen.BAR.width) / 256.0f;
        float uvy1 = (float)(AltitudeSensorScreen.BAR.startY + highMax) / 256.0f;
        float uvy2 = (float)(AltitudeSensorScreen.BAR.startY + lowMax) / 256.0f;
        float px1 = x;
        float px2 = (float)x + (float)AltitudeSensorScreen.BAR.width;
        float py1 = y - highMax + AltitudeSensorScreen.BAR.height;
        float py2 = y - lowMax + AltitudeSensorScreen.BAR.height;
        bufferbuilder.addVertex(ps.last().pose(), px2, py1, 0.0f).setUv(uvx2, uvy1).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        bufferbuilder.addVertex(ps.last().pose(), px1, py1, 0.0f).setUv(uvx1, uvy1).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        bufferbuilder.addVertex(ps.last().pose(), px1, py2, 0.0f).setUv(uvx1, uvy2).setColor(1.0f, 1.0f, 1.0f, 0.0f);
        bufferbuilder.addVertex(ps.last().pose(), px2, py2, 0.0f).setUv(uvx2, uvy2).setColor(1.0f, 1.0f, 1.0f, 0.0f);
        BufferUploader.drawWithShader((MeshData)bufferbuilder.buildOrThrow());
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        Objects.requireNonNull(this);
        int invHighMax = (int)(invHighSignal * 200.0f);
        Objects.requireNonNull(this);
        int invLowMax = (int)(invLowSignal * 200.0f);
        GRABBY.render(graphics, this.guiLeft - this.barWidth / 2, this.barTop - AltitudeSensorScreen.GRABBY.height / 2 + invLowMax);
        GRABBY.render(graphics, this.rightBarLeft - this.barWidth / 2, this.barTop - AltitudeSensorScreen.GRABBY.height / 2 + invHighMax);
        int worldHigh = (int)this.blockEntity.toWorldHeight(this.highSignal);
        int worldLow = (int)this.blockEntity.toWorldHeight(this.lowSignal);
        String lowText = String.valueOf(worldLow);
        String highText = String.valueOf(worldHigh);
        int n = this.barLeft + this.barCenterWidth / 2;
        Objects.requireNonNull(this.font);
        graphics.drawCenteredString(this.font, lowText, n, this.barTop - 9 / 2 + invLowMax, this.draggingLeft || this.overGrabby(mouseX, mouseY, true) ? SimColors.OFF_WHITE : SimColors.WOODEN_BROWN);
        int n2 = this.rightBarLeft + this.barWidth / 2 + 1;
        Objects.requireNonNull(this.font);
        graphics.drawCenteredString(this.font, highText, n2, this.barTop - 9 / 2 + invHighMax, this.draggingRight || this.overGrabby(mouseX, mouseY, false) ? SimColors.OFF_WHITE : SimColors.WOODEN_BROWN);
        int textWidth = this.font.width((FormattedText)this.title);
        int textX = this.guiLeft - textWidth / 2 - 10;
        int n3 = this.height / 2;
        Objects.requireNonNull(this.font);
        graphics.drawCenteredString(this.font, this.title, textX, n3 - 9, SimColors.OFF_WHITE);
    }

    private boolean overBar(double mouseX, double mouseY, boolean left) {
        int x = left ? this.guiLeft : this.rightBarLeft + 1;
        int y = this.barTop;
        return mouseX > (double)x && mouseX < (double)(x + this.barWidth) && mouseY > (double)y && mouseY < (double)(y + this.barHeight);
    }

    private boolean overGrabby(double mouseX, double mouseY, boolean left) {
        float visualHighPT = this.visualHighSignal.getValue(0.0f);
        float visualLowPT = this.visualLowSignal.getValue(0.0f);
        float invHighSignal = 1.0f - visualHighPT;
        float invLowSignal = 1.0f - visualLowPT;
        Objects.requireNonNull(this);
        int invHighMax = (int)(invHighSignal * 200.0f);
        Objects.requireNonNull(this);
        int invLowMax = (int)(invLowSignal * 200.0f);
        int x = (left ? this.barLeft : this.rightBarLeft) - this.barWidth / 2;
        int y = this.barTop - AltitudeSensorScreen.GRABBY.height / 2 + (left ? invLowMax : invHighMax);
        return mouseX > (double)x && mouseX < (double)(x + AltitudeSensorScreen.GRABBY.width) && mouseY > (double)y && mouseY < (double)(y + AltitudeSensorScreen.GRABBY.height);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.draggingLeft = this.overGrabby(mouseX, mouseY, true);
        this.draggingRight = this.overGrabby(mouseX, mouseY, false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        if (this.draggingLeft || this.draggingRight) {
            this.updateValues(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    private void updateValues(double mouseX, double mouseY) {
        int barTop = this.guiTop + 3;
        int barHeight = 200;
        float mouseProgress = (float)Mth.clamp((double)(1.0 - (mouseY - (double)barTop) / 200.0), (double)0.0, (double)1.0);
        float change = AltitudeSensorScreen.hasControlDown() ? (this.draggingLeft ? mouseProgress - this.lowSignal : mouseProgress - this.highSignal) : 0.0f;
        if (this.outOfBounds(this.lowSignal + change) || this.outOfBounds(this.highSignal + change)) {
            return;
        }
        if (this.draggingLeft) {
            this.lowSignal = mouseProgress;
            this.highSignal += change;
        } else if (this.draggingRight) {
            this.highSignal = mouseProgress;
            this.lowSignal += change;
        }
        double d = this.highSignal;
        Objects.requireNonNull(this);
        this.visualHighSignal.chase(d, (double)0.85f, LerpedFloat.Chaser.EXP);
        double d2 = this.lowSignal;
        Objects.requireNonNull(this);
        this.visualLowSignal.chase(d2, (double)0.85f, LerpedFloat.Chaser.EXP);
        int soundSteps = 15;
        double newSoundStep = Math.floor(mouseProgress * 15.0f);
        if (newSoundStep != (double)this.soundStep) {
            this.soundStep = (int)newSoundStep;
            Minecraft.getInstance().player.playSound(SoundEvents.LEVER_CLICK, 0.2f, 0.25f + mouseProgress * 0.5f);
        }
    }

    public boolean outOfBounds(float value) {
        return value < 0.0f || value > 1.0f;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.draggingLeft = false;
        this.draggingRight = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void tick() {
        ++this.ticksOpen;
        this.visualHighSignal.tickChaser();
        this.visualLowSignal.tickChaser();
    }

    public void onClose() {
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new ConfigureAltitudeSensorPacket(this.blockEntity.getBlockPos(), this.highSignal, this.lowSignal)});
        super.onClose();
    }
}
