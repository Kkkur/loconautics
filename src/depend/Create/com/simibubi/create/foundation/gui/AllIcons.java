/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.gui.element.DelegatedStencilElement
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.joml.Matrix4f
 */
package com.simibubi.create.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.Create;
import net.createmod.catnip.gui.element.DelegatedStencilElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

public class AllIcons
implements ScreenElement {
    public static final ResourceLocation ICON_ATLAS = Create.asResource("textures/gui/icons.png");
    public static final int ICON_ATLAS_SIZE = 256;
    private static int x = 0;
    private static int y = -1;
    private int iconX;
    private int iconY;
    public static final AllIcons I_ADD = AllIcons.newRow();
    public static final AllIcons I_TRASH = AllIcons.next();
    public static final AllIcons I_3x3 = AllIcons.next();
    public static final AllIcons I_TARGET = AllIcons.next();
    public static final AllIcons I_PRIORITY_VERY_LOW = AllIcons.next();
    public static final AllIcons I_PRIORITY_LOW = AllIcons.next();
    public static final AllIcons I_PRIORITY_HIGH = AllIcons.next();
    public static final AllIcons I_PRIORITY_VERY_HIGH = AllIcons.next();
    public static final AllIcons I_BLACKLIST = AllIcons.next();
    public static final AllIcons I_WHITELIST = AllIcons.next();
    public static final AllIcons I_WHITELIST_OR = AllIcons.next();
    public static final AllIcons I_WHITELIST_AND = AllIcons.next();
    public static final AllIcons I_WHITELIST_NOT = AllIcons.next();
    public static final AllIcons I_RESPECT_NBT = AllIcons.next();
    public static final AllIcons I_IGNORE_NBT = AllIcons.next();
    public static final AllIcons I_CONFIRM = AllIcons.newRow();
    public static final AllIcons I_NONE = AllIcons.next();
    public static final AllIcons I_OPEN_FOLDER = AllIcons.next();
    public static final AllIcons I_REFRESH = AllIcons.next();
    public static final AllIcons I_ACTIVE = AllIcons.next();
    public static final AllIcons I_PASSIVE = AllIcons.next();
    public static final AllIcons I_ROTATE_PLACE = AllIcons.next();
    public static final AllIcons I_ROTATE_PLACE_RETURNED = AllIcons.next();
    public static final AllIcons I_ROTATE_NEVER_PLACE = AllIcons.next();
    public static final AllIcons I_MOVE_PLACE = AllIcons.next();
    public static final AllIcons I_MOVE_PLACE_RETURNED = AllIcons.next();
    public static final AllIcons I_MOVE_NEVER_PLACE = AllIcons.next();
    public static final AllIcons I_CART_ROTATE = AllIcons.next();
    public static final AllIcons I_CART_ROTATE_PAUSED = AllIcons.next();
    public static final AllIcons I_CART_ROTATE_LOCKED = AllIcons.next();
    public static final AllIcons I_DONT_REPLACE = AllIcons.newRow();
    public static final AllIcons I_REPLACE_SOLID = AllIcons.next();
    public static final AllIcons I_REPLACE_ANY = AllIcons.next();
    public static final AllIcons I_REPLACE_EMPTY = AllIcons.next();
    public static final AllIcons I_CENTERED = AllIcons.next();
    public static final AllIcons I_ATTACHED = AllIcons.next();
    public static final AllIcons I_INSERTED = AllIcons.next();
    public static final AllIcons I_FILL = AllIcons.next();
    public static final AllIcons I_PLACE = AllIcons.next();
    public static final AllIcons I_REPLACE = AllIcons.next();
    public static final AllIcons I_CLEAR = AllIcons.next();
    public static final AllIcons I_OVERLAY = AllIcons.next();
    public static final AllIcons I_FLATTEN = AllIcons.next();
    public static final AllIcons I_LMB = AllIcons.next();
    public static final AllIcons I_SCROLL = AllIcons.next();
    public static final AllIcons I_RMB = AllIcons.next();
    public static final AllIcons I_TOOL_DEPLOY = AllIcons.newRow();
    public static final AllIcons I_SKIP_MISSING = AllIcons.next();
    public static final AllIcons I_SKIP_BLOCK_ENTITIES = AllIcons.next();
    public static final AllIcons I_DICE = AllIcons.next();
    public static final AllIcons I_TUNNEL_SPLIT = AllIcons.next();
    public static final AllIcons I_TUNNEL_FORCED_SPLIT = AllIcons.next();
    public static final AllIcons I_TUNNEL_ROUND_ROBIN = AllIcons.next();
    public static final AllIcons I_TUNNEL_FORCED_ROUND_ROBIN = AllIcons.next();
    public static final AllIcons I_TUNNEL_PREFER_NEAREST = AllIcons.next();
    public static final AllIcons I_TUNNEL_RANDOMIZE = AllIcons.next();
    public static final AllIcons I_TUNNEL_SYNCHRONIZE = AllIcons.next();
    public static final AllIcons I_TOOLBOX = AllIcons.next();
    public static final AllIcons I_VIEW_SCHEDULE = AllIcons.next();
    public static final AllIcons I_TOOL_MOVE_XZ = AllIcons.newRow();
    public static final AllIcons I_TOOL_MOVE_Y = AllIcons.next();
    public static final AllIcons I_TOOL_ROTATE = AllIcons.next();
    public static final AllIcons I_TOOL_MIRROR = AllIcons.next();
    public static final AllIcons I_ARM_ROUND_ROBIN = AllIcons.next();
    public static final AllIcons I_ARM_FORCED_ROUND_ROBIN = AllIcons.next();
    public static final AllIcons I_ARM_PREFER_FIRST = AllIcons.next();
    public static final AllIcons I_ADD_INVERTED_ATTRIBUTE = AllIcons.next();
    public static final AllIcons I_FLIP = AllIcons.next();
    public static final AllIcons I_ROLLER_PAVE = AllIcons.next();
    public static final AllIcons I_ROLLER_FILL = AllIcons.next();
    public static final AllIcons I_ROLLER_WIDE_FILL = AllIcons.next();
    public static final AllIcons I_PLAY = AllIcons.newRow();
    public static final AllIcons I_PAUSE = AllIcons.next();
    public static final AllIcons I_STOP = AllIcons.next();
    public static final AllIcons I_PLACEMENT_SETTINGS = AllIcons.next();
    public static final AllIcons I_ROTATE_CCW = AllIcons.next();
    public static final AllIcons I_HOUR_HAND_FIRST = AllIcons.next();
    public static final AllIcons I_MINUTE_HAND_FIRST = AllIcons.next();
    public static final AllIcons I_HOUR_HAND_FIRST_24 = AllIcons.next();
    public static final AllIcons I_PATTERN_SOLID = AllIcons.newRow();
    public static final AllIcons I_PATTERN_CHECKERED = AllIcons.next();
    public static final AllIcons I_PATTERN_CHECKERED_INVERSED = AllIcons.next();
    public static final AllIcons I_PATTERN_CHANCE_25 = AllIcons.next();
    public static final AllIcons I_PATTERN_CHANCE_50 = AllIcons.newRow();
    public static final AllIcons I_PATTERN_CHANCE_75 = AllIcons.next();
    public static final AllIcons I_FOLLOW_DIAGONAL = AllIcons.next();
    public static final AllIcons I_FOLLOW_MATERIAL = AllIcons.next();
    public static final AllIcons I_CLEAR_CHECKED = AllIcons.next();
    public static final AllIcons I_SCHEMATIC = AllIcons.newRow();
    public static final AllIcons I_SEQ_REPEAT = AllIcons.next();
    public static final AllIcons VALUE_BOX_HOVER_6PX = AllIcons.next();
    public static final AllIcons VALUE_BOX_HOVER_4PX = AllIcons.next();
    public static final AllIcons VALUE_BOX_HOVER_8PX = AllIcons.next();
    public static final AllIcons I_MTD_LEFT = AllIcons.newRow();
    public static final AllIcons I_MTD_CLOSE = AllIcons.next();
    public static final AllIcons I_MTD_RIGHT = AllIcons.next();
    public static final AllIcons I_MTD_SCAN = AllIcons.next();
    public static final AllIcons I_MTD_REPLAY = AllIcons.next();
    public static final AllIcons I_MTD_USER_MODE = AllIcons.next();
    public static final AllIcons I_MTD_SLOW_MODE = AllIcons.next();
    public static final AllIcons I_CONFIG_UNLOCKED = AllIcons.newRow();
    public static final AllIcons I_CONFIG_LOCKED = AllIcons.next();
    public static final AllIcons I_CONFIG_DISCARD = AllIcons.next();
    public static final AllIcons I_CONFIG_SAVE = AllIcons.next();
    public static final AllIcons I_CONFIG_RESET = AllIcons.next();
    public static final AllIcons I_CONFIG_BACK = AllIcons.next();
    public static final AllIcons I_CONFIG_PREV = AllIcons.next();
    public static final AllIcons I_CONFIG_NEXT = AllIcons.next();
    public static final AllIcons I_DISABLE = AllIcons.next();
    public static final AllIcons I_CONFIG_OPEN = AllIcons.next();
    public static final AllIcons I_FX_SURFACE_OFF = AllIcons.newRow();
    public static final AllIcons I_FX_SURFACE_ON = AllIcons.next();
    public static final AllIcons I_FX_FIELD_OFF = AllIcons.next();
    public static final AllIcons I_FX_FIELD_ON = AllIcons.next();
    public static final AllIcons I_FX_BLEND = AllIcons.next();
    public static final AllIcons I_FX_BLEND_OFF = AllIcons.next();
    public static final AllIcons I_SEND_ONLY = AllIcons.newRow();
    public static final AllIcons I_SEND_AND_RECEIVE = AllIcons.next();
    public static final AllIcons I_PARTIAL_REQUESTS = AllIcons.next();
    public static final AllIcons I_FULL_REQUESTS = AllIcons.next();
    public static final AllIcons I_MOVE_GAUGE = AllIcons.next();

    public AllIcons(int x, int y) {
        this.iconX = x * 16;
        this.iconY = y * 16;
    }

    private static AllIcons next() {
        return new AllIcons(++x, y);
    }

    private static AllIcons newRow() {
        x = 0;
        return new AllIcons(0, ++y);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)ICON_ATLAS);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(ICON_ATLAS, x, y, 0, (float)this.iconX, (float)this.iconY, 16, 16, 256, 256);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void render(PoseStack ms, MultiBufferSource buffer, int color) {
        VertexConsumer builder = buffer.getBuffer(RenderType.text((ResourceLocation)ICON_ATLAS));
        Matrix4f matrix = ms.last().pose();
        Color rgb = new Color(color);
        int light = 0xF000F0;
        Vec3 vec1 = new Vec3(0.0, 0.0, 0.0);
        Vec3 vec2 = new Vec3(0.0, 1.0, 0.0);
        Vec3 vec3 = new Vec3(1.0, 1.0, 0.0);
        Vec3 vec4 = new Vec3(1.0, 0.0, 0.0);
        float u1 = (float)this.iconX * 1.0f / 256.0f;
        float u2 = (float)(this.iconX + 16) * 1.0f / 256.0f;
        float v1 = (float)this.iconY * 1.0f / 256.0f;
        float v2 = (float)(this.iconY + 16) * 1.0f / 256.0f;
        this.vertex(builder, matrix, vec1, rgb, u1, v1, light);
        this.vertex(builder, matrix, vec2, rgb, u1, v2, light);
        this.vertex(builder, matrix, vec3, rgb, u2, v2, light);
        this.vertex(builder, matrix, vec4, rgb, u2, v1, light);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void vertex(VertexConsumer builder, Matrix4f matrix, Vec3 vec, Color rgb, float u, float v, int light) {
        builder.addVertex(matrix, (float)vec.x, (float)vec.y, (float)vec.z).setColor(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 255).setUv(u, v).setLight(light);
    }

    @OnlyIn(value=Dist.CLIENT)
    public DelegatedStencilElement asStencil() {
        return (DelegatedStencilElement)new DelegatedStencilElement().withStencilRenderer((ms, w, h, alpha) -> this.render(ms, 0, 0)).withBounds(16, 16);
    }
}
