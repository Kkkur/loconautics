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
package dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.SimplePropellerVisual;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite.AndesitePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite.AndesitePropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class AndesitePropellerVisual
extends SimplePropellerVisual<AndesitePropellerBlockEntity> {
    public AndesitePropellerVisual(VisualizationContext context, AndesitePropellerBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @Override
    public PartialModel getModel(BlockState state) {
        return (Boolean)state.getValue((Property)BasePropellerBlock.REVERSED) != false ? AeroPartialModels.ANDESITE_PROPELLER_REVERSED : AeroPartialModels.ANDESITE_PROPELLER;
    }

    @Override
    public float getAngle(float partialTicks) {
        BlockState state = ((AndesitePropellerBlockEntity)this.blockEntity).getBlockState();
        BlockPos pos = ((AndesitePropellerBlockEntity)this.blockEntity).getBlockPos();
        return super.getAngle(partialTicks) + AndesitePropellerVisual.rotationOffset((BlockState)state, (Direction.Axis)((Direction)state.getValue((Property)AndesitePropellerBlock.FACING)).getAxis(), (Vec3i)pos);
    }
}
