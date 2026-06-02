/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonnullType
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.Pointing
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.BlockModelProvider
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel
 *  net.neoforged.neoforge.client.model.generators.ModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelBuilder$ElementBuilder$FaceBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.client.model.generators.ModelFile$ExistingModelFile
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder$PartBuilder
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.foundation.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.chassis.LinearChassisBlock;
import com.simibubi.create.content.contraptions.chassis.RadialChassisBlock;
import com.simibubi.create.content.contraptions.mounted.CartAssembleRailType;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleExtenderBlock;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonnullType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.Pointing;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class BlockStateGen {
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> axisBlockProvider(boolean customItem) {
        return (c, p) -> BlockStateGen.axisBlock(c, p, BlockStateGen.getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockProvider(boolean customItem) {
        return (c, p) -> p.directionalBlock((Block)c.get(), BlockStateGen.getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalBlockProviderIgnoresWaterlogged(boolean customItem) {
        return (c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, BlockStateGen.getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalBlockProvider(boolean customItem) {
        return (c, p) -> p.horizontalBlock((Block)c.get(), BlockStateGen.getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalAxisBlockProvider(boolean customItem) {
        return (c, p) -> BlockStateGen.horizontalAxisBlock(c, p, BlockStateGen.getBlockModel(customItem, c, p));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simpleCubeAll(String path) {
        return (c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().cubeAll(c.getName(), p.modLoc("block/" + path)));
    }

    public static <T extends DirectionalAxisKineticBlock> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> directionalAxisBlockProvider() {
        return (c, p) -> BlockStateGen.directionalAxisBlock(c, p, ($, vertical) -> p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/" + (vertical != false ? "vertical" : "horizontal"))));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalWheelProvider(boolean customItem) {
        return (c, p) -> BlockStateGen.horizontalWheel(c, p, BlockStateGen.getBlockModel(customItem, c, p));
    }

    private static <T extends Block> Function<BlockState, ModelFile> getBlockModel(boolean customItem, DataGenContext<Block, T> c, RegistrateBlockstateProvider p) {
        return $ -> customItem ? AssetLookup.partialBaseModel(c, p, new String[0]) : AssetLookup.standardModel(c, p);
    }

    public static <T extends Block> void directionalBlockIgnoresWaterlogged(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStatesExcept(state -> {
            Direction dir = (Direction)state.getValue((Property)BlockStateProperties.FACING);
            return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).rotationX(dir == Direction.DOWN ? 180 : (dir.getAxis().isHorizontal() ? 90 : 0)).rotationY(dir.getAxis().isVertical() ? 0 : ((int)dir.toYRot() + 180) % 360).build();
        }, new Property[]{BlockStateProperties.WATERLOGGED});
    }

    public static <T extends Block> void axisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        BlockStateGen.axisBlock(ctx, prov, modelFunc, false);
    }

    public static <T extends Block> void axisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc, boolean uvLock) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStatesExcept(state -> {
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)BlockStateProperties.AXIS);
            return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).uvLock(uvLock).rotationX(axis == Direction.Axis.Y ? 0 : 90).rotationY(axis == Direction.Axis.X ? 90 : (axis == Direction.Axis.Z ? 180 : 0)).build();
        }, new Property[]{BlockStateProperties.WATERLOGGED});
    }

    public static <T extends Block> void simpleBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).build(), new Property[]{BlockStateProperties.WATERLOGGED});
    }

    public static <T extends Block> void horizontalAxisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStates(state -> {
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)BlockStateProperties.HORIZONTAL_AXIS);
            return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).rotationY(axis == Direction.Axis.X ? 90 : 0).build();
        });
    }

    public static <T extends DirectionalAxisKineticBlock> void directionalAxisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BiFunction<BlockState, Boolean, ModelFile> modelFunc) {
        prov.getVariantBuilder((Block)ctx.getEntry()).forAllStates(state -> {
            int xRot;
            boolean vertical;
            boolean alongFirst = (Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
            Direction direction = (Direction)state.getValue((Property)DirectionalAxisKineticBlock.FACING);
            boolean bl = direction.getAxis().isHorizontal() && direction.getAxis() == Direction.Axis.X == alongFirst ? true : (vertical = false);
            int n = direction == Direction.DOWN ? 270 : (xRot = direction == Direction.UP ? 90 : 0);
            int yRot = direction.getAxis().isVertical() ? (alongFirst ? 0 : 90) : (int)direction.toYRot();
            return ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state, vertical)).rotationX(xRot).rotationY(yRot).build();
        });
    }

    public static <T extends Block> void horizontalWheel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> modelFunc) {
        prov.getVariantBuilder((Block)ctx.get()).forAllStates(state -> ConfiguredModel.builder().modelFile((ModelFile)modelFunc.apply((BlockState)state)).rotationX(90).rotationY(((int)((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).toYRot() + 180) % 360).build());
    }

    public static <T extends Block> void cubeAll(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, String textureSubDir) {
        BlockStateGen.cubeAll(ctx, prov, textureSubDir, ctx.getName());
    }

    public static <T extends Block> void cubeAll(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, String textureSubDir, String name) {
        String texturePath = "block/" + textureSubDir + name;
        prov.simpleBlock((Block)ctx.get(), (ModelFile)prov.models().cubeAll(ctx.getName(), prov.modLoc(texturePath)));
    }

    public static NonNullBiConsumer<DataGenContext<Block, CartAssemblerBlock>, RegistrateBlockstateProvider> cartAssembler() {
        return (c, p) -> p.getVariantBuilder((Block)c.get()).forAllStates(state -> {
            int yRotation;
            CartAssembleRailType type = (CartAssembleRailType)((Object)((Object)((Object)state.getValue(CartAssemblerBlock.RAIL_TYPE))));
            Boolean powered = (Boolean)state.getValue((Property)CartAssemblerBlock.POWERED);
            Boolean backwards = (Boolean)state.getValue((Property)CartAssemblerBlock.BACKWARDS);
            RailShape shape = (RailShape)state.getValue(CartAssemblerBlock.RAIL_SHAPE);
            int n = yRotation = shape == RailShape.EAST_WEST ? 270 : 0;
            if (backwards.booleanValue()) {
                yRotation += 180;
            }
            return ConfiguredModel.builder().modelFile((ModelFile)p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block_" + type.getSerializedName() + (powered != false ? "_powered" : "")))).rotationY(yRotation % 360).build();
        });
    }

    public static NonNullBiConsumer<DataGenContext<Block, BlazeBurnerBlock>, RegistrateBlockstateProvider> blazeHeater() {
        return (c, p) -> ConfiguredModel.builder().modelFile((ModelFile)p.models().getExistingFile(p.modLoc("block/" + c.getName() + "/block"))).build();
    }

    public static <B extends LinearChassisBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> linearChassis() {
        return (c, p) -> {
            ResourceLocation side = p.modLoc("block/" + c.getName() + "_side");
            ResourceLocation top = p.modLoc("block/linear_chassis_end");
            ResourceLocation top_sticky = p.modLoc("block/linear_chassis_end_sticky");
            ArrayList<ModelBuilder> models = new ArrayList<ModelBuilder>(4);
            for (boolean isTopSticky : Iterate.trueAndFalse) {
                for (boolean isBottomSticky : Iterate.trueAndFalse) {
                    models.add(((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)p.models().withExistingParent(c.getName() + (isTopSticky ? "_top" : "") + (isBottomSticky ? "_bottom" : ""), "block/cube_bottom_top")).texture("side", side)).texture("bottom", isBottomSticky ? top_sticky : top)).texture("top", isTopSticky ? top_sticky : top));
                }
            }
            BiFunction<Boolean, Boolean, ModelFile> modelFunc = (t, b) -> (ModelFile)models.get((t != false ? 0 : 2) + (b != false ? 0 : 1));
            BlockStateGen.axisBlock(c, p, state -> (ModelFile)modelFunc.apply((Boolean)state.getValue((Property)LinearChassisBlock.STICKY_TOP), (Boolean)state.getValue((Property)LinearChassisBlock.STICKY_BOTTOM)));
        };
    }

    public static <B extends RadialChassisBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> radialChassis() {
        return (c, p) -> {
            String suffix;
            String path = "block/" + c.getName();
            ResourceLocation side = p.modLoc(path + "_side");
            ResourceLocation side_sticky = p.modLoc(path + "_side_sticky");
            String templateModelPath = "block/radial_chassis";
            ModelFile.ExistingModelFile base = p.models().getExistingFile(p.modLoc(templateModelPath + "/base"));
            ArrayList<ModelBuilder> faces = new ArrayList<ModelBuilder>(3);
            ArrayList<ModelBuilder> stickyFaces = new ArrayList<ModelBuilder>(3);
            for (Direction.Axis axis : Iterate.axes) {
                suffix = "side_" + axis.getSerializedName();
                faces.add(((BlockModelBuilder)p.models().withExistingParent("block/" + c.getName() + "_" + suffix, p.modLoc(templateModelPath + "/" + suffix))).texture("side", side));
            }
            for (Direction.Axis axis : Iterate.axes) {
                suffix = "side_" + axis.getSerializedName();
                stickyFaces.add(((BlockModelBuilder)p.models().withExistingParent("block/" + c.getName() + "_" + suffix + "_sticky", p.modLoc(templateModelPath + "/" + suffix))).texture("side", side_sticky));
            }
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder((Block)c.get());
            BlockState propertyGetter = (BlockState)((RadialChassisBlock)c.get()).defaultBlockState().setValue((Property)RadialChassisBlock.AXIS, (Comparable)Direction.Axis.Y);
            for (Direction.Axis axis : Iterate.axes) {
                ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile((ModelFile)base).rotationX(axis != Direction.Axis.Y ? 90 : 0).rotationY(axis != Direction.Axis.X ? 0 : 90).addModel()).condition((Property)RadialChassisBlock.AXIS, (Comparable[])new Direction.Axis[]{axis}).end();
            }
            for (Direction.Axis axis : Iterate.horizontalDirections) {
                for (boolean sticky : Iterate.trueAndFalse) {
                    for (Direction.Axis axis2 : Iterate.axes) {
                        int horizontalAngle = (int)axis.toYRot();
                        int index = axis2.ordinal();
                        int xRot = 0;
                        int yRot = 0;
                        if (axis2 == Direction.Axis.X) {
                            xRot = -horizontalAngle + 180;
                        }
                        if (axis2 == Direction.Axis.Y) {
                            yRot = horizontalAngle;
                        }
                        if (axis2 == Direction.Axis.Z) {
                            yRot = -horizontalAngle + 270;
                            if (axis.getAxis() == Direction.Axis.Z) {
                                index = 0;
                                xRot = horizontalAngle + 180;
                                yRot = 90;
                            }
                        }
                        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile((ModelFile)(sticky ? stickyFaces : faces).get(index)).rotationX((xRot + 360) % 360).rotationY((yRot + 360) % 360).addModel()).condition((Property)RadialChassisBlock.AXIS, (Comparable[])new Direction.Axis[]{axis2}).condition((Property)((RadialChassisBlock)c.get()).getGlueableSide(propertyGetter, (Direction)axis), (Comparable[])new Boolean[]{sticky}).end();
                    }
                }
            }
        };
    }

    public static <P extends Block> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> naturalStoneTypeBlock(String type) {
        return (c, p) -> {
            ConfiguredModel[] variants = new ConfiguredModel[4];
            for (int i = 0; i < variants.length; ++i) {
                variants[i] = ConfiguredModel.builder().modelFile((ModelFile)p.models().cubeAll(type + "_natural_" + i, p.modLoc("block/palettes/stone_types/natural/" + type + "_" + i))).buildLast();
            }
            p.getVariantBuilder((Block)c.get()).partialState().setModels(variants);
        };
    }

    public static <P extends EncasedPipeBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> encasedPipe() {
        return (c, p) -> {
            ModelFile open = AssetLookup.partialBaseModel(c, p, "open");
            ModelFile flat = AssetLookup.partialBaseModel(c, p, "flat");
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder((Block)c.get());
            for (boolean flatPass : Iterate.trueAndFalse) {
                for (Direction d : Iterate.directions) {
                    int verticalAngle = d == Direction.UP ? 90 : (d == Direction.DOWN ? -90 : 0);
                    ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(flatPass ? flat : open).rotationX(verticalAngle).rotationY((int)(d.toYRot() + (float)(d.getAxis().isVertical() ? 90 : 0)) % 360).addModel()).condition((Property)EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(d), (Comparable[])new Boolean[]{!flatPass}).end();
                }
            }
        };
    }

    public static <P extends TrapDoorBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> uvLockedTrapdoorBlock(P block, ModelFile bottom, ModelFile top, ModelFile open) {
        return (c, p) -> p.getVariantBuilder((Block)block).forAllStatesExcept(state -> {
            int xRot = 0;
            int yRot = (int)((Direction)state.getValue((Property)TrapDoorBlock.FACING)).toYRot() + 180;
            boolean isOpen = (Boolean)state.getValue((Property)TrapDoorBlock.OPEN);
            if (!isOpen) {
                yRot = 0;
            }
            return ConfiguredModel.builder().modelFile(isOpen ? open : (state.getValue((Property)TrapDoorBlock.HALF) == Half.TOP ? top : bottom)).rotationX(xRot).rotationY(yRot %= 360).uvLock(!isOpen).build();
        }, new Property[]{TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED});
    }

    public static <P extends WhistleExtenderBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> whistleExtender() {
        return (c, p) -> {
            BlockModelProvider models = p.models();
            String basePath = "block/steam_whistle/extension/";
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder((Block)c.get());
            for (WhistleBlock.WhistleSize size : WhistleBlock.WhistleSize.values()) {
                String basePathSize = basePath + size.getSerializedName() + "_";
                ModelFile.ExistingModelFile topRim = models.getExistingFile(Create.asResource(basePathSize + "top_rim"));
                ModelFile.ExistingModelFile single = models.getExistingFile(Create.asResource(basePathSize + "single"));
                ModelFile.ExistingModelFile double_ = models.getExistingFile(Create.asResource(basePathSize + "double"));
                ((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile((ModelFile)topRim).addModel()).condition(WhistleExtenderBlock.SIZE, (Comparable[])new WhistleBlock.WhistleSize[]{size}).condition(WhistleExtenderBlock.SHAPE, (Comparable[])new WhistleExtenderBlock.WhistleExtenderShape[]{WhistleExtenderBlock.WhistleExtenderShape.DOUBLE}).end().part().modelFile((ModelFile)single).addModel()).condition(WhistleExtenderBlock.SIZE, (Comparable[])new WhistleBlock.WhistleSize[]{size}).condition(WhistleExtenderBlock.SHAPE, (Comparable[])new WhistleExtenderBlock.WhistleExtenderShape[]{WhistleExtenderBlock.WhistleExtenderShape.SINGLE}).end().part().modelFile((ModelFile)double_).addModel()).condition(WhistleExtenderBlock.SIZE, (Comparable[])new WhistleBlock.WhistleSize[]{size}).condition(WhistleExtenderBlock.SHAPE, (Comparable[])new WhistleExtenderBlock.WhistleExtenderShape[]{WhistleExtenderBlock.WhistleExtenderShape.DOUBLE, WhistleExtenderBlock.WhistleExtenderShape.DOUBLE_CONNECTED}).end();
            }
        };
    }

    public static <P extends FluidPipeBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> pipe() {
        return (c, p) -> {
            String path = "block/" + c.getName();
            String LU = "lu";
            String RU = "ru";
            String LD = "ld";
            String RD = "rd";
            String LR = "lr";
            String UD = "ud";
            String U = "u";
            String D = "d";
            String L = "l";
            String R = "r";
            ImmutableList orientations = ImmutableList.of((Object)LU, (Object)RU, (Object)LD, (Object)RD, (Object)LR, (Object)UD, (Object)U, (Object)D, (Object)L, (Object)R);
            ImmutableMap uvs = ImmutableMap.builder().put((Object)LU, (Object)Pair.of((Object)12, (Object)4)).put((Object)RU, (Object)Pair.of((Object)8, (Object)4)).put((Object)LD, (Object)Pair.of((Object)12, (Object)0)).put((Object)RD, (Object)Pair.of((Object)8, (Object)0)).put((Object)LR, (Object)Pair.of((Object)4, (Object)8)).put((Object)UD, (Object)Pair.of((Object)0, (Object)8)).put((Object)U, (Object)Pair.of((Object)4, (Object)4)).put((Object)D, (Object)Pair.of((Object)0, (Object)0)).put((Object)L, (Object)Pair.of((Object)4, (Object)0)).put((Object)R, (Object)Pair.of((Object)0, (Object)4)).build();
            IdentityHashMap<Direction.Axis, ResourceLocation> coreTemplates = new IdentityHashMap<Direction.Axis, ResourceLocation>();
            HashMap<Pair<String, Direction.Axis>, ModelFile> coreModels = new HashMap<Pair<String, Direction.Axis>, ModelFile>();
            for (Direction.Axis axis : Iterate.axes) {
                coreTemplates.put(axis, p.modLoc(path + "/core_" + axis.getSerializedName()));
            }
            for (Direction.Axis axis : Iterate.axes) {
                ResourceLocation parent = (ResourceLocation)coreTemplates.get(axis);
                for (String s : orientations) {
                    Pair key = Pair.of((Object)s, (Object)axis);
                    String modelName = path + "/" + s + "_" + axis.getSerializedName();
                    coreModels.put((Pair<String, Direction.Axis>)key, (ModelFile)((BlockModelBuilder)p.models().withExistingParent(modelName, parent)).element().from(4.0f, 4.0f, 4.0f).to(12.0f, 12.0f, 12.0f).face(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis)).end().face(Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis)).end().faces((arg_0, arg_1) -> BlockStateGen.lambda$pipe$28((Map)uvs, s, arg_0, arg_1)).end());
                }
            }
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder((Block)c.get());
            for (Direction.Axis axis : Iterate.axes) {
                BlockStateGen.putPart(coreModels, builder, axis, LU, true, false, true, false);
                BlockStateGen.putPart(coreModels, builder, axis, RU, true, false, false, true);
                BlockStateGen.putPart(coreModels, builder, axis, LD, false, true, true, false);
                BlockStateGen.putPart(coreModels, builder, axis, RD, false, true, false, true);
                BlockStateGen.putPart(coreModels, builder, axis, UD, true, true, false, false);
                BlockStateGen.putPart(coreModels, builder, axis, U, true, false, false, false);
                BlockStateGen.putPart(coreModels, builder, axis, D, false, true, false, false);
                BlockStateGen.putPart(coreModels, builder, axis, LR, false, false, true, true);
                BlockStateGen.putPart(coreModels, builder, axis, L, false, false, true, false);
                BlockStateGen.putPart(coreModels, builder, axis, R, false, false, false, true);
            }
        };
    }

    private static void putPart(Map<Pair<String, Direction.Axis>, ModelFile> coreModels, MultiPartBlockStateBuilder builder, Direction.Axis axis, String s, boolean up, boolean down, boolean left, boolean right) {
        Direction positiveAxis = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        Map propertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;
        Direction upD = Pointing.UP.getCombinedDirection(positiveAxis);
        Direction leftD = Pointing.LEFT.getCombinedDirection(positiveAxis);
        Direction rightD = Pointing.RIGHT.getCombinedDirection(positiveAxis);
        Direction downD = Pointing.DOWN.getCombinedDirection(positiveAxis);
        if (axis == Direction.Axis.Y || axis == Direction.Axis.X) {
            leftD = leftD.getOpposite();
            rightD = rightD.getOpposite();
        }
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(coreModels.get(Pair.of((Object)s, (Object)axis))).addModel()).condition((Property)propertyMap.get(upD), (Comparable[])new Boolean[]{up}).condition((Property)propertyMap.get(leftD), (Comparable[])new Boolean[]{left}).condition((Property)propertyMap.get(rightD), (Comparable[])new Boolean[]{right}).condition((Property)propertyMap.get(downD), (Comparable[])new Boolean[]{down}).end();
    }

    public static Function<BlockState, ConfiguredModel[]> mapToAir(@NonnullType RegistrateBlockstateProvider p) {
        return state -> ConfiguredModel.builder().modelFile((ModelFile)p.models().getExistingFile(p.mcLoc("block/air"))).build();
    }

    private static /* synthetic */ void lambda$pipe$28(Map uvs, String s, Direction d, ModelBuilder.ElementBuilder.FaceBuilder builder) {
        Pair pair = (Pair)uvs.get(s);
        float u = ((Integer)pair.getKey()).intValue();
        float v = ((Integer)pair.getValue()).intValue();
        if (d == Direction.UP) {
            builder.uvs(u + 4.0f, v + 4.0f, u, v);
        }
        if (d == Direction.DOWN) {
            builder.uvs(u + 4.0f, v, u, v + 4.0f);
        }
        if (d == Direction.NORTH) {
            builder.uvs(u, v, u + 4.0f, v + 4.0f);
        }
        if (d == Direction.SOUTH) {
            builder.uvs(u + 4.0f, v, u, v + 4.0f);
        }
        if (d == Direction.EAST) {
            builder.uvs(u, v, u + 4.0f, v + 4.0f);
        }
        if (d == Direction.WEST) {
            builder.uvs(u + 4.0f, v, u, v + 4.0f);
        }
        builder.texture("#0");
    }
}
