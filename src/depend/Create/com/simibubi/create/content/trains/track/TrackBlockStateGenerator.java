/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class TrackBlockStateGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return ((TrackShape)((Object)state.getValue(TrackBlock.SHAPE))).getModelRotation();
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        TrackShape value = (TrackShape)((Object)state.getValue(TrackBlock.SHAPE));
        if (value == TrackShape.NONE) {
            return prov.models().getExistingFile(prov.mcLoc("block/air"));
        }
        return prov.models().getExistingFile(Create.asResource("block/track/" + value.getModel()));
    }
}
