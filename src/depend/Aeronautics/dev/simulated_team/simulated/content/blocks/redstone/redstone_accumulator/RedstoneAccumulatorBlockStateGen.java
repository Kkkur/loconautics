/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel$Builder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder$PartBuilder
 */
package dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator.RedstoneAccumulatorBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class RedstoneAccumulatorBlockStateGen {
    public static ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> rotateHorizontal(Direction direction, ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> builder) {
        boolean angleOffset = false;
        builder.rotationY(((int)direction.toYRot() + 0) % 360);
        return builder;
    }

    public static <P extends RedstoneAccumulatorBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> generate() {
        return (ctx, prov) -> {
            ModelFile backOff = RedstoneAccumulatorBlockStateGen.sub(prov, "block_back_off");
            ModelFile backOn = RedstoneAccumulatorBlockStateGen.sub(prov, "block_back_on");
            ModelFile front = RedstoneAccumulatorBlockStateGen.sub(prov, "block_front");
            ModelFile middleOff = RedstoneAccumulatorBlockStateGen.sub(prov, "block_middle_off");
            ModelFile middleOn = RedstoneAccumulatorBlockStateGen.sub(prov, "block_middle_on");
            ModelFile torchOff = RedstoneAccumulatorBlockStateGen.sub(prov, "torch_off");
            ModelFile torchOn = RedstoneAccumulatorBlockStateGen.sub(prov, "torch_on");
            MultiPartBlockStateBuilder builder = prov.getMultipartBuilder((Block)ctx.get());
            for (BlockState state : ((RedstoneAccumulatorBlock)ctx.get()).getStateDefinition().getPossibleStates()) {
                Direction facing = (Direction)state.getValue((Property)RedstoneAccumulatorBlock.FACING);
                boolean powered = (Boolean)state.getValue((Property)RedstoneAccumulatorBlock.POWERED);
                boolean sidePowered = (Boolean)state.getValue((Property)RedstoneAccumulatorBlock.SIDE_POWERED);
                boolean powering = (Boolean)state.getValue((Property)RedstoneAccumulatorBlock.POWERING);
                int yRot = (int)facing.getOpposite().toYRot();
                ((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(front).rotationY(yRot).addModel()).condition((Property)RedstoneAccumulatorBlock.FACING, (Comparable[])new Direction[]{facing}).end().part().modelFile(powered ? backOn : backOff).rotationY(yRot).addModel()).condition((Property)RedstoneAccumulatorBlock.POWERED, (Comparable[])new Boolean[]{powered}).condition((Property)RedstoneAccumulatorBlock.FACING, (Comparable[])new Direction[]{facing}).end().part().modelFile(sidePowered ? middleOn : middleOff).rotationY(yRot).addModel()).condition((Property)RedstoneAccumulatorBlock.SIDE_POWERED, (Comparable[])new Boolean[]{sidePowered}).condition((Property)RedstoneAccumulatorBlock.FACING, (Comparable[])new Direction[]{facing}).end().part().modelFile(powering ? torchOn : torchOff).rotationY(yRot).addModel()).condition((Property)RedstoneAccumulatorBlock.POWERING, (Comparable[])new Boolean[]{powering}).condition((Property)RedstoneAccumulatorBlock.FACING, (Comparable[])new Direction[]{facing}).end();
            }
        };
    }

    private static ModelFile sub(RegistrateBlockstateProvider p, String suffix) {
        return p.models().getExistingFile(Simulated.path("block/redstone_accumulator/" + suffix));
    }
}
