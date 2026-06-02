/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler
 *  com.simibubi.create.foundation.particle.AirParticleData
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.items.plunger_launcher;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import com.simibubi.create.foundation.particle.AirParticleData;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerEntityRenderer;
import dev.simulated_team.simulated.index.SimItems;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public static class PlungerLauncherItemRenderer.RenderHandler
extends ShootableGadgetRenderHandler {
    public void basicShoot(InteractionHand hand) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            boolean rightHand = hand == InteractionHand.MAIN_HAND ^ player.getMainArm() == HumanoidArm.LEFT;
            if (rightHand) {
                this.rightHandAnimation = 0.2f;
                this.dontReequipRight = false;
            } else {
                this.leftHandAnimation = 0.2f;
                this.dontReequipLeft = false;
            }
            Vec3 focusPos1 = LaunchedPlungerEntityRenderer.getFirstPersonFocusPos(0.0f);
            int i = 0;
            while ((double)i < Math.random() * 4.0) {
                Vec3 m2 = VecHelper.offsetRandomly((Vec3)player.getViewVector(0.0f), (RandomSource)player.level().random, (float)0.5f);
                player.level().addParticle((ParticleOptions)new AirParticleData(1.0f, 0.25f), focusPos1.x, focusPos1.y, focusPos1.z, m2.x, m2.y, m2.z);
                ++i;
            }
            this.playSound(hand, player.position());
        }
    }

    public void playSound(InteractionHand hand, Vec3 position) {
    }

    protected boolean appliesTo(ItemStack stack) {
        return SimItems.PLUNGER_LAUNCHER.is(stack.getItem());
    }

    protected void transformTool(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.translate(flip * -0.1f, 0.05f, 0.14f);
        TransformStack.of((PoseStack)ms).rotateXDegrees(recoil * 80.0f);
    }

    protected void transformHand(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.scale(0.0f, 0.0f, 0.0f);
    }
}
