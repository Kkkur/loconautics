/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.Collection;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CarriageCouplingRenderer {
    public static void renderAll(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
        Collection<Train> trains = CreateClient.RAILWAYS.trains.values();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        BlockState air = Blocks.AIR.defaultBlockState();
        float partialTicks = AnimationTickHolder.getPartialTicks();
        ClientLevel level = Minecraft.getInstance().level;
        for (Train train : trains) {
            List<Carriage> carriages = train.carriages;
            for (int i = 0; i < carriages.size() - 1; ++i) {
                Carriage carriage = carriages.get(i);
                CarriageContraptionEntity entity = (CarriageContraptionEntity)((Object)carriage.getDimensional((Level)level).entity.get());
                Carriage carriage2 = carriages.get(i + 1);
                CarriageContraptionEntity entity2 = (CarriageContraptionEntity)((Object)carriage.getDimensional((Level)level).entity.get());
                if (entity == null || entity2 == null) continue;
                CarriageBogey bogey1 = carriage.trailingBogey();
                CarriageBogey bogey2 = carriage2.leadingBogey();
                Vec3 anchor = (Vec3)bogey1.couplingAnchors.getSecond();
                Vec3 anchor2 = (Vec3)bogey2.couplingAnchors.getFirst();
                if (anchor == null || anchor2 == null || !anchor.closerThan((Position)camera, 64.0)) continue;
                int lightCoords = CarriageCouplingRenderer.getPackedLightCoords(entity, partialTicks);
                int lightCoords2 = CarriageCouplingRenderer.getPackedLightCoords(entity2, partialTicks);
                double diffX = anchor2.x - anchor.x;
                double diffY = anchor2.y - anchor.y;
                double diffZ = anchor2.z - anchor.z;
                float yRot = AngleHelper.deg((double)Mth.atan2((double)diffZ, (double)diffX)) + 90.0f;
                float xRot = AngleHelper.deg((double)Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)));
                Vec3 position = entity.getPosition(partialTicks);
                Vec3 position2 = entity2.getPosition(partialTicks);
                ms.pushPose();
                ms.pushPose();
                ms.translate(anchor.x - camera.x, anchor.y - camera.y, anchor.z - camera.z);
                ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.TRAIN_COUPLING_HEAD, (BlockState)air).rotateYDegrees(-yRot)).rotateXDegrees(xRot)).light(lightCoords).renderInto(ms, vb);
                float margin = 0.1875f;
                double couplingDistance = (double)((float)train.carriageSpacing.get(i).intValue() - 2.0f * margin) - bogey1.type.getConnectorAnchorOffset((boolean)bogey1.isUpsideDown()).z - bogey2.type.getConnectorAnchorOffset((boolean)bogey2.isUpsideDown()).z;
                int couplingSegments = (int)Math.round(couplingDistance * 4.0);
                double stretch = (anchor2.distanceTo(anchor) - (double)(2.0f * margin)) * 4.0 / (double)couplingSegments;
                for (int j = 0; j < couplingSegments; ++j) {
                    ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.TRAIN_COUPLING_CABLE, (BlockState)air).rotateYDegrees(-yRot + 180.0f)).rotateXDegrees(-xRot)).translate(0.0f, 0.0f, margin + 0.125f)).scale(1.0f, 1.0f, (float)stretch)).translate(0.0f, 0.0f, (float)j / 4.0f)).light(lightCoords).renderInto(ms, vb);
                }
                ms.popPose();
                ms.pushPose();
                Vec3 translation = position2.subtract(position).add(anchor2).subtract(camera);
                ms.translate(translation.x, translation.y, translation.z);
                ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.TRAIN_COUPLING_HEAD, (BlockState)air).rotateYDegrees(-yRot + 180.0f)).rotateXDegrees(-xRot)).light(lightCoords2).renderInto(ms, vb);
                ms.popPose();
                ms.popPose();
            }
        }
    }

    public static int getPackedLightCoords(Entity pEntity, float pPartialTicks) {
        BlockPos blockpos = BlockPos.containing((Position)pEntity.getLightProbePosition(pPartialTicks));
        return LightTexture.pack((int)CarriageCouplingRenderer.getBlockLightLevel(pEntity, blockpos), (int)CarriageCouplingRenderer.getSkyLightLevel(pEntity, blockpos));
    }

    protected static int getSkyLightLevel(Entity pEntity, BlockPos pPos) {
        return pEntity.level().getBrightness(LightLayer.SKY, pPos);
    }

    protected static int getBlockLightLevel(Entity pEntity, BlockPos pPos) {
        return pEntity.isOnFire() ? 15 : pEntity.level().getBrightness(LightLayer.BLOCK, pPos);
    }
}
