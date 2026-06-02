/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder$PartBuilder
 */
package dev.simulated_team.simulated.content.blocks.directional_gearshift;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.directional_gearshift.DirectionalGearshiftBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class DirectionalGearshiftGenerator {
    public static <P extends DirectionalGearshiftBlock> void generate(DataGenContext<Block, P> context, RegistrateBlockstateProvider provider) {
        MultiPartBlockStateBuilder builder = provider.getMultipartBuilder((Block)context.get());
        for (BlockState state : ((DirectionalGearshiftBlock)((Object)context.get())).getStateDefinition().getPossibleStates()) {
            int xRot;
            boolean vertical;
            boolean alongFirst = (Boolean)state.getValue((Property)DirectionalGearshiftBlock.AXIS_ALONG_FIRST_COORDINATE);
            Direction direction = (Direction)state.getValue((Property)DirectionalGearshiftBlock.FACING);
            boolean leftOn = (Boolean)state.getValue((Property)DirectionalGearshiftBlock.LEFT_POWERED);
            boolean rightOn = (Boolean)state.getValue((Property)DirectionalGearshiftBlock.RIGHT_POWERED);
            boolean bl = direction.getAxis().isHorizontal() && direction.getAxis() == Direction.Axis.X == alongFirst ? true : (vertical = false);
            int n = direction == Direction.DOWN ? 270 : (xRot = direction == Direction.UP ? 90 : 0);
            int yRot = direction.getAxis().isVertical() ? (alongFirst ? 0 : 90) : (int)direction.toYRot();
            ((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(DirectionalGearshiftGenerator.model(provider, "middle", false, vertical)).rotationY(yRot).rotationX(xRot).addModel()).condition((Property)DirectionalGearshiftBlock.FACING, (Comparable[])new Direction[]{direction}).condition((Property)DirectionalGearshiftBlock.AXIS_ALONG_FIRST_COORDINATE, (Comparable[])new Boolean[]{alongFirst}).end().part().modelFile(DirectionalGearshiftGenerator.model(provider, "left", leftOn, vertical)).rotationY(yRot).rotationX(xRot).addModel()).condition((Property)DirectionalGearshiftBlock.LEFT_POWERED, (Comparable[])new Boolean[]{leftOn}).condition((Property)DirectionalGearshiftBlock.FACING, (Comparable[])new Direction[]{direction}).condition((Property)DirectionalGearshiftBlock.AXIS_ALONG_FIRST_COORDINATE, (Comparable[])new Boolean[]{alongFirst}).end().part().modelFile(DirectionalGearshiftGenerator.model(provider, "right", rightOn, vertical)).rotationY(yRot).rotationX(xRot).addModel()).condition((Property)DirectionalGearshiftBlock.RIGHT_POWERED, (Comparable[])new Boolean[]{rightOn}).condition((Property)DirectionalGearshiftBlock.FACING, (Comparable[])new Direction[]{direction}).condition((Property)DirectionalGearshiftBlock.AXIS_ALONG_FIRST_COORDINATE, (Comparable[])new Boolean[]{alongFirst}).end().part();
        }
    }

    private static ModelFile model(RegistrateBlockstateProvider p, String part, boolean powered, boolean vertical) {
        return p.models().getExistingFile(Simulated.path("block/directional_gearshift/" + (vertical ? "vertical/" : "horizontal/") + part + (powered ? "_powered" : "")));
    }
}
