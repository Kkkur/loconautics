/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.font.GlyphInfo
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.gui.font.FontSet
 *  net.minecraft.client.gui.font.glyphs.BakedGlyph
 *  net.minecraft.client.gui.font.glyphs.BakedGlyph$Effect
 *  net.minecraft.client.gui.font.glyphs.EmptyGlyph
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.util.FormattedCharSink
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.joml.Matrix4f
 */
package com.simibubi.create.content.trains.display;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(value=Dist.CLIENT)
static class FlapDisplayRenderer.FlapDisplayRenderOutput
implements FormattedCharSink {
    final MultiBufferSource bufferSource;
    final float r;
    final float g;
    final float b;
    final float a;
    final Matrix4f pose;
    final int light;
    final boolean paused;
    FlapDisplaySection section;
    float x;
    private int lineIndex;
    private Level level;

    public FlapDisplayRenderer.FlapDisplayRenderOutput(MultiBufferSource buffer, int color, Matrix4f pose, int light, int lineIndex, boolean paused, Level level, boolean glowing) {
        this.bufferSource = buffer;
        this.lineIndex = lineIndex;
        this.level = level;
        this.a = glowing ? 0.975f : 0.85f;
        this.r = (float)(color >> 16 & 0xFF) / 255.0f;
        this.g = (float)(color >> 8 & 0xFF) / 255.0f;
        this.b = (float)(color & 0xFF) / 255.0f;
        this.pose = pose;
        this.light = glowing ? 0xF000F0 : light;
        this.paused = paused;
    }

    public void nextSection(FlapDisplaySection section) {
        this.section = section;
        this.x = 0.0f;
    }

    public boolean accept(int charIndex, Style style, int glyph) {
        float standardWidth;
        FontSet fontset = this.getFontSet();
        int ticks = this.paused ? 0 : AnimationTickHolder.getTicks((LevelAccessor)this.level);
        float time = this.paused ? 0.0f : AnimationTickHolder.getRenderTime((LevelAccessor)this.level);
        float dim = 1.0f;
        if (this.section.renderCharsIndividually() && this.section.spinning[Math.min(charIndex, this.section.spinning.length)]) {
            float speed = this.section.spinningTicks > 5 && this.section.spinningTicks < 20 ? 1.75f : 2.5f;
            float cycle = time / speed + (float)charIndex * 16.83f + (float)this.lineIndex * 0.75f;
            float partial = cycle % 1.0f;
            int cyclingGlyph = this.section.cyclingOptions[(int)cycle % this.section.cyclingOptions.length].charAt(0);
            glyph = this.paused ? cyclingGlyph : (partial > 0.5f ? (partial > 0.75f ? 95 : 45) : cyclingGlyph);
            dim = 0.75f;
        }
        GlyphInfo glyphinfo = fontset.getGlyphInfo(glyph, false);
        float glyphWidth = glyphinfo.getAdvance(false);
        if (!this.section.renderCharsIndividually() && this.section.spinning[0]) {
            int n = ticks % 3 == 0 ? (glyphWidth == 6.0f ? 45 : (glyphWidth == 1.0f ? 39 : glyph)) : (glyph = glyph);
            int n2 = ticks % 3 == 2 ? (glyphWidth == 6.0f ? 95 : (glyphWidth == 1.0f ? 46 : glyph)) : (glyph = glyph);
            if (ticks % 3 != 1) {
                dim = 0.75f;
            }
        }
        BakedGlyph bakedglyph = style.isObfuscated() && glyph != 32 ? fontset.getRandomGlyph(glyphinfo) : fontset.getGlyph(glyph);
        TextColor textcolor = style.getColor();
        float red = this.r * dim;
        float green = this.g * dim;
        float blue = this.b * dim;
        if (textcolor != null) {
            int i = textcolor.getValue();
            red = (float)(i >> 16 & 0xFF) / 255.0f;
            green = (float)(i >> 8 & 0xFF) / 255.0f;
            blue = (float)(i & 0xFF) / 255.0f;
        }
        float f = standardWidth = this.section.wideFlaps ? 9.0f : 7.0f;
        if (this.section.renderCharsIndividually()) {
            this.x += (standardWidth - glyphWidth) / 2.0f;
        }
        if (this.isNotEmpty(bakedglyph)) {
            VertexConsumer vertexconsumer = this.bufferSource.getBuffer(this.renderTypeOf(bakedglyph));
            bakedglyph.render(style.isItalic(), this.x, 0.0f, this.pose, vertexconsumer, red, green, blue, this.a, this.light);
        }
        this.x = this.section.renderCharsIndividually() ? (this.x += standardWidth - (standardWidth - glyphWidth) / 2.0f) : (this.x += glyphWidth);
        return true;
    }

    public float finish(int bgColor) {
        if (bgColor == 0) {
            return this.x;
        }
        float a = (float)(bgColor >> 24 & 0xFF) / 255.0f;
        float r = (float)(bgColor >> 16 & 0xFF) / 255.0f;
        float g = (float)(bgColor >> 8 & 0xFF) / 255.0f;
        float b = (float)(bgColor & 0xFF) / 255.0f;
        BakedGlyph bakedglyph = this.getFontSet().whiteGlyph();
        VertexConsumer vertexconsumer = this.bufferSource.getBuffer(this.renderTypeOf(bakedglyph));
        bakedglyph.renderEffect(new BakedGlyph.Effect(-1.0f, 9.0f, this.section.size, -2.0f, 0.01f, r, g, b, a), this.pose, vertexconsumer, this.light);
        return this.x;
    }

    private FontSet getFontSet() {
        return Minecraft.getInstance().font.getFontSet(Style.DEFAULT_FONT);
    }

    private RenderType renderTypeOf(BakedGlyph bakedglyph) {
        return bakedglyph.renderType(Font.DisplayMode.NORMAL);
    }

    private boolean isNotEmpty(BakedGlyph bakedglyph) {
        return !(bakedglyph instanceof EmptyGlyph);
    }
}
