/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.ryanhcode.sable.mixinhelpers.entity.entity_collision;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TheFasterEntityCollisionContext
extends EntityCollisionContext {
    private final Entity entity;

    public TheFasterEntityCollisionContext(Entity entity) {
        super(false, 0.0, ItemStack.EMPTY, atack -> false, entity);
        this.entity = entity;
    }

    public boolean isHoldingItem(Item item) {
        LivingEntity livingEntity;
        Entity entity = this.entity;
        return entity instanceof LivingEntity && (livingEntity = (LivingEntity)entity).getMainHandItem().is(item);
    }

    public boolean canStandOnFluid(FluidState fluidState, FluidState fluidState2) {
        LivingEntity livingEntity;
        Entity entity = this.entity;
        return entity instanceof LivingEntity && (livingEntity = (LivingEntity)entity).canStandOnFluid(fluidState) && !fluidState.getType().isSame(fluidState2.getType());
    }

    public boolean isDescending() {
        return this.entity.isDescending();
    }

    public boolean isAbove(VoxelShape shape, BlockPos pos, boolean bl) {
        return this.entity.getY() > (double)pos.getY() + shape.max(Direction.Axis.Y) - (double)1.0E-5f;
    }
}
