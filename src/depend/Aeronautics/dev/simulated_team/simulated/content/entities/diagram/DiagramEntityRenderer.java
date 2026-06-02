/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.simulated_team.simulated.content.entities.diagram;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.entities.diagram.DiagramEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DiagramEntityRenderer
extends EntityRenderer<DiagramEntity> {
    public DiagramEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(DiagramEntity entity, float yaw, float pt, PoseStack ms, MultiBufferSource buffer, int light) {
        PartialModel partialModel = entity.size == 3 ? SimPartialModels.CONTRAPTION_DIAGRAM_3x3 : (entity.size == 2 ? SimPartialModels.CONTRAPTION_DIAGRAM_2x2 : SimPartialModels.CONTRAPTION_DIAGRAM_1x1);
        SuperByteBuffer sbb = CachedBuffers.partial((PartialModel)partialModel, (BlockState)Blocks.AIR.defaultBlockState());
        ((SuperByteBuffer)((SuperByteBuffer)sbb.rotateYDegrees(-yaw)).rotateXDegrees(90.0f + entity.getXRot())).translate(-0.5, -0.03125, -0.5);
        if (entity.size == 2) {
            sbb.translate(0.5, 0.0, -0.5);
        }
        sbb.disableDiffuse().light(light).renderInto(ms, buffer.getBuffer(Sheets.solidBlockSheet()));
        super.render((Entity)entity, yaw, pt, ms, buffer, light);
    }

    public ResourceLocation getTextureLocation(DiagramEntity entity) {
        return null;
    }
}
