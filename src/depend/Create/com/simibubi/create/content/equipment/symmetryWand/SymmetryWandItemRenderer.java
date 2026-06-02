/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.symmetryWand;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SymmetryWandItemRenderer
extends CustomRenderedItemModelRenderer {
    protected static final PartialModel BITS = PartialModel.of((ResourceLocation)Create.asResource("item/wand_of_symmetry/bits"));
    protected static final PartialModel CORE = PartialModel.of((ResourceLocation)Create.asResource("item/wand_of_symmetry/core"));
    protected static final PartialModel CORE_GLOW = PartialModel.of((ResourceLocation)Create.asResource("item/wand_of_symmetry/core_glow"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float worldTime = AnimationTickHolder.getRenderTime() / 20.0f;
        int maxLight = 0xF000F0;
        renderer.render(model.getOriginalModel(), light);
        renderer.renderSolidGlowing(CORE.get(), maxLight);
        renderer.renderGlowing(CORE_GLOW.get(), maxLight);
        float floating = Mth.sin((float)worldTime) * 0.05f;
        float angle = worldTime * -10.0f % 360.0f;
        ms.translate(0.0f, floating, 0.0f);
        ms.mulPose(Axis.YP.rotationDegrees(angle));
        renderer.renderGlowing(BITS.get(), maxLight);
    }
}
