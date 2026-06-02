/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outline
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.blockEntity.behaviour.filtering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import java.util.ArrayList;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FilteringRenderer {
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        HitResult target = mc.hitResult;
        if (!(target instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult result = (BlockHitResult)target;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (mc.player.isShiftKeyDown()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity sbe = (SmartBlockEntity)blockEntity;
        ItemStack mainhandItem = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
        for (BlockEntityBehaviour b : sbe.getAllBehaviours()) {
            SidedFilteringBehaviour sidedFilteringBehaviour;
            FilteringBehaviour behaviour;
            if (!(b instanceof FilteringBehaviour) || (behaviour = (FilteringBehaviour)b) instanceof SidedFilteringBehaviour && (behaviour = (sidedFilteringBehaviour = (SidedFilteringBehaviour)behaviour).get(result.getDirection())) == null || !behaviour.isActive()) continue;
            if (behaviour.slotPositioning instanceof ValueBoxTransform.Sided) {
                ((ValueBoxTransform.Sided)behaviour.slotPositioning).fromSide(result.getDirection());
            }
            if (!behaviour.slotPositioning.shouldRender((LevelAccessor)world, pos, state) || !behaviour.mayInteract((Player)mc.player)) continue;
            ItemStack filter = behaviour.getFilter();
            boolean isFilterSlotted = filter.getItem() instanceof FilterItem;
            boolean showCount = behaviour.isCountVisible();
            MutableComponent label = behaviour.getLabel();
            boolean hit = behaviour.slotPositioning.testHit((LevelAccessor)world, pos, state, target.getLocation().subtract(Vec3.atLowerCornerOf((Vec3i)pos)));
            AABB emptyBB = new AABB(Vec3.ZERO, Vec3.ZERO);
            AABB bb = isFilterSlotted ? emptyBB.inflate((double)0.45f, (double)0.31f, (double)0.2f) : emptyBB.inflate(0.25);
            ValueBox.ItemValueBox box = new ValueBox.ItemValueBox((Component)label, bb, pos, filter, behaviour.getCountLabelForValueBox());
            box.passive(!hit || behaviour.bypassesInput(mainhandItem));
            Outliner.getInstance().showOutline((Object)Pair.of((Object)("filter" + behaviour.netId()), (Object)pos), (Outline)box.transform(behaviour.slotPositioning)).lineWidth(0.015625f).withFaceTexture((BindableTexture)(hit ? AllSpecialTextures.THIN_CHECKERED : null)).highlightFace(result.getDirection());
            if (!hit) continue;
            ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
            tip.add(label.copy());
            tip.add(behaviour.getTip());
            if (showCount) {
                tip.add(behaviour.getAmountTip());
            }
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
        }
    }

    public static void renderOnBlockEntity(SmartBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be == null || be.isRemoved()) {
            return;
        }
        Level level = be.getLevel();
        BlockPos blockPos = be.getBlockPos();
        for (BlockEntityBehaviour b : be.getAllBehaviours()) {
            Entity cameraEntity;
            if (!(b instanceof FilteringBehaviour)) continue;
            FilteringBehaviour behaviour = (FilteringBehaviour)b;
            if (!be.isVirtual() && (cameraEntity = Minecraft.getInstance().cameraEntity) != null && level == cameraEntity.level()) {
                float max = behaviour.getRenderDistance();
                if (cameraEntity.position().distanceToSqr(VecHelper.getCenterOf((Vec3i)blockPos)) > (double)(max * max)) continue;
            }
            if (!behaviour.isActive() || behaviour.getFilter().isEmpty() && !(behaviour instanceof SidedFilteringBehaviour)) continue;
            ValueBoxTransform slotPositioning = behaviour.slotPositioning;
            BlockState blockState = be.getBlockState();
            if (slotPositioning instanceof ValueBoxTransform.Sided) {
                ValueBoxTransform.Sided sided = (ValueBoxTransform.Sided)slotPositioning;
                Direction side = sided.getSide();
                for (Direction d : Iterate.directions) {
                    ItemStack filter = behaviour.getFilter(d);
                    if (filter.isEmpty()) continue;
                    sided.fromSide(d);
                    if (!slotPositioning.shouldRender((LevelAccessor)level, blockPos, blockState)) continue;
                    ms.pushPose();
                    slotPositioning.transform((LevelAccessor)level, blockPos, blockState, ms);
                    if (AllBlocks.CONTRAPTION_CONTROLS.has(blockState)) {
                        ValueBoxRenderer.renderFlatItemIntoValueBox(filter, ms, buffer, light, overlay);
                    } else {
                        ValueBoxRenderer.renderItemIntoValueBox(filter, ms, buffer, light, overlay);
                    }
                    ms.popPose();
                }
                sided.fromSide(side);
                continue;
            }
            if (!slotPositioning.shouldRender((LevelAccessor)level, blockPos, blockState)) continue;
            ms.pushPose();
            slotPositioning.transform((LevelAccessor)level, blockPos, blockState, ms);
            ValueBoxRenderer.renderItemIntoValueBox(behaviour.getFilter(), ms, buffer, light, overlay);
            ms.popPose();
        }
    }
}
