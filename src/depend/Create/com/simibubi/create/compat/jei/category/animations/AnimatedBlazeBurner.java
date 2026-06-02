/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AnimatedBlazeBurner
extends AnimatedKinetics {
    private BlazeBurnerBlock.HeatLevel heatLevel;

    public AnimatedBlazeBurner withHeat(BlazeBurnerBlock.HeatLevel heatLevel) {
        this.heatLevel = heatLevel;
        return this;
    }

    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 200.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 23;
        float offset = (Mth.sin((float)(AnimationTickHolder.getRenderTime() / 16.0f)) + 0.5f) / 16.0f;
        this.blockElement(AllBlocks.BLAZE_BURNER.getDefaultState()).atLocal(0.0, 1.65, 0.0).scale((double)scale).render(graphics);
        PartialModel blaze = this.heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_SUPER : AllPartialModels.BLAZE_ACTIVE;
        PartialModel rods2 = this.heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS_2 : AllPartialModels.BLAZE_BURNER_RODS_2;
        this.blockElement(blaze).atLocal(1.0, 1.8, 1.0).rotate(0.0, 180.0, 0.0).scale((double)scale).render(graphics);
        this.blockElement(rods2).atLocal(1.0, 1.7 + (double)offset, 1.0).rotate(0.0, 180.0, 0.0).scale((double)scale).render(graphics);
        matrixStack.scale((float)scale, (float)(-scale), (float)scale);
        matrixStack.translate(0.0, -1.8, 0.0);
        SpriteShiftEntry spriteShift = this.heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllSpriteShifts.SUPER_BURNER_FLAME : AllSpriteShifts.BURNER_FLAME;
        float spriteWidth = spriteShift.getTarget().getU1() - spriteShift.getTarget().getU0();
        float spriteHeight = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)Minecraft.getInstance().level);
        float speed = 0.03125f + 0.015625f * (float)this.heatLevel.ordinal();
        double vScroll = speed * time;
        vScroll -= Math.floor(vScroll);
        vScroll = vScroll * (double)spriteHeight / 2.0;
        double uScroll = speed * time / 2.0f;
        uScroll -= Math.floor(uScroll);
        uScroll = uScroll * (double)spriteWidth / 2.0;
        CachedBuffers.partial((PartialModel)AllPartialModels.BLAZE_BURNER_FLAME, (BlockState)Blocks.AIR.defaultBlockState()).shiftUVScrolling(spriteShift, (float)uScroll, (float)vScroll).light(0xF000F0).renderInto(matrixStack, graphics.bufferSource().getBuffer(RenderType.cutoutMipped()));
        matrixStack.popPose();
    }
}
