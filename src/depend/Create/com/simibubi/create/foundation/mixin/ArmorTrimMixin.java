/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.armortrim.ArmorTrim
 *  net.minecraft.world.item.armortrim.TrimMaterial
 *  net.minecraft.world.item.armortrim.TrimPattern
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.simibubi.create.foundation.mixin;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import java.util.function.BiFunction;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ArmorTrim.class})
public abstract class ArmorTrimMixin {
    @Shadow
    @Final
    private Holder<TrimMaterial> material;
    @Shadow
    @Final
    private Holder<TrimPattern> pattern;
    @Unique
    private final BiFunction<Boolean, Holder<ArmorMaterial>, ResourceLocation> create$textureCardboard = Util.memoize((inner, armorMaterial) -> {
        String assetPath = ((TrimPattern)this.pattern.value()).assetId().getPath();
        String colorSuffix = ArmorTrimMixin.getColorPaletteSuffix(this.material, (Holder<ArmorMaterial>)armorMaterial);
        return Create.asResource("trims/models/armor/card_" + assetPath + (inner != false ? "_leggings_" : "_") + colorSuffix);
    });

    @Shadow
    private static String getColorPaletteSuffix(Holder<TrimMaterial> trimMaterial, Holder<ArmorMaterial> armorMaterial) {
        throw new AssertionError();
    }

    @Inject(method={"innerTexture"}, at={@At(value="HEAD")}, cancellable=true)
    private void create$swapTexturesForCardboardTrimsInner(Holder<ArmorMaterial> armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        if (armorMaterial == AllArmorMaterials.CARDBOARD) {
            cir.setReturnValue((Object)this.create$textureCardboard.apply(true, armorMaterial));
        }
    }

    @Inject(method={"outerTexture"}, at={@At(value="HEAD")}, cancellable=true)
    private void create$swapTexturesForCardboardTrimsOuter(Holder<ArmorMaterial> armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        if (armorMaterial == AllArmorMaterials.CARDBOARD) {
            cir.setReturnValue((Object)this.create$textureCardboard.apply(false, armorMaterial));
        }
    }
}
