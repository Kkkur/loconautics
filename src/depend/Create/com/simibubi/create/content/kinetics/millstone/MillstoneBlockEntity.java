/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 *  net.neoforged.neoforge.items.wrapper.RecipeWrapper
 */
package com.simibubi.create.content.kinetics.millstone;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.ItemStackHandlerAccessor;
import com.simibubi.create.foundation.sound.SoundScapes;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class MillstoneBlockEntity
extends KineticBlockEntity
implements Clearable {
    public ItemStackHandler inputInv = new ItemStackHandler(1);
    public ItemStackHandler outputInv = new ItemStackHandler(9);
    public IItemHandler capability = new MillstoneInventoryHandler();
    public int timer;
    private MillingRecipe lastRecipe;

    public MillstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.MILLSTONE.get(), (be, context) -> be.capability);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.MILLSTONE);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if (this.getSpeed() == 0.0f) {
            return;
        }
        if (this.inputInv.getStackInSlot(0).isEmpty()) {
            return;
        }
        float pitch = Mth.clamp((float)(Math.abs(this.getSpeed()) / 256.0f + 0.45f), (float)0.85f, (float)1.0f);
        SoundScapes.play(SoundScapes.AmbienceGroup.MILLING, this.worldPosition, pitch);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSpeed() == 0.0f) {
            return;
        }
        for (int i = 0; i < this.outputInv.getSlots(); ++i) {
            if (this.outputInv.getStackInSlot(i).getCount() != this.outputInv.getSlotLimit(i)) continue;
            return;
        }
        if (this.timer > 0) {
            this.timer -= this.getProcessingSpeed();
            if (this.level.isClientSide) {
                this.spawnParticles();
                return;
            }
            if (this.timer <= 0) {
                this.process();
            }
            return;
        }
        if (this.inputInv.getStackInSlot(0).isEmpty()) {
            return;
        }
        RecipeWrapper inventoryIn = new RecipeWrapper((IItemHandler)this.inputInv);
        if (this.lastRecipe == null || !this.lastRecipe.matches((RecipeInput)inventoryIn, this.level)) {
            Optional recipe = AllRecipeTypes.MILLING.find(inventoryIn, this.level);
            if (!recipe.isPresent()) {
                this.timer = 100;
                this.sendData();
            } else {
                this.lastRecipe = (MillingRecipe)recipe.get().value();
                this.timer = this.lastRecipe.getProcessingDuration();
                this.sendData();
            }
            return;
        }
        this.timer = this.lastRecipe.getProcessingDuration();
        this.sendData();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    public void clearContent() {
        ((ItemStackHandlerAccessor)this.inputInv).create$getStacks().clear();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(this.level, this.worldPosition, (IItemHandler)this.inputInv);
        ItemHelper.dropContents(this.level, this.worldPosition, (IItemHandler)this.outputInv);
    }

    private void process() {
        RecipeWrapper inventoryIn = new RecipeWrapper((IItemHandler)this.inputInv);
        if (this.lastRecipe == null || !this.lastRecipe.matches((RecipeInput)inventoryIn, this.level)) {
            Optional recipe = AllRecipeTypes.MILLING.find(inventoryIn, this.level);
            if (recipe.isEmpty()) {
                return;
            }
            this.lastRecipe = (MillingRecipe)recipe.get().value();
        }
        ItemStack stackInSlot = this.inputInv.getStackInSlot(0);
        ItemStack craftingRemainingItem = stackInSlot.getCraftingRemainingItem();
        stackInSlot.shrink(1);
        this.inputInv.setStackInSlot(0, stackInSlot);
        this.lastRecipe.rollResults(this.level.random).forEach(stack -> ItemHandlerHelper.insertItemStacked((IItemHandler)this.outputInv, (ItemStack)stack, (boolean)false));
        if (!craftingRemainingItem.isEmpty()) {
            ItemHandlerHelper.insertItemStacked((IItemHandler)this.outputInv, (ItemStack)craftingRemainingItem, (boolean)false);
        }
        this.award(AllAdvancements.MILLSTONE);
        this.sendData();
        this.setChanged();
    }

    public void spawnParticles() {
        ItemStack stackInSlot = this.inputInv.getStackInSlot(0);
        if (stackInSlot.isEmpty()) {
            return;
        }
        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
        float angle = this.level.random.nextFloat() * 360.0f;
        Vec3 offset = new Vec3(0.0, 0.0, 0.5);
        offset = VecHelper.rotate((Vec3)offset, (double)angle, (Direction.Axis)Direction.Axis.Y);
        Vec3 target = VecHelper.rotate((Vec3)offset, (double)(this.getSpeed() > 0.0f ? 25.0 : -25.0), (Direction.Axis)Direction.Axis.Y);
        Vec3 center = offset.add(VecHelper.getCenterOf((Vec3i)this.worldPosition));
        target = VecHelper.offsetRandomly((Vec3)target.subtract(offset), (RandomSource)this.level.random, (float)0.0078125f);
        this.level.addParticle((ParticleOptions)data, center.x, center.y, center.z, target.x, target.y, target.z);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Timer", this.timer);
        compound.put("InputInventory", (Tag)this.inputInv.serializeNBT(registries));
        compound.put("OutputInventory", (Tag)this.outputInv.serializeNBT(registries));
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.timer = compound.getInt("Timer");
        this.inputInv.deserializeNBT(registries, compound.getCompound("InputInventory"));
        this.outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
        super.read(compound, registries, clientPacket);
    }

    public int getProcessingSpeed() {
        return Mth.clamp((int)((int)Math.abs(this.getSpeed() / 16.0f)), (int)1, (int)512);
    }

    private boolean canProcess(ItemStack stack) {
        ItemStackHandler tester = new ItemStackHandler(1);
        tester.setStackInSlot(0, stack);
        RecipeWrapper inventoryIn = new RecipeWrapper((IItemHandler)tester);
        if (this.lastRecipe != null && this.lastRecipe.matches((RecipeInput)inventoryIn, this.level)) {
            return true;
        }
        return AllRecipeTypes.MILLING.find(inventoryIn, this.level).isPresent();
    }

    private class MillstoneInventoryHandler
    extends CombinedInvWrapper {
        public MillstoneInventoryHandler() {
            super(new IItemHandlerModifiable[]{MillstoneBlockEntity.this.inputInv, MillstoneBlockEntity.this.outputInv});
        }

        public boolean isItemValid(int slot, ItemStack stack) {
            if (MillstoneBlockEntity.this.outputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot))) {
                return false;
            }
            return MillstoneBlockEntity.this.canProcess(stack) && super.isItemValid(slot, stack);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (MillstoneBlockEntity.this.outputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot))) {
                return stack;
            }
            if (!this.isItemValid(slot, stack)) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (MillstoneBlockEntity.this.inputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot))) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    }
}
