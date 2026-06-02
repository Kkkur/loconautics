/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.gui.AllIcons
 *  net.createmod.catnip.gui.element.DelegatedStencilElement
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 */
package dev.simulated_team.simulated.index;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.gui.AllIcons;
import dev.simulated_team.simulated.Simulated;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class SimIcons
extends AllIcons {
    public static final ResourceLocation ICON_ATLAS = Simulated.path("textures/gui/icons.png");
    public static final int ICON_ATLAS_SIZE = 64;
    private static int x = 0;
    private static int y = -1;
    private final int iconX;
    private final int iconY;
    public static final SimIcons HALF_EXTEND = SimIcons.newRow();
    public static final SimIcons FULL_EXTEND = SimIcons.next();
    public static final SimIcons ADD_OR_EDIT = SimIcons.newRow();
    public static final SimIcons HAMBURGER = SimIcons.next();
    public static final SimIcons CANCEL = SimIcons.next();
    public static final SimIcons CONFIG = SimIcons.next();
    public static final SimIcons KEY_ARROW_UP = SimIcons.newRow();
    public static final SimIcons KEY_ARROW_LEFT = SimIcons.next();
    public static final SimIcons KEY_ARROW_DOWN = SimIcons.next();
    public static final SimIcons KEY_ARROW_RIGHT = SimIcons.next();

    public SimIcons(int x, int y) {
        super(x, y);
        this.iconX = x * 16;
        this.iconY = y * 16;
    }

    private static SimIcons next() {
        return new SimIcons(++x, y);
    }

    private static SimIcons newRow() {
        x = 0;
        return new SimIcons(0, ++y);
    }

    public void bind() {
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)ICON_ATLAS);
    }

    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(ICON_ATLAS, x, y, 0, (float)this.iconX, (float)this.iconY, 16, 16, 64, 64);
    }

    public void render(PoseStack ms, MultiBufferSource buffer, int color) {
        VertexConsumer builder = buffer.getBuffer(RenderType.text((ResourceLocation)ICON_ATLAS));
        Matrix4f matrix = ms.last().pose();
        Color rgb = new Color(color);
        int light = 0xF000F0;
        Vec3 vec1 = new Vec3(0.0, 0.0, 0.0);
        Vec3 vec2 = new Vec3(0.0, 1.0, 0.0);
        Vec3 vec3 = new Vec3(1.0, 1.0, 0.0);
        Vec3 vec4 = new Vec3(1.0, 0.0, 0.0);
        float u1 = (float)this.iconX * 1.0f / 64.0f;
        float u2 = (float)(this.iconX + 16) * 1.0f / 64.0f;
        float v1 = (float)this.iconY * 1.0f / 64.0f;
        float v2 = (float)(this.iconY + 16) * 1.0f / 64.0f;
        this.vertex(builder, matrix, vec1, rgb, u1, v1, 0xF000F0);
        this.vertex(builder, matrix, vec2, rgb, u1, v2, 0xF000F0);
        this.vertex(builder, matrix, vec3, rgb, u2, v2, 0xF000F0);
        this.vertex(builder, matrix, vec4, rgb, u2, v1, 0xF000F0);
    }

    private void vertex(VertexConsumer builder, Matrix4f matrix, Vec3 vec, Color rgb, float u, float v, int light) {
        builder.addVertex(matrix, (float)vec.x, (float)vec.y, (float)vec.z).setColor(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 255).setUv(u, v).setLight(light);
    }

    public DelegatedStencilElement asStencil() {
        return (DelegatedStencilElement)new DelegatedStencilElement().withStencilRenderer((ms, w, h, alpha) -> this.render(ms, 0, 0)).withBounds(16, 16);
    }
}
