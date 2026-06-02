/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$OverrideOnly
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.util.hold_interaction;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public abstract class BlockHoldInteraction
implements InteractCallback {
    private BlockPos interactionPos = null;

    @ApiStatus.OverrideOnly
    public void start() {
    }

    @ApiStatus.OverrideOnly
    public void stop() {
        this.interactionPos = null;
    }

    @ApiStatus.OverrideOnly
    public void release() {
    }

    public boolean isActive() {
        return HoldInteractionManager.isActive(this);
    }

    public boolean isBlockActive(BlockPos pos) {
        return this.isActive() && pos.equals((Object)this.interactionPos);
    }

    public void renderOverlay(GuiGraphics graphics, int width1, int height1, boolean hideGui) {
    }

    public static double getInteractionRange(Player player) {
        return player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
    }

    public static boolean inInteractionRange(Player player, Position target, double reachBuffer) {
        double distance = BlockHoldInteraction.getInteractionRange(player) + reachBuffer;
        Vec3 eyePosition = player.getEyePosition();
        return Sable.HELPER.projectOutOfSubLevel(player.level(), JOMLConversion.toJOML((Position)target)).distanceSquared(eyePosition.x, eyePosition.y, eyePosition.z) < distance * distance;
    }

    public static boolean inInteractionRange(Player player, Vector3dc target, double reachBuffer) {
        double distance = BlockHoldInteraction.getInteractionRange(player) + reachBuffer;
        Vec3 eyePosition = player.getEyePosition();
        return Sable.HELPER.projectOutOfSubLevel(player.level(), target, new Vector3d()).distanceSquared(eyePosition.x, eyePosition.y, eyePosition.z) < distance * distance;
    }

    public static boolean inInteractionRange(Player player, Position target) {
        return BlockHoldInteraction.inInteractionRange(player, target, 0.0);
    }

    public int getCrouchBlockingTicks() {
        return 0;
    }

    public BlockPos getInteractionPos() {
        return this.interactionPos;
    }

    public void startHold(Level level, Player player, BlockPos blockPos) {
        HoldInteractionManager.start(this);
        this.interactionPos = blockPos;
    }

    @Override
    public InteractCallback.Result onAttack(int modifiers, int action, KeyMapping leftKey) {
        if (this.isActive()) {
            return new InteractCallback.Result(true);
        }
        return InteractCallback.super.onAttack(modifiers, action, leftKey);
    }

    @Override
    public InteractCallback.Result onUse(int modifiers, int action, KeyMapping rightKey) {
        if (action == 0 && this.isActive()) {
            this.release();
            HoldInteractionManager.stop();
        }
        return InteractCallback.super.onUse(modifiers, action, rightKey);
    }

    public boolean activeTick(Level level, LocalPlayer player) {
        return false;
    }

    @Override
    public InteractCallback.Result onMouseMove(double yaw, double pitch) {
        if (this.isActive() && this.activeOnMouseMove(yaw, pitch)) {
            return new InteractCallback.Result(true);
        }
        return InteractCallback.super.onMouseMove(yaw, pitch);
    }

    public boolean activeOnMouseMove(double yaw, double pitch) {
        return false;
    }
}
