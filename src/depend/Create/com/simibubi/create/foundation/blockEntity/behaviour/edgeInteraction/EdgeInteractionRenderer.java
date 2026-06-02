/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outline
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionHandler;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EdgeInteractionRenderer {
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        HitResult target = mc.hitResult;
        if (target == null || !(target instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult result = (BlockHitResult)target;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        LocalPlayer player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        if (player.isShiftKeyDown()) {
            return;
        }
        EdgeInteractionBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, pos, EdgeInteractionBehaviour.TYPE);
        if (behaviour == null) {
            return;
        }
        if (!behaviour.requiredItem.test(heldItem.getItem())) {
            return;
        }
        Direction face = result.getDirection();
        List<Direction> connectiveSides = EdgeInteractionHandler.getConnectiveSides((Level)world, pos, face, behaviour);
        if (connectiveSides.isEmpty()) {
            return;
        }
        Direction closestEdge = connectiveSides.get(0);
        double bestDistance = Double.MAX_VALUE;
        Vec3 center = VecHelper.getCenterOf((Vec3i)pos);
        for (Direction direction : connectiveSides) {
            double distance = Vec3.atLowerCornerOf((Vec3i)direction.getNormal()).subtract(target.getLocation().subtract(center)).length();
            if (distance > bestDistance) continue;
            bestDistance = distance;
            closestEdge = direction;
        }
        AABB bb = EdgeInteractionHandler.getBB(pos, closestEdge);
        boolean hit = bb.contains(target.getLocation());
        Vec3 offset = Vec3.atLowerCornerOf((Vec3i)closestEdge.getNormal()).scale(0.5).add(Vec3.atLowerCornerOf((Vec3i)face.getNormal()).scale(0.469)).add(VecHelper.CENTER_OF_ORIGIN);
        ValueBox box = new ValueBox(CommonComponents.EMPTY, bb, pos).passive(!hit).transform(new EdgeValueBoxTransform(offset)).wideOutline();
        Outliner.getInstance().showOutline((Object)"edge", (Outline)box).highlightFace(face);
        if (!hit) {
            return;
        }
        ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
        tip.add(CreateLang.translateDirect("logistics.crafter.connected", new Object[0]));
        tip.add(CreateLang.translateDirect(CrafterHelper.areCraftersConnected((BlockAndTintGetter)world, pos, pos.relative(closestEdge)) ? "logistics.crafter.click_to_separate" : "logistics.crafter.click_to_merge", new Object[0]));
        CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
    }

    static class EdgeValueBoxTransform
    extends ValueBoxTransform.Sided {
        private Vec3 add;

        public EdgeValueBoxTransform(Vec3 add) {
            this.add = add;
        }

        @Override
        protected Vec3 getSouthLocation() {
            return Vec3.ZERO;
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return this.add;
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
        }
    }
}
