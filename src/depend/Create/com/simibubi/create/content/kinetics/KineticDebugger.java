/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.SuperByteBufferCache
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KineticDebugger {
    public static boolean rainbowDebug = false;

    public static void tick() {
        if (!KineticDebugger.isActive()) {
            if (KineticBlockEntityRenderer.rainbowMode) {
                KineticBlockEntityRenderer.rainbowMode = false;
                SuperByteBufferCache.getInstance().invalidate();
            }
            return;
        }
        KineticBlockEntity be = KineticDebugger.getSelectedBE();
        if (be == null) {
            return;
        }
        ClientLevel world = Minecraft.getInstance().level;
        BlockPos toOutline = be.hasSource() ? be.source : be.getBlockPos();
        BlockState state = be.getBlockState();
        VoxelShape shape = world.getBlockState(toOutline).getBlockSupportShape((BlockGetter)world, toOutline);
        if (be.getTheoreticalSpeed() != 0.0f && !shape.isEmpty()) {
            Outliner.getInstance().chaseAABB((Object)"kineticSource", shape.bounds().move(toOutline)).lineWidth(0.0625f).colored(be.hasSource() ? Color.generateFromLong((long)be.network).getRGB() : 0xFFCC00);
        }
        if (state.getBlock() instanceof IRotate) {
            Direction.Axis axis = ((IRotate)state.getBlock()).getRotationAxis(state);
            Vec3 vec = Vec3.atLowerCornerOf((Vec3i)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis).getNormal());
            Vec3 center = VecHelper.getCenterOf((Vec3i)be.getBlockPos());
            Outliner.getInstance().showLine((Object)"rotationAxis", center.add(vec), center.subtract(vec)).lineWidth(0.0625f);
        }
    }

    public static boolean isActive() {
        return KineticDebugger.isF3DebugModeActive() && rainbowDebug;
    }

    public static boolean isF3DebugModeActive() {
        return Minecraft.getInstance().getDebugOverlay().showDebugScreen();
    }

    public static KineticBlockEntity getSelectedBE() {
        HitResult obj = Minecraft.getInstance().hitResult;
        ClientLevel world = Minecraft.getInstance().level;
        if (obj == null) {
            return null;
        }
        if (world == null) {
            return null;
        }
        if (!(obj instanceof BlockHitResult)) {
            return null;
        }
        BlockHitResult ray = (BlockHitResult)obj;
        BlockEntity be = world.getBlockEntity(ray.getBlockPos());
        if (!(be instanceof KineticBlockEntity)) {
            return null;
        }
        return (KineticBlockEntity)be;
    }
}
