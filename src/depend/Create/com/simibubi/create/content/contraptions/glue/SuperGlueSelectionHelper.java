/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.glue;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SuperGlueSelectionHelper {
    public static Set<BlockPos> searchGlueGroup(Level level, BlockPos startPos, BlockPos endPos, boolean includeOther) {
        if (endPos == null || startPos == null) {
            return null;
        }
        AABB bb = SuperGlueEntity.span(startPos, endPos);
        ArrayList<BlockPos> frontier = new ArrayList<BlockPos>();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        HashSet<BlockPos> attached = new HashSet<BlockPos>();
        HashSet<SuperGlueEntity> cachedOther = new HashSet<SuperGlueEntity>();
        visited.add(startPos);
        frontier.add(startPos);
        while (!frontier.isEmpty()) {
            BlockPos currentPos = (BlockPos)frontier.remove(0);
            attached.add(currentPos);
            for (Direction d : Iterate.directions) {
                boolean alreadySticky;
                BlockPos offset = currentPos.relative(d);
                boolean gluePresent = includeOther && SuperGlueEntity.isGlued((LevelAccessor)level, currentPos, d, cachedOther);
                boolean bl = alreadySticky = includeOther && SuperGlueEntity.isSideSticky(level, currentPos, d) || SuperGlueEntity.isSideSticky(level, offset, d.getOpposite());
                if (!alreadySticky && !gluePresent && !bb.contains(Vec3.atCenterOf((Vec3i)offset)) || !BlockMovementChecks.isMovementNecessary(level.getBlockState(offset), level, offset) || !SuperGlueEntity.isValidFace(level, currentPos, d) || !SuperGlueEntity.isValidFace(level, offset, d.getOpposite()) || !visited.add(offset)) continue;
                frontier.add(offset);
            }
        }
        if (attached.size() < 2 && attached.contains(endPos)) {
            return null;
        }
        return attached;
    }

    public static boolean collectGlueFromInventory(Player player, int requiredAmount, boolean simulate) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        if (requiredAmount == 0) {
            return true;
        }
        NonNullList items = player.getInventory().items;
        for (int i = -1; i < items.size(); ++i) {
            int slot = i == -1 ? player.getInventory().selected : i;
            ItemStack stack = (ItemStack)items.get(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof SuperGlueItem)) continue;
            int charges = Math.min(requiredAmount, stack.getMaxDamage() - stack.getDamageValue());
            stack.hurtAndBreak(charges, (LivingEntity)player, EquipmentSlot.MAINHAND);
            if ((requiredAmount -= charges) > 0) continue;
            return true;
        }
        return false;
    }
}
