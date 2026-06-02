/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.trains.bogey;

import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.blockEntity.CachedRenderBBBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBogeyBlockEntity
extends CachedRenderBBBlockEntity {
    public static final String BOGEY_STYLE_KEY = "BogeyStyle";
    public static final String BOGEY_DATA_KEY = "BogeyData";
    private CompoundTag bogeyData;
    LerpedFloat virtualAnimation = LerpedFloat.angular();

    public AbstractBogeyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract BogeyStyle getDefaultStyle();

    public CompoundTag getBogeyData() {
        if (this.bogeyData == null || !this.bogeyData.contains(BOGEY_STYLE_KEY)) {
            this.bogeyData = this.createBogeyData();
        }
        return this.bogeyData;
    }

    public void setBogeyData(@NotNull CompoundTag newData) {
        if (!newData.contains(BOGEY_STYLE_KEY)) {
            ResourceLocation style = this.getDefaultStyle().id;
            NBTHelper.writeResourceLocation((CompoundTag)newData, (String)BOGEY_STYLE_KEY, (ResourceLocation)style);
        }
        this.bogeyData = newData;
    }

    public void setBogeyStyle(@NotNull BogeyStyle style) {
        ResourceLocation location = style.id;
        CompoundTag data = this.getBogeyData();
        NBTHelper.writeResourceLocation((CompoundTag)data, (String)BOGEY_STYLE_KEY, (ResourceLocation)location);
        this.markUpdated();
    }

    @NotNull
    public BogeyStyle getStyle() {
        CompoundTag data = this.getBogeyData();
        ResourceLocation currentStyle = NBTHelper.readResourceLocation((CompoundTag)data, (String)BOGEY_STYLE_KEY);
        BogeyStyle style = AllBogeyStyles.BOGEY_STYLES.get(currentStyle);
        if (style == null) {
            this.setBogeyStyle(this.getDefaultStyle());
            return this.getStyle();
        }
        return style;
    }

    protected void saveAdditional(@NotNull CompoundTag tag, // Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries) {
        CompoundTag data = this.getBogeyData();
        if (data != null) {
            tag.put(BOGEY_DATA_KEY, (Tag)data);
        }
        super.saveAdditional(tag, registries);
    }

    protected void loadAdditional(@NotNull CompoundTag tag, // Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries) {
        this.bogeyData = tag.contains(BOGEY_DATA_KEY) ? tag.getCompound(BOGEY_DATA_KEY) : this.createBogeyData();
        super.loadAdditional(tag, registries);
    }

    private CompoundTag createBogeyData() {
        CompoundTag nbt = new CompoundTag();
        NBTHelper.writeResourceLocation((CompoundTag)nbt, (String)BOGEY_STYLE_KEY, (ResourceLocation)this.getDefaultStyle().id);
        boolean upsideDown = false;
        Block block = this.getBlockState().getBlock();
        if (block instanceof AbstractBogeyBlock) {
            AbstractBogeyBlock bogeyBlock = (AbstractBogeyBlock)block;
            upsideDown = bogeyBlock.isUpsideDown(this.getBlockState());
        }
        nbt.putBoolean("UpsideDown", upsideDown);
        return nbt;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2.0);
    }

    public float getVirtualAngle(float partialTicks) {
        return this.virtualAnimation.getValue(partialTicks);
    }

    public void animate(float distanceMoved) {
        BlockState blockState = this.getBlockState();
        Block block = blockState.getBlock();
        if (!(block instanceof AbstractBogeyBlock)) {
            return;
        }
        AbstractBogeyBlock type = (AbstractBogeyBlock)block;
        double angleDiff = (double)(360.0f * distanceMoved) / (Math.PI * 2 * type.getWheelRadius());
        double newWheelAngle = ((double)this.virtualAnimation.getValue() - angleDiff) % 360.0;
        this.virtualAnimation.setValue(newWheelAngle);
    }

    private void markUpdated() {
        this.setChanged();
        Level level = this.getLevel();
        if (level != null) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
