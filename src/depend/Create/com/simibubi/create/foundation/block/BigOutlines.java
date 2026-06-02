/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.foundation.block.IHaveBigOutline;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BigOutlines {
    static BlockHitResult result = null;

    public static void pick() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.cameraEntity;
        if (!(entity instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer player = (LocalPlayer)entity;
        if (mc.level == null) {
            return;
        }
        result = null;
        Vec3 origin = player.getEyePosition(AnimationTickHolder.getPartialTicks((LevelAccessor)mc.level));
        double maxRange = mc.hitResult == null ? Double.MAX_VALUE : mc.hitResult.getLocation().distanceToSqr(origin) + 0.5;
        double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        Vec3 target = RaycastHelper.getTraceTarget((Player)player, Math.min(maxRange, range) + 1.0, origin);
        RaycastHelper.rayTraceUntil(origin, target, pos -> {
            BlockPos.MutableBlockPos p = BlockPos.ZERO.mutable();
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; ++z) {
                        Vec3 vec;
                        double interactionDist;
                        BlockHitResult hit;
                        p.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockState blockState = mc.level.getBlockState((BlockPos)p);
                        if (!(blockState.getBlock() instanceof IHaveBigOutline) || (hit = blockState.getInteractionShape((BlockGetter)mc.level, (BlockPos)p).clip(origin, target, p.immutable())) == null || result != null && Vec3.atCenterOf((Vec3i)p).distanceToSqr(origin) >= Vec3.atCenterOf((Vec3i)result.getBlockPos()).distanceToSqr(origin) || (interactionDist = (vec = hit.getLocation()).distanceToSqr(origin)) >= maxRange) continue;
                        BlockPos hitPos = hit.getBlockPos();
                        vec = vec.subtract(Vec3.atCenterOf((Vec3i)hitPos));
                        vec = VecHelper.clampComponentWise((Vec3)vec, (float)1.0f);
                        vec = vec.add(Vec3.atCenterOf((Vec3i)hitPos));
                        result = new BlockHitResult(vec, hit.getDirection(), hitPos, hit.isInside());
                    }
                }
            }
            return result != null;
        });
        if (result != null) {
            mc.hitResult = result;
        }
    }
}
