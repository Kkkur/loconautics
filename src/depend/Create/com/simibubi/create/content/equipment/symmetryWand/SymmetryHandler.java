/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent$Stage
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.event.level.BlockEvent$BreakEvent
 *  net.neoforged.neoforge.event.level.BlockEvent$EntityPlaceEvent
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.equipment.symmetryWand;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.equipment.symmetryWand.mirror.EmptyMirror;
import com.simibubi.create.content.equipment.symmetryWand.mirror.SymmetryMirror;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.joml.Vector3f;

@EventBusSubscriber
public class SymmetryHandler {
    private static int tickCounter = 0;

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        Inventory inv = player.getInventory();
        for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
            if (!AllItems.WAND_OF_SYMMETRY.isIn(inv.getItem(i))) continue;
            SymmetryWandItem.apply(player.level(), inv.getItem(i), player, event.getPos(), event.getPlacedBlock());
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onBlockDestroyed(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Player player = event.getPlayer();
        Inventory inv = player.getInventory();
        for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
            if (!AllItems.WAND_OF_SYMMETRY.isIn(inv.getItem(i))) continue;
            SymmetryWandItem.remove(player.level(), inv.getItem(i), player, event.getPos());
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        RandomSource random = RandomSource.create();
        for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
            SymmetryMirror mirror;
            ItemStack stackInSlot = player.getInventory().getItem(i);
            if (!AllItems.WAND_OF_SYMMETRY.isIn(stackInSlot) || !SymmetryWandItem.isEnabled(stackInSlot) || (mirror = SymmetryWandItem.getMirror(stackInSlot)) instanceof EmptyMirror) continue;
            BlockPos pos = BlockPos.containing((Position)mirror.getPosition());
            float yShift = 0.0f;
            double speed = 0.0625;
            yShift = Mth.sin((float)((float)((double)AnimationTickHolder.getRenderTime() * speed))) / 5.0f;
            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
            Camera info = mc.gameRenderer.getMainCamera();
            Vec3 view = info.getPosition();
            PoseStack ms = event.getPoseStack();
            ms.pushPose();
            ms.translate((double)pos.getX() - view.x(), (double)pos.getY() - view.y(), (double)pos.getZ() - view.z());
            ms.translate(0.0f, yShift + 0.2f, 0.0f);
            mirror.applyModelTransform(ms);
            BakedModel model = mirror.getModel().get();
            VertexConsumer builder = buffer.getBuffer(RenderType.solid());
            mc.getBlockRenderer().getModelRenderer().tesselateBlock((BlockAndTintGetter)player.level(), model, Blocks.AIR.defaultBlockState(), pos, ms, builder, true, random, Mth.getSeed((Vec3i)pos), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
            ms.popPose();
            buffer.endBatch();
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.level == null) {
            return;
        }
        if (mc.isPaused()) {
            return;
        }
        if (++tickCounter % 10 == 0) {
            for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
                SymmetryMirror mirror;
                ItemStack stackInSlot = player.getInventory().getItem(i);
                if (stackInSlot == null || !AllItems.WAND_OF_SYMMETRY.isIn(stackInSlot) || !SymmetryWandItem.isEnabled(stackInSlot) || (mirror = SymmetryWandItem.getMirror(stackInSlot)) instanceof EmptyMirror) continue;
                RandomSource random = mc.level.random;
                double offsetX = (random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (random.nextDouble() - 0.5) * 0.3;
                Vec3 pos = mirror.getPosition().add(0.5 + offsetX, 0.25, 0.5 + offsetZ);
                Vec3 speed = new Vec3(0.0, random.nextDouble() * 1.0 / 8.0, 0.0);
                mc.level.addParticle((ParticleOptions)ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
            }
        }
    }

    public static void drawEffect(BlockPos from, BlockPos to) {
        Vec3 pos;
        ClientLevel level = Minecraft.getInstance().level;
        RandomSource random = level.random;
        double density = 0.8f;
        Vec3 start = Vec3.atLowerCornerOf((Vec3i)from).add(0.5, 0.5, 0.5);
        Vec3 end = Vec3.atLowerCornerOf((Vec3i)to).add(0.5, 0.5, 0.5);
        Vec3 diff = end.subtract(start);
        Vec3 step = diff.normalize().scale(density);
        int steps = (int)(diff.length() / step.length());
        for (int i = 3; i < steps - 1; ++i) {
            pos = start.add(step.scale((double)i));
            Vec3 speed = new Vec3(0.0, random.nextDouble() * -40.0, 0.0);
            level.addParticle((ParticleOptions)new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f), pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
        }
        Vec3 speed = new Vec3(0.0, random.nextDouble() * 1.0 / 32.0, 0.0);
        pos = start.add(step.scale(2.0));
        level.addParticle((ParticleOptions)ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
        speed = new Vec3(0.0, random.nextDouble() * 1.0 / 32.0, 0.0);
        pos = start.add(step.scale((double)steps));
        level.addParticle((ParticleOptions)ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
    }
}
