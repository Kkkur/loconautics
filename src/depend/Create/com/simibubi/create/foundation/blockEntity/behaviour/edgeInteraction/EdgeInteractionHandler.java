/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.LogicalSide
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.RaycastHelper;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class EdgeInteractionHandler {
    @SubscribeEvent
    public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack heldItem = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isSpectator()) {
            return;
        }
        EdgeInteractionBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, pos, EdgeInteractionBehaviour.TYPE);
        if (behaviour == null) {
            return;
        }
        if (!behaviour.requiredItem.test(heldItem.getItem())) {
            return;
        }
        BlockHitResult ray = RaycastHelper.rayTraceRange(world, player, 10.0);
        if (ray == null) {
            return;
        }
        Direction activatedDirection = EdgeInteractionHandler.getActivatedDirection(world, pos, ray.getDirection(), ray.getLocation(), behaviour);
        if (activatedDirection == null) {
            return;
        }
        if (event.getSide() != LogicalSide.CLIENT) {
            behaviour.connectionCallback.apply(world, pos, pos.relative(activatedDirection));
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 0.1f);
    }

    public static List<Direction> getConnectiveSides(Level world, BlockPos pos, Direction face, EdgeInteractionBehaviour behaviour) {
        ArrayList<Direction> sides = new ArrayList<Direction>(6);
        if (BlockHelper.hasBlockSolidSide(world.getBlockState(pos.relative(face)), (BlockGetter)world, pos.relative(face), face.getOpposite())) {
            return sides;
        }
        for (Direction direction : Iterate.directions) {
            BlockPos neighbourPos;
            if (direction.getAxis() == face.getAxis() || BlockHelper.hasBlockSolidSide(world.getBlockState((neighbourPos = pos.relative(direction)).relative(face)), (BlockGetter)world, neighbourPos.relative(face), face.getOpposite()) || !behaviour.connectivityPredicate.test(world, pos, face, direction)) continue;
            sides.add(direction);
        }
        return sides;
    }

    public static Direction getActivatedDirection(Level world, BlockPos pos, Direction face, Vec3 hit, EdgeInteractionBehaviour behaviour) {
        for (Direction facing : EdgeInteractionHandler.getConnectiveSides(world, pos, face, behaviour)) {
            AABB bb = EdgeInteractionHandler.getBB(pos, facing);
            if (!bb.contains(hit)) continue;
            return facing;
        }
        return null;
    }

    static AABB getBB(BlockPos pos, Direction direction) {
        AABB bb = new AABB(pos);
        Vec3i vec = direction.getNormal();
        int x = vec.getX();
        int y = vec.getY();
        int z = vec.getZ();
        double margin = 0.625;
        double absX = (double)Math.abs(x) * margin;
        double absY = (double)Math.abs(y) * margin;
        double absZ = (double)Math.abs(z) * margin;
        bb = bb.contract(absX, absY, absZ);
        bb = bb.move(absX / 2.0, absY / 2.0, absZ / 2.0);
        bb = bb.move((double)x / 2.0, (double)y / 2.0, (double)z / 2.0);
        bb = bb.inflate(0.00390625);
        return bb;
    }
}
