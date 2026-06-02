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
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class SmartFluidPipeGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        AttachFace attachFace = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
        return attachFace == AttachFace.CEILING ? 180 : (attachFace == AttachFace.FLOOR ? 0 : 270);
    }

    @Override
    protected int getYRotation(BlockState state) {
        AttachFace attachFace = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
        int angle = this.horizontalAngle((Direction)state.getValue((Property)SmartFluidPipeBlock.FACING));
        return angle + (attachFace == AttachFace.CEILING ? 180 : 0);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return AssetLookup.partialBaseModel(ctx, prov, new String[0]);
    }
}
