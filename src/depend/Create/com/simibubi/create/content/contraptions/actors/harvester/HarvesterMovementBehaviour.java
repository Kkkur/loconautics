/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.CocoaBlock
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.GrowingPlantBlock
 *  net.minecraft.world.level.block.MushroomBlock
 *  net.minecraft.world.level.block.SugarCaneBlock
 *  net.minecraft.world.level.block.SweetBerryBushBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.SpecialPlantable
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.actors.harvester;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.farmersdelight.FarmersDelightCompat;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterActorVisual;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.CropBlockAccessor;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public class HarvesterMovementBehaviour
implements MovementBehaviour {
    @Override
    public boolean isActive(MovementContext context) {
        return MovementBehaviour.super.isActive(context) && !VecHelper.isVecPointingTowards((Vec3)context.relativeMotion, (Direction)((Direction)context.state.getValue((Property)HarvesterBlock.FACING)).getOpposite());
    }

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)HarvesterBlock.FACING)).getNormal()).scale(0.45);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Level world = context.world;
        if (world.isClientSide) {
            return;
        }
        BlockState stateVisited = world.getBlockState(pos);
        if (stateVisited.isAir() || AllTags.AllBlockTags.NON_HARVESTABLE.matches(stateVisited)) {
            return;
        }
        boolean notCropButCuttable = false;
        if (!this.isValidCrop(world, pos, stateVisited)) {
            if (this.isValidOther(world, pos, stateVisited)) {
                notCropButCuttable = true;
            } else {
                return;
            }
        }
        ItemStack item = ItemStack.EMPTY;
        float effectChance = 1.0f;
        if (stateVisited.is(BlockTags.LEAVES)) {
            item = new ItemStack((ItemLike)Items.SHEARS);
            effectChance = 0.45f;
        }
        MutableBoolean seedSubtracted = new MutableBoolean(notCropButCuttable);
        BlockState state = stateVisited;
        BlockHelper.destroyBlockAs(world, pos, null, item, effectChance, stack -> {
            if (((Boolean)AllConfigs.server().kinetics.harvesterReplants.get()).booleanValue() && !seedSubtracted.getValue().booleanValue() && ItemHelper.sameItem(stack, new ItemStack((ItemLike)state.getBlock()))) {
                stack.shrink(1);
                seedSubtracted.setTrue();
            }
            this.collectOrDropItem(context, (ItemStack)stack);
        });
        BlockState cutCrop = this.cutCrop(world, pos, stateVisited);
        world.setBlockAndUpdate(pos, cutCrop.canSurvive((LevelReader)world, pos) ? cutCrop : Blocks.AIR.defaultBlockState());
    }

    public boolean isValidCrop(Level world, BlockPos pos, BlockState state) {
        boolean harvestPartial = (Boolean)AllConfigs.server().kinetics.harvestPartiallyGrown.get();
        boolean replant = (Boolean)AllConfigs.server().kinetics.harvesterReplants.get();
        Block block = state.getBlock();
        if (block instanceof CropBlock) {
            CropBlock crop = (CropBlock)block;
            if (harvestPartial) {
                return state != crop.getStateForAge(0) || !replant;
            }
            return crop.isMaxAge(state);
        }
        if (state.getCollisionShape((BlockGetter)world, pos).isEmpty() || state.getBlock() instanceof CocoaBlock) {
            for (Property property : state.getProperties()) {
                if (!(property instanceof IntegerProperty)) continue;
                IntegerProperty ageProperty = (IntegerProperty)property;
                if (!property.getName().equals(BlockStateProperties.AGE_1.getName())) continue;
                int age = (Integer)state.getValue((Property)ageProperty);
                if (state.getBlock() instanceof SweetBerryBushBlock && age <= 1 && replant || age == 0 && replant || !harvestPartial && ageProperty.getPossibleValues().size() - 1 != age) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isValidOther(Level world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CropBlock) {
            return false;
        }
        if (state.getBlock() instanceof SugarCaneBlock) {
            return true;
        }
        if (state.is(BlockTags.LEAVES)) {
            return true;
        }
        if (state.getBlock() instanceof CocoaBlock) {
            return (Integer)state.getValue((Property)CocoaBlock.AGE) == 2;
        }
        if (state.getCollisionShape((BlockGetter)world, pos).isEmpty()) {
            if (state.getBlock() instanceof GrowingPlantBlock) {
                return true;
            }
            for (Property property : state.getProperties()) {
                if (!(property instanceof IntegerProperty) || !property.getName().equals(BlockStateProperties.AGE_1.getName())) continue;
                return false;
            }
            if (state.getBlock() instanceof MushroomBlock && Mods.FARMERSDELIGHT.isLoaded()) {
                return FarmersDelightCompat.shouldHarvestMushroom(world, pos, state);
            }
            if (state.getBlock() instanceof BushBlock) {
                return true;
            }
            if (state.getBlock() instanceof SpecialPlantable) {
                return true;
            }
        }
        return false;
    }

    private BlockState cutCrop(Level world, BlockPos pos, BlockState state) {
        if (!((Boolean)AllConfigs.server().kinetics.harvesterReplants.get()).booleanValue()) {
            if (state.getFluidState().isEmpty()) {
                return Blocks.AIR.defaultBlockState();
            }
            return state.getFluidState().createLegacyBlock();
        }
        Block block = state.getBlock();
        if (block instanceof CropBlock) {
            CropBlock crop = (CropBlock)block;
            BlockState newState = crop.getStateForAge(0);
            if (!newState.is(block)) {
                return newState;
            }
            IntegerProperty ageProperty = ((CropBlockAccessor)crop).create$callGetAgeProperty();
            return (BlockState)state.setValue((Property)ageProperty, (Comparable)Integer.valueOf(0));
        }
        if (block == Blocks.SWEET_BERRY_BUSH) {
            return (BlockState)state.setValue((Property)BlockStateProperties.AGE_3, (Comparable)Integer.valueOf(1));
        }
        if (AllTags.AllBlockTags.SUGAR_CANE_VARIANTS.matches(block) || block instanceof GrowingPlantBlock) {
            if (state.getFluidState().isEmpty()) {
                return Blocks.AIR.defaultBlockState();
            }
            return state.getFluidState().createLegacyBlock();
        }
        if (state.getCollisionShape((BlockGetter)world, pos).isEmpty() || block instanceof CocoaBlock) {
            for (Property property : state.getProperties()) {
                if (!(property instanceof IntegerProperty) || !property.getName().equals(BlockStateProperties.AGE_1.getName())) continue;
                return (BlockState)state.setValue((Property)((IntegerProperty)property), (Comparable)Integer.valueOf(0));
            }
        }
        if (state.getFluidState().isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }
        return state.getFluidState().createLegacyBlock();
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffers) {
        if (!VisualizationManager.supportsVisualization((LevelAccessor)context.world)) {
            HarvesterRenderer.renderInContraption(context, renderWorld, matrices, buffers);
        }
    }

    @Override
    @Nullable
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new HarvesterActorVisual(visualizationContext, simulationWorld, movementContext);
    }
}
