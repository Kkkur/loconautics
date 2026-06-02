/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class PackagerLinkGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return state.getValue((Property)PackagerLinkBlock.FACE) == AttachFace.CEILING ? 180 : 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        Direction facing = (Direction)state.getValue((Property)PackagerLinkBlock.FACING);
        return this.horizontalAngle(facing);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        Object variant;
        Object object = variant = state.getValue((Property)PackagerLinkBlock.FACE) == AttachFace.WALL ? "block_horizontal" : "block_vertical";
        if (((Boolean)state.getValue((Property)PackagerLinkBlock.POWERED)).booleanValue()) {
            variant = (String)variant + "_powered";
        }
        return prov.models().getExistingFile(prov.modLoc("block/stock_link/" + (String)variant));
    }
}
