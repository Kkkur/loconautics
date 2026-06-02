/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class ModulatingLinkedReceiverRenderer
extends SmartBlockEntityRenderer<ModulatingLinkedReceiverBlockEntity> {
    public ModulatingLinkedReceiverRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(ModulatingLinkedReceiverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        super.renderSafe((SmartBlockEntity)be, partialTicks, ms, bufferSource, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe((SmartBlockEntity)be, partialTicks, ms, bufferSource, light, overlay);
        Direction facing = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.FACING);
        Vec3 pixelNormal = new Vec3(facing.step()).scale(0.0625);
        float minPos = 5.5f * ((float)(be.minRange - 1) * 275.0f) / (255.0f * (20.0f + (float)be.minRange - 1.0f));
        float maxPos = 5.5f * ((float)(be.maxRange - 1) * 275.0f) / (255.0f * (20.0f + (float)be.maxRange - 1.0f));
        for (boolean bottom : Iterate.trueAndFalse) {
            SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)SimPartialModels.MODULATING_RECEIVER_PLATE, (BlockState)be.getBlockState());
            if (bottom) {
                superBuffer.translate(pixelNormal.scale((double)minPos));
            } else {
                superBuffer.translate(pixelNormal.scale(0.5 + (double)maxPos));
            }
            if (facing.getAxis().isHorizontal()) {
                superBuffer.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getOpposite())), Direction.UP);
            }
            superBuffer.rotateCentered(AngleHelper.rad((double)(-90.0f - AngleHelper.verticalAngle((Direction)facing))), Direction.EAST);
            superBuffer.light(light);
            superBuffer.renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
        }
    }
}
