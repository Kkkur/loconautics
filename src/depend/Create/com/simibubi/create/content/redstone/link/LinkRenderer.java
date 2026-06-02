/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.datafixers.util.Pair
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outline
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.redstone.link;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LinkRenderer {
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        HitResult target = mc.hitResult;
        if (target == null || !(target instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult result = (BlockHitResult)target;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        LinkBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, pos, LinkBehaviour.TYPE);
        if (behaviour == null) {
            return;
        }
        MutableComponent freq1 = CreateLang.translateDirect("logistics.firstFrequency", new Object[0]);
        MutableComponent freq2 = CreateLang.translateDirect("logistics.secondFrequency", new Object[0]);
        for (boolean first : Iterate.trueAndFalse) {
            AABB bb = new AABB(Vec3.ZERO, Vec3.ZERO).inflate(0.25);
            MutableComponent label = first ? freq1 : freq2;
            boolean hit = behaviour.testHit(first, target.getLocation());
            ValueBoxTransform transform = first ? behaviour.firstSlot : behaviour.secondSlot;
            ValueBox box = new ValueBox((Component)label, bb, pos).passive(!hit);
            boolean empty = ((RedstoneLinkNetworkHandler.Frequency)behaviour.getNetworkKey().get(first)).getStack().isEmpty();
            if (!empty) {
                box.wideOutline();
            }
            Outliner.getInstance().showOutline((Object)Pair.of((Object)first, (Object)pos), (Outline)box.transform(transform)).highlightFace(result.getDirection());
            if (!hit) continue;
            ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
            tip.add(label.copy());
            tip.add(CreateLang.translateDirect(empty ? "logistics.filter.click_to_set" : "logistics.filter.click_to_replace", new Object[0]));
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
        }
    }

    public static void renderOnBlockEntity(SmartBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be == null || be.isRemoved()) {
            return;
        }
        Entity cameraEntity = Minecraft.getInstance().cameraEntity;
        float max = AllConfigs.client().filterItemRenderDistance.getF();
        if (!be.isVirtual() && cameraEntity != null && cameraEntity.position().distanceToSqr(VecHelper.getCenterOf((Vec3i)be.getBlockPos())) > (double)(max * max)) {
            return;
        }
        LinkBehaviour behaviour = be.getBehaviour(LinkBehaviour.TYPE);
        if (behaviour == null) {
            return;
        }
        for (boolean first : Iterate.trueAndFalse) {
            ValueBoxTransform transform = first ? behaviour.firstSlot : behaviour.secondSlot;
            ItemStack stack = first ? behaviour.frequencyFirst.getStack() : behaviour.frequencyLast.getStack();
            ms.pushPose();
            transform.transform((LevelAccessor)be.getLevel(), be.getBlockPos(), be.getBlockState(), ms);
            ValueBoxRenderer.renderItemIntoValueBox(stack, ms, buffer, light, overlay);
            ms.popPose();
        }
    }
}
