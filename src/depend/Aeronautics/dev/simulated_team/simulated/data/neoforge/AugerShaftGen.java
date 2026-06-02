/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel$Builder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder$PartBuilder
 */
package dev.simulated_team.simulated.data.neoforge;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class AugerShaftGen {
    public static ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> rotate(Direction direction, ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> builder) {
        builder.rotationX(direction.getAxis().isHorizontal() ? -90 : (direction == Direction.UP ? 180 : 0));
        builder.rotationY(direction.getAxis().isVertical() ? 0 : ((int)direction.toYRot() + 180) % 360);
        return builder;
    }

    public static <P extends AugerShaftBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> generate(String name, boolean cog) {
        return (c, p) -> {
            ModelFile axis_y = cog ? AugerShaftGen.sub(p, name, "cog_axis_y") : AugerShaftGen.sub(p, name, "axis_y");
            ModelFile connection_top = AugerShaftGen.sub(p, name, "connection_top");
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder((Block)c.get());
            for (Direction.Axis axis : Direction.Axis.values()) {
                ((MultiPartBlockStateBuilder.PartBuilder)AugerShaftGen.rotate(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis), (ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder>)builder.part().modelFile(axis_y)).addModel()).condition((Property)AugerShaftBlock.AXIS, (Comparable[])new Direction.Axis[]{axis}).condition((Property)AugerShaftBlock.ENCASED, (Comparable[])new Boolean[]{false}).end();
            }
            for (Direction.Axis axis : Direction.Axis.values()) {
                ((MultiPartBlockStateBuilder.PartBuilder)AugerShaftGen.rotate(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis), (ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder>)builder.part().modelFile(AugerShaftGen.sub(p, name, "axis_y_encased"))).addModel()).condition((Property)AugerShaftBlock.AXIS, (Comparable[])new Direction.Axis[]{axis}).condition((Property)AugerShaftBlock.ENCASED, (Comparable[])new Boolean[]{true}).end();
            }
            for (Direction.Axis axis : Direction.Axis.values()) {
                ((MultiPartBlockStateBuilder.PartBuilder)AugerShaftGen.rotate(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis), (ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder>)builder.part().modelFile(connection_top)).addModel()).condition((Property)AugerShaftBlock.AXIS, (Comparable[])new Direction.Axis[]{axis}).condition(AugerShaftBlock.SECTION, (Comparable[])new AugerShaftBlock.BarrelSection[]{AugerShaftBlock.BarrelSection.END, AugerShaftBlock.BarrelSection.SINGLE}).condition((Property)AugerShaftBlock.ENCASED, (Comparable[])new Boolean[]{false}).end();
                ((MultiPartBlockStateBuilder.PartBuilder)AugerShaftGen.rotate(Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis), (ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder>)builder.part().modelFile(connection_top)).addModel()).condition((Property)AugerShaftBlock.AXIS, (Comparable[])new Direction.Axis[]{axis}).condition(AugerShaftBlock.SECTION, (Comparable[])new AugerShaftBlock.BarrelSection[]{AugerShaftBlock.BarrelSection.FRONT, AugerShaftBlock.BarrelSection.SINGLE}).condition((Property)AugerShaftBlock.ENCASED, (Comparable[])new Boolean[]{false}).end();
            }
            if (!cog) {
                for (Direction.Axis axis : Direction.values()) {
                    ((MultiPartBlockStateBuilder.PartBuilder)AugerShaftGen.rotate(axis.getOpposite(), (ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder>)builder.part().modelFile(AugerShaftGen.sub(p, name, "bracket_top_" + axis.getAxis().getName()))).addModel()).condition((Property)AugerShaftBlock.PROPERTY_BY_DIRECTION.get(axis), (Comparable[])new Boolean[]{true}).condition((Property)AugerShaftBlock.ENCASED, (Comparable[])new Boolean[]{false}).end();
                }
            }
        };
    }

    private static ModelFile sub(RegistrateBlockstateProvider p, String name, String suffix) {
        return p.models().getExistingFile(Simulated.path("block/auger_shaft/" + suffix));
    }
}
