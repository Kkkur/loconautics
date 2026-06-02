/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler
 *  com.simibubi.create.foundation.item.render.CustomRenderedItemModel
 *  com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer
 *  com.simibubi.create.foundation.item.render.PartialItemModelRenderer
 *  com.simibubi.create.foundation.particle.AirParticleData
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.item.ItemCooldowns
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 */
package dev.simulated_team.simulated.content.items.plunger_launcher;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.particle.AirParticleData;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerEntity;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerEntityRenderer;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItem;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.mixin_interface.PlayerLaunchedPlungerExtension;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class PlungerLauncherItemRenderer
extends CustomRenderedItemModelRenderer {
    public static final Vector3d focusPos = new Vector3d();
    public static final Matrix4f itemProjMat = new Matrix4f();

    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.scale(0.8f, 0.8f, 0.8f);
        ms.translate(0.0f, 0.0f, 0.15f);
        renderer.render(model.getOriginalModel(), light);
        LocalPlayer player = Minecraft.getInstance().player;
        DeltaTracker timer = Minecraft.getInstance().getTimer();
        float partialTicks = timer.getGameTimeDeltaPartialTick(false);
        PlayerLaunchedPlungerExtension duck = (PlayerLaunchedPlungerExtension)player;
        LaunchedPlungerEntity plunger = duck.simulated$getLaunchedPlunger();
        if (player.getCooldowns().getCooldownPercent(stack.getItem(), partialTicks) <= 0.6f || plunger != null && plunger.getOther() == null) {
            if ((plunger == null || plunger.isRemoved() || plunger.getOther() != null) && player.getCooldowns().getCooldownPercent(stack.getItem(), partialTicks) <= 0.4f) {
                this.renderPlunger(ms, buffer, light, true);
            }
            this.renderPlunger(ms, buffer, light, false);
        }
        ms.translate(0.125f, -0.0625f, -0.3125f);
        ms.translate(0.0f, 0.0f, 0.0625f);
        if (transformType.firstPerson()) {
            Vector3f focusPoint = new Vector3f();
            ms.last().pose().transformPosition(focusPoint);
            itemProjMat.set((Matrix4fc)RenderSystem.getProjectionMatrix());
            focusPos.set((double)focusPoint.x, (double)focusPoint.y, (double)focusPoint.z);
        }
    }

    private void renderPlunger(PoseStack ms, MultiBufferSource buffer, int light, boolean first) {
        ms.pushPose();
        SuperByteBuffer body = CachedBuffers.partial((PartialModel)SimPartialModels.LAUNCHED_PLUNGER_BODY, (BlockState)Blocks.AIR.defaultBlockState());
        SuperByteBuffer spool = CachedBuffers.partial((PartialModel)SimPartialModels.LAUNCHED_PLUNGER_SPOOL, (BlockState)Blocks.AIR.defaultBlockState());
        SuperByteBuffer joint = CachedBuffers.partial((PartialModel)SimPartialModels.LAUNCHED_PLUNGER_JOINT, (BlockState)Blocks.AIR.defaultBlockState());
        ms.translate(0.125f * (float)(first ? -1 : 1), -0.0625f, -0.3125f);
        DeltaTracker timer = Minecraft.getInstance().getTimer();
        float partialTicks = timer.getGameTimeDeltaPartialTick(false);
        ItemCooldowns cooldowns = Minecraft.getInstance().player.getCooldowns();
        float cooldown = cooldowns.getCooldownPercent(SimItems.PLUNGER_LAUNCHER.asItem(), partialTicks);
        if (cooldown > 0.0f && PlungerLauncherItem.reloadCooldown) {
            if (!first) {
                float slideIn = Mth.clamp((float)Mth.map((float)cooldown, (float)0.3f, (float)0.6f, (float)0.0f, (float)1.0f), (float)0.0f, (float)1.0f);
                slideIn = (float)Math.pow(slideIn, 3.0);
                ms.translate(0.0f, 0.0f, -slideIn / 12.0f);
            } else {
                float slideIn = Mth.clamp((float)Mth.map((float)cooldown, (float)0.1f, (float)0.4f, (float)0.0f, (float)1.0f), (float)0.0f, (float)1.0f);
                slideIn = (float)Math.pow(slideIn, 3.0);
                ms.translate(0.0f, 0.0f, -slideIn / 12.0f);
            }
        }
        body.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        joint.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        ms.translate(0.0f, 0.0f, 0.1875f);
        spool.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        ms.popPose();
    }

    public static class RenderHandler
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
}
