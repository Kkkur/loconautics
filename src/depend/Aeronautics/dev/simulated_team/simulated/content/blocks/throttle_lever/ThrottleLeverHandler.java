/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  com.simibubi.create.foundation.gui.AllGuiTextures
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.TextureSheetSegment
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.throttle_lever;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlock;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import dev.simulated_team.simulated.network.packets.ThrottleLeverSignalPacket;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;

public class ThrottleLeverHandler
extends BlockHoldInteraction {
    protected boolean inverted = false;
    protected int lastSignal = 0;
    protected int signal = 0;
    protected float value = 0.0f;
    protected float animatedValue;
    protected float lastAnimatedValue;

    @Override
    public void startHold(Level level, Player player, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ThrottleLeverBlockEntity) {
            ThrottleLeverBlockEntity be = (ThrottleLeverBlockEntity)blockEntity;
            this.lastSignal = be.state;
            this.inverted = (Boolean)be.getBlockState().getValue((Property)ThrottleLeverBlock.INVERTED);
            this.signal = this.inverted ? 15 - be.state : be.state;
            this.animatedValue = this.lastAnimatedValue = (this.value = (float)this.signal / 15.0f);
        }
        super.startHold(level, player, pos);
    }

    @Override
    public boolean activeTick(Level level, LocalPlayer player) {
        if (level.getBlockEntity(this.getInteractionPos()) instanceof ThrottleLeverBlockEntity && BlockHoldInteraction.inInteractionRange((Player)player, (Position)this.getInteractionPos().getCenter(), 0.0)) {
            float speed = 0.85f;
            this.lastAnimatedValue = this.animatedValue;
            this.animatedValue = this.animatedValue * 0.14999998f + (float)this.signal / 15.0f * 0.85f;
            return false;
        }
        return true;
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, int width, int height, boolean hideGui) {
        if (hideGui) {
            return;
        }
        int h = 14;
        int w = 100;
        int x = width / 2 - 50 + 16;
        int y = height / 2 - 7;
        PoseStack ps = graphics.pose();
        ps.pushPose();
        ps.translate((float)(x + 50), (float)(y + 7), 0.0f);
        ps.mulPose(Axis.ZP.rotationDegrees(90.0f));
        ps.translate((float)(-x - 50), (float)(-y - 7), 0.0f);
        AllGuiTextures.BRASS_FRAME_TL.render(graphics, x, y);
        AllGuiTextures.BRASS_FRAME_TR.render(graphics, x + 100 - 4, y);
        AllGuiTextures.BRASS_FRAME_BL.render(graphics, x, y + 14 - 4);
        AllGuiTextures.BRASS_FRAME_BR.render(graphics, x + 100 - 4, y + 14 - 4);
        int zLevel = 2;
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)x, (int)(y + 4), (int)3, (int)6, (int)2, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_LEFT);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(x + 100 - 3), (int)(y + 4), (int)3, (int)6, (int)2, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_RIGHT);
        UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)(x + 4), (int)y, (int)92, (int)3, (int)2, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_TOP);
        UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)(x + 4), (int)(y + 14 - 3), (int)92, (int)3, (int)2, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_BOTTOM);
        int valueBarX = x + 3;
        int valueBarWidth = 94;
        for (int w1 = 0; w1 < 94; w1 += AllGuiTextures.VALUE_SETTINGS_BAR.getWidth() - 1) {
            UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)(valueBarX + w1), (int)(y + 3), (int)Math.min(AllGuiTextures.VALUE_SETTINGS_BAR.getWidth() - 1, 94 - w1), (int)8, (int)2, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_BAR);
        }
        ps.popPose();
        ps.pushPose();
        ps.translate(0.0, 0.0, 4.0);
        float partialTick = AnimationTickHolder.getPartialTicks();
        float currentValue = this.lastAnimatedValue * (1.0f - partialTick) + this.animatedValue * partialTick;
        float cursorY = (1.0f - 2.0f * currentValue) * 3.0f * 14.0f + 2.0f;
        int cx = x + 50 - 7;
        float cy = (float)(y + 7 - 9) + cursorY;
        int cursorWidth = 14;
        ps.pushPose();
        ps.translate(0.0f, cy, 0.0f);
        AllGuiTextures.VALUE_SETTINGS_CURSOR_LEFT.render(graphics, cx - 3, 0);
        UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)cx, (int)0, (int)14, (int)14, (int)2, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_CURSOR);
        AllGuiTextures.VALUE_SETTINGS_CURSOR_RIGHT.render(graphics, cx + 14, 0);
        ps.translate(0.0, 0.0, 4.0);
        graphics.drawString(Minecraft.getInstance().font, String.valueOf(this.inverted ? 15 - this.signal : this.signal), cx + 1, 3, SimColors.THROTTLE_VALUE_BROWN, false);
        ps.popPose();
        ps.popPose();
    }

    @Override
    public boolean activeOnMouseMove(double yaw, double pitch) {
        this.value -= (float)(pitch / 180.0);
        this.value = Math.min(1.0f, Math.max(0.0f, this.value));
        int newSignal = Math.round(this.value * 15.0f);
        this.signal = Math.min(15, Math.max(0, newSignal));
        if (this.signal != this.lastSignal) {
            this.lastSignal = this.signal;
            this.changed();
        }
        return true;
    }

    private void changed() {
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new ThrottleLeverSignalPacket(this.getInteractionPos(), this.signal)});
    }
}
