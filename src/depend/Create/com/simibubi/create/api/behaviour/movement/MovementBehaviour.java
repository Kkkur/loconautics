/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.behaviour.movement;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface MovementBehaviour {
    public static final SimpleRegistry<Block, MovementBehaviour> REGISTRY = SimpleRegistry.create();

    public static <B extends Block> NonNullConsumer<? super B> movementBehaviour(MovementBehaviour behaviour) {
        return b -> REGISTRY.register((Block)b, behaviour);
    }

    default public boolean isActive(MovementContext context) {
        return !context.disabled;
    }

    default public void tick(MovementContext context) {
    }

    default public void startMoving(MovementContext context) {
    }

    default public void visitNewPosition(MovementContext context, BlockPos pos) {
    }

    default public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.ZERO;
    }

    @Nullable
    default public ItemStack canBeDisabledVia(MovementContext context) {
        Block block = context.state.getBlock();
        if (block == null) {
            return null;
        }
        return new ItemStack((ItemLike)block);
    }

    default public void onDisabledByControls(MovementContext context) {
        this.cancelStall(context);
    }

    default public boolean mustTickWhileDisabled() {
        return false;
    }

    @Deprecated(since="6.0.9", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    default public void dropItem(MovementContext context, ItemStack stack) {
        this.collectOrDropItem(context, stack);
    }

    default public void collectOrDropItem(MovementContext context, ItemStack stack) {
        ItemStack remainder = (Boolean)AllConfigs.server().kinetics.moveItemsToStorage.get() != false ? ItemHandlerHelper.insertItem((IItemHandler)context.contraption.getStorage().getAllItems(), (ItemStack)stack, (boolean)false) : stack;
        if (remainder.isEmpty()) {
            return;
        }
        Vec3 vec = context.position;
        if (vec == null) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(context.world, vec.x, vec.y, vec.z, remainder);
        itemEntity.setDeltaMovement(context.motion.add(0.0, 0.5, 0.0).scale((double)(context.world.random.nextFloat() * 0.3f)));
        context.world.addFreshEntity((Entity)itemEntity);
    }

    default public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
    }

    default public void stopMoving(MovementContext context) {
    }

    default public void cancelStall(MovementContext context) {
        context.stall = false;
    }

    default public void writeExtraData(MovementContext context) {
    }

    default public boolean disableBlockEntityRendering() {
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    default public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return null;
    }
}
