/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.world.entity.player.Player
 */
package dev.simulated_team.simulated.content.blocks.handle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;

public class PlayerHoldingHandleRenderer {
    private static final Set<UUID> holdingPlayers = new HashSet<UUID>();

    public static void updatePlayerList(Collection<UUID> uuids) {
        holdingPlayers.clear();
        holdingPlayers.addAll(uuids);
    }

    public static void afterSetupAnim(Player player, HumanoidModel<?> model) {
        if (holdingPlayers.contains(player.getUUID())) {
            PlayerHoldingHandleRenderer.setHangingPose(model);
        }
    }

    private static void setHangingPose(HumanoidModel<?> model) {
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        model.leftArm.zRot = 0.0f;
        model.leftArm.zRot = 0.0f;
        model.leftArm.xRot = (float)Math.toRadians(-80.0) + model.head.xRot;
        model.rightArm.xRot = (float)Math.toRadians(-80.0) + model.head.xRot;
        model.rightArm.yRot = (float)Math.toRadians(-15.0);
        model.leftArm.yRot = (float)Math.toRadians(15.0);
    }
}
