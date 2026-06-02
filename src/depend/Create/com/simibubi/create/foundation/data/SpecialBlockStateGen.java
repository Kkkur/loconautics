/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.foundation.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public abstract class SpecialBlockStateGen {
    protected Property<?>[] getIgnoredProperties() {
        return new Property[0];
    }

    public final <T extends Block> void generate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(this.getModel(ctx, prov, (BlockState)state)).rotationX((this.getXRotation((BlockState)state) + 360) % 360).rotationY((this.getYRotation((BlockState)state) + 360) % 360).build(), this.getIgnoredProperties());
    }

    protected int horizontalAngle(Direction direction) {
        if (direction.getAxis().isVertical()) {
            return 0;
        }
        return (int)direction.toYRot();
    }

    protected abstract int getXRotation(BlockState var1);

    protected abstract int getYRotation(BlockState var1);

    public abstract <T extends Block> ModelFile getModel(DataGenContext<Block, T> var1, RegistrateBlockstateProvider var2, BlockState var3);
}
