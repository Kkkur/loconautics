/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class FunnelMovementBehaviour
implements MovementBehaviour {
    private final boolean hasFilter;

    public static FunnelMovementBehaviour andesite() {
        return new FunnelMovementBehaviour(false);
    }

    public static FunnelMovementBehaviour brass() {
        return new FunnelMovementBehaviour(true);
    }

    private FunnelMovementBehaviour(boolean hasFilter) {
        this.hasFilter = hasFilter;
    }

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        Direction facing = FunnelBlock.getFunnelFacing(context.state);
        Vec3 vec = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
        if (facing != Direction.UP) {
            return vec.scale((Boolean)context.state.getValue((Property)FunnelBlock.EXTRACTING) != false ? 0.15 : 0.65);
        }
        return vec.scale(0.65);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        MovementBehaviour.super.visitNewPosition(context, pos);
        if (((Boolean)context.state.getValue((Property)FunnelBlock.EXTRACTING)).booleanValue()) {
            this.extract(context, pos);
        } else {
            this.succ(context, pos);
        }
    }

    private void extract(MovementContext context, BlockPos pos) {
        Level world = context.world;
        Vec3 entityPos = context.position;
        if (context.state.getValue((Property)FunnelBlock.FACING) != Direction.DOWN) {
            entityPos = entityPos.add(0.0, -0.5, 0.0);
        }
        if (!world.getBlockState(pos).getCollisionShape((BlockGetter)world, pos).isEmpty()) {
            return;
        }
        if (!world.getEntitiesOfClass(ItemEntity.class, new AABB(BlockPos.containing((Position)entityPos))).isEmpty()) {
            return;
        }
        FilterItemStack filter = context.getFilterFromBE();
        int filterAmount = context.blockEntityData.getInt("FilterAmount");
        boolean upTo = context.blockEntityData.getBoolean("UpTo");
        filterAmount = this.hasFilter ? filterAmount : 1;
        ItemStack extract = ItemHelper.extract((IItemHandler)context.contraption.getStorage().getAllItems(), s -> filter.test(world, (ItemStack)s), upTo ? ItemHelper.ExtractionCountMode.UPTO : ItemHelper.ExtractionCountMode.EXACTLY, filterAmount, false);
        if (extract.isEmpty()) {
            return;
        }
        if (world.isClientSide) {
            return;
        }
        ItemEntity entity = new ItemEntity(world, entityPos.x, entityPos.y, entityPos.z, extract);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setPickUpDelay(5);
        world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.0625f, 0.1f);
        world.addFreshEntity((Entity)entity);
    }

    private void succ(MovementContext context, BlockPos pos) {
        Level world = context.world;
        List items = world.getEntities((Entity)null, new AABB(pos), e -> e instanceof ItemEntity || e instanceof PackageEntity);
        FilterItemStack filter = context.getFilterFromBE();
        for (Entity entity : items) {
            ItemStack remainder;
            ItemStack toInsert;
            if (!entity.isAlive() || !filter.test(context.world, toInsert = ItemHelper.fromItemEntity(entity)) || (remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)context.contraption.getStorage().getAllItems(), (ItemStack)toInsert, (boolean)false)).getCount() == toInsert.getCount()) continue;
            if (remainder.isEmpty()) {
                entity.discard();
                continue;
            }
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity item = (ItemEntity)entity;
            item.setItem(remainder);
        }
    }
}
