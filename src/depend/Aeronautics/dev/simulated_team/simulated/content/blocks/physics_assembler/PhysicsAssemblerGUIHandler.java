/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.util.SableDistUtil
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.physics_assembler;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.util.SableDistUtil;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.network.packets.AssemblePacket;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PhysicsAssemblerGUIHandler
extends BlockHoldInteraction {
    private static final double PULLED_THRESHOLD = 0.015;
    public static int lastSignal = 0;
    public static float animatedVelocity;
    public static float animatedValue;
    public static float lastAnimatedValue;

    @Override
    public void startHold(Level level, Player player, BlockPos blockPos) {
        super.startHold(level, player, blockPos);
        BlockEntity be = level.getBlockEntity(blockPos);
        if (!(be instanceof PhysicsAssemblerBlockEntity)) {
            return;
        }
        PhysicsAssemblerBlockEntity assembler = (PhysicsAssemblerBlockEntity)be;
        animatedValue = 0.0f;
        if (Sable.HELPER.getContaining((BlockEntity)assembler) != null) {
            animatedValue = 1.0f;
        }
        animatedVelocity = 0.0f;
    }

    @Override
    public void release() {
        BlockEntity blockEntity = SableDistUtil.getClientLevel().getBlockEntity(this.getInteractionPos());
        if (blockEntity instanceof PhysicsAssemblerBlockEntity) {
            boolean inPlot;
            PhysicsAssemblerBlockEntity be = (PhysicsAssemblerBlockEntity)blockEntity;
            if (be.holdingLever) {
                return;
            }
            boolean bl = inPlot = this.getSubLevelHolding() != null;
            if (inPlot && (double)animatedValue < 0.015 && (double)lastAnimatedValue < 0.015 || !inPlot && (double)lastAnimatedValue > 0.985 && (double)animatedValue > 0.985) {
                VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new AssemblePacket(this.getInteractionPos())});
                inPlot = !inPlot;
                be.setClientHoldLeverInPlace(true);
            }
            be.visualAngle.setValue((double)animatedValue * 45.0);
            be.clientFlickLeverTo(inPlot);
            be.stopControllingPlayer();
        }
    }

    @Override
    public boolean activeTick(Level level, LocalPlayer player) {
        if (level == null) {
            return true;
        }
        BlockEntity blockEntity = level.getBlockEntity(this.getInteractionPos());
        if (blockEntity instanceof PhysicsAssemblerBlockEntity) {
            PhysicsAssemblerBlockEntity be = (PhysicsAssemblerBlockEntity)blockEntity;
            if (BlockHoldInteraction.inInteractionRange((Player)player, (Position)this.getInteractionPos().getCenter(), 2.0)) {
                lastAnimatedValue = animatedValue;
                animatedValue += animatedVelocity;
                animatedVelocity *= 0.8f;
                be.updateControlledByPlayer(animatedValue * 45.0f);
                return false;
            }
            boolean inPlot = this.getSubLevelHolding() != null;
            be.visualAngle.setValue((double)animatedValue * 45.0);
            be.clientFlickLeverTo(inPlot);
            be.stopControllingPlayer();
            return true;
        }
        return true;
    }

    @Override
    public boolean activeOnMouseMove(double yaw, double pitch) {
        double scalar = 0.5 - Math.abs(0.5 - (double)animatedValue) + 0.05;
        PhysicsAssemblerBlockEntity be = (PhysicsAssemblerBlockEntity)SableDistUtil.getClientLevel().getBlockEntity(this.getInteractionPos());
        if (be == null) {
            return false;
        }
        if ((double)(animatedValue -= (float)(pitch / 80.0 * scalar)) > 1.0) {
            animatedValue = 1.0f;
        } else if ((double)animatedValue < 0.0) {
            animatedValue = 0.0f;
            animatedVelocity = 0.0f;
        }
        int signal = Math.round(animatedValue * 4.0f);
        if (signal != lastSignal) {
            lastSignal = signal;
            if ((float)signal == 0.0f || (float)signal == 4.0f) {
                SimSoundEvents.ASSEMBLER_SHIFT.playAt((Level)Minecraft.getInstance().level, (Vec3i)this.getInteractionPos(), 0.5f, 0.8f + animatedValue * 0.3f, false);
            } else {
                SimSoundEvents.ASSEMBLER_TICK.playAt((Level)Minecraft.getInstance().level, (Vec3i)this.getInteractionPos(), 0.5f, 0.8f + animatedValue * 0.3f, false);
            }
        }
        return true;
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, int width1, int height1, boolean hideGui) {
        if (hideGui) {
            return;
        }
        PoseStack ps = graphics.pose();
        ps.pushPose();
        ps.translate((float)(graphics.guiWidth() / 2), (float)(graphics.guiHeight() / 2), 0.0f);
        int height = 72;
        ps.translate(10.0f, -36.0f, 0.0f);
        graphics.blit(SimGUITextures.ASSEMBLER_TRACK_START.location, 0, 0, 0.0f, 0.0f, 14, 6, 32, 32);
        ps.translate(0.0f, 6.0f, 0.0f);
        for (int c = 0; c < 6; ++c) {
            graphics.blit(SimGUITextures.ASSEMBLER_TRACK_MIDDLE.location, 0, 0, 0.0f, 7.0f, 14, 10, 32, 32);
            ps.translate(0.0f, 10.0f, 0.0f);
        }
        graphics.blit(SimGUITextures.ASSEMBLER_TRACK_END.location, 0, 0, 0.0f, 18.0f, 14, 6, 32, 32);
        float value = Mth.lerp((float)AnimationTickHolder.getPartialTicks(), (float)lastAnimatedValue, (float)animatedValue);
        ps.translate(-2.0f, -12.0f - 51.0f * value, 0.0f);
        graphics.blit(SimGUITextures.ASSEMBLER_TRACK_MIDDLE.location, 0, 0, 14.0f, 0.0f, 18, 14, 32, 32);
        ps.popPose();
    }

    public ClientSubLevel getSubLevelHolding() {
        return Sable.HELPER.getContainingClient((Vec3i)this.getInteractionPos());
    }

    public double getFraction() {
        return animatedValue;
    }
}
