/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.common.world.AuxiliaryLightManager
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.api.schematic.nbt.PartialSafeNBT;
import com.simibubi.create.api.schematic.requirement.SpecialBlockEntityItemRequirement;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.content.redstone.RoseQuartzLampBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

public class CopycatBlockEntity
extends SmartBlockEntity
implements SpecialBlockEntityItemRequirement,
TransformableBlockEntity,
PartialSafeNBT,
Clearable {
    private BlockState material = AllBlocks.COPYCAT_BASE.getDefaultState();
    private ItemStack consumedItem = ItemStack.EMPTY;

    public CopycatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockState getMaterial() {
        return this.material;
    }

    public boolean hasCustomMaterial() {
        return !AllBlocks.COPYCAT_BASE.has(this.getMaterial());
    }

    public void setMaterial(BlockState blockState) {
        BlockState wrapperState = this.getBlockState();
        if (!this.material.is(blockState.getBlock())) {
            for (Direction side : Iterate.directions) {
                CopycatBlockEntity cbe;
                BlockState otherMaterial;
                BlockEntity blockEntity;
                BlockPos neighbour = this.worldPosition.relative(side);
                BlockState neighbourState = this.level.getBlockState(neighbour);
                if (neighbourState != wrapperState || !((blockEntity = this.level.getBlockEntity(neighbour)) instanceof CopycatBlockEntity) || !(otherMaterial = (cbe = (CopycatBlockEntity)blockEntity).getMaterial()).is(blockState.getBlock())) continue;
                blockState = otherMaterial;
                break;
            }
        }
        this.material = blockState;
        if (!this.level.isClientSide()) {
            this.notifyUpdate();
            return;
        }
        this.redraw();
    }

    public boolean cycleMaterial() {
        if (this.material.hasProperty((Property)TrapDoorBlock.HALF) && this.material.getOptionalValue((Property)TrapDoorBlock.OPEN).orElse(false).booleanValue()) {
            this.setMaterial((BlockState)this.material.cycle((Property)TrapDoorBlock.HALF));
        } else if (this.material.hasProperty((Property)BlockStateProperties.FACING)) {
            this.setMaterial((BlockState)this.material.cycle((Property)BlockStateProperties.FACING));
        } else if (this.material.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING)) {
            this.setMaterial((BlockState)this.material.setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)((Direction)this.material.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getClockWise()));
        } else if (this.material.hasProperty((Property)BlockStateProperties.AXIS)) {
            this.setMaterial((BlockState)this.material.cycle((Property)BlockStateProperties.AXIS));
        } else if (this.material.hasProperty((Property)BlockStateProperties.HORIZONTAL_AXIS)) {
            this.setMaterial((BlockState)this.material.cycle((Property)BlockStateProperties.HORIZONTAL_AXIS));
        } else if (this.material.hasProperty((Property)BlockStateProperties.LIT)) {
            this.setMaterial((BlockState)this.material.cycle((Property)BlockStateProperties.LIT));
        } else if (this.material.hasProperty((Property)RoseQuartzLampBlock.POWERING)) {
            this.setMaterial((BlockState)this.material.cycle((Property)RoseQuartzLampBlock.POWERING));
        } else {
            return false;
        }
        return true;
    }

    public ItemStack getConsumedItem() {
        return this.consumedItem;
    }

    public void setConsumedItem(ItemStack stack) {
        this.consumedItem = stack.copyWithCount(1);
        this.setChanged();
    }

    private void redraw() {
        if (!this.isVirtual()) {
            this.requestModelDataUpdate();
        }
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
            this.updateLight();
        }
    }

    private void updateLight() {
        AuxiliaryLightManager lightManager;
        if (this.level != null && (lightManager = this.level.getAuxLightManager(this.getBlockPos())) != null) {
            lightManager.setLightAt(this.getBlockPos(), this.material.getLightEmission((BlockGetter)this.level, this.getBlockPos()));
        }
    }

    public void onLoad() {
        super.onLoad();
        this.updateLight();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        if (this.consumedItem.isEmpty()) {
            return ItemRequirement.NONE;
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, this.consumedItem);
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        this.material = transform.apply(this.material);
        this.notifyUpdate();
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.consumedItem = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag.getCompound("Item"));
        BlockState prevMaterial = this.material;
        if (!tag.contains("Material")) {
            this.consumedItem = ItemStack.EMPTY;
            return;
        }
        this.material = NbtUtils.readBlockState(this.blockHolderGetter(), (CompoundTag)tag.getCompound("Material"));
        if (this.material != null && !clientPacket) {
            BlockState blockState = this.getBlockState();
            if (blockState == null) {
                return;
            }
            Block block = blockState.getBlock();
            if (!(block instanceof CopycatBlock)) {
                return;
            }
            CopycatBlock cb = (CopycatBlock)block;
            BlockState acceptedBlockState = cb.getAcceptedBlockState(this.level, this.worldPosition, this.consumedItem, null);
            if (acceptedBlockState != null && this.material.is(acceptedBlockState.getBlock())) {
                return;
            }
            this.consumedItem = ItemStack.EMPTY;
            this.material = AllBlocks.COPYCAT_BASE.getDefaultState();
        }
        if (clientPacket && prevMaterial != this.material) {
            this.redraw();
        }
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        ItemStack stackWithoutComponents = new ItemStack(this.consumedItem.getItemHolder(), this.consumedItem.getCount(), DataComponentPatch.EMPTY);
        this.write(tag, registries, stackWithoutComponents, this.material);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        this.write(tag, registries, this.consumedItem, this.material);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, ItemStack stack, BlockState material) {
        tag.put("Item", stack.saveOptional(registries));
        tag.put("Material", (Tag)NbtUtils.writeBlockState((BlockState)material));
    }

    public ModelData getModelData() {
        return ModelData.builder().with(CopycatModel.MATERIAL_PROPERTY, (Object)this.material).build();
    }

    public void clearContent() {
        this.material = AllBlocks.COPYCAT_BASE.getDefaultState();
        this.consumedItem = ItemStack.EMPTY;
    }
}
