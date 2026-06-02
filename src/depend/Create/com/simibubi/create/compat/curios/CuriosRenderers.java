/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.geom.builders.LayerDefinition
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.world.item.Item
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$RegisterLayerDefinitions
 *  top.theillusivec4.curios.api.client.CuriosRendererRegistry
 */
package com.simibubi.create.compat.curios;

import com.simibubi.create.AllItems;
import com.simibubi.create.compat.curios.GogglesCurioRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(value=Dist.CLIENT)
public class CuriosRenderers {
    public static void register() {
        CuriosRendererRegistry.register((Item)((Item)AllItems.GOGGLES.get()), () -> new GogglesCurioRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(GogglesCurioRenderer.LAYER)));
    }

    public static void onLayerRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GogglesCurioRenderer.LAYER, () -> LayerDefinition.create((MeshDefinition)GogglesCurioRenderer.mesh(), (int)1, (int)1));
    }
}
