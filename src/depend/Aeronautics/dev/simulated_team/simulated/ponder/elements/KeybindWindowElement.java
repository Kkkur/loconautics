/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nullable
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.InputElementBuilder
 *  net.createmod.ponder.enums.PonderGuiTextures
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.element.InputWindowElement
 *  net.createmod.ponder.foundation.ui.PonderUI
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.ponder.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.InputElementBuilder;
import net.createmod.ponder.enums.PonderGuiTextures;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class KeybindWindowElement
extends InputWindowElement {
    private final Vec3 sceneSpace;
    private final Pointing direction;
    @Nullable
    Component keybind;
    @Nullable
    ScreenElement icon;
    ItemStack item = ItemStack.EMPTY;

    public KeybindWindowElement(Vec3 sceneSpace, Pointing direction) {
        super(sceneSpace, direction);
        this.sceneSpace = sceneSpace;
        this.direction = direction;
    }

    @NotNull
    public Builder builder() {
        return new Builder();
    }

    public void render(@NotNull PonderScene scene, PonderUI screen, @NotNull GuiGraphics graphics, float partialTicks, float fade) {
        MutableComponent text;
        float xFade;
        Font font = screen.getFontRenderer();
        int width = 0;
        int height = 0;
        float f = this.direction == Pointing.RIGHT ? -1.0f : (xFade = this.direction == Pointing.LEFT ? 1.0f : 0.0f);
        float yFade = this.direction == Pointing.DOWN ? -1.0f : (this.direction == Pointing.UP ? 1.0f : 0.0f);
        xFade *= 10.0f * (1.0f - fade);
        yFade *= 10.0f * (1.0f - fade);
        boolean hasItem = !this.item.isEmpty();
        boolean hasText = this.keybind != null;
        boolean hasIcon = this.icon != null;
        int keyWidth = 0;
        Object object = text = hasText ? this.keybind : Component.empty();
        if (fade < 0.0625f) {
            return;
        }
        Vec2 sceneToScreen = scene.getTransform().sceneToScreen(this.sceneSpace, partialTicks);
        if (hasIcon) {
            width += 24;
            height = 24;
        }
        if (hasText) {
            keyWidth = font.width((FormattedText)text);
            width += keyWidth;
        }
        if (hasItem) {
            width += 24;
            height = 24;
        }
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(sceneToScreen.x + xFade, sceneToScreen.y + yFade, 400.0f);
        PonderUI.renderSpeechBox((GuiGraphics)graphics, (int)0, (int)0, (int)width, (int)height, (boolean)false, (Pointing)this.direction, (boolean)true);
        poseStack.translate(0.0f, 0.0f, 100.0f);
        if (hasText) {
            Objects.requireNonNull(font);
            graphics.drawString(font, (Component)text, 2, (int)((float)(height - 9) / 2.0f + 2.0f), PonderPalette.WHITE.getColorObject().scaleAlpha(fade).getRGB(), false);
        }
        if (hasIcon) {
            poseStack.pushPose();
            poseStack.translate((float)keyWidth, 0.0f, 0.0f);
            poseStack.scale(1.5f, 1.5f, 1.5f);
            this.icon.render(graphics, 0, 0);
            poseStack.popPose();
        }
        if (hasItem) {
            ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)this.item).at((float)(keyWidth + (hasIcon ? 24 : 0)), 0.0f)).scale(1.5).render(graphics);
            RenderSystem.disableDepthTest();
        }
        poseStack.popPose();
    }

    public class Builder
    implements InputElementBuilder {
        public Builder withItem(ItemStack stack) {
            KeybindWindowElement.this.item = stack;
            return this;
        }

        public Builder leftClick() {
            KeybindWindowElement.this.icon = PonderGuiTextures.ICON_LMB;
            return this;
        }

        public Builder scroll() {
            KeybindWindowElement.this.icon = PonderGuiTextures.ICON_SCROLL;
            return this;
        }

        public Builder rightClick() {
            KeybindWindowElement.this.icon = PonderGuiTextures.ICON_RMB;
            return this;
        }

        public Builder showing(ScreenElement icon) {
            KeybindWindowElement.this.icon = icon;
            return this;
        }

        public Builder whileSneaking() {
            throw new UnsupportedOperationException();
        }

        public Builder whileCTRL() {
            throw new UnsupportedOperationException();
        }

        public Builder keybind(String keybind) {
            KeybindWindowElement.this.keybind = Component.keybind((String)keybind).append(" +");
            return this;
        }
    }
}
