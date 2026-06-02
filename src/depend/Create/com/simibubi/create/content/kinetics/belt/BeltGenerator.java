/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class BeltGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        Direction direction = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltSlope slope = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        return slope == BeltSlope.VERTICAL ? 90 : (slope == BeltSlope.SIDEWAYS && direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 180 : 0);
    }

    @Override
    protected int getYRotation(BlockState state) {
        Boolean casing = (Boolean)state.getValue((Property)BeltBlock.CASING);
        BeltSlope slope = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        boolean flip = slope == BeltSlope.UPWARD;
        boolean rotate = casing != false && slope == BeltSlope.VERTICAL;
        Direction direction = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        return this.horizontalAngle(direction) + (flip ? 180 : 0) + (rotate ? 90 : 0);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        boolean negative;
        Boolean casing = (Boolean)state.getValue((Property)BeltBlock.CASING);
        if (!casing.booleanValue()) {
            return prov.models().getExistingFile(prov.modLoc("block/belt/particle"));
        }
        BeltPart part = (BeltPart)((Object)state.getValue(BeltBlock.PART));
        Direction direction = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltSlope slope = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        boolean downward = slope == BeltSlope.DOWNWARD;
        boolean diagonal = slope == BeltSlope.UPWARD || downward;
        boolean vertical = slope == BeltSlope.VERTICAL;
        boolean pulley = part == BeltPart.PULLEY;
        boolean sideways = slope == BeltSlope.SIDEWAYS;
        boolean bl = negative = direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        if (!casing.booleanValue() && pulley) {
            part = BeltPart.MIDDLE;
        }
        if ((vertical && negative || downward || sideways && negative) && part != BeltPart.MIDDLE && !pulley) {
            BeltPart beltPart = part = part == BeltPart.END ? BeltPart.START : BeltPart.END;
        }
        if (!casing.booleanValue() && vertical) {
            slope = BeltSlope.HORIZONTAL;
        }
        if (casing.booleanValue() && vertical) {
            slope = BeltSlope.SIDEWAYS;
        }
        String path = "block/" + (casing != false ? "belt_casing/" : "belt/");
        String slopeName = slope.getSerializedName();
        String partName = part.getSerializedName();
        if (diagonal) {
            slopeName = "diagonal";
        }
        ResourceLocation location = prov.modLoc(path + slopeName + "_" + partName);
        return prov.models().getExistingFile(location);
    }
}
