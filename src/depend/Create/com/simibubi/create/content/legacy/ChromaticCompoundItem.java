/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BeaconBlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.legacy;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ChromaticCompoundItem
extends Item {
    public ChromaticCompoundItem(Item.Properties properties) {
        super(properties);
    }

    public int getLight(ItemStack stack) {
        return (Integer)stack.getOrDefault(AllDataComponents.CHROMATIC_COMPOUND_COLLECTING_LIGHT, (Object)0);
    }

    public boolean isBarVisible(ItemStack stack) {
        return this.getLight(stack) > 0;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f * (float)this.getLight(stack) / (float)((Integer)AllConfigs.server().recipes.lightSourceCountForRefinedRadiance.get()).intValue());
    }

    public int getBarColor(ItemStack stack) {
        return Color.mixColors((int)4275305, (int)0xFFFFFF, (float)((float)this.getLight(stack) / (float)((Integer)AllConfigs.server().recipes.lightSourceCountForRefinedRadiance.get()).intValue()));
    }

    public int getMaxStackSize(ItemStack stack) {
        return this.isBarVisible(stack) ? 1 : 16;
    }

    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        ItemStack newStack;
        Level world = entity.level();
        ItemStack itemStack = entity.getItem();
        Vec3 positionVec = entity.position();
        CRecipes config = AllConfigs.server().recipes;
        if (world.isClientSide) {
            int light = this.getLight(itemStack);
            if (world.random.nextInt((Integer)config.lightSourceCountForRefinedRadiance.get() + 20) < light) {
                Vec3 start = VecHelper.offsetRandomly((Vec3)positionVec, (RandomSource)world.random, (float)3.0f);
                Vec3 motion = positionVec.subtract(start).normalize().scale((double)0.2f);
                world.addParticle((ParticleOptions)ParticleTypes.END_ROD, start.x, start.y, start.z, motion.x, motion.y, motion.z);
            }
            return false;
        }
        double y = entity.getY();
        double yMotion = entity.getDeltaMovement().y;
        int minHeight = world.getMinBuildHeight();
        CompoundTag data = entity.getPersistentData();
        if (y < (double)minHeight && y - yMotion < (double)(-10 + minHeight) && ((Boolean)config.enableShadowSteelRecipe.get()).booleanValue()) {
            newStack = AllItems.SHADOW_STEEL.asStack();
            newStack.setCount(stack.getCount());
            data.putBoolean("JustCreated", true);
            entity.setItem(newStack);
        }
        if (!((Boolean)config.enableRefinedRadianceRecipe.get()).booleanValue()) {
            return false;
        }
        if (this.getLight(itemStack) >= (Integer)config.lightSourceCountForRefinedRadiance.get()) {
            newStack = AllItems.REFINED_RADIANCE.asStack();
            ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
            newEntity.setDeltaMovement(entity.getDeltaMovement());
            newEntity.getPersistentData().putBoolean("JustCreated", true);
            itemStack.remove(AllDataComponents.CHROMATIC_COMPOUND_COLLECTING_LIGHT);
            world.addFreshEntity((Entity)newEntity);
            stack.split(1);
            entity.setItem(stack);
            if (stack.isEmpty()) {
                entity.discard();
            }
            return false;
        }
        boolean isOverBeacon = false;
        int entityX = Mth.floor((double)entity.getX());
        int entityZ = Mth.floor((double)entity.getZ());
        int localWorldHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, entityX, entityZ);
        BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos(entityX, Math.min(Mth.floor((double)entity.getY()), localWorldHeight), entityZ);
        while (testPos.getY() > minHeight) {
            testPos.move(Direction.DOWN);
            BlockState state = world.getBlockState((BlockPos)testPos);
            if (state.getLightBlock((BlockGetter)world, (BlockPos)testPos) >= 15 && state.getBlock() != Blocks.BEDROCK) break;
            if (state.getBlock() != Blocks.BEACON) continue;
            BlockEntity be = world.getBlockEntity((BlockPos)testPos);
            if (!(be instanceof BeaconBlockEntity)) break;
            BeaconBlockEntity bte = (BeaconBlockEntity)be;
            if (bte.beamSections.isEmpty()) break;
            isOverBeacon = true;
            break;
        }
        if (isOverBeacon) {
            ItemStack newStack2 = AllItems.REFINED_RADIANCE.asStack();
            newStack2.setCount(stack.getCount());
            data.putBoolean("JustCreated", true);
            entity.setItem(newStack2);
            return false;
        }
        RandomSource r = world.random;
        int range = 3;
        float rate = 0.5f;
        if (r.nextFloat() > rate) {
            return false;
        }
        BlockPos randomOffset = BlockPos.containing((Position)VecHelper.offsetRandomly((Vec3)positionVec, (RandomSource)r, (float)range));
        BlockState state = world.getBlockState(randomOffset);
        TransportedItemStackHandlerBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, randomOffset, TransportedItemStackHandlerBehaviour.TYPE);
        if (behaviour == null) {
            if (this.checkLight(stack, entity, world, itemStack, positionVec, randomOffset, state)) {
                world.destroyBlock(randomOffset, false);
            }
            return false;
        }
        MutableBoolean success = new MutableBoolean(false);
        behaviour.handleProcessingOnAllItems(ts -> {
            ItemStack heldStack = ts.stack;
            Item patt0$temp = heldStack.getItem();
            if (!(patt0$temp instanceof BlockItem)) {
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            }
            BlockItem blockItem = (BlockItem)patt0$temp;
            if (blockItem.getBlock() == null) {
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            }
            BlockState stateToCheck = blockItem.getBlock().defaultBlockState();
            if (!success.getValue().booleanValue() && this.checkLight(stack, entity, world, itemStack, positionVec, randomOffset, stateToCheck)) {
                success.setTrue();
                if (ts.stack.getCount() == 1) {
                    return TransportedItemStackHandlerBehaviour.TransportedResult.removeItem();
                }
                TransportedItemStack left = ts.copy();
                left.stack.shrink(1);
                return TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(left);
            }
            return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
        });
        return false;
    }

    public boolean checkLight(ItemStack stack, ItemEntity entity, Level world, ItemStack itemStack, Vec3 positionVec, BlockPos randomOffset, BlockState state) {
        if (state.getLightEmission((BlockGetter)world, randomOffset) == 0) {
            return false;
        }
        if (state.getDestroySpeed((BlockGetter)world, randomOffset) == -1.0f) {
            return false;
        }
        if (state.getBlock() == Blocks.BEACON) {
            return false;
        }
        ClipContext context = new ClipContext(positionVec.add(new Vec3(0.0, 0.5, 0.0)), VecHelper.getCenterOf((Vec3i)randomOffset), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)entity);
        if (!randomOffset.equals((Object)world.clip(context).getBlockPos())) {
            return false;
        }
        ItemStack newStack = stack.split(1);
        newStack.set(AllDataComponents.CHROMATIC_COMPOUND_COLLECTING_LIGHT, (Object)(this.getLight(itemStack) + 1));
        ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
        newEntity.setDeltaMovement(entity.getDeltaMovement());
        newEntity.setDefaultPickUpDelay();
        world.addFreshEntity((Entity)newEntity);
        entity.lifespan = 6000;
        if (stack.isEmpty()) {
            entity.discard();
        }
        return true;
    }
}
