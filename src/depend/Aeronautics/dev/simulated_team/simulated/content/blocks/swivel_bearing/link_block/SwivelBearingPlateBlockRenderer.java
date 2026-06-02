/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing.link_block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlock;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.link_block.SwivelBearingPlateBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class SwivelBearingPlateBlockRenderer
extends KineticBlockEntityRenderer<SwivelBearingPlateBlockEntity> {
    public SwivelBearingPlateBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected SuperByteBuffer getRotatedModel(SwivelBearingPlateBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)SimPartialModels.SHAFT_SIXTEENTH, (BlockState)state, (Direction)((Direction)state.getValue((Property)SwivelBearingBlock.FACING)));
    }
}
