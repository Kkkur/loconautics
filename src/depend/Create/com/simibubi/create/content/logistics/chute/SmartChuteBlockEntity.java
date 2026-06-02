/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.SmartChuteBlock;
import com.simibubi.create.content.logistics.chute.SmartChuteFilterSlotPositioning;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SmartChuteBlockEntity
extends ChuteBlockEntity
implements Clearable {
    FilteringBehaviour filtering;

    public SmartChuteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.SMART_CHUTE.get(), (be, context) -> be.itemHandler);
    }

    @Override
    protected boolean canAcceptItem(ItemStack stack) {
        return super.canAcceptItem(stack) && this.canActivate() && this.filtering.test(stack);
    }

    @Override
    protected int getExtractionAmount() {
        return this.filtering.isCountVisible() && !this.filtering.anyAmount() ? this.filtering.getAmount() : 64;
    }

    @Override
    protected ItemHelper.ExtractionCountMode getExtractionMode() {
        return this.filtering.isCountVisible() && !this.filtering.anyAmount() && !this.filtering.upTo ? ItemHelper.ExtractionCountMode.EXACTLY : ItemHelper.ExtractionCountMode.UPTO;
    }

    @Override
    protected boolean canActivate() {
        BlockState blockState = this.getBlockState();
        return blockState.hasProperty((Property)SmartChuteBlock.POWERED) && (Boolean)blockState.getValue((Property)SmartChuteBlock.POWERED) == false;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.filtering = new FilteringBehaviour(this, new SmartChuteFilterSlotPositioning()).showCountWhen(this::isExtracting).withCallback($ -> this.invVersionTracker.reset());
        behaviours.add(this.filtering);
        super.addBehaviours(behaviours);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.filtering.setFilter(ItemStack.EMPTY);
    }

    private boolean isExtracting() {
        boolean up = this.getItemMotion() < 0.0f;
        BlockPos chutePos = this.worldPosition.relative(up ? Direction.UP : Direction.DOWN);
        BlockState blockState = this.level.getBlockState(chutePos);
        return !AbstractChuteBlock.isChute(blockState) && !blockState.canBeReplaced();
    }
}
