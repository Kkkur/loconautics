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
package com.simibubi.create.content.redstone.smartObserver;

import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlock;
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

public class SmartObserverGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return switch ((AttachFace)state.getValue((Property)SmartObserverBlock.TARGET)) {
            default -> throw new MatchException(null, null);
            case AttachFace.CEILING -> -90;
            case AttachFace.WALL -> 0;
            case AttachFace.FLOOR -> 90;
        };
    }

    @Override
    protected int getYRotation(BlockState state) {
        return this.horizontalAngle((Direction)state.getValue((Property)ThresholdSwitchBlock.FACING)) + 180;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return AssetLookup.forPowered(ctx, prov).apply(state);
    }
}
