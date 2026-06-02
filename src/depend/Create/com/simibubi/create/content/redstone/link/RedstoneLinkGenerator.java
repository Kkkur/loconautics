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
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.redstone.link;

import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class RedstoneLinkGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        Direction facing = (Direction)state.getValue((Property)RedstoneLinkBlock.FACING);
        return facing == Direction.UP ? 0 : (facing == Direction.DOWN ? 180 : 270);
    }

    @Override
    protected int getYRotation(BlockState state) {
        Direction facing = (Direction)state.getValue((Property)RedstoneLinkBlock.FACING);
        return facing.getAxis().isVertical() ? 180 : this.horizontalAngle(facing);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        Object variant;
        Object object = variant = (Boolean)state.getValue((Property)RedstoneLinkBlock.RECEIVER) != false ? "receiver" : "transmitter";
        if (((Direction)state.getValue((Property)RedstoneLinkBlock.FACING)).getAxis().isHorizontal()) {
            variant = (String)variant + "_vertical";
        }
        if (((Boolean)state.getValue((Property)RedstoneLinkBlock.POWERED)).booleanValue()) {
            variant = (String)variant + "_powered";
        }
        return prov.models().getExistingFile(prov.modLoc("block/redstone_link/" + (String)variant));
    }
}
