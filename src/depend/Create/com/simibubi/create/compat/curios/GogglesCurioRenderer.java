/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.geom.ModelLayerLocation
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.model.geom.builders.CubeDeformation
 *  net.minecraft.client.model.geom.builders.CubeListBuilder
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  top.theillusivec4.curios.api.SlotContext
 *  top.theillusivec4.curios.api.client.ICurioRenderer
 */
package com.simibubi.create.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.Create;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(value=Dist.CLIENT)
public class GogglesCurioRenderer
implements ICurioRenderer {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Create.asResource("goggles"), "goggles");
    private final HumanoidModel<LivingEntity> model;

    public GogglesCurioRenderer(ModelPart part) {
        this.model = new HumanoidModel(part);
    }

    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.model.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);
        ICurioRenderer.followHeadRotations((LivingEntity)slotContext.entity(), (ModelPart[])new ModelPart[]{this.model.head});
        matrixStack.pushPose();
        matrixStack.translate((double)this.model.head.x / 16.0, (double)this.model.head.y / 16.0, (double)this.model.head.z / 16.0);
        matrixStack.mulPose(Axis.ZP.rotation(this.model.head.zRot));
        matrixStack.mulPose(Axis.YP.rotation(this.model.head.yRot));
        matrixStack.mulPose(Axis.XP.rotation(this.model.head.xRot));
        matrixStack.translate(0.0, -0.25, 0.0);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        matrixStack.scale(0.625f, 0.625f, 0.625f);
        if (!slotContext.entity().getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrixStack.translate(0.0, -0.25, 0.0);
        }
        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, (Level)mc.level, 0);
        matrixStack.popPose();
    }

    public static MeshDefinition mesh() {
        CubeListBuilder builder = new CubeListBuilder();
        MeshDefinition mesh = HumanoidModel.createMesh((CubeDeformation)CubeDeformation.NONE, (float)0.0f);
        mesh.getRoot().addOrReplaceChild("head", builder, PartPose.ZERO);
        return mesh;
    }
}
