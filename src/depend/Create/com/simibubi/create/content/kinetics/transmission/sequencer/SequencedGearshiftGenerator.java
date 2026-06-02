/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class SequencedGearshiftGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return (Boolean)state.getValue((Property)SequencedGearshiftBlock.VERTICAL) != false ? 90 : 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return state.getValue(SequencedGearshiftBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        Object variant = "idle";
        int seq = (Integer)state.getValue((Property)SequencedGearshiftBlock.STATE);
        if (seq > 0) {
            variant = "seq_" + seq;
        }
        return prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/" + (String)variant));
    }
}
