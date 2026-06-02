/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

class AllMountedDispenseItemBehaviors.1
extends DefaultMountedDispenseBehavior {
    AllMountedDispenseItemBehaviors.1() {
    }

    @Override
    protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        Item item = stack.getItem();
        if (!(item instanceof SpawnEggItem)) {
            return super.execute(stack, context, pos, facing);
        }
        SpawnEggItem egg = (SpawnEggItem)item;
        Level level = context.world;
        if (level instanceof ServerLevel) {
            BlockPos offset;
            ServerLevel serverLevel = (ServerLevel)level;
            EntityType type = egg.getType(stack);
            Entity entity = type.spawn(serverLevel, stack, null, pos.offset((Vec3i)(offset = BlockPos.containing((double)(facing.x + 0.7), (double)(facing.y + 0.7), (double)(facing.z + 0.7)))), MobSpawnType.DISPENSER, facing.y < 0.5, false);
            if (entity != null) {
                entity.setDeltaMovement(context.motion.scale(2.0));
            }
        }
        stack.shrink(1);
        return stack;
    }
}
