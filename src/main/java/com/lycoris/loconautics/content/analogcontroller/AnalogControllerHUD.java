package com.lycoris.loconautics.content.analogcontroller;

import com.lycoris.loconautics.core.LoconauticsConstants;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Renders the Analog Controller HUD overlay while the player is mounted.
 *
 * Mirrors TrainHUD's pattern exactly: reads state directly from the client-side
 * block entity each frame. No packets, no cached fields — same as TrainHUD reads
 * from carriage.train.throttle on the client entity.
 */

// TODO: tweak the lerped value for smooth HUD

public class AnalogControllerHUD {

    public static final LayeredDraw.Layer OVERLAY = AnalogControllerHUD::render;

    private static final ResourceLocation WIDGETS =
            ResourceLocation.fromNamespaceAndPath("create", "textures/gui/widgets.png");
    private static final ResourceLocation LOCK_ICON =
            LoconauticsConstants.id("textures/gui/lock_icon.png");

    // UV coords in 256×256 widgets.png (from Create's AllGuiTextures)
    private static final int SPEED_BG_W  = 182, SPEED_BG_H  = 5;
    private static final int SPEED_W     = 182, SPEED_H     = 5;
    private static final int THROTTLE_W  = 182, THROTTLE_H  = 5;
    private static final int PTR_W       = 6,   PTR_H       = 9;
    private static final int FRAME_W     = 186, FRAME_H     = 7;

    // Direction zone — where Create draws the compass arrow; we draw the number here
    private static final int DIR_ZONE_X  = 77, DIR_ZONE_W  = 28;

    // Lock icon size
    private static final int LOCK_W = 8, LOCK_H = 10;

    // Animated bar values — chase actual values each frame like TrainHUD
    private static final LerpedFloat displayedSpeed    = LerpedFloat.linear();
    private static final LerpedFloat displayedThrottle = LerpedFloat.linear();

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.options.hideGui) return;

        if (!AnalogControllerClientHandler.isControlling()) return;

        BlockPos pos = AnalogControllerClientHandler.getMountedPos();
        if (pos == null) return;
        if (mc.level == null) return;

        BlockEntity be = mc.level.getBlockEntity(pos);
        if (!(be instanceof AnalogControllerBlockEntity ace)) return;

        int power    = ace.getCurrentPower();
        boolean locked   = ace.isLocked();
        int maxPower = ace.getMaxPower();

        // Tick lerped values toward actual targets (same pattern as TrainHUD)
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        displayedSpeed.chase(power / 15.0, 0.5, LerpedFloat.Chaser.EXP);
        displayedSpeed.tickChaser();
        displayedThrottle.chase(maxPower / 15.0, 0.75, LerpedFloat.Chaser.EXP);
        displayedThrottle.tickChaser();
        float animSpeed    = displayedSpeed.getValue(partialTicks);
        float animThrottle = displayedThrottle.getValue(partialTicks);

        // Anchor: same as Create's TrainHUD
        int originX = graphics.guiWidth() / 2 - 91;
        int barY    = graphics.guiHeight() - 29;

        // 1. SPEED_BG — always full width, drawn first
        graphics.blit(WIDGETS, originX, barY, 0,
                0, 190, SPEED_BG_W, SPEED_BG_H, 256, 256);

        // 2. SPEED — fills from left proportional to animated speed
        int fillW = Math.round(animSpeed * SPEED_W);
        if (fillW > 0) {
            graphics.blit(WIDGETS, originX, barY, 0,
                    0, 185, fillW, SPEED_H, 256, 256);
        }

        // 3. THROTTLE — drawn from the RIGHT using animated throttle cap
        int throttleW = Math.round((1.0f - animThrottle) * THROTTLE_W);
        int invW      = THROTTLE_W - throttleW;
        if (throttleW > 0) {
            graphics.blit(WIDGETS, originX + invW, barY, 0,
                    invW, 195, throttleW, THROTTLE_H, 256, 256);
        }

        // 4. FRAME — drawn before pointer so pointer renders on top
        graphics.blit(WIDGETS, originX - 2, barY + SPEED_BG_H - 4, 0,
                0, 200, FRAME_W, FRAME_H, 256, 256);

        // 5. THROTTLE_PTR — sits at the cap boundary, above the frame, below the lock
        int ptrX = originX + Math.max(1, invW) - 3;
        graphics.blit(WIDGETS, ptrX, barY - 2, 0,
                0, 209, PTR_W, PTR_H, 256, 256);

        // Direction zone: Create draws at (77, -20) relative to the bar origin.
        // In absolute coords that is originX + 77, barY - 20.
        // The zone is 28px wide; we center our content within it.
        int dirZoneAbsX = originX + DIR_ZONE_X;   // left edge of the 28px zone
        int dirZoneCtrY = barY - 10;               // vertical center: zone is 20px tall starting at barY-20, center = barY-10

        // Blit DIRECTION background (the brass circle) — same call Create makes
        graphics.blit(WIDGETS, dirZoneAbsX, barY - 20, 0,
                77, 165, 28, 20, 256, 256);

        // Power number — centered over the background
        String powerText = String.valueOf(power);
        int textW = mc.font.width(powerText);
        int textX = dirZoneAbsX + (DIR_ZONE_W - textW) / 2;
        int textY = dirZoneCtrY - mc.font.lineHeight / 2 + 4;
        graphics.drawString(mc.font, powerText, textX, textY, 0xFFFFFF, true);

        // Lock icon centered on the speed bar
        if (locked) {
            int iconX = originX + (SPEED_BG_W - LOCK_W) / 2;
            int iconY = barY + (SPEED_BG_H - LOCK_H) / 2;
            graphics.blit(LOCK_ICON, iconX, iconY, 0, 0, LOCK_W, LOCK_H, LOCK_W, LOCK_H);
        }
    }
}