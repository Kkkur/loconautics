/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={HumanoidArmorLayer.class})
public interface HumanoidArmorLayerAccessor {
    @Accessor(value="ARMOR_LOCATION_CACHE")
    public static Map<String, ResourceLocation> create$getArmorLocationCache() {
        throw new RuntimeException();
    }

    @Accessor(value="innerModel")
    public HumanoidModel<?> create$getInnerModel();

    @Accessor(value="outerModel")
    public HumanoidModel<?> create$getOuterModel();

    @Invoker(value="setPartVisibility")
    public void create$callSetPartVisibility(HumanoidModel<?> var1, EquipmentSlot var2);
}
