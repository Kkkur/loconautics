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
package com.simibubi.create.content.redstone.nixieTube;

import com.simibubi.create.content.redstone.nixieTube.DoubleFaceAttachedBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class NixieTubeGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return ((DoubleFaceAttachedBlock.DoubleAttachFace)((Object)state.getValue((Property)NixieTubeBlock.FACE))).xRot();
    }

    @Override
    protected int getYRotation(BlockState state) {
        DoubleFaceAttachedBlock.DoubleAttachFace face = (DoubleFaceAttachedBlock.DoubleAttachFace)((Object)state.getValue((Property)NixieTubeBlock.FACE));
        return this.horizontalAngle((Direction)state.getValue((Property)NixieTubeBlock.FACING)) + (face == DoubleFaceAttachedBlock.DoubleAttachFace.WALL || face == DoubleFaceAttachedBlock.DoubleAttachFace.WALL_REVERSED ? 180 : 0);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return prov.models().withExistingParent(ctx.getName(), prov.modLoc("block/nixie_tube/block"));
    }
}
