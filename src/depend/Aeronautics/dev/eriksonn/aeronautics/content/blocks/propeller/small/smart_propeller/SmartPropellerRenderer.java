/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.SimplePropellerRenderer;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class SmartPropellerRenderer
extends SimplePropellerRenderer<SmartPropellerBlockEntity> {
    public SmartPropellerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderSafe(SmartPropellerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = this.getRenderedBlockState(be);
        RenderType type = this.getRenderType(be, state);
        SmartPropellerRenderer.renderRotatingBuffer((KineticBlockEntity)be, (SuperByteBuffer)this.getRotatedModel(be, state), (PoseStack)ms, (VertexConsumer)buffer.getBuffer(type), (int)light);
        Direction.Axis horizontal = (Direction.Axis)state.getValue((Property)BlockStateProperties.HORIZONTAL_AXIS);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer propeller = CachedBuffers.partialFacing((PartialModel)this.getCurrentModel(be), (BlockState)state, (Direction)Direction.UP).light(light);
        SuperByteBuffer hinge = CachedBuffers.partialFacing((PartialModel)AeroPartialModels.SMART_PROPELLER_HINGE, (BlockState)state, (Direction)Direction.UP).light(light);
        float hingeAngle = be.getLerpedHingeAngle(partialTicks);
        float angle = this.getAngle(partialTicks, Direction.UP, be);
        Direction d = Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)horizontal);
        hinge.rotateCentered(AngleHelper.rad((double)hingeAngle), d.getClockWise());
        propeller.rotateCentered(AngleHelper.rad((double)hingeAngle), d.getClockWise());
        float factChecked = AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)d));
        propeller.rotateCentered(factChecked, Direction.UP);
        hinge.rotateCentered(factChecked, Direction.UP);
        SmartPropellerRenderer.kineticRotationTransform((SuperByteBuffer)propeller, (KineticBlockEntity)be, (Direction.Axis)Direction.UP.getAxis(), (float)angle, (int)light);
        propeller.translate(0.0f, 0.625f, 0.0f);
        propeller.rotateCentered(AngleHelper.rad((double)90.0), Direction.EAST);
        hinge.translate(0.0f, -0.0625f, 0.0f);
        hinge.rotateCentered(AngleHelper.rad((double)90.0), Direction.EAST);
        propeller.renderInto(ms, vb);
        hinge.renderInto(ms, vb);
    }

    @Override
    public PartialModel getCurrentModel(SmartPropellerBlockEntity be) {
        return (Boolean)be.getBlockState().getValue((Property)SmartPropellerBlock.REVERSED) != false ? AeroPartialModels.SMART_PROPELLER_REVERSED : AeroPartialModels.SMART_PROPELLER;
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SmartPropellerBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state, (Direction)Direction.DOWN);
    }
}
