/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small.wooden;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.SimplePropellerVisual;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite.AndesitePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.wooden.WoodenPropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class WoodenPropellerVisual
extends SimplePropellerVisual<WoodenPropellerBlockEntity> {
    public WoodenPropellerVisual(VisualizationContext context, WoodenPropellerBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @Override
    public PartialModel getModel(BlockState state) {
        return (Boolean)state.getValue((Property)BasePropellerBlock.REVERSED) != false ? AeroPartialModels.WOODEN_PROPELLER_REVERSED : AeroPartialModels.WOODEN_PROPELLER;
    }

    @Override
    public float getAngle(float partialTicks) {
        BlockState state = ((WoodenPropellerBlockEntity)this.blockEntity).getBlockState();
        BlockPos pos = ((WoodenPropellerBlockEntity)this.blockEntity).getBlockPos();
        return super.getAngle(partialTicks) + WoodenPropellerVisual.rotationOffset((BlockState)state, (Direction.Axis)((Direction)state.getValue((Property)AndesitePropellerBlock.FACING)).getAxis(), (Vec3i)pos);
    }
}
