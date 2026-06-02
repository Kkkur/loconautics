/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.content.redstone.DirectedDirectionalBlock
 *  com.simibubi.create.foundation.data.BlockStateGen
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateItemModelProvider
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.AbstractFurnaceBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ItemModelBuilder
 */
package dev.simulated_team.simulated.data;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_inductor.RedstoneInductorBlock;
import dev.simulated_team.simulated.content.blocks.symmetric_sail.SymmetricSailBlock;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import dev.simulated_team.simulated.service.SimBlockStateService;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;

public class SimBlockStateGen {
    public static <T extends DirectionalAxisKineticBlock> void directionalKineticAxisBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        BlockStateGen.directionalAxisBlock(ctx, (RegistrateBlockstateProvider)prov, (blockState, vertical) -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + (vertical != false ? "vertical" : "horizontal"))));
    }

    public static <T extends Block> void facingPoweredAxisBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        prov.directionalBlock((Block)ctx.getEntry(), blockState -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block" + ((Boolean)blockState.getValue((Property)BlockStateProperties.POWERED) != false ? "_powered" : ""))));
    }

    public static <T extends Block> void facingBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, String modelPath) {
        prov.directionalBlock((Block)ctx.getEntry(), blockState -> prov.models().getExistingFile(prov.modLoc(modelPath)));
    }

    public static <T extends Block> void horizontalFacingLitBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        prov.horizontalBlock((Block)ctx.get(), blockState -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block" + ((Boolean)blockState.getValue((Property)AbstractFurnaceBlock.LIT) != false ? "_lit" : ""))));
    }

    public static <T extends Block> void redstoneInductorBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        prov.horizontalBlock((Block)ctx.getEntry(), blockState -> {
            boolean inverted = (Boolean)blockState.getValue((Property)RedstoneInductorBlock.INVERTED);
            return prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block" + (inverted ? "_inverted" : "") + ((Boolean)blockState.getValue((Property)BlockStateProperties.POWERED) != false ? "_powered" : "")));
        });
    }

    public static <T extends DirectionalAxisKineticBlock> void directionalPoweredAxisBlockstate(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        BlockStateGen.directionalAxisBlock(ctx, (RegistrateBlockstateProvider)prov, (blockState, vertical) -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + (vertical != false ? "vertical" : "horizontal") + ((Boolean)blockState.getValue((Property)BlockStateProperties.POWERED) != false ? "_powered" : ""))));
    }

    public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> coloredBlockItemModel(String texture, String ... folders) {
        return (c, p) -> {
            Object path = "block";
            for (String folder : folders) {
                path = (String)path + "/" + ("_".equals(folder) ? c.getName() : folder);
            }
            ((ItemModelBuilder)p.withExistingParent(c.getName(), p.modLoc((String)path))).texture("0", p.modLoc("block/" + texture));
        };
    }

    public static <T extends AbstractDirectionalAxisBlock> void directionalAxisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov) {
        SimBlockStateService.INSTANCE.directionalAxisBlock(ctx, prov, (blockState, vertical) -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + (vertical != false ? "vertical" : "horizontal"))));
    }

    public static XYHolder xySymmetricSail(BlockState state) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)SymmetricSailBlock.AXIS);
        return new XYHolder(axis == Direction.Axis.Y ? 0 : 90, axis == Direction.Axis.X ? 90 : (axis == Direction.Axis.Z ? 180 : 0));
    }

    public static XYHolder xyAltitudeSensor(BlockState state) {
        int yRot = ((int)((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).toYRot() + 180) % 360;
        int xRot = ((AttachFace)state.getValue((Property)BlockStateProperties.ATTACH_FACE)).ordinal() * 90;
        return new XYHolder(xRot, yRot);
    }

    public static <I extends BlockItem, P> NonNullFunction<ItemBuilder<I, P>, P> customItemModel(ResourceLocation path) {
        return b -> b.model(SimBlockStateGen.customBlockItemModel(path)).build();
    }

    public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> customBlockItemModel(ResourceLocation path) {
        return (c, p) -> p.withExistingParent(c.getName(), path);
    }

    public static XYHolder xyLaser(BlockState state) {
        Direction dir = (Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        int yRot = (int)((dir.getAxis().isVertical() ? 0.0f : dir.toYRot()) + 180.0f);
        int xRot = switch ((AttachFace)state.getValue((Property)DirectedDirectionalBlock.TARGET)) {
            default -> throw new MatchException(null, null);
            case AttachFace.CEILING -> -90;
            case AttachFace.WALL -> 0;
            case AttachFace.FLOOR -> 90;
        };
        return new XYHolder((xRot + 360) % 360, (yRot + 360) % 360);
    }

    public record XYHolder(int xRot, int yRot) {
    }
}
