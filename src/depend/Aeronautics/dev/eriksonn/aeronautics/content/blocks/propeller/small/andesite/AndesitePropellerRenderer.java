/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.SimplePropellerRenderer;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite.AndesitePropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;

public class AndesitePropellerRenderer
extends SimplePropellerRenderer<AndesitePropellerBlockEntity> {
    public AndesitePropellerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public PartialModel getCurrentModel(AndesitePropellerBlockEntity be) {
        return (Boolean)be.getBlockState().getValue((Property)BasePropellerBlock.REVERSED) != false ? AeroPartialModels.ANDESITE_PROPELLER_REVERSED : AeroPartialModels.ANDESITE_PROPELLER;
    }

    @Override
    public float getAngle(float partialTicks, Direction dir, AndesitePropellerBlockEntity be) {
        return super.getAngle(partialTicks, dir, be) + AndesitePropellerRenderer.getRotationOffsetForPosition((KineticBlockEntity)be, (BlockPos)be.getBlockPos(), (Direction.Axis)dir.getAxis());
    }
}
