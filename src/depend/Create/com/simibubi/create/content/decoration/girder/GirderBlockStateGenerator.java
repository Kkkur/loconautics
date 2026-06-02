/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder$PartBuilder
 */
package com.simibubi.create.content.decoration.girder;

import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class GirderBlockStateGenerator {
    public static void blockStateWithShaft(DataGenContext<Block, GirderEncasedShaftBlock> c, RegistrateBlockstateProvider p) {
        MultiPartBlockStateBuilder builder = p.getMultipartBuilder((Block)c.get());
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, new String[0])).rotationY(0).addModel()).condition(GirderEncasedShaftBlock.HORIZONTAL_AXIS, (Comparable[])new Direction.Axis[]{Direction.Axis.Z}).end();
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, new String[0])).rotationY(90).addModel()).condition(GirderEncasedShaftBlock.HORIZONTAL_AXIS, (Comparable[])new Direction.Axis[]{Direction.Axis.X}).end();
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, "top")).addModel()).condition((Property)GirderEncasedShaftBlock.TOP, (Comparable[])new Boolean[]{true}).end();
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, "bottom")).addModel()).condition((Property)GirderEncasedShaftBlock.BOTTOM, (Comparable[])new Boolean[]{true}).end();
    }

    public static void blockState(DataGenContext<Block, GirderBlock> c, RegistrateBlockstateProvider p) {
        MultiPartBlockStateBuilder builder = p.getMultipartBuilder((Block)c.get());
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, "pole")).addModel()).condition((Property)GirderBlock.X, (Comparable[])new Boolean[]{false}).condition((Property)GirderBlock.Z, (Comparable[])new Boolean[]{false}).end();
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, "x")).addModel()).condition((Property)GirderBlock.X, (Comparable[])new Boolean[]{true}).end();
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, "z")).addModel()).condition((Property)GirderBlock.Z, (Comparable[])new Boolean[]{true}).end();
        for (boolean x : Iterate.trueAndFalse) {
            ((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, "top")).addModel()).condition((Property)GirderBlock.TOP, (Comparable[])new Boolean[]{true}).condition((Property)GirderBlock.X, (Comparable[])new Boolean[]{x}).condition((Property)GirderBlock.Z, (Comparable[])new Boolean[]{!x}).end().part().modelFile(AssetLookup.partialBaseModel(c, p, "bottom")).addModel()).condition((Property)GirderBlock.BOTTOM, (Comparable[])new Boolean[]{true}).condition((Property)GirderBlock.X, (Comparable[])new Boolean[]{x}).condition((Property)GirderBlock.Z, (Comparable[])new Boolean[]{!x}).end();
        }
        ((MultiPartBlockStateBuilder.PartBuilder)builder.part().modelFile(AssetLookup.partialBaseModel(c, p, "cross")).addModel()).condition((Property)GirderBlock.X, (Comparable[])new Boolean[]{true}).condition((Property)GirderBlock.Z, (Comparable[])new Boolean[]{true}).end();
    }
}
