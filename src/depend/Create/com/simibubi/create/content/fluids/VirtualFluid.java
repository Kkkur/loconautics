/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Properties
 */
package com.simibubi.create.content.fluids;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class VirtualFluid
extends BaseFlowingFluid {
    private final boolean source;

    public static VirtualFluid createSource(BaseFlowingFluid.Properties properties) {
        return new VirtualFluid(properties, true);
    }

    public static VirtualFluid createFlowing(BaseFlowingFluid.Properties properties) {
        return new VirtualFluid(properties, false);
    }

    public VirtualFluid(BaseFlowingFluid.Properties properties, boolean source) {
        super(properties);
        this.source = source;
    }

    public Fluid getSource() {
        if (this.source) {
            return this;
        }
        return super.getSource();
    }

    public Fluid getFlowing() {
        if (this.source) {
            return super.getFlowing();
        }
        return this;
    }

    public Item getBucket() {
        return Items.AIR;
    }

    protected BlockState createLegacyBlock(FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    public boolean isSource(FluidState p_207193_1_) {
        return this.source;
    }

    public int getAmount(FluidState p_207192_1_) {
        return 0;
    }
}
