/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.content.kinetics.saw.SawActorVisual;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawRenderer;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import java.util.Optional;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class SawMovementBehaviour
extends BlockBreakingMovementBehaviour {
    @Override
    public boolean isActive(MovementContext context) {
        return super.isActive(context) && !VecHelper.isVecPointingTowards((Vec3)context.relativeMotion, (Direction)((Direction)context.state.getValue((Property)SawBlock.FACING)).getOpposite());
    }

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)SawBlock.FACING)).getNormal()).scale((double)0.65f);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        super.visitNewPosition(context, pos);
        Vec3 facingVec = Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)SawBlock.FACING)).getNormal());
        facingVec = (Vec3)context.rotation.apply(facingVec);
        Direction closestToFacing = Direction.getNearest((double)facingVec.x, (double)facingVec.y, (double)facingVec.z);
        if (closestToFacing.getAxis().isVertical() && context.data.contains("BreakingPos")) {
            context.data.remove("BreakingPos");
            context.stall = false;
        }
    }

    @Override
    public boolean canBreak(Level world, BlockPos breakingPos, BlockState state) {
        return super.canBreak(world, breakingPos, state) && SawBlockEntity.isSawable(state);
    }

    @Override
    protected void onBlockBroken(MovementContext context, BlockPos pos, BlockState brokenState) {
        if (brokenState.is(BlockTags.LEAVES)) {
            return;
        }
        Optional<AbstractBlockBreakQueue> dynamicTree = TreeCutter.findDynamicTree(brokenState.getBlock(), pos);
        if (dynamicTree.isPresent()) {
            dynamicTree.get().destroyBlocks(context.world, null, (stack, dropPos) -> this.dropItemFromCutTree(context, (BlockPos)stack, (ItemStack)dropPos));
            return;
        }
        TreeCutter.findTree((BlockGetter)context.world, pos, brokenState).destroyBlocks(context.world, null, (stack, dropPos) -> this.dropItemFromCutTree(context, (BlockPos)stack, (ItemStack)dropPos));
    }

    public void dropItemFromCutTree(MovementContext context, BlockPos pos, ItemStack stack) {
        ItemStack remainder = ItemHandlerHelper.insertItem((IItemHandler)context.contraption.getStorage().getAllItems(), (ItemStack)stack, (boolean)false);
        if (remainder.isEmpty()) {
            return;
        }
        Level world = context.world;
        Vec3 dropPos = VecHelper.getCenterOf((Vec3i)pos);
        float distance = context.position == null ? 1.0f : (float)dropPos.distanceTo(context.position);
        ItemEntity entity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, remainder);
        entity.setDeltaMovement(context.relativeMotion.scale((double)(distance / 20.0f)));
        world.addFreshEntity((Entity)entity);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @Nullable
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new SawActorVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        SawRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }

    @Override
    protected boolean shouldDestroyStartBlock(BlockState stateToBreak) {
        return !TreeCutter.canDynamicTreeCutFrom(stateToBreak.getBlock());
    }

    @Override
    protected DamageSource getDamageSource(Level level) {
        return CreateDamageSources.saw(level);
    }
}
