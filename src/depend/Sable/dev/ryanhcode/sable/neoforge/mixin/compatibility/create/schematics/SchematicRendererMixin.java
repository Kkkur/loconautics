/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.schematics.client.SchematicRenderer
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.createmod.catnip.render.ShadedBlockSbbBuilder
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.schematics.client.SchematicRenderer;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.schematics.SchematicLevelExtension;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.render.ShadedBlockSbbBuilder;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SchematicRenderer.class})
public class SchematicRendererMixin {
    @Final
    @Shadow
    private BlockPos anchor;

    @Inject(method={"drawLayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/ModelBlockRenderer;clearCache()V", shift=At.Shift.BEFORE)})
    private void sable$drawLayer(RenderType layer, CallbackInfoReturnable<SuperByteBuffer> cir, @Local BlockRenderDispatcher dispatcher, @Local ModelBlockRenderer renderer, @Local RandomSource random, @Local SchematicLevel mainRenderWorld, @Local PoseStack poseStack, @Local BlockPos.MutableBlockPos mutableBlockPos, @Local ShadedBlockSbbBuilder sbbBuilder) {
        for (SchematicLevelExtension.SchematicSubLevel subLevel : ((SchematicLevelExtension)mainRenderWorld).sable$getSubLevels()) {
            SchematicLevel renderWorld = subLevel.level();
            BoundingBox bounds = renderWorld.getBounds();
            renderWorld.renderMode = true;
            poseStack.pushPose();
            poseStack.translate(subLevel.position().x, subLevel.position().y, subLevel.position().z);
            poseStack.mulPose(new Quaternionf((Quaterniondc)subLevel.orientation()));
            for (BlockPos localPos : BlockPos.betweenClosed((int)bounds.minX(), (int)bounds.minY(), (int)bounds.minZ(), (int)bounds.maxX(), (int)bounds.maxY(), (int)bounds.maxZ())) {
                BlockPos.MutableBlockPos pos = mutableBlockPos.setWithOffset((Vec3i)localPos, (Vec3i)this.anchor);
                BlockState state = renderWorld.getBlockState((BlockPos)pos);
                if (state.getRenderShape() != RenderShape.MODEL) continue;
                BakedModel model = dispatcher.getBlockModel(state);
                BlockEntity blockEntity = renderWorld.getBlockEntity(localPos);
                ModelData modelData = blockEntity != null ? blockEntity.getModelData() : ModelData.EMPTY;
                modelData = model.getModelData((BlockAndTintGetter)renderWorld, (BlockPos)pos, state, modelData);
                long seed = state.getSeed((BlockPos)pos);
                random.setSeed(seed);
                if (!model.getRenderTypes(state, random, modelData).contains(layer)) continue;
                poseStack.pushPose();
                poseStack.translate((float)localPos.getX(), (float)localPos.getY(), (float)localPos.getZ());
                renderer.tesselateBlock((BlockAndTintGetter)renderWorld, model, state, (BlockPos)pos, poseStack, (VertexConsumer)sbbBuilder, true, random, seed, OverlayTexture.NO_OVERLAY, modelData, layer);
                poseStack.popPose();
            }
            poseStack.popPose();
            renderWorld.renderMode = false;
        }
    }
}
