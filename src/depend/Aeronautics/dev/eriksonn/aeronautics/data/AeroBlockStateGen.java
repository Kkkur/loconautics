/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.foundation.data.BlockStateGen
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.eriksonn.aeronautics.data;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class AeroBlockStateGen {
    public static <T extends DirectionalAxisKineticBlock> void directionalPoweredAxisBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        BlockStateGen.directionalAxisBlock(ctx, (RegistrateBlockstateProvider)prov, (blockState, vertical) -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + (vertical != false ? "vertical" : "horizontal") + ((Boolean)blockState.getValue((Property)BlockStateProperties.POWERED) != false ? "_powered" : ""))));
    }
}
