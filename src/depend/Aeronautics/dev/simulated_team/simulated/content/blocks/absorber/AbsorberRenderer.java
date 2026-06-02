/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.ryanhcode.sable.util.SableDistUtil
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package dev.simulated_team.simulated.content.blocks.absorber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.sable.util.SableDistUtil;
import dev.simulated_team.simulated.content.blocks.absorber.AbsorberBlock;
import dev.simulated_team.simulated.content.blocks.absorber.AbsorberBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class AbsorberRenderer
extends SmartBlockEntityRenderer<AbsorberBlockEntity> {
    public AbsorberRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(AbsorberBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe((SmartBlockEntity)be, partialTicks, ms, buffer, light, overlay);
        Level level = SableDistUtil.getClientLevel();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        BlockState blockState = be.getBlockState();
        float yRot = (float)Math.toRadians(AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue((Property)AbsorberBlock.HORIZONTAL_FACING))) + 180.0f);
        float pos = be.animationTimer.getValue(partialTicks);
        float target = be.animationTimer.getChaseTarget();
        if ((double)target > 0.5) {
            float fallTime = 0.3f;
            if (pos < 0.3f) {
                pos = 1.0f - pos * pos / 0.09f;
            } else {
                pos = (pos - 0.3f) / 0.7f;
                float bounce = (float)(Math.exp((double)(-pos) * 4.0) * Math.sin((double)pos * Math.PI * 3.0));
                float smoothing = 0.05f;
                bounce = (float)Math.sqrt(bounce * bounce + 0.0025000002f) - 0.05f;
                pos = bounce / 2.0f;
            }
        } else {
            pos = 1.0f - pos;
            float startVelocity = 2.0f;
            pos *= Mth.lerp((float)pos, (float)2.0f, (float)1.0f);
        }
        float movementDistance = 8.0f;
        float totalMovement = (1.0f + (1.0f - pos) * 8.0f) / 16.0f;
        SuperByteBuffer sponge = CachedBuffers.partial((PartialModel)((Boolean)blockState.getValue((Property)AbsorberBlock.WET) != false ? SimPartialModels.ABSORBER_SPONGE_WET : SimPartialModels.ABSORBER_SPONGE_DRY), (BlockState)blockState);
        sponge.translate(0.0, 0.25, 0.0);
        sponge.scale(1.0f, 1.0f - pos * 8.0f / 9.0f, 1.0f);
        sponge.light(light).renderInto(ms, vb);
        Matrix4f rotationMatrix = new Matrix4f();
        this.apply(CachedBuffers.partial((PartialModel)SimPartialModels.ABSORBER_HAT, (BlockState)blockState), ms, light, vb, yRot, totalMovement, rotationMatrix);
        this.apply(CachedBuffers.partial((PartialModel)SimPartialModels.ABSORBER_PIVOT, (BlockState)blockState), ms, light, vb, yRot, totalMovement /= 2.0f, rotationMatrix);
        float height = totalMovement + 0.03125f;
        float length = 0.43125f;
        float width = (float)Math.sqrt(0.18597656f - height * height);
        rotationMatrix.m22(width /= 0.43125f);
        rotationMatrix.m21(height /= 0.43125f);
        rotationMatrix.m11(width);
        rotationMatrix.m12(-height);
        this.apply(CachedBuffers.partial((PartialModel)SimPartialModels.ABSORBER_ARM, (BlockState)blockState), ms, light, vb, yRot, totalMovement, rotationMatrix);
        rotationMatrix.m21(-height);
        rotationMatrix.m12(height);
        rotationMatrix.m00(0.98f);
        this.apply(CachedBuffers.partial((PartialModel)SimPartialModels.ABSORBER_ARM, (BlockState)blockState), ms, light, vb, yRot, totalMovement, rotationMatrix);
    }

    void apply(SuperByteBuffer buffer, PoseStack ms, int light, VertexConsumer vb, float yRot, float offset, Matrix4f rotationMatrix) {
        buffer.translate(0.5, 0.25 + (double)offset, 0.5);
        Matrix4f r = new Matrix4f().rotate(yRot, 0.0f, 1.0f, 0.0f);
        buffer.mulPose((Matrix4fc)r.mul((Matrix4fc)rotationMatrix));
        buffer.translate(-0.5, 0.0, -0.5);
        buffer.light(light).renderInto(ms, vb);
    }
}
