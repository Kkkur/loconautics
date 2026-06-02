/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.logistics.crate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.crate.BottomlessItemHandler;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CreativeCrateBlockEntity
extends CrateBlockEntity
implements Clearable {
    FilteringBehaviour filtering;
    BottomlessItemHandler inv = new BottomlessItemHandler(this.filtering::getFilter);

    public CreativeCrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.CREATIVE_CRATE.get(), (be, context) -> be.inv);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.filtering = this.createFilter();
        behaviours.add(this.filtering);
        this.filtering.setLabel(CreateLang.translateDirect("logistics.creative_crate.supply", new Object[0]));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.inv != null) {
            this.invalidateCapabilities();
        }
    }

    public void clearContent() {
        this.filtering.setFilter(ItemStack.EMPTY);
    }

    public FilteringBehaviour createFilter() {
        return new FilteringBehaviour(this, new ValueBoxTransform(this){

            @Override
            public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
                TransformStack.of((PoseStack)ms).rotateXDegrees(90.0f);
            }

            @Override
            public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
                return new Vec3(0.5, 0.84375, 0.5);
            }

            @Override
            public float getScale() {
                return super.getScale();
            }
        });
    }
}
