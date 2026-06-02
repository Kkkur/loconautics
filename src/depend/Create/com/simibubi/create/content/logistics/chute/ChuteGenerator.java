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
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class ChuteGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return this.horizontalAngle((Direction)state.getValue((Property)ChuteBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        boolean horizontal = state.getValue((Property)ChuteBlock.FACING) != Direction.DOWN;
        ChuteBlock.Shape shape = (ChuteBlock.Shape)((Object)state.getValue(ChuteBlock.SHAPE));
        if (!horizontal) {
            return shape == ChuteBlock.Shape.NORMAL ? AssetLookup.partialBaseModel(ctx, prov, new String[0]) : (shape == ChuteBlock.Shape.INTERSECTION || shape == ChuteBlock.Shape.ENCASED ? AssetLookup.partialBaseModel(ctx, prov, "intersection") : AssetLookup.partialBaseModel(ctx, prov, "windowed"));
        }
        return shape == ChuteBlock.Shape.INTERSECTION ? AssetLookup.partialBaseModel(ctx, prov, "diagonal", "intersection") : (shape == ChuteBlock.Shape.ENCASED ? AssetLookup.partialBaseModel(ctx, prov, "diagonal", "encased") : AssetLookup.partialBaseModel(ctx, prov, "diagonal"));
    }
}
