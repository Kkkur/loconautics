/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.config.ui.BaseConfigScreen
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.gui.element.BoxElement
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.lang.FontHelper
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.theme.Color
 *  net.createmod.ponder.foundation.ui.PonderTagIndexScreen
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ConfirmLinkScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.renderer.CubeMap
 *  net.minecraft.client.renderer.PanoramaRenderer
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.infrastructure.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.CreateBuildInfo;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.theme.Color;
import net.createmod.ponder.foundation.ui.PonderTagIndexScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class CreateMainMenuScreen
extends AbstractSimiScreen {
    public static final CubeMap PANORAMA_RESOURCES = new CubeMap(Create.asResource("textures/gui/title/background/panorama"));
    public static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = ResourceLocation.withDefaultNamespace((String)"textures/gui/title/background/panorama_overlay.png");
    public static final PanoramaRenderer PANORAMA = new PanoramaRenderer(PANORAMA_RESOURCES);
    private static final Component CURSEFORGE_TOOLTIP = Component.literal((String)"CurseForge").withStyle(s -> s.withColor(16545884).withBold(Boolean.valueOf(true)));
    private static final Component MODRINTH_TOOLTIP = Component.literal((String)"Modrinth").withStyle(s -> s.withColor(4182827).withBold(Boolean.valueOf(true)));
    public static final String CURSEFORGE_LINK = "https://www.curseforge.com/minecraft/mc-mods/create";
    public static final String MODRINTH_LINK = "https://modrinth.com/mod/create";
    public static final String ISSUE_TRACKER_LINK = "https://github.com/Creators-of-Create/Create/issues";
    public static final String SUPPORT_LINK = "https://github.com/Creators-of-Create/Create/wiki/Supporting-the-Project";
    protected final Screen parent;
    protected boolean returnOnClose;
    private PanoramaRenderer vanillaPanorama;
    private long firstRenderTime;
    private Button gettingStarted;

    public CreateMainMenuScreen(Screen parent) {
        this.parent = parent;
        this.returnOnClose = true;
        this.vanillaPanorama = parent instanceof TitleScreen ? Screen.PANORAMA : new PanoramaRenderer(TitleScreen.CUBE_MAP);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.firstRenderTime == 0L) {
            this.firstRenderTime = Util.getMillis();
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float f = (float)(Util.getMillis() - this.firstRenderTime) / 1000.0f;
        float alpha = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        float elapsedPartials = this.minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        if (this.parent instanceof TitleScreen) {
            if (alpha < 1.0f) {
                this.vanillaPanorama.render(graphics, this.width, this.height, 1.0f, elapsedPartials);
            }
            PANORAMA.render(graphics, this.width, this.height, 1.0f, elapsedPartials);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            graphics.blit(PANORAMA_OVERLAY_TEXTURES, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        }
        RenderSystem.enableDepthTest();
        PoseStack ms = graphics.pose();
        for (int side : Iterate.positiveAndNegative) {
            ms.pushPose();
            ms.translate((float)(this.width / 2), 60.0f, 200.0f);
            ms.scale((float)(24 * side), (float)(24 * side), 32.0f);
            ms.translate(-1.75 * (double)(alpha * alpha / 2.0f + 0.5f), 0.25, 0.0);
            TransformStack.of((PoseStack)ms).rotateXDegrees(45.0f);
            GuiGameElement.of((BlockState)AllBlocks.LARGE_COGWHEEL.getDefaultState()).rotateBlock(0.0, (double)((float)Util.getMillis() / 32.0f * (float)side), 0.0).render(graphics);
            ms.translate(-1.0f, 0.0f, -1.0f);
            GuiGameElement.of((BlockState)AllBlocks.COGWHEEL.getDefaultState()).rotateBlock(0.0, (double)((float)Util.getMillis() / -16.0f * (float)side + 22.5f), 0.0).render(graphics);
            ms.popPose();
        }
        RenderSystem.enableBlend();
        ms.pushPose();
        ms.translate((float)(this.width / 2 - 32), 32.0f, -10.0f);
        ms.pushPose();
        ms.scale(0.25f, 0.25f, 0.25f);
        AllGuiTextures.LOGO.render(graphics, 0, 0);
        ms.popPose();
        new BoxElement().withBackground(-2013265920).flatBorder(new Color(0x1000000)).at(-32.0f, 56.0f, 100.0f).withBounds(128, 11).render(graphics);
        ms.popPose();
        ms.pushPose();
        ms.translate(0.0f, 0.0f, 200.0f);
        graphics.drawCenteredString(this.font, (Component)Component.literal((String)"Create").withStyle(ChatFormatting.BOLD).append((Component)Component.literal((String)(" v" + CreateBuildInfo.VERSION)).withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.WHITE})), this.width / 2, 89, -1787033);
        ms.popPose();
        RenderSystem.disableDepthTest();
    }

    protected void init() {
        super.init();
        this.returnOnClose = true;
        this.addButtons();
    }

    private void addButtons() {
        int yStart = this.height / 4 + 40;
        int center = this.width / 2;
        int bHeight = 20;
        int bShortWidth = 98;
        int bLongWidth = 200;
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)CreateLang.translateDirect("menu.return", new Object[0]), $ -> this.linkTo(this.parent)).bounds(center - 100, yStart + 92, bLongWidth, bHeight).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)CreateLang.translateDirect("menu.configure", new Object[0]), $ -> this.linkTo((Screen)new BaseConfigScreen((Screen)this, "create"))).bounds(center - 100, yStart + 24 + -16, bLongWidth, bHeight).build());
        this.gettingStarted = Button.builder((Component)CreateLang.translateDirect("menu.ponder_index", new Object[0]), $ -> this.linkTo((Screen)new PonderTagIndexScreen())).bounds(center + 2, yStart + 48 + -16, bShortWidth, bHeight).build();
        this.gettingStarted.active = !(this.parent instanceof TitleScreen);
        this.addRenderableWidget((GuiEventListener)this.gettingStarted);
        this.addRenderableWidget((GuiEventListener)new PlatformIconButton(center - 100, yStart + 48 + -16, bShortWidth / 2, bHeight, AllGuiTextures.CURSEFORGE_LOGO, 0.085f, b -> this.linkTo(CURSEFORGE_LINK), Tooltip.create((Component)CURSEFORGE_TOOLTIP)));
        this.addRenderableWidget((GuiEventListener)new PlatformIconButton(center - 50, yStart + 48 + -16, bShortWidth / 2, bHeight, AllGuiTextures.MODRINTH_LOGO, 0.0575f, b -> this.linkTo(MODRINTH_LINK), Tooltip.create((Component)MODRINTH_TOOLTIP)));
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)CreateLang.translateDirect("menu.report_bugs", new Object[0]), $ -> this.linkTo(ISSUE_TRACKER_LINK)).bounds(center + 2, yStart + 68, bShortWidth, bHeight).build());
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)CreateLang.translateDirect("menu.support", new Object[0]), $ -> this.linkTo(SUPPORT_LINK)).bounds(center - 100, yStart + 68, bShortWidth, bHeight).build());
    }

    protected void renderWindowForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderWindowForeground(graphics, mouseX, mouseY, partialTicks);
        this.renderables.forEach(w -> w.render(graphics, mouseX, mouseY, partialTicks));
        if (this.parent instanceof TitleScreen) {
            if (mouseX < this.gettingStarted.getX() || mouseX > this.gettingStarted.getX() + 98) {
                return;
            }
            if (mouseY < this.gettingStarted.getY() || mouseY > this.gettingStarted.getY() + 20) {
                return;
            }
            graphics.renderComponentTooltip(this.font, FontHelper.cutTextComponent((Component)CreateLang.translateDirect("menu.only_ingame", new Object[0]), (FontHelper.Palette)FontHelper.Palette.ALL_GRAY), mouseX, mouseY);
        }
    }

    private void linkTo(Screen screen) {
        this.returnOnClose = false;
        ScreenOpener.open((Screen)screen);
    }

    private void linkTo(String url) {
        this.returnOnClose = false;
        ScreenOpener.open((Screen)new ConfirmLinkScreen(p_213069_2_ -> {
            if (p_213069_2_) {
                Util.getPlatform().openUri(url);
            }
            this.minecraft.setScreen((Screen)this);
        }, url, true));
    }

    public boolean isPauseScreen() {
        return true;
    }

    protected static class PlatformIconButton
    extends Button {
        protected final AllGuiTextures icon;
        protected final float scale;

        public PlatformIconButton(int pX, int pY, int pWidth, int pHeight, AllGuiTextures icon, float scale, Button.OnPress pOnPress, Tooltip tooltip) {
            super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
            this.icon = icon;
            this.scale = scale;
            this.setTooltip(tooltip);
        }

        protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pt) {
            super.renderWidget(graphics, pMouseX, pMouseY, pt);
            PoseStack pPoseStack = graphics.pose();
            pPoseStack.pushPose();
            pPoseStack.translate((float)(this.getX() + this.width / 2) - (float)this.icon.getWidth() * this.scale / 2.0f, (float)(this.getY() + this.height / 2) - (float)this.icon.getHeight() * this.scale / 2.0f, 0.0f);
            pPoseStack.scale(this.scale, this.scale, 1.0f);
            this.icon.render(graphics, 0, 0);
            pPoseStack.popPose();
        }
    }
}
