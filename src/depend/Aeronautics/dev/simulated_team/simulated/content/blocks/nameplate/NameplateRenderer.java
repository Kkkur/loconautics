/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.nameplate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateBlock;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class NameplateRenderer
extends SafeBlockEntityRenderer<NameplateBlockEntity> {
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square((int)16);
    private final BlockEntityRendererProvider.Context context;

    public NameplateRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    public void renderSafe(NameplateBlockEntity be, float pPartialTick, PoseStack ps, MultiBufferSource pBuffer, int packedLight, int pPackedOverlay) {
        boolean glowing;
        int textColor;
        Font font = this.context.getFont();
        BlockState state = be.getBlockState();
        Direction facing = (Direction)state.getValue((Property)NameplateBlock.FACING);
        NameplateBlock.Position pos = (NameplateBlock.Position)((Object)state.getValue(NameplateBlock.POSITION));
        if (pos != NameplateBlock.Position.LEFT && pos != NameplateBlock.Position.SINGLE) {
            return;
        }
        ps.pushPose();
        ps.translate(0.5, 0.5, 0.5);
        ps.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() + 180.0f));
        ps.translate(-0.5, -0.5, -0.5);
        ps.translate(1.0, 1.0, 1.0);
        ps.translate(0.0, 0.0, -0.253125);
        int pixelsTall = be.glowing ? 5 : 6;
        int pixelsLeft = 3;
        ps.translate(-0.1875, -(16.0 - (double)pixelsTall) / 16.0 / 2.0, 0.0);
        ps.scale((float)((double)pixelsTall / 16.0), (float)((double)pixelsTall / 16.0), (float)((double)pixelsTall / 16.0));
        ps.scale(0.14285715f, 0.14285715f, 0.14285715f);
        ps.mulPose(Axis.ZP.rotationDegrees(180.0f));
        int availableSpace = (be.getControllerWidth() * 16 - 6) * 7 / pixelsTall + 1;
        String trimmed = font.plainSubstrByWidth(be.getName(), availableSpace);
        int width = font.width(trimmed);
        double centerPixels = (double)(availableSpace - 1) / 2.0 - (double)width / 2.0;
        ps.translate(centerPixels, 0.0, 0.0);
        MutableComponent textComponent = SimLang.text(trimmed).component();
        List sequences = font.split((FormattedText)textComponent, width);
        if (be.glowing) {
            textColor = be.getTextColor().getTextColor();
            glowing = NameplateRenderer.isOutlineVisible(be.getBlockPos(), textColor);
            packedLight = 0xF000F0;
        } else {
            textColor = be.getDarkColor(be.getTextColor());
            glowing = false;
        }
        for (FormattedCharSequence sequence : sequences) {
            if (glowing) {
                font.drawInBatch8xOutline(sequence, 0.0f, 0.0f, textColor, be.getDarkColor(be.getTextColor()), ps.last().pose(), pBuffer, packedLight);
                continue;
            }
            font.drawInBatch(sequence, 0.0f, 0.0f, textColor, false, ps.last().pose(), pBuffer, Font.DisplayMode.NORMAL, 0, packedLight);
        }
        ps.popPose();
    }

    private static boolean isOutlineVisible(BlockPos blockPos, int i) {
        if (i == DyeColor.BLACK.getTextColor()) {
            return true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer != null && minecraft.options.getCameraType().isFirstPerson() && localPlayer.isScoping()) {
            return true;
        }
        Entity entity = minecraft.getCameraEntity();
        return entity != null && entity.distanceToSqr(Vec3.atCenterOf((Vec3i)blockPos)) < (double)OUTLINE_RENDER_DISTANCE;
    }
}
