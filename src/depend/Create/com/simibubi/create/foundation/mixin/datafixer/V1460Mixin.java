/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  net.minecraft.util.datafix.fixes.References
 *  net.minecraft.util.datafix.schemas.V1460
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.simibubi.create.foundation.mixin.datafixer;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.simibubi.create.foundation.utility.DataFixerHelper;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={V1460.class})
public class V1460Mixin {
    @Inject(at={@At(value="RETURN")}, method={"registerEntities"})
    private void create$registerEntitiesToBeFixed(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci) {
        Map map = (Map)ci.getReturnValue();
        for (DataFixerHelper.BlockPosFixer fixer : DataFixerHelper.BLOCK_POS_FIXERS_VIEW) {
            if (fixer.reference() != References.ENTITY) continue;
            schema.registerSimple(map, fixer.id());
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"registerBlockEntities"})
    private void create$registerBlockEntitiesToBeFixed(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci) {
        Map map = (Map)ci.getReturnValue();
        for (DataFixerHelper.BlockPosFixer fixer : DataFixerHelper.BLOCK_POS_FIXERS_VIEW) {
            if (fixer.reference() != References.BLOCK_ENTITY) continue;
            schema.registerSimple(map, fixer.id());
        }
    }
}
