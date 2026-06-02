/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package dev.simulated_team.simulated.neoforge.service;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlock;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import dev.simulated_team.simulated.data.SimBlockStateGen;
import dev.simulated_team.simulated.data.neoforge.AugerShaftGen;
import dev.simulated_team.simulated.service.SimBlockStateService;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class NeoForgeSimBlockStateService
implements SimBlockStateService {
    @Override
    public <T extends Block> void genericModelBuilder(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, SimBlockStateGen.XYHolder> xyGetter, Function<BlockState, Object> modelGetter) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStates(state -> {
            Object patt0$temp = modelGetter.apply((BlockState)state);
            if (patt0$temp instanceof ModelFile) {
                ModelFile model = (ModelFile)patt0$temp;
                SimBlockStateGen.XYHolder rotations = (SimBlockStateGen.XYHolder)xyGetter.apply((BlockState)state);
                return ConfiguredModel.builder().modelFile(model).rotationX(rotations.xRot()).rotationY(rotations.yRot()).build();
            }
            throw new IllegalArgumentException("ModelGetter must return a ModelFile");
        });
    }

    @Override
    public <P extends AugerShaftBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> augerShaftGenerate(String name, boolean cog) {
        return AugerShaftGen.generate(name, cog);
    }

    @Override
    public <T extends AbstractDirectionalAxisBlock> void directionalAxisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BiFunction<BlockState, Boolean, Object> modelFunc) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStates(state -> {
            int xRot;
            boolean vertical;
            boolean alongFirst = (Boolean)state.getValue((Property)AbstractDirectionalAxisBlock.AXIS_ALONG_FIRST_COORDINATE);
            Direction direction = (Direction)state.getValue((Property)AbstractDirectionalAxisBlock.FACING);
            boolean bl = direction.getAxis().isHorizontal() && direction.getAxis() == Direction.Axis.X == alongFirst ? true : (vertical = false);
            int n = direction == Direction.DOWN ? 270 : (xRot = direction == Direction.UP ? 90 : 0);
            int yRot = direction.getAxis().isVertical() ? (alongFirst ? 0 : 90) : (int)direction.toYRot();
            Object model = modelFunc.apply((BlockState)state, vertical);
            if (!(model instanceof ModelFile)) {
                throw new AssertionError((Object)"Required Model file!");
            }
            ModelFile m = (ModelFile)model;
            return ConfiguredModel.builder().modelFile(m).rotationX(xRot).rotationY(yRot).build();
        });
    }
}
