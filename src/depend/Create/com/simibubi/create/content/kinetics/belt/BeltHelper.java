/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 */
package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;

public class BeltHelper {
    public static Map<Item, Boolean> uprightCache = new Object2BooleanOpenHashMap();
    public static final ResourceManagerReloadListener LISTENER = resourceManager -> uprightCache.clear();

    public static boolean isItemUpright(ItemStack stack) {
        return uprightCache.computeIfAbsent(stack.getItem(), item -> {
            boolean isFluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
            boolean useUpright = AllTags.AllItemTags.UPRIGHT_ON_BELT.matches(stack);
            boolean forceDisableUpright = !AllTags.AllItemTags.NOT_UPRIGHT_ON_BELT.matches(stack);
            return (isFluidHandler || useUpright) && forceDisableUpright;
        });
    }

    public static BeltBlockEntity getSegmentBE(LevelAccessor world, BlockPos pos) {
        Level l;
        if (world instanceof Level && !(l = (Level)world).isLoaded(pos)) {
            return null;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof BeltBlockEntity)) {
            return null;
        }
        return (BeltBlockEntity)blockEntity;
    }

    public static BeltBlockEntity getControllerBE(LevelAccessor world, BlockPos pos) {
        BeltBlockEntity segment = BeltHelper.getSegmentBE(world, pos);
        if (segment == null) {
            return null;
        }
        BlockPos controllerPos = segment.controller;
        if (controllerPos == null) {
            return null;
        }
        return BeltHelper.getSegmentBE(world, controllerPos);
    }

    public static BeltBlockEntity getBeltForOffset(BeltBlockEntity controller, float offset) {
        return BeltHelper.getBeltAtSegment(controller, (int)Math.floor(offset));
    }

    public static BeltBlockEntity getBeltAtSegment(BeltBlockEntity controller, int segment) {
        BlockPos pos = BeltHelper.getPositionForOffset(controller, segment);
        BlockEntity be = controller.getLevel().getBlockEntity(pos);
        if (be == null || !(be instanceof BeltBlockEntity)) {
            return null;
        }
        return (BeltBlockEntity)be;
    }

    public static BlockPos getPositionForOffset(BeltBlockEntity controller, int offset) {
        BlockPos pos = controller.getBlockPos();
        Vec3i vec = controller.getBeltFacing().getNormal();
        BeltSlope slope = (BeltSlope)((Object)controller.getBlockState().getValue(BeltBlock.SLOPE));
        int verticality = slope == BeltSlope.DOWNWARD ? -1 : (slope == BeltSlope.UPWARD ? 1 : 0);
        return pos.offset(offset * vec.getX(), Mth.clamp((int)offset, (int)0, (int)(controller.beltLength - 1)) * verticality, offset * vec.getZ());
    }

    public static Vec3 getVectorForOffset(BeltBlockEntity controller, float offset) {
        BeltSlope slope = (BeltSlope)((Object)controller.getBlockState().getValue(BeltBlock.SLOPE));
        int verticality = slope == BeltSlope.DOWNWARD ? -1 : (slope == BeltSlope.UPWARD ? 1 : 0);
        float verticalMovement = verticality;
        if ((double)offset < 0.5) {
            verticalMovement = 0.0f;
        }
        verticalMovement *= Math.min(offset, (float)controller.beltLength - 0.5f) - 0.5f;
        Vec3 vec = VecHelper.getCenterOf((Vec3i)controller.getBlockPos());
        Vec3 horizontalMovement = Vec3.atLowerCornerOf((Vec3i)controller.getBeltFacing().getNormal()).scale((double)(offset - 0.5f));
        if (slope == BeltSlope.VERTICAL) {
            horizontalMovement = Vec3.ZERO;
        }
        vec = vec.add(horizontalMovement).add(0.0, (double)verticalMovement, 0.0);
        return vec;
    }

    public static Vec3 getBeltVector(BlockState state) {
        BeltSlope slope = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        int verticality = slope == BeltSlope.DOWNWARD ? -1 : (slope == BeltSlope.UPWARD ? 1 : 0);
        Vec3 horizontalMovement = Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue(BeltBlock.HORIZONTAL_FACING)).getNormal());
        if (slope == BeltSlope.VERTICAL) {
            return new Vec3(0.0, (double)((Direction)state.getValue(BeltBlock.HORIZONTAL_FACING)).getAxisDirection().getStep(), 0.0);
        }
        return new Vec3(0.0, (double)verticality, 0.0).add(horizontalMovement);
    }
}
